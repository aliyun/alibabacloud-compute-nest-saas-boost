import React, { useState } from 'react';
import {Tabs, Form, Input, Button, Space, Spin, Tooltip} from 'antd';
import {EditOutlined, EyeInvisibleOutlined, EyeOutlined, ReloadOutlined} from '@ant-design/icons';
import { ProviderInfo, PaymentKeys} from '@/pages/ParameterManagement/component/interface';
import { initialProviderInfo, initialPaymentKeys } from '@/pages/ParameterManagement/common';
import { PageContainer } from '@ant-design/pro-layout';
import ProCard from '@ant-design/pro-card';

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

    const handleCancel = () => {
        onCancelEdit();
        setLocalProviderInfo(providerInfo);
    };

    const handleChange = (key: keyof ProviderInfo, value: string) => {
        setLocalProviderInfo({ ...localProviderInfo, [key]: value });
    };

    return (
        <Form>
            <Form.Item label={<label style={{ fontWeight: 'bold' }}>服务商名称</label>}>
                {editing ? (
                    <Input value={localProviderInfo.name} onChange={(e) => handleChange('name', e.target.value)} />
                ) : (
                    <span>{providerInfo.name}</span>
                )}
            </Form.Item>
            <Form.Item label={<label style={{ fontWeight: 'bold' }}>官方链接</label>}>
                {editing ? (
                    <Input value={localProviderInfo.officialLink} onChange={(e) => handleChange('officialLink', e.target.value)} />
                ) : (
                    <a href={"https://computenest.console.aliyun.com/"} target="_blank" rel="noopener noreferrer">{providerInfo.officialLink}</a>
                )}
            </Form.Item>
            <Form.Item label={<label style={{ fontWeight: 'bold' }}>服务商简介</label>}>
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
                    <Button onClick={handleCancel}>取消</Button>
                </Space>
            ) : null}
        </Form>
    );
};

const PaymentKeyForm: React.FC<{
    paymentKeys: PaymentKeys,
    onUpdatePaymentKeys: (updatedKeys: PaymentKeys) => void,
    editing: boolean, // Add this prop
    isPrivateKeysVisible: boolean,
    onCancelEdit: () => void, // Add this prop
}> = ({ paymentKeys, onUpdatePaymentKeys, editing, isPrivateKeysVisible, onCancelEdit }) => {
    const [localPaymentKeys, setLocalPaymentKeys] = useState(paymentKeys);

    const handleSave = () => {
        onCancelEdit();
        onUpdatePaymentKeys(localPaymentKeys);
    };

    const handleCancel = () => {
        onCancelEdit();
        setLocalPaymentKeys(paymentKeys);
    };

    const handleChange = (key: keyof PaymentKeys, value: string) => {
        setLocalPaymentKeys({ ...localPaymentKeys, [key]: value });
    };

    return (
        <Form>
            <Form.Item label={<label style={{ fontWeight: 'bold' }}>官方公钥(支付宝)</label>}>
                {editing ? (
                    <Input value={localPaymentKeys.alipayPublicKey} onChange={(e) => handleChange('alipayPublicKey', e.target.value)} />
                ) : (
                    isPrivateKeysVisible ? (
                        <span>{paymentKeys.alipayPublicKey}</span>
                    ) : (
                        <span>********************</span>
                    )
                )}
            </Form.Item>
            <Form.Item label={<label style={{ fontWeight: 'bold' }}>服务商私钥(支付宝)</label>}>
                {editing ? (
                    <Input value={localPaymentKeys.alipayPrivateKey} onChange={(e) => handleChange('alipayPrivateKey', e.target.value)} />
                ) : (
                    isPrivateKeysVisible ? (
                        <span>{paymentKeys.alipayPrivateKey}</span>
                    ) : (
                        <span>********************</span>
                    )
                )}
            </Form.Item>
            <Form.Item label={<label style={{ fontWeight: 'bold' }}>官方公钥(微信)</label>}>
                {editing ? (
                    <Input value={localPaymentKeys.wechatPublicKey} onChange={(e) => handleChange('wechatPublicKey', e.target.value)} />
                ) : (
                    isPrivateKeysVisible ? (
                        <span>{paymentKeys.wechatPublicKey}</span>
                    ) : (
                        <span>********************</span>
                    )
                )}
            </Form.Item>
            <Form.Item label={<label style={{ fontWeight: 'bold' }}>官方私钥(微信)</label>}>
                {editing ? (
                    <Input value={localPaymentKeys.wechatPrivateKey} onChange={(e) => handleChange('wechatPrivateKey', e.target.value)} />
                ) : (
                    isPrivateKeysVisible ? (
                        <span>{paymentKeys.wechatPrivateKey}</span>
                    ) : (
                        <span>********************</span>
                    )
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
                    <Button onClick={handleCancel}>取消</Button>
                </Space>
            ) : null}
        </Form>
    );
};

