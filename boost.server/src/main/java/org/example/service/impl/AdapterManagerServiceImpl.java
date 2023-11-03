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
import org.example.common.adapter.CloudMonitorClient;
import org.example.common.adapter.ComputeNestSupplierClient;
import org.example.common.adapter.OosClient;
import org.example.common.adapter.OtsClient;
import org.example.common.config.AlipayConfig;
import org.example.common.config.AliyunConfig;
import org.example.common.config.OosSecretParamConfig;
import org.example.service.AdapterManagerService;
import org.springframework.boot.ApplicationArguments;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service
@EnableScheduling
public class AdapterManagerServiceImpl implements AdapterManagerService {

    @Resource
    private AliyunConfig aliyunConfig;

    @Resource
    private AlipayConfig alipayConfig;

    @Resource
    private OosClient oosClient;

    @Resource
    private OtsClient otsClient;

    @Resource
    private CloudMonitorClient cloudMonitorClient;

    @Resource
    private ComputeNestSupplierClient computeNestSupplierClient;

    @Resource
    private BaseAlipayClient baseAlipayClient;

    @Resource
    private OosSecretParamConfig oosSecretParamConfig;

    @Override
    public void clientInjection() throws Exception {
        aliyunConfig.createClient();
        oosClient.createClient(aliyunConfig);
        oosSecretParamConfig.init();
        otsClient.createClient(aliyunConfig);
        cloudMonitorClient.createClient(aliyunConfig);
        computeNestSupplierClient.createClient(aliyunConfig);
        baseAlipayClient.createClient(alipayConfig);
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {
        clientInjection();
    }

    @Scheduled(fixedDelay = 3000000)
    public void updateClient() throws Exception{
       clientInjection();
    }
}
