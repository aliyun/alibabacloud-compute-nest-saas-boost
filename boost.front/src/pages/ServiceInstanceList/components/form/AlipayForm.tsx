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
import {Modal} from 'antd';
import {CreateServiceInstanceForm} from "@/pages/ServiceInstanceList/components/form/CreateServiceInstanceForm";
import {createOrder} from "@/services/backend/order";
import {ModalForm, ProFormInstance} from "@ant-design/pro-form";
import {CreateModalProps} from "@/pages/ServiceInstanceList/components/interface/CreateServiceInstanceModelInterface";

export const handleAlipaySubmit = async (values: API.createOrderParams, index:number) => {

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
        document.forms[index].setAttribute('target', '_self')
        document.forms[index].submit();
    }
};

const CreateModal: React.FC<CreateModalProps> = ({
                                                     createModalVisible, setCreateModalVisible
                                                 }) => {
    const [submitting, setSubmitting] = useState(false);
    const form = useRef<ProFormInstance>();

    let isError: boolean = false;
    const handleCreateSubmit = async () => {
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
            }, 2);
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

    return (
        <ModalForm
            title="创建服务实例"
            size={'large'}
            open={createModalVisible}
            formRef={form}
            modalProps={{
                destroyOnClose: true,
                onCancel: () => setCreateModalVisible(false),
            }}
            onFinish={async (values) => {
                await handleCreateSubmit();
                return true;
            }}
        >
            <div style={{ display: 'flex', flexDirection: 'column' }}>

            <CreateServiceInstanceForm/>
            </div>
        </ModalForm>
    );
};

export default CreateModal;
