---
layout: layouts/post.html
title: "Why We Estimate: The True Value of Estimation in Agile Development"
date: "2024-07-23"
modified: 2024-07-23
category: "articles"
tags: 
  - Agile Development
  - Project Management
  - Task Estimation
  - Sprint Planning
comments: true
aliases:
  - /articles/howto/2024/07/23/why-we-estimate.html
  - /articles/articles/2024/07/23/why-we-estimate.html
  - /articles/tutorial/2024/07/23/why-we-estimate.html
  - /articles/blog/2024/07/23/why-we-estimate.html
  - /articles/tech/2024/07/23/why-we-estimate.html
---

Reading the article [“4 Fallacious Reasons Why We Estimate”](https://web.archive.org/web/20200419145539/https://tech.transferwise.com/4-fallacious-reasons-why-we-estimate/), I felt it missed the most important part of why we estimate. The article seemed to suggest that estimation is only fraught with drawbacks and is ultimately useless. However, from my experience working with various teams at Red Hat, I believe that estimation holds significant value, albeit for reasons that might not be immediately apparent. In this blog post, I will delve into why we estimate, addressing misconceptions and highlighting the true value of this practice.

<!-- more -->

### No Visible Progress, Endless Issues, and Frustration

In one of our sprints, we assigned one task per developer. However, because no one finished their task, we moved all the ‘in progress’ issues to the next sprint. This lack of visible progress can be problematic and frustrating for everyone involved:

- **Project Manager (PM):** The PM is frustrated because they cannot see progress and have no idea if tasks will be delivered in the current sprint, the next one, or even three months down the line.
- **Team Members:** Other team members are frustrated because they want to understand where the team stands in terms of achieving common goals. As a team, everyone wants to reach their objectives together.
- **Individual Developers:** It is frustrating for developers not to be able to finish tasks despite making progress. Marking tasks as done provides a sense of accomplishment, which is crucial for maintaining motivation during the sprint.

### Splitting Issues

Let’s consider one of my assigned issues: “Make the factories work with Theia IDE.” One major component was implementing the import of projects defined in the factory definition. This issue could take one or two sprints to complete, but without clear estimation, it was challenging to gauge the time required accurately.

Any task that takes more than a week can be frustrating for the reasons mentioned above. Therefore, my first step was to split the issue into smaller, more manageable tasks that could be “delivered.” Initially, I split the issue into two parts, but I remained open to further splitting if necessary. However, sometimes splitting issues isn’t enough to create clear and concise tasks that can be resolved quickly.

### Ending Up with Results

To ensure tasks can be solved within a short period, each task should have a clear definition of done, which could be delivering something, documentation, etc. Here are some examples:

- **Spike:** This could result in more issues or a Proof of Concept (PoC).
- **Demo Scripts and Acceptance Criteria:** These should be defined before estimating issues. Acceptance criteria, following the Given-When-Then pattern, help validate the task’s completion.

For instance, the issue described in [GitHub issue #2465](https://github.com/eclipse-jkube/jkube/issues/2465) can be easily estimated because the steps to completion are well-defined.

### How We Should Estimate

- **Define the Goal and Clear Definition of Done:** Include a demo at the end and/or acceptance criteria.
- **Maximum 5 Days:** If a task is estimated to take longer, it should be split into smaller tasks.
- **Explain the Time Estimate:** Provide implementation details and reasoning.
- **Use Relative Sizing:** In previous teams, we estimated using trivial/small/medium/large to avoid discussing specific timeframes.
- **Collaborative Estimation:** Estimating with team members forces you to think through the implementation. Explaining your estimates can reveal hidden complexities.

### Experiments and Spikes

Spike issues involve investigating a technology or figuring out an implementation approach. For example, I spent time figuring out how to implement the import functionality. A spike should result in a PoC or a clear understanding of the task. After a spike, you can create and estimate new tasks more accurately.

### Conclusion

While some may view estimation as an exercise in futility, my experience has shown that it provides essential visibility and structure to the development process. Estimations help manage expectations, break down complex tasks, and maintain team morale by showing incremental progress. By understanding and implementing effective estimation practices, we can harness their true value, making our development cycles more predictable and our goals more achievable.
