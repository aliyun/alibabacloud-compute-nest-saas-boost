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
import org.example.common.ListResult;
import static org.example.common.constant.Constants.BUCKET;
import static org.example.common.constant.Constants.SAAS_BOOST;
import org.example.common.constant.PayChannel;
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

    @Value("${stack-name}")
    private String stackName;

    @Override
    public BaseResult<Boolean> putCert(UserInfoModel userInfoModel, PutCertParam param) {
        String bucketName = String.format("%s-%s-%s", SAAS_BOOST, stackName, BUCKET);
        String certName = String.format("%s/%s", param.getPayChannel().getDisplayName(), param.getCertName());
        return baseOssHelper.putCert(bucketName, certName, param.getCertContent());
    }

    @Override
    public ListResult<String> listCerts(UserInfoModel userInfoModel, PayChannel payChannel) {
        String bucketName = String.format("%s-%s-%s", SAAS_BOOST, stackName, BUCKET);
        String keyPrefix = String.format("%s/", payChannel.getDisplayName());
        return baseOssHelper.listCerts(bucketName, keyPrefix);
    }

    @Override
    public BaseResult<Boolean> deleteCert(UserInfoModel userInfoModel, DeleteCertParam param) {
        String bucketName = String.format("%s-%s-%s", SAAS_BOOST, stackName, BUCKET);
        String certName = String.format("%s/%s", param.getPayChannel().getDisplayName(), param.getCertName());
        return baseOssHelper.deleteCert(bucketName, certName);
    }
}

