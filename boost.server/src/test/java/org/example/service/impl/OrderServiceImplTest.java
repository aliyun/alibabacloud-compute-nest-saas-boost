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

import com.alipay.api.AlipayApiException;
import com.aliyun.computenestsupplier20210521.models.CreateServiceInstanceResponse;
import org.example.common.BaseResult;
import org.example.common.ListResult;
import org.example.common.constant.PaymentType;
import org.example.common.constant.ProductName;
import org.example.common.constant.TradeStatus;
import org.example.common.dataobject.OrderDO;
import org.example.common.dto.OrderDTO;
import org.example.common.helper.OrderOtsHelper;
import org.example.common.helper.ServiceInstanceLifeStyleHelper;
import org.example.common.helper.WalletHelper;
import org.example.common.model.UserInfoModel;
import org.example.common.param.CreateOrderParam;
import org.example.common.param.GetOrderParam;
import org.example.common.param.GetServiceCostParam;
import org.example.common.param.ListOrdersParam;
import org.example.common.param.RefundOrderParam;
import org.example.service.AlipayService;
import org.example.service.ServiceInstanceLifecycleService;
import org.example.service.ServiceManager;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.slf4j.Logger;
import org.springframework.http.HttpStatus;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyBoolean;
import static org.mockito.Mockito.anyDouble;
import static org.mockito.Mockito.anyLong;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.when;

class OrderServiceImplTest {

    @Mock
    AlipayService alipayService;

    @Mock
    OrderOtsHelper orderOtsHelper;

    @Mock
    WalletHelper walletHelper;

    @Mock
    ServiceInstanceLifecycleService serviceInstanceLifecycleService;

    @Mock
    Logger log;

    @Mock
    private ServiceInstanceLifeStyleHelper serviceInstanceLifeStyleHelper;

    @Mock
    private ServiceManager serviceManager;

    @InjectMocks
    OrderServiceImpl orderServiceImpl;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    void testCreateOrder() throws AlipayApiException {
        when(alipayService.createTransaction(anyDouble(), anyString(), anyString())).thenReturn("createTransactionResponse");
        CreateServiceInstanceResponse response = new CreateServiceInstanceResponse();
        response.setStatusCode(HttpStatus.OK.value());
        when(serviceInstanceLifecycleService.createServiceInstance(any(), any(), anyBoolean())).thenReturn(response);
        when(serviceManager.getServiceCost(any(), any(GetServiceCostParam.class))).thenReturn(new BaseResult<Double>("code", "message", Double.valueOf(0), "requestId"));
        List<OrderDTO> orderList = new ArrayList<>();
        orderList.add(new OrderDTO());
        ListResult<OrderDTO> orderDtoListResult = ListResult.genSuccessListResult(orderList, 1);
        when(orderOtsHelper.listOrders(anyList(), anyList(), anyList(), anyString(), anyList())).thenReturn(orderDtoListResult);
        CreateOrderParam createOrderParam = new CreateOrderParam();
        createOrderParam.setType(PaymentType.ALIPAY);
        createOrderParam.setProductComponents("{\n" +
                "  \"RegionId\":\"cn-hangzhou\",\n" +
                "  \"SpecificationName\":\"低配版(Entry Level Package)\",\n" +
                "  \"PayPeriod\":1,\n \"PayPeriodUnit\":\"Month\"\n" +
                "}");
        createOrderParam.setProductName(ProductName.SERVICE_INSTANCE);
        BaseResult<String> result = orderServiceImpl.createOrder(new UserInfoModel("sub", "name", "loginName", "123", "123"), createOrderParam);
        Assertions.assertTrue(result.getData().equals("createTransactionResponse"));
    }

