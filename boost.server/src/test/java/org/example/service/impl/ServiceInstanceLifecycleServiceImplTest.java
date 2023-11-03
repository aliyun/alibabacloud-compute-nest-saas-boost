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

package org.example.service.impl;

import com.aliyun.computenestsupplier20210521.models.CreateServiceInstanceRequest;
import com.aliyun.computenestsupplier20210521.models.CreateServiceInstanceResponse;
import com.aliyun.computenestsupplier20210521.models.GetServiceInstanceRequest;
import com.aliyun.computenestsupplier20210521.models.GetServiceInstanceResponse;
import com.aliyun.computenestsupplier20210521.models.GetServiceInstanceResponseBody;
import com.aliyun.computenestsupplier20210521.models.GetServiceRequest;
import com.aliyun.computenestsupplier20210521.models.GetServiceResponse;
import com.aliyun.computenestsupplier20210521.models.GetServiceResponseBody;
import com.aliyun.computenestsupplier20210521.models.ListServiceInstancesRequest;
import com.aliyun.computenestsupplier20210521.models.ListServiceInstancesResponse;
import com.aliyun.computenestsupplier20210521.models.ListServiceInstancesResponseBody;
import com.aliyun.computenestsupplier20210521.models.ListServiceInstancesResponseBody.ListServiceInstancesResponseBodyServiceInstances;
import com.aliyun.computenestsupplier20210521.models.ListServiceInstancesResponseBody.ListServiceInstancesResponseBodyServiceInstancesService;
import com.aliyun.computenestsupplier20210521.models.ListServiceInstancesResponseBody.ListServiceInstancesResponseBodyServiceInstancesServiceServiceInfos;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import mockit.Expectations;
import mockit.Injectable;
import mockit.Mocked;
import mockit.Tested;
import mockit.Verifications;
import org.example.common.BaseResult;
import org.example.common.ListResult;
import org.example.common.adapter.ComputeNestSupplierClient;
import org.example.common.config.SpecificationConfig;
import org.example.common.errorinfo.ErrorInfo;
import org.example.common.exception.BizException;
import org.example.common.helper.WalletHelper;
import org.example.common.model.ListServiceInstancesModel;
import org.example.common.model.ServiceInstanceModel;
import org.example.common.model.ServiceMetadataModel;
import org.example.common.model.UserInfoModel;
import org.example.common.param.GetServiceInstanceParam;
import org.example.common.param.GetServiceMetadataParam;
import org.example.common.param.ListServiceInstancesParam;
import org.example.common.utils.HttpUtil;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.http.HttpStatus;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import static org.example.common.constant.ComputeNestConstants.TEMPLATE_CONFIGS;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class ServiceInstanceLifecycleServiceImplTest {

    @Tested
    private ServiceInstanceLifecycleServiceImpl serviceInstanceLifecycleService;

    @Injectable
    private ComputeNestSupplierClient computeNestSupplierClient;

    @Injectable
    private WalletHelper walletHelper;

    @Injectable
    private SpecificationConfig specificationConfig;

    @Mocked
    CacheManager cacheManager;

    @Mocked
    Cache cache;

    @Mocked
    GetServiceResponse getServiceResponse;

    @Mocked
    GetServiceResponseBody getServiceResponseBody;

    @Mocked
    ObjectMapper mapper;

    @Mocked
    JsonNode deployMetadataRootNode;

    private ListServiceInstancesResponseBody createListServiceInstancesResponseBody() {
        ListServiceInstancesResponseBodyServiceInstancesServiceServiceInfos serviceInfos = new ListServiceInstancesResponseBodyServiceInstancesServiceServiceInfos().setName("serviceInfoName-2");
        ListServiceInstancesResponseBodyServiceInstancesService service = new ListServiceInstancesResponseBodyServiceInstancesService().setServiceInfos(Arrays.asList(serviceInfos));
        ListServiceInstancesResponseBodyServiceInstances deployedServiceInstance = new ListServiceInstancesResponseBodyServiceInstances().setServiceInstanceId("si-serviceInstanceId-1")
                .setStatus("deployed").setService(service);
        ListServiceInstancesResponseBody responseBody = new ListServiceInstancesResponseBody().setMaxResults(20)
                .setServiceInstances(Arrays.asList(deployedServiceInstance, deployedServiceInstance));
        responseBody.setTotalCount(2);
        return responseBody;
    }

    @Test
    void listServiceInstances() throws Exception {
        UserInfoModel userInfoModel = new UserInfoModel();
        userInfoModel.setAid("aliYunId");
        ListServiceInstancesParam listServiceInstancesParam = new ListServiceInstancesParam();
        listServiceInstancesParam.setMaxResults(20);
        listServiceInstancesParam.setStatus("deployed");
        listServiceInstancesParam.setServiceInstanceId("si-serviceInstanceId");
        listServiceInstancesParam.setServiceInstanceName("serviceInfoName-2");
        new Expectations() {{
            ListServiceInstancesResponse response = new ListServiceInstancesResponse();
            response.setBody(createListServiceInstancesResponseBody());
            computeNestSupplierClient.listServiceInstances(withAny(new ListServiceInstancesRequest()));
            result = response;
        }};

        ListResult<ServiceInstanceModel> result = serviceInstanceLifecycleService.listServiceInstances(userInfoModel, listServiceInstancesParam);
        assertEquals(String.valueOf(HttpStatus.OK.value()), result.getCode());
        assertEquals(2, result.getCount());
        assertEquals("si-serviceInstanceId-1", result.getData().get(0).getServiceInstanceId());
        assertEquals("deployed", result.getData().get(1).getStatus());

        new Verifications() {{
            ListServiceInstancesRequest request;
            computeNestSupplierClient.listServiceInstances(request = withCapture());
            assertEquals(20, request.getMaxResults());
            assertEquals("ServiceType", request.getFilter().get(0).getName());
            assertEquals(Arrays.asList("managed"), request.getFilter().get(0).getValue());
        }};
    }

    @Test
    void listServiceInstancesWithBlankAid() {
        UserInfoModel userInfoModel = new UserInfoModel();
        ListServiceInstancesParam listServiceInstancesParam = new ListServiceInstancesParam();
        try {
            BaseResult<ListServiceInstancesModel> result = serviceInstanceLifecycleService.listServiceInstances(userInfoModel, listServiceInstancesParam);
            assert false;
        } catch (BizException bizException) {
            assertEquals(ErrorInfo.VERIFY_FAILED.getStatusCode(), bizException.getStatusCode());
            assertEquals(ErrorInfo.VERIFY_FAILED.getCode(), bizException.getCode());
            assertEquals(ErrorInfo.VERIFY_FAILED.getMessage(), bizException.getMessage());
        }
    }


    @Test
    void getServiceInstance() throws Exception {
        UserInfoModel userInfoModel = new UserInfoModel();
        userInfoModel.setAid("1000000000000000");
        GetServiceInstanceParam getServiceInstanceParam = new GetServiceInstanceParam("si-serviceInstanceId");
        new Expectations() {{
            GetServiceInstanceResponse response = new GetServiceInstanceResponse();
            response.setBody(new GetServiceInstanceResponseBody().setServiceInstanceId("si-serviceInstanceId")
                    .setUserId(1000000000000000L)
                    .setService(new GetServiceInstanceResponseBody.GetServiceInstanceResponseBodyService()
                            .setServiceInfos(Arrays.asList(new GetServiceInstanceResponseBody.GetServiceInstanceResponseBodyServiceServiceInfos()
                                    .setName("si-serviceInstanceName")
                                    .setImage("image")
                                    .setShortDescription("shortDescription")))));
            computeNestSupplierClient.getServiceInstance(withAny(new GetServiceInstanceRequest()));
            result = response;
        }};

        BaseResult<ServiceInstanceModel> result = serviceInstanceLifecycleService.getServiceInstance(userInfoModel, getServiceInstanceParam);
        assertEquals(String.valueOf(HttpStatus.OK.value()), result.getCode());
        assertEquals("si-serviceInstanceId", result.getData().getServiceInstanceId());
        assertEquals("shortDescription", result.getData().getServiceModel().getDescription());

        new Verifications() {{
            GetServiceInstanceRequest request;
            computeNestSupplierClient.getServiceInstance(request = withCapture());
            assert request != null;
            assertEquals("si-serviceInstanceId", request.getServiceInstanceId());
        }};
    }

    @Test
    public void testCreateServiceInstance() {
        new Expectations() {{
            computeNestSupplierClient.createServiceInstance(withAny(new CreateServiceInstanceRequest()));
            result = new CreateServiceInstanceResponse();
        }};
        Map<String, Object> map = new HashMap<>();
        map.put("RegionId", "cn-hangzhou");
        UserInfoModel userInfoModel = new UserInfoModel();
        userInfoModel.setAid("100");
        Assertions.assertDoesNotThrow(() -> serviceInstanceLifecycleService.createServiceInstance(userInfoModel, map, true));
    }

    @Test
    public void testGetServiceMetadata(@Mocked HttpUtil httpUtil, @Mocked GetServiceRequest getServiceRequest) throws JsonProcessingException {
        String rosTemplate = "{\n" +
                "  \"ROSTemplateFormatVersion\": \"2015-09-01\",\n" +
                "  \"Description\": \"a\",\n" +
                "  \"Parameters\": {\n" +
                "    \"ZoneId\": {\n" +
                "      \"Type\": \"String\",\n" +
                "      \"AssociationProperty\": \"ALIYUN::ECS::Instance::ZoneId\",\n" +
                "      \"Label\": {\n" +
                "        \"en\": \"VSwitch Availability Zone\",\n" +
                "        \"zh-cn\": \"交换机可用区\"\n" +
                "      }\n" +
                "    }\n" +
                "  },\n" +
                "  \"Resources\": {\n" +
                "    \"EcsSecurityGroup\": {\n" +
                "      \"Type\": \"ALIYUN::ECS::SecurityGroup\",\n" +
                "      \"Properties\": {\n" +
                "        \"SecurityGroupName\": null\n" +
                "      }\n" +
                "    }\n" +
                "  },\n" +
                "  \"Metadata\": {\n" +
                "    \"ALIYUN::ROS::Interface\": {\n" +
                "      \"ParameterGroups\": [\n" +
                "        {\n" +
                "          \"Parameters\": [\n" +
                "            \"ZoneId\"\n" +
                "          ]\n" +
                "        }\n" +
                "      ],\n" +
                "      \"TemplateTags\": [\n" +
                "        \"Creates one ECS(RabbitMQ) instance - Existing Vpc\"\n" +
                "      ]\n" +
                "    }\n" +
                "  }\n" +
                "}";
        new Expectations() {{
            computeNestSupplierClient.getService(withAny(getServiceRequest));
            result = getServiceResponse;
            getServiceResponse.getBody();
            result = getServiceResponseBody;
            getServiceResponseBody.getDeployMetadata();
            result = "deployMetadata";
            mapper.readTree(anyString);
            result = deployMetadataRootNode;
            deployMetadataRootNode.get(anyString);
            result = deployMetadataRootNode;
            HttpUtil.doGet("templateUrl");
            result = rosTemplate;
            deployMetadataRootNode.get(TEMPLATE_CONFIGS).get(0);
            result = deployMetadataRootNode;
            deployMetadataRootNode.get("Name").asText();
            result = "templateName";
            deployMetadataRootNode.get("Url").asText();
            result = "templateUrl";
            mapper.readTree(rosTemplate);
            result = deployMetadataRootNode;
            deployMetadataRootNode.toString();
            result = "parameterMetadata";
            deployMetadataRootNode.toString();
            result = "specifications";
        }};

        UserInfoModel userInfoModel = new UserInfoModel();
        userInfoModel.setAid("1111111");
        GetServiceMetadataParam getServiceMetadataParam = new GetServiceMetadataParam();
        getServiceMetadataParam.setServiceId("test");
        BaseResult<ServiceMetadataModel> result = serviceInstanceLifecycleService.getServiceMetadata(userInfoModel, getServiceMetadataParam);

        assertNotNull(result);
    }
}
