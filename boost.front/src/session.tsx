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

import {getAuthToken} from "@/services/backend/user";

import clientConfig from "./config";
import {randomStateUtil} from "@/util/randomStateUtil";
import {LOGIN_CONSTANTS} from "@/constants";

function getUrlParameter(parameterName:string) {
  let queryString = window.location.search
  // 使用 URLSearchParams 对象解析查询参数
  let params = new URLSearchParams(queryString);
  // 判断是否存在特定参数
  if (params.has(parameterName)) {
    console.log("URL contains "+parameterName+" parameter.");
    return params.get(parameterName)
  }
  return ''
}

export function logout() {
  document.cookie = 'ticket=;path=/;'
}

export function getTicket() {
  const cookies = document.cookie.split(";")
  let ticket = undefined
  for (let i = 0; i < cookies.length; i++) {
    const parts = cookies[i].split("=");
    if (parts[0].trim() === "ticket") {
     const cookieValue = decodeURIComponent(parts[1].trim());
     if(cookieValue === "") {
        return undefined
     }
     ticket = JSON.parse(cookieValue);
     break;
    }
  }
  return ticket
}

export async function redirectLogin() {

  const urlCode = getUrlParameter(LOGIN_CONSTANTS.CODE)
  const urlState = getUrlParameter(LOGIN_CONSTANTS.STATE)
  const urlSessionState = getUrlParameter(LOGIN_CONSTANTS.SESSION_STATE)
      // 获取当前页面的URL

    const url = window.location.href;
    let baseUrl = url;
    // 查找参数部分的起始位置
    const startIndex = url.indexOf('?');

    // 如果存在参数，则去掉参数
    if (startIndex !== -1) {
      baseUrl = url.substring(0, startIndex);
    }
  console.log(urlCode);

  if ( urlCode !== '') {
    const authToken: API.BaseResultAuthTokenModel_ = await getAuthToken({
      code : urlCode,
      redirectUri : baseUrl,
      sessionState : urlSessionState,
      state : urlState
    } as API.getAuthTokenParams)

    const token = authToken.data?.id_token
    const ticket = {
      token: token
    }
    console.log(token)
    const cookieValue = encodeURIComponent(JSON.stringify(ticket));
    document.cookie = `ticket=${cookieValue}; path=/`;
    const urlParams = new URL(window.location.href).searchParams;
    console.log(urlParams);
    /** 此方法会跳转到 redirect 参数所在的位置 */
    let redirect = urlParams.get('redirect');
    if (!redirect) {
      window.location.href = baseUrl;
    } else {
      window.location.href = filterInvalidPaths(url);
    }
  } else {
    const {  redirectUriPrefix, clientId} = clientConfig;
    let href = window.location.href;
    href = filterInvalidPaths(href);
    let redirectUrl = new URL(href);
    redirectUrl.hash = '';
    //生成一个长度在6-9之间的随机数字字符串
    const state : String = randomStateUtil(Math.floor(Math.random() * 4) + 6);
    let redirectUri = `${redirectUriPrefix}?response_type=code&client_id=${clientId}&redirect_uri=${redirectUrl}&state=${state}`
    if (redirectUriPrefix != "https://signin.aliyun.com/oauth2/v1/auth") {
      redirectUri = redirectUri + '&scope=openid'
    }
    window.location.href = redirectUri
  }
  //history.push(loginPath);
}
function filterInvalidPaths(url:string){
  const startIndex = url.indexOf('?');
  if (startIndex !== -1) {
    url = url.substring(0, startIndex);
  }
  return url;
}


