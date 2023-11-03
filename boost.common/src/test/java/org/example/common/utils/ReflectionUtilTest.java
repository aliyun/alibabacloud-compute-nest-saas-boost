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

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;

class ReflectionUtilTest {

    @Test
    void testGetPropertyNames() {
        assertThat(ReflectionUtil.getPropertyNames(String.class)).isEqualTo(Arrays.asList("bytes", "empty"));
    }

    @Test
    void testGetAllFields() {
        final Field[] result = ReflectionUtil.getAllFields(String.class);
        Assertions.assertTrue(result.length > 0);
    }

    @Test
    void testGetFieldByColumnName() {
        final Field expectedResult = null;
        final Field result = ReflectionUtil.getFieldByColumnName(String.class, "columnName");
        assertThat(result).isEqualTo(expectedResult);
    }

    @Test
    void testFindMethod() {
        final Method expectedResult = null;
        final Method result = ReflectionUtil.findMethod(String.class, "methodName", String.class);
        assertThat(result).isEqualTo(expectedResult);
    }
}
