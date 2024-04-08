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
import org.example.common.constant.ComputeNestConstants;
import org.example.common.dto.CommodityDTO;
import org.example.common.errorinfo.ErrorInfo;
import org.example.common.exception.BizException;
import org.example.common.helper.ots.CommodityOtsHelper;
import org.example.common.utils.JsonUtil;
import org.example.common.utils.TokenUtil;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
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

    public boolean checkSpiToken(Object param, Class<? extends Object> paramClass) {
        CommonRequest commonRequest = null;
        try {
            Method getTokenMethod = paramClass.getMethod("getToken");
            String spiToken = (String) getTokenMethod.invoke(param);
            Method getCommodityCode = paramClass.getMethod("getCommodityCode");
            String commodityCode = (String) getCommodityCode.invoke(param);
            CommodityDTO commodity = commodityOtsHelper.getCommodity(commodityCode);
            commonRequest = initialCommonRequest(commodity.getServiceId());
            CommonResponse commonResponse = acsApiCaller.getCommonResponse(commonRequest);
            String data = commonResponse.getData();
            if (StringUtils.isNotEmpty(data)) {
                Map<String, String> map = JsonUtil.parseObject(data, Map.class);
                String serviceKey = map.getOrDefault(SERVICE_KEY, null);
                String currentSpiToken = TokenUtil.createSpiToken(param, serviceKey);
                if (StringUtils.isEmpty(currentSpiToken) || !currentSpiToken.equals(spiToken)) {
                    throw new BizException(ErrorInfo.SPI_TOKEN_VALIDATION_FAILED.getStatusCode(), ErrorInfo.SPI_TOKEN_VALIDATION_FAILED.getCode(),
                            String.format(ErrorInfo.SPI_TOKEN_VALIDATION_FAILED.getMessage(), spiToken));
                }
                return true;
            }
            throw new BizException(ErrorInfo.SERVICE_PROVIDER_KEY_NOT_EXIST.getStatusCode(), ErrorInfo.SERVICE_PROVIDER_KEY_NOT_EXIST.getCode(),
                    String.format(ErrorInfo.SERVICE_PROVIDER_KEY_NOT_EXIST.getMessage(), commodity.getServiceId(), commodityCode));
        } catch (ClientException e) {
            throw new BizException(ErrorInfo.COMMON_REQUEST_FAILED.getStatusCode(), e.getErrCode(),
                    String.format(ErrorInfo.COMMON_REQUEST_FAILED.getMessage(), GET_SERVICE_PROVIDER_KEY, JsonUtil.toJsonString(commonRequest), e.getMessage()), e);
        } catch (InvocationTargetException | NoSuchMethodException | IllegalAccessException e) {
            throw new BizException(ErrorInfo.INVALID_SPI_PARAMETER.getStatusCode(), ErrorInfo.INVALID_SPI_PARAMETER.getCode(),
                    String.format(ErrorInfo.INVALID_SPI_PARAMETER.getMessage(), paramClass), e);
        }
    }

    private CommonRequest initialCommonRequest(String serviceId) {
        CommonRequest request = new CommonRequest();
        request.setSysRegionId(ComputeNestConstants.DEFAULT_REGION_ID);
        request.setSysProduct(ComputeNestConstants.SUPPLIER_SYS_PRODUCT_NAME);
        request.setSysMethod(MethodType.POST);
        request.setSysDomain(ComputeNestConstants.SERVICE_ENDPOINT);
        request.setSysVersion(ComputeNestConstants.COMPUTE_NEST_SUPPLIER_API_VERSION);
        request.setSysAction(GET_SERVICE_PROVIDER_KEY);
        request.putQueryParameter(ComputeNestConstants.SERVICE_ID, serviceId);
        return request;
    }
}
