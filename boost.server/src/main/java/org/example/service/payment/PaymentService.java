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
package org.example.service.payment;

import org.example.common.dataobject.OrderDO;

import java.math.BigDecimal;
import java.util.Map;

public interface PaymentService {
    /**
     * Verifies the trade callback received from the payment channel.
     * @param unverifiedOrder the order to be verified
     * @param map the request body from the payment channel
     * @return a String indicating the result of the verification
     */
    String verifyTradeCallback(OrderDO unverifiedOrder, Map<String, String> map);

    /**
     * Creates a new transaction with the specified details in the payment channel.
     *
     * @param totalAmount the total amount for the transaction
     * @param subject     the subject or description of the transaction
     * @param outTradeNo  the external trade number for reference
     * @return a String representing the transaction identifier or reference
     */
    String createTransaction(BigDecimal totalAmount, String subject, String outTradeNo);

    /**
     * Processes a refund for a specific order in the payment channel.
     *
     * @param orderId      the identifier of the order to be refunded
     * @param refundAmount the amount to be refunded
     * @param refundId     the identifier for the refund transaction
     * @return a Boolean indicating whether the refund was successful
     */
    Boolean refundOrder(String orderId, BigDecimal refundAmount, String refundId);

    /**
     * Gets the type of the payment channel.
     *
     * @return a String indicating the type of the payment channel
     */
    String getType();
}
