# Steps to release a new version

1. Make sure [CHANGELOG.md](../CHANGELOG.md) mentions release changes
2. Bump the version in `pluginVersion` property in [gradle.properties](../gradle.properties)
3. Create and push a git tag for the new version pointing to the releasing commit
4. run `release` action on GitHub