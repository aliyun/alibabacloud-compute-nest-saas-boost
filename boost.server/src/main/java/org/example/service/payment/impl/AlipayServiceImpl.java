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
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Map;
import java.util.Optional;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.math3.util.Precision;
import org.example.common.BaseResult;
import org.example.common.adapter.BaseAlipayClient;
import org.example.common.config.BoostAlipayConfig;
import org.example.common.constant.AliPayConstants;
import org.example.common.constant.PayChannel;
import org.example.common.constant.TradeStatus;
import org.example.common.dataobject.OrderDO;
import org.example.common.dto.OrderDTO;
import org.example.common.errorinfo.ErrorInfo;
import org.example.common.exception.BizException;
import org.example.common.helper.ots.OrderOtsHelper;
import org.example.common.model.PaymentOrderModel;
import org.example.common.model.UserInfoModel;
import org.example.common.param.order.GetOrderParam;
import org.example.common.utils.DateUtil;
import org.example.common.utils.HttpUtil;
import org.example.common.utils.MoneyUtil;
import org.example.service.order.OrderService;
import org.example.service.payment.PaymentService;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

/**
 * @author mengjunwei.mjw
 */
@Service
@Slf4j
public class AlipayServiceImpl implements PaymentService {

    @Resource
    private BoostAlipayConfig boostAlipayConfig;

    @Resource
    private OrderService orderService;

    @Resource
    private BaseAlipayClient baseAlipayClient;

    @Resource
    private OrderOtsHelper orderOtsHelper;

    @Override
    public String verifyTradeCallback(HttpServletRequest request) {
        PaymentOrderModel payOrderModel = HttpUtil.requestToObject(request, PaymentOrderModel.class);
        log.info("verifyTradeCallback, payOrderModel:{}", payOrderModel);
        OrderDO unverifiedOrder = new OrderDO();
        BeanUtils.copyProperties(payOrderModel, unverifiedOrder);
        if (payOrderModel.getTotalAmount() == null || payOrderModel.getBuyerPayAmount() == null ||
                payOrderModel.getReceiptAmount() == null) {
            log.error("Payment order verification failed, totalAmount:{}, buyerPayAmount:{}, receiptAmount:{}",
                    payOrderModel.getTotalAmount(), payOrderModel.getBuyerPayAmount(), payOrderModel.getReceiptAmount());
            return AliPayConstants.VERIFY_FAIL_RESULT;
        }
        BigDecimal totalAmountYuan = new BigDecimal(payOrderModel.getTotalAmount());
        unverifiedOrder.setTotalAmount(MoneyUtil.toCents(totalAmountYuan));
        BigDecimal buyerPayAmountYuan = new BigDecimal(payOrderModel.getBuyerPayAmount());
        unverifiedOrder.setBuyerPayAmount(MoneyUtil.toCents(buyerPayAmountYuan));
        BigDecimal receiptAmountYuan = new BigDecimal(payOrderModel.getReceiptAmount());
        unverifiedOrder.setReceiptAmount(MoneyUtil.toCents(receiptAmountYuan));

        unverifiedOrder.setOrderId(payOrderModel.getOutTradeNo());
        Map<String, String> map = HttpUtil.requestToMap(request);
        String orderId = map.get(AliPayConstants.OUT_TRADE_NO);
        OrderDTO orderFromOts = Optional.ofNullable(orderService.getOrder(null, new GetOrderParam(orderId)))
                .map(BaseResult::getData)
                .orElse(null);

        if (orderFromOts == null || orderFromOts.getTradeStatus() == null) {
            log.error("Order not found, order id:{}", orderId);
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

        boolean hasSignatureVerified;
        AlipayTradeQueryResponse response;
        if(boostAlipayConfig.getSignatureMethod().equals(AliPayConstants.OOS_ALIPAY_SIGNATURE_OF_CERT)) {
            hasSignatureVerified = baseAlipayClient.verifySignatureWithCert(unverifiedOrder.getSign(), AlipaySignature.getSignCheckContentV1(map));
            response = baseAlipayClient.queryOutTrade(orderId, true);
        } else {
            hasSignatureVerified = baseAlipayClient.verifySignatureWithKey(unverifiedOrder.getSign(), AlipaySignature.getSignCheckContentV1(map));
            response = baseAlipayClient.queryOutTrade(orderId, false);
        }

        if (!hasSignatureVerified) {
            log.error("Signature verification failed, trade number: {}", orderId);
            return AliPayConstants.VERIFY_FAIL_RESULT;
        }

        if (response != null && TradeStatus.TRADE_SUCCESS.name().equals(response.getTradeStatus())) {
            String currentIs08601Time = DateUtil.getCurrentIs08601Time();
            unverifiedOrder.setGmtPayment(currentIs08601Time);
            unverifiedOrder.setOrderId(orderId);
            unverifiedOrder.setProductComponents(orderFromOts.getProductComponents());
            unverifiedOrder.setTradeStatus(TradeStatus.TRADE_SUCCESS);
            unverifiedOrder.setPayPeriod(orderFromOts.getPayPeriod());
            unverifiedOrder.setPayPeriodUnit(orderFromOts.getPayPeriodUnit());
            unverifiedOrder.setServiceId(orderFromOts.getServiceId());
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
    public String verifyRefundCallback(HttpServletRequest request) {
        throw new BizException(ErrorInfo.UNKNOWN_ERROR.getStatusCode(), ErrorInfo.UNKNOWN_ERROR.getCode(),
                ErrorInfo.UNKNOWN_ERROR.getMessage());
    }

    @Override
    public String createTransaction(OrderDTO order) {
        if (StringUtils.isNotEmpty(order.getPaymentForm()) && PayChannel.ALIPAY.equals(order.getPayChannel())) {
            return order.getPaymentForm();
        }
        String transaction = baseAlipayClient.createOutTrade(order);
        OrderDO orderDO = new OrderDO();
        orderDO.setOrderId(order.getOrderId());
        orderDO.setPayChannel(PayChannel.ALIPAY);
        orderDO.setPaymentForm(transaction);
        orderOtsHelper.updateOrder(orderDO);
        return transaction;
    }

    @Override
    public void verifyTradeCallback(HttpServletRequest request, HttpServletResponse response) {
        throw new BizException(ErrorInfo.UNKNOWN_ERROR);
    }

    @Override
    public void verifyRefundCallback(HttpServletRequest request, HttpServletResponse response) {
        throw new BizException(ErrorInfo.UNKNOWN_ERROR);
    }

    @Override
    public Boolean refundOutTrade(OrderDTO order) {
        return baseAlipayClient.refundOutTrade(order);
    }

    private Boolean verifyBusinessData(OrderDO unverifiedOrder, OrderDTO orderFromTableStore) {
        if (orderFromTableStore.getTotalAmount() == null) {
            return Boolean.FALSE;
        }
        log.info("unverifiedOrder:sellerId{}, appId{}, totalAmount{}, pid{}, appid{}, totalAmount{}",
                unverifiedOrder.getSellerId(), unverifiedOrder.getAppId(), unverifiedOrder.getTotalAmount(),
                boostAlipayConfig.getPid(), boostAlipayConfig.getAppId(), orderFromTableStore.getTotalAmount());
        return unverifiedOrder.getSellerId().equals(boostAlipayConfig.getPid())
                && unverifiedOrder.getAppId().equals(boostAlipayConfig.getAppId())
                && Precision.equals(orderFromTableStore.getTotalAmount(), unverifiedOrder.getTotalAmount());
    }

    @Override
    public String getType() {
        return PayChannel.ALIPAY.getValue();
    }
}
