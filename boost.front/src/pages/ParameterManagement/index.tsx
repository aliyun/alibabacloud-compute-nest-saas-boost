import React, {useEffect, useState} from 'react';
import {updateConfigParameter, listConfigParameters} from "@/services/backend/parameterManager";
import {Tabs, Button, Space, Spin, Tooltip, message, Row, Col} from 'antd';
import {EditOutlined, EyeInvisibleOutlined, EyeOutlined, ReloadOutlined} from '@ant-design/icons';
import { ProviderInfo, PaymentKeys} from '@/pages/ParameterManagement/component/interface';
import {
    initialProviderInfo,
    initialPaymentKeys,
    initialProviderInfoNameList,
    initialProviderInfoEncryptedList,
    initialPaymentKeysNameList,
    initialPaymentKeysEncryptedList, encryptedCredentialsMap,

} from '@/pages/ParameterManagement/common';
import { PageContainer } from '@ant-design/pro-layout';
import ProCard from '@ant-design/pro-card';
import {ProFormText, ProForm, ProFormTextArea} from '@ant-design/pro-form';

const ActionButtons: React.FC<{ onSave: () => void; onCancel: () => void }> = ({ onSave, onCancel }) => (
    <Row justify="end" style={{ marginTop: '0px', marginBottom: '24px' }}>
        <Col>
            <Button type="primary" onClick={onSave}>
                保存
            </Button>
            <Button onClick={onCancel}>取消</Button>
        </Col>
    </Row>
);

const ProviderInfoForm: React.FC<{
    providerInfo: ProviderInfo,
    onUpdateProviderInfo: (updatedInfo: ProviderInfo) => void,
    editing: boolean,
    onCancelEdit: () => void,
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

    const getFieldProps = (key: keyof ProviderInfo, label: string, placeholder: string) => ({
        label: <label style={{ fontWeight: 'bold' }}>{label}</label>,
        initialValue: localProviderInfo[key],
        placeholder: placeholder,
        fieldProps: {
            disabled: !editing,
            onChange: (e: React.ChangeEvent<HTMLInputElement | HTMLTextAreaElement>) => {
                handleChange(key, e.target.value);
            },
        },
    });

    return (
        <ProForm
            layout="vertical"
            colon={false}
        >
            <ProFormText {...getFieldProps('providerName', '服务商名称', '请输入服务商名称')} />
            <ProFormText {...getFieldProps('providerOfficialLink', '官方链接', '请输入服务商官方链接')} />
            <ProFormTextArea {...getFieldProps('providerDescription', '服务商简介', '请输入服务商简介')} />

            {editing && <ActionButtons onSave={handleSave} onCancel={handleCancel} />}
        </ProForm>
    );
};

const PaymentKeyForm: React.FC<{
    paymentKeys: PaymentKeys,
    onUpdatePaymentKeys: (updatedKeys: PaymentKeys) => void,
    editing: boolean, // Add this prop
    privateKeysVisible: boolean,
    onCancelEdit: () => void, // Add this prop
}> = ({ paymentKeys, onUpdatePaymentKeys, editing, privateKeysVisible, onCancelEdit }) => {
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

    const getFieldProps = (
        key: keyof PaymentKeys,
        label: string,
        placeholder: string,
    ) => ({
        label: <label style={{ fontWeight: 'bold' }}>{label}</label>,
        placeholder: placeholder,
        initialValue: localPaymentKeys[key],
        fieldProps: {
            disabled: !editing,
            type: editing && privateKeysVisible ? 'text' : 'password',
            onChange: (e: React.ChangeEvent<HTMLInputElement>) => {
                handleChange(key, e.target.value);
            },
        },
    });

    return (
        <ProForm
            layout="vertical"
            colon={false}
        >
            <ProFormText {...getFieldProps('alipayPublicKey', '官方公钥(支付宝)', '请输入官方公钥(支付宝)')} />
            <ProFormText {...getFieldProps('alipayPrivateKey', '服务商私钥(支付宝)', '请输入服务商私钥(支付宝)')} />
            <ProFormText {...getFieldProps('wechatPublicKey', '官方公钥(微信)', '请输入官方公钥(微信)')} />
            <ProFormText {...getFieldProps('wechatPrivateKey', '服务商私钥(微信)', '请输入服务商私钥(微信)')} />

            {editing && <ActionButtons onSave={handleSave} onCancel={handleCancel} />}
        </ProForm>
    );
};

