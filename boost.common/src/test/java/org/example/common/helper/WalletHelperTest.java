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

import org.example.common.config.SpecificationConfig;
import org.example.common.constant.PayPeriodUnit;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.mockito.Mockito.*;

class WalletHelperTest {

    @Mock
    SpecificationConfig specificationConfig;

    @InjectMocks
    WalletHelper walletHelper;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    void testGetServiceCost() {
        when(specificationConfig.getPriceBySpecificationName(anyString(), anyString(), any(PayPeriodUnit.class))).thenReturn(Double.valueOf(0));

        Double result = walletHelper.getServiceCost("serviceId", "specificationName", Long.valueOf(1), PayPeriodUnit.Month);
        Assertions.assertEquals(Double.valueOf(0), result);
    }

    @Test
    void testGetRefundAmount() {
        Double result = walletHelper.getRefundAmount(100.0d, "2023-08-16T00:00:00Z", "2023-08-01T00:00:00Z", 1L, PayPeriodUnit.Month);
        Assertions.assertEquals(Double.valueOf(50.0), result);

        result = walletHelper.getRefundAmount(100.0d, "2023-08-16T12:00:00Z", "2023-08-16T00:00:00Z", 1L, PayPeriodUnit.Day);
        Assertions.assertEquals(Double.valueOf(50.0), result);

        result = walletHelper.getRefundAmount(100.0d, "2023-06-30T00:00:00Z", "2023-01-01T00:00:00Z", 1L, PayPeriodUnit.Year);
        Assertions.assertEquals(Double.valueOf(50.0), result);
    }
}
