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

package org.example.controller;

import org.example.common.BaseResult;
import org.example.common.ListResult;
import org.example.common.dto.OrderDTO;
import org.example.common.model.UserInfoModel;
import org.example.common.param.order.GetOrderParam;
import org.example.common.param.order.ListOrdersParam;
import org.example.common.param.order.RefundOrderParam;
import org.example.service.order.OrderService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.slf4j.Logger;

import java.util.Arrays;

import static org.mockito.Mockito.*;

class OrderControllerTest {
    @Mock
    private OrderService orderService;
    @Mock
    private Logger log;
    @InjectMocks
    private OrderController orderController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
    }


    @Test
    void testGetOrder() {
        when(orderService.getOrder(any(), any())).thenReturn(new BaseResult<OrderDTO>("code", "message", new OrderDTO(), "requestId"));

        BaseResult<OrderDTO> result = orderController.getOrder(new UserInfoModel("sub", "name", "loginName", "aid", "uid", Boolean.TRUE), new GetOrderParam("orderId"));
        Assertions.assertEquals(new BaseResult<OrderDTO>("code", "message", new OrderDTO(), "requestId"), result);
    }

    @Test
    void testListOrders() {
        when(orderService.listOrders(any(), any())).thenReturn(ListResult.genSuccessListResult(Arrays.asList(new OrderDTO()), 1));

        ListResult<OrderDTO> result = orderController.listOrders(new UserInfoModel("sub", "name", "loginName", "aid", "uid", Boolean.TRUE), new ListOrdersParam());
        Assertions.assertTrue(result.getCount() != 0);
    }

    @Test
    void testRefundOrder() {
        when(orderService.refundOrders(any(), any())).thenReturn(new BaseResult<Long>("code", "message", Long.valueOf(0), "requestId"));

        BaseResult<Long> result = orderController.refundOrder(new UserInfoModel("sub", "name", "loginName", "aid", "uid", Boolean.TRUE), new RefundOrderParam());
        Assertions.assertEquals(new BaseResult<Long>("code", "message", Long.valueOf(0), "requestId"), result);
    }
}
