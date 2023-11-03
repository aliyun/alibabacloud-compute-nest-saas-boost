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

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.example.common.constant.PayloadType;
import org.example.service.AdapterManagerFcService;
import org.example.service.InvokeService;
import org.example.service.OrderFcService;
import org.example.service.ServiceInstanceFcService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Map;

@Service
@Slf4j
public class InvokeServiceImpl implements InvokeService {

    @Resource
    private OrderFcService orderFcService;

    @Resource
    private ServiceInstanceFcService serviceInstanceFcService;

    @Resource
    private AdapterManagerFcService adapterManagerFcService;

    @Override
    public String invoke(Map<String, String> header, String payload) throws Exception {
        adapterManagerFcService.clientInjection(header);
        ObjectMapper objectMapper = new ObjectMapper();
        Map<String, Object> map = null;
        try {
            map = objectMapper.readValue(payload, Map.class);
        } catch (JsonProcessingException e) {
            log.error("transform error.", e);
        }
        PayloadType parsedPayload = PayloadType.valueOf(String.valueOf(map.get("payload")));
        switch (parsedPayload) {
            case CLOSE_EXPIRED_ORDERS:
                orderFcService.closeExpiredOrders();
                break;
            case REFUND_ORDERS:
                orderFcService.refundOrders();
                break;
            case DELETE_SERVICE_INSTANCES:
                serviceInstanceFcService.deleteServiceInstances();
            default:
                break;
        }
        return "success";
    }
}
