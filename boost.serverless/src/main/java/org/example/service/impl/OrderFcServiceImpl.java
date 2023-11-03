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

import lombok.extern.slf4j.Slf4j;
import org.example.common.ListResult;
import org.example.common.adapter.BaseAlipayClient;
import org.example.common.adapter.ComputeNestSupplierClient;
import org.example.common.constant.OrderOtsConstant;
import org.example.common.constant.TradeStatus;
import org.example.common.dataobject.OrderDO;
import org.example.common.dto.OrderDTO;
import org.example.common.helper.OrderOtsHelper;
import org.example.common.helper.BaseOtsHelper.OtsFilter;
import org.example.common.utils.DateUtil;
import org.example.service.OrderFcService;
import org.example.task.RefundOrderTask;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ScheduledExecutorService;

@Service
@Slf4j
public class OrderFcServiceImpl implements OrderFcService {

    private OrderOtsHelper orderOtsHelper;

    private BaseAlipayClient baseAlipayClient;

    private ComputeNestSupplierClient computeNestSupplierClient;

    private ScheduledExecutorService scheduledThreadPool;

    public OrderFcServiceImpl(OrderOtsHelper orderOtsHelper, BaseAlipayClient baseAlipayClient,
                              ComputeNestSupplierClient computeNestSupplierClient, ScheduledExecutorService scheduledThreadPool) {
        this.orderOtsHelper = orderOtsHelper;
        this.baseAlipayClient = baseAlipayClient;
        this.computeNestSupplierClient = computeNestSupplierClient;
        this.scheduledThreadPool = scheduledThreadPool;
    }

    @Override
    public void closeExpiredOrders() {
        OtsFilter filter = OtsFilter.builder()
                .key("tradeStatus")
                .values(Arrays.asList(TradeStatus.WAIT_BUYER_PAY.name()))
                .build();
        Long startTime = DateUtil.getMinutesAgoLocalDateTimeMillis(30);
        Long endTime = DateUtil.getMinutesAgoLocalDateTimeMillis(15);
        OtsFilter rangeFilter = OtsFilter.builder()
                .key(OrderOtsConstant.SEARCH_INDEX_FIELD_NAME_1)
                .values(Arrays.asList(startTime, endTime))
                .build();

        String nextToken = null;
        List<OrderDTO> orders = null;
        do {
            ListResult<OrderDTO> result = orderOtsHelper.listOrders(Collections.singletonList(filter), Arrays.asList(rangeFilter), nextToken, true);
            if (result != null && result.getData() != null && !result.getData().isEmpty()) {
                orders = result.getData();
                nextToken = result.getNextToken();

                for (OrderDTO order : orders) {
                    String orderId = order.getOrderId();
                    try {
                        OrderDO orderDO = new OrderDO();
                        BeanUtils.copyProperties(order, orderDO);
                        orderDO.setTradeStatus(TradeStatus.TRADE_FINISHED);
                        orderOtsHelper.updateOrder(orderDO);
                        log.info("Order close success. order id = {}", orderDO.getOrderId());
                    } catch (Exception e) {
                        log.error("Order closed failed. Order id = {}", orderId, e);
                    }
                }
            } else {
                break;
            }
        }
        while (!CollectionUtils.isEmpty(orders) || !StringUtils.isEmpty(nextToken));
    }

    @Override
    public void refundOrders() {
        OtsFilter filter = OtsFilter.builder()
                .key("tradeStatus")
                .values(Arrays.asList(TradeStatus.REFUNDING.name()))
                .build();
        Long endTime = DateUtil.getCurrentLocalDateTimeMills();
        Long startTime = DateUtil.getOneYearAgoLocalDateTimeMills();
        OtsFilter rangeFilter = OtsFilter.builder()
                .key(OrderOtsConstant.SEARCH_INDEX_FIELD_NAME_1)
                .values(Arrays.asList(startTime, endTime))
                .build();

        String nextToken = null;
        List<OrderDTO> orders = null;
        do {
            ListResult<OrderDTO> result = orderOtsHelper.listOrders(Collections.singletonList(filter), Collections.singletonList(rangeFilter), nextToken, true);
            if (result != null && result.getData() != null && !result.getData().isEmpty()) {
                orders = new ArrayList<>(result.getData());
                nextToken = result.getNextToken();
                CountDownLatch latch = new CountDownLatch(orders.size());
                for (OrderDTO order : orders) {
                    try {
                        log.info("Refunding order, order id = {}.", order.getOrderId());
                        RefundOrderTask refundOrderTask = RefundOrderTask.builder().orderId(order.getOrderId())
                                .refundAmount(order.getRefundAmount()).refundId(order.getRefundId())
                                .serviceInstanceId(order.getServiceInstanceId())
                                .computeNestSupplierClient(computeNestSupplierClient)
                                .baseAlipayClient(baseAlipayClient)
                                .orderOtsHelper(orderOtsHelper)
                                .scheduledThreadPool(scheduledThreadPool)
                                .countDownLatch(latch)
                                .build();
                        scheduledThreadPool.submit(refundOrderTask);
                    } catch (Exception e) {
                        log.error("Refund Order failed. order id = {}", order.getOrderId(), e);
                    }
                }
                orders.clear();
                try {
                    latch.await(); // 等待所有任务执行完毕
                } catch (InterruptedException e) {
                    log.error("Interrupted while waiting for refund tasks to complete.", e);
                    Thread.currentThread().interrupt();
                }
            } else {
                break;
            }
        } while (!CollectionUtils.isEmpty(orders) || !StringUtils.isEmpty(nextToken));
    }
}




