import React, {useEffect, useState} from "react";
import {WechatPayPaymentKeys} from "@/pages/Parameter/component/interface";
import { ProForm, ProFormText, ProFormRadio } from '@ant-design/pro-form';
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
        setLocalWechatPayPaymentKeys(wechatPayPaymentKeys);
        onCancelEdit();
    };

    const handleChange = (key: string, value: string) => {
        setLocalWechatPayPaymentKeys({ ...localWechatPayPaymentKeys, [key as keyof WechatPayPaymentKeys]: value });
    };

    const handleEnvironmentChange = (key: keyof WechatPayPaymentKeys, value: string) => {
        setLocalWechatPayPaymentKeys({ ...localWechatPayPaymentKeys, [key]: value });
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

            <ProFormRadio.Group
                name="paymentEnvironment"
                label={<label style={{ fontWeight: 'bold' }}>支付环境</label>}
                initialValue='https://api.mch.weixin.qq.com/sandboxnew'
                options={[
                    { label: '沙盒环境', value: 'https://api.mch.weixin.qq.com/sandboxnew' },
                    { label: '正式环境', value: 'https://api.mch.weixin.qq.com' }
                ]}
                fieldProps={{
                    value: localWechatPayPaymentKeys['WechatPayGateway'],
                    onChange: (e:any) => handleEnvironmentChange("WechatPayGateway", e.target.value),
                    disabled: !editing,
                }}
            />

            <ProFormText {...getFieldPropsVisible('WechatPayAppId', '应用ID', '请输入应用ID')} />
            <ProFormText {...getFieldPropsVisible('WechatPayPid', '商户ID', '请输入商户ID')} />
            <ProFormText {...getFieldPropsInvisible('WechatPayApiKey', 'Api私钥', '请输入Api私钥')} />

            <CertUploadButton
                name="WechatPayAppCertPath"
                label="应用公钥证书"
                payChannel={'WECHATPAY'}
                editing={editing}
                uploadedCertName={localWechatPayPaymentKeys['WechatPayAppCertPath']}
                handlePaymentKeysChange={handleChange}
            />

            <CertUploadButton
                name="WechatPayCertPath"
                label="微信支付公钥证书"
                payChannel={'WECHATPAY'}
                editing={editing}
                uploadedCertName={localWechatPayPaymentKeys['WechatPayCertPath']}
                handlePaymentKeysChange={handleChange}
            />

            <CertUploadButton
                name="WechatPayPlatformCertPath"
                label="微信支付根证书"
                payChannel={'WECHATPAY'}
                editing={editing}
                uploadedCertName={localWechatPayPaymentKeys['WechatPayPlatformCertPath']}
                handlePaymentKeysChange={handleChange}
            />

            {editing && <ActionButtons onSave={handleSave} onCancel={handleCancel} />}
        </ProForm>
    );
};