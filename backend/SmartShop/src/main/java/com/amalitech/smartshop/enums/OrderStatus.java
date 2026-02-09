package com.amalitech.smartshop.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum OrderStatus {
    PENDING,
    PROCESSING,
    SHIPPED,
    DELIVERED,
    CANCELLED;

    @JsonCreator
    public static OrderStatus fromString(String value) {
        return OrderStatus.valueOf(value.toUpperCase());
    }

    @JsonValue
    public String toValue() {
        return name();
    }
}
