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
package org.javamoney.moneta.function;

import org.testng.annotations.Test;

import javax.money.MonetaryAmount;
import javax.money.MonetaryAmounts;
import javax.money.MonetaryOperator;
import java.math.BigDecimal;
import java.math.MathContext;
import java.util.Locale;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

/**
 * @author Anatole
 * @author Werner
 * 
 */
public class MonetaryUtilTest {

	/**
	 * Test method for
	 * {@link org.javamoney.moneta.function.MonetaryUtil#reciprocal()}.
	 */
	@Test
	public void testReciprocal() {
		MonetaryAmount m = MonetaryAmounts.getDefaultAmountFactory()
				.setCurrency("CHF").setNumber(200).create();
		MonetaryAmount r = m.with(MonetaryUtil.reciprocal());
		assertEquals(
				MonetaryAmounts.getDefaultAmountFactory().setCurrency("CHF")
						.setNumber(BigDecimal.ONE.divide(BigDecimal.valueOf(200)))
						.create(),
				r);
	}

	/**
	 * Test method for
	 * {@link org.javamoney.moneta.function.MonetaryUtil#permil(java.math.BigDecimal)}
	 * .
	 */
	@Test
	public void testPermilBigDecimal() {
		MonetaryAmount m = MonetaryAmounts.getDefaultAmountFactory()
				.setCurrency(
						"CHF").setNumber(100).create();
		MonetaryAmount r = m.with(MonetaryUtil.permil(BigDecimal
				.valueOf(25)));
		assertEquals(
				MonetaryAmounts.getDefaultAmountFactory().setCurrency("CHF")
						.setNumber(
								new BigDecimal("2.5")).create(),
				r);
	}

	/**
	 * Test method for
	 * {@link org.javamoney.moneta.function.MonetaryUtil#permil(java.lang.Number)}
	 * .
	 */
	@Test
	public void testPermilNumber() {
		MonetaryAmount m = MonetaryAmounts.getDefaultAmountFactory()
				.setCurrency(
						"CHF").setNumber(100).create();
		MonetaryAmount r = m.with(MonetaryUtil.permil(25));
		assertEquals(
				MonetaryAmounts.getDefaultAmountFactory().setCurrency("CHF")
						.setNumber(
								new BigDecimal("2.5")).create(),
				r);
	}

	/**
	 * Test method for
	 * {@link org.javamoney.moneta.function.MonetaryUtil#permil(java.lang.Number, java.math.MathContext)}
	 * .
	 */
	@Test
	public void testPermilNumberMathContext() {
		MonetaryAmount m = MonetaryAmounts.getDefaultAmountFactory()
				.setCurrency(
						"CHF").setNumber(100).create();
		MonetaryAmount r = m.with(MonetaryUtil.permil(25,
				MathContext.DECIMAL64));
		assertEquals(
				MonetaryAmounts.getDefaultAmountFactory().setCurrency("CHF")
						.setNumber(
								new BigDecimal("2.5")).create(),
				r);
	}

	/**
	 * Test method for
	 * {@link org.javamoney.moneta.function.MonetaryUtil#percent(java.math.BigDecimal)}
	 * .
	 */
	@Test
	public void testPercentBigDecimal() {
		MonetaryAmount m = MonetaryAmounts.getDefaultAmountFactory()
				.setCurrency(
						"CHF").setNumber(100L).create();
		MonetaryAmount r = m.with(MonetaryUtil.percent(BigDecimal
				.valueOf(25)));
		assertEquals(
				MonetaryAmounts.getDefaultAmountFactory().setCurrency("CHF")
						.setNumber(
								new BigDecimal("25")).create(),
				r);
	}

	/**
	 * Test method for
	 * {@link org.javamoney.moneta.function.MonetaryUtil#percent(java.lang.Number)}
	 * .
	 */
	@Test
	public void testPercentNumber() {
		MonetaryAmount m = MonetaryAmounts.getDefaultAmountFactory()
				.setCurrency(
						"CHF").setNumber(100).create();
		MonetaryAmount r = m.with(MonetaryUtil.percent((long) 25));
		assertEquals(
				MonetaryAmounts.getDefaultAmountFactory().setCurrency("CHF")
						.setNumber(
								new BigDecimal("25")).create(),
				r);
	}

	/**
	 * Test method for
	 * {@link org.javamoney.moneta.function.MonetaryUtil#percent(java.lang.Number)}
	 * .
	 */
	@Test
	public void testPercentToString() {
		MonetaryOperator p = MonetaryUtil.percent((short) 25);
		assertTrue(p.toString().contains("25"));
		assertTrue(p.toString().contains("%"));
	}

	/**
	 * Test method for
	 * {@link org.javamoney.moneta.function.MonetaryUtil#percent(java.lang.Number)}
	 * .
	 */
	@Test
	public void testPercentGetDisplayName() {
		MonetaryOperator p = MonetaryUtil.percent((byte) 25);
		assertEquals("25%", ((Percent) p).getDisplayName(Locale.ENGLISH));
	}

