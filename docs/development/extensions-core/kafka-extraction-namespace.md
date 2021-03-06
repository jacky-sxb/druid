---
id: kafka-extraction-namespace
title: "Apache Kafka Lookups"
---

<!--
  ~ Licensed to the Apache Software Foundation (ASF) under one
  ~ or more contributor license agreements.  See the NOTICE file
  ~ distributed with this work for additional information
  ~ regarding copyright ownership.  The ASF licenses this file
  ~ to you under the Apache License, Version 2.0 (the
  ~ "License"); you may not use this file except in compliance
  ~ with the License.  You may obtain a copy of the License at
  ~
  ~   http://www.apache.org/licenses/LICENSE-2.0
  ~
  ~ Unless required by applicable law or agreed to in writing,
  ~ software distributed under the License is distributed on an
  ~ "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
  ~ KIND, either express or implied.  See the License for the
  ~ specific language governing permissions and limitations
  ~ under the License.
  -->

> Lookups are an [experimental](../experimental.md) feature.

To use this Apache Druid extension, [include](../../development/extensions.md#loading-extensions) `druid-lookups-cached-global` and `druid-kafka-extraction-namespace` in the extensions load list.

If you need updates to populate as promptly as possible, it is possible to plug into a Kafka topic whose key is the old value and message is the desired new value (both in UTF-8) as a LookupExtractorFactory.

```json
{
  "type":"kafka",
  "kafkaTopic":"testTopic",
  "kafkaProperties":{"zookeeper.connect":"somehost:2181/kafka"}
}
```

|Parameter|Description|Required|Default|
|---------|-----------|--------|-------|
|`kafkaTopic`|The Kafka topic to read the data from|Yes||
|`kafkaProperties`|Kafka consumer properties. At least"zookeeper.connect" must be specified. Only the zookeeper connector is supported|Yes||
|`connectTimeout`|How long to wait for an initial connection|No|`0` (do not wait)|
|`isOneToOne`|The map is a one-to-one (see [Lookup DimensionSpecs](../../querying/dimensionspecs.md))|No|`false`|

The extension `kafka-extraction-namespace` enables reading from a Kafka feed which has name/key pairs to allow renaming of dimension values. An example use case would be to rename an ID to a human readable format.

The consumer properties `group.id` and `auto.offset.reset` CANNOT be set in `kafkaProperties` as they are set by the extension as `UUID.randomUUID().toString()` and `smallest` respectively.

See [lookups](../../querying/lookups.md) for how to configure and use lookups.

## Limitations

Currently the Kafka lookup extractor feeds the entire Kafka stream into a local cache. If you are using on-heap caching, this can easily clobber your java heap if the Kafka stream spews a lot of unique keys.
off-heap caching should alleviate these concerns, but there is still a limit to the quantity of data that can be stored.
There is currently no eviction policy.

## Testing the Kafka rename functionality

To test this setup, you can send key/value pairs to a Kafka stream via the following producer console:

```
./bin/kafka-console-producer.sh --property parse.key=true --property key.separator="->" --broker-list localhost:9092 --topic testTopic
```

Renames can then be published as `OLD_VAL->NEW_VAL` followed by newline (enter or return)
