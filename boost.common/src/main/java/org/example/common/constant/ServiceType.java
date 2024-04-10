package org.example.common.constant;

import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;

@Getter
public enum ServiceType {

    /**
     * resources belongs to user
     */
    user(0, "private"),

    /**
     * resources belongs to supplier
     */
    managed(1, "managed")

    ;
    private final Integer value;

    private final String outerName;

    ServiceType(int value, String outerName) {
        this.value = value;
        this.outerName = outerName;
    }

    public static ServiceType to(Integer value) {
        for (ServiceType st : values()) {
            if (st.getValue().equals(value)) {
                return st;
            }
        }
        return null;
    }

    public static ServiceType to(String outerName) {
        for (ServiceType st : values()) {
            if (st.outerName.equals(outerName)) {
                return st;
            }
        }
        return null;
    }

    @JsonValue
    public String getOuterName() {
        return outerName;
    }

    @Override
    public String toString() {
        return this.outerName;
    }
}
