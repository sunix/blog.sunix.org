---
layout: post
title: "Building a Gift Card Management App with GitHub Copilot: My First Completed Side Project"
modified: 2025-11-14
categories: [articles, Howto]
tags: 
  - GitHub Copilot
  - AI-Assisted Development
  - PWA
  - Side Projects
  - Developer Productivity
comments: true
---

Like many developers, my life is littered with unfinished side projects. But this time was different. This time, I had GitHub Copilot as my coding companion, and I actually finished. Let me tell you how AI-assisted development helped me build a [gift card management application](https://sunix.github.io/gift-card/) that I actually use every day — and I did it almost entirely without opening an IDE.

<!-- more -->

## The Problem: Managing Gift Cards in the Real World

My wife works at Red Hat, and like many companies, they offer a CSE (Comité Social et Économique) benefit program. One of the perks is getting gift cards with discounts — including a nice 5% discount at a major supermarket chain. Sounds great, right?

Here's the catch: I was using Google Wallet on my mobile to track these cards, but I could never keep the balance updated. After each shopping trip, I'd forget to update it. Then I'd be at the checkout, unsure if I had enough on the card or not. It was frustrating enough that I decided to build something better — a simple app where I could display the barcode to be scanned at the store and manually update the balance right there.

## Another Side Project... Or Is It?

"Alright, here we go again," I thought. Another side project that I'd start enthusiastically and abandon halfway through. My digital graveyard of unfinished projects was already pretty crowded.

![Side projects comic strip](/images/side-project-comic.png)
*[Source: CommitStrip.com](https://www.commitstrip.com/en/2014/11/25/west-side-project-story/?setLocale=1) - A story we all know too well.*

But this time felt different. I had been experimenting with AI tools. I had recently used ChatGPT to build a sample website for a tennis club in just a few days. The speed was incredible. Before starting this gift card app, I even consulted ChatGPT about the technology stack — should I build a PWA (Progressive Web App) or go native with Kotlin? Based on the advice, I decided on a PWA for its cross-platform benefits and easier maintenance.

Still, I was skeptical. Would I actually finish this one?

## The Game Changer: Discovering GitHub Copilot Workspace

Then I stumbled upon something that changed everything: the ability to assign entire projects to GitHub Copilot directly in GitHub. Not just code completion or suggestions, but actual project creation and feature development.

I decided to give it a shot. I created an issue with my requirements and assigned it to Copilot. My first prompt was straightforward:

> "Create a Progressive Web App for managing gift cards. The app should allow users to:
> - Store gift card information (name, barcode number)
> - Generate and display barcodes to be scanned at stores
> - Track and update card balances
> - Work offline
> - Install as a mobile app"

![Assigning an issue to Copilot](/images/copilot-issue-assignment.png)
*When you assign an issue to Copilot, it immediately creates a "[WIP]" Pull Request and starts working.*

And then... I waited. Copilot started working. It created a project structure, set up the PWA infrastructure, implemented the basic features, and even opened a Pull Request. It was like having a junior developer who worked incredibly fast but needed guidance.

## The Reality Check: When AI Needs Direction

Of course, it wasn't perfect. The barcode generation system Copilot created was problematic. Instead of using an existing library, Copilot tried to reimplement Code128 barcode generation from scratch. While the generated barcodes looked right visually, they weren't compatible with the actual barcode readers at stores. There are nuances to barcode standards — Code128, Code39, EAN-13, and others — each with specific encoding rules that need to be precise down to the pixel.

I created an issue to fix it, but the improvements weren't quite right either. I got frustrated and did what any developer would do: I asked ChatGPT for help separately, opened up my IDE, started debugging manually. Eventually, I discovered [bwip-js](https://github.com/metafloor/bwip-js), a reliable barcode generation library that handles all the complexity correctly. I manually amended [Copilot's PR](https://github.com/sunix/gift-card/pull/3) to integrate bwip-js, and finally, the barcodes worked perfectly at the store.

Only later did I discover what I should have done from the start: just mention `@copilot` in the issue comments! When you need Copilot to adjust something, iterate on a feature, or fix a problem, you don't need to spin up a whole new workflow. Just comment on the issue or PR, mention @copilot, and it will respond and make the necessary changes. This was a game-changer for me.

## Finding My Rhythm: The Perfect Workflow

Once I understood how to work with Copilot effectively, I developed a rhythm that actually worked:

1. **Create an issue** - Write down what I want in clear, specific terms
2. **Assign it to @copilot** - Let it do the heavy lifting
3. **Review the PR** - Look at what Copilot created
4. **Provide feedback** - Use `@copilot` mentions in comments for adjustments
5. **Iterate** - Repeat steps 3-4 until satisfied
6. **Merge and move on** - Deploy and create the next issue

What I loved most was the visibility into Copilot's "thinking" process. I could see the session logs, watch it reason through problems, and even see screenshots it took when testing with Playwright. It was like pair programming with someone who documents everything they do.

![Copilot's checklist of steps](/images/copilot-checklist.png)
*Copilot shows you exactly what steps it's taking, with checkboxes showing progress. You can see the original prompt and watch it work through each task.*

![Code changes by Copilot](/images/copilot-pr-diff.png)
*The Pull Request shows all the changes Copilot made, just like any other PR. You can review the diff and request changes.*

The workflow became almost meditative. I'd create issues during my coffee break, assign them to Copilot, and come back later to review and comment. No pressure to sit down for a long coding session. No context-switching overhead. Just incremental progress, issue by issue.

## The Result: A Finished Project

After several iterations over a few weeks, I had a working app. Not just a proof of concept, but something I actually deployed and use regularly:

- 📱 **Progressive Web App** that installs on my phone
- 📊 **Barcode generation** using bwip-js that works reliably with store scanners
- 💰 **Balance tracking** that I can update on the go
- 🔒 **Offline support** so it works everywhere
- 🎨 **Clean UI** that's simple and functional

You can check out the code on [GitHub](https://github.com/sunix/gift-card) and try the [live application](https://sunix.github.io/gift-card/) yourself.

But here's what really matters: **I finished it**. For the first time in years, I completed a side project from start to finish. It's deployed. It's in use. It solves my actual problem.

## Lessons Learned

Working with GitHub Copilot taught me several things:

1. **AI is a collaborator, not a replacement** - You still need to guide, review, and make decisions
2. **Clear communication matters** - The better your issue descriptions, the better the results
3. **Iteration is key** - Don't expect perfection on the first try; embrace the feedback loop
4. **Learn the tools** - Understanding features like `@copilot` mentions saves tons of time
5. **Small steps win** - Breaking work into issues and letting AI handle them one by one actually works

## The Cherry on Top

Want to know something funny? This blog post itself was generated with the help of Copilot. I created an issue asking for a blog post about the experience, provided the structure I wanted (following classical rhetoric: exordium, narration, argumentation, refutation, and peroration), and iterated on the draft with `@copilot` mentions until it captured my voice and experience.

It's turtles all the way down.

## Final Thoughts

I'm genuinely excited about this new way of building things. GitHub Copilot didn't just help me finish a project — it changed how I approach side projects entirely. Instead of thinking "I need to set aside a weekend to code this," I think "I can break this into issues and make progress during coffee breaks."

The barrier to finishing projects isn't just time or skill anymore. It's about having the right collaboration model. For me, that model is now: me plus AI, working asynchronously, iterating on issues, and actually shipping.

If you've been putting off that side project, maybe give GitHub Copilot a try. Create an issue. Assign it to @copilot. See what happens. You might just finish something for once.

And when you do, it feels pretty great.

---

*Have you used GitHub Copilot or other AI coding assistants for your projects? What's your experience been? I'd love to hear about it in the comments below.*
