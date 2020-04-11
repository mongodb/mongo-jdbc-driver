## Version Policy
we support two types of versions:  
#### Release
* MajorVersion: 1.2.1
* MinorVersion: 2.0
* Qualifier: 1.2-beta-2  
Only release versions can be uploaded to the Central Repository, which means files that won't change and that only depend on other files already released and available in the repository.

#### Snapshot
* 1.2-SNAPSHOT.  
Continuous development is typically performed with snapshot versions supported by the Snapshot version policy. These version values have to end with -SNAPSHOT. This allows repeated uploads where the actual number used is composed of a date/timestamp and an enumerator and the retrieval can still use the -SNAPSHOT version string. 

If you want to publish a special snapshot version to be tested by others or continuously integrated, you can release a snapshot version with version number and current timestamp such as "_1.2-yyyyMMddHHmm-SNAPSHOT_". The client will be able to retrieve it by the version number or the latest snapshot.

Detailed descriptions for version policies can be viewed here: [[https://help.sonatype.com/repomanager3/formats/maven-repositories#MavenRepositories-MavenRepositoryFormat]]

The current version is specified inside `gradle.properties` file. 

## Release procedures
The release is handled in evergreen `Release` task.  

For the SNAPSHOT version, every successful evergreen PR build will release the artifacts to the Sonatype SNAPSHOT repo in https://oss.sonatype.org/#view-repositories;snapshots~browsestorage

For the Release version, follow the steps below:
* Create a release branch in your local fork
* Update version in _gradle.properties_, and update the _README.md_ file to reflect the new version. Commit these changes:  
```git commit -m "BUMP v<revision>"```
* Once the “BUMP” commit has been created, push and create a PR for this change
* Once the PR has been approved and merged, checkout the upstream master branch locally
* Tag it for release as follows:  
```git tag -a -m "<new version>" v<new version> <githash>```   
The tag command MUST:  
  * be annotated (using the -a option)  
  * include a message that contains the semantic version for the release  
  * have a tagname that is exactly the name as the semantic version, prefixed with "v"  
* Once tagged, push the newly created tag to master
```git push origin v<revision> ```
* Check the evergreen build for the previous PR. You don't need to do anything if the `Release` task is not yet run. If the release task was run before the tag has been pushed, the task will fail and you just need to restart it.
* This task will run through all the tests, check the release comment and tag name. If all passed, it will automatically push the release artifacts to Maven-Central. Check the released version in https://oss.sonatype.org 
* Create a new branch in your forked repo and update version back to a SNAPSHOT for the next version in _gradle.properties_ and commit  
```git commit -am "BUMP <next revision>-SNAPSHOT"```
* Push that too  
```git push```
* Create a PR and get it merged.
* Create Github release and upload artifact from maven central at https://oss.sonatype.org: mongodb-jdbc-\<version\>.jar, following instructions here https://help.github.com/en/github/administering-a-repository/managing-releases-in-a-repository 
