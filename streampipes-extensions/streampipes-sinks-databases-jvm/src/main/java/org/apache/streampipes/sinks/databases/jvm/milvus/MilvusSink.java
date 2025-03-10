/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package org.apache.streampipes.sinks.databases.jvm.milvus;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import io.milvus.param.Constant;
import io.milvus.pool.MilvusClientV2Pool;
import io.milvus.pool.PoolConfig;
import io.milvus.v2.client.ConnectConfig;
import io.milvus.v2.client.MilvusClientV2;
import io.milvus.v2.common.DataType;
import io.milvus.v2.service.collection.request.AddFieldReq;
import io.milvus.v2.service.collection.request.CreateCollectionReq;
import io.milvus.v2.service.database.request.CreateDatabaseReq;
import io.milvus.v2.service.vector.request.InsertReq;
import org.apache.streampipes.commons.exceptions.SpRuntimeException;
import org.apache.streampipes.extensions.api.pe.context.EventSinkRuntimeContext;
import org.apache.streampipes.model.DataSinkType;
import org.apache.streampipes.model.extensions.ExtensionAssetType;
import org.apache.streampipes.model.graph.DataSinkDescription;
import org.apache.streampipes.model.runtime.Event;
import org.apache.streampipes.model.schema.EventProperty;
import org.apache.streampipes.model.schema.EventPropertyNested;
import org.apache.streampipes.model.schema.EventPropertyPrimitive;
import org.apache.streampipes.model.schema.EventSchema;
import org.apache.streampipes.model.schema.PropertyScope;
import org.apache.streampipes.sdk.builder.DataSinkBuilder;
import org.apache.streampipes.sdk.builder.StreamRequirementsBuilder;
import org.apache.streampipes.sdk.helpers.EpRequirements;
import org.apache.streampipes.sdk.helpers.Labels;
import org.apache.streampipes.sdk.helpers.Locales;
import org.apache.streampipes.vocabulary.XSD;
import org.apache.streampipes.wrapper.params.compat.SinkParams;
import org.apache.streampipes.wrapper.standalone.StreamPipesDataSink;

import java.time.Duration;
import java.util.*;

public class MilvusSink extends StreamPipesDataSink {

    public static final String URI_KEY = "milvus_uri";

    public static final String TOKEN_KEY = "milvus_token";

    public static final String DBNAME_KEY = "milvus_dbname";
    public static final String DATABASE_REPLICA_NUMBER_KEY = "database_replica_number";

    public static final String COLLECTION_NAME_KEY = "collection_name";

    public static final String VECTOR_KEY = "vector";

    public final Gson gson = new Gson();

    public MilvusClientV2Pool pool;
    public MilvusClientV2 client;
    String vector;
    DataType type;

    //discussion里介绍一下milvus，贴一个milvus官网链接，pom修改依赖版本
    //数据库，向量配置和表配置移到这里来
    @Override
    public DataSinkDescription declareModel() {
        return DataSinkBuilder
                .create("org.apache.streampipes.sinks.databases.jvm.milvus", 0)
                .withLocales(Locales.EN)
                .withAssets(ExtensionAssetType.DOCUMENTATION, ExtensionAssetType.ICON).
                category(DataSinkType.DATABASE)
                .requiredTextParameter(Labels.withId(URI_KEY))
                .requiredTextParameter(Labels.withId(TOKEN_KEY), "root:Milvus")
                .requiredTextParameter(Labels.withId(DBNAME_KEY))
                .requiredTextParameter(Labels.withId(DATABASE_REPLICA_NUMBER_KEY), "2")
                .requiredTextParameter(Labels.withId(COLLECTION_NAME_KEY))
                .requiredStream(StreamRequirementsBuilder
                        .create()
                        .requiredPropertyWithUnaryMapping(EpRequirements.numberReq(),
                                Labels.withId(VECTOR_KEY),
                                PropertyScope.NONE)
                        .build())
                .build();
    }

    //数据库等的实例化放到初始化这里一起进行
    @Override
    public void onInvocation(SinkParams parameters,
                             EventSinkRuntimeContext runtimeContext) throws SpRuntimeException {
        var extractor = parameters.extractor();
        final String uri = extractor.singleValueParameter(URI_KEY, String.class);
        final String token = extractor.singleValueParameter(TOKEN_KEY, String.class);
        final String dbName = extractor.singleValueParameter(DBNAME_KEY, String.class);

        ConnectConfig connectConfig = ConnectConfig.builder()
                .uri(uri)
                .token(token)
                .dbName(dbName)
                .build();

        PoolConfig poolConfig = PoolConfig.builder()
                .maxIdlePerKey(10) // max idle clients per key
                .maxTotalPerKey(20) // max total(idle + active) clients per key
                .maxTotal(100) // max total clients for all keys
                .maxBlockWaitDuration(Duration.ofSeconds(5L)) // getClient() will wait 5 seconds if no idle client available
                .minEvictableIdleDuration(Duration.ofSeconds(10L)) // if number
                .build();
        try {
            pool = new MilvusClientV2Pool(poolConfig, connectConfig);
            client = pool.getClient("client_name");

            //create a dataBase
            Map<String, String> properties = new HashMap<>();
            properties.put(Constant.DATABASE_REPLICA_NUMBER, DATABASE_REPLICA_NUMBER_KEY);
            CreateDatabaseReq createDatabaseReq = CreateDatabaseReq.builder()
                    .databaseName(DBNAME_KEY)
                    .properties(properties)
                    .build();
            client.createDatabase(createDatabaseReq);
            client.useDatabase(DBNAME_KEY);

            EventSchema schema = parameters.getModel().getInputStreams().get(0).getEventSchema();

            // create a collection with schema, when indexParams is specified, it will create index as well
            CreateCollectionReq.CollectionSchema collectionSchema = client.createSchema();
            this.vector = parameters.extractor().mappingPropertyValue(VECTOR_KEY);
            extractEventProperties(schema.getEventProperties(), "", collectionSchema);

            CreateCollectionReq createCollectionReq = CreateCollectionReq.builder()
                    .collectionName(COLLECTION_NAME_KEY)
                    .collectionSchema(collectionSchema)
                    .build();
            client.createCollection(createCollectionReq);
        } catch (Exception e) {
            //todo add log
            throw new RuntimeException(e);
        }

    }

