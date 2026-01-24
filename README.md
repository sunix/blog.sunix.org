blog.sunix.org
==========

This is the source code for [https://blog.sunix.org](https://blog.sunix.org) - Sun Seng David TAN's (a.k.a Sunix) personal blog about Java, Cloud, and Open Source Software Development.

<a href="https://blog.sunix.org/factory?url=https://github.com/sunix/blog.sunix.org/tree/gh-pages"><img src="https://che.openshift.io/factory/resources/factory-contribute.svg" /></a>

## Technology Stack

This blog is built with the following technologies:

- **[Quarkus](https://quarkus.io/)** 3.25 - Supersonic Subatomic Java Framework
- **[Roq](https://quarkiverse.github.io/quarkiverse-docs/quarkus-roq/dev/)** 1.8 - Static site generator for Quarkus
- **[Java](https://www.java.com/)** 21 - Programming language
- **[Maven](https://maven.apache.org/)** - Build tool
- **[Tailwind CSS](https://tailwindcss.com/)** - CSS framework (via Quarkus Web Bundler)
- **[Qute](https://quarkus.io/guides/qute)** - Templating engine
- **[GitHub Pages](https://pages.github.com/)** - Hosting platform
- **[Surge.sh](https://surge.sh)** - PR preview deployments

## Quick Start

### Prerequisites

- Java 21 or higher
- Maven 3.9+ (or use the included Maven wrapper `./mvnw`)

### Local Development

1. Clone the repository:
   ```bash
   git clone https://github.com/sunix/blog.sunix.org.git
   cd blog.sunix.org
   ```

2. Run the development server:
   ```bash
   ./mvnw quarkus:dev
   ```
   
   The site will be available at `http://localhost:8080`
   
   Dev mode features live reload - any changes to content or templates will be instantly reflected.

3. Build the site:
   ```bash
   ./mvnw clean package
   ```
   
   The built application will be in `target/quarkus-app/`

### Adding Content

#### Blog Posts

Create a new directory under `content/posts/` with the format `YYYY-MM-DD-post-slug/`:

```bash
mkdir -p content/posts/2026-01-22-my-new-post
```

Create an `index.md` file inside with frontmatter:

```markdown
---
layout: layouts/post.html
title: "My New Post Title"
date: "2026-01-22"
category: "articles"
tags:
  - Java
  - Quarkus
  - Cloud
---

Your content here...
```

#### Static Pages

Create HTML or Markdown files in the `content/` directory:

```bash
touch content/my-page.html
```

With frontmatter:

```html
---
layout: main
title: My Page Title
---

<div>
  Your page content here...
</div>
```

## Deployment

The blog is automatically deployed to GitHub Pages when changes are pushed to the `gh-pages` branch.

## PR Preview

You can preview your changes before merging by commenting `/preview` on a pull request. This will trigger a GitHub Actions workflow that builds and deploys your PR to [Surge.sh](https://surge.sh).

### Setup (for repository maintainers)

To enable PR previews, add a `SURGE_TOKEN` secret to the repository:

1. Install surge: `npm install -g surge`
2. Login: `surge login`
3. Generate token: `surge token`
4. Add the token as a secret in GitHub Actions settings:
   - Go to: Repository Settings > Secrets and variables > Actions > New repository secret
   - Name: `SURGE_TOKEN`
   - Value: (paste the token from step 3)

### Usage

On any pull request, comment `/preview` to trigger a preview build. The bot will:
- Comment when the build starts
- Deploy to `https://pr-{number}-sunix-blog-preview.surge.sh`
- Update the comment with the preview URL when ready

Enjoy.
