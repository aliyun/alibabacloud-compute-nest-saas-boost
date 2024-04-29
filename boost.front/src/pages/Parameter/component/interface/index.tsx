export interface ProviderInfo {
    ProviderName: string;
    ProviderOfficialLink: string;
    ProviderDescription: string;
    ProviderLogoUrl: string;
}

export interface AlipayPaymentKeys {
    AlipayAppId: string;
    AlipayPid: string;
    AlipayOfficialPublicKey: string;
    AlipayPrivateKey: string;
    AlipaySignatureMethod: string;
    AlipayGateway: string;
    AlipayAppCertPath: string;
    AlipayCertPath: string;
    AlipayRootCertPath: string;
}
export interface WechatPayPaymentKeys {
    WechatPayAppId: string;
    WechatPayMchId: string;
    WechatPayApiV3Key: string;
    WechatPayMchSerialNo: string;
    WechatPayGateway: string;
    WechatPayPrivateKeyPath: string;
}