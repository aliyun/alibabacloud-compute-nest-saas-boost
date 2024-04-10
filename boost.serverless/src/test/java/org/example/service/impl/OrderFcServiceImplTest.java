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

package org.example.service.impl;

import org.example.common.ListResult;
import org.example.common.adapter.BaseAlipayClient;
import org.example.common.adapter.ComputeNestSupplierClient;
import org.example.common.dataobject.OrderDO;
import org.example.common.dto.OrderDTO;
import org.example.common.helper.ots.OrderOtsHelper;
import org.example.common.utils.DateUtil;
import org.example.process.OrderProcessor;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.util.Arrays;
import java.util.Collections;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ScheduledExecutorService;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.powermock.api.mockito.PowerMockito.whenNew;

@RunWith(PowerMockRunner.class)
@PrepareForTest({OrderFcServiceImpl.class, DateUtil.class})
public class OrderFcServiceImplTest {

    @Mock
    private OrderOtsHelper orderOtsHelper;
    @Mock
    private BaseAlipayClient baseAlipayClient;
    @Mock
    private ComputeNestSupplierClient computeNestSupplierClient;
    @Mock
    private ScheduledExecutorService scheduledThreadPool;

    @InjectMocks
    private OrderFcServiceImpl orderService;

    @Mock
    private OrderProcessor orderProcessor;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testCloseExpiredOrders() throws Exception {
        OrderDTO order1 = new OrderDTO();
        order1.setOrderId("order1");
        OrderDTO order2 = new OrderDTO();
        order2.setOrderId("order2");

        ListResult<OrderDTO> result1 = new ListResult<>();
        result1.setData(Arrays.asList(order1, order2));
        result1.setNextToken("token1");
        ListResult<OrderDTO> result2 = new ListResult<>();
        result2.setData(Collections.emptyList());

        PowerMockito.mockStatic(DateUtil.class);
        PowerMockito.when(DateUtil.getMinutesAgoLocalDateTimeMillis(anyInt())).thenReturn(10L, 20L);

        when(orderOtsHelper.listOrders(anyList(), anyList(),anyList(), eq(null), anyList())).thenReturn(result1);
        when(orderOtsHelper.listOrders(anyList(), anyList(),anyList(), eq("token1"), anyList())).thenReturn(result2);

        OrderDO orderDO1 = new OrderDO();
        orderDO1.setOrderId("order1");
        OrderDO orderDO2 = new OrderDO();
        orderDO2.setOrderId("order2");

        whenNew(OrderDO.class).withNoArguments().thenReturn(orderDO1, orderDO2);

        orderService.closeExpiredOrders();

        verify(orderProcessor, times(1)).doWhileLoop(anyList(), anyList(), any());
    }

    @Test
    public void testRefundOrders() throws Exception {
        OrderDTO order1 = new OrderDTO();
        order1.setOrderId("order1");
        OrderDTO order2 = new OrderDTO();
        order2.setOrderId("order2");

        ListResult<OrderDTO> result1 = new ListResult<>();
        result1.setData(Arrays.asList(order1, order2));
        result1.setNextToken("token1");
        ListResult<OrderDTO> result2 = new ListResult<>();
        result2.setData(Collections.emptyList());
        result2.setNextToken(null);
        PowerMockito.mockStatic(DateUtil.class);
        PowerMockito.when(DateUtil.getCurrentLocalDateTimeMillis()).thenReturn(100000L);
        PowerMockito.when(DateUtil.getOneYearAgoLocalDateTimeMillis()).thenReturn(90000L);

        when(orderOtsHelper.listOrders(anyList(), anyList(), anyList(), eq(null), anyList())).thenReturn(result1);
        when(orderOtsHelper.listOrders(anyList(), anyList(), anyList(), eq("token1"), anyList())).thenReturn(result2);

        CountDownLatch latch = mock(CountDownLatch.class);
        whenNew(CountDownLatch.class).withParameterTypes(int.class).withArguments(eq(2)).thenReturn(latch);

        orderService.refundOrders();

        verify(orderProcessor, times(1)).doWhileLoopOfThreadTask(anyList(), anyList(), any());
    }
}
