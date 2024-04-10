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

import React from "react";
import {ProColumns, ProTable} from "@ant-design/pro-components";
import {ProTableProps} from "@ant-design/pro-table/lib";

export interface ServiceInstanceTableProps {
    serviceInstances: API.ServiceInstanceModel[];
    search: true;
    columns: ProColumns<API.ServiceInstanceModel>[];
    onCreate: () => void;
    onRefresh: () => void;
    onSubmit: (values: {
        status?: string;
        serviceInstanceId?: string;
        serviceInstanceName?: string;
    }) => void;
    options?: ProTableProps<API.ServiceInstanceModel, API.ServiceInstanceModel>['options'];
}

export const ServiceInstanceTableInterface: React.FC<ServiceInstanceTableProps> = ({
                                                                                       serviceInstances,
                                                                                       columns,
                                                                                       onCreate,
                                                                                       onRefresh,
                                                                                       onSubmit,
                                                                                       options = false,

                                                                                   }) => {
    return (
        <ProTable<API.ServiceInstanceModel>
            headerTitle="实例列表"
            rowKey="key"
            dataSource={serviceInstances}
            search={{
                labelWidth: 'auto',
                defaultCollapsed: false,
                layout: 'horizontal',
                span: 6,
                optionRender: (searchConfig, formProps, dom) => [
                    <div style={{ display: 'flex', gap: '8px', marginBottom: '24px', marginRight: '+220px'}}>
                        <div style={{ flexGrow: 1 }}>{dom[0]}</div> {/* 查询输入框 */}
                        <div>{dom[1]}</div> {/* 查询按钮 */}
                        <div>{dom[2]}</div> {/* 重置按钮 */}
                    </div>
                ],
            }}

            columns={columns}
            onSubmit={onSubmit}
            options={{
                search: true,
                density: false,
                fullScreen: false,
                reload: false,
                setting: false,
                ...options,
            }}

        />
    );
};

