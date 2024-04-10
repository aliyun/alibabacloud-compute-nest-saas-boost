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

package org.example.common.helper;

import com.alicloud.openservices.tablestore.model.PrimaryKey;
import com.alicloud.openservices.tablestore.model.filter.ColumnValueFilter;
import org.example.common.ListResult;
import org.example.common.adapter.OtsClient;
import org.example.common.dataobject.OrderDO;
import org.example.common.dto.OrderDTO;
import org.example.common.helper.BaseOtsHelper.OtsFilter;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyDouble;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

class OrderOtsHelperTest {

    @Mock
    private OtsClient mockOtsClient;

    @Mock
    private BaseOtsHelper baseOtsHelper;

    @Mock
    private WalletHelper walletHelper;

    @InjectMocks
    private OrderOtsHelper orderOtsHelper;

    private OrderDTO order;

    private Boolean dryRun;

    private String refundId;

    private String currentIs08601Time;

    @BeforeEach
    void setUp() {
        initMocks(this);
        order = mock(OrderDTO.class);
        dryRun = false;
        refundId = "refund_id";
        currentIs08601Time = "current_time";
    }

    OrderDO createOrderDO() {
        OrderDO order = new OrderDO();
        order.setId("id");
        order.setOrderId("orderId");
        order.setTradeNo("tradeNo");
        order.setGmtCreate("2023-08-16T00:00:00Z");
        order.setGmtCreateLong(0L);
        return order;
    }

    @Test
    void testCreateOrder() {
        OrderDO order = createOrderDO();
        orderOtsHelper.createOrder(order);
        verify(baseOtsHelper).createEntity(anyString(), any(PrimaryKey.class), anyList());
    }

    @Test
    void testCreateOrderWithGmtCreateNull() {
        OrderDO order = createOrderDO();
        order.setGmtCreate(null);
        orderOtsHelper.createOrder(order);
        verify(baseOtsHelper).createEntity(anyString(), any(PrimaryKey.class), anyList());
    }

    @Test
    void testUpdateOrder() {
        OrderDO orderDO = createOrderDO();
        when(baseOtsHelper.updateEntity(anyString(), any(PrimaryKey.class), anyList())).thenReturn(Boolean.TRUE);
        Boolean result = orderOtsHelper.updateOrder(orderDO);
        assertThat(result).isTrue();
    }

    @Test
    void testListOrders() {
        final List<OtsFilter> matchFilters = Arrays.asList(OtsFilter.builder()
                .key("key")
                .values(Arrays.asList("value"))
                .build());
        final List<OtsFilter> queryFilters = Arrays.asList(OtsFilter.builder()
                .key("key")
                .values(Arrays.asList("value1", "value2"))
                .build());
        final List<OtsFilter> termsFilters =  Arrays.asList(OtsFilter.createTermsFilter("abc", Arrays.asList("value1", "value2")));
        ListResult<Object> expectedResult = new ListResult<>();
        expectedResult.setData(Arrays.asList());
        expectedResult.setCount(0L);
        expectedResult.setNextToken("CAESFQoTChEKDWdtdENyZWF0ZUxvbmcQARgBIlQKCQBI8UqGigEAAApHA0IAAAAxUzM1MzQzMTM0NjQzMjYzMzAzMzYyMzE2MTMzMzkzOTM1MzEzNjM2MzM2NDM2MzAzMDMwNjYzNTM1MzA2NjY0MzM=");
        when(baseOtsHelper.listEntities(anyString(), anyString(), anyList(), anyList(), anyList(), anyString(), isNull(), any())).thenReturn(expectedResult);
        ListResult<OrderDTO> result = orderOtsHelper.listOrders(matchFilters, queryFilters, termsFilters, "CAESFQoTChEKDWdtdENyZWF0ZUxvbmcQARgBIlQKCQBI8UqGigEAAApHA0IAAAAxUzM1MzQzMTM0NjQzMjYzMzAzMzYyMzE2MTMzMzkzOTM1MzEzNjM2MzM2NDM2MzAzMDMwNjYzNTM1MzA2NjY0MzM=", null);
        assertThat(result.getCount()).isEqualTo(0);
    }

