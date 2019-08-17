package org.bohdanrakov.jackcompiler;

import org.bohdanrakov.jackcompiler.tokens.Token;
import org.bohdanrakov.jackcompiler.tokens.TokenType;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

public class JackTokenizer {

    private static Set<Character> symbols;
    private static Set<String> keywords;

    static {
        symbols = Set.of('{', '}', '(', ')', '[', ']', '.', ',', ';', '+', '-', '*', '/', '&', '|', '<', '>', '=', '~');
        keywords = Set.of("class", "constructor", "function", "method", "field", "static", "var", "int", "char",
                "boolean", "void", "true", "false", "null", "this", "let", "do", "if", "else", "while", "return");
    }

    private String source;
    private List<Token> tokens;
    private int currentIndex = -1;

    public JackTokenizer(String source) {
        this.source = source;
        tokens = new ArrayList<>();
    }

    public void tokenize() {
        while (currentIndex < source.length() - 1) {
            currentIndex++;
            char currentChar = getCurrentChar();
            if (currentChar == '/') {
                processComment();
            } else if (symbols.contains(currentChar)) {
                processSymbol(currentChar);
            } else if (currentChar == ' ' || currentChar == '\n' || currentChar == '\r' || currentChar == '\t') {
                //ignore
            } else if (currentChar == '"') {
                processString();
            } else if (isDigit(currentChar)) {
                processInteger();
            } else if (isAllowedWordStartSymbol(currentChar)){
                processWord();
            } else {
                throw new RuntimeException(currentChar + " character is not allowed here");
            }
        }
    }

    private void processSymbol(char currentChar) {
        if (currentChar == '<') {
            tokens.add(new Token(TokenType.SYMBOL, "&lt;"));
        } else if (currentChar == '>') {
            tokens.add(new Token(TokenType.SYMBOL, "&gt;"));
        } else if (currentChar == '&') {
            tokens.add(new Token(TokenType.SYMBOL, "&amp;"));
        }
        else {
            tokens.add(new Token(TokenType.SYMBOL, String.valueOf(currentChar)));
        }
    }

    private boolean isAllowedWordStartSymbol(char currentChar) {
        return ('A' <= currentChar && currentChar <= 'Z') ||
                ('a' <= currentChar && currentChar <= 'z' ) ||
                currentChar == '_';
    }

    private boolean isDigit(char character) {
        return character >= '0' && character <= '9';
    }

    private void processComment() {
        char nextChar = getNextChar();
        if (nextChar == '/') {
            processSingleLineComment();
        } else if (nextChar == '*') {
            processMultilineComment();
        } else {
            tokens.add(new Token(TokenType.SYMBOL, "/"));
            currentIndex++;
        }
    }

    private char getNextChar() {
        if (isSourceEndReached()) {
            return '\0';
        }
        return source.charAt(currentIndex + 1);
    }

    private boolean isSourceEndReached() {
        return currentIndex + 1 == source.length();
    }

    private char getCurrentChar() {
        return source.charAt(currentIndex);
    }

    private void processSingleLineComment() {
        while (currentIndex < source.length() && getCurrentChar() != '\n') {
            currentIndex++;
        }
    }

    private void processMultilineComment() {

        while (currentIndex < source.length()) {
            if (getCurrentChar() == '*' && getNextChar() == '/') {
                currentIndex++;
                break;
            }
            currentIndex++;
        }

        if (getCurrentChar() == '\0') {
            throw new RuntimeException("Reached end of file while parsing multiline comment");
        }
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
        while (currentIndex < source.length() - 1 && isDigit(getNextChar())) {
            integerValue.append(getNextChar());
            currentIndex++;
        }
        tokens.add(new Token(TokenType.INTEGER_CONSTANT, integerValue.toString()));
    }

    private void processWord() {
        StringBuilder wordAccumulator = new StringBuilder();
        wordAccumulator.append(getCurrentChar());
        while (isAllowedWordStartSymbol(getNextChar()) || isDigit(getNextChar())) {
            wordAccumulator.append(getNextChar());
            currentIndex++;
        }
        String word = wordAccumulator.toString();
        if (keywords.contains(word)) {
            tokens.add(new Token(TokenType.KEYWORD, word));
        } else {
            tokens.add(new Token(TokenType.IDENTIFIER, word));
        }
    }

    public List<Token> getTokens() {
        return tokens;
    }
}
