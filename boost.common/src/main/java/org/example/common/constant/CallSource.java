/*
 *Copyright (c) Alibaba Group, Inc. or its affiliates. All Rights Reserved.
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

package org.example.common.constant;

import lombok.Getter;

@Getter
public enum CallSource {

    /**
     * 计算巢用户
     */
    User(1),

    /**
     * 云市场
     */
    Market(2),

    /**
     * 计算巢服务商
     */
    Supplier(3),

    /**
     * 凌霄
     */
    Css(5),

    /**
     * 计算巢SaaS Boost
     */
    SaaSBoost(6),

    ;
    private final Integer value;

    CallSource(Integer value) {
        this.value = value;
    }

    public static CallSource to(Integer value) {
        for (CallSource source : values()) {
            if (source.getValue().equals(value)) {
                return source;
            }
        }
        return null;
    }
}