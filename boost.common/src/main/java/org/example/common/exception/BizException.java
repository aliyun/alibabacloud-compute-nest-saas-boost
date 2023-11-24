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

package org.example.common.exception;

import org.example.common.errorinfo.CommonErrorInfo;
import lombok.Data;

@Data
public class BizException extends RuntimeException {

    private static final long serialVersionUID = 5680133981298179266L;

    private int statusCode;

    private String code;

    private String message;

    public BizException() {
        super();
    }

    public BizException(CommonErrorInfo commonErrorInfo) {
        super(commonErrorInfo.getCode());
        this.statusCode = commonErrorInfo.getStatusCode();
        this.code = commonErrorInfo.getCode();
        this.message = commonErrorInfo.getMessage();
    }

    public BizException(CommonErrorInfo commonErrorInfo, Throwable cause) {
        super(commonErrorInfo.getCode(), cause);
        this.statusCode = commonErrorInfo.getStatusCode();
        this.code = commonErrorInfo.getCode();
        this.message = commonErrorInfo.getMessage();
    }

    public BizException(int statusCode, String code, String message) {
        super(code);
        this.statusCode = statusCode;
        this.code = code;
        this.message = message;
    }

    public BizException(int statusCode, String code, String message, Throwable cause) {
        super(code, cause);
        this.statusCode = statusCode;
        this.code = code;
        this.message = message;
    }

    @Override
    public Throwable fillInStackTrace() {
        return this;
    }
}
