import React, {useEffect, useState} from "react";
import {WechatPaymentKeys} from "@/pages/Parameter/component/interface";
import { ProForm, ProFormText, ProFormRadio } from '@ant-design/pro-form';
import {ActionButtons} from "@/pages/Parameter/common";

export const WechatPaymentKeyForm: React.FC<{
    wechatPaymentKeys: WechatPaymentKeys,
    onUpdateWechatPaymentKeys: (updatedKeys: WechatPaymentKeys) => void,
    editing: boolean,
    privateKeysVisible: boolean,
    onCancelEdit: () => void,
}> = ({
          wechatPaymentKeys,
          onUpdateWechatPaymentKeys,
          editing,
          privateKeysVisible,
          onCancelEdit,
      }) => {
    const [localWechatPaymentKeys, setLocalWechatPaymentKeys] = useState(wechatPaymentKeys);

    useEffect(() => {
        setLocalWechatPaymentKeys(wechatPaymentKeys);
    }, [wechatPaymentKeys]);

    const handleSave = () => {
        onUpdateWechatPaymentKeys(localWechatPaymentKeys);
        onCancelEdit();
    };

    const handleCancel = () => {
        setLocalWechatPaymentKeys(wechatPaymentKeys);
        onCancelEdit();
    };

    const handleChange = (key: keyof WechatPaymentKeys, value: string) => {
        setLocalWechatPaymentKeys({ ...localWechatPaymentKeys, [key]: value });
    };

    const handleEnvironmentChange = (key: keyof WechatPaymentKeys, value: string) => {
        setLocalWechatPaymentKeys({ ...localWechatPaymentKeys, [key]: value });
    };

    const getFieldProps = (key: keyof WechatPaymentKeys, label: string, placeholder: string) => ({
        label: <label style={{ fontWeight: 'bold' }}>{label}</label>,
        placeholder: placeholder,
        value: localWechatPaymentKeys[key],
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
            submitter={{
                render: (_) => (<></>),
            }}
        >
            <ProFormText {...getFieldProps('WechatAppId', '应用ID', '请输入应用ID')} />
            <ProFormText {...getFieldProps('WechatPid', '商户ID', '请输入商户ID')} />
            <ProFormText {...getFieldProps('WechatOfficialPublicKey', '官方公钥', '请输入官方公钥')} />
            <ProFormText {...getFieldProps('WechatPrivateKey', '服务商私钥', '请输入服务商私钥')} />

            <ProFormRadio.Group
                name="paymentEnvironment"
                label={<label style={{ fontWeight: 'bold' }}>支付环境</label>}
                initialValue='https://openapi-sandbox.dl.alipaydev.com/gateway.do'
                options={[
                    { label: '沙盒环境', value: 'https://openapi-sandbox.dl.alipaydev.com/gateway.do' },
                    { label: '正式环境', value: 'https://openapi.alipay.com/gateway.do' }
                ]}
                fieldProps={{
                    value: localWechatPaymentKeys['WechatGateway'],
                    onChange: (e:any) => handleEnvironmentChange("WechatGateway", e.target.value),
                    disabled: !editing, // Optional: if the radio group should be disabled when not editing
                }}
            />

            {editing && <ActionButtons onSave={handleSave} onCancel={handleCancel} />}
        </ProForm>
    );
};