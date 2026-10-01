---
layout: layouts/post.html
title: "From minikube to kind: mounting your local sources into a pod"
date: 2026-10-01
modified: 2026-10-01
category: articles
tags:
  - Kubernetes
  - kind
  - minikube
  - Make
  - Developer Experience
comments: true
---

I moved a local Kubernetes dev environment from minikube to [kind](https://kind.sigs.k8s.io/). The part that needed rethinking was mounting my project sources from my laptop into a running container, so I could edit locally and have the app hot reload in the cluster. Here is how it maps from minikube to kind, and the small Makefile I ended up with.

<!-- more -->

### What I had with minikube

With minikube, the recipe was:

1. Start the cluster with the mount: `minikube start --mount --mount-string="/path/to/project:/home/docker/project"`.
2. Patch the deployment to add a `hostPath` volume pointing to `/home/docker/project`, and mount it in the container.

`minikube mount /path/to/project:/home/docker/project` does the same job as `--mount-string`, as a foreground process in a separate terminal. You need one or the other, not both.

### How it works with kind

With kind, each Kubernetes node is a Docker container. Your disk is not visible in it by default. There are two mounts to chain:

```
your laptop ──extraMounts──► kind node ──hostPath──► your pod
```

- `extraMounts`, in the kind cluster config, makes a directory of your laptop visible inside the node. It is declared **when the cluster is created** and cannot be changed afterwards: to change it, delete and recreate the cluster.
- `hostPath`, in the deployment, picks a directory **inside the node** and mounts it in the container. This part is the same as with minikube.

Without `extraMounts`, the `hostPath` points to a directory that doesn't exist in the node. With `type: Directory`, the pod stays stuck in `ContainerCreating`. Without it, Kubernetes silently creates an empty directory and your app starts with no files.

### Mount the parent once, choose the project later

Since `extraMounts` is fixed at cluster creation, I mount the **parent directory** of all my projects once, at a fixed path in the node (`/projects`):

```yaml
kind: Cluster
apiVersion: kind.x-k8s.io/v1alpha4
nodes:
  - role: control-plane
    extraMounts:
      - hostPath: /home/me/projects
        containerPath: /projects
```

Then the deployment patch chooses which subdirectory to use:

```yaml
spec:
  template:
    spec:
      volumes:
        - name: project-src
          hostPath:
            path: /projects/my-project
            type: Directory
      containers:
        - name: my-container
          volumeMounts:
            - name: project-src
              mountPath: /app
```

```bash
kubectl patch deployment my-deployment --patch-file patch-mount.yaml
```

Switching project is just changing `path` and patching again, no cluster recreation. New subdirectories created later under `/home/me/projects` are visible in the node right away.

Keeping a fixed path in the node (`/projects`) means the patch never depends on where each developer keeps their projects on their own machine. Only the cluster creation needs to know that.

A few notes:

- **Multi-node clusters:** add the same `extraMounts` to every node, or a pod scheduled on a node without it will not find its files.
- **Docker Desktop (macOS/Windows):** the host path must be in the shared folders (Settings → Resources → File sharing). `/Users` is shared by default.
- **Local images:** the equivalent of `minikube image load` is `kind load docker-image my-image:tag --name dev`. Use `imagePullPolicy: IfNotPresent` and avoid the `latest` tag, or Kubernetes will try to pull the image.

### Hot reload

Mount a **directory**, never a single file. Many editors save by writing a temporary file and renaming it over the original. A single-file mount keeps showing the old version, while a directory mount sees the change.

Dev servers (Vite, nodemon, webpack, Quarkus dev mode...) detect changes through inotify on Linux. minikube's `mount` uses 9p, which does not forward inotify events, which is why polling was often needed there. kind's `extraMounts` are plain Docker bind mounts, and the events reach the container.

On Linux, kind is known to hit inotify limits (`too many open files`, or reload silently stopping). Raise them:

```bash
sudo sysctl fs.inotify.max_user_watches=524288
sudo sysctl fs.inotify.max_user_instances=512
```

To make it permanent, put both settings in `/etc/sysctl.d/99-kind.conf`.

On Docker Desktop, events usually go through, but if changes are not detected, switch your tool to polling (`CHOKIDAR_USEPOLLING=true`, `WATCHPACK_POLLING=true`, Vite's `server.watch.usePolling`, `nodemon -L`...).

Also remember to run the container in dev mode: the production image usually doesn't start a file watcher, so override `command` in the patch if needed.

### Putting it in a Makefile

Here is the whole thing in a Makefile. The kind config and the patch are written inline with `define`, so there are no extra files to maintain, and the project to mount is a parameter.

```makefile
# Per-developer settings (not committed, optional)
-include local.mk

CLUSTER      ?= dev
DEPLOYMENT   ?= my-deployment
CONTAINER    ?= my-container
PROJECTS_DIR ?= $(HOME)/projects
PROJECT      ?= my-project
MOUNT_PATH   ?= /app
IMAGE        ?= my-image:tag

# Where the projects directory is mounted inside the kind node (same for everyone)
NODE_DIR := /projects

define KIND_CONFIG
kind: Cluster
apiVersion: kind.x-k8s.io/v1alpha4
nodes:
  - role: control-plane
    extraMounts:
      - hostPath: $(abspath $(PROJECTS_DIR))
        containerPath: $(NODE_DIR)
endef
export KIND_CONFIG

define PATCH
spec:
  template:
    spec:
      volumes:
        - name: project-src
          hostPath:
            path: $(NODE_DIR)/$(PROJECT)
            type: Directory
      containers:
        - name: $(CONTAINER)
          volumeMounts:
            - name: project-src
              mountPath: $(MOUNT_PATH)
endef
export PATCH

.PHONY: cluster mount load delete

cluster: ## Create the kind cluster (mounting PROJECTS_DIR) if it doesn't exist
	kind get clusters | grep -qx $(CLUSTER) || echo "$$KIND_CONFIG" | kind create cluster --name $(CLUSTER) --config=-

mount: ## Mount PROJECT into the deployment
	kubectl patch deployment $(DEPLOYMENT) -p "$$PATCH"
	kubectl rollout status deployment/$(DEPLOYMENT)

load: ## Load the local image into the cluster
	kind load docker-image $(IMAGE) --name $(CLUSTER)

delete: ## Delete the cluster
	kind delete cluster --name $(CLUSTER)
```

How it works:

- **`define` + `export`:** Make expands the `$(...)` variables inside the YAML, then passes it to the recipe as an environment variable, read with `$$PATCH`. Newlines are kept, and `kubectl patch -p` accepts YAML, so there is no one-line JSON to escape.
- **`--config=-`:** kind reads its config from stdin, so no `kind-config.yaml` file is needed.
- **`abspath`:** turns a relative `PROJECTS_DIR` into an absolute path for the bind mount.
- **`make cluster`** does nothing if the cluster already exists, and **`make mount`** waits for the new pods to be ready.
- Recipe lines must start with a **tab**, not spaces.

### Per-developer settings with `local.mk`

Every developer keeps their projects somewhere different. The Makefile, committed in the repo, holds defaults. Each developer can override them in a `local.mk` file, ignored by git:

```makefile
# local.mk
PROJECTS_DIR = $(HOME)/work
PROJECT      = api
```

```
# .gitignore
local.mk
```

`-include local.mk` loads the file if it exists, without failing when it doesn't. It is included **before** the `?=` defaults, so its values win. A committed `local.mk.example` documents the available settings.

The precedence is then:

1. the command line (`make mount PROJECT=other`), for a one-off change;
2. `local.mk`, for a developer's permanent settings;
3. environment variables, thanks to `?=`;
4. the defaults in the Makefile.

I first considered a `.env` file instead of `local.mk`. Make would read it as its own variable assignments rather than a real `.env`: quotes become part of the value, `~` is not expanded, and Docker Compose reads `.env` automatically too. `local.mk` makes it clear the file belongs to Make, and allows Make syntax like `$(HOME)`.

### Daily usage

```bash
cp local.mk.example local.mk   # once, then adapt
make cluster
make load
make mount
make mount PROJECT=api         # switch project
make delete
```

The only constraint left from kind is that `PROJECTS_DIR` is read when the cluster is created. To mount a different parent directory, run `make delete` then `make cluster`. Everything else can change while the cluster is running.

### Trying it for real

To check all of this, I ran the Makefile above, unchanged, against a throwaway kind cluster. The demo folder looks like this:

```
demo/
├── Makefile           # the one above
├── local.mk
├── image/             # the "production" image
│   ├── Dockerfile
│   └── server.js
└── projects/          # mounted in the node as /projects
    ├── api/server.js
    └── my-project/server.js
```

The app is a tiny Node.js server that returns a message. It runs with `node --watch`, which restarts the process when a source file changes, like a dev server would:

```js
const http = require('http');
const message = 'Hello from the image';
http.createServer((req, res) => res.end(message + '\n')).listen(3000);
console.log('listening on 3000');
```

```dockerfile
FROM node:22-alpine
WORKDIR /app
COPY server.js .
CMD ["node", "--watch", "server.js"]
```

The image has `Hello from the image` built in. `projects/my-project/server.js` and `projects/api/server.js` are the same file, saying `Hello from my laptop` and `Hello from the api project`.

`local.mk` uses a relative `PROJECTS_DIR` (`abspath` takes care of it). It also sets the container name, because `kubectl create deployment` names the container after the image:

```makefile
PROJECTS_DIR = ./projects
CLUSTER      = demo
CONTAINER    = my-image
IMAGE        = my-image:1.0
```

#### Create the cluster and load the image

```
$ docker build -q -t my-image:1.0 image
sha256:34ab32c108135c3b9f607f568735358bc494eb8fae516dbcd5abd2cfa78217ed

$ make cluster
kind get clusters | grep -qx demo || echo "$KIND_CONFIG" | kind create cluster --name demo --config=-
Creating cluster "demo" ...
 ✓ Ensuring node image (kindest/node:v1.34.0) 🖼
 ✓ Preparing nodes 📦
 ✓ Writing configuration 📜
 ✓ Starting control-plane 🕹️
 ✓ Installing CNI 🔌
 ✓ Installing StorageClass 💾
Set kubectl context to "kind-demo"
```

The projects directory is now visible inside the node, at `/projects`:

```
$ docker exec demo-control-plane ls -R /projects
/projects:
api
my-project

/projects/api:
server.js

/projects/my-project:
server.js
```

```
$ make load
kind load docker-image my-image:1.0 --name demo
Image: "my-image:1.0" with ID "sha256:34ab32c108135c3b9f607f568735358bc494eb8fae516dbcd5abd2cfa78217ed" not yet present on node "demo-control-plane", loading...
```

#### A real deployment, before the mount

```
$ kubectl create deployment my-deployment --image=my-image:1.0 --port=3000
deployment.apps/my-deployment created

$ kubectl rollout status deployment/my-deployment
Waiting for deployment "my-deployment" rollout to finish: 0 of 1 updated replicas are available...
deployment "my-deployment" successfully rolled out
```

With a non-`latest` tag, `kubectl create deployment` sets `imagePullPolicy: IfNotPresent`, so the loaded image is used. In another terminal, `kubectl port-forward deploy/my-deployment 3000:3000`, then:

```
$ curl -s localhost:3000
Hello from the image
```

#### Mount the local sources

```
$ make mount
kubectl patch deployment my-deployment -p "$PATCH"
deployment.apps/my-deployment patched
kubectl rollout status deployment/my-deployment
Waiting for deployment "my-deployment" rollout to finish: 1 old replicas are pending termination...
deployment "my-deployment" successfully rolled out

$ curl -s localhost:3000
Hello from my laptop
```

The code now comes from my disk, not from the image. Note that `port-forward` is bound to a **pod**, not to the deployment: after each rollout, the old pod is gone and the port-forward stops with `lost connection to pod`. Restart it after each `make mount`.

#### Edit on the laptop, see it in the pod

I edit the file on my laptop with `sed -i`, which writes a new file and renames it over the old one, like many editors do:

```
$ sed -i 's/Hello from my laptop/Hello again, edited on my laptop/' projects/my-project/server.js

$ curl -s localhost:3000
Hello again, edited on my laptop

$ kubectl logs deploy/my-deployment
listening on 3000
Restarting 'server.js'
listening on 3000

$ kubectl get pods
NAME                             READY   STATUS    RESTARTS   AGE
my-deployment-7dc776757d-8sp5b   1/1     Running   0          8s
```

`node --watch` received the inotify event through the two mounts and restarted the process. The pod itself did not restart (`RESTARTS 0`).

#### Switch project without recreating the cluster

```
$ make mount PROJECT=api
kubectl patch deployment my-deployment -p "$PATCH"
deployment.apps/my-deployment patched
kubectl rollout status deployment/my-deployment
Waiting for deployment "my-deployment" rollout to finish: 1 old replicas are pending termination...
deployment "my-deployment" successfully rolled out

$ curl -s localhost:3000
Hello from the api project
```

#### A project that doesn't exist

With `type: Directory`, a typo in `PROJECT` fails loudly instead of mounting an empty directory:

```
$ timeout 30 make mount PROJECT=does-not-exist
kubectl patch deployment my-deployment -p "$PATCH"
deployment.apps/my-deployment patched
kubectl rollout status deployment/my-deployment
Waiting for deployment "my-deployment" rollout to finish: 1 old replicas are pending termination...
make: *** [Makefile:50: mount] Error 1

$ kubectl get pods
NAME                             READY   STATUS              RESTARTS   AGE
my-deployment-74bfcf657f-lbmtw   0/1     ContainerCreating   0          30s
my-deployment-89668c7f4-cpw8m    1/1     Running             0          34s

$ kubectl get events --field-selector reason=FailedMount -o custom-columns=REASON:.reason,MESSAGE:.message | tail -1
FailedMount   MountVolume.SetUp failed for volume "project-src" : hostPath type check failed: /projects/does-not-exist is not a directory
```

The new pod stays in `ContainerCreating`. The rolling update keeps the old pod running, so the app is still up. `make mount` with the right project fixes it.

#### The inotify limit, for real

On my first try, the mount worked but hot reload did nothing: the file had changed in the pod, `node --watch` didn't restart, and there was no error in the logs. Watching the directory by hand inside the pod showed why:

```
$ kubectl exec deploy/my-deployment -- node -e "const fs=require('fs');fs.watch('/app',(e,f)=>console.log('dir',e,f));fs.watch('/app/server.js',(e,f)=>console.log('file',e,f));setTimeout(()=>{},7000)"
node:internal/fs/watchers:262
    throw error;
    ^

Error: EMFILE: too many open files, watch '/app'
    at FSWatcher.<computed> (node:internal/fs/watchers:254:19)
    at Object.watch (node:fs:2554:36)
    ...
command terminated with exit code 1
```

I had a few other kind clusters running, and the default limit of 128 inotify instances was used up:

```
$ find /proc/*/fd -lname 'anon_inode:inotify' 2>/dev/null | wc -l
120
$ sysctl fs.inotify.max_user_instances
fs.inotify.max_user_instances = 128
```

After `sudo sysctl fs.inotify.max_user_instances=512` and a `kubectl rollout restart deployment/my-deployment`, hot reload worked as shown above. `node --watch` gave up silently when it couldn't watch, so if reload stops working on Linux, check this limit first.

#### Clean up

```
$ make delete
kind delete cluster --name demo
Deleting cluster "demo" ...
Deleted nodes: ["demo-control-plane"]
```
