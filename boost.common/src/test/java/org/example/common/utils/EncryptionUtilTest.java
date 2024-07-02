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
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

class EncryptionUtilTest {

    @Test
    void testdiji() {
        System.out.println(EncryptionUtil.getMd5HexString("低级版"));
    }

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

    @Test
    public void getMd5HexString() {
        String data1 = "10000#cn-hangzhou#20#[Filter(name=ServiceId, values=[1000, 2000, 3000]), Filter(name=DeployType, "
                + "values=[ros, spi])]#not null#false#[{\"key\":\"key\",\"value\":\"value\"}]";
        String data2 = "10000#cn-hangzhou#20#[Filter(name=ServiceId, values=[1000, 2000, 3000]), Filter(name=DeployType, "
                + "values=[ros, spi])]#not null#false#[{\"key\":\"key1\",\"value\":\"value2\"}]";

        String md5HexString1 = EncryptionUtil.getMd5HexString(data1);
        String md5HexString2 = EncryptionUtil.getMd5HexString(data1);
        String md5HexString3 = EncryptionUtil.getMd5HexString(data2);

        assertEquals(32, md5HexString1.length());
        assertEquals(md5HexString1, md5HexString2);
        assertNotEquals(md5HexString1, md5HexString3);

    }
}
