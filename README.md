blog.sunix.org
==========


This is the sources of [http://blog.sunix.org](http://blog.sunix.org).

<a href="https://blog.sunix.org/factory?url=https://github.com/sunix/blog.sunix.org/tree/gh-pages"><img src="https://che.openshift.io/factory/resources/factory-contribute.svg" /></a>


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


