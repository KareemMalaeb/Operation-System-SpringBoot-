package com.example.OperationSystem.enums;

import com.fasterxml.jackson.annotation.JsonCreator;

public enum Encoterms {
    EXW("EXW"),
    FOB("FOB"),
    CIF("CIF"),
    DAP("DAP"),
    DDP("DDP");

    private final String label;

    Encoterms(String label) { this.label = label; }

    public String getLabel() { return label; }

    @JsonCreator
    public static Encoterms fromValue(String value) {
        if (value == null || value.isBlank()) return null;
        return Encoterms.valueOf(value);
    }
}