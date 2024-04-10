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
package org.example.common.param.service;

import lombok.Data;
import org.example.common.param.TemplateParameterParam;

import javax.validation.constraints.NotBlank;
import java.util.List;

@Data
public class GetServiceTemplateParameterConstraintsParam {

    /**
     * Template name
     */
    @NotBlank
    private String templateName;

    /**
     * Deploy region
     */
    @NotBlank
    private String deployRegionId;

    /**
     * Template parameter
     */
    private List<TemplateParameterParam> parameters;

    /**
     * Nest service id
     */
    private String serviceId;

    /**
     * Nest service version
     */
    private String serviceVersion;
}
