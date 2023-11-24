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

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;

import static org.example.common.utils.NamingConventionUtil.toCamelCase;

@Slf4j
public class BeanUtil {
    public static void populateObject(Map<String, Object> map, Object target) {
        if (target == null) {
            return;
        }
        for (Map.Entry<String, Object> entry : map.entrySet()) {
            String key = toCamelCase(entry.getKey());
            Object value = entry.getValue();

            String setterMethodName = "set" + key.substring(0, 1).toUpperCase() + key.substring(1);
            Method[] methods = target.getClass().getMethods();

            // Find the matching setter method
            for (Method method : methods) {
                try {
                    if (StringUtils.isNotEmpty(method.getName()) && method.getName().equals(setterMethodName) && method.getParameterCount() == 1) {
                        Class<?> paramType = method.getParameterTypes()[0];
                        /*
                         * If the field type is an enumeration type and the value passed in is a string,
                         *  attempt to convert the string to the corresponding enumeration type.
                         */
                        if (paramType.isEnum() && value instanceof String) {
                            Object enumValue = Enum.valueOf((Class<Enum>) paramType, (String) value);
                            method.invoke(target, enumValue);
                        } else {
                            method.invoke(target, value);
                        }
                        break;
                    }
                } catch (IllegalAccessException e) {
                    log.error("IllegalAccessException occurred while invoking method. Make sure the method is accessible.", e);
                } catch (InvocationTargetException e) {
                    log.error("InvocationTargetException occurred: {}", method.getName(), e);
                }
            }
        }
    }
}
