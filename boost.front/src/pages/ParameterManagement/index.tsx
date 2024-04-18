import React, {useEffect, useState} from 'react';
import {updateConfigParameter, listConfigParameters} from "@/services/backend/parameterManager";
import {Tabs, Form, Input, Button, Space, Spin, Tooltip, message} from 'antd';
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
                    <Input value={localProviderInfo.providerName} onChange={(e) => handleChange('providerName', e.target.value)} />
                ) : (
                    <span>{providerInfo.providerName}</span>
                )}
            </Form.Item>
            <Form.Item label={<label style={{ fontWeight: 'bold' }}>官方链接</label>}>
                {editing ? (
                    <Input value={localProviderInfo.providerOfficialLink} onChange={(e) => handleChange('providerOfficialLink', e.target.value)} />
                ) : (
                    <a href={"https://computenest.console.aliyun.com/"} target="_blank" rel="noopener noreferrer">{providerInfo.providerOfficialLink}</a>
                )}
            </Form.Item>
            <Form.Item label={<label style={{ fontWeight: 'bold' }}>服务商简介</label>}>
                {editing ? (
                    <Input.TextArea value={localProviderInfo.providerDescription} onChange={(e) => handleChange('providerDescription', e.target.value)} />
                ) : (
                    <p>{providerInfo.providerDescription}</p>
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

    return (
        <Form>
            <Form.Item label={<label style={{ fontWeight: 'bold' }}>官方公钥(支付宝)</label>}>
                {editing ? (
                    <Input value={localPaymentKeys.alipayPublicKey} onChange={(e) => handleChange('alipayPublicKey', e.target.value)} />
                ) : (
                    privateKeysVisible ? (
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
                    privateKeysVisible ? (
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
                    privateKeysVisible ? (
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
                    privateKeysVisible ? (
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
    const [privateKeysVisible, setIsPrivateKeysVisible] = useState<boolean>(false);
    const [providerInfo, setProviderInfo] = useState<ProviderInfo>(initialProviderInfo);
    const [paymentKeys, setPaymentKeys] = useState<PaymentKeys>(initialPaymentKeys);

    useEffect(() => {
        (async () => {
            if (activeTabKey == 'providerInfo'){
                await loadProviderInfo();
            } else if (activeTabKey == 'paymentKeys'){
                await loadPaymentKeys();
            }
        })();
    }, [activeTabKey]);

    const handleTabChange = (key: string) => {
        setActiveTabKey(key);
    };

    const handleRefresh = async () => {
        setRefreshing(true);
        if (activeTabKey == 'providerInfo'){
            await loadProviderInfo();
        } else if (activeTabKey == 'paymentKeys'){
            await loadPaymentKeys();
        }
        setRefreshing(false);
    };

    const handleEdit = () => {
        setEditing(true);
    };

    const handleCancelEdit = () => {
        setEditing(false);
    };

    const handleTogglePrivateKeysVisibility = () => {
        setIsPrivateKeysVisible(!privateKeysVisible);
    };

    const loadProviderInfo = () => {
        listConfigParameters({
            name: initialProviderInfoNameList,
            encrypted: initialProviderInfoEncryptedList,
        }).then((result) => {
            onListProviderInfo(result)
            console.log(result);
        });
    };

    const loadPaymentKeys = () => {
        listConfigParameters({
            name: initialPaymentKeysNameList,
            encrypted: initialPaymentKeysEncryptedList,
        }).then((result) => {
            onListPaymentKeys(result)
            console.log(result);
        });
    }

    const onUpdateProviderInfo = (updatedInfo: ProviderInfo) => {
        const providerInfoKeys = Object.keys(updatedInfo) as Array<keyof ProviderInfo>;
        providerInfoKeys.forEach(field => {
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
        paymentKeysKeys.forEach(field => {
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

    const onListProviderInfo = (result: API.BaseResult) => {
        if (
            result != undefined &&
            result.data != undefined &&
            result.data.configParameterModels != undefined &&
            result.data.configParameterModels.length > 0
        ) {
            const [listProviderInfo, setListProviderInfo] = useState<ProviderInfo>(initialProviderInfo);

            result.data.configParameterModels.forEach((configParam: API.ConfigParameterModel) => {
                const { name, id } = configParam;

                if (name != undefined && name in listProviderInfo) {
                    setListProviderInfo((prevProviderInfo) => ({
                        ...prevProviderInfo,
                        [name]: id,
                    }));
                }
            });
        }
    };

    const onListPaymentKeys = (result: API.BaseResult) => {
        if (
            result != undefined &&
            result.data != undefined &&
            result.data.configParameterModels != undefined &&
            result.data.configParameterModels.length > 0
        ) {
            const [listPaymentKeys, setListPaymentKeys] = useState<PaymentKeys>(initialPaymentKeys);
            result.data.configParameterModels.forEach((configParam: API.ConfigParameterModel) => {
                const { name, id } = configParam;
                if (name != undefined && name in listPaymentKeys) {
                    setListPaymentKeys((prevPaymentKeys) => ({
                        ...prevPaymentKeys,
                        [name]: id,
                    }));
                }
            });
        }
    }

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
                            privateKeysVisible ? (
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
                                    privateKeysVisible={privateKeysVisible}
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