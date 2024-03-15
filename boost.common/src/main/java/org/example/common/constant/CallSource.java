package org.example.common.constant;

import lombok.Getter;

@Getter
public enum CallSource {

    /**
     * 计算巢用户
     */
    User(1),

    /**
     * 云市场
     */
    Market(2),

    /**
     * 计算巢服务商
     */
    Supplier(3),

    /**
     * 凌霄
     */
    Css(5),


    ;
    private final Integer value;

    CallSource(Integer value) {
        this.value = value;
    }

    public static CallSource to(Integer value) {
        for (CallSource source : values()) {
            if (source.getValue().equals(value)) {
                return source;
            }
        }
        return null;
    }
}