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

package org.example.common.helper.oss;


import javax.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.example.common.BaseResult;
import org.example.common.adapter.OssClient;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class BaseOssHelper {

    @Resource
    private OssClient ossClient;
    
    public BaseResult<Boolean> putCert(String bucketName, String certName, String certContent) {
        return ossClient.putObject(bucketName, certName, certContent);
    }
    
    public BaseResult<Boolean> deleteCert(String bucketName, String certName) {
        return ossClient.deleteObject(bucketName, certName);
    }
}
