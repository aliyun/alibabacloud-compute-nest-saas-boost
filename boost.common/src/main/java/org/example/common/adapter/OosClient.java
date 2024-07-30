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


import com.aliyun.oos20190601.models.GetParametersResponse;
import com.aliyun.oos20190601.models.GetSecretParametersResponse;
import com.aliyun.oos20190601.models.UpdateParameterResponse;
import com.aliyun.oos20190601.models.UpdateSecretParameterResponse;
import java.util.List;
import org.example.common.config.AliyunConfig;

public interface OosClient {

    /**
     * Get oos secret parameter
     * @param name name
     * @return GetSecretParameterResponse
     */
    GetSecretParametersResponse listSecretParameters(List<String> name);

    /**
     * Update oos secret parameter
     * @param name name
     * @param value value
     * @return UpdateSecretParameterResponse
     */
    UpdateSecretParameterResponse updateSecretParameter(String name, String value);

    /**
     * Get oos parameter
     * @param name name
     * @return GetParameterResponse
     */
    GetParametersResponse listParameters(List<String> name);

    /**
     * Update oos parameter
     * @param name name
     * @param value value
     * @return UpdateParameterResponse
     */
    UpdateParameterResponse updateParameter(String name, String value);

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


