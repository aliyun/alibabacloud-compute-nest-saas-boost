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

import com.alicloud.openservices.tablestore.model.Column;
import com.alicloud.openservices.tablestore.model.ColumnType;
import com.alicloud.openservices.tablestore.model.ColumnValue;
import com.alicloud.openservices.tablestore.model.PrimaryKey;
import com.alicloud.openservices.tablestore.model.PrimaryKeyColumn;
import com.alicloud.openservices.tablestore.model.PrimaryKeyValue;
import com.alicloud.openservices.tablestore.model.Row;
import org.example.common.dataobject.OrderDO;
import org.example.common.dto.CommoditySpecificationDTO;
import org.example.common.dto.OrderDTO;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class OtsUtilTest {

    @Test
    void testConvertToColumnList() {
        final List<Column> expectedResult = Arrays.asList(
                new Column("fieldName", new ColumnValue("value", ColumnType.STRING)));
        OrderDTO orderDTO = new OrderDTO();
        orderDTO.setOrderId("123");
        final List<Column> result = OtsUtil.convertToColumnList(orderDTO);
        assertThat(result.size()).isEqualTo(1);
        Assertions.assertTrue(result.get(0).getValue().asString().equals("123"));
    }

    @Test
    void testCreateColumnValue() {
        final ColumnValue expectedResult = new ColumnValue("value", ColumnType.STRING);
        final ColumnValue result = OtsUtil.createColumnValue("value");
        assertThat(result).isEqualTo(expectedResult);
    }

    @Test
    void testCreateListColumnValue() {
        final Row row = new Row(new PrimaryKey(
                new PrimaryKeyColumn[]{new PrimaryKeyColumn("pid", PrimaryKeyValue.fromString("123")), new PrimaryKeyColumn("cid", PrimaryKeyValue.fromString("123"))}),
                Arrays.asList(new Column("accountId", new ColumnValue(123L, ColumnType.INTEGER)), new Column("payPeriods", new ColumnValue("[1,2,3]", ColumnType.STRING))));
        CommoditySpecificationDTO commoditySpecificationDTO = OtsUtil.convertRowToDTO(row, CommoditySpecificationDTO.class);
        assertThat(commoditySpecificationDTO.getPayPeriods().equals(Arrays.asList(1, 2, 3)));
    }

    @Test
    void testConvertRowToDTO() {
        final Row row = new Row(new PrimaryKey(
                new PrimaryKeyColumn[]{new PrimaryKeyColumn("userId", PrimaryKeyValue.fromLong(123L))}),
                Arrays.asList(new Column("accountId", new ColumnValue(123L, ColumnType.INTEGER))));
        OrderDO order = OtsUtil.convertRowToDTO(row, OrderDO.class);
        assertThat(order.getUserId()).isEqualTo(123L);
    }
}
