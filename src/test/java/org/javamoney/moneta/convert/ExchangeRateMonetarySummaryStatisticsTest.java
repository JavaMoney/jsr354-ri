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
package org.javamoney.moneta.convert;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotSame;
import static org.javamoney.moneta.convert.ConversionConstants.BRAZILIAN_REAL;
import static org.javamoney.moneta.convert.ConversionConstants.DOLLAR;
import static org.junit.Assert.assertNotNull;

import javax.money.CurrencyUnit;
import javax.money.convert.ExchangeRateProvider;

import org.javamoney.moneta.Money;
import org.javamoney.moneta.convert.ExchangeRateMonetarySummaryStatistics;
import org.javamoney.moneta.function.DefaultMonetarySummaryStatistics;
import org.javamoney.moneta.function.MonetarySummaryStatistics;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

public class ExchangeRateMonetarySummaryStatisticsTest {

	private ExchangeRateProvider provider;

	@BeforeTest
	public void init() {
		provider = new ExchangeRateProviderMock();
	}

	@Test
	public void shouldConvertWhenIsDifferentCurrency() {


		MonetarySummaryStatistics summary = new ExchangeRateMonetarySummaryStatistics(
				BRAZILIAN_REAL, provider);
		summary.accept(Money.of(10, DOLLAR));

		assertEquals(BRAZILIAN_REAL, summary.getCurrencyUnit());
		assertEquals(1L, summary.getCount());
		assertNotSame(0L, summary.getMin().getNumber().longValue());
		assertNotSame(0L, summary.getMax().getNumber().longValue());
		assertNotSame(0L, summary.getSum().getNumber().longValue());
		assertNotSame(0L, summary.getAverage().getNumber().longValue());
	}

	@Test
	public void toTest() {
		MonetarySummaryStatistics summary = createSummary(BRAZILIAN_REAL);
		MonetarySummaryStatistics summaryDollar = summary.to(DOLLAR);

		assertEquals(DOLLAR, summaryDollar.getCurrencyUnit());
		assertEquals(3L, summaryDollar.getCount());
		assertNotSame(0L, summaryDollar.getMin().getNumber().longValue());
		assertNotSame(0L, summaryDollar.getMax().getNumber().longValue());
		assertNotSame(0L, summaryDollar.getSum().getNumber().longValue());
		assertNotSame(0L, summaryDollar.getAverage().getNumber().longValue());
	}

	@Test
	public void combineTest() {
		MonetarySummaryStatistics summaryA = createSummary(BRAZILIAN_REAL);
		MonetarySummaryStatistics summaryB = createSummary(DOLLAR);
		MonetarySummaryStatistics result = summaryA.combine(summaryB);

		assertEquals(BRAZILIAN_REAL, result.getCurrencyUnit());
		assertEquals(6L, result.getCount());
		assertNotNull(result.getMin());
		assertNotSame(110L, result.getMax().getNumber().longValue());
		assertNotSame(210L, result.getSum().getNumber().longValue());
		assertNotSame(70L, result.getAverage().getNumber().longValue());
	}

	@Test
	public void combineImplSummaryTest() {
		MonetarySummaryStatistics summaryA = createSummary(BRAZILIAN_REAL);
		MonetarySummaryStatistics summaryB = createSummaryDefault(DOLLAR);
		MonetarySummaryStatistics result = summaryA.combine(summaryB);

		assertEquals(BRAZILIAN_REAL, result.getCurrencyUnit());
		assertEquals(6L, result.getCount());
		assertNotNull(result.getMin());
		assertNotSame(110L, result.getMax().getNumber().longValue());
		assertNotSame(210L, result.getSum().getNumber().longValue());
		assertNotSame(70L, result.getAverage().getNumber().longValue());
	}

	private MonetarySummaryStatistics createSummary(CurrencyUnit currencyUnit) {
		MonetarySummaryStatistics summary = new ExchangeRateMonetarySummaryStatistics(
				currencyUnit, provider);
		summary.accept(Money.of(10, currencyUnit));
		summary.accept(Money.of(90, currencyUnit));
		summary.accept(Money.of(110, currencyUnit));
		return summary;
	}

	private MonetarySummaryStatistics createSummaryDefault(
			CurrencyUnit currencyUnit) {
		MonetarySummaryStatistics summary = DefaultMonetarySummaryStatistics.of(
				currencyUnit);
		summary.accept(Money.of(10, currencyUnit));
		summary.accept(Money.of(90, currencyUnit));
		summary.accept(Money.of(110, currencyUnit));
		return summary;
	}
}
