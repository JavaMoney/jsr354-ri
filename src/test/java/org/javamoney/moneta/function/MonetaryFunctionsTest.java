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
package org.javamoney.moneta.function;

import static org.junit.Assert.assertEquals;
import java.math.BigDecimal;
import java.math.MathContext;
import java.util.Locale;

import javax.money.MonetaryOperator;

import org.javamoney.moneta.Money;
import org.junit.Test;

/**
 * @author Anatole
 * 
 */
public class MonetaryFunctionsTest {

	/**
	 * Test method for
	 * {@link org.javamoney.moneta.function.MonetaryFunctions#reciprocal()}.
	 */
	@Test
	public void testReciprocal() {
		Money m = Money.of("CHF", 200);
		Money r = m.with(MonetaryFunctions.reciprocal());
		assertEquals(
				Money.of("CHF", BigDecimal.ONE.divide(BigDecimal.valueOf(200))),
				r);
	}

	/**
	 * Test method for
	 * {@link org.javamoney.moneta.function.MonetaryFunctions#permil(java.math.BigDecimal)}
	 * .
	 */
	@Test
	public void testPermilBigDecimal() {
		Money m = Money.of("CHF", 100);
		Money r = m.with(MonetaryFunctions.permil(BigDecimal.valueOf(25)));
		assertEquals(
				Money.of("CHF", new BigDecimal("2.5")),
				r);
	}

	/**
	 * Test method for
	 * {@link org.javamoney.moneta.function.MonetaryFunctions#permil(java.lang.Number)}
	 * .
	 */
	@Test
	public void testPermilNumber() {
		Money m = Money.of("CHF", 100);
		Money r = m.with(MonetaryFunctions.permil(25));
		assertEquals(
				Money.of("CHF", new BigDecimal("2.5")),
				r);
	}

	/**
	 * Test method for
	 * {@link org.javamoney.moneta.function.MonetaryFunctions#permil(java.lang.Number, java.math.MathContext)}
	 * .
	 */
	@Test
	public void testPermilNumberMathContext() {
		Money m = Money.of("CHF", 100);
		Money r = m.with(MonetaryFunctions.permil(25, MathContext.DECIMAL64));
		assertEquals(
				Money.of("CHF", new BigDecimal("2.5")),
				r);
	}

	/**
	 * Test method for
	 * {@link org.javamoney.moneta.function.MonetaryFunctions#percent(java.math.BigDecimal)}
	 * .
	 */
	@Test
	public void testPercentBigDecimal() {
		Money m = Money.of("CHF", 100L);
		Money r = m.with(MonetaryFunctions.percent(BigDecimal.valueOf(25)));
		assertEquals(
				Money.of("CHF", new BigDecimal("25")),
				r);
	}

	/**
	 * Test method for
	 * {@link org.javamoney.moneta.function.MonetaryFunctions#percent(java.lang.Number)}
	 * .
	 */
	@Test
	public void testPercentNumber() {
		Money m = Money.of("CHF", 100);
		Money r = m.with(MonetaryFunctions.percent((long)25));
		assertEquals(
				Money.of("CHF", new BigDecimal("25")),
				r);
	}

	/**
	 * Test method for
	 * {@link org.javamoney.moneta.function.MonetaryFunctions#percent(java.lang.Number)}
	 * .
	 */
	@Test
	public void testPercentToString() {
		MonetaryOperator p = MonetaryFunctions.percent((short)25);
		assertEquals("25%", p.toString());
	}

	/**
	 * Test method for
	 * {@link org.javamoney.moneta.function.MonetaryFunctions#percent(java.lang.Number)}
	 * .
	 */
	@Test
	public void testPercentGetDisplayName() {
		MonetaryOperator p = MonetaryFunctions.percent((byte)25);
		assertEquals("25%", ((Percent) p).getDisplayName(Locale.ENGLISH));
	}

