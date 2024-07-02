/*
*Copyright (c) Alibaba Group;
*Licensed under the Apache License, Version 2.0 (the "License");
*you may not use this file except in compliance with the License.
*You may obtain a copy of the License at

*   http://www.apache.org/licenses/LICENSE-2.0

*Unless required by applicable law or agreed to in writing, software
*distributed under the License is distributed on an "AS IS" BASIS,
*WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
*See the License for the specific language governing permissions and
*limitations under the License.
*/

package org.example.common.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.ExclusionStrategy;
import com.google.gson.FieldAttributes;
import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.google.gson.internal.LinkedTreeMap;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.io.StringReader;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Slf4j
public class JsonUtil {

    private static final String PERIOD = ".";

    private static final Gson GSON_WITH_NULL = new GsonBuilder().serializeNulls().create();

    private static final Gson GSON_UPPER_CAMEL_CASE = getUpperGson();

    public static Gson GSON = getGson();

    private static ObjectMapper mapper = new ObjectMapper();

    public static String toJsonString(Object object) {
        return GSON.toJson(object);
    }

    public static String toJsonStringExcludeFields(Object object, List<String> fieldList) {
        Gson gson = new GsonBuilder().addSerializationExclusionStrategy(new ExclusionStrategy() {
            @Override
            public boolean shouldSkipField(FieldAttributes fieldAttributes) {
                return fieldList.contains(fieldAttributes.getName());
            }

            @Override
            public boolean shouldSkipClass(Class<?> arg0) {
                return false;
            }
        }).create();
        return gson.toJson(object);
    }

    public static String toJsonStringWithNull(Object object) {
        return GSON_WITH_NULL.toJson(object);
    }

    public static <T> T parseObject(String json, Class<T> classOfT) {
        return GSON.fromJson(json, classOfT);
    }

    public static <T> T parseObjectCustom(String json, Class<T> classOfT) {
        JsonReader reader = new JsonReader(new StringReader(json));
        reader.setLenient(true);
        try {
            return (T) read(reader);
        } catch (IOException e) {
            log.error("Json processing exception.", e);
            throw new IllegalArgumentException(e);
        }
    }

    public static <T> T fromJson(String json, Type typeOfT) {
        return GSON.fromJson(json, typeOfT);
    }

    public static Object read(JsonReader in) throws IOException {
        JsonToken token = in.peek();
        //判断字符串的实际类型
        switch (token) {
            case BEGIN_ARRAY:
                List<Object> list = new ArrayList<>();
                in.beginArray();
                while (in.hasNext()) {
                    list.add(read(in));
                }
                in.endArray();
                return list;

            case BEGIN_OBJECT:
                Map<String, Object> map = new LinkedTreeMap<>();
                in.beginObject();
                while (in.hasNext()) {
                    map.put(in.nextName(), read(in));
                }
                in.endObject();
                return map;
            case STRING:
                return in.nextString();
            case NUMBER:
                String s = in.nextString();
                if (s.contains(".")) {
                    return Double.valueOf(s);
                } else {
                    return Long.valueOf(s);
                }
            case BOOLEAN:
                return in.nextBoolean();
            case NULL:
                in.nextNull();
                return null;
            default:
                throw new IllegalStateException();
        }
    }

    public static <T> T parseJsonList(String jsonStr, Type typeOfT) {
        Gson gson = new Gson();
        return gson.fromJson(jsonStr, typeOfT);
    }

    public static Boolean isJson(String value) {
        JsonElement jsonElement;
        try {
            jsonElement = JsonParser.parseString(value);
        } catch (Exception e) {
            return false;
        }
        if (jsonElement == null || jsonElement.isJsonNull()) {
            return false;
        }
        if (!jsonElement.isJsonObject() && !jsonElement.isJsonArray()) {
            return false;
        }
        return true;
    }

    public static Gson getGson() {
        Gson gson = new GsonBuilder().disableHtmlEscaping().create();
        return gson;
    }

    public static Gson getUpperGson() {
        Gson gson = new GsonBuilder().disableHtmlEscaping().setFieldNamingPolicy(FieldNamingPolicy.UPPER_CAMEL_CASE)
                .create();
        return gson;
    }

    public static <T> T parseObjectByJackson(String json, Class<T> classOfT) {
        // To convert JSON strings with underscores to camel case, please use the @JsonProperty annotation.
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        try {
            return mapper.readValue(json, classOfT);
        } catch (JsonProcessingException e) {
            log.error("Json processing exception.", e);
            throw new IllegalArgumentException(e);
        }
    }

    public static <T> T parseObjectUpperCamelCase(String json, Type typeOfT) {
        return GSON_UPPER_CAMEL_CASE.fromJson(json, typeOfT);
    }
}
