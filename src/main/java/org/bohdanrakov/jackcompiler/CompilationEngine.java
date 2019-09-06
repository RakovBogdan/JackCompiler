package org.bohdanrakov.jackcompiler;

import org.bohdanrakov.jackcompiler.tokens.Token;
import org.bohdanrakov.jackcompiler.tokens.TokenType;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static org.bohdanrakov.jackcompiler.tokens.TokenType.*;

public class CompilationEngine {

    private static List<String> subroutinesTypes = List.of("function", "constructor", "method");
    private List<Token> tokens;
    private List<String> result;
    private int tokenNumber;

    public List<String> compileClass(List<Token> tokens) {
        initializeCompilingFromStart(tokens);

        result.add("<class>");
        Token classToken = getNextToken();
        checkTokenType(classToken.getTokenType(), KEYWORD);
        checkTokenValue(classToken.getStringValue(), "class");
        result.add("<keyword> " + classToken.getStringValue() + " </keyword>");

        Token identifierToken = getNextToken();
        checkTokenType(identifierToken.getTokenType(), IDENTIFIER);
        result.add("<identifier> " + identifierToken.getStringValue() + " </identifier>");

        Token openCurlyBraceToken = getNextToken();
        checkTokenType(openCurlyBraceToken.getTokenType(), SYMBOL);
        checkTokenValue(openCurlyBraceToken.getStringValue(), "{");
        result.add("<symbol> " + openCurlyBraceToken.getStringValue() + " </symbol>");

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

    private void initializeCompilingFromStart(List<Token> tokens) {
        tokenNumber = 0;
        this.tokens = tokens;
        result = new ArrayList<>();
    }

    private void compileClassVariableDeclaration() {
        result.add("<classVarDec>");

        final String classVariableTokenValue = getCurrentToken().getStringValue();
        checkTokenValue(classVariableTokenValue, "static", "field");
        result.add("<keyword> " + classVariableTokenValue + " </keyword>");

        Token variableTypeToken = getNextToken();
        if (Set.of("int", "char", "boolean").contains(variableTypeToken.getStringValue())) {
            result.add("<keyword> " + variableTypeToken.getStringValue() + " </keyword>");
        } else {
            checkTokenType(variableTypeToken.getTokenType(), IDENTIFIER);
            result.add("<identifier> " + variableTypeToken.getStringValue() + " </identifier>");
        }

        Token variableNameToken = getNextToken();
        checkTokenType(variableNameToken.getTokenType(), IDENTIFIER);
        result.add("<identifier> " + variableNameToken.getStringValue() + " </identifier>");

        while (getNextToken().getStringValue().equals(",")) {
            result.add("<symbol> " + getCurrentToken().getStringValue() + " </symbol>");
            variableNameToken = getNextToken();
            checkTokenType(variableNameToken.getTokenType(), IDENTIFIER);
            result.add("<identifier> " + variableNameToken.getStringValue() + " </identifier>");
        }

        Token endOfClassVariableDeclaration = getNextToken();
        checkTokenValue(endOfClassVariableDeclaration.getStringValue(), ";");
        result.add("<symbol> " + endOfClassVariableDeclaration.getStringValue() + " </symbol>");

        result.add("</classVarDec>");
    }

    private void compileSubroutineDeclaration() {

    }

    private Token getCurrentToken() {
        return tokens.get(tokenNumber);
    }

    private Token getNextToken() {
        if (tokenNumber < tokens.size()) {
            return tokens.get(tokenNumber++);
        } else {
            throw new RuntimeException("There is no next token");
        }
    }

    private void checkTokenValue(String currentTokenValue, String... allowedTokenValues) {
        for (String expectedValue : allowedTokenValues) {
            if (expectedValue.equals(currentTokenValue)) {
                return;
            }
        }
        throw new RuntimeException("The source code token value:" + currentTokenValue +
                " doesn't match allowed token types:" + Arrays.toString(allowedTokenValues));
    }

    private void checkTokenType(TokenType currentTokenType, TokenType... allowedTokenTypes) {
        for (TokenType allowedTokenType : allowedTokenTypes) {
            if (currentTokenType == allowedTokenType) {
                return;
            }
        }
        throw new RuntimeException("The source code token type:" + currentTokenType +
                " doesn't match allowed token types:" + Arrays.toString(allowedTokenTypes));
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
