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

import React from 'react';
import type {TabsProps} from 'antd';
import {Tabs} from 'antd';
import ServiceInstanceContent from "@/pages/ServiceInstanceContent";
import ServiceInstanceMonitor from "@/pages/ServiceInstanceMonitor";
import {ServiceInstanceOrder} from "@/pages/ServiceInstanceOrder/ServiceInstanceOrder";
import {useParams} from "react-router";


const ServiceInstanceDetail: React.FC = () => {
    const {id} = useParams<{ id: string }>();
    const {status} = useParams<{ status: string }>();
    const items: TabsProps['items'] = [
        {
            key: 'description',
            label: `概览`,
            children:
                <ServiceInstanceContent
                    serviceInstanceId={id} status={status}/>,
        },
        {
            key: 'monitor',
            label: `监控`,
            children: <ServiceInstanceMonitor  serviceInstanceId={id}/>,
        },
        {
            key:'serviceInstanceOrders',
            label: `订单`,
            children: <ServiceInstanceOrder serviceInstanceId={id} status={status}/>,
        }
    ];
    return <Tabs defaultActiveKey="1" items={items}/>
}

export default ServiceInstanceDetail;
