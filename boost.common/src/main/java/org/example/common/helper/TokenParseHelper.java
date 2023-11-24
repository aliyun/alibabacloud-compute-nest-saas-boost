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

package org.example.common.helper;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.nimbusds.jose.crypto.RSASSAVerifier;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jwt.SignedJWT;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.example.common.errorinfo.ErrorInfo;
import org.example.common.exception.BizException;
import org.example.common.model.UserInfoModel;
import org.example.common.utils.HttpUtil;
import org.example.common.utils.JsonUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
@Slf4j
public class TokenParseHelper {

    @Value("${oauth.public-key.url}")
    private String publicKeyUri;

    private static final String PATTERN = "Bearer\\s+\\S+";

    private static final int TOKEN_LEN = 2;

    public List<RSAKey> publicKeyCache;

    @PostConstruct
    public void init() {
        String keys = Objects.requireNonNull(JSON.parseObject(HttpUtil.doGet(publicKeyUri))).getString("keys");
        publicKeyCache = new ArrayList<>();
        try {
            JSONArray publicKeyList = JSON.parseArray(keys);
            for (Object object : publicKeyList) {
                RSAKey rsaKey = RSAKey.parse(JSONObject.toJSONString(object));
                publicKeyCache.add(rsaKey);
            }
        } catch (Exception e) {
            log.error("Failed to obtain public key, uri = {}.", publicKeyUri, e);
            throw new BizException(ErrorInfo.SERVER_UNAVAILABLE.getStatusCode(), ErrorInfo.SERVER_UNAVAILABLE.getCode(), ErrorInfo.SERVER_UNAVAILABLE.getMessage());
        }
    }

    public SignedJWT token2Jwt(String token) {
        if (token == null) {
            log.error("Token is null.");
            throw new BizException(ErrorInfo.VERIFY_FAILED.getStatusCode(), ErrorInfo.VERIFY_FAILED.getCode(), ErrorInfo.VERIFY_FAILED.getMessage());
        }
        try {
            return SignedJWT.parse(token);
        } catch (ParseException e) {
            log.error("Token to JWT Error,message = {}", e.getMessage());
            throw new BizException(ErrorInfo.VERIFY_FAILED.getStatusCode(), ErrorInfo.VERIFY_FAILED.getCode(), ErrorInfo.VERIFY_FAILED.getMessage());
        }
    }


    public boolean verifySign(SignedJWT signedJwt) {
        if (null == signedJwt) {
            log.error("Input signed Jwt is null.");
            return false;
        }
        List<RSAKey> publicKeyList = this.publicKeyCache;
        RSAKey rsaKey = null;
        for (RSAKey key : publicKeyList) {
            if (signedJwt.getHeader().getKeyID().equals(key.getKeyID())) {
                rsaKey = key;
            }
        }
        if (rsaKey != null) {
            try {
                RSASSAVerifier rsassaVerifier = new RSASSAVerifier(rsaKey.toRSAPublicKey());
                if (signedJwt.verify(rsassaVerifier)) {
                    return true;
                }
            } catch (Exception e) {
                log.error("Can't verify signature for id token.");
            }
        }
        return false;
    }

    public String parseBearerTokenToToken(String nativeToken) {
        if (StringUtils.isNotEmpty(nativeToken)) {
            Matcher matcher = Pattern.compile(PATTERN).matcher(nativeToken);
            boolean match = matcher.matches();
            if (match) {
                String[] token = nativeToken.split(" ");
                if (token.length == TOKEN_LEN) {
                    return token[1];
                }
            }
        }
        log.error("Failed to extract token from the bearer token.");
        throw new BizException(ErrorInfo.VERIFY_FAILED.getStatusCode(), ErrorInfo.VERIFY_FAILED.getCode(), ErrorInfo.VERIFY_FAILED.getMessage());
    }


    public UserInfoModel getUserInfoFromIdToken(String singleToken) {
        SignedJWT signedJwt = token2Jwt(singleToken);
        if (verifySign(signedJwt)) {
            String jwtStr = signedJwt.getPayload().toString();
            return JsonUtil.parseObjectByJackson(jwtStr, UserInfoModel.class);
        }
        log.error("Verifying SignedJWT Failed.");
        throw new BizException(ErrorInfo.VERIFY_FAILED.getStatusCode(), ErrorInfo.VERIFY_FAILED.getCode(), ErrorInfo.VERIFY_FAILED.getMessage());
    }
}


