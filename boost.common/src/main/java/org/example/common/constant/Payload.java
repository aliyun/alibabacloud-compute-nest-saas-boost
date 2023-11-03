/*
*Copyright (c) Alibaba Group, Inc. or its affiliates. All Rights Reserved.
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

import org.example.common.utils.JsonUtil;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Payload {

    /**
     * Payload type
     */
    private PayloadType type;

    /**
     * The actual method to be called is determined by the payload name.
     * CLOSE_EXPIRED_ORDERS : null
     * REFUND_ORDER : orderId
     * DELETE_SERVICE_INSTANCES : ServiceInstanceIds
     */
    private String data;

    @Override
    public String toString(){
        return JsonUtil.toJsonString(this);
    }
}
