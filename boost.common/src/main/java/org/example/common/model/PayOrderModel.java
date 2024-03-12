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

package org.example.common.model;

import org.example.common.constant.PayChannel;

public class PayOrderModel {

    /**
     * Product total amount
     */
    private Double totalAmount;

    /**
     * Product name
     */
    private String subject;

    /**
     * Product description summary
     */
    private String body;

    /**
     * Product out trade number
     */
    private String outTradeNo;

    /**
     * Payment type
     */
    private PayChannel payChannel;
}
