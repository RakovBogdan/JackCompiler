package org.bohdanrakov.jackcompiler;

import org.bohdanrakov.jackcompiler.tokens.Token;
import org.bohdanrakov.jackcompiler.tokens.TokenType;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static org.bohdanrakov.jackcompiler.tokens.TokenType.*;

public class CompilationEngine {

    private static Set<String> subroutinesTypes = Set.of("function", "constructor", "method");
    private List<Token> tokens;
    private List<String> result;
    private int tokenNumber;

    public List<String> compileClass(List<Token> tokens) {
        tokenNumber = 0;
        this.tokens = tokens;
        result = new ArrayList<>();
        result.add("<class>");
        result.add("<keyword> " + getSpecifiedValue(KEYWORD, "class") + " </keyword>");
        result.add("<identifier> " + getNextTokenStringValueByType(IDENTIFIER) + " </identifier>");
        result.add("<symbol> " + getSpecifiedValue(SYMBOL, "{") + " </symbol>");

        Token nextToken;
        while (!(nextToken = getNextToken()).getStringValue().equals("}")) {
            if (subroutinesTypes.contains(nextToken.getStringValue())) {
                compileSubroutineDeclaration();
            } else {
                compileClassVariableDeclaration();
            }
        }

        result.add("<symbol> " + nextToken.getStringValue() + " </symbol>");
        result.add("</class>");

        return result;
    }

    private void compileClassVariableDeclaration() {

    }

    private void compileSubroutineDeclaration() {

    }

    private Token getNextToken() {
        if (tokenNumber < tokens.size()) {
            return tokens.get(tokenNumber++);
        } else {
            throw new RuntimeException("There is no next token");
        }
    }

    private String getSpecifiedValue(TokenType tokenType, String expectedValue) {
        String nextTokenStringValue = getNextTokenStringValueByType(tokenType);
        checkTokenValue(nextTokenStringValue, expectedValue);
        return nextTokenStringValue;
    }

    private void checkTokenValue(String tokenValue, String expectedValue) {
        if (!expectedValue.equals(tokenValue)) {
            throw new RuntimeException("Wrong Value " + tokenValue + " was Passed. Expected " + expectedValue);
        }
    }

    private String getNextTokenStringValueByType(TokenType tokenType) {
        final Token nextToken = checkNextTokenByType(tokenType);
        return nextToken.getStringValue();
    }

    private Token checkNextTokenByType(TokenType tokenType) {
        final Token nextToken = getNextToken();
        if (nextToken.getTokenType() != tokenType) {
            throw new RuntimeException("The expected token type " + tokenType +
                    "is not same as in source code " + nextToken.getTokenType());
        }

        return nextToken;
    }

    public List<String> compileClassWithoutAnalyzing(List<Token> tokens) {
        List<String> result = new ArrayList<>();
        result.add("<tokens>");
        List<String> toXml = tokens.stream().map(token -> {
            if (TokenType.KEYWORD == token.getTokenType()) {
                return "<keyword> " + token.getStringValue() + " </keyword>";
            } else if (TokenType.SYMBOL == token.getTokenType()) {
                return "<symbol> " + token.getStringValue() +  " </symbol>";
            } else if (IDENTIFIER == token.getTokenType()) {
                return "<identifier> " + token.getStringValue() + " </identifier>";
            } else if (TokenType.INTEGER_CONSTANT == token.getTokenType()) {
                return "<integerConstant> " + token.getStringValue() + " </integerConstant>";
            } else if (TokenType.STRING_CONSTANT == token.getTokenType()) {
                return "<stringConstant> " + token.getStringValue() + " </stringConstant>";
            }
            return "";
        }).collect(Collectors.toList());
        result.addAll(toXml);
        result.add("</tokens>");
        return result;
    }
}
