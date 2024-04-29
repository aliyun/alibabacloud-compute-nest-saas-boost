import {AlipayPaymentKeys, ProviderInfo, WechatPayPaymentKeys} from '@/pages/Parameter/component/interface';
import React, {useEffect, useState} from "react";
import {Button, Col, message, Row, Space, UploadFile} from "antd";
import {ProFormUploadButton} from '@ant-design/pro-form';
import {FileTextOutlined, InboxOutlined} from '@ant-design/icons';
import {deleteCert, putCert} from '@/services/backend/cert';
import {UploadRequestOption} from 'rc-upload/lib/interface';

export const initialProviderInfo: ProviderInfo = {
    ProviderName: '',
    ProviderOfficialLink: '',
    ProviderDescription: '',
    ProviderLogoUrl: '',
};
export const initialProviderInfoNameList = [
    'ProviderName',
    'ProviderOfficialLink',
    'ProviderDescription',
    'ProviderLogoUrl',
];
export const initialProviderInfoEncryptedList = [
    false,
    false,
    false,
    false,
];
export const initialAlipayPaymentKeys: AlipayPaymentKeys = {
    AlipayAppId: '',
    AlipayPid: '',
    AlipayOfficialPublicKey: '',
    AlipayPrivateKey: '',
    AlipaySignatureMethod: '',
    AlipayGateway: '',
    AlipayAppCertPath: '',
    AlipayCertPath: '',
    AlipayRootCertPath: '',
};

export const initialWechatPayPaymentKeys: WechatPayPaymentKeys = {
    WechatPayAppId: '',
    WechatPayPid: '',
    WechatPayApiKey: '',
    WechatPayGateway: '',
    WechatPayAppCertPath: '',
    WechatPayCertPath: '',
    WechatPayPlatformCertPath: '',
};
export const initialPaymentKeysNameList = {
    alipay: ['AlipayAppId', 'AlipayPid', 'AlipayOfficialPublicKey', 'AlipayPrivateKey', 'AlipaySignatureMethod',
        'AlipayGateway', 'AlipayAppCertPath', 'AlipayCertPath', 'AlipayRootCertPath'],
    wechatPay: ['WechatPayAppId', 'WechatPayPid', 'WechatPayApiKey', 'WechatPayGateway', 'WechatPayAppCertPath',
        'WechatPayCertPath', 'WechatPayPlatformCertPath'],
};
export const initialPaymentKeysEncryptedList = {
    alipay: [false, false, true, true, false, false, true, true, true],
    wechatPay: [false, false, true, false, true, true, true],
};

export const encryptedCredentialsMap = {
    'ProviderName': false,
    'ProviderOfficialLink': false,
    'ProviderDescription': false,
    'ProviderLogoUrl': false,
    'AlipayAppId': false,
    'AlipayPid': false,
    'AlipayOfficialPublicKey': true,
    'AlipayPrivateKey': true,
    'AlipaySignatureMethod': false,
    'AlipayGateway': false,
    'AlipayAppCertPath': true,
    'AlipayCertPath': true,
    'AlipayRootCertPath': true,
    'WechatPayAppId': false,
    'WechatPayPid': false,
    'WechatPayApiKey': true,
    'WechatPayGateway': false,
    'WechatPayAppCertPath': true,
    'WechatPayCertPath': true,
    'WechatPayPlatformCertPath': true,
};

export const ActionButtons: React.FC<{ onSave: () => void; onCancel: () => void }> = ({ onSave, onCancel }) => (
    <Row justify="end" style={{ marginTop: '0px', marginBottom: '24px' }}>
        <Col>
            <Space>
                <Button type="primary" onClick={onSave}>
                    保存
                </Button>
                <Button onClick={onCancel}>取消</Button>
            </Space>
        </Col>
    </Row>
);

interface CertUploadButtonProps<TKey extends string> {
    name: string;
    label: string;
    payChannel: string;
    editing: boolean;
    uploadedCertName: string;
    handlePaymentKeysChange: (key: TKey, value: string) => void;
}

type PayChannelType = 'ALIPAY' | 'WECHATPAY' | 'PAYPAL' | 'CREDIT_CARD' | 'PAY_POST';

