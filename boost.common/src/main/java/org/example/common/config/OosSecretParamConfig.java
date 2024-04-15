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

import org.example.common.adapter.OosClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;

import static org.example.common.constant.AliPayConstants.OOS_SECRET_ADMIN_AID;
import static org.example.common.constant.AliPayConstants.OOS_SECRET_APP_ID;
import static org.example.common.constant.AliPayConstants.OOS_SECRET_OFFICIAL_PUBLIC_KEY;
import static org.example.common.constant.AliPayConstants.OOS_SECRET_PID;
import static org.example.common.constant.AliPayConstants.OOS_SECRET_PRIVATE_KEY;
import static org.example.common.constant.Constants.OAUTH_CLIENT_ID;
import static org.example.common.constant.Constants.OAUTH_CLIENT_SECRET;
import static org.example.common.constant.Constants.SERVICE_INSTANCE_ID;

@Configuration
public class OosSecretParamConfig {

    @Value("${stack-name}")
    private String stackName;

    @Resource
    private OosClient oosClient;

    private Map<String, String> secretMap;

    public String getSecretValue(String name) {
        String format = String.format("%s-%s-%s", SERVICE_INSTANCE_ID, stackName, name);
        return this.secretMap.get(format);
    }

    public void init() {
        secretMap = new HashMap<>(10, 0.75F);
        putSecretValue(OOS_SECRET_APP_ID);
        putSecretValue(OOS_SECRET_PID);
        putSecretValue(OOS_SECRET_OFFICIAL_PUBLIC_KEY);
        putSecretValue(OOS_SECRET_PRIVATE_KEY);
        putSecretValue(OAUTH_CLIENT_ID);
        putSecretValue(OAUTH_CLIENT_SECRET);
        putSecretValue(OOS_SECRET_ADMIN_AID);
//        this.secretMap = oosClient.getSecretParametersByPath(String.format("%s/%s", SERVICE_INSTANCE_ID, stackName));
    }

    private void putSecretValue(String parameterName) {
        String format = String.format("%s-%s-%s", SERVICE_INSTANCE_ID, stackName, parameterName);
        secretMap.put(format, oosClient.getSecretParameter(format));
    }
}
