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

package org.example.common.dataobject;

import lombok.Data;
import org.example.common.constant.PayPeriodUnit;
import org.example.common.constant.PaymentType;
import org.example.common.constant.ProductName;
import org.example.common.constant.TradeStatus;

@Data
public class OrderDO {

    /**
     * Primary key : Id.
     */
    private String id;

    /**
     * The Order ID, which corresponds to the Alipay Out Transaction Number.
     */
    private String orderId;

    /**
     * Alipay transaction number.
     */
    private String tradeNo;

    /**
     * User Id. corresponds to the aliyun uid.
     */
    private Long userId;

    /**
     * Account Id. corresponds to the aliyun aid.
     */
    private Long accountId;

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
    private Double buyerPayAmount;

    /**
     * Seller received amount.
     */
    private Double receiptAmount;

    /**
     * Service name of the purchase, corresponding to Alipay subject.
     */
    private ProductName productName;

    /**
     * Total Amount.
     */
    private Double totalAmount;

    /**
     * Seller Id.
     */
    private String sellerId;

    /**
     * Alipay appid.
     */
    private String appId;

    /**
     * Nest service configs.
     */
    private String productComponents;

    /**
     * Signature.
     */
    private String sign;

    /**
     * Payment type.
     */
    private PaymentType type;

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
    private Double refundAmount;

    /**
     * Compute nest service instance id.
     */
    private String serviceInstanceId;

    /**
     * Pay Period.
     */
    private Long payPeriod;

    /**
     * Pay Period Unit.
     */
    private PayPeriodUnit payPeriodUnit;

    /**
     * Specification Name.
     */
    private String specificationName;
}
