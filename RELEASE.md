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

#### Start Release Ticket
Move the JIRA ticket for the release to the "In Progress" state.
Ensure that its fixVersion matches the version being released.

#### Ensure Evergreen Passing
Ensure that the build you are releasing is passing the tests on the evergreen waterfall.

#### Complete the Release in JIRA
Go to the [SQL releases page](https://jira.mongodb.org/projects/SQL?selectedItem=com.atlassian.jira.jira-projects-plugin%3Arelease-page&status=unreleased), and ensure that all the tickets in the fixVersion to be released are closed.
Ensure that all the tickets have the correct type.
The only uncompleted ticket in the release should be the release ticket.
If there are any remaining tickets that will not be included in this release, remove the fixVersion and assign them a new one if appropriate.
Close the release on JIRA, adding the current date (you may need to ask the SQL project manager to do this).

### Release Tasks

#### Ensure master up to date
Ensure you have the `master` branch checked out, and that you have pulled the latest commit from `mongodb/mongo-jdbc-driver`.

#### Commit the release version, tag, and push
- Update the version in `gradle.properties` to the version being released.
- Commit this change with the commit message `BUMP v<version>`
- Create an annotated tag with message `<version>` and name `v<version>`
- Push the newly created commit and tag to master

### Post-Release Tasks

#### Wait for evergreen
Wait for the evergreen version to finish, and ensure that the release task completes successfully.
You may need to bump task priorities if queues are long.

#### Verify release artifacts
Check that the version just released is available in the [Sonatype Nexus Repo Manager](https://oss.sonatype.org/#nexus-search;quick~mongodb-jdbc)

#### Update master branch to SNAPSHOT version
- Update `gradle.properties` to version `<new_version>-SNAPSHOT`, where the new version is a patch increment over the version just released.
- Commit this change with the commit message `BUMP <new version>-SNAPSHOT` and push it.

#### Create Github Release
Create a Github release and upload the release artifact from Sonatype/Maven Central.

#### Close Release Ticket
Move the JIRA ticket tracking this release to the "Closed" state.
