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

import com.google.gson.Gson;
import lombok.Getter;
import lombok.Setter;
import org.example.common.model.UserInfoModel;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

class JsonUtilTest {

    @Getter
    @Setter
    static class Dummy {
        private String key1;
        private Integer key2;
    }

    @Test
    public void toJsonString() {
        Dummy d = new Dummy();
        d.setKey1("value");
        d.setKey2(1);
        String s = JsonUtil.toJsonString(d);
        assertEquals("{\"key1\":\"value\",\"key2\":1}", s);
    }

    @Test
    public void nullToStringWithNull() {
        String s = JsonUtil.toJsonStringWithNull(null);
        assertEquals("null", s);
    }

    @Test
    public void nullToStringWithNull1() {
        Map<String, String> map = new HashMap<>();
        map.put(null, "v1");
        map.put("v2", null);
        String s = JsonUtil.toJsonStringWithNull(map);
        assertEquals("{\"null\":\"v1\",\"v2\":null}", s);
    }

    @Test
    public void parseObject() {
        String s = "{\"key1\":\"value\",\"key2\":1}";
        Dummy dummy = JsonUtil.parseObject(s, Dummy.class);
        assertEquals("value", dummy.getKey1());
        assertEquals(1, dummy.getKey2());
    }

    @Test
    public void parseJsonList() {
        String jsonList = "[\"a\", \"b\"]";
        List list = JsonUtil.parseJsonList(jsonList, List.class);
        assertEquals(2, list.size());
        assertEquals("a", list.get(0));
        assertEquals("b", list.get(1));
    }

    @Test
    void testIsJson() {
        assertThat(JsonUtil.isJson("value")).isFalse();
    }

    @Test
    void testIsJsonWithYaml() {
        String input = "ROSTemplateFormatVersion: '2015-09-01'\n" +
                "Description: \"a\"\n" +
                "Parameters:\n" +
                "  ZoneId:\n" +
                "    Type: String\n" +
                "    AssociationProperty: ALIYUN::ECS::Instance::ZoneId\n" +
                "    Label:\n" +
                "      en: VSwitch Availability Zone\n" +
                "      zh-cn: 交换机可用区\n" +
                "Resources:\n" +
                "  EcsSecurityGroup:\n" +
                "    Type: ALIYUN::ECS::SecurityGroup\n" +
                "    Properties:\n" +
                "      SecurityGroupName:\n" +
                "Metadata:\n" +
                "  'ALIYUN::ROS::Interface':\n" +
                "    ParameterGroups:\n" +
                "      - Parameters:\n" +
                "          - ZoneId\n" +
                "    TemplateTags:\n" +
                "      - Creates one ECS(RabbitMQ) instance - Existing Vpc";
        assertThat(JsonUtil.isJson(input)).isFalse();
    }

    @Test
    void testGetGson() {
        Assertions.assertDoesNotThrow(()->JsonUtil.toJsonString(null));
        final Gson result = JsonUtil.getGson();
    }

    @Test
    void testGetUpperGson() {
        // Setup
        // Run the test
        final Gson result = JsonUtil.getUpperGson();

        // Verify the results
    }

    @Test
    void testParseObjectByJackson() throws Exception {
        UserInfoModel userInfoModel = new UserInfoModel();
        userInfoModel.setLoginName("rain");
        System.out.println(JsonUtil.toJsonString(userInfoModel));
        assertThat(JsonUtil.parseObjectByJackson("{\"login_name\":\"rain\"}\n", UserInfoModel.class).getLoginName().equals("rain"));
    }

    @Getter
    @Setter
    static class Demo {
        private String key1;
        private List<Object> key2;
    }

    @Test
    public void isJsonTest() {
        String json = "{ \"foo\": \"bar\" }";
        assertEquals(true, JsonUtil.isJson(json));
        json = "[1, 2, 3]";
        assertEquals(true, JsonUtil.isJson(json));
        json = "[\"1\", \"2\", \"3\"]";
        assertEquals(true, JsonUtil.isJson(json));
        json = "[1]";
        assertEquals(true, JsonUtil.isJson(json));
        json = "[\"1\"]";
        assertEquals(true, JsonUtil.isJson(json));
        json = "{\"a\":\"b\",\"c\":\"d\"}";
        assertEquals(true, JsonUtil.isJson(json));
        json = "{}";
        assertEquals(true, JsonUtil.isJson(json));
        json = "[]";
        assertEquals(true, JsonUtil.isJson(json));
        json = "";
        assertEquals(false, JsonUtil.isJson(json));
        json = "[1";
        assertEquals(false, JsonUtil.isJson(json));
        json = "{\"a\":\"b\"";
        assertEquals(false, JsonUtil.isJson(json));
        json = "{\"a\"}";
        assertEquals(false, JsonUtil.isJson(json));
        json = "{\"a\":\"b\",\"c\":\"d\",}";
        assertEquals(false, JsonUtil.isJson(json));
        json = "jsonString";
        assertEquals(false, JsonUtil.isJson(json));
        json = "{\n" +
                "  \"Parameters\": {\n" +
                "    \"item\": {\n" +
                "      \"Type\": \"Json\",\n" +
                "      \"Default\": [\"1\"]\n" +
                "    }\n" +
                "  },\n" +
                "  \"Outputs\": {\n" +
                "  }\n" +
                "}";
        assertEquals(true, JsonUtil.isJson(json));
    }

}
