export interface ProviderInfo {
    name: string;
    officialLink: string;
    description: string;
}

export interface PaymentKeys {
    alipayPublicKey: string;
    alipayPrivateKey: string;
    wechatPublicKey: string;
    wechatPrivateKey: string;
}

export const initialProviderInfo: ProviderInfo = {
    name: '',
    officialLink: '',
    description: '',
};

export const initialPaymentKeys: PaymentKeys = {
    alipayPublicKey: '',
    alipayPrivateKey: '',
    wechatPublicKey: '',
    wechatPrivateKey: '',
};