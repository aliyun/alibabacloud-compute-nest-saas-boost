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
import com.alicloud.openservices.tablestore.model.ColumnValue;
import com.alicloud.openservices.tablestore.model.PrimaryKey;
import com.alicloud.openservices.tablestore.model.PrimaryKeyBuilder;
import com.alicloud.openservices.tablestore.model.PrimaryKeyValue;
import com.alicloud.openservices.tablestore.model.filter.SingleColumnValueFilter;
import com.alicloud.openservices.tablestore.model.search.sort.FieldSort;
import com.alicloud.openservices.tablestore.model.search.sort.SortOrder;
import lombok.extern.slf4j.Slf4j;
import org.example.common.ListResult;
import org.example.common.constant.OrderOtsConstant;
import org.example.common.dataobject.OrderDO;
import org.example.common.dto.OrderDTO;
import org.example.common.helper.BaseOtsHelper.OtsFilter;
import org.example.common.utils.DateUtil;
import org.example.common.utils.Md5Util;
import org.example.common.utils.OtsUtil;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import javax.annotation.Resource;
import java.util.List;

@Component
@Slf4j
public class OrderOtsHelper {

    @Resource
    private BaseOtsHelper baseOtsHelper;

    public void createOrder(OrderDO order) {
        PrimaryKey primaryKey = createPrimaryKey(order.getOrderId());
        if (!StringUtils.isEmpty(order.getGmtCreate())) {
            //Default time zone is Shanghai, China.
            order.setGmtCreateLong(DateUtil.parseFromIsO8601DateString(order.getGmtCreate()));
        } else {
            String currentIs08601Time = DateUtil.getCurrentIs08601Time();
            order.setGmtCreate(currentIs08601Time);
            order.setGmtCreateLong(DateUtil.parseFromIsO8601DateString(currentIs08601Time));
        }
        List<Column> columns = OtsUtil.convertToColumnList(order);
        baseOtsHelper.createEntity(OrderOtsConstant.TABLE_NAME, primaryKey, columns);
    }

    public Boolean updateOrder(OrderDO orderDO) {
        PrimaryKey primaryKey = createPrimaryKey(orderDO.getOrderId());
        if (!StringUtils.isEmpty(orderDO.getGmtCreate()) && DateUtil.isValidIsO8601DateFormat(orderDO.getGmtCreate())) {
            orderDO.setGmtCreateLong(DateUtil.parseFromIsO8601DateString(orderDO.getGmtCreate()));
        }
        List<Column> columns = OtsUtil.convertToColumnList(orderDO);
        baseOtsHelper.updateEntity(OrderOtsConstant.TABLE_NAME, primaryKey, columns);
        return Boolean.TRUE;
    }

    public ListResult<OrderDTO> listOrders(List<OtsFilter> matchFilters, List<OtsFilter> queryFilters, String nextToken, Boolean reverse) {
        FieldSort fieldSort = new FieldSort(OrderOtsConstant.SEARCH_INDEX_FIELD_NAME_1);
        fieldSort.setOrder(reverse ? SortOrder.DESC : SortOrder.ASC);
        return baseOtsHelper.listEntities(OrderOtsConstant.TABLE_NAME, OrderOtsConstant.SEARCH_INDEX_NAME, matchFilters, queryFilters, nextToken, fieldSort, OrderDTO.class);
    }

    public OrderDTO getOrder(String orderId, Long accountId) {
        PrimaryKey primaryKey = createPrimaryKey(orderId);
        SingleColumnValueFilter singleColumnValueFilter = null;
        if (accountId != null) {
            singleColumnValueFilter =
                    new SingleColumnValueFilter(OrderOtsConstant.FILTER_NAME_0, SingleColumnValueFilter.CompareOperator.EQUAL, ColumnValue.fromLong(accountId));
            singleColumnValueFilter.setPassIfMissing(true);
            singleColumnValueFilter.setLatestVersionsOnly(true);
        }
        return baseOtsHelper.getEntity(OrderOtsConstant.TABLE_NAME, primaryKey, singleColumnValueFilter, OrderDTO.class);
    }

    private PrimaryKey createPrimaryKey(String outTradeNo) {
        String outTradeNoMd5PrimaryKey = Md5Util.md5(outTradeNo);
        if (StringUtils.isEmpty(outTradeNoMd5PrimaryKey)) {
            return null;
        }
        return PrimaryKeyBuilder.createPrimaryKeyBuilder()
                .addPrimaryKeyColumn(OrderOtsConstant.PRIMARY_KEY_NAME, PrimaryKeyValue.fromString(outTradeNoMd5PrimaryKey)).build();
    }
}
