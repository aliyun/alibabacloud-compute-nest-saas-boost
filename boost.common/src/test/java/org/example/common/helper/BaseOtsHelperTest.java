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

import com.alicloud.openservices.tablestore.model.CapacityUnit;
import com.alicloud.openservices.tablestore.model.Column;
import com.alicloud.openservices.tablestore.model.ColumnType;
import com.alicloud.openservices.tablestore.model.ColumnValue;
import com.alicloud.openservices.tablestore.model.ConsumedCapacity;
import com.alicloud.openservices.tablestore.model.GetRowResponse;
import com.alicloud.openservices.tablestore.model.PrimaryKey;
import com.alicloud.openservices.tablestore.model.PrimaryKeyBuilder;
import com.alicloud.openservices.tablestore.model.PrimaryKeyColumn;
import com.alicloud.openservices.tablestore.model.PrimaryKeyValue;
import com.alicloud.openservices.tablestore.model.Response;
import com.alicloud.openservices.tablestore.model.Row;
import com.alicloud.openservices.tablestore.model.RowPutChange;
import com.alicloud.openservices.tablestore.model.RowUpdateChange;
import com.alicloud.openservices.tablestore.model.SingleRowQueryCriteria;
import com.alicloud.openservices.tablestore.model.UpdateRowResponse;
import com.alicloud.openservices.tablestore.model.filter.SingleColumnValueFilter;
import com.alicloud.openservices.tablestore.model.search.SearchRequest;
import com.alicloud.openservices.tablestore.model.search.SearchResponse;
import com.alicloud.openservices.tablestore.model.search.sort.FieldSort;
import com.alicloud.openservices.tablestore.model.search.sort.SortOrder;
import org.example.common.ListResult;
import org.example.common.adapter.OtsClient;
import org.example.common.constant.OrderOtsConstant;
import org.example.common.dataobject.OrderDO;
import org.example.common.dto.OrderDTO;
import org.example.common.helper.BaseOtsHelper.OtsFilter;
import org.example.common.helper.BaseOtsHelper.OtsFilter.OtsFilterBuilder;
import org.example.common.utils.OtsUtil;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.slf4j.Logger;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

class BaseOtsHelperTest {

    @Mock
    private OtsClient mockOtsClient;

    @Mock
    Logger log;

    @InjectMocks
    BaseOtsHelper baseOtsHelper;

    @BeforeEach
    void setUp() {
        initMocks(this);
    }

