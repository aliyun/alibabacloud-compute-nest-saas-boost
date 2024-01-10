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
package org.example.job;

import lombok.extern.slf4j.Slf4j;
import org.example.common.constant.DeployType;
import org.example.common.constant.PayloadType;
import org.example.service.OrderFcService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.concurrent.Future;

@Component
@Slf4j
@Order(2)
@ConditionalOnProperty(name = "deploy.type", havingValue = "k8s")
public class OrderJobApplicationRunner implements ApplicationRunner {

    @Value("${deploy.type}")
    private String deployType;

    @Value("${cronjob.type}")
    private PayloadType cronjobType;

    @Resource
    private OrderFcService orderFcService;

    private final ConfigurableApplicationContext context;

    public OrderJobApplicationRunner(ConfigurableApplicationContext context) {
        this.context = context;
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {
        try {
            Future<?> taskFuture = null;

            if (DeployType.K8S.getDeployType().equals(deployType)) {
                log.info("OrderJob start, jobType: {}", cronjobType);
                switch (cronjobType) {
                    case CLOSE_EXPIRED_ORDERS:
                        taskFuture = orderFcService.closeExpiredOrders();
                        break;
                    case REFUND_ORDERS:
                        taskFuture = orderFcService.refundOrders();
                        break;
                    case CLOSE_FINISHED_ORDERS:
                        taskFuture = orderFcService.closeFinishedOrders();
                    default:
                        break;
                }
            }

            if (taskFuture != null) {
                taskFuture.get();
            }
            Thread.sleep(10000);
            log.info("OrderJob success, spring boot will be shutdown");
            context.close();
            log.info("Exiting the application.");
            System.exit(0);
        } catch (Exception e) {
            log.error("OrderJob error, job type = {}.", cronjobType, e);
            System.exit(1);
        }
    }
}
