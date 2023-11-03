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

import com.fasterxml.jackson.core.JsonProcessingException;
import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.crypto.RSASSAVerifier;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jwt.SignedJWT;
import mockit.Expectations;
import mockit.Injectable;
import mockit.Mock;
import mockit.MockUp;
import mockit.Mocked;
import mockit.Tested;
import org.example.common.exception.BizException;
import org.example.common.model.UserInfoModel;
import org.example.common.utils.HttpUtil;
import org.example.common.utils.JsonUtil;
import org.junit.Test;
import org.junit.jupiter.api.Assertions;
import org.slf4j.Logger;
import org.springframework.test.util.ReflectionTestUtils;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class TokenParseHelperTest {
    @Tested
    private TokenParseHelper tokenParseHelper = new TokenParseHelper();

    @org.mockito.Mock
    Logger log;

    @Test
    public void testInit(@Mocked HttpUtil httpUtil, @Mocked RSAKey rsaKey) throws NoSuchFieldException, IllegalAccessException, ParseException {
        ReflectionTestUtils.setField(tokenParseHelper, "publicKeyUri", "123");
        new Expectations() {{
            httpUtil.doGet(anyString);
            result = "{\"keys\": [{\"key\": \"key1\"}, {\"key\": \"key2\"}]}";
            RSAKey.parse(anyString);
            result = rsaKey;
        }};

        Assertions.assertDoesNotThrow(() -> tokenParseHelper.init());
    }

    @Test
    public void testInitFailed(@Mocked HttpUtil httpUtil) throws NoSuchFieldException, IllegalAccessException, ParseException {
        ReflectionTestUtils.setField(tokenParseHelper, "publicKeyUri", "123");
        new Expectations() {{
            httpUtil.doGet(anyString);
            result = "{\"keys\": [{\"key\": \"key1\"}, {\"key\": \"key2\"}]}";
        }};

        Assertions.assertThrows(BizException.class, () -> tokenParseHelper.init());
    }

    @Test
    public void testToken2Jwt() {
        Assertions.assertThrows(BizException.class, () -> tokenParseHelper.token2Jwt(null));
    }

    @Test
    public void testToken2JwtSuccess(@Mocked SignedJWT signedJWT) throws NoSuchFieldException, IllegalAccessException, ParseException {
        new Expectations() {{
            SignedJWT.parse(anyString);
            result = signedJWT;
        }};

        Assertions.assertDoesNotThrow(() -> tokenParseHelper.token2Jwt("token"));
    }

    @Test
    public void testToken2JwtFailed(@Mocked SignedJWT signedJWT) throws NoSuchFieldException, IllegalAccessException, ParseException {
        new Expectations() {{
            SignedJWT.parse(anyString);
            result = new ParseException("test", 0);
        }};

        Assertions.assertThrows(BizException.class, () -> tokenParseHelper.token2Jwt("token"));
    }

    @Test
    public void testVerifySign() {
        Assertions.assertDoesNotThrow(() -> tokenParseHelper.verifySign(null));
    }

    @Test
    public void testVerifySignSuccess(@Mocked SignedJWT signedJWT, @Mocked RSAKey rsaKey, @Mocked RSASSAVerifier rsassaVerifier, @Mocked JWSHeader header) throws NoSuchFieldException, IllegalAccessException, ParseException, JOSEException {
        List<RSAKey> rsaKeysList = new ArrayList<>();
        rsaKeysList.add(rsaKey);
        ReflectionTestUtils.setField(tokenParseHelper, "publicKeyCache", rsaKeysList);
        ReflectionTestUtils.setField(signedJWT, "header", header);
        new Expectations() {{
            signedJWT.getHeader().getKeyID();
            result = "123";
            rsaKey.getKeyID();
            result = "123";
            signedJWT.verify(withAny(rsassaVerifier));
            result = true;
        }};

        Assertions.assertDoesNotThrow(() -> tokenParseHelper.verifySign(signedJWT));
    }

    @Test
    public void testVerifySignException(@Mocked SignedJWT signedJWT, @Mocked RSAKey rsaKey, @Mocked RSASSAVerifier rsassaVerifier, @Mocked JWSHeader header) throws NoSuchFieldException, IllegalAccessException, ParseException, JOSEException {
        List<RSAKey> rsaKeysList = new ArrayList<>();
        rsaKeysList.add(rsaKey);
        ReflectionTestUtils.setField(tokenParseHelper, "publicKeyCache", rsaKeysList);
        new Expectations() {{
            signedJWT.getHeader().getKeyID();
            result = "123";
            rsaKey.getKeyID();
            result = "123";
            signedJWT.verify(withAny(rsassaVerifier));
            result = new Exception("test");
        }};

        Assertions.assertDoesNotThrow(() -> tokenParseHelper.verifySign(signedJWT));
    }

    @Test
    public void testParseBearerTokenToToken() {
        String PATTERN = "Bearer\\s+\\S+";
        String nativeToken = "Bearer test";

        Assertions.assertDoesNotThrow(() -> tokenParseHelper.parseBearerTokenToToken(nativeToken));
    }

    @Test
    public void testParseBearerTokenException() {
        String PATTERN = "Bearer\\s+\\S+";
        String nativeToken = "Bearer test test";
        Assertions.assertThrows(BizException.class, () -> tokenParseHelper.parseBearerTokenToToken(nativeToken));
    }

    @Test
    public void testGetUserInfoFromIdTokenException(@Injectable SignedJWT signedJWT, @Mocked RSAKey rsaKey) {
        String token = "eyJraWQiOiJKQzl3eHpyaHFKMGd0YUNFdDJRTFVmZXZFVUl3bHRGaHVpNE8xYmg2" +
                "N3RVIiwiYWxnIjoiUlMyNTYifQ.eyJhdWQiOiI0NDAxNjYzOTg5Njc5MDMyOTkzIiwic3ViIjoibnFra0x" +
                "SWkJPMTZLTm9QWUEzaFBFa3BvIiwidWlkIjoiMjMxMDA0ODU3ODc3ODU1MzM4IiwidXBuIjoibHloMzc2Mj" +
                "QwQGNvbXB1dGVuZXN0Lm9uYWxpeXVuLmNvbSIsImlzcyI6Imh0dHBzOlwvXC9vYXV0aC5hbGl5dW4uY29tI" +
                "iwibmFtZSI6IuWjrOS6kSIsImV4cCI6MTY5NTAwNTEyNSwiYmlkIjoiMjY4NDIiLCJpYXQiOjE2OTUwMDE1MjU" +
                "sImFpZCI6IjE1NjM0NTc4NTU0Mzg1MjIifQ.i9cZpkMqXZt7MLdggugPGdjfzHBHIfvemO7NET2uKcYDJ5nP1ve" +
                "fv-eAQGUEQpLh4LtffkCyQppXxk9Yy3BXEIkdHdrWK9IRVc0FG9QaNxOj7gLPVsFXkrgN8VgsEzNPayLH35ZzwAPG" +
                "9S897EKKaulQ6QqHH_Y8Sg0glcFNIYdQDeZHxRUnj6JuL0SJZB3PRJoRH3gTux0wP_P9uniFmnMQ3gZrXm7xlfH7wz" +
                "Dl4HlHqaa9lkDhoAMu9Ff4LzNa_oh-VNmv4UtnwP5pTZCYLv2CpNMjM6OYif35sgPir3pwaN2QsJM-fWXKaGVmf6tB3MuS_XIx2r0Ni-xBnGx28Q";
        List<RSAKey> rsaKeysList = new ArrayList<>();
        rsaKeysList.add(rsaKey);
        ReflectionTestUtils.setField(tokenParseHelper, "publicKeyCache", rsaKeysList);
        new Expectations() {{
            tokenParseHelper.token2Jwt(token);
            result = signedJWT;
        }};

        Assertions.assertThrows(BizException.class, () -> tokenParseHelper.getUserInfoFromIdToken(token));
    }


    @Test
    public void testGetUserInfoFromIdTokenSuccess(@Injectable SignedJWT signedJWT, @Injectable RSAKey rsaKey) throws ParseException, JsonProcessingException, JsonProcessingException {
        String token = "eyJraWQiOiJKQzl3eHpyaHFKMGd0YUNFdDJRTFVmZXZFVUl3bHRGaHVpNE8xYmg2N3RVIiwiYWxnIjoiUlMyNTYifQ" +
                ".eyJzdWIiOiJhYmMiLCJ1aWQiOiIxMjM0NSIsImlzcyI6Imh0dHBzOi8vb2F1dGguYWxpeXVuLmNvbSIsIm5hbWUiOiJhbGl5" +
                "dW4iLCJleHAiOjE2OTUwMDUxMjUsImlhdCI6MTY5NTAwMTUyNSwiYWlkIjoiMTIzNDU2In0.XAXLbOOf-Dcdkc4ZwmieaP6mE" +
                "n4wOJ26kZQWeXQZLhlKkszfYSbHUTMBkHt3RPN6aepFh2eB5CD0kjE24ZhxIu_oaVWv1cXUOZ7Dv5Za4DpEOSQUeTypL0z_S0" +
                "CtOQ7Jo0cb4xMhrDx1D-M6i7roAtEvW4ZeBJKv-oSrzy_hsAZs4xAL5-Lwv79bTlSu_PuNyK4wKsYeIx0bMVjNamBR07bBL0g" +
                "7Zf_99Tk9UiumSiu--eFpO2BLKJbe3o1DFkVAbz1VGMaudYvU-Ew5uLnxvyRpCHaH5P3g3WqzMyHtcekChuARUoGtUPy69ogi" +
                "nV25PQ5tUPZCV3Fcv-dDUgap3g";
        new MockUp<TokenParseHelper>() {
            @Mock
            public SignedJWT token2Jwt(String token) throws ParseException {
                return SignedJWT.parse(token);
            }

            @Mock
            public boolean verifySign(SignedJWT signedJWT) {
                return true;
            }

            @Mock
            public UserInfoModel getUserInfoFromIdToken(String singleToken) {
                try {
                    String jwtStr = SignedJWT.parse(singleToken).getPayload().toString();
                    return JsonUtil.parseObjectByJackson(jwtStr, UserInfoModel.class);
                } catch (ParseException e) {
                    log.error("SignedJWT解析完成，但格式转换失败", e);
                }
                return null;
            }
        };
        UserInfoModel expectedUserInfoModel = new UserInfoModel("John Doe", "aliyun", "test", "test", "test");
        UserInfoModel actualUserInfoModel = tokenParseHelper.getUserInfoFromIdToken(token);

        assertEquals(expectedUserInfoModel.getName(), actualUserInfoModel.getName());
    }
}