    @Test
    void testCreateOrderWithExistedServiceInstance() throws AlipayApiException {
        when(alipayService.createTransaction(anyDouble(), anyString(), anyString())).thenReturn("createTransactionResponse");
        CreateServiceInstanceResponse response = new CreateServiceInstanceResponse();
        response.setStatusCode(HttpStatus.OK.value());
        when(serviceInstanceLifecycleService.createServiceInstance(any(), any(), anyBoolean())).thenReturn(response);
        when(serviceManager.getServiceCost(any(), any(GetServiceCostParam.class))).thenReturn(new BaseResult<Double>("code", "message", Double.valueOf(0), "requestId"));
        List<OrderDTO> orderList = new ArrayList<>();
        orderList.add(new OrderDTO());
        ListResult<OrderDTO> orderDtoListResult = ListResult.genSuccessListResult(orderList, 1);
        when(orderOtsHelper.listOrders(anyList(), any(), anyList(), any(), anyList())).thenReturn(orderDtoListResult);

        CreateOrderParam createOrderParam = new CreateOrderParam();
        createOrderParam.setType(PaymentType.ALIPAY);
        createOrderParam.setProductComponents("{\n" +
                "  \"RegionId\":\"cn-hangzhou\",\n" +
                "  \"SpecificationName\":\"低配版(Entry Level Package)\",\n" +
                "  \"PayPeriod\":1,\n \"PayPeriodUnit\":\"Month\",\n" +
                "  \"ServiceInstanceId\":\"si-123\"\n" +
                "}");
        createOrderParam.setProductName(ProductName.SERVICE_INSTANCE);
        BaseResult<String> result = orderServiceImpl.createOrder(new UserInfoModel("sub", "name", "loginName", "123", "123"), createOrderParam);
        Assertions.assertTrue(result.getData().equals("createTransactionResponse"));
    }

    @Test
    void testGetOrder() {
        when(orderOtsHelper.getOrder(anyString(), anyLong())).thenReturn(new OrderDTO());

        BaseResult<OrderDTO> result = orderServiceImpl.getOrder(new UserInfoModel("sub", "name", "loginName", "123", "123"), new GetOrderParam("orderId"));
        Assertions.assertTrue(result.getCode().equals("200"));
    }

    @Test
    void testListOrders() {
        when(orderOtsHelper.listOrders(any(), any(),anyList(), anyString(), anyList())).thenReturn(ListResult.genSuccessListResult(Arrays.asList(new OrderDTO()), 1, "requestId"));
        ListOrdersParam listOrdersParam = new ListOrdersParam();
        listOrdersParam.setStartTime("2022-09-08T09:09:09Z");
        listOrdersParam.setEndTime("2022-09-08T09:09:09Z");
        ListResult<OrderDTO> result = orderServiceImpl.listOrders(new UserInfoModel("sub", "name", "loginName", "123", "123"),listOrdersParam );
        Assertions.assertNull(result);
    }

    @Test
    void testUpdateOrder() {
        when(alipayService.refundOrder(anyString(), anyDouble(), anyString())).thenReturn(Boolean.TRUE);
        when(orderOtsHelper.updateOrder(any())).thenReturn(Boolean.TRUE);
        when(serviceInstanceLifecycleService.createServiceInstance(any(), any(), anyBoolean())).thenReturn(null);

        orderServiceImpl.updateOrder(new UserInfoModel(), new OrderDO());
    }

    @Test
    void testRefundOrder() {
        OrderDTO orderDTO = createMockOrderDTO();
        List<OrderDTO> orderList = createMockOrderList();
        ListResult<OrderDTO> orderDtoListResult = ListResult.genSuccessListResult(orderList, 1);
        when(orderOtsHelper.getOrder(anyString(), anyLong())).thenReturn(orderDTO);
        when(walletHelper.getRefundAmount(anyDouble(), anyString(), anyString(), anyLong(), any())).thenReturn(Double.valueOf(0));
        when(orderOtsHelper.updateOrder(any())).thenReturn(Boolean.TRUE);
        when(orderOtsHelper.listOrders(anyList(), any(), any(), any(), anyList())).thenReturn(orderDtoListResult);

        RefundOrderParam refundOrderParam = createMockRefundOrderParam("123", true);
        refundOrderParam.setServiceInstanceId(null);
        BaseResult<Double> result = orderServiceImpl.refundOrder(createMockUserInfoModel(), refundOrderParam);
        Assertions.assertTrue(result.getCode().equals("200"));
        refundOrderParam.setDryRun(false);
        result = orderServiceImpl.refundOrder(createMockUserInfoModel(), refundOrderParam);
        Assertions.assertTrue(result.getCode().equals("200"));
    }

