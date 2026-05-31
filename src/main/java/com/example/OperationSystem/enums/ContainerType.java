package com.example.OperationSystem.enums;

import com.fasterxml.jackson.annotation.JsonCreator;

public enum ContainerType {

    Standard20ft(" 20ft"),
    Standard40ft(" 40ft"),
    HighCube20ft(" 20ft HQ"),
    HighCube40ft(" 40ft HQ"),
    Refrigerated20ft("Reefer 20ft"),
    Refrigerated40ft("Reefer 40ft"),
    OpenTop20ft("Open Top 20ft"),
    OpenTop40ft("Open Top 40ft"),
    FlatRack20ft("Flat Rack 20ft"),
    FlatRack40ft("Flat Rack 40ft"),
    OpenSide20ft("Open Side 20ft"),
    OpenSide40ft("Open Side 40ft"),
    Tank20ft("Tank 20ft"),
    Tank40ft("Tank 40ft"),
    Ventilated20ft("Ventilated 20ft"),
    Ventilated40ft("Ventilated 40ft"),
    DoubleDoor20ft("Double Door 20ft"),
    DoubleDoor40ft("Double Door 40ft");

    ContainerType(String label) { this.label = label; }

    private final String label;

    public String getLabel() { return label; }

    @JsonCreator
    public static ContainerType fromValue(String value) {
        if (value == null || value.isBlank()) return null;
        return ContainerType.valueOf(value);
    }
}