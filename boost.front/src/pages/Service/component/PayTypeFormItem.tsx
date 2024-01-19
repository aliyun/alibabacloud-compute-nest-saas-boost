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
import {ProFormItem, ProFormRadio} from "@ant-design/pro-form";
import {Col, Radio, Row, Space} from "antd";
import ProCard from '@ant-design/pro-card';

import {PayTypeEnum} from "@/constants";
import {AlipayCircleOutlined} from "@ant-design/icons";

const PayTypeFormItem: React.FC = () => {
    const payTypeEntries = Object.entries(PayTypeEnum);

    return (
        <ProFormItem
            name="type"
            initialValue={"ALIPAY"}
            rules={[{ required: true, message: '请选择支付方式' }]}
        >
            <ProFormRadio.Group initialValue={PayTypeEnum.ALIPAY}>
                <Row gutter={16}>
                    {payTypeEntries.map(([key, value]) => (
                        <Col key={key} span={12}>
                            <ProCard hoverable bordered>
                                <Radio value={key}>
                                    <Space>
                                        {key === 'ALIPAY' && <AlipayCircleOutlined style={{color: '#009fe8'}} />}
                                        {value}
                                    </Space>
                                </Radio>
                            </ProCard>
                        </Col>
                    ))}
                </Row>
            </ProFormRadio.Group>
        </ProFormItem>
    );
};
export default PayTypeFormItem;
