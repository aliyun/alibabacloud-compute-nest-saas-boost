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


import cn.hutool.json.JSONUtil;
import com.alibaba.fastjson2.JSONObject;
import com.alipay.api.AlipayApiException;
import com.ijpay.core.IJPayHttpResponse;
import com.ijpay.core.enums.AuthTypeEnum;
import com.ijpay.core.enums.RequestMethodEnum;
import com.ijpay.core.utils.DateTimeZoneUtil;
import com.ijpay.wxpay.WxPayApi;
import com.ijpay.wxpay.WxPayApiConfig;
import com.ijpay.wxpay.WxPayApiConfigKit;
import com.ijpay.wxpay.enums.v3.BasePayApiEnum;
import com.ijpay.wxpay.model.v3.Amount;
import com.ijpay.wxpay.model.v3.RefundAmount;
import com.ijpay.wxpay.model.v3.RefundModel;
import com.ijpay.wxpay.model.v3.UnifiedOrderModel;
import com.wechat.pay.contrib.apache.httpclient.WechatPayHttpClientBuilder;
import com.wechat.pay.contrib.apache.httpclient.auth.PrivateKeySigner;
import com.wechat.pay.contrib.apache.httpclient.auth.ScheduledUpdateCertificatesVerifier;
import com.wechat.pay.contrib.apache.httpclient.auth.WechatPay2Credentials;
import com.wechat.pay.contrib.apache.httpclient.auth.WechatPay2Validator;
import com.wechat.pay.contrib.apache.httpclient.util.PemUtil;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.impl.client.CloseableHttpClient;
import org.example.common.adapter.BaseWechatPayClient;
import org.example.common.config.BoostWechatPayConfig;
import org.example.common.constant.PayChannel;
import org.example.common.constant.WechatPayConstants;
import org.example.common.dto.OrderDTO;
import org.example.common.errorinfo.ErrorInfo;
import org.example.common.exception.BizException;
import org.example.common.helper.LocalCertStorageHelper;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.net.SocketTimeoutException;
import java.nio.charset.StandardCharsets;
import java.security.PrivateKey;
import java.util.HashMap;
import java.util.Map;

import static org.example.common.constant.WechatPayConstants.MCH_ID;

@Component
@Slf4j
@Getter
public class BaseWechatPayClientImpl implements BaseWechatPayClient {

    private BoostWechatPayConfig wechatPayConfig;

    private CloseableHttpClient wechatPayHttpClient;

    @Resource
    private LocalCertStorageHelper localCertStorageHelper;

    private static final Integer CLOSE_TRANSACTION_TIME = 15;

    private static final int OK = 200;

    private static final String SUCCESS = "SUCCESS";

    private static final String LOCAL_CERT_STORAGE_PATH = "/home/admin/application/boost.server/target/cert";

    private String keyPath;

    private ScheduledUpdateCertificatesVerifier scheduledUpdateCertificatesVerifier;


    @Override
    @Retryable(value = {SocketTimeoutException.class, Exception.class}, backoff = @Backoff(delay = 2000))
    public String nativePay(OrderDTO order) {
        try {
            String subject = order.getCommodityName();
            if(subject == null || subject.isEmpty() ) {
                subject = "Unknown Subject";
            }

            String timeExpire = DateTimeZoneUtil.dateToTimeZone(System.currentTimeMillis() + 60000L * CLOSE_TRANSACTION_TIME);
            UnifiedOrderModel unifiedOrderModel = new UnifiedOrderModel()
                    .setAppid(wechatPayConfig.getAppId())
                    .setMchid(wechatPayConfig.getMchId())
                    .setDescription(subject)
                    .setOut_trade_no(order.getOrderId())
                    .setTime_expire(timeExpire)
                    .setAttach(subject)
                    .setNotify_url(wechatPayConfig.getPayNotifyUrl())
                    .setAmount(new Amount().setTotal(order.getTotalAmount().intValue()));

            IJPayHttpResponse response = WxPayApi.v3(
                    RequestMethodEnum.POST,
                    wechatPayConfig.getGateway(),
                    BasePayApiEnum.NATIVE_PAY.toString(),
                    wechatPayConfig.getMchId(),
                    wechatPayConfig.getMchSerialNo(),
                    null,
                    keyPath,
                    JSONUtil.toJsonStr(unifiedOrderModel),
                    AuthTypeEnum.RSA.getCode()
            );
            JSONObject jsonObject = JSONObject.parseObject(response.getBody());
            return jsonObject.getString(WechatPayConstants.CODE_URL);
        } catch (Exception e) {
            throw new BizException(ErrorInfo.SERVER_UNAVAILABLE.getStatusCode(), ErrorInfo.SERVER_UNAVAILABLE.getCode(),
                    String.format(ErrorInfo.SERVER_UNAVAILABLE.getMessage(), e));
        }
    }

