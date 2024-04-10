package org.example.common.adapter;

import com.aliyuncs.CommonRequest;
import com.aliyuncs.CommonResponse;
import com.aliyuncs.exceptions.ClientException;
import org.example.common.config.AliyunConfig;

public interface AcsApiCaller {

    /**
     * request aliyun api
     *
     * @param request
     * @return
     * @throws ClientException
     */
    CommonResponse getCommonResponse(CommonRequest request) throws ClientException;

    /**
     * create aliyun ak client
     *
     * @param aliyunConfig
     */
    void createClient(AliyunConfig aliyunConfig);

    /**
     * create aliyun ak client
     *
     * @param accessKeyId
     * @param accessKeySecret
     */
    void createClient(String accessKeyId, String accessKeySecret);

    /**
     * create aliyun ak client
     *
     * @param accessKeyId
     * @param accessKeySecret
     * @param securityToken
     */
    void createClient(String accessKeyId, String accessKeySecret, String securityToken);

}
