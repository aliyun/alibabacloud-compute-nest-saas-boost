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

import com.alicloud.openservices.tablestore.model.search.sort.FieldSort;
import com.alicloud.openservices.tablestore.model.search.sort.Sort.Sorter;
import com.alicloud.openservices.tablestore.model.search.sort.SortOrder;
import com.alipay.api.AlipayApiException;
import com.aliyun.computenestsupplier20210521.models.CreateServiceInstanceResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.example.common.BaseResult;
import org.example.common.ListResult;
import org.example.common.constant.ComputeNestConstants;
import org.example.common.constant.OrderOtsConstant;
import org.example.common.constant.PayPeriodUnit;
import org.example.common.constant.TradeStatus;
import org.example.common.dataobject.OrderDO;
import org.example.common.dto.OrderDTO;
import org.example.common.errorinfo.ErrorInfo;
import org.example.common.exception.BizException;
import org.example.common.helper.BaseOtsHelper.OtsFilter;
import org.example.common.helper.OrderOtsHelper;
import org.example.common.helper.ServiceInstanceLifeStyleHelper;
import org.example.common.helper.WalletHelper;
import org.example.common.model.UserInfoModel;
import org.example.common.param.CreateOrderParam;
import org.example.common.param.GetOrderParam;
import org.example.common.param.GetServiceCostParam;
import org.example.common.param.ListOrdersParam;
import org.example.common.param.RefundOrderParam;
import org.example.common.utils.BeanUtil;
import org.example.common.utils.DateUtil;
import org.example.common.utils.JsonUtil;
import org.example.common.utils.UuidUtil;
import org.example.service.AlipayService;
import org.example.service.OrderService;
import org.example.service.ServiceInstanceLifecycleService;
import org.example.service.ServiceManager;
import org.springframework.beans.BeanUtils;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.example.common.constant.ComputeNestConstants.PAY_PERIOD;
import static org.example.common.constant.ComputeNestConstants.PAY_PERIOD_UNIT;

@Service
@Slf4j
public class OrderServiceImpl implements OrderService {

    @Resource
    private AlipayService alipayService;

    @Resource
    private OrderOtsHelper orderOtsHelper;

    @Resource
    private WalletHelper walletHelper;

    @Resource
    private ServiceInstanceLifecycleService serviceInstanceLifecycleService;

    @Resource
    private ServiceManager serviceManager;

    @Resource
    private ServiceInstanceLifeStyleHelper serviceInstanceLifeStyleHelper;


    @Override
    public BaseResult<String> createOrder(UserInfoModel userInfoModel, CreateOrderParam param) throws AlipayApiException {
        Long userId = Long.parseLong(userInfoModel.getUid());
        Long accountId = Long.parseLong(userInfoModel.getAid());
        String orderId = UuidUtil.generateOrderId(userId, param.getType().getValue());
        Map<String, Object> nestParameters = (Map<String, Object>) JsonUtil.parseObjectCustom(param.getProductComponents(), Map.class);
        if (nestParameters == null) {
            return BaseResult.fail("product components can't be null.");
        }
        Long payPeriod = ((Number) nestParameters.remove(PAY_PERIOD)).longValue();
        PayPeriodUnit payPeriodUnit = PayPeriodUnit.valueOf(String.valueOf(nestParameters.remove(PAY_PERIOD_UNIT)));
        Object serviceInstanceIdObj = nestParameters.remove(ComputeNestConstants.SERVICE_INSTANCE_ID);
        String serviceInstanceId = serviceInstanceIdObj != null ? String.valueOf(serviceInstanceIdObj) : null;
        GetServiceCostParam getServiceCostParam = new GetServiceCostParam();
        BeanUtil.populateObject(nestParameters, getServiceCostParam);
        getServiceCostParam.setPayPeriodUnit(payPeriodUnit);
        getServiceCostParam.setPayPeriod(payPeriod);
        Double cost = serviceManager.getServiceCost(userInfoModel, getServiceCostParam).getData();
        if (StringUtils.isEmpty(serviceInstanceId)) {
            CreateServiceInstanceResponse response = serviceInstanceLifecycleService.createServiceInstance(userInfoModel, nestParameters, true);
            if (response == null || !response.getStatusCode().equals(HttpStatus.OK.value())) {
                return BaseResult.fail(ErrorInfo.SERVER_UNAVAILABLE);
            }
        }

        String webForm = alipayService.createTransaction(cost, param.getProductName().getDisplayName(), orderId);
        if (StringUtils.isNotEmpty(webForm)) {
            OrderDO orderDataObject = createOrderDataObject(orderId, param, accountId, cost, accountId, getServiceCostParam);
            orderDataObject.setServiceInstanceId(serviceInstanceId);
            orderOtsHelper.createOrder(orderDataObject);
            log.info("The Alipay web form has been successfully created with the following content{}.", webForm);
            return BaseResult.success(webForm);
        }
        log.warn("The Alipay web form create failed, user id = {}, order id = {}", accountId, orderId);
        return BaseResult.fail("The Alipay web form create failed.");
    }

