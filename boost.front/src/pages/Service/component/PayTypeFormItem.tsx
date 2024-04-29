import React, {useEffect, useState} from "react";
import {ProFormRadio} from "@ant-design/pro-form";
import {Space, Spin} from "antd";
import { PayChannelEnum } from "@/constants";
import { AlipayCircleOutlined, WechatOutlined } from "@ant-design/icons";
import { FormattedMessage } from "@@/exports";
import {listConfigParameters} from "@/services/backend/parameterManager";
import {
    paymentConfiguredEncryptedList,
    paymentConfiguredNameList
} from "@/pages/Parameter/common";

const PayTypeFormItem: React.FC = () => {
    const payTypeEntries = Object.entries(PayChannelEnum);
    const [refreshing, setRefreshing] = useState(false);
    const [alipayAllConfigured, setAlipayAllConfigured] = useState(false);
    const [wechatPayAllConfigured, setWechatPayAllConfigured] = useState(false);
    const [paymentAllConfigured, setPaymentAllConfigured] = useState(false);

    useEffect(() => {
        handleRefresh();
    }, []);

    const loadPaymentMethod = async (parameterNames: string[], encrypted: boolean[]) => {
        const configParameterQueryModels = parameterNames.map((name, index) => ({ name, encrypted: encrypted[index] }));
        const listParams = { configParameterQueryModels };
        const result = await listConfigParameters(listParams);

        if (result.data?.length) {
            const configStatus = result.data.reduce((acc, param) => {
                if (param.name === 'AlipaySignatureMethod') {
                    if (param.value === 'PrivateKey') {
                        acc['AlipaySignatureMethodWithKey'] = true;
                    } else if (param.value === 'Certificate') {
                        acc['AlipaySignatureMethodWithCert'] = true;
                    }
                } else if (param.value !== 'waitToConfig') {
                    acc[param.name] = true;
                }
                return acc;
            }, {});

            const alipayRequiredKeysWithKey = ['AlipayOfficialPublicKey', 'AlipaySignatureMethodWithKey'];
            const alipayRequiredKeysWithCert = ['AlipaySignatureMethodWithCert',
                'AlipayAppCertPath', 'AlipayCertPath', 'AlipayRootCertPath'];
            const alipayConfigMapWithKey = alipayRequiredKeysWithKey.reduce(
                (map, key) => ({ ...map, [key]: configStatus[key] ?? false }), {}
            );
            const alipayConfigMapWithCert = alipayRequiredKeysWithCert.reduce(
                (map, key) => ({ ...map, [key]: configStatus[key] ?? false }), {}
            );
            const alipayAllConfigured = Object.values(alipayConfigMapWithCert).every(value => value !== false) ||
                Object.values(alipayConfigMapWithKey).every(value => value !== false);
            setAlipayAllConfigured(alipayAllConfigured);

            const wechatPayRequiredKeys = ['WechatPayMchSerialNo', 'WechatPayPrivateKeyPath'];
            const wechatPayConfigMap = wechatPayRequiredKeys.reduce(
                (map, key) => ({ ...map, [key]: configStatus[key] ?? false }), {}
            );
            const wechatPayAllConfigured = Object.values(wechatPayConfigMap).every(value => value !== false);
            setWechatPayAllConfigured(wechatPayAllConfigured);
        }
    };

    const handleRefresh = async () => {
        setRefreshing(true);
        await loadPaymentMethod(paymentConfiguredNameList, paymentConfiguredEncryptedList);
        setRefreshing(false);
        setPaymentAllConfigured(true);
    };

    return (
        <Spin spinning={refreshing}>
            {paymentAllConfigured ? <ProFormRadio.Group
                label={<FormattedMessage id="label.payment-method" defaultMessage="支付方式" />}
                name="payChannel"
                initialValue={alipayAllConfigured? "ALIPAY" : "WECHATPAY"}
                rules={[{
                    required: true,
                    message: <FormattedMessage id="message.select-payment-method" defaultMessage="请选择支付方式" />
                }]}
                options={payTypeEntries.filter(([key, value]) => {
                    if (key === 'ALIPAY' && alipayAllConfigured) {
                        return true;
                    }
                    return key === 'WECHATPAY' && wechatPayAllConfigured;
                }).map(([key, value]) => ({
                    label: (
                        <Space>
                            {key === 'ALIPAY' && <AlipayCircleOutlined style={{ color: '#009fe8' }} />}
                            {key === 'WECHATPAY' && <WechatOutlined style={{ color: '#1AAD19' }} />}
                            {value}
                        </Space>
                    ),
                    value: key,
                }))}
            /> : null}
        </Spin>
    )
};
export default PayTypeFormItem;
