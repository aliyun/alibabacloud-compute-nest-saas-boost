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

package org.example.common.adapter.impl;

import com.aliyun.oss.HttpMethod;
import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import com.aliyun.oss.model.GeneratePresignedUrlRequest;
import com.aliyun.oss.model.PutObjectResult;
import com.aliyun.oss.model.VoidResult;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import lombok.extern.slf4j.Slf4j;
import org.example.common.BaseResult;
import org.example.common.adapter.OssClient;
import org.example.common.config.AliyunConfig;
import org.example.common.errorinfo.ErrorInfo;
import org.example.common.exception.BizException;
import org.example.common.utils.JsonUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/**
 * @author mengjunwei.mjw
 */
@Service
@Slf4j
public class OssClientImpl implements OssClient {

    private OSS client;

    @Value("${service.region-id}")
    private String regionId;

    private static final String NEW_LINE = "\n";

    private static final String OSS_ENDPOINT = "oss-%s.aliyuncs.com";

    @Override
    public BaseResult<Boolean> putObject(String bucketName, String objectName, String objectContent) {
        try {
            PutObjectResult putObjectResult = client.putObject(bucketName, objectName,
                    new ByteArrayInputStream(objectContent.getBytes()));
            if (putObjectResult.getResponse() == null ||
                    putObjectResult.getResponse().getErrorResponseAsString() == null) {
                return BaseResult.success(Boolean.TRUE);
            }
            return BaseResult.fail(putObjectResult.getResponse().getErrorResponseAsString());
        } catch (Exception e) {
            log.error("ossClient.putObject request:{}{}, throw Exception", JsonUtil.toJsonString(bucketName),
                    JsonUtil.toJsonString(objectName), e);
            throw new BizException(ErrorInfo.OBJECT_UPLOAD_FAILED.getStatusCode(), ErrorInfo.OBJECT_UPLOAD_FAILED.getCode(),
                    String.format(ErrorInfo.OBJECT_UPLOAD_FAILED.getMessage(), e.getMessage()), e);
        }
    }

    @Override
    public BaseResult<Boolean> deleteObject(String bucketName, String objectName) {
        if (doesObjectExist(bucketName, objectName)){
            try {
                VoidResult voidResult = client.deleteObject(bucketName, objectName);
                if (voidResult.getResponse() == null || voidResult.getResponse().getErrorResponseAsString() == null) {
                    return BaseResult.success(Boolean.TRUE);
                }
                return BaseResult.fail(voidResult.getResponse().getErrorResponseAsString());
            } catch (Exception e) {
                log.error("ossClient.deleteObject request:{}{}, throw Exception", JsonUtil.toJsonString(bucketName),
                        JsonUtil.toJsonString(objectName), e);
                throw new BizException(ErrorInfo.OBJECT_DELETION_FAILED.getStatusCode(), ErrorInfo.OBJECT_DELETION_FAILED.getCode(),
                        String.format(ErrorInfo.OBJECT_DELETION_FAILED.getMessage(), e.getMessage()), e);
            }
        }
        return BaseResult.fail("Object does not exist");
    }

    @Override
    public BaseResult<String> getObjectUrlWithSign(String bucketName, String objectName) {
        if (doesObjectExist(bucketName, objectName)){
            try {
                Date expiration = new Date(System.currentTimeMillis() + 60 * 1000L);
                GeneratePresignedUrlRequest request = new GeneratePresignedUrlRequest(bucketName, objectName, HttpMethod.GET);
                request.setExpiration(expiration);
                URL signedUrl = client.generatePresignedUrl(request);
                return BaseResult.success(signedUrl.toString());
            } catch (Exception e) {
                log.error("ossClient.getObjectUrlWithSign request:{}{}, throw Exception", JsonUtil.toJsonString(bucketName),
                        JsonUtil.toJsonString(objectName), e);
                throw new BizException(ErrorInfo.OBJECT_RETRIEVAL_FAILED.getStatusCode(), ErrorInfo.OBJECT_RETRIEVAL_FAILED.getCode(),
                        String.format(ErrorInfo.OBJECT_RETRIEVAL_FAILED.getMessage(), e.getMessage()), e);
            }
        }
        return BaseResult.fail("Object does not exist");
    }

    @Override
    public BaseResult<String> getObjectContent(String bucketName, String objectName) {
        if (doesObjectExist(bucketName, objectName)){
            try {
                InputStream inputStream = client.getObject(bucketName, objectName).getObjectContent();
                StringBuilder stringBuilder = new StringBuilder();
                BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));
                String line;
                while ((line = reader.readLine()) != null) {
                    stringBuilder.append(line);
                    stringBuilder.append(NEW_LINE);
                }
                if (stringBuilder.length() > 0) {
                    stringBuilder.deleteCharAt(stringBuilder.length() - 1);
                }
                String fileContent = stringBuilder.toString();
                return BaseResult.success(fileContent);
            } catch (Exception e) {
                log.error("ossClient.getObjectContent request:{}{}, throw Exception", JsonUtil.toJsonString(bucketName),
                        JsonUtil.toJsonString(objectName), e);
                throw new BizException(ErrorInfo.OBJECT_RETRIEVAL_FAILED.getStatusCode(), ErrorInfo.OBJECT_RETRIEVAL_FAILED.getCode(),
                        String.format(ErrorInfo.OBJECT_RETRIEVAL_FAILED.getMessage(), e.getMessage()), e);
            }
        }
        return BaseResult.fail("Object does not exist");
    }

    @Override
    public void createClient(AliyunConfig aliyunConfig) throws Exception {
        String endPoint = String.format(OSS_ENDPOINT, regionId);
        this.client = new OSSClientBuilder().build(endPoint, aliyunConfig.getClient().getAccessKeyId(),
                aliyunConfig.getClient().getAccessKeySecret(), aliyunConfig.getClient().getSecurityToken());
    }

    @Override
    public void createClient(String accessKeyId, String accessKeySecret, String securityToken) throws Exception {
        String endPoint = String.format(OSS_ENDPOINT, regionId);
        this.client = new OSSClientBuilder().build(endPoint, accessKeyId, accessKeySecret, securityToken);
    }

    @Override
    public void createClient(String accessKeyId, String accessKeySecret) throws Exception {
        String endPoint = String.format(OSS_ENDPOINT, regionId);
        this.client = new OSSClientBuilder().build(endPoint, accessKeyId, accessKeySecret);
    }

    private boolean doesObjectExist(String bucketName, String objectName) {
        try {
            return client.doesObjectExist(bucketName, objectName);
        } catch (Exception e) {
            log.error("ossClient.doesObjectExist request:{}, throw Exception", JsonUtil.toJsonString(bucketName), e);
            throw new BizException(ErrorInfo.OBJECT_EXISTENCE_CHECK_FAILED);
        }
    }
}
