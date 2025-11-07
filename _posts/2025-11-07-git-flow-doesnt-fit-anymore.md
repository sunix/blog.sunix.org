---
layout: post
title: "Why I Think Git Flow Doesn't Fit Most Projects Anymore"
modified: 2025-11-07
categories: [articles, Howto]
tags: 
  - Git
  - Git Flow
  - Trunk-Based Development
  - CI/CD
  - Software Development
  - DevOps
comments: true
---

I've used **Git Flow** on quite a few projects over the years — and honestly, I've reached a point where I don't think it's the right choice for most modern teams anymore.

It's not that Git Flow was bad. In fact, when Vincent Driessen came up with it back in 2010, it made a lot of sense. We didn't have solid CI/CD pipelines yet. Releases were manual. Teams were smaller and slower. Having a clean branching model for "features," "releases," and "hotfixes" was a huge step forward at the time.

But now? It just feels outdated.

<!-- more -->

## The Problems I Keep Running Into

Every time I see Git Flow used in a modern setup — with automated testing, continuous integration, and frequent releases — it causes more friction than it solves.

Here's why:

* **Too much complexity.** The number of branches you have to maintain (`develop`, `release`, `hotfix`, etc.) adds unnecessary overhead. Everyone ends up spending time managing branches instead of shipping code.
* **Messy history.** With all the merges going back and forth, the Git history becomes unreadable. You lose the nice, linear flow that helps understand what actually happened.
* **Outdated assumptions.** Git Flow was built for a world of scheduled releases. But with CI/CD, we can deploy anytime — sometimes multiple times a day. The model doesn't fit that reality.
* **Long-lived branches and circular merges.** These "living" branches constantly merging into each other create confusion and technical debt. It's too easy to break things accidentally.
* **Rigid structure.** Modern development needs flexibility. You want to be able to test, experiment, and integrate quickly. Git Flow gets in the way of that.

## What Works Better for Me

I've had much better results using **Trunk-Based Development**.
Everyone works off `main` (or `master`), creates short-lived branches, and merges back quickly — ideally after just a day or two. It keeps things simple and stable.

A few key practices that help:

* Use **`git rebase`** instead of merging in your Pull Requests. It keeps the history linear and easy to read.
* Create **maintenance branches** only when you really need them — for example, to patch already released versions. When that happens, just use `git cherry-pick` to backport fixes.

This setup keeps your main branch always production-ready, while still letting you move fast.

## The "Shift Left" Connection

If your team has already shifted left — meaning you test, review, and automate early in the process — you don't need extra "safety" branches like `develop` or `release`.

CI runs your tests before merging. You can spin up preview environments for every Pull Request. Your main branch stays stable by design.

That's the world we live in now, and it makes Git Flow's complexity unnecessary.

## So… What Do You Think?

I know this might sound opinionated, and I'm not saying Git Flow should *never* be used. There are still cases — maybe in projects with slow release cycles or strict approval gates — where it can make sense.

But for most modern teams? I think it's time to move on.

I'd really like to hear from others:

* Are you still using Git Flow?
* Have you switched to something simpler like Trunk-Based Development or GitHub Flow?
* What's worked (or failed) for your team?

Let's talk about it — I'm genuinely curious how others see this shift.
