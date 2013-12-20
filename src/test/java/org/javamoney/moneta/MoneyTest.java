/*
 * Copyright (c) 2012, 2013, Credit Suisse (Anatole Tresch), Werner Keil. Licensed under the Apache
 * License, Version 2.0 (the "License"); you may not use this file except in compliance with the
 * License. You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License. Contributors: Anatole Tresch - initial implementation Wernner Keil - extensions and
 * adaptions.
 */
package org.javamoney.moneta;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;

import javax.money.CurrencyUnit;
import javax.money.MonetaryAmount;
import javax.money.MonetaryAmounts;
import javax.money.MonetaryContext;
import javax.money.MonetaryCurrencies;
import javax.money.MonetaryOperator;
import javax.money.MonetaryQuery;

import org.junit.Test;

/**
 * @author Anatole
 * @author Werner
 */
public class MoneyTest {
// TODO break this down into smaller test classes, 1.5k LOC seems a bit large;-)
	
	private static final BigDecimal TEN = new BigDecimal(10.0d);
	protected static final CurrencyUnit EURO = MonetaryCurrencies
			.getCurrency("EUR");
	protected static final CurrencyUnit DOLLAR = MonetaryCurrencies
			.getCurrency("USD");

	/**
	 * Test method for
	 * {@link org.javamoney.moneta.Money#of(javax.money.CurrencyUnit, java.math.BigDecimal)} .
	 */
	@Test
	public void testOfCurrencyUnitBigDecimal() {
		Money m = Money.of(MonetaryCurrencies.getCurrency("EUR"), TEN);
		assertEquals(TEN, m.getNumber().numberValue(BigDecimal.class));
	}

	@Test
	public void testOfCurrencyUnitDouble() {
		Money m = Money.of(MonetaryCurrencies.getCurrency("EUR"), 10.0d);
		assertTrue(TEN.doubleValue() == m.getNumber().doubleValue());
	}

	/**
	 * Test method for {@link org.javamoney.moneta.Money#getCurrency()}.
	 */
	@Test
	public void testGetCurrency() {
		MonetaryAmount money = Money.of(EURO, BigDecimal.TEN);
		assertNotNull(money.getCurrency());
		assertEquals("EUR", money.getCurrency().getCurrencyCode());
	}

	@Test
	public void testSubtractMonetaryAmount() {
		Money money1 = Money.of(EURO, BigDecimal.TEN);
		Money money2 = Money.of(EURO, BigDecimal.ONE);
		Money moneyResult = money1.subtract(money2);
		assertNotNull(moneyResult);
		assertEquals(9d, moneyResult.getNumber().doubleValue(), 0d);
	}

	@Test
	public void testDivideAndRemainder_BigDecimal() {
		Money money1 = Money.of(EURO, BigDecimal.ONE);
		Money[] divideAndRemainder = money1
				.divideAndRemainder(new BigDecimal(
						"0.50000000000000000001"));
		assertThat(
				divideAndRemainder[0].getNumber().numberValue(
						BigDecimal.class),
				equalTo(BigDecimal.ONE));
		assertThat(
				divideAndRemainder[1].getNumber().numberValue(
						BigDecimal.class),
				equalTo(new BigDecimal("0.49999999999999999999")));
	}

	@Test
	public void testDivideToIntegralValue_BigDecimal() {
		Money money1 = Money.of(EURO, BigDecimal.ONE);
		Money result = money1.divideToIntegralValue(new BigDecimal(
				"0.50000000000000000001"));
		assertThat(result.getNumber().numberValue(BigDecimal.class),
				equalTo(BigDecimal.ONE));
	}

	/**
	 * Test method for {@link org.javamoney.moneta.Money#hashCode()}.
	 */
	@Test
	public void testHashCode() {
		Money money1 = Money.of(EURO, BigDecimal.ONE);
		Money money2 = Money.of(EURO, new BigDecimal("1"));
		assertEquals(money1.hashCode(), money2.hashCode());
		Money money3 = Money.of(DOLLAR, 1.0);
		assertTrue(money1.hashCode() != money3.hashCode());
		assertTrue(money2.hashCode() != money3.hashCode());
		Money money4 = Money.of(DOLLAR, BigDecimal.ONE);
		assertTrue(money1.hashCode() != money4.hashCode());
		assertTrue(money2.hashCode() != money4.hashCode());
		Money money5 = Money.of(DOLLAR, BigDecimal.ONE);
		Money money6 = Money.of(DOLLAR, 1.0);
		assertTrue(money1.hashCode() != money5.hashCode());
		assertTrue(money2.hashCode() != money5.hashCode());
		assertTrue(money1.hashCode() != money6.hashCode());
		assertTrue(money2.hashCode() != money6.hashCode());
		// Test equality for values with different scales, but same numeric
		// values
		assertTrue(Money.of("CHF", BigDecimal.valueOf(0d)).hashCode() == Money
				.of("CHF", BigDecimal.valueOf(0)).hashCode());
		assertTrue(Money.of("CHF", BigDecimal.ZERO).hashCode() == Money.of(
				"CHF", BigDecimal.valueOf(0)).hashCode());
		assertTrue(Money.of("CHF", BigDecimal.valueOf(5)).hashCode() == Money
				.of("CHF", new BigDecimal("5.0")).hashCode());
		assertTrue(Money.of("CHF", BigDecimal.valueOf(5)).hashCode() == Money
				.of("CHF", new BigDecimal("5.00")).hashCode());
		assertTrue(Money.of("CHF", BigDecimal.valueOf(5)).hashCode() == Money
				.of("CHF", new BigDecimal("5.000")).hashCode());
		assertTrue(Money.of("CHF", BigDecimal.valueOf(5)).hashCode() == Money
				.of("CHF", new BigDecimal("5.0000")).hashCode());
		assertTrue(Money.of("CHF", new BigDecimal("-1.23")).hashCode() == Money
				.of("CHF", new BigDecimal("-1.230")).hashCode());
		assertTrue(Money.of("CHF", new BigDecimal("-1.23")).hashCode() == Money
				.of("CHF", new BigDecimal("-1.2300")).hashCode());
		assertTrue(Money.of("CHF", new BigDecimal("-1.23")).hashCode() == Money
				.of("CHF", new BigDecimal("-1.23000")).hashCode());
		assertTrue(Money.of("CHF", new BigDecimal("-1.23")).hashCode() == Money
				.of("CHF", new BigDecimal("-1.230000000000000000000"))
				.hashCode());
	}

	/**
	 * Test method for {@link org.javamoney.moneta.Money#getDefaultMathContext()}.
	 */
	@Test
	public void testGetDefaultMathContext() {
		Money money1 = Money.of(EURO, BigDecimal.ONE);
		assertEquals(Money.DEFAULT_MONETARY_CONTEXT,
				money1.getMonetaryContext());
	}

	/**
	 * Test method for
	 * {@link org.javamoney.moneta.Money#of(javax.money.CurrencyUnit, java.math.BigDecimal, java.math.MathContext)}
	 * .
	 */
	@Test
	public void testOfCurrencyUnitBigDecimalMathContext() {
		Money m = Money.of(
				EURO,
				BigDecimal.valueOf(2.15),
				new MonetaryContext.Builder(Money.class).setPrecision(
						2).setFixedScale(true).setAttribute(
						RoundingMode.DOWN).build());
		Money m2 = Money.of(EURO, BigDecimal.valueOf(2.1));
		assertEquals(m, m2);
		Money m3 = m.multiply(100);
		assertEquals(Money.of(EURO, 210), m3.abs());
	}

