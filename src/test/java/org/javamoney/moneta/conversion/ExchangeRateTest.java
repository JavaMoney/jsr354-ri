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

import java.util.Arrays;

import javax.money.CurrencyUnit;
import javax.money.convert.ConversionContext;
import javax.money.convert.ExchangeRate;
import javax.money.convert.RateType;

import org.javamoney.moneta.TestCurrency;
import org.junit.Test;

public class ExchangeRateTest {

	@Test
	public void testExchangeRateExchangeRateTypeCurrencyUnitCurrencyUnitNumberString() {
		CurrencyUnit base = TestCurrency.of("CHF");
		CurrencyUnit term = TestCurrency.of("EUR");
		ExchangeRate rate = new ExchangeRate.Builder("myProvider",
				RateType.DEFERRED).setBase(base).setTerm(term).setFactor(1.5)
				.create();
		assertEquals(base, rate.getBase());
		assertEquals(term, rate.getTerm());
		assertTrue(1.5d == rate.getFactor().doubleValue());
		assertEquals(ConversionContext.of("myProvider", RateType.DEFERRED),
				rate.getConversionContext());
		assertEquals(Arrays.asList(new ExchangeRate[] { rate }),
				rate.getExchangeRateChain());
	}

	@Test
	public void testExchangeRateExchangeRateTypeCurrencyUnitCurrencyUnitNumberStringLongLong() {
		CurrencyUnit base = TestCurrency.of("CHF");
		CurrencyUnit term = TestCurrency.of("EUR");
		ExchangeRate rate = new ExchangeRate.Builder("test", RateType.DEFERRED)
				.setBase(base).setTerm(term).setFactor(1.5).create();
		assertEquals(base, rate.getBase());
		assertEquals(term, rate.getTerm());
		assertTrue(1.5d == rate.getFactor().doubleValue());
		assertEquals(ConversionContext.of("test", RateType.DEFERRED),
				rate.getConversionContext());
		assertEquals(Arrays.asList(new ExchangeRate[] { rate }),
				rate.getExchangeRateChain());
	}

	@Test
	public void testExchangeRateExchangeRateTypeCurrencyUnitCurrencyUnitNumberStringExchangeRateArray() {
		CurrencyUnit base = TestCurrency.of("CHF");
		CurrencyUnit baseTerm = TestCurrency.of("EUR");
		CurrencyUnit term = TestCurrency.of("USD");
		ExchangeRate rate1 = new ExchangeRate.Builder("test", RateType.DEFERRED)
				.setBase(base).setTerm(baseTerm).setFactor(0.8).create();
		ExchangeRate rate2 = new ExchangeRate.Builder("test", RateType.DEFERRED)
				.setBase(baseTerm).setTerm(term).setFactor(1.4).create();

		// derived rate
		ExchangeRate rate = new ExchangeRate.Builder("test", RateType.DEFERRED)
				.setBase(base).setTerm(term).setFactor(0.8 * 1.4)
				.setRateChain(rate1, rate2).create();

		assertEquals(base, rate.getBase());
		assertEquals(term, rate.getTerm());
		assertTrue(0.8d * 1.4d == rate.getFactor().doubleValue());
		assertEquals(ConversionContext.of("test", RateType.DEFERRED),
				rate.getConversionContext());
		assertEquals(Arrays.asList(new ExchangeRate[] { rate1, rate2 }),
				rate.getExchangeRateChain());
	}

	@Test
	public void testExchangeRateExchangeRateTypeCurrencyUnitCurrencyUnitNumberStringLongLongExchangeRateArray() {
		CurrencyUnit base = TestCurrency.of("CHF");
		CurrencyUnit baseTerm = TestCurrency.of("EUR");
		CurrencyUnit term = TestCurrency.of("USD");
		ExchangeRate rate1 = new ExchangeRate.Builder("test", RateType.DEFERRED)
				.setBase(base).setTerm(baseTerm).setFactor(0.8).create();
		ExchangeRate rate2 = new ExchangeRate.Builder("test", RateType.DEFERRED)
				.setBase(baseTerm).setTerm(term).setFactor(1.4).create();

		// derived rate
		ExchangeRate rate = new ExchangeRate.Builder("test", RateType.DEFERRED)
				.setBase(base).setTerm(term).setFactor(0.8 * 1.4)
				.setRateChain(rate1, rate2).create();
		assertEquals(base, rate.getBase());
		assertEquals(term, rate.getTerm());
		assertTrue(0.8d * 1.4d == rate.getFactor().doubleValue());
		assertEquals(ConversionContext.of("test", RateType.DEFERRED),
				rate.getConversionContext());
		assertEquals(Arrays.asList(new ExchangeRate[] { rate1, rate2 }),
				rate.getExchangeRateChain());
	}

	@Test
	public void testToString() {
		CurrencyUnit base = TestCurrency.of("CHF");
		CurrencyUnit baseTerm = TestCurrency.of("EUR");
		CurrencyUnit term = TestCurrency.of("USD");
		ExchangeRate rate1 = new ExchangeRate.Builder("test", RateType.DEFERRED)
				.setBase(base).setTerm(baseTerm).setFactor(0.8).create();
		ExchangeRate rate2 = new ExchangeRate.Builder("test", RateType.DEFERRED)
				.setBase(baseTerm).setTerm(term).setFactor(1.4).create();

		// derived rate
		ExchangeRate rate = new ExchangeRate.Builder("test", RateType.DEFERRED)
				.setBase(base).setTerm(term).setFactor(0.8 * 1.4)
				.setRateChain(rate1, rate2).create();
		String toString = rate.toString();
		assertTrue(toString.contains("ExchangeRate ["));
		assertTrue(toString.contains("base=CHF, factor=1.1199999999999999, conversionContext="));
		assertTrue(toString.contains("RATE_TYPE=DEFERRED"));
		assertTrue(toString.contains("PROVIDER=test"));
	}

}
