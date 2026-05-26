package com.example.OperationSystem.enums;

public enum Encoterms {
    EXW("EXW"),
    FOB("FOB"),
    CIF("CIF"),
    DAP("DAP"),
    DDP("DDP");

    private final String label;

    Encoterms(String label) { this.label = label; }

    public String getLabel() { return label; }
}
