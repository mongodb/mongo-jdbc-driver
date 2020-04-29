package com.mongodb.jdbc;

import static org.junit.jupiter.api.Assertions.*;

import java.sql.DatabaseMetaData;
import java.sql.SQLException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class MongoDatabaseMetaDataTest extends MongoMock {
    private static DatabaseMetaData databaseMetaData;

    static {}

    @Test
    void testGetCatalogAndSchemaName() throws SQLException {}
}
