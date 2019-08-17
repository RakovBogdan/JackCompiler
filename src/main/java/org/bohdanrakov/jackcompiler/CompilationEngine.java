package org.bohdanrakov.jackcompiler;

import org.bohdanrakov.jackcompiler.tokens.TokenType;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class CompilationEngine {

    private JackTokenizer jackTokenizer;
    private List<String> result;

    public void compileClass() {

    }

    public void compileClassWithoutAnalyzing() {
        result = new ArrayList<>();
        result.add("<tokens>");
        List<String> toXml = jackTokenizer.getTokens().stream().map(token -> {
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
    }

    public void setJackTokenizer(JackTokenizer jackTokenizer) {
        this.jackTokenizer = jackTokenizer;
    }

    public List<String> getResult() {
        return result;
    }

    public void reset() {
        result = new ArrayList<>();
    }
}
