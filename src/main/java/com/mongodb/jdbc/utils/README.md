# MongoDB JDBC Driver Configuration Tracing

The MongoDB JDBC driver includes a configuration tracing feature that helps users understand how configurations are resolved at runtime. This feature can be used to debug configuration issues by showing where each configuration value comes from and how precedence is resolved when multiple sources provide the same configuration option.

## Enabling Configuration Tracing

Configuration tracing can be enabled in two ways:

1. **System Property**: Set the `mongodb.jdbc.config.trace.enabled` system property to `true` when starting your Java application:

   ```
   java -Dmongodb.jdbc.config.trace.enabled=true -jar your-application.jar
   ```

2. **Connection Property**: Set the `configtrace` connection property to `true` when creating a JDBC connection:

   ```java
   Properties props = new Properties();
   props.setProperty("configtrace", "true");
   Connection conn = DriverManager.getConnection("jdbc:mongodb://localhost:27017/test", props);
   ```

## Configuration Sources

The configuration tracer tracks values from the following sources, in order of precedence (highest to lowest):

1. **Connection Property**: Properties passed to the `DriverManager.getConnection()` method
2. **URL Parameter**: Parameters specified in the JDBC connection URL
3. **System Property**: JVM system properties that influence driver behavior
4. **Environment Variable**: Relevant environment variables that affect the driver
5. **Default Value**: Default values provided by the driver

## Traced Configuration Values

The configuration tracer logs the following information for each configuration value:

- **Name**: The name of the configuration option
- **Value**: The value of the configuration option (masked for sensitive values)
- **Source**: The source of the configuration option
- **Precedence**: When a value from a higher-precedence source overrides a value from a lower-precedence source

## JAAS Configuration Tracing

The configuration tracer also includes enhanced tracing for JAAS configurations, which can be particularly useful for debugging Kerberos authentication issues. It traces:

- **Login Module**: The JAAS login module class name
- **Options**: The options passed to the login module
- **Source**: The source of the JAAS configuration (file, programmatic, etc.)

## Log Output

Configuration trace logs are written to the same destination as other MongoDB JDBC driver logs, which can be configured using the `loglevel` and `logdir` connection properties.

Most configuration trace logs are written at the `FINE` level, with summary information written at the `INFO` level. To see all trace logs, set the log level to `FINE` or lower.

## Example Output

```
INFO: MongoDB JDBC Configuration Tracing enabled
FINE: Config: database=test (from Connection Property)
FINE: Config: user=admin (from URL Parameter)
FINE: Config: authSource=admin (from URL Parameter)
FINE: Config: x509pempath=/path/to/cert.pem (from Connection Property)
FINE: Config: MONGODB_JDBC_X509_CLIENT_CERT_PATH=/path/to/cert.pem (from Environment Variable)
FINE: JAAS Configuration for 'MongoClient': LoginModule=com.sun.security.auth.module.Krb5LoginModule, Source=Default Value
FINE:   JAAS Option: useKeyTab=true
FINE:   JAAS Option: principal=user@EXAMPLE.COM
FINE:   JAAS Option: keyTab=/path/to/keytab
FINE:   JAAS Control Flag: REQUIRED
INFO: === MongoDB JDBC Configuration Trace Summary ===
INFO: database=test (from Connection Property)
INFO: user=admin (from URL Parameter)
INFO: authSource=admin (from URL Parameter)
INFO: x509pempath=/path/to/cert.pem (from Connection Property)
INFO: MONGODB_JDBC_X509_CLIENT_CERT_PATH=/path/to/cert.pem (from Environment Variable)
INFO: === End Configuration Trace Summary ===
```

## Limitations

- Java does not provide native APIs to determine whether a system property originated from a command-line argument, environment variable, or was set programmatically
- The origin of JAAS configurations cannot always be determined precisely
- Some internal driver defaults may not be exposed through the tracing interface

## Integration with MongoDB JDBC Driver

The configuration tracing feature is fully integrated into the MongoDB JDBC driver and does not require any additional libraries or tools. It is designed to be lightweight and have minimal impact on performance when not enabled.

When enabled, the configuration tracer instruments key methods in the driver that handle configuration parsing, including:

- URL parameter parsing
- Connection property handling
- System property access
- Environment variable lookup
- Authentication mechanism selection
- X.509 certificate path resolution
- JAAS configuration loading

## Use Cases

The configuration tracing feature is particularly useful in the following scenarios:

1. **Troubleshooting authentication issues**: Identify which authentication mechanism is being used and where credentials are coming from
2. **Debugging SSL/TLS configuration**: Trace the origin of SSL/TLS settings like truststore and keystore paths
3. **Understanding configuration precedence**: See how different configuration sources interact and which ones take precedence
4. **Auditing security settings**: Verify that security-related configurations are being applied correctly
5. **Diagnosing connection problems**: Identify misconfigurations that might be causing connection failures
