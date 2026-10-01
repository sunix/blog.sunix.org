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
