export interface RootState {
    providerInfo: {
        providerName: string;
        providerOfficialLink: string;
        providerDescription: string;
        providerLogoUrl: string;
    };
    paymentMethod: {
        alipayConfigured: boolean;
        wechatPayConfigured: boolean;
    };
}
