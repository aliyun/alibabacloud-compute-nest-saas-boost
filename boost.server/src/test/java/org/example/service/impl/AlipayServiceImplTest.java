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
import org.apache.commons.beanutils.BeanUtils;
import org.example.common.BaseResult;
import org.example.common.adapter.BaseAlipayClient;
import org.example.common.config.BoostAlipayConfig;
import org.example.common.constant.AliPayConstants;
import org.example.common.constant.PayChannel;
import org.example.common.constant.TradeStatus;
import org.example.common.dataobject.OrderDO;
import org.example.common.dto.OrderDTO;
import org.example.common.helper.ots.OrderOtsHelper;
import org.example.common.model.PaymentOrderModel;
import org.example.common.model.UserInfoModel;
import org.example.common.param.order.GetOrderParam;
import org.example.common.utils.HttpUtil;
import org.example.common.utils.MoneyUtil;
import org.example.service.order.OrderService;
import org.example.service.payment.impl.AlipayServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.mockito.ArgumentMatchers.anyBoolean;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.slf4j.Logger;
import org.springframework.mock.web.MockHttpServletRequest;

import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.when;

class AlipayServiceImplTest {

    @Mock
    private BoostAlipayConfig boostAlipayConfig;

    @Mock
    private OrderService orderService;

    @Mock
    private BaseAlipayClient baseAlipayClient;

    @Mock
    private Logger log;

    @InjectMocks
    private AlipayServiceImpl alipayService;

    @Mock
    private OrderOtsHelper orderOtsHelper;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    void testVerifyTradeCallbackAlreadySuccess() {
        OrderDTO orderDTO = new OrderDTO();
        orderDTO.setTradeStatus(TradeStatus.TRADE_SUCCESS);
        orderDTO.setOrderId("alipay-123");
        orderDTO.setPayChannel(PayChannel.ALIPAY);
        BigDecimal bigDecimal = new BigDecimal(100.0);
        orderDTO.setTotalAmount(MoneyUtil.toCents(bigDecimal));
        when(orderOtsHelper.getOrder(anyString(), anyLong())).thenReturn(orderDTO);
        when(boostAlipayConfig.getAppId()).thenReturn("getAppidResponse");
        when(boostAlipayConfig.getPid()).thenReturn("getPidResponse");

        BaseResult<OrderDTO> orderResult = new BaseResult<>(orderDTO);
        when(orderService.getOrder(any(), any())).thenReturn(orderResult);

        when(baseAlipayClient.queryOutTrade(anyString(), anyBoolean())).thenReturn(null);
        when(baseAlipayClient.verifySignatureWithKey(anyString(), anyString())).thenReturn(true);

        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setParameter("trade_status", "TRADE_SUCCESS");
        request.setParameter("type", "ALIPAY");
        request.setParameter("total_amount", "100.0");
        Map<String, String> requestToMap = HttpUtil.requestToMap(request);
        OrderDO orderDO = HttpUtil.requestToObject(request, OrderDO.class);
        String result = alipayService.verifyTradeCallback(request);
        assertEquals("success", result);
    }

    @Test
    void testVerifyTradeCallbackSuccess() throws InvocationTargetException, IllegalAccessException {
        Map<String, String> map = new HashMap<>();
        map.put(AliPayConstants.OUT_TRADE_NO, "alipay-123");
        OrderDTO orderFromOts = new OrderDTO();
        orderFromOts.setTradeStatus(TradeStatus.WAIT_BUYER_PAY);
        BigDecimal bigDecimal = BigDecimal.valueOf(100.0);
        orderFromOts.setTotalAmount(MoneyUtil.toCents(bigDecimal));
        orderFromOts.setPayChannel(PayChannel.ALIPAY);
        BaseResult<OrderDTO> orderResult = new BaseResult<>(orderFromOts);
        when(orderOtsHelper.getOrder(anyString(), any())).thenReturn(orderFromOts);
        when(orderService.getOrder(eq(null), any(GetOrderParam.class))).thenReturn(orderResult);
        when(baseAlipayClient.verifySignatureWithKey(eq("validSignature"), anyString())).thenReturn(true);
        AlipayTradeQueryResponse queryResponse = new AlipayTradeQueryResponse();
        queryResponse.setTradeStatus(TradeStatus.TRADE_SUCCESS.name());
        when(baseAlipayClient.queryOutTrade(eq("validOrderId"), anyBoolean())).thenReturn(queryResponse);
        when(boostAlipayConfig.getPid()).thenReturn("111");
        when(boostAlipayConfig.getAppId()).thenReturn("222");
        when(boostAlipayConfig.getSignatureMethod()).thenReturn("PrivateKey");
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setParameter(AliPayConstants.OUT_TRADE_NO, "validOrderId");
        request.setParameter("seller_id", "111");
        request.setParameter("app_id", "222");
        request.setParameter("total_amount", "100.0");
        request.setParameter("sign", "validSignature");
        Map<String, String> requestToMap = HttpUtil.requestToMap(request);
        PaymentOrderModel orderDO = HttpUtil.requestToObject(request, PaymentOrderModel.class);
        OrderDO unverifiedOrder = new OrderDO();
        BeanUtils.copyProperties(unverifiedOrder, orderDO);
        unverifiedOrder.setTotalAmount(MoneyUtil.toCents(new BigDecimal(orderDO.getTotalAmount())));
        String result = alipayService.verifyTradeCallback(request);
        assertEquals("success", result);
        Mockito.verify(orderService, Mockito.times(1)).updateOrder(Mockito.any(UserInfoModel.class), Mockito.any(OrderDO.class));
    }

