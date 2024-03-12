// @ts-ignore
/* eslint-disable */
import { request } from '@umijs/max';

/** 创建商品规格 POST /api/createCommoditySpecification */
export async function createCommoditySpecification(
  body: API.CreateCommoditySpecificationParam,
  options?: { [key: string]: any },
) {
  return request<API.BaseResultVoid_>('/api/createCommoditySpecification', {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
    },
    data: body,
    ...(options || {}),
  });
}

/** 删除商品规格 DELETE /api/deleteCommoditySpecification */
export async function deleteCommoditySpecification(
  // 叠加生成的Param类型 (非body参数swagger默认没有生成对象)
  params: API.deleteCommoditySpecificationParams,
  options?: { [key: string]: any },
) {
  return request<API.BaseResult>('/api/deleteCommoditySpecification', {
    method: 'DELETE',
    params: {
      ...params,
    },
    ...(options || {}),
  });
}

/** 获取所有商品规格 POST /api/listAllSpecifications */
export async function listAllSpecifications(
  // 叠加生成的Param类型 (非body参数swagger默认没有生成对象)
  params: API.listAllSpecificationsParams,
  options?: { [key: string]: any },
) {
  return request<API.ListResultCommoditySpecificationDTO_>('/api/listAllSpecifications', {
    method: 'POST',
    params: {
      ...params,
    },
    ...(options || {}),
  });
}

/** 更新商品规格信息 PUT /api/updateCommoditySpecification */
export async function updateCommoditySpecification(
  body: API.UpdateCommoditySpecificationParam,
  options?: { [key: string]: any },
) {
  return request<API.BaseResultVoid_>('/api/updateCommoditySpecification', {
    method: 'PUT',
    headers: {
      'Content-Type': 'application/json',
    },
    data: body,
    ...(options || {}),
  });
}
