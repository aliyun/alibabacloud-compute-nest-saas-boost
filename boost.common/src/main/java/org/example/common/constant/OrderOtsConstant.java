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

public interface OrderOtsConstant {

    /**
     * OTS table name : order.
     */
    String TABLE_NAME = "order";

    /**
     * OTS table order primary key name : id.
     */
    String PRIMARY_KEY_NAME = "id";

    /**
     * OTS search-index name: order_index.
     */
    String SEARCH_INDEX_NAME = "order_index";

    /**
     * Range filter on search-index: gmtCreateLong(order creation time).
     */
    String SEARCH_INDEX_FIELD_NAME_1 = "gmtCreateLong";

    /**
     * Match filter on search-index: accountId.
     */
    String FILTER_NAME_0 = "accountId";
}
