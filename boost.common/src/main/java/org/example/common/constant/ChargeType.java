package org.example.common.constant;

import lombok.Getter;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

@Getter
public enum ChargeType {

    /*===================按量付费类型====================*/
    POST_PAID("PostPaid"),


    /*===============预付费，包年包月类型=================*/
    PRE_PAID("PrePaid"),
    ;

    private final String value;

    ChargeType(String value) {
        this.value = value;
    }

    public static ChargeType to(String value) {
        for (ChargeType chargeType : ChargeType.values()) {
            if (chargeType.getValue().equalsIgnoreCase(value)) {
                return chargeType;
            }
        }
        throw new IllegalArgumentException("Unknown ChargeType value: " + value);
    }
}
