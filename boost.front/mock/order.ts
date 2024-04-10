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

import {Request, Response} from "express";

const createOrder = (req: Request, res: Response) => {
    const jsonString = `<form name="punchout_form" method="post" action="https://openapi-sandbox.dl.alipaydev.com/gateway.do?charset=utf-8&method=alipay.trade.page.pay&sign=FC82SgImO2wQq%2BHkfYthgPhZpHrOntWablevEyfxi8N0OjGgwH4Jxnz72Hzyg2xEtaZTJpPz1G8cyzUScX4mtZ3v4aHl0CRZIu5A1uRL7Z9mPn03Am0QDsYF0uveU3RbYYweH%2Bn4HUdsnAXQuxHtzaOZ90AuybTztyjvGGCldCK%2BLnTGhM2F%2BGONoMwxtTc069rWF4bhgECfzf1FpXNorvQ1%2FO2Gfk0A05EssjnzNonkbYtXRjZ%2F4hGGDY1EbWU0pRUtBV1Ng%2BGBm5f0xG9z2VYEHw4dRTkx4IaADd0%2F7iHfg%2BLhTp1VWgND%2B1zBSGIEppxnBbjVi4sQ6%2FjiUIthbg%3D%3D&return_url=http%3A%2F%2Flocalhost%3A8000%2FPayCallBack&notify_url=http%3A%2F%2F39.104.200.110%3A9999%2Falipay%2FverifyTradeCallback&version=1.0&app_id=9021000123614461&sign_type=RSA2&timestamp=2023-08-14+16%3A43%3A31&alipay_sdk=alipay-sdk-java-4.38.28.ALL&format=json">
                        <input type="hidden" name="biz_content" value="{&quot;out_trade_no&quot;:&quot;7202308141643286693313c77eda8ba453971476896702&quot;,&quot;total_amount&quot;:100.0,&quot;subject&quot;:&quot;serviceInstance&quot;,&quot;product_code&quot;:&quot;FAST_INSTANT_TRADE_PAY&quot;}">
                        <input type="submit" value="立即支付" style="display:none" >
                    </form>`;

    res.json({
        data: jsonString,
    });
}

const getOrder = (req: Request, res: Response) => {
    res.json({
        'data': {
            'tradeStatus': 'TRADE_SUCCESS',
            'gmtCreate': '2023-08-08 10:40:23',
            'productName': 'ros',
            'totalAmount': 100.0,
            'productComponents': 'json对象',
            'type': 'Alipay'
        }
    });

}

const listOrder = (req: Request, res: Response) => {
    res.json({
        data: [
            {
                "tradeStatus": "TRADE_SUCCESS",
                "gmtCreate": "2023-08-08T02:40:23Z",
                "productName": "ServiceInstance",
                "totalAmount": 100.0,
                "productComponents": "json对象",
                "type": "Alipay"
            },
            {
                "tradeStatus": "WAIT_BUYER_PAY",
                "gmtCreate": "2023-08-08T02:40:23Z",
                "productName": "ServiceInstance",
                "totalAmount": 100.0,
                "productComponents": "json对象",
                "type": "Alipay"
            }
        ]
    });
}

const refundOrder = (req: Request, res: Response) => {
    res.json({
        data:11.11
    });
}
export default {
    'GET /api/getOrder': getOrder,
    'POST /api/listOrders': listOrder,
    'POST /api/createOrder': createOrder,
    'POST /api/refundOrder': refundOrder,

};
