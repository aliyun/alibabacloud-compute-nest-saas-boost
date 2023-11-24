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
import org.example.common.adapter.BaseAlipayClient;
import org.example.common.adapter.ComputeNestSupplierClient;
import org.example.common.constant.OrderOtsConstant;
import org.example.common.constant.TradeStatus;
import org.example.common.dataobject.OrderDO;
import org.example.common.dto.OrderDTO;
import org.example.common.helper.BaseOtsHelper.OtsFilter;
import org.example.common.helper.OrderOtsHelper;
import org.example.common.utils.DateUtil;
import org.example.process.OrderProcessor;
import org.example.service.OrderFcService;
import org.example.task.RefundOrderTask;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ScheduledExecutorService;

@Service
@Slf4j
public class OrderFcServiceImpl implements OrderFcService {

    private OrderOtsHelper orderOtsHelper;

    private BaseAlipayClient baseAlipayClient;

    private ComputeNestSupplierClient computeNestSupplierClient;

    private ScheduledExecutorService scheduledThreadPool;

    private OrderProcessor orderProcessor;

    public OrderFcServiceImpl(OrderOtsHelper orderOtsHelper, BaseAlipayClient baseAlipayClient,
                              ComputeNestSupplierClient computeNestSupplierClient, ScheduledExecutorService scheduledThreadPool, OrderProcessor orderProcessor) {
        this.orderOtsHelper = orderOtsHelper;
        this.baseAlipayClient = baseAlipayClient;
        this.computeNestSupplierClient = computeNestSupplierClient;
        this.scheduledThreadPool = scheduledThreadPool;
        this.orderProcessor = orderProcessor;
    }

    @Override
    public void closeExpiredOrders() {
        OtsFilter queryFilter = OtsFilter.createMatchFilter("tradeStatus", TradeStatus.WAIT_BUYER_PAY.name());
        Long startTime = DateUtil.getMinutesAgoLocalDateTimeMillis(30);
        Long endTime = DateUtil.getMinutesAgoLocalDateTimeMillis(15);
        OtsFilter rangeFilter = OtsFilter.createRangeFilter(OrderOtsConstant.GMT_CREATE_LONG, startTime, endTime);
        orderProcessor.doWhileLoop(Collections.singletonList(queryFilter), Collections.singletonList(rangeFilter), this::closeOrder);
    }

    private void closeOrder(OrderDTO order) {
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

    private void createRefundOrderTask(OrderDTO order, CountDownLatch countDownLatch) {
        log.info("Refunding order, order id = {}.", order.getOrderId());
        RefundOrderTask refundOrderTask = RefundOrderTask.builder().orderId(order.getOrderId())
                .refundAmount(order.getRefundAmount()).refundId(order.getRefundId())
                .serviceInstanceId(order.getServiceInstanceId())
                .computeNestSupplierClient(computeNestSupplierClient)
                .baseAlipayClient(baseAlipayClient)
                .orderOtsHelper(orderOtsHelper)
                .scheduledThreadPool(scheduledThreadPool)
                .countDownLatch(countDownLatch)
                .build();
        scheduledThreadPool.submit(refundOrderTask);
    }

    @Override
    public void refundOrders() {
        OtsFilter filter = OtsFilter.createMatchFilter("tradeStatus", TradeStatus.REFUNDING.name());
        Long endTime = DateUtil.getCurrentLocalDateTimeMillis();
        Long startTime = DateUtil.getOneYearAgoLocalDateTimeMillis();
        OtsFilter rangeFilter = OtsFilter.createRangeFilter(OrderOtsConstant.GMT_CREATE_LONG, startTime, endTime);
        orderProcessor.doWhileLoopOfThreadTask(Collections.singletonList(filter), Collections.singletonList(rangeFilter), this::createRefundOrderTask);
    }

    @Override
    public void closeFinishedOrders() {
        OtsFilter filter = OtsFilter.createMatchFilter("tradeStatus", TradeStatus.TRADE_SUCCESS.name());
        Long startTime = 0L;
        Long endTime = DateUtil.getCurrentLocalDateTimeMillis();
        OtsFilter rangeFilter = OtsFilter.createRangeFilter(OrderOtsConstant.BILLING_END_DATE_LONG, startTime, endTime);
        orderProcessor.doWhileLoop(Collections.singletonList(filter), Collections.singletonList(rangeFilter), this::closeOrder);
    }
}




