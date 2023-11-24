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

import org.example.common.constant.PayPeriodUnit;
import org.example.common.param.GetServiceCostParam;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.slf4j.Logger;

import java.util.HashMap;

import static org.example.common.utils.BeanUtil.populateObject;

class BeanUtilTest {

    @Mock
    Logger log;

    @Mock
    BeanUtil beanUtil;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    void testPopulateObject() {
        HashMap<String, Object> map = new HashMap<>();
        map.put("PayPeriodUnit", "Month");
        map.put("notExist", "notExist");
        map.put("PayPeriod", 12313L);
        GetServiceCostParam param = new GetServiceCostParam();
        populateObject(map, param);
        Assertions.assertTrue(param.getPayPeriodUnit() == PayPeriodUnit.Month);
    }
}