const ParameterManagement: React.FC = () => {
    const [activeTabKey, setActiveTabKey] = useState<string>('providerInfo');
    const [refreshing, setRefreshing] = useState(false);
    const [editing, setEditing] = useState(false);
    const [privateKeysVisible, setIsPrivateKeysVisible] = useState(false);
    const [providerInfo, setProviderInfo] = useState<ProviderInfo>(initialProviderInfo);
    const [paymentKeys, setPaymentKeys] = useState<PaymentKeys>(initialPaymentKeys);

    const loadConfigParameters = async (parameterNames: string[], encrypted: boolean[]) => {
        const result = await listConfigParameters({ name: parameterNames, encrypted });
        if (
            result.data && result.data.configParameterModels
        ) {
            if (result.data.configParameterModels.length > 0) {
                const configParams = result.data.configParameterModels.reduce(
                    (acc, configParam) => ({
                        ...acc,
                        [configParam.name as string]: configParam.id,
                    }),
                    {}
                );

                if (parameterNames === initialProviderInfoNameList) {
                    setProviderInfo(configParams as ProviderInfo);
                } else if (parameterNames === initialPaymentKeysNameList) {
                    setPaymentKeys(configParams as PaymentKeys);
                }
            }

        }
    };

    useEffect(() => {
        fetchData();
    }, [activeTabKey]);

    const handleTabChange = (key: string) => setActiveTabKey(key);
    const handleRefresh = async () => {
        setRefreshing(true);
        await fetchData();
        setRefreshing(false);
    };
    const handleEdit = () => setEditing(true);
    const handleCancelEdit = () => setEditing(false);
    const handleTogglePrivateKeysVisibility = () => setIsPrivateKeysVisible(!privateKeysVisible);

    const fetchData = async () =>  {
        if (activeTabKey === 'providerInfo') {
            await loadConfigParameters(initialProviderInfoNameList, initialProviderInfoEncryptedList);
        } else if (activeTabKey === 'paymentKeys') {
            await loadConfigParameters(initialPaymentKeysNameList, initialPaymentKeysEncryptedList);
        }
    }

    const onUpdateProviderInfo = (updatedInfo: ProviderInfo) => {
        const providerInfoKeys = Object.keys(updatedInfo) as Array<keyof ProviderInfo>;
        providerInfoKeys.forEach((field) => {
            if (updatedInfo[field] !== providerInfo[field]) {
                try {
                    updateConfigParameter({
                        name: updatedInfo[field],
                        encrypted: encryptedCredentialsMap[field],
                    }).then((result) => {
                        setProviderInfo(updatedInfo);
                        message.success('个人信息更新成功');
                        console.log(result);
                    });
                } catch (e) {
                    message.error('个人信息更新失败');
                    console.log(e);
                }
            }
        });
    };

    const onUpdatePaymentKeys = (updatedKeys: PaymentKeys) => {
        const paymentKeysKeys = Object.keys(updatedKeys) as Array<keyof PaymentKeys>;
        paymentKeysKeys.forEach((field) => {
            if (updatedKeys[field] !== paymentKeys[field]) {
                try {
                    updateConfigParameter({
                        name: updatedKeys[field],
                        encrypted: encryptedCredentialsMap[field],
                    }).then((result) => {
                        setPaymentKeys(updatedKeys);
                        message.success('支付密钥更新成功');
                        console.log(result);
                    });
                } catch (e) {
                    message.error('支付密钥更新失败');
                }
            }
        });
    };

    return (
        <PageContainer title="参数管理">
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
                                <a key="edit" onClick={handleEdit} style={{ color: 'inherit' }}>
                                    <EditOutlined /> 编辑
                                </a>
                            </Tooltip>
                        ) : null}
                        <Tooltip key="refresh" title="刷新参数">
                            <a key="refresh" onClick={handleRefresh} style={{ color: 'inherit' }}>
                                <ReloadOutlined />
                            </a>
                        </Tooltip>
                        {activeTabKey === 'paymentKeys' ? (
                            privateKeysVisible ? (
                                <Tooltip key="hidePrivateKeys" title="隐藏加密参数">
                                    <a key="hidePrivateKeys" onClick={handleTogglePrivateKeysVisibility} style={{ color: 'inherit' }}>
                                        <EyeInvisibleOutlined />
                                    </a>
                                </Tooltip>
                            ) : (
                                <Tooltip key="showPrivateKeys" title="显示加密参数">
                                    <a key="showPrivateKeys" onClick={handleTogglePrivateKeysVisibility} style={{ color: 'inherit' }}>
                                        <EyeOutlined />
                                    </a>
                                </Tooltip>
                            )
                        ) : null}
                    </Space>
                    <Tabs
                        activeKey={activeTabKey}
                        onChange={handleTabChange}
                        style={{ marginTop: '-24px' }}
                        items={[
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
                                        privateKeysVisible={privateKeysVisible}
                                        onCancelEdit={handleCancelEdit}
                                    />
                                ),
                            },
                        ]}
                    />
                </Spin>
            </ProCard>
        </PageContainer>
    );
};

export default ParameterManagement;