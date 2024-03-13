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

package org.example.service.order.impl;

import com.alicloud.openservices.tablestore.model.search.sort.FieldSort;
import com.alicloud.openservices.tablestore.model.search.sort.Sort.Sorter;
import com.alicloud.openservices.tablestore.model.search.sort.SortOrder;
import com.aliyun.computenestsupplier20210521.models.CreateServiceInstanceResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.example.common.BaseResult;
import org.example.common.ListResult;
import org.example.common.constant.OrderOtsConstant;
import org.example.common.constant.PayChannel;
import org.example.common.constant.RefundReason;
import org.example.common.constant.TradeStatus;
import org.example.common.dataobject.OrderDO;
import org.example.common.dto.OrderDTO;
import org.example.common.errorinfo.ErrorInfo;
import org.example.common.exception.BizException;
import org.example.common.helper.BaseOtsHelper.OtsFilter;
import org.example.common.helper.OrderOtsHelper;
import org.example.common.helper.ServiceInstanceLifeStyleHelper;
import org.example.common.helper.SpiTokenHelper;
import org.example.common.helper.WalletHelper;
import org.example.common.model.CommodityPriceModel;
import org.example.common.model.RefundDetailModel;
import org.example.common.model.ServiceMetadataModel;
import org.example.common.model.UserInfoModel;
import org.example.common.param.order.CreateOrderParam;
import org.example.common.param.order.GetOrderParam;
import org.example.common.param.order.ListOrdersParam;
import org.example.common.param.order.RefundOrderParam;
import org.example.common.param.service.GetServiceMetadataParam;
import org.example.common.param.si.UpdateServiceInstanceAttributeParam;
import org.example.common.utils.DateUtil;
import org.example.common.utils.JsonUtil;
import org.example.common.utils.UuidUtil;
import org.example.service.base.ServiceInstanceLifecycleService;
import org.example.service.base.ServiceManager;
import org.example.service.order.OrderService;
import org.example.service.payment.PaymentServiceManger;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Slf4j
public class OrderServiceImpl implements OrderService {

    @Resource
    private PaymentServiceManger alipayService;

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

    @Resource
    private SpiTokenHelper spiTokenHelper;

    @Resource
    private PaymentServiceManger paymentServiceManger;

    private static final Integer DEFAULT_RETENTION_DAYS = 3;


    @Override
    public BaseResult<OrderDTO> createOrder(CreateOrderParam param) {
        if (!spiTokenHelper.checkSpiToken(param, param.getToken())) {
            return BaseResult.fail(ErrorInfo.SPI_TOKEN_VALIDATION_FAILED);
        }
        CommodityPriceModel commodityCost = walletHelper.getCommodityCost(param.getCommodityCode(), param.getSpecificationName(), param.getPayPeriod());
        Long userId = Long.parseLong(param.getUserId());
        String orderId = UuidUtil.generateOrderId(userId, param.getPayChannel().getValue());

        String transaction = paymentServiceManger.createTransaction(commodityCost.getTotalAmount(), param.getSpecificationName(), orderId, param.getPayChannel());
        OrderDO orderDataObject = createOrderDataObject(orderId, param, userId, commodityCost.getTotalAmount(), userId, transaction);
        orderOtsHelper.createOrder(orderDataObject);
        OrderDTO orderDTO = new OrderDTO();
        BeanUtils.copyProperties(orderDataObject, orderDTO);
        return BaseResult.success(orderDTO);
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
        List<OtsFilter> multiMatchFilters = new ArrayList<>();
        OtsFilter aidMatchFilter = OtsFilter.createMatchFilter(OrderOtsConstant.ACCOUNT_ID, userInfoModel.getAid());
        matchFilters.add(aidMatchFilter);
        List<Sorter> sorters = new ArrayList<>();

        if (StringUtils.isNotEmpty(param.getOrderId())) {
            OtsFilter orderIdMatchFilter = OtsFilter.createMatchFilter(OrderOtsConstant.ORDER_ID, param.getOrderId());
            matchFilters.add(orderIdMatchFilter);
        }

        if (StringUtils.isNotEmpty(param.getServiceInstanceId())) {
            OtsFilter serviceInstanceMatchFilter = OtsFilter.createMatchFilter(OrderOtsConstant.SERVICE_INSTANCE_ID, param.getServiceInstanceId());
            matchFilters.add(serviceInstanceMatchFilter);
            sorters.add(new FieldSort(OrderOtsConstant.BILLING_END_DATE_MILLIS, SortOrder.DESC));
        }

        if (StringUtils.isNotEmpty(param.getStartTime()) && StringUtils.isNotEmpty(param.getEndTime())) {
            Long startTimeMills = DateUtil.parseFromIsO8601DateString(param.getStartTime());
            Long endTimeMills = DateUtil.parseFromIsO8601DateString(param.getEndTime());
            OtsFilter rangeFilter = OtsFilter.builder().key(OrderOtsConstant.GMT_CREATE_LONG).values(Arrays.asList(startTimeMills, endTimeMills)).build();
            rangeFilters.add(rangeFilter);
        }

        if (param.getTradeStatus() != null && param.getTradeStatus().size() > 0) {
            List<Object> tradeStatus = param.getTradeStatus().stream().map((item) -> ((Object) item)).collect(Collectors.toList());
            OtsFilter tradeStatusMatchFilters = OtsFilter.createTermsFilter(OrderOtsConstant.TRADE_STATUS, tradeStatus);
            multiMatchFilters.add(tradeStatusMatchFilters);
        }
        FieldSort fieldSort = new FieldSort(OrderOtsConstant.GMT_CREATE_LONG);
        fieldSort.setOrder(SortOrder.DESC);
        sorters.add(fieldSort);
        return orderOtsHelper.listOrders(matchFilters, rangeFilters, multiMatchFilters, param.getNextToken(), sorters);
    }

