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
import {getStatusEnum} from "@/pages/ServiceInstance/common";
import moment from "moment";
import dayjs from "dayjs";
import utc from "dayjs/plugin/utc";
import {listOrders, refundOrder} from "@/services/backend/order";
import {getServiceInstance} from "@/services/backend/serviceInstance";
import {ModalForm, ProFormInstance} from "@ant-design/pro-form";
import PayFormItem from "@/pages/ServiceInstanceList/components/form/PayTypeFormItem";
import ProCard from "@ant-design/pro-card";
import {PayPeriodFormItem} from "@/pages/ServiceInstanceList/components/form/PayPeriodFormItem";
import {getServiceCost} from "@/services/backend/serviceManager";
import {handleAlipaySubmit} from "@/pages/ServiceInstanceList/components/form/AlipayForm";

dayjs.extend(utc);

interface ServiceInstanceContentProps {
    serviceInstanceId?: string;

    status?: string;
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
    const form = useRef<ProFormInstance>();
    const [order, setOrder] = useState<API.OrderDTO | undefined>(undefined);
    const [currentPrice, setCurrentPrice] = useState<number | null>(null);
    const [selectedMonths, setSelectedMonths] = useState<number>(1);
    const [submitting, setSubmitting] = useState(false);
    const [visible, setVisible] = useState(false);
    const [refundAmount, setRefundAmount] = useState<string>("0.00");
    const {Paragraph} = Typography;

    const handleOptionChange = (month: number) => {
        setSelectedMonths(month);
    };

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
                tradeStatus: "TRADE_SUCCESS",
                maxResults: 1
            });
            if (result != undefined && result.data != undefined && result.data.length > 0) {
                setOrder(result.data.at(0));
            }

        })();
    }, [serviceInstanceId]);

    useEffect(() => {
        const fetchServiceCost = async () => {
            try {
                if (selectedMonths && order != undefined && order.specificationName != undefined) {
                    const response = await getServiceCost({
                        specificationName: order.specificationName,
                        payPeriod: selectedMonths,
                        payPeriodUnit: "Month",
                    } as API.getServiceCostParams);
                    setCurrentPrice(response.data || null);
                    return;
                }
                setCurrentPrice(null);
            } catch (error) {
                Modal.error({
                    title: '套餐名不匹配',
                    content: (
                        <div>
                            <p>套餐名不匹配，请修改后重新运行流水线：</p>
                            <p>
                                <a
                                    href="https://aliyun.github.io/alibabacloud-compute-nest-saas-boost/"
                                    target="_blank"
                                    rel="noopener noreferrer"
                                >
                                    https://aliyun.github.io/alibabacloud-compute-nest-saas-boost/
                                </a>
                            </p>
                        </div>
                    ),
                });
                setCurrentPrice(100);
            }
        };
        fetchServiceCost();
    }, [selectedMonths]);

    const handleConfirmRefund = async (): Promise<void> => {
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

    const handleButtonClick = async () => {
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
    };

    const handleModalClose = () => {
        setVisible(false);
    };

    const handleCreateSubmit = async () => {
        if (submitting) {
            return;
        }
        try {
            setSubmitting(true);
            if (!(form !== undefined && form?.current !== null && form?.current?.getFieldFormatValue !== undefined && form?.current?.getFieldFormatValue)) {
                return;
            }
            const {PayPeriod, type} = await form?.current?.getFieldFormatValue();
            if (order != null && order.specificationName && order.serviceInstanceId) {
                const productComponents = {
                    SpecificationName: order.specificationName,
                    PayPeriod: PayPeriod,
                    PayPeriodUnit: "Month",
                    ServiceInstanceId: order.serviceInstanceId
                };
                console.log(productComponents);
                await handleAlipaySubmit({
                    productComponents: JSON.stringify(productComponents),
                    type: type,
                    productName: 'SERVICE_INSTANCE',
                }, 1);
            }
        } catch (error) {
            console.log('Error: ', error);
        } finally {
            setSubmitting(false);
        }
    }

    if (data !== undefined) {
        const {outputs, parameters} = processServiceInstanceData(data);
        // @ts-ignore
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
                            <div>{order?.billingsEndDateLong ? dayjs(order?.billingsEndDateLong).format('YYYY-MM-DD HH:mm:ss') : ''}</div>
                            <ModalForm
                                title="续费"
                                size={'large'}
                                trigger={<Button hidden={getStatusEnum()[data?.status].status.toLocaleLowerCase() !== 'success'}>续费</Button>}
                                formRef={form}
                                modalProps={{
                                    destroyOnClose: true,
                                }}
                                onFinish={async (values) => {
                                    await handleCreateSubmit();
                                    return true;
                                }}
                            >
                                <div style={{display: 'flex', flexDirection: 'column'}}>
                                    <ProCard title="按月购买" bordered headerBordered={false} gutter={16} hoverable>
                                        <PayPeriodFormItem onChange={handleOptionChange}/>
                                        <div style={{textAlign: "right", padding: "16px"}}>
                                            当前价格: <span
                                            style={{color: "red"}}>{currentPrice ? currentPrice.toFixed(2) : "加载中..."}</span>
                                        </div>
                                    </ProCard>
                                    <PayFormItem/>
                                </div>
                            </ModalForm>
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
                    <Descriptions.Item label="释放服务实例">

                        <div style={{display: 'flex', justifyContent: 'space-between', alignItems: 'center'}}>
                            <Button title={"删除服务实例"} onClick={() => handleButtonClick()} hidden={getStatusEnum()[data?.status].status.toLocaleLowerCase() !== 'success'}>删除服务实例</Button>
                        </div>
                        <Modal open={visible} onCancel={handleModalClose} footer={null}>
                            <ProCard title="退款金额">
                                <Paragraph>您当前服务实例可退金额为：<span
                                    style={{color: "red"}}>{refundAmount}</span></Paragraph>
                                <div style={{marginTop: 16, textAlign: 'right'}}>
                                    <Button style={{width: '100px'}} className="ant-btn ant-btn-primary" type="primary"
                                            onClick={handleConfirmRefund}>
                                        退款
                                    </Button>
                                    <Button style={{width: '100px'}} className="ant-btn ant-btn-default"
                                            onClick={handleModalClose}>取消</Button>
                                </div>
                            </ProCard>
                        </Modal>
                    </Descriptions.Item>

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
