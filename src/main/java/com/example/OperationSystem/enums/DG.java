package com.example.OperationSystem.enums;

public enum DG {
    DG("DG"),
    NON_DG("Non-DG");

    private final String label;

    DG(String label) { this.label = label; }

    public String getLabel() { return label; }
}
