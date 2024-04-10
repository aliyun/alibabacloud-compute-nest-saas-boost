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

package org.example.common.adapter.impl;

import com.alicloud.openservices.tablestore.SyncClient;
import com.alicloud.openservices.tablestore.model.GetRangeRequest;
import com.alicloud.openservices.tablestore.model.GetRangeResponse;
import com.alicloud.openservices.tablestore.model.GetRowRequest;
import com.alicloud.openservices.tablestore.model.GetRowResponse;
import com.alicloud.openservices.tablestore.model.PutRowRequest;
import com.alicloud.openservices.tablestore.model.PutRowResponse;
import com.alicloud.openservices.tablestore.model.RangeRowQueryCriteria;
import com.alicloud.openservices.tablestore.model.RowPutChange;
import com.alicloud.openservices.tablestore.model.RowUpdateChange;
import com.alicloud.openservices.tablestore.model.SingleRowQueryCriteria;
import com.alicloud.openservices.tablestore.model.UpdateRowRequest;
import com.alicloud.openservices.tablestore.model.UpdateRowResponse;
import com.alicloud.openservices.tablestore.model.search.SearchRequest;
import com.alicloud.openservices.tablestore.model.search.SearchResponse;
import com.aliyun.teaopenapi.models.Config;
import org.example.common.adapter.OtsClient;
import org.example.common.config.AliyunConfig;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class OtsClientImpl implements OtsClient {

    @Value("${service.region-id}")
    private String regionId;

    @Value("${ots-instance-name}")
    private String instanceName;

    private SyncClient syncClient;

    @Override
    public PutRowResponse putRow(RowPutChange rowPutChange) {
        return syncClient.putRow(new PutRowRequest(rowPutChange));
    }

    @Override
    public UpdateRowResponse updateRow(RowUpdateChange rowUpdateChange) {
        return syncClient.updateRow(new UpdateRowRequest(rowUpdateChange));
    }

    @Override
    public GetRangeResponse getRange(RangeRowQueryCriteria rangeRowQueryCriteria) {
        return syncClient.getRange(new GetRangeRequest(rangeRowQueryCriteria));
    }

    @Override
    public GetRowResponse getRow(SingleRowQueryCriteria singleRowQueryCriteria) {
        return syncClient.getRow(new GetRowRequest(singleRowQueryCriteria));
    }

    @Override
    public SearchResponse search(SearchRequest searchRequest) {
        return syncClient.search(searchRequest);
    }

    @Override
    public void createClient(AliyunConfig aliyunConfig) throws Exception {
        this.syncClient = new SyncClient(getEntpoint(), aliyunConfig.getClient().getAccessKeyId(),
                aliyunConfig.getClient().getAccessKeySecret(), instanceName, aliyunConfig.getClient().getSecurityToken());
    }

    @Override
    public void createClient(String accessKeyId, String accessKeySecret, String securityToken) {
        this.syncClient = new SyncClient(getEntpoint(), accessKeyId, accessKeySecret, instanceName, securityToken);
    }

    @Override
    public void createClient(String accessKeyId, String accessKeySecret) throws Exception {
        Config config = new Config().setAccessKeyId(accessKeyId).setAccessKeySecret(accessKeySecret);
        config.endpoint = String.format("oos.%s.aliyuncs.com", regionId);
        this.syncClient = new SyncClient(getEntpoint(), accessKeyId, accessKeySecret, instanceName);
    }

    private String getEntpoint() {
        return String.format("https://%s.%s.ots.aliyuncs.com", instanceName, regionId);
    }
}