    @Test
    void testRefundOrderWithExpiredServiceInstanceId() {
        OrderDTO orderDTO = createMockOrderDTO();
        List<OrderDTO> orderList = createMockOrderList();
        ListResult<OrderDTO> orderDtoListResult = ListResult.genSuccessListResult(orderList, 1);
        when(orderOtsHelper.getOrder(anyString(), anyLong())).thenReturn(orderDTO);
        when(walletHelper.getRefundAmount(anyDouble(), anyString(), anyString(), anyLong(), any())).thenReturn(Double.valueOf(0));
        when(orderOtsHelper.updateOrder(any())).thenReturn(Boolean.TRUE);
        when(orderOtsHelper.listOrders(anyList(), any(), any(), any(), anyList())).thenReturn(orderDtoListResult);
        when(serviceInstanceLifeStyleHelper.checkServiceInstanceExpiration(anyList(), anyLong())).thenReturn(true);
        RefundOrderParam refundOrderParam = createMockRefundOrderParam("123", true);
        BaseResult<Double> result = orderServiceImpl.refundOrder(createMockUserInfoModel(), refundOrderParam);
        Assertions.assertTrue(result.getCode().equals("200"));
        refundOrderParam.setDryRun(false);
        result = orderServiceImpl.refundOrder(createMockUserInfoModel(), refundOrderParam);
        Assertions.assertTrue(result.getCode().equals("200"));
    }

    @Test
    void testRefundOrderWithServiceInstanceId() {
        OrderDTO orderDTO = createMockOrderDTO();
        List<OrderDTO> orderList = createMockOrderList();
        ListResult<OrderDTO> orderDtoListResult = ListResult.genSuccessListResult(orderList, 1);
        when(orderOtsHelper.getOrder(anyString(), anyLong())).thenReturn(orderDTO);
        when(walletHelper.getRefundAmount(anyDouble(), anyString(), anyString(), anyLong(), any())).thenReturn(Double.valueOf(0));
        when(orderOtsHelper.updateOrder(any())).thenReturn(Boolean.TRUE);
        when(orderOtsHelper.listOrders(anyList(), any(), any(), any(), anyList())).thenReturn(orderDtoListResult);
        when(serviceInstanceLifeStyleHelper.checkServiceInstanceExpiration(anyList(), anyLong())).thenReturn(false);
        when(orderOtsHelper.isOrderInConsuming(any(), anyLong())).thenReturn(true);
        RefundOrderParam refundOrderParam = createMockRefundOrderParam("123", true);
        BaseResult<Double> result = orderServiceImpl.refundOrder(createMockUserInfoModel(), refundOrderParam);
        Assertions.assertTrue(result.getCode().equals("200"));
        refundOrderParam.setDryRun(false);
        result = orderServiceImpl.refundOrder(createMockUserInfoModel(), refundOrderParam);
        Assertions.assertTrue(result.getCode().equals("200"));
    }

    private OrderDTO createMockOrderDTO() {
        OrderDTO orderDTO = new OrderDTO();
        orderDTO.setOrderId("123");
        orderDTO.setTradeStatus(TradeStatus.REFUNDING);
        orderDTO.setTotalAmount(123.99);
        return orderDTO;
    }

    private List<OrderDTO> createMockOrderList() {
        List<OrderDTO> orderList = new ArrayList<>();
        OrderDTO firstOrderDto = new OrderDTO();
        firstOrderDto.setBillingStartDateLong(0L);
        firstOrderDto.setBillingEndDateLong(123L);
        OrderDTO latestOrderDto = new OrderDTO();
        latestOrderDto.setBillingStartDateLong(123L);
        latestOrderDto.setBillingEndDateLong(1234L);
        orderList.add(firstOrderDto);
        orderList.add(latestOrderDto);
        return orderList;
    }

    private RefundOrderParam createMockRefundOrderParam(String orderId, boolean isDryRun) {
        RefundOrderParam refundOrderParam = new RefundOrderParam();
        refundOrderParam.setOrderId(orderId);
        refundOrderParam.setDryRun(isDryRun);
        refundOrderParam.setServiceInstanceId("si-123");
        return refundOrderParam;
    }

    private UserInfoModel createMockUserInfoModel() {
        return new UserInfoModel("sub", "name", "loginName", "123", "123");
    }
}

