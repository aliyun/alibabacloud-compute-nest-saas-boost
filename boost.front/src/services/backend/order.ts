// @ts-ignore
/* eslint-disable */
import { request } from '@umijs/max';

/** 查询一行订单 GET /api/getOrder */
export async function getOrder(
  // 叠加生成的Param类型 (非body参数swagger默认没有生成对象)
  params: API.getOrderParams,
  options?: { [key: string]: any },
) {
  return request<API.BaseResultOrderDTO_>('/api/getOrder', {
    method: 'GET',
    params: {
      ...params,
    },
    ...(options || {}),
  });
}

/** 查询订单列表 POST /api/listOrders */
export async function listOrders(body: API.ListOrdersParam, options?: { [key: string]: any }) {
  return request<API.ListResultOrderDTO_>('/api/listOrders', {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
    },
    data: body,
    ...(options || {}),
  });
}

/** 订单退款 POST /api/refundOrder */
export async function refundOrder(body: API.RefundOrderParam, options?: { [key: string]: any }) {
  return request<API.BaseResultLong_>('/api/refundOrder', {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
    },
    data: body,
    ...(options || {}),
  });
}

/** 创建订单 POST /api/spi/createOrder */
export async function createOrder(body: API.CreateOrderParam, options?: { [key: string]: any }) {
  return request<API.OrderDTO>('/api/spi/createOrder', {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
    },
    data: body,
    ...(options || {}),
  });
}
