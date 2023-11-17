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
import org.example.common.ListResult;
import org.example.common.dto.OrderDTO;
import org.example.common.helper.BaseOtsHelper.OtsFilter;
import org.example.common.helper.OrderOtsHelper;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.function.BiConsumer;
import java.util.function.Consumer;


@Component
@Slf4j
public class OrderProcessor {

    @Resource
    private OrderOtsHelper orderOtsHelper;

    public void doWhileLoop(List<OtsFilter> queryFilters, List<OtsFilter> rangeFilters, Consumer<OrderDTO> consumer) {
        doWhileLoopInternal(queryFilters, rangeFilters, consumer, null, (order, latch) -> {
            consumer.accept(order);
        });
    }

    public void doWhileLoopOfThreadTask(List<OtsFilter> queryFilters, List<OtsFilter> rangeFilters, BiConsumer<OrderDTO, CountDownLatch> consumer) {
        doWhileLoopInternal(queryFilters, rangeFilters, null, consumer, consumer);
    }

    private void doWhileLoopInternal(List<OtsFilter> queryFilters, List<OtsFilter> rangeFilters, Consumer<OrderDTO> consumer, BiConsumer<OrderDTO, CountDownLatch> taskConsumer, BiConsumer<OrderDTO, CountDownLatch> latchConsumer) {
        String nextToken = null;
        List<OrderDTO> orders = null;

        do {
            ListResult<OrderDTO> result = orderOtsHelper.listOrders(queryFilters, rangeFilters, nextToken, null);
            if (result != null && result.getData() != null && !result.getData().isEmpty()) {
                orders = new ArrayList<>(result.getData());
                nextToken = result.getNextToken();

                if (!CollectionUtils.isEmpty(orders)) {
                    if (taskConsumer != null) {
                        CountDownLatch innerLatch = new CountDownLatch(orders.size());
                        orders.forEach(order -> {
                            taskConsumer.accept(order, innerLatch);
                        });
                        orders.clear();
                        try {
                            innerLatch.await(); // 等待所有任务执行完毕
                        } catch (InterruptedException e) {
                            log.error("Interrupted while waiting for tasks to complete.", e);
                            Thread.currentThread().interrupt();
                        }
                    } else if (consumer != null) {
                        orders.forEach(consumer);
                        orders.clear();
                    }
                } else {
                    break;
                }
            }
        } while (!CollectionUtils.isEmpty(orders) || !StringUtils.isEmpty(nextToken));
    }
}
