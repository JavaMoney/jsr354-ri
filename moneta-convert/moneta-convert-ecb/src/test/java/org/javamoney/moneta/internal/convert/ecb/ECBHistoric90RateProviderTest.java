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
package org.javamoney.moneta.internal.convert.ecb;

import static javax.money.convert.MonetaryConversions.getExchangeRateProvider;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertTrue;
import static org.testng.Assert.fail;

import java.math.BigDecimal;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.Month;
import java.time.YearMonth;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import javax.money.CurrencyUnit;
import javax.money.Monetary;
import javax.money.MonetaryAmount;
import javax.money.MonetaryException;
import javax.money.convert.ConversionQuery;
import javax.money.convert.ConversionQueryBuilder;
import javax.money.convert.CurrencyConversion;
import javax.money.convert.ExchangeRateProvider;

import org.javamoney.moneta.Money;
import org.javamoney.moneta.convert.ExchangeRateType;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

public class ECBHistoric90RateProviderTest {

    private static final CurrencyUnit EURO = Monetary
            .getCurrency("EUR");
    private static final CurrencyUnit DOLLAR = Monetary
            .getCurrency("USD");

    private static final CurrencyUnit BRAZILIAN_REAL = Monetary
            .getCurrency("BRL");

    private ExchangeRateProvider provider;

    @BeforeTest
    public void setup() throws InterruptedException {
        provider = getExchangeRateProvider(ExchangeRateType.ECB_HIST90);
        Thread.sleep(10_000L);

    }

    @Test
    public void shouldReturnsECBHistoric90RateProvider() {
        assertTrue(Objects.nonNull(provider));
        assertEquals(provider.getClass(), ECBHistoric90RateProvider.class);
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
    public void shouldConvertsDollarToEuro() {
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
	public void shouldSetTimeInLocalDateTime2() {

		LocalDate localDate = LocalDate.now().minusDays(90)
				.with(TemporalAdjusters.next(DayOfWeek.FRIDAY));

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

	@Test(expectedExceptions = MonetaryException.class)
	public void shouldReturnErrorWhenDoesNotFindTheExchangeRate() {

		LocalDate localDate = YearMonth.of(2011, Month.JANUARY).atDay(9);
		ConversionQuery conversionQuery = ConversionQueryBuilder.of()
				.set(localDate).setTermCurrency(EURO).build();
		CurrencyConversion currencyConversion = provider
				.getCurrencyConversion(conversionQuery);
		assertNotNull(currencyConversion);
		MonetaryAmount money = Money.of(BigDecimal.TEN, DOLLAR);
		currencyConversion.apply(money);
		fail();
	}

	@Test(expectedExceptions = MonetaryException.class)
	public void shouldReturnErrorWhenFindFromHistoricalUsingPeriod() {

		LocalDate localDate = YearMonth.of(2011, Month.JANUARY).atDay(9);

		ConversionQuery conversionQuery = ConversionQueryBuilder.of()
				.setTermCurrency(EURO)
				.set(withDaysBetween(localDate.minusDays(1), localDate))
				.build();
		CurrencyConversion currencyConversion = provider
				.getCurrencyConversion(conversionQuery);
		assertNotNull(currencyConversion);
		MonetaryAmount money = Money.of(BigDecimal.TEN, DOLLAR);
		currencyConversion.apply(money);
		fail();

	}

	@Test(expectedExceptions = MonetaryException.class)
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

	public final LocalDate[] withDaysBetween(LocalDate begin, LocalDate end) {

		int days = (int) ChronoUnit.DAYS.between(begin, end);

		List<LocalDate> dates = new ArrayList<>();
		for (int index = days; index >= 0; index--) {
			dates.add(begin.plusDays(index));
		}

		return dates.toArray(new LocalDate[dates.size()]);

	}
}