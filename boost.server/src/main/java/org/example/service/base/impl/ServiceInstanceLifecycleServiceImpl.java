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

package org.example.service.base.impl;

import com.aliyun.computenestsupplier20210521.models.ContinueDeployServiceInstanceRequest;
import com.aliyun.computenestsupplier20210521.models.ContinueDeployServiceInstanceResponse;
import com.aliyun.computenestsupplier20210521.models.CreateServiceInstanceRequest;
import com.aliyun.computenestsupplier20210521.models.CreateServiceInstanceResponse;
import com.aliyun.computenestsupplier20210521.models.GetServiceInstanceRequest;
import com.aliyun.computenestsupplier20210521.models.GetServiceInstanceResponse;
import com.aliyun.computenestsupplier20210521.models.GetServiceInstanceResponseBody;
import com.aliyun.computenestsupplier20210521.models.GetServiceInstanceResponseBody.GetServiceInstanceResponseBodyService;
import com.aliyun.computenestsupplier20210521.models.GetServiceInstanceResponseBody.GetServiceInstanceResponseBodyServiceServiceInfos;
import com.aliyun.computenestsupplier20210521.models.ListServiceInstancesRequest;
import com.aliyun.computenestsupplier20210521.models.ListServiceInstancesRequest.ListServiceInstancesRequestFilter;
import com.aliyun.computenestsupplier20210521.models.ListServiceInstancesResponse;
import com.aliyun.computenestsupplier20210521.models.ListServiceInstancesResponseBody;
import com.aliyun.computenestsupplier20210521.models.ListServiceInstancesResponseBody.ListServiceInstancesResponseBodyServiceInstances;
import com.aliyun.computenestsupplier20210521.models.ListServiceInstancesResponseBody.ListServiceInstancesResponseBodyServiceInstancesService;
import com.aliyun.computenestsupplier20210521.models.ListServiceInstancesResponseBody.ListServiceInstancesResponseBodyServiceInstancesServiceServiceInfos;
import com.aliyun.computenestsupplier20210521.models.UpdateServiceInstanceAttributeRequest;
import com.aliyun.computenestsupplier20210521.models.UpdateServiceInstanceAttributeResponse;
import com.aliyun.tea.TeaException;
import com.aliyuncs.CommonRequest;
import com.aliyuncs.CommonResponse;
import com.aliyuncs.exceptions.ClientException;
import com.aliyuncs.http.MethodType;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpStatus;
import org.example.common.BaseResult;
import org.example.common.ListResult;
import org.example.common.adapter.AcsApiCaller;
import org.example.common.adapter.ComputeNestSupplierClient;
import org.example.common.constant.CallSource;
import org.example.common.constant.ChargeType;
import org.example.common.constant.ComputeNestConstants;
import org.example.common.constant.OrderType;
import org.example.common.constant.PayPeriodUnit;
import org.example.common.constant.ServiceType;
import org.example.common.dataobject.OrderDO;
import org.example.common.dto.OrderDTO;
import org.example.common.errorinfo.ErrorInfo;
import org.example.common.exception.BizException;
import org.example.common.helper.ServiceInstanceLifeStyleHelper;
import org.example.common.helper.WalletHelper;
import org.example.common.model.CommodityPriceModel;
import org.example.common.model.ServiceInstanceModel;
import org.example.common.model.ServiceMetadataModel;
import org.example.common.model.ServiceModel;
import org.example.common.model.UserInfoModel;
import org.example.common.param.order.CreateOrderParam;
import org.example.common.param.order.ListOrdersParam;
import org.example.common.param.payment.CreateTransactionParam;
import org.example.common.param.service.GetServiceMetadataParam;
import org.example.common.param.si.GetServiceInstanceParam;
import org.example.common.param.si.ListServiceInstancesParam;
import org.example.common.param.si.RenewServiceInstanceParam;
import org.example.common.param.si.UpdateServiceInstanceAttributeParam;
import org.example.common.utils.DateUtil;
import org.example.common.utils.JsonUtil;
import org.example.common.utils.OpenAPIErrorMessageUtil;
import org.example.common.utils.UuidUtil;
import org.example.service.base.ServiceInstanceLifecycleService;
import org.example.service.base.ServiceManager;
import org.example.service.order.OrderService;
import org.example.service.payment.PaymentServiceManger;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.example.common.constant.ComputeNestConstants.COMPUTE_NEST_SUPPLIER_API_VERSION;
import static org.example.common.constant.ComputeNestConstants.DEFAULT_REGION_ID;
import static org.example.common.constant.ComputeNestConstants.PAY_PERIOD;
import static org.example.common.constant.ComputeNestConstants.PAY_PERIOD_UNIT;
import static org.example.common.constant.ComputeNestConstants.TEMPLATE_NAME_PREFIX;


