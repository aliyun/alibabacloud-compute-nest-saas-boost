package org.example.common.adapter.impl;

import com.aliyuncs.CommonRequest;
import com.aliyuncs.CommonResponse;
import com.aliyuncs.DefaultAcsClient;
import com.aliyuncs.IAcsClient;
import com.aliyuncs.exceptions.ClientException;
import com.aliyuncs.exceptions.ServerException;
import com.aliyuncs.profile.DefaultProfile;
import com.aliyuncs.profile.IClientProfile;
import lombok.extern.slf4j.Slf4j;
import org.example.common.adapter.AcsApiCaller;
import org.example.common.config.AliyunConfig;
import org.example.common.constant.ComputeNestConstants;
import org.example.common.utils.JsonUtil;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class AcsApiCallerImpl implements AcsApiCaller {

    private IAcsClient client;

    @Override
    public CommonResponse getCommonResponse(CommonRequest request) throws ClientException {
        try {
            CommonResponse commonResponse = this.client.getCommonResponse(request);
            log.info("CommonApiCall request:{}, response:{}", JsonUtil.toJsonString(request), JsonUtil.toJsonString(commonResponse));
            return commonResponse;
        } catch (ServerException e) {
            log.error("CommonApiCall request:{}, throw serverException", JsonUtil.toJsonString(request), e);
            throw e;
        } catch (ClientException e) {
            log.warn("ApiCall request:{}, throw clientException", JsonUtil.toJsonString(request), e);
            throw e;
        } catch (Exception e) {
            log.error("CommonApiCall request:{}, throw Exception", JsonUtil.toJsonString(request), e);
            throw e;
        }
    }

    @Override
    public void createClient(AliyunConfig aliyunConfig) {
        try {
            String accessKeyId = aliyunConfig.getClient().getAccessKeyId();
            String accessKeySecret = aliyunConfig.getClient().getAccessKeySecret();
            String securityToken = aliyunConfig.getClient().getSecurityToken();
            IClientProfile profile = DefaultProfile.getProfile(ComputeNestConstants.DEFAULT_REGION_ID, accessKeyId,
                    accessKeySecret, securityToken);

            this.client = new DefaultAcsClient(profile);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void createClient(String accessKeyId, String accessKeySecret) {
        try {
            IClientProfile profile = DefaultProfile.getProfile(ComputeNestConstants.DEFAULT_REGION_ID, accessKeyId,
                    accessKeySecret);

            this.client = new DefaultAcsClient(profile);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void createClient(String accessKeyId, String accessKeySecret, String securityToken) {
        try {
            IClientProfile profile = DefaultProfile.getProfile(ComputeNestConstants.DEFAULT_REGION_ID, accessKeyId,
                    accessKeySecret, securityToken);

            this.client = new DefaultAcsClient(profile);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