	/**
	 * Test method for
	 * {@link org.javamoney.moneta.Money#of(javax.money.CurrencyUnit, java.lang.Number)} .
	 */
	@Test
	public void testOfCurrencyUnitNumber() {
		Money m = Money.of(EURO, (byte) 2);
		assertNotNull(m);
		assertEquals(EURO, m.getCurrency());
		assertEquals(Byte.valueOf((byte) 2),
				m.getNumber().numberValue(Byte.class));
		m = Money.of(DOLLAR, (short) -2);
		assertNotNull(m);
		assertEquals(DOLLAR, m.getCurrency());
		assertEquals(Short.valueOf((short) -2),
				m.getNumber().numberValue(Short.class));
		m = Money.of(EURO, (int) -12);
		assertNotNull(m);
		assertEquals(EURO, m.getCurrency());
		assertEquals(Integer.valueOf((int) -12),
				m.getNumber().numberValue(Integer.class));
		m = Money.of(DOLLAR, (long) 12);
		assertNotNull(m);
		assertEquals(DOLLAR, m.getCurrency());
		assertEquals(Long.valueOf((long) 12),
				m.getNumber().numberValue(Long.class));
		m = Money.of(EURO, (float) 12.23);
		assertNotNull(m);
		assertEquals(EURO, m.getCurrency());
		assertEquals(Float.valueOf((float) 12.23), m.getNumber()
				.numberValue(Float.class));
		m = Money.of(DOLLAR, (double) -12.23);
		assertNotNull(m);
		assertEquals(DOLLAR, m.getCurrency());
		assertEquals(Double.valueOf((double) -12.23), m.getNumber()
				.numberValue(Double.class));
		m = Money.of(EURO, (Number) BigDecimal.valueOf(234.2345));
		assertNotNull(m);
		assertEquals(EURO, m.getCurrency());
		assertEquals(BigDecimal.valueOf(234.2345),
				m.getNumber().numberValue(BigDecimal.class));
		m = Money.of(DOLLAR, (Number) BigInteger.valueOf(23232312321432432L));
		assertNotNull(m);
		assertEquals(DOLLAR, m.getCurrency());
		assertEquals(Long.valueOf(23232312321432432L), m.getNumber()
				.numberValue(Long.class));
		assertEquals(BigInteger.valueOf(23232312321432432L),
				m.getNumber().numberValue(BigInteger.class));
	}

	/**
	 * Test method for
	 * {@link org.javamoney.moneta.Money#of(javax.money.CurrencyUnit, java.lang.Number, java.math.MathContext)}
	 * .
	 */
	@Test
	public void testOfCurrencyUnitNumberMathContext() {
		MonetaryContext mc = new MonetaryContext.Builder(Money.class)
				.setMaxScale(2345)
				.setFixedScale(true).setAttribute(RoundingMode.CEILING)
				.build();
		Money m = Money.of(EURO, (byte) 2, mc);
		assertNotNull(m);
		assertEquals(mc, m.getMonetaryContext());
		assertEquals(EURO, m.getCurrency());
		assertEquals(Byte.valueOf((byte) 2),
				m.getNumber().numberValue(Byte.class));
		m = Money.of(DOLLAR, (short) -2, mc);
		assertNotNull(m);
		assertEquals(mc, m.getMonetaryContext());
		assertEquals(DOLLAR, m.getCurrency());
		assertEquals(Short.valueOf((short) -2),
				m.getNumber().numberValue(Short.class));
		m = Money.of(EURO, (int) -12, mc);
		assertNotNull(m);
		assertEquals(mc, m.getMonetaryContext());
		assertEquals(EURO, m.getCurrency());
		assertEquals(Integer.valueOf((int) -12),
				m.getNumber().numberValue(Integer.class));
		m = Money.of(DOLLAR, (long) 12, mc);
		assertEquals(mc, m.getMonetaryContext());
		assertNotNull(m);
		assertEquals(DOLLAR, m.getCurrency());
		assertEquals(Long.valueOf((long) 12),
				m.getNumber().numberValue(Long.class));
		m = Money.of(EURO, (float) 12.23, mc);
		assertNotNull(m);
		assertEquals(mc, m.getMonetaryContext());
		assertEquals(EURO, m.getCurrency());
		assertEquals(Float.valueOf((float) 12.23), m.getNumber()
				.numberValue(Float.class));
		m = Money.of(DOLLAR, (double) -12.23, mc);
		assertNotNull(m);
		assertEquals(mc, m.getMonetaryContext());
		assertEquals(DOLLAR, m.getCurrency());
		assertEquals(mc, m.getMonetaryContext());
		assertEquals(Double.valueOf((double) -12.23), m.getNumber()
				.numberValue(Double.class));
		m = Money.of(EURO, (Number) BigDecimal.valueOf(234.2345), mc);
		assertNotNull(m);
		assertEquals(EURO, m.getCurrency());
		assertEquals(mc, m.getMonetaryContext());
		assertEquals(BigDecimal.valueOf(234.2345),
				m.getNumber().numberValue(BigDecimal.class));
		m = Money.of(DOLLAR, (Number) BigInteger.valueOf(23232312321432432L),
				mc);
		assertNotNull(m);
		assertEquals(DOLLAR, m.getCurrency());
		assertEquals(mc, m.getMonetaryContext());
		assertEquals(Long.valueOf(23232312321432432L), m.getNumber()
				.numberValue(Long.class));
		assertEquals(BigInteger.valueOf(23232312321432432L),
				m.getNumber().numberValue(BigInteger.class));
	}

	/**
	 * Test method for {@link org.javamoney.moneta.Money#of(java.lang.String, java.lang.Number)} .
	 */
	@Test
	public void testOfStringNumber() {
		Money m = Money.of("EUR", (byte) 2);
		assertNotNull(m);
		assertEquals(EURO, m.getCurrency());
		assertEquals(Byte.valueOf((byte) 2),
				m.getNumber().numberValue(Byte.class));
		m = Money.of("USD", (short) -2);
		assertNotNull(m);
		assertEquals(DOLLAR, m.getCurrency());
		assertEquals(Short.valueOf((short) -2),
				m.getNumber().numberValue(Short.class));
		m = Money.of("EUR", (int) -12);
		assertNotNull(m);
		assertEquals(EURO, m.getCurrency());
		assertEquals(Integer.valueOf((int) -12),
				m.getNumber().numberValue(Integer.class));
		m = Money.of("USD", (long) 12);
		assertNotNull(m);
		assertEquals(DOLLAR, m.getCurrency());
		assertEquals(Long.valueOf((long) 12),
				m.getNumber().numberValue(Long.class));
		m = Money.of("EUR", (float) 12.23);
		assertNotNull(m);
		assertEquals(EURO, m.getCurrency());
		assertEquals(Float.valueOf((float) 12.23), m.getNumber()
				.numberValue(Float.class));
		m = Money.of("USD", (double) -12.23);
		assertNotNull(m);
		assertEquals(DOLLAR, m.getCurrency());
		assertEquals(Double.valueOf((double) -12.23), m.getNumber()
				.numberValue(Double.class));
		m = Money.of("EUR", (Number) BigDecimal.valueOf(234.2345));
		assertNotNull(m);
		assertEquals(EURO, m.getCurrency());
		assertEquals(BigDecimal.valueOf(234.2345),
				m.getNumber().numberValue(BigDecimal.class));
		m = Money.of("USD", (Number) BigInteger.valueOf(23232312321432432L));
		assertNotNull(m);
		assertEquals(DOLLAR, m.getCurrency());
		assertEquals(Long.valueOf(23232312321432432L), m.getNumber()
				.numberValue(Long.class));
		assertEquals(BigInteger.valueOf(23232312321432432L),
				m.getNumber().numberValue(BigInteger.class));
	}

