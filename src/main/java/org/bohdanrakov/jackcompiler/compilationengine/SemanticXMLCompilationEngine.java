package org.bohdanrakov.jackcompiler.compilationengine;

import org.bohdanrakov.jackcompiler.tokenizer.tokens.Token;
import org.bohdanrakov.jackcompiler.tokenizer.tokens.TokenType;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.bohdanrakov.jackcompiler.tokenizer.tokens.TokenType.*;

public class SemanticXMLCompilationEngine implements CompilationEngine {

    private final Set<String> statementKeywords = Stream.of("let", "if", "while", "do", "return")
            .collect(Collectors.toSet());

    private final Set<String> operators = Stream.of("+", "-", "*", "/", "&amp;", "|", "&lt;", "&gt;", "=")
            .collect(Collectors.toSet());

    private final Set<String> keywordConstant = Stream.of("true", "false", "null", "this")
            .collect(Collectors.toSet());

    private int tokenCursor;
    private List<Token> tokens;
    private Token currentToken;


    public List<String> compileClass(List<Token> tokens) {
        tokenCursor = -1;
        this.tokens = tokens;
        List<String> result = new ArrayList<>();

        result.add("<class>");
        proceedNextToken();
        validateTokenValue("class");
        result.add(tokenToXML(currentToken));

        proceedNextToken();
        validateTokenType(IDENTIFIER);
        result.add(tokenToXML(currentToken));

        proceedNextToken();
        validateTokenValue("{");
        result.add(tokenToXML(currentToken));

        proceedNextToken();
        while (currentToken.getStringValue().equals("static") ||
                currentToken.getStringValue().equals("field")) {
            compileClassVariableDeclaration(result);
            proceedNextToken();
        }

        while (currentToken.getStringValue().equals("constructor")
                || currentToken.getStringValue().equals("function")
                || currentToken.getStringValue().equals("method")) {
            compileSubroutineDeclaration(result);
            proceedNextToken();
        }
        validateTokenValue("}");
        result.add(tokenToXML(currentToken));

        result.add("</class>");
        return result;
    }

    private void compileClassVariableDeclaration(List<String> result) {
        result.add("<classVarDec>");
        result.add(tokenToXML(currentToken));

        proceedNextToken();
        validateTokenType(INTEGER_CONSTANT, STRING_CONSTANT, KEYWORD, IDENTIFIER);
        result.add(tokenToXML(currentToken));

        proceedNextToken();
        validateTokenType(IDENTIFIER);
        result.add(tokenToXML(currentToken));

        proceedNextToken();
        while (currentToken.getStringValue().equals(",")) {
            result.add(tokenToXML(currentToken));

            proceedNextToken();
            validateTokenType(IDENTIFIER);
            result.add(tokenToXML(currentToken));

            proceedNextToken();
        }

        validateTokenValue(";");
        result.add(tokenToXML(currentToken));
        result.add("</classVarDec>");
    }

    private void compileSubroutineDeclaration(List<String> result) {
        result.add("<subroutineDec>");
        result.add(tokenToXML(currentToken));

        proceedNextToken();
        if (!"void".equals(currentToken.getStringValue())) {
            validateTokenType(INTEGER_CONSTANT, STRING_CONSTANT, KEYWORD, IDENTIFIER);
        }
        result.add(tokenToXML(currentToken));

        proceedNextToken();
        validateTokenType(IDENTIFIER);
        result.add(tokenToXML(currentToken));

        proceedNextToken();
        validateTokenValue("(");
        result.add(tokenToXML(currentToken));

        result.add("<parameterList>");
        proceedNextToken();
        if(!")".equals(currentToken.getStringValue())) {
            validateTokenType(INTEGER_CONSTANT, STRING_CONSTANT, KEYWORD, IDENTIFIER);
            result.add(tokenToXML(currentToken));

            proceedNextToken();
            validateTokenType(IDENTIFIER);
            result.add(tokenToXML(currentToken));

            proceedNextToken();
            while (",".equals(currentToken.getStringValue())) {
                result.add(tokenToXML(currentToken));

                proceedNextToken();
                validateTokenType(INTEGER_CONSTANT, STRING_CONSTANT, KEYWORD, IDENTIFIER);
                result.add(tokenToXML(currentToken));

                proceedNextToken();
                validateTokenType(IDENTIFIER);
                result.add(tokenToXML(currentToken));

                proceedNextToken();
            }
        }

        result.add("</parameterList>");
        validateTokenValue(")");
        result.add(tokenToXML(currentToken));

        compileSubroutineBody(result);
        result.add("</subroutineDec>");
    }

