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

package org.example.common.constant;

public enum TradeStatus {

    /**
     * The actions associated with this status are as follows:
     *  1. Unpaid transaction timeout closure
     *  2. full refund after payment is completed
     */
    TRADE_CLOSED("交易关闭"),

    /**
     * Transaction payment successfully completed.
     */
    TRADE_SUCCESS("支付成功"),

    /**
     * Transaction created, waiting for buyer's payment.
     */
    WAIT_BUYER_PAY("交易创建"),

    /**
     * Transaction ended, non-refundable.
     */
    TRADE_FINISHED( "交易完结"),

    /**
     * Refunded.
     */
    REFUNDED("已经退款"),

    /**
     * Refund in progress, awaiting processing by Function Compute.
     */
    REFUNDING("退款中");

    private final String displayName;

    TradeStatus(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    @Override
    public String toString() {
        return name();
    }
}

