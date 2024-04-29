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
    WechatPayPid: string;
    WechatPayApiKey: string;
    WechatPayGateway: string;
    WechatPayAppCertPath: string;
    WechatPayCertPath: string;
    WechatPayPlatformCertPath: string;
}