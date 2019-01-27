package org.javamoney.moneta.internal.format;

import org.javamoney.moneta.CurrencyUnitBuilder;
import org.testng.annotations.Test;

import javax.money.CurrencyUnit;

import static org.testng.Assert.*;

public class ParseContextTest {
    public static final CurrencyUnit EUR = CurrencyUnitBuilder.of("EUR", "test").build();

    @Test
    public void testSkip() {
        ParseContext context = new ParseContext(" EUR");
        context.skipWhitespace();
        assertEquals(context.getIndex(), 1);
    }

    @Test
    public void testReset() {
        ParseContext context = new ParseContext(" EUR");
        context.skipWhitespace();
        assertEquals(context.getIndex(), 1);
        context.setError();
        context.setErrorIndex(2);
        context.setErrorMessage("Error");
        context.setParsedNumber(25);
        context.setParsedCurrency(EUR);
        context.reset();
        assertEquals(context.getOriginalInput(), " EUR");
        assertEquals(context.getIndex(), 0);
        assertEquals(context.getErrorIndex(), -1);
        assertNull(context.getParsedNumber());
        assertNull(context.getParsedCurrency());
        assertNull(context.getErrorMessage());
    }

    @Test
    public void testToString() {
        ParseContext context = new ParseContext(" EUR");
        context.skipWhitespace();
        assertEquals(context.getIndex(), 1);
        context.setError();
        context.setErrorIndex(2);
        context.setErrorMessage("Error");
        context.setParsedNumber(25);
        context.setParsedCurrency(EUR);
        assertEquals(context.toString(), "ParseContext [index=1, errorIndex=2, originalInput=' EUR', parsedNumber=25', parsedCurrency=BuildableCurrencyUnit(currencyCode=EUR, numericCode=-1, defaultFractionDigits=2, context=CurrencyContext (\n" +
                "{provider=test}))]");
    }
}