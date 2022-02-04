### Test Generator  
Included in the test harness is a test generator that will generate baseline configurations for test cases based on
the `description`, `db`, and either `query` or `meta_function` fields for all test cases in the 
`resources/integration_test/tests` directory.
#### Initial Tests
Create a yaml file in the `resources/integration_test/tests` directory in the following format.  The required fields 
are `description`, `db`, and one of either `sql` or `meta_function`.
```
# test.yaml
tests:
  - description: test_case_description
    db: database_to_use
    sql: SQL Query
    
  - description: test_case_description
    db: database_to_use
    # meta_function takes an array.  Function name followed by arguments.
    meta_function: [Function name, arg1, arg2, ..., argn]
```
#### Running  
```
./gradlew runTestGenerator  
```
Generated files will be written to the `resources/generated_test` directory with the description as the 
filename prefix, one file per test case.  

To test the generated test cases, copy the file(s) to the `resources/integration_test/tests` directory and verify it 
passes the integration test.  Once verified, commit the file in the `resources/integration_test/tests` directory to 
add it to the integration test.

### Data Loader
#### Running
```
./gradlew runDataloader
```
#### YAML Fields
`db`: Database to use  
`collection`: Collection to use  
`docs`: Uses standard JSON to represent collection data. This is useful for simple types such as int and string.  
`docsExtJson`: uses extended JSON to represent collection data. This is useful for complex types such as binData and objectid.  

