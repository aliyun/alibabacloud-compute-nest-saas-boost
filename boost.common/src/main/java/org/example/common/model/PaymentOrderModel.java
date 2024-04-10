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
package org.example.common.model;

import lombok.Data;
import org.example.common.constant.PayChannel;
import org.example.common.constant.PayPeriodUnit;
import org.example.common.constant.TradeStatus;

import java.io.Serializable;

@Data
public class PaymentOrderModel implements Serializable {

    private static final long serialVersionUID = 8570027662002343560L;

    /**
     * The Order ID, which corresponds to the Alipay Out Transaction Number.
     */
    private String outTradeNo;

    /**
     * Alipay transaction number.
     */
    private String tradeNo;

    /**
     * User Id. corresponds to the aliyun uid.
     */
    private Long userId;

    /**
     * Transaction status.
     */
    private TradeStatus tradeStatus;

    /**
     * Transaction creation time.
     */
    private String gmtCreate;

    /**
     * Transaction creation time in long timestamp format.
     */
    private Long gmtCreateLong;

    /**
     * Transaction payment time.
     */
    private String gmtPayment;

    /**
     * Buyer payment amount.
     */
    private String buyerPayAmount;

    /**
     * Seller received amount.
     */
    private String receiptAmount;

    /**
     * Total Amount.cents
     */
    private String totalAmount;

    /**
     * Seller Id.
     */
    private String sellerId;

    /**
     * Alipay appid.
     */
    private String appId;

    /**
     * Signature.
     */
    private String sign;

    /**
     * Payment type.
     */
    private PayChannel payChannel;

    /**
     * Refund ID, can only be created once.
     */
    private String refundId;

    /**
     * Refund Date.
     */
    private String refundDate;

    /**
     * Refund Amount.
     */
    private Long refundAmount;

    /**
     * Pay Period.
     */
    private Long payPeriod;

    /**
     * Pay Period Unit.
     */
    private PayPeriodUnit payPeriodUnit;

    /**
     * payment form.
     */
    private String paymentForm;
}
