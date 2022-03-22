package org.bohdanrakov.jackcompiler.compilationengine;

import org.bohdanrakov.jackcompiler.tokenizer.tokens.Token;
import org.bohdanrakov.jackcompiler.tokenizer.tokens.TokenType;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static org.bohdanrakov.jackcompiler.tokenizer.tokens.TokenType.IDENTIFIER;

public class TokenXMLCompilationEngine implements CompilationEngine {

    public List<String> compileClass(List<Token> tokens) {
        List<String> result = new ArrayList<>();
        result.add("<tokens>");
        List<String> toXml = tokens.stream()
                .map(this::tokenToXML)
                .collect(Collectors.toList());
        result.addAll(toXml);
        result.add("</tokens>");
        return result;
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
