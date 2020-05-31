# Versioning
Version number policy could be referred from here: https://docs.oracle.com/middleware/1212/core/MAVEN/maven_version.htm#MAVEN8855

Among all the version standards Maven supports, we will use MajorVersion, IncrementalVersion and Qualifier inside this project.

The current version number is specified inside `gradle.properties` file. 

# Release procedures
The release is handled in evergreen `Release` variant.  

## Snapshot Versions

Every successful untagged build in evergreen will release the artifacts to the Sonatype SNAPSHOT repo in https://oss.sonatype.org/#view-repositories;snapshots~browsestorage

## Release Versions

Follow these instructions for creating a Release Version.

* Checkout the upstream master branch locally
```
git remote add upstream git@github.com:10gen/adl-jdbc-driver.git
git fetch upstream
git checkout -b up-master upstream/master
```
* Update version in `gradle.properties`, and update the `README.md` file to reflect the new version. Commit these changes:  
```git commit -am "BUMP v<version>"```
* Tag it for release as follows:  
```git tag -a -m "<new version>" v<new version> <githash>```   
The tag command MUST:  
  * be annotated (using the -a option)  
  * include a message that contains the semantic version for the release  
  * have a tagname that is exactly the name as the semantic version, prefixed with "v"  
* Once tagged, push the newly created tag to master
```
   git push upstream v<revision>
   git push upstream up-master:master
```
* The evergreen release task will run through all the tests, check the release comment and tag name. If all passed, it will automatically push the release artifacts to Maven-Central. Check the released version in https://oss.sonatype.org 
* Update version back to a SNAPSHOT for the next version in `gradle.properties` and commit  
```git commit -am "BUMP <next revision>-SNAPSHOT"```
* Push that too  
```git push upstream```
* Create Github release and upload artifact from maven central at https://oss.sonatype.org: mongodb-jdbc-\<version\>.jar, following instructions here https://help.github.com/en/github/administering-a-repository/managing-releases-in-a-repository 
