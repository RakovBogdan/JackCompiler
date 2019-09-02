package org.bohdanrakov.jackcompiler;

import org.bohdanrakov.jackcompiler.tokens.Token;
import org.bohdanrakov.jackcompiler.tokens.TokenType;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

public class CompilationEngine {

    private Iterator<Token> tokensIterator;

    public List<String> compileClass(List<Token> tokens) {
        tokensIterator = tokens.iterator();
        List<String> result = new ArrayList<>();
        result.add("<class>");
        result.add("<keyword> class </keyword>");
        result.add("</class>");
        return result;
    }

    public List<String> compileClassWithoutAnalyzing(List<Token> tokens) {
        List<String> result = new ArrayList<>();
        result.add("<tokens>");
        List<String> toXml = tokens.stream().map(token -> {
            if (TokenType.KEYWORD == token.getTokenType()) {
                return "<keyword> " + token.getStringValue() + " </keyword>";
            } else if (TokenType.SYMBOL == token.getTokenType()) {
                return "<symbol> " + token.getStringValue() +  " </symbol>";
            } else if (TokenType.IDENTIFIER == token.getTokenType()) {
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
