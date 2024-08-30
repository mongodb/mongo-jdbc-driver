package com.mongodb.jdbc;

import org.bson.BsonValue;

import java.util.Set;

// Simple POJO for deserializing buildInfo results.
public class BuildInfo {
    public String version;
    public Set<String> modules;
    public BsonValue dataLake;
}
