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

package org.example.common.adapter.impl;

import com.alibaba.fastjson.JSONObject;
import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.AlipayConfig;
import com.alipay.api.DefaultAlipayClient;
import com.alipay.api.domain.AlipayTradeCloseModel;
import com.alipay.api.domain.AlipayTradeQueryModel;
import com.alipay.api.domain.AlipayTradeRefundModel;
import com.alipay.api.internal.util.AlipaySignature;
import com.alipay.api.request.AlipayTradePagePayRequest;
import com.alipay.api.response.AlipayTradePagePayResponse;
import com.alipay.api.response.AlipayTradeQueryResponse;
import com.ijpay.alipay.AliPayApi;
import com.ijpay.alipay.AliPayApiConfig;
import com.ijpay.alipay.AliPayApiConfigKit;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.SocketTimeoutException;
import java.util.Optional;
import javax.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.example.common.BaseResult;
import org.example.common.adapter.BaseAlipayClient;
import org.example.common.adapter.OssClient;
import org.example.common.config.BoostAlipayConfig;
import org.example.common.constant.AliPayConstants;
import static org.example.common.constant.AliPayConstants.OUT_TRADE_NO;
import org.example.common.constant.Constants;
import static org.example.common.constant.Constants.BUCKET;
import static org.example.common.constant.Constants.SAAS_BOOST;
import org.example.common.constant.PayChannel;
import org.example.common.constant.TradeStatus;
import org.example.common.dataobject.OrderDO;
import org.example.common.dto.OrderDTO;
import org.example.common.errorinfo.ErrorInfo;
import org.example.common.exception.BizException;
import org.example.common.helper.ots.OrderOtsHelper;
import org.example.common.utils.DateUtil;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class BaseAlipayClientImpl implements BaseAlipayClient {

    private BoostAlipayConfig boostAlipayConfig;

    private AlipayClient alipayClient;

    @Resource
    private OssClient ossClient;

    @Value("${stack-name}")
    private String stackName;

    @Value("${service.region-id}")
    private String regionId;

    private OrderOtsHelper orderOtsHelper;

    private static final Integer CLOSE_TRANSACTION_TIME = 15;

    private static final String SUCCESS = "200";

    @Override
    @Retryable(value = {SocketTimeoutException.class, AlipayApiException.class}, maxAttempts = 3, backoff = @Backoff(delay = 2000))
    public AlipayTradeQueryResponse queryOutTrade(String outTradeNo) {
        try {
            AlipayTradeQueryModel model = new AlipayTradeQueryModel();
            model.setOutTradeNo(outTradeNo);
            return AliPayApi.tradeQueryToResponse(model);
        } catch (AlipayApiException e) {
            throw new BizException(ErrorInfo.ENTITY_NOT_EXIST.getStatusCode(), ErrorInfo.ENTITY_NOT_EXIST.getCode(),
                    String.format(ErrorInfo.ENTITY_NOT_EXIST.getMessage(), outTradeNo), e);
        }
    }

    @Override
    @Retryable(value = {SocketTimeoutException.class, AlipayApiException.class}, maxAttempts = 3, backoff = @Backoff(delay = 2000))
    public boolean verifySignatureWithKey(String sign, String content) {
        try {
            return AlipaySignature.rsaCheck(content, sign, boostAlipayConfig.getAlipayPublicKey(),
                    Constants.TRANSFORMATION_FORMAT_UTF_8, AliPayConstants.SIGN_TYPE_RSA2);
        } catch (AlipayApiException e) {
            throw new BizException(ErrorInfo.SERVER_UNAVAILABLE, e);
        }
    }

    @Override
    @Retryable(value = {SocketTimeoutException.class, AlipayApiException.class}, maxAttempts = 3, backoff = @Backoff(delay = 2000))
    public boolean verifySignatureWithCert(String sign, String content) {
        try {
            return AlipaySignature.rsaCertCheck(content, sign, boostAlipayConfig.getAlipayCertPath(),
                    Constants.TRANSFORMATION_FORMAT_UTF_8, AliPayConstants.SIGN_TYPE_RSA2);
        } catch (AlipayApiException e) {
            throw new BizException(ErrorInfo.SERVER_UNAVAILABLE, e);
        }
    }

    @Override
    @Retryable(value = {SocketTimeoutException.class, AlipayApiException.class}, maxAttempts = 3, backoff = @Backoff(delay = 2000))
    public String createOutTrade(OrderDTO order) {
        AlipayTradePagePayRequest request = new AlipayTradePagePayRequest();
        request.setNotifyUrl(boostAlipayConfig.getNotifyUrl());
        request.setReturnUrl(boostAlipayConfig.getReturnUrl());
        request.setBizContent(new JSONObject().fluentPut(OUT_TRADE_NO, order.getOrderId())
                .fluentPut(AliPayConstants.TOTAL_AMOUNT, formatAmount(order.getTotalAmount()))
                .fluentPut(AliPayConstants.SUBJECT, order.getCommodityName())
                .fluentPut(AliPayConstants.PRODUCT_CODE_PREFIX, AliPayConstants.PRODUCT_CODE_PC_WEB)
                .fluentPut(AliPayConstants.TIME_EXPIRE, DateUtil.getCurrentTimePlusMinutes(CLOSE_TRANSACTION_TIME))
                .fluentPut(AliPayConstants.QR_PAY_MODE, '3')
                .toString());
        log.info("createOutTrade request:{}", request.getBizContent());
        try {
            return Optional.ofNullable(alipayClient.pageExecute(request))
                    .map(AlipayTradePagePayResponse::getBody).orElse(null);
        } catch (AlipayApiException e) {
            throw new BizException(ErrorInfo.SERVER_UNAVAILABLE, e);
        }
    }

    @Override
    @Retryable(value = {SocketTimeoutException.class, AlipayApiException.class}, maxAttempts = 3, backoff = @Backoff(delay = 2000))
    public Boolean refundOutTrade(OrderDTO order) {
        try {
            AlipayTradeRefundModel model = new AlipayTradeRefundModel();
            model.setOutTradeNo(order.getOrderId());
            model.setOutRequestNo(order.getRefundId());
            model.setRefundAmount(formatAmount(order.getRefundAmount()));

            if (AliPayApi.tradeRefundToResponse(model).isSuccess()) {
                order.setTradeStatus(TradeStatus.REFUNDED);
                OrderDO orderDO = new OrderDO();
                BeanUtils.copyProperties(order, orderDO);
                orderOtsHelper.updateOrder(orderDO);
                return Boolean.TRUE;
            }
            return Boolean.FALSE;
        } catch (AlipayApiException e) {
            log.error("Refund failed. Order Id = {}, refund request id = {}", order.getOrderId(), order.getRefundId(), e);
            throw new BizException(ErrorInfo.SERVER_UNAVAILABLE, e);
        }
    }

    @Override
    @Retryable(value = {SocketTimeoutException.class, AlipayApiException.class}, maxAttempts = 3, backoff = @Backoff(delay = 2000))
    public Boolean closeOutTrade(String orderId) {
        try {
            AlipayTradeCloseModel model = new AlipayTradeCloseModel();
            model.setOutTradeNo(orderId);

            if (AliPayApi.tradeCloseToResponse(model).isSuccess()) {
                return Boolean.TRUE;
            }
            return Boolean.FALSE;
        } catch (AlipayApiException e) {
            throw new BizException(ErrorInfo.ENTITY_NOT_EXIST, e);
        }
    }

    @Override
    public void createClient(BoostAlipayConfig boostAlipayConfig) throws Exception {
        this.boostAlipayConfig = boostAlipayConfig;
        if (boostAlipayConfig.getSignatureMethod().equals(AliPayConstants.OOS_ALIPAY_SIGNATURE_OF_CERT)) {
            createClientWithCert();
        } else {
            createClientWithKey();
        }
    }

    @Override
    public void updateClient(String parameterName, String value) {
        this.boostAlipayConfig.updateOosParamConfig(parameterName, value);
        if (boostAlipayConfig.getSignatureMethod().equals(AliPayConstants.OOS_ALIPAY_SIGNATURE_OF_CERT)) {
            createClientWithCert();
        } else {
            createClientWithKey();
        }
    }

    private void createClientWithKey() {
        AlipayConfig alipayConfig = new AlipayConfig();
        alipayConfig.setServerUrl(boostAlipayConfig.getGateway());
        alipayConfig.setAppId(boostAlipayConfig.getAppId());
        alipayConfig.setPrivateKey(boostAlipayConfig.getPrivateKey());
        alipayConfig.setFormat(Constants.JSON_FORMAT);
        alipayConfig.setSignType(AliPayConstants.SIGN_TYPE_RSA2);
        alipayConfig.setAlipayPublicKey(boostAlipayConfig.getAlipayPublicKey());
        alipayConfig.setCharset(Constants.TRANSFORMATION_FORMAT_UTF_8.toLowerCase());
        try {
            alipayClient = new DefaultAlipayClient(alipayConfig);
            AliPayApiConfig alipayApiConfig = AliPayApiConfig.builder().build(new DefaultAlipayClient(alipayConfig));
            alipayApiConfig.setAppId(boostAlipayConfig.getAppId());
            AliPayApiConfigKit.setThreadLocalAppId(boostAlipayConfig.getAppId());
            AliPayApiConfigKit.setThreadLocalAliPayApiConfig(alipayApiConfig);
        } catch (AlipayApiException e) {
            throw new RuntimeException(e);
        }
    }

    private void createClientWithCert() {
        try {
            String bucketName = String.format("%s-%s-%s", SAAS_BOOST, stackName, BUCKET);
            String appCertPath = String.format("%s/%s", PayChannel.ALIPAY.getDisplayName(),
                    boostAlipayConfig.getAppCertPath());
            String alipayCertPath = String.format("%s/%s", PayChannel.ALIPAY.getDisplayName(),
                    boostAlipayConfig.getAlipayCertPath());
            String alipayRootCertPath = String.format("%s/%s", PayChannel.ALIPAY.getDisplayName(),
                    boostAlipayConfig.getAlipayRootCertPath());
            BaseResult<String> appCertContentResult = ossClient.getObjectContent(bucketName, appCertPath);
            BaseResult<String> alipayCertContentResult = ossClient.getObjectContent(bucketName, alipayCertPath);
            BaseResult<String> alipayRootCertContentResult = ossClient.getObjectContent(bucketName, alipayRootCertPath);
            if(appCertContentResult.getCode().equals(SUCCESS) && alipayCertContentResult.getCode().equals(SUCCESS) &&
                    alipayRootCertContentResult.getCode().equals(SUCCESS)) {
                String appCertContent = appCertContentResult.getData();
                String alipayCertContent = alipayCertContentResult.getData();
                String alipayRootCertContent = alipayRootCertContentResult.getData();

                AlipayConfig alipayConfig = new AlipayConfig();
                alipayConfig.setServerUrl(boostAlipayConfig.getGateway());
                alipayConfig.setAppId(boostAlipayConfig.getAppId());
                alipayConfig.setPrivateKey(boostAlipayConfig.getPrivateKey());
                alipayConfig.setAppCertContent(appCertContent);
                alipayConfig.setAlipayPublicCertContent(alipayCertContent);
                alipayConfig.setRootCertContent(alipayRootCertContent);
                alipayConfig.setFormat(Constants.JSON_FORMAT);
                alipayConfig.setCharset(Constants.TRANSFORMATION_FORMAT_UTF_8.toLowerCase());
                alipayConfig.setSignType(AliPayConstants.SIGN_TYPE_RSA2);
                alipayClient = new DefaultAlipayClient(alipayConfig);

                AliPayApiConfig alipayApiConfig = AliPayApiConfig.builder().build(new DefaultAlipayClient(alipayConfig));
                alipayApiConfig.setAppId(boostAlipayConfig.getAppId());
                AliPayApiConfigKit.setThreadLocalAppId(boostAlipayConfig.getAppId());
                AliPayApiConfigKit.setThreadLocalAliPayApiConfig(alipayApiConfig);
            }
        } catch (Exception e) {
            log.error("Failed to create Alipay client with cert. ", e);
        }
    }

    public String formatAmount(Long amountInCents) {
        BigDecimal amountInYuan = new BigDecimal(amountInCents).divide(new BigDecimal("100"), 2, RoundingMode.HALF_UP);
        return amountInYuan.toString();
    }
}
