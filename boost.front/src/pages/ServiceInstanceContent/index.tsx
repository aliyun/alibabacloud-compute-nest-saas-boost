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
import {Badge, Button, Descriptions, Divider, Space} from 'antd';
import {getStatusEnum} from "@/pages/ServiceInstance/common";
import {getServiceInstance} from "@/services/backend/serviceInstance";
import moment from "moment";
import dayjs from "dayjs";
import utc from "dayjs/plugin/utc";
import {listOrders} from "@/services/backend/order";

dayjs.extend(utc);

interface ServiceInstanceContentProps {
    serviceInstanceId?: string;
}

const processServiceInstanceData = (data: API.ServiceInstanceModel) => {
    let outputs = {};
    let parameters = {};

    if (data !== null) {
        if (data?.outputs !== null) {
            outputs = JSON.parse(data?.outputs as string);
        }
        if (data?.parameters !== null) {
            parameters = JSON.parse(data?.parameters as string);
        }
        data.createTime = data.createTime ? moment.utc(data.createTime).local().format('YYYY-MM-DD HH:mm:ss') : '';
        data.updateTime = data.updateTime ? moment.utc(data.updateTime).local().format('YYYY-MM-DD HH:mm:ss') : '';
    }
    return {outputs, parameters};
};

function isIPv4Address(str: string): boolean {
    const pattern = /^(\d{1,3}\.){3}\d{1,3}$/;
    return pattern.test(str);
}

const ServiceInstanceContent: React.FC<ServiceInstanceContentProps> = (props) => {
    const {serviceInstanceId} = props;
    const [data, setData] = useState<API.ServiceInstanceModel>();

    useEffect(() => {
        const params: API.getServiceInstanceParams = {
            serviceInstanceId: serviceInstanceId,
        };

        (async () => {
            const result = await getServiceInstance(params);
            setData(result.data);
            console.log(result);
        })();

        await listOrders({
            serviceInstanceId: serviceInstanceId,
        });
    }, [serviceInstanceId]);

    if (data !== undefined) {
        const {outputs, parameters} = processServiceInstanceData(data);

        return (
            <Space direction="vertical" size="large" style={{display: 'flex'}}>
                <Descriptions bordered={true} title="服务实例" column={2}>
                    <Descriptions.Item label="服务实例Id">{data?.serviceInstanceId} </Descriptions.Item>
                    <Descriptions.Item label="服务实例名">{data?.serviceInstanceName}</Descriptions.Item>
                    <Descriptions.Item label="状态">
                        {
                            <Badge
                                //@ts-ignore
                                status={data ? getStatusEnum()[data?.status].status.toLocaleLowerCase() : 'processing'}
                                //@ts-ignore
                                text={data ? getStatusEnum()[data?.status].text : 'Deployed'}/>
                        }
                    </Descriptions.Item>
                    <Descriptions.Item label="创建时间">
                        {data.createTime}
                    </Descriptions.Item>
                    <Descriptions.Item label="更新时间">{data.updateTime}</Descriptions.Item>
                    <Descriptions.Item label="服务实例到期时间">
                        <div style={{display: 'flex', justifyContent: 'space-between', alignItems: 'center'}}>
                            <div>{data?.serviceInstanceId}</div>
                            <Button>续费</Button>
                        </div>
                    </Descriptions.Item>
                    {
                        Object.keys(outputs).map((key) => {
                            // @ts-ignore
                            let httpUrl = JSON.stringify(outputs[key]).replace(/\"/g, "");
                            if (httpUrl.indexOf('http') >= 0 || isIPv4Address(httpUrl)) {
                                // @ts-ignore
                                return (<Descriptions.Item label={key}>
                                    <a href={httpUrl}>
                                        {httpUrl}
                                    </a>
                                </Descriptions.Item>)
                            } else {
                                return (<Descriptions.Item
                                    // @ts-ignore
                                    label={key}>{JSON.stringify(outputs[key]).replace(/\"/g, "")}</Descriptions.Item>)
                            }
                        })
                    }
                </Descriptions>
                <Divider/>
                <Descriptions bordered={true} title="服务信息" column={2}>
                    <Descriptions.Item label="服务id">{data?.serviceModel?.serviceId}</Descriptions.Item>
                    <Descriptions.Item label="服务名">{data?.serviceModel?.name} </Descriptions.Item>
                    <Descriptions.Item label="描述">{data?.serviceModel?.description} </Descriptions.Item>
                </Descriptions>
                <Divider/>
                <Descriptions bordered={true} title="配置信息" column={1}>
                    {
                        Object.keys(parameters).map((key) => {
                            return (<Descriptions.Item
                                // @ts-ignore
                                label={key}>{JSON.stringify(parameters[key]).replace(/\"/g, "")}</Descriptions.Item>);
                        })
                    }
                </Descriptions>
                <Divider/>
            </Space>
        )
    } else {
        return (<Space></Space>)
    }
}

export default ServiceInstanceContent;
