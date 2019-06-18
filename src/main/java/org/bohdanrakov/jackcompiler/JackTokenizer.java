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
    private StringBuilder currentWord;
    private int currentIndex;

    public JackTokenizer(String source) {
        this.source = source;
    }

    public void tokenize() {
        currentIndex = 0;

        while (currentIndex < source.length()) {
            char currentChar = getCurrentChar();
            if (currentChar == '/') {
                if (source.charAt(currentIndex + 1) == '/') {
                    processSingleLineComment();
                } else if (source.charAt(currentIndex + 1) == '*') {
                    processMultilineComment();
                }
            } else if (symbols.contains((currentChar))) {
                processCurrentWord();
                tokens.add(new Token(TokenType.SYMBOL, String.valueOf(currentChar)));
            } else if (currentChar == ' ' || currentChar == '\n') {
                processCurrentWord();
            } else if (currentChar == '"') {
                processString();
            } else if (Character.isDigit(currentChar)) {
                processInteger();
            } else {
                currentWord.append(currentChar);
            }
        }
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

    private char getCurrentChar() {
        return source.charAt(currentIndex);
    }

    private void processCurrentWord() {
        if (currentWord.length() == 0) {
            return;
        }

        String wordToAdd = currentWord.toString();
        if (KeywordMap.isPresent(wordToAdd)) {
            tokens.add(new Token(TokenType.KEYWORD, wordToAdd));
        } else {
            tokens.add(new Token(TokenType.IDENTIFIER, wordToAdd));
        }
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
