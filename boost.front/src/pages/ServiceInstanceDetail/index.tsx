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
import {useNavigate, useParams} from 'react-router-dom';
import {CallSource} from "@/constants";
import {getHashSearchParams} from "@/util/urlUtil";
import {FormattedMessage} from "@@/exports";

interface TabItem {
    key: string;
    label: string;
    children: JSX.Element;
}

const ServiceInstanceDetail: React.FC = () => {
    const navigate = useNavigate();
    let {id,} = useParams<{ id: string; status: string }>();
    const searchParams = getHashSearchParams();
    console.log(searchParams);
    const initialTabKey = searchParams.get('tab') || 'description'; // 如果 URL 没有 'tab' 参数，则默认为 'description'
    const [source, setSource] = useState<string | null>();
    const [status, setStatus] = useState<string | undefined>();
    const [serviceType, setServiceType] = useState<string | undefined>();
    console.log(initialTabKey);
    const [activeTabKey, setActiveTabKey] = useState<string>(initialTabKey);
    const [items, setItems] = useState<TabItem[]>([]);
    useEffect(() => {
        let source: string | null = searchParams.get('source') != null ? searchParams.get('source') : "Supplier";
        let status = searchParams.get('status') != null ? searchParams.get('status') : "";
        if (status !== null) {
            setStatus(status);
        }
        let serviceType = searchParams.get('serviceType') != null ? searchParams.get('serviceType') : "";
        if (serviceType !== null) {
            setServiceType(serviceType);
        }
        setSource(source);
    }, [])
    useEffect(() => {
        // 当 source 状态更新时，根据条件动态添加订单 Tab
        if (source !== undefined) {
            const newItems = [
                {
                    key: 'description',
                    label: <FormattedMessage id='pages.overview' defaultMessage='概览'/>,
                    children: <ServiceInstanceContent serviceInstanceId={id} status={status} serviceType={serviceType}/>,
                },
                {
                    key: 'monitor',
                    label: <FormattedMessage id='pages.monitoring' defaultMessage='监控'/>,
                    children: <ServiceInstanceMonitor serviceInstanceId={id}/>,
                },
                // 根据 source 条件动态添加订单 Tab
                ...(source !== CallSource[CallSource.Market]) ? [{
                    key: 'serviceInstanceOrders',
                    label: <FormattedMessage id='pages.orders' defaultMessage='订单'/>,
                    children: <Index serviceInstanceId={id} status={status} serviceType={serviceType}/>,
                }] : [],
            ];
            setItems(newItems);
        }
    }, [source, id, status]);

    useEffect(() => {
        const tab = searchParams.get('tab');
        if (tab && items.some(item => item.key === tab)) {
            setActiveTabKey(tab);
        }
    }, [items, searchParams]);

    const onTabChange = (key: string) => {
        setActiveTabKey(key); // 更新活动 Tab Key 状态
        navigate(`/serviceInstance/${id}?tab=${key}`);
    };

    return <Tabs activeKey={activeTabKey} items={items} onChange={onTabChange}/>;
};

export default ServiceInstanceDetail;
