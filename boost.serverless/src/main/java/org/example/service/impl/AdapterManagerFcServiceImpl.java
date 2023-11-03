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

package org.example.service.impl;

import org.example.common.adapter.BaseAlipayClient;
import org.example.common.adapter.ComputeNestSupplierClient;
import org.example.common.adapter.OosClient;
import org.example.common.adapter.OtsClient;
import org.example.common.config.AlipayConfig;
import org.example.common.config.OosSecretParamConfig;
import org.example.common.constant.Constants;
import org.example.service.AdapterManagerFcService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Map;

@Service
public class AdapterManagerFcServiceImpl implements AdapterManagerFcService {

    @Resource
    private AlipayConfig alipayConfig;

    @Resource
    private OosClient oosClient;

    @Resource
    private OtsClient otsClient;

    @Resource
    private ComputeNestSupplierClient computeNestSupplierClient;

    @Resource
    private BaseAlipayClient baseAlipayClient;

    @Resource
    private OosSecretParamConfig oosSecretParamConfig;

    @Override
    public void clientInjection(Map<String, String> header) throws Exception {
        String accessKeyId = header.get(Constants.FC_ACCESS_KEY_ID);
        String accessKeySecret = header.get(Constants.FC_ACCESS_KEY_SECRET);
        String securityToken = header.get(Constants.FC_SECURITY_TOKEN);
        oosClient.createClient(accessKeyId, accessKeySecret, securityToken);
        otsClient.createClient(accessKeyId, accessKeySecret, securityToken);
        computeNestSupplierClient.createClient(accessKeyId, accessKeySecret, securityToken);
        oosSecretParamConfig.init();
        baseAlipayClient.createClient(alipayConfig);
    }
}
