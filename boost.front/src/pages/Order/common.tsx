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
import {centsToYuan} from "@/util/moneyUtil";
import {FormattedMessage} from "@@/exports";
import React from "react";

export const TradeStatusEnum = {
    TRADE_CLOSED: <FormattedMessage id='pages.tradeStatus.trade-closed' defaultMessage='交易关闭'/>,
    TRADE_SUCCESS: <FormattedMessage id='pages.tradeStatus.trade-success' defaultMessage='支付成功'/>,
    WAIT_BUYER_PAY: <FormattedMessage id='pages.tradeStatus.wait-buyer-pay' defaultMessage='交易创建'/>,
    TRADE_FINISHED: <FormattedMessage id='pages.tradeStatus.trade-finished' defaultMessage='交易完成'/>,
    REFUNDING: <FormattedMessage id='pages.tradeStatus.refunding' defaultMessage='正在退款'/>,
    REFUNDED: <FormattedMessage id='pages.tradeStatus.refunded' defaultMessage='退款完成'/>,
};

export const OrderColumns: ProColumns<API.OrderDTO>[] = [
    {
      title: <FormattedMessage id="pages.instanceSearchTable.orderId" defaultMessage="订单号"/>,
      dataIndex: 'orderId',
      key: 'orderId',
      sorter: false, //@ts-ignore
      search: true,
    },
    {
        title: <FormattedMessage id="title.commodity-name" defaultMessage='产品名称'/>,
        dataIndex: 'commodityName',
        key: 'commodityName',
        sorter: false,
        search: false,
    }
    ,
    {
        title: <FormattedMessage id="title.specification-name" defaultMessage='套餐名称'/>,
        dataIndex: 'specificationName',
        key: 'specificationName',
        sorter: false,
        search: false,
        render: (_, record) => {
            return record.specificationName || <FormattedMessage id="message.no-specification" defaultMessage='无套餐'/>;
        },
    },
    {
        title: <FormattedMessage id="title.trade-status" defaultMessage='交易状态'/>,
        dataIndex: 'tradeStatus',
        key: 'tradeStatus',
        valueEnum: TradeStatusEnum,
        render: (dom, entity) => {
            const text = entity.tradeStatus;
            let color = 'default';
            if (text === TradeStatusEnum.TRADE_SUCCESS) {
                color = 'success';
            } else if (text === TradeStatusEnum.TRADE_CLOSED) {
                color = 'gold';
            } else if (text === TradeStatusEnum.WAIT_BUYER_PAY) {
                color = 'warning';
            } else if (text === TradeStatusEnum.TRADE_FINISHED) {
                color = 'error';
            } else if (text === TradeStatusEnum.REFUNDING) {
                color = 'processing';
            } else if (text === TradeStatusEnum.REFUNDED) {
                color = 'default';
            } else {
                color = 'default';
            }
            return <Tag color={color}>{text}</Tag>;
        },
    },
    {
        title: <FormattedMessage id="title.payment-type" defaultMessage='支付类型'/>,
        dataIndex: 'payChannel',
        key: 'payChannel',
        valueEnum: PayChannelEnum,
        search: false,
    },
    {
        title: <FormattedMessage id="title.total-amount" defaultMessage='总金额'/>,
        dataIndex: 'totalAmount',
        key: 'totalAmount',
        sorter: false,
        search: false,
        render: (_, record) => (
            <span>{centsToYuan(record.totalAmount)}</span>
        ),
    },
    {
        title: <FormattedMessage id="title.creation-time" defaultMessage='创建时间'/>,
        tip: <FormattedMessage id="message.query-orders-for-selected-time-range" defaultMessage='查询您选择的时间到当前时间内的所有订单'/>,
        dataIndex: 'gmtCreate',
        key: 'gmtCreate',
        valueType: 'dateTime',
    },
    {
        title: <FormattedMessage id="title.payment-form" defaultMessage="支付表单"/>,
        key: 'paymentForm',
        dataIndex: 'paymentForm',
        sorter: false,
        search: false,
        // hideInForm: true,
        hideInTable: true,
    },
    {
        title: "是否能退款",
        key: 'canRefund',
        dataIndex: 'canRefund',
        sorter: false,
        search: false,
        hideInForm: true,
        hideInTable: true,
    }
];
