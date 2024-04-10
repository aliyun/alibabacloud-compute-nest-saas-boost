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

    public static String generateUuid(String prefix, int randomStrLen) {
        String uuid = UUID.randomUUID().toString().replaceAll("-", "");
        return prefix + uuid.substring(0, randomStrLen);
    }
    public static String generateRefundId(){
        return generateUuid("nest-refund");
    }

    public static String generateUuid(String prefix) {
        return generateUuid(prefix, DEFAULT_RANDOM_STR_LEN);
    }

    public static String generateAliPayOutTradeNo() {
        return generateUuid("alipay-");
    }

    public static String generateOrderId(Long userId, String orderType, String sub) {
        String timestamp = DateUtil.getCurrentTimeString();
        String randomNum = generateRandomNum(1);
        if (userId != null){
            String userIdStr = String.valueOf(userId);
            userIdStr = userIdStr.substring(userIdStr.length() - 3);
            return orderType + timestamp + userIdStr + randomNum;

        } else {
            String stringWithoutDash = sub.replace("-", "");
            int maxChars = Math.min(17, stringWithoutDash.length());
            String resultString = stringWithoutDash.substring(0, maxChars);
            return orderType + timestamp + resultString + randomNum;
        }
        // Concatenate to generate an order number: 1 + 17 + 3 + 1.
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
