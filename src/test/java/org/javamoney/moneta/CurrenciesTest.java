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

import javax.money.CurrencyUnit;
import javax.money.MonetaryCurrencies;

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
	 * {@link net.java.javamoney.ri.CurrencyUnitImpl#of(java.util.Currency)} .
	 */
	@Test
	public void testGetInstanceCurrency() {
		Currency jdkCurrency = Currency.getInstance("CHF");
		CurrencyUnit cur = MonetaryCurrencies.getCurrency("CHF");
		assertNotNull(cur);
		assertEquals(jdkCurrency.getCurrencyCode(), cur.getCurrencyCode());
		assertEquals(jdkCurrency.getNumericCode(), cur.getNumericCode());
		assertEquals(jdkCurrency.getDefaultFractionDigits(),
				cur.getDefaultFractionDigits());
	}

	/**
	 * Test method for
	 * {@link net.java.javamoney.ri.CurrencyUnitImpl#of(java.lang.String)} .
	 */
	@Test
	public void testGetInstanceString() {
		CurrencyUnit cur = MonetaryCurrencies.getCurrency("CHF");
		assertNotNull(cur);
		Currency jdkCurrency = Currency.getInstance("CHF");
		assertEquals(jdkCurrency.getCurrencyCode(), cur.getCurrencyCode());
		assertEquals(jdkCurrency.getNumericCode(), cur.getNumericCode());
		assertEquals(jdkCurrency.getDefaultFractionDigits(),
				cur.getDefaultFractionDigits());
	}

	/**
	 * Test method for
	 * {@link net.java.javamoney.ri.CurrencyUnitImpl#of(java.lang.String, java.lang.String)}
	 * .
	 */
	@Test
	public void testGetInstanceStringString() {
		CurrencyUnit cur = MonetaryCurrencies.getCurrency("USD");
		CurrencyUnit cur2 = MonetaryCurrencies.getCurrency("USD");
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
	 * {@link net.java.javamoney.ri.CurrencyUnitImpl#getCurrencyCode()}.
	 */
	@Test
	public void testGetCurrencyCode() {
		CurrencyUnit cur = MonetaryCurrencies.getCurrency("USD");
		assertEquals("USD", cur.getCurrencyCode());
		cur = MonetaryCurrencies.getCurrency("EUR");
		assertEquals("EUR", cur.getCurrencyCode());
	}

	/**
	 * Test method for
	 * {@link net.java.javamoney.ri.CurrencyUnitImpl#getNumericCode()}.
	 */
	@Test
	public void testGetNumericCode() {
		CurrencyUnit cur = MonetaryCurrencies.getCurrency("USD");
		assertEquals(840, cur.getNumericCode());
		cur = MonetaryCurrencies.getCurrency("EUR");
		assertEquals(978, cur.getNumericCode());
	}

	/**
	 * Test method for
	 * {@link net.java.javamoney.ri.CurrencyUnitImpl#getDefaultFractionDigits()}
	 * .
	 */
	@Test
	public void testGetDefaultFractionDigits() {
		CurrencyUnit cur = MonetaryCurrencies.getCurrency("USD");
		assertEquals(2, cur.getDefaultFractionDigits());
		cur = MonetaryCurrencies.getCurrency("JPY");
		assertEquals(0, cur.getDefaultFractionDigits());
	}

	/**
	 * Test method for
	 * {@link net.java.javamoney.ri.CurrencyUnitImpl#compareTo(javax.money.CurrencyUnit)}
	 * .
	 */
	@Test
	public void testCompareTo() {
		CurrencyUnit cur1 = MonetaryCurrencies.getCurrency("USD");
		CurrencyUnit cur2 = MonetaryCurrencies.getCurrency("EUR");
		assertTrue(cur1 instanceof Comparable);
		assertTrue(cur2 instanceof Comparable);
		assertTrue(0 < ((Comparable<CurrencyUnit>) cur1).compareTo(cur2));
		assertTrue(0 > ((Comparable<CurrencyUnit>) cur2).compareTo(cur1));
		assertEquals(0, ((Comparable<CurrencyUnit>) cur1).compareTo(cur1));
		assertEquals(0, ((Comparable<CurrencyUnit>) cur2).compareTo(cur2));
		// Currencies.Builder builder = new Currencies.Builder();
		// builder.setCurrencyCode("TEST");
		// CurrencyUnit cur3 = builder.create();
		// assertTrue(cur3 instanceof Comparable);
		// assertTrue(0 < ((Comparable<CurrencyUnit>) cur3).compareTo(cur2));
		// assertTrue(0 > ((Comparable<CurrencyUnit>) cur3).compareTo(cur1));
		// assertTrue(0 < ((Comparable<CurrencyUnit>) cur1).compareTo(cur3));
		// assertTrue(0 > ((Comparable<CurrencyUnit>) cur2).compareTo(cur3));
		// assertEquals(0, ((Comparable<CurrencyUnit>) cur3).compareTo(cur3));
	}

	/**
	 * Test method for {@link net.java.javamoney.ri.CurrencyUnitImpl#toString()}
	 * .
	 */
	@Test
	public void testToString() {
		CurrencyUnit cur1 = MonetaryCurrencies.getCurrency("USD");
		String toString = cur1.toString();
		assertNotNull(toString);
		assertTrue(toString.contains("USD"), "Does not contain currency code.");
		// Currencies.Builder builder = new Currencies.Builder();
		// builder.setCurrencyCode("TEST");
		// CurrencyUnit cur3 = builder.create();
		// toString = cur3.toString();
		// assertTrue("Does not contain currency code.",
		// toString.contains("TEST"));
	}

}
