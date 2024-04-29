import React, { useEffect, useState } from "react";
import { AlipayPaymentKeys } from "@/pages/Parameter/component/interface";
import {ProForm, ProFormText, ProFormRadio} from '@ant-design/pro-form';
import {ActionButtons, CertUploadButton} from "@/pages/Parameter/common";

export const AlipayPaymentKeyForm: React.FC<{
    alipayPaymentKeys: AlipayPaymentKeys,
    onUpdateAlipayPaymentKeys: (updatedKeys: AlipayPaymentKeys) => void,
    editing: boolean,
    privateKeysVisible: boolean,
    onCancelEdit: () => void,
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

    const handleChange = (key: string, value: string) => {
        setLocalAlipayPaymentKeys({ ...localAlipayPaymentKeys, [key as keyof AlipayPaymentKeys]: value });
    };

    const getFieldProps = (key: keyof AlipayPaymentKeys, label: string, placeholder: string, isPassword = false) => ({
        label: <label style={{ fontWeight: 'bold' }}>{label}</label>,
        placeholder: placeholder,
        value: localAlipayPaymentKeys[key],
        fieldProps: {
            disabled: !editing,
            type: isPassword && !privateKeysVisible ? 'password' : 'text',
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
                options={[
                    { label: '沙盒环境', value: 'https://openapi-sandbox.dl.alipaydev.com/gateway.do' },
                    { label: '正式环境', value: 'https://openapi.alipay.com/gateway.do' }
                ]}
                fieldProps={{
                    value: localAlipayPaymentKeys['AlipayGateway'],
                    onChange: (e:any) => handleChange("AlipayGateway", e.target.value),
                    disabled: !editing,
                }}
            />

            <ProFormRadio.Group
                name="signatureMethod"
                label={<label style={{ fontWeight: 'bold' }}>签名方式</label>}
                options={[
                    { label: '密钥', value: 'PrivateKey' },
                    { label: '证书', value: 'Certificate' }
                ]}
                fieldProps={{
                    value: localAlipayPaymentKeys['AlipaySignatureMethod'],
                    onChange: (e:any) => handleChange('AlipaySignatureMethod', e.target.value),
                    disabled: !editing,
                }}
            />

            <ProFormText {...getFieldProps('AlipayAppId', '应用ID', '请输入应用ID')} />
            <ProFormText {...getFieldProps('AlipayPid', '商户ID', '请输入商户ID')} />
            <ProFormText {...getFieldProps('AlipayPrivateKey', '应用私钥', '请输入应用私钥', true)} />

            {localAlipayPaymentKeys['AlipaySignatureMethod'] === 'PrivateKey' && (
                <>
                    <ProFormText {...getFieldProps('AlipayOfficialPublicKey', '支付宝公钥', '请输入支付宝公钥', true)} />
                </>
            )}

            {localAlipayPaymentKeys['AlipaySignatureMethod'] === 'Certificate' && (
                <>
                    <CertUploadButton
                        name="AlipayAppCertPath"
                        label="应用公钥证书"
                        payChannel={'ALIPAY'}
                        storageMethod={'local'}
                        editing={editing}
                        uploadedCertName={localAlipayPaymentKeys['AlipayAppCertPath']}
                        handlePaymentKeysChange={handleChange}
                    />

                    <CertUploadButton
                        name="AlipayCertPath"
                        label="支付宝公钥证书"
                        payChannel={'ALIPAY'}
                        storageMethod={'local'}
                        editing={editing}
                        uploadedCertName={localAlipayPaymentKeys['AlipayCertPath']}
                        handlePaymentKeysChange={handleChange}
                    />

                    <CertUploadButton
                        name="AlipayRootCertPath"
                        label="支付宝根证书"
                        payChannel={'ALIPAY'}
                        storageMethod={'local'}
                        editing={editing}
                        uploadedCertName={localAlipayPaymentKeys['AlipayRootCertPath']}
                        handlePaymentKeysChange={handleChange}
                    />
                </>
            )}

            {editing && <ActionButtons onSave={handleSave} onCancel={handleCancel} />}
        </ProForm>
    );
};