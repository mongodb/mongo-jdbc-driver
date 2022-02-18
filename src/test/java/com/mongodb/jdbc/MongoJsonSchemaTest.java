package com.mongodb.jdbc;

import static org.bson.codecs.configuration.CodecRegistries.fromProviders;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import org.bson.codecs.BsonValueCodecProvider;
import org.bson.codecs.Codec;
import org.bson.codecs.DecoderContext;
import org.bson.codecs.ValueCodecProvider;
import org.bson.codecs.configuration.CodecRegistry;
import org.bson.codecs.pojo.PojoCodecProvider;
import org.bson.json.JsonReader;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;

/** Test the deserialization and simplifacation of JsonSchema returned by a sqlgetschema command. */
public class MongoJsonSchemaTest {
    static final CodecRegistry REGISTRY =
            fromProviders(
                    new BsonValueCodecProvider(),
                    new ValueCodecProvider(),
                    PojoCodecProvider.builder().automatic(true).build());
    static final Codec<MongoJsonSchema> MONGO_JSON_SCHEMA_CODEC =
            REGISTRY.get(MongoJsonSchema.class);
    static final Codec<JsonSchema> JSON_SCHEMA_CODEC = REGISTRY.get(JsonSchema.class);

    @TestFactory
    Collection<DynamicTest> runIntegrationTests() throws SQLException {
        ClassLoader classLoader = getClass().getClassLoader();
        File input = new File(classLoader.getResource("mongoJsonSchemaTest/input").getFile());
        assertTrue(input.isDirectory(), input.getPath() + " is not a directory.");
        File expectedOutput =
                new File(classLoader.getResource("mongoJsonSchemaTest/expectedOutput").getFile());
        assertTrue(expectedOutput.isDirectory(), expectedOutput.getPath() + " is not a directory.");
        List<DynamicTest> dynamicTests = new ArrayList<>();
        for (File testEntry : input.listFiles()) {
            File output = new File(expectedOutput.getAbsoluteFile() + "/" + testEntry.getName());
            dynamicTests.add(
                    DynamicTest.dynamicTest(
                            testEntry.getName(),
                            () -> {
                                System.out.println(
                                        "Comparing "
                                                + testEntry.getName()
                                                + " with "
                                                + output.getName());
                                testDeserializeAndSimplifySchema(testEntry, output);
                            }));
        }
        return dynamicTests;
    }

    public void testDeserializeAndSimplifySchema(File input, File output)
            throws FileNotFoundException {
        JsonSchema in_schema = null;
        MongoJsonSchema out_schema = null;
        // Decode the input
        try (JsonReader reader = new JsonReader(new FileReader(input))) {
            in_schema = JSON_SCHEMA_CODEC.decode(reader, DecoderContext.builder().build());
        }
        // Decode the expected out as jsonSchema to make sure that no simplification is happening
        // except the transformation from String to Set<String> if necessary for bsonType.
        try (JsonReader reader = new JsonReader(new FileReader(output))) {
            out_schema = MONGO_JSON_SCHEMA_CODEC.decode(reader, DecoderContext.builder().build());
        }

        // Transform the mongoJsonSchema to a JsonSchema for comparing each other
        MongoJsonSchema simplifiedSchema = MongoJsonSchema.toSimplifiedMongoJsonSchema(in_schema);

        assertEquals(simplifiedSchema, out_schema);
    }
}
