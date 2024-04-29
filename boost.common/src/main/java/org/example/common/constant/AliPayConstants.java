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

public interface AliPayConstants {

    /**
     *  Alipay out trade number.
     */
    String OUT_TRADE_NO = "out_trade_no";

    /**
     *  Signature algorithm type.
     */
    String SIGN_TYPE_RSA2 = "RSA2";

    /**
     * QR code.
     */
    String QR_CODE = "qr_code";

    /**
     * Alipay trade query response.
     */
    String ALIPAY_TRADE_PRECREATE_RESPONSE = "alipay_trade_precreate_response";

    /**
     *  Verify transaction success result.
     */
    String VERIFY_SUCCESS_RESULT = "success";

    /**
     *  Verify transaction failed result.
     */
    String VERIFY_FAIL_RESULT = "failure";

    /**
     * Alipay Application ID is the unique identifier for your registered application.
     */
    String OOS_ALIPAY_APP_ID = "AlipayAppId";

    /**
     * Alipay merchant account
     */
    String OOS_ALIPAY_PID = "AlipayPid";

    /**
     * Alipay app private key
     */
    String OOS_SECRET_ALIPAY_PRIVATE_KEY = "AlipayPrivateKey";

    /**
     * Alipay signature method
     */
    String OOS_ALIPAY_SIGNATURE_METHOD = "AlipaySignatureMethod";

    /**
     * Alipay signature of private key
     */
    String OOS_ALIPAY_SIGNATURE_OF_PRIVATE_KEY = "PrivateKey";

    /**d
     * Alipay signature of certificate
     */
    String OOS_ALIPAY_SIGNATURE_OF_CERT = "Certificate";

    /**
     * Alipay official public key
     */
    String OOS_SECRET_ALIPAY_OFFICIAL_PUBLIC_KEY = "AlipayOfficialPublicKey";

    /**
     * Alipay app certificate path
     */
    String OOS_SECRET_ALIPAY_APP_CERT_PATH = "AlipayAppCertPath";

    /**
     * Alipay certificate path
     */
    String OOS_SECRET_ALIPAY_CERT_PATH = "AlipayCertPath";

    /**
     * Alipay root certificate path
     */
    String OOS_SECRET_ALIPAY_ROOT_CERT_PATH = "AlipayRootCertPath";

    /**
     * Alipay gateway
     */
    String OOS_ALIPAY_GATEWAY = "AlipayGateway";

    String OOS_SECRET_ADMIN_AID = "AdminAid";
}
