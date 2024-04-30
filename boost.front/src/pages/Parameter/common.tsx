import { ProviderInfo, AlipayPaymentKeys, WechatPaymentKeys } from '@/pages/Parameter/component/interface';
import React from "react";
import {Button, Col, Row, Space} from "antd";

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
    AlipayGateway: '',
};

export const initialWechatPaymentKeys: WechatPaymentKeys = {
    WechatAppId: '',
    WechatPid: '',
    WechatOfficialPublicKey: '',
    WechatPrivateKey: '',
    WechatGateway: '',
};
export const initialPaymentKeysNameList = {
    alipay: ['AlipayAppId', 'AlipayPid', 'AlipayOfficialPublicKey', 'AlipayPrivateKey', 'AlipayGateway'],
    wechat: ['WechatAppId', 'WechatPid', 'WechatOfficialPublicKey', 'WechatPrivateKey', 'WechatGateway'],
};
export const initialPaymentKeysEncryptedList = {
    alipay: [true, true, true, true, false],
    wechat: [true, true, true, true, false],
};

export const encryptedCredentialsMap = {
    'ProviderName': false,
    'ProviderOfficialLink': false,
    'ProviderDescription': false,
    'ProviderLogoUrl': false,
    'AlipayAppId': true,
    'AlipayPid': true,
    'AlipayOfficialPublicKey': true,
    'AlipayPrivateKey': true,
    'AlipayGateway': false,
    'WechatAppId': true,
    'WechatPid': true,
    'WechatOfficialPublicKey': true,
    'WechatPrivateKey': true,
    'WechatGateway': false,
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