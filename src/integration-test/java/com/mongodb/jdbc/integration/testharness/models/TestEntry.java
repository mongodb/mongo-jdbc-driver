package com.mongodb.jdbc.integration.testharness.models;

import java.util.ArrayList;
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

    public TestEntry() {
        expected_result = new ArrayList<>();
        expected_sql_type = new ArrayList<>();
        expected_catalog_name = new ArrayList<>();
        expected_column_class_name = new ArrayList<>();
        expected_column_label = new ArrayList<>();
        expected_column_display_size = new ArrayList<>();
        expected_precision = new ArrayList<>();
        expected_scale = new ArrayList<>();
        expected_schema_name = new ArrayList<>();
        expected_is_auto_increment = new ArrayList<>();
        expected_is_case_sensitive = new ArrayList<>();
        expected_is_currency = new ArrayList<>();
        expected_is_definitely_writable = new ArrayList<>();
        expected_is_nullable = new ArrayList<>();
        expected_is_read_only = new ArrayList<>();
        expected_is_searchable = new ArrayList<>();
        expected_is_signed = new ArrayList<>();
        expected_is_writable = new ArrayList<>();
    }
}
