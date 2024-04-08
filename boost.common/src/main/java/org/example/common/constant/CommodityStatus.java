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
package org.example.common.constant;

import lombok.Getter;

@Getter
public enum CommodityStatus {

    /* 草稿状态 */
    DRAFT("Draft"),

    /* 上线状态 */
    ONLINE("Online"),
    ;

    private final String value;

    CommodityStatus(String value) {
        this.value = value;
    }

    /**
     * 将字符串值转换为枚举类型的实用方法。
     *
     * @param value 要转换的字符串值
     * @return 对应的CommodityStatus枚举值
     */
    public static CommodityStatus to(String value) {
        for (CommodityStatus status : CommodityStatus.values()) {
            if (status.getValue().equalsIgnoreCase(value)) {
                return status;
            }
        }
        throw new IllegalArgumentException("Unknown CommodityStatus value: " + value);
    }
}

