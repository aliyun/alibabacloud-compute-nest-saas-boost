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

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.example.common.constant.PayChannel;
import org.example.common.constant.WechatPayConstants;
import org.example.common.dto.OrderDTO;
import org.example.common.helper.ots.OrderOtsHelper;
import org.example.common.model.UserInfoModel;
import org.example.common.param.payment.CreateTransactionParam;
import org.example.common.utils.HttpUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


/**
 * @author mengjunwei.mjw
 */
@Component
@Slf4j
public class PaymentServiceManager {

    private Map<String, PaymentService> payChannelServiceMap = new HashMap<>();

    @Autowired
    private List<PaymentService> payChannelServices;

    @Autowired
    private OrderOtsHelper orderOtsHelper;

    private final String SUBJECT = "subject";

    @PostConstruct
    public void initPayChannelServiceMap() {
        for (PaymentService service : payChannelServices) {
            payChannelServiceMap.put(service.getType(), service);
        }
    }

    public String verifyTradeCallback(HttpServletRequest request, HttpServletResponse response) {
        Map<String, String> map = HttpUtil.requestToMap(request);
        if (map.containsKey(SUBJECT)) {
            return payChannelServiceMap.get(PayChannel.ALIPAY.getValue()).verifyTradeCallback(request);
        } else {
            payChannelServiceMap.get(PayChannel.WECHATPAY.getValue()).verifyTradeCallback(request, response);
//                return WechatPayConstants.VERIFY_SUCCESS_RESULT;
            return WechatPayConstants.VERIFY_FAIL_RESULT;
        }
    }

    public String verifyRefundCallback(HttpServletRequest request, HttpServletResponse response) {
        Map<String, String> map = HttpUtil.requestToMap(request);
        if (map.containsKey(SUBJECT)) {
            return payChannelServiceMap.get(PayChannel.ALIPAY.getValue()).verifyRefundCallback(request);
        } else {
            payChannelServiceMap.get(PayChannel.WECHATPAY.getValue()).verifyRefundCallback(request, response);
            return WechatPayConstants.VERIFY_SUCCESS_RESULT;
        }
    }

    public String createTransaction(UserInfoModel userInfoModel, CreateTransactionParam param) {
        OrderDTO order = orderOtsHelper.getOrder(param.getOrderId(), Long.parseLong(userInfoModel.getAid()));
        return payChannelServiceMap.get(param.getPayChannel().getValue()).createTransaction(order);
    }

    public Boolean refundOrder(OrderDTO order) {
        return payChannelServiceMap.get(order.getPayChannel().getValue()).refundOutTrade(order);
    }

    /**
     * Retrieves the PayChannelService implementation for the given type.
     *
     * @param outTradeNo the type of the payment channel
     * @return the corresponding PayChannelService implementation
     */
    private PaymentService getPayChannelService(String outTradeNo) {
        OrderDTO order = orderOtsHelper.getOrder(outTradeNo, null);
        return payChannelServiceMap.get(order.getPayChannel().getValue());
    }
}