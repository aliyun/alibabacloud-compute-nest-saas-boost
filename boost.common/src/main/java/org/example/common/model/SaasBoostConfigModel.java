package org.example.common.model;

import lombok.Data;

@Data
public class SaasBoostConfigModel {

    /**
     * 是否绑定SaasBoost
     */
    private boolean enabled;

    /**
     * SaasBoost公网访问地址
     */
    private String publicAccessUrl;

    /**
     * SaasBoost商品Code
     */
    private String commodityCode;
}
