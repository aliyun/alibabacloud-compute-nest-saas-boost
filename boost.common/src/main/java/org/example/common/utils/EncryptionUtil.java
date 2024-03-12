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

package org.example.common.utils;

import lombok.extern.slf4j.Slf4j;
import org.bouncycastle.crypto.digests.MD5Digest;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

@Slf4j
public class EncryptionUtil {
    
    public static String encode(byte[] data) {
        return Base64.getEncoder().encodeToString(data);
    }

    public static byte[] decode(String base64String) {
        return Base64.getDecoder().decode(base64String);
    }

    public static String getMd5HexString(String data) {
        if (data == null) {
            return null;
        }

        MD5Digest md5Digest = new MD5Digest();
        byte[] plain = data.getBytes(StandardCharsets.UTF_8);
        md5Digest.update(plain, 0, plain.length);
        byte[] md5Bytes = new byte[md5Digest.getDigestSize()];
        md5Digest.doFinal(md5Bytes, 0);
        return bytesToHex(md5Bytes);
    }

    /**
     * 字节数组转十六进制
     *
     * @param bytes
     * @return
     */
    public static String bytesToHex(byte[] bytes) {
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < bytes.length; i++) {
            String hex = Integer.toHexString(bytes[i] & 0xFF);
            if (hex.length() < 2) {
                sb.append(0);
            }
            sb.append(hex);
        }
        return sb.toString();
    }
}

