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

package org.example.common.config;

import mockit.Mocked;
import mockit.Tested;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.scheduling.concurrent.CustomizableThreadFactory;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;

public class ThreadPoolConfigTest {

    private Integer coorPoolSize = new Integer(20);

    @Mocked
    private CustomizableThreadFactory threadFactoryMock;

    @Tested
    private ThreadPoolConfig threadPoolConfig;

    @Test
    public void testThreadPool(@Mocked ScheduledThreadPoolExecutor executorMock) throws Exception {
        ReflectionTestUtils.setField(threadPoolConfig, "corePoolSize", coorPoolSize);
        ScheduledExecutorService result = threadPoolConfig.threadPool();
        Assert.assertNotNull(result);
    }
}
