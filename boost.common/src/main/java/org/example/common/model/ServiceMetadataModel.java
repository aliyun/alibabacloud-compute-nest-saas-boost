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

import lombok.Data;


@Data
public class ServiceMetadataModel {

    /**
     * 参数元数据
     */
    private String parameterMetadata;

    /**
     * 套餐数据
     */
    private String specifications;

    /**
     * 模板名称
     */
    private String templateName;

    /**
     * 允许的地域
     */
    private String allowedRegions;

    /**
     * 商品code
     */
    private String commodityCode;

    /**
     * 保留天数
     */
    private Integer retentionDays;

    /**
     * 服务状态
     */
    private String status;

    /**
     * 服务版本
     */
    private String version;
}
