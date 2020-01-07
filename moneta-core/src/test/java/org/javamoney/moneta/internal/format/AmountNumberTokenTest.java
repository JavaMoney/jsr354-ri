package org.javamoney.moneta.internal.format;

import org.javamoney.moneta.FastMoney;
import org.javamoney.moneta.spi.MoneyUtils;
import org.testng.annotations.Ignore;
import org.testng.annotations.Test;

import javax.money.format.*;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.util.Locale;

import static java.util.Locale.*;
import static org.javamoney.moneta.format.CurrencyStyle.CODE;
import static org.testng.Assert.*;

public class AmountNumberTokenTest {
    private static final String DEFAULT_STYLE = "default";
    private static final String PATTERN = "#,##0.00 ";
    private static final String PATTERN_WITH_LEADING_SPACES = "  " + PATTERN;

    @Test
    public void testParse_nodigots_throws_exception() {
        AmountNumberToken token = new AmountNumberToken(contextForLocale(US, null), PATTERN);
        ParseContext context = new ParseContext("incorrect amount");
        try {
            token.parse(context);
            fail();
        } catch (MonetaryParseException e) {
            assertEquals(e.getInput(), "incorrect amount");
            assertEquals(e.getErrorIndex(), 0);
            assertEquals(e.getMessage(), "No digits found: \"incorrect amount\"");
        }
        assertEquals(context.getIndex(), 0);
        assertFalse(context.isComplete());
        assertTrue(context.hasError());
        assertEquals(context.getErrorMessage(), "No digits found: \"incorrect amount\"");
        assertNull(context.getParsedNumber());
    }

    @Test
    @Ignore("The current JDK parses a number of '12-54.234' to 12 without error!")
    public void testParse_mismatchingPattern_throws_exception() {
        AmountNumberToken token = new AmountNumberToken(contextForLocale(US, null), PATTERN);
        ParseContext context = new ParseContext("12-54.234");
        try {
            token.parse(context);
            fail();
        } catch (MonetaryParseException e) {
            assertEquals(e.getInput(), "12354:234");
            assertEquals(e.getErrorIndex(), 0);
            assertEquals(e.getMessage(), "Unparseable amount: \"12354ghd.234\"");
        }
        assertEquals(context.getIndex(), 0);
        assertFalse(context.isComplete());
        assertTrue(context.hasError());
        assertEquals(context.getErrorMessage(), "Unparseable amount: \"12354ghd.234\"");
        assertNull(context.getParsedNumber());
    }

    @Test
    public void testParse_US() {
        testParse(US, PATTERN, null, "0", 1, "zero", 0.0, "0");
        testParse(US, PATTERN, null, "00", 2, "zero dd", 0.0, "00");
        testParse(US, PATTERN, null, "0.0", 3, "zero d.d", 0.0, "0.0");
        testParse(US, PATTERN, null, "-0.00", 5, "zero d.dd negative", -0.0, "-0.00");
        testParse(US, PATTERN, null, "123", 3, "int", 123.0, "123");
        testParse(US, PATTERN, null, "-123", 4, "int negative", -123.0, "-123");
        testParse(US, PATTERN, null, "0123", 4, "int padding zero", 123.0, "0123");
        testParse(US, PATTERN, null, "-0123", 5, "int padding zero negative", -123.0, "-0123");
        testParse(US, PATTERN, null, "123.4", 5, "float 1", 123.4, "123.4");
        testParse(US, PATTERN, null, "123.45", 6, "float 2", 123.45, "123.45");
        testParse(US, PATTERN, null, "123.456", 7, "float 3", 123.456, "123.456");
        testParse(US, PATTERN, null, "-123.4", 6, "float 1 negative", -123.4, "-123.4");
        testParse(US, PATTERN, null, "-123.45", 7, "float 2 negative", -123.45, "-123.45");
        testParse(US, PATTERN, null, "-123.456", 8, "float 3 negative", -123.456, "-123.456");
        testParse(US, PATTERN, null, "12,345", 6, "int thousands", 12345.0, "12,345");
        testParse(US, PATTERN, null, "-12,345", 7, "int thousands negative", -12345.0, "-12,345");
        testParse(US, PATTERN, null, "12,345.6", 8, "float 1 thousands", 12345.6, "12,345.6");
        testParse(US, PATTERN, null, "12,345.67", 9, "float 2 thousands", 12345.67, "12,345.67");
        testParse(US, PATTERN, null, "12,345.678", 10, "float 3 thousands", 12345.678, "12,345.678");
        testParse(US, PATTERN, null, "-12,345.6", 9, "float 1 thousands negative", -12345.6, "-12,345.6");
        testParse(US, PATTERN, null, "-12,345.67", 10, "float 2 thousands negative", -12345.67, "-12,345.67");
        testParse(US, PATTERN, null, "-12,345.678", 11, "float 3 thousands negative", -12345.678, "-12,345.678");
        testParse(US, PATTERN, null, "-1,234,567.89", 13, "float 2 million negative", -1234567.89, "-1,234,567.89");
    }

