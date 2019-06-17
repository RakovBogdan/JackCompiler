package org.bohdanrakov.jackcompiler.tokens;

public class Token {

    private TokenType tokenType;
    private String stringValue;

    public Token(TokenType tokenType, String stringValue) {
        this.tokenType = tokenType;
        this.stringValue = stringValue;
    }

    public TokenType getTokenType() {
        return tokenType;
    }

    public void setTokenType(TokenType tokenType) {
        this.tokenType = tokenType;
    }

    public String getStringValue() {
        return stringValue;
    }

    public void setStringValue(String stringValue) {
        this.stringValue = stringValue;
    }
}
