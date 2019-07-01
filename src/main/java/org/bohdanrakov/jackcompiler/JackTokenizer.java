package org.bohdanrakov.jackcompiler;

import org.bohdanrakov.jackcompiler.tokens.KeywordMap;
import org.bohdanrakov.jackcompiler.tokens.Token;
import org.bohdanrakov.jackcompiler.tokens.TokenType;

import java.util.Iterator;
import java.util.List;
import java.util.Set;

public class JackTokenizer {

    private static final String NO_MORE_TOKENS = "There are no more tokens";
    private static Set<Character> symbols;

    static {
        symbols = Set.of('{', '}', '(', ')', '[', ']', '.', ',', ';', '+', '-', '*', '/', '&', '|', '<', '>', '=', '~');
    }

    private String source;
    private List<Token> tokens;
    private Iterator<String> tokensIterator;
    private int currentIndex = -1;

    public JackTokenizer(String source) {
        this.source = source;
    }

    public void tokenize() {
        while (currentIndex < source.length()) {
            advanceIndex();
            char currentChar = getCurrentChar();
            if (currentChar == '/') {
                processComment();
            } else if (symbols.contains(currentChar)) {
                tokens.add(new Token(TokenType.SYMBOL, String.valueOf(currentChar)));
            } else if (currentChar == ' ' || currentChar == '\n' || currentChar == '\t') {
                //ignore
            } else if (currentChar == '"') {
                processString();
            } else if (Character.isDigit(currentChar)) {
                processInteger();
            } else if (isAllowedWordStartSymbol(currentChar)){
                processWord();
            } else {
                throw new RuntimeException(currentChar + " character is not allowed here");
            }
        }
    }

    private boolean isAllowedWordStartSymbol(char currentChar) {
        return ('A' <= currentChar && currentChar <= 'Z') ||
                ('a' <= currentChar && currentChar <= 'z' ) ||
                currentChar == '_';
    }

    private void processComment() {
        char nextChar = getNextChar();
        if (nextChar == '/') {
            processSingleLineComment();
        } else if (nextChar == '*') {
            processMultilineComment();
        }
        throw new RuntimeException("Character '/' not allowed here");
    }

    private char getNextChar() {
        return source.charAt(currentIndex + 1);
    }

    private char getCurrentChar() {
        return source.charAt(currentIndex);
    }

    private void advanceIndex() {
        currentIndex++;
    }

    private void processSingleLineComment() {
        while (getCurrentChar() != '\n') {
            currentIndex++;
        }
    }

    private void processMultilineComment() {

    }

    private void processString() {
        StringBuilder stringConstant = new StringBuilder();
        currentIndex++;
        while (currentIndex < source.length() && getCurrentChar() != '"' ) {
            stringConstant.append(getCurrentChar());
            currentIndex++;
        }
        tokens.add(new Token(TokenType.STRING_CONSTANT, stringConstant.toString()));
    }

    private void processInteger() {
        StringBuilder integerValue = new StringBuilder();
        integerValue.append(getCurrentChar());
        currentIndex++;
        while (currentIndex < source.length() && Character.isDigit(getCurrentChar())) {
            integerValue.append(getCurrentChar());
            currentIndex++;
        }
        tokens.add(new Token(TokenType.INTEGER_CONSTANT, integerValue.toString()));
    }

    private void processWord() {

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