    private void updateBillingDates(OrderDO orderDataObject) {
        if (StringUtils.isNotEmpty(orderDataObject.getServiceInstanceId())) {
            String serviceInstanceId = orderDataObject.getServiceInstanceId();
            OtsFilter serviceInstanceIdQueryFilter = OtsFilter.createMatchFilter(OrderOtsConstant.SERVICE_INSTANCE_ID, serviceInstanceId);
            OtsFilter tradeStatusQueryFilter = OtsFilter.createTermsFilter(OrderOtsConstant.TRADE_STATUS, Arrays.asList(TradeStatus.TRADE_SUCCESS, TradeStatus.TRADE_FINISHED));
            FieldSort fieldSort = new FieldSort(OrderOtsConstant.BILLING_END_DATE_LONG, SortOrder.DESC);
            ListResult<OrderDTO> orderDtoListResult = orderOtsHelper.listOrders(Arrays.asList(serviceInstanceIdQueryFilter), null, Collections.singletonList(tradeStatusQueryFilter), null, Collections.singletonList(fieldSort));
            if (orderDtoListResult != null && orderDtoListResult.getData() != null && orderDtoListResult.getData().size() > 0) {
                Long preBillingEndDateLong = orderDtoListResult.getData().get(0).getBillingsEndDateLong();
                Long currentBillingEndDateTimeLong = walletHelper.getBillingEndDateTimeLong(preBillingEndDateLong, orderDataObject.getPayPeriod(), orderDataObject.getPayPeriodUnit());
                orderDataObject.setBillingsEndDateLong(currentBillingEndDateTimeLong);
                orderDataObject.setBillingsStartDateLong(preBillingEndDateLong);
                return;
            }
        }
        Long billingStartDateMillis = DateUtil.getCurrentLocalDateTimeMillis();
        Long billingEndDateMillis = walletHelper.getBillingEndDateTimeLong(billingStartDateMillis, orderDataObject.getPayPeriod(), orderDataObject.getPayPeriodUnit());
        orderDataObject.setBillingsEndDateLong(billingEndDateMillis);
        orderDataObject.setBillingsStartDateLong(billingStartDateMillis);
        String billingStartDate = DateUtil.parseIs08601DateMillis(orderDataObject.getBillingsStartDateLong());
        String billingEndDate = DateUtil.parseIs08601DateMillis(orderDataObject.getBillingsEndDateLong());
        log.info("the current order with orderId : {}, the billingStartDate = {}, the billing end date = {}", orderDataObject.getOrderId(), billingStartDate, billingEndDate);
    }

    @Override
    public BaseResult<OrderDTO> getOrder(UserInfoModel userInfoModel, GetOrderParam param) {
        Long aid = Optional.ofNullable(userInfoModel)
                .map(UserInfoModel::getAid)
                .map(Long::parseLong)
                .orElse(null);
        return BaseResult.success(orderOtsHelper.getOrder(param.getOrderId(), aid));
    }

