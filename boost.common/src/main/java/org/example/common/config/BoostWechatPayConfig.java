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
import static org.example.common.constant.WechatPayConstants.OOS_SECRET_WECHATPAY_APIV3_KEY;
import static org.example.common.constant.WechatPayConstants.OOS_SECRET_WECHATPAY_MCH_SERIAL_NO;
import static org.example.common.constant.WechatPayConstants.OOS_SECRET_WECHATPAY_PRIVATE_KEY_PATH;
import static org.example.common.constant.WechatPayConstants.OOS_WECHATPAY_APP_ID;
import static org.example.common.constant.WechatPayConstants.OOS_WECHATPAY_GATEWAY;
import static org.example.common.constant.WechatPayConstants.OOS_WECHATPAY_MCH_ID;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Data
@Component
public class BoostWechatPayConfig {

    @Resource
    private OosParamConfig oosParamConfig;

    @Value("${payment.return-url}")
    private String returnUrl;

    @Value("${payment.pay-notify-url}")
    private String payNotifyUrl;

    @Value("${payment.refund-notify-url}")
    private String refundNotifyUrl;

    @Value("${stack-name}")
    private String stackName;

    public String getAppId() {
        return oosParamConfig.getValue(OOS_WECHATPAY_APP_ID);
    }

    public String getMchId() {
        return oosParamConfig.getValue(OOS_WECHATPAY_MCH_ID);
    }

    public String getApiV3Key() {
        return oosParamConfig.getSecretValue(OOS_SECRET_WECHATPAY_APIV3_KEY);
    }

    public String getMchSerialNo() { return oosParamConfig.getValue(OOS_SECRET_WECHATPAY_MCH_SERIAL_NO);}

    public String getPrivateKeyPath() {
        return oosParamConfig.getSecretValue(OOS_SECRET_WECHATPAY_PRIVATE_KEY_PATH);
    }

    public String getGateway() { return oosParamConfig.getValue(OOS_WECHATPAY_GATEWAY); }

    public void updateOosParamConfig(String parameterName, String value) {
        oosParamConfig.updateOosParameterConfig(parameterName, value);
    }
}

