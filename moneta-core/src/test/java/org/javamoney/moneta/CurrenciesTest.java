/**
 * Copyright (c) 2012, 2014, Credit Suisse (Anatole Tresch), Werner Keil and others by the @author tag.
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
package org.javamoney.moneta;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertTrue;

import java.util.Currency;
import java.util.Locale;
import java.util.Set;

import javax.money.CurrencyUnit;
import javax.money.Monetary;

import org.testng.annotations.Test;

/**
 * Tests for {@link CurrencyUnit} implementation classes.
 * 
 * @author Anatole Tresch
 * @author Werner Keil
 */
public class CurrenciesTest {

	/**
	 * Test method for
	 * {@link javax.money.Monetary#getCurrency(java.lang.String, String...)} .
	 */
	@Test
	public void testCurrencyValues() {
		Currency jdkCurrency = Currency.getInstance("CHF");
		CurrencyUnit cur = Monetary.getCurrency("CHF");
		assertNotNull(cur);
		assertEquals(jdkCurrency.getCurrencyCode(), cur.getCurrencyCode());
		assertEquals(jdkCurrency.getNumericCode(), cur.getNumericCode());
		assertEquals(jdkCurrency.getDefaultFractionDigits(),
				cur.getDefaultFractionDigits());
	}

	/**
	 * Test method for
	 * {@link javax.money.Monetary#getCurrency(java.lang.String, String...)} .
	 */
	@Test
	public void testGetCurrencyString() {
		CurrencyUnit cur = Monetary.getCurrency("CHF");
		assertNotNull(cur);
		Currency jdkCurrency = Currency.getInstance("CHF");
		assertEquals(jdkCurrency.getCurrencyCode(), cur.getCurrencyCode());
		assertEquals(jdkCurrency.getNumericCode(), cur.getNumericCode());
		assertEquals(jdkCurrency.getDefaultFractionDigits(),
				cur.getDefaultFractionDigits());
	}

    /**
     * Test method for
     * {@link javax.money.Monetary#getCurrencies(java.util.Locale, String...)}.
     */
    @Test
    public void testGetCurrencyLocale() {
        Set<CurrencyUnit> cur = Monetary.getCurrencies(Locale.US);
        assertNotNull(cur);
        assertTrue(cur.size()==1);
        Currency jdkCurrency = Currency.getInstance(Locale.US);
        CurrencyUnit unit = cur.iterator().next();
        assertEquals(jdkCurrency.getCurrencyCode(), unit.getCurrencyCode());
        assertEquals(jdkCurrency.getNumericCode(), unit.getNumericCode());
        assertEquals(jdkCurrency.getDefaultFractionDigits(),
                     unit.getDefaultFractionDigits());
    }

	/**
	 * Test method for
	 * {@link javax.money.Monetary#getCurrency(java.lang.String, String...)}.
	 * .
	 */
	@Test
	public void testGetMultipleInstancesString() {
		CurrencyUnit cur = Monetary.getCurrency("USD");
		CurrencyUnit cur2 = Monetary.getCurrency("USD");
		assertNotNull(cur2);
		assertTrue(cur == cur2);
		Currency jdkCurrency = Currency.getInstance("USD");
		assertEquals(jdkCurrency.getCurrencyCode(), cur.getCurrencyCode());
		assertEquals(jdkCurrency.getNumericCode(), cur.getNumericCode());
		assertEquals(jdkCurrency.getDefaultFractionDigits(),
				cur.getDefaultFractionDigits());
	}

	/**
	 * Test method for
	 * {@link javax.money.Monetary#getCurrency(java.lang.String, String...)}.
	 */
	@Test
	public void testGetDifferentCurrencyCodes() {
		CurrencyUnit cur = Monetary.getCurrency("USD");
		assertEquals("USD", cur.getCurrencyCode());
		cur = Monetary.getCurrency("EUR");
		assertEquals("EUR", cur.getCurrencyCode());
	}

	/**
	 * Test Comparable method for
	 * {@link javax.money.CurrencyUnit}s.
	 */
	@Test
	public void testCompareTo() {
		CurrencyUnit cur1 = Monetary.getCurrency("USD");
		CurrencyUnit cur2 = Monetary.getCurrency("EUR");
		assertNotNull(cur1);
        assertNotNull(cur2);
		assertTrue(0 < cur1.compareTo(cur2));
		assertTrue(0 > cur2.compareTo(cur1));
		assertEquals(0, cur1.compareTo(cur1));
		assertEquals(0, cur2.compareTo(cur2));
	}

	/**
	 * Test method for {@link CurrencyUnit#toString()}
	 * .
	 */
	@Test
	public void testToString() {
		CurrencyUnit cur1 = Monetary.getCurrency("USD");
		String toString = cur1.toString();
		assertNotNull(toString);
		assertTrue(toString.contains("USD"), "Does not contain currency code.");
	}

}
