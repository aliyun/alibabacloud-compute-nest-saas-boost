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

import java.util.concurrent.Future;

public interface OrderFcService {
    /**
     * Batch scan table store orders and close timeout orders.
     * @return {@link Future}
     */
    Future<?> closeExpiredOrders();

    /**
     * Batch scan table store orders and refunds.
     * @return {@link Future}
     */
    Future<?> refundOrders();

    /**
     * Batch scan table store consumed orders and close.
     * @return {@link Future}
     */
    Future<?> closeFinishedOrders();
}
