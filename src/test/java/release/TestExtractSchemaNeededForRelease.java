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

package release;

import nl.basjes.avroexample.webdata.Measurement;
import org.junit.jupiter.api.Test;

import java.io.BufferedWriter;
import java.io.FileWriter;

import static org.apache.avro.SchemaNormalization.parsingFingerprint64;
import static org.junit.jupiter.api.Assertions.fail;

class TestExtractSchemaNeededForRelease {
    /**
     * When we want to release the schema we NEED to persist the current version of the schema in the sourcecode
     * as a reference to test against in the future.
     * Here we simply write it to a temporary file which is only picked up in case of an actual release.
     */
    @Test
    void extractSchema() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("target/CurrentSchema.csv"))) {
            writer.write(String.valueOf(parsingFingerprint64(Measurement.SCHEMA$)));
            writer.write("|");
            writer.write(String.valueOf(Measurement.SCHEMA$));
            writer.write("\n");
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }
}
