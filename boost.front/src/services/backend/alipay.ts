// @ts-ignore
/* eslint-disable */
import { request } from '@umijs/max';

/** 支付宝异步回调校验接口 POST /alipay/verifyTradeCallback */
export async function verifyTradeCallback(options?: { [key: string]: any }) {
  return request<string>('/alipay/verifyTradeCallback', {
    method: 'POST',
    ...(options || {}),
  });
}
