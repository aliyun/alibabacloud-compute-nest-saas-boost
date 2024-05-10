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

  type BaseResultCommoditySpecificationDTO_ = {
    code?: string;
    data?: CommoditySpecificationDTO;
    message?: string;
    requestId?: string;
  };

  type BaseResultDouble_ = {
    code?: string;
    data?: number;
    message?: string;
    requestId?: string;
  };

  type BaseResultLong_ = {
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

  type BaseResultVoid_ = {
    code?: string;
    message?: string;
    requestId?: string;
  };

  type CommodityDTO = {
    allowedPaymentDurations?: Record<string, any>;
    chargeType?: 'PostPaid' | 'PrePaid';
    commodityCode?: string;
    commodityName?: string;
    commodityStatus?: 'DRAFT' | 'ONLINE';
    description?: string;
    payPeriodUnit?: string;
    payPeriods?: string;
    serviceId?: string;
    unitPrice?: number;
  };

  type CommodityPriceModel = {
    commodityCode?: string;
    commodityName?: string;
    currency?: string;
    paymentForm?: string;
    serviceId?: string;
    specificationName?: string;
    totalAmount?: number;
    unitPrice?: number;
  };

  type CommoditySpecificationDTO = {
    commodityCode?: string;
    currency?: string;
    payPeriodUnit?: string;
    payPeriods?: string;
    specificationName?: string;
    unitPrice?: number;
  };

  type ConfigParameterModel = {
    name?: string;
    type?: string;
    value?: string;
  };

  type ConfigParameterQueryModel = {
    encrypted?: boolean;
    name?: string;
  };

  type CreateCommodityParam = {
    chargeType?: 'PostPaid' | 'PrePaid';
    commodityName?: string;
    commodityStatus?: 'DRAFT' | 'ONLINE';
    description?: string;
    payPeriodUnit?: 'Month' | 'Day' | 'Year';
    payPeriods?: number[];
    serviceId?: string;
    unitPrice?: number;
  };

  type CreateCommoditySpecificationParam = {
    commodityCode?: string;
    currency?: 'CNY';
    payPeriodUnit?: 'Month' | 'Day' | 'Year';
    payPeriods?: number[];
    specificationName?: string;
    unitPrice?: number;
  };

  type CreateOrderParam = {
    chargeType?: 'PostPaid' | 'PrePaid';
    commodityCode?: string;
    orderType?: string;
    payPeriod?: number;
    payPeriodUnit?: 'Month' | 'Day' | 'Year';
    serviceInstanceId?: string;
    specificationName?: string;
    token?: string;
    userId?: string;
  };

  type createTransactionParams = {
    orderId?: string;
    payChannel?: 'ALIPAY' | 'WECHATPAY' | 'PAYPAL' | 'CREDIT_CARD' | 'PAY_POST';
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

  type GetCommodityPriceParam = {
    commodityCode?: string;
    payPeriod?: number;
    payPeriodUnit?: 'Month' | 'Day' | 'Year';
    specificationName?: string;
    token?: string;
  };

  type getCommodityPriceParams = {
    commodityCode?: string;
    payPeriod?: number;
    payPeriodUnit?: 'Month' | 'Day' | 'Year';
    specificationName?: string;
    token?: string;
  };

  type getCommoditySpecificationParams = {
    commodityCode?: string;
    specificationName?: string;
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
    commodityStatus?: 'DRAFT' | 'ONLINE';
    maxResults?: number;
    nextToken?: string;
  };

  type listAllSpecificationsParams = {
    commodityCode?: string;
    maxResults?: number;
    nextToken?: string;
    specificationName?: string;
  };

  type ListConfigParametersParam = {
    configParameterQueryModels?: ConfigParameterQueryModel[];
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
    orderType?: string;
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

  type ListResultConfigParameterModel_ = {
    code?: string;
    count?: number;
    data?: ConfigParameterModel[];
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

  type ListServiceInstancesParam = {
    maxResults?: number;
    nextToken?: string;
    serviceIdList?: string[];
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
    canRefund?: boolean;
    commodityCode?: string;
    commodityName?: string;
    gmtCreate?: string;
    gmtPayment?: string;
    orderId?: string;
    orderType?: string;
    payChannel?: 'ALIPAY' | 'WECHATPAY' | 'PAYPAL' | 'CREDIT_CARD' | 'PAY_POST';
    payPeriod?: number;
    payPeriodUnit?: 'Month' | 'Day' | 'Year';
    paymentForm?: string;
    productComponents?: string;
    receiptAmount?: number;
    refundAmount?: number;
    refundDate?: string;
    refundDetail?: string;
    refundId?: string;
    serviceId?: string;
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
  };

  type RefundOrderParam = {
    dryRun?: boolean;
    orderId?: string;
    payChannel?: 'ALIPAY' | 'WECHATPAY' | 'PAYPAL' | 'CREDIT_CARD' | 'PAY_POST';
    serviceInstanceId?: string;
  };

  type renewServiceInstanceParams = {
    payChannel?: 'ALIPAY' | 'WECHATPAY' | 'PAYPAL' | 'CREDIT_CARD' | 'PAY_POST';
    payPeriod?: number;
    payPeriodUnit?: 'Month' | 'Day' | 'Year';
    serviceInstanceId?: string;
  };

  type ServiceInstanceModel = {
    createTime?: string;
    endTime?: string;
    orderId?: string;
    outputs?: string;
    parameters?: string;
    progress?: number;
    resources?: string;
    serviceInstanceId?: string;
    serviceInstanceName?: string;
    serviceModel?: ServiceModel;
    serviceName?: string;
    serviceType?: 'private' | 'managed';
    source?: 'User' | 'Market' | 'Supplier' | 'Css' | 'SaasBoost';
    status?: string;
    updateTime?: string;
  };

  type ServiceMetadataModel = {
    allowedRegions?: string;
    commodityCode?: string;
    parameterMetadata?: string;
    retentionDays?: number;
    specifications?: string;
    status?: string;
    templateName?: string;
    version?: string;
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

  type UpdateCommodityParam = {
    commodityCode?: string;
    commodityName?: string;
    commodityStatus?: 'DRAFT' | 'ONLINE';
    description?: string;
    payPeriodUnit?: 'Month' | 'Day' | 'Year';
    payPeriods?: number[];
    serviceId?: string;
    unitPrice?: number;
  };

  type UpdateCommoditySpecificationParam = {
    commodityCode?: string;
    payPeriodUnit?: 'Month' | 'Day' | 'Year';
    payPeriods?: number[];
    specificationName?: string;
    unitPrice?: number;
  };

  type UpdateConfigParameterParam = {
    encrypted?: boolean;
    name?: string;
    value?: string;
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