const ParameterManagement: React.FC = () => {
    const [activeTabKey, setActiveTabKey] = useState<string>('providerInfo');
    const [refreshing, setRefreshing] = useState<boolean>(false);
    const [editing, setEditing] = useState(false);
    const [isPrivateKeysVisible, setIsPrivateKeysVisible] = useState<boolean>(false);
    const [providerInfo, setProviderInfo] = useState<ProviderInfo>(initialProviderInfo);
    const [paymentKeys, setPaymentKeys] = useState<PaymentKeys>(initialPaymentKeys);

    const handleTabChange = (key: string) => {
        setActiveTabKey(key);
    };

    const handleRefresh = async () => {
        setRefreshing(true);
        // Simulate data refresh
        await new Promise((resolve) => setTimeout(resolve, 1000));
        setRefreshing(false);
    };

    const handleEdit = () => {
        setEditing(true);
    };

    const handleCancelEdit = () => {
        setEditing(false);
    };

    const handleTogglePrivateKeysVisibility = () => {
        setIsPrivateKeysVisible(!isPrivateKeysVisible);
    }

    const onUpdateProviderInfo = (updatedInfo: ProviderInfo) => {
        setProviderInfo(updatedInfo);
    };

    const onUpdatePaymentKeys = (updatedKeys: PaymentKeys) => {
        setPaymentKeys(updatedKeys);
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
                        {activeTabKey == 'paymentKeys' ? (
                            isPrivateKeysVisible ? (
                                <Tooltip key="hidePrivateKeys" title="隐藏加密参数">
                                    <a
                                        key="hidePrivateKeys"
                                        onClick={() => {
                                            handleTogglePrivateKeysVisibility();
                                        }}
                                        style={{ color: 'inherit' }}
                                    >
                                        <EyeInvisibleOutlined/>
                                    </a>
                                </Tooltip>
                            ) : (
                                <Tooltip key="showPrivateKeys" title="显示加密参数">
                                    <a
                                        key="showPrivateKeys"
                                        onClick={() => {
                                            handleTogglePrivateKeysVisibility();
                                        }}
                                        style={{ color: 'inherit' }}
                                    >
                                        <EyeOutlined/>
                                    </a>
                                </Tooltip>
                            )
                        ):null}
                    </Space>
                    <Tabs activeKey={activeTabKey} onChange={handleTabChange} style={{ marginTop: '-24px' }} items={[
                        {
                            key: 'providerInfo',
                            label: <span style={{ fontSize: '16px', fontWeight: 'bold' }}>服务商个人信息管理</span>,
                            children: (
                                <ProviderInfoForm
                                    providerInfo={providerInfo}
                                    onUpdateProviderInfo={onUpdateProviderInfo}
                                    editing={editing}
                                    onCancelEdit={handleCancelEdit}
                                />
                            ),
                        },
                        {
                            key: 'paymentKeys',
                            label: <span style={{ fontSize: '16px', fontWeight: 'bold' }}>支付密钥管理</span>,
                            children: (
                                <PaymentKeyForm
                                    paymentKeys={paymentKeys}
                                    onUpdatePaymentKeys={onUpdatePaymentKeys}
                                    editing={editing}
                                    isPrivateKeysVisible={isPrivateKeysVisible}
                                    onCancelEdit={handleCancelEdit}
                                />
                            ),
                        },
                    ]} />
                </Spin>
            </ProCard>
        </PageContainer>
    );
};

export default ParameterManagement;