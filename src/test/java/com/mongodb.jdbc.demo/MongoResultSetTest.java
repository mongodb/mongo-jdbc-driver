package com.mongodb.jdbc.demo;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

import com.mongodb.client.MongoCursor;
import com.mongodb.jdbc.MongoResultSet;
import com.mongodb.jdbc.Row;
import java.sql.SQLException;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class MongoResultSetTest {
    @Mock MongoCursor<Row> cursor;
    @Mock Row nextRow;

    static final String CURR_DOC = "currDoc";
    static final String NEXT_DOC = "nextDoc";

    MongoResultSet mongoResultSet;

    @BeforeAll
    void initMocks() {
        MockitoAnnotations.initMocks(this);
    }

    // unit test sample
    @Test
    void returnNextRowWhenAvailable() throws Exception {
        // Mock the cursor and next Row
        when(cursor.hasNext()).thenReturn(true);
        when(cursor.next()).thenReturn(nextRow);

        mongoResultSet = new MongoResultSet(cursor);

        boolean hasNext = mongoResultSet.next();
        assertTrue(hasNext);
        assertEquals(NEXT_DOC, mongoResultSet.getString("label"));
    }

    @Test
    void throwExceptionWhenNotAvailable() throws Exception {
        // Mock the cursor and next Row
        when(cursor.hasNext()).thenReturn(false);

        mongoResultSet = new MongoResultSet(cursor);

        boolean hasNext = mongoResultSet.next();
        assertFalse(hasNext);
        assertThrows(
                SQLException.class,
                () -> {
                    mongoResultSet.getString("label");
                });
    }
}
