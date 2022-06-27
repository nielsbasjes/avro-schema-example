# Introduction
In my 2019 talk at the Dataworks Summit called ["Building Streaming Applications"](https://youtu.be/QOdhaEHbSZM) I spoke about using [Apache Avro](https://avro.apache.org/) for evolving the schema of the data that is transported via a stream.

Over the years several people have asked for an example on how to do all of these ideas in a practical way.

# So what is this project?
This project is intended to be an example project.

**IMPORTANT:**
1. This project is intended to show **A POSSIBLE WAY** of structuring a project that uses records serialized with Apache Avro in an effective way. There are many alternative ways of doing this.
2. I created this mostly from scratch to provide an "as clean as possible" showcase of how to make this work. This project was created specifically as this example. **This code has NOT been used in production.**

# Usecases
In general this can help in having schemas in a schemaless environment.

I have seen two major use cases where this can help.

## Streaming
Most streaming transport systems (like Apache Kafka) only accept a byte array as the data.
    By serializing the records with Avro you can now have independent rolling upgrades and
    canary releases of the producing and consuming systems.
    By using these ideas; these upgrades no longer incur any downtime.
## Databases
Systems like [Apache HBase](https://hbase.apache.org/) and [Google BigTable](https://cloud.google.com/bigtable) are databases that do not have any kind of schema: All values are byte arrays.

Here you can use this to serialize your records and persist them in HBase. Then when a new field is needed or an old field is deprecated there is no need to update any of the old record.

Because the application carries all historical schemas the Avro Schema evolution will take care of reading the old records into the new shape.

# Basic design
- We have a separation between the Schema, the producers and the consumers.
  - This example project is ONLY the Schema
- This schema
  - is published to a schema database (key = 64 bit long, value = json string)
  - is published to an artifact repository as a jar file
    - Both the producers and consumers should have this Schema project as a dependency.

# Usage
- Producers
  - have the current schema from the jar file.
- Consumers
  - have the current schema from the jar file
  - have all previous versions are also in the jar file.
  - can get newer schema versions from the schema registry.

# How this works

1. During the build/development
   1. The current schema is verified to be evolution safe with the full schema history of the released schemas. The build will fail if it is not.
   2. The current JSON of the Avro schema is written to a temporary file. I abused a junit test for this.
2. Releasing the software is done with the `maven-release-plugin`.
   1. Do `mvn release:prepare` and this temporary file is stored in the schema-history and committed as one of the resources in the jar.
    All historical schemas are combined into a single file `SchemaHistory.csv`, this makes reading the resource simpler and more reliable. The format of these files is `<schema fingerprint>|<json string of the schema>`
   3. Do `mvn release:perform` and the jar is built and deployed to the repository and the schema is deployed to the schema registry.
     **NOTE:** These two deploy steps have been disabled/mocked in this example repository; so nothing will really be deployed.


# Notable other things
- The AVRO schema IDLs are put through a filter during the build before being generated into Java code by the `avro-maven-plugin`, this way we can dynamically generate the version of the schema into the schema itself.

# LICENSE

    Avro schema evolution example.
    Copyright (C) 2022 Niels Basjes

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

    https://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.


