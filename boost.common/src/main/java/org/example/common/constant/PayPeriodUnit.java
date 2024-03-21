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

public enum PayPeriodUnit {

    /**
     * Monthly subscription.
     */
    Month("month"),

    /**
     * Daily subscription.
     */
    Day("day"),

    /**
     * Annual subscription.
     */
    Year("year");

    private String name;

    PayPeriodUnit(String name){
        this.name = name;
    }

    public static PayPeriodUnit to(String value) {
        for (PayPeriodUnit unit : PayPeriodUnit.values()) {
            if (unit.name.equalsIgnoreCase(value)) {
                return unit;
            }
        }
        throw new IllegalArgumentException("No enum constant " + PayPeriodUnit.class.getCanonicalName() + " with value " + value);
    }

    @Override
    public String toString() {
        return name();
    }
}
