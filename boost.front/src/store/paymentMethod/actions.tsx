export const SET_ALIPAY_CONFIGURED = false;
export const SET_WECHATPAY_CONFIGURED = false;
export const setAlipayConfigured = (alipayConfigured: boolean) => ({
    type: SET_ALIPAY_CONFIGURED,
    payload: alipayConfigured,
});

export const setWechatPayConfigured = (wechatPayConfigured: boolean) => ({
    type: SET_WECHATPAY_CONFIGURED,
    payload: wechatPayConfigured,
});