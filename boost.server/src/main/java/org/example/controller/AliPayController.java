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

import io.swagger.annotations.ApiOperation;
import org.example.service.AlipayService;
import io.swagger.annotations.Api;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/alipay")
@Api(value="alipay",tags={"alipay"})
public class AliPayController {

    @Resource
    private AlipayService alipayService;

    @ApiOperation(value = "支付宝异步回调校验接口", nickname = "verifyTradeCallback")
    @PostMapping("/verifyTradeCallback")
    public String verifyTradeCallback(HttpServletRequest request) {
        return alipayService.verifyTradeCallback(request);
    }

}

