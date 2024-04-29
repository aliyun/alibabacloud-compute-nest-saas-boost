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

import org.example.common.BaseResult;
import org.example.common.ListResult;
import org.example.common.config.AliyunConfig;

public interface OssClient {

    /**
     * Create oss client by ecs ram role
     * @param aliyunConfig aliyun config
     * @throws Exception Common exception
     */
    void createClient(AliyunConfig aliyunConfig) throws Exception;

    /**
     * Create oss client by fc header;
     * @param accessKeyId accessKeyId
     * @param accessKeySecret accessKeySecret
     * @param securityToken securityToken
     * @throws Exception Common exception
     */
    void createClient(String accessKeyId, String accessKeySecret, String securityToken) throws Exception;

    /**
     * Create oss client by ak;
     * @param accessKeyId accessKeyId
     * @param accessKeySecret accessKeySecret
     * @throws Exception Common exception
     */
    void createClient(String accessKeyId, String accessKeySecret) throws Exception;

    /**
     * Put object.
     * @param bucketName  Bucket name
     * @param objectName    Object name
     * @param objectContent Object content
     * @return {@link BaseResult<Boolean>}
     */
    BaseResult<Boolean> putObject(String bucketName, String objectName, String objectContent);

    /**
     * List objects.
     * @param bucketName Bucket name
     * @param keyPrefix Key prefix
     * @return {@link ListResult<String>}
     */
    ListResult<String> listObjects(String bucketName, String keyPrefix);

    /**
     * Delete object.
     * @param bucketName  Bucket name
     * @param objectName    Object name
     * @return {@link BaseResult<Boolean>}
     */
    BaseResult<Boolean> deleteObject(String bucketName, String objectName);

    /**
     * Get object URL with sign.
     * @param bucketName Bucket name
     * @param objectName Object name
     * @return {@link BaseResult<String>}
     */
    BaseResult<String> getObjectUrlWithSign(String bucketName, String objectName);

    BaseResult<String> getObjectContent(String bucketName, String objectName);
}
