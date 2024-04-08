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

import lombok.extern.slf4j.Slf4j;

import java.util.Random;
import java.util.UUID;

@Slf4j
public class UuidUtil {

    private static final int DEFAULT_RANDOM_STR_LEN = 20;

    private static final String NEST_REFUND_ID_PREFIX = "nest-refund";

    private static final String COMMODITY_CODE_PREFIX = "saas-boost-";

    public static String generateUuid(String prefix, int randomStrLen) {
        String uuid = UUID.randomUUID().toString().replaceAll("-", "");
        return prefix + uuid.substring(0, randomStrLen);
    }

    public static String generateRefundId() {
        return generateUuid(NEST_REFUND_ID_PREFIX);
    }

    public static String generateUuid(String prefix) {
        return generateUuid(prefix, DEFAULT_RANDOM_STR_LEN);
    }

    public static String generateCommodityCode() {
        return generateUuid(COMMODITY_CODE_PREFIX, 8);
    }

    public static String generateOrderId(Long userId) {
        return generateOrderId(userId, null);
    }

    public static String generateOrderId(Long userId, String sub) {
        String timestamp = DateUtil.getCurrentTimeString();
        timestamp = timestamp.substring(Math.max(0, timestamp.length() - 8));
        String randomNum = generateRandomNum(1);
        if (userId != null) {
            String userIdStr = String.valueOf(userId);
            int start = Math.max(0, userIdStr.length() - 8);
            String resultString = userIdStr.substring(start);
            return resultString + timestamp + randomNum;
        } else {
            String stringWithoutDash = sub.replace("-", "");
            int maxChars = Math.min(8, stringWithoutDash.length());
            String resultString = stringWithoutDash.substring(0, maxChars);
            return timestamp + resultString + randomNum;
        }
        // Concatenate to generate an order number: 8 + 8 + 1.
    }

    private static String generateRandomNum(int num) {
        StringBuilder sb = new StringBuilder();
        Random random = new Random();
        for (int i = 0; i < num; i++) {
            sb.append(random.nextInt(10));
        }
        return sb.toString();
    }
}