    @Test
    public void testParse_US_PATTERN_WITH_LEADING_SPACES() {
        testParse(US, PATTERN_WITH_LEADING_SPACES, null, "0", 1, "zero", 0.0, "0");
        testParse(US, PATTERN_WITH_LEADING_SPACES, null, "00", 2, "zero dd", 0.0, "00");
        testParse(US, PATTERN_WITH_LEADING_SPACES, null, "0.0", 3, "zero d.d", 0.0, "0.0");
        testParse(US, PATTERN_WITH_LEADING_SPACES, null, "-0.00", 5, "zero d.dd negative", -0.0, "-0.00");
        testParse(US, PATTERN_WITH_LEADING_SPACES, null, "123", 3, "int", 123.0, "123");
        testParse(US, PATTERN_WITH_LEADING_SPACES, null, "-123", 4, "int negative", -123.0, "-123");
        testParse(US, PATTERN_WITH_LEADING_SPACES, null, "0123", 4, "int padding zero", 123.0, "0123");
        testParse(US, PATTERN_WITH_LEADING_SPACES, null, "-0123", 5, "int padding zero negative", -123.0, "-0123");
        testParse(US, PATTERN_WITH_LEADING_SPACES, null, "123.4", 5, "float 1", 123.4, "123.4");
        testParse(US, PATTERN_WITH_LEADING_SPACES, null, "123.45", 6, "float 2", 123.45, "123.45");
        testParse(US, PATTERN_WITH_LEADING_SPACES, null, "123.456", 7, "float 3", 123.456, "123.456");
        testParse(US, PATTERN_WITH_LEADING_SPACES, null, "-123.4", 6, "float 1 negative", -123.4, "-123.4");
        testParse(US, PATTERN_WITH_LEADING_SPACES, null, "-123.45", 7, "float 2 negative", -123.45, "-123.45");
        testParse(US, PATTERN_WITH_LEADING_SPACES, null, "-123.456", 8, "float 3 negative", -123.456, "-123.456");
        testParse(US, PATTERN_WITH_LEADING_SPACES, null, "12,345", 6, "int thousands", 12345.0, "12,345");
        testParse(US, PATTERN_WITH_LEADING_SPACES, null, "-12,345", 7, "int thousands negative", -12345.0, "-12,345");
        testParse(US, PATTERN_WITH_LEADING_SPACES, null, "12,345.6", 8, "float 1 thousands", 12345.6, "12,345.6");
        testParse(US, PATTERN_WITH_LEADING_SPACES, null, "12,345.67", 9, "float 2 thousands", 12345.67, "12,345.67");
        testParse(US, PATTERN_WITH_LEADING_SPACES, null, "12,345.678", 10, "float 3 thousands", 12345.678, "12,345.678");
        testParse(US, PATTERN_WITH_LEADING_SPACES, null, "-12,345.6", 9, "float 1 thousands negative", -12345.6, "-12,345.6");
        testParse(US, PATTERN_WITH_LEADING_SPACES, null, "-12,345.67", 10, "float 2 thousands negative", -12345.67, "-12,345.67");
        testParse(US, PATTERN_WITH_LEADING_SPACES, null, "-12,345.678", 11, "float 3 thousands negative", -12345.678, "-12,345.678");
        testParse(US, PATTERN_WITH_LEADING_SPACES, null, "-1,234,567.89", 13, "float 2 million negative", -1234567.89, "-1,234,567.89");
        testParse(US, PATTERN_WITH_LEADING_SPACES, null, " -1,234,567.89", 14, "float 2 million negative with leading space", -1234567.89, " -1,234,567.89");
        testParse(US, PATTERN_WITH_LEADING_SPACES, null, " -1,234,567.89 ", 14, "float 2 million negative with leading and trailing space", -1234567.89, " -1,234,567.89 ");
    }

