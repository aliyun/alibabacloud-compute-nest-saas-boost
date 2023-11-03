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

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class OpenAPIErrorMessageUtil {

    private static final Pattern PATTERN = Pattern.compile("\\d+.,\\s(.+)\\srequest\\sid");

    /**
     * @param errorMessage The error message from OpenAPI
     * @return Keep only the error message and remove the prefix "code" and the suffix "request id" from OpenAPI.
     */
    public static String getErrorMessageFromComputeNestError(String errorMessage) {
        Matcher matcher = PATTERN.matcher(errorMessage);
        if (matcher.find()) {
            return matcher.group(1);
        }
        else {
            return "";
        }
    }
}
