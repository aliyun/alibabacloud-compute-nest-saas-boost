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
import {Badge, Button, Descriptions, Divider, message, Modal, Space, Typography} from 'antd';
import {getStatusEnum} from "@/pages/ServiceInstanceList/common";
import moment from "moment";
import dayjs from "dayjs";
import utc from "dayjs/plugin/utc";
import {listOrders, refundOrder} from "@/services/backend/order";
import {ProForm, ProFormDigit, ProFormInstance, ProFormSelect, ProFormText} from "@ant-design/pro-form";
import PayTypeFormItem from "@/pages/Service/component/PayTypeFormItem";
import {handlePaySubmit} from "@/util/aliPayUtil";
import {isIPv4Address, replaceUrlPlaceholders} from "@/util/urlUtil";
import {ServiceInstanceContentProps} from "@/pages/ServiceInstanceContent/components/interface";
import {CallSource, CLOUD_MARKET_ORDER_URL, COMPUTE_NEST_URL} from "@/constants";
import styles from "@/pages/Service/component/css/service.module.css";
import {centsToYuan} from "@/util/moneyUtil";
import {getServiceInstance, renewServiceInstance} from "@/services/backend/serviceInstance";
import {getEstimatedPrice} from "@/services/backend/commodity";
import {getCommoditySpecification} from "@/services/backend/specification";

dayjs.extend(utc);
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

type UnitMappingType = {
    [key: string]: string;
};

const unitMapping: UnitMappingType = {
    month: '月',
    day: '日',
    year: '年',
};

