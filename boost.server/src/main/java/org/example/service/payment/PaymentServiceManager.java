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
import org.example.common.constant.AliPayConstants;
import org.example.common.constant.PayChannel;
import org.example.common.dto.OrderDTO;
import org.example.common.helper.ots.OrderOtsHelper;
import org.example.common.model.UserInfoModel;
import org.example.common.param.payment.CreateTransactionParam;
import org.example.common.utils.HttpUtil;
import org.example.service.payment.impl.WechatPayServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


@Component
@Slf4j
public class PaymentServiceManager {

    private Map<String, PaymentService> payChannelServiceMap = new HashMap<>();

    private WechatPayServiceImpl wechatPayServiceImpl;

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

//    public String verifyTradeCallback(HttpServletRequest request) {
//        Map<String, String> params;
//
//        // 检查请求的Content-Type，来决定解析策略
//        String contentType = request.getContentType();
//        if (contentType != null && contentType.contains("text/xml")) {
//            // 解析微信支付的XML通知
//            // 获取XML格式的内容
//            String xmlMsg = HttpKit.readData(request);
//            params = WxPayKit.xmlToMap(xmlMsg);
//
//            if(WxPayKit.codeIsOk(params.get(WechatPayV2Constants.RETURN_CODE))) {
//                return getPayChannelService(params.get(WechatPayV2Constants.OUT_TRADE_NO)).verifyTradeCallback(request);
//            }
//        } else {
//            // 解析支付宝支付的Form或JSON通知
//            // 将request参数转换为Map
//            params = HttpUtil.requestToMap(request);
//            return getPayChannelService(params.get(AliPayConstants.OUT_TRADE_NO)).verifyTradeCallback(request);
//        }
//
//        return null;
//    }
//
//    public String verifyRefundCallback(HttpServletRequest request){
//        Map<String, String> params;
//        String contentType = request.getContentType();
//        if (contentType != null && contentType.contains("text/xml")) {
//            // 解析微信支付的XML通知
//            // 获取XML格式的内容
//            String xmlMsg = HttpKit.readData(request);
//            params = WxPayKit.xmlToMap(xmlMsg);
//
//            if(WxPayKit.codeIsOk(params.get(WechatPayV2Constants.RETURN_CODE))) {
//                return getPayChannelService(params.get(WechatPayV2Constants.OUT_TRADE_NO)).verifyTradeCallback(request);
//            }
//        } else {
//            params = HttpUtil.requestToMap(request);
//            return getPayChannelService(params.get(AliPayConstants.OUT_TRADE_NO)).verifyRefundCallback(request);
//        }
//        return null;
//    }

    public String verifyTradeCallback(HttpServletRequest request, HttpServletResponse response) {
        if (response == null) {
            payChannelServiceMap.get(PayChannel.WECHATPAY.getValue()).verifyTradeCallback(request, response);
            return null;
        } else {
            Map<String, String> params = HttpUtil.requestToMap(request);
            return getPayChannelService(params.get(AliPayConstants.OUT_TRADE_NO)).verifyTradeCallback(request);
        }
    }

    public String verifyRefundCallback(HttpServletRequest request, HttpServletResponse response) {
        if (response == null) {
            payChannelServiceMap.get(PayChannel.WECHATPAY.getValue()).verifyRefundCallback(request, response);
            return null;
        } else {
            Map<String, String> params = HttpUtil.requestToMap(request);
            return getPayChannelService(params.get(AliPayConstants.OUT_TRADE_NO)).verifyRefundCallback(request);
        }
    }

    public String createTransaction(UserInfoModel userInfoModel, CreateTransactionParam param) {
        OrderDTO order = orderOtsHelper.getOrder(param.getOrderId(), Long.parseLong(userInfoModel.getAid()));
        return payChannelServiceMap.get(param.getPayChannel().getValue()).createOutTrade(order);
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