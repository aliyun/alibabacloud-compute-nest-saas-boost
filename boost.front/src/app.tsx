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

import { Footer, Question, SelectLang, AvatarDropdown, AvatarName } from '@/components';
import { LinkOutlined } from '@ant-design/icons';
import type { Settings as LayoutSettings } from '@ant-design/pro-components';
import { SettingDrawer } from '@ant-design/pro-components';
import type {RequestConfig, RunTimeLayoutConfig} from '@umijs/max';
import { history, Link } from '@umijs/max';
import defaultSettings from '../config/defaultSettings';
import { errorConfig } from './requestErrorConfig';
import React from 'react';
const isDev = process.env.NODE_ENV === 'development';
const loginPath = '/user/login';
import {redirectLogin, getTicket} from "@/session";
import {getUserInfo} from "@/services/backend/user";
import {Provider, useSelector} from 'react-redux';
import { store } from './store';
import {RootState} from "@/store/state";
/**
 * @see  https://umijs.org/zh-CN/plugins/plugin-initial-state
 * */

// src/app.tsx
const authHeaderInterceptor = (url: string, options: RequestConfig) => {
  const ticket = getTicket()
  let authHeader = {}
  if (ticket !== undefined){
    authHeader = { Authorization: 'Bearer ' + ticket.token };
  }
  return {
    url: `${url}`,
    options: { ...options, interceptors: true, headers: authHeader },
  };
};

export async function getInitialState(): Promise<{
  settings?: Partial<LayoutSettings>;
  currentUser?: API.UserInfoModel;
  loading?: boolean;
  fetchUserInfo?: () => Promise<API.UserInfoModel | undefined>;
}> {
  const fetchUserInfo = async () => {
    const ticket = getTicket()
    if (ticket === undefined) {
      await redirectLogin()
    }
    const userInfoResponse = await getUserInfo() as API.BaseResultUserInfoModel_;
    return userInfoResponse.data
  };
  const updateSettings = {
    ...defaultSettings,
    title: '',
    logo: '',
  };
  // 如果不是登录页面，执行
  const { location } = history;
  if (location.pathname !== loginPath) {
    const currentUser = await fetchUserInfo();
    return {
      fetchUserInfo,
      currentUser,
      settings: updateSettings as Partial<LayoutSettings>,
    };
  }
  return {
    fetchUserInfo,
    settings: updateSettings as Partial<LayoutSettings>,
  };
}

// ProLayout 支持的api https://procomponents.ant.design/components/layout
export const layout: RunTimeLayoutConfig = ({ initialState, setInitialState }) => {
  const providerInfo = useSelector((state: RootState) => ({
    name: state.providerInfo.providerName,
    link: state.providerInfo.providerOfficialLink,
    description: state.providerInfo.providerDescription,
    logoUrl: state.providerInfo.providerLogoUrl,
  }));
  const updateSettings = {
    ...initialState?.settings,
    title: providerInfo?.name ? providerInfo.name : '',
    logo: providerInfo?.logoUrl ? providerInfo.logoUrl : '',
  };

  return {
    actionsRender: () => [<Question key="doc" />, <SelectLang key="SelectLang" />],
    menuDataRender: (menuData) => {
      // 根据用户角色过滤菜单项
      if (initialState?.currentUser?.admin) {
        // 如果用户是 admin，则返回完整菜单
        return menuData;
      } else {
        console.log('not admin');
        // 如果不是 admin，过滤掉 'list.commodity-list' 菜单项
        return menuData.filter((item) => item.name !== 'list.commodity-list');
      }
    },
    avatarProps: {
      // @ts-ignore
      src: initialState?.currentUser?.avatar,
      title: <AvatarName />,
      render: (_, avatarChildren) => {
        return <AvatarDropdown>{avatarChildren}</AvatarDropdown>;
      },
    },
    waterMarkProps: {
      content: initialState?.currentUser?.name,
    },
    footerRender: () => <Footer />,
    onPageChange: () => {
      const { location } = history;
      // 如果没有登录，重定向到 login
      if (!initialState?.currentUser && location.pathname != loginPath) {
        redirectLogin()
      }
    },
    layoutBgImgList: [
      {
        src: 'https://mdn.alipayobjects.com/yuyan_qk0oxh/afts/img/D2LWSqNny4sAAAAAAAAAAAAAFl94AQBr',
        left: 85,
        bottom: 100,
        height: '303px',
      },
      {
        src: 'https://mdn.alipayobjects.com/yuyan_qk0oxh/afts/img/C2TWRpJpiC0AAAAAAAAAAAAAFl94AQBr',
        bottom: -68,
        right: -45,
        height: '303px',
      },
      {
        src: 'https://mdn.alipayobjects.com/yuyan_qk0oxh/afts/img/F6vSTbj8KpYAAAAAAAAAAAAAFl94AQBr',
        bottom: 0,
        left: 0,
        width: '331px',
      },
    ],
    links: isDev
        ? [
          <Link key="openapi" to="/umi/plugin/openapi" target="_blank">
            <LinkOutlined />
            <span>OpenAPI 文档</span>
          </Link>,
        ]
        : [],
    menuHeaderRender: undefined,
    // 自定义 403 页面
    // unAccessible: <div>unAccessible</div>,
    // 增加一个 loading 的状态
    childrenRender: (children) => {
      // if (initialState?.loading) return <PageLoading />;
      return (
          <>
            {children}
            {isDev && (
                <SettingDrawer
                    disableUrlParams
                    enableDarkTheme
                    settings={updateSettings}
                    onSettingChange={(settings) => {
                      setInitialState((preInitialState) => ({
                        ...preInitialState,
                        settings,
                      }));
                    }}
                />
            )}
          </>
      );
    },
    ...updateSettings,
    logo: <img src={updateSettings?.logo}/>,
  };
};

/**
 * @name request 配置，可以配置错误处理
 * 它基于 axios 和 ahooks 的 useRequest 提供了一套统一的网络请求和错误处理方案。
 * @doc https://umijs.org/docs/max/request#配置
 */
export const request = {
  ...errorConfig,
  requestInterceptors: [authHeaderInterceptor],

};

export function rootContainer(container: React.ReactNode) {
  return <Provider store={store}>{container}</Provider>;
}