	/**
	 * Test method for
	 * {@link org.javamoney.moneta.function.MonetaryFunctions#minorPart()}.
	 */
	@Test
	public void testMinorPart() {
		Money m = Money.of("CHF", new BigDecimal("1234.56789"));
		Money r = m.with(MonetaryFunctions.minorPart());
		assertEquals(
				Money.of("CHF", new BigDecimal("0.56789")),
				r);
	}

	/**
	 * Test method for
	 * {@link org.javamoney.moneta.function.MonetaryFunctions#majorPart()}.
	 */
	@Test
	public void testMajorPart() {
		Money m = Money.of("CHF", new BigDecimal("1234.56789"));
		Money r = m.with(MonetaryFunctions.majorPart());
		assertEquals(
				Money.of("CHF", new BigDecimal("1234")),
				r);
	}

	/**
	 * Test method for
	 * {@link org.javamoney.moneta.function.MonetaryFunctions#minorUnits()}.
	 */
	@Test
	public void testMinorUnits() {
		Money m = Money.of("CHF", new BigDecimal("1234.56789"));
		Long units = m.query(MonetaryFunctions.minorUnits());
		assertEquals(Long.valueOf(123456L), units);
	}

	/**
	 * Test method for
	 * {@link org.javamoney.moneta.function.MonetaryFunctions#majorUnits()}.
	 */
	@Test
	public void testMajorUnits() {
		Money m = Money.of("CHF", new BigDecimal("1234.56789"));
		Long units = m.query(MonetaryFunctions.majorUnits());
		assertEquals(Long.valueOf(1234L), units);
	}

	// Bad cases

	/**
	 * Test method for
	 * {@link org.javamoney.moneta.function.MonetaryFunctions#minorUnits()}.
	 */
	@Test(expected = NullPointerException.class)
	public void testMinorUnits_Null() {
		MonetaryFunctions.minorUnits().queryFrom(null);
	}

	/**
	 * Test method for
	 * {@link org.javamoney.moneta.function.MonetaryFunctions#majorUnits()}.
	 */
	@Test(expected = NullPointerException.class)
	public void testMajorUnits_Null() {
		MonetaryFunctions.majorUnits().queryFrom(null);
	}

	/**
	 * Test method for
	 * {@link org.javamoney.moneta.function.MonetaryFunctions#majorPart()}.
	 */
	@Test(expected = NullPointerException.class)
	public void testMajorPart_Null() {
		MonetaryFunctions.majorPart().apply(null);
	}

	/**
	 * Test method for
	 * {@link org.javamoney.moneta.function.MonetaryFunctions#majorPart()}.
	 */
	@Test(expected = NullPointerException.class)
	public void testMinorPart_Null() {
		MonetaryFunctions.minorPart().apply(null);
	}

	/**
	 * Test method for
	 * {@link org.javamoney.moneta.function.MonetaryFunctions#percent()}.
	 */
	@Test(expected = NullPointerException.class)
	public void testPercent_Null1() {
		MonetaryFunctions.percent(null);
	}

	/**
	 * Test method for
	 * {@link org.javamoney.moneta.function.MonetaryFunctions#percent()}.
	 */
	@Test(expected = NullPointerException.class)
	public void testPercent_Null2() {
		MonetaryFunctions.percent(1).apply(null);
	}

	/**
	 * Test method for
	 * {@link org.javamoney.moneta.function.MonetaryFunctions#permil()}.
	 */
	@Test(expected = NullPointerException.class)
	public void testPermil_Null1() {
		MonetaryFunctions.permil(null);
	}

	/**
	 * Test method for
	 * {@link org.javamoney.moneta.function.MonetaryFunctions#permil()}.
	 */
	@Test(expected = NullPointerException.class)
	public void testPermil_Null2() {
		MonetaryFunctions.permil(1).apply(null);
	}

	/**
	 * Test method for
	 * {@link org.javamoney.moneta.function.MonetaryFunctions#reciprocal()}.
	 */
	@Test(expected = NullPointerException.class)
	public void testReciprocal_Null() {
		MonetaryFunctions.reciprocal().apply(null);
	}
}
