---
layout: layouts/post.html
title: A Universal Eclipse IDE
date: "2016-05-16"
modified: 2016-05-16
category: "articles"
tags: 
  - che
  - eclipse
  - docker
  - ide
comments: true
contribute: https://blog.sunix.org/factory?url=https://github.com/sunix/blog.sunix.org/tree/gh-pages
---

This year, I am participating in Google Summer of Code as a mentor for the Eclipse umbrella organization. I've submitted a few ideas about Eclipse Che and Eclipse Flux, and some of them have been selected. I am mentoring the project "Pair Programming with Eclipse Che," which involves improving an existing prototype I demonstrated at various developer conferences. You can check out the [GitHub pair programming Che extension](https://github.com/sunix/che-plugin-flux-live-edit).

A few days ago, my padawan Randika encountered issues setting up his Eclipse IDE on the latest Ubuntu. He faced problems with GTK3 and experienced strange performance issues. I suggested he run the Eclipse IDE inside a Docker container, which worked fine. However, since we are not in the same location, it was difficult to assist him with advanced IDE configurations like GWT super dev mode.

I posted in our Gitter chat room, "Let's try something fun," and we decided to launch Eclipse IDE inside an Eclipse Che Docker workspace hosted by Codenvy beta. When I posted a screenshot of this a few hours later, I received a lot of interactions on Twitter from the community: “Say whaaat?”, “WTF? :)”, “Got some more info? :)“, and “Worth writing a blog post on how to set this up?”

<!-- more -->

## Eclipse Mars Running Inside Eclipse Che

Randika ended up writing a blog post explaining how we did this: [Run Eclipse Mars IDE inside Che](http://www.rnavagamuwa.com/open-source/run-eclipse-mars-ide-inside-eclipse-che/). This trick helped me assist Randika in setting up his GWT super dev mode in Eclipse Mars, though it wasn't anything revolutionary like Che.

## Universal Eclipse IDE

So, what's next? You might have seen Tyler’s keynote at EclipseCon 2016 about “Universal Workspaces” [EclipseCon keynote](http://www.infoq.com/presentations/eclipse-che-eclipsecon-2016). Here are my thoughts on building a Universal Eclipse IDE.

In Randika’s demo, although "ssh -X" worked fine on our local computer, I experienced some latency when it ran in the Cloud. On the other hand, Che lacks many features and can't yet fill the gap compared to the classic Eclipse Desktop IDE. Additionally, when you install an Eclipse IDE plugin, it prompts you to restart. But why? Isn't it running on OSGi, one of its key features? I've heard someone say the problem lies with SWT and that it should be replaced with JavaFX. I believe we should use web technologies because they are the present and the future.

So, I have a dream: that one day, every single Eclipse plugin will have its services exposed in REST and/or WebSocket.

I have a dream that Eclipse developers can write plugins that work for both Che and the classic Eclipse IDE.

I have a dream that Che developers won't reinvent the wheel but will reuse Eclipse IDE core OSGi bundles.

I have a dream that we won't have to restart our IDE after installing a plugin.

I have a dream that one day we will all use a Universal Eclipse IDE.

## Yes, We Can!

In my opinion, this isn’t that hard to achieve:

- The Eclipse core bundle could expose a REST API or WebSocket. We would need an additional "org.eclipse.bundle.web" alongside the "org.eclipse.bundle.ui," both using the "org.eclipse.bundle.core" bundle. Eclipse would run with a Jetty HTTP server bundle.
- We would run a headless Eclipse IDE inside a Che Docker-based workspace with X forwarding.
- Eclipse Che cloud IDE client extension and Orion could connect to these Eclipse web services.
- Alternatively, we could improve Eclipse Flux, which started something similar.

Everyone would benefit from this:

- Che could reuse existing bundles of the Eclipse IDE.
- Che could access plugins from the existing Eclipse Marketplace.
- For the Eclipse Desktop IDE, it would be time to rethink the design and decouple the UI from core services.
- JavaScript works everywhere, eliminating the need to maintain SWT graphical libraries for each target architecture.
- We could use any UI library and even mix them: SWT for navigation, GWT for view content, pure Orion JavaScript for the editor, and AngularJS for the marketplace.

## Recap

To me, the Eclipse Universal IDE would be:

- Eclipse Che with a Docker-based workspace
- Eclipse IDE running in the container
- Eclipse IDE exposing web services
- Eclipse Che Cloud IDE (the client side running in the browser) consuming services exposed by the Eclipse IDE and core services exposed by the Che server

Eclipse Flux could be an option for the communication layer.
