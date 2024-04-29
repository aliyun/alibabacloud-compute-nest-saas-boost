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

public interface Constants {

    /**
     * OIDC grant type.
     */
    String GRANT_TYPE = "grant_type";

    /**
     * grant type - authorization code.
     */
    String GRANT_TYPE_AUTHORIZATION = "authorization_code";

    /**
     * OIDC authentication code.
     */
    String CODE = "code";

    /**
     * OIDC client id.
     */
    String CLIENT_ID = "client_id";

    /**
     * OIDC client secret.
     */
    String CLIENT_SECRET = "client_secret";

    /**
     * Redirect URI after successful OIDC authentication.
     */
    String REDIRECT_URI = "redirect_uri";

    /**
     * OIDC authentication nonce.
     */
    String STATE = "state";

    /**
     * Session state for server-side authentication.
     */
    String SESSION_STATE = "session_state";

    /**
     * OIDC authentication scopes, such as openid, aliuid.
     */
    String SCOPE = "scope";

    /**
     * OIDC authentication scope - openid.
     */
    String SCOPE_OPENID = "openid aliuid profile";

    /**
     * File encoding: utf-8.
     */
    String TRANSFORMATION_FORMAT_UTF_8 = "UTF-8";

    /**
     * Json format.
     */
    String JSON_FORMAT = "json";

    /**
     * Set the character encoding and media type of the response to JSON with UTF-8 encoding.
     */
    String STANDARD_CONTENT_TYPE = "application/json;charset=UTF-8";

    /**
     * Request id.
     */
    String REQUEST_ID = "requestId";

    /**
     * Authorization header.
     */
    String AUTHORIZATION = "Authorization";

    /**
     * Fc temporary access key id.
     */
    String FC_ACCESS_KEY_ID = "x-fc-access-key-id";

    /**
     * Fc temporary access key secret.
     */
    String FC_ACCESS_KEY_SECRET = "x-fc-access-key-secret";

    /**
     * Fc temporary security token.
     */
    String FC_SECURITY_TOKEN = "x-fc-security-token";

    /**
     * Aliyun OAuth client id.
     */
    String OAUTH_CLIENT_ID = "OAuthClientId";

    /**
     * Aliyun OAuth client secret.
     */
    String OAUTH_CLIENT_SECRET = "OAuthClientSecret";

    /**
     * Compute nest service instance id.
     */
    String SERVICE_INSTANCE_ID = "ServiceInstanceId";

    /**
     * Saas Boost.
     */
    String SAAS_BOOST = "saas-boost";

    /**
     * Oss bucket.
     */
    String BUCKET = "bucket";

    String ALIPAY_TAG = "Alipay";

    String WECHATPAY_TAG = "WechatPay";
}
