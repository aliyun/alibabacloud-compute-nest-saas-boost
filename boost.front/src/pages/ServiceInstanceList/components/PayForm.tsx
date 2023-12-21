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
import {CreateServiceInstanceForm} from "@/pages/ServiceInstanceList/components/CreateServiceInstanceForm";
import {ModalForm, ProForm, ProFormInstance, ProFormRadio, ProFormSelect} from "@ant-design/pro-form";
import {handleAlipaySubmit} from "@/util/aliPayUtil";
import {Col, Row, Tabs} from "antd";
import ProCard from "@ant-design/pro-card";
import {useModel} from "@@/exports";
import {getServiceMetadata} from "@/services/backend/serviceManager";
import {replaceUrlPlaceholders} from "@/util/urlUtil";
import {CreateModalProps} from "@/pages/ServiceInstanceList/components/interface";
import {cloudMarketPurchaseUrl} from "@/constants";

const CreateModal: React.FC<CreateModalProps> = ({
                                                     createModalVisible, setCreateModalVisible
                                                 }) => {
    const [submitting, setSubmitting] = useState(false);
    const form = useRef<ProFormInstance>();
    const {TabPane} = Tabs;
    const {initialState} = useModel('@@initialState');

    // 从 initialState 中访问 currentUser
    const currentUser = initialState?.currentUser;
    const isAliyunUser = currentUser?.aid !== null;
    const [marketProductCode, setMarketProductCode] = useState<string | undefined>(undefined);
    let isError: boolean = false;

    const handleCreateServiceInstanceSubmit = async (payType: string) => {
        if (submitting) {
            return;
        }
        let formValues = undefined;
        try {
            setSubmitting(true);
            if (!(form !== undefined && form?.current !== null && form?.current?.getFieldFormatValue !== undefined && form?.current?.getFieldFormatValue)) {
                return;
            }
            const {SpecificationName, PayPeriod, type, ...values} = await form?.current?.getFieldFormatValue();
            if (payType == 'PAY_POST') {
                if (marketProductCode == undefined) {
                    return;
                }
                let redirectUrl = replaceUrlPlaceholders(cloudMarketPurchaseUrl, {MarketProductCode: marketProductCode});
                window.open(redirectUrl, '_blank');
                window.location.reload();
            } else {
                const productComponents = {
                    SpecificationName: SpecificationName,
                    PayPeriod: PayPeriod,
                    PayPeriodUnit: "Month",
                    ...values
                    // RegionId: values.regionId
                };
                console.log(productComponents);
                await handleAlipaySubmit({
                    productComponents: JSON.stringify(productComponents),
                    type: type,
                    productName: 'SERVICE_INSTANCE',
                }, 3);
            }
        } catch (error) {
            console.log('Error: ', error);
            form?.current?.setFieldsValue(formValues);
            isError = true;
            return;
        } finally {
            setSubmitting(false);
            if (!isError) {
                form?.current?.resetFields();
                setCreateModalVisible(false);
            }
        }
    };

    useEffect(() => {
        const fetchData = async () => {
            try {
                const serviceMetadataResponse = await getServiceMetadata({});
                const serviceMetadata = serviceMetadataResponse?.data;
                if (serviceMetadata !== undefined) {
                    if (serviceMetadata.commodityCode !== undefined) {
                        setMarketProductCode(serviceMetadata.commodityCode);
                    }
                }
            } catch (error) {
                console.log('Error: ', error);
            }
        }
        fetchData();
    },);

    const submitButtonLoc = {
        render: (_: any, dom: any) => (
            <Row justify="end">
                <Col>{dom}</Col>
            </Row>
        )
    };

    return (
        <ModalForm
            title="创建服务实例"
            size={'large'}
            open={createModalVisible}
            modalProps={{
                destroyOnClose: true,
                onCancel: () => setCreateModalVisible(false),
            }}
            submitter={false}
        >
            <Tabs>
                <TabPane tab={"包年包月"} key={1}>

                    <div style={{display: 'flex', flexDirection: 'column'}}>
                        <ProForm formRef={form} onFinish={async (values) => {
                            await handleCreateServiceInstanceSubmit("");
                            return true;
                        }} submitter={submitButtonLoc}>
                            <CreateServiceInstanceForm/>
                        </ProForm>
                    </div>
                </TabPane>

                {isAliyunUser && (marketProductCode != undefined) && (

                    <TabPane tab="按量付费" key="2">
                        <ProForm onFinish={async (values) => {
                            await handleCreateServiceInstanceSubmit("PAY_POST");
                            return true;
                        }} submitter={submitButtonLoc}>
                            <ProCard title={"支付方式"} bordered headerBordered={true} gutter={16} hoverable>
                                <Row justify="center" style={{marginTop: '16px'}}>
                                    <Col span={24}>
                                        <ProFormSelect
                                            name="type"
                                            initialValue={"PayPost"}
                                            rules={[
                                                {required: true, message: '请选择支付方式'},
                                            ]}
                                        >
                                            <ProFormRadio.Group initialValue={"PayPost"} options={[
                                                {
                                                    label: '云市场按量付费',
                                                    value: 'PayPost',
                                                },
                                            ]}>
                                            </ProFormRadio.Group>
                                        </ProFormSelect>
                                    </Col>
                                </Row>
                            </ProCard>
                        </ProForm>
                    </TabPane>
                )}
            </Tabs>
        </ModalForm>

    );
};

export default CreateModal;
