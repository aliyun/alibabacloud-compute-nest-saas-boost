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

public interface CommoditySpecificationOtsConstant {

    /**
     * OTS table name for commodity specifications.
     */
    String TABLE_NAME = "commoditySpecification";

    /**
     * Primary key name for the CommoditySpecification table: SpecificationCode.
     * SpecificationName is expected to be stored as its MD5 hash value.
     */
    String PRIMARY_KEY_NAME_PID = "pid";

    /**
     * Primary key name for the CommoditySpecification table: SpecificationCode.
     * Commodity Code is expected to be stored as its MD5 hash value.
     */
    String PRIMARY_KEY_NAME_COMMODITY_ID = "cid";

    /**
     * The search index name for the CommoditySpecification table, if any.
     */
    String SEARCH_INDEX_NAME = "commoditySpecificationIndex";

    /**
     * Field for commodity code on CommoditySpecification table.
     */
    String COMMODITY_CODE = "commodityCode";

    /**
     * Field for specification name on CommoditySpecification table.
     */
    String SPECIFICATION_NAME = "specificationName";

    /**
     * Field for owner id on CommoditySpecification table.
     */
    String OWNER_ID = "ownerId";

    /**
     * Field for commodity status on CommoditySpecification table.
     */
    String COMMODITY_STATUS = "commodityStatus";

    /**
     * Set of fields commonly used for range queries in the search index for commodity specifications.
     * Assuming we have fields that represent time or other quantities that may require range queries.
     */
    Set<String> QUERY_FILTERS_SET = Collections.unmodifiableSet(new HashSet<String>() {
    });
}

