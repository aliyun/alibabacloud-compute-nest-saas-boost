import React from 'react';
import ProCard from '@ant-design/pro-card';
import { QRCodeCanvas } from 'qrcode.react';
import alipayLogo from '../../../../public/AlipayLogo/alipay_logo.png'
import {Space} from "antd";

interface AlipayModalProps {
    qrCodeURL: string;
}

const AlipayModal: React.FC<AlipayModalProps> = ({ qrCodeURL}) => {
    if (!qrCodeURL) {
        console.error('No QR code URL provided');
    }

    return (
        <ProCard
            headStyle={{ textAlign: 'center', fontSize: '20px' }}
            bordered
            hoverable
        >
            <img src={alipayLogo} style={{ width: '160px', height: '55px', margin: '8px auto' }} alt="Alipay Logo" />
            {qrCodeURL ? (
                <QRCodeCanvas value={qrCodeURL} size={160} style={{ margin: '8px auto' }} />
            ) : (
                <Space>
                    <div style={{ fontSize: '16px', fontWeight: 'bold', margin: '8px auto', textAlign: 'center' }}>获取二维码失败</div>
                    <div style={{ fontSize: '16px', fontWeight: 'bold', margin: '8px auto', textAlign: 'center' }}>请刷新页面或联系工作人员</div>
                </Space>
            )}
        </ProCard>
    );
};

export default AlipayModal;
