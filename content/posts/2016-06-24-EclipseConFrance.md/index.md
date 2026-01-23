---
layout: layouts/post.html
title: "EclipseCon France 2016: Language Server Protocol, Eclipse Che, and Commit Strip"
date: "2016-06-24"
modified: 2016-06-24
category: "articles"
tags: 
  - eclipseche
  - typescript
  - vscode
  - eclipsecon
  - commitstrip
  - languageserverprotocol
comments: true
contribute: https://blog.sunix.org/factory?url=https://github.com/sunix/blog.sunix.org/tree/gh-pages
---

This blog post will soon be available in French on the [Serli blog site](http://www.serli.com/blog/articles).

Once again, I spent a fantastic week at EclipseCon France in Toulouse. A huge thank you to the Eclipse Foundation for organizing this amazing event! This year, I learned a lot and had many interesting discussions about Eclipse technologies.

<!-- more -->

### Language Server Protocol

To me, the star of the conference was the Language Server Protocol (LSP), introduced by Microsoft's TypeScript and its Language Server. It was featured in three presentations:

- [Xtext's New Adventures with Che](https://www.eclipsecon.org/france2016/session/xtext%E2%80%99s-new-adventures-che)
- [JSDT 2.0](https://www.eclipsecon.org/france2016/session/jsdt-20)
- [TypeScript: Feedback from a Real-Life Project within TypeScript and the Future of JavaScript](https://www.eclipsecon.org/france2016/session/typescript-feedback-real-life-project-within-typescript-and-future-javascript-sponsored)

#### Language Server Protocol

During the Xtext on Che presentation, Sven described providing Xtext to Eclipse Che. Initially, implementing Xtext for another new editor seemed overwhelming due to the rapid emergence of new editors. Instead, he introduced the [TypeScript/VSCode Language Server Protocol](https://github.com/Microsoft/language-server-protocol), which offers a standardized API reusable by any editor, not just Microsoft's VSCode.

This approach has two main benefits:
1. Xtext developers won't need to create and maintain implementations for each editor.
2. Eclipse Che will benefit from languages that implement the Language Service SPI.

In his talk, Sven outlined two main initiatives:
- Providing an XText implementation of the Language Service SPI
- Making Eclipse Che compatible with the Language Server Protocol

During the JSDT talk by Ilya Buziuk and the TypeScript talk by Sebastien Pertus, they highlighted the Eclipse plugin from Angelo Zerr, which uses the Language Server Protocol. This means existing Eclipse JavaScript plugins can be replaced by this new approach.

### Eclipse Che

Eclipse Che was another hot topic at the conference, featured in four sessions:
- [Extending Eclipse Che to Build Custom Cloud IDEs](https://www.eclipsecon.org/france2016/session/extending-eclipse-che-build-custom-cloud-ides)
- [Code in the Cloud with Eclipse Che and Docker](https://www.eclipsecon.org/france2016/session/code-cloud-eclipse-che-and-docker)
- [Xtext’s New Adventures with Che](https://www.eclipsecon.org/france2016/session/xtext%E2%80%99s-new-adventures-che)
- Lunchtime Demo: Pair Programming in the Cloud with Eclipse Che

In the workshop, attendees learned how to create a custom developer environment for the Go language using Docker and add menu actions in the Cloud IDE.

During the "Introduction to Eclipse Che" talk, Stevan and Florent demonstrated Che Java IDE's capabilities, from basic code completion to remote debugging. They discussed the concept of universal workspaces and introduced the Github contribution factory link, developed by the Serli team, which enables quick project cloning in a preconfigured Che workspace.

<iframe width="560" height="315" src="https://www.youtube.com/embed/ekTT36vwHJI?list=PLy7t4z5SYNaRJff0KBMbubOaj8gevvML4" frameborder="0" allowfullscreen></iframe>

I also did a live demo of my pair programming plugin for Che, showcasing the first step towards a Google Docs-like experience in coding.

### CommitStrip

For those who haven't heard of Commit Strip, check out their comics at [Commit Strip](http://commitstrip.com). Thomas and Etienne gave a great keynote at EclipseCon France, sharing their experiences as coders through humor and comics in the session [Explaining Code to My Mom](https://www.eclipsecon.org/france2016/session/explaining-code-my-mom-cassiop%C3%A9e). My favorite strip is [Why Marty and the Doc Might Not Arrive After All](http://www.commitstrip.com/fr/2015/10/21/why-marty-and-the-doc-might-not-arrive-after-all/).

<iframe width="560" height="315" src="https://www.youtube.com/embed/x2EQJpYBdc0?list=PLy7t4z5SYNaRJff0KBMbubOaj8gevvML4" frameborder="0" allowfullscreen></iframe>

### Other Notable Sessions

Here are some other sessions I attended and enjoyed:

#### [What Every Java Developer Should Know About AngularJS](https://www.eclipsecon.org/france2016/session/what-every-java-developer-should-know-about-angularjs)
By Maximilian Koegel and Edgar Mueller [EclipseSource]

This was a comprehensive guide for Java developers wanting to start with JavaScript and AngularJS.

#### [What's New in Eclipse IDE and the Ecosystem Around It](https://www.eclipsecon.org/france2016/session/whats-new-eclipse-ide-and-ecosystem-around-it)
By Sopot Cela and Mickael Istria [Red Hat]

This talk covered upcoming features in the Neon release, including theme enhancements, text editor zoom, substring completion in JDT, smart project import, auto-save, and auto-suggest editors in the marketplace.

#### [The State of Docker and Vagrant Tooling in Eclipse](https://www.eclipsecon.org/france2016/session/state-docker-and-vagrant-tooling-eclipse)
By Roland Grunberg [Red Hat]

Roland presented the current status and future plans for Docker and Vagrant tools in Eclipse.

#### [Java 9 Support in Eclipse](https://www.eclipsecon.org/france2016/session/java-9-support-eclipse)
By Sasikanth Bharadwaj [IBM]

This session provided an overview of Jigsaw support in Eclipse and how to create and configure Jigsaw modules.

#### [JSDT 2.0](https://www.eclipsecon.org/france2016/session/jsdt-20)
By Ilya Buziuk [Red Hat]

JSDT is being renewed with features like running/debugging Node.js code, a JSON editor, and Grunt/Gulp support.

#### [TypeScript: Feedback from a Real-Life Project within TypeScript and the Future of JavaScript](https://www.eclipsecon.org/france2016/session/typescript-feedback-real-life-project-within-typescript-and-future-javascript-sponsored)
By Sebastien Pertus [Microsoft]

This talk included live demos of various TypeScript features using Eclipse tools.

#### [Fast Unit Tests for Eclipse Plugins - Possible Architectures and Available Tooling](https://www.eclipsecon.org/france2016/session/fast-unit-tests-eclipse-plugins-possible-architectures-and-available-tooling)
By Aurelien Pupier [Red Hat]

Aurelien shared his experience with unit testing in Eclipse plugin development, discussing different setup options and productivity tools.

#### [A Generic REST API on Top of Eclipse CDO for Web-Based Modelling](https://www.eclipsecon.org/france2016/session/generic-rest-api-top-eclipse-cdo-web-based-modelling)
By Christophe Ponsard

This session demonstrated a modeling application running EMF behind the scenes with a web UI.

<iframe width="560" height="315" src="https://www.youtube.com/embed/C-kWMlz5yEY?list=PLy7t4z5SYNaRJff0KBMbubOaj8gevvML4" frameborder="0" allowfullscreen></iframe>

### Conclusion

That's all for now! Apologies for not covering other topics like IoT, Science, and Modeling. I will update this blog with links to recorded talks as they become available.

Thank you to the EclipseCon organizers for a great event!

PS: Special thanks to David Gosling, Stevan Le Meur, Florent Benoît, and Aurélien Pupier for the review.
