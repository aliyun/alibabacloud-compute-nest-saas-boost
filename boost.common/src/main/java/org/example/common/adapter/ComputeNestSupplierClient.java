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

package org.example.common.adapter;

import com.aliyun.computenestsupplier20210521.models.ContinueDeployServiceInstanceRequest;
import com.aliyun.computenestsupplier20210521.models.ContinueDeployServiceInstanceResponse;
import com.aliyun.computenestsupplier20210521.models.CreateServiceInstanceRequest;
import com.aliyun.computenestsupplier20210521.models.CreateServiceInstanceResponse;
import com.aliyun.computenestsupplier20210521.models.DeleteServiceInstancesRequest;
import com.aliyun.computenestsupplier20210521.models.DeleteServiceInstancesResponse;
import com.aliyun.computenestsupplier20210521.models.GetServiceInstanceRequest;
import com.aliyun.computenestsupplier20210521.models.GetServiceInstanceResponse;
import com.aliyun.computenestsupplier20210521.models.GetServiceRequest;
import com.aliyun.computenestsupplier20210521.models.GetServiceResponse;
import com.aliyun.computenestsupplier20210521.models.ListServiceInstancesRequest;
import com.aliyun.computenestsupplier20210521.models.ListServiceInstancesResponse;
import org.example.common.config.AliyunConfig;

public interface ComputeNestSupplierClient {

    /**
     * List the created instances of the Compute Nest.
     * @param request request
     * @return {@link ListServiceInstancesResponse}
     * @throws Exception exception
     */
    ListServiceInstancesResponse listServiceInstances(ListServiceInstancesRequest request) throws Exception;

    /**
     * Get detailed information of a service instance.
     * @param request request
     * @return {@link GetServiceInstanceResponse}
     * @throws Exception exception
     */
    GetServiceInstanceResponse getServiceInstance(GetServiceInstanceRequest request) throws Exception;

    /**
     * Create service instance
     * @param request request
     * @return {@link CreateServiceInstanceResponse}
     * @throws Exception bizException
     */
    CreateServiceInstanceResponse createServiceInstance(CreateServiceInstanceRequest request);

    /**
     * Continue deploy service instance.
     * @param request request
     * @return {@link ContinueDeployServiceInstanceResponse}
     * @throws Exception exception
     */
    ContinueDeployServiceInstanceResponse continueDeployServiceInstance(ContinueDeployServiceInstanceRequest request);

    /**
     * Delete service instance.
     * @param deleteServiceInstancesRequest DeleteServiceInstancesRequest
     * @return DeleteServiceInstancesResponse
     */
    DeleteServiceInstancesResponse deleteServiceInstance(DeleteServiceInstancesRequest deleteServiceInstancesRequest);

    /**
     * Create compute nest supplier client by ecs ram role
     * @param aliyunConfig aliyun config
     * @throws Exception Common exception
     */
    void createClient(AliyunConfig aliyunConfig) throws Exception;

    /**
     * Create compute nest supplier client by fc header;
     * @param accessKeyId accessKeyId
     * @param accessKeySecret accessKeySecret
     * @param securityToken securityToken
     * @throws Exception Common exception
     */
    void createClient(String accessKeyId, String accessKeySecret, String securityToken) throws Exception;

    /**
     * Get compute nest service info
     * @param request request
     * @return {@link GetServiceResponse}
     */
    GetServiceResponse getService(GetServiceRequest request);
}