const ServiceInstanceContent: React.FC<ServiceInstanceContentProps> = (props) => {
    const {serviceInstanceId} = props;
    const [data, setData] = useState<API.ServiceInstanceModel>();
    const form = useRef<ProFormInstance>();
    const [order, setOrder] = useState<API.OrderDTO | undefined>(undefined);
    const [currentPrice, setCurrentPrice] = useState<string | undefined>(undefined);
    const [selectedPayPeriod, setSelectedPayPeriod] = useState<number>(1);
    const [submitting, setSubmitting] = useState(false);
    const [visible, setVisible] = useState(false);
    const [refundAmount, setRefundAmount] = useState<string>("0.00");
    const [source, setSource] = useState<string | undefined>(undefined);
    const {Paragraph} = Typography;
    const computeNestDefaultRegion = "cn-hangzhou";
    const [payPeriodUnit, setPayPeriodUnit] = useState('');
    const [payPeriodsOptions, setPayPeriodsOptions] = useState<number[]>([]);


    useEffect(() => {
        setSource(data?.source);
    }, [data]);

    useEffect(() => {
        const params: API.getServiceInstanceParams = {
            serviceInstanceId: serviceInstanceId,
        };

        (async () => {
            const result = await getServiceInstance(params);
            setData(result.data);
            console.log(result);
        })();

        (async () => {
            const result = await listOrders({
                serviceInstanceId: serviceInstanceId,
                tradeStatus: ["TRADE_SUCCESS"],
                maxResults: 1
            });
            if (result != undefined && result.data != undefined && result.data.length > 0) {
                setOrder(result.data.at(0));

                if (result.data?.at(0) != undefined && result?.data?.at(0)?.payPeriodUnit != undefined) {
                    const unitInChinese = unitMapping[result?.data?.at(0)?.payPeriodUnit?.toLowerCase() as keyof UnitMappingType];
                    setPayPeriodUnit(unitInChinese);
                }
            }

        })();
    }, [serviceInstanceId]);

    useEffect(() => {

        (async () => {
            if (order && order.commodityCode) {
                try {
                    const result = await getCommoditySpecification({
                        commodityCode: order.commodityCode,
                        specificationName: order.specificationName
                    });
                    if (result.code === '200' && result.data && result.data.payPeriods) {
                        const periodsArray = JSON.parse(result.data.payPeriods);
                        setPayPeriodsOptions(periodsArray);
                    }
                } catch (error) {
                    console.error('Failed to fetch commodity specification:', error);
                }
            }
        })();
    }, [order]);

    useEffect(() => {
        const fetchCost = async () => {
            if (order != undefined && order.payPeriodUnit && order.specificationName) {
                const params: API.getEstimatedPriceParams = {
                    payPeriod: selectedPayPeriod,
                    payPeriodUnit: order.payPeriodUnit,
                    specificationName: order.specificationName,
                    commodityCode: order.commodityCode
                };

                const result = await getEstimatedPrice(params);
                if (result.totalAmount != undefined) {
                    let totalAmount: string = result.totalAmount.toString();
                    setCurrentPrice(centsToYuan(totalAmount));
                }
            }
        }
        fetchCost();
    }, [selectedPayPeriod]);

    const confirmDeleteServiceInstance = async (): Promise<void> => {
        try {
            if (serviceInstanceId) {
                await refundOrder({serviceInstanceId: props.serviceInstanceId, dryRun: false});
                message.success('退款中');
                window.location.reload();
            }
        } catch (error) {
            console.error(error);
            message.error('退款失败');
        }
        setVisible(false);
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
                    setVisible(true);
                }
            } catch (error) {
                console.error(error);
            }
        }
    };

    const closeRefundModel = () => {
        setVisible(false);
    };

    function navigateToCloudMarketplaceOrderDetails(): void {
        window.open(CLOUD_MARKET_ORDER_URL, '_blank');
        window.location.reload();
    }

    const renewalServiceInstance = async (values: API.renewServiceInstanceParams) => {
        console.log("as");

        if (submitting) {
            return;
        }
        try {
            setSubmitting(true);
            if (order != null && order.specificationName && order.serviceInstanceId) {
                const params: API.renewServiceInstanceParams = {
                    serviceInstanceId: serviceInstanceId,
                    payPeriod: selectedPayPeriod,
                    payPeriodUnit: order.payPeriodUnit,
                    payChannel: values.payChannel
                };

                const result = await renewServiceInstance(params);
                if (result.code == "200" && result.data != undefined) {
                    await handlePaySubmit(result.data.paymentForm);
                }
            }
        } catch (error) {
            console.log('Error: ', error);
        } finally {
            setSubmitting(false);
        }
    }


    if (data !== undefined) {
        const showRefundAndDeleteButtons = data.serviceType == 'managed';
        console.log(data.serviceType);
        const {outputs, parameters} = processServiceInstanceData(data);
        console.log(CallSource[CallSource.Market]);
        let renewalAndDeleteVisible = false;
        if (data.status != undefined) {
            // @ts-ignore
            renewalAndDeleteVisible = getStatusEnum()[data?.status].status.toLocaleLowerCase() !== 'success';
        }
        const title = (
            <div style={{display: 'flex', justifyContent: 'space-between', alignItems: 'center'}}>
                <span>服务实例</span>
                <Space>
                    <div>
                        {showRefundAndDeleteButtons && (
                            <div>
                                <Button title={"删除服务实例"} onClick={() => handleServiceInstanceDelete()}
                                        hidden={renewalAndDeleteVisible} danger={true}>删除服务实例</Button>
                                <Modal
                                    open={visible}
                                    onCancel={closeRefundModel}
                                    footer={null}
                                    title="退款金额"
                                >
                                    <Paragraph>您当前服务实例可退金额为：<span
                                        style={{color: "red"}}>{centsToYuan(refundAmount)}</span></Paragraph>
                                    <div style={{marginTop: 16, textAlign: 'right'}}>
                                        <Button style={{width: '100px'}} type="primary"
                                                onClick={confirmDeleteServiceInstance}>
                                            退款
                                        </Button>
                                        <Button style={{width: '100px'}}
                                                onClick={closeRefundModel}>取消</Button>
                                    </div>
                                </Modal>
                            </div>
                        )}
                    </div>
                    {source != CallSource[CallSource.Market] &&
                        <div style={{display: 'flex', justifyContent: 'space-between', alignItems: 'center'}}>
                            <Button hidden={renewalAndDeleteVisible} onClick={() => setVisible(true)}>
                                续费
                            </Button>
                            <Modal
                                title="续费"
                                open={visible}
                                onCancel={() => setVisible(false)}
                                footer={null}
                                destroyOnClose={true}
                            >
                                <ProForm
                                    onFinish={renewalServiceInstance}
                                >
                                    <div>
                                        {/*<PayPeriodFormItem onChange={handleOptionChange}/>*/}
                                        {/*<ProFormText*/}
                                        {/*    name="payPeriodUnit"*/}
                                        {/*    label="购买周期"*/}
                                        {/*    initialValue={payPeriodUnit}*/}
                                        {/*    disabled={true}*/}
                                        {/*/>*/}
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
                                        {/*<Divider className={styles.msrectangleshape}/>*/}
                                        <div className={styles.currentPrice}>
                                            当前价格:
                                            <span className={styles.priceValue}>
                                        {currentPrice ? `     ¥${currentPrice}` : " 加载中..."}
                                    </span>
                                        </div>
                                        <Divider className={styles.msrectangleshape}/>

                                    </div>
                                </ProForm>
                            </Modal>
                            {/*</ModalForm>*/}
                        </div>}

                </Space>
            </div>
        );
        // @ts-ignore
        return (
            <Space direction="vertical" size="large" style={{display: 'flex'}}>
                <Descriptions bordered={true} title={title} column={2}>

                    <Descriptions.Item label="服务实例ID">{data?.serviceInstanceId} </Descriptions.Item>
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
                    <Descriptions.Item
                        label="到期时间">{source == CallSource[CallSource.Market] ? "按量计费中" :
                        (
                            <div>{order?.billingEndDateMillis ? dayjs(order?.billingEndDateMillis).format('YYYY-MM-DD HH:mm:ss') : ''}</div>
                        )}
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
                    {source == CallSource[CallSource.Market] && <Descriptions.Item label="云市场订单">
                        <div style={{display: 'flex', justifyContent: 'space-between', alignItems: 'center'}}>
                            <Button title={"前往云市场"}
                                    onClick={navigateToCloudMarketplaceOrderDetails}>前往云市场</Button>
                        </div>
                    </Descriptions.Item>}


                </Descriptions>
                <Divider/>
                <Descriptions bordered={true} title="服务信息" column={2}>
                    <Descriptions.Item label="服务ID">{data?.serviceModel?.serviceId}</Descriptions.Item>
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
