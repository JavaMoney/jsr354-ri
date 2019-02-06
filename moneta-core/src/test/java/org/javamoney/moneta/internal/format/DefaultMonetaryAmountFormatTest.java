package org.javamoney.moneta.internal.format;

import org.javamoney.moneta.FastMoney;
import org.testng.annotations.Test;

import javax.money.MonetaryAmount;
import javax.money.format.AmountFormatContext;
import javax.money.format.AmountFormatContextBuilder;
import javax.money.format.MonetaryParseException;

import static java.util.Locale.US;
import static org.testng.Assert.assertEquals;

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
//FIXME        assertEquals(parsedAmount.toString(), "USD 1000.42");
        // see https://github.com/JavaMoney/jsr354-ri/issues/283
        assertEquals(parsedAmount.toString(), "USD 1000.420000000000000000000000000000000000000000000000000000000000000");
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
//FIXME        assertEquals(parsedAmount.toString(), "USD 0.01");
        // see https://github.com/JavaMoney/jsr354-ri/issues/283
        assertEquals(parsedAmount.toString(), "USD 0.010000000000000000000000000000000000000000000000000000000000000");
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

}