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

package org.example.service;

import com.alipay.api.AlipayApiException;

import javax.servlet.http.HttpServletRequest;

public interface AlipayService {

    /**
     * Consuming Alipay notification messages
     * @param request HttpServletRequest
     * @return {@link String}
     */
    String verifyTradeCallback(HttpServletRequest request);

    /**
     * Create Alipay transaction event.
     * @param totalAmount total amount.
     * @param subject subject of order.
     * @param outTradeNo out trade number
     * @return {@link String}
     * @throws AlipayApiException api exception
     */
    String createTransaction(Double totalAmount, String subject, String outTradeNo) throws AlipayApiException;

    /***
     * Refund order.
     * @param orderId order id.
     * @param refundAmount refund amount.
     * @param refundId refund id.
     * @return {@link Boolean}
     */
    Boolean refundOrder(String orderId, Double refundAmount, String refundId);
}
