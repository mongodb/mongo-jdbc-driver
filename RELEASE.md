# Versioning
Version number policy could be referred from here: https://docs.oracle.com/middleware/1212/core/MAVEN/maven_version.htm#MAVEN8855

Among all the version standards Maven supports, we will use MajorVersion, IncrementalVersion and Qualifier inside this project.

The current version number is specified inside `gradle.properties` file. 

# Release Process

## Snapshot Versions

Every successful untagged build in evergreen will release the artifacts to the Sonatype SNAPSHOT repo in https://oss.sonatype.org/#view-repositories;snapshots~browsestorage

## Release Versions

Follow these instructions for creating a Release Version.

### Pre-Release Tasks

First, ensure that you have the master branch checked out, and that your local branch is up to date with mongodb/mongo-jdbc-driver.

### Release Tasks

- First, update the version in `gradle.properties`.
- Commit this change with the commit message `BUMP v<version>`
- Create an annotated tag with message `<version>` and name `v<version>`
- Push the newly created commit and tag to master

### Post-Release Tasks

- Wait for the evergreen version to finish, and ensure that the release task completes successfully.
- Check that the version just released is available in the [Sonatype Nexus Repo Manager](https://oss.sonatype.org/#nexus-search;quick~mongodb-jdbc)
- Update `gradle.properties` to version `<new_version>-SNAPSHOT`, where the new version is a patch increment over the version just released.
- Commit this change with the commit message `BUMP <new version>-SNAPSHOT` and push it.
- Create a Github release and upload the release artifact from Sonatype/Maven Central.
