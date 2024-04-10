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

import com.alipay.api.response.AlipayTradeQueryResponse;
import org.example.common.config.AlipayConfig;

import java.math.BigDecimal;

public interface BaseAlipayClient {

    /**
     * Query order.
     * @param outTradeNumber out payment trade number
     * @return AlipayTradeQueryResponse
     */
    AlipayTradeQueryResponse queryOutTrade(String outTradeNumber);

    /**
     * Verify if the signature is correct.
     * @param sign signature
     * @param content seller_id...
     * @return boolean
     */
    boolean verifySignature(String sign, String content);

    /**
     * Create transaction.
     * @param totalAmount total amount money
     * @param subject payment subject
     * @param outTradeNo out trade number
     * @return String
     */
    String createTransaction(BigDecimal totalAmount, String subject, String outTradeNo);

    /**
     * Order Refund.
     * @param orderId Order Id
     * @param refundAmount Refund Amount
     * @param refundRequestId Refund Request Id
     * @return {@link Boolean}
     */
    Boolean refundOrder(String orderId, BigDecimal refundAmount, String refundRequestId);

    /**
     * Close order.
     * @param orderId Order Id
     * @return {@link Boolean}
     */
    Boolean closeOrder(String orderId);

    /**
     * Create Alipay Client.
     * @param alipayConfig alipay config
     * @throws Exception Common Exception
     */
    void createClient(AlipayConfig alipayConfig) throws Exception;
}
