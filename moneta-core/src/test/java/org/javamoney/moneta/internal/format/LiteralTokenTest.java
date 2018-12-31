package org.javamoney.moneta.internal.format;

import org.javamoney.moneta.FastMoney;
import org.testng.annotations.Test;

import javax.money.MonetaryAmount;
import javax.money.MonetaryException;
import javax.money.format.MonetaryParseException;

import java.io.IOException;

import static org.testng.Assert.*;

public class LiteralTokenTest {

    @Test
    public void testParse() {
        LiteralToken token = new LiteralToken(" some text ");
        ParseContext context = new ParseContext(" some text here");
        token.parse(context);
        assertEquals(context.getIndex(), 11);
    }

    @Test
    public void testParse_throws_exception() {
        LiteralToken token = new LiteralToken(" some text ");
        ParseContext context = new ParseContext("here is some text here");
        try {
            token.parse(context);
        } catch (MonetaryParseException e) {
            assertEquals(e.getInput(), "here is some text here");
            assertEquals(e.getErrorIndex(), 0);
            assertEquals(e.getMessage(), "Parse Error");
        }
        assertEquals(context.getIndex(), 0);
        assertFalse(context.isComplete());
        assertTrue(context.hasError());
        assertEquals(context.getErrorMessage(), "Parse Error");
    }

    @Test
    public void testPrint() throws IOException {
        LiteralToken token = new LiteralToken(" some text ");
        FastMoney amount = FastMoney.of(0,"USD");
        StringBuilder sb = new StringBuilder();
        token.print(sb, amount);
        assertEquals(sb.toString(), " some text ");
    }

    @Test
    public void testToString() {
        LiteralToken token = new LiteralToken(" some text ");
        assertEquals(token.toString(), "LiteralToken [token= some text ]");
    }
}