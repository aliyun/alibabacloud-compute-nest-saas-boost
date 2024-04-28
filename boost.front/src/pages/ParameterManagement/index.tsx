import React, {useEffect, useState} from 'react';
import {updateConfigParameter, listConfigParameters} from "@/services/backend/parameterManager";
import {Tabs, Space, Spin, Tooltip, message} from 'antd';
import {EditOutlined, EyeInvisibleOutlined, EyeOutlined, ReloadOutlined} from '@ant-design/icons';
import { ProviderInfo, AlipayPaymentKeys, WechatPaymentKeys} from '@/pages/Parameter/component/interface';
import {
    initialProviderInfo,
    initialAlipayPaymentKeys,
    initialWechatPaymentKeys,
    initialProviderInfoNameList,
    initialProviderInfoEncryptedList,
    initialPaymentKeysNameList,
    initialPaymentKeysEncryptedList, encryptedCredentialsMap,
} from '@/pages/Parameter/common';
import { PageContainer } from '@ant-design/pro-layout';
import ProCard from '@ant-design/pro-card';
import { useDispatch } from 'react-redux';
import {
    setProviderName,
    setProviderOfficialLink,
    setProviderDescription,
} from "@/store/providerInfo/actions";
import {ProviderInfoForm} from '@/pages/Parameter/ProviderInfo'
import {AlipayPaymentKeyForm} from "@/pages/Parameter/Alipay";
import {WechatPaymentKeyForm} from "@/pages/Parameter/Wechat";

const ParameterManagement: React.FC = () => {
    const [activeTabKey, setActiveTabKey] = useState<string>('providerInfo');
    const [refreshing, setRefreshing] = useState(false);
    const [editing, setEditing] = useState(false);
    const [privateKeysVisible, setIsPrivateKeysVisible] = useState(false);
    const [providerInfo, setProviderInfo] = useState<ProviderInfo>(initialProviderInfo);
    const [alipayPaymentKeys, setAlipayPaymentKeys] = useState<AlipayPaymentKeys>(initialAlipayPaymentKeys);
    const [wechatPaymentKeys, setWechatPaymentKeys] = useState<WechatPaymentKeys>(initialWechatPaymentKeys);
    const loadConfigParameters = async (parameterNames: string[], encrypted: boolean[]) => {
        const configParameterQueryModels: API.ConfigParameterQueryModel[] = parameterNames.map((name, index) => ({
            name,
            encrypted: encrypted[index],
        }));

        const listParams: API.ListConfigParametersParam = {
            configParameterQueryModels,
        };

        const result: API.ListResultConfigParameterModel_ = await listConfigParameters(listParams);
        if (
            result.data && result.data.length > 0
        ) {
            const configParams = result.data.reduce(
                (acc, configParam) => ({
                    ...acc,
                    [configParam.name as string]: configParam.value === 'waitToConfig'? '' : configParam.value,
                }),
                {}
            );
            if (parameterNames === initialProviderInfoNameList) {
                setProviderInfo(configParams as ProviderInfo);
            } else if (parameterNames === initialPaymentKeysNameList.alipay) {
                setAlipayPaymentKeys(configParams as AlipayPaymentKeys);
            } else if (parameterNames === initialPaymentKeysNameList.wechat) {
                setWechatPaymentKeys(configParams as WechatPaymentKeys);
            }
        }
    };

    const dispatch = useDispatch();

    useEffect(() => {
        if (providerInfo.ProviderName) {
            dispatch(setProviderName(providerInfo.ProviderName));
        }

        if (providerInfo.ProviderOfficialLink) {
            dispatch(setProviderOfficialLink(providerInfo.ProviderOfficialLink));
        }

        if (providerInfo.ProviderDescription) {
            dispatch(setProviderDescription(providerInfo.ProviderDescription));
        }
    }, [providerInfo, dispatch]);

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
        } else if (activeTabKey === 'alipayPaymentKeys') {
            await loadConfigParameters(initialPaymentKeysNameList.alipay, initialPaymentKeysEncryptedList.alipay);
        } else if (activeTabKey === 'wechatPaymentKeys') {
            await loadConfigParameters(initialPaymentKeysNameList.wechat, initialPaymentKeysEncryptedList.wechat);
        }
    }

    const onUpdateProviderInfo = (updatedInfo: ProviderInfo) => {
        const providerInfoKeys = Object.keys(updatedInfo) as Array<keyof ProviderInfo>;
        providerInfoKeys.forEach((field) => {
            if (updatedInfo[field] !== providerInfo[field]) {
                try {
                    updateConfigParameter({
                        name: field.valueOf(),
                        value: updatedInfo[field],
                        encrypted: encryptedCredentialsMap[field],
                    }).then((result) => {
                        console.log(result);
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

    const onUpdateAlipayPaymentKeys = (updatedKeys: AlipayPaymentKeys) => {
        const alipayKeys = Object.keys(updatedKeys) as Array<keyof AlipayPaymentKeys>;
        alipayKeys.forEach((field) => {
            if (updatedKeys[field] !== alipayPaymentKeys[field]) {
                try {
                    updateConfigParameter({
                        name: field.valueOf(),
                        value: updatedKeys[field],
                        encrypted: encryptedCredentialsMap[field],
                    }).then((result) => {
                        setAlipayPaymentKeys(updatedKeys);
                        message.success('支付宝参数更新成功');
                        console.log(result);
                    });
                } catch (e) {
                    message.error('支付宝参数更新失败');
                }
            }
        });
    };

    const onUpdateWechatPaymentKeys = (updatedKeys: WechatPaymentKeys) => {
        const wechatKeys = Object.keys(updatedKeys) as Array<keyof WechatPaymentKeys>;
        wechatKeys.forEach((field) => {
            if (updatedKeys[field] !== wechatPaymentKeys[field]) {
                try {
                    updateConfigParameter({
                        name: field.valueOf(),
                        value: updatedKeys[field],
                        encrypted: encryptedCredentialsMap[field],
                    }).then((result) => {
                        setWechatPaymentKeys(updatedKeys); // 假设这是一个 useState 的 setter 函数
                        message.success('微信参数更新成功');
                        console.log(result);
                    });
                } catch (e) {
                    message.error('微信参数更新失败');
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
                        {activeTabKey === 'alipayPaymentKeys' || activeTabKey === 'wechatPaymentKeys' ? (
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
                                key: 'alipayPaymentKeys',
                                label: <span style={{ fontSize: '16px', fontWeight: 'bold' }}>支付宝参数管理</span>,
                                children: (
                                    <AlipayPaymentKeyForm
                                        alipayPaymentKeys={alipayPaymentKeys}
                                        onUpdateAlipayPaymentKeys={onUpdateAlipayPaymentKeys}
                                        editing={editing}
                                        privateKeysVisible={privateKeysVisible}
                                        onCancelEdit={handleCancelEdit}
                                    />
                                ),
                            },
                            {
                                key: 'wechatPaymentKeys',
                                label: <span style={{ fontSize: '16px', fontWeight: 'bold' }}>微信参数管理</span>,
                                children: (
                                    <WechatPaymentKeyForm
                                        wechatPaymentKeys={wechatPaymentKeys}
                                        onUpdateWechatPaymentKeys={onUpdateWechatPaymentKeys}
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