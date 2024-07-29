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

#### Determine the correct version to be released
Go to the [SQL releases page](https://jira.mongodb.org/projects/SQL?selectedItem=com.atlassian.jira.jira-projects-plugin%3Arelease-page&status=unreleased), and check the content of the tickets that are included in the current release. The fix version by default is a patch version. If there is a backwards incompatible API change in the tickets that are set to be released, we should instead update the major version; if there are new features added in the tickets set to be released, we should instead update the minor version. To do so, update the version on the [SQL releases page](https://jira.mongodb.org/projects/SQL?selectedItem=com.atlassian.jira.jira-projects-plugin%3Arelease-page&status=unreleased) under "Actions". This will update the fix version on all of the tickets as well.

#### Start Release Ticket
Move the JIRA ticket for the release to the "In Progress" state.
Ensure that its fixVersion matches the version being released, and update it if it changed in the previous step.

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

#### Create the tag and push
Create an annotated tag and push it:
```
git tag -a -m X.Y.Z vX.Y.Z
git push --tags
```
This should trigger an Evergreen version that can be viewed on the [mongo-jdbc-driver waterfall](https://evergreen.mongodb.com/waterfall/mongo-jdbc-driver).
If it does not, you may have to ask the project manager to give you the right permissions to do so.
Make sure to run the 'release' task, if it is not run automatically.

#### Set Evergreen Priorities
Some evergreen variants may have a long schedule queue.
To speed up release tasks, you can set the task priority for any variant to 101 for release candidates and 200 for actual releases.
If you do not have permissions to set priority above 100, ask the project manager to set the
priority.

### Post-Release Tasks

#### Wait for evergreen
Wait for the evergreen version to finish, and ensure that the release task completes successfully.

#### Verify release artifacts
Check that the version just released is available in the [Sonatype Nexus Repo Manager](https://oss.sonatype.org/#nexus-search;quick~mongodb-jdbc).  
The release artifacts should appear on [Maven Central](https://search.maven.org/search?q=g:org.mongodb%20AND%20a:mongodb-jdbc) after a while.

#### Notify the Web team about the new release
Create a ticket through the [service desk](https://jira.mongodb.org/plugins/servlet/desk/portal/61/create/926) and request the link for the `Download from Maven` button on the Download Center page `https://www.mongodb.com/try/download/jdbc-driver` to be updated.  
You can find the new link in the [json feed](https://translators-connectors-releases.s3.amazonaws.com/mongo-jdbc-driver/mongo-jdbc-downloads.json) under `versions[0].download_link`. Include this link in your ticket.

#### Close Release Ticket
Move the JIRA ticket tracking this release to the "Closed" state.

#### Ensure next release ticket and fixVersion created
Ensure that a JIRA ticket tracking the next release has been created
and is assigned the appropriate fixVersion.
