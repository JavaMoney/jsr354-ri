/**
 * Copyright (c) 2012, 2023, Werner Keil and others by the @author tag.
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
package org.javamoney.moneta.convert.ecb;

import static javax.money.convert.MonetaryConversions.getExchangeRateProvider;
import static org.testng.Assert.*;

import java.math.BigDecimal;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoField;
import java.util.Objects;

import javax.money.CurrencyUnit;
import javax.money.Monetary;
import javax.money.MonetaryAmount;
import javax.money.convert.ConversionQueryBuilder;
import javax.money.convert.CurrencyConversion;
import javax.money.convert.ExchangeRateProvider;
import javax.money.convert.MonetaryConversions;

import org.javamoney.moneta.Money;
import org.javamoney.moneta.convert.ExchangeRateType;
import org.javamoney.moneta.convert.ecb.ECBHistoricRateProvider;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

public class ECBHistoricRateProviderTest {

    private static final CurrencyUnit EURO = Monetary
            .getCurrency("EUR");
    private static final CurrencyUnit DOLLAR = Monetary
            .getCurrency("USD");

    private static final CurrencyUnit BRAZILIAN_REAL = Monetary
            .getCurrency("BRL");

    private ExchangeRateProvider provider;

    @BeforeTest
    public void setup() throws InterruptedException {
        provider = getExchangeRateProvider(ExchangeRateType.ECB_HIST);
    }

    @Test
    public void shouldReturnECBHistoricRateProvider() {
        assertTrue(Objects.nonNull(provider));
        assertEquals(provider.getClass(), ECBHistoricRateProvider.class);
    }

    @Test
    public void shouldReturnSameDollarValue() {
        CurrencyConversion currencyConversion = provider.getCurrencyConversion(DOLLAR);
        assertNotNull(currencyConversion);
        MonetaryAmount money = Money.of(BigDecimal.TEN, DOLLAR);
        MonetaryAmount result = currencyConversion.apply(money);

        assertEquals(result.getCurrency(), DOLLAR);
        assertEquals(result.getNumber().numberValue(BigDecimal.class),
                BigDecimal.TEN);

    }

    @Test
    public void shouldReturnSameBrazilianValue() {
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
    public void shouldReturnSameEuroValue() {
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
    public void shouldConvertDollarToEuro() {
        CurrencyConversion currencyConversion = provider
                .getCurrencyConversion(EURO);
        assertNotNull(currencyConversion);
        MonetaryAmount money = Money.of(BigDecimal.TEN, DOLLAR);
        MonetaryAmount result = currencyConversion.apply(money);

        assertEquals(result.getCurrency(), EURO);
        assertTrue(result.getNumber().doubleValue() > 0);

    }

    @Test
    public void shouldConvertEuroToDollar() {
        CurrencyConversion currencyConversion = provider
                .getCurrencyConversion(DOLLAR);
        assertNotNull(currencyConversion);
        MonetaryAmount money = Money.of(BigDecimal.TEN, EURO);
        MonetaryAmount result = currencyConversion.apply(money);

        assertEquals(result.getCurrency(), DOLLAR);
        assertTrue(result.getNumber().doubleValue() > 0);

    }

    @Test
    public void shouldConvertBrazilianToDollar() {
        CurrencyConversion currencyConversion = provider
                .getCurrencyConversion(DOLLAR);
        assertNotNull(currencyConversion);
        MonetaryAmount money = Money.of(BigDecimal.TEN, BRAZILIAN_REAL);
        MonetaryAmount result = currencyConversion.apply(money);

        assertEquals(result.getCurrency(), DOLLAR);
        assertTrue(result.getNumber().doubleValue() > 0);

    }

    @Test
    public void shouldConvertDollarToBrazilian() {
        CurrencyConversion currencyConversion = provider
                .getCurrencyConversion(BRAZILIAN_REAL);
        assertNotNull(currencyConversion);
        MonetaryAmount money = Money.of(BigDecimal.TEN, DOLLAR);
        MonetaryAmount result = currencyConversion.apply(money);

        assertEquals(result.getCurrency(), BRAZILIAN_REAL);
        assertTrue(result.getNumber().doubleValue() > 0);

    }

    @Test
    void selectFromECBWithGivenDate() {
        MonetaryAmount inEUR = Money.of(BigDecimal.TEN, "EUR");

        final ExchangeRateProvider provider2 = getExchangeRateProvider(ExchangeRateType.ECB);
        LocalDateTime time = LocalDateTime.now();
        // As ECB rates are only updated on 4pm of each day, we check, if it's after 4 or the day is on a weekend (cannot get all bank holidays yet)
        if (isWeekend(time)) {
            time = time.with(DayOfWeek.FRIDAY);
        } else {
            if (time.getHour() < 16) {
                time = time.minusDays(1);
            }
        }
        CurrencyConversion conv2 = provider2.getCurrencyConversion(ConversionQueryBuilder.of()
                .setTermCurrency("USD")
                .set(time)
                .build());
/*
        CurrencyConversion conv2 = MonetaryConversions.getConversion(ConversionQueryBuilder.of()
                .setProviderName(ExchangeRateType.ECB_HIST90.name())
                .setTermCurrency("USD")
                .set(LocalDate.now())
                .build());
*/


        CurrencyConversion conv1 = provider.getCurrencyConversion(ConversionQueryBuilder.of()
                .setTermCurrency("USD")
                .set(LocalDate.of(2008, 1, 2))
                .build());

        assertEquals(inEUR.with(conv1), inEUR.with(conv1));
        assertEquals(inEUR.with(conv2), inEUR.with(conv2));
        assertNotEquals(inEUR.with(conv1), inEUR.with(conv2)); // <- failing step
    }


    /**
     * This method checks or verifies whether passed LocalDate is weekend or not considering
     *
     * - SATURDAY is 6
     * - SUNDAY is 7
     *
     * @param localDateTime
     * @return
     */
    private static boolean isWeekend(LocalDateTime localDateTime) {

        // get Day of week for the passed LocalDate
        return (localDateTime.get(ChronoField.DAY_OF_WEEK) == 6)
                || (localDateTime.get(ChronoField.DAY_OF_WEEK) == 7);
    }
}
