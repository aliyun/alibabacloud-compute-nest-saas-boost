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

import { PageContainer } from '@ant-design/pro-components';
import { useModel } from '@umijs/max';
import { Card, theme } from 'antd';
import React, {ReactNode} from 'react';

/**
 * 每个单独的卡片，为了复用样式抽成了组件
 * @param param0
 * @returns
 */
const InfoCard: React.FC<{
  title: string;
  index: number;
  desc: string;
  href: string;
}> = ({ title, href, index, desc }) => {
  const { useToken } = theme;

  const { token } = useToken();

  return (
    <div
      style={{
        backgroundColor: token.colorBgContainer,
        boxShadow: token.boxShadow,
        borderRadius: '8px',
        fontSize: '14px',
        color: token.colorTextSecondary,
        lineHeight: '22px',
        padding: '16px 19px',
        minWidth: '220px',
        flex: 1,
      }}
    >
      <div
        style={{
          display: 'flex',
          gap: '4px',
          alignItems: 'center',
        }}
      >
        <div
          style={{
            width: 48,
            height: 48,
            lineHeight: '22px',
            backgroundSize: '100%',
            textAlign: 'center',
            padding: '8px 16px 16px 12px',
            color: '#FFF',
            fontWeight: 'bold',
            backgroundImage:
              "url('https://gw.alipayobjects.com/zos/bmw-prod/daaf8d50-8e6d-4251-905d-676a24ddfa12.svg')",
          }}
        >
          {index}
        </div>
        <div
          style={{
            fontSize: '16px',
            color: token.colorText,
            paddingBottom: 8,
          }}
        >
          {title}
        </div>
      </div>
      <div
        style={{
          fontSize: '14px',
          color: token.colorTextSecondary,
          textAlign: 'justify',
          lineHeight: '22px',
          marginBottom: 8,
        }}
      >
        {desc}
      </div>
      <a href={href} target="_blank" rel="noreferrer">
        了解更多 {'>'}
      </a>
    </div>
  );
};

const Welcome: React.FC = () => {
  const { token } = theme.useToken();
  const { initialState } = useModel('@@initialState');
  return (
    <PageContainer>
      <Card
        style={{
          borderRadius: 8,
        }}
        bodyStyle={{
          backgroundImage:
            initialState?.settings?.navTheme === 'realDark'
              ? 'background-image: linear-gradient(75deg, #1A1B1F 0%, #191C1F 100%)'
              : 'background-image: linear-gradient(75deg, #FBFDFF 0%, #F5F7FF 100%)',
        }}
      >
        <div
          style={{
            backgroundPosition: '100% -30%',
            backgroundRepeat: 'no-repeat',
            backgroundSize: '274px auto',
            backgroundImage:
              "url('https://gw.alipayobjects.com/mdn/rms_a9745b/afts/img/A*BuFmQqsB2iAAAAAAAAAAAAAAARQnAQ')",
          }}
        >
          <div
            style={{
              fontSize: '20px',
              color: token.colorTextHeading,
            }}
          >
            欢迎使用 阿里云计算巢 SaaS Boost
          </div>
          <p
            style={{
              fontSize: '14px',
              color: token.colorTextSecondary,
              lineHeight: '22px',
              marginTop: 16,
              marginBottom: 32,
              width: '65%',
            }}
          >
              计算巢SaaS Boost是由阿里云推出的一款开发工具和框架，旨在帮助软件即服务（SaaS）开发者快速构建、部署、扩展和售卖SaaS应用程序。它提供了一组开箱即用的功能和组件，使用户聚焦于业务逻辑的开发，而极大地降低SaaS应用程序开发的复杂性和成本。

          </p>
          <div
            style={{
              display: 'flex',
              flexWrap: 'wrap',
              gap: 16,
            }}
          >
            <InfoCard
              index={1}
              href="https://aliyun.github.io/alibabacloud-compute-nest-saas-boost/"
              title="了解 SaaS Boost"
              desc="SaaS Boost集成了计算巢，拥有多租户架构、持续集成和持续交付（CI/CD）能力，以及套餐管理和应用程序监控功能。这些功能加速了传统软件开发者向SaaS化转型的过程，同时降低了SaaS化转型所需的成本。用户能够轻松、顺畅地在一周内完成从传统软件服务商到SaaS软件服务商的身份转变，上云不再是难题。"
            />
            <InfoCard
              index={2}
              title="了解 计算巢"
              href="https://help.aliyun.com/zh/compute-nest/product-overview/what-is-compute-nest?spm=a2c4g.11186623.0.0.74fc584aVykL3Q"
              desc="计算巢服务是一个开放给服务商和用户的服务管理PaaS平台。计算巢服务为服务商和用户提供了高效、便捷、安全的服务使用体验，服务商能更好地在阿里云上部署、交付和管理服务，用户能集中管理在阿里云上订阅的各类服务商提供的服务。"
            />
            <InfoCard
              index={3}
              title="了解 阿里云"
              href="https://aliyun.com"
              desc="阿里云（Alibaba Cloud）是阿里巴巴集团旗下的云计算服务提供商。作为全球领先的云计算平台之一，阿里云为个人、企业和政府机构提供了广泛的云计算产品和服务。阿里云致力于帮助用户实现数字化转型，提升业务竞争力，并在云计算领域为用户提供全方位的支持和解决方案。"
            />
          </div>
        </div>
      </Card>
    </PageContainer>
  );
};

export default Welcome;
