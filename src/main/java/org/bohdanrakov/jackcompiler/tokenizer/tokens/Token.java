package org.bohdanrakov.jackcompiler.tokenizer.tokens;

import java.util.Objects;

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

    @Override
    public String toString() {
        return "Token{" +
                "tokenType=" + tokenType +
                ", stringValue='" + stringValue + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Token token = (Token) o;
        return tokenType == token.tokenType &&
                Objects.equals(stringValue, token.stringValue);
    }

    @Override
    public int hashCode() {
        return Objects.hash(tokenType, stringValue);
    }
}
