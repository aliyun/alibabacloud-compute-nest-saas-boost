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

import com.alipay.api.internal.util.AlipaySignature;
import com.alipay.api.response.AlipayTradeQueryResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.math3.util.Precision;
import org.example.common.BaseResult;
import org.example.common.adapter.BaseAlipayClient;
import org.example.common.config.AlipayConfig;
import org.example.common.constant.AliPayConstants;
import org.example.common.constant.PayChannel;
import org.example.common.constant.TradeStatus;
import org.example.common.dataobject.OrderDO;
import org.example.common.dto.OrderDTO;
import org.example.common.helper.ots.OrderOtsHelper;
import org.example.common.model.UserInfoModel;
import org.example.common.param.order.GetOrderParam;
import org.example.service.order.OrderService;
import org.example.service.payment.PaymentService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Map;
import java.util.Optional;

@Service
@Slf4j
public class AlipayServiceImpl implements PaymentService {

    @Resource
    private AlipayConfig alipayConfig;

    @Resource
    private OrderService orderService;

    @Resource
    private BaseAlipayClient baseAlipayClient;

    @Resource
    private OrderOtsHelper orderOtsHelper;

    @Override
    public String verifyTradeCallback(OrderDO unverifiedOrder, Map<String, String> map) {
        String orderId = map.get(AliPayConstants.OUT_TRADE_NO);
        OrderDTO orderFromOts = Optional.ofNullable(orderService.getOrder(null, new GetOrderParam(orderId)))
                .map(BaseResult::getData)
                .orElse(null);

        if (orderFromOts == null || orderFromOts.getTradeStatus() == null) {
            return AliPayConstants.VERIFY_FAIL_RESULT;
        }
        //考虑支付宝网络问题多次verify导致重复部署问题
        String tradeStatus = orderFromOts.getTradeStatus().name();
        if (Arrays.asList(TradeStatus.TRADE_SUCCESS.name(), TradeStatus.TRADE_FINISHED.name()).contains(tradeStatus)) {
            return AliPayConstants.VERIFY_SUCCESS_RESULT;
        }

        if (!verifyBusinessData(unverifiedOrder, orderFromOts)) {
            log.error("Business data validation failed, order id:{}", orderId);
            return AliPayConstants.VERIFY_FAIL_RESULT;
        }

        Boolean hasSignatureVerified = baseAlipayClient.verifySignature(unverifiedOrder.getSign(), AlipaySignature.getSignCheckContentV1(map));
        if (!hasSignatureVerified) {
            log.error("Signature verification failed, trade number: {}", orderId);
            return AliPayConstants.VERIFY_FAIL_RESULT;
        }

        AlipayTradeQueryResponse response = baseAlipayClient.queryOutTrade(orderId);
        if (response != null && TradeStatus.TRADE_SUCCESS.name().equals(response.getTradeStatus())) {
            unverifiedOrder.setOrderId(orderId);
            unverifiedOrder.setProductComponents(orderFromOts.getProductComponents());
            unverifiedOrder.setTradeStatus(TradeStatus.TRADE_SUCCESS);
            unverifiedOrder.setPayPeriod(orderFromOts.getPayPeriod());
            unverifiedOrder.setPayPeriodUnit(orderFromOts.getPayPeriodUnit());
            if (StringUtils.isEmpty(unverifiedOrder.getServiceInstanceId())) {
                unverifiedOrder.setServiceInstanceId(orderFromOts.getServiceInstanceId());
            }
            UserInfoModel userInfoModel = new UserInfoModel();
            userInfoModel.setAid(String.valueOf(orderFromOts.getAccountId()));
            orderService.updateOrder(userInfoModel, unverifiedOrder);
            return AliPayConstants.VERIFY_SUCCESS_RESULT;
        }
        log.error("Payment failed, order id:{}", orderId);
        return AliPayConstants.VERIFY_FAIL_RESULT;
    }

    @Override
    public String createTransaction(BigDecimal totalAmount, String subject, String outTradeNo) {
        OrderDTO order = orderOtsHelper.getOrder(outTradeNo, null);
        if (StringUtils.isNotEmpty(order.getPaymentForm())) {
            return order.getPaymentForm();
        }
        String transaction = baseAlipayClient.createTransaction(totalAmount, subject, outTradeNo);
        OrderDO orderDO = new OrderDO();
        orderDO.setOrderId(outTradeNo);
        orderDO.setPayChannel(PayChannel.ALIPAY);
        orderDO.setPaymentForm(transaction);
        orderOtsHelper.updateOrder(orderDO);
        return transaction;
    }

    @Override
    public Boolean refundOrder(String orderId, BigDecimal refundAmount, String refundId) {
        return baseAlipayClient.refundOrder(orderId, refundAmount, refundId);
    }

    private Boolean verifyBusinessData(OrderDO unverifiedOrder, OrderDTO orderFromTableStore) {
        if (orderFromTableStore.getTotalAmount() == null) {
            return Boolean.FALSE;
        }
        return unverifiedOrder.getSellerId().equals(alipayConfig.getPid())
                && unverifiedOrder.getAppId().equals(alipayConfig.getAppId())
                && Precision.equals(orderFromTableStore.getTotalAmount(), unverifiedOrder.getTotalAmount());
    }

    @Override
    public String getType() {
        return PayChannel.ALIPAY.getValue();
    }
}
