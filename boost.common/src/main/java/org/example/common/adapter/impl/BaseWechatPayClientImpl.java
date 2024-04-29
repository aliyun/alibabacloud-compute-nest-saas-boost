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

import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.DateUtil;
import cn.hutool.json.JSONUtil;
import com.alibaba.fastjson2.JSONObject;
import com.alipay.api.AlipayApiException;
import com.ijpay.core.IJPayHttpResponse;
import com.ijpay.core.enums.AuthTypeEnum;
import com.ijpay.core.enums.RequestMethodEnum;
import com.ijpay.core.kit.PayKit;
import com.ijpay.core.kit.WxPayKit;
import com.ijpay.core.utils.DateTimeZoneUtil;
import com.ijpay.wxpay.WxPayApi;
import com.ijpay.wxpay.WxPayApiConfig;
import com.ijpay.wxpay.WxPayApiConfigKit;
import com.ijpay.wxpay.enums.v3.BasePayApiEnum;
import com.ijpay.wxpay.model.v3.Amount;
import com.ijpay.wxpay.model.v3.RefundAmount;
import com.ijpay.wxpay.model.v3.RefundModel;
import com.ijpay.wxpay.model.v3.UnifiedOrderModel;
import java.net.SocketTimeoutException;
import java.security.cert.X509Certificate;
import java.util.HashMap;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.example.common.adapter.BaseWechatPayClient;
import org.example.common.config.WechatPayConfig;
import org.example.common.constant.WechatPayConstants;
import org.example.common.dto.OrderDTO;
import org.example.common.errorinfo.ErrorInfo;
import org.example.common.exception.BizException;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class BaseWechatPayClientImpl implements BaseWechatPayClient {

    private WechatPayConfig wechatPayConfig;

    private String serialNo;

    private static final Integer CLOSE_TRANSACTION_TIME = 15;

    private static final int OK = 200;


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
                    .setMchid(wechatPayConfig.getPid())
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
                    wechatPayConfig.getPid(),
                    getSerialNumber(),
                    null,
                    wechatPayConfig.getKeyPath(),
                    JSONUtil.toJsonStr(unifiedOrderModel),
                    AuthTypeEnum.RSA.getCode()
            );

            if (response.getStatus() == OK) {
                // 根据证书序列号查询对应的证书来验证签名结果
                boolean verifySignature = WxPayKit.verifySignature(response, wechatPayConfig.getPlatformCertPath());
                if (verifySignature) {
                    JSONObject jsonObject = JSONObject.parseObject(response.getBody());
                    return jsonObject.getString(WechatPayConstants.CODE_URL);
                } else {
                    throw new BizException(ErrorInfo.SIG_VERIFY_FAILED.getStatusCode(), ErrorInfo.SIG_VERIFY_FAILED.getCode(),
                            String.format(ErrorInfo.SIG_VERIFY_FAILED.getMessage()));
                }
            }

            return JSONUtil.toJsonStr(response);
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
            params.put("mchid", wechatPayConfig.getPid());

            IJPayHttpResponse response = WxPayApi.v3(
                    RequestMethodEnum.GET,
                    wechatPayConfig.getGateway(),
                    String.format(BasePayApiEnum.ORDER_QUERY_BY_OUT_TRADE_NO.toString(), outTradeNo),
                    wechatPayConfig.getPid(),
                    getSerialNumber(),
                    null,
                    wechatPayConfig.getKeyPath(),
                    params,
                    AuthTypeEnum.RSA.getCode()
            );
            if (response.getStatus() == OK) {
                // 根据证书序列号查询对应的证书来验证签名结果
                boolean verifySignature = WxPayKit.verifySignature(response, wechatPayConfig.getPlatformCertPath());
                if (verifySignature) {
                    return response.getBody();
                } else {
                    throw new BizException(ErrorInfo.SIG_VERIFY_FAILED.getStatusCode(), ErrorInfo.SIG_VERIFY_FAILED.getCode(),
                            String.format(ErrorInfo.SIG_VERIFY_FAILED.getMessage()));
                }
            }
            return JSONUtil.toJsonStr(response);
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
            params.put("mchid", wechatPayConfig.getPid());

            IJPayHttpResponse response = WxPayApi.v3(
                    RequestMethodEnum.GET,
                    wechatPayConfig.getGateway(),
                    String.format(BasePayApiEnum.CLOSE_ORDER_BY_OUT_TRADE_NO.toString(), outTradeNo),
                    wechatPayConfig.getPid(),
                    getSerialNumber(),
                    null,
                    wechatPayConfig.getKeyPath(),
                    params,
                    AuthTypeEnum.RSA.getCode()
            );
            if (response.getStatus() == OK) {
                // 根据证书序列号查询对应的证书来验证签名结果
                boolean verifySignature = WxPayKit.verifySignature(response, wechatPayConfig.getPlatformCertPath());
                if (verifySignature) {
                    return Boolean.TRUE;
                } else {
                    throw new BizException(ErrorInfo.SIG_VERIFY_FAILED.getStatusCode(), ErrorInfo.SIG_VERIFY_FAILED.getCode(),
                            String.format(ErrorInfo.SIG_VERIFY_FAILED.getMessage()));
                }
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
                    .setAmount(new RefundAmount().setRefund(order.getRefundAmount().intValue()).setTotal(order.getTotalAmount().intValue()).setCurrency("CNY"));

            IJPayHttpResponse response = WxPayApi.v3(
                    RequestMethodEnum.POST,
                    wechatPayConfig.getGateway(),
                    BasePayApiEnum.REFUND.toString(),
                    wechatPayConfig.getPid(),
                    getSerialNumber(),
                    null,
                    wechatPayConfig.getKeyPath(),
                    JSONUtil.toJsonStr(refundModel)
            );
            if (response.getStatus() == OK) {
                // 根据证书序列号查询对应的证书来验证签名结果
                boolean verifySignature = WxPayKit.verifySignature(response, wechatPayConfig.getPlatformCertPath());
                if (verifySignature) {
                    return Boolean.TRUE;
                } else {
                    throw new BizException(ErrorInfo.SIG_VERIFY_FAILED.getStatusCode(), ErrorInfo.SIG_VERIFY_FAILED.getCode(),
                            String.format(ErrorInfo.SIG_VERIFY_FAILED.getMessage()));
                }
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
            params.put("mchid", wechatPayConfig.getPid());

            IJPayHttpResponse response = WxPayApi.v3(
                    RequestMethodEnum.GET,
                    wechatPayConfig.getGateway(),
                    String.format(BasePayApiEnum.REFUND_QUERY_BY_OUT_REFUND_NO.toString(), outRefundNo),
                    wechatPayConfig.getPid(),
                    getSerialNumber(),
                    null,
                    wechatPayConfig.getKeyPath(),
                    params,
                    AuthTypeEnum.RSA.getCode()
            );
            if (response.getStatus() == OK) {
                // 根据证书序列号查询对应的证书来验证签名结果
                boolean verifySignature = WxPayKit.verifySignature(response, wechatPayConfig.getPlatformCertPath());
                if (verifySignature) {
                    return response.getBody();
                } else {
                    throw new BizException(ErrorInfo.SIG_VERIFY_FAILED.getStatusCode(), ErrorInfo.SIG_VERIFY_FAILED.getCode(),
                            String.format(ErrorInfo.SIG_VERIFY_FAILED.getMessage()));
                }
            }
            return JSONUtil.toJsonStr(response);
        } catch (Exception e) {
            throw new BizException(ErrorInfo.SERVER_UNAVAILABLE.getStatusCode(), ErrorInfo.SERVER_UNAVAILABLE.getCode(),
                    String.format(ErrorInfo.SERVER_UNAVAILABLE.getMessage(), e));
        }
    }

    private String getSerialNumber() {
        if (StringUtils.isEmpty(serialNo)) {
            // 获取证书序列号
            X509Certificate certificate = PayKit.getCertificate(wechatPayConfig.getCertPath());
            if (null != certificate) {
                serialNo = certificate.getSerialNumber().toString(16).toUpperCase();
                // 提前两天检查证书是否有效
                boolean isValid = PayKit.checkCertificateIsValid(certificate, wechatPayConfig.getPid(), -2);
                log.info("证书是否可用 {} 证书有效期为 {}", isValid, DateUtil.format(certificate.getNotAfter(), DatePattern.NORM_DATETIME_PATTERN));
            }
        }
        return serialNo;
    }

    @Override
    public void createClient(WechatPayConfig wechatPayConfig) throws Exception {
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
            WxPayApiConfig wxPayApiConfig = WxPayApiConfig.builder()
                    .appId(wechatPayConfig.getAppId())
                    .mchId(wechatPayConfig.getPid())
                    .partnerKey(wechatPayConfig.getApiKey())
                    .apiKey3(wechatPayConfig.getApiKey())
                    .keyPath(wechatPayConfig.getKeyPath())
                    .certPath(wechatPayConfig.getCertPath())
                    .platformCertPath(wechatPayConfig.getPlatformCertPath())
                    .build();
            WxPayApiConfigKit.setThreadLocalWxPayApiConfig(wxPayApiConfig);
        } catch (Exception e) {
            log.error("Failed to create WechatPay client with cert. ", e);
        }
    }
}
