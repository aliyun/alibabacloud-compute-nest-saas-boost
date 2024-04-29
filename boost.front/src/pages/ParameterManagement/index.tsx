import React, {useEffect, useState} from 'react';
import {updateConfigParameter, listConfigParameters} from "@/services/backend/parameterManager";
import {Tabs, Space, Spin, Tooltip, message} from 'antd';
import {EditOutlined, EyeInvisibleOutlined, EyeOutlined, ReloadOutlined} from '@ant-design/icons';
import { ProviderInfo, AlipayPaymentKeys, WechatPayPaymentKeys} from '@/pages/Parameter/component/interface';
import {
    initialProviderInfo,
    initialAlipayPaymentKeys,
    initialWechatPayPaymentKeys,
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
    setProviderLogoUrl,
} from "@/store/providerInfo/actions";
import {ProviderInfoForm} from '@/pages/Parameter/ProviderInfo'
import {AlipayPaymentKeyForm} from "@/pages/Parameter/Alipay";
import {WechatPayPaymentKeyForm} from "@/pages/Parameter/WechatPay";

import {FormattedMessage} from "@@/exports";

const ParameterManagement: React.FC = () => {
    const [activeTabKey, setActiveTabKey] = useState<string>('ProviderInfo');
    const [activePaymentMethodKey, setActivePaymentMethodKey] = useState<string>('Alipay');
    const [refreshing, setRefreshing] = useState(false);
    const [editing, setEditing] = useState(false);
    const [privateKeysVisible, setIsPrivateKeysVisible] = useState(false);
    const [providerInfo, setProviderInfo] = useState<ProviderInfo>(initialProviderInfo);
    const [alipayPaymentKeys, setAlipayPaymentKeys] = useState<AlipayPaymentKeys>(initialAlipayPaymentKeys);
    const [wechatPayPaymentKeys, setWechatPayPaymentKeys] = useState<WechatPayPaymentKeys>(initialWechatPayPaymentKeys);
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
            } else if (parameterNames === initialPaymentKeysNameList.wechatPay) {
                setWechatPayPaymentKeys(configParams as WechatPayPaymentKeys);
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
        if (providerInfo.ProviderDescription) {
            dispatch(setProviderLogoUrl(providerInfo.ProviderLogoUrl));
        }
    }, [providerInfo, dispatch]);

    useEffect(() => {
        handleRefresh();
    }, [activeTabKey, activePaymentMethodKey]);

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
        if (activeTabKey === 'ProviderInfo') {
            await loadConfigParameters(initialProviderInfoNameList, initialProviderInfoEncryptedList);
        } else if (activeTabKey === 'PaymentManagement' && activePaymentMethodKey === 'Alipay') {
            await loadConfigParameters(initialPaymentKeysNameList.alipay, initialPaymentKeysEncryptedList.alipay);
        } else if (activeTabKey === 'PaymentManagement' && activePaymentMethodKey === 'WechatPay') {
            await loadConfigParameters(initialPaymentKeysNameList.wechatPay, initialPaymentKeysEncryptedList.wechatPay);
        }
    }

    const onUpdateProviderInfo = (updatedInfo: ProviderInfo) => {
        const providerInfoKeys = Object.keys(updatedInfo) as Array<keyof ProviderInfo>;
        try {
            providerInfoKeys.forEach((field) => {
                if (updatedInfo[field] !== providerInfo[field]) {
                    updateConfigParameter({
                        name: field.valueOf(),
                        value: updatedInfo[field]? updatedInfo[field] : 'waitToConfig',
                        encrypted: encryptedCredentialsMap[field],
                        tag: "ProviderInfo",
                    }).then((result) => {
                        console.log(result);
                        setProviderInfo(updatedInfo);
                    });
                }
            });
            message.success('个人信息更新成功');
        } catch (e) {
            message.error('个人信息更新失败');
            console.log(e);
        }

    };

    const onUpdateAlipayPaymentKeys = (updatedKeys: AlipayPaymentKeys) => {
        const alipayKeys = Object.keys(updatedKeys) as Array<keyof AlipayPaymentKeys>;
        try {
            alipayKeys.forEach((field) => {
                if (updatedKeys[field] !== alipayPaymentKeys[field]) {
                    updateConfigParameter({
                        name: field.valueOf(),
                        value: updatedKeys[field]? updatedKeys[field] : 'waitToConfig',
                        encrypted: encryptedCredentialsMap[field],
                        tag: "Alipay",
                    }).then((result) => {
                        setAlipayPaymentKeys(updatedKeys);
                        console.log(result);
                    });
                }
            });
            message.success('支付宝参数更新成功');
        } catch (e) {
            message.error('支付宝参数更新失败');
        }
    };

    const onUpdateWechatPayPaymentKeys = (updatedKeys: WechatPayPaymentKeys) => {
        const wechatKeys = Object.keys(updatedKeys) as Array<keyof WechatPayPaymentKeys>;
        try {
            wechatKeys.forEach((field) => {
                if (updatedKeys[field] !== wechatPayPaymentKeys[field]) {

                    updateConfigParameter({
                        name: field.valueOf() as string,
                        value: updatedKeys[field]? updatedKeys[field] : 'waitToConfig',
                        encrypted: encryptedCredentialsMap[field],
                        tag: "WechatPay",
                    }).then((result) => {
                        setWechatPayPaymentKeys(updatedKeys);

                        console.log(result);
                    });
                }
            });
            message.success('微信支付参数更新成功');
        } catch (e) {
            message.error('微信支付参数更新失败');
        }
    };


    return (
        <PageContainer title={<FormattedMessage id="menu.parameter-management" defaultMessage="参数管理"/>}>
            <ProCard
                bodyStyle={{
                    padding: '24px',
                    paddingBottom: '0px',
                    marginBottom: '0px',
                }}
            >
                <Spin spinning={refreshing}>
                    <Space direction="horizontal" align="end" style={{ float: 'right', marginTop: '16px' }}>
                        <Tooltip key="help" title="查看帮助文档">
                            {/* 使用 `window.open` 在新标签打开帮助文档 */}
                            <a
                                key="help"
                                onClick={() => window.open('https://computenest.console.aliyun.com/service/detail/' +
                                    'cn-hangzhou/service-fd1aec438c974828bb7b/10?isInstance=true', '_blank')}
                                style={{ color: 'inherit', marginRight: '16px' }}
                            >
                                帮助文档
                            </a>
                        </Tooltip>
                        {!editing ? (
                            <Tooltip key="edit" title="编辑参数">
                                <a key="edit" onClick={handleEdit} style={{ color: 'inherit' }}>
                                    <EditOutlined/>
                                    <span> <FormattedMessage id="button.edit" defaultMessage="编辑"/></span>
                                </a>
                            </Tooltip>
                        ) : null}
                        <Tooltip key="refresh" title="刷新参数">
                            <a key="refresh" onClick={handleRefresh} style={{ color: 'inherit' }}>
                                <ReloadOutlined />
                            </a>
                        </Tooltip>
                        {activeTabKey === 'PaymentManagement' ? (
                            privateKeysVisible ? (
                                <Tooltip key="hidePrivateKeys" title="隐藏加密参数">
                                    <a key="hidePrivateKeys" onClick={handleTogglePrivateKeysVisibility}
                                       style={{ color: 'inherit' }}>
                                        <EyeInvisibleOutlined />
                                    </a>
                                </Tooltip>
                            ) : (
                                <Tooltip key="showPrivateKeys" title="显示加密参数">
                                    <a key="showPrivateKeys" onClick={handleTogglePrivateKeysVisibility}
                                       style={{ color: 'inherit' }}>
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
                                key: 'ProviderInfo',
                                label: <span style={{ fontSize: '16px', fontWeight: 'bold' }}>
                                    <FormattedMessage id="pages.parameterManagementTabs.supplier-personal-information-management"
                                                      defaultMessage="服务商个人信息管理"/></span>,
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
                                key: 'PaymentManagement',
                                label: <span style={{ fontSize: '16px', fontWeight: 'bold' }}>
                                    <FormattedMessage id="pages.parameterManagementTabs.payment-key-management"
                                                      defaultMessage="支付密钥管理"/></span>,
                                children: (
                                    <ProCard
                                        tabs={{
                                            type: 'card',
                                            activeKey: activePaymentMethodKey,
                                            onChange: (key) => setActivePaymentMethodKey(key),
                                        }}
                                    >
                                        <ProCard.TabPane key="Alipay" tab={<span style={{ fontSize: '16px', fontWeight: 'bold' }}>支付宝</span>}>
                                            <AlipayPaymentKeyForm
                                                alipayPaymentKeys={alipayPaymentKeys}
                                                onUpdateAlipayPaymentKeys={onUpdateAlipayPaymentKeys}
                                                editing={editing}
                                                privateKeysVisible={privateKeysVisible}
                                                onCancelEdit={handleCancelEdit}
                                            />
                                        </ProCard.TabPane>
                                        <ProCard.TabPane key="WechatPay" tab={<span style={{ fontSize: '16px', fontWeight: 'bold' }}>微信支付</span>}>
                                            <WechatPayPaymentKeyForm
                                                wechatPayPaymentKeys={wechatPayPaymentKeys}
                                                onUpdateWechatPayPaymentKeys={onUpdateWechatPayPaymentKeys}
                                                editing={editing}
                                                privateKeysVisible={privateKeysVisible}
                                                onCancelEdit={handleCancelEdit}
                                            />
                                        </ProCard.TabPane>
                                    </ProCard>
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