const uuidv4 = require('uuid/v4');

export const CertUploadButton: React.FC<CertUploadButtonProps<
    keyof AlipayPaymentKeys | keyof WechatPayPaymentKeys // 可以扩展更多的键类型
>> = ({ name, label, payChannel, editing, uploadedCertName, handlePaymentKeysChange }) => {
    const [fileList, setFileList] = useState<UploadFile[]>([]);

    useEffect(() => {
        if (uploadedCertName && uploadedCertName != "waitToConfig") {
            const uid = uuidv4();
            const uploadedFile: UploadFile = {
                uid: uid,
                name: uploadedCertName,
                status: 'done',
            };
            setFileList([uploadedFile]);
        } else {
            setFileList([]);
        }
    }, [uploadedCertName]);

    const handleUpload = async (options: UploadRequestOption) => {
        const { file, onSuccess, onError } = options;
        const reader = new FileReader();
        const fileRcFile = file as File;
        const fileBlob = file as Blob;
        reader.readAsText(fileBlob);
        reader.onload = async () => {
            try {
                const certContent = reader.result as string;
                const certName = fileRcFile.name;
                const response = await putCert({payChannel: payChannel as PayChannelType,
                    certName: certName, certContent: certContent});
                if (response.data === true) {
                    onSuccess && onSuccess(response.data);
                } else {
                    onError && onError(new Error('上传失败'));
                }
            } catch (error) {
                onError && onError(new Error('上传过程中发生错误'));
            }
        };
        reader.onerror = () => {
            message.error('文件读取失败');
        };
    };

    const handleFileChange = async (info: any) => {
        const targetFile: UploadFile = {
            uid: info.file.uid,
            name: info.file.name,
            status: info.file.status,
        };
        setFileList([targetFile]);
        if (info.file.status === 'done') {
            message.success('上传成功');
            console.info('上传成功', info.file);
            if (payChannel == "ALIPAY") {
                handlePaymentKeysChange(name as keyof AlipayPaymentKeys, info.file.name);
            } else if (payChannel == "WECHATPAY") {
                handlePaymentKeysChange(name as keyof WechatPayPaymentKeys, info.file.name);
            }
        } else if (info.file.status === 'error') {
            message.error(`上传失败: ${info.file.error}`);
            console.error('上传失败', info.file.error);
        } else if (info.file.status === 'removed') {
            try {
                const response = await deleteCert({
                    payChannel: payChannel as PayChannelType,
                    certName: info.file.name
                });
                if (response.data === true) {
                    if (payChannel == "ALIPAY") {
                        handlePaymentKeysChange(name as keyof AlipayPaymentKeys, "waitToConfig");
                    } else if (payChannel == "WECHATPAY") {
                        handlePaymentKeysChange(name as keyof WechatPayPaymentKeys, "waitToConfig");
                    }
                    message.success('删除成功');
                } else {
                    message.error('删除失败: ' + response.message);
                }
            } catch (error) {
                message.error('删除过程中发生错误');
                console.error('删除过程中发生错误', error);
            }
        }
    };

    const beforeUploadCheck = (file: File) => {
        const isCert = file.type === 'application/x-pem-file' || /\.pem$|\.crt$|\.cer$/.test(file.name);
        if (!isCert) {
            message.error('无效的证书类型，请上传 .pem, .crt 或 .cer 类型的证书文件');
            return false;
        }
        return true;
    };

    return (
        <ProFormUploadButton
            name={name}
            label={<label style={{ fontWeight: 'bold' }}>{label}</label>}
            title="上传"
            max={1}
            fieldProps={{
                accept: '.pem,.crt,.cer',
                customRequest: handleUpload,
                beforeUpload: beforeUploadCheck,
                onChange: handleFileChange,
                fileList: fileList.map(file => ({
                    ...file,
                    type: 'text',
                    icon: <FileTextOutlined />
                })),
                showUploadList: {
                    showPreviewIcon: true,
                    showRemoveIcon: editing,
                    showDownloadIcon: false
                },
            }}
            icon={<InboxOutlined />}
            disabled={!editing}
        />
    );
};