@Service
@Slf4j
public class ServiceInstanceLifecycleServiceImpl implements ServiceInstanceLifecycleService {

    private final ComputeNestSupplierClient computeNestSupplierClient;

    private final ServiceInstanceLifeStyleHelper serviceInstanceLifeStyleHelper;

    @Value("${service.id:null}")
    private String serviceId;

    @Resource
    private OrderService orderService;

    @Resource
    private AcsApiCaller acsApiCaller;

    @Resource
    private ServiceManager serviceManager;

    @Resource
    private WalletHelper walletHelper;

    @Resource
    private PaymentServiceManger paymentServiceManger;

    private static final String PREFIX = "saas-boost";

    private static final Integer DEFAULT_RETENTION_DAYS = 3;

    public ServiceInstanceLifecycleServiceImpl(ComputeNestSupplierClient computeNestSupplierClient, ServiceInstanceLifeStyleHelper serviceInstanceLifeStyleHelper) {
        this.computeNestSupplierClient = computeNestSupplierClient;
        this.serviceInstanceLifeStyleHelper = serviceInstanceLifeStyleHelper;
    }

    @Override
    public ListResult<ServiceInstanceModel> listServiceInstances(UserInfoModel userInfoModel, ListServiceInstancesParam listServiceInstancesParam) {
        if (StringUtils.isBlank(userInfoModel.getAid())) {
            throw new BizException(ErrorInfo.VERIFY_FAILED.getStatusCode(), ErrorInfo.VERIFY_FAILED.getCode(), ErrorInfo.VERIFY_FAILED.getMessage());
        }
        ListServiceInstancesRequest request = convertToRequest(listServiceInstancesParam, DEFAULT_REGION_ID);
        request.setFilter(buildFilterForListServiceInstance(listServiceInstancesParam, userInfoModel));
        try {
            log.info("listServiceInstances request: {}", JsonUtil.toJsonString(request));
            ListServiceInstancesResponse response = computeNestSupplierClient.listServiceInstances(request);
            return convertFromListServiceInstancesResponse(userInfoModel, response);
        } catch (TeaException e) {
            throw new BizException(e.getStatusCode(), e.getCode(), OpenAPIErrorMessageUtil.getErrorMessageFromComputeNestError(e.getMessage()));
        } catch (Exception e) {
            log.error("listServiceInstances error.", e);
            throw new BizException(ErrorInfo.SERVER_UNAVAILABLE.getStatusCode(), ErrorInfo.SERVER_UNAVAILABLE.getCode(), ErrorInfo.SERVER_UNAVAILABLE.getMessage());
        }
    }

