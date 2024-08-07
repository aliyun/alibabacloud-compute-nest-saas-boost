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

package org.example.common.param.cert;

import javax.validation.constraints.NotNull;
import lombok.Data;
import org.example.common.constant.PayChannel;

@Data
public class PutCertParam {

    /**
     * pay channel
     */
    @NotNull
    private PayChannel payChannel;

    /**
     * cert name
     */
    @NotNull
    private String certName;

    /**
     * cert content
     */
    @NotNull
    private String certContent;

    /**
     * storage method
     */
    @NotNull
    private String storageMethod;
}
