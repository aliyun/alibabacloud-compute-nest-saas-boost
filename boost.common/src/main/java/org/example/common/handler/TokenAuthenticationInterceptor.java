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
import org.apache.commons.lang3.StringUtils;
import org.example.common.AdminAPI;
import org.example.common.BaseResult;
import org.example.common.SPI;
import org.example.common.config.OosSecretParamConfig;
import org.example.common.constant.Constants;
import org.example.common.errorinfo.ErrorInfo;
import org.example.common.helper.SpiTokenHelper;
import org.example.common.helper.TokenParseHelper;
import org.example.common.model.JwtAuthenticationTokenModel;
import org.example.common.model.UserInfoModel;
import org.example.common.utils.JsonUtil;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.StreamUtils;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.annotation.Resource;
import javax.servlet.ReadListener;
import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collection;

import static org.example.common.constant.AliPayConstants.OOS_SECRET_ADMIN_AID;
import static org.example.common.constant.AliPayConstants.OOS_SECRET_APP_ID;
import static org.example.common.constant.Constants.SERVICE_INSTANCE_ID;
import static org.example.common.constant.Constants.STANDARD_CONTENT_TYPE;

@Slf4j
@Component
public class TokenAuthenticationInterceptor implements HandlerInterceptor {

    @Resource
    private OosSecretParamConfig oosSecretParamConfig;

    @Resource
    private TokenParseHelper tokenParseHelper;

    @Resource
    private SpiTokenHelper spiTokenHelper;

    private static final String SPI_PREFIX = "/api/spi";

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        if (!(handler instanceof HandlerMethod)) {
            return true;
        }

        try {
            HandlerMethod handlerMethod = (HandlerMethod) handler;
            String requestPath = request.getRequestURI();
            SPI spi = handlerMethod.getMethodAnnotation(SPI.class);
            boolean spiUrl = requestPath.startsWith(SPI_PREFIX);

            if (spi != null || spiUrl) {
                return handleSpi(request, response, spi, spiUrl);
            }

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

    private boolean handleSpi(HttpServletRequest request, HttpServletResponse response, SPI spi, boolean spiUrl) {
        if (spiUrl && spi != null) {
            Class<?> classValue = spi.value();
            try {
                ReReadableHttpServletRequestWrapper reReadableHttpServletRequestWrapper = new ReReadableHttpServletRequestWrapper(request);

                String requestBody = StreamUtils.copyToString(reReadableHttpServletRequestWrapper.getInputStream(), StandardCharsets.UTF_8);
                Object spiObject = JsonUtil.parseObjectByJackson(requestBody, classValue);

                return spiTokenHelper.checkSpiToken(spiObject, classValue);
            } catch (Exception e) {
                log.error("SPI request body parsing failed.", e);
            }
        }
        setResponse(response, ErrorInfo.VERIFY_FAILED);
        return false;
    }


    private boolean isAdminUser(UserInfoModel userInfoModel) {

        String secretValue = oosSecretParamConfig.getSecretValue(OOS_SECRET_ADMIN_AID);
        if (StringUtils.isNotEmpty(secretValue) && StringUtils.isNotEmpty(userInfoModel.getAid())) {
            log.info("Admin user check, secretValue: {}, userInfoModel: {}", secretValue, userInfoModel.getAid());
            return secretValue.equals(userInfoModel.getAid());
        }
        return false;
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

    private static class ReReadableHttpServletRequestWrapper extends HttpServletRequestWrapper {
        private ByteArrayOutputStream cachedBodyOutputStream;

        public ReReadableHttpServletRequestWrapper(HttpServletRequest request) throws IOException {
            super(request);
            InputStream requestInputStream = request.getInputStream();
            cachedBodyOutputStream = new ByteArrayOutputStream();
            byte[] buffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = requestInputStream.read(buffer)) != -1) {
                cachedBodyOutputStream.write(buffer, 0, bytesRead);
            }
        }

        @Override
        public ServletInputStream getInputStream() {
            return new CachedBodyServletInputStream(cachedBodyOutputStream.toByteArray());
        }

        private class CachedBodyServletInputStream extends ServletInputStream {
            private ByteArrayInputStream input;

            public CachedBodyServletInputStream(byte[] cachedBody) {
                this.input = new ByteArrayInputStream(cachedBody);
            }

            @Override
            public int read() throws IOException {
                return input.read();
            }

            @Override
            public boolean isFinished() {
                return input.available() == 0;
            }

            @Override
            public boolean isReady() {
                return true;
            }

            @Override
            public void setReadListener(ReadListener readListener) {
                throw new RuntimeException("Not implemented");
            }
        }
    }
}
