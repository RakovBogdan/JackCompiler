package org.bohdanrakov.jackcompiler.symboltable;

import java.util.HashMap;
import java.util.Map;

import static org.bohdanrakov.jackcompiler.symboltable.VariableKind.*;

public class SymbolTable {

    private Map<String, SymbolRecord> classSymbolTable = new HashMap<>();
    private Map<String, SymbolRecord> subroutineSymbolTable = new HashMap<>();
    private Map<VariableKind, Integer> variablesCount = new HashMap<>();


    public void startSubroutine() {
        subroutineSymbolTable.clear();
        variablesCount.remove(ARG);
        variablesCount.remove(VAR);
    }

    public void define(String name, String type, VariableKind kind) {
        variablesCount.put(kind, variablesCount.getOrDefault(kind, -1) + 1);
        if (STATIC == kind || FIELD == kind) {
            classSymbolTable.put(name, new SymbolRecord(type, kind, variablesCount.get(kind)));
        } else if (ARG == kind || VAR == kind) {
            subroutineSymbolTable.put(name, new SymbolRecord(type, kind, variablesCount.get(kind)));
        }
    }

    public int variableCount(VariableKind kind) {
        return variablesCount.get(kind);
    }

    public VariableKind kindOf(String name) {
        return getSymbolRecord(name).getKind();
    }

    public String typeOf(String name) {
        return getSymbolRecord(name).getType();
    }

    public int indexOf(String name) {
        return getSymbolRecord(name).getIndex();
    }

    private SymbolRecord getSymbolRecord(String name) {
        if (subroutineSymbolTable.containsKey(name)) {
            return subroutineSymbolTable.get(name);
        } else {
            return classSymbolTable.get(name);
        }
    }
}
