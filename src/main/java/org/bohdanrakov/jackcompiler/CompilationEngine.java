package org.bohdanrakov.jackcompiler;

import org.bohdanrakov.jackcompiler.tokens.Token;
import org.bohdanrakov.jackcompiler.tokens.TokenType;

import java.util.*;
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

        final String classOrInstanceVariableTokenValue = getCurrentToken().getStringValue();
        checkTokenValue(classOrInstanceVariableTokenValue, "static", "field");
        result.add("<keyword> " + classOrInstanceVariableTokenValue + " </keyword>");

        Token variableTypeToken = getNextToken();
        compileType(variableTypeToken);

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

    private void compileVariableDeclaration() {
        result.add("<varDec>");

        final String varToken = getCurrentToken().getStringValue();
        checkTokenValue(varToken, "var");
        result.add("<keyword> " + varToken + " </keyword>");

        Token variableTypeToken = getNextToken();
        compileType(variableTypeToken);

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

        result.add("</varDec>");
    }

    private void compileType(Token token) {
        if (Set.of("int", "char", "boolean").contains(token.getStringValue())) {
            result.add("<keyword> " + token.getStringValue() + " </keyword>");
        } else {
            checkTokenType(token.getTokenType(), IDENTIFIER);
            result.add("<identifier> " + token.getStringValue() + " </identifier>");
        }
    }

    private void compileSubroutineDeclaration() {
        result.add("<subroutineDec>");
        checkTokenType(getCurrentToken().getTokenType(), KEYWORD);
        checkTokenValue(getCurrentToken().getStringValue(), "function", "constructor", "method");
        result.add("<keyword> " + getCurrentToken().getStringValue() + " </keyword>");
        Token token = getNextToken();
        if (token.getStringValue().equals("void")) {
            result.add("<keyword> " + getCurrentToken().getStringValue() + " </keyword>");
        } else {
            compileType(token);
        }
        Token subroutineName = getNextToken();
        checkTokenType(subroutineName.getTokenType(), IDENTIFIER);
        result.add("<identifier> " + subroutineName.getStringValue() + " </identifier>");

        Token openParenthesis = getNextToken();
        checkTokenValue(openParenthesis.getStringValue(), "(");
        result.add("<symbol> " + openParenthesis.getStringValue() + " </symbol>");

        Token nextToken;
        if (!(nextToken = getNextToken()).getStringValue().equals(")")) {
            compileSubroutineParam(nextToken);
            while (!(nextToken = getNextToken()).getStringValue().equals(")")) {
                checkTokenValue(nextToken.getStringValue(), ",");
                result.add("<symbol> " + nextToken.getStringValue() + " </symbol>");
                compileSubroutineParam(nextToken);
            }
        }

        Token closeParenthesis = getCurrentToken();
        checkTokenValue(closeParenthesis.getStringValue(), ")");
        result.add("<symbol> " + closeParenthesis.getStringValue() + " </symbol>");

        compileSubroutineBody();

        result.add("</subroutineDec>");
    }

    private void compileSubroutineParam(Token paramType) {
        compileType(paramType);
        Token nextToken = getNextToken();
        checkTokenType(nextToken.getTokenType(), IDENTIFIER);
        result.add("<identifier> " + nextToken.getStringValue() + " </identifier>");
    }

    private void compileSubroutineBody() {
        Token openParenthesis = getNextToken();
        checkTokenValue(openParenthesis.getStringValue(), "{");
        result.add("<symbol> " + openParenthesis.getStringValue() + " </symbol>");

        while (getNextToken().getStringValue().equals("var")) {
            compileVariableDeclaration();
        }

        compileStatements();

        checkTokenValue(getNextToken().getStringValue(), "}");
        result.add("<symbol> " + getCurrentToken().getStringValue() + " </symbol>");
    }

    private void compileStatements() {
        Set<String> statementTypes = Set.of("let", "if", "while", "do", "return");

        while (statementTypes.contains(getCurrentToken().getStringValue())) {
            switch (getCurrentToken().getStringValue()) {
                case "let":
                    compileLetStatement();
                    break;
                case "if":
                    compileIfStatement();
                    break;
                case "while":
                    compileWhileStatement();
                    break;
                case "do":
                    compileDoStatement();
                    break;
                case "return":
                    compileReturnStatement();
                    break;
            }
            getNextToken();
        }
    }

    private void compileLetStatement() {

    }

    private void compileIfStatement() {

    }

    private void compileWhileStatement() {

    }

    private void compileDoStatement() {

    }

    private void compileReturnStatement() {

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
