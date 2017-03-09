---
layout: post
title:  I run Eclipse Neon in Eclipse Che with GTK Broadwayd
modified: 2017-03-10
categories: [articles]
tags: 
  - eclipseche
  - eclipseneon
  - broadwayd
comments: true
contribute: https://beta.codenvy.com/f?url=https://github.com/sunix/blog.sunix.org/tree/gh-pages
---

I've recently posted on twitter:

<blockquote class="twitter-tweet" data-lang="fr"><p lang="en" dir="ltr">I run Eclipse Neon in <a href="https://twitter.com/eclipse_che">@eclipse_che</a> with GTK Broadwayd ... anywhere :) <a href="https://twitter.com/hashtag/ubergeek?src=hash">#ubergeek</a> <a href="https://t.co/REeQj06cHl">pic.twitter.com/REeQj06cHl</a></p>&mdash; Sun S. D. Tan (@sunsengdavidtan) <a href="https://twitter.com/sunsengdavidtan/status/839082778104901632">7 mars 2017</a></blockquote>
<script async src="//platform.twitter.com/widgets.js" charset="utf-8"></script>

And got lot's of feedback on what was behind that. So here is a quick blog post explaining that

<!-- more -->
# Motivation
As an Eclipse Che committer, I'm trying as much as I can to use Eclipse Che as my everyday IDE. It's not always that easy: Che has lots of nice feature that the traditionnal Eclipse IDE don't have. But the opposite also works.
One thing that is currently not working that well is the GWT Super dev mode ... and I could use Eclipse IDE instead but suffering of the installation process.
I have a workspace per task I'm working on and can't really clone my Eclipse IDE as I want.
Few months ago my fellow student from GSoC Randika wrote an article on how we managed to run Eclipse IDE in Che through ssh X11 export : <http://thesite>
It works well but would need the user to have ssh and X export support client side

Another motivation is to make my pair programming even better. So far I had to configure the eclipse plugins for Flux. painful ...

# Broadwayd
During the last Eclipse Con France, Mickael was showing the Eclipse IDE running on Broadwayd and that was what I'd like to do with Che, using the same projects etc ...
doc to broadwayd
very simple


# Dockerfile
https://hub.docker.com/r/sunix/chedev/~/dockerfile/

# Che commands

# Chefile
