/*
*Copyright (c) Alibaba Group;
*Licensed under the Apache License, Version 2.0 (the "License");
*you may not use this file except in compliance with the License.
*You may obtain a copy of the License at

*   http://www.apache.org/licenses/LICENSE-2.0

*Unless required by applicable law or agreed to in writing, software
*distributed under the License is distributed on an "AS IS" BASIS,
*WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
*See the License for the specific language governing permissions and
*limitations under the License.
*/

export const LOGIN_CONSTANTS = {
    CODE : "code",
    STATE : "state",
    SESSION_STATE : "session_state"
}

export const USER_PARAMETERS = "UserParameters";

export enum PayChannelEnum {
    ALIPAY = 'ALIPAY',
    WECHATPAY = 'WECHATPAY',
}

export enum ProductNameEnum {
    SERVICE_INSTANCE = 'ServiceInstance',
}

export const ALIYUN_REGIONS: string[] = [
    "cn-hangzhou", // 华东 1（杭州）
    "cn-shanghai", // 华东 2（上海）
    "cn-qingdao", // 华北 1（青岛）
    "cn-beijing", // 华北 2（北京）
    "cn-zhangjiakou", // 华北 3（张家口）
    "cn-huhehaote", // 华北 5（呼和浩特）
    "cn-wulanchabu", // 华北 6（乌兰察布）
    "cn-shenzhen", // 华南 1（深圳）
    "cn-heyuan", // 华南 2（河源）
    "cn-hongkong", // 中国香港
    "ap-northeast-1", // 亚太东北 1（东京）
    "ap-southeast-1", // 亚太东南 1（新加坡）
    "ap-southeast-2", // 亚太东南 2（悉尼）
    "ap-southeast-3", // 亚太东南 3（吉隆坡）
    "ap-southeast-5", // 亚太东南 5（雅加达）
    "ap-south-1", // 亚太南部 1（孟买）
    "eu-central-1", // 欧洲中部 1（法兰克福）
    "eu-west-1", // 欧洲西部 1（伦敦）
    "me-east-1", // 中东东部 1（迪拜）
    "us-east-1", // 美国东部 1（弗吉尼亚）
    "us-west-1", // 美国西部 1（硅谷）
    "us-west-1", // 美国西部 1（硅谷）
    "ap-northeast-1", // 亚太东北 1（东京）
    "ap-southeast-1", // 亚太东南 1（新加坡）
    "ap-southeast-2", // 亚太东南 2（悉尼）
    "ap-southeast-3", // 亚太东南 3（吉隆坡）
    "ap-southeast-5", // 亚太东南 5（雅加达）
    "ap-south-1", // 亚太南部 1（孟买）
    "eu-central-1", // 欧洲中部 1（法兰克福）
    "eu-west-1", // 欧洲西部 1（伦敦）
    "me-east-1", // 中东东部 1（迪拜）
    "us-east-1", // 美国东部 1（弗吉尼亚）
    "us-west-1", // 美国西部 1（硅谷）
];

export const COMPUTE_NEST_URL = "https://computenest.console.aliyun.com/service/instance/detail/${RegionId}/${ServiceInstanceId}"

export const CLOUD_MARKET_ORDER_URL ="https://msp.aliyun.com/msp/order/list/";

export const CLOUD_MARKET_PURCHASE_URL ="https://market.aliyun.com/products/57252001/${MarketProductCode}.html";

export enum CallSource {
    Market = 2,
    Supplier = 3,
    SaasBoost = 6
}

export const TIME_FORMAT = "YYYY-MM-DDTHH:mm:ss[Z]";