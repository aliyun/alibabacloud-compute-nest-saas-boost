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

import com.aliyun.computenestsupplier20210521.models.ContinueDeployServiceInstanceRequest;
import com.aliyun.computenestsupplier20210521.models.ContinueDeployServiceInstanceResponse;
import com.aliyun.computenestsupplier20210521.models.CreateServiceInstanceResponse;
import org.example.common.BaseResult;
import org.example.common.ListResult;
import org.example.common.model.ServiceMetadataModel;
import org.example.common.model.ServiceInstanceModel;
import org.example.common.model.UserInfoModel;
import org.example.common.param.GetServiceCostParam;
import org.example.common.param.GetServiceInstanceParam;
import org.example.common.param.GetServiceMetadataParam;
import org.example.common.param.ListServiceInstancesParam;

import java.util.Map;

public interface ServiceInstanceLifecycleService {

    /**
     * Get the service instances created by the current user.
     * @param userInfoModel userinfo
     * @param listServiceInstancesParam listServiceInstancesParam
     * @return {@link ListResult<ServiceInstanceModel>}
     */
    ListResult<ServiceInstanceModel> listServiceInstances(UserInfoModel userInfoModel, ListServiceInstancesParam listServiceInstancesParam);

    /**
     * Get detailed information of service instances that are queryable by the current user.
     * @param userInfoModel user information
     * @param getServiceInstanceParam serviceInstanceId
     * @return {@link BaseResult<ServiceInstanceModel>}
     */
    BaseResult<ServiceInstanceModel> getServiceInstance(UserInfoModel userInfoModel, GetServiceInstanceParam getServiceInstanceParam);

    /**
     * Deploy new nest service instance.
     * @param userInfoModel user information
     * @param map map
     * @param dryRun Attempt creation
     * @return {@link CreateServiceInstanceResponse}
     */
    CreateServiceInstanceResponse createServiceInstance(UserInfoModel userInfoModel, Map<String, Object> map, boolean dryRun);

    /**
     * Continue to deploy service instances that were not successfully deployed under the current user.
     * @param request ContinueDeployServiceInstanceRequest
     * @return {@link ContinueDeployServiceInstanceResponse}
     * @throws Exception exception
     */
    ContinueDeployServiceInstanceResponse continueDeployServiceInstance(ContinueDeployServiceInstanceRequest request) throws Exception;

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
     * @return {@link BaseResult<ServiceMetadataModel>}
     */
    BaseResult<ServiceMetadataModel> getServiceMetadata(UserInfoModel userInfoModel, GetServiceMetadataParam getServiceMetadataParam);
}
