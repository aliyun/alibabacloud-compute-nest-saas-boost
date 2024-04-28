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

public interface WechatConstants {

    /**
     *  Wechat out trade number.
     */
    String OUT_TRADE_NO = "out_trade_no";

    /**
     *  Wechat trade number.
     */
    String TRADE_NO = "trade_no";

    /**
     *  Wechat trade total amount.
     */
    String TOTAL_AMOUNT = "total_amount";

    /**
     *  Wechat trade refund amount.
     */
    String REFUND_AMOUNT = "refund_amount";

    /**
     *  Wechat trade refund request id.
     */
    String REFUND_REQUEST_ID = "out_request_no";

    /**
     *  Wechat trade subject.
     */
    String SUBJECT = "subject";

    /**
     *  Wechat product code.
     */
    String PRODUCT_CODE_PREFIX = "product_code";

    /**
     * Wechat trade status prefix.
     */
    String TRADE_STATUS = "trade_status";

    /**
     *  Encrypted signature for asynchronous payment query.
     */
    String SIGN = "sign";

    /**
     *  Wechat Seller Id.
     */
    String SELLER_ID = "seller_id";

    /**
     *  Wechat app id.
     */
    String APP_ID = "app_id";

    /**
     *  Payment sales product code, currently Wechat only supports FAST_INSTANT_TRADE_PAY.
     */
    String PRODUCT_CODE_PC_WEB = "FAST_INSTANT_TRADE_PAY";

    /**
     *  Signature algorithm type.
     */
    String SIGN_TYPE_RSA2 = "RSA2";

    /**
     *  Expired time.
     */
    String TIME_EXPIRE = "time_expire";

    /**
     *  Verify transaction success result.
     */
    String VERIFY_SUCCESS_RESULT = "success";

    /**
     *  Verify transaction failed result.
     */
    String VERIFY_FAIL_RESULT = "failure";

    /**
     * Wechat Application ID is the unique identifier for your registered application.
     */
    String OOS_SECRET_WECHAT_APP_ID = "WechatAppId";

    /**
     * Wechat merchant account
     */
    String OOS_SECRET_WECHAT_PID = "WechatPid";

    /**
     * Wechat app private key
     */
    String OOS_SECRET_WECHAT_PRIVATE_KEY = "WechatPrivateKey";

    /**
     * Wechat official public key
     */
    String OOS_SECRET_WECHAT_OFFICIAL_PUBLIC_KEY = "WechatOfficialPublicKey";

    String OOS_SECRET_ADMIN_AID = "AdminAid";
}