    private void compileSubroutineBody(List<String> result) {
        result.add("<subroutineBody>");

        proceedNextToken();
        validateTokenValue("{");
        result.add(tokenToXML(currentToken));

        while (peekNextToken().getStringValue().equals("var")) {
            compileVariableDeclaration(result);
        }

        compileStatements(result);

        proceedNextToken();
        validateTokenValue("}");
        result.add(tokenToXML(currentToken));
        result.add("</subroutineBody>");
    }

    private void compileStatements(List<String> result) {
        result.add("<statements>");

        while (statementKeywords.contains(peekNextToken().getStringValue())) {
            proceedNextToken();
            if ("let".equals(currentToken.getStringValue())) {
                compileLetStatement(result);
            } else if ("if".equals(currentToken.getStringValue())) {
                compileIfStatement(result);
            } else if ("while".equals(currentToken.getStringValue())) {
                compileWhileStatement(result);
            } else if ("do".equals(currentToken.getStringValue())) {
                compileDoStatement(result);
            } else if ("return".equals(currentToken.getStringValue())) {
                compileReturnStatement(result);
            }
        }

        result.add("</statements>");
    }

    private void compileLetStatement(List<String> result) {
        result.add("<letStatement>");
        result.add(tokenToXML(currentToken));

        proceedNextToken();
        validateTokenType(IDENTIFIER);
        result.add(tokenToXML(currentToken));

        proceedNextToken();
        if (currentToken.getStringValue().equals("[")) {
            result.add(tokenToXML(currentToken));
            compileExpression(result);

            proceedNextToken();
            validateTokenValue("]");
            result.add(tokenToXML(currentToken));
            proceedNextToken();
        }

        validateTokenValue("=");
        result.add(tokenToXML(currentToken));

        compileExpression(result);

        proceedNextToken();
        validateTokenValue(";");
        result.add(tokenToXML(currentToken));

        result.add("</letStatement>");
    }

    private void compileIfStatement(List<String> result) {
        result.add("<ifStatement>");
        result.add(tokenToXML(currentToken));

        proceedNextToken();
        validateTokenValue("(");
        result.add(tokenToXML(currentToken));

        compileExpression(result);

        proceedNextToken();
        validateTokenValue(")");
        result.add(tokenToXML(currentToken));

        proceedNextToken();
        validateTokenValue("{");
        result.add(tokenToXML(currentToken));

        compileStatements(result);

        proceedNextToken();
        validateTokenValue("}");
        result.add(tokenToXML(currentToken));

        if(peekNextToken().getStringValue().equals("else")) {
            proceedNextToken();
            result.add(tokenToXML(currentToken));

            proceedNextToken();
            validateTokenValue("{");
            result.add(tokenToXML(currentToken));

            compileStatements(result);

            proceedNextToken();
            validateTokenValue("}");
            result.add(tokenToXML(currentToken));
        }

        result.add("</ifStatement>");
    }

    private void compileWhileStatement(List<String> result) {
        result.add("<whileStatement>");
        result.add(tokenToXML(currentToken));

        proceedNextToken();
        validateTokenValue("(");
        result.add(tokenToXML(currentToken));

        compileExpression(result);

        proceedNextToken();
        validateTokenValue(")");
        result.add(tokenToXML(currentToken));

        proceedNextToken();
        validateTokenValue("{");
        result.add(tokenToXML(currentToken));

        compileStatements(result);

        proceedNextToken();
        validateTokenValue("}");
        result.add(tokenToXML(currentToken));

        result.add("</whileStatement>");
    }

    private void compileDoStatement(List<String> result) {
        result.add("<doStatement>");
        result.add(tokenToXML(currentToken));

        proceedNextToken();
        validateTokenType(IDENTIFIER);
        result.add(tokenToXML(currentToken));

        proceedNextToken();
        validateTokenValue("(", ".");
        result.add(tokenToXML(currentToken));

        if (currentToken.getStringValue().equals("(")) {

            compileExpressionList(result);

            proceedNextToken();
            validateTokenValue(")");
            result.add(tokenToXML(currentToken));
        } else if (currentToken.getStringValue().equals(".")) {
            proceedNextToken();
            validateTokenType(IDENTIFIER);
            result.add(tokenToXML(currentToken));

            proceedNextToken();
            validateTokenValue("(");
            result.add(tokenToXML(currentToken));

            compileExpressionList(result);

            proceedNextToken();
            validateTokenValue(")");
            result.add(tokenToXML(currentToken));
        }

        proceedNextToken();
        validateTokenValue(";");
        result.add(tokenToXML(currentToken));

        result.add("</doStatement>");

    }

    private void compileReturnStatement(List<String> result) {
        result.add("<returnStatement>");
        result.add(tokenToXML(currentToken));

        if (!peekNextToken().getStringValue().equals(";")) {
            compileExpression(result);
        }

        proceedNextToken();
        validateTokenValue(";");
        result.add(tokenToXML(currentToken));

        result.add("</returnStatement>");
    }

