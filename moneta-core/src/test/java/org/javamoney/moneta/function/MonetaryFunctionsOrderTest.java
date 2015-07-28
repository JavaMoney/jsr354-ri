package org.javamoney.moneta.function;

import static org.javamoney.moneta.function.MonetaryFunctions.sortCurrencyUnit;
import static org.javamoney.moneta.function.MonetaryFunctions.sortCurrencyUnitDesc;
import static org.javamoney.moneta.function.MonetaryFunctions.sortNumber;
import static org.javamoney.moneta.function.MonetaryFunctions.sortNumberDesc;
import static org.javamoney.moneta.function.StreamFactory.BRAZILIAN_REAL;
import static org.javamoney.moneta.function.StreamFactory.DOLLAR;
import static org.javamoney.moneta.function.StreamFactory.EURO;
import static org.javamoney.moneta.function.StreamFactory.currencies;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.money.MonetaryAmount;
import javax.money.convert.ExchangeRateProvider;

import junit.framework.Assert;

import org.javamoney.moneta.Money;
import org.testng.annotations.Test;

public class MonetaryFunctionsOrderTest {

    @Test
    public void sortCurrencyUnitTest() {
        MonetaryAmount money = currencies().sorted(sortCurrencyUnit())
                .findFirst().get();
        Assert.assertEquals(BRAZILIAN_REAL, money.getCurrency());
    }

    @Test
    public void sortCurrencyUnitDescTest() {
        MonetaryAmount money = currencies().sorted(sortCurrencyUnitDesc())
                .findFirst().get();
        Assert.assertEquals(DOLLAR, money.getCurrency());
    }

    @Test
    public void sortorderNumberTest() {
        MonetaryAmount money = currencies().sorted(sortNumber())
                .findFirst().get();
        Assert.assertEquals(BigDecimal.ZERO, money.getNumber().numberValue(BigDecimal.class));
    }

    @Test
    public void sortorderNumberDescTest() {
        MonetaryAmount money = currencies().sorted(sortNumberDesc())
                .findFirst().get();
        Assert.assertEquals(BigDecimal.TEN, money.getNumber().numberValue(BigDecimal.class));
    }

    @Test
    public void sortCurrencyUnitAndNumberTest() {
        MonetaryAmount money = currencies().sorted(sortCurrencyUnit().thenComparing(sortNumber()))
                .findFirst().get();

        Assert.assertEquals(BRAZILIAN_REAL, money.getCurrency());
        Assert.assertEquals(BigDecimal.ZERO, money.getNumber().numberValue(BigDecimal.class));
    }
}
