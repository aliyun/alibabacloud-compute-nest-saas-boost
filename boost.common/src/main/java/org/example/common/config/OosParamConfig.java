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
import java.util.Arrays;
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
import org.example.common.model.ConfigParameterModel;
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
        List<String> secretValueList = Arrays.asList(OAUTH_CLIENT_ID, OOS_SECRET_ADMIN_AID, OAUTH_CLIENT_SECRET,
                OOS_SECRET_ALIPAY_OFFICIAL_PUBLIC_KEY, OOS_SECRET_ALIPAY_PRIVATE_KEY, OOS_SECRET_ALIPAY_APP_CERT_PATH,
                OOS_SECRET_ALIPAY_CERT_PATH, OOS_SECRET_ALIPAY_ROOT_CERT_PATH, OOS_SECRET_WECHATPAY_APIV3_KEY,
                OOS_SECRET_WECHATPAY_MCH_SERIAL_NO, OOS_SECRET_WECHATPAY_PRIVATE_KEY_PATH);
        List<String> valueList = Arrays.asList(OOS_ALIPAY_APP_ID, OOS_ALIPAY_PID, OOS_ALIPAY_GATEWAY,
                OOS_ALIPAY_SIGNATURE_METHOD, OOS_WECHATPAY_APP_ID, OOS_WECHATPAY_MCH_ID, OOS_WECHATPAY_GATEWAY);
        putSecretValueList(secretValueList);
        putValueList(valueList);

        modifiableParameterList = Arrays.asList(OOS_ALIPAY_APP_ID, OOS_ALIPAY_PID, OOS_ALIPAY_GATEWAY,
                OOS_SECRET_ALIPAY_OFFICIAL_PUBLIC_KEY, OOS_SECRET_ALIPAY_PRIVATE_KEY, OOS_SECRET_ALIPAY_APP_CERT_PATH,
                OOS_ALIPAY_SIGNATURE_METHOD, OOS_SECRET_ALIPAY_CERT_PATH,OOS_SECRET_ALIPAY_ROOT_CERT_PATH,
                OOS_WECHATPAY_APP_ID, OOS_WECHATPAY_MCH_ID, OOS_WECHATPAY_GATEWAY, OOS_SECRET_WECHATPAY_APIV3_KEY,
                OOS_SECRET_WECHATPAY_MCH_SERIAL_NO, OOS_SECRET_WECHATPAY_PRIVATE_KEY_PATH);
    }

    private void putSecretValueList(List<String> secretValueList) {
        List<String> formatList = new ArrayList<>();
        for (String secretValue : secretValueList) {
            String format = String.format("%s-%s-%s", SERVICE_INSTANCE_ID, stackName, secretValue);
            formatList.add(format);
        }
        List<ConfigParameterModel> secretParameterModels = parameterOosHelper.listSecretParameters(formatList);
        for (ConfigParameterModel configParameterModel : secretParameterModels) {
            parameterMap.put(configParameterModel.getName(), configParameterModel.getValue());
        }
    }

    private void putValueList(List<String> valueList) {
        List<String> formatList = new ArrayList<>();
        for (String value : valueList) {
            String format = String.format("%s-%s-%s", SERVICE_INSTANCE_ID, stackName, value);
            formatList.add(format);
        }
        List<ConfigParameterModel> parameterModels = parameterOosHelper.listParameters(formatList);
        for (ConfigParameterModel configParameterModel : parameterModels) {
            parameterMap.put(configParameterModel.getName(), configParameterModel.getValue());
        }
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
