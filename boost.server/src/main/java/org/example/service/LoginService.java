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

package org.example.service;

import org.example.common.BaseResult;
import org.example.common.model.AuthTokenModel;
import org.example.common.model.UserInfoModel;
import org.example.common.param.GetAuthTokenParam;
import org.springframework.security.core.annotation.AuthenticationPrincipal;

public interface LoginService {

    /**
     * Get user info : userid, ali uid...
     * @param userInfoModel idToken
     * @return {@link BaseResult < UserInfo >}
     */
    BaseResult<UserInfoModel> getUserInfo(@AuthenticationPrincipal UserInfoModel userInfoModel);

    /**
     * Based on OAuth protocol
     * @param getAuthTokenParam  authentication param
     * @return {@link BaseResult<AuthTokenModel>} idToken and refreshToken
     */
    BaseResult<AuthTokenModel> getAuthToken(GetAuthTokenParam getAuthTokenParam);
}