    @Override
    public void onDetach() {
        client.close();
        pool.close();
    }

    //专心做插入操作
    @Override
    public void onEvent(Event event) {
        if (event == null) {
            return;
        }

        final Map<String, Object> measurementValuePairs = event.getRaw();
        // should be at least a timestamp field and a measurement field
        if (measurementValuePairs.size() <= 1) {
            return;
        }

        final int measurementFieldCount = measurementValuePairs.size() - 1;
        final List<String> measurements = new ArrayList<>(measurementFieldCount);
        final List<DataType> types = new ArrayList<>(measurementFieldCount);
        final List<Object> values = new ArrayList<>(measurementFieldCount);

        for (Map.Entry<String, Object> measurementValuePair : measurementValuePairs.entrySet()) {

            measurements.add(measurementValuePair.getKey());

            final Object value = measurementValuePair.getValue();
            if (value instanceof Integer) {
                types.add(DataType.Int32);
                values.add(value);
            } else if (value instanceof Long) {
                types.add(DataType.Int64);
                values.add(value);
            } else if (value instanceof Float) {
                types.add(DataType.Float);
                values.add(value);
            } else if (value instanceof Double) {
                types.add(DataType.Double);
                values.add(value);
            } else if (value instanceof Boolean) {
                types.add(DataType.Bool);
                values.add(value);
            } else if (value instanceof String) {
                types.add(DataType.String);
                values.add(value);
            } else if (value instanceof List) {
                types.add(DataType.Array);
                values.add(value);
            } else if (value instanceof Byte){
                types.add(DataType.Int8);
                values.add(value);
            } else if (value instanceof Short) {
                types.add(DataType.Int16);
                values.add(value);
            }
        }

        JsonObject vector = new JsonObject();
        vector.add(type.name(), gson.toJsonTree(values));

        InsertReq insertReq = InsertReq.builder()
                .collectionName(COLLECTION_NAME_KEY)
                .data(Collections.singletonList(vector))
                .build();
        client.insert(insertReq);
    }

    private void extractEventProperties(List<EventProperty> properties, String preProperty,
                                        CreateCollectionReq.CollectionSchema collectionSchema)
            throws SpRuntimeException {

        for (EventProperty property : properties) {
            final String name = preProperty + property.getRuntimeName();
            if (property instanceof EventPropertyNested) {
                extractEventProperties(((EventPropertyNested) property).getEventProperties(),
                        name + "_", collectionSchema);

            } else {
                if (property instanceof EventPropertyPrimitive) {
                    final String uri = ((EventPropertyPrimitive) property).getRuntimeType();
                    if(uri.equals(XSD.BYTE.toString())) {
                        collectionSchema.addField(AddFieldReq.builder().fieldName(name).dataType(DataType.Int8).build());
                    } else if (uri.equals(XSD.SHORT.toString())){
                        collectionSchema.addField(AddFieldReq.builder().fieldName(name).dataType(DataType.Int16).build());
                    } else if (uri.equals(XSD.INTEGER.toString())) {
                        collectionSchema.addField(AddFieldReq.builder().fieldName(name).dataType(DataType.Int32).build());
                    } else if (uri.equals(XSD.LONG.toString())) {
                        collectionSchema.addField(AddFieldReq.builder().fieldName(name).dataType(DataType.Int64).build());
                    } else if (uri.equals(XSD.FLOAT.toString())) {
                        collectionSchema.addField(AddFieldReq.builder().fieldName(name).dataType(DataType.Float).build());
                    } else if (uri.equals(XSD.DOUBLE.toString())) {
                        collectionSchema.addField(AddFieldReq.builder().fieldName(name).dataType(DataType.Double).build());
                    } else if (uri.equals(XSD.BOOLEAN.toString())) {
                        collectionSchema.addField(AddFieldReq.builder().fieldName(name).dataType(DataType.Bool).build());
                    } else if (uri.equals(XSD.STRING.toString())) {
                        collectionSchema.addField(AddFieldReq.builder().fieldName(name).dataType(DataType.String).build());
                    }
                } else {
                    collectionSchema.addField(AddFieldReq.builder().fieldName(name).dataType(DataType.Array).build());
                }
            }

        }
    }
}