    @Test
    public void testParse_FR() {
        testParse(FRANCE, PATTERN, null, "0", 1, "zero", 0.0, "0");
        testParse(FRANCE, PATTERN, null, "00", 2, "zero dd", 0.0, "00");
        testParse(FRANCE, PATTERN, null, "0,0", 3, "zero d.d", 0.0, "0,0");
        testParse(FRANCE, PATTERN, null, "-0,00", 5, "zero d.dd negative", -0.0, "-0,00");
        testParse(FRANCE, PATTERN, null, "123", 3, "int", 123.0, "123");
        testParse(FRANCE, PATTERN, null, "-123", 4, "int negative", -123.0, "-123");
        testParse(FRANCE, PATTERN, null, "0123", 4, "int padding zero", 123.0, "0123");
        testParse(FRANCE, PATTERN, null, "-0123", 5, "int padding zero negative", -123.0, "-0123");
        testParse(FRANCE, PATTERN, null, "123,4", 5, "float 1", 123.4, "123,4");
        testParse(FRANCE, PATTERN, null, "123,45", 6, "float 2", 123.45, "123,45");
        testParse(FRANCE, PATTERN, null, "123,456", 7, "float 3", 123.456, "123,456");
        testParse(FRANCE, PATTERN, null, "-123,4", 6, "float 1 negative", -123.4, "-123,4");
        testParse(FRANCE, PATTERN, null, "-123,45", 7, "float 2 negative", -123.45, "-123,45");
        testParse(FRANCE, PATTERN, null, "-123,456", 8, "float 3 negative", -123.456, "-123,456");
        testParse(FRANCE, PATTERN, null, "12 345", 6, "int thousands", 12345.0, "12 345");
        testParse(FRANCE, PATTERN, null, "-12 345", 7, "int thousands negative", -12345.0, "-12 345");
        testParse(FRANCE, PATTERN, null, "12 345,6", 8, "float 1 thousands", 12345.6, "12 345,6");
        testParse(FRANCE, PATTERN, null, "12 345,67", 9, "float 2 thousands", 12345.67, "12 345,67");
        testParse(FRANCE, PATTERN, null, "12 345,678", 10, "float 3 thousands", 12345.678, "12 345,678");
        testParse(FRANCE, PATTERN, null, "-12 345,6", 9, "float 1 thousands negative", -12345.6, "-12 345,6");
        testParse(FRANCE, PATTERN, null, "-12 345,67", 10, "float 2 thousands negative", -12345.67, "-12 345,67");
        testParse(FRANCE, PATTERN, null, "-12 345,678", 11, "float 3 thousands negative", -12345.678, "-12 345,678");
        testParse(FRANCE, PATTERN, null, "-1 234 567,89", 13, "float 2 million negative", -1234567.89, "-1 234 567,89");
        testParse(FRANCE, PATTERN, null, " -1 234 567,89", 14, "float 2 million negative with leading space", -1234567.89, " -1 234 567,89");
        testParse(FRANCE, PATTERN, null, " -1 234 567,89 ", 15, "float 2 million negative with leading and trailing space", -1234567.89, " -1 234 567,89 ");
    }