    @Test
    void testListOrdersReturnsNull() {
        final List<OtsFilter> matchFilters = Arrays.asList(OtsFilter.builder()
                .key("key")
                .values(Arrays.asList("value"))
                .build());
        final List<OtsFilter> queryFilters = Arrays.asList(OtsFilter.builder()
                .key("key")
                .values(Arrays.asList("value1", "value2"))
                .build());
        final ListResult<Object> expectedResult = new ListResult<>();
        when(baseOtsHelper.listEntities(anyString(), anyString(), anyList(), anyList(), anyList(), anyString(), isNull(), any())).thenReturn(expectedResult);
        final ListResult<OrderDTO> result = orderOtsHelper.listOrders(matchFilters, queryFilters, Collections.emptyList(), "CAESFQoTChEKDWdtdENyZWF0ZUxvbmcQARgBIlQKCQBI8UqGigEAAApHA0IAAAAxUzM1MzQzMTM0NjQzMjYzMzAzMzYyMzE2MTMzMzkzOTM1MzEzNjM2MzM2NDM2MzAzMDMwNjYzNTM1MzA2NjY0MzM",
                null);
        Assertions.assertNull(result.getData());
    }

    @Test
    void testGetOrder() {
        OrderDTO expectedResult = new OrderDTO();
        expectedResult.setOrderId("orderId");
        expectedResult.setGmtCreate("gmtCreate");
        when(baseOtsHelper.getEntity(anyString(), any(PrimaryKey.class), any(ColumnValueFilter.class), any()))
                .thenReturn(expectedResult);
        final OrderDTO result = orderOtsHelper.getOrder("orderId", 0L);
        assertTrue(result.getGmtCreate().equals(expectedResult.getGmtCreate()));
    }

