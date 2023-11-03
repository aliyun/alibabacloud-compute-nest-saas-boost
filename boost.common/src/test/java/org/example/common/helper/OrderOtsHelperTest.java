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

import com.alicloud.openservices.tablestore.model.Column;
import com.alicloud.openservices.tablestore.model.PrimaryKey;
import com.alicloud.openservices.tablestore.model.PrimaryKeyBuilder;
import com.alicloud.openservices.tablestore.model.PrimaryKeyValue;
import com.alicloud.openservices.tablestore.model.filter.ColumnValueFilter;
import com.alicloud.openservices.tablestore.model.search.sort.FieldSort;
import org.example.common.ListResult;
import org.example.common.adapter.OtsClient;
import org.example.common.constant.OrderOtsConstant;
import org.example.common.dataobject.OrderDO;
import org.example.common.dto.OrderDTO;
import org.example.common.helper.BaseOtsHelper.OtsFilter;
import org.example.common.utils.OtsUtil;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

class OrderOtsHelperTest {

    @Mock
    private OtsClient mockOtsClient;

    @Mock
    private BaseOtsHelper baseOtsHelper;

    @InjectMocks
    private OrderOtsHelper orderOtsHelperUnderTest;

    @BeforeEach
    void setUp() {
        initMocks(this);
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

    PrimaryKey createPrimaryKey() {
        return PrimaryKeyBuilder.createPrimaryKeyBuilder()
               .addPrimaryKeyColumn(OrderOtsConstant.PRIMARY_KEY_NAME, PrimaryKeyValue.fromString("orderId")).build();
    }

    @Test
    void testCreateOrder() {
        OrderDO order = createOrderDO();
        orderOtsHelperUnderTest.createOrder(order);
        verify(baseOtsHelper).createEntity(anyString(), any(PrimaryKey.class), anyList());
    }

    @Test
    void testCreateOrderWithGmtCreateNull() {
        OrderDO order = createOrderDO();
        order.setGmtCreate(null);
        orderOtsHelperUnderTest.createOrder(order);
        verify(baseOtsHelper).createEntity(anyString(), any(PrimaryKey.class), anyList());
    }

    @Test
    void testUpdateOrder() {
        OrderDO orderDO = createOrderDO();
        PrimaryKey primaryKey = createPrimaryKey();
        List<Column> columns = OtsUtil.convertToColumnList(orderDO);
        when(baseOtsHelper.updateEntity(anyString(), any(PrimaryKey.class),  anyList())).thenReturn(Boolean.TRUE);
        Boolean result = orderOtsHelperUnderTest.updateOrder(orderDO);
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
        final ListResult<Object> expectedResult = new ListResult<>();
        expectedResult.setData(Arrays.asList());
        expectedResult.setCount(0L);
        expectedResult.setNextToken("CAESFQoTChEKDWdtdENyZWF0ZUxvbmcQARgBIlQKCQBI8UqGigEAAApHA0IAAAAxUzM1MzQzMTM0NjQzMjYzMzAzMzYyMzE2MTMzMzkzOTM1MzEzNjM2MzM2NDM2MzAzMDMwNjYzNTM1MzA2NjY0MzM=");
        when(baseOtsHelper.listEntities(anyString(), anyString(), anyList(), anyList(), anyString(), any(FieldSort.class), any())).thenReturn(expectedResult);
        final ListResult<OrderDTO> result = orderOtsHelperUnderTest.listOrders(matchFilters, queryFilters, "CAESFQoTChEKDWdtdENyZWF0ZUxvbmcQARgBIlQKCQBI8UqGigEAAApHA0IAAAAxUzM1MzQzMTM0NjQzMjYzMzAzMzYyMzE2MTMzMzkzOTM1MzEzNjM2MzM2NDM2MzAzMDMwNjYzNTM1MzA2NjY0MzM=",
                false);
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
        when(baseOtsHelper.listEntities(anyString(), anyString(), anyList(), anyList(), anyString(), any(FieldSort.class), any())).thenReturn(expectedResult);
        final ListResult<OrderDTO> result = orderOtsHelperUnderTest.listOrders(matchFilters, queryFilters, "CAESFQoTChEKDWdtdENyZWF0ZUxvbmcQARgBIlQKCQBI8UqGigEAAApHA0IAAAAxUzM1MzQzMTM0NjQzMjYzMzAzMzYyMzE2MTMzMzkzOTM1MzEzNjM2MzM2NDM2MzAzMDMwNjYzNTM1MzA2NjY0MzM",
                false);
        Assertions.assertNull(result.getData());
    }

    @Test
    void testGetOrder() {
        OrderDTO expectedResult = new OrderDTO();
        expectedResult.setOrderId("orderId");
        expectedResult.setGmtCreate("gmtCreate");
        when(baseOtsHelper.getEntity(anyString(), any(PrimaryKey.class), any(ColumnValueFilter.class), any()))
                .thenReturn(expectedResult);
        final OrderDTO result = orderOtsHelperUnderTest.getOrder("orderId", 0L);
        Assertions.assertTrue(result.getGmtCreate().equals(expectedResult.getGmtCreate()));
    }
}
