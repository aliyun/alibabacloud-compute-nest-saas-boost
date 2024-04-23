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

import java.util.HashMap;
import java.util.Map;
import javax.annotation.Resource;
import static org.example.common.constant.AliPayConstants.OOS_PARAMETER_PROVIDER_DESCRIPTION;
import static org.example.common.constant.AliPayConstants.OOS_PARAMETER_PROVIDER_NAME;
import static org.example.common.constant.AliPayConstants.OOS_PARAMETER_PROVIDER_OFFICIAL_LINK;
import static org.example.common.constant.AliPayConstants.OOS_SECRET_ADMIN_AID;
import static org.example.common.constant.AliPayConstants.OOS_SECRET_ALIPAY_OFFICIAL_PUBLIC_KEY;
import static org.example.common.constant.AliPayConstants.OOS_SECRET_ALIPAY_PRIVATE_KEY;
import static org.example.common.constant.AliPayConstants.OOS_SECRET_APP_ID;
import static org.example.common.constant.AliPayConstants.OOS_SECRET_PID;
import static org.example.common.constant.AliPayConstants.OOS_SECRET_WECHAT_OFFICIAL_PUBLIC_KEY;
import static org.example.common.constant.AliPayConstants.OOS_SECRET_WECHAT_PRIVATE_KEY;
import static org.example.common.constant.Constants.OAUTH_CLIENT_ID;
import static org.example.common.constant.Constants.OAUTH_CLIENT_SECRET;
import static org.example.common.constant.Constants.SERVICE_INSTANCE_ID;
import org.example.common.helper.oos.ParameterOosHelper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OosSecretParamConfig {

    @Value("${stack-name}")
    private String stackName;

    @Resource
    private ParameterOosHelper parameterOosHelper;

    private Map<String, String> secretMap;

    public String getSecretValue(String name) {
        String format = String.format("%s-%s-%s", SERVICE_INSTANCE_ID, stackName, name);
        return this.secretMap.get(format);
    }

    public String getValue(String name) {
        String format = String.format("%s-%s-%s", SERVICE_INSTANCE_ID, stackName, name);
        return this.secretMap.get(format);
    }

    public void init() {
        secretMap = new HashMap<>(10, 0.75F);
        putSecretValue(OOS_SECRET_APP_ID);
        putSecretValue(OOS_SECRET_PID);
        putSecretValue(OOS_SECRET_ALIPAY_OFFICIAL_PUBLIC_KEY);
        putSecretValue(OOS_SECRET_ALIPAY_PRIVATE_KEY);
        putSecretValue(OAUTH_CLIENT_ID);
        putSecretValue(OAUTH_CLIENT_SECRET);
        putValue(OOS_PARAMETER_PROVIDER_NAME);
        putValue(OOS_PARAMETER_PROVIDER_DESCRIPTION);
        putValue(OOS_PARAMETER_PROVIDER_OFFICIAL_LINK);
        putSecretValue(OOS_SECRET_WECHAT_OFFICIAL_PUBLIC_KEY);
        putSecretValue(OOS_SECRET_WECHAT_PRIVATE_KEY);
//        this.secretMap = oosClient.getSecretParametersByPath(String.format("%s/%s", SERVICE_INSTANCE_ID, stackName));
    }

    private void putSecretValue(String parameterName) {
        String format = String.format("%s-%s-%s", SERVICE_INSTANCE_ID, stackName, parameterName);
        secretMap.put(format, parameterOosHelper.getSecretParameter(format));
    }

    private void putValue(String parameterName) {
        String format = String.format("%s-%s-%s", SERVICE_INSTANCE_ID, stackName, parameterName);
        secretMap.put(format, parameterOosHelper.getParameter(format));
    }
}
