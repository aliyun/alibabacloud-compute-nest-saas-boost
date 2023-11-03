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

import com.alipay.api.AlipayApiException;
import com.aliyun.computenestsupplier20210521.models.CreateServiceInstanceResponse;
import lombok.extern.slf4j.Slf4j;
import org.example.common.BaseResult;
import org.example.common.ListResult;
import org.example.common.constant.OrderOtsConstant;
import org.example.common.constant.PayPeriodUnit;
import org.example.common.constant.TradeStatus;
import org.example.common.dataobject.OrderDO;
import org.example.common.dto.OrderDTO;
import org.example.common.errorinfo.ErrorInfo;
import org.example.common.helper.BaseOtsHelper.OtsFilter;
import org.example.common.helper.OrderOtsHelper;
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
import org.springframework.beans.BeanUtils;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.util.Arrays;
import java.util.Collections;
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

    @Override
    public BaseResult<String> createOrder(UserInfoModel userInfoModel, CreateOrderParam param) throws AlipayApiException {
        Long userId = Long.parseLong(userInfoModel.getUid());
        Long accountId = Long.parseLong(userInfoModel.getAid());
        String orderId = UuidUtil.generateOrderId(userId, param.getType().getValue());
        Map<String, Object> nestParameters = (Map<String, Object>) JsonUtil.parseObjectCustom(param.getProductComponents(), Map.class);
        long payPeriod = ((Number) nestParameters.remove(PAY_PERIOD)).longValue();
        GetServiceCostParam getServiceCostParam = new GetServiceCostParam();
        if (nestParameters == null) {
            return BaseResult.fail("product components can't be null.");
        }
        BeanUtil.populateObject(nestParameters, getServiceCostParam);
        getServiceCostParam.setPayPeriodUnit(PayPeriodUnit.valueOf( String.valueOf(nestParameters.remove(PAY_PERIOD_UNIT))));
        getServiceCostParam.setPayPeriod(payPeriod);
        Double cost = serviceInstanceLifecycleService.getServiceCost(userInfoModel, getServiceCostParam).getData();
        CreateServiceInstanceResponse response = serviceInstanceLifecycleService.createServiceInstance(userInfoModel, nestParameters, true);
        if (response == null || !response.getStatusCode().equals(HttpStatus.OK.value())) {
            return BaseResult.fail(ErrorInfo.SERVER_UNAVAILABLE);
        }
        String webForm = alipayService.createTransaction(cost, param.getProductName().getDisplayName(), orderId);
        if (!StringUtils.isEmpty(webForm)) {
            orderOtsHelper.createOrder(createOrderDataObject(orderId, param, accountId, cost, accountId, getServiceCostParam));
            log.info("The Alipay web form has been successfully created with the following content{}.", webForm);
            return BaseResult.success(webForm);
        }
        log.warn("The Alipay web form create failed, user id = {}, order id = {}", accountId, orderId);
        return BaseResult.fail("The Alipay web form create failed.");
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
        OtsFilter matchFilter = OtsFilter.builder().key(OrderOtsConstant.FILTER_NAME_0).values(Collections.singletonList(userInfoModel.getAid())).build();
        Long startTimeMills = DateUtil.parseFromIsO8601DateString(param.getStartTime());
        Long endTimeMills = DateUtil.parseFromIsO8601DateString(param.getEndTime());
        OtsFilter rangeFilter = OtsFilter.builder().key(OrderOtsConstant.SEARCH_INDEX_FIELD_NAME_1).values(Arrays.asList(startTimeMills, endTimeMills)).build();
        return orderOtsHelper.listOrders(Collections.singletonList(matchFilter), Collections.singletonList(rangeFilter), param.getNextToken(), param.getReverse());
    }

    @Override
    public void updateOrder(UserInfoModel userInfoModel, OrderDO orderDO) {
        if (DateUtil.isValidSimpleDateTimeFormat(orderDO.getGmtCreate())) {
            orderDO.setGmtCreate(DateUtil.convertToIso8601Format(orderDO.getGmtCreate()));
        }
        if (DateUtil.isValidSimpleDateTimeFormat(orderDO.getGmtPayment())) {
            orderDO.setGmtPayment(DateUtil.convertToIso8601Format(orderDO.getGmtPayment()));
        }
        CreateServiceInstanceResponse serviceInstanceResponse = null;
        try {
            serviceInstanceResponse = serviceInstanceLifecycleService.createServiceInstance(userInfoModel, JsonUtil.parseObjectCustom(orderDO.getProductComponents(), Map.class), false);
            orderDO.setServiceInstanceId(serviceInstanceResponse.body.serviceInstanceId);
            orderOtsHelper.updateOrder(orderDO);
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
        OrderDTO order = orderOtsHelper.getOrder(param.getOrderId(), Long.valueOf(userInfoModel.getAid()));
        String currentIs08601Time = DateUtil.getCurrentIs08601Time();
        Double totalAmount = order.getTotalAmount() == null ? order.getReceiptAmount() : order.getTotalAmount();
        Double refundAmount = walletHelper.getRefundAmount(totalAmount, currentIs08601Time, order.getGmtPayment(), order.getPayPeriod(), order.getPayPeriodUnit());
        if (param.getDryRun()) {
            return BaseResult.success(refundAmount);
        }
        String refundId = UuidUtil.generateRefundId();
        OrderDO refundOrder = new OrderDO();
        BeanUtils.copyProperties(order, refundOrder);
        refundOrder.setRefundId(refundId);
        refundOrder.setRefundAmount(refundAmount);
        refundOrder.setRefundDate(currentIs08601Time);
        refundOrder.setTradeStatus(TradeStatus.REFUNDING);
        orderOtsHelper.updateOrder(refundOrder);
        return BaseResult.success(refundAmount);
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

