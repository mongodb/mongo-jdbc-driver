<img height="90" alt="MongoDB Atlas JDBC Driver" align="right" src="resources/media/MongoDBAtlasJDBC.png" />

# MongoDB Atlas JDBC Driver

MongoDB Atlas JDBC provides SQL connectivity to [Atlas](https://www.mongodb.com/atlas) databases and datalakes for client applications developed in Java.
MongoDB Atlas JDBC is a JDBC Type 4 driver compatible with the JDBC 4.2 specification.

### Prerequisites
MongoDB Atlas JDBC driver requires Java 1.8 or higher.

## Getting the Driver

### Download and Install
You can download the precompiled driver (jar) from [Maven Central](https://search.maven.org/artifact/org.mongodb/mongodb-jdbc).

### Build From Source
To build and test the driver run the following commands from root dir.

#### To build the lean jar which does not include the dependencies
```
./gradlew clean build
```
#### To run demo locally
```
./gradlew clean :demo:run
```
#### To generate the source Jar
```
./gradlew clean :sourceJar  
```
You can find the generated jar in build/libs/
#### To generate the fat Jar which includes all the dependencies
```
./gradlew clean :shadowJar
```
You can find the generated jar in build/libs/
#### To generate the test Jar
```
./gradlew clean :testJar  
```
You can find the generated jar in build/libs/ 
#### To run the unit tests
```
./gradlew clean test
```
#### To fix lint problem
```
./gradlew spotlessApply
```
## Usage

### Connection URL and properties

#### Connection URL
The connection URL is based of MongoDB connection string and has the `jdbc:` prefix.
The general format for the connection URL is as follows, with items in square brackets ([ ]) being optional:
```
jdbc:mongodb://[username:password@]host1[:port1][,...hostN[:portN]][/[defaultauthdb][?property1=value1[&property2=value2]...]
```

See [https://www.mongodb.com/docs/manual/reference/connection-string/](https://www.mongodb.com/docs/manual/reference/connection-string/) for the URI format 
and [https://www.mongodb.com/docs/manual/reference/connection-string/#std-label-connections-connection-options](https://www.mongodb.com/docs/manual/reference/connection-string/#std-label-connections-connection-options) for the supported options..

##### Notes
- Special characters in the JDBC url have to be URL encoded.
- The driver can not connect to a local mongod instance

#### Connection Properties
In addition to the standard MongoDB connection options the driver supports a number of additional properties specific to the JDBC driver. 
These properties have to be specified using an additional Properties object parameter to DriverManager.getConnection.

| Property                      | Type    | Required | Default | Description   |
| ----------------------------- | ------- | -------- | :-----: | ------------- |
| database                      | String  | Yes      | Null    | The name of the database used when querying |
| LogLevel                      | String  | No       | OFF     | The log level used for logging. Supported levels by increasing verbosity are 'OFF', 'SEVERE', 'WARNING', 'FINE', 'INFO' and 'FINER' |
| LogDir                        | String  | No       | Null    | The directory to use for log files. If not logging directory is specified, the logs are sent to the console |


## Integration Testing
Integration testing requires a local MongoDB and Atlas Data Lake instance to be running
#### Environment Variables
**ADL_TEST_LOCAL_USER**: Local ADL username  
**ADL_TEST_LOCAL_PWD**: Local ADL password  
**ADL_TEST_LOCAL_AUTH_DB**: Local ADL authentication database  
**HAVE_LOCAL_MONGOHOUSE**: "1" if using local mongohouse source  
**LOCAL_MONGOHOUSE_DIR**: Path to local mongohouse source

**MDB_TEST_LOCAL_PORT** (Optional): Local MongoDB port

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
