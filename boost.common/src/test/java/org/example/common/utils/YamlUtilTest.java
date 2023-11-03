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
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.powermock.api.mockito.PowerMockito;
import org.yaml.snakeyaml.Yaml;

import java.util.HashMap;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;

public class YamlUtilTest {

    private YamlUtil yamlUtil;

    @Mock
    private Yaml yaml;

    @Mock
    private ObjectMapper objectMapper;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        yamlUtil = new YamlUtil();
    }

    @Test
    public void testConvertYamlToJson_shouldReturnCorrectJson() throws JsonProcessingException {
        String yamlData = "name: John Smith\nage: 30";
        Map<String, Object> data = new HashMap<>();
        data.put("name", "John Smith");
        data.put("age", 30);
        PowerMockito.when(yaml.load(anyString())).thenReturn(data);
        PowerMockito.when(objectMapper.writeValueAsString(any())).thenReturn("{\"name\":\"John Smith\",\"age\":30}");

        String result = yamlUtil.convertYamlToJson(yamlData);
        Assert.assertEquals("{\"name\":\"John Smith\",\"age\":30}", result);
    }
}
