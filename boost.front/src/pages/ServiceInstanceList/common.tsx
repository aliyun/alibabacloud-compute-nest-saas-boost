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

import {ProColumns} from "@ant-design/pro-components";
import {FormattedMessage} from "@@/exports";
import React from "react";
import {Link} from "react-router-dom";

const statusEnum = {
    'Created': {
        text: (
            <FormattedMessage id="pages.instanceSearchTable.status.created" defaultMessage="待部署"/>
        ),
        status: 'Success',
    },
    'Deployed': {
        text: (
            <FormattedMessage id="pages.instanceSearchTable.status.running" defaultMessage="已部署"/>
        ),
        status: 'Success',
    },
    'Deploying': {
        text: (
            <FormattedMessage id="pages.instanceSearchTable.status.deploying" defaultMessage="部署中"/>
        ),
        status: 'Processing',
    },
    'DeployedFailed': {
        text: (
            <FormattedMessage
                id="pages.instanceSearchTable.status.deployFailed"
                defaultMessage="部署失败"
            />
        ),
        status: 'Error',
    },
    'UpgradeFailed': {
        text: (
            <FormattedMessage
                id="pages.instanceSearchTable.status.upgradeFailed"
                defaultMessage="升级失败"
            />
        ),
        status: 'Error',
    },
    'Deleting': {
        text: (
            <FormattedMessage
                id="pages.instanceSearchTable.status.deleting"
                defaultMessage="删除中"
            />
        ),
        status: 'Processing',
    },
    'DeletedFailed': {
        text: (
            <FormattedMessage
                id="pages.instanceSearchTable.status.deleteFailed"
                defaultMessage="删除失败"
            />
        ),
        status: 'Error',
    },
}


const listColumns: ProColumns<API.ServiceInstanceModel>[] = [
    {
        title: (
            <FormattedMessage
                id="pages.instanceSearchTable.serviceInstanceId"
                defaultMessage="实例Id"
            />
        ),
        dataIndex: 'serviceInstanceId',
        render: (dom, entity) => {
            return (
                <Link
                    to={{
                        pathname: `/serviceInstance/${entity.serviceInstanceId}`,
                        search: `source=${entity.source}`,
                    }}
                >
                    {dom}
                </Link>
            )
        },
    },
    {
        title: <FormattedMessage id="pages.instanceSearchTable.serviceInstanceName" defaultMessage="实例名称"/>,
        dataIndex: 'serviceInstanceName',
    },
    {
        title: <FormattedMessage id="pages.instanceSearchTable.status" defaultMessage="Status"/>,
        dataIndex: 'status',
        hideInForm: true,
        valueEnum: statusEnum,
        onFilter: true,

    },
    {
        title: <FormattedMessage id="pages.instanceSearchTable.serviceName" defaultMessage="服务名"/>,
        search: false,
        render: (text, record) => {
            return record.serviceModel ? record.serviceModel.name : '';
        }
    },
    {
        title: <FormattedMessage id="pages.instanceSearchTable.order" defaultMessage="订单号"/>,
        search: false,
        dataIndex: 'orderId',
        render: (text, record) => {
            return (
                <Link
                to={{
                    pathname: `/serviceInstance/${record.serviceInstanceId}/`,
                    search: `tab=serviceInstanceOrders&orderId=${record.orderId}`,
                }}
            >
                {text}
            </Link>);
        }
    }
    ,
    {
        title: (
            <FormattedMessage
                id="pages.instanceSearchTable.createTime"
                defaultMessage="Create Time"
            />
        ),
        sorter: false,
        search: false,
        dataIndex: 'createTime',
        valueType: 'dateTime',
    },
    {
        title: (
            <FormattedMessage
                id="pages.instanceSearchTable.updateTime"
                defaultMessage="更新时间"
            />
        ),
        sorter: false,
        search: false,
        dataIndex: 'updateTime',
        valueType: 'dateTime',
    },
];


export function getListColumns() {
    return listColumns
}

export function getStatusEnum() {
    return statusEnum
}
