// @ts-ignore
/* eslint-disable */
import { request } from '@umijs/max';

/** 根据填报表批量查询参数 GET /api/listConfigParameters */
export async function listConfigParameters(
  // 叠加生成的Param类型 (非body参数swagger默认没有生成对象)
  params: API.listConfigParametersParams,
  options?: { [key: string]: any },
) {
  return request<API.BaseResultListConfigParametersModel_>('/api/listConfigParameters', {
    method: 'GET',
    params: {
      ...params,
    },
    ...(options || {}),
  });
}

/** 根据输入的参数名更新参数 POST /api/updateConfigParameter */
export async function updateConfigParameter(
  // 叠加生成的Param类型 (非body参数swagger默认没有生成对象)
  params: API.updateConfigParameterParams,
  options?: { [key: string]: any },
) {
  return request<API.BaseResultVoid_>('/api/updateConfigParameter', {
    method: 'POST',
    params: {
      ...params,
    },
    ...(options || {}),
  });
}
