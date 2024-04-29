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

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.example.common.dto.OrderDTO;

public interface PaymentService {
    /**
     * Verifies the trade callback received from the payment channel.
     * @param request http request
     * @return a String indicating the result of the verification
     */
    String verifyTradeCallback(HttpServletRequest request);

    /**
     * Verifies the refund callback received from the payment channel.
     * @param request http request
     * @return a String indicating the result of the verification
     */
    String verifyRefundCallback(HttpServletRequest request);

    /**
     * Verifies the trade callback received from the payment channel.
     * @param request http request
     * @param response http response
     */
    void verifyTradeCallback(HttpServletRequest request, HttpServletResponse response);

    /**
     * Verifies the refund callback received from the payment channel.
     * @param request http request
     * @param response http response
     */
    void verifyRefundCallback(HttpServletRequest request, HttpServletResponse response);

    String createOutTrade(OrderDTO order);


    /**
     * Refunds the specified order.
     * @param order order
     * @return Boolean
     */
    Boolean refundOutTrade(OrderDTO order);

    /**
     * Gets the type of the payment channel.
     *
     * @return a String indicating the type of the payment channel
     */
    String getType();
}
