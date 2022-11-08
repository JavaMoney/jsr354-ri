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
                Money.of(new BigDecimal("1000.345"), BASE_CURRENCY)
        );
    }

    @Test
    public void testFastMoneyConversion() {

        testMoneyConversionHelper(
                FastMoney.of(0, BASE_CURRENCY),
                FastMoney.of(1000, BASE_CURRENCY),
                FastMoney.of(new BigDecimal("1000.345"), BASE_CURRENCY)
        );
    }

    private void testMoneyConversionHelper(MonetaryAmount zeroAmount, MonetaryAmount roundedAmount, MonetaryAmount amountWithCents) {

        assertEquals(zeroAmount.with(
                exactRateProvider.getCurrencyConversion(TERM_CURRENCY)
                ).toString(),
                "UAH 0.00"
        );

        assertEquals(
                amountWithCents.with(
                        exactRateProvider.getCurrencyConversion(TERM_CURRENCY)
                ).toString(),
                "UAH 28465.82"
        );

        assertEquals(
                roundedAmount.with(
                        exactRateProvider.getCurrencyConversion(TERM_CURRENCY)
                ).toString(),
                "UAH 28456.00"
        );

        assertEquals(
                amountWithCents.with(
                        integerRateProvider.getCurrencyConversion(TERM_CURRENCY)
                ).toString(),
                "UAH 28009.66"
        );

        assertEquals(
                roundedAmount.with(
                        integerRateProvider.getCurrencyConversion(TERM_CURRENCY)
                ).toString(),
                "UAH 28000.00"
        );
    }
}