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

package org.example.common.dto;

import lombok.Data;

@Data
public class CommoditySpecificationDTO {

    /**
     * Code of the commodity associated with this specification.
     */
    private String commodityCode;

    /**
     * Name of the specification.
     */
    private String specificationName;

    /**
     * Allowed payment durations for subscription-based scenarios, such as [1, 2, 3] for years or months.
     */
    private String payPeriods;

    /**
     * Price per unit for the specification.
     */
    private Long unitPrice;

    /**
     * Currency unit for the specification.
     */
    private String currency;

    /**
     * Unit for pricing calculation (e.g., Year, Month, or PostPaid).
     */
    private String payPeriodUnit;
}
