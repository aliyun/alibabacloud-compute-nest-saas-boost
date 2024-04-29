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
import com.alipay.api.DefaultAlipayClient;
import com.alipay.api.internal.util.AlipaySignature;
import com.alipay.api.request.AlipayTradeCloseRequest;
import com.alipay.api.request.AlipayTradePagePayRequest;
import com.alipay.api.request.AlipayTradeQueryRequest;
import com.alipay.api.request.AlipayTradeRefundRequest;
import com.alipay.api.response.AlipayTradeCloseResponse;
import com.alipay.api.response.AlipayTradePagePayResponse;
import com.alipay.api.response.AlipayTradeQueryResponse;
import com.alipay.api.response.AlipayTradeRefundResponse;
import lombok.extern.slf4j.Slf4j;
import org.example.common.adapter.BaseAlipayClient;
import org.example.common.config.AlipayConfig;
import org.example.common.constant.AliPayConstants;
import org.example.common.constant.Constants;
import org.example.common.errorinfo.ErrorInfo;
import org.example.common.exception.BizException;
import org.example.common.utils.DateUtil;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.net.SocketTimeoutException;
import java.util.Optional;

import static org.example.common.constant.AliPayConstants.OUT_TRADE_NO;
import static org.example.common.constant.AliPayConstants.REFUND_AMOUNT;
import static org.example.common.constant.AliPayConstants.REFUND_REQUEST_ID;

@Component
@Slf4j
public class BaseAlipayClientImpl implements BaseAlipayClient {

    private AlipayConfig alipayConfig;

    private AlipayClient alipayClient;

    private static final Integer CLOSE_TRANSACTION_TIME = 15;

    @Override
    @Retryable(value = {SocketTimeoutException.class, AlipayApiException.class}, maxAttempts = 3, backoff = @Backoff(delay = 2000))
    public AlipayTradeQueryResponse queryOutTrade(String outTradeNumber) {
        AlipayTradeQueryRequest request = new AlipayTradeQueryRequest();
        request.setBizContent(new JSONObject().fluentPut(OUT_TRADE_NO, outTradeNumber).toString());
        try {
            return alipayClient.execute(request);
        } catch (AlipayApiException e) {
            throw new BizException(ErrorInfo.ENTITY_NOT_EXIST.getStatusCode(), ErrorInfo.ENTITY_NOT_EXIST.getCode(),
                    String.format(ErrorInfo.ENTITY_NOT_EXIST.getMessage(), outTradeNumber), e);
        }
    }

    @Override
    @Retryable(value = {SocketTimeoutException.class, AlipayApiException.class}, maxAttempts = 3, backoff = @Backoff(delay = 2000))
    public boolean verifySignature(String sign, String content) {
        try {
            return AlipaySignature.rsaCheck(content, sign, alipayConfig.getOfficialPublicKey(),
                    Constants.TRANSFORMATION_FORMAT_UTF_8, AliPayConstants.SIGN_TYPE_RSA2);
        } catch (AlipayApiException e) {
            throw new BizException(ErrorInfo.SERVER_UNAVAILABLE, e);
        }
    }

    @Override
    @Retryable(value = {SocketTimeoutException.class, AlipayApiException.class}, maxAttempts = 3, backoff = @Backoff(delay = 2000))
    public String createTransaction(BigDecimal totalAmount, String subject, String outTradeNo) {
        AlipayTradePagePayRequest request = new AlipayTradePagePayRequest();
        request.setNotifyUrl(alipayConfig.getNotifyUrl());
        request.setReturnUrl(alipayConfig.getReturnUrl());
        request.setBizContent(new JSONObject().fluentPut(OUT_TRADE_NO, outTradeNo)
                .fluentPut(AliPayConstants.TOTAL_AMOUNT, totalAmount).fluentPut(AliPayConstants.SUBJECT, subject)
                .fluentPut(AliPayConstants.PRODUCT_CODE_PREFIX, AliPayConstants.PRODUCT_CODE_PC_WEB)
                .fluentPut(AliPayConstants.TIME_EXPIRE, DateUtil.getCurrentTimePlusMinutes(CLOSE_TRANSACTION_TIME)).toString());
        try {
            return Optional.ofNullable(alipayClient.pageExecute(request))
                    .map(AlipayTradePagePayResponse::getBody).orElse(null);
        } catch (AlipayApiException e) {
            throw new BizException(ErrorInfo.SERVER_UNAVAILABLE, e);
        }
    }

    @Override
    @Retryable(value = {SocketTimeoutException.class, AlipayApiException.class}, maxAttempts = 3, backoff = @Backoff(delay = 2000))
    public Boolean refundOrder(String orderId, BigDecimal refundAmount, String refundRequestId) {
        Double formatRefundAmount = Double.parseDouble(String.format("%.2f", refundAmount));
        JSONObject bizContent = new JSONObject().fluentPut(OUT_TRADE_NO, orderId)
                .fluentPut(REFUND_AMOUNT, formatRefundAmount)
                .fluentPut(REFUND_REQUEST_ID, refundRequestId);
        AlipayTradeRefundRequest alipayTradeRefundRequest = new AlipayTradeRefundRequest();
        alipayTradeRefundRequest.setBizContent(bizContent.toString());
        try {
            AlipayTradeRefundResponse execute = alipayClient.execute(alipayTradeRefundRequest);
            if (execute.isSuccess()) {
                return Boolean.TRUE;
            }
        } catch (AlipayApiException e) {
            log.error("Refund failed. Order Id = {}, refund request id = {}", orderId, refundRequestId, e);
        }
        return Boolean.FALSE;
    }

    @Override
    @Retryable(value = {SocketTimeoutException.class, AlipayApiException.class}, maxAttempts = 3, backoff = @Backoff(delay = 2000))
    public Boolean closeOrder(String orderId) {
        AlipayTradeCloseRequest request = new AlipayTradeCloseRequest();
        JSONObject bizContent = new JSONObject();
        bizContent.put(OUT_TRADE_NO, orderId);
        request.setBizContent(bizContent.toString());
        AlipayTradeCloseResponse response;
        try {
            response = alipayClient.execute(request);
            if (response.isSuccess()) {
                return Boolean.TRUE;
            }
        } catch (AlipayApiException e) {
            throw new BizException(ErrorInfo.ENTITY_NOT_EXIST, e);
        }
        return Boolean.FALSE;
    }

    @Override
    public void createClient(AlipayConfig alipayConfig) throws Exception {
        this.alipayConfig = alipayConfig;
        alipayClient = new DefaultAlipayClient(
                alipayConfig.getGateway(),
                alipayConfig.getAppId(),
                alipayConfig.getPrivateKey(),
                Constants.JSON_FORMAT,
                Constants.TRANSFORMATION_FORMAT_UTF_8.toLowerCase(),
                alipayConfig.getOfficialPublicKey(),
                AliPayConstants.SIGN_TYPE_RSA2);
    }

    @Override
    public void updateClient(String parameterName, String value) throws Exception {
        this.alipayConfig.updateOosParamConfig(parameterName, value);
        alipayClient = new DefaultAlipayClient(
                alipayConfig.getGateway(),
                alipayConfig.getAppId(),
                alipayConfig.getPrivateKey(),
                Constants.JSON_FORMAT,
                Constants.TRANSFORMATION_FORMAT_UTF_8.toLowerCase(),
                alipayConfig.getOfficialPublicKey(),
                AliPayConstants.SIGN_TYPE_RSA2);
    }
}
