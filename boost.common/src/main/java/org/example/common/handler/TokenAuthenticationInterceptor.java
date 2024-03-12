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

import com.nimbusds.jwt.SignedJWT;
import lombok.extern.slf4j.Slf4j;
import org.example.common.AdminAPI;
import org.example.common.BaseResult;
import org.example.common.constant.Constants;
import org.example.common.errorinfo.ErrorInfo;
import org.example.common.helper.TokenParseHelper;
import org.example.common.model.JwtAuthenticationTokenModel;
import org.example.common.model.UserInfoModel;
import org.example.common.utils.JsonUtil;
import org.springframework.beans.factory.annotation.Value;
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

    @Value("${service.admin.aid}")
    private String adminAid;

    @Resource
    public TokenParseHelper tokenParseHelper;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        if (!(handler instanceof HandlerMethod)) {
            return true;
        }
        HandlerMethod handlerMethod = (HandlerMethod) handler;

        try {
            AdminAPI adminOnly = handlerMethod.getMethodAnnotation(AdminAPI.class);

            String token = request.getHeader(Constants.AUTHORIZATION);
            String singleToken = tokenParseHelper.parseBearerTokenToToken(token);
            SignedJWT signedJwt = tokenParseHelper.token2Jwt(singleToken);

            if (!(tokenParseHelper.verifySign(signedJwt))) {
                return setResponse(response, ErrorInfo.SIGNATURE_PARSED_FAILED);
            }

            UserInfoModel userInfoModel = tokenParseHelper.getUserInfoFromIdToken(singleToken);
            boolean adminUser = isAdminUser(userInfoModel);
            userInfoModel.setAdmin(adminUser);
            if (adminOnly != null && !adminUser) {
                setResponse(response, ErrorInfo.USER_NOT_ADMIN);
            }

            Collection<SimpleGrantedAuthority> authorities = new ArrayList<>();
            JwtAuthenticationTokenModel authentication = new JwtAuthenticationTokenModel(singleToken, userInfoModel, authorities);
            SecurityContextHolder.getContext().setAuthentication(authentication);
            authentication.setAuthenticated(true);
            return true;
        } catch (Exception e) {
            log.error("Token verification failed.", e);
            return setResponse(response, ErrorInfo.VERIFY_FAILED);
        }
    }

    private boolean isAdminUser(UserInfoModel userInfoModel) {
        return adminAid.equals(userInfoModel.getAid());
    }

    private boolean setResponse(HttpServletResponse response, ErrorInfo errorInfo) {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType(STANDARD_CONTENT_TYPE);
        try {
            response.getWriter().write(JsonUtil.toJsonString(new BaseResult<>(errorInfo.getCode(), errorInfo.getMessage(), null)));
        } catch (IOException ex) {
            log.error("Token verification failed, Response definition failed.", ex);
        }
        return false;
    }
}
