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

package org.example.service.certificate;

import javax.validation.Valid;
import org.example.common.BaseResult;
import org.example.common.model.UserInfoModel;
import org.example.common.param.cert.DeleteCertParam;
import org.example.common.param.cert.PutCertParam;

public interface CertificateService {

    /**
     * put certificate.
     * @param userInfoModel UserInfo
     * @param putCertParam PutCertParam
     * @return {@link BaseResult<Boolean>}
     */
    BaseResult<Boolean> putCert(UserInfoModel userInfoModel, @Valid PutCertParam putCertParam);

    /**
     * Delete certificate.
     * @param userInfoModel UserInfo
     * @param deleteCertParam DeleteCertParam
     * @return {@link BaseResult<Boolean>}
     */
    BaseResult<Boolean> deleteCert(UserInfoModel userInfoModel, @Valid DeleteCertParam deleteCertParam);
}
