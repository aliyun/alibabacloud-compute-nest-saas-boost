import React, {useEffect, useState} from "react";
import {AlipayPaymentKeys} from "@/pages/Parameter/component/interface";
import {ProForm, ProFormText} from "@ant-design/pro-form";
import {ActionButtons} from "@/pages/Parameter/common";

export const AlipayPaymentKeyForm: React.FC<{
    alipayPaymentKeys: AlipayPaymentKeys,
    onUpdateAlipayPaymentKeys: (updatedKeys: AlipayPaymentKeys) => void,
    editing: boolean, // Add this prop
    privateKeysVisible: boolean,
    onCancelEdit: () => void, // Add this prop
}> = ({ alipayPaymentKeys, onUpdateAlipayPaymentKeys, editing, privateKeysVisible, onCancelEdit }) => {
    const [localAlipayPaymentKeys, setLocalAlipayPaymentKeys] = useState(alipayPaymentKeys);

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
            {editing && <ActionButtons onSave={handleSave} onCancel={handleCancel} />}
        </ProForm>
    );
};