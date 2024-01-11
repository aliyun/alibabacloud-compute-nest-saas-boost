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

package org.example.common.adapter;

import org.springframework.boot.ApplicationRunner;
import org.springframework.scheduling.annotation.Scheduled;

import java.util.Map;

public interface AdapterManager extends ApplicationRunner {

    /**
     * Creating and injecting the required client for the serverless during method runtime.
     * @param header Authorization parameters.
     * @throws Exception create exception.
     */
    void clientInjection(Map<String, String> header) throws Exception;

    /**
     * Client ak injection.
     * @throws Exception update exception.
     */
    @Scheduled(cron = "0 */30 * * * *",fixedDelay = 300000)
    void updateClient() throws Exception;
}
