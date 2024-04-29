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
     *  WechatPay out trade number.
     */
    String OUT_TRADE_NO = "out_trade_no";

    /**
     *  WechatPay return code
     */
    String RETURN_CODE = "return_code";

    /**
     * WechatPay return message
     */
    String RETURN_MSG = "return_msg";

    /**
     *  WechatPay result code
     */
    String RESULT_CODE = "result_code";

    /**
     *  WechatPay PID
     */
    String P_ID = "mch_id";

    /**
     *  WechatPay trade total amount.
     */
    String TOTAL_AMOUNT = "total_fee";

    /**
     *  WechatPay trade refund amount.
     */
    String REFUND_AMOUNT = "refund_fee";

    /**
     *  WechatPay trade refund status.
     */
    String REFUND_STATUS = "refund_status";

    /**
     * WechatPay error code.
     */
    String ERR_CODE = "err_code";

    /**
     * WechatPay error description.
     */
    String ERR_CODE_DES = "err_code_des";

    /**
     * WechatPay QR code url.
     */
    String CODE_URL = "code_url";

    /**
     * WechatPay encrypted data.
     */
    String REQ_INFO = "req_info";

    /**
     * WechatPay trade status prefix.
     */
    String TRADE_STATUS = "trade_status";

    /**
     *  WechatPay app id.
     */
    String APP_ID = "app_id";

    /**
     * WechatPay Pay Application ID
     */
    String OOS_WECHATPAY_APP_ID = "WechatPayAppId";

    /**
     * WechatPay Pay Merchant ID
     */
    String OOS_WECHATPAY_PID = "WechatPayPid";

    /**
     * WechatPay Pay API Key
     */
    String OOS_SECRET_WECHATPAY_API_KEY = "WechatPayApiKey";

    /**
     * WechatPay Pay Merchant Key file path
     */
    String OOS_SECRET_WECHATPAY_APP_CERT_PATH = "WechatPayAppCertPath";

    /**
     * WechatPay Pay Merchant Cert file path
     */
    String OOS_SECRET_WECHATPAY_CERT_PATH = "WechatPayCertPath";

    /**
     * WechatPay Pay Platform Cert file path
     */
    String OOS_SECRET_WECHATPAY_PLATFORM_CERT_PATH = "WechatPayPlatformCertPath";

    /**
     * WechatPay Pay Gateway URL
     */
    String OOS_WECHATPAY_GATEWAY = "WechatPayGateway";
}