    @Test
    void testGetEntity() {
        OrderDTO expectedResult = new OrderDTO();
        expectedResult.setOrderId("orderId");
        expectedResult.setGmtCreate("gmtCreate");
        GetRowResponse getRowResponse = new GetRowResponse(new Response("requestId"), new Row(new PrimaryKey(
                new PrimaryKeyColumn[]{new PrimaryKeyColumn("id", PrimaryKeyValue.fromString("orderId"))}),
                Arrays.asList(new Column("gmtCreate", new ColumnValue("gmtCreate", ColumnType.STRING)))),
                new ConsumedCapacity(new CapacityUnit(0, 0)));
        when(mockOtsClient.getRow(any(SingleRowQueryCriteria.class))).thenReturn(getRowResponse);
        PrimaryKey primaryKey = PrimaryKeyBuilder.createPrimaryKeyBuilder()
                .addPrimaryKeyColumn(OrderOtsConstant.PRIMARY_KEY_NAME, PrimaryKeyValue.fromString("orderId")).build();
        SingleColumnValueFilter singleColumnValueFilter =
                new SingleColumnValueFilter(OrderOtsConstant.ACCOUNT_ID, SingleColumnValueFilter.CompareOperator.EQUAL, ColumnValue.fromLong(1L));
        final OrderDTO result = baseOtsHelper.getEntity("order", primaryKey, singleColumnValueFilter, OrderDTO.class);
        Assertions.assertTrue(result.getGmtCreate().equals(expectedResult.getGmtCreate()));
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
    void testCreateEntity() {
        final OrderDO order = createOrderDO();
        List<Column> columns = OtsUtil.convertToColumnList(order);
        PrimaryKey primaryKey = PrimaryKeyBuilder.createPrimaryKeyBuilder()
                .addPrimaryKeyColumn(OrderOtsConstant.PRIMARY_KEY_NAME, PrimaryKeyValue.fromString("orderId")).build();
        baseOtsHelper.createEntity("order", primaryKey, columns);
        verify(mockOtsClient).putRow(any(RowPutChange.class));
    }

    @Test
    void testUpdateEntity() {
        final OrderDO orderDO = createOrderDO();
        List<Column> columns = OtsUtil.convertToColumnList(orderDO);
        when(mockOtsClient.updateRow(any(RowUpdateChange.class))).thenReturn(new UpdateRowResponse(new Response("requestId"), new Row(new PrimaryKey(
                new PrimaryKeyColumn[]{new PrimaryKeyColumn("id", PrimaryKeyValue.fromString("orderId"))}),
                Arrays.asList(new Column("gmtCreate", new ColumnValue("2023-08-16T00:00:00Z", ColumnType.STRING)))), new ConsumedCapacity(new CapacityUnit(0, 0))));
        PrimaryKey primaryKey = PrimaryKeyBuilder.createPrimaryKeyBuilder()
                .addPrimaryKeyColumn(OrderOtsConstant.PRIMARY_KEY_NAME, PrimaryKeyValue.fromString("orderId")).build();
        final Boolean result = baseOtsHelper.updateEntity("order", primaryKey, columns);
        assertThat(result).isTrue();
    }

    List<OtsFilter> createMatchFilters() {
        return Arrays.asList(OtsFilter.builder()
                .key("key")
                .values(Arrays.asList("value"))
                .build());
    }

    List<OtsFilter> createQueryFilters() {
        return Arrays.asList(OtsFilter.builder()
                .key("key")
                .values(Arrays.asList("value1", "value2"))
                .build());
    }

    @Test
    void testListEntities() {
        final List<OtsFilter> matchFilters = createMatchFilters();
        final List<OtsFilter> queryFilters = createQueryFilters();
        final ListResult<OrderDTO> expectedResult = new ListResult<>();
        FieldSort fieldSort = new FieldSort(OrderOtsConstant.GMT_CREATE_LONG);
        fieldSort.setOrder(SortOrder.DESC);
        expectedResult.setData(Arrays.asList());
        expectedResult.setCount(0L);
        String nextToken = "CAESFQoTChEKDWdtdENyZWF0ZUxvbmcQARgBIlQKCQBI8UqGigEAAApHA0IAAAAxUzM1MzQzMTM0NjQzMjYzMzAzMzYyMzE2MTMzMzkzOTM1MzEzNjM2MzM2NDM2MzAzMDMwNjYzNTM1MzA2NjY0MzM=";
        expectedResult.setNextToken(nextToken);
        final SearchResponse searchResponse = new SearchResponse(new Response("requestId"));
        when(mockOtsClient.search(any(SearchRequest.class))).thenReturn(searchResponse);
        ListResult<OrderDTO> result = baseOtsHelper.listEntities("order", "order_index", matchFilters, queryFilters, nextToken, Arrays.asList(fieldSort), OrderDTO.class);
        assertThat(result.getCount()).isEqualTo(0);
    }

    @Mock
    private OtsFilterBuilder builder;

    @Test
    public void testBuilder() {
        String key = "testKey";
        List<Object> values = new ArrayList<>();
        values.add("value1");
        values.add("value2");
        when(builder.key(key)).thenReturn(builder);
        when(builder.values(values)).thenReturn(builder);
        when(builder.build()).thenReturn(new OtsFilter(key, values));
        OtsFilter filter = OtsFilter.builder()
                .key(key)
                .values(values)
                .build();
        assertEquals(key, filter.getKey());
        assertEquals(values, filter.getValues());
    }
}

