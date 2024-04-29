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
import org.example.common.config.BoostAlipayConfig;
import org.example.common.dto.OrderDTO;

public interface BaseAlipayClient {

    /**
     * Query order.
     * @param outTradeNo out payment trade number
     * @return AlipayTradeQueryResponse
     */
    AlipayTradeQueryResponse queryOutTrade(String outTradeNo, Boolean certModel);

    /**
     * Verify if the signature is correct.
     * @param sign signature
     * @param content seller_id...
     * @return boolean
     */
    boolean verifySignatureWithKey(String sign, String content);

    /**
     * Verify if the signature is correct.
     * @param sign signature
     * @param content seller_id...
     * @return boolean
     */
    boolean verifySignatureWithCert(String sign, String content);

    /**
     * Create order.
     * @param order order
     * @return String
     */
    String createOutTrade(OrderDTO order);

    /**
     * Order Refund.
     * @param order order
     * @return {@link Boolean}
     */
    Boolean refundOutTrade(OrderDTO order);

    /**
     * Close order.
     * @param orderId Order Id
     * @return {@link Boolean}
     */
    Boolean closeOutTrade(String orderId);

    /**
     * Create Alipay Client.
     * @param boostAlipayConfig alipay config
     * @throws Exception Common Exception
     */
    void createClient(BoostAlipayConfig boostAlipayConfig) throws Exception;

    /**
     * Update Alipay Client.
     * @param parameterName parameter name, value is parameter value
     * @throws Exception Common Exception
     */
    void updateClient(String parameterName, String value) throws Exception;
}
