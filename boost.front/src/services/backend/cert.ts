// @ts-ignore
/* eslint-disable */
import { request } from '@umijs/max';

/** 删除证书 POST /api/deleteCert */
export async function deleteCert(body: API.DeleteCertParam, options?: { [key: string]: any }) {
  return request<API.BaseResultBoolean_>('/api/deleteCert', {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
    },
    data: body,
    ...(options || {}),
  });
}

/** 查询证书列表 POST /api/listCerts */
export async function listCerts(
  body: 'ALIPAY' | 'WECHATPAY' | 'PAYPAL' | 'CREDIT_CARD' | 'PAY_POST',
  options?: { [key: string]: any },
) {
  return request<API.ListResultString_>('/api/listCerts', {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
    },
    data: body,
    ...(options || {}),
  });
}

/** 上传证书 POST /api/putCert */
export async function putCert(body: API.PutCertParam, options?: { [key: string]: any }) {
  return request<API.BaseResultBoolean_>('/api/putCert', {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
    },
    data: body,
    ...(options || {}),
  });
}
