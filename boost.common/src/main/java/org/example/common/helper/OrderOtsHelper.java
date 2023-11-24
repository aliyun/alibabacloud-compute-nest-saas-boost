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
import com.alicloud.openservices.tablestore.model.search.sort.Sort;
import com.alicloud.openservices.tablestore.model.search.sort.SortOrder;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.example.common.ListResult;
import org.example.common.constant.OrderOtsConstant;
import org.example.common.constant.TradeStatus;
import org.example.common.dataobject.OrderDO;
import org.example.common.dto.OrderDTO;
import org.example.common.helper.BaseOtsHelper.OtsFilter;
import org.example.common.utils.DateUtil;
import org.example.common.utils.Md5Util;
import org.example.common.utils.OtsUtil;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Component
@Slf4j
public class OrderOtsHelper {

    @Resource
    private BaseOtsHelper baseOtsHelper;

    @Resource
    private WalletHelper walletHelper;

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

    public ListResult<OrderDTO> listOrders(List<OtsFilter> matchFilters, List<OtsFilter> queryFilters, List<OtsFilter> multiMatchFilters, String nextToken, List<Sort.Sorter> sorters) {
        return baseOtsHelper.listEntities(OrderOtsConstant.TABLE_NAME, OrderOtsConstant.SEARCH_INDEX_NAME, matchFilters, queryFilters, multiMatchFilters, nextToken, sorters, OrderDTO.class);
    }

    public OrderDTO getOrder(String orderId, Long accountId) {
        PrimaryKey primaryKey = createPrimaryKey(orderId);
        SingleColumnValueFilter singleColumnValueFilter = null;
        if (accountId != null) {
            singleColumnValueFilter =
                    new SingleColumnValueFilter(OrderOtsConstant.ACCOUNT_ID, SingleColumnValueFilter.CompareOperator.EQUAL, ColumnValue.fromLong(accountId));
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

    public Boolean validateOrderCanBeRefunded(OrderDTO order, Long accountId) {
        if (order != null && !StringUtils.isEmpty(order.getOrderId()) && accountId != null) {
            List<OrderDTO> orderDtoList = listServiceInstanceOrders(order.getServiceInstanceId(), accountId, Boolean.TRUE, TradeStatus.TRADE_SUCCESS);
            if (StringUtils.isNotEmpty(order.getOrderId()) && orderDtoList != null && orderDtoList.size() > 0) {
                return order.getOrderId().equals(orderDtoList.get(0).getOrderId());
            }
        }

        throw new IllegalArgumentException("Only the latest order is eligible for a refund.");
    }

    public List<OrderDTO> listServiceInstanceOrders(String serviceInstanceId, Long accountId, Boolean reverse, TradeStatus tradeStatus) {
        OtsFilter serviceInstanceIdMatchFilter = OtsFilter.createMatchFilter(OrderOtsConstant.SERVICE_INSTANCE_ID, serviceInstanceId);
        OtsFilter tradeStatusMatchFilter = OtsFilter.createMatchFilter(OrderOtsConstant.TRADE_STATUS, tradeStatus.name());
        OtsFilter accountMatchFilter = OtsFilter.createMatchFilter(OrderOtsConstant.ACCOUNT_ID, accountId);
        FieldSort fieldSort = new FieldSort(OrderOtsConstant.BILLING_END_DATE_LONG, reverse ? SortOrder.DESC : SortOrder.ASC);

        // Initialize the list to collect all orders
        List<OrderDTO> allOrders = new ArrayList<>();
        String nextToken = null;

        do {
            // Fetch the list of orders using the nextToken
            ListResult<OrderDTO> orderDtoListResult = listOrders(
                    Arrays.asList(serviceInstanceIdMatchFilter, accountMatchFilter, tradeStatusMatchFilter),
                    null, null,
                    nextToken,
                    Collections.singletonList(fieldSort)
            );

            if (orderDtoListResult != null && orderDtoListResult.getData() != null) {
                allOrders.addAll(orderDtoListResult.getData());
                nextToken = orderDtoListResult.getNextToken();
            } else {
                break;
            }
        } while (nextToken != null && nextToken.length()>0);
        return allOrders;
    }


    public Double refundUnconsumedOrder(OrderDTO order, Boolean dryRun, String refundId, String currentIs08601Time) {
        Double totalAmount = order.getTotalAmount() == null ? order.getReceiptAmount() : order.getTotalAmount();
        if (!dryRun) {
            OrderDO refundOrder = createRefundOrder(order, refundId, totalAmount, currentIs08601Time);
            updateOrder(refundOrder);
        }
        return totalAmount;
    }

    public Double refundConsumingOrder(OrderDTO order, Boolean dryRun, String refundId, String currentIs08601Time) {
        // Process logic for orders that are currently being consumed or consumed.
        Double totalAmount = order.getTotalAmount() == null ? order.getReceiptAmount() : order.getTotalAmount();
        Double refundAmount = walletHelper.getRefundAmount(totalAmount, currentIs08601Time, order.getGmtPayment(), order.getPayPeriod(), order.getPayPeriodUnit());
        if (dryRun) {
            return refundAmount;
        }
        OrderDO refundOrder = createRefundOrder(order, refundId, refundAmount, currentIs08601Time);
        updateOrder(refundOrder);
        return refundAmount;
    }

    public Boolean isOrderInConsuming(OrderDTO orderDTO, Long currentLocalDateTimeMillis) {
        if (orderDTO == null || orderDTO.getBillingStartDateLong() == null || orderDTO.getBillingEndDateLong() == null) {
            return Boolean.TRUE;
        }
        return currentLocalDateTimeMillis >= orderDTO.getBillingStartDateLong() && currentLocalDateTimeMillis < orderDTO.getBillingEndDateLong();
    }

    private OrderDO createRefundOrder(OrderDTO order, String refundId, Double refundAmount, String refundDate) {
        OrderDO refundOrder = new OrderDO();
        BeanUtils.copyProperties(order, refundOrder);
        refundOrder.setRefundId(refundId);
        refundOrder.setRefundAmount(refundAmount);
        refundOrder.setRefundDate(refundDate);
        refundOrder.setTradeStatus(TradeStatus.REFUNDING);
        return refundOrder;
    }
}
