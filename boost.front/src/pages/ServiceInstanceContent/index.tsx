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
import {Badge, Button, Descriptions, Divider, message, Modal, Space, Spin, Typography} from 'antd';
import {getStatusEnum} from "@/pages/ServiceInstanceList/common";
import dayjs from "dayjs";
import utc from "dayjs/plugin/utc";
import {listOrders, refundOrder} from "@/services/backend/order";
import {ProForm, ProFormSelect} from "@ant-design/pro-form";
import {PageContainer} from "@ant-design/pro-layout";
import PayTypeFormItem from "@/pages/Service/component/PayTypeFormItem";
import {isIPv4Address, replaceUrlPlaceholders} from "@/util/urlUtil";
import {ServiceInstanceContentProps} from "@/pages/ServiceInstanceContent/components/interface";
import {CallSource, COMPUTE_NEST_URL} from "@/constants";
import styles from "@/pages/Service/component/css/service.module.css";
import {centsToYuan} from "@/util/moneyUtil";
import {getServiceInstance, renewServiceInstance} from "@/services/backend/serviceInstance";
import {getCommoditySpecification} from "@/services/backend/specification";
import {
    convertToLocaleTime,
    navigateToCloudMarketplaceOrderDetails,
    processServiceInstanceData,
    unitMapping,
    UnitMappingType
} from "@/pages/ServiceInstanceContent/components/constants"
import {getCommodity, getCommodityPrice} from "@/services/backend/commodity";
import {FormattedMessage} from "@@/exports";
import {RenewalModal} from "@/pages/PaymentMethod";

