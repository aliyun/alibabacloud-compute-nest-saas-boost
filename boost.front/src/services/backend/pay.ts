// @ts-ignore
/* eslint-disable */
import { request } from '@umijs/max';

/** 支付异步回调校验接口 POST /pay/verifyTradeCallback */
export async function verifyTradeCallback(options?: { [key: string]: any }) {
  return request<string>('/pay/verifyTradeCallback', {
    method: 'POST',
    ...(options || {}),
  });
}
