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
import static org.example.common.constant.AliPayConstants.OOS_ALIPAY_APP_ID;
import static org.example.common.constant.AliPayConstants.OOS_ALIPAY_GATEWAY;
import static org.example.common.constant.AliPayConstants.OOS_ALIPAY_PID;
import static org.example.common.constant.AliPayConstants.OOS_ALIPAY_SIGNATURE_METHOD;
import static org.example.common.constant.AliPayConstants.OOS_SECRET_ALIPAY_CERT_PATH;
import static org.example.common.constant.AliPayConstants.OOS_SECRET_ALIPAY_OFFICIAL_PUBLIC_KEY;
import static org.example.common.constant.AliPayConstants.OOS_SECRET_ALIPAY_PRIVATE_KEY;
import static org.example.common.constant.AliPayConstants.OOS_SECRET_ALIPAY_ROOT_CERT_PATH;
import static org.example.common.constant.AliPayConstants.OOS_SECRET_ALIPAY_APP_CERT_PATH;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Data
@Component
public class BoostAlipayConfig {

    @Resource
    private OosParamConfig oosParamConfig;

    @Value("${payment.return-url}")
    private String returnUrl;

    @Value("${payment.pay-notify-url}")
    private String notifyUrl;

    @Value("${stack-name}")
    private String stackName;

    public String getAppId() {
        return oosParamConfig.getValue(OOS_ALIPAY_APP_ID);
    }

    public String getPid() {
        return oosParamConfig.getValue(OOS_ALIPAY_PID);
    }

    public String getPrivateKey() {
        return oosParamConfig.getSecretValue(OOS_SECRET_ALIPAY_PRIVATE_KEY);
    }

    public String getSignatureMethod() { return oosParamConfig.getValue(OOS_ALIPAY_SIGNATURE_METHOD); }

    public String getAlipayPublicKey() { return oosParamConfig.getSecretValue(OOS_SECRET_ALIPAY_OFFICIAL_PUBLIC_KEY); }

    public String getAppCertPath() { return oosParamConfig.getSecretValue(OOS_SECRET_ALIPAY_APP_CERT_PATH); }

    public String getAlipayCertPath() { return oosParamConfig.getSecretValue(OOS_SECRET_ALIPAY_CERT_PATH); }

    public String getAlipayRootCertPath() { return oosParamConfig.getSecretValue(OOS_SECRET_ALIPAY_ROOT_CERT_PATH); }
    
    public String getGateway() {
        return oosParamConfig.getValue(OOS_ALIPAY_GATEWAY);
    }

    public void updateOosParamConfig(String parameterName, String value) {
        oosParamConfig.updateOosParameterConfig(parameterName, value);
    }
}

