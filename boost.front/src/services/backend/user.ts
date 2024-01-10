// @ts-ignore
/* eslint-disable */
import { request } from '@umijs/max';

/** 获取认证配置 GET /api/getAuthConfiguration */
export async function getAuthConfiguration(options?: { [key: string]: any }) {
  return request<API.BaseResultAuthConfigurationModel_>('/api/getAuthConfiguration', {
    method: 'GET',
    ...(options || {}),
  });
}

/** 获取指定用户的授权Token GET /api/getAuthToken */
export async function getAuthToken(
  // 叠加生成的Param类型 (非body参数swagger默认没有生成对象)
  params: API.getAuthTokenParams,
  options?: { [key: string]: any },
) {
  return request<API.BaseResultAuthTokenModel_>('/api/getAuthToken', {
    method: 'GET',
    params: {
      ...params,
    },
    ...(options || {}),
  });
}

/** 获取已授权用户的信息 GET /api/getUserInfo */
export async function getUserInfo(options?: { [key: string]: any }) {
  return request<API.BaseResultUserInfoModel_>('/api/getUserInfo', {
    method: 'GET',
    ...(options || {}),
  });
}
