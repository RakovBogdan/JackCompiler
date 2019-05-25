package org.bohdanrakov.jackcompiler;

import java.util.ArrayList;
import java.util.List;

public class CompilationEngine {

    private JackTokenizer jackTokenizer;
    private List<String> result;

    public void compileClass() {

    }

    public void setJackTokenizer(JackTokenizer jackTokenizer) {
        this.jackTokenizer = jackTokenizer;
    }

    public List<String> getResult() {
        return result;
    }

    public void reset() {
        result = new ArrayList<>();
    }
}
