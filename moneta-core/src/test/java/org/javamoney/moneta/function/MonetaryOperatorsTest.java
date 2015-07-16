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

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertTrue;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;

import javax.money.CurrencyUnit;
import javax.money.Monetary;
import javax.money.MonetaryAmount;
import javax.money.MonetaryOperator;

import org.javamoney.moneta.Money;
import org.testng.annotations.Test;

/**
 * @author Anatole
 * @author Werner
 *
 */
public class MonetaryOperatorsTest {

	/**
	 * Test method for
	 * {@link org.javamoney.moneta.function.MonetaryOperators#reciprocal()}.
	 */
	@Test
	public void testReciprocal() {
		MonetaryAmount m = Monetary.getDefaultAmountFactory()
				.setCurrency("CHF").setNumber(200).create();
		MonetaryAmount r = m.with(MonetaryOperators.reciprocal());
        //noinspection BigDecimalMethodWithoutRoundingCalled
        assertEquals(
				Monetary.getDefaultAmountFactory().setCurrency("CHF")
						.setNumber(BigDecimal.ONE.divide(BigDecimal.valueOf(200)))
						.create(),
				r);
	}

	/**
	 * Test method for
	 * {@link org.javamoney.moneta.function.MonetaryOperators#permil(java.math.BigDecimal)}
	 * .
	 */
	@Test
	public void testPermilBigDecimal() {
		MonetaryAmount m = Monetary.getDefaultAmountFactory()
				.setCurrency(
						"CHF").setNumber(100).create();
		MonetaryAmount r = m.with(MonetaryOperators.permil(BigDecimal
				.valueOf(25)));
		assertEquals(
				Monetary.getDefaultAmountFactory().setCurrency("CHF")
						.setNumber(
								new BigDecimal("2.5")).create(),
				r);
	}

	/**
	 * Test method for
	 * {@link org.javamoney.moneta.function.MonetaryOperators#permil(java.lang.Number)}
	 * .
	 */
	@Test
	public void testPermilNumber() {
		MonetaryAmount m = Monetary.getDefaultAmountFactory()
				.setCurrency(
						"CHF").setNumber(100).create();
		MonetaryAmount r = m.with(MonetaryOperators.permil(25));
		assertEquals(
				Monetary.getDefaultAmountFactory().setCurrency("CHF")
						.setNumber(
								new BigDecimal("2.5")).create(),
				r);
	}

	/**
	 * Test method for
	 * {@link org.javamoney.moneta.function.MonetaryOperators#permil(java.lang.Number, java.math.MathContext)}
	 * .
	 */
	@Test
	public void testPermilNumberMathContext() {
		MonetaryAmount m = Monetary.getDefaultAmountFactory()
				.setCurrency(
						"CHF").setNumber(100).create();
		MonetaryAmount r = m.with(MonetaryOperators.permil(25,
				MathContext.DECIMAL64));
		assertEquals(
				Monetary.getDefaultAmountFactory().setCurrency("CHF")
						.setNumber(
								new BigDecimal("2.5")).create(),
				r);
	}

	/**
	 * Test method for
	 * {@link org.javamoney.moneta.function.MonetaryOperators#percent(java.math.BigDecimal)}
	 * .
	 */
	@Test
	public void testPercentBigDecimal() {
		MonetaryAmount m = Monetary.getDefaultAmountFactory()
				.setCurrency(
						"CHF").setNumber(100L).create();
		MonetaryAmount r = m.with(MonetaryOperators.percent(BigDecimal
				.valueOf(25)));
		assertEquals(
				Monetary.getDefaultAmountFactory().setCurrency("CHF")
						.setNumber(
								new BigDecimal("25")).create(),
				r);
	}

	/**
	 * Test method for
	 * {@link org.javamoney.moneta.function.MonetaryOperators#percent(java.lang.Number)}
	 * .
	 */
	@Test
	public void testPercentNumber() {
		MonetaryAmount m = Monetary.getDefaultAmountFactory()
				.setCurrency(
						"CHF").setNumber(100).create();
		MonetaryAmount r = m.with(MonetaryOperators.percent((long) 25));
		assertEquals(
				Monetary.getDefaultAmountFactory().setCurrency("CHF")
						.setNumber(
								new BigDecimal("25")).create(),
				r);
	}

	/**
	 * Test method for
	 * {@link org.javamoney.moneta.function.MonetaryOperators#percent(java.lang.Number)}
	 * .
	 */
	@Test
	public void testPercentToString() {
		MonetaryOperator p = MonetaryOperators.percent((short) 25);
		assertTrue(p.toString().contains("25"));
		assertTrue(p.toString().contains("%"));
	}

	/**
	 * Test method for
	 * {@link org.javamoney.moneta.function.MonetaryOperators#minorPart()}.
	 */
	@Test
	public void testMinorPart() {
		MonetaryAmount m = Monetary.getDefaultAmountFactory()
				.setCurrency(
						"CHF").setNumber(new BigDecimal(
						"1234.56789")).create();
		MonetaryAmount r = m.with(MonetaryOperators.minorPart());
		assertEquals(
				Monetary.getDefaultAmountFactory().setCurrency("CHF")
						.setNumber(
								new BigDecimal("0.56789")).create(),
				r);
	}

