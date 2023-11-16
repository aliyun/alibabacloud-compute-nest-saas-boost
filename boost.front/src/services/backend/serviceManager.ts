// @ts-ignore
/* eslint-disable */
import { request } from '@umijs/max';

/** 获取服务支付金额 GET /api/getServiceCost */
export async function getServiceCost(
  // 叠加生成的Param类型 (非body参数swagger默认没有生成对象)
  params: API.getServiceCostParams,
  options?: { [key: string]: any },
) {
  return request<API.BaseResultDouble_>('/api/getServiceCost', {
    method: 'GET',
    params: {
      ...params,
      parameters: undefined,
      ...params['parameters'],
    },
    ...(options || {}),
  });
}

/** 获取服务元数据数据 GET /api/getServiceMetadata */
export async function getServiceMetadata(
  // 叠加生成的Param类型 (非body参数swagger默认没有生成对象)
  params: API.getServiceMetadataParams,
  options?: { [key: string]: any },
) {
  return request<API.BaseResultServiceMetadataModel_>('/api/getServiceMetadata', {
    method: 'GET',
    params: {
      ...params,
    },
    ...(options || {}),
  });
}

/** 获取服务模版参数限制信息 POST /api/getServiceTemplateParameterConstraints */
export async function getServiceTemplateParameterConstraints(
  body: API.GetServiceTemplateParameterConstraintsParam,
  options?: { [key: string]: any },
) {
  return request<API.ListResultGetServiceTemplateParameterConstraintsResponseBodyParameterConstraints_>(
    '/api/getServiceTemplateParameterConstraints',
    {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
      },
      data: body,
      ...(options || {}),
    },
  );
}
