// @ts-ignore
/* eslint-disable */
import { request } from '@umijs/max';

/** 创建商品 POST /api/createCommodity */
export async function createCommodity(
  // 叠加生成的Param类型 (非body参数swagger默认没有生成对象)
  params: API.createCommodityParams,
  options?: { [key: string]: any },
) {
  return request<API.BaseResultCommodityDTO_>('/api/createCommodity', {
    method: 'POST',
    params: {
      ...params,
    },
    ...(options || {}),
  });
}

/** 删除商品 DELETE /api/deleteCommodity */
export async function deleteCommodity(
  // 叠加生成的Param类型 (非body参数swagger默认没有生成对象)
  params: API.deleteCommodityParams,
  options?: { [key: string]: any },
) {
  return request<API.BaseResultVoid_>('/api/deleteCommodity', {
    method: 'DELETE',
    params: {
      ...params,
    },
    ...(options || {}),
  });
}

/** 获取所有商品 POST /api/listAllCommodities */
export async function listAllCommodities(
  // 叠加生成的Param类型 (非body参数swagger默认没有生成对象)
  params: API.listAllCommoditiesParams,
  options?: { [key: string]: any },
) {
  return request<API.ListResultCommodityDTO_>('/api/listAllCommodities', {
    method: 'POST',
    params: {
      ...params,
    },
    ...(options || {}),
  });
}

/** 获取商品信息 POST /api/spi/getCommodity */
export async function getCommodity(body: API.GetCommodityParam, options?: { [key: string]: any }) {
  return request<API.CommodityDTO>('/api/spi/getCommodity', {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
    },
    data: body,
    ...(options || {}),
  });
}

/** 获取商品价格 POST /api/spi/getCommodityPrice */
export async function getCommodityPrice(
  body: API.GetCommodityPriceParam,
  options?: { [key: string]: any },
) {
  return request<API.CommodityPriceModel>('/api/spi/getCommodityPrice', {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
    },
    data: body,
    ...(options || {}),
  });
}

/** 更新商品信息 PUT /api/updateCommodity} */
export async function updateCommodity(
  // 叠加生成的Param类型 (非body参数swagger默认没有生成对象)
  params: API.updateCommodityParams,
  options?: { [key: string]: any },
) {
  return request<API.BaseResultVoid_>('/api/updateCommodity}', {
    method: 'PUT',
    params: {
      ...params,
    },
    ...(options || {}),
  });
}