    @Override
    public ListResult<OrderDTO> listOrders(UserInfoModel userInfoModel, ListOrdersParam param) {
        List<OtsFilter> matchFilters = new ArrayList<>();
        List<OtsFilter> rangeFilters = new ArrayList<>();
        OtsFilter aidMatchFilter = OtsFilter.createMatchFilter(OrderOtsConstant.ACCOUNT_ID, userInfoModel.getAid());
        matchFilters.add(aidMatchFilter);
        List<Sorter> sorters = new ArrayList<>();

        if (StringUtils.isNotEmpty(param.getServiceInstanceId())) {
            OtsFilter serviceInstanceMatchFilter = OtsFilter.createMatchFilter(OrderOtsConstant.SERVICE_INSTANCE_ID, param.getServiceInstanceId());
            matchFilters.add(serviceInstanceMatchFilter);
            sorters.add(new FieldSort(OrderOtsConstant.BILLING_END_DATE_LONG, SortOrder.DESC));
        }
        if (StringUtils.isNotEmpty(param.getStartTime()) && StringUtils.isNotEmpty(param.getEndTime())) {
            Long startTimeMills = DateUtil.parseFromIsO8601DateString(param.getStartTime());
            Long endTimeMills = DateUtil.parseFromIsO8601DateString(param.getEndTime());
            OtsFilter rangeFilter = OtsFilter.builder().key(OrderOtsConstant.GMT_CREATE_LONG).values(Arrays.asList(startTimeMills, endTimeMills)).build();
            rangeFilters.add(rangeFilter);
        }

        if (param.getTradeStatus() != null) {
            OtsFilter tradeStatusMatchFilters = OtsFilter.createMatchFilter(OrderOtsConstant.TRADE_STATUS, param.getTradeStatus());
            matchFilters.add(tradeStatusMatchFilters);
        }
        FieldSort fieldSort = new FieldSort(OrderOtsConstant.GMT_CREATE_LONG);
        fieldSort.setOrder(SortOrder.DESC);
        sorters.add(fieldSort);

        return orderOtsHelper.listOrders(matchFilters, rangeFilters, Collections.emptyList(), param.getNextToken(), sorters);
    }

    @Override
    public void updateOrder(UserInfoModel userInfoModel, OrderDO orderDO) {
        CreateServiceInstanceResponse serviceInstanceResponse = null;
        try {
            if (DateUtil.isValidSimpleDateTimeFormat(orderDO.getGmtCreate())) {
                orderDO.setGmtCreate(DateUtil.simpleDateStringConvertToIso8601Format(orderDO.getGmtCreate()));
            }
            if (DateUtil.isValidSimpleDateTimeFormat(orderDO.getGmtPayment())) {
                orderDO.setGmtPayment(DateUtil.simpleDateStringConvertToIso8601Format(orderDO.getGmtPayment()));
            }
            updateBillingDates(orderDO);

            if (StringUtils.isEmpty(orderDO.getServiceInstanceId())) {
                serviceInstanceResponse = serviceInstanceLifecycleService.createServiceInstance(userInfoModel, JsonUtil.parseObjectCustom(orderDO.getProductComponents(), Map.class), false);
                orderDO.setServiceInstanceId(serviceInstanceResponse.body.serviceInstanceId);
                orderOtsHelper.updateOrder(orderDO);
            } else {
                //todo 更新end time
                orderOtsHelper.updateOrder(orderDO);
            }
        } catch (Exception e) {
            if (serviceInstanceResponse != null && !StringUtils.isEmpty(serviceInstanceResponse.body.serviceInstanceId)) {
                orderDO.setTradeStatus(TradeStatus.REFUNDING);
                orderOtsHelper.updateOrder(orderDO);
            } else {
                String refundId = UuidUtil.generateRefundId();
                alipayService.refundOrder(orderDO.getOrderId(), orderDO.getReceiptAmount(), refundId);
            }
        }
    }

