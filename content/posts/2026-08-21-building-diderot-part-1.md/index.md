---
layout: layouts/post.html
title: "Building diderot: a package manager for AI agent skills, one prompt at a time (part 1)"
date: 2026-08-21
modified: 2026-08-25
category: making-of
tags:
  - AI-Assisted Development
  - Claude Code
  - Agent Skills
  - Java
  - Quarkus
  - OCI
  - Open Source
comments: true
---

I've started keeping a `MAKING-OF.md` journal in my repositories - a first-person log of how each project comes together with an AI agent, including the dead ends and the arguments. This post is the first chapter of [diderot](https://github.com/sunix/diderot)'s journal, lightly edited for the blog: how a "Helm for AI agent skills" went from a name-picking session to a working `update` / `install` / `status` over git sources, in two working sessions. The code this post walks through landed in [diderot#2](https://github.com/sunix/diderot/pull/2).

<!-- more -->

## Why this exists

It started with a different question entirely: I had just made [ai-skills](https://github.com/sunix/ai-skills), my library of reusable agent skills, [installable in other projects](https://github.com/sunix/ai-skills/pull/17) - and I wanted a way to manage those installs. Something like Helm, but for skills: push them to OCI registries, declare them in a per-repo manifest, install and update them like dependencies.

Before writing a line, Claude and I surveyed what already existed, and the survey stung a little: most of the idea was already built. `gh skill` (GitHub CLI v2.90+) does install/pin/update from GitHub repos. `npx skills` (Vercel) has the manifest (`.skills.json`) and the lockfile. Claude Code has plugin marketplaces. The Helm-like CLI I was picturing existed three times over - for git sources.

What none of them do: **OCI registries** (the artifact stores enterprises already run, with auth, replication, signing, and an air-gap story), **content-digest lockfiles** (every existing tool pins tags or versions; tags move, digests don't), and **cosign verification**. That's the gap. That's the project.

## First, the name

The hardest part, obviously. The shortlist came from checking npm, crates.io, and GitHub availability in one pass: *metier* (the skill **and** the Jacquard loom - free everywhere), *compagnon* (the French institution of craft-skill transmission), *guilde*, and *diderot*.

I picked **diderot**, against the availability argument: the npm name is squatted by a dependency-injection library dead since 2022, and PyPI is taken too. Don't care. The Encyclopédie's full title is *"Dictionnaire raisonné des sciences, des arts et des métiers"* - a registry of skills, built to be distributed. A CLI that pushes skills to registries could not be named anything else. (crates.io is free, and `@sunix/diderot` exists as an npm fallback if ever needed.)

## Go was the obvious choice, so naturally it's Java

Claude's recommendation was unambiguous: Go. `oras-go` is *the* reference OCI-artifact library (Helm and the `oras` CLI itself are built on it), sigstore tooling is Go-native, and every potential contributor in that ecosystem already speaks it.

I chose Java with Quarkus anyway, and it's not stubbornness - it's twenty years of it. I've been part of the [Paris JUG](https://www.parisjug.org/) crew since 2015 and led it from 2019 to 2023, and most of my open source work has been Java in exactly this problem space: the [Fabric8 Kubernetes Java client](https://github.com/fabric8io/kubernetes-client) and [Eclipse JKube](https://github.com/eclipse-jkube/jkube) at Red Hat - Java talking to container registries and cloud-native APIs, which is precisely what "the OCI ecosystem speaks Go" is supposed to rule out - plus Eclipse Che and Nuxeo before that. More recently [jdtls-mcp](https://github.com/sunix/jdtls-mcp) and Erasmus. GraalVM native-image closes the distribution gap: same single static binary as Go, and Quarkus makes that path boring.

The acknowledged risk stands: [oras-java](https://github.com/oras-project/oras-java) is the official ORAS SDK but still *incubating*, so I'm probably signing up for upstream contributions along the way. That's how jdtls-mcp went with the MCP Java SDK, and honestly, filing real issues against a young SDK is half the fun.

## Stealing Helm's homework

The design is a deliberate Helm transposition. `Chart.yaml` declares dependencies with version constraints; `helm dependency update` resolves them and writes `Chart.lock`; `helm dependency build` reproduces exactly what the lock says. Same split here: `diderot.yaml` -> `diderot update` -> `diderot.lock` -> `diderot install`.

One deliberate improvement over Helm: the lock pins **content digests**, not versions - OCI digest for registry sources, git tree SHA for git sources (the trick `gh skill` already uses). A version tag can be re-pushed; a digest can't lie.

One thing deliberately *not* taken from Helm: templating and values. A skill is markdown and files; there's nothing to render.

## M1: one user story

The first milestone is one user story, and it's mine: I keep reusable skills in [ai-skills](https://github.com/sunix/ai-skills), and in every project where I work with an agent I want to declare which ones it should have, like any other dependency. Concretely, drop this in a project:

```yaml
# diderot.yaml
skills:
  - name: making-of
    source: git+https://github.com/sunix/ai-skills#skills/documentation/making-of
    version: main
targets: [claude]
```

then run `diderot update && diderot install`, and `.claude/skills/making-of/` exists with exactly the bytes the lockfile pinned - same bytes tomorrow, same bytes on a teammate's machine or in the throwaway workspace a remote coding agent spins up, even if the `main` branch has moved on. And when I (or an agent, it happens) fumble an installed skill file, `diderot status` must say `DRIFTED` and exit non-zero instead of letting the corruption ride along silently - with `diderot install` as the repair. That full loop - declare, lock, install, verify, repair - is what M1 had to deliver.

## Three verbs, and who runs which

**`diderot update`** reads `diderot.yaml` - the *constraints*. And a constraint promises nothing; `main` here is a moving branch, **not** a fixed version:

```yaml
# diderot.yaml - what the user maintains
skills:
  - name: making-of
    source: git+https://github.com/sunix/ai-skills#skills/documentation/making-of
    version: main    # wherever ai-skills' main points today - a moving target
```

`update` goes to the network and nails that moving target down: the branch becomes the exact commit it pointed to *at that instant*, plus the digest of the skill's bytes, both written to `diderot.lock`:

```yaml
# diderot.lock - what update generates; never edited by hand
skills:
- name: making-of
  source: git+https://github.com/sunix/ai-skills#skills/documentation/making-of
  resolved: 64358bc5644155d4513bf17e421119fc6eec9127    # the commit main pointed to
  digest: tree:0f755a27d65b67d80d6d1ee2ed0d8a7963fa8f23  # the exact bytes of the skill dir
```

Run `update` again next month and `resolved` may well change - `main` moved, and re-resolving it is exactly the job. It is the only command that ever moves the lock. **`diderot install`** reads *only* the lock: no resolution, no opinion, just materialize the pinned bytes and verify them - the `npm ci` / `helm dependency build` of the family. **`diderot status`** is the read-only audit: compare disk against lock, `ok`/`DRIFTED`/`MISSING`, non-zero exit on trouble.

Who types what: the manifest author, day one, runs `update` then `install`. A teammate cloning a repo whose lock is committed runs **`install` and nothing else** - by far the most travelled path. And "teammate" increasingly means a machine: a remote coding agent - a cloud Claude Code session, a Copilot coding agent - wakes up in a fresh throwaway workspace with nothing but the repository clone, and needs its skills materialized before it starts thinking. One `diderot install` in the bootstrap script and the agent's toolbox travels with the repo, identical on every spin-up - that audience is who the lockfile really serves.

Writing that down triggered the obvious challenge (mine, this time - Claude defended the Helm split): is `update` even necessary? Half yes, half no. As a *mandatory first step* it's friction - cargo generates the lock on first build, npm resolves what's missing - so a coming change will make `install` welcoming: resolve by itself when the lock is absent or the manifest declares a skill the lock doesn't know, with a `--frozen` flag for unattended runs that fails instead of resolving. But as the *explicit "advance the versions" verb*, `update` is irreplaceable: when a lock exists, `install` must never move it - that's the whole reproducibility contract. One verb obeys the lock, one verb moves it.

## A tour of the machinery, for whoever opens the hood

M1 is eight new classes plus three test files, and the tour follows the verbs in lockfile order: `update` writes it, `install` obeys it, `status` audits it.

`diderot update` enters through `commands/UpdateCommand.java` - and finds almost nothing there. The picocli commands (~40 lines each) are deliberately dumb: parse the options, build the engine, translate exceptions into `error: ...` plus exit code 1, and that's the whole class:

```java
@Override
public Integer call() {
    PrintWriter out = spec.commandLine().getOut();
    try {
        new Workspace(directory.toAbsolutePath().normalize(),
                new GitCli(GitCli.defaultCacheRoot()), out).update();
        return 0;
    } catch (Exception e) {
        spec.commandLine().getErr().println("error: " + e.getMessage());
        return 1;
    }
}
```

All three delegate to the same object, `core/Workspace.java`, which *is* the feature: one class, three public methods - `update()`, `install()`, `status()` - and not a single picocli import, which is exactly what lets the tests drive it without spawning a CLI.

`Workspace.update()` reads `diderot.yaml` into a dumb Jackson DTO, and the whole resolution is one loop:

```java
for (ManifestSkill skill : manifest.skills) {
    SourceRef ref = SourceRef.parse(skill.source);      // scheme + repo URL + path in repo
    Path repo = git.ensureFresh(ref.url());
    String commit = git.resolveCommit(repo, skill.version);
    if (!git.blobExists(repo, commit, ref.path() + "/SKILL.md")) {
        throw new IOException("Skill '" + skill.name + "': no SKILL.md at ...");
    }
    locked.resolved = commit;
    locked.digest = "tree:" + git.treeSha(repo, commit, ref.path());
}
Yaml.write(lockPath(), lock);
```

`core/SourceRef.java` is the parser on that first line - a record with a `Kind`:

```java
public record SourceRef(Kind kind, String url, String path) {
    public enum Kind { GIT, OCI }
```

It splits `git+https://github.com/sunix/ai-skills#skills/documentation/making-of` into `GIT`, the repo URL, and the path inside the repo - and it already parses `oci://` too. Milestone M2 will plug into that same seam; until then `update` fails politely on an OCI source instead of mysteriously.

Then comes the only class allowed to touch the outside world: `git/GitCli.java`, the single subprocess boundary. **Git does git**: everything it runs is a call to the real binary - no protocol reimplementation, no JGit; it's the approach Go modules used for years. `ensureFresh()` keeps one **bare** clone per repository URL, shared by all projects on the machine:

```java
Path repo = cacheRoot.resolve(cacheKey(url));   // ~/.cache/diderot/git/<sha256(url) prefix>
if (!Files.isDirectory(repo)) {
    run(null, "git", "clone", "--bare", "--quiet", url, repo.toString());
} else {
    run(repo, "git", "fetch", "--quiet", "--prune", "origin",
            "+refs/heads/*:refs/heads/*", "+refs/tags/*:refs/tags/*");
}
```

Its siblings are one git invocation each: `resolveCommit()` turns `main` (or a tag, or a short SHA) into a full commit via `rev-parse <ref>^\{commit}`; `blobExists()` is `cat-file -e` - the SKILL.md gate above; `treeSha()` asks `rev-parse <commit>:<path>` for the directory's tree SHA, which becomes the `digest: tree:...` line in the lockfile.

The lock written, `Workspace.install()` takes over - today, tomorrow, or on a colleague's machine. It reads `diderot.lock` back, and for each skill x target (`claude` -> `.claude/skills`, `agents` -> `.agents/skills`) it replaces the installed directory with exactly the locked bytes, and proves it before moving on:

```java
Path dest = target.skillsDir(root).resolve(skill.name);
deleteRecursively(dest);
git.extract(repo, skill.resolved, ref.path(), dest);   // git archive -> commons-compress untar
String actual = "tree:" + GitTreeHasher.treeSha(dest);
if (!actual.equals(skill.digest)) {
    throw new IOException("Digest mismatch for '" + skill.name + "' in " + dest + " ...");
}
```

Notice what `install` does **not** do: transform anything. A skill is already in the format agents consume - a folder with a `SKILL.md` - so installing is a byte-for-byte materialization of the locked git tree into `.claude/skills/`: no rendering, no templating, no rewriting for the agent's benefit. And that identity is load-bearing, not laziness: it's the only reason a digest recorded from the *source* tree can be checked against the *installed* directory at all. The day install transforms something, the verification model dies with it.

Last of the trio, `status()` is the install-time verification turned into a standalone check - the same hash pointed at the installed directories, with any `MISSING`/`DRIFTED` turning the exit code non-zero, so any script can gate on it.

## Java does the hashing

My favorite piece of the milestone: drift detection needed a way to hash what's actually installed in `.claude/skills/`, where there is no `.git` to ask. So `GitTreeHasher` re-implements git's object hashing in ~80 lines of pure Java - blobs as `sha1("blob <size>\0" + content)`, trees as sorted `"<mode> <name>\0" + sha` entries, with git's quirky rule that directories sort as if their name ended in `/`. The lockfile digest and the on-disk hash speak the same language, so `install` can verify what it just wrote and `status` can catch a single flipped byte.

The sort line carries most of that weight:

```java
// git sorts tree entries as if directory names had a trailing '/'
entries.sort(Comparator.comparing(e -> e.isDir() ? e.name() + "/" : e.name()));
```

Bytewise, `-` (0x2D) sorts before `/` (0x2F), so under git's rule a file named `sub-file.txt` comes *before* a directory named `sub` - while a naive name sort puts them the other way around. Wrong order means different tree bytes, which means a different SHA that matches nothing.

The main discussion with Claude was about how to *prove* that hasher correct. A fixed expected-SHA string in the test would just assert that the code does what the code does. Instead the test builds a directory designed to hurt - nested dirs, an executable script, and a file named `sub-file.txt` that sorts differently once you know the trailing-`/` rule - then asks the **real git** (`git init && git add -A && git write-tree`) for the answer and requires our Java to match it:

```java
String expected = gitTreeSha(repo);              // real `git write-tree`
assertEquals(expected, GitTreeHasher.treeSha(repo));
```

Git itself is the oracle: any divergence from git's object format - a wrong mode string, naive sorting, a missed header byte - fails the test, not just the cases I thought of. All eleven tests (hasher, source parsing, and a full update->install->drift->repair cycle against local fixture repos, no network anywhere in the suite) came back green:

```text
[INFO] Tests run: 11, Failures: 0, Errors: 0, Skipped: 0
[INFO] BUILD SUCCESS
```

And the real-world run, against the actual ai-skills repo - including sabotaging an installed file to watch `status` catch it:

```text
$ diderot update
locked making-of      https://github.com/sunix/ai-skills@64358bc56441 (tree:0f755a27d65b...)
$ diderot install
installed making-of   -> .claude/skills/making-of (tree:0f755a27d65b... verified)
$ echo "sabotage" >> .claude/skills/making-of/SKILL.md && diderot status
DRIFTED  making-of    .claude/skills/making-of      # exit 1
$ diderot install && diderot status
ok       making-of    .claude/skills/making-of      # exit 0
```

Final cross-check, the one that made me smile: `git rev-parse origin/main:skills/documentation/making-of` on ai-skills returns `0f755a27d65b67d80d6d1ee2ed0d8a7963fa8f23` - character for character the digest diderot had written into `diderot.lock`.

## Closing part one

The git chapter ends here, and it ends whole: a project can declare its skills, lock them, install them byte-for-byte anywhere, and catch anyone - human or agent - who bends an installed file. Two stories are already queued for part two: the welcoming `install` (resolve by itself when there's no lock yet, `--frozen` for unattended runs), and M2, where ORAS finally enters - `push` to a real registry, `oci://` sources resolved by digest, and cosign.

The full journal lives in the repo: [github.com/sunix/diderot](https://github.com/sunix/diderot) - MAKING-OF.md, dead ends included - and everything this chapter describes is in [PR #2](https://github.com/sunix/diderot/pull/2). See you in part two.

## The series

diderot's journal, one post per chapter. You are on part 1:

1. **From a name to a git-backed lockfile** - the gap survey, the Go-vs-Java reversal, stealing Helm's homework, and `update` / `install` / `status` over git sources with content-digest locking. **You are reading this one.**
2. [OCI at last: skills in real registries](/posts/building-diderot-skills-in-real-oci-registries-one-prompt-at-a-time-part-2/) - `push` as an OCI artifact, `oci://` sources pinned by digest, and a skill published by a GitHub Action then installed into a project that had never heard of diderot. **Read this next.**
3. One line to install it - GraalVM native binaries, a `curl | sh` installer that refuses tampered bytes, a JBang catalog entry, and the version-reporting bug only a packaging milestone would have found. Written, but not on the blog yet; [read the chapter in the repo](https://github.com/sunix/diderot/blob/main/doc/making-of/03-packaging-and-install.md) in the meantime.
4. All I wanted was versioned skills - using diderot for real to publish an actual library of skills: per-skill versions, the design argument I lost, and the five things in the way. Written, but not on the blog yet; [read the chapter in the repo](https://github.com/sunix/diderot/blob/main/doc/making-of/04-releasing-the-skills.md) in the meantime.

And the chapters that don't exist yet, in the order they are queued:

- **`add` and `remove`** - declaring a skill still means hand-editing `diderot.yaml`, which is the next thing to fix.
- **Semver ranges** - `version: "^1.0.0"` resolved from the tag list - the feature part four built the prerequisite for ([#19](https://github.com/sunix/diderot/issues/19)).
- **Signing** - cosign and sigstore-java, drafted in [#7](https://github.com/sunix/diderot/pull/7) and deliberately parked until it has a milestone to belong to.

The journal is written as the work happens, so the list grows from the top of that queue.
