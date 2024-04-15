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


import mockit.Tested;
import org.example.common.constant.PayChannel;
import org.example.common.dataobject.OrderDO;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.slf4j.Logger;
import org.springframework.mock.web.MockHttpServletRequest;

import java.util.HashMap;
import java.util.Map;

import static org.example.common.constant.TradeStatus.TRADE_SUCCESS;

class HttpUtilTest {

    @Mock
    Logger log;

    @Tested
    private HttpUtil testedHttpUtil;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    void testDoGet() {
        String result = HttpUtil.doGet("http://baidu.com");
        Assertions.assertNotNull(result);
    }

    @Test
    void testGetExpandUrl() {
        String result = HttpUtil.getExpandUrl("http://baidu.com", "paramObj");
        Assertions.assertNotNull(result);
    }

    @Test
    void testRequestToMap() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setParameter("trade_status", "TRADE_SUCCESS");
        request.setParameter("type", "ALIPAY");
        Map<String, String> requestToMap = HttpUtil.requestToMap(request);
        Map<String, String> map = new HashMap<>();
        map.put("trade_status", "TRADE_SUCCESS");
        map.put("type", "ALIPAY");
        Assertions.assertEquals(map, requestToMap);
    }

    @Test
    void testRequestToObject() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setParameter("trade_status", "TRADE_SUCCESS");
        request.setParameter("pay_channel", "ALIPAY");
        request.setParameter("notExistField", "notExistValue");
        OrderDO orderDO = new OrderDO();
        orderDO.setPayChannel(PayChannel.ALIPAY);
        orderDO.setTradeStatus(TRADE_SUCCESS);
        OrderDO result = HttpUtil.requestToObject(request, OrderDO.class);
        Assertions.assertEquals(orderDO, result);
    }
}

