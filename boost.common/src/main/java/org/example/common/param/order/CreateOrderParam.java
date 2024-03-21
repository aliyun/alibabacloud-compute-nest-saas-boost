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
import org.example.common.constant.ChargeType;
import org.example.common.constant.OrderType;
import org.example.common.constant.PayChannel;
import org.example.common.constant.PayPeriodUnit;
import org.example.common.param.SpiBaseParam;

import javax.validation.constraints.NotNull;

@Data
public class CreateOrderParam extends SpiBaseParam {

    /**
     * 计费类型
     */
//    @NotNull
    private ChargeType chargeType = ChargeType.PRE_PAID;

    /**
     * 支付周期
     */
    @NotNull
    private Long payPeriod;

    /**
     * 支付周期单位
     */
    @NotNull
    private PayPeriodUnit payPeriodUnit;

    /**
     * 支付渠道
     */
    private PayChannel payChannel = PayChannel.ALIPAY;

    /**
     * 订单类型
     */
//    @NotEmpty
    private String orderType = OrderType.BUY.toString();

    /**
     * 套餐名称
     */
    private String specificationName;

    /**
     * 账号id
     */
    private String userId;
}
