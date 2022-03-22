package org.bohdanrakov.jackcompiler.compilationengine;

import org.bohdanrakov.jackcompiler.tokenizer.tokens.Token;

import java.util.List;

public interface CompilationEngine {

    List<String> compileClass(List<Token> tokens);
}
