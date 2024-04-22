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

package org.example.common.utils;

import java.math.BigDecimal;
import java.text.DecimalFormat;

public class MoneyUtil {

    public static final DecimalFormat DECIMAL_FORMAT = new DecimalFormat("##0.00");

    public static String format(String value){
        if(value == null || value.isEmpty()){
            value = "0.00";
        }
        return DECIMAL_FORMAT.format(new BigDecimal(value));
    }

    public static String add(String valueStr, String addStr){
        BigDecimal value = new BigDecimal(valueStr);
        BigDecimal augend = new BigDecimal(addStr);
        return DECIMAL_FORMAT.format(value.add(augend));
    }

    public static BigDecimal add(BigDecimal value, BigDecimal augend){
        return value.add(augend);
    }

    // Subtraction
    public static String subtract(String valueStr, String subtractStr){
        BigDecimal value = new BigDecimal(valueStr);
        BigDecimal subtrahend = new BigDecimal(subtractStr);
        return DECIMAL_FORMAT.format(value.subtract(subtrahend));
    }

    public static BigDecimal subtract(BigDecimal value, BigDecimal subtrahend){
        return value.subtract(subtrahend);
    }

    // Multiplication
    public static String multiply(String valueStr, String multiplierStr){
        BigDecimal value = new BigDecimal(valueStr);
        BigDecimal multiplier = new BigDecimal(multiplierStr);
        return DECIMAL_FORMAT.format(value.multiply(multiplier));
    }

    public static BigDecimal multiply(BigDecimal value, BigDecimal multiplier){
        return value.multiply(multiplier);
    }

    // Division
    public static String divide(String valueStr, String dividerStr){
        BigDecimal value = new BigDecimal(valueStr);
        BigDecimal divider = new BigDecimal(dividerStr);
        return DECIMAL_FORMAT.format(value.divide(divider, 2, BigDecimal.ROUND_HALF_UP));
    }

    public static BigDecimal divide(BigDecimal value, BigDecimal divider){
        return value.divide(divider, 8, BigDecimal.ROUND_HALF_DOWN);
    }

    // Comparison
    public static boolean isGreaterOrEqual(String valueStr, String compareStr){
        BigDecimal value = new BigDecimal(valueStr);
        BigDecimal compareTo = new BigDecimal(compareStr);
        return value.compareTo(compareTo) >= 0;
    }

    public static boolean isGreater(BigDecimal value, BigDecimal compareTo){
        return value.compareTo(compareTo) > 0;
    }

    public static BigDecimal fromCents(long cents) {
        return BigDecimal.valueOf(cents, 2);
    }

    public static long toCents(BigDecimal value) {
        return value.multiply(BigDecimal.valueOf(100)).longValue();
    }
}


