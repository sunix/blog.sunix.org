blog.sunix.org
==========

This is the source code for [https://blog.sunix.org](https://blog.sunix.org) - Sun Seng David TAN's (a.k.a Sunix) personal blog about Java, Cloud, and Open Source Software Development.

<a href="https://blog.sunix.org/factory?url=https://github.com/sunix/blog.sunix.org/tree/gh-pages"><img src="https://che.openshift.io/factory/resources/factory-contribute.svg" /></a>

## Technology Stack

This blog is built with the following technologies:

- **[Jekyll](https://jekyllrb.com/)** 4.3 - Static site generator
- **[Ruby](https://www.ruby-lang.org/)** 3.3 - Programming language
- **[Kramdown](https://kramdown.gettalong.org/)** - Markdown parser
- **[Rouge](https://github.com/rouge-ruby/rouge)** - Syntax highlighter
- **[Jekyll Paginate](https://github.com/jekyll/jekyll-paginate)** - Pagination plugin
- **[GitHub Pages](https://pages.github.com/)** - Hosting platform
- **[Surge.sh](https://surge.sh)** - PR preview deployments
- **[Eclipse Che](https://www.eclipse.org/che/)** - Cloud development environment (via devfile)

## Quick Start

### Prerequisites

- Ruby 3.3 or higher
- Bundler gem

### Local Development

1. Clone the repository:
   ```bash
   git clone https://github.com/sunix/blog.sunix.org.git
   cd blog.sunix.org
   ```

2. Install dependencies:
   ```bash
   bundle install
   ```

3. Build the site:
   ```bash
   bundle exec jekyll build
   ```

4. Serve locally:
   ```bash
   bundle exec jekyll serve
   ```
   
   The site will be available at `http://localhost:4000`

### Development with Eclipse Che

You can also develop this blog using Eclipse Che with the included `devfile.yaml`:

1. Use the factory link above or open the repository in an Eclipse Che workspace
2. Run the `serve` command to start the Jekyll development server
3. Access the site via the exposed endpoint on port 4000

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


