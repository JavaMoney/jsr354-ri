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
 * 
 * Contributors: Anatole Tresch - initial implementation Wernner Keil -
 * extensions and adaptions.
 */
package org.javamoney.moneta;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.Currency;

import javax.money.CurrencyUnit;

import org.junit.Test;

/**
 * Tests for the {@link CurrencyUnitImpl} class.
 * 
 * @author Anatole Tresch
 */
public class MoneyCurrencyTest {

	/**
	 * Test method for
	 * {@link net.java.javamoney.ri.CurrencyUnitImpl#of(java.util.Currency)} .
	 */
	@Test
	public void testGetInstanceCurrency() {
		Currency jdkCurrency = Currency.getInstance("CHF");
		MoneyCurrency cur = MoneyCurrency.of(jdkCurrency);
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
		MoneyCurrency cur = MoneyCurrency.of("CHF");
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
		MoneyCurrency cur = MoneyCurrency.of("USD");
		MoneyCurrency cur2 = MoneyCurrency
				.of("USD");
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
		CurrencyUnit cur = MoneyCurrency.of("USD");
		assertEquals("USD", cur.getCurrencyCode());
		cur = MoneyCurrency.of("EUR");
		assertEquals("EUR", cur.getCurrencyCode());
	}

	/**
	 * Test method for
	 * {@link net.java.javamoney.ri.CurrencyUnitImpl#getNumericCode()}.
	 */
	@Test
	public void testGetNumericCode() {
		MoneyCurrency cur = MoneyCurrency.of("USD");
		assertEquals(840, cur.getNumericCode());
		cur = MoneyCurrency.of("EUR");
		assertEquals(978, cur.getNumericCode());
	}

	/**
	 * Test method for
	 * {@link net.java.javamoney.ri.CurrencyUnitImpl#getDefaultFractionDigits()}
	 * .
	 */
	@Test
	public void testGetDefaultFractionDigits() {
		MoneyCurrency cur = MoneyCurrency.of("USD");
		assertEquals(2, cur.getDefaultFractionDigits());
		cur = MoneyCurrency.of("JPY");
		assertEquals(0, cur.getDefaultFractionDigits());
	}

	/**
	 * Test method for
	 * {@link net.java.javamoney.ri.CurrencyUnitImpl#compareTo(javax.money.CurrencyUnit)}
	 * .
	 */
	@SuppressWarnings("unchecked")
	@Test
	public void testCompareTo() {
		MoneyCurrency cur1 = MoneyCurrency.of("USD");
		MoneyCurrency cur2 = MoneyCurrency.of("EUR");
		assertTrue(cur1 instanceof Comparable);
		assertTrue(cur2 instanceof Comparable);
		assertTrue(0 < ((Comparable<CurrencyUnit>) cur1).compareTo(cur2));
		assertTrue(0 > ((Comparable<CurrencyUnit>) cur2).compareTo(cur1));
		assertEquals(0, ((Comparable<CurrencyUnit>) cur1).compareTo(cur1));
		assertEquals(0, ((Comparable<CurrencyUnit>) cur2).compareTo(cur2));
		MoneyCurrency.Builder builder = new MoneyCurrency.Builder();
		builder.setCurrencyCode("TEST");
		MoneyCurrency cur3 = builder.build();
		assertTrue(cur3 instanceof Comparable);
		assertTrue(0 < ((Comparable<CurrencyUnit>) cur3).compareTo(cur2));
		assertTrue(0 > ((Comparable<CurrencyUnit>) cur3).compareTo(cur1));
		assertTrue(0 < ((Comparable<CurrencyUnit>) cur1).compareTo(cur3));
		assertTrue(0 > ((Comparable<CurrencyUnit>) cur2).compareTo(cur3));
		assertEquals(0, ((Comparable<CurrencyUnit>) cur3).compareTo(cur3));
	}

	/**
	 * Test method for {@link net.java.javamoney.ri.CurrencyUnitImpl#toString()}
	 * .
	 */
	@Test
	public void testToString() {
		MoneyCurrency cur1 = MoneyCurrency.of("USD");
		String toString = cur1.toString();
		assertNotNull(toString);
		assertTrue("Does not contain currency code.", toString.contains("USD"));
		MoneyCurrency.Builder builder = new MoneyCurrency.Builder();
		builder.setCurrencyCode("TEST");
		MoneyCurrency cur3 = builder.build();
		toString = cur3.toString();
		assertTrue("Does not contain currency code.", toString.contains("TEST"));
	}

}