	/**
	 * Test method for
	 * {@link org.javamoney.moneta.Money#of(java.lang.String, java.lang.Number, java.math.MathContext)}
	 * .
	 */
	@Test
	public void testOfStringNumberMathContext() {
		MonetaryContext mc = new MonetaryContext.Builder(Money.class)
				.setMaxScale(2345).setFixedScale(true)
				.setAttribute(RoundingMode.CEILING).build();
		Money m = Money.of("EUR", (byte) 2, mc);
		assertNotNull(m);
		assertEquals(mc, m.getMonetaryContext());
		assertEquals(EURO, m.getCurrency());
		assertEquals(Byte.valueOf((byte) 2),
				m.getNumber().numberValue(Byte.class));
		m = Money.of("USD", (short) -2, mc);
		assertNotNull(m);
		assertEquals(mc, m.getMonetaryContext());
		assertEquals(DOLLAR, m.getCurrency());
		assertEquals(Short.valueOf((short) -2),
				m.getNumber().numberValue(Short.class));
		m = Money.of("EUR", (int) -12, mc);
		assertNotNull(m);
		assertEquals(mc, m.getMonetaryContext());
		assertEquals(EURO, m.getCurrency());
		assertEquals(Integer.valueOf((int) -12),
				m.getNumber().numberValue(Integer.class));
		m = Money.of("USD", (long) 12, mc);
		assertEquals(mc, m.getMonetaryContext());
		assertNotNull(m);
		assertEquals(DOLLAR, m.getCurrency());
		assertEquals(Long.valueOf((long) 12),
				m.getNumber().numberValue(Long.class));
		m = Money.of("EUR", (float) 12.23, mc);
		assertNotNull(m);
		assertEquals(mc, m.getMonetaryContext());
		assertEquals(EURO, m.getCurrency());
		assertEquals(Float.valueOf((float) 12.23), m.getNumber()
				.numberValue(Float.class));
		m = Money.of("USD", (double) -12.23, mc);
		assertNotNull(m);
		assertEquals(mc, m.getMonetaryContext());
		assertEquals(DOLLAR, m.getCurrency());
		assertEquals(mc, m.getMonetaryContext());
		assertEquals(Double.valueOf((double) -12.23), m.getNumber()
				.numberValue(Double.class));
		m = Money.of("EUR", (Number) BigDecimal.valueOf(234.2345), mc);
		assertNotNull(m);
		assertEquals(EURO, m.getCurrency());
		assertEquals(mc, m.getMonetaryContext());
		assertEquals(BigDecimal.valueOf(234.2345),
				m.getNumber().numberValue(BigDecimal.class));
		m = Money
				.of("USD", (Number) BigInteger.valueOf(23232312321432432L), mc);
		assertNotNull(m);
		assertEquals(DOLLAR, m.getCurrency());
		assertEquals(mc, m.getMonetaryContext());
		assertEquals(Long.valueOf(23232312321432432L), m.getNumber()
				.numberValue(Long.class));
		assertEquals(BigInteger.valueOf(23232312321432432L),
				m.getNumber().numberValue(BigInteger.class));
	}

	/**
	 * Test method for {@link org.javamoney.moneta.Money#equals(java.lang.Object)}.
	 */
	@Test
	public void testEqualsObject() {
		Money[] moneys = new Money[] {
				Money.of("CHF", 0),
				Money.of("CHF", BigDecimal.ONE),
				Money.of("XXX", BigDecimal.ONE),
				Money.of("XXX", BigDecimal.ONE.negate())
		};
		for (int i = 0; i < moneys.length; i++) {
			for (int j = 0; j < moneys.length; j++) {
				if (i == j) {
					assertEquals(moneys[i], moneys[j]);
				}
				else {
					assertNotSame(moneys[i], moneys[j]);
				}
			}
		}
		// Test equality for values with different scales, but same numeric
		// values
		assertTrue(Money.of("CHF", BigDecimal.valueOf(0d)).equals(
				Money.of("CHF", BigDecimal.valueOf(0))));
		assertTrue(Money.of("CHF", BigDecimal.ZERO).equals(
				Money.of("CHF", BigDecimal.valueOf(0))));
		assertTrue(Money.of("CHF", BigDecimal.valueOf(5)).equals(
				Money.of("CHF", new BigDecimal("5.0"))));
		assertTrue(Money.of("CHF", BigDecimal.valueOf(5)).equals(
				Money.of("CHF", new BigDecimal("5.00"))));
		assertTrue(Money.of("CHF", BigDecimal.valueOf(5)).equals(
				Money.of("CHF", new BigDecimal("5.000"))));
		assertTrue(Money.of("CHF", BigDecimal.valueOf(5)).equals(
				Money.of("CHF", new BigDecimal("5.0000"))));
		assertTrue(Money.of("CHF", new BigDecimal("-1.23")).equals(
				Money.of("CHF", new BigDecimal("-1.230"))));
		assertTrue(Money.of("CHF", new BigDecimal("-1.23")).equals(
				Money.of("CHF", new BigDecimal("-1.2300"))));
		assertTrue(Money.of("CHF", new BigDecimal("-1.23")).equals(
				Money.of("CHF", new BigDecimal("-1.23000"))));
		assertTrue(Money.of("CHF", new BigDecimal("-1.23")).equals(
				Money.of("CHF", new BigDecimal("-1.230000000000000000000"))));
	}

	/**
	 * Test differently created MonetaryAmount or Money instances for equality
	 * {@link org.javamoney.moneta.Money#equals(Object)}.
	 */
	@Test
	public void testEqualsMonetarAmount() {
		MonetaryAmount m = MonetaryAmounts.getDefaultAmountFactory()
				.withCurrency(
						"CHF").with(100).create();
		MonetaryAmount m2 = Money.of("CHF", 100);
		Money m3 = Money.of("CHF", 100);
		assertTrue(m.equals(m2));
		assertTrue(m.equals(m3));
		assertTrue(m2.equals(m3));
	}

	/**
	 * Test method for {@link org.javamoney.moneta.Money#compareTo(javax.money.MonetaryAmount)}.
	 */
	@Test
	public void testCompareTo() {
		Money m1 = Money.of("CHF", -2);
		Money m2 = Money.of("CHF", 0);
		Money m3 = Money.of("CHF", -0);
		Money m4 = Money.of("CHF", 2);
		assertEquals(0, m2.compareTo(m3));
		assertEquals(0, m2.compareTo(m2));
		assertEquals(0, m3.compareTo(m3));
		assertEquals(0, m3.compareTo(m2));
		assertTrue(m1.compareTo(m2) < 0);
		assertTrue(m2.compareTo(m1) > 0);
		assertTrue(m1.compareTo(m3) < 0);
		assertTrue(m2.compareTo(m3) == 0);
		assertTrue(m1.compareTo(m4) < 0);
		assertTrue(m3.compareTo(m4) < 0);
		assertTrue(m4.compareTo(m1) > 0);
		assertTrue(m4.compareTo(m2) > 0);
	}