    @Test
    public void testParse_FR_PATTERN_WITH_LEADING_SPACES() {
        testParse(FRANCE, PATTERN_WITH_LEADING_SPACES, null, "0", 1, "zero", 0.0, "0");
        testParse(FRANCE, PATTERN_WITH_LEADING_SPACES, null, "00", 2, "zero dd", 0.0, "00");
        testParse(FRANCE, PATTERN_WITH_LEADING_SPACES, null, "0,0", 3, "zero d.d", 0.0, "0,0");
        testParse(FRANCE, PATTERN_WITH_LEADING_SPACES, null, "-0,00", 5, "zero d.dd negative", -0.0, "-0,00");
        testParse(FRANCE, PATTERN_WITH_LEADING_SPACES, null, "123", 3, "int", 123.0, "123");
        testParse(FRANCE, PATTERN_WITH_LEADING_SPACES, null, "-123", 4, "int negative", -123.0, "-123");
        testParse(FRANCE, PATTERN_WITH_LEADING_SPACES, null, "0123", 4, "int padding zero", 123.0, "0123");
        testParse(FRANCE, PATTERN_WITH_LEADING_SPACES, null, "-0123", 5, "int padding zero negative", -123.0, "-0123");
        testParse(FRANCE, PATTERN_WITH_LEADING_SPACES, null, "123,4", 5, "float 1", 123.4, "123,4");
        testParse(FRANCE, PATTERN_WITH_LEADING_SPACES, null, "123,45", 6, "float 2", 123.45, "123,45");
        testParse(FRANCE, PATTERN_WITH_LEADING_SPACES, null, "123,456", 7, "float 3", 123.456, "123,456");
        testParse(FRANCE, PATTERN_WITH_LEADING_SPACES, null, "-123,4", 6, "float 1 negative", -123.4, "-123,4");
        testParse(FRANCE, PATTERN_WITH_LEADING_SPACES, null, "-123,45", 7, "float 2 negative", -123.45, "-123,45");
        testParse(FRANCE, PATTERN_WITH_LEADING_SPACES, null, "-123,456", 8, "float 3 negative", -123.456, "-123,456");
        testParse(FRANCE, PATTERN_WITH_LEADING_SPACES, null, "12 345", 6, "int thousands", 12345.0, "12 345");
        testParse(FRANCE, PATTERN_WITH_LEADING_SPACES, null, "-12 345", 7, "int thousands negative", -12345.0, "-12 345");
        testParse(FRANCE, PATTERN_WITH_LEADING_SPACES, null, "12 345,6", 8, "float 1 thousands", 12345.6, "12 345,6");
        testParse(FRANCE, PATTERN_WITH_LEADING_SPACES, null, "12 345,67", 9, "float 2 thousands", 12345.67, "12 345,67");
        testParse(FRANCE, PATTERN_WITH_LEADING_SPACES, null, "12 345,678", 10, "float 3 thousands", 12345.678, "12 345,678");
        testParse(FRANCE, PATTERN_WITH_LEADING_SPACES, null, "-12 345,6", 9, "float 1 thousands negative", -12345.6, "-12 345,6");
        testParse(FRANCE, PATTERN_WITH_LEADING_SPACES, null, "-12 345,67", 10, "float 2 thousands negative", -12345.67, "-12 345,67");
        testParse(FRANCE, PATTERN_WITH_LEADING_SPACES, null, "-12 345,678", 11, "float 3 thousands negative", -12345.678, "-12 345,678");
        testParse(FRANCE, PATTERN_WITH_LEADING_SPACES, null, "-1 234 567,89", 13, "float 2 million negative", -1234567.89, "-1 234 567,89");
        testParse(FRANCE, PATTERN_WITH_LEADING_SPACES, null, " -1 234 567,89", 14, "float 2 million negative with leading space", -1234567.89, " -1 234 567,89");
        testParse(FRANCE, PATTERN_WITH_LEADING_SPACES, null, " -1 234 567,89 ", 15, "float 2 million negative with leading and trailing space", -1234567.89, " -1 234 567,89 ");
    }

    @Test
    public void testParse_FR_with_NBSP() {
        testParse(FRANCE, PATTERN, null, "12\u00A0345", 6, "int thousands", 12345.0, "12 345");
        testParse(FRANCE, PATTERN, null, "-12\u00A0345", 7, "int thousands negative", -12345.0, "-12 345");
        testParse(FRANCE, PATTERN, null, "12\u00A0345,6", 8, "float 1 thousands", 12345.6, "12 345,6");
        testParse(FRANCE, PATTERN, null, "12\u00A0345,67", 9, "float 2 thousands", 12345.67, "12 345,67");
        testParse(FRANCE, PATTERN, null, "12\u00A0345,678", 10, "float 3 thousands", 12345.678, "12 345,678");
        testParse(FRANCE, PATTERN, null, "-12\u00A0345,6", 9, "float 1 thousands negative", -12345.6, "-12 345,6");
        testParse(FRANCE, PATTERN, null, "-12\u00A0345,67", 10, "float 2 thousands negative", -12345.67, "-12 345,67");
        testParse(FRANCE, PATTERN, null, "-12\u00A0345,678", 11, "float 3 thousands negative", -12345.678, "-12 345,678");
        testParse(FRANCE, PATTERN, null, "-1\u00A0234\u00A0567,89", 13, "float 2 million negative", -1234567.89, "-1 234 567,89");
        testParse(FRANCE, PATTERN, null, "\u00A0-1\u00A0234\u00A0567,89", 14, "float 2 million negative with leading space", -1234567.89, " -1 234 567,89");
        testParse(FRANCE, PATTERN, null, "\u00A0-1\u00A0234\u00A0567,89\u00A0", 14, "float 2 million negative with leading and trailing space", -1234567.89, " -1 234 567,89 ");
    }

