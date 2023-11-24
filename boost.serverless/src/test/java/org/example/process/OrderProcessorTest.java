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

package org.example.process;

import lombok.extern.slf4j.Slf4j;
import org.example.common.dto.OrderDTO;
import org.example.common.helper.BaseOtsHelper;
import org.example.common.helper.OrderOtsHelper;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.util.ReflectionTestUtils;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@Slf4j
public class OrderProcessorTest {

    private OrderProcessor orderProcessor;

    @Mock
    private OrderOtsHelper orderOtsHelper;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        orderProcessor = new OrderProcessor();
        ReflectionTestUtils.setField(orderProcessor, "orderOtsHelper", orderOtsHelper);
    }

    @Test
    public void testDoWhileLoop() {
        List<BaseOtsHelper.OtsFilter> queryFilters = new ArrayList<>();
        List<BaseOtsHelper.OtsFilter> rangeFilters = new ArrayList<>();
        Consumer<OrderDTO> consumer = mock(Consumer.class);

        orderProcessor.doWhileLoop(queryFilters, rangeFilters, consumer);

        verify(orderOtsHelper, times(1)).listOrders(eq(queryFilters), eq(rangeFilters), isNull(), isNull(), isNull());
        verify(consumer, times(0)).accept(any(OrderDTO.class));
    }

    @Test
    public void testDoWhileLoopOfThreadTask() {
        List<BaseOtsHelper.OtsFilter> queryFilters = new ArrayList<>();
        List<BaseOtsHelper.OtsFilter> rangeFilters = new ArrayList<>();
        BiConsumer<OrderDTO, CountDownLatch> taskConsumer = mock(BiConsumer.class);

        orderProcessor.doWhileLoopOfThreadTask(queryFilters, rangeFilters, taskConsumer);

        verify(taskConsumer, times(0)).accept(any(OrderDTO.class), any(CountDownLatch.class));
    }

    @Test
    public void testDoWhileLoopInternal() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException, InterruptedException {
        // 设计思路: 使用反射获取私有方法doWhileLoopInternal，构造一个空的queryFilters和rangeFilters列表，构造一个消费者consumer，构造一个任务消费者taskConsumer，构造一个任务计数器innerLatch，调用doWhileLoopInternal方法
        // 验证是否能够进入循环、执行taskConsumer.accept方法、执行innerLatch.await方法和执行consumer.accept方法
        List<BaseOtsHelper.OtsFilter> queryFilters = new ArrayList<>();
        List<BaseOtsHelper.OtsFilter> rangeFilters = new ArrayList<>();
        Consumer<OrderDTO> consumer = mock(Consumer.class);
        BiConsumer<OrderDTO, CountDownLatch> taskConsumer = mock(BiConsumer.class);
        CountDownLatch innerLatch = mock(CountDownLatch.class);
        List<OrderDTO> orders = new ArrayList<>();
        orders.add(mock(OrderDTO.class));

        Method doWhileLoopInternalMethod = OrderProcessor.class.getDeclaredMethod("doWhileLoopInternal", List.class, List.class, Consumer.class, BiConsumer.class, BiConsumer.class);
        doWhileLoopInternalMethod.setAccessible(true);
        doWhileLoopInternalMethod.invoke(orderProcessor, queryFilters, rangeFilters, consumer, taskConsumer, taskConsumer);

        verify(orderOtsHelper, times(1)).listOrders(eq(queryFilters), eq(rangeFilters), isNull(), isNull(), isNull());
        verify(taskConsumer, times(0)).accept(any(OrderDTO.class), eq(innerLatch));
        verify(innerLatch, times(0)).await();
        verify(consumer, times(0)).accept(any(OrderDTO.class));
    }

}

