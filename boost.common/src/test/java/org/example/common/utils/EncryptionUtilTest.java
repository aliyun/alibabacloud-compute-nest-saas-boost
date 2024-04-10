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
import java.util.Arrays;
import static org.assertj.core.api.Assertions.assertThat;

class EncryptionUtilTest {

    @Test
    void testDncode() {
        Assertions.assertTrue(()->{
            byte[] test = new byte[]{-83,-21,46,-106};
            byte[] results = EncryptionUtil.decode("result");
            boolean equal = Arrays.equals(test, results);
            return equal;
        });
    }

    @Test
    void testEecode() {
        assertThat("base64String").isEqualTo(EncryptionUtil.encode(new byte[]{109, -85, 30, -21, -124, -83, -82, 41, -32}));
    }
}
