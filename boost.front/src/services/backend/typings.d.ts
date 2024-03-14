declare namespace API {
  type AuthConfigurationModel = {
    authUrl?: string;
    clientId?: string;
  };

  type AuthTokenModel = {
    /** expires_in */
    expires_in?: string;
    /** id_token */
    id_token?: string;
    /** refresh_token */
    refresh_token?: string;
  };

  type BaseResult = {
    code?: string;
    data?: Record<string, any>;
    message?: string;
    requestId?: string;
  };

  type BaseResultAuthConfigurationModel_ = {
    code?: string;
    data?: AuthConfigurationModel;
    message?: string;
    requestId?: string;
  };

  type BaseResultAuthTokenModel_ = {
    code?: string;
    data?: AuthTokenModel;
    message?: string;
    requestId?: string;
  };

  type BaseResultCommodityDTO_ = {
    code?: string;
    data?: CommodityDTO;
    message?: string;
    requestId?: string;
  };

  type BaseResultCommodityPriceModel_ = {
    code?: string;
    data?: CommodityPriceModel;
    message?: string;
    requestId?: string;
  };

  type BaseResultDouble_ = {
    code?: string;
    data?: number;
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

  type BaseResultServiceMetadataModel_ = {
    code?: string;
    data?: ServiceMetadataModel;
    message?: string;
    requestId?: string;
  };

  type BaseResultUserInfoModel_ = {
    code?: string;
    data?: UserInfoModel;
    message?: string;
    requestId?: string;
  };

  type BaseResultVoid_ = {
    code?: string;
    message?: string;
    requestId?: string;
  };

  type CommodityDTO = {
    allowedPaymentDurations?: Record<string, any>;
    chargeType?: string;
    commodityCode?: string;
    commodityName?: string;
    serviceId?: string;
    unitPrice?: number;
  };

  type CommodityPriceModel = {
    totalAmount?: number;
    unitPrice?: number;
  };

  type CommoditySpecificationDTO = {
    allowedPaymentDurations?: Record<string, any>;
    commodityCode?: string;
    currency?: string;
    payPeriodUnit?: string;
    payPeriods?: string;
    specificationName?: string;
    unitPrice?: number;
  };

  type createCommodityParams = {
    chargeType?: 'POST_PAID' | 'PRE_PAID';
    commodityName?: string;
    serviceId?: string;
  };

  type CreateCommoditySpecificationParam = {
    commodityCode?: string;
    currency?: 'CNY';
    payPeriodUnit?: 'Month' | 'Day' | 'Year';
    payPeriods?: number[];
    specificationName?: string;
    unitPrice?: number;
  };

  type createOrderParams = {
    chargeType?: 'POST_PAID' | 'PRE_PAID';
    commodityCode?: string;
    orderType?: string;
    payChannel?: 'ALIPAY' | 'WECHATPAY' | 'PAYPAL' | 'CREDIT_CARD' | 'PAY_POST';
    payPeriod?: number;
    payPeriodUnit?: 'Month' | 'Day' | 'Year';
    specificationName?: string;
    token?: string;
    userId?: string;
  };

  type deleteCommodityParams = {
    commodityCode?: string;
  };

  type deleteCommoditySpecificationParams = {
    commodityCode?: string;
    specificationName?: string;
  };

  type getAuthTokenParams = {
    code?: string;
    redirectUri?: string;
    sessionState?: string;
    state?: string;
  };

  type getCommodityParams = {
    commodityCode?: string;
    token?: string;
  };

  type getCommodityPriceParams = {
    commodityCode?: string;
    payPeriod?: number;
    payPeriodUnit?: 'Month' | 'Day' | 'Year';
    specificationName?: string;
    token?: string;
  };

  type getOrderParams = {
    orderId?: string;
  };

  type getServiceCostParams = {
    parameters?: Record<string, any>;
    payPeriod?: number;
    payPeriodUnit?: 'Month' | 'Day' | 'Year';
    serviceId?: string;
    specificationName?: string;
  };

  type getServiceInstanceParams = {
    serviceInstanceId?: string;
  };

  type getServiceMetadataParams = {
    serviceId?: string;
  };

  type GetServiceTemplateParameterConstraintsParam = {
    deployRegionId?: string;
    parameters?: TemplateParameterParam[];
    serviceId?: string;
    serviceVersion?: string;
    templateName?: string;
  };

  type GetServiceTemplateParameterConstraintsResponseBodyParameterConstraints = {
    allowedValues?: string[];
    associationParameterNames?: string[];
    behavior?: string;
    behaviorReason?: string;
    originalConstraints?: GetServiceTemplateParameterConstraintsResponseBodyParameterConstraintsOriginalConstraints[];
    parameterKey?: string;
    type?: string;
  };

  type GetServiceTemplateParameterConstraintsResponseBodyParameterConstraintsOriginalConstraints = {
    allowedValues?: string[];
    propertyName?: string;
    resourceName?: string;
    resourceType?: string;
  };

  type listAllCommoditiesParams = {
    maxResults?: number;
    nextToken?: string;
  };

  type listAllSpecificationsParams = {
    commodityCode?: string;
    maxResults?: number;
    nextToken?: string;
    specificationName?: string;
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
    orderId?: string;
    serviceInstanceId?: string;
    startTime?: string;
    tradeStatus?: (
      | 'TRADE_CLOSED'
      | 'TRADE_SUCCESS'
      | 'WAIT_BUYER_PAY'
      | 'TRADE_FINISHED'
      | 'REFUNDED'
      | 'REFUNDING'
    )[];
  };

  type ListResultCommodityDTO_ = {
    code?: string;
    count?: number;
    data?: CommodityDTO[];
    message?: string;
    nextToken?: string;
    requestId?: string;
  };

  type ListResultCommoditySpecificationDTO_ = {
    code?: string;
    count?: number;
    data?: CommoditySpecificationDTO[];
    message?: string;
    nextToken?: string;
    requestId?: string;
  };

  type ListResultGetServiceTemplateParameterConstraintsResponseBodyParameterConstraints_ = {
    code?: string;
    count?: number;
    data?: GetServiceTemplateParameterConstraintsResponseBodyParameterConstraints[];
    message?: string;
    nextToken?: string;
    requestId?: string;
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
    billingEndDateMillis?: number;
    billingStartDateMillis?: number;
    gmtCreate?: string;
    gmtPayment?: string;
    orderId?: string;
    payPeriod?: number;
    payPeriodUnit?: 'Month' | 'Day' | 'Year';
    paymentForm?: string;
    productComponents?: string;
    productName?: 'SERVICE_INSTANCE';
    receiptAmount?: number;
    refundAmount?: number;
    refundDate?: string;
    refundDetail?: string;
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
    type?: 'ALIPAY' | 'WECHATPAY' | 'PAYPAL' | 'CREDIT_CARD' | 'PAY_POST';
  };

  type RefundOrderParam = {
    dryRun?: boolean;
    orderId?: string;
    payChannel?: 'ALIPAY' | 'WECHATPAY' | 'PAYPAL' | 'CREDIT_CARD' | 'PAY_POST';
    serviceInstanceId?: string;
  };

  type ServiceInstanceModel = {
    createTime?: string;
    orderId?: string;
    outputs?: string;
    parameters?: string;
    progress?: number;
    resources?: string;
    serviceInstanceId?: string;
    serviceInstanceName?: string;
    serviceModel?: ServiceModel;
    serviceName?: string;
    source?: 'Market' | 'Supplier';
    status?: string;
    updateTime?: string;
  };

  type ServiceMetadataModel = {
    allowedRegions?: string;
    commodityCode?: string;
    parameterMetadata?: string;
    retentionDays?: number;
    specifications?: string;
    templateName?: string;
  };

  type ServiceModel = {
    description?: string;
    image?: string;
    name?: string;
    serviceId?: string;
  };

  type TemplateParameterParam = {
    parameterKey?: string;
    parameterValue?: string;
  };

  type updateCommodityParams = {
    commodityCode?: string;
    serviceId?: string;
  };

  type updateCommoditySpecificationParams = {
    commodityCode?: string;
    payPeriodUnit?: 'Month' | 'Day' | 'Year';
    payPeriods?: number[];
    specificationName?: string;
    unitPrice?: number;
  };

  type UserInfoModel = {
    admin?: boolean;
    aid?: string;
    /** login_name */
    login_name?: string;
    name?: string;
    sub?: string;
    uid?: string;
  };
}
