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
import {PageContainer} from '@ant-design/pro-layout';
import {Button, Pagination, Typography} from 'antd';
import dayjs from 'dayjs';
import utc from 'dayjs/plugin/utc';
import {PayTypeEnum} from '@/pages/ServiceInstanceList/components/form/PayTypeFormItem';
import moment from "moment";
import {ProTable} from "@ant-design/pro-components";
import {OrderColumns, TradeStatusEnum} from "@/pages/Order/common";
import {handleGoToPage} from "@/nextTokenUtil";
import {listOrders} from "@/services/backend/order";


export enum ProductNameEnum {
    SERVICE_INSTANCE = 'ServiceInstance',
}

dayjs.extend(utc);

const OrderQueryPage: React.FC = () => {
    const [orders, setOrders] = useState<API.OrderDTO[]>([]);
    const [currentPage, setCurrentPage] = useState<number>(1);
    const [total, setTotal] = useState<number>(0);
    const pageSize = 10;
    const [nextTokens, setNextTokens] = useState<(string | undefined)[]>([undefined]);
    const [shouldFetchData, setShouldFetchData] = useState(false);
    const [filterValues, setFilterValues] = useState<{
        tradeStatus?: | 'TRADE_CLOSED'
            | 'TRADE_SUCCESS'
            | 'WAIT_BUYER_PAY'
            | 'TRADE_FINISHED'
            | 'REFUNDED'
            | 'REFUNDING';
        gmtCreate?: string;
        type?: string;
    }>({});

    const fetchData = async (currentPage: number, show: boolean) => {
        const params: API.ListOrdersParam = {
            maxResults: pageSize,
            nextToken: nextTokens[currentPage - 1],
        };
        if (filterValues.tradeStatus != undefined) {
            params.tradeStatus = filterValues.tradeStatus;
        }
        if (filterValues.gmtCreate != null) {
            let startTime = moment(filterValues.gmtCreate).utc().format('YYYY-MM-DDTHH:mm:ss[Z]');
            params.startTime = startTime;
        } else {
            const currentTime = dayjs();
            const utcTime = currentTime.utc().format('YYYY-MM-DDTHH:mm:ss[Z]');
            params.startTime = currentTime.utc().subtract(1, 'year').format('YYYY-MM-DDTHH:mm:ss[Z]');
            params.endTime = utcTime;
        }
        const result: API.ListResultOrderDTO_ = await listOrders(params);
        if (result.data !== undefined) {
            setTotal(result.count || 0);
            nextTokens[currentPage] = result.nextToken;
            const transformedData = result.data?.map((item: API.OrderDTO) => {
                const localTime = item.gmtCreate ? moment.utc(item.gmtCreate).local().format('YYYY-MM-DD HH:mm:ss') : '';
                return {
                    ...item,
                    gmtCreate: localTime,
                    tradeStatus: TradeStatusEnum[item.tradeStatus as keyof typeof TradeStatusEnum],
                    productName: ProductNameEnum[item.productName as keyof typeof ProductNameEnum],
                    type: PayTypeEnum[item.type as keyof typeof PayTypeEnum],
                };
            }) || [];
            if (show) {
                //@ts-ignored
                setOrders(transformedData);
                setShouldFetchData(false);
            }

        }
    };


    useEffect(() => {
        fetchData(currentPage, true);
    }, [currentPage, shouldFetchData]);

    const columns = OrderColumns.concat([
        {
            title: '',
            key: 'action',
            sorter: false,
            search: false,
            // @ts-ignore
        },
    ]);

    return (
        <PageContainer>
            <ProTable
                onSubmit={(values) => {
                    setFilterValues(values);
                    setShouldFetchData(true);
                }}
                options={{
                    search: false,
                    density: false,
                    fullScreen: true,
                    reload: ()=>{
                        setShouldFetchData(true);
                    },
                    setting: false,
                }}

                search={{
                    labelWidth: 'auto',
                    defaultCollapsed: false,
                    layout: 'vertical',
                    optionRender: ({searchText, resetText}, {form}) => [
                        <Button
                            type="primary"
                            key="search"
                            onClick={() => {
                                form?.submit();
                            }}
                        >
                            {searchText}
                        </Button>,
                        <Button
                            key="reset"
                            onClick={() => {
                                form?.resetFields();
                            }}
                        >
                            {resetText}
                        </Button>,
                    ],
                }}
                columns={columns} dataSource={orders} rowKey="key" pagination={false}/>
            <Pagination
                style={{marginTop: '16px', textAlign: 'right'}}
                current={currentPage}
                pageSize={pageSize}
                total={total}
                onChange={(page, pageSize) => handleGoToPage(page, currentPage, total, fetchData, setCurrentPage)}
                prevIcon={<span
                    onClick={() => handleGoToPage(currentPage + 1, currentPage, total, fetchData, setCurrentPage)}>上一页</span>}
                nextIcon={<span
                    onClick={() => handleGoToPage(currentPage - 1, currentPage, total, fetchData, setCurrentPage)}>下一页</span>}
                showSizeChanger={false}
            />
        </PageContainer>
    );
};

export default OrderQueryPage;
