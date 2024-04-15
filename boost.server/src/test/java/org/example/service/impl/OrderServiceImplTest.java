/*
*Copyright (c) Alibaba Group;
*Licensed under the Apache License, Version 2L (the "License");
*you may not use this file except in compliance with the License.
*You may obtain a copy of the License at

*   http://www.apache.org/licenses/LICENSE-2L

*Unless required by applicable law or agreed to in writing, software
*distributed under the License is distributed on an "AS IS" BASIS,
*WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
*See the License for the specific language governing permissions and
*limitations under the License.
*/

package org.example.service.impl;

import com.aliyun.computenestsupplier20210521.models.CreateServiceInstanceResponse;
import org.example.common.BaseResult;
import org.example.common.ListResult;
import org.example.common.constant.ServiceType;
import org.example.common.constant.TradeStatus;
import org.example.common.dataobject.OrderDO;
import org.example.common.dto.OrderDTO;
import org.example.common.helper.ServiceInstanceLifeStyleHelper;
import org.example.common.helper.WalletHelper;
import org.example.common.helper.ots.OrderOtsHelper;
import org.example.common.model.ServiceInstanceModel;
import org.example.common.model.ServiceMetadataModel;
import org.example.common.model.UserInfoModel;
import org.example.common.param.order.GetOrderParam;
import org.example.common.param.order.ListOrdersParam;
import org.example.common.param.order.RefundOrderParam;
import org.example.common.param.service.GetServiceMetadataParam;
import org.example.common.param.si.GetServiceInstanceParam;
import org.example.service.base.ServiceInstanceLifecycleService;
import org.example.service.base.ServiceManager;
import org.example.service.order.impl.OrderServiceImpl;
import org.example.service.payment.PaymentServiceManger;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.slf4j.Logger;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyBoolean;
import static org.mockito.Mockito.anyLong;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.powermock.api.mockito.PowerMockito.mock;

class OrderServiceImplTest {

    @Mock
    PaymentServiceManger alipayService;

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
    void testGetOrder() {
        when(orderOtsHelper.getOrder(anyString(), anyLong())).thenReturn(new OrderDTO());

        BaseResult<OrderDTO> result = orderServiceImpl.getOrder(new UserInfoModel("sub", "name", "loginName", "123", "123", Boolean.TRUE), new GetOrderParam("orderId"));
        Assertions.assertTrue(result.getCode().equals("200"));
    }

    @Test
    void testListOrders() {
        when(orderOtsHelper.listOrders(any(), any(),anyList(), anyString(), anyList())).thenReturn(ListResult.genSuccessListResult(Arrays.asList(new OrderDTO()), 1, "requestId"));
        ListOrdersParam listOrdersParam = new ListOrdersParam();
        listOrdersParam.setStartTime("2022-09-08T09:09:09Z");
        listOrdersParam.setEndTime("2022-09-08T09:09:09Z");
        ListResult<OrderDTO> result = orderServiceImpl.listOrders(new UserInfoModel("sub", "name", "loginName", "123", "123", Boolean.TRUE),listOrdersParam );
        Assertions.assertNull(result);
    }

    @Test
    void testUpdateOrder() {
        when(alipayService.refundOrder(anyString(), any(), anyString())).thenReturn(Boolean.TRUE);
        when(orderOtsHelper.updateOrder(any())).thenReturn(Boolean.TRUE);
        when(serviceInstanceLifecycleService.createServiceInstance(any(), any(), anyBoolean(), anyString())).thenReturn(null);

        orderServiceImpl.updateOrder(new UserInfoModel(), new OrderDO());
    }

    @Test
    void updateOrderWithException() {
        UserInfoModel userInfoModel = mock(UserInfoModel.class);
        ServiceMetadataModel serviceMetadataModel = new ServiceMetadataModel();
        serviceMetadataModel.setRetentionDays(1);
        CreateServiceInstanceResponse response = mock(CreateServiceInstanceResponse.class);
        OrderDO orderDO = mock(OrderDO.class);
        when(orderDO.getProductComponents()).thenReturn("{\n" +
                "  \"RegionId\":\"cn-hangzhou\",\n" +
                "  \"SpecificationName\":\"低配版(Entry Level Package)\",\n" +
                "  \"PayPeriod\":1,\n \"PayPeriodUnit\":\"Month\",\n" +
                "  \"ServiceInstanceId\":\"si-123\"\n" +
                "}");
        when(orderDO.getOrderId()).thenReturn("order-id");
        when(orderDO.getReceiptAmount()).thenReturn(100L);
        when(serviceInstanceLifecycleService.createServiceInstance(any(), any(), anyBoolean(), anyString())).thenReturn(response);
        when(serviceManager.getServiceMetadata(any(UserInfoModel.class), any(GetServiceMetadataParam.class)))
                .thenReturn(BaseResult.success(serviceMetadataModel));

        orderServiceImpl.updateOrder(userInfoModel, orderDO);

        ArgumentCaptor<OrderDO> orderCaptor = ArgumentCaptor.forClass(OrderDO.class);
        verify(orderOtsHelper).updateOrder(orderCaptor.capture());
        OrderDO capturedOrderDO = orderCaptor.getValue();
        if (capturedOrderDO.getTradeStatus() == TradeStatus.REFUNDED) {
            verify(alipayService).refundOrder(eq("order-id"), eq(BigDecimal.valueOf(100)), anyString());
        }
    }

