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

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import javax.annotation.Resource;
import static org.example.common.constant.Constants.OAUTH_CLIENT_ID;
import static org.example.common.constant.Constants.OAUTH_CLIENT_SECRET;

@Configuration
public class OauthConfig {

    @Getter
    @Value("${oauth.auth-server.url}")
    private String tokenUrl;

    @Getter
    @Value("${oauth.auth-code.url}")
    private String authUrl;

    @Value("${stack-name}")
    private String stackName;

    @Resource
    private OosParamConfig oosParamConfig;


    public String getOauthClientId() {
        return oosParamConfig.getSecretValue(OAUTH_CLIENT_ID);
    }

    public String getOauthClientSecret() {
        return oosParamConfig.getSecretValue(OAUTH_CLIENT_SECRET);
    }
}
