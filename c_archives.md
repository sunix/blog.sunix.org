---
layout: page
title: Archives
permalink: /archives/
---

<div class="space-y-10">

{% if site.posts != empty %}

{% for post in site.posts %}
{% capture this_year %}{{ post.date | date: "%Y" }}{% endcapture %}
{% unless year == this_year %}
{% assign year = this_year %}
{% unless post == site.posts.first %}
</ul>
</section>
{% endunless %}
<section class="rounded-[24px] border border-white/40 dark:border-white/10 bg-white/70 dark:bg-white/5 backdrop-blur p-6 shadow-sm">
  <h3 id="{{ year }}" class="text-xl font-extrabold mb-4 flex items-center gap-2">
    <span class="inline-flex h-2 w-2 rounded-full bg-gradient-to-r from-emerald-500 to-violet-500"></span>
    {{ year }}
  </h3>
  <ul class="space-y-3">
{% endunless %}
  <li class="flex flex-col sm:flex-row sm:items-center gap-2">
    <time datetime="{{ post.date | date:"%Y-%m-%d" }}" class="text-sm font-mono text-slate-600 dark:text-slate-400">
      {{ post.date | date:"%Y-%m-%d" }}
    </time>
    <span class="hidden sm:inline text-slate-400">→</span>
    <a href="{{ site.baseurl }}{{ post.url }}" class="font-semibold text-slate-900 dark:text-slate-100 hover:text-fuchsia-600 dark:hover:text-cyan-400">{{ post.title | capitalize }}</a>
  </li>
{% if forloop.last %}
  </ul>
</section>
{% endif %}
{% endfor %}

{% else %}

<p class="text-slate-600 dark:text-slate-400">No posts yet</p>

{% endif %}

</div>
