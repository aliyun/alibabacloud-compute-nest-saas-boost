import React, { useState } from 'react';
import {Tabs, Form, Input, Button, Space, Spin, Tooltip} from 'antd';
import { EditOutlined, ReloadOutlined } from '@ant-design/icons';
import { ProviderInfo, PaymentKeys, initialProviderInfo, initialPaymentKeys } from '@/pages/ParameterManagement/common';
import { PageContainer } from '@ant-design/pro-layout';
import ProCard from '@ant-design/pro-card';

const { TabPane } = Tabs;

const ProviderInfoForm: React.FC<{
    providerInfo: ProviderInfo,
    onUpdateProviderInfo: (updatedInfo: ProviderInfo) => void,
    editing: boolean, // Add this prop
    onCancelEdit: () => void, // Add this prop
}> = ({ providerInfo, onUpdateProviderInfo, editing, onCancelEdit }) => {
    const [localProviderInfo, setLocalProviderInfo] = useState(providerInfo);

    const handleSave = () => {
        onCancelEdit();
        onUpdateProviderInfo(localProviderInfo);
    };

    const handleChange = (key: keyof ProviderInfo, value: string) => {
        setLocalProviderInfo({ ...localProviderInfo, [key]: value });
    };

    return (
        <Form>
            <Form.Item label="服务商名称">
                {editing ? (
                    <Input value={localProviderInfo.name} onChange={(e) => handleChange('name', e.target.value)} />
                ) : (
                    <span>{providerInfo.name}</span>
                )}
            </Form.Item>
            <Form.Item label="官方链接">
                {editing ? (
                    <Input value={localProviderInfo.officialLink} onChange={(e) => handleChange('officialLink', e.target.value)} />
                ) : (
                    <a href={providerInfo.officialLink} target="_blank" rel="noopener noreferrer">{providerInfo.officialLink}</a>
                )}
            </Form.Item>
            <Form.Item label="服务商简介">
                {editing ? (
                    <Input.TextArea value={localProviderInfo.description} onChange={(e) => handleChange('description', e.target.value)} />
                ) : (
                    <p>{providerInfo.description}</p>
                )}
            </Form.Item>
            {editing ? (
                <Space
                    style={{
                        float: 'right',
                        marginTop: '0px',
                        marginBottom: '24px', // Add this line
                    }}
                >
                    <Button type="primary" onClick={handleSave}>保存</Button>
                    <Button onClick={onCancelEdit}>取消</Button>
                </Space>
            ) : null}
        </Form>
    );
};

const PaymentKeyForm: React.FC<{
    paymentKeys: PaymentKeys,
    onUpdatePaymentKeys: (updatedKeys: PaymentKeys) => void,
    editing: boolean, // Add this prop
    onCancelEdit: () => void, // Add this prop
}> = ({ paymentKeys, onUpdatePaymentKeys, editing, onCancelEdit }) => {
    const [localPaymentKeys, setLocalPaymentKeys] = useState(paymentKeys);

    const handleSave = () => {
        onCancelEdit();
        onUpdatePaymentKeys(localPaymentKeys);
    };

    const handleChange = (key: keyof PaymentKeys, value: string) => {
        setLocalPaymentKeys({ ...localPaymentKeys, [key]: value });
    };

    return (
        <Form>
            <Form.Item label="官方公钥(支付宝)">
                {editing ? (
                    <Input value={localPaymentKeys.alipayPublicKey} onChange={(e) => handleChange('alipayPublicKey', e.target.value)} />
                ) : (
                    <span>{paymentKeys.alipayPublicKey}</span>
                )}
            </Form.Item>
            <Form.Item label="服务商私钥(支付宝)">
                {editing ? (
                    <Input value={localPaymentKeys.alipayPrivateKey} onChange={(e) => handleChange('alipayPrivateKey', e.target.value)} />
                ) : (
                    <span>{paymentKeys.alipayPrivateKey}</span>
                )}
            </Form.Item>
            <Form.Item label="官方公钥(微信)">
                {editing ? (
                    <Input value={localPaymentKeys.wechatPublicKey} onChange={(e) => handleChange('wechatPublicKey', e.target.value)} />
                ) : (
                    <span>{paymentKeys.wechatPublicKey}</span>
                )}
            </Form.Item>
            <Form.Item label="官方私钥(微信)">
                {editing ? (
                    <Input value={localPaymentKeys.wechatPrivateKey} onChange={(e) => handleChange('wechatPrivateKey', e.target.value)} />
                ) : (
                    <span>{paymentKeys.wechatPrivateKey}</span>
                )}
            </Form.Item>
            {editing ? (
                <Space
                    style={{
                        float: 'right',
                        marginTop: '0px',
                        marginBottom: '24px', // Add this line
                    }}
                >
                    <Button type="primary" onClick={handleSave}>保存</Button>
                    <Button onClick={onCancelEdit}>取消</Button>
                </Space>
            ) : null}
        </Form>
    );
};

