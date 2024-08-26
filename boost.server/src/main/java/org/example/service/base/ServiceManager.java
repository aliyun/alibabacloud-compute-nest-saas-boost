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

package org.example.service.base;

import com.aliyun.computenestsupplier20210521.models.GetServiceTemplateParameterConstraintsResponse;
import com.aliyun.computenestsupplier20210521.models.GetServiceTemplateParameterConstraintsResponseBody.GetServiceTemplateParameterConstraintsResponseBodyParameterConstraints;
import org.example.common.BaseResult;
import org.example.common.ListResult;
import org.example.common.model.ServiceMetadataModel;
import org.example.common.model.ServiceVersionModel;
import org.example.common.model.UserInfoModel;
import org.example.common.param.GetServiceCostParam;
import org.example.common.param.service.GetServiceMetadataParam;
import org.example.common.param.service.GetServiceTemplateParameterConstraintsParam;
import org.example.common.param.service.ListServicesParam;

import java.util.List;

public interface ServiceManager {

    /**
     * Get ROS service template parameter constraints.
     *
     * @param getServiceTemplateParameterConstraintsParam
     * @return {@link GetServiceTemplateParameterConstraintsResponse}
     */
    ListResult<GetServiceTemplateParameterConstraintsResponseBodyParameterConstraints> getServiceTemplateParameterConstraints(GetServiceTemplateParameterConstraintsParam getServiceTemplateParameterConstraintsParam);


    /**
     * Query the cost of the current service package.
     * @param param GetServiceCostParam
     * @param userInfoModel UserInfo
     * @return {@link BaseResult<Double>}
     */
    BaseResult<Double> getServiceCost(UserInfoModel userInfoModel, GetServiceCostParam param);

    /**
     * Get the compute nest metadata
     * @param userInfoModel userInfoModel
     * @param getServiceMetadataParam getServiceMetadataParam
     * @return {@link BaseResult < ServiceMetadataModel >}
     */
    BaseResult<ServiceMetadataModel> getServiceMetadata(UserInfoModel userInfoModel, GetServiceMetadataParam getServiceMetadataParam);

    /**
     * Bind commodity to service
     * @param serviceId serviceId
     * @param commodityCode commodityCode
     * @param publicAccessUrl publicAccessUrl
     * @param serviceVersion serviceVersion
     * @return {@link BaseResult<Void>}
     */
    BaseResult<Void> bindCommodity(String serviceId, String commodityCode, String publicAccessUrl, String serviceVersion);

    /**
     * List all service versions
     * @param listServiceVersionsParam listServiceVersionsParam
     * @param userInfoModel userInfoModel
     * @return {@link List<ServiceVersionModel>
     */
    List<ServiceVersionModel> listServices(UserInfoModel userInfoModel, ListServicesParam listServiceVersionsParam);
}