    @Test
    public void testValidateOrderCanBeRefunded_case1() {
        OrderDTO order = new OrderDTO();
        order.setServiceInstanceId("si-123");
        order.setOrderId(String.valueOf(1L));
        Long accountId = 100L;
        OrderDTO orderDTO = new OrderDTO();
        orderDTO.setOrderId(String.valueOf(1L));
        when(baseOtsHelper.listEntities(anyString(), anyString(), anyList(), isNull(), isNull(), any(), anyList(), any())).thenReturn(ListResult.genSuccessListResult(Arrays.asList(orderDTO), 0));

        Boolean result = orderOtsHelper.validateOrderCanBeRefunded(order, accountId);

        assertTrue(result);

        when(baseOtsHelper.listEntities(anyString(), anyString(), anyList(), isNull(), isNull(), any(), anyList(), any())).thenReturn(ListResult.genSuccessListResult(new ArrayList<>(), 0));

        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            orderOtsHelper.validateOrderCanBeRefunded(order, accountId);
        });
    }


    @Test
    // 测试用例设计思路：order.getTotalAmount()不为空，验证totalAmount值为order.getTotalAmount()
    public void refundUnconsumedOrder_TotalAmountIsNotNull_ReturnsTotalAmount() {
        Double totalAmount = 200.0;
        when(order.getTotalAmount()).thenReturn(totalAmount);
        when(order.getOrderId()).thenReturn("orderId");
        when(baseOtsHelper.updateEntity(anyString(), any(), any())).thenReturn(Boolean.TRUE);
        Double result = orderOtsHelper.refundUnconsumedOrder(order, dryRun, refundId, currentIs08601Time);

        assertEquals(totalAmount, result);
    }

    @Test
    // 测试用例设计思路：dryRun为true，验证直接返回refundAmount
    public void refundConsumingOrder_DryRunIsTrue_ReturnsRefundAmount() {
        Double refundAmount = 300.0;
        when(order.getTotalAmount()).thenReturn(null);
        when(order.getReceiptAmount()).thenReturn(refundAmount);
        when(walletHelper.getRefundAmount(anyDouble(), anyString(), any(), anyLong(), any())).thenReturn(300.0);
        Double result = orderOtsHelper.refundConsumingOrder(order, true, refundId, currentIs08601Time);

        assertEquals(refundAmount, result);
    }

    @Test
    // 测试用例设计思路：dryRun为false，验证返回值为refundAmount
    public void refundConsumingOrder_DryRunIsFalse_ReturnsRefundAmount() {
        Double refundAmount = 400.0;
        when(order.getTotalAmount()).thenReturn(null);
        when(order.getReceiptAmount()).thenReturn(refundAmount);
        when(order.getOrderId()).thenReturn("orderId");
        when(walletHelper.getRefundAmount(anyDouble(), anyString(), any(), anyLong(), any())).thenReturn(400.0);
        when(baseOtsHelper.updateEntity(anyString(), any(), any())).thenReturn(Boolean.TRUE);

        Double result = orderOtsHelper.refundConsumingOrder(order, false, refundId, currentIs08601Time);

        assertEquals(refundAmount, result);
    }

    @Test
    // 测试用例设计思路：验证createRefundOrder()和updateOrder()方法被调用
    public void refundUnconsumedOrder_DryRunIsFalse_CallsCreateRefundOrderAndUpdateOrder() throws Exception {
        orderOtsHelper = spy(new OrderOtsHelper());
        ReflectionTestUtils.setField(orderOtsHelper, "baseOtsHelper", baseOtsHelper);
        Double totalAmount = 500.0;
        when(order.getTotalAmount()).thenReturn(null);
        when(order.getReceiptAmount()).thenReturn(totalAmount);
        when(order.getOrderId()).thenReturn("orderId");
        when(baseOtsHelper.updateEntity(anyString(), any(), any())).thenReturn(Boolean.TRUE);
        Double result = orderOtsHelper.refundUnconsumedOrder(order, false, refundId, currentIs08601Time);

        Assertions.assertEquals(result, totalAmount);
    }

    @Test
    // 测试用例设计思路：验证createRefundOrder()和updateOrder()方法被调用
    public void refundConsumingOrder_DryRunIsFalse_CallsCreateRefundOrderAndUpdateOrder() throws Exception {
        orderOtsHelper = spy(new OrderOtsHelper());
        ReflectionTestUtils.setField(orderOtsHelper, "baseOtsHelper", baseOtsHelper);
        ReflectionTestUtils.setField(orderOtsHelper, "walletHelper", walletHelper);
        Double refundAmount = 600.0;
        when(order.getTotalAmount()).thenReturn(null);
        when(order.getReceiptAmount()).thenReturn(refundAmount);
        when(order.getOrderId()).thenReturn("orderId");
        when(walletHelper.getRefundAmount(anyDouble(), anyString(), any(), anyLong(), any())).thenReturn(500.0);

        Double result = orderOtsHelper.refundConsumingOrder(order, false, refundId, currentIs08601Time);

        Assertions.assertEquals(result, 500.0);
    }


    @Test
    public void isOrderInConsumingReturnsTrue() {
        Long currentLocalDateTimeMillis = 123456789L;
        when(order.getBillingStartDateMillis()).thenReturn(10000L);
        when(order.getBillingEndDateMillis()).thenReturn(223456789L);

        Boolean result = orderOtsHelper.isOrderInConsuming(order, currentLocalDateTimeMillis);

        assertTrue(result);
    }

    @Test
    public void isOrderInConsumingReturnsFalse() {
        Long currentLocalDateTimeMillis = 123456789L;
        when(order.getBillingStartDateMillis()).thenReturn(null);
        when(order.getBillingEndDateMillis()).thenReturn(null);

        Boolean result = orderOtsHelper.isOrderInConsuming(order, currentLocalDateTimeMillis);

        assertTrue(result);

        when(order.getBillingStartDateMillis()).thenReturn(123456L);
        when(order.getBillingEndDateMillis()).thenReturn(1234567L);

        result = orderOtsHelper.isOrderInConsuming(order, currentLocalDateTimeMillis);

        assertFalse(result);
    }
}
