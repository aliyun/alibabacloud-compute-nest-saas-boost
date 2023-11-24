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

package org.example.common.aspect;


import com.alicloud.openservices.tablestore.ClientException;
import com.alicloud.openservices.tablestore.TableStoreException;
import com.alipay.api.AlipayApiException;
import org.example.common.BaseResult;
import org.example.common.exception.BizException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingRequestHeaderException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import static com.alicloud.openservices.tablestore.core.ErrorCode.SERVER_UNAVAILABLE;
import static org.example.common.errorinfo.ErrorInfo.INVALID_INPUT;

@Slf4j
@RestControllerAdvice
@Order(2)
public class ExceptionHandlerAdvice {

    /**
     * Missing request header
     */
    @ExceptionHandler({MissingRequestHeaderException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public BaseResult<String> badRequestException(MissingRequestHeaderException ex) {
        log.error("Missing request header.", ex);
        return new BaseResult<>(INVALID_INPUT.getCode(), "missing request header. " + ex.getLocalizedMessage());
    }

    /**
     * Illegal argument exception
     */
    @ExceptionHandler({IllegalArgumentException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public BaseResult<String> badRequestException(IllegalArgumentException ex) {
        log.error("Illegal argument exception.", ex);
        return new BaseResult<>(INVALID_INPUT.getCode(), "Illegal argument exception.");
    }

    /**
     * Alipay exception handler
     */
    @ExceptionHandler({AlipayApiException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public BaseResult<String> badRequestException(AlipayApiException ex) {
        log.error("Alipay exception", ex);
        return new BaseResult<>(String.valueOf(HttpStatus.BAD_REQUEST.value()), "alipay api exception.");
    }

    /**
     * Access denied exception
     */
    @ExceptionHandler({AccessDeniedException.class})
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public BaseResult<String> badRequestException(AccessDeniedException ex) {
        log.error("Access Denied", ex);
        return new BaseResult<>(HttpStatus.FORBIDDEN.getReasonPhrase(), ex.getMessage());
    }

    /**
     * Parameter missing exception handler
     */
    @ExceptionHandler({MissingServletRequestParameterException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public BaseResult<String> badRequestException(Exception ex) {
        log.error("Parameter missing exception.", ex);
        return new BaseResult<>(INVALID_INPUT.getCode(), "Parameter missing exception, " + ex.getLocalizedMessage());
    }

    /**
     * Parameter validation exception handler
     */
    @ExceptionHandler({MethodArgumentNotValidException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public BaseResult<String> handlerMethodArgumentNotValidException(Exception ex) {
        log.error("Parameter validation exception.", ex);
        return new BaseResult<>(INVALID_INPUT.getCode(), "Parameter validation exception.");
    }

    /**
     * Null pointer exception
     */
    @ExceptionHandler(NullPointerException.class)
    @ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR)
    public BaseResult<String> handleTypeMismatchException(NullPointerException ex) {
        log.error("Null pointer exception.", ex);
        return BaseResult.fail("NullPointer");
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR)
    public BaseResult<String> handleUnexpectedServer(Exception ex) {
        log.error("System exception.", ex);
        return BaseResult.fail(SERVER_UNAVAILABLE);
    }

    /**
     * System exception handler
     */
    @ExceptionHandler(Throwable.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public BaseResult<String> exception(Throwable throwable) {
        log.error("System exception.", throwable);
        return new BaseResult<>(SERVER_UNAVAILABLE + "System exception, please contact the administrator!");
    }

    /**
     * Business exception handler
     */
    @ExceptionHandler(value = BizException.class)
    public ResponseEntity<BaseResult<String>> bizExceptionHandler(BizException e) {
        log.error("Business exception.", e);
        return new ResponseEntity<>(new BaseResult<>(e.getCode(), e.getMessage(), "Business exception"), HttpStatus.valueOf(e.getStatusCode()));
    }

    /**
     * OTS Client exception handler
     */
    @ExceptionHandler(value = ClientException.class)
    public ResponseEntity<BaseResult<String>> tableStoreClientExceptionHandler(ClientException e) {
        log.error("Table store client exception.", e);
        return new ResponseEntity<>(new BaseResult<>(e.getMessage(), "Business exception, please contact the administrator!"), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    /**
     * OTS Server exception handler
     */
    @ExceptionHandler(value = TableStoreException.class)
    public ResponseEntity<BaseResult<String>> tableStoreServerExceptionHandler(TableStoreException e) {
        log.error("Table store server exception.", e);
        return new ResponseEntity<>(new BaseResult<>(e.getMessage(), "Business exception, please contact the administrator!"), HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