    @Override
    public void updateOrder(UserInfoModel userInfoModel, OrderDO orderDO) {
        try {
            updateBillingDates(orderDO);
            Integer retentionDays = fetchRetentionDays(userInfoModel);
            Long endTimeMillis = DateUtil.getIsO8601FutureDateMillis(orderDO.getBillingEndDateMillis(), retentionDays);
            String endTime = DateUtil.parseIs08601DateMillis(endTimeMillis);
            createOrUpdateServiceInstance(userInfoModel, orderDO, endTime);
        } catch (Exception e) {
            handleUpdateOrderFailure(e, orderDO);
        }
    }

    private void updateBillingDates(OrderDO orderDataObject) {
        if (DateUtil.isValidSimpleDateTimeFormat(orderDataObject.getGmtCreate())) {
            orderDataObject.setGmtCreate(DateUtil.simpleDateStringConvertToIso8601Format(orderDataObject.getGmtCreate()));
        }
        if (DateUtil.isValidSimpleDateTimeFormat(orderDataObject.getGmtPayment())) {
            orderDataObject.setGmtPayment(DateUtil.simpleDateStringConvertToIso8601Format(orderDataObject.getGmtPayment()));
        }
        if (StringUtils.isNotEmpty(orderDataObject.getServiceInstanceId())) {
            String serviceInstanceId = orderDataObject.getServiceInstanceId();
            OtsFilter serviceInstanceIdQueryFilter = OtsFilter.createMatchFilter(OrderOtsConstant.SERVICE_INSTANCE_ID, serviceInstanceId);
            OtsFilter tradeStatusQueryFilter = OtsFilter.createTermsFilter(OrderOtsConstant.TRADE_STATUS, Arrays.asList(TradeStatus.TRADE_SUCCESS, TradeStatus.TRADE_FINISHED));
            FieldSort fieldSort = new FieldSort(OrderOtsConstant.BILLING_END_DATE_MILLIS, SortOrder.DESC);
            ListResult<OrderDTO> orderDtoListResult = orderOtsHelper.listOrders(Arrays.asList(serviceInstanceIdQueryFilter), null, Collections.singletonList(tradeStatusQueryFilter), null, Collections.singletonList(fieldSort));
            if (orderDtoListResult != null && orderDtoListResult.getData() != null && orderDtoListResult.getData().size() > 0) {
                Long preBillingEndDateLong = orderDtoListResult.getData().get(0).getBillingEndDateMillis();
                Long currentBillingEndDateTimeLong = walletHelper.getBillingEndDateTimeMillis(preBillingEndDateLong, orderDataObject.getPayPeriod(), orderDataObject.getPayPeriodUnit());
                orderDataObject.setBillingEndDateMillis(currentBillingEndDateTimeLong);
                orderDataObject.setBillingStartDateMillis(preBillingEndDateLong);
                return;
            }
        }
        Long billingStartDateMillis = DateUtil.getCurrentLocalDateTimeMillis();
        Long billingEndDateMillis = walletHelper.getBillingEndDateTimeMillis(billingStartDateMillis, orderDataObject.getPayPeriod(), orderDataObject.getPayPeriodUnit());
        orderDataObject.setBillingEndDateMillis(billingEndDateMillis);
        orderDataObject.setBillingStartDateMillis(billingStartDateMillis);
        String billingStartDate = DateUtil.parseIs08601DateMillis(orderDataObject.getBillingStartDateMillis());
        String billingEndDate = DateUtil.parseIs08601DateMillis(orderDataObject.getBillingEndDateMillis());
        log.info("the current order with orderId : {}, the billingStartDate = {}, the billing end date = {}", orderDataObject.getOrderId(), billingStartDate, billingEndDate);
    }

