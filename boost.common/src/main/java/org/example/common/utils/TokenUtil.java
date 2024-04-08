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
package org.example.common.utils;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;

import java.beans.PropertyDescriptor;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
public class TokenUtil {

    /**
     * Generate a validation token based on parameters and a security key.
     *
     * @param param  The parameter map.
     * @param isvKey The security key for the ISV.
     * @return The generated MD5 token.
     */
    public static String createSpiToken(Object param, String isvKey) {
        if (StringUtils.isNotEmpty(isvKey)) {
            Map<String, String> paramMap = beanPropertiesToMap(param);
            String params = buildUrlParams(paramMap);
            params += "&key=" + isvKey;
            String md5Token = EncryptionUtil.getMd5HexString(params);
            log.info("createValidToken {} token {}.", params, md5Token);
            return md5Token;
        }
        return null;
    }

    public static String buildUrlParams(Map<String, String> paramMap) {
        return paramMap.entrySet().stream()
                .filter(e -> StringUtils.isNotBlank(e.getKey()))
                .sorted(Map.Entry.comparingByKey())
                .map(e -> e.getKey() + "=" + e.getValue())
                .collect(Collectors.joining("&"));
    }

    public static Map<String, String> beanPropertiesToMap(Object bean) {
        Map<String, String> propertiesMap = new HashMap<>();
        BeanWrapper beanWrapper = new BeanWrapperImpl(bean);
        PropertyDescriptor[] propertyDescriptors = beanWrapper.getPropertyDescriptors();

        for (PropertyDescriptor pd : propertyDescriptors) {
            String propertyName = pd.getName();
            if (!"class".equals(propertyName) && !"token".equals(propertyName)) {
                Object propertyValue = beanWrapper.getPropertyValue(propertyName);
                if (propertyValue != null) {
                    propertiesMap.put(propertyName, propertyValue.toString());
                }
            }
        }
        return propertiesMap;
    }
}
