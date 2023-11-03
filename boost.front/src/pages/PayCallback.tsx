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

import React, {useEffect, useState} from 'react';
import {Modal, Spin} from 'antd';
import {getOrder} from "@/services/backend/order";

const PayCallback: React.FC = () => {
    const [loading, setLoading] = useState(true);

    useEffect(() => {
        handlePaymentCallback();
    }, []);

    const handlePaymentCallback = async () => {
        const hash = window.location.hash.substring(1);
        const urlParams = new URLSearchParams(hash);
        console.log(urlParams);
        const params = Object.fromEntries(urlParams.entries());
        console.log(params);
        const getOrderParam = {
            orderId:params.out_trade_no,
        } as API.getOrderParams;
        const result = await getOrder(getOrderParam) as API.BaseResultOrderDTO_;
        let success:boolean = verifyTradeStatus(result);
        if (success){
            handlePaymentSuccess();
        } else{
            handlePaymentFailure(getOrderParam);
        }
    };

    const handlePaymentFailure = (params: API.getOrderParams) => {
        let count = 1;
        const maxAttempts = 3;
        const intervalDelay = 5000;
        const interval = setInterval(async () => {
            if (count >= maxAttempts) {
                clearInterval(interval);
                paymentFailureModel();
                return;
            } else {
                const result = await getOrder(params);
                let success:boolean = verifyTradeStatus(result);
                if (success) {
                    clearInterval(interval);
                    handlePaymentSuccess();
                    return;
                } else {
                    count++;
                    setLoading(true);
                }
            }
        }, intervalDelay);
    };

    const handlePaymentSuccess = () => {
        Modal.success({
            title: '支付成功',
            content: '服务实例正在部署中',
            onOk: () => {
                window.location.hash = '#/serviceInstance';
            },
        });
    };

    const paymentFailureModel = () => {
        setLoading(false);

        Modal.error({
            title: '支付失败',
            content: '很抱歉，支付未成功，请稍后再试。',
            onOk: () => {
                window.location.href = '/welcome';
            },
        });
    };

    const verifyTradeStatus = (result : API.BaseResultOrderDTO_ ):boolean=>{
        if (result.data !== undefined){
            const orderDto = result.data as API.OrderDTO;
            if (orderDto != undefined && (orderDto.tradeStatus === 'TRADE_SUCCESS' || orderDto.tradeStatus === 'TRADE_FINISHED' )){
                return true;
            }
        }
        return false;
    }

    return (
        <div style={{display: 'flex', justifyContent: 'center', alignItems: 'center', height: '100vh'}}>
            <Spin spinning={loading} tip="正在加载中...">
                <h1>处理中，请稍等...</h1>
            </Spin>
        </div>
    );
};

export default PayCallback;