    private Integer fetchRetentionDays(UserInfoModel userInfoModel) {
        BaseResult<ServiceMetadataModel> serviceMetadataResult =
                serviceManager.getServiceMetadata(userInfoModel, new GetServiceMetadataParam());
        return Optional.ofNullable(serviceMetadataResult.getData())
                .map(ServiceMetadataModel::getRetentionDays)
                .orElse(DEFAULT_RETENTION_DAYS);
    }

    private void createOrUpdateServiceInstance(UserInfoModel userInfoModel, OrderDO orderDO, String endTime) {
        if (StringUtils.isEmpty(orderDO.getServiceInstanceId())) {
            CreateServiceInstanceResponse serviceInstanceResponse =
                    serviceInstanceLifecycleService.createServiceInstance(
                            userInfoModel, JsonUtil.parseObjectCustom(orderDO.getProductComponents(), Map.class), false, endTime);
            Optional.ofNullable(serviceInstanceResponse)
                    .map(response -> response.body)
                    .map(body -> body.serviceInstanceId)
                    .ifPresent(orderDO::setServiceInstanceId);
        } else {
            updateServiceInstance(userInfoModel, orderDO.getServiceInstanceId(), endTime);
        }
        orderOtsHelper.updateOrder(orderDO);
    }

    private void updateServiceInstance(UserInfoModel userInfoModel, String serviceInstanceId, String endTime) {
        UpdateServiceInstanceAttributeParam updateServiceInstanceAttributeParam = new UpdateServiceInstanceAttributeParam();
        updateServiceInstanceAttributeParam.setServiceInstanceId(serviceInstanceId);
        updateServiceInstanceAttributeParam.setEndTime(endTime);
        serviceInstanceLifecycleService.updateServiceInstanceAttribute(userInfoModel, updateServiceInstanceAttributeParam);
    }

    private void handleUpdateOrderFailure(Exception e, OrderDO orderDO) {
        log.error("updateOrder error, orderDO = {}", orderDO, e);
        RefundDetailModel refundDetailModel = new RefundDetailModel();
        refundDetailModel.setRefundReason(RefundReason.SERVICE_INSTANCE_CREATION_FAILURE_REFUND);
        refundDetailModel.setMessage(String.format(RefundReason.SERVICE_INSTANCE_CREATION_FAILURE_REFUND.getDefaultMessage(), orderDO.getOrderId()));
        orderDO.setRefundDetail(JsonUtil.toJsonString(refundDetailModel));
        Long currentLocalDateTimeMillis = DateUtil.getCurrentLocalDateTimeMillis();
        String currentLocalDateTime = DateUtil.parseIs08601DateMillis(currentLocalDateTimeMillis);
        orderDO.setRefundDate(currentLocalDateTime);
        if (orderDO.getServiceInstanceId() != null && !orderDO.getServiceInstanceId().isEmpty()) {
            orderDO.setTradeStatus(TradeStatus.REFUNDING);
        } else {
            String refundId = UuidUtil.generateRefundId();
            alipayService.refundOrder(orderDO.getOrderId(), orderDO.getReceiptAmount(), refundId, PayChannel.ALIPAY);
            orderDO.setTradeStatus(TradeStatus.REFUNDED);
        }
        orderOtsHelper.updateOrder(orderDO);
    }

    @Override
    public BaseResult<Double> refundOrders(UserInfoModel userInfoModel, RefundOrderParam param) {
        String refundId = UuidUtil.generateRefundId();
        Long currentLocalDateTimeMillis = DateUtil.getCurrentLocalDateTimeMillis();
        String currentLocalDateTime = DateUtil.parseIs08601DateMillis(currentLocalDateTimeMillis);
        validateRefundParams(userInfoModel, param);
        if (StringUtils.isNotEmpty(param.getServiceInstanceId())) {
            return handleServiceInstanceRefund(userInfoModel, param, refundId, currentLocalDateTimeMillis, currentLocalDateTime);
        } else {
            return handleSingleOrderRefund(userInfoModel, param, refundId, currentLocalDateTimeMillis, currentLocalDateTime);
        }
    }

    private BaseResult<Double> handleServiceInstanceRefund(UserInfoModel userInfoModel, RefundOrderParam param, String refundId, Long currentLocalDateTimeMillis, String currentLocalDateTime) {
        List<OrderDTO> orderDTOList = orderOtsHelper.listServiceInstanceOrders(
                param.getServiceInstanceId(),
                Long.valueOf(userInfoModel.getAid()),
                false,
                TradeStatus.TRADE_SUCCESS
        );
        if (CollectionUtils.isEmpty(orderDTOList) || serviceInstanceLifeStyleHelper.checkServiceInstanceExpiration(orderDTOList, currentLocalDateTimeMillis)) {
            return BaseResult.success(0D);
        }
        return refundValidOrders(param, refundId, currentLocalDateTime, orderDTOList);
    }

