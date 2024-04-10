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

import React, {useEffect, useRef, useState} from 'react';
import ProCard from '@ant-design/pro-card';
import {Avatar, Col, message, Row, Spin, Typography} from "antd";
import styles from "./component/css/service.module.css";
import {PageContainer} from "@ant-design/pro-layout";
import {serviceColumns, ServiceModel} from './common';
import profileImage from '../../../public/logo.png'
import {GlobalOutlined} from "@ant-design/icons";
import {FetchResult} from "@/util/nextTokenUtil";
import {listAllCommodities} from "@/services/backend/commodity";
import {ActionType} from "@ant-design/pro-table/lib";
import {getServiceMetadata} from "@/services/backend/serviceManager";
import {ProColumns, ProTable} from "@ant-design/pro-components";
import {useDispatch, useSelector} from 'react-redux';
import {RootState} from "@/store/state";
import {initialProviderInfoEncryptedList, initialProviderInfoNameList} from "@/pages/Parameter/common";
import {listConfigParameters} from "@/services/backend/parameterManager";
import {
    setProviderDescription,
    setProviderLogoUrl,
    setProviderName,
    setProviderOfficialLink
} from "@/store/providerInfo/actions";
import {FormattedMessage} from "@@/exports";
import {renderToString} from "react-dom/server";

const {Paragraph} = Typography;

const ServicePage: React.FC = () => {
    const dispatch = useDispatch();
    const [refreshing, setRefreshing] = useState(false);

    useEffect(() => {
        handleRefresh();
    }, []);
    const loadConfigParameters = async (parameterNames: string[], encrypted: boolean[]) => {
        const configParameterQueryModels: API.ConfigParameterQueryModel[] = parameterNames.map((name, index) => ({
            name,
            encrypted: encrypted[index],
        }));

        const listParams: API.ListConfigParametersParam = {
            configParameterQueryModels,
        };

        const result: API.ListResultConfigParameterModel_ = await listConfigParameters(listParams);
        if (result.data && result.data.length > 0) {
            result.data.forEach((configParam) => {
                if (configParam.name && configParam.value) {
                    let value = configParam.value === 'waitToConfig' ? '' : configParam.value;
                    // 针对 'ProviderLogoUrl' 名称进行特殊处理
                    if (configParam.name === 'ProviderLogoUrl' && configParam.value === 'waitToConfig') {
                        value = profileImage;
                    }
                    if (configParam.name === 'ProviderName') {
                        dispatch(setProviderName(value));
                    } else if (configParam.name === 'ProviderOfficialLink') {
                        dispatch(setProviderOfficialLink(value));
                    } else if (configParam.name === 'ProviderDescription') {
                        dispatch(setProviderDescription(value));
                    } else if (configParam.name === 'ProviderLogoUrl') {
                        dispatch(setProviderLogoUrl(value));
                    }
                }
            });
        }
    };

    const handleRefresh = async () => {
        setRefreshing(true);
        await loadConfigParameters(initialProviderInfoNameList, initialProviderInfoEncryptedList);
        setRefreshing(false);
    };

    const providerInfo = useSelector((state: RootState) => ({
        name: state.providerInfo.providerName,
        link: state.providerInfo.providerOfficialLink,
        description: state.providerInfo.providerDescription,
        logoUrl: state.providerInfo.providerLogoUrl,
    }));
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
        title: <FormattedMessage id="pages.instanceSearchTable.titleOption" defaultMessage='操作'/>,
        dataIndex: 'action',
        valueType: 'option',
        render: (text, record, _) => [

            record.serviceStatus === 'Online' && record.serviceId !== undefined && record.version !== undefined &&
            <a onClick={() => handleUpdateCommodityStatus(record.serviceId, record.version)}><FormattedMessage id="button.go-to-purchase" defaultMessage='前往购买'/></a>

        ],
    };

    const columns: ProColumns<ServiceModel>[] = [
        ...serviceColumns,
        actionColumn,
    ];

    return (
        <PageContainer title={<FormattedMessage id="menu.featured-services" defaultMessage="精选服务"/>}>
            <ProCard bordered={true}
                     className={styles.supplierProCard}
            >
                <Spin spinning={refreshing}>
                    <div>
                        <Row align="middle">
                            <Col>
                                <Avatar size={64} src={providerInfo.logoUrl? providerInfo.logoUrl:'1'} shape="circle" className={styles.supplierImage}/>
                            </Col>
                            <Col flex="auto" className={styles.imageTitleGap}>
                                <div className={styles.supplierTitle}>{providerInfo.name? providerInfo.name: '服务商名待填'}</div> {/* 使用 providerInfo.name */}
                                <a href={providerInfo.link? providerInfo.link: '服务商官网链接待填'} target="_blank" rel="noopener noreferrer" // 使用 providerInfo.link
                                   className={styles.supplierDetails}><GlobalOutlined
                                    className={styles.globalOutlined}/>{providerInfo.link? providerInfo.link: '服务商官网链接待填'}</a>
                            </Col>
                        </Row>
                        <Paragraph/>
                        <Paragraph className={styles.supplierDescription}>{providerInfo.description? providerInfo.description: '服务商描述待填'}</Paragraph> {/* 使用 providerInfo.description */}
                        <Paragraph/>
                    </div>
                </Spin>
            </ProCard>
            <ProTable columns={columns} rowKey="serviceId"
                      headerTitle={<FormattedMessage id="menu.featured-commodities" defaultMessage="精选商品"/>}
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