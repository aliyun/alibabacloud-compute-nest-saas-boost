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

import React, {useRef, useState} from 'react';
import {FormInstance, Modal} from 'antd';
import PayFormItem from "@/pages/ServiceInstanceList/components/CreateServiceInstance/PayTypeFormItem";
import {
    SpecificationFormItem
} from "@/pages/ServiceInstanceList/components/CreateServiceInstance/SpecificationFormItem";
import {createOrder} from "@/services/backend/order";
import {ProForm, ProFormInstance} from "@ant-design/pro-form";

interface CreateModalProps {
    createModalVisible: boolean;
    setCreateModalVisible: (visible: boolean) => void;
}

export const FormContext = React.createContext<FormInstance<any> | null>(null);
const CreateModal: React.FC<CreateModalProps> = ({
                                                     createModalVisible, setCreateModalVisible
                                                 }) => {
    const [submitting, setSubmitting] = useState(false);
    const form = useRef<ProFormInstance>();

    const handleCreateSubmit = async () => {
        if (submitting) {
            return;
        }
        try {
            setSubmitting(true);
            if (!(form !== undefined && form?.current !== null && form?.current?.getFieldFormatValue !== undefined)) {
                return;
            }
            const {specification, type, ...values} = await form?.current?.getFieldFormatValue();
            console.log(specification);
            const productComponents = {
                SpecificationName: specification?.specificationName,
                PayPeriod: specification?.payPeriod,
                PayPeriodUnit: "Month",
                ...values
                // RegionId: values.regionId
            };
            console.log("ttt");
            console.log(productComponents);
            await handleSubmit({
                productComponents: JSON.stringify(productComponents),
                type: type,
                productName: 'SERVICE_INSTANCE',
            });
            form?.current?.resetFields();

            setCreateModalVisible(false);
        } catch (error) {
            console.log('Error: ', error);
        } finally {
            form?.current?.resetFields();
            setSubmitting(false);
            // window.location.reload();
        }
    };

    const handleSubmit = async (values: API.createOrderParams) => {
        try {
            const response = await createOrder(values);
            if (response.code !== '200') {
                Modal.error({
                    title: 'Error',
                    content: response.message,
                });
            }
            const formString = response.data;
            if (formString != undefined) {
                let divForm = document.getElementsByTagName('divform')
                const div = document.createElement('div');
                div.innerHTML = formString;
                document.body.appendChild(div);
                document.forms[2].setAttribute('target', '_self') // 加了_blank可能出问题所以我注释了
                document.forms[2].submit();
            }
        } catch (error) {
            console.log('Error: ', error);
        }
    };

    return (
        <Modal
            title="创建"
            open={createModalVisible}
            onCancel={() => setCreateModalVisible(false)}
            onOk={handleCreateSubmit}
        >
            <ProForm formRef={form} layout="vertical" submitter={{
                resetButtonProps: {
                    style: {
                        display: 'none',
                    },
                }, submitButtonProps: {
                    style: {
                        display: 'none',
                    },
                },
            }}>
                <SpecificationFormItem/>
                <PayFormItem/>
            </ProForm>
        </Modal>
    );
};

export default CreateModal;
