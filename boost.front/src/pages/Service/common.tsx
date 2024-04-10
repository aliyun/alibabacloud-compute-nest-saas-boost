import {ProColumns} from "@ant-design/pro-components";

export const profileImageUrl = "boost.front/public/logo.png";

export const contactEmail = "email@example.com";

export const featuredServiceTitle = "Compute Nest SaaS Boost";

export const featuredServiceDescription1 = "计算巢SaaS Boost是由阿里云推出的一款开发工具和框架，旨在帮助软件即服务（SaaS）开发者快速构建、部署、扩展和售卖SaaS应用程序。它提供了一组开箱即用的功能和组件，使用户聚焦于业务逻辑的开发，而极大地降低SaaS应用程序开发的复杂性和成本。";

export const featuredServiceDescription2 = "SaaS Boost集成了计算巢，拥有多租户架构、持续集成和持续交付（CI/CD）能力，以及套餐管理和应用程序监控功能。这些功能加速了传统软件开发者向SaaS化转型的过程，同时降低了SaaS化转型所需的成本。用户能够轻松、顺畅地在一周内完成从传统软件服务商到SaaS软件服务商的身份转变，上云不再是难题。";

export const serviceColumns: ProColumns<ServiceModel>[] = [
    {
        title: '商品名',
        dataIndex: 'commodityName',
        key: 'commodityName',
        search: false,
    },
    {
        title: '商品介绍',
        dataIndex: 'description',
        key: 'description',
        search: false,
    },
];

export type ServiceModel = {

    commodityName?: string;

    serviceId?: string;

    version?: string;

    serviceStatus?: string;

    description?: string;
};