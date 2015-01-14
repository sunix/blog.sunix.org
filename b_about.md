---
layout: page
title: About me
comments: yes
permalink: /about/
---

<img title="{{site.author}}" src="{{site.aboutme_photo}}" alt="{{site.author}}"/>
<span>{{site.aboutme}}</span>
<br />

{% if site.ShowContactInfo == "True" %}
You can contact me via: {% if site.email != empty %}[Email](mailto:{{site.email}}) / {% endif %}{% if site.github_username != empty %}[Github](https://github.com/{{site.github_username}}){% endif %} / {% if site.twitter_username != empty %}[Twitter](https://twitter.com/{{site.twitter_username}}){% endif %}
{% endif %}

