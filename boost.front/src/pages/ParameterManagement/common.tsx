import { ProviderInfo, PaymentKeys} from '@/pages/ParameterManagement/component/interface';

export const initialProviderInfo: ProviderInfo = {
    providerName: '',
    providerOfficialLink: '',
    providerDescription: '',
};

export const initialPaymentKeys: PaymentKeys = {
    alipayPublicKey: '',
    alipayPrivateKey: '',
    wechatPublicKey: '',
    wechatPrivateKey: '',
};

export const initialProviderInfoNameList = [
    'providerName',
    'providerOfficialLink',
    'providerDescription',
];
export const initialProviderInfoEncryptedList = [
    false,
    false,
    false,
];
export const initialPaymentKeysNameList = [
    'alipayPublicKey',
    'alipayPrivateKey',
    'wechatPublicKey',
    'wechatPrivateKey',
];
export const initialPaymentKeysEncryptedList = [
    true,
    true,
    true,
    true,
];

export const encryptedCredentialsMap = {
    'providerName': false,
    'providerOfficialLink': false,
    'providerDescription': false,
    'alipayPublicKey': true,
    'alipayPrivateKey': true,
    'wechatPublicKey': true,
    'wechatPrivateKey': true,
}