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

## Integration Testing
### mysql
#### Environment Variables
**ADL_TEST_HOST**: ADL hostname  
**ADL_TEST_USER**: ADL username  
**ADL_TEST_PWD**:  ADL password  
**ADL_TEST_AUTH_DB**: ADL authentication database
### mongosql
Integration testing for mongosql requires a local MongoDB and Atlas Data Lake instance to be running
#### Environment Variables
**ADL_TEST_LOCAL_USER**: Local ADL username  
**ADL_TEST_LOCAL_PWD**: Local ADL password  
**ADL_TEST_LOCAL_AUTH_DB**: Local ADL authentication database  

**MDB_TEST_LOCAL_PORT**: Local MongoDB port
#### To load integration test data
```
./gradlew runDataLoader
```
#### To generate integration test baseline configuration files
```
./gradlew runTestGenerator  
```

### Running Local mongod and Atlas Data Lake
`run_adl.sh` is a helper script that will start a local mongod and Atlas Data Lake instance, used for integration testing.
#### Start 
```
./resources/run_adl.sh start
```
#### Stop
```
./resources/run_adl.sh stop
```
#### Skip
Use the `SKIP_RUN_ADL` option to skip the start and stop operations for those managing their own local instances.
```
export SKIP_RUN_ADL=1
```