package org.javamoney.moneta.format;

import static org.testng.Assert.assertEquals;
import org.javamoney.moneta.Money;
import org.javamoney.moneta.function.MoneyProducer;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import javax.money.CurrencyUnit;
import javax.money.Monetary;
import javax.money.MonetaryAmount;
import javax.money.format.AmountFormatContext;
import javax.money.format.MonetaryAmountFormat;
import javax.money.format.MonetaryParseException;
import java.io.IOException;
import java.text.NumberFormat;
import java.util.Locale;

public class MonetaryAmountDecimalFormatTest {

    private static final int EXAMPLE_VALUE = 10;

    private  MonetaryAmountFormat format;

    private Locale locale;

    private CurrencyUnit currencyUnit;

    private NumberFormat numberFormat;

    @BeforeMethod
    public void setup() {
        locale = Locale.US;
        currencyUnit = Monetary.getCurrency(locale);
        currencyUnit = Monetary.getCurrency(locale);
        format = MonetaryAmountDecimalFormatBuilder.of(locale).withProducer(new MoneyProducer())
                .withCurrencyUnit(currencyUnit).build();
        numberFormat = NumberFormat.getCurrencyInstance(locale);
    }

    @Test
    public void shouldReturnContext() {
        AmountFormatContext context = format.getContext();
        Assert.assertEquals(MonetaryAmountDecimalFormat.STYLE, context.getFormatName());
    }

    @Test(expectedExceptions = NullPointerException.class)
    public void shouldReturnsErrorWhenAppendableIsNull() throws IOException {
        format.print(null, null);
    }

    @Test
    public void shouldPrintNullWhenMonetaryAmountIsNull() throws IOException {
        StringBuilder sb = new StringBuilder();
        format.print(sb, null);
        assertEquals(sb.toString(), "null");
    }

    @Test
    public void shouldPrintMonetaryAmount() throws IOException {
        StringBuilder sb = new StringBuilder();
        MonetaryAmount money = Money.of(EXAMPLE_VALUE, currencyUnit);
        format.print(sb, money);

        assertEquals(sb.toString(), numberFormat.format(EXAMPLE_VALUE));
    }

    @Test
    public void shouldQueryFromNullWhenMonetaryAmountIsNull() throws IOException {
        assertEquals(format.queryFrom(null), "null");
    }

    @Test
    public void shouldQueryFromMonetaryAmount() throws IOException {
        MonetaryAmount money = Money.of(EXAMPLE_VALUE, currencyUnit);
        assertEquals(format.queryFrom(money), numberFormat.format(EXAMPLE_VALUE));
    }

    @Test(expectedExceptions = NullPointerException.class)
    public void shouldReturnErrorParseWhenMonetaryAmountIsNull() {
        format.parse(null);
    }

    @Test(expectedExceptions = MonetaryParseException.class)
    public void shouldReturnErrorParseWhenMonetaryAmountIsInvalid() {
        format.parse("ERROR");
    }

    @Test
    public void shouldParseMonetaryAmount() throws IOException {
        MonetaryAmount money = Money.of(EXAMPLE_VALUE, currencyUnit);
        String parse = format.queryFrom(money);
        assertEquals(format.parse(parse), money);
    }

}
