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

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class UuidUtilTest {

    @Test
    void testGenerateUuid1() {
        assertThat(UuidUtil.generateUuid("prefix", 0)).isEqualTo("prefix");
    }

    @Test
    void testGenerateRefundId() {
        Assertions.assertEquals(UuidUtil.generateRefundId().length(), 31);
    }

    @Test
    void testGenerateUuid2() {
        Assertions.assertDoesNotThrow(()->UuidUtil.generateUuid("prefix"));
        Assertions.assertEquals(UuidUtil.generateUuid("prefix").length(), "prefixc72725e208494f2e93ef".length());

    }

    @Test
    void testGenerateAliPayOutTradeNo() {
        Assertions.assertEquals(UuidUtil.generateAliPayOutTradeNo().length(), 27);
    }

    @Test
    void testGenerateOrderId() {
        assertThat(UuidUtil.generateOrderId(1234567890L, "Alipay").length()).isEqualTo(27L);
    }
}