    private List<ListServiceInstancesRequestFilter> buildFilterForListServiceInstance(ListServiceInstancesParam listServiceInstancesParam, UserInfoModel userInfoModel) {
        ListServiceInstancesRequestFilter accountIdFilter = serviceInstanceLifeStyleHelper.createFilter(ComputeNestConstants.USER_ID, Collections.singletonList(userInfoModel.getAid()));
        List<ListServiceInstancesRequestFilter> filterList = new ArrayList<>(Collections.singletonList(accountIdFilter));

        if (listServiceInstancesParam.getServiceIdList() != null && !listServiceInstancesParam.getServiceIdList().isEmpty()) {
            ListServiceInstancesRequestFilter serviceIdFilter = serviceInstanceLifeStyleHelper.createFilter(ComputeNestConstants.SERVICE_ID, listServiceInstancesParam.getServiceIdList());
            filterList.add(serviceIdFilter);
        }

        if (!StringUtils.isEmpty(listServiceInstancesParam.getServiceInstanceId())) {
            ListServiceInstancesRequestFilter serviceInstanceIdFilter = serviceInstanceLifeStyleHelper.createFilter(ComputeNestConstants.SERVICE_INSTANCE_ID,
                    Collections.singletonList(listServiceInstancesParam.getServiceInstanceId()));
            filterList.add(serviceInstanceIdFilter);
        }

        if (!StringUtils.isEmpty(listServiceInstancesParam.getServiceInstanceName())) {
            ListServiceInstancesRequestFilter serviceInstanceNameFilter = serviceInstanceLifeStyleHelper.createFilter(ComputeNestConstants.SERVICE_INSTANCE_NAME,
                    Collections.singletonList(listServiceInstancesParam.getServiceInstanceName()));
            filterList.add(serviceInstanceNameFilter);
        }

        if (!StringUtils.isEmpty(listServiceInstancesParam.getStatus())) {
            ListServiceInstancesRequestFilter serviceInstanceStatusFilter = serviceInstanceLifeStyleHelper.createFilter(ComputeNestConstants.SERVICE_INSTANCE_STATUS,
                    Collections.singletonList(listServiceInstancesParam.getStatus()));
            filterList.add(serviceInstanceStatusFilter);
        }
        return filterList;
    }

    @Override
    public BaseResult<ServiceInstanceModel> getServiceInstance(UserInfoModel userInfoModel, GetServiceInstanceParam getServiceInstanceParam) {
        if (StringUtils.isBlank(userInfoModel.getAid())) {
            throw new BizException(ErrorInfo.VERIFY_FAILED.getStatusCode(), ErrorInfo.VERIFY_FAILED.getCode(), ErrorInfo.VERIFY_FAILED.getMessage());
        }
        GetServiceInstanceRequest request = new GetServiceInstanceRequest();
        request.setServiceInstanceId(getServiceInstanceParam.getServiceInstanceId());
        try {
            GetServiceInstanceResponse response = computeNestSupplierClient.getServiceInstance(request);
            GetServiceInstanceResponseBody responseBody = filterServiceInstanceResponseWithAid(userInfoModel, response);
            if (responseBody == null) {
                log.info("The current logged-in user with aid={} does not have permission to view ServiceInstanceId={}.", userInfoModel.getAid(), response.getBody().getServiceInstanceId());
                return BaseResult.success(null);
            }
            ServiceInstanceModel serviceInstanceModel = new ServiceInstanceModel();
            BeanUtils.copyProperties(responseBody, serviceInstanceModel);
            serviceInstanceModel.setServiceType(ServiceType.to(responseBody.getServiceType()));
            serviceInstanceModel.setServiceModel(buildServiceModel(responseBody));
            if (responseBody.getSource() != null) {
                serviceInstanceModel.setSource(CallSource.valueOf(responseBody.getSource()));
            }
            return BaseResult.success(serviceInstanceModel);
        } catch (TeaException e) {
            throw new BizException(e.getStatusCode(), e.getCode(), OpenAPIErrorMessageUtil.getErrorMessageFromComputeNestError(e.getMessage()));
        } catch (Exception e) {
            throw new BizException(ErrorInfo.SERVER_UNAVAILABLE.getStatusCode(), ErrorInfo.SERVER_UNAVAILABLE.getCode(), ErrorInfo.SERVER_UNAVAILABLE.getMessage());
        }
    }

