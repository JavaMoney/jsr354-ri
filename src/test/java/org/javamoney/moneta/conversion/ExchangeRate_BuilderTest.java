/*
 * Copyright (c) 2012, 2013, Credit Suisse (Anatole Tresch), Werner Keil. Licensed under the Apache
 * License, Version 2.0 (the "License"); you may not use this file except in compliance with the
 * License. You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */
package org.javamoney.moneta.conversion;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Arrays;

import javax.money.CurrencyUnit;
import javax.money.convert.ConversionContext;
import javax.money.convert.ExchangeRate;
import javax.money.convert.RateType;

import org.javamoney.moneta.TestCurrency;
import org.javamoney.moneta.spi.DefaultNumberValue;
import org.junit.Ignore;
import org.junit.Test;

public class ExchangeRate_BuilderTest {

	@Test
	public void testWithConversionContext() {
		ExchangeRate.Builder b = new ExchangeRate.Builder("test",  RateType.DEFERRED);
		ExchangeRate.Builder b2 = b.setContext(ConversionContext.of("test",  RateType.DEFERRED));
		assertTrue(b == b2);
		b2 = b.setContext(ConversionContext.of("test2",  RateType.DEFERRED));
		assertTrue(b == b2);
	}

	@Test
	public void testGetSetBase() {
		ExchangeRate.Builder b = new ExchangeRate.Builder("test",  RateType.DEFERRED);
		ExchangeRate.Builder b2 = b.setBase(TestCurrency.of("CHF"));
		assertTrue(b == b2);
	}

	@Test
	public void testGetSetTerm() {
		ExchangeRate.Builder b = new ExchangeRate.Builder("test",  RateType.DEFERRED);
		ExchangeRate.Builder b2 = b.setTerm(TestCurrency.of("CHF"));
		assertTrue(b == b2);
	}

	@Test
	public void testGetSetExchangeRateChain() {
		CurrencyUnit base = TestCurrency.of("CHF");
		CurrencyUnit baseTerm = TestCurrency.of("EUR");
		CurrencyUnit term = TestCurrency.of("USD");
		ExchangeRate rate1 = new ExchangeRate.Builder("test",  RateType.DEFERRED)
				.setBase(base)
				.setTerm(baseTerm).setFactor(DefaultNumberValue.of(0.8))
				.create();
		ExchangeRate rate2 = new ExchangeRate.Builder("test",  RateType.DEFERRED)
				.setBase(baseTerm)
				.setTerm(term).setFactor(DefaultNumberValue.of(1.4))
				.create();
		ExchangeRate.Builder b = new ExchangeRate.Builder("test",  RateType.DEFERRED).setBase(base)
				.setTerm(term)
				.setRateChain(rate1, rate2);
		ExchangeRate rate = b.setFactor(DefaultNumberValue.of(9))
				.setContext(ConversionContext.of("test",  RateType.DEFERRED))
				.create();
		assertEquals(rate.getFactor().numberValue(BigDecimal.class), BigDecimal.valueOf(9));
		assertEquals(rate.getExchangeRateChain(),
				Arrays.asList(new ExchangeRate[] { rate1, rate2 }));
	}

	@Test
	public void testGetSetBaseLeadingFactor() {
		ExchangeRate.Builder b = new ExchangeRate.Builder("test",  RateType.DEFERRED);
		ExchangeRate.Builder b2 = b.setFactor(DefaultNumberValue.of(Long.MAX_VALUE));
		assertTrue(b == b2);
		b.setFactor(DefaultNumberValue.of(100L));
		CurrencyUnit base = TestCurrency.of("CHF");
		CurrencyUnit term = TestCurrency.of("USD");
		ExchangeRate rate = b.setBase(base)
				.setContext(ConversionContext.of("test",  RateType.DEFERRED))
				.setTerm(term).create();
		assertEquals(BigDecimal.valueOf(100L), rate.getFactor().numberValue(BigDecimal.class));
	}

	@Test
	@Ignore
	public void testGetSetTermLeadingFactorBigDecimal() {
		ExchangeRate.Builder b = new ExchangeRate.Builder("test",  RateType.DEFERRED);
		ExchangeRate.Builder b2 = b.setFactor(DefaultNumberValue.of(1.2));
		assertTrue(b == b2);
		CurrencyUnit base = TestCurrency.of("CHF");
		CurrencyUnit term = TestCurrency.of("USD");
		ExchangeRate rate = b.setBase(base)
				.setContext(ConversionContext.of("test",  RateType.DEFERRED))
				.setTerm(term).create();
		assertEquals(BigDecimal.ONE.divide(BigDecimal.valueOf(1),
				RoundingMode.HALF_EVEN), rate.getFactor());
	}


	@Test
	public void testBuild() {
		CurrencyUnit base = TestCurrency.of("CHF");
		CurrencyUnit baseTerm = TestCurrency.of("EUR");
		CurrencyUnit term = TestCurrency.of("USD");
		ExchangeRate rate1 = new ExchangeRate.Builder("test",  RateType.DEFERRED)
				.setContext(ConversionContext.of("test",  RateType.DEFERRED))
				.setBase(base).setTerm(baseTerm)
				.setFactor(DefaultNumberValue.of(0.8)).create();
		ExchangeRate rate2 = new ExchangeRate.Builder("test",  RateType.DEFERRED)
				.setContext(ConversionContext.of("test",  RateType.DEFERRED))
				.setBase(baseTerm).setTerm(term)
				.setFactor(DefaultNumberValue.of(1.4)).create();

		ExchangeRate.Builder b = new ExchangeRate.Builder("test",  RateType.DEFERRED);
		b.setContext(ConversionContext.of("bla",  RateType.DEFERRED));
		b.setBase(base);
		b.setTerm(term);
		b.setFactor(DefaultNumberValue.of(2.2));
		ExchangeRate rate = b.create();
		assertEquals(rate.getConversionContext(), ConversionContext.of("bla",  RateType.DEFERRED));
		assertEquals(base, rate.getBase());
		assertEquals(term, rate.getTerm());
		assertEquals(BigDecimal.valueOf(2.2d), rate.getFactor().numberValue(BigDecimal.class));

		b = new ExchangeRate.Builder("test",  RateType.DEFERRED);
		b.setBase(TestCurrency.of("CHF"));
		b.setTerm(TestCurrency.of("USD"));
		b.setRateChain(rate1, rate2);
		b.setFactor(DefaultNumberValue.of(2.0));
		rate = b.create();
		assertEquals(rate.getConversionContext(), ConversionContext.of("test",  RateType.DEFERRED));
		assertEquals(TestCurrency.of("CHF"), rate.getBase());
		assertEquals(TestCurrency.of("USD"), rate.getTerm());
		assertEquals(BigDecimal.valueOf(2.0), rate.getFactor().numberValue(BigDecimal.class));
	}
}