    @Override
    @Retryable(value = {SocketTimeoutException.class, Exception.class}, backoff = @Backoff(delay = 2000))
    public String queryOutTrade(String outTradeNo) {
        try {
            Map<String, String> params = new HashMap<>(16);
            params.put(MCH_ID, wechatPayConfig.getMchId());

            IJPayHttpResponse response = WxPayApi.v3(
                    RequestMethodEnum.GET,
                    wechatPayConfig.getGateway(),
                    String.format(BasePayApiEnum.ORDER_QUERY_BY_OUT_TRADE_NO.toString(), outTradeNo),
                    wechatPayConfig.getMchId(),
                    wechatPayConfig.getMchSerialNo(),
                    null,
                    keyPath,
                    params,
                    AuthTypeEnum.RSA.getCode()
            );

            return response.getBody();
        } catch (Exception e) {
            throw new BizException(ErrorInfo.SERVER_UNAVAILABLE.getStatusCode(), ErrorInfo.SERVER_UNAVAILABLE.getCode(),
                    String.format(ErrorInfo.SERVER_UNAVAILABLE.getMessage(), e));
        }
    }

    @Override
    @Retryable(value = {SocketTimeoutException.class, AlipayApiException.class}, backoff = @Backoff(delay = 2000))
    public Boolean closeOutTrade(String outTradeNo) {
        try {
            Map<String, String> params = new HashMap<>(16);
            params.put(MCH_ID, wechatPayConfig.getMchId());

            IJPayHttpResponse response = WxPayApi.v3(
                    RequestMethodEnum.GET,
                    wechatPayConfig.getGateway(),
                    String.format(BasePayApiEnum.CLOSE_ORDER_BY_OUT_TRADE_NO.toString(), outTradeNo),
                    wechatPayConfig.getMchId(),
                    wechatPayConfig.getMchSerialNo(),
                    null,
                    keyPath,
                    params,
                    AuthTypeEnum.RSA.getCode()
            );
            if(response.getStatus() == OK) {
                return Boolean.TRUE;
            }
            return Boolean.FALSE;
        } catch (Exception e) {
            throw new BizException(ErrorInfo.SERVER_UNAVAILABLE.getStatusCode(), ErrorInfo.SERVER_UNAVAILABLE.getCode(),
                    String.format(ErrorInfo.SERVER_UNAVAILABLE.getMessage(), e));
        }
    }

    @Override
    @Retryable(value = {SocketTimeoutException.class, Exception.class}, backoff = @Backoff(delay = 2000))
    public Boolean refundOutTrade(OrderDTO order) {
        try {
            RefundModel refundModel = new RefundModel()
                    .setOut_trade_no(order.getOrderId())
                    .setOut_refund_no(order.getRefundId())
                    .setNotify_url(wechatPayConfig.getRefundNotifyUrl())
                    .setAmount(new RefundAmount().setRefund(order.getRefundAmount().intValue())
                            .setTotal(order.getTotalAmount().intValue()).setCurrency("CNY"));

            IJPayHttpResponse response = WxPayApi.v3(
                    RequestMethodEnum.POST,
                    wechatPayConfig.getGateway(),
                    BasePayApiEnum.REFUND.toString(),
                    wechatPayConfig.getMchId(),
                    wechatPayConfig.getMchSerialNo(),
                    null,
                    keyPath,
                    JSONUtil.toJsonStr(refundModel)
            );
            if(response.getStatus() == OK && response.getBody().contains(SUCCESS)) {
                return Boolean.TRUE;
            }
            return Boolean.FALSE;
        } catch (Exception e) {
            throw new BizException(ErrorInfo.SERVER_UNAVAILABLE.getStatusCode(), ErrorInfo.SERVER_UNAVAILABLE.getCode(),
                    String.format(ErrorInfo.SERVER_UNAVAILABLE.getMessage(), e));
        }
    }

