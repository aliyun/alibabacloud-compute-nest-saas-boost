import React, {useEffect, useState} from "react";
import {AlipayPaymentKeys} from "@/pages/Parameter/component/interface";
import { ProForm, ProFormText, ProFormRadio } from '@ant-design/pro-form';
import {ActionButtons} from "@/pages/Parameter/common";

export const AlipayPaymentKeyForm: React.FC<{
    alipayPaymentKeys: AlipayPaymentKeys,
    onUpdateAlipayPaymentKeys: (updatedKeys: AlipayPaymentKeys) => void,
    editing: boolean, // Add this prop
    privateKeysVisible: boolean,
    onCancelEdit: () => void, // Add this prop
}> = ({ alipayPaymentKeys, onUpdateAlipayPaymentKeys, editing, privateKeysVisible, onCancelEdit }) => {
    const [localAlipayPaymentKeys, setLocalAlipayPaymentKeys] = useState(alipayPaymentKeys);
    const [paymentEnvironment, setPaymentEnvironment] = useState('Sandbox');

    useEffect(() => {
        setLocalAlipayPaymentKeys(alipayPaymentKeys);
    }, [alipayPaymentKeys]);

    const handleSave = () => {
        onUpdateAlipayPaymentKeys(localAlipayPaymentKeys);
        onCancelEdit();
    };

    const handleCancel = () => {
        setLocalAlipayPaymentKeys(alipayPaymentKeys);
        onCancelEdit();
    };

    const handleChange = (key: keyof AlipayPaymentKeys, value: string) => {
        setLocalAlipayPaymentKeys({ ...localAlipayPaymentKeys, [key]: value });
    };

    const handleEnvironmentChange = (e: any) => {
        setPaymentEnvironment(e.target.value);
    };

    const getFieldProps = (key: keyof AlipayPaymentKeys, label: string, placeholder: string) => ({
        label: <label style={{ fontWeight: 'bold' }}>{label}</label>,
        placeholder: placeholder,
        value: localAlipayPaymentKeys[key],
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
            <ProFormText {...getFieldProps('AlipayAppId', '应用ID(支付宝)', '请输入应用ID')} />
            <ProFormText {...getFieldProps('AlipayPid', '商户ID(支付宝)', '请输入商户ID')} />
            <ProFormText {...getFieldProps('AlipayOfficialPublicKey', '官方公钥(支付宝)', '请输入官方公钥')} />
            <ProFormText {...getFieldProps('AlipayPrivateKey', '服务商私钥(支付宝)', '请输入服务商私钥')} />

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