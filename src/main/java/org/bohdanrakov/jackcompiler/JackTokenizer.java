package org.bohdanrakov.jackcompiler;

import java.util.Iterator;
import java.util.List;

public class JackTokenizer {

    public static final String NO_MORE_TOKENS = "There are no more tokens";
    private List<String> tokens;
    private Iterator<String> tokensIterator;

    public JackTokenizer(String source) {
        tokenize(source);
    }

    void tokenize(String source) {
    }

    public void advance() {
        if (hasMoreTokens()) {
            tokensIterator.next();
        } else {
            throw new RuntimeException(NO_MORE_TOKENS);
        }
    }

    private boolean hasMoreTokens() {
        return tokensIterator.hasNext();
    }
}
