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

package org.example.common.param.order;

import lombok.Data;
import org.apache.commons.lang3.StringUtils;
import org.example.common.constant.ChargeType;
import org.example.common.constant.OrderType;
import org.example.common.constant.PayPeriodUnit;
import org.example.common.errorinfo.ErrorInfo;
import org.example.common.exception.BizException;
import org.example.common.param.SpiBaseParam;

import javax.validation.constraints.NotNull;

@Data
public class CreateOrderParam extends SpiBaseParam {

    /**
     * charge type
     */
    private ChargeType chargeType = ChargeType.PrePaid;

    /**
     * pay period
     */
    @NotNull
    private Long payPeriod;

    /**
     * pay period unit, e.g. Month
     */
    @NotNull
    private PayPeriodUnit payPeriodUnit;

    /**
     * order type
     */
    private String orderType = OrderType.BUY.toString();

    /**
     * This represents the set of features or services included in a commodity offer，
     * and is also used to define package in the Compute Nest.
     */
    private String specificationName;

    /**
     * aliUid
     */
    private String userId;

    public void checkOrderParam() {
        if (StringUtils.isEmpty(this.getUserId())) {
            throw new BizException(ErrorInfo.PARAMETER_MISSING.getStatusCode(), ErrorInfo.PARAMETER_MISSING.getCode(),
                    String.format(ErrorInfo.PARAMETER_MISSING.getMessage(), "userId"));
        }

        if (this.chargeType == null) {
            throw new BizException(ErrorInfo.PARAMETER_MISSING.getStatusCode(), ErrorInfo.PARAMETER_MISSING.getCode(),
                    String.format(ErrorInfo.PARAMETER_MISSING.getMessage(), "chargeType"));
        }

        if (StringUtils.isEmpty(this.orderType)) {
            throw new BizException(ErrorInfo.PARAMETER_MISSING.getStatusCode(), ErrorInfo.PARAMETER_MISSING.getCode(),
                    String.format(ErrorInfo.PARAMETER_MISSING.getMessage(), "orderType"));
        }
    }
}