	/**
	 * Test method for
	 * {@link org.javamoney.moneta.function.MonetaryOperators#majorPart()}.
	 */
	@Test
	public void testMajorPart() {
		MonetaryAmount m = Monetary.getDefaultAmountFactory()
				.setCurrency(
						"CHF").setNumber(new BigDecimal(
						"1234.56789")).create();
		MonetaryAmount r = m.with(MonetaryOperators.majorPart());
		assertEquals(
				Monetary.getDefaultAmountFactory().setCurrency(
						"CHF").setNumber(new BigDecimal("1234")).create(),
				r);
	}



	/**
	 * Test method for
	 * {@link org.javamoney.moneta.function.MonetaryOperators#majorPart()}.
	 */
	@Test(expectedExceptions = NullPointerException.class)
	public void testMajorPart_Null() {
		MonetaryOperators.majorPart().apply(null);
	}

	/**
	 * Test method for
	 * {@link org.javamoney.moneta.function.MonetaryOperators#majorPart()}.
	 */
	@Test(expectedExceptions = NullPointerException.class)
	public void testMinorPart_Null() {
		MonetaryOperators.minorPart().apply(null);
	}

	/**
	 * Test method for
	 * {@link org.javamoney.moneta.function.MonetaryOperators#percent(Number)}.
	 */
	@Test(expectedExceptions = NullPointerException.class)
	public void testPercent_Null1() {
		MonetaryOperators.percent(null);
	}

	/**
	 * Test method for
	 * {@link org.javamoney.moneta.function.MonetaryOperators#percent(Number)}.
	 */
	@Test(expectedExceptions = NullPointerException.class)
	public void testPercent_Null2() {
		MonetaryOperators.percent(1).apply(null);
	}

	/**
	 * Test method for
	 * {@link org.javamoney.moneta.function.MonetaryOperators#permil(Number)}.
	 */
	@Test(expectedExceptions = NullPointerException.class)
	public void testPermil_Null1() {
		MonetaryOperators.permil(null);
	}

	/**
	 * Test method for
	 * {@link org.javamoney.moneta.function.MonetaryOperators#permil(Number)}.
	 */
	@Test(expectedExceptions = NullPointerException.class)
	public void testPermil_Null2() {
		MonetaryOperators.permil(1).apply(null);
	}

	/**
	 * Test method for
	 * {@link org.javamoney.moneta.function.MonetaryOperators#reciprocal()}.
	 */
	@Test(expectedExceptions = NullPointerException.class)
	public void testReciprocal_Null() {
		MonetaryOperators.reciprocal().apply(null);
	}

	@Test
	public void shouldRouding() {
		CurrencyUnit euro = Monetary.getCurrency("EUR");
		MonetaryAmount money = Money.parse("EUR 2.355432");
		MonetaryAmount result = MonetaryOperators.rounding().apply(money);
		assertNotNull(result);
		assertEquals(result.getCurrency(), euro);
		assertEquals(Double.valueOf(2.36), result.getNumber().doubleValue());
	}

	@Test(expectedExceptions = NullPointerException.class)
	public void shouldReturnErrorWhenRoundingTypeIsNull() {
		MonetaryAmount money = Money.parse("EUR 2.355432");
		MonetaryOperators.rounding(null).apply(money);
	}

	@Test
	public void shouldRoudingUsingRoundingMode() {
		CurrencyUnit euro = Monetary.getCurrency("EUR");
		MonetaryAmount money = Money.parse("EUR 2.355432");
		MonetaryAmount result = MonetaryOperators.rounding(RoundingMode.HALF_EVEN).apply(money);
		assertNotNull(result);
		assertEquals(result.getCurrency(), euro);
		assertEquals(Double.valueOf(2.36), result.getNumber().doubleValue());
	}

	@Test
	public void shouldRoudingUsingRoundingModeAndScale() {
		CurrencyUnit euro = Monetary.getCurrency("EUR");
		MonetaryAmount money = Money.parse("EUR 2.355432");
		MonetaryAmount result = MonetaryOperators.rounding(RoundingMode.HALF_EVEN, 4).apply(money);
		assertNotNull(result);
		assertEquals(result.getCurrency(), euro);
		assertEquals(Double.valueOf(2.3554), result.getNumber().doubleValue());
	}

	@Test
	public void shouldRoudingUsingScale() {
		CurrencyUnit euro = Monetary.getCurrency("EUR");
		MonetaryAmount money = Money.parse("EUR 2.355432");
		MonetaryAmount result = MonetaryOperators.rounding(4).apply(money);
		assertNotNull(result);
		assertEquals(result.getCurrency(), euro);
		assertEquals(Double.valueOf(2.3554), result.getNumber().doubleValue());
	}
	//
	@Test(expectedExceptions = NullPointerException.class)
	public void shouldReturnErrorWhenExchangeCurrencyIsNull() {
		MonetaryOperators.exchange(null);
	}

	@Test
	public void shouldExchangeCurrencyPositiveValue() {
		CurrencyUnit real = Monetary.getCurrency("BRL");
		MonetaryAmount money = Money.parse("EUR 2.35");
		MonetaryAmount result = MonetaryOperators.exchange(real).apply(money);
		assertNotNull(result);
		assertEquals(result.getCurrency(), real);
		assertEquals(Double.valueOf(2.35), result.getNumber().doubleValue());
	}

	@Test
	public void shouldExchangeCurrencyNegativeValue() {
		CurrencyUnit real = Monetary.getCurrency("BRL");
		MonetaryAmount money = Money.parse("BHD -1.345");
		MonetaryAmount result = MonetaryOperators.exchange(real).apply(money);
		assertNotNull(result);
		assertEquals(result.getCurrency(), real);
		assertEquals(Double.valueOf(-1.345), result.getNumber().doubleValue());
	}
}

