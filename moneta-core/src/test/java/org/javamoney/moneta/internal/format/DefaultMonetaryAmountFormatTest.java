package org.javamoney.moneta.internal.format;

import org.javamoney.moneta.FastMoney;
import org.testng.annotations.Test;

import javax.money.CurrencyUnit;
import javax.money.Monetary;
import javax.money.MonetaryAmount;
import javax.money.format.AmountFormatContext;
import javax.money.format.AmountFormatContextBuilder;
import javax.money.format.MonetaryParseException;

import static java.util.Locale.US;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertSame;

public class DefaultMonetaryAmountFormatTest {

    @Test
    public void testFormat() {
        AmountFormatContextBuilder builder = AmountFormatContextBuilder.of(US);
        AmountFormatContext context = builder.build();
        DefaultMonetaryAmountFormat format = new DefaultMonetaryAmountFormat(context);
        String formatted = format.format(FastMoney.of(1000.42, "USD"));
        assertEquals(formatted, "USD1,000.42");
    }

    @Test
    public void testFormat_with_custom_pattern() {
        AmountFormatContextBuilder builder = AmountFormatContextBuilder.of(US);
        builder.set("pattern", "0.00 ¤");
        AmountFormatContext context = builder.build();
        DefaultMonetaryAmountFormat format = new DefaultMonetaryAmountFormat(context);
        String formatted = format.format(FastMoney.of(1000.42, "USD"));
        assertEquals(formatted, "1000.42 USD");
    }

    @Test
    public void testFormat_with_two_patterns() {
        AmountFormatContextBuilder builder = AmountFormatContextBuilder.of(US);
        builder.set("pattern", "0.00 ¤;0.0 ¤");
        AmountFormatContext context = builder.build();
        DefaultMonetaryAmountFormat format = new DefaultMonetaryAmountFormat(context);
        String formatted = format.format(FastMoney.of(1000.42, "USD"));
        assertEquals(formatted, "1000.42 USD");
        formatted = format.format(FastMoney.of(-1000.42, "USD"));
        assertEquals(formatted, "-1000.4 USD");
    }

    @Test
    public void testFormat_with_two_patterns_without_currency() {
        AmountFormatContextBuilder builder = AmountFormatContextBuilder.of(US);
        builder.set("pattern", "0.00;0.0");
        AmountFormatContext context = builder.build();
        DefaultMonetaryAmountFormat format = new DefaultMonetaryAmountFormat(context);
        String formatted = format.format(FastMoney.of(1000.42, "USD"));
        assertEquals(formatted, "1000.42");
        formatted = format.format(FastMoney.of(-1000.42, "USD"));
        assertEquals(formatted, "-1000.4");
    }

    @Test
    public void testParse() {
        AmountFormatContextBuilder builder = AmountFormatContextBuilder.of(US);
        AmountFormatContext context = builder.build();
        DefaultMonetaryAmountFormat format = new DefaultMonetaryAmountFormat(context);
        MonetaryAmount parsedAmount = format.parse("USD1,000.42");
        assertEquals(parsedAmount.getCurrency().getCurrencyCode(), "USD");
        assertEquals(parsedAmount.getNumber().doubleValueExact(), 1000.42D);
        assertEquals(parsedAmount.toString(), "USD 1000.42");
    }

    @Test
    public void testParse_with_custom_pattern() {
        AmountFormatContextBuilder builder = AmountFormatContextBuilder.of(US);
        builder.set("pattern", "0.00 ¤");
        AmountFormatContext context = builder.build();
        DefaultMonetaryAmountFormat format = new DefaultMonetaryAmountFormat(context);
        MonetaryAmount parsedAmount = format.parse("0.01 USD");
        assertEquals(parsedAmount.getCurrency().getCurrencyCode(), "USD");
        assertEquals(parsedAmount.getNumber().doubleValueExact(), 0.01D);
        assertEquals(parsedAmount.toString(), "USD 0.01");
    }

    @Test
    public void testParse_pattern_without_currency_sign() {
        AmountFormatContextBuilder builder = AmountFormatContextBuilder.of(US);
        builder.set("pattern", "0.00");
        AmountFormatContext context = builder.build();
        DefaultMonetaryAmountFormat format = new DefaultMonetaryAmountFormat(context);
        try {
            format.parse("1,000.42");
        } catch (MonetaryParseException e) {
            assertEquals(e.getMessage(), "Failed to parse currency. Is currency sign ¤ present in pattern?");
            assertEquals(e.getErrorIndex(), -1);
        }
    }

    @Test
    public void testParse_pattern_without_currency_sign_but_with_currency_in_context() {
        CurrencyUnit usd = Monetary.getCurrency("USD");
        AmountFormatContextBuilder builder = AmountFormatContextBuilder.of(US);
        builder.set("pattern", "0.00");
        builder.set(CurrencyUnit.class, usd);
        AmountFormatContext context = builder.build();
        DefaultMonetaryAmountFormat format = new DefaultMonetaryAmountFormat(context);
        MonetaryAmount parsedAmount = format.parse("0.01");
        assertSame(parsedAmount.getCurrency(), usd);
        assertEquals(parsedAmount.getNumber().doubleValueExact(), 0.01D);
        assertEquals(parsedAmount.toString(), "USD 0.01");
    }

    /**
     * Test related to https://github.com/JavaMoney/jsr354-ri/issues/294
     */
    @Test
    public void testParse_pattern_with_currency_sign_and_with_currency_in_context_but_amount_is_without_currency_code() {
        CurrencyUnit usd = Monetary.getCurrency("USD");
        AmountFormatContextBuilder builder = AmountFormatContextBuilder.of(US);
        builder.set("pattern", "0.00 ¤");
        builder.set(CurrencyUnit.class, usd);
        AmountFormatContext context = builder.build();
        DefaultMonetaryAmountFormat format = new DefaultMonetaryAmountFormat(context);
        try {
            MonetaryAmount parsedAmount = format.parse("0.01");
        } catch (MonetaryParseException e) {
            assertEquals(e.getMessage(), "Error parsing CurrencyUnit: no input.");
            assertEquals(e.getErrorIndex(), -1);
        }
//FIXME        assertSame(parsedAmount.getCurrency(), usd);
//FIXME        assertEquals(parsedAmount.getNumber().doubleValueExact(), 0.01D);
//FIXME        assertEquals(parsedAmount.toString(), "USD 0.01");
    }
}