    @Override
    public BaseResult<Double> refundOrder(UserInfoModel userInfoModel, RefundOrderParam param) {
        Boolean dryRun = param.getDryRun();
        String refundId = UuidUtil.generateRefundId();
        Long currentLocalDateTimeMillis = DateUtil.getCurrentLocalDateTimeMillis();
        String currentLocalDateTime = DateUtil.parseIs08601DateMillis(currentLocalDateTimeMillis);
        Double allRefundAmount = 0D;
        if (StringUtils.isNotEmpty(param.getServiceInstanceId())) {
            List<OrderDTO> orderDTOList = orderOtsHelper.listServiceInstanceOrders(param.getServiceInstanceId(), Long.valueOf(userInfoModel.getAid()), false, TradeStatus.TRADE_SUCCESS);
            if (orderDTOList != null && orderDTOList.size() > 0) {
                if (serviceInstanceLifeStyleHelper.checkServiceInstanceExpiration(orderDTOList, currentLocalDateTimeMillis)) {
                    OrderDTO expiredOrder = orderDTOList.get(orderDTOList.size() - 1);
                    Double refundAmount = orderOtsHelper.refundConsumingOrder(expiredOrder, dryRun, refundId, currentLocalDateTime);
                    return BaseResult.success(refundAmount);
                }
                int index = 0;
                for (; index < orderDTOList.size(); index++) {
                    OrderDTO orderDTO = orderDTOList.get(index);
                    if (TradeStatus.TRADE_FINISHED.equals(orderDTO.getTradeStatus())) {
                        continue;
                    }
                    if (orderOtsHelper.isOrderInConsuming(orderDTO, currentLocalDateTimeMillis)) {
                        break;
                    }
                }
                allRefundAmount += orderOtsHelper.refundConsumingOrder(orderDTOList.get(index++), dryRun, refundId, currentLocalDateTime);
                for (; index < orderDTOList.size(); index++) {
                    allRefundAmount += orderOtsHelper.refundUnconsumedOrder(orderDTOList.get(index), dryRun, refundId, currentLocalDateTime);
                }
                return BaseResult.success(allRefundAmount);
            }
        } else {
            OrderDTO order = orderOtsHelper.getOrder(param.getOrderId(), Long.valueOf(userInfoModel.getAid()));
            if (orderOtsHelper.validateOrderCanBeRefunded(order, Long.valueOf(userInfoModel.getAid()))) {
                if (orderOtsHelper.isOrderInConsuming(order, currentLocalDateTimeMillis)) {
                    allRefundAmount += orderOtsHelper.refundConsumingOrder(order, dryRun, refundId, currentLocalDateTime);
                } else {
                    allRefundAmount += orderOtsHelper.refundUnconsumedOrder(order, dryRun, refundId, currentLocalDateTime);
                }
                return BaseResult.success(allRefundAmount);
            }
            throw new BizException(ErrorInfo.CURRENT_ORDER_CANT_BE_DELETED);
        }
        throw new IllegalArgumentException("The order ID and service instance ID cannot be both empty.");
    }

    private OrderDO createOrderDataObject(String orderId, CreateOrderParam createOrderParam, Long userId, Double totalAmount, Long accountId, GetServiceCostParam getServiceCostParam) {
        OrderDO orderDO = new OrderDO();
        orderDO.setUserId(userId);
        orderDO.setTotalAmount(totalAmount);
        orderDO.setOrderId(orderId);
        orderDO.setAccountId(accountId);
        orderDO.setTradeStatus(TradeStatus.WAIT_BUYER_PAY);
        BeanUtils.copyProperties(createOrderParam, orderDO);
        BeanUtils.copyProperties(getServiceCostParam, orderDO);
        return orderDO;
    }
}

