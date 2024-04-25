import React, {useEffect, useState} from 'react';
import {ProviderInfo} from '@/pages/Parameter/component/interface';
import {ProFormText, ProForm, ProFormTextArea} from '@ant-design/pro-form';
import {ActionButtons} from "@/pages/Parameter/common";

export const ProviderInfoForm: React.FC<{
    providerInfo: ProviderInfo,
    onUpdateProviderInfo: (updatedInfo: ProviderInfo) => void,
    editing: boolean,
    onCancelEdit: () => void,
}> = ({ providerInfo, onUpdateProviderInfo, editing, onCancelEdit }) => {
    const [localProviderInfo, setLocalProviderInfo] = useState(providerInfo);

    useEffect(() => {
        setLocalProviderInfo(providerInfo);
    }, [providerInfo]);
    const handleSave = () => {
        onCancelEdit();
        onUpdateProviderInfo(localProviderInfo);
    };

    const handleCancel = () => {
        setLocalProviderInfo(providerInfo);
        onCancelEdit();
    };

    const handleChange = (key: keyof ProviderInfo, value: string) => {
        setLocalProviderInfo({ ...localProviderInfo, [key]: value });
    };

    const getFieldProps = (key: keyof ProviderInfo, label: string, placeholder: string) => ({
        label: <label style={{ fontWeight: 'bold' }}>{label}</label>,
        placeholder: placeholder,
        value: localProviderInfo[key],
        fieldProps: {
            disabled: !editing,
            onChange: (e: React.ChangeEvent<HTMLInputElement | HTMLTextAreaElement>) => {
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
            <ProFormText {...getFieldProps('ProviderName', '服务商名称', '请输入服务商名称')} />
            <ProFormText {...getFieldProps('ProviderOfficialLink', '官方链接', '请输入服务商官方链接')} />
            <ProFormTextArea {...getFieldProps('ProviderDescription', '服务商简介', '请输入服务商简介')} />

            {editing && <ActionButtons onSave={handleSave} onCancel={handleCancel} />}
        </ProForm>
    );
};