### How to build: (run from root dir)
```
./gradlew clean build
```
### To run demo locally
```
./gradlew clean :demo:run
```
### To generate source Jar
```
./gradlew clean :sourceJar  
```
You can find the generated jar in build/libs/
### To generate fat Jar which includes all the dependencies
```
./gradlew clean :shadowJar
```
You can find the generated jar in build/libs/
### To generate test Jar
```
./gradlew clean :testJar  
```
You can find the generated jar in build/libs/ 
### To run the unit tests
```
./gradlew clean test
```
### To fix lint problem
```
./gradlew spotlessApply
```
### Release process
Make sure the following environment variables set:
- NEXUS_USERNAME
- NEXUS_PASSWORD
- NEXUS_PROFILE_ID
- NEXUS_URL
- SIGNING_KEY_ID
- SIGNING_PASSWORD
- RING_FILE_GPG_BASE64

```
./gradle/publish.sh 
```
### To load integration test data
./gradlew runDataLoader
### To generate integration test baseline configuration files
```
./gradlew runTestGenerator  
```
Accepts a description matcher to generate specific tests:  
```
./gradlew runTestGenerator --args="Test_Description"
```

