import React, {useEffect, useState} from "react";
import {WechatPayPaymentKeys} from "@/pages/Parameter/component/interface";
import { ProForm, ProFormText } from '@ant-design/pro-form';
import {ActionButtons, CertUploadButton} from "@/pages/Parameter/common";

export const WechatPayPaymentKeyForm: React.FC<{
    wechatPayPaymentKeys: WechatPayPaymentKeys,
    onUpdateWechatPayPaymentKeys: (updatedKeys: WechatPayPaymentKeys) => void,
    editing: boolean,
    privateKeysVisible: boolean,
    onCancelEdit: () => void,
}> = ({
          wechatPayPaymentKeys,
          onUpdateWechatPayPaymentKeys,
          editing,
          privateKeysVisible,
          onCancelEdit,
      }) => {
    const [localWechatPayPaymentKeys, setLocalWechatPayPaymentKeys] = useState(wechatPayPaymentKeys);

    useEffect(() => {
        setLocalWechatPayPaymentKeys(wechatPayPaymentKeys);
    }, [wechatPayPaymentKeys]);

    const handleSave = () => {
        onUpdateWechatPayPaymentKeys(localWechatPayPaymentKeys);
        onCancelEdit();
    };

    const handleCancel = () => {
        onCancelEdit();
    };

    const handleChange = (key: string, value: string) => {
        setLocalWechatPayPaymentKeys(prevKeys => {
            const updatedKeys = { ...prevKeys, [key]: value };
            console.log(`更改后的 ${key} , 值: ${value}`);
            console.log("更新后的状态", updatedKeys);
            return updatedKeys; // 返回合并了新值的状态对象
        });
    };

    const getFieldPropsVisible = (key: keyof WechatPayPaymentKeys, label: string, placeholder: string) => ({
        label: <label style={{ fontWeight: 'bold' }}>{label}</label>,
        placeholder: placeholder,
        value: localWechatPayPaymentKeys[key],
        fieldProps: {
            disabled: !editing,
            type: 'text',
            onChange: (e: React.ChangeEvent<HTMLInputElement>) => {
                handleChange(key, e.target.value);
            },
        },
    });

    const getFieldPropsInvisible = (key: keyof WechatPayPaymentKeys, label: string, placeholder: string) => ({
        label: <label style={{ fontWeight: 'bold' }}>{label}</label>,
        placeholder: placeholder,
        value: localWechatPayPaymentKeys[key],
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

            <ProFormText {...getFieldPropsVisible('WechatPayAppId', '应用ID', '请输入应用ID')} />
            <ProFormText {...getFieldPropsVisible('WechatPayMchId', '商户ID', '请输入商户ID')} />
            <ProFormText {...getFieldPropsInvisible('WechatPayApiV3Key', 'ApiV3私钥', '请输入ApiV3私钥')} />
            <ProFormText {...getFieldPropsInvisible('WechatPayMchSerialNo', '商户API证书序列号', '请输入商户API证书序列号')} />

            <CertUploadButton
                name="WechatPayPrivateKeyPath"
                label="商户API私钥证书"
                payChannel={'WECHATPAY'}
                storageMethod={'oss'}
                editing={editing}
                uploadedCertName={localWechatPayPaymentKeys['WechatPayPrivateKeyPath']}
                handlePaymentKeysChange={handleChange}
            />

            {editing && <ActionButtons onSave={handleSave} onCancel={handleCancel} />}
        </ProForm>
    );
};