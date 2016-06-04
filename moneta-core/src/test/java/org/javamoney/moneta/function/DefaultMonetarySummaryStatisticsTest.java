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
package org.javamoney.moneta.function;

import static org.javamoney.moneta.function.StreamFactory.BRAZILIAN_REAL;
import static org.javamoney.moneta.function.StreamFactory.DOLLAR;

import javax.money.CurrencyUnit;

import junit.framework.Assert;

import org.javamoney.moneta.Money;
import org.testng.annotations.Test;

public class DefaultMonetarySummaryStatisticsTest {

	@Test
	public void shouldBeEmpty() {
		DefaultMonetarySummaryStatistics summary = new DefaultMonetarySummaryStatistics(
				BRAZILIAN_REAL);
		Assert.assertEquals(0L, summary.getCount());
		Assert.assertEquals(0L, summary.getMin().getNumber().longValue());
		Assert.assertEquals(0L, summary.getMax().getNumber().longValue());
		Assert.assertEquals(0L, summary.getSum().getNumber().longValue());
		Assert.assertEquals(0L, summary.getAverage().getNumber().longValue());

	}

	@Test(expectedExceptions = NullPointerException.class)
	public void shouldErrorWhenIsNull() {
		DefaultMonetarySummaryStatistics summary = new DefaultMonetarySummaryStatistics(
				BRAZILIAN_REAL);
		summary.accept(null);
	}

	@Test
	public void shouldStayEmptyWhenIsDifferentCurrency() {
		DefaultMonetarySummaryStatistics summary = new DefaultMonetarySummaryStatistics(
				BRAZILIAN_REAL);
		summary.accept(Money.of(10, DOLLAR));
		Assert.assertEquals(0L, summary.getCount());
		Assert.assertEquals(0L, summary.getMin().getNumber().longValue());
		Assert.assertEquals(0L, summary.getMax().getNumber().longValue());
		Assert.assertEquals(0L, summary.getSum().getNumber().longValue());
		Assert.assertEquals(0L, summary.getAverage().getNumber().longValue());
	}

	@Test
	public void shouldBeSameValueWhenOneMonetaryIsAdded() {
		DefaultMonetarySummaryStatistics summary = new DefaultMonetarySummaryStatistics(
				BRAZILIAN_REAL);
		summary.accept(Money.of(10, BRAZILIAN_REAL));
		Assert.assertEquals(1L, summary.getCount());
		Assert.assertEquals(10L, summary.getMin().getNumber().longValue());
		Assert.assertEquals(10L, summary.getMax().getNumber().longValue());
		Assert.assertEquals(10L, summary.getSum().getNumber().longValue());
		Assert.assertEquals(10L, summary.getAverage().getNumber().longValue());
	}

	@Test
	public void addTest() {
		MonetarySummaryStatistics summary = createSummary(BRAZILIAN_REAL);
		Assert.assertEquals(3L, summary.getCount());
		Assert.assertEquals(10L, summary.getMin().getNumber().longValue());
		Assert.assertEquals(110L, summary.getMax().getNumber().longValue());
		Assert.assertEquals(210L, summary.getSum().getNumber().longValue());
		Assert.assertEquals(70L, summary.getAverage().getNumber().longValue());
	}

	@Test
	public void combineTest() {
		MonetarySummaryStatistics summaryA = createSummary(BRAZILIAN_REAL);
		MonetarySummaryStatistics summaryB = createSummary(BRAZILIAN_REAL);
		summaryA.combine(summaryB);
		Assert.assertEquals(6L, summaryA.getCount());
		Assert.assertEquals(10L, summaryA.getMin().getNumber().longValue());
		Assert.assertEquals(110L, summaryA.getMax().getNumber().longValue());
		Assert.assertEquals(420L, summaryA.getSum().getNumber().longValue());
		Assert.assertEquals(70L, summaryA.getAverage().getNumber().longValue());
	}

	@Test
	public void shouldIgnoreCombineWhenCurrencyInSummaryAreDifferent() {
		MonetarySummaryStatistics summaryA = createSummary(BRAZILIAN_REAL);
		MonetarySummaryStatistics summaryB = createSummary(DOLLAR);
		summaryA.combine(summaryB);
		Assert.assertEquals(3L, summaryA.getCount());
		Assert.assertEquals(10L, summaryA.getMin().getNumber().longValue());
		Assert.assertEquals(110L, summaryA.getMax().getNumber().longValue());
		Assert.assertEquals(210L, summaryA.getSum().getNumber().longValue());
		Assert.assertEquals(70L, summaryA.getAverage().getNumber().longValue());
	}

	private MonetarySummaryStatistics createSummary(CurrencyUnit currencyUnit) {
		MonetarySummaryStatistics summary = new DefaultMonetarySummaryStatistics(
				currencyUnit);
		summary.accept(Money.of(10, currencyUnit));
		summary.accept(Money.of(90, currencyUnit));
		summary.accept(Money.of(110, currencyUnit));
		return summary;
	}


}