    @Override
    public CreateServiceInstanceResponse createServiceInstance(UserInfoModel userInfoModel, Map<String, Object> map, boolean dryRun, String endTime) {
        CreateServiceInstanceRequest request = new CreateServiceInstanceRequest();
//        request.setUserId(userInfoModel.getAid());
        request.setServiceId(serviceId);
        request.setClientToken(UuidUtil.generateUuid(PREFIX));
        request.setRegionId(ComputeNestConstants.DEFAULT_REGION_ID);
        request.setEndTime(endTime);
        map.remove(PAY_PERIOD);
        map.remove(PAY_PERIOD_UNIT);
        Object specificationName = map.remove(ComputeNestConstants.SPECIFICATION_NAME);
        if (specificationName != null && !ComputeNestConstants.DEFAULT_SPECIFICATION_NAME.equals(String.valueOf(specificationName))) {
            request.setSpecificationName(String.valueOf(specificationName));
        }
        request.setParameters(map);
        request.setDryRun(dryRun);
        request.setTemplateName(String.valueOf(map.remove(TEMPLATE_NAME_PREFIX)));
        return computeNestSupplierClient.createServiceInstance(request);
    }

    @Override
    public ContinueDeployServiceInstanceResponse continueDeployServiceInstance(ContinueDeployServiceInstanceRequest request) {
        return computeNestSupplierClient.continueDeployServiceInstance(request);
    }

    @Override
    public UpdateServiceInstanceAttributeResponse updateServiceInstanceAttribute(UserInfoModel userInfoModel, UpdateServiceInstanceAttributeParam updateServiceInstanceAttributeParam) {
        UpdateServiceInstanceAttributeRequest request = new UpdateServiceInstanceAttributeRequest();
        BeanUtils.copyProperties(updateServiceInstanceAttributeParam, request);
        return computeNestSupplierClient.updateServiceInstanceAttribute(request);
    }

    @Override
    public String payOrderCallback(UserInfoModel userInfoModel, OrderDO orderDO) throws ClientException {
        String orderType = StringUtils.isEmpty(orderDO.getServiceInstanceId()) ? OrderType.BUY.name() : OrderType.RENEW.name();
        CommonRequest commonRequest = buildPayOrderCallbackRequest(orderDO, userInfoModel.getAid(), orderType, orderDO.getServiceInstanceId());
        CommonResponse commonResponse = acsApiCaller.getCommonResponse(commonRequest);
        return processCommonResponse(commonResponse, orderDO);
    }

    private String processCommonResponse(CommonResponse commonResponse, OrderDO orderDO) {
        if (commonResponse.getHttpStatus() == HttpStatus.SC_OK && StringUtils.isNotEmpty(commonResponse.getData())) {
            ServiceInstanceModel serviceInstanceModel = JsonUtil.parseObjectUpperCamelCase(commonResponse.getData(), ServiceInstanceModel.class);
            log.info("payOrderCallback success, orderId = {}, serviceInstanceId = {}", orderDO.getOrderId(), serviceInstanceModel.getServiceInstanceId());
            return serviceInstanceModel.getServiceInstanceId();
        } else {
            String errorMsg = String.format(ErrorInfo.SERVICE_INSTANCE_CREATE_FAILED.getMessage(), orderDO.getOrderId());
            throw new BizException(ErrorInfo.SERVICE_INSTANCE_CREATE_FAILED.getStatusCode(),
                    ErrorInfo.SERVICE_INSTANCE_CREATE_FAILED.getCode(),
                    errorMsg);
        }
    }

