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
import org.example.common.constant.AliPayConstants;
import org.example.common.dataobject.OrderDO;
import org.example.common.dto.OrderDTO;
import org.example.common.filter.RepeatableReadHttpRequest;
import org.example.common.helper.ots.OrderOtsHelper;
import org.example.common.model.PaymentOrderModel;
import org.example.common.model.UserInfoModel;
import org.example.common.param.payment.CreateTransactionParam;
import org.example.common.utils.HttpUtil;
import org.example.common.utils.MoneyUtil;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Component
@Slf4j
public class PaymentServiceManager {

    private Map<String, PaymentService> payChannelServiceMap = new HashMap<>();

    @Autowired
    private List<PaymentService> payChannelServices;

    @Autowired
    private OrderOtsHelper orderOtsHelper;

    @PostConstruct
    public void initPayChannelServiceMap() {
        for (PaymentService service : payChannelServices) {
            payChannelServiceMap.put(service.getType(), service);
        }
    }

    /**
     * Retrieves the PayChannelService implementation for the given type.
     *
     * @param outTradeNo the type of the payment channel
     * @return the corresponding PayChannelService implementation
     */
    public PaymentService getPayChannelService(String outTradeNo) {
        OrderDTO order = orderOtsHelper.getOrder(outTradeNo, null);
        return payChannelServiceMap.get(order.getPayChannel().getValue());
    }

    public String verifyTradeCallback(HttpServletRequest request) {
        PaymentOrderModel payOrderModel = HttpUtil.requestToObject(request, PaymentOrderModel.class);
        log.info("verifyTradeCallback, payOrderModel:{}", payOrderModel);
        OrderDO unverifiedOrder = new OrderDO();
        BeanUtils.copyProperties(payOrderModel, unverifiedOrder);
        BigDecimal totalAmountYuan = new BigDecimal(payOrderModel.getTotalAmount());
        unverifiedOrder.setTotalAmount(MoneyUtil.toCents(totalAmountYuan));
        if (payOrderModel.getBuyerPayAmount() != null) {
            BigDecimal buyerPayAmountYuan = new BigDecimal(payOrderModel.getBuyerPayAmount());
            unverifiedOrder.setBuyerPayAmount(MoneyUtil.toCents(buyerPayAmountYuan));
        }

        if (payOrderModel.getReceiptAmount() != null) {
            BigDecimal receiptAmountYuan = new BigDecimal(payOrderModel.getReceiptAmount());
            unverifiedOrder.setReceiptAmount(MoneyUtil.toCents(receiptAmountYuan));
        }
        unverifiedOrder.setOrderId(payOrderModel.getOutTradeNo());

        Map<String, String> map = HttpUtil.requestToMap(request);
        String orderId = map.get(AliPayConstants.OUT_TRADE_NO);
        return getPayChannelService(orderId).verifyTradeCallback(unverifiedOrder, map);
    }


    public String createTransaction(UserInfoModel userInfoModel, CreateTransactionParam param) {
        String orderId = param.getOrderId();
        OrderDTO order = orderOtsHelper.getOrder(param.getOrderId(), Long.parseLong(userInfoModel.getAid()));
        Long totalAmountCents = order.getTotalAmount();
        BigDecimal totalAmount = MoneyUtil.fromCents(totalAmountCents);
        return payChannelServiceMap.get(param.getPayChannel().getValue()).createTransaction(totalAmount, order.getCommodityName(), orderId);
    }

    public Boolean refundOrder(String orderId, BigDecimal refundAmount, String refundId) {
        return getPayChannelService(orderId).refundOrder(orderId, refundAmount, refundId);
    }

}
