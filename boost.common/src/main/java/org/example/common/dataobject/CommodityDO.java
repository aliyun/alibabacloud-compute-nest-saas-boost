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
import org.example.common.constant.ChargeType;
import org.example.common.constant.CommodityStatus;
import org.example.common.constant.PayPeriodUnit;

import java.io.Serializable;

@Data
public class CommodityDO implements Serializable {

    private static final long serialVersionUID = -1469237098541930864L;

    /**
     * Partition ID generated as MD5(commodityCode).
     */
    private String pid;

    /**
     * Unique code of the commodity.
     */
    private String commodityCode;

    /**
     * Name of the commodity.
     */
    private String commodityName;

    /**
     * Type of charge (e.g., PrePaid for subscription-based, PostPaid for pay-as-you-go).
     */
    private ChargeType chargeType;

    /**
     * The service ID associated with the commodity in the computation nest.
     */
    private String serviceId;

    /**
     * Owner ID of the commodity.
     */
    private String ownerId;

    /**
     * Default Unit price of the commodity.
     */
    private Long unitPrice;

    /**
     * Status of the commodity.
     */
    private CommodityStatus commodityStatus;

    /**
     * Description of the commodity.
     */
    private String description;

    /**
     * Unit for pricing calculation (e.g., Year, Month, or PostPaid).
     */
    private String payPeriodUnit;

    /**
     * Allowed payment durations for subscription-based scenarios, such as [1, 2, 3] for years or months.
     */
    private String payPeriods;

    /**
     * Version of the service associated with the commodity.
     */
    private String serviceVersion;
}
