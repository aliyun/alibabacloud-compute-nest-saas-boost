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

import {Tag} from 'antd';
import {ProColumns} from "@ant-design/pro-components";
import {PayChannelEnum} from "@/constants";

export const TradeStatusEnum = {
    TRADE_CLOSED: '交易关闭',
    TRADE_SUCCESS: '支付成功',
    WAIT_BUYER_PAY: '交易创建',
    TRADE_FINISHED: '交易完成',
    REFUNDING: '正在退款',
    REFUNDED: '退款完成',
};

export const OrderColumns: ProColumns<API.OrderDTO>[] = [
    {
      title: '订单号',
      dataIndex: 'orderId',
      key: 'orderId',
      sorter: false, //@ts-ignore
      search: true,
    },
    {
        title: '产品名称',
        dataIndex: 'productName',
        key: 'productName',
        sorter: false,
        search: false,
    }
    ,
    {
        title: '套餐名称',
        dataIndex: 'specificationName',
        key: 'specificationName',
        sorter: false,
        search: false,
    },
    {
        title: '交易状态',
        dataIndex: 'tradeStatus',
        key: 'tradeStatus',
        valueEnum: TradeStatusEnum,
        render: (dom, entity) => {
            const text = entity.tradeStatus;
            let color = 'default';
            switch (text) {
                case TradeStatusEnum.TRADE_SUCCESS:
                    color = 'success';
                    break;
                case TradeStatusEnum.TRADE_CLOSED:
                    color = 'gold';
                    break;
                case TradeStatusEnum.WAIT_BUYER_PAY:
                    color = 'warning';
                    break;
                case TradeStatusEnum.TRADE_FINISHED:
                    color = 'error';
                    break;
                case TradeStatusEnum.REFUNDING:
                    color = 'processing';
                    break;
                case TradeStatusEnum.REFUNDED:
                    color = 'default';
                    break;
                default:
                    color = 'default';
                    break;
            }
            return <Tag color={color}>{text}</Tag>;
        },
    },
    {
        title: '支付类型',
        dataIndex: 'payChannel',
        key: 'payChannel',
        valueEnum: PayChannelEnum,
        search: false,
    },
    {
        title: '总金额',
        dataIndex: 'totalAmount',
        key: 'totalAmount',
        sorter: false,
        search: false,
    },
    {
        title: '创建时间',
        tip: '查询您选择的时间到当前时间内的所有订单',
        dataIndex: 'gmtCreate',
        key: 'gmtCreate',
        valueType: 'dateTime',
    },
    {
        title: "支付表单",
        key: 'paymentForm',
        dataIndex: 'paymentForm',
        sorter: false,
        search: false,
        // hideInForm: true,
        hideInTable: true,
    }
];
