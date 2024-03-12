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
package org.example.service.payment;

import lombok.extern.slf4j.Slf4j;
import org.example.common.constant.PayChannel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
@Slf4j
public class PaymentServiceManger {
    private Map<String, PaymentService> payChannelServiceMap = new HashMap<>();

    @Autowired
    private List<PaymentService> payChannelServices;

    @PostConstruct
    public void initPayChannelServiceMap() {
        for (PaymentService service : payChannelServices) {
            payChannelServiceMap.put(service.getType(), service);
        }
    }

    /**
     * Retrieves the PayChannelService implementation for the given type.
     *
     * @param type the type of the payment channel
     * @return the corresponding PayChannelService implementation
     */
    public PaymentService getPayChannelService(String type) {
        return payChannelServiceMap.get(type);
    }

    public String verifyTradeCallback(HttpServletRequest request, PayChannel payChannel) {
        return getPayChannelService(payChannel.getValue()).verifyTradeCallback(request);
    }


    public String createTransaction(Double totalAmount, String subject, String outTradeNo, PayChannel payChannel) {
        return getPayChannelService(payChannel.getValue()).createTransaction(totalAmount, subject, outTradeNo);
    }

    public Boolean refundOrder(String orderId, Double refundAmount, String refundId, PayChannel payChannel) {
        return getPayChannelService(payChannel.getValue()).refundOrder(orderId, refundAmount, refundId);
    }
}
