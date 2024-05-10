// @ts-ignore
/* eslint-disable */
import { request } from '@umijs/max';

/** 创建商品 POST /api/createCommodity */
export async function createCommodity(
  body: API.CreateCommodityParam,
  options?: { [key: string]: any },
) {
  return request<API.BaseResultCommodityDTO_>('/api/createCommodity', {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
    },
    data: body,
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

/** 获取商品信息 POST /api/getCommodity */
export async function getCommodity(
  // 叠加生成的Param类型 (非body参数swagger默认没有生成对象)
  params: API.getCommodityParams,
  options?: { [key: string]: any },
) {
  return request<API.CommodityDTO>('/api/getCommodity', {
    method: 'POST',
    params: {
      ...params,
    },
    ...(options || {}),
  });
}

/** 获取预测价格 POST /api/getCommodityPrice */
export async function getCommodityPrice(
  // 叠加生成的Param类型 (非body参数swagger默认没有生成对象)
  params: API.getCommodityPriceParams,
  options?: { [key: string]: any },
) {
  return request<API.CommodityPriceModel>('/api/getCommodityPrice', {
    method: 'POST',
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

/** 获取商品价格 POST /api/spi/getCommodityPrice */
export async function getCommodityPriceSpi(
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

/** 更新商品信息 POST /api/updateCommodity} */
export async function updateCommodity(
  body: API.UpdateCommodityParam,
  options?: { [key: string]: any },
) {
  return request<API.BaseResultVoid_>('/api/updateCommodity}', {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
    },
    data: body,
    ...(options || {}),
  });
}
