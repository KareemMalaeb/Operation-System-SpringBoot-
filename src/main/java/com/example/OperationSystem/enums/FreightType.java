package com.example.OperationSystem.enums;

public enum FreightType {
    Air("Air"),
    Sea("Sea"),
    Land("Land");

    private final String label;

    FreightType(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
}
