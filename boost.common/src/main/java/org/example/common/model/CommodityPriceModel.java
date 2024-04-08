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

package org.example.common.model;

import lombok.Data;

@Data
public class CommodityPriceModel {

    /**
     * unitPrice of commodity
     */
    private Long unitPrice;

    /**
     * payment total Amount
     */
    private Long totalAmount;

    /**
     * The unique code representing a specific commodity.
     * This code corresponds directly to a service within the Compute Nest ecosystem,
     * ensuring a one-to-one mapping between the commodity and its associated service.
     */
    private String commodityCode;

    /**
     * commodity name
     */
    private String commodityName;

    /**
     * This represents the set of features or services included in a commodity offer，
     * and is also used to define package in the Compute Nest.
     */
    private String specificationName;

    /**
     * payment currency
     */
    private String currency;

    /**
     * 服务id
     */
    private String serviceId;
}
