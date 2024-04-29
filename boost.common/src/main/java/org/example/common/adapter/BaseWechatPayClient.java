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

package org.example.common.adapter;

import org.example.common.config.WechatPayConfig;
import org.example.common.dto.OrderDTO;

public interface BaseWechatPayClient {

    /**
     * Query order.
     * @param outTradeNo out payment trade number
     * @return {@link String}
     */
    String queryOutTrade(String outTradeNo);


    /**
     * Native Pay.
     * @param order order
     * @return {@link String}
     */
    String nativePay(OrderDTO order);

    /**
     * Order Refund.
     * @param order order
     * @return {@link Boolean}
     */
    Boolean refundOutTrade(OrderDTO order);

    /**
     * Query Refund Order.
     * @param outRefundNo Refund Id
     * @return {@link String}
     */
    String queryRefundOutTrade(String outRefundNo);

    /**
     * Close order.
     * @param outTradeNo Order Id
     * @return {@link Boolean}
     */
    Boolean closeOutTrade(String outTradeNo);

    /**
     * Create WechatPay Client.
     * @param wechatPayConfig wechat pay config
     * @throws Exception Common Exception
     */
    void createClient(WechatPayConfig wechatPayConfig) throws Exception;

    /**
     * Update WechatPay Client.
     * @param parameterName  parameter name
     * @param value value
     * @throws Exception Common Exception
     */
    void updateClient(String parameterName, String value) throws Exception;
}
