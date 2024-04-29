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

package org.example.task;

import com.aliyun.computenestsupplier20210521.models.DeleteServiceInstancesRequest;
import com.aliyun.computenestsupplier20210521.models.DeleteServiceInstancesResponse;
import com.aliyun.computenestsupplier20210521.models.UpdateServiceInstanceAttributeRequest;
import com.aliyun.computenestsupplier20210521.models.UpdateServiceInstanceAttributeResponse;
import lombok.Builder;
import lombok.extern.slf4j.Slf4j;
import org.example.common.adapter.BaseAlipayClient;
import org.example.common.adapter.BaseWechatPayClient;
import org.example.common.adapter.ComputeNestSupplierClient;
import org.example.common.constant.ComputeNestConstants;
import org.example.common.constant.PayChannel;
import org.example.common.constant.TradeStatus;
import org.example.common.dataobject.OrderDO;
import org.example.common.dto.OrderDTO;
import org.example.common.helper.ots.OrderOtsHelper;
import org.example.common.utils.DateUtil;
import org.example.common.utils.JsonUtil;

import java.util.Collections;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ScheduledExecutorService;

@Builder
@Slf4j
public class RefundOrderTask implements Runnable {

    private OrderOtsHelper orderOtsHelper;

    private BaseAlipayClient baseAlipayClient;

    private BaseWechatPayClient baseWechatPayClient;

    private ComputeNestSupplierClient computeNestSupplierClient;

    private String orderId;

    private Long refundAmount;

    private String refundId;

    private String serviceInstanceId;

    private ScheduledExecutorService scheduledThreadPool;

    private CountDownLatch countDownLatch;

    private PayChannel payChannel;

    private static final int MAX_RETRY_TIMES = 3;

    @Override
    public void run() {
        int retryCount = 0;
        boolean success = false;
        while (retryCount < MAX_RETRY_TIMES && !success) {
            try {
                OrderDO orderDO = new OrderDO();
                orderDO.setTradeStatus(TradeStatus.REFUNDED);
                orderDO.setOrderId(orderId);
                OrderDTO order = orderOtsHelper.getOrder(orderId, null);
                Long currentLocalDateTimeMillis = DateUtil.getCurrentLocalDateTimeMillis();
                if (shouldDeleteServiceInstance(currentLocalDateTimeMillis, order)) {
                    DeleteServiceInstancesRequest deleteServiceInstancesRequest = new DeleteServiceInstancesRequest();
                    deleteServiceInstancesRequest.setServiceInstanceId(Collections.singletonList(serviceInstanceId));
                    DeleteServiceInstancesResponse deleteServiceInstancesResponse = computeNestSupplierClient.deleteServiceInstance(deleteServiceInstancesRequest);
                    log.info("Delete service instance status code = {}", deleteServiceInstancesResponse.getStatusCode());
                } else {
                    String endTime = DateUtil.parseIs08601DateMillis(order.getBillingStartDateMillis());
                    UpdateServiceInstanceAttributeRequest updateServiceInstanceAttributeRequest = new UpdateServiceInstanceAttributeRequest();
                    updateServiceInstanceAttributeRequest.setServiceInstanceId(serviceInstanceId);
                    updateServiceInstanceAttributeRequest.setRegionId(ComputeNestConstants.DEFAULT_REGION_ID);
                    updateServiceInstanceAttributeRequest.setEndTime(endTime);
                    UpdateServiceInstanceAttributeResponse updateServiceInstanceAttributeResponse = computeNestSupplierClient.updateServiceInstanceAttribute(updateServiceInstanceAttributeRequest);
                    log.info("Update service instance attribute success. request = {}, response = {}.", JsonUtil.toJsonString(updateServiceInstanceAttributeRequest),
                            JsonUtil.toJsonString(updateServiceInstanceAttributeResponse));
                }
                Boolean updateOrder = orderOtsHelper.updateOrder(orderDO);
                Boolean refundSuccess = Boolean.TRUE;
                order.setOrderId(orderId);
                order.setRefundAmount(refundAmount);
                order.setRefundId(refundId);
                if (payChannel != null && payChannel != PayChannel.PAY_POST && refundAmount > 0) {
                    if (PayChannel.ALIPAY == payChannel) {
                        refundSuccess = baseAlipayClient.refundOutTrade(order);
                    } else if (PayChannel.WECHATPAY == payChannel) {
                        refundSuccess = baseWechatPayClient.refundOutTrade(order);
                    }
                }
                log.info("refund {}. Update order {}.", refundSuccess, updateOrder);
                log.info("Order refund success. order id = {}.", orderDO.getOrderId());
                success = true;
            } catch (Exception e) {
                retryCount++;
                log.error("Failed to execute code. Retry count: {}", retryCount, e);
                try {
                    Thread.sleep(3000);
                } catch (InterruptedException ex) {
                    Thread.currentThread().interrupt();
                }
            } finally {
                countDownLatch.countDown();
            }
        }
        if (!success) {
            log.error("Failed to execute refund after multiple retries.");
        }
    }


    private Boolean shouldDeleteServiceInstance(Long currentLocalDateTimeMillis, OrderDTO order) {
        if (order.getPayChannel() == PayChannel.PAY_POST) {
            return Boolean.TRUE;
        }

        if (order.getBillingStartDateMillis() != null && order.getBillingEndDateMillis() != null) {
            return currentLocalDateTimeMillis >= order.getBillingStartDateMillis() && currentLocalDateTimeMillis <= order.getBillingEndDateMillis();
        }
        return Boolean.FALSE;
    }
}
