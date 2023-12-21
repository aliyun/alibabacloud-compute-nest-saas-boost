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
import {Tabs} from 'antd';
import ServiceInstanceContent from "@/pages/ServiceInstanceContent";
import ServiceInstanceMonitor from "@/pages/ServiceInstanceMonitor";
import {Index} from "@/pages/ServiceInstanceOrder";
import {useParams} from "react-router";
import {CallSource} from "@/constants";

interface TabItem {
    key: string;
    label: string;
    children: JSX.Element;
}

const ServiceInstanceDetail: React.FC = () => {
    const {id} = useParams<{ id: string }>();
    const {status} = useParams<{ status: string }>();
    // 假设这是父组件的状态和 setState 函数
    const [source, setSource] = useState<string | undefined>(undefined);

    // 回调函数，用于从子组件获取 source 数据

    const [items, setItems] = useState<TabItem[]>([]);

    useEffect(() => {
        let newItems = [{
            key: 'description',
            label: `概览`,
            children: <ServiceInstanceContent serviceInstanceId={id} status={status} onSourceChange={setSource}/>,
        },
            {
                key: 'monitor',
                label: `监控`,
                children: <ServiceInstanceMonitor serviceInstanceId={id}/>,
            }];
        const setOrdersTab = async () => {
            console.log(source);
            // 如果 source 等于 'Market'，则添加订单标签
            if (source != undefined && source != CallSource[CallSource.Market]) {
                newItems.push({
                    key: 'serviceInstanceOrders',
                    label: `订单`,
                    children: <Index serviceInstanceId={id} status={status}/>,
                });
            }
            setItems(newItems);
        };
        setOrdersTab();
    }, [source]);

    return <Tabs defaultActiveKey="1" items={items}/>
}

export default ServiceInstanceDetail;
