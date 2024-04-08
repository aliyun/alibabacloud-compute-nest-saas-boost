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

    ENTITY_NOT_EXIST(400, "EntityNotExist","The entity \"%s\" does not exist."),

    SERVER_UNAVAILABLE(503,"ServerUnavailable","Server is unavailable."),

    SPECIFICATION_NOT_EXIST(400, "SpecificationNotExist", "The Specification does not exist."),

    COLUMN_VALUE_IS_NULL(400, "Column_Value_Is_Null", "The column value \"%s\" can't be null."),

    CURRENT_ORDER_CANT_BE_REFUNDED(400, "Current_Order_Cant_Be_Refunded", "The Current Order Cant be refunded."),

    SIGNATURE_PARSED_FAILED(400, "SignatureParsedFailed", "AliPay Signature Parsed Failed"),

    USER_NOT_ADMIN(400, "UserNotAdmin",  "Access denied. The current user is not an administrator."),

    SPI_TOKEN_VALIDATION_FAILED(403, "SpiTokenValidationFailed", "The specified SPI Token \"%s\" Validation Failed"),

    SERVICE_INSTANCE_CREATE_FAILED(400, "ServiceInstanceCreateFailed", "the specified order id \"%s\" create service instance failed."),

    PARAMETER_MISSING(400, "ParameterMissing", "The parameter \"%s\" is missing."),

    COMMON_REQUEST_FAILED(400, "%s", "Common Request Failed, request action (\"%s\"), request parameters (\"%s\") failed. Request Error message: \"%s\"."),

    SERVICE_PROVIDER_KEY_NOT_EXIST(400, "ServiceProviderKeyNotExist", "The specified serviceProviderKey does not exist for serviceId=%s and commodityCode=%s."),

    DELETION_NOT_ALLOWED(400, "DeletionNotAllowed", "Service Instance \"%s\" Deletion is not allowed for the current serviceType: \"%s\"."),

    INVALID_SPI_PARAMETER(400, "InvalidParameter", "The provided SPI Parameter \"%s\" missing token or commodity code."),

    ;

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