    @Override
    public BaseResult<CommodityPriceModel> renewServiceInstance(UserInfoModel userInfoModel, RenewServiceInstanceParam renewServiceInstanceParam) {
        PayPeriodUnit payPeriodUnit = renewServiceInstanceParam.getPayPeriodUnit();
        Long payPeriod = renewServiceInstanceParam.getPayPeriod();
        String serviceInstanceId = renewServiceInstanceParam.getServiceInstanceId();
        CommodityPriceModel commodityPriceModel = walletHelper.getServiceInstanceRenewAmount(serviceInstanceId, payPeriod, payPeriodUnit);
        CreateOrderParam createOrderParam = new CreateOrderParam();
        BeanUtils.copyProperties(renewServiceInstanceParam, createOrderParam);
        BeanUtils.copyProperties(commodityPriceModel, createOrderParam);
        createOrderParam.setOrderType(OrderType.RENEW.name());
        createOrderParam.setUserId(userInfoModel.getAid());
        createOrderParam.setChargeType(ChargeType.PrePaid);
        createOrderParam.setServiceInstanceId(serviceInstanceId);
        OrderDTO order = orderService.createOrder(createOrderParam);
        CreateTransactionParam createTransactionParam = new CreateTransactionParam();
        createTransactionParam.setPayChannel(renewServiceInstanceParam.getPayChannel());
        createTransactionParam.setOrderId(order.getOrderId());
        String transaction = paymentServiceManger.createTransaction(userInfoModel, createTransactionParam);
        commodityPriceModel.setPaymentForm(transaction);
        return BaseResult.success(commodityPriceModel);
    }

    private CommonRequest buildPayOrderCallbackRequest(OrderDO orderDO, String aid, String orderType, String serviceInstanceId) {
        CommonRequest request = new CommonRequest();
        request.setSysRegionId(DEFAULT_REGION_ID);
        request.setSysMethod(MethodType.POST);
        request.setSysDomain(ComputeNestConstants.SERVICE_ENDPOINT);
        request.setSysVersion(COMPUTE_NEST_SUPPLIER_API_VERSION);
        request.setSysAction("PayOrderCallback");
        request.putQueryParameter("OrderId", orderDO.getOrderId());
        request.putQueryParameter("OrderType", orderType);
        request.putQueryParameter("BuyerAliUid", aid);
        request.putQueryParameter("ServiceId", orderDO.getServiceId());
        request.putQueryParameter("EndTime", DateUtil.parseIs08601DateMillis(orderDO.getBillingEndDateMillis()));
        if (StringUtils.isNotEmpty(serviceInstanceId)) {
            request.putQueryParameter("ServiceInstanceId", serviceInstanceId);
        }
        return request;
    }

    private void updateServiceInstance(UserInfoModel userInfoModel, OrderDO orderDO) {
        Integer retentionDays = fetchRetentionDays(userInfoModel);
        Long endTimeMillis = DateUtil.getIsO8601FutureDateMillis(orderDO.getBillingEndDateMillis(), retentionDays);
        String endTime = DateUtil.parseIs08601DateMillis(endTimeMillis);

        UpdateServiceInstanceAttributeParam updateServiceInstanceAttributeParam = new UpdateServiceInstanceAttributeParam();
        updateServiceInstanceAttributeParam.setServiceInstanceId(orderDO.getServiceInstanceId());
        updateServiceInstanceAttributeParam.setEndTime(endTime);
        updateServiceInstanceAttribute(userInfoModel, updateServiceInstanceAttributeParam);
    }

    private Integer fetchRetentionDays(UserInfoModel userInfoModel) {
        BaseResult<ServiceMetadataModel> serviceMetadataResult =
                serviceManager.getServiceMetadata(userInfoModel, new GetServiceMetadataParam());
        return Optional.ofNullable(serviceMetadataResult.getData())
                .map(ServiceMetadataModel::getRetentionDays)
                .orElse(DEFAULT_RETENTION_DAYS);
    }

