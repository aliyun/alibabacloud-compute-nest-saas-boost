declare namespace API {
  type AuthTokenModel = {
    /** expires_in */
    expires_in?: string;
    /** id_token */
    id_token?: string;
    /** refresh_token */
    refresh_token?: string;
  };

  type BaseResultAuthTokenModel_ = {
    code?: string;
    data?: AuthTokenModel;
    message?: string;
    requestId?: string;
  };

  type BaseResultDouble_ = {
    code?: string;
    data?: number;
    message?: string;
    requestId?: string;
  };

  type BaseResultGetServiceMetadataModel_ = {
    code?: string;
    data?: GetServiceMetadataModel;
    message?: string;
    requestId?: string;
  };

  type BaseResultMetricDatasModel_ = {
    code?: string;
    data?: MetricDatasModel;
    message?: string;
    requestId?: string;
  };

  type BaseResultOrderDTO_ = {
    code?: string;
    data?: OrderDTO;
    message?: string;
    requestId?: string;
  };

  type BaseResultServiceInstanceModel_ = {
    code?: string;
    data?: ServiceInstanceModel;
    message?: string;
    requestId?: string;
  };

  type BaseResultString_ = {
    code?: string;
    data?: string;
    message?: string;
    requestId?: string;
  };

  type BaseResultUserInfoModel_ = {
    code?: string;
    data?: UserInfoModel;
    message?: string;
    requestId?: string;
  };

  type createOrderParams = {
    productComponents?: string;
    productName?: 'SERVICE_INSTANCE';
    type?: 'ALIPAY' | 'WECHATPAY' | 'PAYPAL' | 'CREDIT_CARD';
  };

  type getAuthTokenParams = {
    code?: string;
    redirectUri?: string;
    sessionState?: string;
    state?: string;
  };

  type getOrderParams = {
    orderId?: string;
  };

  type getServiceCostParams = {
    parameters?: Record<string, any>;
    payPeriod?: number;
    payPeriodUnit?: 'Month' | 'Day' | 'Year';
    specificationName?: string;
  };

  type getServiceInstanceParams = {
    serviceInstanceId?: string;
  };

  type GetServiceMetadataModel = {
    parameterMetadata?: string;
    specifications?: string;
    templateName?: string;
  };

  type getServiceMetadataParams = {
    serviceId?: string;
  };

  type listMetricsParams = {
    endTime?: string;
    metricName?: string;
    serviceInstanceId?: string;
    startTime?: string;
  };

  type ListOrdersParam = {
    endTime?: string;
    maxResults?: number;
    nextToken?: string;
    reverse?: boolean;
    startTime?: string;
  };

  type ListResultMetricMetaDataModel_ = {
    code?: string;
    count?: number;
    data?: MetricMetaDataModel[];
    message?: string;
    nextToken?: string;
    requestId?: string;
  };

  type ListResultOrderDTO_ = {
    code?: string;
    count?: number;
    data?: OrderDTO[];
    message?: string;
    nextToken?: string;
    requestId?: string;
  };

  type ListResultServiceInstanceModel_ = {
    code?: string;
    count?: number;
    data?: ServiceInstanceModel[];
    message?: string;
    nextToken?: string;
    requestId?: string;
  };

  type listServiceInstancesParams = {
    maxResults?: number;
    nextToken?: string;
    serviceInstanceId?: string;
    serviceInstanceName?: string;
    status?: string;
  };

  type MetricDatasModel = {
    dataPoints?: string;
  };

  type MetricMetaDataModel = {
    metricDescription?: string;
    metricName?: string;
    statistics?: string[];
    unit?: string;
  };

  type OrderDTO = {
    accountId?: number;
    gmtCreate?: string;
    gmtPayment?: string;
    orderId?: string;
    payPeriod?: number;
    payPeriodUnit?: 'Month' | 'Day' | 'Year';
    productComponents?: string;
    productName?: 'SERVICE_INSTANCE';
    receiptAmount?: number;
    refundAmount?: number;
    refundDate?: string;
    refundId?: string;
    serviceInstanceId?: string;
    specificationName?: string;
    totalAmount?: number;
    tradeStatus?:
      | 'TRADE_CLOSED'
      | 'TRADE_SUCCESS'
      | 'WAIT_BUYER_PAY'
      | 'TRADE_FINISHED'
      | 'REFUNDED'
      | 'REFUNDING';
    type?: 'ALIPAY' | 'WECHATPAY' | 'PAYPAL' | 'CREDIT_CARD';
  };

  type RefundOrderParam = {
    dryRun?: boolean;
    orderId?: string;
  };

  type ServiceInstanceModel = {
    createTime?: string;
    outputs?: string;
    parameters?: string;
    progress?: number;
    resources?: string;
    serviceInstanceId?: string;
    serviceInstanceName?: string;
    serviceModel?: ServiceModel;
    serviceName?: string;
    status?: string;
    updateTime?: string;
  };

  type ServiceModel = {
    description?: string;
    image?: string;
    name?: string;
    serviceId?: string;
  };

  type UserInfoModel = {
    aid?: string;
    /** login_name */
    login_name?: string;
    name?: string;
    sub?: string;
    uid?: string;
  };
}
