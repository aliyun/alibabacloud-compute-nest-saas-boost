## 钉钉交流群
如有任何问题交流，请使用钉钉扫描加入
![image.png](https://ucc.alicdn.com/pic/developer-ecology/zxldgoa2vzjb2_b03cb29c72f84a28817bc08840c1ac7d.png)

## 目录
- [介绍](##介绍)
- [SaaS Boost快速部署](##SaaS-Boost快速部署)
    - [前提条件](###前提条件)
    - [计费说明](###[计费说明])
    - [参数说明](###参数说明)
    - [部署流程](###[部署流程])
    - [使用](###使用)
- [开发者指南](##开发者指南)
    - [快速创建服务](###快速创建服务)
    - [商品和套餐管理](###商品和套餐管理)
    - [修改配置参数](###修改配置参数)


## 介绍

计算巢SaaS Boost是由阿里云推出的一款开发工具和框架，旨在帮助软件即服务（SaaS）开发者基于计算巢快速构建、部署、扩展和售卖SaaS应用程序（**同时支持私有化部署和全托管模式**）。它提供了一组开箱即用的功能和组件，使用户聚焦于业务逻辑的开发，而极大地降低SaaS应用程序开发的复杂性和成本。 计算巢SaaS Boost提供了许多功能，包括：

计算巢能力：SaaS Boost底层为[阿里云计算巢](https://computenest.console.aliyun.com/)，可加速软件服务商在云上的部署、交付和管理服务, 实现一周上云。
多租户架构：SaaS Boost为多租户应用程序提供了强大的多租户数据隔离和管理功能，使得开发者可以轻松地构建和管理多个租户的数据。
持续集成和持续交付（CI/CD）能力：计算巢SaaS Boost提供了云效流水线，支持CI/CD能力，使开发者能够实现自动化的代码构建、测试和部署。可快速迭代和交付新功能，提高开发效率和产品质量。
商品和套餐管理：Saas Boost平台通过与计算巢服务的紧密整合，为商家提供一种灵活的商品和套餐管理机制，允许同步定价并保持套餐名称的一致性。
SaaS应用程序监控：SaaS Boost提供了监控功能，帮助开发者跟踪和分析应用程序的性能和运行状况，并及时发现和解决问题。

## SaaS Boost快速部署

### 前提条件

使用阿里云计算巢SaaS Boost服务实例，需要对部分阿里云资源进行访问和创建操作。因此您的账号需要包含如下资源的权限。
**说明**：当您的账号是RAM账号时，才需要添加此权限。

| 权限策略名称                              | 备注                         |
|-------------------------------------|----------------------------|
| AliyunECSFullAccess                 | 管理云服务器服务（ECS）的权限           |
| AliyunVPCFullAccess                 | 管理专有网络（VPC）的权限             |
| AliyunROSFullAccess                 | 管理资源编排服务（ROS）的权限           |
| AliyunCloudMonitorFullAccess        | 管理云监控（CMS）的权限              |
| AliyunOSSFullAccess                 | 管理对象存储服务(OSS)的权限           |
| AliyunOOSFullAccess                 | 管理运维编排服务(OOS)的权限           |
| AliyunFCFullAccess                  | 管理函数计算(FC)服务的权限            |
| AliyunOTSFullAccess                 | 管理表格存储服务(OTS)的权限           |
| AliyunComputeNestSupplierFullAccess | 管理计算巢服务（ComputeNest）的商家侧权限 |

### 计费说明

计算巢SaaS Boost部署涉及的费用主要涉及：

- 云服务器服务（ECS）费用
- 表格存储（OTS）费用
- 函数计算（FC）费用
- 对象存储（OSS）费用
- 流量带宽费用

### 参数说明

| 参数组       | 参数项                     | 说明                                                                         |
|-----------|-------------------------|----------------------------------------------------------------------------|
| 服务实例      | 服务实例名称                  | 长度不超过64个字符，必须以英文字母开头，可包含数字、英文字母、短划线（-）和下划线（_）                              |
|           | 地域                      | 服务实例部署的地域                                                                  |
| 待部署的计算巢服务 | 计算巢服务Id                 | 待部署的用户企业的计算巢服务Id                                                           |
| OAuth配置   | OAuthClientId           | 阿里云OAuth认证Client Id                                                        |
|           | OAuthClientSecret       | 阿里云OAuth认证 Client Secret                                                   |
| 支付宝（沙箱）配置 | AlipayAppId             | 支付宝（沙箱）应用ID                                                                |
|           | AlipayPid               | 支付宝（沙箱）应用商家Id                                                              |
|           | AlipayPrivateKey        | 支付宝（沙箱）应用私钥                                                                |
|           | AlipayOfficialPublicKey | 支付宝官方应用公钥                                                                  |
| 流水线配置     | 企业                      | 选择/新建您的企业                                                                  |
|           | GitHub服务连接              | 连接到GitHub的凭证。可通过账号密码方式创建                                                   |
|           | GitRepoEndpoint         | 仓库地址                                                                       |
| 付费类型配置    | 付费类型                    | 按量付费/包年包月                                                                  |
| ECS实例配置   | 实例类型                    | 部署的ECS实例类型                                                                 |
|           | 实例密码                    | 服务器登录密码,长度8-30，必须包含三项（大写字母、小写字母、数字、 ()`~!@#$%^&*_-+=｜{}[]:;'<>,.?/ 中的特殊符号） |
| 可用区配置     | 交换机可用区                  | 该实例类型可部署的可用区                                                               |
| 网络配置      | 是否新建VPC                 | 选择是否在当前可用区新建VPC                                                            |
|           | 专有网络VPC实例Id             | 选择当前可用区下的VPC实例                                                             |
|           | 交换机实例Id                 | 选择当前VPC支持的交换机                                                              |

### 部署流程

部署计算巢SaaS Boost的流程如下：

#### 1. **Fork当前仓库**

Fork [SaaS Boost仓库](https://github.com/aliyun/alibabacloud-compute-nest-saas-boost)到您的个人仓库。
说明：
计算巢SaaS Boost作为一个开发工具和框架已经集成了诸多有助于SaaS应用程序快速构建、部署、扩展和售卖的功能，但以下几点理由可能要求开发者fork SaaS Boost源代码仓库到其个人仓库中：
1. 用户私有参数配置： 开发者在部署SaaS应用时，常常涉及一些敏感或特定于个人环境的静态配置参数，如网络配置、支付密钥、数据库连接字符串等。由于这些参数通常具有保密性且因开发者环境各异，直接在公共的SaaS Boost源代码中硬编码或存储显然是不合适的，因此SaaS Boost服务会在部署过程中将用户提供的静态配置参数自动配置到开发者的个人仓库中。
2. 定制化需求： 开发者在使用SaaS Boost的过程中，可能遇到特定的业务场景或客户需求，需要对框架进行个性化调整或扩展。Forking源代码仓库允许开发者在其个人仓库中直接修改或添加代码，以适应这些特殊需求，而不影响原始SaaS Boost项目的通用性。
3. 贡献与反馈： 如果开发者在使用过程中发现了潜在的bug、性能瓶颈或者有改进框架功能的想法，他们可以fork源代码仓库，进行问题修复或功能增强的开发工作。完成后再通过Pull Request (PR)的方式将改动提交回SaaS Boost官方仓库，为项目社区做出贡献。这有助于提升整个SaaS Boost框架的稳定性和功能丰富度。

#### 2. **创建支付宝沙箱账号**

指引图片中被框住的参数在创建服务实例中会被使用到。创建支付宝沙箱账号和创建计算巢服务实例步骤可颠倒顺序。

1.  按照[支付宝沙箱文档](https://open.alipay.com/develop/sandbox/app)中的说明，创建支付宝沙箱账号。其中应用网关地址和授权回调地址待创建好服务实例后回填。
    ![image.png](https://ucc.alicdn.com/pic/developer-ecology/zxldgoa2vzjb2_6a2fe8532d7b4ba9875073cd6a152ad9.png)


2. 保留支付宝沙箱应用的私钥和支付宝的官方公钥

![image.png](https://ucc.alicdn.com/pic/developer-ecology/zxldgoa2vzjb2_f9541511d8664dfca1ceb41954d7e65b.png)





#### 3. **创建计算巢服务实例**

1. 访问[计算巢控制台](https://computenest.console.aliyun.com/)选择[SaaS Boost一键部署服务](https://computenest.console.aliyun.com/service/detail/cn-hangzhou/service-fd1aec438c974828bb7b/5?type=user&isRecommend=true)
2. 安装提示填写部署参数
    1. 选择要部署的服务。目前仅支持示例服务。
    2. 选择部署地域
    3. 填入支付宝沙箱信息（可跳过）
       ![7.png](https://ucc.alicdn.com/pic/developer-ecology/zxldgoa2vzjb2_06bbb43c2bd14de8bfda5131a6d9d159.png)



    4. 在流水线配置中，创建您的企业。根据前文fork的仓库信息创建Git连接凭证，填入前文fork的仓库地址。

       a. 创建云效企业 

![image.png](https://ucc.alicdn.com/pic/developer-ecology/zxldgoa2vzjb2_713c955d756846289632d1e1f7efb942.png)

       b. 进入 【流水线】

![image.png](https://ucc.alicdn.com/pic/developer-ecology/zxldgoa2vzjb2_e1adbf6692a74ff988a51cd11379d52b.png)

       c. 点击【全局设置】并进入

![image.png](https://ucc.alicdn.com/pic/developer-ecology/zxldgoa2vzjb2_6b53df606f354324a445e7b31ae214ea.png)

        d. 选择右上角Github服务连接并新建

![image.png](https://ucc.alicdn.com/pic/developer-ecology/zxldgoa2vzjb2_1f8b9cc83f444f2a948fd5dc812a9d54.png)

        e. Github处授权
![image.png](https://ucc.alicdn.com/pic/developer-ecology/zxldgoa2vzjb2_3c4936adc7984c20bdffd1f3bc34cc3f.png)



    5. 选择付费类型和想要部署的Ecs实例规格及部署可用区






![9.png](https://ucc.alicdn.com/pic/developer-ecology/zxldgoa2vzjb2_b9b7ed3812d3419f9486f56424314bfd.png)














    6. 选择/新建专有网络(VPC)和交换机(VSW)
      为了减少参数的填写和理解，建议选择新建。
![10.png](https://ucc.alicdn.com/pic/developer-ecology/zxldgoa2vzjb2_f4316bcaaa6e469e892a418f490cbac6.png)

3. 确认同意服务协议并点击**立即创建**
   进入部署阶段。
   ![11.png](https://ucc.alicdn.com/pic/developer-ecology/zxldgoa2vzjb2_a5e75344939c4d67bf3e6065e3ac4fa6.png)

4. 服务实例创建好后回填参数
    1. 在部署的实例信息中将 支付宝异步通知URL 和 支付宝同步通知URL 分别回填到[支付宝沙箱应用](https://open.alipay.com/develop/sandbox/app)配置中的应用网关地址和授权回调地址(
       步骤2.1)
       ![image.png](https://ucc.alicdn.com/pic/developer-ecology/zxldgoa2vzjb2_bcc3dcde0ba54c2b921b0a9ba3b110a3.png)



#### 5. **访问计算巢SaaS Boost服务**

点击部署好的服务实例详情页的Endpoint即可访问示例SaaS Boost界面。
![13.png](https://ucc.alicdn.com/pic/developer-ecology/zxldgoa2vzjb2_462f7096f8384184860ea4385a54719f.png)

### 使用

一旦计算巢SaaS Boost部署完成，您就可以开始使用它来开发、部署和销售您的软件应用程序。以下是一些关键的使用说明：

1. **配置您的SaaS应用（计算巢Service）**：
    - 上传[部署物](https://help.aliyun.com/zh/compute-nest/deployment-object-management/?spm=a2c4g.11174283.0.i2)或使用[快速ROS模版](https://help.aliyun.com/zh/ros/use-cases/?spm=a2c4g.11174283.0.0.5c3848aezYq1dI)搭建您服务的计算巢Service。
    - 在application.properties文件中配置套餐
    - 重新运行流水线，完成SaaS应用的部署。

2. **商品和套餐管理**：
   在SaaS Boost中创建商品时，需将其链接至预先设定的计算巢服务，并对该服务定制价格体系；如服务有附加套餐，亦须在SaaS Boost为各套餐配上相应定价。有关详细操作，请参考随附的开发者指南图文教程。

3. **链接SaaS Boost商品至计算巢服务**：
   创建完商品后，需要将第二步新建的商品码和该SaaS Boost公网访问地址链接至您售卖的服务中。如下图所示：

4. **支付宝沙箱测试**：在正式售卖前，请利用支付宝沙箱APP测试售卖能力。
    1. 如果在创建计算巢服务实例处选择跳过配置支付宝信息。则在使用前需要进行配置。见[对支付宝服务的配置进行修改](#对支付宝服务的配置进行修改)
    2. 在支付宝沙箱工具中下载支付宝客户端沙箱版安卓APP
       ![14.png](https://ucc.alicdn.com/pic/developer-ecology/zxldgoa2vzjb2_a3b282edfb1d48d79d3a69696e9f9495.png)

    3. 选择想要创建的服务实例套餐





![15.png](https://ucc.alicdn.com/pic/developer-ecology/zxldgoa2vzjb2_fdcafa94e822499e86f8986383dbed08.png)

    4. 使用沙箱应用中提供的账号密码进行支付测试。

    5. 查看已部署的服务实例详情
![16.png](https://ucc.alicdn.com/pic/developer-ecology/zxldgoa2vzjb2_39d8cfd8bfcc4e9c8244fd7921888be8.png)


    6. 查看已部署服务实例监控

![17.png](https://ucc.alicdn.com/pic/developer-ecology/zxldgoa2vzjb2_717f4e7eaab44ef69300ca7fbe6988f7.png)

    7. 查看历史订单
![18.png](https://ucc.alicdn.com/pic/developer-ecology/zxldgoa2vzjb2_90696c3f16754529b258aab42901c4b1.png)
8. 在通过沙箱测试后，务必再次使用正式的支付宝账号进行售卖测试。

## 开发者指南

### 快速创建服务

如果您还没有为您即将售卖的软件创建计算巢服务。请基于以下文档快速创建计算巢服务。或查看[官方文档](https://help.aliyun.com/zh/compute-nest/create-services/?spm=a2c4g.11186623.0.0.4c775cf3ToRZLg)

1. 登录到[计算巢控制台](https://computenest.console.aliyun.com/)，选择"新建服务"
   ![service-creation-1.png](https://ucc.alicdn.com/pic/developer-ecology/zxldgoa2vzjb2_ed9d2ea127ff4b1d9c06998cb20982a6.png)

2. 仿照下图配置，选择托管版服务和Spring Boot服务。
   ![service-creation-2.png](https://ucc.alicdn.com/pic/developer-ecology/zxldgoa2vzjb2_e257d5623ef54f5596ffb68eefb06b96.png)
3. 填写配置参数
   ![service-creation-3.png](https://ucc.alicdn.com/pic/developer-ecology/zxldgoa2vzjb2_d97277235a8b40278f5fea9d0d29e5af.png)

4. 点击"创建服务"，等待服务创建成功。
5. 后续当您想对您软件部署物进行修改时，可对该服务进行编辑，重新选择部署物。
   ![service-creation-4.png](https://ucc.alicdn.com/pic/developer-ecology/zxldgoa2vzjb2_321ecc2ae7d8451ba590533d8b10de5a.png)


### Saas Boost 商品与套餐管理指南

本文档旨在指导如何在Saas Boost平台上适当地管理和定价商品，以及如何保持商品与计算巢(Calculating Nest)服务和套餐之间的一致性。

#### 商品与服务的关联

在Saas Boost平台上创建的每个商品应对应计算巢中的单个服务。商家可基于此服务为商品设定一个基础价格。

##### 创建商品

1. 登录到Saas Boost。
2. 导航至**商品管理**页面。
3. 点击**新增商品**按钮进入创建界面。
4. 输入服务相关信息，并在`服务ID`字段中填写对应的计算巢服务ID。
5. 完整填写商品信息，包括名称、描述和基础定价。
6. 点击提交，完成商品创建。
   ![image.png](https://ucc.alicdn.com/pic/developer-ecology/zxldgoa2vzjb2_028f2f15dddc41dcaf315e6a7bde1e14.png)

#### 套餐的创建与定价

在Saas Boost中为商品设定的每个套餐应对应计算巢中的相应服务套餐。每个套餐的名称必须与计算巢中的套餐名称完全一致。在这个过程中，开发者应该先在计算巢中进行[套餐配置](https://help.aliyun.com/zh/compute-nest/create-package-settings-for-a-service?spm=a2c4g.11174283.0.i5)，而后在SaaS Boost中对上述配置的套餐定制价格。


##### 套餐命名统一

为保证用户体验的一致性，请确保计算巢中的套餐名称与Saas Boost上的套餐名称相匹配。

##### 添加SaaS Boost套餐与定价

1. 选择已创建好的商品，并进入其套餐管理界面。
2. 点击**新增套餐**，进入套餐详细配置页面。
3. 在`套餐名`字段中准确输入计算巢中对应套餐的名称。
4. 根据套餐配置设定一个定价。
5. 确认信息无误后保存新套餐。
   计算巢处套餐配置示例如下所示：
   ![image.png](https://ucc.alicdn.com/pic/developer-ecology/zxldgoa2vzjb2_ac1818ac59a6403a98936bbeead1d54e.png)
   SaaS Boost处套餐示例如下所示：
   ![image.png](https://ucc.alicdn.com/pic/developer-ecology/zxldgoa2vzjb2_695f226263674373a2ea2b9224a837ed.png)


#### 注意要点

- 保持Saas Boost上的套餐名称与计算巢中套餐的名称保持严格一致。
- 定价时要考虑对应计算巢服务套餐的具体配置。
- 定期审核和更新Saas Boost与计算巢之间的套餐对应关系，以保持数据的最新性和准确性。

遵循这些指导原则，商家可以保证Saas Boost平台上的商品与计算巢中服务和套餐的适当整合和价格一致性。


### 修改配置参数

在创建SaaS Boost一键部署实例时，我们配置了一些加密参数。如认证服务-OAuth的Client Id和Client
Secret，以及支付宝的处的私钥，应用网关地址和授权回调地址。
当您需要对这些修改时请遵循一下操作：

#### 对支付宝服务的配置进行修改

支付宝的配置项包括支付宝公钥、支付宝私钥、支付宝网关地址、授权回调地址。

1. 打开部署的服务实例详情页面，复制支付宝的同步通知URL和支付宝的异步通知URL。如红框所示。
   ![image.png](https://ucc.alicdn.com/pic/developer-ecology/zxldgoa2vzjb2_bcc3dcde0ba54c2b921b0a9ba3b110a3.png)

2. 在阿里云控制台，访问[运维编排-OOS工作台](https://oos.console.aliyun.com/cn-beijing/parameter/welcome)。点击参数仓库。
   ![service-config-2.png](https://ucc.alicdn.com/pic/developer-ecology/zxldgoa2vzjb2_f5d35ad4f45f4e179f450bc29245aea0.png)

3. 选择地域为服务实例部署地域，点击加密参数栏，在搜索框中搜索OOS加密参数名
   ![service-config-3.png](https://ucc.alicdn.com/pic/developer-ecology/zxldgoa2vzjb2_83ed45c8b7614ca0a9ec1bece3a5505d.png)
4. 点击进入加密参数编辑界面进行修改和保存即可。

5. 需要登录到支付宝（沙箱）[控制台](https://open.alipay.com/develop/sandbox/app)。对应用网关地址，授权回调地址和应用公钥进行修改和设置。
   ![image.png](https://ucc.alicdn.com/pic/developer-ecology/zxldgoa2vzjb2_fe6cf03b0c12494ca35f2fb5c11263e8.png)
