/*
 * Copyright 2022-present MongoDB, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.mongodb.jdbc.integration.testharness.models;

import java.util.List;
import java.util.Map;

public class TestEntry {
    public String description;
    public String db;
    public String sql;
    public List<Object> meta_function;
    public String skip_reason;
    public Integer row_count;
    public Boolean row_count_gte;
    public Boolean ordered;
    public List<String> duplicated_columns_names;
    public List<Object> expected_result;
    public List<Map<String, Object>> expected_result_extended_json;
    public List<String> expected_sql_type;
    public List<String> expected_bson_type;
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
