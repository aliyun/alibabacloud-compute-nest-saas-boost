// @ts-ignore
/* eslint-disable */
import { request } from '@umijs/max';

/** 获取全部可监控的表单信息 GET /api/listMetricMetaDatas */
export async function listMetricMetaDatas(options?: { [key: string]: any }) {
  return request<API.ListResultMetricMetaDataModel_>('/api/listMetricMetaDatas', {
    method: 'GET',
    ...(options || {}),
  });
}

/** 获取指定表单的监控数据 GET /api/listMetrics */
export async function listMetrics(
  // 叠加生成的Param类型 (非body参数swagger默认没有生成对象)
  params: API.listMetricsParams,
  options?: { [key: string]: any },
) {
  return request<API.BaseResultMetricDatasModel_>('/api/listMetrics', {
    method: 'GET',
    params: {
      ...params,
    },
    ...(options || {}),
  });
}
