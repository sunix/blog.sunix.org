---
layout: post
title: A Universal Eclipse IDE
modified: 2016-05-16
categories: [articles, Howto]
tags: 
  - che
  - eclipse
  - docker
  - ide
comments: true
contribute: https://codenvy.com/f?id=6z5761px6w9exebd
---

This year I am participating to the Google Summer of Code as a mentor for the Eclipse umbrella organisation. I have submitted few ideas about Eclipse Che and Eclipse Flux and some of them have been selected. As far as i’m concerned, I am going to mentor the subject “Pair Programming with Eclipse Che” which will consist in improving an existing prototype I have demo-ed in various developer conferences [Github pair programming che extension](https://github.com/sunix/che-plugin-flux-live-edit)

Few days ago, my padawan Randika encountered issue to set up his Eclipse IDE on his latest Ubuntu. He had issues with GTK3 and weird performance problem. So I first suggested him to run Eclipse IDE inside a Docker container. It worked fine, but as we are not living in the same location, I couldn’t help him efficiently for advanced IDE configuration such as GWT super dev mode.
I posted to our Gitter chat room “Let's try something fun” and we started launching Eclipse IDE inside an Eclipse Che Docker workspace and hosted by Codenvy beta.
When I posted a screenshot of this few hours later, I’ve got a lot of interactions on Twitter from the community: “Say whaaat?”, “WTF? :)”, “got some more info ? :)“, “worth writing a blog post how to set this up?”

<!-- more -->

## Eclipse Mars running inside Eclipse Che
So here this is how Randika ended up in writing a blog post explaning how we did this: [Run Eclipse Mars IDE inside Che](http://www.rnavagamuwa.com/open-source/run-eclipse-mars-ide-inside-eclipse-che/) .
There is nothing new, nothing revolutionary like Che ;) but thanks to this trick, I managed to help Randika setting up his GWT super dev mode in Eclipse Mars.


## Universal Eclipse IDE
OK, so now what? You have probably seen Tyler’s keynote at EclipseCon 2016 talking about “Universal workspaces” [Eclipsecon keynote](http://www.infoq.com/presentations/eclipse-che-eclipsecon-2016) . I’d like to share my thoughts about building a Universal Eclipse IDE.

In Randika’s demo, although “ssh -X” worked fine on our Iocal computer I had a bit of latency when it is running in the Cloud.
On another side, Che is missing a lot of features and I don’t see how it can fill the gap compared to the classic Eclipse Desktop IDE.
At last, when you install and Eclipse IDE plugin, it asks you to restart …. But what ? I thought it was running on OSGi and that was one of its key features? I heard someone saying that the problem come from SWT and that we should replace it with JAVA FX. I believe we should use web technologie, because it’s the future !!! … sorry the present !!!!

So ... I have a dream ...

... that one day each single Eclipse plugin would have its services exposed in Rest and/or Websocket.

I have a dream that one day Eclipse developers could write plugins that works either for Che or classic Eclipse IDE.

I have a dream today!

I have a dream that one day Che developers won’t reinvent the wheel and reuse Eclipse IDE core OSGi bundles.

I have a dream that one day we won’t have to restart our IDE after a plugin installation.

I have a dream that one day we would all use a Universal Eclipse IDE.

## Let's do it
In my opinion, this isn’t that hard:
Eclipse core bundle could expose Rest API or Websocket. We would just need to have an additional org.eclipse.bundle.web next to the org.eclipse.bundle.ui and org.eclipse.bundle.core bundles. Eclipse would run with a jetty http server bundle.

- We would run that headless Eclipse IDE inside a Che docker based workspace as we did previously but with X forwarding.
- Eclipse Che cloud IDE client extension and Orion would connect to this Eclipse web services.
- Or we could think about improving Eclipse Flux that started to make something similar.

Everyone would get benefits from it:

- Che would reuse existing bundles of Eclipse IDE
- Che could get plugins from the existing Eclipse Marketplace
- For Eclipse Desktop IDE, it would be time to rethink design and decouple the UI from core services
- Javascript works everywhere. No need to maintain all the SWT graphical libraries binding for each target architectures
- We would be free of using any UI library, we could even mix them: SWT for navigation, GWT for view content, pure Orion javascript for the editor and Angular JS for the marketplace :)

## Recap
To recap, to me, the Eclipse Universal IDE would be:

- Eclipse Che with a docker-based workspace
- Eclipse IDE running in the container
- Eclipse IDE exposing web services
- Eclipse Che Cloud IDE (the client side that runs in the browser) consuming services exposed by the Eclipse IDE and core services exposed by the Che server

Eclipse Flux could be an option for the communication layer.