    private BaseResult<Double> refundValidOrders(RefundOrderParam param, String refundId, String currentLocalDateTime, List<OrderDTO> orderDTOList) {
        Double allRefundAmount = 0D;
        boolean consumingOrderFound = false;
        RefundDetailModel refundDetailModel = createRefundDetailModel(param, RefundReason.SERVICE_INSTANCE_DELETION_REFUND);

        for (OrderDTO order : orderDTOList) {
            // 检查是否为正在消费的订单
            if (!consumingOrderFound && orderOtsHelper.isOrderInConsuming(order, DateUtil.getCurrentLocalDateTimeMillis())) {
                consumingOrderFound = true;
                refundDetailModel.setMessage(createRefundMessage(param.getServiceInstanceId(), order.getOrderId()));
                order.setRefundDetail(JsonUtil.toJsonString(refundDetailModel));
                allRefundAmount += orderOtsHelper.refundConsumingOrder(order, param.getDryRun(), refundId, currentLocalDateTime);
            }
            // 找到消费中的订单后，后面所有订单都视为未消费订单进行退款
            else if (consumingOrderFound) {
                refundDetailModel.setMessage(createRefundMessage(param.getServiceInstanceId(), order.getOrderId()));
                order.setRefundDetail(JsonUtil.toJsonString(refundDetailModel));
                allRefundAmount += orderOtsHelper.refundUnconsumedOrder(order, param.getDryRun(), refundId, currentLocalDateTime);
            }
        }

        log.info("Refunded service instance orders. Total refund amount: {}", allRefundAmount);
        return BaseResult.success(allRefundAmount);
    }

    private BaseResult<Double> handleSingleOrderRefund(UserInfoModel userInfoModel, RefundOrderParam param, String refundId, Long currentLocalDateTimeMillis, String currentLocalDateTime) {
        OrderDTO order = orderOtsHelper.getOrder(param.getOrderId(), Long.valueOf(userInfoModel.getAid()));
        if (!orderOtsHelper.validateOrderCanBeRefunded(order, Long.valueOf(userInfoModel.getAid()))) {
            throw new BizException(ErrorInfo.CURRENT_ORDER_CANT_BE_REFUNDED);
        }
        RefundReason refundReason = RefundReason.ORDER_CANCELLATION_REFUND;
        RefundDetailModel refundDetailModel = createRefundDetailModel(param, refundReason);
        refundDetailModel.setMessage(refundReason.getDefaultMessage());
        order.setRefundDetail(JsonUtil.toJsonString(refundDetailModel));
        Double refundAmount = orderOtsHelper.isOrderInConsuming(order, currentLocalDateTimeMillis) ?
                orderOtsHelper.refundConsumingOrder(order, param.getDryRun(), refundId, currentLocalDateTime) :
                orderOtsHelper.refundUnconsumedOrder(order, param.getDryRun(), refundId, currentLocalDateTime);
        return BaseResult.success(refundAmount);
    }

    private void validateRefundParams(UserInfoModel userInfoModel, RefundOrderParam param) {
        if (userInfoModel == null || StringUtils.isEmpty(userInfoModel.getAid())) {
            throw new IllegalArgumentException("User info is invalid.");
        }
        if (param == null || (StringUtils.isEmpty(param.getServiceInstanceId()) && StringUtils.isEmpty(param.getOrderId()))) {
            throw new IllegalArgumentException("The order ID and service instance ID cannot be both empty.");
        }
    }

    private RefundDetailModel createRefundDetailModel(RefundOrderParam param, RefundReason refundReason) {
        RefundDetailModel refundDetailModel = new RefundDetailModel();
        refundDetailModel.setRefundReason(refundReason);
        return refundDetailModel;
    }

    private String createRefundMessage(String serviceInstanceId, String orderId) {
        return String.format(RefundReason.SERVICE_INSTANCE_DELETION_REFUND.getDefaultMessage(), serviceInstanceId, orderId);
    }

    private OrderDO createOrderDataObject(String orderId, CreateOrderParam createOrderParam, Long userId, Double totalAmount, Long accountId, String paymentForm) {
        OrderDO orderDO = new OrderDO();
        BeanUtils.copyProperties(createOrderParam, orderDO);
        orderDO.setUserId(userId);
        orderDO.setTotalAmount(totalAmount);
        orderDO.setOrderId(orderId);
        orderDO.setAccountId(accountId);
        orderDO.setTradeStatus(TradeStatus.WAIT_BUYER_PAY);
        orderDO.setPaymentForm(paymentForm);
        return orderDO;
    }
}

