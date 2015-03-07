/**
 * Copyright (c) 2012, 2015, Credit Suisse (Anatole Tresch), Werner Keil and others by the @author tag.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package org.javamoney.moneta.convert.internal;

import static javax.money.convert.MonetaryConversions.getExchangeRateProvider;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertTrue;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.Month;
import java.time.YearMonth;
import java.util.Objects;

import javax.money.CurrencyUnit;
import javax.money.MonetaryAmount;
import javax.money.MonetaryCurrencies;
import javax.money.convert.ConversionQuery;
import javax.money.convert.ConversionQueryBuilder;
import javax.money.convert.CurrencyConversion;
import javax.money.convert.ExchangeRateProvider;

import org.javamoney.moneta.ExchangeRateType;
import org.javamoney.moneta.Money;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

public class ECBHistoricRateProviderTest {

    private static final CurrencyUnit EURO = MonetaryCurrencies
            .getCurrency("EUR");
    private static final CurrencyUnit DOLLAR = MonetaryCurrencies
            .getCurrency("USD");

    private static final CurrencyUnit BRAZILIAN_REAL = MonetaryCurrencies
            .getCurrency("BRL");

    private ExchangeRateProvider provider;

    @BeforeTest
    public void setup() throws InterruptedException {
        provider = getExchangeRateProvider(ExchangeRateType.ECB_HIST);
        Thread.sleep(20_000L);
    }

    @Test
    public void shouldReturnsECBHistoricRateProvider() {
        assertTrue(Objects.nonNull(provider));
        assertEquals(provider.getClass(), ECBHistoricRateProvider.class);
    }

    @Test
    public void shouldReturnsSameDollarValue() {
        CurrencyConversion currencyConversion = provider.getCurrencyConversion(DOLLAR);
        assertNotNull(currencyConversion);
        MonetaryAmount money = Money.of(BigDecimal.TEN, DOLLAR);
        MonetaryAmount result = currencyConversion.apply(money);

        assertEquals(result.getCurrency(), DOLLAR);
        assertEquals(result.getNumber().numberValue(BigDecimal.class),
                BigDecimal.TEN);

    }

    @Test
    public void shouldReturnsSameBrazilianValue() {
        CurrencyConversion currencyConversion = provider
                .getCurrencyConversion(BRAZILIAN_REAL);
        assertNotNull(currencyConversion);
        MonetaryAmount money = Money.of(BigDecimal.TEN, BRAZILIAN_REAL);
        MonetaryAmount result = currencyConversion.apply(money);

        assertEquals(result.getCurrency(), BRAZILIAN_REAL);
        assertEquals(result.getNumber().numberValue(BigDecimal.class),
                BigDecimal.TEN);

    }

    @Test
    public void shouldReturnsSameEuroValue() {
        CurrencyConversion currencyConversion = provider
                .getCurrencyConversion(EURO);
        assertNotNull(currencyConversion);
        MonetaryAmount money = Money.of(BigDecimal.TEN, EURO);
        MonetaryAmount result = currencyConversion.apply(money);

        assertEquals(result.getCurrency(), EURO);
        assertEquals(result.getNumber().numberValue(BigDecimal.class),
                BigDecimal.TEN);

    }

    @Test
    public void shouldConvertsDolarToEuro() {
        CurrencyConversion currencyConversion = provider
                .getCurrencyConversion(EURO);
        assertNotNull(currencyConversion);
        MonetaryAmount money = Money.of(BigDecimal.TEN, DOLLAR);
        MonetaryAmount result = currencyConversion.apply(money);

        assertEquals(result.getCurrency(), EURO);
        assertTrue(result.getNumber().doubleValue() > 0);

    }

    @Test
    public void shouldConvertsEuroToDollar() {
        CurrencyConversion currencyConversion = provider
                .getCurrencyConversion(DOLLAR);
        assertNotNull(currencyConversion);
        MonetaryAmount money = Money.of(BigDecimal.TEN, EURO);
        MonetaryAmount result = currencyConversion.apply(money);

        assertEquals(result.getCurrency(), DOLLAR);
        assertTrue(result.getNumber().doubleValue() > 0);

    }

    @Test
    public void shouldConvertsBrazilianToDollar() {
        CurrencyConversion currencyConversion = provider
                .getCurrencyConversion(DOLLAR);
        assertNotNull(currencyConversion);
        MonetaryAmount money = Money.of(BigDecimal.TEN, BRAZILIAN_REAL);
        MonetaryAmount result = currencyConversion.apply(money);

        assertEquals(result.getCurrency(), DOLLAR);
        assertTrue(result.getNumber().doubleValue() > 0);

    }

    @Test
    public void shouldConvertsDollarToBrazilian() {
        CurrencyConversion currencyConversion = provider
                .getCurrencyConversion(BRAZILIAN_REAL);
        assertNotNull(currencyConversion);
        MonetaryAmount money = Money.of(BigDecimal.TEN, DOLLAR);
        MonetaryAmount result = currencyConversion.apply(money);

        assertEquals(result.getCurrency(), BRAZILIAN_REAL);
        assertTrue(result.getNumber().doubleValue() > 0);

    }

    @Test
    public void shouldSetTimeInLocalDateTime() {

        LocalDate localDate = YearMonth.of(2014, Month.JANUARY).atDay(9);
        ConversionQuery conversionQuery = ConversionQueryBuilder.of()
                .setTermCurrency(EURO).set(localDate).build();
        CurrencyConversion currencyConversion = provider
                .getCurrencyConversion(conversionQuery);
        assertNotNull(currencyConversion);
        MonetaryAmount money = Money.of(BigDecimal.TEN, DOLLAR);
        MonetaryAmount result = currencyConversion.apply(money);

        assertEquals(result.getCurrency(), EURO);
        assertTrue(result.getNumber().doubleValue() > 0);

    }
}