    @Test
    void updateOrderOfServiceInstanceIdWithException() {
        UserInfoModel userInfoModel = mock(UserInfoModel.class);
        ServiceMetadataModel serviceMetadataModel = new ServiceMetadataModel();
        serviceMetadataModel.setRetentionDays(1);
        CreateServiceInstanceResponse response = mock(CreateServiceInstanceResponse.class);
        OrderDO orderDO = mock(OrderDO.class);
        when(orderDO.getProductComponents()).thenReturn("{\n" +
                "  \"RegionId\":\"cn-hangzhou\",\n" +
                "  \"SpecificationName\":\"低配版(Entry Level Package)\",\n" +
                "  \"PayPeriod\":1,\n \"PayPeriodUnit\":\"Month\",\n" +
                "  \"ServiceInstanceId\":\"si-123\"\n" +
                "}");
        when(orderDO.getServiceInstanceId()).thenReturn("si-id");
        when(orderDO.getReceiptAmount()).thenReturn(100L);
        when(serviceInstanceLifecycleService.createServiceInstance(any(), any(), anyBoolean(), anyString())).thenReturn(response);
        when(serviceManager.getServiceMetadata(any(UserInfoModel.class), any(GetServiceMetadataParam.class)))
                .thenReturn(BaseResult.success(serviceMetadataModel));

        orderServiceImpl.updateOrder(userInfoModel, orderDO);

        ArgumentCaptor<OrderDO> orderCaptor = ArgumentCaptor.forClass(OrderDO.class);
        verify(orderOtsHelper).updateOrder(orderCaptor.capture());
        OrderDO capturedOrderDO = orderCaptor.getValue();
        if (capturedOrderDO.getTradeStatus() == TradeStatus.REFUNDED) {
            verify(alipayService).refundOrder(eq("order-id"), eq(BigDecimal.valueOf(100)), anyString());
        }
    }

    @Test
    void testUpdateOrderWithMultiOrders() {
        UserInfoModel userInfoModel = mock(UserInfoModel.class);
        ServiceMetadataModel serviceMetadataModel = new ServiceMetadataModel();
        serviceMetadataModel.setRetentionDays(1);
        CreateServiceInstanceResponse response = mock(CreateServiceInstanceResponse.class);
        List<OrderDTO> orderList = createMockOrderList();
        ListResult<OrderDTO> orderDtoListResult = ListResult.genSuccessListResult(orderList, 1);
        OrderDO orderDO = mock(OrderDO.class);
        when(orderDO.getProductComponents()).thenReturn("{\n" +
                "  \"RegionId\":\"cn-hangzhou\",\n" +
                "  \"SpecificationName\":\"低配版(Entry Level Package)\",\n" +
                "  \"PayPeriod\":1,\n \"PayPeriodUnit\":\"Month\",\n" +
                "  \"ServiceInstanceId\":\"si-123\"\n" +
                "}");
        when(orderDO.getServiceInstanceId()).thenReturn("si-id");
        when(orderOtsHelper.listOrders(anyList(), any(),anyList(), any(), anyList())).thenReturn(orderDtoListResult);
        when(orderDO.getReceiptAmount()).thenReturn(100L);
        when(serviceInstanceLifecycleService.createServiceInstance(any(), any(), anyBoolean(), anyString())).thenReturn(response);
        when(serviceManager.getServiceMetadata(any(UserInfoModel.class), any(GetServiceMetadataParam.class)))
                .thenReturn(BaseResult.success(serviceMetadataModel));

        orderServiceImpl.updateOrder(userInfoModel, orderDO);

        ArgumentCaptor<OrderDO> orderCaptor = ArgumentCaptor.forClass(OrderDO.class);
        verify(orderOtsHelper).updateOrder(orderCaptor.capture());
        OrderDO capturedOrderDO = orderCaptor.getValue();
        if (capturedOrderDO.getTradeStatus() == TradeStatus.REFUNDED) {
            verify(alipayService).refundOrder(eq("order-id"), eq(BigDecimal.valueOf(100)), anyString());
        }
    }

