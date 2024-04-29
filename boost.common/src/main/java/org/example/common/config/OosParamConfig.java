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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.Resource;
import static org.example.common.constant.AliPayConstants.OOS_ALIPAY_APP_ID;
import static org.example.common.constant.AliPayConstants.OOS_ALIPAY_GATEWAY;
import static org.example.common.constant.AliPayConstants.OOS_ALIPAY_PID;
import static org.example.common.constant.AliPayConstants.OOS_ALIPAY_SIGNATURE_METHOD;
import static org.example.common.constant.AliPayConstants.OOS_SECRET_ADMIN_AID;
import static org.example.common.constant.AliPayConstants.OOS_SECRET_ALIPAY_APP_CERT_PATH;
import static org.example.common.constant.AliPayConstants.OOS_SECRET_ALIPAY_CERT_PATH;
import static org.example.common.constant.AliPayConstants.OOS_SECRET_ALIPAY_OFFICIAL_PUBLIC_KEY;
import static org.example.common.constant.AliPayConstants.OOS_SECRET_ALIPAY_PRIVATE_KEY;
import static org.example.common.constant.AliPayConstants.OOS_SECRET_ALIPAY_ROOT_CERT_PATH;
import static org.example.common.constant.Constants.OAUTH_CLIENT_ID;
import static org.example.common.constant.Constants.OAUTH_CLIENT_SECRET;
import static org.example.common.constant.Constants.SERVICE_INSTANCE_ID;
import static org.example.common.constant.WechatPayConstants.OOS_SECRET_WECHATPAY_APIV3_KEY;
import static org.example.common.constant.WechatPayConstants.OOS_SECRET_WECHATPAY_MCH_SERIAL_NO;
import static org.example.common.constant.WechatPayConstants.OOS_SECRET_WECHATPAY_PRIVATE_KEY_PATH;
import static org.example.common.constant.WechatPayConstants.OOS_WECHATPAY_APP_ID;
import static org.example.common.constant.WechatPayConstants.OOS_WECHATPAY_GATEWAY;
import static org.example.common.constant.WechatPayConstants.OOS_WECHATPAY_MCH_ID;
import org.example.common.helper.oos.ParameterOosHelper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OosParamConfig {

    @Value("${stack-name}")
    private String stackName;

    @Resource
    private ParameterOosHelper parameterOosHelper;

    private Map<String, String> parameterMap;

    private List<String> modifiableParameterList;

    public String getSecretValue(String name) {
        String format = String.format("%s-%s-%s", SERVICE_INSTANCE_ID, stackName, name);
        return this.parameterMap.get(format);
    }

    public String getValue(String name) {
        String format = String.format("%s-%s-%s", SERVICE_INSTANCE_ID, stackName, name);
        return this.parameterMap.get(format);
    }

    public void init() {
        parameterMap = new HashMap<>(10, 0.75F);
        putSecretValue(OAUTH_CLIENT_ID);
        putSecretValue(OOS_SECRET_ADMIN_AID);
        putSecretValue(OAUTH_CLIENT_SECRET);

        putValue(OOS_ALIPAY_APP_ID);
        putValue(OOS_ALIPAY_PID);
        putSecretValue(OOS_SECRET_ALIPAY_OFFICIAL_PUBLIC_KEY);
        putSecretValue(OOS_SECRET_ALIPAY_PRIVATE_KEY);
        putValue(OOS_ALIPAY_GATEWAY);
        putValue(OOS_ALIPAY_SIGNATURE_METHOD);
        putSecretValue(OOS_SECRET_ALIPAY_APP_CERT_PATH);
        putSecretValue(OOS_SECRET_ALIPAY_CERT_PATH);
        putSecretValue(OOS_SECRET_ALIPAY_ROOT_CERT_PATH);

        putValue(OOS_WECHATPAY_APP_ID);
        putValue(OOS_WECHATPAY_MCH_ID);
        putSecretValue(OOS_SECRET_WECHATPAY_APIV3_KEY);
        putSecretValue(OOS_SECRET_WECHATPAY_MCH_SERIAL_NO);
        putSecretValue(OOS_SECRET_WECHATPAY_PRIVATE_KEY_PATH);
        putValue(OOS_WECHATPAY_GATEWAY);

        modifiableParameterList = new ArrayList<>();
        modifiableParameterList.add(OOS_ALIPAY_APP_ID);
        modifiableParameterList.add(OOS_ALIPAY_PID);
        modifiableParameterList.add(OOS_SECRET_ALIPAY_OFFICIAL_PUBLIC_KEY);
        modifiableParameterList.add(OOS_SECRET_ALIPAY_PRIVATE_KEY);
        modifiableParameterList.add(OOS_ALIPAY_GATEWAY);
        modifiableParameterList.add(OOS_ALIPAY_SIGNATURE_METHOD);
        modifiableParameterList.add(OOS_SECRET_ALIPAY_APP_CERT_PATH);
        modifiableParameterList.add(OOS_SECRET_ALIPAY_CERT_PATH);
        modifiableParameterList.add(OOS_SECRET_ALIPAY_ROOT_CERT_PATH);

        modifiableParameterList.add(OOS_WECHATPAY_APP_ID);
        modifiableParameterList.add(OOS_WECHATPAY_MCH_ID);
        modifiableParameterList.add(OOS_SECRET_WECHATPAY_APIV3_KEY);
        modifiableParameterList.add(OOS_SECRET_WECHATPAY_MCH_SERIAL_NO);
        modifiableParameterList.add(OOS_WECHATPAY_GATEWAY);
        modifiableParameterList.add(OOS_SECRET_WECHATPAY_PRIVATE_KEY_PATH);
    }

    private void putSecretValue(String parameterName) {
        String format = String.format("%s-%s-%s", SERVICE_INSTANCE_ID, stackName, parameterName);
        parameterMap.put(format, parameterOosHelper.getSecretParameter(format));
    }

    private void putValue(String parameterName) {
        String format = String.format("%s-%s-%s", SERVICE_INSTANCE_ID, stackName, parameterName);
        parameterMap.put(format, parameterOosHelper.getParameter(format));
    }

    public void updateOosParameterConfig(String parameterName, String value) {
        String[] parts = parameterName.split("-");
        String parameterNameExtracted = parts[parts.length - 1];
        if (modifiableParameterList.contains(parameterNameExtracted)) {
            String format = String.format("%s-%s-%s", SERVICE_INSTANCE_ID, stackName, parameterNameExtracted);
            parameterMap.put(format, value);
        }
    }
}
