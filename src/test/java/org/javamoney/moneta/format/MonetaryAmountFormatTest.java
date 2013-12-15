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
package org.javamoney.moneta.format;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.io.IOException;
import java.math.BigDecimal;
import java.text.ParseException;
import java.util.Locale;

import javax.money.MonetaryAmounts;
import javax.money.MonetaryCurrencies;
import javax.money.format.MonetaryAmountFormat;
import javax.money.format.MonetaryFormats;

import org.junit.Test;

/**
 * @author Anatole
 * 
 */
public class MonetaryAmountFormatTest {

	/**
	 * Test method for
	 * {@link javax.money.format.MonetaryAmountFormat#getAmountStyle()} .
	 */
	@Test
	public void testGetAmountStyle() {

	}

	/**
	 * Test method for
	 * {@link javax.money.format.MonetaryAmountFormat#getDefaultCurrency()} .
	 */
	@Test
	public void testGetDefaultCurrency() {
		MonetaryAmountFormat defaultFormat = MonetaryFormats.getAmountFormat(
				Locale.GERMANY);
		assertNull(defaultFormat.getDefaultCurrency());
		defaultFormat = MonetaryFormats.getAmountFormat(
				Locale.GERMANY, MonetaryCurrencies.getCurrency("CHF"));
		assertEquals(MonetaryCurrencies.getCurrency("CHF"),
				defaultFormat.getDefaultCurrency());
	}

	/**
	 * Test method for
	 * {@link javax.money.format.MonetaryAmountFormat#format(javax.money.MonetaryAmount)}
	 * .
	 */
	@Test
	public void testFormat() {
		MonetaryAmountFormat defaultFormat = MonetaryFormats.getAmountFormat(
				Locale.GERMANY);
		assertEquals(
				"CHF 12,50"
				,
				defaultFormat.format(MonetaryAmounts.getDefaultAmountFactory().getAmount(
						"CHF", 12.50)));
		assertEquals(
				"INR 123.456.789.101.112,12",
				defaultFormat.format(MonetaryAmounts.getDefaultAmountFactory().getAmount(
						"INR",
						123456789101112.123456)));
		defaultFormat = MonetaryFormats.getAmountFormat(new Locale("", "IN"));
		assertEquals(
				"CHF 1,211,112.50",
				defaultFormat.format(MonetaryAmounts.getDefaultAmountFactory().getAmount(
						"CHF",
						1211112.50)));
		assertEquals(
				"INR 123,456,789,101,112.12",
				defaultFormat.format(MonetaryAmounts.getDefaultAmountFactory().getAmount(
						"INR",
						123456789101112.123456)));
		// Locale india = new Locale("", "IN");
		// defaultFormat = MonetaryFormats.getAmountFormatBuilder(india)
		// .setNumberGroupSizes(3, 2).build();
		// assertEquals("INR 12,34,56,78,91,01,112.12",
		// defaultFormat.format(MonetaryAmounts.getAmount("INR",
		// 123456789101112.123456)));
	}

	/**
	 * Test method for
	 * {@link javax.money.format.MonetaryAmountFormat#print(java.lang.Appendable, javax.money.MonetaryAmount)}
	 * .
	 * 
	 * @throws IOException
	 */
	@Test
	public void testPrint() throws IOException {
		StringBuilder b = new StringBuilder();
		MonetaryAmountFormat defaultFormat = MonetaryFormats.getAmountFormat(
				Locale.GERMANY);
		defaultFormat.print(b,
				MonetaryAmounts.getDefaultAmountFactory().getAmount("CHF", 12.50));
		assertEquals("CHF 12,50", b.toString());
		b.setLength(0);
		defaultFormat.print(b, MonetaryAmounts.getDefaultAmountFactory().getAmount("INR",
				123456789101112.123456));
		assertEquals("INR 123.456.789.101.112,12",
				b.toString());
		b.setLength(0);
		defaultFormat = MonetaryFormats.getAmountFormat(new Locale("", "IN"));
		defaultFormat.print(b,
				MonetaryAmounts.getDefaultAmountFactory().getAmount("CHF", 1211112.50));
		assertEquals("CHF 1,211,112.50",
				b.toString());
		b.setLength(0);
		defaultFormat.print(b, MonetaryAmounts.getDefaultAmountFactory().getAmount("INR",
				123456789101112.123456));
		assertEquals("INR 123,456,789,101,112.12",
				b.toString());
		b.setLength(0);
		// Locale india = new Locale("", "IN");
		// defaultFormat = MonetaryFormats.getAmountFormat(india)
		// .setNumberGroupSizes(3, 2).build();
		// defaultFormat.print(b, MonetaryAmounts.getAmount("INR",
		// 123456789101112.123456));
		// assertEquals("INR 12,34,56,78,91,01,112.12",
		// b.toString());
	}

	/**
	 * Test method for
	 * {@link javax.money.format.MonetaryAmountFormat#parse(java.lang.CharSequence)}
	 * .
	 * 
	 * @throws ParseException
	 */
	@Test
	public void testParse() throws ParseException {
		MonetaryAmountFormat defaultFormat = MonetaryFormats.getAmountFormat(
				Locale.GERMANY);
		assertEquals(
				MonetaryAmounts.getDefaultAmountFactory().getAmount("EUR",
						new BigDecimal("12.50")),
				defaultFormat.parse("EUR 12,50"));
		assertEquals(
				MonetaryAmounts.getDefaultAmountFactory().getAmount("EUR",
						new BigDecimal("12.50")),
				defaultFormat.parse("  \t EUR 12,50"));
		assertEquals(
				MonetaryAmounts.getDefaultAmountFactory().getAmount("EUR",
						new BigDecimal("12.50")),
				defaultFormat.parse("  \t EUR  \t\n\r 12,50"));
		assertEquals(
				MonetaryAmounts.getDefaultAmountFactory().getAmount("CHF",
						new BigDecimal("12.50")),
				defaultFormat.parse("CHF 12,50"));
		// defaultFormat = MonetaryFormats
		// .getAmountFormatBuilder(new Locale("", "IN"))
		// .setNumberGroupSizes(3, 2).build();
		// assertEquals(MonetaryAmounts.getAmount("INR", new BigDecimal(
		// "123456789101112.12")),
		// defaultFormat.parse("INR 12,34,56,78,91,01,112.12"));
	}
}
