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
     * parameterMetadata for compute nest service
     */
    private String parameterMetadata;

    /**
     * compute nest service specifications, contains the predefined parameters.
     */
    private String specifications;

    /**
     * template name
     */
    private String templateName;

    /**
     * A list of regions where deployment is allowed.
     */
    private String allowedRegions;

    /**
     * The unique code representing a specific commodity.
     * This code corresponds directly to a service within the Compute Nest ecosystem,
     * ensuring a one-to-one mapping between the commodity and its associated service.
     */
    private String commodityCode;

    /**
     * The number of days a hosted Compute Nest service instance is retained after it has expired.
     */
    private Integer retentionDays;

    /**
     * compute nest service status, including:
     * 1. CREATING: The service is being created.
     * 2. ONLINE: The service is active.
     * 3. CREATING: The service is inactive.
     * ...
     */
    private String status;

    /**
     * service version
     */
    private String version;
}
