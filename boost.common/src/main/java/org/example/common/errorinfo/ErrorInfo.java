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

package org.example.common.errorinfo;

public enum ErrorInfo implements CommonErrorInfo {
    SUCCESS(200,"Success", "Success."),

    RESOURCE_NOT_FOUND(400,"InvalidParameter", "The specified resource or parameter can not be found."),

    INVALID_INPUT(400, "InvalidInput", "The input parameter(s) is invalid."),

    VERIFY_FAILED(401,"UserUnauthorized","User is Unauthorized."),

    ALIPAY_PAYMENT_FAILED(400, "PaymentFailed","Payment failed."),

    SIGNATURE_VERIFICATION_ERROR(400, "SignatureVerificationError", "AliPay Signature Verification Error"),

    ENTITY_NOT_EXIST(400, "EntityNotExist","The order entity does not exist."),

    SERVER_UNAVAILABLE(503,"ServerUnavailable","Server is unavailable."),

    SPECIFICATION_NOT_EXIST(400, "SpecificationNotExist", "The Specification does not exist."),

    COLUMN_VALUE_IS_NULL(400, "Column_Value_Is_Null", "The column value can't be null.");

    private final int statusCode;

    private final String code;

    private final String message;


    @Override
    public int getStatusCode() {
        return statusCode;
    }

    ErrorInfo(String code, String message) {
        this(400,code, message);
    }

    @Override
    public String getCode() {
        return code;
    }

    @Override
    public String getMessage() {
        return message;
    }

    ErrorInfo(int statusCode, String code, String message) {
        this.statusCode = statusCode;
        this.code = code;
        this.message = message;
    }
}