	/**
	 * Test method for {@link org.javamoney.moneta.Money#with(java.math.MathContext)}.
	 */
	@Test
	public void testWithMonetaryContext() {
		Money m = Money.of("CHF", 10);
		assertEquals(Money.DEFAULT_MONETARY_CONTEXT, m.getMonetaryContext());
		MonetaryContext mc = new MonetaryContext.Builder(Money.class)
				.setPrecision(128).setAttribute(RoundingMode.HALF_EVEN)
				.build();
		MonetaryAmount m2 = m.getFactory().with(mc).create();
		assertNotNull(m2);
		assertTrue(m != m2);
		assertEquals(Money.DEFAULT_MONETARY_CONTEXT, m.getMonetaryContext());
		assertEquals(mc, m2.getMonetaryContext());
	}

	/**
	 * Test method for {@link org.javamoney.moneta.Money#abs()}.
	 */
	@Test
	public void testAbs() {
		Money m = Money.of("CHF", 10);
		assertEquals(m, m.abs());
		assertTrue(m == m.abs());
		m = Money.of("CHF", 0);
		assertEquals(m, m.abs());
		assertTrue(m == m.abs());
		m = Money.of("CHF", -10);
		assertEquals(m.negate(), m.abs());
		assertTrue(m != m.abs());
	}

	/**
	 * Test method for {@link org.javamoney.moneta.Money#add(javax.money.MonetaryAmount)} .
	 */
	@Test
	public void testAdd() {
		Money money1 = Money.of(EURO, BigDecimal.TEN);
		Money money2 = Money.of(EURO, BigDecimal.ONE);
		Money moneyResult = money1.add(money2);
		assertNotNull(moneyResult);
		assertEquals(11d, moneyResult.getNumber()
				.doubleValue(), 0d);
	}

	/**
	 * Test method for {@link org.javamoney.moneta.Money#divide(java.lang.Number)}.
	 */
	@Test
	public void testDivideNumber() {
		Money m = Money.of("CHF", 100);
		assertEquals(
				Money.of("CHF",
						BigDecimal.valueOf(100).divide(BigDecimal.valueOf(5))),
				m.divide(BigDecimal.valueOf(5)));
	}

	/**
	 * Test method for {@link org.javamoney.moneta.Money#divideAndRemainder(java.lang.Number)}.
	 */
	@Test
	public void testDivideAndRemainderNumber() {
		Money m = Money.of("CHF", 100);
		assertEquals(
				Money.of(
						"CHF",
						BigDecimal.valueOf(33)),
				m.divideAndRemainder(
						BigDecimal.valueOf(3))[0]);
		assertEquals(
				Money.of(
						"CHF",
						BigDecimal.valueOf(1)),
				m.divideAndRemainder(
						BigDecimal.valueOf(3))[1]);
	}

	/**
	 * Test method for {@link org.javamoney.moneta.Money#divideToIntegralValue(java.lang.Number)} .
	 */
	@Test
	public void testDivideToIntegralValueNumber() {
		Money m = Money.of("CHF", 100);
		assertEquals(
				Money.of(
						"CHF",
						BigDecimal.valueOf(5)),
				m.divideToIntegralValue(
						BigDecimal.valueOf(20)));
		assertEquals(
				Money.of(
						"CHF",
						BigDecimal.valueOf(33)),
				m.divideToIntegralValue(
						BigDecimal.valueOf(3)));
	}

	/**
	 * Test method for {@link org.javamoney.moneta.Money#multiply(java.lang.Number)}.
	 */
	@Test
	public void testMultiplyNumber() {
		Money m = Money.of("CHF", 100);
		assertEquals(Money.of("CHF", 400), m.multiply(4));
		assertEquals(Money.of("CHF", 200), m.multiply(2));
		assertEquals(Money.of("CHF", new BigDecimal("50.0")), m.multiply(0.5));
	}

	/**
	 * Test method for {@link org.javamoney.moneta.Money#negate()}.
	 */
	@Test
	public void testNegate() {
		Money m = Money.of("CHF", 100);
		assertEquals(Money.of("CHF", -100), m.negate());
		m = Money.of("CHF", -123.234);
		assertEquals(Money.of("CHF", 123.234), m.negate());
	}

	/**
	 * Test method for {@link org.javamoney.moneta.Money#plus()}.
	 */
	@Test
	public void testPlus() {
		Money m = Money.of("CHF", 100);
		assertEquals(Money.of("CHF", 100), m.plus());
		m = Money.of("CHF", 123.234);
		assertEquals(Money.of("CHF", 123.234), m.plus());
	}

	/**
	 * Test method for {@link org.javamoney.moneta.Money#subtract(javax.money.MonetaryAmount)}.
	 */
	@Test
	public void testSubtract() {
		Money m = Money.of("CHF", 100);
		Money s1 = Money.of("CHF", 100);
		Money s2 = Money.of("CHF", 200);
		Money s3 = Money.of("CHF", 0);
		assertEquals(Money.of("CHF", 0), m.subtract(s1));
		assertEquals(Money.of("CHF", -100), m.subtract(s2));
		assertEquals(Money.of("CHF", 100), m.subtract(s3));
		assertTrue(m == m.subtract(s3));
		m = Money.of("CHF", -123.234);
		assertEquals(Money.of("CHF", new BigDecimal("-223.234")),
				m.subtract(s1));
		assertEquals(Money.of("CHF", new BigDecimal("-323.234")),
				m.subtract(s2));
		assertEquals(Money.of("CHF", new BigDecimal("-123.234")),
				m.subtract(s3));
		assertTrue(m == m.subtract(s3));
		m = Money.of("CHF", 12.402345534);
		s1 = Money.of("CHF", 2343.45);
		s2 = Money.of("CHF", 12.402345534);
		s3 = Money.of("CHF", -2343.45);
		assertEquals(Money.of("CHF", new BigDecimal("12.402345534")
				.subtract(new BigDecimal("2343.45"))), m.subtract(s1));
		assertEquals(Money.of("CHF", new BigDecimal("12.402345534")
				.subtract(new BigDecimal("12.402345534"))),
				m.subtract(s2));
		assertEquals(Money.of("CHF", 0), m.subtract(s2));
		assertEquals(Money.of("CHF", new BigDecimal("2355.852345534")),
				m.subtract(s3));
		assertTrue(m == m.subtract(Money.of("CHF", 0)));
	}

	/**
	 * Test method for {@link org.javamoney.moneta.Money#remainder(java.lang.Number)}.
	 */
	@Test
	public void testRemainderNumber() {
		Money[] moneys = new Money[] { Money.of("CHF", 100),
				Money.of("CHF", 34242344), Money.of("CHF", 23123213.435),
				Money.of("CHF", 0), Money.of("CHF", -100),
				Money.of("CHF", -723527.36532) };
		for (Money m : moneys) {
			assertEquals(
					"Invalid remainder of " + 10.50,
					m.getFactory()
							.with(m.getCurrency())
							.with(m.getNumber()
									.numberValue(BigDecimal.class)
									.remainder(
											BigDecimal.valueOf(10.50)))
							.create(),
					m.remainder(10.50));
			assertEquals(
					"Invalid remainder of " + -30.20,
					m.getFactory()
							.with(m.getCurrency())
							.with(m.getNumber()
									.numberValue(BigDecimal.class)
									.remainder(
											BigDecimal.valueOf(-30.20)))
							.create(),
					m.remainder(-30.20));
			assertEquals(
					"Invalid remainder of " + -3,
					m.getFactory()
							.with(m.getCurrency())
							.with(m.getNumber()
									.numberValue(BigDecimal.class)
									.remainder(
											BigDecimal.valueOf(-3))).create(),
					m.remainder(-3));
			assertEquals(
					"Invalid remainder of " + 3,
					m.getFactory()
							.with(m.getCurrency())
							.with(m.getNumber()
									.numberValue(BigDecimal.class)
									.remainder(
											BigDecimal.valueOf(3))).create(),
					m.remainder(3));
		}
	}

