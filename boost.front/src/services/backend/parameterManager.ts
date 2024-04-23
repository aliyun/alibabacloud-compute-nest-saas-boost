// @ts-ignore
/* eslint-disable */
import { request } from '@umijs/max';

/** 根据填报表批量查询参数 POST /api/listConfigParameters */
export async function listConfigParameters(
  body: API.ListConfigParametersParam,
  options?: { [key: string]: any },
) {
  return request<API.ListResultConfigParameterModel_>('/api/listConfigParameters', {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
    },
    data: body,
    ...(options || {}),
  });
}

/** 根据输入的参数名更新参数 POST /api/updateConfigParameter */
export async function updateConfigParameter(
  body: API.UpdateConfigParameterParam,
  options?: { [key: string]: any },
) {
  return request<API.BaseResultVoid_>('/api/updateConfigParameter', {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
    },
    data: body,
    ...(options || {}),
  });
}
