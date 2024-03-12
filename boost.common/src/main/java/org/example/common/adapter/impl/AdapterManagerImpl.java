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

import lombok.extern.slf4j.Slf4j;
import org.example.common.adapter.AcsApiCaller;
import org.example.common.adapter.AdapterManager;
import org.example.common.adapter.BaseAlipayClient;
import org.example.common.adapter.CloudMonitorClient;
import org.example.common.adapter.ComputeNestSupplierClient;
import org.example.common.adapter.OosClient;
import org.example.common.adapter.OtsClient;
import org.example.common.config.AlipayConfig;
import org.example.common.config.AliyunConfig;
import org.example.common.config.OosSecretParamConfig;
import org.example.common.constant.Constants;
import org.example.common.constant.DeployType;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.core.annotation.Order;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Map;

@Service
@EnableScheduling
@Slf4j
@Order(-1)
public class AdapterManagerImpl implements AdapterManager {

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

    @Resource
    private AcsApiCaller acsApiCaller;

    @Value("${deploy.type}")
    private String deployType;

    @Value("${boost.module}")
    private String module;

    private static final String BOOST_SERVERLESS_MODULE = "serverless";

    @Override
    public void clientInjection(Map<String, String> header) throws Exception {

        if ((header == null || header.isEmpty())) {
            oosClient.createClient(aliyunConfig);
            oosSecretParamConfig.init();
            otsClient.createClient(aliyunConfig);
            computeNestSupplierClient.createClient(aliyunConfig);
            cloudMonitorClient.createClient(aliyunConfig);
            acsApiCaller.createClient(aliyunConfig);
        } else {
            String accessKeyId = header.get(Constants.FC_ACCESS_KEY_ID);
            String accessKeySecret = header.get(Constants.FC_ACCESS_KEY_SECRET);
            String securityToken = header.get(Constants.FC_SECURITY_TOKEN);
            oosClient.createClient(accessKeyId, accessKeySecret, securityToken);
            otsClient.createClient(accessKeyId, accessKeySecret, securityToken);
            computeNestSupplierClient.createClient(accessKeyId, accessKeySecret, securityToken);
            oosSecretParamConfig.init();
            acsApiCaller.createClient(accessKeyId, accessKeySecret, securityToken);
        }
        baseAlipayClient.createClient(alipayConfig);
        log.info("Client injection success");
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {
        if (DeployType.ECS.getDeployType().equals(deployType) && BOOST_SERVERLESS_MODULE.equals(module)) {
            log.info("Skip client injection for ECS Serverless.");
            return;
        }
        clientInjection(null);
    }

    @Scheduled(fixedRate = 30 * 60 * 1000, initialDelay = 3000000)
    @Override
    public void updateClient() throws Exception {
        if (DeployType.ECS.getDeployType().equals(deployType) && BOOST_SERVERLESS_MODULE.equals(module)) {
            log.info("Skip client injection for ECS Serverless.");
            return;
        }
        aliyunConfig.init();
        clientInjection(null);
    }
}
