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

package org.example.common;

import org.example.common.errorinfo.CommonErrorInfo;
import lombok.Data;
import org.slf4j.MDC;
import org.springframework.http.HttpStatus;

import java.io.Serializable;

import static org.example.common.constant.Constants.REQUEST_ID;

@Data
public class BaseResult<T> implements Serializable {
    private static final long serialVersionUID = 5680133981298179266L;

    /**
     * Status code.
     */
    protected String code;

    /**
     * Response message.
     */
    protected String message;

    /**
     * Return data.
     */
    private T data;

    protected String requestId;

    public static <T> BaseResult<T> success() {
        return new BaseResult<>();
    }

    public static <T> BaseResult<T> success(T data) {
        return new BaseResult<>(data);
    }

    public static <T> BaseResult<T> fail(String message) {
        return new BaseResult<>(String.valueOf(HttpStatus.INTERNAL_SERVER_ERROR.value()), message);
    }

    public static <T> BaseResult<T> fail(CommonErrorInfo commonErrorInfo) {
        return new BaseResult<>(commonErrorInfo);
    }

    public BaseResult() {
        this.code = String.valueOf(HttpStatus.OK.value());
        this.message = HttpStatus.OK.getReasonPhrase();
        this.requestId = MDC.get(REQUEST_ID);
    }

    public BaseResult(String code, String message, T data, String requestId) {
        this.code = code;
        this.message = message;
        this.data = data;
        this.requestId = requestId;
    }

    public BaseResult(HttpStatus httpStatus) {
        this(String.valueOf(httpStatus.value()), httpStatus.getReasonPhrase());
    }

    public BaseResult(CommonErrorInfo commonErrorInfo) {
        this(commonErrorInfo.getCode(), commonErrorInfo.getMessage());
    }

    /**
     *  If there is no data returned, you can manually specify the status code and message.
     */
    public BaseResult(String code, String msg) {
        this(code,msg,null,MDC.get(REQUEST_ID));
    }

    /**
     * When data is returned, the status code is 200, and the default message is "Operation successful!"
     */
    public BaseResult(T data) {
        this(HttpStatus.OK.getReasonPhrase(), data);
    }

    /**
     * When data is returned, the status code is 200, and the message can be manually specified.
     */
    public BaseResult(String msg, T data) {
        this(String.valueOf(HttpStatus.OK.value()), msg, data, MDC.get(REQUEST_ID));
    }

    /**
     * When data is returned, you can customize the status code and manually specify the message.
     */
    public BaseResult(String code, String msg,T data) {
        this(code, msg, data, MDC.get(REQUEST_ID));
    }
}
