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

package org.example.common.adapter.impl;

import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.DefaultAlipayClient;
import com.alipay.api.domain.AlipayTradeQueryModel;
import com.alipay.api.request.AlipayTradeCloseRequest;
import com.alipay.api.request.AlipayTradePagePayRequest;
import com.alipay.api.request.AlipayTradeRefundRequest;
import com.alipay.api.response.AlipayTradeCloseResponse;
import com.alipay.api.response.AlipayTradePagePayResponse;
import com.alipay.api.response.AlipayTradeQueryResponse;
import com.ijpay.alipay.AliPayApi;
import com.ijpay.alipay.AliPayApiConfig;
import com.ijpay.alipay.AliPayApiConfigKit;
import java.lang.reflect.Field;
import mockit.Expectations;
import mockit.Injectable;
import mockit.Tested;
import mockit.internal.reflection.FieldReflection;
import org.example.common.adapter.OssClient;
import org.example.common.config.BoostAlipayConfig;
import org.example.common.constant.AliPayConstants;
import org.example.common.constant.Constants;
import org.example.common.dto.OrderDTO;
import org.example.common.exception.BizException;
import org.example.common.helper.LocalCertStorageHelper;
import org.junit.jupiter.api.Assertions;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.Test;

class BaseAlipayClientImplTest {

    @Injectable
    private BoostAlipayConfig boostAlipayConfig;

    @Tested
    private BaseAlipayClientImpl alipayClientImpl;

    @Injectable
    private AlipayTradePagePayRequest alipayTradePagePayRequest;

    @Injectable
    private LocalCertStorageHelper localCertStorageHelper;

    @Injectable
    private OssClient ossClient;

    private AlipayClient alipayClient = new DefaultAlipayClient(
            "https://openapi-sandbox.dl.alipaydev.com/gateway.do",
            null,
            privateKey,
            Constants.JSON_FORMAT,
            Constants.TRANSFORMATION_FORMAT_UTF_8.toLowerCase(),
            publicKey,
            AliPayConstants.SIGN_TYPE_RSA2);
    private AliPayApiConfig alipayApiConfig = AliPayApiConfig.builder()
            .setAppId("123")
            .setAliPayPublicKey(publicKey)
            .setPrivateKey(privateKey)
            .setCharset(Constants.TRANSFORMATION_FORMAT_UTF_8.toLowerCase())
            .setServiceUrl("https://openapi-sandbox.dl.alipaydev.com/gateway.do")
            .setSignType(AliPayConstants.SIGN_TYPE_RSA2)
            .build();

    private static final String privateKey = "MIIEvAIBADANBgkqhkiG9w0BAQEFAASCBKYwggSiAgEAAoIBAQCIB3gIM9zBAF4iuP3YTnhaAIWeO" +
            "xHtjFLEteP8msDW/L98PbIASu0WK/DBId0YNCuEiEwkX1wvbylWeGIQvWEb+YVqxRAJ39bsp1YMylUCoL+S8L9rG09zhuCqDqqNgUtlh7" +
            "FLuXMj3Oxq+TGmKj3kdVVaKxJTly7E2MB7z+0I7NpciwesI7/pJuNjz4DmfMRz7buqSPJ9qHWKgT7ZkUkjxxMMLY/fP6QaitBsn1ljpK7" +
            "UKob3UfoKSxVNZ5xym0O+9ElaFCEbyyLCoBmlvX8Ld40Kmi5IJ4/C6CUqpCYeeblRFmi1v5mDwp8mM6pnqOIMUCjTyBIQgxBbKuezNZsJAg" +
            "MBAAECggEAVW8jcztndYxOk05OmDFIo3ZadXOyIdkVDtkSdM+ZOC2L8nMpPcfROjCkyBgFrbLudhHIiLufwWb2tolrRgo7WH+RzOox1ZHRgdbjU" +
            "6ssUzc0uwwbnSCM+QkIR6ZodfFzsjPUJbQ23Zpenh0ekrdLIhaTYctVekykohtkYR7JR+XUxgc0PrsSgdy18wRA0BLdwq5ubsdDTCExBBaWH8KkblcFZ" +
            "0H5dwF/UjkS47Tg6hyhMJ5olGdq7kFUWAUnK26fmOGLk91HeeHjFih3mCA333ARMF2uUgQxNB7LsKzprBTlDxpznol9diqepwX/B/N27Qw9r4wa62aGoI1NLBtc" +
            "NQKBgQDdA3xudLDLnxNsHnFET68PoJXNA6W7Fc4glehBqB/27Qvphx/28vvfYKzwJLPh9s1CbyVTrT5bNWY3nt29MBZoHCP/tFol/xIRruD33wO3MxmOq/YbH6rYbYf8Ku" +
            "DGdmScJX1TSLGBws2ZYQEIkJP+s47TgsUlG3Mw3NErM1O2RwKBgQCdkAMyTaNfFZ6dkOVuOYaZnj8g1BEIhuqGxykbAdWA2pFrK7uZ+WAoibm4uF6SoRTtDJm4ZMOystwBdD2Drjqtk9" +
            "4JPfrw9gRPZvp8aLgtECmYyltdZucWJ1uSRF3TeDOOs2DkdclZUL6owQU9+Wwng+YRMiB1gk9RWMw56a28LwKBgAtch4QCGaiOB+iR9A6n7f7mqWy9mEJyz4LbUfI2G1aNPwSFPr5qJUqJZLp/mIOwN" +
            "UMKBag0irUTnKqVTx7PnwdBExStB8lWQG6KR+RWIZHEXbb43hDs4tJ3ZvsulRl+spNYVgX3AbEC+6C73Nq82+7G+jujSFEtBRHo+BO5cr83AoGAWbgVeeFns88VwNcvLbKoCHVpsQHQqTBZ/2EM8Qn8TyEmSQZgDu9HaA" +
            "30qVCi2Y/L4KeaKLuIDAt454MPPBpL42cdudmHOQp9rdEPtSPULCTV6uDfYbIkyCKlLAat/qqlgQ53j9/aRXeK+YDfwUhz45Rhv0zx2rfW/pU3xYpgmZUCgYAoHUgobzmrO2XJG2337drvTz/KvPVZDDZNoUaqdfzR1tZTC" +
            "wZL1EHNXr99MRoIBDhwIl5Xl/GEPZ8J03PvmtYdKnnilzC5e6OjGqQXc/w9FuQYvMZZvXyw3yYz9iPybtuZynRuJ7BJ64sdTMi/gIq88G681AScmH7SzH0AJ+43XZ==";

