/*
*Copyright (c) Alibaba Group, Inc. or its affiliates. All Rights Reserved.
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

package org.example.common.constant;

public interface WechatPayConstants {

    /**
     * WechatPay request id.
     */
    String REQUEST_ID = "id";

    /**
     *  WechatPay PID
     */
    String MCH_ID = "mchid";

    /**
     *  WechatPay amount.
     */
    String AMOUNT = "amount";

    /**
     *  WechatPay trade total amount.
     */
    String TOTAL_AMOUNT = "total";

    /**
     *  WechatPay trade refund amount.
     */
    String REFUND_AMOUNT = "refund";

    /**
     *  WechatPay trade refund status.
     */
    String REFUND_STATE = "refund_state";

    /**
     *  Verify transaction success result.
     */
    String VERIFY_SUCCESS_RESULT = "success";

    /**
     *  Verify transaction failed result.
     */
    String VERIFY_FAIL_RESULT = "failure";

    /**
     * WechatPay QR code url.
     */
    String CODE_URL = "code_url";

    /**
     * WechatPay encrypted data.
     */
    String REQ_INFO = "req_info";

    /**
     *  WechatPay out trade number.
     */
    String OUT_TRADE_NO = "out_trade_no";

    /**
     * WechatPay trade status prefix.
     */
    String TRADE_STATE = "trade_state";

    /**
     * WechatPay gmt payment
     */
    String GMT_PAYMENT = "gmtPayment";

    /**
     *  WechatPay refund date.
     */
    String REFUND_DATE = "refundDate";

    /**
     *  WechatPay app id.
     */
    String APP_ID = "appid";

    /**
     * WechatPay Pay Application ID
     */
    String OOS_WECHATPAY_APP_ID = "WechatPayAppId";

    /**
     * WechatPay Pay Merchant ID
     */
    String OOS_WECHATPAY_MCH_ID = "WechatPayMchId";

    /**
     * WechatPay Pay API Key
     */
    String OOS_SECRET_WECHATPAY_APIV3_KEY = "WechatPayApiV3Key";

    /**
     * WechatPay Pay Merchant Serial No
     */
    String OOS_SECRET_WECHATPAY_MCH_SERIAL_NO = "WechatPayMchSerialNo";

    /**
     * WechatPay Pay Merchant Key file path
     */
    String OOS_SECRET_WECHATPAY_PRIVATE_KEY_PATH = "WechatPayPrivateKeyPath";

    /**
     * WechatPay Pay Gateway URL
     */
    String OOS_WECHATPAY_GATEWAY = "WechatPayGateway";

    /**
     * Content Type for response
     */
    String CONTENT_TYPE = "Content-type";

    String CODE = "code";

    String ERROR = "ERROR";

    String MESSAGE = "message";

    String RESOURCE = "resource";

    /**
     * Key for ciphertext in resource map
     */
    String RESOURCE_CIPHERTEXT = "ciphertext";

    /**
     * Key for nonce in resource map
     */
    String RESOURCE_NONCE = "nonce";

    /**
     * Key for associated data in resource map
     */
    String RESOURCE_ASSOCIATED_DATA = "associated_data";
}