	/**
	 * Test method for {@link org.javamoney.moneta.Money#scaleByPowerOfTen(int)} .
	 */
	@Test
	public void testScaleByPowerOfTen() {
		Money[] moneys = new Money[] { Money.of("CHF", 100),
				Money.of("CHF", 34242344), Money.of("CHF", 23123213.435),
				Money.of("CHF", 0), Money.of("CHF", -100),
				Money.of("CHF", -723527.36532) };
		for (Money m : moneys) {
			for (int p = -10; p < 10; p++) {
				assertEquals(
						"Invalid scaleByPowerOfTen.",
						m.getFactory()
								.with(m.getCurrency())
								.with(m.getNumber()
										.numberValue(BigDecimal.class)
										.scaleByPowerOfTen(
												p)).create(),
						m.scaleByPowerOfTen(p));
			}
		}
	}

	/**
	 * Test method for {@link org.javamoney.moneta.Money#isZero()}.
	 */
	@Test
	public void testIsZero() {
		Money[] moneys = new Money[] { Money.of("CHF", 100),
				Money.of("CHF", 34242344), Money.of("CHF", 23123213.435),
				Money.of("CHF", -100),
				Money.of("CHF", -723527.36532) };
		for (Money m : moneys) {
			assertFalse(m.isZero());
		}
		moneys = new Money[] { Money.of("CHF", 0),
				Money.of("CHF", 0.0), Money.of("CHF", BigDecimal.ZERO),
				Money.of("CHF", new BigDecimal("0.00000000000000000")) };
		for (Money m : moneys) {
			assertTrue(m.isZero());
		}
	}

	/**
	 * Test method for {@link org.javamoney.moneta.Money#isPositive()}.
	 */
	@Test
	public void testIsPositive() {
		Money[] moneys = new Money[] { Money.of("CHF", 100),
				Money.of("CHF", 34242344), Money.of("CHF", 23123213.435) };
		for (Money m : moneys) {
			assertTrue(m.isPositive());
		}
		moneys = new Money[] { Money.of("CHF", 0),
				Money.of("CHF", 0.0), Money.of("CHF", BigDecimal.ZERO),
				Money.of("CHF", new BigDecimal("0.00000000000000000")),
				Money.of("CHF", -100),
				Money.of("CHF", -34242344), Money.of("CHF", -23123213.435) };
		for (Money m : moneys) {
			assertFalse(m.isPositive());
		}
	}

	/**
	 * Test method for {@link org.javamoney.moneta.Money#isPositiveOrZero()}.
	 */
	@Test
	public void testIsPositiveOrZero() {
		Money[] moneys = new Money[] { Money.of("CHF", 0),
				Money.of("CHF", 0.0), Money.of("CHF", BigDecimal.ZERO),
				Money.of("CHF", new BigDecimal("0.00000000000000000")),
				Money.of("CHF", 100),
				Money.of("CHF", 34242344), Money.of("CHF", 23123213.435) };
		for (Money m : moneys) {
			assertTrue("Invalid positiveOrZero (expected true): " + m,
					m.isPositiveOrZero());
		}
		moneys = new Money[] {
				Money.of("CHF", -100),
				Money.of("CHF", -34242344), Money.of("CHF", -23123213.435) };
		for (Money m : moneys) {
			assertFalse("Invalid positiveOrZero (expected false): " + m,
					m.isPositiveOrZero());
		}
	}

	/**
	 * Test method for {@link org.javamoney.moneta.Money#isNegative()}.
	 */
	@Test
	public void testIsNegative() {
		Money[] moneys = new Money[] { Money.of("CHF", 0),
				Money.of("CHF", 0.0), Money.of("CHF", BigDecimal.ZERO),
				Money.of("CHF", new BigDecimal("0.00000000000000000")),
				Money.of("CHF", 100),
				Money.of("CHF", 34242344), Money.of("CHF", 23123213.435) };
		for (Money m : moneys) {
			assertFalse("Invalid isNegative (expected false): " + m,
					m.isNegative());
		}
		moneys = new Money[] {
				Money.of("CHF", -100),
				Money.of("CHF", -34242344), Money.of("CHF", -23123213.435) };
		for (Money m : moneys) {
			assertTrue("Invalid isNegative (expected true): " + m,
					m.isNegative());
		}
	}

	/**
	 * Test method for {@link org.javamoney.moneta.Money#isNegativeOrZero()}.
	 */
	@Test
	public void testIsNegativeOrZero() {
		Money[] moneys = new Money[] {
				Money.of("CHF", 100),
				Money.of("CHF", 34242344), Money.of("CHF", 23123213.435) };
		for (Money m : moneys) {
			assertFalse("Invalid negativeOrZero (expected false): " + m,
					m.isNegativeOrZero());
		}
		moneys = new Money[] { Money.of("CHF", 0),
				Money.of("CHF", 0.0), Money.of("CHF", BigDecimal.ZERO),
				Money.of("CHF", new BigDecimal("0.00000000000000000")),
				Money.of("CHF", -100),
				Money.of("CHF", -34242344), Money.of("CHF", -23123213.435) };
		for (Money m : moneys) {
			assertTrue("Invalid negativeOrZero (expected true): " + m,
					m.isNegativeOrZero());
		}
	}

	/**
	 * Test method for {@link org.javamoney.moneta.Money#with(java.lang.Number)} .
	 */
	@Test
	public void testWithNumber() {
		Money[] moneys = new Money[] {
				Money.of("CHF", 100),
				Money.of("CHF", 34242344),
				Money.of("CHF", new BigDecimal("23123213.435")),
				Money.of("CHF", new BigDecimal("-23123213.435")),
				Money.of("CHF", -23123213),
				Money.of("CHF", 0) };
		Money s = Money.of("CHF", 10);
		MonetaryAmount[] moneys2 = new MonetaryAmount[] {
				s.getFactory().with(s.getCurrency()).with(100).create(),
				s.getFactory().with(s.getCurrency()).with(34242344)
						.create(),
				s.getFactory().with(s.getCurrency())
						.with(new BigDecimal("23123213.435")).create(),
				s.getFactory().with(s.getCurrency())
						.with(new BigDecimal("-23123213.435")).create(),
				s.getFactory().with(s.getCurrency()).with(-23123213)
						.create(),
				s.getFactory().with(s.getCurrency()).with(0).create() };
		for (int i = 0; i < moneys.length; i++) {
			assertEquals("with(Number) failed.", moneys[i], moneys2[i]);
		}
	}

