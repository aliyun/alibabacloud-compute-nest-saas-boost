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

import com.alipay.api.internal.util.AlipaySignature;
import com.alipay.api.response.AlipayTradeQueryResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.math3.util.Precision;
import org.example.common.BaseResult;
import org.example.common.adapter.BaseAlipayClient;
import org.example.common.config.AlipayConfig;
import org.example.common.constant.AliPayConstants;
import org.example.common.constant.TradeStatus;
import org.example.common.dataobject.OrderDO;
import org.example.common.dto.OrderDTO;
import org.example.common.model.UserInfoModel;
import org.example.common.param.GetOrderParam;
import org.example.common.utils.HttpUtil;
import org.example.service.AlipayService;
import org.example.service.OrderService;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.Map;
import java.util.Optional;

@Service
@Slf4j
@Lazy
public class AlipayServiceImpl implements AlipayService {

    @Resource
    private AlipayConfig alipayConfig;

    @Resource
    private OrderService orderService;

    @Resource
    private BaseAlipayClient baseAlipayClient;

    @Override
    public String verifyTradeCallback(HttpServletRequest request) {
        OrderDO unverifiedOrder = HttpUtil.requestToObject(request, OrderDO.class);
        Map<String, String> map = HttpUtil.requestToMap(request);
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
            unverifiedOrder.setServiceInstanceId(orderFromOts.getServiceInstanceId());
            UserInfoModel userInfoModel = new UserInfoModel();
            userInfoModel.setAid(String.valueOf(orderFromOts.getAccountId()));
            log.info(unverifiedOrder.getServiceInstanceId());
            orderService.updateOrder(userInfoModel, unverifiedOrder);
            return AliPayConstants.VERIFY_SUCCESS_RESULT;
        }
        log.error("Payment failed, order id:{}", orderId);
        return AliPayConstants.VERIFY_FAIL_RESULT;
    }

    @Override
    public String createTransaction(Double totalAmount, String subject, String outTradeNo) {
        return baseAlipayClient.createTransaction(totalAmount, subject, outTradeNo);
    }

    @Override
    public Boolean refundOrder(String orderId, Double refundAmount, String refundId) {
        return baseAlipayClient.refundOrder(orderId, refundAmount, refundId);
    }

    private Boolean verifyBusinessData(OrderDO unverifiedOrder, OrderDTO orderFromTableStore) {
        if (orderFromTableStore.getTotalAmount() == null ) {
            return Boolean.FALSE;
        }
        return unverifiedOrder.getSellerId().equals(alipayConfig.getPid())
                && unverifiedOrder.getAppId().equals(alipayConfig.getAppId())
                && Precision.equals(orderFromTableStore.getTotalAmount(), unverifiedOrder.getTotalAmount());
    }
}
