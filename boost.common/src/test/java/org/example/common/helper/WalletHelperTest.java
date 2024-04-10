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

import mockit.Expectations;
import mockit.Mocked;
import org.example.common.config.SpecificationConfig;
import org.example.common.constant.PayPeriodUnit;
import org.example.common.utils.DateUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.when;

class WalletHelperTest {

    @Mock
    SpecificationConfig specificationConfig;

    @InjectMocks
    WalletHelper walletHelper;

    @Mocked
    private DateUtil dateUtilMock;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    void testGetServiceCost() {
        when(specificationConfig.getPriceBySpecificationName(anyString(), anyString(), any(PayPeriodUnit.class))).thenReturn(Double.valueOf(0));

        Double result = walletHelper.getServiceCost("serviceId", "specificationName", Long.valueOf(1), PayPeriodUnit.Month);
        assertEquals(Double.valueOf(0), result);
    }

    @Test
    void testGetRefundAmount() {
        Double result = walletHelper.getRefundAmount(100.0d, "2023-08-16T00:00:00Z", "2023-08-01T00:00:00Z", 1L, PayPeriodUnit.Month);
        assertEquals(Double.valueOf(50.0), result);

        result = walletHelper.getRefundAmount(100.0d, "2023-08-16T12:00:00Z", "2023-08-16T00:00:00Z", 1L, PayPeriodUnit.Day);
        assertEquals(Double.valueOf(50.0), result);

        result = walletHelper.getRefundAmount(100.0d, "2023-06-30T00:00:00Z", "2023-01-01T00:00:00Z", 1L, PayPeriodUnit.Year);
        assertEquals(Double.valueOf(50.0), result);
    }

    @Test
    public void testGetBillingEndDateTimeLongNotNull() {
        Long lastBillingEndDateLong = 123456789L;
        Long payPeriod = 30L;
        PayPeriodUnit payPeriodUnit = PayPeriodUnit.Day;
        Long billingDays = 30L;
        Long expectedBillingEndDateTimeLong = 123456789L + 2592000000L;

        new Expectations() {{
            DateUtil.getIsO8601FutureDateMillis(lastBillingEndDateLong, billingDays);
            result = expectedBillingEndDateTimeLong;
        }};

        WalletHelper walletHelper = new WalletHelper();
        Long actualBillingEndDateTimeLong = walletHelper.getBillingEndDateTimeMillis(lastBillingEndDateLong, payPeriod, payPeriodUnit);

        assertEquals(expectedBillingEndDateTimeLong, actualBillingEndDateTimeLong);
    }

    @Test
    public void testGetBillingEndDateTimeLongNull() {
        Long lastBillingEndDateLong = null;
        Long payPeriod = 30L;
        PayPeriodUnit payPeriodUnit = PayPeriodUnit.Day;
        Long billingDays = 30L;
        String currentDate = "2022-01-01";
        Long expectedBillingEndDateTimeLong = 1640995200000L;

        new Expectations() {{
            DateUtil.getCurrentIs08601Time();
            result = currentDate;

            DateUtil.getIsO8601FutureDateMillis(currentDate, billingDays);
            result = expectedBillingEndDateTimeLong;
        }};

        WalletHelper walletHelper = new WalletHelper();
        Long actualBillingEndDateTimeLong = walletHelper.getBillingEndDateTimeMillis(lastBillingEndDateLong, payPeriod, payPeriodUnit);

        assertEquals(expectedBillingEndDateTimeLong, actualBillingEndDateTimeLong);
    }
}
