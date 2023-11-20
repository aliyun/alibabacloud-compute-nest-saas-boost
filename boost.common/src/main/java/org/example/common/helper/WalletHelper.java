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


import org.apache.commons.lang3.StringUtils;
import org.example.common.config.SpecificationConfig;
import org.example.common.constant.PayPeriodUnit;
import org.example.common.errorinfo.ErrorInfo;
import org.example.common.exception.BizException;
import org.example.common.utils.DateUtil;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

import static org.example.common.utils.DateUtil.parseFromIsO8601DateString;

@Component
public class WalletHelper {

    @Resource
    private SpecificationConfig specificationConfig;

    public Double getServiceCost(String serviceId, String specificationName, Long payPeriod, PayPeriodUnit payPeriodUnit) {
        Double unitPrice = specificationConfig.getPriceBySpecificationName(serviceId, specificationName, payPeriodUnit);
        if (unitPrice != null) {
            return Double.parseDouble(String.format("%.2f", payPeriod * unitPrice));
        }
        throw new BizException(ErrorInfo.SPECIFICATION_NOT_EXIST);
    }

    public Long getBillingEndDateTimeLong(Long lastBillingEndDateLong, Long payPeriod, PayPeriodUnit payPeriodUnit) {
        Long billingDays = getBillingDays(payPeriodUnit, payPeriod);
        if (lastBillingEndDateLong != null) {
            return DateUtil.getIsO8601FutureDateMillis(lastBillingEndDateLong, billingDays);
        } else {
            String currentDate = DateUtil.getCurrentIs08601Time();
            return DateUtil.getIsO8601FutureDateMillis(currentDate, billingDays);
        }
    }

    public Double getRefundAmount(Double totalAmount, String refundDate, String paymentDate, Long payPeriod, PayPeriodUnit payPeriodUnit) {
        if (!checkNotNull(totalAmount, refundDate, paymentDate, payPeriod, payPeriodUnit)) {
            throw new BizException(ErrorInfo.SERVER_UNAVAILABLE);
        }
        Long refundMillis = parseFromIsO8601DateString(refundDate);
        Long paymentMillis = parseFromIsO8601DateString(paymentDate);
        Long endMillis = DateUtil.getIsO8601FutureDateMillis(paymentDate, getBillingDays(payPeriodUnit, payPeriod));
        Double refundRatio = Double.valueOf(endMillis - refundMillis) / Double.valueOf(endMillis - paymentMillis);
        Double refundAmount = Double.parseDouble(String.format("%.2f", totalAmount * refundRatio));
        return refundAmount > 0 ? refundAmount : 0;
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

    private Boolean checkNotNull(Double totalAmount, String refundDate, String paymentDate, Long payPeriod, PayPeriodUnit payPeriodUnit) {
        return totalAmount != null && StringUtils.isNotEmpty(refundDate) && StringUtils.isNotEmpty(paymentDate) && payPeriod != null && payPeriodUnit != null;
    }
}
