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

package org.example.service.order;

import org.example.common.BaseResult;
import org.example.common.ListResult;
import org.example.common.dataobject.OrderDO;
import org.example.common.dto.OrderDTO;
import org.example.common.model.UserInfoModel;
import org.example.common.param.order.CreateOrderParam;
import org.example.common.param.order.GetOrderParam;
import org.example.common.param.order.ListOrdersParam;
import org.example.common.param.order.RefundOrderParam;

import javax.validation.Valid;

public interface OrderService {

    OrderDTO createOrder(@Valid CreateOrderParam param);

    /**
     * Get a row of order from table store:order.
     * @param param GetOrderParam
     * @param userInfoModel UserInfo
     * @return {@link BaseResult<String>}
     */
    BaseResult<OrderDTO> getOrder(UserInfoModel userInfoModel, GetOrderParam param);

    /**
     * List orders from table store:order.
     * @param param ListOrdersParam
     * @param userInfoModel UserInfo
     * @return {@link ListResult <OrderDTO>}
     */
    ListResult<OrderDTO> listOrders(UserInfoModel userInfoModel, ListOrdersParam param);

    /**
     * Update table store:order.
     * @param userInfoModel user info
     * @param param Order data object
     */
    void updateOrder(UserInfoModel userInfoModel, OrderDO param);

    /**
     * Order refund.
     * @param param param
     * @param userInfoModel user info
     * @return true or false
     */
    BaseResult<Long> refundOrders(UserInfoModel userInfoModel, RefundOrderParam param);
}
