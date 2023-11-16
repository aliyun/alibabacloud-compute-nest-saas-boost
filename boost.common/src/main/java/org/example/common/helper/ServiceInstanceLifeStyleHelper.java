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
package org.example.common.helper;

import com.aliyun.computenestsupplier20210521.models.ListServiceInstancesRequest.ListServiceInstancesRequestFilter;
import org.example.common.dto.OrderDTO;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ServiceInstanceLifeStyleHelper {

    public Boolean checkServiceInstanceExpiration(List<OrderDTO> orderDtoList, Long currentLocalDateTimeMillis) {
        if (orderDtoList != null && !orderDtoList.isEmpty()) {
            OrderDTO orderDTO = orderDtoList.get(orderDtoList.size() - 1);
            if (orderDTO != null && orderDTO.getBillingEndDateLong() != null) {
                return currentLocalDateTimeMillis >= orderDTO.getBillingEndDateLong();
            }
        }
        return Boolean.FALSE;
    }

    public ListServiceInstancesRequestFilter createFilter(String key, List<String> values) {
        return new ListServiceInstancesRequestFilter()
                .setName(key)
                .setValue(values);
    }
}
