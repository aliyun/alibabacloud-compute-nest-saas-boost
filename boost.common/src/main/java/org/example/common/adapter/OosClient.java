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

package org.example.common.adapter;


import org.example.common.config.AliyunConfig;

import java.util.Map;

public interface OosClient {

    /**
     * Get oos secret parameter
     * @param name name
     * @return secret parameter
     */
    String getSecretParameter(String name);

    /**
     * Get secret parameters by path
     * @param path oos secret parameters path
     * @return {@link Map}
     */
    Map<String, String> getSecretParametersByPath(String path);

    /**
     * Create oos client by ecs ram role
     * @param aliyunConfig aliyun config
     * @throws Exception Common exception
     */
    void createClient(AliyunConfig aliyunConfig) throws Exception;

    /**
     * Create oos client by fc header;
     * @param accessKeyId accessKeyId
     * @param accessKeySecret accessKeySecret
     * @param securityToken securityToken
     * @throws Exception Common exception
     */
    void createClient(String accessKeyId, String accessKeySecret, String securityToken) throws Exception;

    /**
     * Create oos client by ak;
     * @param accessKeyId accessKeyId
     * @param accessKeySecret accessKeySecret
     * @throws Exception Common exception
     */
    void createClient(String accessKeyId, String accessKeySecret) throws Exception;
}


