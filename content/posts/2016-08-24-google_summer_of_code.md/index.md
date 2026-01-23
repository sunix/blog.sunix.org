---
layout: layouts/post.html
title:  News from Eclipse Che Google Summer of Code 2016 Projects
date: "2016-08-24"
modified: 2016-08-24
category: "articles"
tags: 
  - eclipseche
  - gsoc
  - pairprogramming
  - unittest
comments: true
contribute: https://blog.sunix.org/factory?url=https://github.com/sunix/blog.sunix.org/tree/gh-pages
---

Summer is heating up in the Eclipse Che ecosystem. More contributors, including Red Hat and SAP, are joining the Che adventure. Everyone is working hard on Che improvements, such as implementing the Language Server Protocol and multi-container workspaces.

But they are not the only ones working this summer.

Earlier this year, Florent and I submitted a few ideas to the Google Summer of Code (GSoC). The GSoC is a program organized by Google to encourage students to be more involved in open-source projects. Students get a well-paid summer job, and open-source projects gain promising new contributors. As part of the Eclipse Foundation, Eclipse Che is participating in this year’s Google Summer of Code. Codenvy and Serli are also participating as Eclipse Che GSoC project mentors for 2016.

<!-- more -->

## Unit Tests in Che

In Che, Java unit test execution is possible out of the box thanks to Maven. To execute a test class opened in the editor, you can simply create a Maven command in Che:

```
clean test -Dtest=${current.class.fqn}
```

You can even connect the debugger to it:

```
clean test -Dtest=${current.class.fqn} -Dmaven.surefire.debug
```

However, this is not as good as the unit test UI we have in the classic Eclipse IDE.

As part of the GSoC program, Mirage Abeysekara, a student from Sri Lanka, started implementing a Che extension for unit test execution:

- **Context menu for running test cases**: In the project explorer, right-clicking on a Java test class should show a menu item to run that test class.
- **Main menu item for running all tests**: The main menu bar contains a menu item for running all the test classes in the project.
- **GUI for showing test runner state**: After launching the tests, the test runner window will open and show the current status of the test runner: launching, waiting for results, and finished.
- **View the details of a failing test**: A window will display the failing test cases, including the stack trace and the differences in assertions.
- **Navigation to the failing class**: In the details window, there must be an action to let you jump to the failing test class.
- **Extendable framework**: The test runner plugin must provide an extendable framework for adding new test runner implementations without modifying the REST services and the GUI.

So far, Mirage is in his final steps. You can check out his work at [GitHub](https://github.com/Mirage20/che/tree/che-java-test-runner-updated/plugins/plugin-java-test-runner) and view an overview of his online demo on [YouTube](https://www.youtube.com/watch?v=NgksCjhA9SY).

## Pair Programming

Separately, Randika Navagamuwa is working on improving an existing prototype for pair programming within Che. This prototype was demoed during the last EclipseCon: [InfoQ Presentation](https://www.infoq.com/presentations/pair-programming-cloud).

The prototype shows multiple Che editors connected to the same workspace and modifying the same file. All the editors receive live updates, just like in Google Docs.

Randika is adding new features to the existing prototype and improving the code quality. There’s still a lot to do, but he has already implemented the display of other participants’ cursors. To do this, he had to get involved in two other Eclipse Foundation projects:

- **Eclipse Orion**: Che uses Orion as an embedded editor. Randika made a first demo just with Orion to display additional cursors using annotations.
- **Eclipse Flux**: Used to exchange messages during the pair programming session. Exchanging cursor positions involved registering a new message type in Flux.

Randika is currently refactoring the extension to simplify the implementation of new protocols used for the pair programming session.

A video demo of his work can be found here: [YouTube Demo](https://youtu.be/OZNceE-sWto).

For this first GSoC experience, we used Gitter for student/mentor daily communication. Che has also opened a Gitter channel ([Eclipse Che Gitter](https://gitter.im/eclipse/che)). We preferred this option to IRC, as IRC may be blocked by company firewalls. Generic questions go to the main Che Gitter channel, and specific questions related to the GSoC project go to mentors through direct messages.

We’ve also tried to blog as much as we could about what we were doing during this program.

Randika and I did a lot of instant messaging sessions when things were unclear. We organized video calls when necessary. We even used Che’s pair programming extension for our own pair programming sessions! It works pretty well, even with the Che server running in the US (AWS) and with two clients running in Sri Lanka and France.

I wish all the best to the two students who are very close to their final evaluation! I hope they will continue to contribute to Che even after this GSoC program.