	/**
	 * Test method for
	 * {@link org.javamoney.moneta.function.MonetaryUtil#minorPart()}.
	 */
	@Test
	public void testMinorPart() {
		MonetaryAmount m = MonetaryAmounts.getDefaultAmountFactory()
				.setCurrency(
						"CHF").setNumber(new BigDecimal(
						"1234.56789")).create();
		MonetaryAmount r = m.with(MonetaryUtil.minorPart());
		assertEquals(
				MonetaryAmounts.getDefaultAmountFactory().setCurrency("CHF")
						.setNumber(
								new BigDecimal("0.56789")).create(),
				r);
	}

	/**
	 * Test method for
	 * {@link org.javamoney.moneta.function.MonetaryUtil#majorPart()}.
	 */
	@Test
	public void testMajorPart() {
		MonetaryAmount m = MonetaryAmounts.getDefaultAmountFactory()
				.setCurrency(
						"CHF").setNumber(new BigDecimal(
						"1234.56789")).create();
		MonetaryAmount r = m.with(MonetaryUtil.majorPart());
		assertEquals(
				MonetaryAmounts.getDefaultAmountFactory().setCurrency(
						"CHF").setNumber(new BigDecimal("1234")).create(),
				r);
	}

	/**
	 * Test method for
	 * {@link org.javamoney.moneta.function.MonetaryUtil#minorUnits()}.
	 */
	@Test
	public void testMinorUnits() {
		MonetaryAmount m = MonetaryAmounts.getDefaultAmountFactory()
				.setCurrency(
						"CHF").setNumber(new BigDecimal(
						"1234.56789")).create();
		Long units = m.query(MonetaryUtil.minorUnits());
		assertEquals(Long.valueOf(123456L), units);
	}

	/**
	 * Test method for
	 * {@link org.javamoney.moneta.function.MonetaryUtil#majorUnits()}.
	 */
	@Test
	public void testMajorUnits() {
		MonetaryAmount m = MonetaryAmounts.getDefaultAmountFactory()
				.setCurrency("CHF").setNumber(new BigDecimal(
						"1234.56789")).create();
		Long units = m.query(MonetaryUtil.majorUnits());
		assertEquals(Long.valueOf(1234L), units);
	}

	// Bad cases

	/**
	 * Test method for
	 * {@link org.javamoney.moneta.function.MonetaryUtil#minorUnits()}.
	 */
	@Test(expectedExceptions = NullPointerException.class)
	public void testMinorUnits_Null() {
		MonetaryUtil.minorUnits().queryFrom(null);
	}

	/**
	 * Test method for
	 * {@link org.javamoney.moneta.function.MonetaryUtil#majorUnits()}.
	 */
	@Test(expectedExceptions = NullPointerException.class)
	public void testMajorUnits_Null() {
		MonetaryUtil.majorUnits().queryFrom(null);
	}

	/**
	 * Test method for
	 * {@link org.javamoney.moneta.function.MonetaryUtil#majorPart()}.
	 */
	@Test(expectedExceptions = NullPointerException.class)
	public void testMajorPart_Null() {
		MonetaryUtil.majorPart().apply(null);
	}

	/**
	 * Test method for
	 * {@link org.javamoney.moneta.function.MonetaryUtil#majorPart()}.
	 */
	@Test(expectedExceptions = NullPointerException.class)
	public void testMinorPart_Null() {
		MonetaryUtil.minorPart().apply(null);
	}

	/**
	 * Test method for
	 * {@link org.javamoney.moneta.function.MonetaryUtil#percent(Number)}.
	 */
	@Test(expectedExceptions = NullPointerException.class)
	public void testPercent_Null1() {
		MonetaryUtil.percent(null);
	}

	/**
	 * Test method for
	 * {@link org.javamoney.moneta.function.MonetaryUtil#percent(Number)}.
	 */
	@Test(expectedExceptions = NullPointerException.class)
	public void testPercent_Null2() {
		MonetaryUtil.percent(1).apply(null);
	}

	/**
	 * Test method for
	 * {@link org.javamoney.moneta.function.MonetaryUtil#permil(Number)}.
	 */
	@Test(expectedExceptions = NullPointerException.class)
	public void testPermil_Null1() {
		MonetaryUtil.permil(null);
	}

	/**
	 * Test method for
	 * {@link org.javamoney.moneta.function.MonetaryUtil#permil(Number)}.
	 */
	@Test(expectedExceptions = NullPointerException.class)
	public void testPermil_Null2() {
		MonetaryUtil.permil(1).apply(null);
	}

	/**
	 * Test method for
	 * {@link org.javamoney.moneta.function.MonetaryUtil#reciprocal()}.
	 */
	@Test(expectedExceptions = NullPointerException.class)
	public void testReciprocal_Null() {
		MonetaryUtil.reciprocal().apply(null);
	}
}
