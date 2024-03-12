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

package org.example.common.adapter.impl;

import com.aliyuncs.CommonRequest;
import com.aliyuncs.http.MethodType;
import org.example.common.dataobject.OrderDO;
import org.example.common.model.UserInfoModel;
import org.junit.jupiter.api.Test;
class AcsApiCallerImplTest {

    @Test
    public void testPayOrderCallback() {
        AcsApiCallerImpl acsApiCaller = new AcsApiCallerImpl();
        acsApiCaller.createClient("", "");
        OrderDO orderDO = new OrderDO();
        orderDO.setOrderId("0202403251005284455220");
        orderDO.setServiceId("");
        UserInfoModel userInfoModel = new UserInfoModel();
        userInfoModel.setAid("1563457855438522");
        CommonRequest test = test();

    }

    private CommonRequest test() {
        CommonRequest request = new CommonRequest();
        request.setSysRegionId("cn-hangzhou");
        request.setSysProduct("ComputeNestSupplier");
        request.setSysMethod(MethodType.POST);
        request.setSysDomain("computenestsupplier.cn-hangzhou.aliyuncs.com");
        request.setSysVersion("2021-05-21");
        request.setSysAction("PayOrderCallback");
        request.putQueryParameter("ServiceId", "service-70b5fa5c9d784e80ae47");
        request.putQueryParameter("OrderId", "0202403251114505685227");
        request.putQueryParameter("OrderType", "1");
        request.putQueryParameter("RegionId", "cn-hangzhou");
        request.putQueryParameter("UserCallerAid", "1563457855438522");
        return request;
    }
}