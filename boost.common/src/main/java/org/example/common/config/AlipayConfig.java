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

import javax.annotation.Resource;
import lombok.Data;
import static org.example.common.constant.AliPayConstants.OOS_SECRET_ALIPAY_OFFICIAL_PUBLIC_KEY;
import static org.example.common.constant.AliPayConstants.OOS_SECRET_ALIPAY_PRIVATE_KEY;
import static org.example.common.constant.AliPayConstants.OOS_SECRET_ALIPAY_APP_ID;
import static org.example.common.constant.AliPayConstants.OOS_SECRET_ALIPAY_PID;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Data
@Component
public class AlipayConfig {

    @Resource
    private OosSecretParamConfig oosSecretParamConfig;

    @Value("${alipay.gateway}")
    private String gateway;

    @Value("${alipay.return-url}")
    private String returnUrl;

    @Value("${alipay.notify-url}")
    private String notifyUrl;

    @Value("${stack-name}")
    private String stackName;

    public String getAppId() {
        return oosSecretParamConfig.getSecretValue(OOS_SECRET_ALIPAY_APP_ID);
    }

    public String getPid() {
        return oosSecretParamConfig.getSecretValue(OOS_SECRET_ALIPAY_PID);
    }

    public String getPrivateKey() {
        return oosSecretParamConfig.getSecretValue(OOS_SECRET_ALIPAY_PRIVATE_KEY);
    }

    public String getOfficialPublicKey() {
        return oosSecretParamConfig.getSecretValue(OOS_SECRET_ALIPAY_OFFICIAL_PUBLIC_KEY);
    }
}

