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

import com.aliyuncs.CommonRequest;
import com.aliyuncs.CommonResponse;
import com.aliyuncs.exceptions.ClientException;
import mockit.Expectations;
import mockit.Injectable;
import mockit.Tested;
import mockit.Verifications;
import org.example.common.adapter.AcsApiCaller;
import org.example.common.dto.CommodityDTO;
import org.example.common.exception.BizException;
import org.example.common.helper.ots.CommodityOtsHelper;
import org.example.common.param.order.CreateOrderParam;
import org.example.common.utils.TokenUtil;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class SpiTokenHelperTest {

    @Tested
    private SpiTokenHelper spiTokenHelper;

    @Injectable
    private AcsApiCaller acsApiCaller;

    @Injectable
    private CommodityOtsHelper commodityOtsHelper;

    @Test
    public void testCheckSpiToken_Success() throws ClientException {
        String commodityCode = "commodity_code";
        String spiToken = "spi_token";
        CreateOrderParam param = new CreateOrderParam();
        param.setCommodityCode(commodityCode);
        param.setToken("d0bb029154adb6e74d40118bc775b597");
        CommodityDTO expectedCommodityDTO = new CommodityDTO();
        expectedCommodityDTO.setServiceId("service_id");

        new Expectations() {{
            commodityOtsHelper.getCommodity(commodityCode);
            result = expectedCommodityDTO;
            CommonResponse commonResponse = new CommonResponse();
            commonResponse.setData("{\"ServiceKey\":\"expected_service_key\"}");
            acsApiCaller.getCommonResponse((CommonRequest) any);
            result = commonResponse;
            TokenUtil.createSpiToken(param, "expected_service_key");
            result = spiToken;
        }};

        spiTokenHelper.checkSpiToken(param, "d0bb029154adb6e74d40118bc775b597", commodityCode);

        new Verifications() {{
            commodityOtsHelper.getCommodity(commodityCode);
            acsApiCaller.getCommonResponse((CommonRequest) any);
            TokenUtil.createSpiToken(param, "expected_service_key");
        }};
    }

    @Test
    public void testCheckSpiToken_MismatchToken() throws ClientException {
        String commodityCode = "commodity_code";
        String invalidSpiToken = "invalid_spi_token";
        String validSpiToken = "valid_spi_token";
        Object param = new Object();
        CommodityDTO expectedCommodityDTO = new CommodityDTO();
        expectedCommodityDTO.setServiceId("service_id");

        new Expectations() {{
            commodityOtsHelper.getCommodity(commodityCode);
            result = expectedCommodityDTO;
            CommonResponse commonResponse = new CommonResponse();
            commonResponse.setData("{\"ServiceKey\":\"expected_service_key\"}");
            acsApiCaller.getCommonResponse((CommonRequest) any);
            result = commonResponse;
            TokenUtil.createSpiToken(param, "expected_service_key");
            result = validSpiToken;
        }};

        BizException thrown = Assertions.assertThrows(BizException.class, () -> {
            spiTokenHelper.checkSpiToken(param, invalidSpiToken, commodityCode);
        });

        Assertions.assertEquals("SpiTokenValidationFailed", thrown.getCode());
    }

    @Test
    public void testCheckSpiToken_EmptyData() throws ClientException {
        String commodityCode = "commodity_code";
        String spiToken = "spi_token";
        Object param = new Object();
        CommodityDTO expectedCommodityDTO = new CommodityDTO();
        expectedCommodityDTO.setServiceId("service_id");

        new Expectations() {{
            commodityOtsHelper.getCommodity(commodityCode);
            result = expectedCommodityDTO;
            CommonResponse commonResponse = new CommonResponse();
            commonResponse.setData("");
            acsApiCaller.getCommonResponse((CommonRequest) any);
            result = commonResponse;
        }};

        BizException thrown = Assertions.assertThrows(BizException.class, () -> {
            spiTokenHelper.checkSpiToken(param, spiToken, commodityCode);
        });

        Assertions.assertEquals("ServiceProviderKeyNotExist", thrown.getCode());
    }

    @Test
    public void testCheckSpiToken_ClientException() throws ClientException {
        String commodityCode = "commodity_code";
        String spiToken = "spi_token";
        Object param = new Object();
        CommodityDTO expectedCommodityDTO = new CommodityDTO();
        expectedCommodityDTO.setServiceId("service_id");

        new Expectations() {{
            commodityOtsHelper.getCommodity(commodityCode);
            result = expectedCommodityDTO;
            acsApiCaller.getCommonResponse((CommonRequest) any);
            result = new ClientException("ErrorCode", "error");
        }};

        BizException thrown = Assertions.assertThrows(BizException.class, () -> {

            spiTokenHelper.checkSpiToken(param, spiToken, commodityCode);
        });

        Assertions.assertEquals("ErrorCode", thrown.getCode());
    }
}