    @Test
    void testRefundOrderWithExpiredServiceInstanceId() {
        OrderDTO orderDTO = createMockOrderDTO();
        List<OrderDTO> orderList = createMockOrderList();
        ListResult<OrderDTO> orderDtoListResult = ListResult.genSuccessListResult(orderList, 1);
        ServiceInstanceModel serviceInstanceModel = new ServiceInstanceModel();
        serviceInstanceModel.setServiceType(ServiceType.managed);
        GetServiceInstanceParam getServiceInstanceParam = new GetServiceInstanceParam();
        getServiceInstanceParam.setServiceInstanceId("si-123");

        when(serviceInstanceLifecycleService.getServiceInstance(any(), any(GetServiceInstanceParam.class))).thenReturn(BaseResult.success(serviceInstanceModel));
        when(orderOtsHelper.getOrder(anyString(), anyLong())).thenReturn(orderDTO);
        when(walletHelper.getRefundAmount(any(), anyString(), anyString(), anyLong(), any())).thenReturn(Long.valueOf(0));
        when(orderOtsHelper.updateOrder(any())).thenReturn(Boolean.TRUE);
        when(orderOtsHelper.listServiceInstanceOrders(anyString(), anyLong(), any(), any())).thenReturn(orderList);
        when(serviceInstanceLifeStyleHelper.checkServiceInstanceExpiration(anyList(), anyLong())).thenReturn(true);
        RefundOrderParam refundOrderParam = createMockRefundOrderParam("123", true);
        BaseResult<Long> result = orderServiceImpl.refundOrders(createMockUserInfoModel(), refundOrderParam);
        Assertions.assertTrue(result.getCode().equals("200"));
        refundOrderParam.setDryRun(false);
        result = orderServiceImpl.refundOrders(createMockUserInfoModel(), refundOrderParam);
        Assertions.assertTrue(result.getCode().equals("200"));
    }

    @Test
    void testRefundOrderWithServiceInstanceId() {
        OrderDTO orderDTO = createMockOrderDTO();
        List<OrderDTO> orderList = createMockOrderList();
        ListResult<OrderDTO> orderDtoListResult = ListResult.genSuccessListResult(orderList, 1);
        ServiceInstanceModel serviceInstanceModel = new ServiceInstanceModel();
        serviceInstanceModel.setServiceType(ServiceType.managed);
        GetServiceInstanceParam getServiceInstanceParam = new GetServiceInstanceParam();
        getServiceInstanceParam.setServiceInstanceId("si-123");
        when(serviceInstanceLifecycleService.getServiceInstance(any(), any(GetServiceInstanceParam.class))).thenReturn(BaseResult.success(serviceInstanceModel));

        when(orderOtsHelper.getOrder(anyString(), anyLong())).thenReturn(orderDTO);
        when(walletHelper.getRefundAmount(any(), anyString(), anyString(), anyLong(), any())).thenReturn(Long.valueOf(0));
        when(orderOtsHelper.updateOrder(any())).thenReturn(Boolean.TRUE);
        when(orderOtsHelper.listServiceInstanceOrders(anyString(), anyLong(), any(), any())).thenReturn(orderList);
        when(serviceInstanceLifeStyleHelper.checkServiceInstanceExpiration(anyList(), anyLong())).thenReturn(false);
        when(orderOtsHelper.isOrderInConsuming(any(), anyLong())).thenReturn(true);
        RefundOrderParam refundOrderParam = createMockRefundOrderParam("123", true);
        BaseResult<Long> result = orderServiceImpl.refundOrders(createMockUserInfoModel(), refundOrderParam);
        Assertions.assertTrue(result.getCode().equals("200"));
        refundOrderParam.setDryRun(false);
        result = orderServiceImpl.refundOrders(createMockUserInfoModel(), refundOrderParam);
        Assertions.assertTrue(result.getCode().equals("200"));
    }

    private OrderDTO createMockOrderDTO() {
        OrderDTO orderDTO = new OrderDTO();
        orderDTO.setOrderId("123");
        orderDTO.setTradeStatus(TradeStatus.REFUNDING);
        orderDTO.setTotalAmount(12399L);
        return orderDTO;
    }

    private List<OrderDTO> createMockOrderList() {
        List<OrderDTO> orderList = new ArrayList<>();
        OrderDTO firstOrderDto = new OrderDTO();
        firstOrderDto.setBillingStartDateMillis(0L);
        firstOrderDto.setBillingEndDateMillis(123L);
        OrderDTO latestOrderDto = new OrderDTO();
        latestOrderDto.setBillingStartDateMillis(123L);
        latestOrderDto.setBillingEndDateMillis(1234L);
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
        return new UserInfoModel("sub", "name", "loginName", "123", "123", Boolean.TRUE);
    }
}

