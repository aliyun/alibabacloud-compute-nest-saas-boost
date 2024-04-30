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

package org.example.common.model;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.common.constant.CallSource;
import org.example.common.constant.ServiceType;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ServiceInstanceModel {

    private String serviceInstanceId;

    private String serviceInstanceName;

    private String createTime;

    private String updateTime;

    private String status;

    private Long progress;

    private String serviceName;

    private ServiceModel serviceModel;

    private String parameters;

    private String outputs;

    private String resources;

    private CallSource source;

    private String orderId;

    private ServiceType serviceType;

    private String endTime;
}
