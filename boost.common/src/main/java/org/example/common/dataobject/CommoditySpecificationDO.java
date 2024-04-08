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

package org.example.common.dataobject;

import lombok.Data;
import org.example.common.constant.Currency;
import org.example.common.constant.PayPeriodUnit;

import java.io.Serializable;

@Data
public class CommoditySpecificationDO implements Serializable {

    private static final long serialVersionUID = 187088115785196749L;

    /**
     * Primary key: Partition ID generated as MD5(SpecificationName).
     */
    private String pid;

    /**
     * Partition ID generated as MD5(CommodityCode).
     */
    private String cid;

    /**
     * Code of the commodity associated with this specification.
     */
    private String commodityCode;

    /**
     * Name of the specification. It is unique within the same commodity.
     */
    private String specificationName;

    /**
     * Unit for pricing calculation (e.g., Year, Month, or PostPaid).
     */
    private PayPeriodUnit payPeriodUnit;

    /**
     * Allowed payment durations for subscription-based scenarios, such as [1, 2, 3] for years or months.
     */
    private String payPeriods;

    /**
     * Price per unit.
     */
    private Long unitPrice;

    /**
     * Currency unit (e.g., CNY for Chinese Yuan).
     */
    private Currency currency;
}
