package com.example.OperationSystem.enums;

import com.fasterxml.jackson.annotation.JsonCreator;

public enum DG {
    DG("DG"),
    NON_DG("Non-DG");

    private final String label;

    DG(String label) { this.label = label; }

    public String getLabel() { return label; }

    @JsonCreator
    public static DG fromValue(String value) {
        if (value == null || value.isBlank()) return null;
        return DG.valueOf(value);
    }
}