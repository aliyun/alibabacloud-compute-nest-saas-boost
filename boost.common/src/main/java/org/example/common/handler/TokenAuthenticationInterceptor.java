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

package org.example.common.handler;

import org.example.common.BaseResult;
import org.example.common.constant.Constants;
import org.example.common.errorinfo.ErrorInfo;
import org.example.common.helper.TokenParseHelper;
import org.example.common.model.JwtAuthenticationTokenModel;
import org.example.common.model.UserInfoModel;
import org.example.common.utils.JsonUtil;
import com.nimbusds.jwt.SignedJWT;
import lombok.extern.slf4j.Slf4j;

import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;

import static org.example.common.constant.Constants.STANDARD_CONTENT_TYPE;

@Slf4j
@Component
public class TokenAuthenticationInterceptor implements HandlerInterceptor {

    @Resource
    public TokenParseHelper tokenParseHelper;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        if (!(handler instanceof HandlerMethod)) {
            return true;
        }
        try {
            String token = request.getHeader(Constants.AUTHORIZATION);
            String singleToken = tokenParseHelper.parseBearerTokenToToken(token);
            SignedJWT signedJwt = tokenParseHelper.token2Jwt(singleToken);

            if (!(tokenParseHelper.verifySign(signedJwt))) {
                return setResponse(response);
            }

            UserInfoModel userInfoModel = tokenParseHelper.getUserInfoFromIdToken(singleToken);
            Collection<SimpleGrantedAuthority> authorities = new ArrayList<>();
            JwtAuthenticationTokenModel authentication = new JwtAuthenticationTokenModel(singleToken, userInfoModel, authorities);
            SecurityContextHolder.getContext().setAuthentication(authentication);
            authentication.setAuthenticated(true);
            return true;
        } catch (Exception e) {
            log.error("Token verification failed.", e);
            return setResponse(response);
        }
    }

    private boolean setResponse(HttpServletResponse response) {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType(STANDARD_CONTENT_TYPE);
        try {
            response.getWriter().write(JsonUtil.toJsonString(new BaseResult<>(ErrorInfo.VERIFY_FAILED.getCode(), ErrorInfo.VERIFY_FAILED.getMessage(), null)));
        } catch (IOException ex) {
            log.error("Token verification failed, Response definition failed.", ex);
        }
        return false;
    }
}
