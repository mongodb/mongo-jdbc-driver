/*
 * Copyright (c) 2008 - 2013 10gen, Inc. <http://10gen.com>
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
 *
 */

import org.bson.BsonArray;
import org.bson.BsonDocument;
import org.bson.BsonDocumentReader;
import org.bson.BsonInt32;
import org.bson.BsonString;
import org.bson.BsonValue;
import org.bson.codecs.BsonValueCodecProvider;
import org.bson.codecs.DecoderContext;
import org.bson.codecs.ValueCodecProvider;
import org.bson.codecs.pojo.PojoCodecProvider;
import org.bson.codecs.configuration.CodecRegistry;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientOptions;
import com.mongodb.MongoClientURI;
import com.mongodb.MongoClientURI;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;

import java.util.List;

import static java.util.Arrays.asList;
import static org.bson.codecs.configuration.CodecRegistries.fromProviders;

public class JdbcPojo {
    public static void main(String[] args) {

        var registry = fromProviders(
                new BsonValueCodecProvider(),
                new ValueCodecProvider(),
                PojoCodecProvider.builder().automatic(true).build());

        var rowCodec = registry.get(Row.class);

        var document =
                new BsonDocument("values",
                        new BsonArray(
                                asList(
                                        new BsonDocument()
                                                .append("database", new BsonString("myDb"))
                                                .append("table", new BsonString("myTable"))
                                                .append("tableAlias", new BsonString("myTableAlias"))
                                                .append("column", new BsonString("a"))
                                                .append("columnAlias", new BsonString("aAlias"))
                                                .append("value", new BsonInt32(3)),
                                        new BsonDocument()
                                                .append("database", new BsonString("myDb"))
                                                .append("table", new BsonString("myTable2"))
                                                .append("tableAlias", new BsonString("myTableAlias2"))
                                                .append("column", new BsonString("b"))
                                                .append("columnAlias", new BsonString("bAlias"))
                                                .append("value", new BsonInt32(4)))));

        var row = rowCodec.decode(new BsonDocumentReader(document), DecoderContext.builder().build());

        System.out.println(row);

		System.out.println("----------------------");

		MongoClientURI uri = new MongoClientURI("mongodb://localhost");
		MongoClient client = new MongoClient(uri);
		MongoDatabase db = client.getDatabase("test").withCodecRegistry(registry);
		MongoCollection<Row> col = db.getCollection("test", Row.class);
		CodecRegistry cod = col.getCodecRegistry();
		cod.get(Row.class);
		MongoCursor<Row> cur = col.find(Row.class).iterator();
		while (cur.hasNext()) {
			Row r = cur.next();
			System.out.println(r);
		}
    }

    public static class Row {
        public List<Column> values;

        @Override
        public String toString() {
            return "Row{" +
                    "values=" + values +
                    '}';
        }
    }

    public static class Column {
        public String database;
        public String table;
        public String tableAlias;
        public String column;
        public String columnAlias;
        public BsonValue value;

        @Override
        public String toString() {
            return "Column{" +
                    "database='" + database + '\'' +
                    ", table='" + table + '\'' +
                    ", tableAlias='" + tableAlias + '\'' +
                    ", column='" + column + '\'' +
                    ", columnAlias='" + columnAlias + '\'' +
                    ", value=" + value +
                    '}';
        }
    }
}


