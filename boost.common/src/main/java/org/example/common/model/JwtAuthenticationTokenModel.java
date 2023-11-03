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

package org.example.common.model;

import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;



@Slf4j
public class JwtAuthenticationTokenModel extends AbstractAuthenticationToken {

    private final String token;

    private final UserInfoModel userInfoModel;

    public JwtAuthenticationTokenModel(String token) {
        super(null);
        this.token = token;
        this.userInfoModel = null;
        setAuthenticated(true);
    }

    public JwtAuthenticationTokenModel(String token, UserInfoModel userInfoModel, Collection<? extends GrantedAuthority> authorities) {
        super(authorities);
        this.token = token;
        this.userInfoModel = userInfoModel;
        setAuthenticated(true);
    }

    // Get authenticated user information
    @Override
    public Object getCredentials() {
        return token;
    }

    // Get current user information
    @Override
    public Object getPrincipal() {
        return userInfoModel;
    }


}