dayjs.extend(utc);
const ServiceInstanceContent: React.FC<ServiceInstanceContentProps> = (props) => {
    const {serviceInstanceId} = props;
    const [data, setData] = useState<API.ServiceInstanceModel>();
    const [order, setOrder] = useState<API.OrderDTO | undefined>(undefined);
    const [currentPrice, setCurrentPrice] = useState<string | undefined>(undefined);
    const [selectedPayPeriod, setSelectedPayPeriod] = useState<number>(-1);
    const [submitting, setSubmitting] = useState(false);
    const [refundAmount, setRefundAmount] = useState<string>("0.00");
    const [source, setSource] = useState<string | undefined>(undefined);
    const {Paragraph} = Typography;
    const computeNestDefaultRegion = "cn-hangzhou";
    const [payPeriodsOptions, setPayPeriodsOptions] = useState<number[]>([]);
    const [refundModalVisible, setRefundModalVisible] = useState(false);
    const [renewalFormVisible, setRenewalFormVisible] = useState(false);
    const [createTime, setCreateTime] = useState<string | undefined>(undefined);
    const [updateTime, setUpdateTime] = useState<string | undefined>(undefined);
    const [currentOrder, setCurrentOrder] = useState<API.CommodityPriceModel | null>(null);
    const [renewalModalVisible, setRenewalModalVisible] = useState(false);
    const [tradeResult, setTradeResult] = useState<string | null>(null);
    const [activePaymentMethodKey, setActivePaymentMethodKey] = useState<string>('ALIPAY');
    const [showRefundAndDeleteButtons, setShowRefundAndDeleteButtons] = useState<boolean>(false);

    useEffect(()=>{
        setShowRefundAndDeleteButtons(data?.serviceType == 'managed');
    },[order, data])
    useEffect(() => {
        const fetchData = async () => {
            try {
                const serviceInstanceResult = await getServiceInstance({serviceInstanceId});
                if (serviceInstanceResult?.data) {
                    const {
                        createTime: fetchedCreateTime,
                        updateTime: fetchedUpdateTime,
                        source: source,
                        ...restData
                    } = serviceInstanceResult.data;
                    setData(restData);
                    setCreateTime(convertToLocaleTime(fetchedCreateTime));
                    setUpdateTime(convertToLocaleTime(fetchedUpdateTime));
                    setSource(source);
                }

                const orderResult = await listOrders({
                    serviceInstanceId,
                    tradeStatus: ["TRADE_SUCCESS"],
                    maxResults: 1,
                });
                if (orderResult?.data && orderResult.data.length > 0) {
                    setOrder(orderResult.data.at(0));
                }
            } catch (error) {
                console.error('Error fetching data:', error);
            }
        };

        fetchData();
    }, []);

    useEffect(() => {

        (async () => {
            if (order && order.commodityCode) {
                try {
                    if (order.specificationName) {
                        const result = await getCommoditySpecification({
                            commodityCode: order.commodityCode,
                            specificationName: order.specificationName
                        });
                        if (result.code === '200' && result.data && result.data.payPeriods) {
                            const periodsArray = JSON.parse(result.data.payPeriods);
                            setPayPeriodsOptions(periodsArray);
                        }
                    } else {
                        const result = await getCommodity({
                            commodityCode: order.commodityCode,
                        });
                        if (result.payPeriods != undefined) {
                            const periodsArray = JSON.parse(result.payPeriods);
                            setPayPeriodsOptions(periodsArray);
                        }
                    }
                } catch (error) {
                    console.error('Failed to fetch allowed pay periods:', error);
                }
            }
        })();
    }, [order]);

    useEffect(() => {
        const fetchCost = async () => {
            if (order != undefined && order.payPeriodUnit) {
                const params: API.getCommodityPriceParams = {
                    payPeriod: selectedPayPeriod,
                    payPeriodUnit: order.payPeriodUnit,
                    specificationName: order.specificationName,
                    commodityCode: order.commodityCode
                };

                const result = await getCommodityPrice(params);
                if (result.totalAmount != undefined) {
                    let totalAmount: string = result.totalAmount.toString();
                    setCurrentPrice(centsToYuan(totalAmount));
                }
            }
        }
        fetchCost();
    }, [selectedPayPeriod]);

    const LoadingIndicator = () => {
        return (
            <div style={{ minHeight: '100vh', display: 'flex', alignItems: 'center', justifyContent: 'center' }}>
                <Spin size="large" tip="加载中..." />
                <FormattedMessage id="message.service-instance-loading" defaultMessage='加载中'/>
            </div>
        );
    };

    const confirmDeleteServiceInstance = async (): Promise<void> => {
        try {
            if (serviceInstanceId) {
                await refundOrder({serviceInstanceId: props.serviceInstanceId, dryRun: false});
                message.success(<FormattedMessage id="message.refunding" defaultMessage='退款中'/>);
                window.location.reload();
            }
        } catch (error) {
            console.error(error);
            message.error(<FormattedMessage id="message.refund-failed" defaultMessage='退款失败'/>);
        }
        setRefundModalVisible(false);
    };

    const handleServiceInstanceDelete = async () => {
        if (serviceInstanceId == undefined) {
            return;
        }

        if (source != undefined && source == CallSource[CallSource.Market]) {
            let redirectParameters = {
                ServiceInstanceId: serviceInstanceId,
                RegionId: computeNestDefaultRegion
            }
            let redirectUrl = replaceUrlPlaceholders(COMPUTE_NEST_URL, redirectParameters);
            window.open(redirectUrl, '_blank');
            window.location.reload();
        } else {
            try {
                const response = await refundOrder(
                    {serviceInstanceId: serviceInstanceId, dryRun: true} as API.RefundOrderParam
                );
                const data = response?.data;
                if (data !== undefined) {
                    setRefundAmount(data.toFixed(2));
                    setRefundModalVisible(true);
                }
            } catch (error) {
                console.error(error);
            }
        }
    };

    const renewalServiceInstance = async (values: API.renewServiceInstanceParams) => {
        if (submitting) {
            return;
        }
        try {
            setSubmitting(true);
            if (order != null && order.serviceInstanceId) {
                const params: API.renewServiceInstanceParams = {
                    serviceInstanceId: serviceInstanceId,
                    payPeriod: selectedPayPeriod,
                    payPeriodUnit: order.payPeriodUnit,
                    payChannel: values.payChannel
                };

                const result = await renewServiceInstance(params);
                if (result.code == "200" && result.data != undefined) {
                    setActivePaymentMethodKey(values.payChannel as string);
                    setCurrentOrder(result.data);
                    setTradeResult(result.data.paymentForm as string);
                    setRenewalFormVisible(false);
                    setRenewalModalVisible(true);
                    console.log(result.data.paymentForm as string);
                    console.log(true);
                }
            }
        } catch (error) {
            console.log('Error: ', error);
        } finally {
            setSubmitting(false);
        }
    }

    if (data !== undefined && order !== undefined) {
        // const showRefundAndDeleteButtons = data.serviceType == 'managed' && order?.payChannel == 'ALIPAY';
        const {outputs, parameters} = processServiceInstanceData(data);
        let renewalAndDeleteVisible = false;
        if (data.status != undefined) {
            // @ts-ignore
            renewalAndDeleteVisible = getStatusEnum()[data?.status].status.toLocaleLowerCase() !== 'success';
        }
        const title = (
            <div style={{display: 'flex', justifyContent: 'space-between', alignItems: 'center'}}>
                <span><FormattedMessage id="menu.list.table-list" defaultMessage='服务实例'/></span>
                <Space>
                    <div>
                        {showRefundAndDeleteButtons && (
                            <div>
                                <Button title={"删除服务实例"} onClick={() => handleServiceInstanceDelete()}
                                        hidden={renewalAndDeleteVisible} danger={true}><FormattedMessage id="button.delete-service-instance" defaultMessage='删除服务实例'/></Button>
                                <Modal
                                    open={refundModalVisible}
                                    onCancel={() => {
                                        setRefundModalVisible(false);
                                    }}
                                    footer={null}
                                    title="删除服务实例"
                                >
                                    <Paragraph><FormattedMessage id="message.current-refundable-amount" defaultMessage='您当前服务实例可退金额为：'/><span style={{color: "red"}}>{centsToYuan(refundAmount)}</span></Paragraph>
                                    <div style={{marginTop: 16, textAlign: 'right'}}>
                                        <Space>

                                            <Button style={{width: '100px'}} type="primary"
                                                    onClick={confirmDeleteServiceInstance}>
                                                <FormattedMessage id='button.refund' defaultMessage='退款'/>
                                            </Button>
                                            <Button style={{width: '100px'}}
                                                    onClick={() => {
                                                        setRefundModalVisible(false);
                                                    }}><FormattedMessage id='button.cancel' defaultMessage='取消'/></Button>
                                        </Space>
                                    </div>
                                </Modal>
                            </div>
                        )}
                    </div>
                    {source != CallSource[CallSource.Market] &&
                        <div style={{display: 'flex', justifyContent: 'space-between', alignItems: 'center'}}>

                            <Button hidden={renewalAndDeleteVisible} onClick={() => setRenewalFormVisible(true)}>
                                <FormattedMessage id='button.renew' defaultMessage='续费'/>
                            </Button>
                            <Modal title="续费" open={renewalFormVisible}
                                   onCancel={() => setRenewalFormVisible(false)}
                                   footer={null} destroyOnClose={true}>
                                <ProForm onFinish={renewalServiceInstance}
                                         submitter={{
                                             render: (props, doms) => {
                                                 return (
                                                     <Space>
                                                         <Button type="primary" htmlType="submit" loading={submitting}>
                                                             <FormattedMessage id='button.renew' defaultMessage='续费'/>
                                                         </Button>
                                                         <Button onClick={() => {
                                                             setRenewalFormVisible(false);
                                                         }}>
                                                             <FormattedMessage id='button.cancel' defaultMessage='取消'/>
                                                         </Button>
                                                     </Space>
                                                 );
                                             },
                                         }}
                                >
                                    <ProFormSelect
                                        name="PayPeriod"
                                        label="续费时间"
                                        options={payPeriodsOptions.map((period) => ({
                                            label: `${period} ${unitMapping[order?.payPeriodUnit?.toLowerCase() as keyof UnitMappingType]}`,
                                            value: period,
                                        }))}
                                        fieldProps={{
                                            onChange: (value) => {
                                                if (value) {
                                                    setSelectedPayPeriod(value);
                                                }
                                            },
                                        }}
                                        required={true}
                                    />

                                    <PayTypeFormItem/>
                                    <Space>
                                        <div className={styles.currentPrice}>
                                            <FormattedMessage id='message.current-price' defaultMessage="当前价格:"/>
                                            <span className={styles.priceValue}>
                                            {currentPrice ? `     ¥${currentPrice}` :
                                                <FormattedMessage id='message.select-renewal-time' defaultMessage=" 请选择续费时间"/>}
                                            </span>
                                        </div>
                                    </Space>
                                    <Divider className={styles.msrectangleshape}/>
                                </ProForm>
                            </Modal>
                        </div>}
                </Space>
            </div>
        );

        return (
            // @ts-ignore
            <PageContainer title={props.serviceInstanceId}>
                {(renewalModalVisible && tradeResult) ?
                    <RenewalModal
                    serviceInstanceModel={data}
                    qrCodeURL={tradeResult? tradeResult : ""}
                    orderId={currentOrder?.orderId ? currentOrder?.orderId as string : ""}
                    orderAmount={currentOrder?.totalAmount? currentOrder?.totalAmount : -1}
                    visible={renewalModalVisible}
                    onClose={() => setRenewalModalVisible(false)}
                    activePaymentMethodKey={activePaymentMethodKey}
                >
                </RenewalModal> : null}
                <Space direction="vertical" size="large" style={{display: 'flex'}}>
                    <Descriptions bordered={true} title={title} column={2}>

                        <Descriptions.Item label={<FormattedMessage id='pages.instanceSearchTable.serviceInstanceId' defaultMessage="实例ID"/>}>{data?.serviceInstanceId} </Descriptions.Item>
                        <Descriptions.Item label={<FormattedMessage id='pages.instanceSearchTable.serviceInstanceName' defaultMessage="实例名称"/>}>{data?.serviceInstanceName}</Descriptions.Item>
                        <Descriptions.Item label={<FormattedMessage id='pages.instanceSearchTable.status' defaultMessage="状态"/>}>
                            {
                                <Badge
                                    //@ts-ignore
                                    status={data ? getStatusEnum()[data?.status].status.toLocaleLowerCase() : 'processing'}
                                    //@ts-ignore
                                    text={data ? getStatusEnum()[data?.status].text : 'Deployed'}/>
                            }
                        </Descriptions.Item>
                        <Descriptions.Item label={<FormattedMessage id='pages.instanceSearchTable.createTime' defaultMessage="创建时间"/>}>
                            {createTime}
                        </Descriptions.Item>
                        <Descriptions.Item label={<FormattedMessage id='pages.instanceSearchTable.updateTime' defaultMessage="更新时间"/>}>
                            {updateTime}</Descriptions.Item>
                        <Descriptions.Item
                            label={<FormattedMessage id='pages.instanceSearchTable.expiration-time' defaultMessage="到期时间"/>}>
                            {source == CallSource[CallSource.Market] ? <FormattedMessage id='message.paying-by-usage' defaultMessage="按量计费中"/> :
                            (
                                <div>{data.endTime ? dayjs(data.endTime).format('YYYY-MM-DD HH:mm:ss') : ''}</div>
                            )}
                        </Descriptions.Item>
                        {
                            Object.keys(outputs).map((key) => {
                                // @ts-ignore
                                let httpUrl = JSON.stringify(outputs[key]).replace(/"/g, "");
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
                                        label={key}>{JSON.stringify(outputs[key]).replace(/"/g, "")}</Descriptions.Item>)
                                }
                            })
                        }
                        {source == CallSource[CallSource.Market] && <Descriptions.Item label={<FormattedMessage id='pages.instanceSearchTable.cloud-market-order' defaultMessage="云市场订单"/>}>
                            <div style={{display: 'flex', justifyContent: 'space-between', alignItems: 'center'}}>
                                <Button title={"前往云市场"}
                                        onClick={navigateToCloudMarketplaceOrderDetails}><FormattedMessage id='button.goto-cloud-market' defaultMessage="前往云市场"/></Button>
                            </div>
                        </Descriptions.Item>}


                    </Descriptions>
                    <Divider/>
                    <Descriptions bordered={true} title={<FormattedMessage id='title.service-info' defaultMessage="服务信息"/>} column={2}>
                        <Descriptions.Item label={<FormattedMessage id='title.label.service-id' defaultMessage="服务ID"/>}>{data?.serviceModel?.serviceId}</Descriptions.Item>
                        <Descriptions.Item label={<FormattedMessage id='title.label.service-name' defaultMessage="服务名"/>}>{data?.serviceModel?.name} </Descriptions.Item>
                        <Descriptions.Item label={<FormattedMessage id='title.label.description' defaultMessage="描述"/>}>{data?.serviceModel?.description} </Descriptions.Item>
                    </Descriptions>
                    <Divider/>
                    <Descriptions bordered={true} title={<FormattedMessage id='title.configuration-info' defaultMessage="配置信息"/>} column={1}>
                        {
                            Object.keys(parameters).map((key) => {
                                return (<Descriptions.Item
                                    // @ts-ignore
                                    label={key}>{JSON.stringify(parameters[key]).replace(/"/g, "")}</Descriptions.Item>);
                            })
                        }
                    </Descriptions>
                    <Divider/>
                </Space>
            </PageContainer>
        )
    } else {
        return (<LoadingIndicator />)
    }
}

export default ServiceInstanceContent;
