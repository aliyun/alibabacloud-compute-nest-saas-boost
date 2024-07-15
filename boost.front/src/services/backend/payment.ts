// @ts-ignore
/* eslint-disable */
import { request } from '@umijs/max';

/** 创建交易 POST /api/payment/createTransaction */
export async function createTransaction(
  // 叠加生成的Param类型 (非body参数swagger默认没有生成对象)
  params: API.createTransactionParams,
  options?: { [key: string]: any },
) {
  return request<API.BaseResultString_>('/api/payment/createTransaction', {
    method: 'POST',
    params: {
      ...params,
    },
    ...(options || {}),
  });
}

/** 退款统一异步回调校验 POST /api/payment/verifyRefundCallback */
export async function verifyRefundCallback(options?: { [key: string]: any }) {
  return request<string>('/api/payment/verifyRefundCallback', {
    method: 'POST',
    ...(options || {}),
  });
}

/** 付款统一异步回调校验 POST /api/payment/verifyTradeCallback */
export async function verifyTradeCallback(options?: { [key: string]: any }) {
  return request<string>('/api/payment/verifyTradeCallback', {
    method: 'POST',
    ...(options || {}),
  });
}