	/**
	 * Test method for
	 * {@link org.javamoney.moneta.Money#with(javax.money.CurrencyUnit, java.lang.Number)} .
	 */
	@Test
	public void testWithCurrencyUnitNumber() {
		Money[] moneys = new Money[] {
				Money.of("CHF", 100),
				Money.of("USD", 34242344), Money.of("EUR", 23123213.435),
				Money.of("USS", -23123213.435), Money.of("USN", -23123213),
				Money.of("GBP", 0) };
		Money s = Money.of("XXX", 10);
		MonetaryAmount[] moneys2 = new MonetaryAmount[] {
				s.getFactory()
						.with(MonetaryCurrencies.getCurrency("CHF"))
						.with(100).create(),
				s.getFactory()
						.with(MonetaryCurrencies.getCurrency("USD"))
						.with(34242344).create(),
				s.getFactory()
						.with(MonetaryCurrencies.getCurrency("EUR"))
						.with(new BigDecimal(
								"23123213.435")).create(),
				s.getFactory()
						.with(MonetaryCurrencies.getCurrency("USS"))
						.with(new BigDecimal(
								"-23123213.435")).create(),
				s.getFactory()
						.with(MonetaryCurrencies.getCurrency("USN"))
						.with(-23123213).create(),
				s.getFactory()
						.with(MonetaryCurrencies.getCurrency("GBP"))
						.with(0).create() };
		for (int i = 0; i < moneys.length; i++) {
			assertEquals("with(Number) failed.", moneys[i], moneys2[i]);
		}
	}

	/**
	 * Test method for {@link org.javamoney.moneta.Money#getScale()}.
	 */
	@Test
	public void testGetScale() {
		Money[] moneys = new Money[] {
				Money.of("CHF", 100),
				Money.of("USD", 34242344), Money.of("EUR", 23123213.435),
				Money.of("USS", -23123213.435), Money.of("USN", -23123213),
				Money.of("GBP", 0) };
		for (Money m : moneys) {
			assertEquals("Scale for " + m,
					m.getNumber().numberValue(BigDecimal.class)
							.scale(),
					m.getScale());
		}
	}

	/**
	 * Test method for {@link org.javamoney.moneta.Money#getPrecision()}.
	 */
	@Test
	public void testGetPrecision() {
		Money[] moneys = new Money[] {
				Money.of("CHF", 100),
				Money.of("USD", 34242344), Money.of("EUR", 23123213.435),
				Money.of("USS", -23123213.435), Money.of("USN", -23123213),
				Money.of("GBP", 0) };
		for (Money m : moneys) {
			assertEquals("Precision for " + m,
					m.getNumber().numberValue(BigDecimal.class)
							.precision(),
					m.getPrecision());
		}
	}

	/**
	 * Test method for {@link org.javamoney.moneta.Money#longValue()}.
	 */
	@Test
	public void testLongValue() {
		Money m = Money.of("CHF", 100);
		assertEquals("longValue of " + m, 100L,
				m.getNumber()
						.longValue());
		m = Money.of("CHF", -100);
		assertEquals("longValue of " + m, -100L,
				m.getNumber()
						.longValue());
		m = Money.of("CHF", -100.3434);
		assertEquals("longValue of " + m, -100L,
				m.getNumber()
						.longValue());
		m = Money.of("CHF", 100.3434);
		assertEquals("longValue of " + m, 100L,
				m.getNumber()
						.longValue());
		m = Money.of("CHF", 0);
		assertEquals("longValue of " + m, 0L,
				m.getNumber().longValue());
		m = Money.of("CHF", -0.0);
		assertEquals("longValue of " + m, 0L,
				m.getNumber()
						.longValue());
		m = Money.of("CHF", Long.MAX_VALUE);
		assertEquals("longValue of " + m, Long.MAX_VALUE,
				m.getNumber().longValue());
		m = Money.of("CHF", Long.MIN_VALUE);
		assertEquals("longValue of " + m, Long.MIN_VALUE,
				m.getNumber().longValue());
		// try {
		m = Money
				.of("CHF",
						new BigDecimal(
								"12121762517652176251725178251872652765321876352187635217835378125"));
		m.getNumber().longValue();
	}

	/**
	 * Test method for {@link org.javamoney.moneta.Money#longValueExact()}.
	 */
	@Test
	public void testLongValueExact() {
		Money m = Money.of("CHF", 100);
		assertEquals("longValue of " + m, 100L, m.getNumber()
				.longValueExact());
		m = Money.of("CHF", -100);
		assertEquals("longValue of " + m, -100L, m.getNumber()
				.longValueExact());
		m = Money.of("CHF", 0);
		assertEquals("longValue of " + m, 0L, m.getNumber().longValueExact());
		m = Money.of("CHF", -0.0);
		assertEquals("longValue of " + m, 0L, m.getNumber().longValue());
		m = Money.of("CHF", Long.MAX_VALUE);
		assertEquals("longValue of " + m, Long.MAX_VALUE,
				m.getNumber().longValue());
		m = Money.of("CHF", Long.MIN_VALUE);
		assertEquals("longValue of " + m, Long.MIN_VALUE,
				m.getNumber().longValue());
		try {
			m = Money
					.of("CHF",
							new BigDecimal(
									"12121762517652176251725178251872652765321876352187635217835378125"));
			m.getNumber().longValueExact();
			fail("longValueExact(12121762517652176251725178251872652765321876352187635217835378125) should fail!");
		} catch (ArithmeticException e) {
			// OK
		}
		try {
			m = Money.of("CHF", -100.3434);
			m.getNumber().longValueExact();
			fail("longValueExact(-100.3434) should raise an ArithmeticException.");
		} catch (ArithmeticException e) {
			// OK
		}
		try {
			m = Money.of("CHF", 100.3434);
			m.getNumber().longValueExact();
			fail("longValueExact(100.3434) should raise an ArithmeticException.");
		} catch (ArithmeticException e) {
			// OK
		}
	}

	/**
	 * Test method for {@link org.javamoney.moneta.Money#doubleValue()}.
	 */
	@Test
	public void testDoubleValue() {
		Money m = Money.of("CHF", 100);
		assertEquals("doubleValue of " + m, 100d, m.getNumber()
				.doubleValue(), 0.0d);
		m = Money.of("CHF", -100);
		assertEquals("doubleValue of " + m, -100d, m.getNumber()
				.doubleValue(), 0.0d);
		m = Money.of("CHF", -100.3434);
		assertEquals("doubleValue of " + m, -100.3434, m
				.getNumber().doubleValue(), 0.0d);
		m = Money.of("CHF", 100.3434);
		assertEquals("doubleValue of " + m, 100.3434, m.getNumber()
				.doubleValue(), 0.0d);
		m = Money.of("CHF", 0);
		assertEquals("doubleValue of " + m, 0d, m.getNumber()
				.doubleValue(), 0.0d);
		m = Money.of("CHF", -0.0);
		assertEquals("doubleValue of " + m, 0d, m.getNumber()
				.doubleValue(), 0.0d);
		m = Money.of("CHF", Double.MAX_VALUE);
		assertEquals("doubleValue of " + m, Double.MAX_VALUE,
				m.getNumber().doubleValue(),
				0.0d);
		m = Money.of("CHF", Double.MIN_VALUE);
		assertEquals("doubleValue of " + m, Double.MIN_VALUE,
				m.getNumber().doubleValue(),
				0.0d);
		// try {
		m = Money
				.of("CHF",
						new BigDecimal(
								"12121762517652176251725178251872652765321876352187635217835378125"));
		m.getNumber().doubleValue();
		// fail("doubleValue(12121762517652176251725178251872652765321876352187635217835378125) should fail!");
		// } catch (ArithmeticException e) {
		// // OK
		// }
	}

	/**
	 * Test method for {@link org.javamoney.moneta.Money#signum()}.
	 */
	@Test
	public void testSignum() {
		Money m = Money.of("CHF", 100);
		assertEquals("signum of " + m, 1, m.signum());
		m = Money.of("CHF", -100);
		assertEquals("signum of " + m, -1, m.signum());
		m = Money.of("CHF", 100.3435);
		assertEquals("signum of " + m, 1, m.signum());
		m = Money.of("CHF", -100.3435);
		assertEquals("signum of " + m, -1, m.signum());
		m = Money.of("CHF", 0);
		assertEquals("signum of " + m, 0, m.signum());
		m = Money.of("CHF", -0);
		assertEquals("signum of " + m, 0, m.signum());
	}

