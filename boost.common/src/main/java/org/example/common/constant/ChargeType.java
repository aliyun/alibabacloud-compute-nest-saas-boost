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
public enum ChargeType {

    /*===================按量付费类型====================*/
    PostPaid("PostPaid"),


    /*===============预付费，包年包月类型=================*/
    PrePaid("PrePaid"),
    ;

    private final String value;

    ChargeType(String value) {
        this.value = value;
    }

    public static ChargeType to(String value) {
        for (ChargeType chargeType : ChargeType.values()) {
            if (chargeType.getValue().equalsIgnoreCase(value)) {
                return chargeType;
            }
        }
        throw new IllegalArgumentException("Unknown ChargeType value: " + value);
    }
}
