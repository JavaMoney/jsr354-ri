/*
 * Copyright (c) 2012, 2013, Credit Suisse (Anatole Tresch), Werner Keil.
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
package org.javamoney.moneta.conversion;

import static org.junit.Assert.assertEquals;

import javax.money.CurrencyUnit;
import javax.money.convert.ConversionContext;
import javax.money.convert.CurrencyConversionException;

import org.javamoney.moneta.TestCurrency;
import org.junit.Test;

public class CurrencyConversionExceptionTest {

	private static ConversionContext CONTEXT100 = new ConversionContext.Builder()
			.setProvider("test").setTimestamp(100L).create();

	@Test
	public void testCurrencyConversionExceptionCurrencyUnitCurrencyUnitContext() {
		CurrencyUnit base = TestCurrency.of("CHF");
		CurrencyUnit term = TestCurrency.of("EUR");
		CurrencyConversionException ex = new CurrencyConversionException(base,
				term, CONTEXT100);
		assertEquals(null, ex.getCause());
		assertEquals(base, ex.getBase());
		assertEquals(term, ex.getTerm());
		assertEquals(CONTEXT100, ex.getConversionContext());
		assertEquals("Cannot convert CHF into EUR", ex.getMessage());
	}

	@Test
	public void testCurrencyConversionExceptionCurrencyUnitCurrencyUnitLongStringContextStringThrowable() {
		CurrencyUnit base = TestCurrency.of("CHF");
		CurrencyUnit term = TestCurrency.of("EUR");
		Exception cause = new Exception("cause");
		CurrencyConversionException ex = new CurrencyConversionException(base,
				term, CONTEXT100, "blabla", cause);
		assertEquals(cause, ex.getCause());
		assertEquals(base, ex.getBase());
		assertEquals(term, ex.getTerm());
		assertEquals(CONTEXT100, ex.getConversionContext());
		assertEquals("Cannot convert CHF into EUR: blabla", ex.getMessage());
	}

	@Test
	public void testToString() {
		CurrencyUnit base = TestCurrency.of("CHF");
		CurrencyUnit term = TestCurrency.of("EUR");
		Exception cause = new Exception("cause");
		CurrencyConversionException ex = new CurrencyConversionException(base,
				term, CONTEXT100, "blabla", cause);
		assertEquals(
				"CurrencyConversionException [base=CHF, term=EUR, conversionContext=ConversionContext [attributes={class java.lang.String={PROVIDER=test}, class java.lang.Long={TIMESTAMP=100}}]]: Cannot convert CHF into EUR: blabla",
				ex.toString());
	}

}
