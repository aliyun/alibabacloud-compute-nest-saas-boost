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

package org.example.common.adapter;

import com.alicloud.openservices.tablestore.model.GetRangeResponse;
import com.alicloud.openservices.tablestore.model.GetRowResponse;
import com.alicloud.openservices.tablestore.model.PutRowResponse;
import com.alicloud.openservices.tablestore.model.RangeRowQueryCriteria;
import com.alicloud.openservices.tablestore.model.RowPutChange;
import com.alicloud.openservices.tablestore.model.RowUpdateChange;
import com.alicloud.openservices.tablestore.model.SingleRowQueryCriteria;
import com.alicloud.openservices.tablestore.model.UpdateRowResponse;
import com.alicloud.openservices.tablestore.model.search.SearchRequest;
import com.alicloud.openservices.tablestore.model.search.SearchResponse;
import org.example.common.config.AliyunConfig;

public interface OtsClient {

    /**
     * Insert a row of data into Table Store.
     * @param rowPutChange Column names and column values to be inserted.
     * @return {@link PutRowResponse}
     */
    PutRowResponse putRow(RowPutChange rowPutChange);

    /***
     * Update a row of data in Table Store
     * @param rowUpdateChange Column names and column values to be updated.
     * @return {@link UpdateRowResponse}
     */
    UpdateRowResponse updateRow(RowUpdateChange rowUpdateChange);

    /**
     * Range query in Table Store.
     * @param rangeRowQueryCriteria Query Criteria
     * @return {@link GetRangeResponse}
     */
    GetRangeResponse getRange(RangeRowQueryCriteria rangeRowQueryCriteria);

    /**
     * Query a row of data in Table Store.
     * @param singleRowQueryCriteria Query Criteria
     * @return {@link GetRowResponse}
     */
    GetRowResponse getRow(SingleRowQueryCriteria singleRowQueryCriteria);

    /**
     * Query data using range query with a multi-dimensional index.
     * @param searchRequest Support range queries, composite queries, precise queries, etc.
     * @return {@link SearchResponse}
     */
    SearchResponse search(SearchRequest searchRequest);

    /**
     * Create ots client by ecs ram role
     * @param aliyunConfig aliyun config
     * @throws Exception Common exception
     */
    void createClient(AliyunConfig aliyunConfig) throws Exception;

    /**
     * Create ots client by fc header;
     * @param accessKeyId accessKeyId
     * @param accessKeySecret accessKeySecret
     * @param securityToken securityToken
     * @throws Exception Common exception
     */
    void createClient(String accessKeyId, String accessKeySecret, String securityToken);
}
