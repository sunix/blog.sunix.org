---
layout: page
title: About me
comments: yes
permalink: /about/
---

<div class="rounded-[28px] border border-white/40 dark:border-white/10 bg-white/70 dark:bg-white/5 backdrop-blur p-8 shadow-sm mb-8">
  <div class="flex flex-col md:flex-row gap-6 items-start">
    <img title="{{site.author}}" src="{{site.aboutme_photo}}" alt="{{site.author}}" class="w-32 h-32 rounded-2xl shadow-glow"/>
    <div class="flex-1">
      <h2 class="text-2xl font-extrabold mb-3">{{site.author}}</h2>
      <p class="text-slate-700 dark:text-slate-300 leading-relaxed">{{site.aboutme}}</p>
    </div>
  </div>
</div>

{% if site.ShowContactInfo == "True" %}
<div class="rounded-[24px] border border-white/40 dark:border-white/10 bg-white/70 dark:bg-white/5 backdrop-blur p-6 shadow-sm">
  <h3 class="text-lg font-extrabold mb-4">Get in touch</h3>
  <div class="flex flex-wrap gap-3">
    {% if site.email != empty %}
    <a href="mailto:{{site.email}}" class="inline-flex items-center gap-2 rounded-xl px-4 py-2 text-sm font-semibold bg-slate-900 text-white dark:bg-white dark:text-slate-900 hover:opacity-90 transition">
      📧 Email
    </a>
    {% endif %}
    {% if site.github_username != empty %}
    <a href="https://github.com/{{site.github_username}}" class="inline-flex items-center gap-2 rounded-xl px-4 py-2 text-sm font-semibold border border-slate-200 dark:border-white/10 bg-white/70 dark:bg-white/5 hover:bg-white transition">
      🐙 GitHub
    </a>
    {% endif %}
    {% if site.twitter_username != empty %}
    <a href="https://twitter.com/{{site.twitter_username}}" class="inline-flex items-center gap-2 rounded-xl px-4 py-2 text-sm font-semibold border border-slate-200 dark:border-white/10 bg-white/70 dark:bg-white/5 hover:bg-white transition">
      🐦 Twitter/X
    </a>
    {% endif %}
  </div>
</div>
{% endif %}