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

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public interface CommodityOtsConstant {

    /**
     * OTS table name for commodities.
     */
    String TABLE_NAME = "commodity";

    /**
     * Primary key name for the Commodity table: CommodityCode.
     * CommodityCode is expected to be stored as its MD5 hash value.
     */
    String PRIMARY_KEY_NAME = "pid";

    /**
     * The search index name for the Commodity table, if any.
     */
    String SEARCH_INDEX_NAME = "commodityIndex";

    // Define other fields that might be included in search and filters
    /**
     * Filter based on commodity name on the search index.
     */
    String COMMODITY_NAME = "commodityName";

    /**
     * Filter based on charge type on the search index.
     */
    String CHARGE_TYPE = "chargeType";

    /**
     * Filter based on service ID on the search index.
     */
    String SERVICE_ID = "serviceId";

    /**
     * Set of fields commonly used for match filters in the search index.
     */
    Set<String> MATCH_FILTERS_SET = Collections.unmodifiableSet(new HashSet<String>() {
        {
            add(COMMODITY_NAME);
            add(CHARGE_TYPE);
            add(SERVICE_ID);
        }
    });

    /**
     * Set of fields commonly used for range queries in the search index.
     * Assuming we have fields that represent time or other quantities that may require range queries.
     */
    Set<String> QUERY_FILTERS_SET = Collections.unmodifiableSet(new HashSet<String>() {
        // Add fields that might require range queries, if there are any.
    });

    /**
     * Attribute name for commodity code, before MD5 hashing.
     */
    String ORIGINAL_COMMODITY_CODE = "commodityCode";
}

