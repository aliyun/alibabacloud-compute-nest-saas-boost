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
package org.example.service.payment.impl;

import org.example.common.constant.PayChannel;
import org.example.common.dataobject.OrderDO;
import org.example.service.payment.PaymentService;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class WechatPayServiceImpl implements PaymentService {


    @Override
    public String verifyTradeCallback(OrderDO unverifiedOrder, Map<String, String> map) {
        return null;
    }

    @Override
    public String createTransaction(Double totalAmount, String subject, String outTradeNo) {
        return null;
    }

    @Override
    public Boolean refundOrder(String orderId, Double refundAmount, String refundId) {
        return null;
    }

    @Override
    public String getType() {
        return PayChannel.WECHATPAY.getValue();
    }
}
