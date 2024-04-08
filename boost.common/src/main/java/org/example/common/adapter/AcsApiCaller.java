package org.example.common.adapter;

import com.aliyuncs.CommonRequest;
import com.aliyuncs.CommonResponse;
import com.aliyuncs.exceptions.ClientException;
import org.example.common.config.AliyunConfig;

public interface AcsApiCaller {

    /**
     * 调用阿里云API
     *
     * @param request
     * @return
     * @throws ClientException
     */
    CommonResponse getCommonResponse(CommonRequest request) throws ClientException;

    /**
     * 创建阿里云Client
     *
     * @param aliyunConfig
     */
    void createClient(AliyunConfig aliyunConfig);

    /**
     * 创建阿里云Client
     *
     * @param accessKeyId
     * @param accessKeySecret
     */
    void createClient(String accessKeyId, String accessKeySecret);

    /**
     * 创建阿里云Client
     *
     * @param accessKeyId
     * @param accessKeySecret
     * @param securityToken
     */
    void createClient(String accessKeyId, String accessKeySecret, String securityToken);

}
