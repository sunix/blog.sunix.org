---
layout: page
title: Categories
permalink: /categories/
---

<!-- Categories cloud -->
<div class="mb-10">
  <h2 class="text-2xl font-extrabold mb-5">All Categories</h2>
  <div class="flex flex-wrap gap-2">
  {% if site.posts != empty %}
  {% for cat in site.categories %}
  <a href="#{{ cat[0] }}" class="rounded-full px-4 py-2 text-sm font-semibold border border-white/40 dark:border-white/10 bg-white/70 dark:bg-white/5 hover:bg-gradient-to-r hover:from-amber-200/70 hover:to-fuchsia-200/70 dark:hover:from-amber-300/20 dark:hover:to-fuchsia-300/20 transition">
    {{ cat[0] | join: "/" }} <span class="text-xs text-slate-600 dark:text-slate-400">({{ cat[1].size }})</span>
  </a>
  {% endfor %}
  {% endif %}
  </div>
</div>

<!-- Posts by category -->
<div class="space-y-10">
{% if site.posts != empty %}
{% for cat in site.categories %}
<section id="{{ cat[0] }}" class="rounded-[24px] border border-white/40 dark:border-white/10 bg-white/70 dark:bg-white/5 backdrop-blur p-6 shadow-sm">
  <h3 class="text-xl font-extrabold mb-4 flex items-center gap-2">
    <span class="inline-flex h-2 w-2 rounded-full bg-gradient-to-r from-amber-500 to-fuchsia-500"></span>
    {{ cat[0] }}
  </h3>
  <ul class="space-y-3">
  {% for post in cat[1] %}
  <li class="flex flex-col sm:flex-row sm:items-center gap-2">
    <time datetime="{{ post.date | date:"%Y-%m-%d" }}" class="text-sm font-mono text-slate-600 dark:text-slate-400">{{ post.date | date:"%Y-%m-%d" }}</time>
    <span class="hidden sm:inline text-slate-400">→</span>
    <a href="{{ site.baseurl }}{{ post.url }}" title="{{ post.title }}" class="font-semibold text-slate-900 dark:text-slate-100 hover:text-fuchsia-600 dark:hover:text-cyan-400">{{ post.title }}</a>
  </li>
  {% endfor %}
  </ul>
</section>
{% endfor %}
{% else %}
<p class="text-slate-600 dark:text-slate-400">No posts yet</p>
{% endif %}
</div>