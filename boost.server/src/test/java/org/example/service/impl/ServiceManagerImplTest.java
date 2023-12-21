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

import com.aliyun.computenestsupplier20210521.models.GetServiceRequest;
import com.aliyun.computenestsupplier20210521.models.GetServiceResponse;
import com.aliyun.computenestsupplier20210521.models.GetServiceResponseBody;
import com.aliyun.computenestsupplier20210521.models.GetServiceTemplateParameterConstraintsRequest;
import com.aliyun.computenestsupplier20210521.models.GetServiceTemplateParameterConstraintsResponse;
import com.aliyun.computenestsupplier20210521.models.GetServiceTemplateParameterConstraintsResponseBody;
import com.aliyun.computenestsupplier20210521.models.GetServiceTemplateParameterConstraintsResponseBody.GetServiceTemplateParameterConstraintsResponseBodyParameterConstraints;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import mockit.Expectations;
import mockit.Injectable;
import mockit.Mocked;
import mockit.Tested;
import org.example.common.BaseResult;
import org.example.common.ListResult;
import org.example.common.adapter.ComputeNestSupplierClient;
import org.example.common.constant.PayPeriodUnit;
import org.example.common.helper.WalletHelper;
import org.example.common.model.ServiceMetadataModel;
import org.example.common.model.UserInfoModel;
import org.example.common.param.GetServiceCostParam;
import org.example.common.param.GetServiceMetadataParam;
import org.example.common.param.GetServiceTemplateParameterConstraintsParam;
import org.example.common.utils.HttpUtil;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.cache.Cache;

import java.util.Arrays;

import static org.example.common.constant.ComputeNestConstants.TEMPLATE_CONFIGS;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;


class ServiceManagerImplTest {

    @Tested
    private ServiceManagerImpl serviceManager;

    @Injectable
    private WalletHelper walletHelper;

    @Injectable
    private ComputeNestSupplierClient computeNestSupplierClient;

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
            getServiceResponseBody.getLicenseMetadata();
            result =   "{\"RetentionDays\":1}";
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
        BaseResult<ServiceMetadataModel> result = serviceManager.getServiceMetadata(userInfoModel, getServiceMetadataParam);
        Assertions.assertEquals(1, result.getData().getRetentionDays());
        assertNotNull(result);
    }

    @Test
    public void testGetServiceCost() {
        new Expectations() {{
            walletHelper.getServiceCost(anyString, anyString, anyLong, any(PayPeriodUnit.class));
            result = 1.0;
        }};
        GetServiceCostParam getServiceCostParam = new GetServiceCostParam();
        getServiceCostParam.setPayPeriod(1L);
        getServiceCostParam.setServiceId("test");
        getServiceCostParam.setParameters(new String[0]);
        getServiceCostParam.setPayPeriodUnit(PayPeriodUnit.Month);
        UserInfoModel userInfoModel = new UserInfoModel();
        userInfoModel.setAid("123");
        BaseResult<Double> result = serviceManager.getServiceCost(userInfoModel, getServiceCostParam);

        assertEquals(1.0, result.getData(), 0.001);
    }

    @Test
    public void testGetServiceTemplateParameterConstraints() {
        GetServiceTemplateParameterConstraintsResponseBodyParameterConstraints constraints = new GetServiceTemplateParameterConstraintsResponseBodyParameterConstraints();
        constraints.setAllowedValues(Arrays.asList("abc"));
        GetServiceTemplateParameterConstraintsResponse response = new GetServiceTemplateParameterConstraintsResponse();
        GetServiceTemplateParameterConstraintsResponseBody responseBody = new GetServiceTemplateParameterConstraintsResponseBody();
        responseBody.setParameterConstraints(Arrays.asList(constraints));
        response.setBody(responseBody);
        new Expectations() {{
            computeNestSupplierClient.getServiceTemplateParameterConstraints(withAny(new GetServiceTemplateParameterConstraintsRequest()));
            result = response;
        }};

        GetServiceTemplateParameterConstraintsParam param = new GetServiceTemplateParameterConstraintsParam();
        param.setParameters(null);
        param.setTemplateName("test");
        param.setDeployRegionId("cn-beijing");
        ListResult<GetServiceTemplateParameterConstraintsResponseBodyParameterConstraints> serviceTemplateParameterConstraints = serviceManager.getServiceTemplateParameterConstraints(param);
        Assertions.assertEquals(Arrays.asList("abc"), serviceTemplateParameterConstraints.getData().get(0).getAllowedValues());
    }
}