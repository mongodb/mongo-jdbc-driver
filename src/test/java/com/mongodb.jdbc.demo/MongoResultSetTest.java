package com.mongodb.jdbc.demo;

import com.mongodb.client.MongoCursor;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import com.mongodb.jdbc.MongoResultSet;
import org.bson.Document;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;

import java.sql.SQLDataException;
import java.sql.SQLException;

@ExtendWith(MockitoExtension.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class MongoResultSetTest {
    @Mock MongoCursor<Document> cursor;
    @Mock Document nextDocument;

    static final String CURR_DOC = "currDoc";
    static final String NEXT_DOC = "nextDoc";

    MongoResultSet mongoResultSet;

    @BeforeAll
    void initMocks() {
        MockitoAnnotations.initMocks(this);
    }

    // unit test sample
    @Test
    void returnNextDocumentWhenAvailable() throws Exception {
        // Mock the cursor and next Document
        when(cursor.hasNext()).thenReturn(true);
        when(cursor.next()).thenReturn(nextDocument);
        when(nextDocument.get(any())).thenReturn(NEXT_DOC);
        when(nextDocument.containsKey(any())).thenReturn(true);

        mongoResultSet = new MongoResultSet(cursor);

        var hasNext = mongoResultSet.next();
        assertTrue(hasNext);
        assertEquals(NEXT_DOC, mongoResultSet.getString("label"));
    }

    @Test
    void throwExceptionWhenNotAvailable() throws Exception {
        // Mock the cursor and next Document
        when(cursor.hasNext()).thenReturn(false);

        mongoResultSet = new MongoResultSet(cursor);

        var hasNext = mongoResultSet.next();
        assertFalse(hasNext);
        assertThrows(SQLException.class, () -> {
            mongoResultSet.getString("label");
        });
    }
}
