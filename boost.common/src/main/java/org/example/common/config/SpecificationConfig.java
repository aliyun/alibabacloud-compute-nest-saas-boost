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

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.example.common.constant.PayPeriodUnit;
import org.example.common.errorinfo.ErrorInfo;
import org.example.common.exception.BizException;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
@ConfigurationProperties(prefix = "nest")
@Data
@Slf4j
public class SpecificationConfig {

    private static final String DEFAULT = "default";

    private int serviceCount;

    public List<ServiceConfig> serviceConfigs;

    @Data
    public static class ServiceConfig {

        private String id;

        private String name;

        private List<Specification> specifications;
    }

    @Data
    public static class Parameter {
        private String name;

        private String type;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Specification {
        private String specificationName;

        private Double monthPrice;

        private Double yearPrice;
    }

    public ServiceConfig getNestServiceById(String serviceId) {
        if (StringUtils.isEmpty(serviceId)) {
            return serviceConfigs.get(0);
        } else {
            Optional<ServiceConfig> serviceConfig = serviceConfigs.stream().filter(service -> service.getId().equals(serviceId)).findFirst();
            return serviceConfig.orElse(serviceConfigs.get(0));
        }
    }

    public Double getPriceBySpecificationName(String serviceId, String specificationName, PayPeriodUnit payPeriodUnit) {
        log.info("Get price by specification name. ServiceId:{}, specificationName:{}, payPeriodUnit:{}", serviceId, specificationName, payPeriodUnit);
        ServiceConfig packages = getNestServiceById(serviceId);
        if (payPeriodUnit == null) {
            throw new IllegalArgumentException("payPeriodUnit can not be null");
        }

        for (Specification specification : packages.getSpecifications()) {
            if (specification.getSpecificationName().equals(specificationName)) {
                switch (payPeriodUnit) {
                    case Month:
                        return specification.getMonthPrice();
                    case Year:
                        return specification.getYearPrice();
                    default:
                        break;
                }
            }
        }
        if (!DEFAULT.equals(specificationName)) {
            throw new IllegalArgumentException("specificationName is not exist");
        }
        ServiceConfig defaultService = getNestServiceById(DEFAULT);
        switch (payPeriodUnit) {
            case Month:
                return defaultService.getSpecifications().get(0).getMonthPrice();
            case Year:
                return defaultService.getSpecifications().get(0).getYearPrice();
            default:
                throw new BizException(ErrorInfo.SERVER_UNAVAILABLE);
        }
    }

    public ServiceConfig getDefaultService() {
        return getNestServiceById("");
    }
}

