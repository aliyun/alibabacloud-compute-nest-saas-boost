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

public enum PayChannel {

    /**
     * Alipay
     */
    ALIPAY("Alipay", "0"),

    /**
     * Wechat pay v3
     */
    WECHATPAY("WechatPay", "1"),

    /**
     * PayPal
     */
    PAYPAL("PayPal", "2"),

    /**
     * credit card
     */
    CREDIT_CARD("Credit Card", "3"),

    /**
     * Pay as you go
     */
    PAY_POST("Pay Post", "4");

    private final String displayName;

    private final String value;

    PayChannel(String displayName, String value) {
        this.displayName = displayName;
        this.value = value;
    }

    PayChannel(String displayName) {
        this.displayName = displayName;
        this.value = "0";
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getValue() {
        return value;
    }
}

