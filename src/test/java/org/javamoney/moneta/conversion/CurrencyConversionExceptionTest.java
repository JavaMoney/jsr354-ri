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

import static org.junit.Assert.*;

import javax.money.CurrencyUnit;
import javax.money.convert.CurrencyConversionException;

import org.javamoney.moneta.TestCurrency;
import org.junit.Test;

public class CurrencyConversionExceptionTest {

	@Test
	public void testCurrencyConversionExceptionCurrencyUnitCurrencyUnitLongString() {
		CurrencyUnit base = TestCurrency.of("CHF");
		CurrencyUnit term = TestCurrency.of("EUR");
		CurrencyConversionException ex = new CurrencyConversionException(base, term, 100L, "test");
		assertEquals(null, ex.getCause());
		assertEquals(base, ex.getBase());
		assertEquals(term, ex.getTerm());
		assertEquals(Long.valueOf(100), ex.getTimestamp());
		assertEquals("Cannot convert CHF into EUR: test", ex.getMessage());
	}

	@Test
	public void testCurrencyConversionExceptionCurrencyUnitCurrencyUnitLong() {
		CurrencyUnit base = TestCurrency.of("CHF");
		CurrencyUnit term = TestCurrency.of("EUR");
		CurrencyConversionException ex = new CurrencyConversionException(base, term, 100L);
		assertEquals(null, ex.getCause());
		assertEquals(base, ex.getBase());
		assertEquals(term, ex.getTerm());
		assertEquals(Long.valueOf(100), ex.getTimestamp());
		assertEquals("Cannot convert CHF into EUR", ex.getMessage());
	}
	

	@Test
	public void testCurrencyConversionExceptionCurrencyUnitCurrencyUnitLongStringThrowable() {
		CurrencyUnit base = TestCurrency.of("CHF");
		CurrencyUnit term = TestCurrency.of("EUR");
		Exception cause = new Exception("cause");
		CurrencyConversionException ex = new CurrencyConversionException(base, term, 100L, "test", cause);
		assertEquals(cause, ex.getCause());
		assertEquals(base, ex.getBase());
		assertEquals(term, ex.getTerm());
		assertEquals(Long.valueOf(100), ex.getTimestamp());
		assertEquals("Cannot convert CHF into EUR: test", ex.getMessage());
	}


	@Test
	public void testToString() {
		CurrencyUnit base = TestCurrency.of("CHF");
		CurrencyUnit term = TestCurrency.of("EUR");
		Exception cause = new Exception("cause");
		CurrencyConversionException ex = new CurrencyConversionException(base, term, 100L, "test", cause);
		assertEquals("CurrencyConversionException [base=CHF, term=EUR, timestamp=100]: Cannot convert CHF into EUR: test", ex.toString());
	}

}
