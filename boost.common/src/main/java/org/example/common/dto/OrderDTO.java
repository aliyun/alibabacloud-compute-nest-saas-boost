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

package org.example.common.dto;

import lombok.Data;
import org.example.common.constant.PayChannel;
import org.example.common.constant.PayPeriodUnit;
import org.example.common.constant.TradeStatus;

@Data
public class OrderDTO {

    /**
     * The Order ID, which corresponds to the Alipay Out Transaction Number.
     */
    private String orderId;

    /**
     * Transaction status.
     */
    private TradeStatus tradeStatus;

    /**
     * Transaction creation time.
     */
    private String gmtCreate;

    /**
     * alipay : subject
     */
    private String commodityName;

    /**
     * commodity code
     */
    private String commodityCode;

    /**
     * Desc:nest service configs
     */
    private String productComponents;

    /**
     * Total Amount.
     */
    private Long totalAmount;

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
     * Compute nest service instance id.
     */
    private String serviceInstanceId;

    /**
     * Transaction payment time.
     */
    private String gmtPayment;

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

    /**
     * Seller received amount.
     */
    private Long receiptAmount;

    /**
     * Account Id. corresponds to the aliyun aid.
     */
    private Long accountId;

    /**
     * billing start date millis.
     */
    private Long billingStartDateMillis;

    /**
     * billing end date millis.
     */
    private Long billingEndDateMillis;

    /**
     * refund detail.
     */
    private String refundDetail;


    /**
     * payment form.
     */
    private String paymentForm;

    /**
     * service id.
     */
    private String serviceId;

    /**
     * order type.
     */
    private String orderType;
}
