import React from 'react';
import ProCard from '@ant-design/pro-card';
import { QRCodeCanvas } from 'qrcode.react';
import wechatPayLogo from '../../../../public/WechatPayLogo/wechat_pay_logo.png'
import qrCodeDescImage from '../../../../public/WechatPayLogo/qr_code_description.png'
import {Space} from "antd";

interface WechatPayModalProps {
    qrCodeURL: string;
}

const WechatPayModal: React.FC<WechatPayModalProps> = ({ qrCodeURL}) => {
    if (!qrCodeURL) {
        console.error('No QR code URL provided');
    }

    return (
        <ProCard
            headStyle={{ textAlign: 'center', fontSize: '20px' }}
            bordered
            hoverable
        >
            <img src={wechatPayLogo} style={{ width: '160px', height: '44px', margin: '8px auto' }} alt="WechatPay Logo" />
            {qrCodeURL ? (
                <QRCodeCanvas value={qrCodeURL} size={160} style={{ margin: '8px auto' }} />
            ) : (
                <Space>
                    <div style={{ fontSize: '16px', fontWeight: 'bold', margin: '8px auto', textAlign: 'center' }}>获取二维码失败</div>
                    <div style={{ fontSize: '16px', fontWeight: 'bold', margin: '8px auto', textAlign: 'center' }}>请刷新页面或联系工作人员</div>
                </Space>
            )}
            <img src={qrCodeDescImage} alt="QR Code Description" style={{ width: '160px', height: '56px', margin: '8px auto' }} />
        </ProCard>
    );
};

export default WechatPayModal;
