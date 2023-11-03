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
import lombok.Builder;
import lombok.extern.slf4j.Slf4j;
import org.example.common.adapter.BaseAlipayClient;
import org.example.common.adapter.ComputeNestSupplierClient;
import org.example.common.constant.TradeStatus;
import org.example.common.dataobject.OrderDO;
import org.example.common.dto.OrderDTO;
import org.example.common.helper.OrderOtsHelper;

import java.util.Collections;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ScheduledExecutorService;

@Builder
@Slf4j
public class RefundOrderTask implements Runnable{

    private OrderOtsHelper orderOtsHelper;

    private BaseAlipayClient baseAlipayClient;

    private ComputeNestSupplierClient computeNestSupplierClient;

    private String orderId;

    private Double refundAmount;

    private String refundId;

    private String serviceInstanceId;

    private ScheduledExecutorService scheduledThreadPool;

    private CountDownLatch countDownLatch;

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
                Boolean alipaySuccess = baseAlipayClient.refundOrder(orderId, Double.parseDouble(String.format("%.2f", refundAmount)), refundId);
                DeleteServiceInstancesRequest deleteServiceInstancesRequest = new DeleteServiceInstancesRequest();
                deleteServiceInstancesRequest.setServiceInstanceId(Collections.singletonList(serviceInstanceId));
                DeleteServiceInstancesResponse deleteServiceInstancesResponse = computeNestSupplierClient.deleteServiceInstance(deleteServiceInstancesRequest);
                Boolean updateOrder = orderOtsHelper.updateOrder(orderDO);
                log.info("Alipay refund {}. Update order {}. Delete service instance status code = {}", alipaySuccess, updateOrder, deleteServiceInstancesResponse.getStatusCode());
                log.info("Order refund success. order id = {}.", orderDO.getOrderId());
                OrderDTO order = orderOtsHelper.getOrder(orderId, null);
                success = order.getTradeStatus() == TradeStatus.REFUNDED;
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
            log.error("Failed to execute code after multiple retries.");
        }
    }
}
