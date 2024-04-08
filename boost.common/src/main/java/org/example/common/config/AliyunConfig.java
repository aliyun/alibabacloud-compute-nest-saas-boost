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

package org.example.common.config;

import com.aliyun.credentials.Client;
import com.aliyun.credentials.exception.CredentialException;
import com.aliyun.credentials.models.Config;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.example.common.constant.DeployType;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

@Data
@Component
@Slf4j
public class AliyunConfig {

    private static final String ECS_CONFIG_TYPE = "ecs_ram_role";

    private static final String OIDC_CONFIG_TYPE = "oidc_role_arn";

    private static final String OIDC_ROLE_SESSION_NAME = "saas-boost-role-session";

    private Client client = null;

    private static final String OIDC_PROVIDER_ARN = "ALIBABA_CLOUD_OIDC_PROVIDER_ARN";

    private static final String ROLE_ARN = "ALIBABA_CLOUD_ROLE_ARN";

    private static final String OIDC_TOKEN_FILE_PATH = "ALIBABA_CLOUD_OIDC_TOKEN_FILE";

    @Value("${deploy.type}")
    private String deployType;

    @Value("${ecs-role-name:null}")
    private String roleName;

    @Value("${boost.module}")
    private String module;

    private static final String BOOST_SERVERLESS_MODULE = "serverless";

    @PostConstruct
    public void init() throws CredentialException {
        if (DeployType.LOCAL.getDeployType().equals(deployType)) {
            return;
        }

        if (DeployType.ECS.getDeployType().equals(deployType)) {
            if (!BOOST_SERVERLESS_MODULE.equals(module)) {
                Config config = new Config();
                config.type = ECS_CONFIG_TYPE;
                config.roleName = roleName;
                this.client = new Client(config);
            }

        } else {
            Config credConf = new Config();
            credConf.type = OIDC_CONFIG_TYPE;
            credConf.roleArn = System.getenv(ROLE_ARN);
            credConf.oidcProviderArn = System.getenv(OIDC_PROVIDER_ARN);
            credConf.oidcTokenFilePath = System.getenv(OIDC_TOKEN_FILE_PATH);
            credConf.roleSessionName = OIDC_ROLE_SESSION_NAME;
            this.client = new Client(credConf);
        }
    }
}

