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
package org.example.common.param.commodity;

import lombok.Data;
import org.example.common.constant.ChargeType;
import org.example.common.constant.CommodityStatus;
import org.example.common.constant.PayPeriodUnit;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;

@Data
public class CreateCommodityParam {

    /**
     * Name of the commodity.
     */
    @NotEmpty
    private String commodityName;

    /**
     * Type of charge (e.g., PrePaid for subscription-based, PostPaid for pay-as-you-go).
     */
    @NotNull
    private ChargeType chargeType;

    /**
     * The service ID associated with the commodity in the computation nest.
     */
    @NotEmpty
    private String serviceId;

    /**
     * Unit price of the commodity.
     */
    @NotNull
    private Long unitPrice;

    /**
     * Description of the commodity.
     */
    private String description;

    /**
     * Status of the commodity.
     */
    private CommodityStatus commodityStatus;

    /**
     * Unit for pricing calculation (e.g., Year, Month, or PostPaid).
     */
    @NotNull
    private PayPeriodUnit payPeriodUnit;

    /**
     * Allowed payment durations for subscription-based scenarios, such as [1, 2, 3] for years or months.
     */
    @NotNull
    private List<Long> payPeriods;
}
