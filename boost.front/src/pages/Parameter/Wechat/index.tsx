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
    const [paymentEnvironment, setPaymentEnvironment] = useState('Sandbox');

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

    const handleEnvironmentChange = (e: any) => {
        setPaymentEnvironment(e.target.value);
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
            <ProFormText {...getFieldProps('WechatAppId', '应用ID(微信)', '请输入应用ID')} />
            <ProFormText {...getFieldProps('WechatPid', '商户ID(微信)', '请输入商户ID')} />
            <ProFormText {...getFieldProps('WechatOfficialPublicKey', '官方公钥(微信)', '请输入官方公钥')} />
            <ProFormText {...getFieldProps('WechatPrivateKey', '服务商私钥(微信)', '请输入服务商私钥')} />

            <ProFormRadio.Group
                name="paymentEnvironment"
                label="支付环境"
                options={[
                    { label: 'Sandbox', value: 'sandbox' },
                    { label: '正式环境', value: 'production' }
                ]}
                fieldProps={{
                    onChange: handleEnvironmentChange,
                    disabled: !editing, // Optional: if the radio group should be disabled when not editing
                }}
            />

            {editing && <ActionButtons onSave={handleSave} onCancel={handleCancel} />}
        </ProForm>
    );
};