package org.javamoney.moneta.format;

import org.javamoney.moneta.function.FastMoneyProducer;
import org.javamoney.moneta.function.MoneyProducer;
import org.testng.Assert;
import org.testng.annotations.Test;

import javax.money.CurrencyUnit;
import javax.money.Monetary;
import javax.money.format.MonetaryAmountFormat;
import java.util.Locale;

import static org.testng.Assert.assertEquals;

public class MonetaryAmountDecimalFormatBuilderTest {

    @Test
    public void shouldCreateDefaultBuilder() {
        MonetaryAmountDecimalFormat format = (MonetaryAmountDecimalFormat) MonetaryAmountDecimalFormatBuilder.newInstance().build();
        assertEquals(format.getCurrencyUnit(), Monetary.getCurrency(Locale.getDefault()));
        assertEquals(format.getProducer().getClass(), MoneyProducer.class);
        assertEquals(format.getDecimalFormat().getCurrency().getCurrencyCode(),format.getCurrencyUnit().getCurrencyCode());
    }

    @Test
    public void shouldCreateSettingProducer() {
        MonetaryAmountDecimalFormat format = (MonetaryAmountDecimalFormat) MonetaryAmountDecimalFormatBuilder.newInstance().withProducer(new FastMoneyProducer())
                .build();
        assertEquals(format.getCurrencyUnit(), Monetary.getCurrency(Locale.getDefault()));
        assertEquals(format.getProducer().getClass(), FastMoneyProducer.class);
        assertEquals(format.getDecimalFormat().getCurrency().getCurrencyCode(),format.getCurrencyUnit().getCurrencyCode());
    }

    @Test
    public void shouldCreateSettingCurrencyUnit() {
        CurrencyUnit currencyUnit = Monetary.getCurrency("BRL");
        MonetaryAmountDecimalFormat format = (MonetaryAmountDecimalFormat) MonetaryAmountDecimalFormatBuilder.newInstance().withProducer(new FastMoneyProducer())
                .withCurrencyUnit(currencyUnit).build();
        assertEquals(format.getCurrencyUnit(), currencyUnit);
        assertEquals(format.getProducer().getClass(), FastMoneyProducer.class);
        assertEquals(format.getDecimalFormat().getCurrency().getCurrencyCode(),format.getCurrencyUnit().getCurrencyCode());
    }



}
