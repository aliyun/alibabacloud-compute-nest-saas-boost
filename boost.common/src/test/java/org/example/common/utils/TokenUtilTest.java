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

import org.example.common.constant.ChargeType;
import org.example.common.constant.PayChannel;
import org.example.common.constant.PayPeriodUnit;
import org.example.common.param.order.CreateOrderParam;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;


class TokenUtilTest {


    @Test
    public void testCreateSpiToken() {
        CreateOrderParam param = new CreateOrderParam();
        param.setChargeType(ChargeType.PRE_PAID);
        param.setPayPeriod(12L);
        param.setPayPeriodUnit(PayPeriodUnit.Month);
        param.setPayChannel(PayChannel.ALIPAY);
        param.setOrderType("new");
        param.setSpecificationName("basic");
        param.setCommodityCode("CC12345");
        param.setToken("ignoredToken");

        Map<String, String> map = new HashMap<>();
        map.put("commodityCode", "CC12345");
        map.put("chargeType", ChargeType.PRE_PAID.toString());
        map.put("payPeriodUnit", PayPeriodUnit.Month.toString());
        map.put("payChannel", PayChannel.ALIPAY.toString());
        map.put("orderType", "new");
        map.put("specificationName", "basic");
        map.put("payPeriod", "12");

        String data = TokenUtil.buildUrlParams(map);
        data += "&key=" + "secretKey";
        String md5HexString = EncryptionUtil.getMd5HexString(data);
        String actualToken = TokenUtil.createSpiToken(param, "secretKey");

        assertEquals(md5HexString, actualToken);
    }
}