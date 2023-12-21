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
import {Button} from "antd";
import {ProTableProps} from "@ant-design/pro-table/lib";

export interface ServiceInstanceTableProps {
    serviceInstances: API.ServiceInstanceModel[];
    search: true;
    columns: ProColumns<API.ServiceInstanceModel>[];
    onUpdateUserPassword: (record: API.ServiceInstanceModel) => void;
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
                                                                                       onUpdateUserPassword,
                                                                                       onCreate,
                                                                                       onRefresh,
                                                                                       onSubmit,
                                                                                       options = false,

                                                                                   }) => {
    return (
        <ProTable<API.ServiceInstanceModel>
            headerTitle=""
            rowKey="key"
            dataSource={serviceInstances}
            search={{
                labelWidth: 'auto',
                defaultCollapsed: false,
                layout: 'vertical',
                // filterType: 'light',
                optionRender: ({searchText, resetText}, {form}) => [
                    <Button type="primary" key="search"
                        onClick={() => {
                            form?.submit();
                        }}
                    >
                        {searchText}
                    </Button>,
                    <Button key="reset"
                        onClick={() => {
                            form?.resetFields();
                            onSubmit({});
                        }}
                    >
                        {resetText}
                    </Button>,
                ],
            }}
            columns={columns}
            toolBarRender={() => [
                <Button type="primary" onClick={onCreate} key="create">
                    创建
                </Button>,
                options && (
                    <Button key="reload" onClick={onRefresh}>
                        刷新
                    </Button>
                ),
            ]}
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

export interface CreateModalProps {

    createModalVisible: boolean;

    setCreateModalVisible: (visible: boolean) => void;

    handleCreateSubmit: () => void;
}

export interface Specification {
    Name: string;
    Parameters: { [key: string]: string[] };
    OrderList: string[];
    Type: string;
    Description: string;
}


export interface ParameterTypeInterfaceArray extends Record<string, ParameterTypeInterface> {}

export interface ParameterTypeInterface {
    Type?: string;
    NoEcho?: boolean;
    Label?: {
        en?: string;
        'zh-cn'?: string;
    };
    AllowedPattern?: string;
    MaxValue?: number;
    MinValue?: number;
    Default?: any;
    Description?: {
        'zh-cn': string;
        en: string;
    };
    AssociationProperty?: string;
    AssociationPropertyMetadata?: {
        Visible?: {
            Condition?: {
                'Fn::Equals': [any, any];
            };
        };
    };
    AllowedValues?: string[];
}

export interface ParameterGroupsInterface {
    "ALIYUN::ROS::Interface"
        : {
        TemplateTags: string[];
        ParameterGroups: {
            Parameters: string[];
            Label: {
                default: {
                    en: string;
                    'zh-cn': string;
                };
            };
        }[];
    };
}