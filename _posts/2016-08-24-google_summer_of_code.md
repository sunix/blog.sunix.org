---
layout: post
title:  News from Eclipse Che Google Summer of Code 2016 projects
modified: 2016-08-24
categories: [articles]
tags: 
  - eclipseche
  - gsoc
  - pairprogramming
  - unittest
comments: true
contribute: https://blog.sunix.org/cheeze?url=https://github.com/sunix/blog.sunix.org/tree/gh-pages
---

Summer is getting hot in Eclipse Che ecosystem. We have more and more contributors joining this new Che adventure such as Red Hat and SAP. Everyone is working hard on Che improvements such as implementing the Language Server Protocol and multi-container workspaces.

But they are not the only ones working this summer.

Earlier this year Florent and I submitted few ideas to the Google Summer of Code. The GSoC is a program organized by Google to encourage students to be more involved open source projects. Students get a well paid summer job and open source projects get new promising contributors. As part of the Eclipse Foundation, Eclipse Che will be part of this year’s Google Summer of Code. Codenvy and Serli will also participate in this 2016 edition as Eclipse Che GSoC project mentors.

<!-- more -->


# Unit tests in Che
In Che, Java unit test execution is possible out of the box thanks to Maven. To execute a test class opened in the editor, you can simply create a Maven command in Che:

    clean test -Dtest=${current.class.fqn}

You can even connect the debugger to it:

    clean test -Dtest=${current.class.fqn} -Dmaven.surefire.debug

However, this is not as good as the unit test UI we have in the classic Eclipse IDE.

So as part of the GSoC program, Mirage Abeysekara, a student from Sri Lanka, started to implement a Che extension for unit test execution:

 - Context menu for run the test cases: In the project explorer right clicking on a Java test class should show a menu item for run that test class.
 - Main menu item for run all tests: Main menu bar contains a menu item for running all the test classes of the project.
 - GUI for showing test runner state: After launching the tests the test runner window will open and show the current status of the test runner:launch, waiting for results, and finished.
 - View the details of a failing test: A window will display for the user to view the failing test cases including stack trace and difference of the assertions.
 - Navigation to the failing class: In the details window there must be an action to let you jump to the failing test class.
 - Extendable framework: The test runner plugin must provide an extendable framework for adding new test runner implementations without modification to the REST services and the GUI.

So far so good, Mirage is in his final steps. It’s possible to checkout his work at <https://github.com/Mirage20/che/tree/che-java-test-runner-updated/plugins/plugin-java-test-runner> and get an overview of his online demo: <https://www.youtube.com/watch?v=NgksCjhA9SY> 

# Pair programming
Separately, Randika Navagamuwa is working in improving an existing prototype for pair programming within Che. This prototype has been demoed during the last EclipseCon: <https://www.infoq.com/presentations/pair-programming-cloud> 

The prototype shows multiple Che editors connected to the same workspace and modifying the exact same file. All the editors get changes from other editors live updated, just like in Google docs.

Randika is working on adding new features in the existing prototype as well as improving the code quality. There’s a lot left to do, but he’s implemented the display of other participants’ cursors already. To do this he had to get involved in two other Eclipse Foundation projects:

 - Eclipse Orion: Che uses Orion as an embedded editor. Randika made a first demo just with Orion to display additional cursor using annotations.
 - Eclipse Flux: used to exchange messages during the pair programming session. Exchanging cursors positions involved registering new message type in Flux.

Randika is currently refactoring the extension to simplify the implementation of new protocols used for the pair programming session.

A video demo of his work can be found here: <https://youtu.be/OZNceE-sWto> .

For this first GSoC experience, we used Gitter for the student/mentor daily communication. Che has also opened a Gitter channel (https://gitter.im/eclipse/che).  We prefered that option to IRC as IRC may be blocked by companies firewalls. Generic questions are going to the main Che Gitter channel and specific questions to the GSoC project are going to mentors through direct messages.

We’ve also tried to blog as much as we could about things we were doing during this program.

Randika and I did a lot of IM sessions when things were not clear. And we organized video calls when it was necessary. We even used Che’s pair programming extension for our own pair programming sessions!!! It works pretty well even with the Che server running in the US (AWS) and with two clients running in Sri Lanka and France.

I wish all the best to the two students who are very close to their final evaluation!!! And I hope they will still contribute to Che even after this GSoC program.
