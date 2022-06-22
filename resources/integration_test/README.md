## Integration Tests Specifications


For the integration tests, a specific pattern is being used to test each function. Essentially, one test is used to 
check the metadata results of the function. Additional tests are used to check the result sets. For 
example, in `dbmd_constant_result_sets.yml`, `getTypeInfo_resultset_metadata_validation` is checking the metadata of 
the `getTypeInfo()` function, and `getTypeInfo_returns_constant_result_set` is checking the actual results against the 
expected result set of the `getTypeInfo()` function.