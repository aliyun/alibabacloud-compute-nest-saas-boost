package org.example.common.adapter;

import com.aliyuncs.CommonRequest;
import com.aliyuncs.CommonResponse;
import com.aliyuncs.exceptions.ClientException;
import org.example.common.config.AliyunConfig;

public interface AcsApiCaller {

    CommonResponse getCommonResponse(CommonRequest request) throws ClientException;

    void createClient(AliyunConfig aliyunConfig);

    void createClient(String accessKeyId, String accessKeySecret);

}
