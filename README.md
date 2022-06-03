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
The connection URL is based on MongoDB connection string and has the `jdbc:` prefix.
The general format for the connection URL is as follows, with items in square brackets ([ ]) being optional:
```
jdbc:mongodb://[username:password@]host1[:port1][,...hostN[:portN]][/[defaultauthdb][?option1=value1[&option2=value2]...]
```

For more details :
- URI format: [https://www.mongodb.com/docs/manual/reference/connection-string/](https://www.mongodb.com/docs/manual/reference/connection-string/)
- Supported options: [https://www.mongodb.com/docs/manual/reference/connection-string/#std-label-connections-connection-options](https://www.mongodb.com/docs/manual/reference/connection-string/#std-label-connections-connection-options)

##### Notes
- Special characters in the JDBC url have to be URL encoded.
- The driver can not connect to a mongod instance, only to Atlas.

#### Connection Properties
All connection options can also be specified through a Properties object parameter instead of the being directly in the URL.
However, if an option is in both the URL and the Properties object, the connection will fail.

In addition to the standard MongoDB connection options there are a number of additional properties specific to the JDBC driver. 
These properties can only be specified using an additional Properties object parameter and not in the URL.

| Property                      | Type    | Required | Default | Description   |
| ----------------------------- | ------- | -------- | :-----: | ------------- |
| database                      | String  | Yes      | Null    | The name of the database used when querying |
| loglevel                      | String  | No       | OFF     | The log level used for logging. Supported levels by increasing verbosity are 'OFF', 'SEVERE', 'WARNING', 'FINE', 'INFO' and 'FINER' |
| logdir                        | String  | No       | Null    | The directory to use for log files. If not logging directory is specified, the logs are sent to the console |

The following example demonstrates how to open a connection specifying :
- The standard options `user` and `password` via a Properties object and ssl and authSource via the URL.
- The JDBC specific options `database` (mandatory) and `LogLevel` via a Properties object. 
```
         java.util.Properties p = new java.util.Properties();
         p.setProperty("user", "user");
         p.setProperty("password", "foo");
         p.setProperty("database", "test");
         p.setProperty("LogLevel", Level.FINER.getName());
         System.out.println("Connecting to database test...");
         Connection conn = DriverManager.getConnection("mongodb://mydatalake-xxxx.a.query.mongodb.net/?ssl=true&authSource=admin", p);
```

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
