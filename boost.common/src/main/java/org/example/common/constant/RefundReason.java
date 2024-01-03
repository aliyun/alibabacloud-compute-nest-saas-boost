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
public enum RefundReason {

    /**
     * 订单主动退款
     */
    ORDER_CANCELLATION_REFUND("OrderCancellationRefund", "Order refund, orderId = %s." ),

    /**
     * 服务实例删除退款
     */
    SERVICE_INSTANCE_DELETION_REFUND("ServiceInstanceDeletionRefund", "Service instance deletion, serviceInstanceId = %s, orderId = %s." ),

    /**
     * 服务实例创建失败退款
     */
    SERVICE_INSTANCE_CREATION_FAILURE_REFUND("ServiceInstanceCreationFailureRefund", "Service instance creation failure. orderId = %s." );

    private final String displayName;

    private final String defaultMessage;

    RefundReason(String displayName, String defaultMessage) {
        this.displayName = displayName;
        this.defaultMessage = defaultMessage;
    }

    public String getDefaultMessage() {
        return defaultMessage;
    }
}
