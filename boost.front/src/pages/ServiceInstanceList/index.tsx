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

import {PageContainer, ProColumns, ProTable,} from '@ant-design/pro-components';
import React, {useEffect, useRef, useState} from 'react';
import {getListColumns} from "@/pages/ServiceInstanceList/common";
import {message, Pagination} from "antd";
import {FetchResult, handleGoToPage} from "@/util/nextTokenUtil";
import {listAllCommodities} from "@/services/backend/commodity";
import {ActionType} from "@ant-design/pro-table/lib";
import {listServiceInstances} from "@/services/backend/serviceInstance";

const ServiceInstanceList: React.FC = () => {
    const [currentPage, setCurrentPage] = useState<number>(1);
    const [total, setTotal] = useState<number>(0);
    const pageSize = 10;
    const [filterValues, setFilterValues] = useState<{
        status?: string;
        serviceInstanceId?: string;
        serviceInstanceName?: string;
        serviceIdList?: string[];
    }>({});
    const [nextTokens, setNextTokens] = useState<(string | undefined)[]>([undefined]);
    const [commodities, setCommodities] = useState<API.CommodityDTO[]>([]);
    const [activeServiceId, setActiveServiceId] = useState<string | undefined>(undefined);
    const actionRef = useRef<ActionType>();
    const [isCommoditiesFetched, setIsCommoditiesFetched] = useState(false);
    const [initialCommodityName, setInitialCommodityName] = useState("待选择");

    const fetchCommodities = async () => {
        try {
            const result = await listAllCommodities({maxResults: pageSize});
            const fetchedCommodities = result.data || [];
            setCommodities(result.data || []);
            if (fetchedCommodities.length > 0) {
                const defaultCommodityId = String(fetchedCommodities[0].serviceId);
                setActiveServiceId(defaultCommodityId);
                setFilterValues(oldFilterValues => ({
                    ...oldFilterValues,
                    serviceIdList: [defaultCommodityId],
                }));
                setInitialCommodityName(fetchedCommodities[0].commodityName || fetchedCommodities[0].commodityCode || "未知商品");
                console.log(filterValues);
            }
        } catch (error) {
            message.error('Failed to fetch commodities');
        }
        setIsCommoditiesFetched(true);
    };

    useEffect(() => {
        fetchCommodities();
    }, []);

    useEffect(() => {
        if (activeServiceId != undefined) {
            setFilterValues({...filterValues, serviceIdList: [activeServiceId]});
            actionRef.current?.reload();
        }
    }, [activeServiceId]);

    const fetchServiceInstances = async (params: {
        pageSize: number;
        current: number;
        [key: string]: any;
    }): Promise<FetchResult<API.ServiceInstanceModel> | undefined> => {
        try {
            if (!isCommoditiesFetched) {
                // 如果商品名单还未加载完成，返回空值或延迟请求
                return {
                    data: [],
                    success: true,
                    total: 0,
                };
            }
            if (filterValues?.serviceIdList == undefined || filterValues.serviceIdList.length === 0) {
                message.error("至少选择一个商品");
                return;
            }
            console.log(filterValues);
            const response: API.ListResultServiceInstanceModel_ = await listServiceInstances({
                maxResults: pageSize,
                nextToken: nextTokens[currentPage - 1],
                serviceInstanceId: filterValues.serviceInstanceId,
                ...filterValues,
            });

            if (response.data !== undefined) {
                nextTokens[currentPage] = response.nextToken;
                setTotal(response.count || 0);
                return {
                    data: response.data as API.ServiceInstanceModel[],
                    success: true,
                    total: response.count || 0,
                };
            }
        } catch (error) {
            message.error('Failed to fetch service instances');
            return {
                data: [],
                success: false,
                total: 0,
            };
        }
    };

    const columns: ProColumns<API.ServiceInstanceModel>[] = [
        {
            title: '商品',
            dataIndex: 'service',
            valueType: 'select',
            fieldProps: {
                mode: 'multiple',
                defaultValue: commodities.length > 0 ? [String(commodities[0].serviceId)] : [],
            },
            initialValue: initialCommodityName,
            valueEnum: commodities.reduce((obj, commodity) => {
                const key = String(commodity.serviceId);
                const text = commodity.commodityName || commodity.commodityCode || "未知商品";
                obj[key] = {text};
                return obj;
            }, {} as Record<string, { text: string }>),
            hideInTable: true,
            search: {
                transform: (value) => {
                    return ({...filterValues, serviceIdList: Array.isArray(value) ? value : value ? [value] : []});
                },

            },
        },
        ...getListColumns(),
    ];

    return (
        <PageContainer>
            <ProTable<API.ServiceInstanceModel>
                headerTitle="实例列表"
                rowKey="serviceInstanceId"
                pagination={false}
                actionRef={actionRef}
                search={{
                    labelWidth: 'auto',
                    defaultCollapsed: false,
                    layout: 'horizontal',
                    span: 6,
                    optionRender: (searchConfig, formProps, dom) => [
                        <div style={{display: 'flex', gap: '8px', marginBottom: '24px', marginRight: '+220px'}}>
                            <div style={{flexGrow: 1}}>{dom[0]}</div>
                            {/* 查询输入框 */}
                            <div>{dom[1]}</div>
                            {/* 查询按钮 */}
                            <div>{dom[2]}</div>
                            {/* 重置按钮 */}
                        </div>
                    ],
                }}

                columns={columns}
                onSubmit={(values) => {
                    console.log(values);
                    setFilterValues(values);
                    actionRef.current?.reload();
                }} options={{
                search: false,
                density: false,
                fullScreen: false,
                reload: () => {
                    actionRef.current?.reload();
                }, setting: false,
            }}
                onRefresh={() => actionRef.current?.reload()}
                //@ts-ignore
                request={fetchServiceInstances}
            />
            <Pagination
                style={{marginTop: '16px', textAlign: 'right'}}
                current={currentPage}
                pageSize={pageSize}
                total={total}
                onChange={(page, pageSize) => handleGoToPage(page, currentPage, total, fetchServiceInstances, setCurrentPage, actionRef, pageSize)}
                showSizeChanger={false}
            />
        </PageContainer>
    );
};
export default ServiceInstanceList;
