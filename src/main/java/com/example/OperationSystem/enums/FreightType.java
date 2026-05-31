package com.example.OperationSystem.enums;

import com.fasterxml.jackson.annotation.JsonCreator;

public enum FreightType {
    Air("Air"),
    Sea("Sea"),
    Land("Land");

    private final String label;

    FreightType(String label) { this.label = label; }

    public String getLabel() { return label; }

    @JsonCreator
    public static FreightType fromValue(String value) {
        if (value == null || value.isBlank()) return null;
        return FreightType.valueOf(value);
    }
}