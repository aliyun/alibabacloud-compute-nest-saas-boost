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
import org.example.common.constant.ChargeType;
import org.example.common.constant.CommodityStatus;

import java.util.List;
import java.util.Map;

@Data
public class CommodityDTO {

    /**
     * Unique code of the commodity.
     */
    private String commodityCode;

    /**
     * Name of the commodity.
     */
    private String commodityName;

    /**
     * Type of charge for the commodity.
     */
    private ChargeType chargeType;

    /**
     * The service ID associated with the commodity.
     */
    private String serviceId;

    /**
     * Allowed payment durations of the commodity.
     */
    private Map<String, List<String>> allowedPaymentDurations;

    /**
     * Default Unit price of the commodity.
     */
    private Long unitPrice;

    /**
     * Description of the commodity.
     */
    private String description;

    /**
     * Status of the commodity.
     */
    private CommodityStatus commodityStatus;
}
