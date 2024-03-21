package org.example.common.adapter.impl;

import com.aliyuncs.CommonRequest;
import com.aliyuncs.CommonResponse;
import com.aliyuncs.DefaultAcsClient;
import com.aliyuncs.IAcsClient;
import com.aliyuncs.exceptions.ClientException;
import com.aliyuncs.exceptions.ServerException;
import com.aliyuncs.http.MethodType;
import com.aliyuncs.profile.DefaultProfile;
import com.aliyuncs.profile.IClientProfile;
import lombok.extern.slf4j.Slf4j;
import org.example.common.adapter.AcsApiCaller;
import org.example.common.config.AliyunConfig;
import org.example.common.utils.JsonUtil;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class AcsApiCallerImpl implements AcsApiCaller {

    private IAcsClient client;

    private static final String DEFAULT_REGION_ID = "cn-hangzhou";


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
            IClientProfile profile = DefaultProfile.getProfile(DEFAULT_REGION_ID, accessKeyId,
                    accessKeySecret, securityToken);

            this.client = new DefaultAcsClient(profile);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void createClient(String accessKeyId, String accessKeySecret) {
        try {
            IClientProfile profile = DefaultProfile.getProfile(DEFAULT_REGION_ID, accessKeyId,
                    accessKeySecret);

            this.client = new DefaultAcsClient(profile);
            getCommonResponse(test());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private CommonRequest test() {
        CommonRequest request = new CommonRequest();
        request.setSysRegionId("cn-hangzhou");
        request.setSysProduct("ComputeNestSupplier");
        request.setSysMethod(MethodType.POST);
        request.setSysDomain("computenestsupplier.cn-hangzhou.aliyuncs.com");
        request.setSysVersion("2021-05-21");
        request.setSysAction("GetService");
        request.putQueryParameter("ServiceId", "service-70b5fa5c9d784e80ae47");
        request.putQueryParameter("ServiceVersion", "1");
        request.putQueryParameter("RegionId", "cn-hangzhou");
        return request;
    }
}
