import { ProviderInfo, PaymentKeys} from '@/pages/ParameterManagement/component/interface';

export const initialProviderInfo: ProviderInfo = {
    ProviderName: '',
    ProviderOfficialLink: '',
    ProviderDescription: '',
};

export const initialPaymentKeys: PaymentKeys = {
    AlipayOfficialPublicKey: '',
    AlipayPrivateKey: '',
    WechatOfficialPublicKey: '',
    WechatPrivateKey: '',
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
export const initialPaymentKeysNameList = [
    'AlipayOfficialPublicKey',
    'AlipayPrivateKey',
    'WechatOfficialPublicKey',
    'WechatPrivateKey',
];
export const initialPaymentKeysEncryptedList = [
    true,
    true,
    true,
    true,
];

export const encryptedCredentialsMap = {
    'ProviderName': false,
    'ProviderOfficialLink': false,
    'ProviderDescription': false,
    'AlipayOfficialPublicKey': true,
    'AlipayPrivateKey': true,
    'WechatOfficialPublicKey': true,
    'WechatPrivateKey': true,
}