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

package org.example.common.param.commodity.specification;

import lombok.Data;
import org.example.common.constant.Currency;
import org.example.common.constant.PayPeriodUnit;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;

@Data
public class CreateCommoditySpecificationParam extends CommoditySpecificationParam {

    /**
     * Unit for pricing calculation.
     */
    @NotNull
    private PayPeriodUnit payPeriodUnit;

    /**
     * Allowed payment durations for the specification.
     */
    @NotEmpty(message = "payPeriods is null.")
    private List<Long> payPeriods;

    /**
     * Price per unit for the specification.
     */
    @NotNull(message = "unitPrice is null.")
    private Double unitPrice;

    /**
     * Currency unit for the specification.
     */
    @NotNull(message = "currency is null.")
    private Currency currency;
}
