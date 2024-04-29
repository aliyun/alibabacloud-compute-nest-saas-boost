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

package org.example.common.helper;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.example.common.ListResult;
import org.example.common.config.SpecificationConfig;
import org.example.common.constant.OrderOtsConstant;
import org.example.common.constant.PayPeriodUnit;
import org.example.common.dto.CommodityDTO;
import org.example.common.dto.CommoditySpecificationDTO;
import org.example.common.dto.OrderDTO;
import org.example.common.errorinfo.ErrorInfo;
import org.example.common.exception.BizException;
import org.example.common.helper.ots.BaseOtsHelper;
import org.example.common.helper.ots.CommodityOtsHelper;
import org.example.common.helper.ots.CommoditySpecificationOtsHelper;
import org.example.common.helper.ots.OrderOtsHelper;
import org.example.common.model.CommodityPriceModel;
import org.example.common.utils.DateUtil;
import org.example.common.utils.MoneyUtil;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static org.example.common.utils.DateUtil.parseFromIsO8601DateString;

@Component
@Slf4j
public class WalletHelper {

    @Resource
    private SpecificationConfig specificationConfig;

    @Resource
    private CommoditySpecificationOtsHelper commoditySpecificationOtsHelper;

    @Resource
    private CommodityOtsHelper commodityOtsHelper;

    @Resource
    private OrderOtsHelper orderOtsHelper;

    private static final String DECIMAL_FORMAT = "%.2f";

    public Double getServiceCost(String serviceId, String specificationName, Long payPeriod, PayPeriodUnit payPeriodUnit) {
        Double unitPrice = specificationConfig.getPriceBySpecificationName(serviceId, specificationName, payPeriodUnit);
        if (unitPrice != null) {
            return Double.parseDouble(String.format(DECIMAL_FORMAT, payPeriod * unitPrice));
        }
        throw new BizException(ErrorInfo.SPECIFICATION_NOT_EXIST);
    }

    public CommodityPriceModel getCommodityCost(String commodityCode, String specificationName, Long payPeriod) {
        Long unitPrice;
        CommodityPriceModel commodityPriceModel = new CommodityPriceModel();
        CommodityDTO commodity = commodityOtsHelper.getCommodity(commodityCode);
        commodityPriceModel.setCommodityCode(commodityCode);
        commodityPriceModel.setServiceId(commodity.getServiceId());
        commodityPriceModel.setCommodityName(commodity.getCommodityName());
        if (StringUtils.isEmpty(specificationName)) {
            unitPrice = commodity.getUnitPrice();
        } else {
            commodityPriceModel.setSpecificationName(specificationName);
            CommoditySpecificationDTO commoditySpecification = commoditySpecificationOtsHelper.getCommoditySpecification(commodityCode, specificationName);
            unitPrice = commoditySpecification.getUnitPrice();
        }

        if (unitPrice != null) {
            commodityPriceModel.setUnitPrice(unitPrice);

            commodityPriceModel.setTotalAmount(payPeriod * unitPrice);
            return commodityPriceModel;
        }
        throw new BizException(ErrorInfo.SPECIFICATION_NOT_EXIST);
    }

    public Long getBillingEndDateTimeMillis(Long lastBillingEndDateLong, Long payPeriod, PayPeriodUnit payPeriodUnit) {
        Long billingDays = getBillingDays(payPeriodUnit, payPeriod);
        if (lastBillingEndDateLong != null) {
            return DateUtil.getIsO8601FutureDateMillis(lastBillingEndDateLong, billingDays);
        } else {
            String currentDate = DateUtil.getCurrentIs08601Time();
            return DateUtil.getIsO8601FutureDateMillis(currentDate, billingDays);
        }
    }

    public Long getRefundAmount(Long totalAmount, String refundDate, String paymentDate, Long payPeriod, PayPeriodUnit payPeriodUnit) {
        if (!checkNotNull(totalAmount, refundDate, paymentDate, payPeriod, payPeriodUnit)) {
            throw new BizException(ErrorInfo.SERVER_UNAVAILABLE);
        }
        Long refundMillis = parseFromIsO8601DateString(refundDate);
        Long paymentMillis = parseFromIsO8601DateString(paymentDate);
        Long endMillis = DateUtil.getIsO8601FutureDateMillis(paymentDate, getBillingDays(payPeriodUnit, payPeriod));
        BigDecimal refundRatio = MoneyUtil.divide(BigDecimal.valueOf(endMillis - refundMillis), BigDecimal.valueOf(endMillis - paymentMillis));
        BigDecimal refundAmount = MoneyUtil.multiply(MoneyUtil.fromCents(totalAmount), refundRatio);
        return MoneyUtil.isGreater(refundAmount, BigDecimal.ZERO) ? MoneyUtil.toCents(refundAmount) : 0L;
    }

    private Long getBillingDays(PayPeriodUnit payPeriodUnit, Long payPeriod) {
        switch (payPeriodUnit) {
            case Day:
                return payPeriod;
            case Year:
                return payPeriod * 360;
            default:
                return payPeriod * 30;
        }
    }

    private Boolean checkNotNull(Long totalAmount, String refundDate, String paymentDate, Long payPeriod, PayPeriodUnit payPeriodUnit) {
        return totalAmount != null && StringUtils.isNotEmpty(refundDate) && StringUtils.isNotEmpty(paymentDate) && payPeriod != null && payPeriodUnit != null;
    }

    public CommodityPriceModel getServiceInstanceRenewAmount(String serviceInstanceId, Long payPeriod, PayPeriodUnit payPeriodUnit) {
        List<BaseOtsHelper.OtsFilter> matchFilters = new ArrayList<>();
        BaseOtsHelper.OtsFilter serviceInstanceMatchFilter = BaseOtsHelper.OtsFilter.createMatchFilter(OrderOtsConstant.SERVICE_INSTANCE_ID, serviceInstanceId);
        matchFilters.add(serviceInstanceMatchFilter);
        ListResult<OrderDTO> orderList = orderOtsHelper.listOrders(matchFilters, null, null, null, null);
        if (orderList.getData() != null && orderList.getData().size() > 0) {
            OrderDTO order = orderList.getData().get(0);
            return getCommodityCost(order.getCommodityCode(), order.getSpecificationName(), payPeriod);
        }
        throw new BizException(ErrorInfo.SERVICE_INSTANCE_ENTITY_NOT_EXIST.getStatusCode(), ErrorInfo.SERVICE_INSTANCE_ENTITY_NOT_EXIST.getCode(),
                String.format(ErrorInfo.SERVICE_INSTANCE_ENTITY_NOT_EXIST.getMessage(), serviceInstanceId));
    }
}