const ParameterManagement: React.FC = () => {
    const [activeTabKey, setActiveTabKey] = useState<string>('providerInfo');
    const [refreshing, setRefreshing] = useState<boolean>(false);
    const [editing, setEditing] = useState(false);

    const handleTabChange = (key: string) => {
        setActiveTabKey(key);
    };

    const handleRefresh = async () => {
        setRefreshing(true);
        // Simulate data refresh
        await new Promise((resolve) => setTimeout(resolve, 1000));
        setRefreshing(false);
        console.log('Data refreshed.');
    };

    const handleEdit = () => {
        setEditing(true);
        console.log('Edit button clicked');
    };

    const handleCancelEdit = () => {
        setEditing(false);
        console.log('Cancel edit');
    };

    return (
        <PageContainer title={'参数管理'}>
            <ProCard
                bodyStyle={{
                    padding: '24px',
                    paddingBottom: '0px',
                    marginBottom: '0px',
                }}
            >
                <Spin spinning={refreshing}>
                    <Space direction="horizontal" align="end" style={{ float: 'right', marginTop: '16px' }}>
                        {!editing ? (
                            <Tooltip key="edit" title="编辑参数">
                                <a
                                    key="edit"
                                    onClick={() => {
                                        handleEdit();
                                    }}
                                    style={{color: 'inherit'}}
                                >
                                    <EditOutlined/>
                                    <span> 编辑</span>
                                </a>
                            </Tooltip>
                        ) : null}
                        <Tooltip key="refresh" title="刷新参数">
                            <a
                                key="refresh"
                                onClick={() => {
                                    handleRefresh();
                                }}
                                style={{color: 'inherit'}}
                            >
                                <ReloadOutlined/>
                            </a>
                        </Tooltip>
                    </Space>
                    <Tabs activeKey={activeTabKey} onChange={handleTabChange} style={{marginTop: '-24px' }}>
                        <TabPane tab={<span style={{ fontSize: '16px', fontWeight: 'bold' }}>服务商个人信息管理</span>} key="providerInfo">
                            <ProviderInfoForm
                                providerInfo={initialProviderInfo}
                                onUpdateProviderInfo={(updatedInfo) => console.log('Update provider info:', updatedInfo)}
                                editing={editing}
                                onCancelEdit={handleCancelEdit}
                            />
                        </TabPane>
                        <TabPane tab={<span style={{ fontSize: '16px', fontWeight: 'bold' }}>支付密钥管理</span>} key="paymentKeys">
                            <PaymentKeyForm
                                paymentKeys={initialPaymentKeys}
                                onUpdatePaymentKeys={(updatedKeys) => console.log('Update payment keys:', updatedKeys)}
                                editing={editing}
                                onCancelEdit={handleCancelEdit}
                            />
                        </TabPane>
                    </Tabs>
                </Spin>
            </ProCard>
        </PageContainer>
    );
};

export default ParameterManagement;


