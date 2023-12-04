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

import org.example.common.constant.Constants;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.StatusLine;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.lang.reflect.Field;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
@Slf4j
public class HttpUtil {

    public static String doGet(String url) {
        try (CloseableHttpClient client = HttpClients.createDefault()) {
            HttpGet request = new HttpGet(url);
            HttpResponse response = client.execute(request);
            if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
                return EntityUtils.toString(response.getEntity(), StandardCharsets.UTF_8);
            }
        } catch (IOException e) {
            log.error("DoGet exception.", e);

        }
        return null;
    }

    public static String getExpandUrl(String url, Object paramObj) {
        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(url);
        if (paramObj instanceof Map) {
            for (Map.Entry<String, Object> param : ((Map<String, Object>) paramObj).entrySet()) {
                if (null != param.getValue()) {
                    builder.queryParam(param.getKey(), param.getValue());
                }
            }
        } else {
            for (Field param : paramObj.getClass().getDeclaredFields()) {
                param.setAccessible(true);

                try {
                    Object value = param.get(paramObj);
                    if (null != value) {
                        if (value instanceof List) {
                            builder.queryParam(param.getName(), (List) value);
                        } else {
                            builder.queryParam(param.getName(), value);
                        }
                    }
                } catch (IllegalAccessException e) {
                    log.warn("Field: {} value retrieval failed, exception: {}", param.getName(), e);
                }
            }
        }

        return builder.build().encode().toUri().toString();
    }


    public static String doPost(String url, List<NameValuePair> params) {
        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            HttpPost post = new HttpPost(url);
            post.setEntity(new UrlEncodedFormEntity(params, Constants.TRANSFORMATION_FORMAT_UTF_8));
            try (CloseableHttpResponse httpResponse = httpClient.execute(post)) {
                StatusLine statusLine = httpResponse.getStatusLine();
                if (statusLine != null) {
                    if (statusLine.getStatusCode() == org.springframework.http.HttpStatus.OK.value()) {
                        return EntityUtils.toString(httpResponse.getEntity());
                    } else {
                        log.error("Do post failed, status code = {}, message = {}", statusLine.getStatusCode(), statusLine.getReasonPhrase());
                    }
                }
            }
        } catch (IOException e) {
            log.error("Do post by closeableHttpClient failed, message={}", e.getMessage(), e);
        }
        return null;
    }

    public static Map<String, String> requestToMap(HttpServletRequest request) {
        if (request == null) {
            return null;
        }
        return request.getParameterMap().entrySet().stream()
                .collect(Collectors.toMap(Map.Entry::getKey, entry -> String.join(",", entry.getValue())));
    }

    public static <T> T requestToObject(HttpServletRequest request, Class<T> clazz) {
        Map<String, String> map = request.getParameterMap().entrySet().stream()
                .collect(Collectors.toMap(entry -> NamingConventionUtil.underlineToCamel(entry.getKey()), entry -> String.join(",", entry.getValue())));
        T object = null;
        try {
            object = clazz.getDeclaredConstructor().newInstance();
            BeanWrapper beanWrapper = new BeanWrapperImpl(object);
            beanWrapper.setAutoGrowNestedPaths(true);
            for (Map.Entry<String, String> entry : map.entrySet()) {
                String propertyName = entry.getKey();
                String propertyValue = entry.getValue();

                if (beanWrapper.isWritableProperty(propertyName)) {
                    beanWrapper.setPropertyValue(propertyName, propertyValue);
                }
            }
        } catch (Exception e) {
            log.error("Request to object failed.", e);
        }
        return object;
    }
}
