//
// Avro schema evolution example.
// Copyright (C) 2022 Niels Basjes
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
// https://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.
//

package nl.basjes.avroexample.schemahistory;

import nl.basjes.avroexample.webdata.Measurement;
import org.apache.avro.Schema;
import org.apache.avro.message.SchemaStore;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public final class HistoricalSchemas {
    private HistoricalSchemas() {
        // Nothing here
    }

    /**
     * Get a full list of all known Schemas
     * @return A list of all known schemas: both historical and current.
     * @throws IOException In case of a problem reading the schema history.
     */
    public static List<Schema> getSchemaHistory() throws IOException {
        List<Schema> schemaList = new ArrayList<>();

        // Make sure the current schema is also in there.
        // This is needed when not doing a full release (i.e. when doing a `mvn install` during development).
        schemaList.add(Measurement.SCHEMA$);

        ClassLoader classLoader = ClassLoader.getSystemClassLoader();
        try (InputStream is = classLoader.getResourceAsStream("schema-history/SchemaHistory.csv")) {
            if (is == null) {
                throw new IOException("Unable to read the schema-history/SchemaHistory.csv from the classpath");
            }
            try (InputStreamReader isr = new InputStreamReader(is);
                BufferedReader reader = new BufferedReader(isr)) {
                schemaList.addAll(reader
                    .lines()
                    .map(line -> line.substring(line.indexOf('|') + 1))
                    .map(json -> new Schema.Parser().parse(json))
                    .collect(Collectors.toList()));
            }
        }
        return schemaList;
    }

    /**
     * Get a basic instance of an Avro {@link SchemaStore} with all known Schemas.
     * @return A {@link SchemaStore} instance containing all known schemas: both historical and current.
     * @throws IOException In case of a problem reading the schema history.
     */
    public static SchemaStore getSchemaStore() throws IOException {
        SchemaStore.Cache schemaStore = new SchemaStore.Cache();
        for (Schema schema : getSchemaHistory()) {
            schemaStore.addSchema(schema);
        }
        return schemaStore;
    }

}
