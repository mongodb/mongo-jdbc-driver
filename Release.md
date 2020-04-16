# Versioning
Version number policy could be referred from here: https://docs.oracle.com/middleware/1212/core/MAVEN/maven_version.htm#MAVEN8855

Among all the version standards Maven supports, we will use MajorVersion, IncrementalVersion and Qualifier inside this project.

The current version number is specified inside `gradle.properties` file. 

# Release procedures
The release is handled in evergreen `Release` variant.  

For the SNAPSHOT version, every successful evergreen PR build will release the artifacts to the Sonatype SNAPSHOT repo in https://oss.sonatype.org/#view-repositories;snapshots~browsestorage

## Release Versions

Follow these instructions for creating a Release Version.

* Create a release branch in your local fork
* Update version in `gradle.properties`, and update the `README.md` file to reflect the new version. Commit these changes:  
```git commit -m "BUMP v<version>"```
* Once the “BUMP” commit has been created, push and create a PR for this change
* Once the PR has been approved and merged, checkout the upstream master branch locally
```
git remote add upstream git@github.com:10gen/adl-jdbc-driver.git
git fetch upstream
git checkout -b up-masterupstream/master
```
* Tag it for release as follows:  
```git tag -a -m "<new version>" v<new version> <githash>```   
The tag command MUST:  
  * be annotated (using the -a option)  
  * include a message that contains the semantic version for the release  
  * have a tagname that is exactly the name as the semantic version, prefixed with "v"  
* Once tagged, push the newly created tag to master
```git push origin v<revision> ```
* The release task will run through all the tests, check the release comment and tag name. If all passed, it will automatically push the release artifacts to Maven-Central. Check the released version in https://oss.sonatype.org 
* Update version back to a SNAPSHOT for the next version in _gradle.properties_ and commit  
```git commit -am "BUMP <next revision>-SNAPSHOT"```
* Push that too  
```git push```
* Create Github release and upload artifact from maven central at https://oss.sonatype.org: mongodb-jdbc-\<version\>.jar, following instructions here https://help.github.com/en/github/administering-a-repository/managing-releases-in-a-repository 
