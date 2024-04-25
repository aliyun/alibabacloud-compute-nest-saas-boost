import { ProviderInfo, AlipayPaymentKeys, WechatPaymentKeys } from '@/pages/Parameter/component/interface';
import React from "react";
import {Button, Col, Row} from "antd";

export const initialProviderInfo: ProviderInfo = {
    ProviderName: '',
    ProviderOfficialLink: '',
    ProviderDescription: '',
};
export const initialProviderInfoNameList = [
    'ProviderName',
    'ProviderOfficialLink',
    'ProviderDescription',
];
export const initialProviderInfoEncryptedList = [
    false,
    false,
    false,
];
export const initialAlipayPaymentKeys: AlipayPaymentKeys = {
    AlipayAppId: '',
    AlipayPid: '',
    AlipayOfficialPublicKey: '',
    AlipayPrivateKey: '',
};

export const initialWechatPaymentKeys: WechatPaymentKeys = {
    WechatAppId: '',
    WechatPid: '',
    WechatOfficialPublicKey: '',
    WechatPrivateKey: '',
};
export const initialPaymentKeysNameList = {
    alipay: ['AlipayAppId', 'AlipayPid', 'AlipayOfficialPublicKey', 'AlipayPrivateKey'],
    wechat: ['WechatAppId', 'WechatPid', 'WechatOfficialPublicKey', 'WechatPrivateKey'],
};
export const initialPaymentKeysEncryptedList = {
    alipay: [true, true, true, true],
    wechat: [true, true, true, true],
};

export const encryptedCredentialsMap = {
    'ProviderName': false,
    'ProviderOfficialLink': false,
    'ProviderDescription': false,
    'AlipayAppId': true,
    'AlipayPid': true,
    'AlipayOfficialPublicKey': true,
    'AlipayPrivateKey': true,
    'WechatAppId': true,
    'WechatPid': true,
    'WechatOfficialPublicKey': true,
    'WechatPrivateKey': true,
};

export const ActionButtons: React.FC<{ onSave: () => void; onCancel: () => void }> = ({ onSave, onCancel }) => (
    <Row justify="end" style={{ marginTop: '0px', marginBottom: '24px' }}>
        <Col>
            <Button type="primary" onClick={onSave}>
                保存
            </Button>
            <Button onClick={onCancel}>取消</Button>
        </Col>
    </Row>
);