    private void compileExpression(List<String> result) {
        result.add("<expression>");

        compileTerm(result);

        while (operators.contains(peekNextToken().getStringValue())) {
            proceedNextToken();
            result.add(tokenToXML(currentToken));

            compileTerm(result);
        }

        result.add("</expression>");
    }

    private void compileExpressionList(List<String> result) {
        result.add("<expressionList>");

        if (!peekNextToken().getStringValue().equals(")")) {
            compileExpression(result);
            while (peekNextToken().getStringValue().equals(",")) {
                proceedNextToken();
                result.add(tokenToXML(currentToken));

                compileExpression(result);
            }
        }

        result.add("</expressionList>");
    }

    private void compileTerm(List<String> result) {
        result.add("<term>");

        proceedNextToken();
        TokenType tokenType = currentToken.getTokenType();
        String tokenValue = currentToken.getStringValue();

        if (tokenType.equals(INTEGER_CONSTANT) || tokenType.equals(STRING_CONSTANT) || keywordConstant.contains(tokenValue)) {
            result.add(tokenToXML(currentToken));
        } else if (tokenType.equals(IDENTIFIER)) {
            result.add(tokenToXML(currentToken));
            if (peekNextToken().getStringValue().equals("[")) {
                proceedNextToken();
                result.add(tokenToXML(currentToken));

                compileExpression(result);

                proceedNextToken();
                validateTokenValue("]");
                result.add(tokenToXML(currentToken));
            } else if (peekNextToken().getStringValue().equals("(")) {
                proceedNextToken();
                result.add(tokenToXML(currentToken));

                compileExpressionList(result);

                proceedNextToken();
                validateTokenValue(")");
                result.add(tokenToXML(currentToken));
            } else if (peekNextToken().getStringValue().equals(".")) {
                proceedNextToken();
                result.add(tokenToXML(currentToken));

                proceedNextToken();
                validateTokenType(IDENTIFIER);
                result.add(tokenToXML(currentToken));

                proceedNextToken();
                validateTokenValue("(");
                result.add(tokenToXML(currentToken));

                compileExpressionList(result);

                proceedNextToken();
                validateTokenValue(")");
                result.add(tokenToXML(currentToken));
            }
        } else if (currentToken.getStringValue().equals("(")) {
            result.add(tokenToXML(currentToken));

            compileExpression(result);

            proceedNextToken();
            validateTokenValue(")");
            result.add(tokenToXML(currentToken));
        } else if (currentToken.getStringValue().equals("-") || currentToken.getStringValue().equals("~")) {
            result.add(tokenToXML(currentToken));
            compileTerm(result);
        }

        result.add("</term>");
    }

    private void compileVariableDeclaration(List<String> result) {
        result.add("<varDec>");
        proceedNextToken();
        result.add(tokenToXML(currentToken));

        proceedNextToken();
        validateTokenType(INTEGER_CONSTANT, STRING_CONSTANT, KEYWORD, IDENTIFIER);
        result.add(tokenToXML(currentToken));

        proceedNextToken();
        validateTokenType(IDENTIFIER);
        result.add(tokenToXML(currentToken));

        proceedNextToken();
        while (",".equals(currentToken.getStringValue())) {
            result.add(tokenToXML(currentToken));

            proceedNextToken();
            validateTokenType(IDENTIFIER);
            result.add(tokenToXML(currentToken));

            proceedNextToken();
        }

        validateTokenValue(";");
        result.add(tokenToXML(currentToken));
        result.add("</varDec>");
    }

    private void proceedNextToken() {
        if (tokenCursor < tokens.size() - 1) {
            currentToken =  tokens.get(++tokenCursor);
        } else {
            throw new RuntimeException("Compilation unexpectedly ran out of tokens");
        }
    }

    private Token peekNextToken() {
        if (tokenCursor < tokens.size() - 2) {
            return tokens.get(tokenCursor + 1);
        }
        throw new RuntimeException("Compilation unexpectedly ran out of tokens");
    }

    private void validateTokenType(TokenType... expectedTokenTypes) {
        Stream.of(expectedTokenTypes)
                .filter(expectedTokenType -> expectedTokenType.equals(currentToken.getTokenType()))
                .findAny()
                .orElseThrow(() -> new RuntimeException("Expected token types: "
                        + Arrays.toString(expectedTokenTypes) + ", found: " + currentToken.getTokenType()));
    }

    private void validateTokenValue(String... expectedValues) {
        Stream.of(expectedValues)
                .filter(expectedValue -> expectedValue.equals(currentToken.getStringValue()))
                .findAny()
                .orElseThrow(() ->  new RuntimeException("Expected token values: " + Arrays.toString(expectedValues)
                + ", found: " + currentToken.getStringValue()));
    }

    private String tokenToXML(Token token) {
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
        throw new RuntimeException("Unexpected token type");
    }
}
