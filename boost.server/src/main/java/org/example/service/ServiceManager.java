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

package org.example.service;

import com.aliyun.computenestsupplier20210521.models.GetServiceTemplateParameterConstraintsRequest;
import com.aliyun.computenestsupplier20210521.models.GetServiceTemplateParameterConstraintsResponse;
import com.aliyun.computenestsupplier20210521.models.GetServiceTemplateParameterConstraintsResponseBody.GetServiceTemplateParameterConstraintsResponseBodyParameterConstraints;
import org.example.common.BaseResult;
import org.example.common.ListResult;
import org.example.common.model.ServiceMetadataModel;
import org.example.common.model.UserInfoModel;
import org.example.common.param.GetServiceCostParam;
import org.example.common.param.GetServiceMetadataParam;

public interface ServiceManager {

    /**
     * Get ROS service template parameter constraints.
     *
     * @param request
     * @return {@link GetServiceTemplateParameterConstraintsResponse}
     */
    ListResult<GetServiceTemplateParameterConstraintsResponseBodyParameterConstraints> getServiceTemplateParameterConstraints(GetServiceTemplateParameterConstraintsRequest request);


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
}
