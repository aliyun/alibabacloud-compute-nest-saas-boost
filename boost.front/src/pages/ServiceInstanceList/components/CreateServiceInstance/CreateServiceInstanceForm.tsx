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

const CreateModal: React.FC<CreateModalProps> = ({
                                                     createModalVisible, setCreateModalVisible
                                                 }) => {
    const [submitting, setSubmitting] = useState(false);
    const form = useRef<ProFormInstance>();
    async function getFieldAndCheck(fieldName:string) {
        const fieldValue = await form?.current?.getFieldValue(fieldName);
        if (fieldValue === undefined) {
            Modal.error({
                title: 'Error',
                content: fieldName + ' is undefined',
            });
            return true;
        }
        return false;
    }
    let isError:boolean = false;
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
            formValues = await form?.current?.getFieldFormatValue();
            console.log(formValues);
            const {SpecificationName, PayPeriod, type, ...values} = formValues;
            const fieldsToCheck = ['SpecificationName', 'PayPeriod'];
            for (const field of fieldsToCheck) {
                const wasUndefined = await getFieldAndCheck(field);
                if (wasUndefined) {
                    return;
                }
            }
            const productComponents = {
                SpecificationName: SpecificationName,
                PayPeriod: PayPeriod,
                PayPeriodUnit: "Month",
                ...values
                // RegionId: values.regionId
            };
            console.log(productComponents);
            await handleSubmit({
                productComponents: JSON.stringify(productComponents),
                type: type,
                productName: 'SERVICE_INSTANCE',
            });
        } catch (error) {
            console.log('Error: ', error);
            form?.current?.setFieldsValue(formValues);
            isError = true;
            console.log(form.current?.getFieldsValue);
            return;
        } finally {
            setSubmitting(false);
            if (!isError) {
                form?.current?.resetFields();
                setCreateModalVisible(false);
            }
            // window.location.reload();

        }
    };

    const handleSubmit = async (values: API.createOrderParams) => {

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
            }
            } onFinishFailed={()=>{
            }
            }>
                <SpecificationFormItem />
                <PayFormItem />
            </ProForm>
        </Modal>
    );
};

export default CreateModal;
