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

import React from "react";
import ProCard from "@ant-design/pro-card";
import {ProFormRadio, ProFormSelect} from "@ant-design/pro-form";
import {Col, Row} from "antd";

export enum PayTypeEnum {
    ALIPAY = '支付宝',
}

const PayFormItem: React.FC = () => {
    const payTypeEntries = Object.entries(PayTypeEnum);

    return (
        <ProCard title={"支付方式"} bordered headerBordered={false} gutter={16} hoverable>
            <Row justify="center" style={{marginTop: '20px'}}>
                <Col span={24}>
                    <ProFormSelect
                        name="type"
                        initialValue={"ALIPAY"}
                        rules={[
                            {required: true, message: '请选择支付方式'},
                        ]}
                    >
                        <ProFormRadio.Group initialValue={PayTypeEnum.ALIPAY}
                                            options={payTypeEntries.map(([key, value]) => ({
                                                label: value,
                                                value: key
                                            }))}>
                        </ProFormRadio.Group>
                    </ProFormSelect>
                </Col>
            </Row>
        </ProCard>
    );
};
export default PayFormItem;
