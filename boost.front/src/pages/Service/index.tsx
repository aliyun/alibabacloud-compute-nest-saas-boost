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

import React, {useRef} from 'react';
import ProCard from '@ant-design/pro-card';
import {Avatar, Col, message, Row, Typography} from "antd";
import styles from "./component/css/service.module.css";
import {PageContainer} from "@ant-design/pro-layout";
import {companyDescription, companyTitle, companyWebsiteUrl, serviceColumns, ServiceModel} from './common';
import profileImage from '../../../public/logo.png'
import {GlobalOutlined} from "@ant-design/icons";
import {FetchResult} from "@/util/nextTokenUtil";
import {listAllCommodities} from "@/services/backend/commodity";
import {ActionType} from "@ant-design/pro-table/lib";
import {getServiceMetadata} from "@/services/backend/serviceManager";
import {ProColumns, ProTable} from "@ant-design/pro-components";

const {Paragraph} = Typography;

const ServicePage: React.FC = () => {
    const actionRef = useRef<ActionType>();
    const fetchServices = async (params: {
        pageSize: number;
        current: number;
        [key: string]: any;
    }): Promise<FetchResult<ServiceModel>> => {
        const param: API.listAllCommoditiesParams = {
            commodityStatus: 'ONLINE',
            maxResults: 20,
        };

        try {
            const result: API.ListResultCommodityDTO_ = await listAllCommodities(param);
            if (result.data !== undefined) {
                let tempServiceModelList: ServiceModel[] = [];
                for (const commodity of result.data) {
                    try {
                        const serviceMetadataResponse: API.BaseResultServiceMetadataModel_ = await getServiceMetadata({serviceId: commodity.serviceId});
                        let data = serviceMetadataResponse.data;
                        console.log(data);
                        if (data?.status == 'Online') {
                            tempServiceModelList.push({
                                commodityName: commodity.commodityName,
                                serviceId: commodity.serviceId,
                                description: commodity.description,
                                version: data.version,
                                serviceStatus: data.status
                            });
                            console.log(tempServiceModelList);
                        }
                    } catch (error) {

                    }

                }

                return {
                    data: tempServiceModelList,
                    success: true,
                    total: tempServiceModelList.length || 0,
                };
            }
        } catch (error) {
            message.error('Failed to fetch commodities');
        }
        return {
            data: [],
            success: false,
            total: 0,
        };
    };

    const handleUpdateCommodityStatus = (serviceId: string | undefined, version: string | undefined) => {
        window.location.href = `https://computenest.console.aliyun.com/service/instance/create/cn-hangzhou?type=user&ServiceId=${serviceId}&ServiceVersion=${version}`;
    };

    const actionColumn: ProColumns<ServiceModel> = {
        title: '操作',
        dataIndex: 'action',
        valueType: 'option',
        render: (text, record, _, action) => [

            record.serviceStatus === 'Online' && record.serviceId !== undefined && record.version !== undefined &&
            <a onClick={() => handleUpdateCommodityStatus(record.serviceId, record.version)}>前往购买</a>

        ],
    };

    const columns: ProColumns<ServiceModel>[] = [
        ...serviceColumns,
        actionColumn,
    ];

    return (
        <PageContainer title={"精选服务"}>
            <ProCard bordered={true} className={styles.supplierProCard}>
                <div>
                    <Row align="middle">
                        <Col>
                            <Avatar size={64} src={profileImage} shape="circle" className={styles.supplierImage}/>
                        </Col>
                        <Col flex="auto" className={styles.imageTitleGap}>
                            <div className={styles.supplierTitle}>{companyTitle}</div>

                            <a href={companyWebsiteUrl} target="_blank" rel="noopener noreferrer"
                               className={styles.supplierDetails}><GlobalOutlined
                                className={styles.globalOutlined}/>{companyWebsiteUrl}</a>

                        </Col>
                    </Row>
                    <Paragraph/>
                    <Paragraph className={styles.supplierDescription}>{companyDescription}</Paragraph>
                    <Paragraph/>
                </div>
            </ProCard>
            <ProTable columns={columns} rowKey="serviceId"
                      headerTitle={"精选商品"}
                      actionRef={actionRef}
                      pagination={false}
                      request={fetchServices}
                      options={{
                          search: false,
                          density: false,
                          fullScreen: false,
                          reload: true,
                          setting: false
                      }}
                      search={false}
            />
        </PageContainer>
    );
};

export default ServicePage;
