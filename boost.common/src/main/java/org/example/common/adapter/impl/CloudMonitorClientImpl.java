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


import com.aliyun.cms20190101.Client;
import com.aliyun.cms20190101.models.DescribeMetricListRequest;
import com.aliyun.cms20190101.models.DescribeMetricListResponse;
import com.aliyun.teaopenapi.models.Config;
import com.aliyun.teautil.models.RuntimeOptions;
import lombok.extern.slf4j.Slf4j;
import org.example.common.adapter.CloudMonitorClient;
import org.example.common.config.AliyunConfig;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class CloudMonitorClientImpl implements CloudMonitorClient {

    private static final String SERVICE_ENDPOINT = "metrics.cn-hangzhou.aliyuncs.com";

    private Client cloudMonitorClient;

    @Override
    public DescribeMetricListResponse getMetricList(DescribeMetricListRequest request) throws Exception {
        RuntimeOptions runtime = new RuntimeOptions();
        return this.cloudMonitorClient.describeMetricListWithOptions(request, runtime);
    }

    @Override
    public void createClient(AliyunConfig aliyunConfig) throws Exception {
        Config config = new Config().setCredential(aliyunConfig.getClient());
        config.endpoint = SERVICE_ENDPOINT;
        this.cloudMonitorClient = new Client(config);
    }

    @Override
    public void createClient(String accessKeyId, String accessKeySecret) throws Exception {
        Config config = new Config().setAccessKeyId(accessKeyId).setAccessKeySecret(accessKeySecret);
        config.endpoint = SERVICE_ENDPOINT;
        this.cloudMonitorClient = new Client(config);
    }

    @Override
    public void createClient(String accessKeyId, String accessKeySecret, String securityToken) throws Exception {
        this.cloudMonitorClient = new Client(new Config().setAccessKeyId(accessKeyId)
                .setAccessKeySecret(accessKeySecret).setSecurityToken(securityToken)
                .setEndpoint(SERVICE_ENDPOINT));
    }
}