    private ServiceModel buildServiceModel(GetServiceInstanceResponseBody responseBody) {
        ServiceModel serviceModel = new ServiceModel();
        if (responseBody.getService() != null) {
            GetServiceInstanceResponseBodyService service = responseBody.getService();
            serviceModel.setServiceId(service.getServiceId());
            if (service.getServiceInfos()!= null && !service.getServiceInfos().isEmpty()) {
                GetServiceInstanceResponseBodyServiceServiceInfos serviceInfo = service.getServiceInfos().get(0);
                serviceModel.setName(serviceInfo.getName());
                serviceModel.setImage(serviceInfo.getImage());
                serviceModel.setDescription(serviceInfo.getShortDescription());
            }
        }
        return serviceModel;
    }
    private GetServiceInstanceResponseBody filterServiceInstanceResponseWithAid(UserInfoModel userInfoModel, GetServiceInstanceResponse response) {
        GetServiceInstanceResponseBody responseBody = response.getBody();
        if (responseBody.getUserId().toString().equals(userInfoModel.getAid())) {
            return responseBody;
        } else {
            return null;
        }
    }

    private ListServiceInstancesRequest convertToRequest(ListServiceInstancesParam param, String regionId) {
        return new ListServiceInstancesRequest().setMaxResults(param.getMaxResults())
                .setNextToken(param.getNextToken()).setRegionId(regionId)
                .setShowDeleted(false).setTag(null);
    }

    private ListResult<ServiceInstanceModel> convertFromListServiceInstancesResponse(UserInfoModel userInfoModel, ListServiceInstancesResponse response) {
        ListServiceInstancesResponseBody responseBody = response.getBody();
        List<ListServiceInstancesResponseBodyServiceInstances> listInstances = responseBody.getServiceInstances();
        List<ServiceInstanceModel> serviceInstanceModelList = listInstances.stream()
                .map(instanceResponseBody -> {
                    ServiceInstanceModel serviceInstanceModel = new ServiceInstanceModel();
                    BeanUtils.copyProperties(instanceResponseBody, serviceInstanceModel);
                    if (instanceResponseBody.getSource() != null) {
                        serviceInstanceModel.setSource(CallSource.valueOf(instanceResponseBody.getSource()));
                    }
                    if (instanceResponseBody.getServiceType() != null) {
                        serviceInstanceModel.setServiceType(ServiceType.to(instanceResponseBody.getServiceType()));
                    }
                    ListOrdersParam listOrdersParam = new ListOrdersParam();
                    listOrdersParam.setServiceInstanceId(instanceResponseBody.getServiceInstanceId());
                    ListResult<OrderDTO> orderResult = orderService.listOrders(userInfoModel, listOrdersParam);
                    listOrdersParam.setMaxResults(1);
                    String latestOrderId = Optional.ofNullable(orderResult)
                            .map(ListResult::getData)
                            .filter(data -> !data.isEmpty())
                            .map(data -> data.get(0))
                            .map(OrderDTO::getOrderId)
                            .orElse(null);
                    serviceInstanceModel.setOrderId(latestOrderId);
                    ServiceModel serviceModel = buildServiceForList(instanceResponseBody);
                    serviceInstanceModel.setServiceModel(serviceModel);
                    return serviceInstanceModel;
                })
                .collect(Collectors.toList());
        return ListResult.genSuccessListResult(serviceInstanceModelList, responseBody.getTotalCount(), responseBody.getNextToken());
    }

    private ServiceModel buildServiceForList(ListServiceInstancesResponseBodyServiceInstances instanceResponseBody) {
        ListServiceInstancesResponseBodyServiceInstancesService service = instanceResponseBody.getService();
        ListServiceInstancesResponseBodyServiceInstancesServiceServiceInfos firstServiceInfo = instanceResponseBody.getService().getServiceInfos().get(0);
        return new ServiceModel(service.getServiceId(), firstServiceInfo.getName(),
                firstServiceInfo.getShortDescription(), firstServiceInfo.getImage());
    }
}
