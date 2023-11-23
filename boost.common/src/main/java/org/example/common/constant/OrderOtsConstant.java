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

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

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
    String GMT_CREATE_LONG = "gmtCreateLong";

    String BILLING_END_DATE_LONG = "billingEndDateLong";

    String SERVICE_INSTANCE_ID = "serviceInstanceId";

    /**
     * Match filter on search-index: accountId.
     */
    String ACCOUNT_ID = "accountId";

    String TRADE_STATUS = "tradeStatus";

    Set<String> MATCH_FILTERS_SET =  Collections.unmodifiableSet(new HashSet<String>(){
        {
            add(GMT_CREATE_LONG);
            add(SERVICE_INSTANCE_ID);
            add(ACCOUNT_ID);
            add(TRADE_STATUS);
        }
    });

    Set<String> QUERY_FILTERS_SET =  Collections.unmodifiableSet(new HashSet<String>(){
        {
            add(GMT_CREATE_LONG);
            add(BILLING_END_DATE_LONG);
        }
    });
}