    @Test
    public void testParse_FR_with_NBSP_and_preset_decimal_symbols() {
        DecimalFormat decimalFormat = (DecimalFormat) NumberFormat.getCurrencyInstance(FRANCE);
        DecimalFormatSymbols syms = decimalFormat.getDecimalFormatSymbols();
        syms.setGroupingSeparator('\u00A0');

        testParse(FRANCE, PATTERN, syms, "12\u00A0345", 6, "int thousands", 12345.0, "12 345");
        testParse(FRANCE, PATTERN, syms, "-12\u00A0345", 7, "int thousands negative", -12345.0, "-12 345");
        testParse(FRANCE, PATTERN, syms, "12\u00A0345,6", 8, "float 1 thousands", 12345.6, "12 345,6");
        testParse(FRANCE, PATTERN, syms, "12\u00A0345,67", 9, "float 2 thousands", 12345.67, "12 345,67");
        testParse(FRANCE, PATTERN, syms, "12\u00A0345,678", 10, "float 3 thousands", 12345.678, "12 345,678");
        testParse(FRANCE, PATTERN, syms, "-12\u00A0345,6", 9, "float 1 thousands negative", -12345.6, "-12 345,6");
        testParse(FRANCE, PATTERN, syms, "-12\u00A0345,67", 10, "float 2 thousands negative", -12345.67, "-12 345,67");
        testParse(FRANCE, PATTERN, syms, "-12\u00A0345,678", 11, "float 3 thousands negative", -12345.678, "-12 345,678");
        testParse(FRANCE, PATTERN, syms, "-1\u00A0234\u00A0567,89", 13, "float 2 million negative", -1234567.89, "-1 234 567,89");
        testParse(FRANCE, PATTERN, syms, "\u00A0-1\u00A0234\u00A0567,89", 14, "float 2 million negative with leading space", -1234567.89, " -1 234 567,89");
        testParse(FRANCE, PATTERN, syms, "\u00A0-1\u00A0234\u00A0567,89\u00A0", 14, "float 2 million negative with leading and trailing space", -1234567.89, " -1 234 567,89 ");
    }

    @Test
    public void testParse_CN() {
        testParse(CHINA, PATTERN, null, "0", 1, "zero", 0.0, "0");
        testParse(CHINA, PATTERN, null, "00", 2, "zero dd", 0.0, "00");
        testParse(CHINA, PATTERN, null, "0.0", 3, "zero d.d", 0.0, "0.0");
        testParse(CHINA, PATTERN, null, "-0.00", 5, "zero d.dd negative", -0.0, "-0.00");
        testParse(CHINA, PATTERN, null, "123", 3, "int", 123.0, "123");
        testParse(CHINA, PATTERN, null, "-123", 4, "int negative", -123.0, "-123");
        testParse(CHINA, PATTERN, null, "0123", 4, "int padding zero", 123.0, "0123");
        testParse(CHINA, PATTERN, null, "-0123", 5, "int padding zero negative", -123.0, "-0123");
        testParse(CHINA, PATTERN, null, "123.4", 5, "float 1", 123.4, "123.4");
        testParse(CHINA, PATTERN, null, "123.45", 6, "float 2", 123.45, "123.45");
        testParse(CHINA, PATTERN, null, "123.456", 7, "float 3", 123.456, "123.456");
        testParse(CHINA, PATTERN, null, "-123.4", 6, "float 1 negative", -123.4, "-123.4");
        testParse(CHINA, PATTERN, null, "-123.45", 7, "float 2 negative", -123.45, "-123.45");
        testParse(CHINA, PATTERN, null, "-123.456", 8, "float 3 negative", -123.456, "-123.456");
        testParse(CHINA, PATTERN, null, "12,345", 6, "int thousands", 12345.0, "12,345");
        testParse(CHINA, PATTERN, null, "-12,345", 7, "int thousands negative", -12345.0, "-12,345");
        testParse(CHINA, PATTERN, null, "12,345.6", 8, "float 1 thousands", 12345.6, "12,345.6");
        testParse(CHINA, PATTERN, null, "12,345.67", 9, "float 2 thousands", 12345.67, "12,345.67");
        testParse(CHINA, PATTERN, null, "12,345.678", 10, "float 3 thousands", 12345.678, "12,345.678");
        testParse(CHINA, PATTERN, null, "-12,345.6", 9, "float 1 thousands negative", -12345.6, "-12,345.6");
        testParse(CHINA, PATTERN, null, "-12,345.67", 10, "float 2 thousands negative", -12345.67, "-12,345.67");
        testParse(CHINA, PATTERN, null, "-12,345.678", 11, "float 3 thousands negative", -12345.678, "-12,345.678");
        testParse(CHINA, PATTERN, null, "-1,234,567.89", 13, "float 2 million negative", -1234567.89, "-1,234,567.89");
        testParse(CHINA, PATTERN, null, "-123,4567.89", 12, "grouped by ten thousands", -1234567.89, "-123,4567.89");
    }

