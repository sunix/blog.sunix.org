---
layout: post
title:  EclipseCon France 2016 - Language Server Protocol, Eclipse Che and Commit Strip
modified: 2016-06-24
categories: [articles]
tags: 
  - eclipseche
  - typescript
  - vscode
  - eclipsecon
  - commitstrip
  - languageserverprotocol
comments: true
contribute: https://codenvy.com/f?id=k307cp4ad7ib5gex
---


This blog post will be soon available in French in [Serli blog site](http://www.serli.com/blog/articles)

Once again, I've spent a very nice week at EclipseCon France in Toulouse. Thank you Eclipse foundation for this great event! This year I've learned a lot and had very nice and interesting discussions about Eclipse technologies.

To me, the guest star to this conference was the Language Server Protocol. Brought to us by Microsoft TypeScript and its Language server. It had been featured during 3 presentations:

<!-- more -->

- [Xtext new adventure with Che](https://www.eclipsecon.org/france2016/session/xtext%E2%80%99s-new-adventures-che)
- [JSDT 2.0](https://www.eclipsecon.org/france2016/session/jsdt-20)
- [TypeScript: Feedback from a real life project within TypeScript and the future of JavaScript](https://www.eclipsecon.org/france2016/session/typescript-feedback-real-life-project-within-typescript-and-future-javascript-sponsored)



# Language Server Protocol
During the Xtext on Che presentation, Sven was telling us his story about providing Xtext to Eclipse Che: at the very beginning, his plan to implementing Xtext for yet another new editor looked like the straw that breaks the camel's back: many new editors are showing up every month and it would have been too much work to maintain and support them all. Instead of that, he introduced us to the [TypeScript/VSCode Language Server Protocol](https://github.com/Microsoft/language-server-protocol) that would kill two birds with one stone: 

Many programing languages already provide a service API for developers tools and IDE. What TypeScript is doing is making its Language Server Protocol a standard and an API that can be reused not only by Microsoft VSCode, but also by any other editors. This is not only for TypeScript: the language server could have multiple implementations using the same and unique SPI.

So sven is starting 2 things:

- Providing an XText implementation to Language Service SPI
- Making Eclipse Che compatible with the Language Server Protocol

They all would get benefits:

- Xtext developers won’t have to create and maintain each editor implementations.
- Eclipse Che would get benefit of all languages that implement the Language Service SPI 

During JSDT by Ilya Buziuk and the TypeScript talk by Sebastien Pertus, the Eclipse plugin from Angelo Zerr was featured. Angelo has been working on a Eclipse Ide TypeScript plugin that use the Language Server Protocol!!! For your information, you can write classic Javascript in TypeScript and thus all the existing Eclipse Javascript plugin will be replace by this.
It seems that the api is based on TypeScript JSON RPC, to me, maybe it would be interesting to expose this with websocket and do something similar to Eclipse Flux.




# Eclipse Che
Eclipse Che was also a subject that interested a lot of people. We had 4 slots related to Eclipse Che:

- [Extending Eclipse Che to build custom cloud IDEs](https://www.eclipsecon.org/france2016/session/extending-eclipse-che-build-custom-cloud-ides)
- [Code in the cloud with Eclipse Che and Docker](https://www.eclipsecon.org/france2016/session/code-cloud-eclipse-che-and-docker)
- [Xtext’s New Adventures With Che](https://www.eclipsecon.org/france2016/session/xtext%E2%80%99s-new-adventures-che)
- Lunchtime Demo: Pair programming in the cloud with Eclipse Che

The workshop was the first one we were doing for Eclipse Che. The goal of this workshop was to create a ready to use developer environment for the Go language:  Attendees learned how to create a custom workspace stack with Docker, how to add menu actions in the Cloud IDE, etc. It is a good way to understand how things are architectured in Che.

During the talk ”Introduction to Eclipse Che”, Stevan and Florent made a great introduction to Eclipse Che, showing Che Java IDE capabilities from basic code completion to remote debugging. They talked about the concept of universal workspace and finished with the Github contribution factory link (which is BTW a contribution made with love by Serli team): each click triggers a clone of a github project in a preconfigured Che workspace. They made a live demo showing a contribution to a github project in less than 1 minutes … explanations included.

At lunch time, I did myself a live demo of my pair programming plugin for Che. This is the very first step to have a Google Docs like experience.




# CommitStrip
If you, “coders” haven’t heard about Commit strip, it’s time to go to <http://commitstrip.com> and have fun reading their comics.
Thomas and Etienne did a great keynote of Eclipsecon France, presenting their work through the subject [Explaining Code to My Mom](https://www.eclipsecon.org/france2016/session/explaining-code-my-mom-cassiop%C3%A9e). Through great jokes and comics slides, they managed to share their experience of as “coders” in a very fun way. At some point, we all felt like it was our own coder life experience. By the way, my favorite strip is this one:  [
Why Marty and the Doc might not arrive after all](http://www.commitstrip.com/fr/2015/10/21/why-marty-and-the-doc-might-not-arrive-after-all/) .


# Last but not least
... here are some notes of other presentations I attended and liked:

### [What every Java developer should know about AngularJS.](https://www.eclipsecon.org/france2016/session/what-every-java-developer-should-know-about-angularjs)
By Maximilian Koegel and Edgar Mueller [Eclipsesource]

This was a complete starting guide for Java developers who would like to start with JavaScript, and AngularJS. Covering configuration tools, TypeScript, test framework etc…
I would love to help them setting up a developer environment with Eclipse Che for the next Eclipse Con ;)

### [What's new in Eclipse IDE and the ecosystem around it](https://www.eclipsecon.org/france2016/session/whats-new-eclipse-ide-and-ecosystem-around-it)

By Sopot Cela and Mickael Istria [Red Hat]

This talk was about news and noteworthy that is coming with the next Neon release.
These are the ones I’m looking forward to test in Neon

 - Theme: disable theme to use native GTK widget
 - Zoom in text editor (with Ctrl +/-)
 - Completion substring in jdt
 - Smart project import (types)
 - Auto save
 - Auto suggest editors in market place if no detected installation.

Ideas for the future:

 - GTK on the web: interesting, running Eclipse IDE on Che would be even easier (broadway)

### [The State of Docker and Vagrant Tooling in Eclipse](https://www.eclipsecon.org/france2016/session/state-docker-and-vagrant-tooling-eclipse )
By Roland Grunberg [Red Hat]

He presented the status of the docker and Vagrant tools in Eclipse.

#### Docker tooling:
 - UI for pulling and search in registries
 - Exec shell running docker container (which seems to be a simple docker exec)

#### Vagrant tooling:
 - Perspective
 - Launch VMs
 - Start stop ssh
 - Access to a docker container launched with vagrant

#### Future:
 - Support of compose
 - Debug support for any language (c/c++)

### [Java 9 support in Eclipse](https://www.eclipsecon.org/france2016/session/java-9-support-eclipse )
By Sasikanth Bharadwaj [IBM]

It was mainly an overview of the Jigsaw support in Eclipse. For the time being, to get the support in Eclipse (even neon) we need to install a patch from the market place.

Sasikanth explained how Jigsaw module-info.java worked and how to create and configure jigsaw modules in Eclipse. At the moment, the content assist is very basic: only syntax completion on module-info.java is supported, no completion on packages to be exported. Though these stuff are very promising.

### [JSDT 2.0](https://www.eclipsecon.org/france2016/session/jsdt-20)
By Ilya Buziuk [Red Hat]

JSDT is not dead!!! It is actually getting renewed and is trying to fill the gap with other competitors. After all we are more and more full stack developers. During this session, we had a live demo of the following features:

- Run / debug of nodejs code
- Json editor (by Angelo Zerr)
- Grunt / Gulp support

He also told us about a new Eclipse product for web development that will come with Neon: EPP

### [TypeScript: Feedback from a real life project within TypeScript and the future of JavaScript](https://www.eclipsecon.org/france2016/session/typescript-feedback-real-life-project-within-typescript-and-future-javascript-sponsored)
By Sebastien Pertus [Microsoft]

This talk was a very good introduction to TypeScript with live demos of different aspect of the language using eclipse tools.
For the demo, he used Angelo’s plugin:

- Comparison of a TypeScript class with the generated js files (in es5 and es6)
- Decorator usage (a decorator is like a Java annotation)
- Running serializer
- Not nullable


### [Fast Unit tests for Eclipse plugins - possible architectures and available tooling](https://www.eclipsecon.org/france2016/session/fast-unit-tests-eclipse-plugins-possible-architectures-and-available-tooling)
By Aurelien Pupier [Red Hat]

In my opinion, unit tests were not well covered in the very first PDE design. And this why most of us still have troubles in finding the right setup in Eclipse plugins development for unit testing. Aurelien shared his experience in this exercise and talked about different options on where to organize our unit tests in our eclipse plugins source code.

- In a source folder in the same project
- In a separate plug-in with “optional” require-bundle
- In a fragment

The samples of his demo are available in this Github project <https://github.com/apupier/EclipsePlugins-Testing>

Aurelien also showed us few tools he is using to be more productive with unit tests:

- More unit : Provide short cut and navigation features for testing: For instance, Ctrl-G to switch the open editor from the class to test to tested class. 
- Infinitest: auto run test on the fly of modified test classes. Provides instance feedback while writing tests.
- eclEmma: a tool that shows code coverage in Eclipse.

### [A generic REST API on top of Eclipse CDO for web-based modelling](https://www.eclipsecon.org/france2016/session/generic-rest-api-top-eclipse-cdo-web-based-modelling)
By Christophe Ponsard

These guys demoed their modeling application that runs EMF behind the scene. The demo contained a Web UI of the EMF based application. It looks promising if we keep in mind our wishes to have EMF running inside Eclipse CHE.

# The end
That’s all guys, I’m very sorry not having covered the other subjects that were present in the conference (IoT, Science, Modeling …). I will keep this blog updated and will provide links to any recorded talk available online.
Thank you to the Eclipsecon people for organizing this so great event :)

