package org.javamoney.moneta.spi;

import org.javamoney.moneta.FastMoney;
import org.javamoney.moneta.Money;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import javax.money.CurrencyUnit;
import javax.money.Monetary;
import javax.money.MonetaryAmount;
import javax.money.convert.*;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

public class CurrencyConversionTest {

    private AbstractRateProvider exactRateProvider;
    private AbstractRateProvider integerRateProvider;

    private final static String BASE_CURRENCY = "USD";
    private final static String TERM_CURRENCY = "UAH";


    private interface ExchangeRateStub extends ExchangeRate {

        @Override
        default ConversionContext getContext() {
            return ConversionContext.OTHER_CONVERSION;
        }

        @Override
        default CurrencyUnit getBaseCurrency() {
            return Monetary.getCurrency(BASE_CURRENCY);
        }

        @Override
        default CurrencyUnit getCurrency() {
            return Monetary.getCurrency(TERM_CURRENCY);
        }

        @Override
        default List<ExchangeRate> getExchangeRateChain() {
            return Collections.emptyList();
        }
    }

    @BeforeClass
    public void init() {

        exactRateProvider = new AbstractRateProvider(ProviderContext.of("test_ctx_accurate")) {
            @Override
            public ExchangeRate getExchangeRate(ConversionQuery conversionQuery) {
                return (ExchangeRateStub) () -> new DefaultNumberValue(new BigDecimal("28.456"));
            }
        };

        integerRateProvider = new AbstractRateProvider(ProviderContext.of("test_ctx_estimated")) {
            @Override
            public ExchangeRate getExchangeRate(ConversionQuery conversionQuery) {
                return (ExchangeRateStub) () -> new DefaultNumberValue(28);
            }
        };
    }

    @Test
    public void testMoneyConversion() {
        testMoneyConversionHelper(
                Money.of(0, BASE_CURRENCY),
                Money.of(1000, BASE_CURRENCY),
                Money.of(new BigDecimal("1000.345"), BASE_CURRENCY));
    }

    @Test
    public void testFastMoneyConversion() {
        testMoneyConversionHelper(
                FastMoney.of(0, BASE_CURRENCY),
                FastMoney.of(1000, BASE_CURRENCY),
                FastMoney.of(new BigDecimal("1000.345"), BASE_CURRENCY));
    }

    private void testMoneyConversionHelper(MonetaryAmount zeroAmount, MonetaryAmount roundedAmount, MonetaryAmount amountWithCents) {

        assertConverted(zeroAmount.with(exactRateProvider.getCurrencyConversion(TERM_CURRENCY)), "0");
        assertConverted(amountWithCents.with(exactRateProvider.getCurrencyConversion(TERM_CURRENCY)), "28465.81732");
        assertConverted(roundedAmount.with(exactRateProvider.getCurrencyConversion(TERM_CURRENCY)), "28456");
        assertConverted(amountWithCents.with(integerRateProvider.getCurrencyConversion(TERM_CURRENCY)), "28009.66");
        assertConverted(roundedAmount.with(integerRateProvider.getCurrencyConversion(TERM_CURRENCY)), "28000");
    }

    private void assertConverted(MonetaryAmount converted, String expectedValue) {
        assertEquals(converted.getCurrency(), Monetary.getCurrency(TERM_CURRENCY));
        assertTrue(converted.isEqualTo(Money.of(new BigDecimal(expectedValue), TERM_CURRENCY)),
                converted + " should have numeric value " + expectedValue);
    }
}