	/**
	 * Test method for {@link org.javamoney.moneta.Money#isLessThan(javax.money.MonetaryAmount)} .
	 */
	@Test
	public void testIsLessThan() {
		assertFalse(Money.of("CHF", BigDecimal.valueOf(0d)).isLessThan(
				Money.of("CHF", BigDecimal.valueOf(0))));
		assertFalse(Money.of("CHF", BigDecimal.valueOf(0.00000000001d))
				.isLessThan(Money.of("CHF", BigDecimal.valueOf(0d))));
		assertFalse(Money.of("CHF", 15).isLessThan(
				Money.of("CHF", 10)));
		assertFalse(Money.of("CHF", 15.546).isLessThan(
				Money.of("CHF", 10.34)));
		assertTrue(Money.of("CHF", 5).isLessThan(
				Money.of("CHF", 10)));
		assertTrue(Money.of("CHF", 5.546).isLessThan(
				Money.of("CHF", 10.34)));
	}

	/**
	 * Test method for
	 * {@link org.javamoney.moneta.Money#isLessThanOrEqualTo(javax.money.MonetaryAmount)} .
	 */
	@Test
	public void testIsLessThanOrEqualTo() {
		assertTrue(Money.of("CHF", BigDecimal.valueOf(0d))
				.isLessThanOrEqualTo(
						Money.of("CHF", BigDecimal.valueOf(0))));
		assertFalse(Money.of("CHF", BigDecimal.valueOf(0.00000000001d))
				.isLessThanOrEqualTo(Money.of("CHF", BigDecimal.valueOf(0d))));
		assertFalse(Money.of("CHF", 15).isLessThanOrEqualTo(
				Money.of("CHF", 10)));
		assertFalse(Money.of("CHF", 15.546).isLessThan(
				Money.of("CHF", 10.34)));
		assertTrue(Money.of("CHF", 5).isLessThanOrEqualTo(
				Money.of("CHF", 10)));
		assertTrue(Money.of("CHF", 5.546).isLessThanOrEqualTo(
				Money.of("CHF", 10.34)));
	}

	/**
	 * Test method for {@link org.javamoney.moneta.Money#isGreaterThan(javax.money.MonetaryAmount)}
	 * .
	 */
	@Test
	public void testIsGreaterThan() {
		assertFalse(Money.of("CHF", BigDecimal.valueOf(0d)).isGreaterThan(
				Money.of("CHF", BigDecimal.valueOf(0))));
		assertTrue(Money.of("CHF", BigDecimal.valueOf(0.00000000001d))
				.isGreaterThan(Money.of("CHF", BigDecimal.valueOf(0d))));
		assertTrue(Money.of("CHF", 15).isGreaterThan(
				Money.of("CHF", 10)));
		assertTrue(Money.of("CHF", 15.546).isGreaterThan(
				Money.of("CHF", 10.34)));
		assertFalse(Money.of("CHF", 5).isGreaterThan(
				Money.of("CHF", 10)));
		assertFalse(Money.of("CHF", 5.546).isGreaterThan(
				Money.of("CHF", 10.34)));
	}

	/**
	 * Test method for
	 * {@link org.javamoney.moneta.Money#isGreaterThanOrEqualTo(javax.money.MonetaryAmount)} .
	 */
	@Test
	public void testIsGreaterThanOrEqualTo() {
		assertTrue(Money.of("CHF", BigDecimal.valueOf(0d))
				.isGreaterThanOrEqualTo(
						Money.of("CHF", BigDecimal.valueOf(0))));
		assertTrue(Money
				.of("CHF", BigDecimal.valueOf(0.00000000001d))
				.isGreaterThanOrEqualTo(
						Money.of("CHF", BigDecimal.valueOf(0d))));
		assertTrue(Money.of("CHF", 15).isGreaterThanOrEqualTo(
				Money.of("CHF", 10)));
		assertTrue(Money.of("CHF", 15.546).isGreaterThanOrEqualTo(
				Money.of("CHF", 10.34)));
		assertFalse(Money.of("CHF", 5).isGreaterThanOrEqualTo(
				Money.of("CHF", 10)));
		assertFalse(Money.of("CHF", 5.546).isGreaterThanOrEqualTo(
				Money.of("CHF", 10.34)));
	}

	/**
	 * Test method for {@link org.javamoney.moneta.Money#isEqualTo(javax.money.MonetaryAmount)}.
	 */
	@Test
	public void testIsEqualTo() {
		assertTrue(Money.of("CHF", BigDecimal.valueOf(0d)).isEqualTo(
				Money.of("CHF", BigDecimal.valueOf(0))));
		assertFalse(Money.of("CHF", BigDecimal.valueOf(0.00000000001d))
				.isEqualTo(Money.of("CHF", BigDecimal.valueOf(0d))));
		assertTrue(Money.of("CHF", BigDecimal.valueOf(5d)).isEqualTo(
				Money.of("CHF", BigDecimal.valueOf(5))));
		assertTrue(Money.of("CHF", BigDecimal.valueOf(1d)).isEqualTo(
				Money.of("CHF", BigDecimal.valueOf(1.00))));
		assertTrue(Money.of("CHF", BigDecimal.valueOf(1d)).isEqualTo(
				Money.of("CHF", BigDecimal.ONE)));
		assertTrue(Money.of("CHF", BigDecimal.valueOf(1)).isEqualTo(
				Money.of("CHF", BigDecimal.ONE)));
		assertTrue(Money.of("CHF", new BigDecimal("1.0000")).isEqualTo(
				Money.of("CHF", new BigDecimal("1.00"))));
		// Test with different scales, but numeric equal values
		assertTrue(Money.of("CHF", BigDecimal.valueOf(0d)).isEqualTo(
				Money.of("CHF", BigDecimal.valueOf(0))));
		assertTrue(Money.of("CHF", BigDecimal.ZERO).isEqualTo(
				Money.of("CHF", BigDecimal.valueOf(0))));
		assertTrue(Money.of("CHF", BigDecimal.valueOf(5)).isEqualTo(
				Money.of("CHF", new BigDecimal("5.0"))));
		assertTrue(Money.of("CHF", BigDecimal.valueOf(5)).isEqualTo(
				Money.of("CHF", new BigDecimal("5.00"))));
		assertTrue(Money.of("CHF", BigDecimal.valueOf(5)).isEqualTo(
				Money.of("CHF", new BigDecimal("5.000"))));
		assertTrue(Money.of("CHF", BigDecimal.valueOf(5)).isEqualTo(
				Money.of("CHF", new BigDecimal("5.0000"))));
		assertTrue(Money.of("CHF", new BigDecimal("-1.23")).isEqualTo(
				Money.of("CHF", new BigDecimal("-1.230"))));
		assertTrue(Money.of("CHF", new BigDecimal("-1.23")).isEqualTo(
				Money.of("CHF", new BigDecimal("-1.2300"))));
		assertTrue(Money.of("CHF", new BigDecimal("-1.23")).isEqualTo(
				Money.of("CHF", new BigDecimal("-1.23000"))));
		assertTrue(Money.of("CHF", new BigDecimal("-1.23")).isEqualTo(
				Money.of("CHF", new BigDecimal("-1.230000000000000000000"))));
	}

