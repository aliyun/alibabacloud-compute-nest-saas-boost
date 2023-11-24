// @ts-ignore
/* eslint-disable */
import { request } from '@umijs/max';

/** 获取指定ID的服务实例详情 GET /api/getServiceInstance */
export async function getServiceInstance(
  // 叠加生成的Param类型 (非body参数swagger默认没有生成对象)
  params: API.getServiceInstanceParams,
  options?: { [key: string]: any },
) {
  return request<API.BaseResultServiceInstanceModel_>('/api/getServiceInstance', {
    method: 'GET',
    params: {
      ...params,
    },
    ...(options || {}),
  });
}

/** 获取该用户下的全部服务实例列表 GET /api/listServiceInstances */
export async function listServiceInstances(
  // 叠加生成的Param类型 (非body参数swagger默认没有生成对象)
  params: API.listServiceInstancesParams,
  options?: { [key: string]: any },
) {
  return request<API.ListResultServiceInstanceModel_>('/api/listServiceInstances', {
    method: 'GET',
    params: {
      ...params,
    },
    ...(options || {}),
  });
}
