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

import {Button, Col, Modal, Row} from "antd";
import React, {ReactNode, useEffect, useRef, useState} from "react";
import ProCard from "@ant-design/pro-card";
import {ProFormRadio} from '@ant-design/pro-components';
import {ProFormDigit, ProFormInstance} from "@ant-design/pro-form";

export const PayPeriodFormItem: React.FC<{ onChange: (month: number) => void }> = ({onChange}) => {
    const [selectedMonth, setSelectedMonth] = useState<number | undefined>(undefined);
    const [showCustomModal, setShowCustomModal] = useState(false);
    const [customButtonText, setCustomButtonText] = useState('自定义');
    const [customButtonStyle, setCustomButtonStyle] = useState({});
    const [buttonStyle, setButtonStyle] = useState({});
    const defaultButtonSize = 12;

    const handleCustomModalOpen = () => {
        setSelectedMonth(defaultButtonSize + 1);
        setShowCustomModal(true);
    };

    const handleCustomModalClose = () => {
        setShowCustomModal(false);
    };

    const handleCustomMonthSubmit = () => {
        if (selectedMonth !== undefined && selectedMonth !== null) {
            setCustomButtonText(selectedMonth?.toString());
            setCustomButtonStyle({borderColor: '#1890ff'});
            onChange(selectedMonth);
            handleCustomModalClose();
        }
    };

    useEffect(()=>{
        if (selectedMonth !== undefined) {
            onChange(selectedMonth);
        }
    }, [selectedMonth]);

    const options: Array<string | { label: ReactNode, value: number }> = [];
    for (let i = 1; i <= defaultButtonSize; i++) {
        options.push({label: i.toString(), value: i});
    }

    return (
        <ProCard type={"inner"} bordered headerBordered={false} gutter={16} hoverable>
            <Row justify="center" style={{marginTop: '20px'}}>
                <Col span={24}>
                    <ProFormRadio.Group radioType={"button"} name={"PayPeriod"} rules={[{required: true, message: '请选择订购的时间'}]}
                                        addonAfter={(
                                            <Button style={customButtonStyle}
                                                    onClick={handleCustomModalOpen}
                                            >
                                                {customButtonText}
                                            </Button>
                                        )} options={options} style={{display: 'flex', justifyContent: 'left'}}
                                        layout={"vertical"}
                                        fieldProps={{
                                            onChange: (e) => {
                                                setSelectedMonth(e.target.value);
                                                setCustomButtonStyle({});
                                                setCustomButtonText("自定义");
                                                onChange(e.target.value);
                                            }, style: buttonStyle, value: selectedMonth, name: "PayPeriod",
                                        }}>
                    </ProFormRadio.Group>
                </Col>
            </Row>
            <Modal
                title="自定义月份"
                open={showCustomModal}
                onCancel={handleCustomModalClose}
                footer={[
                    <Button key="cancel" onClick={handleCustomModalClose}>
                        取消
                    </Button>,
                    <Button key="submit" type="primary" onClick={handleCustomMonthSubmit}>
                        确定
                    </Button>,
                ]}
                destroyOnClose
            >
                <ProFormDigit name={"PayPeriod"} rules={[{required: true, message: '请输入月份'}]}
                              placeholder={selectedMonth === undefined ? "请输入月份" : selectedMonth.toString()}
                              min={defaultButtonSize + 1} fieldProps={{
                    onChange: (e) => {
                        if (e !== null) {
                            setSelectedMonth(e);
                            setButtonStyle({});
                            setCustomButtonText(e?.toString());
                            setCustomButtonStyle({borderColor: '#1890ff'})
                            onChange(e);
                        } else {
                            setSelectedMonth(undefined);
                        }
                    },
                    style: customButtonStyle,
                    defaultValue: defaultButtonSize + 1,
                    value: selectedMonth,
                }}
                />
            </Modal>
        </ProCard>
    )
}
