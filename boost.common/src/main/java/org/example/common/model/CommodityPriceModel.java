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
     * 单价
     */
    private Double unitPrice;

    /**
     * 支付总价
     */
    private Double totalAmount;

    /**
     * 商品code
     */
    private String commodityCode;

    /**
     * 商品名称
     */
    private String commodityName;

    /**
     * 套餐名称
     */
    private String specificationName;

    /**
     * 币种
     */
    private String currency;

    /**
     * 服务id
     */
    private String serviceId;
}