    private static final String publicKey = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAsxQgEeBMB6FX+TPbxRzxayt/12F2V1" +
            "3v3WRhqJnuPhWyqQL14zrXy2F1/X95/3vwXkWfO5pjV5EPLrOd0di8nCT6ieOTe5PtLtG4ro1x4pjSLBD7j/+/+EHl6kiQTHvnHJI+9aOYl" +
            "eZXc+wj/yqO6FeLKt0PKZ13MpFUy0rhYS2GGr+ajRRO4bwUthziSNs/j/txJKA4jVrNa8bDiYlcN/6TE3Vk/3c+OjYbw7Hll89Y2bhA+D" +
            "wORTKxRSj95oeES+co/y+CPVy619OjlyA2btgh6QMiQMwBVGxOu68ComhMNQLPF5P1Sjd/YdCkiPF9BuEDaZvotCQ43lqoxv+qUQIDAQAB";


    private void setPrivateKey(com.alipay.api.AlipayClient alipayClient) throws NoSuchFieldException {
        Field configField = DefaultAlipayClient.class.getDeclaredField("privateKey");
        configField.setAccessible(true);
        FieldReflection.setFieldValue(configField, alipayClient, privateKey);
    }

    private void setPublicKey(com.alipay.api.AlipayClient alipayClient) throws NoSuchFieldException {
        Field configField = DefaultAlipayClient.class.getDeclaredField("alipayPublicKey");
        configField.setAccessible(true);
        FieldReflection.setFieldValue(configField, alipayClient, publicKey);
    }

    private void setClient() throws NoSuchFieldException {
        Field clientConfig = BaseAlipayClientImpl.class.getDeclaredField("alipayClient");
        clientConfig.setAccessible(true);
        FieldReflection.setFieldValue(clientConfig, alipayClientImpl, alipayClient);
    }

    @Test
    void testCreateTransaction() throws AlipayApiException, NoSuchFieldException {
        AlipayTradePagePayResponse response = new AlipayTradePagePayResponse();
        response.setBody("body");
        setPrivateKey(alipayClient);

        new Expectations() {{
            alipayClient.pageExecute(withAny(new AlipayTradePagePayRequest()));
            result = response;

            boostAlipayConfig.getSignatureMethod();
            result = "PrivateKey";
        }};
        setClient();
        OrderDTO order = new OrderDTO();
        order.setOrderId("orderId");
        order.setTotalAmount(100L);
        order.setCommodityName("commodityName");
        alipayClientImpl.createOutTrade(order);
        Assertions.assertTrue(response.isSuccess());
    }

    @Test
    void testRefundOrder() throws AlipayApiException, NoSuchFieldException {
        setPrivateKey(alipayClient);
        AliPayApiConfigKit.setThreadLocalAliPayApiConfig(alipayApiConfig);
        new Expectations(){{
            alipayClient.pageExecute(withAny(new AlipayTradeRefundRequest()));
            result = true;

            boostAlipayConfig.getSignatureMethod();
            result = "PrivateKey";
        }};
        setClient();
        OrderDTO order = new OrderDTO();
        order.setOrderId("orderId");
        order.setTotalAmount(100L);
        order.setRefundAmount(100L);
        Assertions.assertDoesNotThrow(()->alipayClientImpl.refundOutTrade(order));
    }

    @Test
    void testCloseOrder() throws NoSuchFieldException, AlipayApiException {
        setPrivateKey(alipayClient);
        AliPayApiConfigKit.setThreadLocalAliPayApiConfig(alipayApiConfig);
        new Expectations(){{
            alipayClient.pageExecute(withAny(new AlipayTradeCloseRequest()));
            AlipayTradeCloseResponse alipayTradeCloseResponse = new AlipayTradeCloseResponse();
            alipayTradeCloseResponse.setBody("body");
            result = alipayTradeCloseResponse;

            boostAlipayConfig.getSignatureMethod();
            result = "PrivateKey";
        }};
        setClient();
        Assertions.assertDoesNotThrow(()->alipayClientImpl.closeOutTrade("orderId"));
    }

    @Test
    void testQueryOutTrade() throws NoSuchFieldException, AlipayApiException {
        setPrivateKey(alipayClient);
        AliPayApiConfigKit.setThreadLocalAliPayApiConfig(alipayApiConfig);
        new Expectations(){{
            AliPayApi.tradeQueryToResponse(withAny(new AlipayTradeQueryModel()));
            AlipayTradeQueryResponse response = new AlipayTradeQueryResponse();
            response.setBody("body");
            result = response;
        }};
        setClient();
        Assertions.assertDoesNotThrow( ()->alipayClientImpl.queryOutTrade("orderId", false));
    }

    @Test
    void testVerifySignature() throws NoSuchFieldException {
        setPublicKey(alipayClient);
        assertThrows(BizException.class, ()->alipayClientImpl.verifySignatureWithKey("sign","content"));
    }
}