    /**
     * testcase for https://github.com/JavaMoney/jsr354-ri/issues/281
     */
    @Test
    public void testParse_with_literals_in_pattern() {
        testParse(US, "#,##0.00 ", null, "-123.45", 7, "without literal", -123.45, "-123.45");
        testParse(US, "BEFORE #,##0.00 ", null, "BEFORE 123.45", 13, "Literal on start", 123.45, "BEFORE 123.45");
//FIXME        testParse(US, "ONE TWO #,##0.00 ", "ONE TWO -123.45", 7, "Two literals on start", -123.45);
    }

    @Test
    public void testPrint_US() throws IOException {
        AmountNumberToken token = new AmountNumberToken(contextForLocale(US, null), PATTERN);
        FastMoney amount = FastMoney.of(-42.00,"USD");
        StringBuilder sb = new StringBuilder();
        token.print(sb, amount);
        assertEquals(sb.toString(), "-42.00 ");
    }

    @Test
    public void testToString_US() {
        AmountNumberToken token = new AmountNumberToken(contextForLocale(US, null), PATTERN);
        assertEquals(token.toString(), "AmountNumberToken [locale=en_US, partialNumberPattern=#,##0.00 ]");
    }

    private AmountFormatContext contextForLocale(Locale locale, DecimalFormatSymbols syms) {
        AmountFormatQuery amountFormatQuery = AmountFormatQueryBuilder.of(locale).set(CODE).build();
        AmountFormatContextBuilder builder = AmountFormatContextBuilder.of(DEFAULT_STYLE);
        builder.setLocale(locale);
        builder.importContext(amountFormatQuery, false);
        builder.setMonetaryAmountFactory(amountFormatQuery.getMonetaryAmountFactory());
        if (syms != null) {
            builder.set(DecimalFormatSymbols.class, syms);
        }
        AmountFormatContext amountFormatContext = builder.build();
        return amountFormatContext;
    }

    private void testParse(Locale locale, String pattern, DecimalFormatSymbols syms, String input, int expectedIndex, String comment, double amount, String formatted) {
        String errorPrefix = "For " + locale + " " + comment + " input \"" + input + "\": ";
        AmountNumberToken token = new AmountNumberToken(contextForLocale(locale, syms), pattern);
        ParseContext context = new ParseContext(input);
        try {
            token.parse(context);
        } catch (MonetaryParseException e) {
            System.err.println(e.getMessage());
            assertEquals(e.getInput(), input);
            assertEquals(e.getErrorIndex(), 0);

            assertNull(context.getParsedNumber(), errorPrefix);
            assertFalse(context.isComplete(), errorPrefix);
            assertTrue(context.hasError(), errorPrefix);
            assertEquals(context.getOriginalInput(), input, errorPrefix);
            fail(errorPrefix + context.getErrorMessage());
        }

        assertEquals(context.getParsedNumber().doubleValue(), amount, errorPrefix);
        assertEquals(context.getIndex(), expectedIndex, errorPrefix);
        assertFalse(context.isComplete(), errorPrefix);
        assertFalse(context.hasError(), errorPrefix);
        assertEquals(MoneyUtils.replaceNbspWithSpace(context.getOriginalInput()), formatted, errorPrefix);
    }
}