	/**
	 * Test method for {@link org.javamoney.moneta.Money#getNumberType()}.
	 */
	@Test
	public void testGetImplementationType() {
		assertEquals(Money.of("CHF", 0).getMonetaryContext()
				.getAmountType(),
				Money.class);
		assertEquals(Money.of("CHF", 0.34738746d).getMonetaryContext()
				.getAmountType(),
				Money.class);
		assertEquals(Money.of("CHF", 100034L).getMonetaryContext()
				.getAmountType(), Money.class);
	}

	/**
	 * Test method for {@link org.javamoney.moneta.Money#query(javax.money.MonetaryQuery)}.
	 */
	@Test
	public void testQuery() {
		MonetaryQuery<Integer> q = new MonetaryQuery<Integer>() {
			@Override
			public Integer queryFrom(MonetaryAmount amount) {
				return Money.from(amount).getPrecision();
			}
		};
		Money[] moneys = new Money[] {
				Money.of("CHF", 100),
				Money.of("USD", 34242344), Money.of("EUR", 23123213.435),
				Money.of("USS", -23123213.435), Money.of("USN", -23123213),
				Money.of("GBP", 0) };
		for (int i = 0; i < moneys.length; i++) {
			assertEquals((Integer) moneys[i].query(q),
					(Integer) moneys[i].getPrecision());
		}
	}

	/**
	 * Test method for {@link org.javamoney.moneta.Money#getNumber(java.lang.Class)}.
	 */
	@Test
	public void testAsTypeClassOfT() {
		Money m = Money.of("CHF", 13.656);
		assertEquals(m.getNumber().byteValue(),
				13);
		assertEquals(m.getNumber().shortValue(),
				13);
		assertEquals(m.getNumber().intValue(),
				13);
		assertEquals(m.getNumber().longValue(),
				13L);
		assertEquals(m.getNumber().floatValue(),
				13.656f, 0.0);
		assertEquals(m.getNumber().doubleValue(),
				13.656, 0.0);
		assertEquals(m.getNumber().numberValue(BigDecimal.class),
				BigDecimal.valueOf(13.656));
	}


	/**
	 * Test method for {@link org.javamoney.moneta.Money#stripTrailingZeros()}.
	 */
	@Test
	public void testStripTrailingZeroes() {
		assertEquals(BigDecimal.ZERO, Money.of("CHF", new BigDecimal("0.0"))
				.stripTrailingZeros().getNumber().numberValue(BigDecimal.class));
		assertEquals(BigDecimal.ZERO, Money.of("CHF", new BigDecimal("0.00"))
				.stripTrailingZeros().getNumber().numberValue(BigDecimal.class));
		assertEquals(BigDecimal.ZERO, Money
				.of("CHF", new BigDecimal("0.000"))
				.stripTrailingZeros().getNumber().numberValue(BigDecimal.class));
		assertEquals(new BigDecimal("12.123"),
				Money.of("CHF", new BigDecimal("12.123000"))
						.stripTrailingZeros().getNumber().numberValue(BigDecimal.class));
		assertEquals(new BigDecimal("12.123"),
				Money.of("CHF", new BigDecimal("12.12300"))
						.stripTrailingZeros().getNumber().numberValue(BigDecimal.class));
		assertEquals(new BigDecimal("12.123"),
				Money.of("CHF", new BigDecimal("12.1230"))
						.stripTrailingZeros()
						.getNumber().numberValue(BigDecimal.class));
		assertEquals(new BigDecimal("12.123"),
				Money.of("CHF", new BigDecimal("12.123"))
						.stripTrailingZeros()
						.getNumber().numberValue(BigDecimal.class));
	}

	/**
	 * Test method for {@link org.javamoney.moneta.Money#toString()}.
	 */
	@Test
	public void testToString() {
		assertEquals("XXX 1.23455645",
				Money.of("XXX", new BigDecimal("1.23455645"))
						.toString());
		assertEquals("CHF 1234", Money.of("CHF", 1234).toString());
		assertEquals("CHF 1234.0", Money.of("CHF", new BigDecimal("1234.0"))
				.toString());
		assertEquals("CHF 1234.1", Money.of("CHF", new BigDecimal("1234.1"))
				.toString());
		assertEquals("CHF 0.0100", Money.of("CHF", new BigDecimal("0.0100"))
				.toString());
	}

	/**
	 * Test method for {@link org.javamoney.moneta.Money#with(javax.money.MonetaryOperator)}.
	 */
	@Test
	public void testWithMonetaryOperator() {
		MonetaryOperator adj = new MonetaryOperator() {
			@SuppressWarnings("unchecked")
			@Override
			public MonetaryAmount apply(MonetaryAmount amount) {
				return Money.of(amount.getCurrency(), -100);
			}
		};
		Money m = Money.of("XXX", 1.23455645d);
		Money a = m.with(adj);
		assertNotNull(a);
		assertNotSame(m, a);
		assertEquals(m.getCurrency(), a.getCurrency());
		assertEquals(Money.of(m.getCurrency(), -100), a);
		adj = new MonetaryOperator() {
			@SuppressWarnings("unchecked")
			@Override
			public MonetaryAmount apply(MonetaryAmount amount) {
				return amount.multiply(2).getFactory()
						.with(MonetaryCurrencies.getCurrency("CHF"))
						.create();
			}
		};
		a = m.with(adj);
		assertNotNull(a);
		assertNotSame(m, a);
		assertEquals(MonetaryCurrencies.getCurrency("CHF"), a.getCurrency());
		assertEquals(Money.of(a.getCurrency(), 1.23455645d * 2), a);
	}

	/**
	 * Test method for {@link org.javamoney.moneta.Money#from(javax.money.MonetaryAmount)}.
	 */
	@Test
	public void testFrom() {
		Money m = Money.of("XXX", new BigDecimal("1.2345"));
		Money m2 = Money.from(m);
		assertTrue(m == m2);
	}

	@Test
	public void testSerialization() throws IOException, ClassNotFoundException {
		Money m = Money.of("XXX", new BigDecimal("1.2345"));
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		ObjectOutputStream oos = new ObjectOutputStream(bos);
		oos.writeObject(m);
		oos.flush();
		ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(
				bos.toByteArray()));
		Money m2 = (Money) ois.readObject();
		assertEquals(m, m2);
		assertTrue(m != m2);
	}

	// Bad cases

	/**
	 * Test method for {@link org.javamoney.moneta.RoundedMoney#add(javax.money.MonetaryAmount)} .
	 */
	@Test(expected = IllegalArgumentException.class)
	public void testAdd_WrongCurrency() {
		Money m1 = Money.of(EURO, BigDecimal.TEN);
		Money m2 = Money.of("CHF", BigDecimal.TEN);
		m1.add(m2);
	}

	/**
	 * Test method for {@link org.javamoney.moneta.RoundedMoney#add(javax.money.MonetaryAmount)} .
	 */
	@Test(expected = IllegalArgumentException.class)
	public void testSubtract_WrongCurrency() {
		Money m1 = Money.of(EURO, BigDecimal.TEN);
		Money m2 = Money.of("CHF", BigDecimal.TEN);
		m1.subtract(m2);
	}

	/**
	 * Test method for {@link org.javamoney.moneta.RoundedMoney#add(javax.money.MonetaryAmount)} .
	 */
	@Test(expected = IllegalArgumentException.class)
	public void testDivide_WrongCurrency() {
		Money m1 = Money.of(EURO, BigDecimal.TEN);
		Money m2 = Money.of("CHF", BigDecimal.TEN);
		m1.subtract(m2);
	}
}
