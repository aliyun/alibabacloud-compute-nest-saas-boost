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

import {Button, Col, Input, Modal, Row} from "antd";
import React, {useEffect, useState} from "react";
import ProCard from "@ant-design/pro-card";

export const PayPeriodFormItem: React.FC<{ onChange: (month: number) => void }> = ({onChange}) => {
    const [selectedMonth, setSelectedMonth] = useState<number | null>(null);
    const [showCustomModal, setShowCustomModal] = useState(false);
    const handleCustomModalOpen = () => {
        setSelectedMonth(null);
        setShowCustomModal(true);
    };

    const handleCustomModalClose = () => {
        setShowCustomModal(false);
    };

    const handleCustomMonthChange = (e: React.ChangeEvent<HTMLInputElement>) => {
        const month = parseInt(e.target.value);
        if (!isNaN(month)) {
            if (month == selectedMonth) {
                setSelectedMonth(null);
            } else {
                setSelectedMonth(month);
            }
        }

    };

    const handleCustomMonthSubmit = () => {
        if (selectedMonth !== null) {
            onChange(selectedMonth);
            handleCustomModalClose();
        }
    };

    useEffect(() => {
        setSelectedMonth(null);
    }, []);
    const handleOptionChange = (month: number) => {
        if (month == selectedMonth) {
            setSelectedMonth(null);
        } else {
            setSelectedMonth(month);
        }
        onChange(month);
    };

    const renderButton = (month: number) => (
        <Button
            key={month}
            onClick={() => handleOptionChange(month)}
            style={{
                background: selectedMonth === month ? "#89c1f5" : "#f5f5f5",
                color: selectedMonth === month ? "#fff" : "#000",
            }}
        >
            {month}
        </Button>
    );

    return (
        <ProCard title="按月购买" bordered headerBordered={false} gutter={16} hoverable>
            <Row justify="center" style={{marginTop: '20px'}}>
                <Col span={24}>
                    {/*<div style={{textAlign: 'left', marginBottom: '10px'}}><button>按月购买</button></div>*/}
                    <Button.Group style={{display: 'flex', justifyContent: 'left'}}>
                        {[...Array(8)].map((_, index) => renderButton(index + 1))}
                        {<Button onClick={handleCustomModalOpen} style={{background: "#f5f5f5", color: "#000"}}>
                            自定义
                        </Button>}
                    </Button.Group>
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
                <Input
                    placeholder={selectedMonth === null ? "请输入月份" : selectedMonth.toString()}
                    value={selectedMonth === null ? "" : selectedMonth.toString()}
                    onChange={handleCustomMonthChange}
                    type={'number'}
                    min={1}
                />
            </Modal>
        </ProCard>)
}
