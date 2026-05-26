package com.example.OperationSystem.enums;

public enum Encoterm {

    EXW("EXW"),
    FOB("FOB"),
    CIF("CIF"),
    DAP("DAP"),
    DDP("DDP");
    
    private final String label;

    Encoterm(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
}
