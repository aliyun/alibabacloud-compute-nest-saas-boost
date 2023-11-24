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
package org.example.common.config;


import org.example.common.constant.PayPeriodUnit;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

class SpecificationConfigTest {
    private SpecificationConfig specificationConfig;

    @BeforeEach
    void setUp() {
        specificationConfig = new SpecificationConfig();
        specificationConfig.setServiceCount(2);

        SpecificationConfig.ServiceConfig serviceConfig1 = new SpecificationConfig.ServiceConfig();
        serviceConfig1.setId("1");
        serviceConfig1.setName("Service 1");
        serviceConfig1.setSpecifications(Arrays.asList(
                new SpecificationConfig.Specification("Spec 1", 10.0, 100.0),
                new SpecificationConfig.Specification("Spec 2", 20.0, 200.0)
        ));

        SpecificationConfig.ServiceConfig serviceConfig2 = new SpecificationConfig.ServiceConfig();
        serviceConfig2.setId("2");
        serviceConfig2.setName("Service 2");
        serviceConfig2.setSpecifications(Arrays.asList(
                new SpecificationConfig.Specification("Spec 3", 30.0, 300.0),
                new SpecificationConfig.Specification("Spec 4", 40.0, 400.0)
        ));

        specificationConfig.setServiceConfigs(Arrays.asList(serviceConfig1, serviceConfig2));
    }

    @Test
    void testGetNestServiceByIdExistingServiceId() {
        SpecificationConfig.ServiceConfig result = specificationConfig.getNestServiceById("1");
        assertNotNull(result);
        assertEquals("1", result.getId());
        assertEquals("Service 1", result.getName());
    }

    @Test
    void testGetNestServiceByIdNonExistingServiceId() {
        SpecificationConfig.ServiceConfig result = specificationConfig.getNestServiceById("3");
        assertNotNull(result);
        assertEquals("1", result.getId());
        assertEquals("Service 1", result.getName());
    }

    @Test
    void testGetPriceBySpecificationNameExistingSpecificationName() {
        double result = specificationConfig.getPriceBySpecificationName("1", "Spec 1", PayPeriodUnit.Month);
        assertEquals(10.0, result);
    }

    @Test
    void testGetPriceBySpecificationNameNonExistingSpecificationName() {
        assertThrows(IllegalArgumentException.class, () -> specificationConfig.getPriceBySpecificationName("1", "Spec 3", PayPeriodUnit.Year));
    }

    @Test
    void testGetPriceBySpecificationNameInvalidPayPeriodUnit() {
        assertThrows(IllegalArgumentException.class, () -> specificationConfig.getPriceBySpecificationName("1", "Spec 1", null));
    }

    @Test
    void testGetDefaultService() {
        SpecificationConfig.ServiceConfig result = specificationConfig.getDefaultService();
        assertNotNull(result);
        assertEquals("1", result.getId());
        assertEquals("Service 1", result.getName());
    }
}


