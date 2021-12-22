package com.mongodb.jdbc.integration.testharness.models;

import java.util.List;

public class TestEntry {
    public String description;
    public String sql;
    public List<Object> meta_function;
    public String skip_reason;
    public Integer row_count;
    public Boolean row_count_gte;
    public Boolean ordered;
    public List<Object> expected_result;
    public List<String> expected_sql_type;
    public List<String> expected_catalog_name;
    public List<String> expected_column_class_name;
    public List<String> expected_column_label;
    public List<Integer> expected_column_display_size;
    public List<Integer> expected_precision;
    public List<Integer> expected_scale;
    public List<String> expected_schema_name;
    public List<Boolean> expected_is_auto_increment;
    public List<Boolean> expected_is_case_sensitive;
    public List<Boolean> expected_is_currency;
    public List<Boolean> expected_is_definitely_writable;
    public List<String> expected_is_nullable;
    public List<Boolean> expected_is_read_only;
    public List<Boolean> expected_is_searchable;
    public List<Boolean> expected_is_signed;
    public List<Boolean> expected_is_writable;
}
