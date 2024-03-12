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

package org.example.service.impl;

import com.alipay.api.response.AlipayTradeQueryResponse;
import org.example.common.BaseResult;
import org.example.common.adapter.BaseAlipayClient;
import org.example.common.config.AlipayConfig;
import org.example.common.constant.AliPayConstants;
import org.example.common.constant.TradeStatus;
import org.example.common.dataobject.OrderDO;
import org.example.common.dto.OrderDTO;
import org.example.common.model.UserInfoModel;
import org.example.common.param.order.GetOrderParam;
import org.example.common.param.payment.CreateTransactionParam;
import org.example.service.order.OrderService;
import org.example.service.payment.PaymentServiceManger;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.slf4j.Logger;
import org.springframework.mock.web.MockHttpServletRequest;

import java.util.HashMap;
import java.util.Map;

import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyDouble;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.when;

class AlipayServiceImplTest {

    @Mock
    AlipayConfig alipayConfig;

    @Mock
    OrderService orderService;

    @Mock
    BaseAlipayClient baseAlipayClient;

    @Mock
    Logger log;

    @InjectMocks
    PaymentServiceManger paymentServiceManger;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    void testVerifyTradeCallbackAlreadySuccess() {
        OrderDTO orderDTO = new OrderDTO();
        orderDTO.setTradeStatus(TradeStatus.TRADE_SUCCESS);
        orderDTO.setOrderId("alipay-123");
        orderDTO.setTotalAmount(100.0);
        when(alipayConfig.getAppId()).thenReturn("getAppidResponse");
        when(alipayConfig.getPid()).thenReturn("getPidResponse");

        BaseResult<OrderDTO> orderResult = new BaseResult<>(orderDTO);
        when(orderService.getOrder(any(), any())).thenReturn(orderResult);

        when(baseAlipayClient.queryOutTrade(anyString())).thenReturn(null);
        when(baseAlipayClient.verifySignature(anyString(), anyString())).thenReturn(true);

        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setParameter("trade_status", "TRADE_SUCCESS");
        request.setParameter("type", "ALIPAY");
        request.setParameter("total_amount", "100.0");

        String result = paymentServiceManger.verifyTradeCallback(request);
        Assertions.assertEquals("success", result);
    }

    @Test
    void testVerifyTradeCallbackSuccess() {
        Map<String, String> map = new HashMap<>();
        map.put(AliPayConstants.OUT_TRADE_NO, "alipay-123");
        OrderDTO orderFromOts = new OrderDTO();
        orderFromOts.setTradeStatus(TradeStatus.WAIT_BUYER_PAY);
        orderFromOts.setTotalAmount(100.0);
        BaseResult<OrderDTO> orderResult = new BaseResult<>(orderFromOts);

        when(orderService.getOrder(eq(null), any(GetOrderParam.class))).thenReturn(orderResult);
        when(baseAlipayClient.verifySignature(eq("validSignature"), anyString())).thenReturn(true);
        AlipayTradeQueryResponse queryResponse = new AlipayTradeQueryResponse();
        queryResponse.setTradeStatus(TradeStatus.TRADE_SUCCESS.name());
        when(baseAlipayClient.queryOutTrade(eq("alipay-123"))).thenReturn(queryResponse);
        when(alipayConfig.getPid()).thenReturn("111");
        when(alipayConfig.getAppId()).thenReturn("222");
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setParameter(AliPayConstants.OUT_TRADE_NO, "validOrderId");
        request.setParameter("seller_id", "111");
        request.setParameter("app_id", "222");
        request.setParameter("total_amount", "100.0");
        request.setParameter("sign", "validSignature");

        String result = paymentServiceManger.verifyTradeCallback(request);
        Assertions.assertEquals("success", result);
        Mockito.verify(orderService, Mockito.times(1)).updateOrder(Mockito.any(UserInfoModel.class), Mockito.any(OrderDO.class));
    }

    @Test
    void testVerifyTradeCallbackOrderNotFound() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setParameter(AliPayConstants.OUT_TRADE_NO, "invalidOrderId");
        BaseResult<OrderDTO> orderResult = BaseResult.success();
        when(orderService.getOrder(eq(null), any(GetOrderParam.class))).thenReturn(orderResult);
        String result = paymentServiceManger.verifyTradeCallback(request);
        Assertions.assertEquals("failure", result);
        Mockito.verify(orderService, Mockito.never()).updateOrder(Mockito.any(UserInfoModel.class), Mockito.any(OrderDO.class));
    }

    @Test
    void testVerifyBusinessDataFailed() {
        OrderDO unverifiedOrder = new OrderDO();
        unverifiedOrder.setSign("invalidSignature");
        Map<String, String> map = new HashMap<>();
        map.put(AliPayConstants.OUT_TRADE_NO, "alipay-123");

        OrderDTO orderFromOts = new OrderDTO();
        orderFromOts.setTradeStatus(TradeStatus.WAIT_BUYER_PAY);
        orderFromOts.setTotalAmount(100.0);

        BaseResult<OrderDTO> orderResult = new BaseResult<>(orderFromOts);
        when(orderService.getOrder(eq(null), any(GetOrderParam.class))).thenReturn(orderResult);
        when(alipayConfig.getPid()).thenReturn("111");
        when(alipayConfig.getAppId()).thenReturn("222");
        when(baseAlipayClient.verifySignature(eq("invalidSignature"), anyString())).thenReturn(false);

        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setParameter(AliPayConstants.OUT_TRADE_NO, "alipay-123");
        request.setParameter("seller_id", "111");
        request.setParameter("app_id", "222");
        request.setParameter("total_amount", "99.0");
        String result = paymentServiceManger.verifyTradeCallback(request);
        Assertions.assertEquals("failure", result);
        Mockito.verify(orderService, Mockito.never()).updateOrder(Mockito.any(UserInfoModel.class), Mockito.any(OrderDO.class));
    }

    @Test
    void testCreateTransaction() {
        when(baseAlipayClient.createTransaction(anyDouble(), anyString(), anyString())).thenReturn("createTransactionResponse");

        String result = paymentServiceManger.createTransaction(Mockito.any(UserInfoModel.class), Mockito.any(CreateTransactionParam.class));
        Assertions.assertEquals("createTransactionResponse", result);
    }

    @Test
    void testRefundOrder() {
        when(baseAlipayClient.refundOrder(anyString(), anyDouble(), anyString())).thenReturn(Boolean.TRUE);

        Boolean result = paymentServiceManger.refundOrder("orderId", Double.valueOf(0), "refundId");
        Assertions.assertEquals(Boolean.TRUE, result);
    }
}
