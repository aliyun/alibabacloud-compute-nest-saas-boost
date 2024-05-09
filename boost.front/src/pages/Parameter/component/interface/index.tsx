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
    AlipayGateway: string;
}
export interface WechatPaymentKeys {
    WechatAppId: string;
    WechatPid: string;
    WechatOfficialPublicKey: string;
    WechatPrivateKey: string;
    WechatGateway: string;
}