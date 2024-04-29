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
package org.example.service.certificate.impl;

import javax.annotation.Resource;
import org.example.common.BaseResult;
import org.example.common.constant.StorageType;
import org.example.common.helper.LocalCertStorageHelper;
import org.example.common.helper.oss.BaseOssHelper;
import org.example.common.model.UserInfoModel;
import org.example.common.param.cert.DeleteCertParam;
import org.example.common.param.cert.PutCertParam;
import org.example.service.certificate.CertificateService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class CertificateServiceImpl implements CertificateService {

    @Resource
    private BaseOssHelper baseOssHelper;

    @Resource
    private LocalCertStorageHelper localCertStorageHelper;

    @Value("${stack-name}")
    private String stackName;

    private final String SUCCESS = "200";

    private final String bucketName = stackName;

    @Override
    public BaseResult<Boolean> putCert(UserInfoModel userInfoModel, PutCertParam param) {
        String certName = String.format("%s/%s", param.getPayChannel().getDisplayName(), param.getCertName());
        String storageMethod = param.getStorageMethod();

        if (storageMethod.equals(StorageType.LOCAL.getMethod())) {
            return localCertStorageHelper.putCert(certName, param.getCertContent());
        }
        if (storageMethod.equals(StorageType.OSS.getMethod())) {
            return baseOssHelper.putCert(bucketName, certName, param.getCertContent());
        }
        if (storageMethod.equals(StorageType.BOTH.getMethod())) {
            BaseResult<Boolean> localResult = localCertStorageHelper.putCert(certName, param.getCertContent());
            BaseResult<Boolean> ossResult = baseOssHelper.putCert(bucketName, certName, param.getCertContent());
            if (localResult.getCode().equals(SUCCESS) && ossResult.getCode().equals(SUCCESS)) {
                return BaseResult.success(true);
            }
            return BaseResult.fail("Failed to put cert to both local and oss, local message:" +
                    localResult.getMessage() + ", oss message:" + ossResult.getMessage());
        }
        return BaseResult.fail("Invalid storage method");
    }

    @Override
    public BaseResult<Boolean> deleteCert(UserInfoModel userInfoModel, DeleteCertParam param) {
        String certName = String.format("%s/%s", param.getPayChannel().getDisplayName(), param.getCertName());
        String storageMethod = param.getStorageMethod();

        if (storageMethod.equals(StorageType.LOCAL.getMethod())) {
            return localCertStorageHelper.deleteCert(certName);
        }
        if (storageMethod.equals(StorageType.OSS.getMethod())) {
            return baseOssHelper.deleteCert(bucketName, certName);
        }
        if (storageMethod.equals(StorageType.BOTH.getMethod())) {
            BaseResult<Boolean> localResult = localCertStorageHelper.deleteCert(certName);
            BaseResult<Boolean> ossResult = baseOssHelper.deleteCert(bucketName, certName);
            if (localResult.getCode().equals(SUCCESS) && ossResult.getCode().equals(SUCCESS)) {
                return BaseResult.success(true);
            }
            return BaseResult.fail("Failed to delete cert to both local and oss, local message:" +
                    localResult.getMessage() + ", oss message:" + ossResult.getMessage());
        }
        return BaseResult.fail("Invalid storage method");
    }
}

