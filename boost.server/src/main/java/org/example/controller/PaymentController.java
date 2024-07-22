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

package org.example.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import javax.servlet.http.HttpServletResponse;
import org.example.common.BaseResult;
import org.example.common.model.UserInfoModel;
import org.example.common.param.payment.CreateTransactionParam;
import org.example.service.payment.PaymentServiceManager;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import springfox.documentation.annotations.ApiIgnore;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/api")
@Api(value="payment",tags={"payment"})
public class PaymentController {

    @Resource
    private PaymentServiceManager paymentServiceManager;

    @ApiOperation(value = "付款统一异步回调校验", nickname = "verifyTradeCallback")
    @PostMapping("/payment/verifyTradeCallback")
    public String verifyTradeCallback(HttpServletRequest request, HttpServletResponse response) {
        return paymentServiceManager.verifyTradeCallback(request, response);
    }

    @ApiOperation(value = "退款统一异步回调校验", nickname = "verifyRefundCallback")
    @PostMapping("/payment/verifyRefundCallback")
    public String verifyRefundCallback(HttpServletRequest request, HttpServletResponse response) {
        return paymentServiceManager.verifyRefundCallback(request, response);
    }

    @ApiOperation(value = "创建交易", nickname = "createTransaction")
    @PostMapping("/payment/createTransaction")
    public BaseResult<String> createTransaction(@ApiIgnore @AuthenticationPrincipal UserInfoModel userInfoModel, CreateTransactionParam param) {
        return BaseResult.success(paymentServiceManager.createTransaction(userInfoModel, param));
    }
}

