import {
    SET_ALIPAY_CONFIGURED,
    SET_WECHATPAY_CONFIGURED
} from './actions';

// 首先，定义State和Action的类型
interface State {
    alipayConfigured: boolean;
    wechatPayConfigured: boolean;
}

interface Action {
    type: boolean;
    payload: any; // 或者更具体的类型，取决于你的payload
}

// initialState 明确为State类型
const initialState: State = {
    alipayConfigured: false,
    wechatPayConfigured: false
};

export const paymentMethodReducer = (state: State = initialState, action: Action): State => {
    switch (action.type) {
        case SET_ALIPAY_CONFIGURED:
            return {...state, alipayConfigured: action.payload};
        case SET_WECHATPAY_CONFIGURED:
            return {...state, wechatPayConfigured: action.payload};
        default:
            return state;
    }
};

