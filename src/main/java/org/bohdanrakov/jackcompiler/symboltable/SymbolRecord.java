package org.bohdanrakov.jackcompiler.symboltable;

public class SymbolRecord {

    private String type;
    private VariableKind kind;
    private int index;

    public SymbolRecord(String type, VariableKind kind, int index) {
        this.type = type;
        this.kind = kind;
        this.index = index;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public VariableKind getKind() {
        return kind;
    }

    public void setKind(VariableKind kind) {
        this.kind = kind;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }
}