    @Test
    void testVerifyTradeCallbackOrderNotFound() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setParameter(AliPayConstants.OUT_TRADE_NO, "invalidOrderId");
        BaseResult<OrderDTO> orderResult = BaseResult.success();
        when(orderService.getOrder(eq(null), any(GetOrderParam.class))).thenReturn(orderResult);
        String result = alipayService.verifyTradeCallback(request);
        assertEquals("failure", result);
        Mockito.verify(orderService, Mockito.never()).updateOrder(Mockito.any(UserInfoModel.class), Mockito.any(OrderDO.class));
    }

    @Test
    void testVerifyBusinessDataFailed() throws InvocationTargetException, IllegalAccessException {
        OrderDO unverifiedOrder = new OrderDO();
        unverifiedOrder.setSign("invalidSignature");
        Map<String, String> map = new HashMap<>();
        map.put(AliPayConstants.OUT_TRADE_NO, "alipay-123");

        OrderDTO orderFromOts = new OrderDTO();
        orderFromOts.setTradeStatus(TradeStatus.WAIT_BUYER_PAY);
        BigDecimal bigDecimal = new BigDecimal(100.0);
        orderFromOts.setTotalAmount(MoneyUtil.toCents(bigDecimal));
        orderFromOts.setPayChannel(PayChannel.ALIPAY);
        BaseResult<OrderDTO> orderResult = new BaseResult<>(orderFromOts);
        when(orderOtsHelper.getOrder(anyString(), anyLong())).thenReturn(orderFromOts);

        when(orderService.getOrder(eq(null), any(GetOrderParam.class))).thenReturn(orderResult);
        when(boostAlipayConfig.getPid()).thenReturn("111");
        when(boostAlipayConfig.getAppId()).thenReturn("222");
        when(baseAlipayClient.verifySignatureWithKey(eq("invalidSignature"), anyString())).thenReturn(false);

        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setParameter(AliPayConstants.OUT_TRADE_NO, "alipay-123");
        request.setParameter("seller_id", "111");
        request.setParameter("app_id", "222");
        request.setParameter("total_amount", "99.0");
        Map<String, String> requestToMap = HttpUtil.requestToMap(request);
        PaymentOrderModel orderDO = HttpUtil.requestToObject(request, PaymentOrderModel.class);
        BeanUtils.copyProperties(unverifiedOrder, orderDO);
        unverifiedOrder.setTotalAmount(MoneyUtil.toCents(new BigDecimal(orderDO.getTotalAmount())));
        String result = alipayService.verifyTradeCallback(request);
        assertEquals("failure", result);
        Mockito.verify(orderService, Mockito.never()).updateOrder(Mockito.any(UserInfoModel.class), Mockito.any(OrderDO.class));
    }

    @Test
    public void testCreateTransaction() {
        String outTradeNo = "TestTradeNo";

        OrderDTO mockOrder = new OrderDTO();
        mockOrder.setOrderId("TestTradeNo");
        mockOrder.setCommodityName("Test Subject");
        mockOrder.setTotalAmount(100L);
        mockOrder.setPaymentForm("");

        when(orderOtsHelper.getOrder(outTradeNo, null)).thenReturn(mockOrder);
        String expectedTransaction = "ExpectedTransactionForm";
        when(baseAlipayClient.createOutTrade(mockOrder)).thenReturn(expectedTransaction);

        String result = alipayService.createTransaction(mockOrder);

        assertEquals(expectedTransaction, result);
    }

    @Test
    void testRefundOrder() {
        when(baseAlipayClient.refundOutTrade(any())).thenReturn(Boolean.TRUE);

        OrderDTO order = new OrderDTO();
        order.setOrderId("orderId");
        order.setTotalAmount(100L);
        order.setRefundAmount(100L);
        Boolean result = alipayService.refundOutTrade(order);
        assertEquals(Boolean.TRUE, result);
    }
}