    @Override
    public String queryRefundOutTrade(String outRefundNo) {
        try {
            Map<String, String> params = new HashMap<>(16);
            params.put(MCH_ID, wechatPayConfig.getMchId());

            IJPayHttpResponse response = WxPayApi.v3(
                    RequestMethodEnum.GET,
                    wechatPayConfig.getGateway(),
                    String.format(BasePayApiEnum.REFUND_QUERY_BY_OUT_REFUND_NO.toString(), outRefundNo),
                    wechatPayConfig.getMchId(),
                    wechatPayConfig.getMchSerialNo(),
                    null,
                    keyPath,
                    params,
                    AuthTypeEnum.RSA.getCode()
            );

            return response.getBody();
        } catch (Exception e) {
            throw new BizException(ErrorInfo.SERVER_UNAVAILABLE.getStatusCode(), ErrorInfo.SERVER_UNAVAILABLE.getCode(),
                    String.format(ErrorInfo.SERVER_UNAVAILABLE.getMessage(), e));
        }
    }

    @Override
    public ScheduledUpdateCertificatesVerifier getVerifier() {
        return scheduledUpdateCertificatesVerifier;

    }

    @Override
    public void createClient(BoostWechatPayConfig wechatPayConfig) throws Exception {
        this.wechatPayConfig = wechatPayConfig;
        createClientWithCert();
    }

    @Override
    public void updateClient(String parameterName, String value) {
        this.wechatPayConfig.updateOosParamConfig(parameterName, value);
        createClientWithCert();
    }

    private void createClientWithCert() {
        try {
            keyPath = String.format("%s/%s/%s", LOCAL_CERT_STORAGE_PATH, PayChannel.WECHATPAY.getDisplayName(),
                    wechatPayConfig.getPrivateKeyPath());
            if (localCertStorageHelper.doesFileExist(keyPath)) {
                WxPayApiConfig wxPayApiConfig = WxPayApiConfig.builder()
                        .appId(wechatPayConfig.getAppId())
                        .mchId(wechatPayConfig.getMchId())
                        .apiKey3(wechatPayConfig.getApiV3Key())
                        .keyPath(keyPath)
                        .build();
                WxPayApiConfigKit.setThreadLocalWxPayApiConfig(wxPayApiConfig);

                PrivateKey privateKey = getPrivateKey(keyPath);
                PrivateKeySigner privateKeySigner = new PrivateKeySigner(wechatPayConfig.getMchSerialNo(), privateKey);
                WechatPay2Credentials wechatPay2Credentials = new WechatPay2Credentials(wechatPayConfig.getMchId(),
                        privateKeySigner);
                log.info("config keyPath: {}, getMchSerialNo :{}, privateKey :{}, mchId :{} ", keyPath, wechatPayConfig.getMchSerialNo(), privateKey, wechatPayConfig.getMchId());
                scheduledUpdateCertificatesVerifier = new ScheduledUpdateCertificatesVerifier(wechatPay2Credentials,
                        wechatPayConfig.getApiV3Key().getBytes(StandardCharsets.UTF_8));

                WechatPayHttpClientBuilder builder = WechatPayHttpClientBuilder.create()
                        .withMerchant(wechatPayConfig.getMchId(), wechatPayConfig.getMchSerialNo(), privateKey)
                        .withValidator(new WechatPay2Validator(scheduledUpdateCertificatesVerifier));

                wechatPayHttpClient = builder.build();


                log.info("WechatPay client created.");
            }
        } catch (Exception e) {
            log.error("Failed to create WechatPay client with cert. ", e);
        }
    }

    private PrivateKey getPrivateKey(String filename){
        try {
            return PemUtil.loadPrivateKey(new FileInputStream(filename));
        } catch (FileNotFoundException e) {
            throw new RuntimeException("Private key file does not exist", e);
        }
    }
}
