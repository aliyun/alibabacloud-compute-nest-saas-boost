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

package org.example.common.helper;

import com.aliyuncs.CommonRequest;
import com.aliyuncs.CommonResponse;
import com.aliyuncs.exceptions.ClientException;
import com.aliyuncs.http.MethodType;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.example.common.adapter.AcsApiCaller;
import org.example.common.dto.CommodityDTO;
import org.example.common.errorinfo.ErrorInfo;
import org.example.common.exception.BizException;
import org.example.common.helper.ots.CommodityOtsHelper;
import org.example.common.utils.JsonUtil;
import org.example.common.utils.TokenUtil;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Map;

@Component
@Slf4j
public class SpiTokenHelper {

    @Resource
    private AcsApiCaller acsApiCaller;

    @Resource
    private CommodityOtsHelper commodityOtsHelper;

    private static final String GET_SERVICE_PROVIDER_KEY = "GetServiceProviderKey";

    private static final String SERVICE_KEY = "ServiceKey";

    public void checkSpiToken(Object param, String spiToken, String commodityCode) {
        CommodityDTO commodity = commodityOtsHelper.getCommodity(commodityCode);
        CommonRequest commonRequest = initialCommonRequest(commodity.getServiceId());
        try {
            CommonResponse commonResponse = acsApiCaller.getCommonResponse(commonRequest);
            String data = commonResponse.getData();
            if (StringUtils.isNotEmpty(data)) {
                Map<String, String> map = JsonUtil.parseObject(data, Map.class);
                String serviceKey = map.getOrDefault(SERVICE_KEY, null);
                String currentSpiToken = TokenUtil.createSpiToken(param, serviceKey);
                if (StringUtils.isEmpty(currentSpiToken) || !currentSpiToken.equals(spiToken)) {
                    throw new BizException(ErrorInfo.SPI_TOKEN_VALIDATION_FAILED.getStatusCode(), ErrorInfo.SPI_TOKEN_VALIDATION_FAILED.getCode(), String.format(ErrorInfo.SPI_TOKEN_VALIDATION_FAILED.getMessage(), spiToken));
                }
                return;
            }
            throw new BizException(ErrorInfo.SERVICE_PROVIDER_KEY_NOT_EXIST.getStatusCode(), ErrorInfo.SERVICE_PROVIDER_KEY_NOT_EXIST.getCode(), String.format(ErrorInfo.SERVICE_PROVIDER_KEY_NOT_EXIST.getMessage(), commodity.getServiceId(), commodityCode));
        } catch (ClientException e) {
            throw new BizException(ErrorInfo.COMMON_REQUEST_FAILED.getStatusCode(), e.getErrCode(), String.format(ErrorInfo.COMMON_REQUEST_FAILED.getMessage(), GET_SERVICE_PROVIDER_KEY, JsonUtil.toJsonString(commonRequest), e.getMessage()), e);
        }
    }

    private CommonRequest initialCommonRequest(String serviceId) {
        CommonRequest request = new CommonRequest();
        request.setSysRegionId("cn-hangzhou");
        request.setSysProduct("ComputeNestSupplier");
        request.setSysMethod(MethodType.POST);
        request.setSysDomain("computenestsupplier.cn-hangzhou.aliyuncs.com");
        request.setSysVersion("2021-05-21");
        request.setSysAction(GET_SERVICE_PROVIDER_KEY);
        request.putQueryParameter("ServiceId", serviceId);
        return request;
    }
}
