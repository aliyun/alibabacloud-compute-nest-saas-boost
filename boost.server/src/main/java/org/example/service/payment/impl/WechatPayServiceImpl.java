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

import cn.hutool.http.ContentType;
import cn.hutool.json.JSONUtil;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.ijpay.core.kit.HttpKit;
import com.ijpay.core.kit.WxPayKit;
import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.math3.util.Precision;
import org.example.common.BaseResult;
import org.example.common.adapter.BaseWechatPayClient;
import org.example.common.config.WechatPayConfig;
import org.example.common.constant.AliPayConstants;
import org.example.common.constant.PayChannel;
import org.example.common.constant.TradeStatus;
import org.example.common.constant.WechatPayConstants;
import org.example.common.dataobject.OrderDO;
import org.example.common.dto.OrderDTO;
import org.example.common.errorinfo.ErrorInfo;
import org.example.common.exception.BizException;
import org.example.common.helper.ots.OrderOtsHelper;
import org.example.common.model.UserInfoModel;
import org.example.common.param.order.GetOrderParam;
import org.example.service.order.OrderService;
import org.example.service.payment.PaymentService;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class WechatPayServiceImpl implements PaymentService {

    @Resource
    private WechatPayConfig wechatPayConfig;

    @Resource
    private OrderService orderService;

    @Resource
    private BaseWechatPayClient baseWechatPayClient;

    @Resource
    private OrderOtsHelper orderOtsHelper;

    @Override
    public void verifyTradeCallback(HttpServletRequest request, HttpServletResponse response) {
        Map<String, String> responseMap = new HashMap<>(12);
        try {
            String timestamp = request.getHeader("Wechatpay-Timestamp");
            String nonce = request.getHeader("Wechatpay-Nonce");
            String serialNo = request.getHeader("Wechatpay-Serial");
            String signature = request.getHeader("Wechatpay-Signature");
            String result = HttpKit.readData(request);
            // 需要通过证书序列号查找对应的证书，verifyNotify 中有验证证书的序列号
            String plainText = WxPayKit.verifyNotify(serialNo, result, signature, nonce, timestamp,
                    wechatPayConfig.getApiKey(), wechatPayConfig.getPlatformCertPath());
            if (StringUtils.isEmpty(plainText)) {
                verifyFail(response, responseMap);
                return;
            }

            Gson gson = new Gson();
            Type mapType = new TypeToken<Map<String, String>>(){}.getType();
            Map<String, String> plainMap = gson.fromJson(plainText, mapType);
            String orderId = plainMap.get(AliPayConstants.OUT_TRADE_NO);
            OrderDTO orderFromOts = Optional.ofNullable(orderService.getOrder(null, new GetOrderParam(orderId)))
                    .map(BaseResult::getData)
                    .orElse(null);
            if (orderFromOts == null || orderFromOts.getTradeStatus() == null) {
                verifyFail(response, responseMap);
                return;
            }

            String tradeStatus = orderFromOts.getTradeStatus().name();
            if (Arrays.asList(TradeStatus.TRADE_SUCCESS.name(), TradeStatus.TRADE_FINISHED.name()).contains(tradeStatus)) {
                verifySuccess(response);
                return;
            }

            if (verifyBusinessData(plainMap, orderFromOts) && "SUCCESS".equals(plainMap.get(WechatPayConstants.TRADE_STATUS))) {
                OrderDO verifiedOrder = new OrderDO();
                verifiedOrder.setTradeStatus(TradeStatus.TRADE_SUCCESS);
                UserInfoModel userInfoModel = new UserInfoModel();
                userInfoModel.setAid(String.valueOf(orderFromOts.getAccountId()));
                orderService.updateOrder(userInfoModel, verifiedOrder);
                verifySuccess(response);
                return;
            }
            verifyFail(response, responseMap);
        } catch (IOException e) {
            throw new BizException(ErrorInfo.UNKNOWN_ERROR, e);
        } catch (Exception e) {
            throw new BizException(ErrorInfo.SIG_VERIFY_FAILED, e);
        }
    }

    @Override
    public void verifyRefundCallback(HttpServletRequest request, HttpServletResponse response) {
        Map<String, String> responseMap = new HashMap<>(12);
        try {
            String timestamp = request.getHeader("Wechatpay-Timestamp");
            String nonce = request.getHeader("Wechatpay-Nonce");
            String serialNo = request.getHeader("Wechatpay-Serial");
            String signature = request.getHeader("Wechatpay-Signature");
            String result = HttpKit.readData(request);
            // 需要通过证书序列号查找对应的证书，verifyNotify 中有验证证书的序列号
            String plainText = WxPayKit.verifyNotify(serialNo, result, signature, nonce, timestamp,
                    wechatPayConfig.getApiKey(), wechatPayConfig.getPlatformCertPath());
            if (StringUtils.isEmpty(plainText)) {
                verifyFail(response, responseMap);
                return;
            }

            Gson gson = new Gson();
            Type mapType = new TypeToken<Map<String, String>>(){}.getType();
            Map<String, String> plainMap = gson.fromJson(plainText, mapType);
            String orderId = plainMap.get(AliPayConstants.OUT_TRADE_NO);
            OrderDTO orderFromOts = Optional.ofNullable(orderService.getOrder(null, new GetOrderParam(orderId)))
                    .map(BaseResult::getData)
                    .orElse(null);

            if (orderFromOts == null || orderFromOts.getTradeStatus() == null) {
                verifyFail(response, responseMap);
                return;
            }
            String tradeStatus = orderFromOts.getTradeStatus().name();
            if (TradeStatus.REFUNDED.name().equals(tradeStatus)) {
                verifySuccess(response);
                return;
            }
            if (verifyRefundData(plainMap, orderFromOts) && "SUCCESS".equals(plainMap.get(WechatPayConstants.REFUND_STATUS))) {
                OrderDO verifiedOrder = new OrderDO();
                verifiedOrder.setTradeStatus(TradeStatus.REFUNDED);
                UserInfoModel userInfoModel = new UserInfoModel();
                userInfoModel.setAid(String.valueOf(orderFromOts.getAccountId()));
                orderService.updateOrder(userInfoModel, verifiedOrder);
                verifySuccess(response);
                return;
            }
            verifyFail(response, responseMap);
        } catch (IOException e) {
            throw new BizException(ErrorInfo.UNKNOWN_ERROR, e);
        } catch (Exception e) {
            throw new BizException(ErrorInfo.SIG_VERIFY_FAILED, e);
        }
    }

    @Override
    public String verifyTradeCallback(HttpServletRequest request) {
        throw new BizException(ErrorInfo.UNKNOWN_ERROR);
    }

    @Override
    public String verifyRefundCallback(HttpServletRequest request) {
        throw new BizException(ErrorInfo.UNKNOWN_ERROR);
    }

    @Override
    public String createOutTrade(OrderDTO order) {
        if (StringUtils.isNotEmpty(order.getPaymentForm())) {
            return order.getPaymentForm();
        }
        String transaction = baseWechatPayClient.nativePay(order);
        OrderDO orderDO = new OrderDO();
        orderDO.setOrderId(order.getOrderId());
        orderDO.setPayChannel(PayChannel.WECHATPAY);
        orderDO.setPaymentForm(transaction);
        orderOtsHelper.updateOrder(orderDO);
        return transaction;
    }

    @Override
    public Boolean refundOutTrade(OrderDTO order) {
        return baseWechatPayClient.refundOutTrade(order);
    }

    @Override
    public String getType() {
        return PayChannel.WECHATPAY.getValue();
    }

    private Boolean verifyBusinessData(Map<String, String> unverifiedOrder, OrderDTO orderFromTableStore) {
        if (orderFromTableStore.getTotalAmount() == null) {
            return Boolean.FALSE;
        }
        return unverifiedOrder.get(WechatPayConstants.P_ID).equals(wechatPayConfig.getPid())
                && unverifiedOrder.get(WechatPayConstants.APP_ID).equals(wechatPayConfig.getAppId())
                && Precision.equals(orderFromTableStore.getTotalAmount().intValue(),  Integer.parseInt(unverifiedOrder.get(WechatPayConstants.TOTAL_AMOUNT)));
    }

    private Boolean verifyRefundData(Map<String, String> unverifiedOrder, OrderDTO orderFromTableStore) {
        if (orderFromTableStore.getTotalAmount() == null) {
            return Boolean.FALSE;
        }
        return unverifiedOrder.get(WechatPayConstants.P_ID).equals(wechatPayConfig.getPid())
                && unverifiedOrder.get(WechatPayConstants.APP_ID).equals(wechatPayConfig.getAppId())
                && Precision.equals(orderFromTableStore.getTotalAmount().intValue(),  Integer.parseInt(unverifiedOrder.get(WechatPayConstants.TOTAL_AMOUNT)))
                && Precision.equals(orderFromTableStore.getRefundAmount().intValue(),  Integer.parseInt(unverifiedOrder.get(WechatPayConstants.REFUND_AMOUNT)));
    }

    private void verifySuccess(HttpServletResponse response) throws IOException {
        response.setStatus(HttpServletResponse.SC_OK); // 设置成功状态码
        response.setHeader("Content-type", ContentType.JSON.toString());
        response.flushBuffer(); // 清空响应缓冲区发送响应
    }

    private void verifyFail(HttpServletResponse response, Map<String, String> responseMap) throws IOException {
        response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR); // 设置错误状态码
        responseMap.put("code", "ERROR");
        responseMap.put("message", "签名错误");
        response.setHeader("Content-type", ContentType.JSON.toString());
        response.getOutputStream().write(JSONUtil.toJsonStr(responseMap).getBytes(StandardCharsets.UTF_8));
        response.flushBuffer(); // 清空响应缓冲区发送响应
    }
}