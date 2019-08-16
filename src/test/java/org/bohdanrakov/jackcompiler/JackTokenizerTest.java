package org.bohdanrakov.jackcompiler;

import org.bohdanrakov.jackcompiler.tokens.TokenType;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.*;

@RunWith(MockitoJUnitRunner.class)
public class JackTokenizerTest {

    @InjectMocks
    JackTokenizer testInstance;

    @Test
    public void shouldProcessSingleLineComment() {
        testInstance = new JackTokenizer("class //single line comment\n");
        testInstance.tokenize();
        assertEquals(1, testInstance.getTokens().size());
        assertSame(testInstance.getTokens().get(0).getTokenType(), TokenType.KEYWORD);
        assertEquals("class", testInstance.getTokens().get(0).getStringValue());
    }

    @Test
    public void shouldProcessMultilineComment() {
        testInstance = new JackTokenizer("class /*multi line comment\nclass class class ////////////// * ////\n*/");
        testInstance.tokenize();
        assertEquals(1, testInstance.getTokens().size());
        assertSame(testInstance.getTokens().get(0).getTokenType(), TokenType.KEYWORD);
        assertEquals("class", testInstance.getTokens().get(0).getStringValue());
    }

    @Test
    public void shouldProcessSymbols() {
        testInstance = new JackTokenizer("class {;}");
        testInstance.tokenize();
        assertEquals(4, testInstance.getTokens().size());
        assertSame(testInstance.getTokens().get(1).getTokenType(), TokenType.SYMBOL);
        assertSame(testInstance.getTokens().get(2).getTokenType(), TokenType.SYMBOL);
        assertSame(testInstance.getTokens().get(3).getTokenType(), TokenType.SYMBOL);
        assertEquals("{", testInstance.getTokens().get(1).getStringValue());
        assertEquals(";", testInstance.getTokens().get(2).getStringValue());
        assertEquals("}", testInstance.getTokens().get(3).getStringValue());
    }

    @Test
    public void shouldProcessStringLiterals() {
        testInstance = new JackTokenizer("var stringVariable = \"Some string value\";");
        testInstance.tokenize();
        assertEquals(5, testInstance.getTokens().size());
        assertSame(testInstance.getTokens().get(3).getTokenType(), TokenType.STRING_CONSTANT);
        assertEquals("Some string value", testInstance.getTokens().get(3).getStringValue());
    }

    @Test
    public void shouldProcessInteger() {
        testInstance = new JackTokenizer("var integerVariable = 12345;");
        testInstance.tokenize();
        assertEquals(5, testInstance.getTokens().size());
        assertSame(testInstance.getTokens().get(3).getTokenType(), TokenType.INTEGER_CONSTANT);
        assertEquals("12345", testInstance.getTokens().get(3).getStringValue());
    }

    @Test
    public void shouldProcessKeywords() {
        testInstance = new JackTokenizer(
                "class Person {\n" +
                "   field int age;\n" +
                "   method int getAge() {\n" +
                "       return age;\n" +
                "   }\n" +
                "}");
        testInstance.tokenize();
        assertEquals(18, testInstance.getTokens().size());
        assertSame(testInstance.getTokens().get(0).getTokenType(), TokenType.KEYWORD);
        assertEquals("class", testInstance.getTokens().get(0).getStringValue());
        assertEquals("field", testInstance.getTokens().get(3).getStringValue());
        assertEquals("int", testInstance.getTokens().get(4).getStringValue());
        assertEquals("method", testInstance.getTokens().get(7).getStringValue());
    }
}