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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.math.BigDecimal;
import java.math.BigInteger;

import javax.money.CurrencyUnit;
import javax.money.MonetaryAmount;
import javax.money.MonetaryCurrencies;
import javax.money.MonetaryOperator;
import javax.money.MonetaryQuery;

import org.junit.Test;

/**
 * @author Anatole
 * 
 */
public class FastMoneyTest {

	private static final BigDecimal TEN = new BigDecimal(10.0d);
	protected static final CurrencyUnit EURO = MonetaryCurrencies
			.getCurrency("EUR");
	protected static final CurrencyUnit DOLLAR = MonetaryCurrencies
			.getCurrency("USD");

	/**
	 * Test method for
	 * {@link org.javamoney.moneta.FastMoney#of(javax.money.CurrencyUnit, java.math.BigDecimal)} .
	 */
	@Test
	public void testOfCurrencyUnitBigDecimal() {
		FastMoney m = FastMoney.of(MonetaryCurrencies.getCurrency("EUR"), TEN);
		assertEquals(new BigDecimal("10"),
				m.getNumber().numberValue(BigDecimal.class));
	}

	@Test
	public void testOfCurrencyUnitDouble() {
		FastMoney m = FastMoney
				.of(MonetaryCurrencies.getCurrency("EUR"), 10.0d);
		assertTrue(TEN.doubleValue() == m.getNumber().doubleValue());
	}

	/**
	 * Test method for {@link org.javamoney.moneta.FastMoney#getCurrency()}.
	 */
	@Test
	public void testGetCurrency() {
		MonetaryAmount money = FastMoney.of(EURO, BigDecimal.TEN);
		assertNotNull(money.getCurrency());
		assertEquals("EUR", money.getCurrency().getCurrencyCode());
	}

	@Test
	public void testSubtractMonetaryAmount() {
		FastMoney money1 = FastMoney.of(EURO, BigDecimal.TEN);
		FastMoney money2 = FastMoney.of(EURO, BigDecimal.ONE);
		FastMoney moneyResult = money1.subtract(money2);
		assertNotNull(moneyResult);
		assertEquals(9d, moneyResult.getNumber().doubleValue(), 0d);
	}

	@Test
	public void testDivideAndRemainder_BigDecimal() {
		FastMoney money1 = FastMoney.of(EURO, BigDecimal.ONE);
		FastMoney[] divideAndRemainder = money1
				.divideAndRemainder(new BigDecimal(
						"0.50001"));
		assertEquals(
				divideAndRemainder[0].getNumber().numberValue(BigDecimal.class),
				new BigDecimal("1"));
		assertEquals(
				divideAndRemainder[1].getNumber().numberValue(BigDecimal.class),
				new BigDecimal("0.49999"));
	}

	@Test
	public void testDivideToIntegralValue_BigDecimal() {
		FastMoney money1 = FastMoney.of(EURO, BigDecimal.ONE);
		FastMoney result = money1.divideToIntegralValue(new BigDecimal(
				"0.5001"));
		assertEquals(result.getNumber().numberValue(BigDecimal.class),
				new BigDecimal(
						"1.0"));
	}

	

	/**
	 * Test method for {@link org.javamoney.moneta.FastMoney#hashCode()}.
	 */
	@Test
	public void testHashCode() {
		FastMoney money1 = FastMoney.of(EURO, BigDecimal.ONE);
		FastMoney money2 = FastMoney.of(EURO, new BigDecimal("1"));
		assertEquals(money1.hashCode(), money2.hashCode());
		FastMoney money3 = FastMoney.of(DOLLAR, 1.0);
		assertTrue(money1.hashCode() != money3.hashCode());
		assertTrue(money2.hashCode() != money3.hashCode());
		FastMoney money4 = FastMoney.of(DOLLAR, BigDecimal.ONE);
		assertTrue(money1.hashCode() != money4.hashCode());
		assertTrue(money2.hashCode() != money4.hashCode());
		FastMoney money5 = FastMoney.of(DOLLAR, BigDecimal.ONE);
		FastMoney money6 = FastMoney.of(DOLLAR, 1.0);
		assertTrue(money1.hashCode() != money5.hashCode());
		assertTrue(money2.hashCode() != money5.hashCode());
		assertTrue(money1.hashCode() != money6.hashCode());
		assertTrue(money2.hashCode() != money6.hashCode());
	}

	/**
	 * Test method for
	 * {@link org.javamoney.moneta.FastMoney#of(javax.money.CurrencyUnit, java.lang.Number)} .
	 */
	@Test
	public void testOfCurrencyUnitNumber() {
		FastMoney m = FastMoney.of(EURO, (byte) 2);
		assertNotNull(m);
		assertEquals(EURO, m.getCurrency());
		assertEquals(Byte.valueOf((byte) 2),
				m.getNumber().numberValue(Byte.class));
		m = FastMoney.of(DOLLAR, (short) -2);
		assertNotNull(m);
		assertEquals(DOLLAR, m.getCurrency());
		assertEquals(Short.valueOf((short) -2),
				m.getNumber().numberValue(Short.class));
		m = FastMoney.of(EURO, (int) -12);
		assertNotNull(m);
		assertEquals(EURO, m.getCurrency());
		assertEquals(Integer.valueOf((int) -12),
				m.getNumber().numberValue(Integer.class));
		m = FastMoney.of(DOLLAR, (long) 12);
		assertNotNull(m);
		assertEquals(DOLLAR, m.getCurrency());
		assertEquals(Long.valueOf((long) 12),
				m.getNumber().numberValue(Long.class));
		m = FastMoney.of(EURO, (float) 12.23);
		assertNotNull(m);
		assertEquals(EURO, m.getCurrency());
		assertEquals(Float.valueOf((float) 12.23),
				m.getNumber().numberValue(Float.class));
		m = FastMoney.of(DOLLAR, (double) -12.23);
		assertNotNull(m);
		assertEquals(DOLLAR, m.getCurrency());
		assertEquals(Double.valueOf((double) -12.23), m.getNumber()
				.numberValue(Double.class));
		m = FastMoney.of(EURO, (Number) BigDecimal.valueOf(234.2345));
		assertNotNull(m);
		assertEquals(EURO, m.getCurrency());
		assertEquals(new BigDecimal("234.2345"),
				m.getNumber().numberValue(BigDecimal.class));
		m = FastMoney.of(DOLLAR, (Number) BigInteger.valueOf(232323123L));
		assertNotNull(m);
		assertEquals(DOLLAR, m.getCurrency());
		assertEquals(Long.valueOf(232323123L),
				m.getNumber().numberValue(Long.class));
		assertEquals(BigInteger.valueOf(232323123L),
				m.getNumber().numberValue(BigInteger.class));
	}

	/**
	 * Test method for {@link org.javamoney.moneta.FastMoney#of(java.lang.String, java.lang.Number)}
	 * .
	 */
	@Test
	public void testOfStringNumber() {
		FastMoney m = FastMoney.of("EUR", (byte) 2);
		assertNotNull(m);
		assertEquals(EURO, m.getCurrency());
		assertEquals(Byte.valueOf((byte) 2),
				m.getNumber().numberValue(Byte.class));
		m = FastMoney.of("USD", (short) -2);
		assertNotNull(m);
		assertEquals(DOLLAR, m.getCurrency());
		assertEquals(Short.valueOf((short) -2),
				m.getNumber().numberValue(Short.class));
		m = FastMoney.of("EUR", (int) -12);
		assertNotNull(m);
		assertEquals(EURO, m.getCurrency());
		assertEquals(Integer.valueOf((int) -12),
				m.getNumber().numberValue(Integer.class));
		m = FastMoney.of("USD", (long) 12);
		assertNotNull(m);
		assertEquals(DOLLAR, m.getCurrency());
		assertEquals(Long.valueOf((long) 12),
				m.getNumber().numberValue(Long.class));
		m = FastMoney.of("EUR", (float) 12.23);
		assertNotNull(m);
		assertEquals(EURO, m.getCurrency());
		assertEquals(Float.valueOf((float) 12.23),
				m.getNumber().numberValue(Float.class));
		m = FastMoney.of("USD", (double) -12.23);
		assertNotNull(m);
		assertEquals(DOLLAR, m.getCurrency());
		assertEquals(Double.valueOf((double) -12.23), m.getNumber()
				.numberValue(Double.class));
		m = FastMoney.of("EUR", (Number) BigDecimal.valueOf(234.2345));
		assertNotNull(m);
		assertEquals(EURO, m.getCurrency());
		assertEquals(new BigDecimal("234.2345"),
				m.getNumber().numberValue(BigDecimal.class));
		m = FastMoney.of("USD", (Number) BigInteger.valueOf(21432432L));
		assertNotNull(m);
		assertEquals(DOLLAR, m.getCurrency());
		assertEquals(Long.valueOf(21432432L),
				m.getNumber().numberValue(Long.class));
		assertEquals(BigInteger.valueOf(21432432L),
				m.getNumber().numberValue(BigInteger.class));
	}

	/**
	 * Test method for {@link org.javamoney.moneta.FastMoney#equals(java.lang.Object)}.
	 */
	@Test
	public void testEqualsObject() {
		FastMoney[] moneys = new FastMoney[] {
				FastMoney.of("CHF", BigDecimal.ZERO),
				FastMoney.of("CHF", BigDecimal.ONE),
				FastMoney.of("XXX", BigDecimal.ONE),
				FastMoney.of("XXX", BigDecimal.ONE.negate())
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
	}

	/**
	 * Test method for {@link org.javamoney.moneta.FastMoney#compareTo(javax.money.MonetaryAmount)}
	 * .
	 */
	@Test
	public void testCompareTo() {
		FastMoney m1 = FastMoney.of("CHF", -2);
		FastMoney m2 = FastMoney.of("CHF", 0);
		FastMoney m3 = FastMoney.of("CHF", -0);
		FastMoney m4 = FastMoney.of("CHF", 2);
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
	 * Test method for {@link org.javamoney.moneta.FastMoney#abs()}.
	 */
	@Test
	public void testAbs() {
		FastMoney m = FastMoney.of("CHF", 10);
		assertEquals(m, m.abs());
		assertTrue(m == m.abs());
		m = FastMoney.of("CHF", 0);
		assertEquals(m, m.abs());
		assertTrue(m == m.abs());
		m = FastMoney.of("CHF", -10);
		assertEquals(m.negate(), m.abs());
		assertTrue(m != m.abs());
	}

	/**
	 * Test method for {@link org.javamoney.moneta.FastMoney#add(javax.money.MonetaryAmount)} .
	 */
	@Test
	public void testAdd() {
		FastMoney money1 = FastMoney.of(EURO, BigDecimal.TEN);
		FastMoney money2 = FastMoney.of(EURO, BigDecimal.ONE);
		FastMoney moneyResult = money1.add(money2);
		assertNotNull(moneyResult);
		assertEquals(11d, moneyResult.getNumber().doubleValue(), 0d);
	}

	/**
	 * Test method for {@link org.javamoney.moneta.FastMoney#divide(java.lang.Number)}.
	 */
	@Test
	public void testDivideNumber() {
		FastMoney m = FastMoney.of("CHF", 100);
		assertEquals(
				FastMoney.of("CHF",
						BigDecimal.valueOf(100).divide(BigDecimal.valueOf(5))),
				m.divide(BigDecimal.valueOf(5)));
	}

	/**
	 * Test method for {@link org.javamoney.moneta.FastMoney#divideAndRemainder(java.lang.Number)} .
	 */
	@Test
	public void testDivideAndRemainderNumber() {
		FastMoney m = FastMoney.of("CHF", 100);
		assertEquals(
				FastMoney.of(
						"CHF",
						BigDecimal.valueOf(33)),
				m.divideAndRemainder(
						BigDecimal.valueOf(3))[0]);
		assertEquals(
				FastMoney.of(
						"CHF",
						BigDecimal.valueOf(1)),
				m.divideAndRemainder(
						BigDecimal.valueOf(3))[1]);
	}

	/**
	 * Test method for
	 * {@link org.javamoney.moneta.FastMoney#divideToIntegralValue(java.lang.Number)} .
	 */
	@Test
	public void testDivideToIntegralValueNumber() {
		FastMoney m = FastMoney.of("CHF", 100);
		assertEquals(
				FastMoney.of(
						"CHF",
						BigDecimal.valueOf(5)),
				m.divideToIntegralValue(
						BigDecimal.valueOf(20)));
		assertEquals(
				FastMoney.of(
						"CHF",
						BigDecimal.valueOf(33)),
				m.divideToIntegralValue(
						BigDecimal.valueOf(3)));
	}

	/**
	 * Test method for {@link org.javamoney.moneta.FastMoney#multiply(java.lang.Number)}.
	 */
	@Test
	public void testMultiplyNumber() {
		FastMoney m = FastMoney.of("CHF", 100);
		assertEquals(FastMoney.of("CHF", 400), m.multiply(4));
		assertEquals(FastMoney.of("CHF", 200), m.multiply(2));
		assertEquals(FastMoney.of("CHF", new BigDecimal("50.0")),
				m.multiply(0.5));
	}

	/**
	 * Test method for {@link org.javamoney.moneta.FastMoney#negate()}.
	 */
	@Test
	public void testNegate() {
		FastMoney m = FastMoney.of("CHF", 100);
		assertEquals(FastMoney.of("CHF", -100), m.negate());
		m = FastMoney.of("CHF", -123.234);
		assertEquals(FastMoney.of("CHF", 123.234), m.negate());
	}

	/**
	 * Test method for {@link org.javamoney.moneta.FastMoney#plus()}.
	 */
	@Test
	public void testPlus() {
		FastMoney m = FastMoney.of("CHF", 100);
		assertEquals(FastMoney.of("CHF", 100), m.plus());
		m = FastMoney.of("CHF", 123.234);
		assertEquals(FastMoney.of("CHF", 123.234), m.plus());
	}

	/**
	 * Test method for {@link org.javamoney.moneta.FastMoney#subtract(javax.money.MonetaryAmount)} .
	 */
	@Test
	public void testSubtract() {
		FastMoney m = FastMoney.of("CHF", 100);
		FastMoney s1 = FastMoney.of("CHF", 100);
		FastMoney s2 = FastMoney.of("CHF", 200);
		FastMoney s3 = FastMoney.of("CHF", 0);
		assertEquals(FastMoney.of("CHF", 0), m.subtract(s1));
		assertEquals(FastMoney.of("CHF", -100), m.subtract(s2));
		assertEquals(FastMoney.of("CHF", 100), m.subtract(s3));
		assertTrue(m == m.subtract(s3));
		m = FastMoney.of("CHF", -123.234);
		assertEquals(FastMoney.of("CHF", new BigDecimal("-223.234")),
				m.subtract(s1));
		assertEquals(FastMoney.of("CHF", new BigDecimal("-323.234")),
				m.subtract(s2));
		assertEquals(FastMoney.of("CHF", new BigDecimal("-123.234")),
				m.subtract(s3));
		assertTrue(m == m.subtract(s3));
		m = FastMoney.of("CHF", 12.40234);
		s1 = FastMoney.of("CHF", 2343.45);
		s2 = FastMoney.of("CHF", 12.40234);
		s3 = FastMoney.of("CHF", -2343.45);
		assertEquals(FastMoney.of("CHF", new BigDecimal("12.40234")
				.subtract(new BigDecimal("2343.45"))), m.subtract(s1));
		assertEquals(FastMoney.of("CHF", new BigDecimal("12.402345534")
				.subtract(new BigDecimal("12.402345534"))),
				m.subtract(s2));
		assertEquals(FastMoney.of("CHF", 0), m.subtract(s2));
		assertEquals(FastMoney.of("CHF", new BigDecimal("2355.852345534")),
				m.subtract(s3));
		assertTrue(m == m.subtract(FastMoney.of("CHF", 0)));
	}

	/**
	 * Test method for {@link org.javamoney.moneta.FastMoney#remainder(java.lang.Number)} .
	 */
	@Test
	public void testRemainderNumber() {
		FastMoney[] moneys = new FastMoney[] { FastMoney.of("CHF", 100),
				FastMoney.of("CHF", 34242344),
				FastMoney.of("CHF", 23123213.435),
				FastMoney.of("CHF", 0), FastMoney.of("CHF", -100),
				FastMoney.of("CHF", -723527.36532) };
		for (FastMoney m : moneys) {
			assertEquals(
					"Invalid remainder of " + 10.50,
					m.getFactory()
							.with(m.getNumber().numberValue(BigDecimal.class)
									.remainder(
											BigDecimal.valueOf(10.50)))
							.create(),
					m.remainder(10.50));
			assertEquals(
					"Invalid remainder of " + -30.20,
					m.getFactory()
							.with(m.getNumber().numberValue(BigDecimal.class)
									.remainder(
											BigDecimal.valueOf(-30.20)))
							.create(),
					m.remainder(-30.20));
			assertEquals(
					"Invalid remainder of " + -3,
					m.getFactory()
							.with(m.getNumber().numberValue(BigDecimal.class)
									.remainder(
											BigDecimal.valueOf(-3))).create(),
					m.remainder(-3));
			assertEquals(
					"Invalid remainder of " + 3,
					m.getFactory()
							.with(m.getNumber().numberValue(BigDecimal.class)
									.remainder(
											BigDecimal.valueOf(3))).create(),
					m.remainder(3));
		}
	}

	/**
	 * Test method for {@link org.javamoney.moneta.FastMoney#scaleByPowerOfTen(int)} .
	 */
	@Test
	public void testScaleByPowerOfTen() {
		FastMoney[] moneys = new FastMoney[] { FastMoney.of("CHF", 100),
				FastMoney.of("CHF", 34242344),
				FastMoney.of("CHF", 23123213.435),
				FastMoney.of("CHF", 0), FastMoney.of("CHF", -100),
				FastMoney.of("CHF", -723527.36532) };
		for (FastMoney m : moneys) {
			for (int p = -10; p < 10; p++) {
				assertEquals(
						"Invalid scaleByPowerOfTen.",
						m.getFactory()
								.with(m.getNumber()
										.numberValue(BigDecimal.class)
										.scaleByPowerOfTen(
												p)).create(),
						m.scaleByPowerOfTen(p));
			}
		}
	}

	/**
	 * Test method for {@link org.javamoney.moneta.FastMoney#isZero()}.
	 */
	@Test
	public void testIsZero() {
		FastMoney[] moneys = new FastMoney[] { FastMoney.of("CHF", 100),
				FastMoney.of("CHF", 34242344),
				FastMoney.of("CHF", 23123213.435),
				FastMoney.of("CHF", -100),
				FastMoney.of("CHF", -723527.36532) };
		for (FastMoney m : moneys) {
			assertFalse(m.isZero());
		}
		moneys = new FastMoney[] { FastMoney.of("CHF", 0),
				FastMoney.of("CHF", 0.0), FastMoney.of("CHF", BigDecimal.ZERO),
				FastMoney.of("CHF", new BigDecimal("0.00000000000000000")) };
		for (FastMoney m : moneys) {
			assertTrue(m.isZero());
		}
	}

	/**
	 * Test method for {@link org.javamoney.moneta.FastMoney#isPositive()}.
	 */
	@Test
	public void testIsPositive() {
		FastMoney[] moneys = new FastMoney[] { FastMoney.of("CHF", 100),
				FastMoney.of("CHF", 34242344),
				FastMoney.of("CHF", 23123213.435) };
		for (FastMoney m : moneys) {
			assertTrue(m.isPositive());
		}
		moneys = new FastMoney[] { FastMoney.of("CHF", 0),
				FastMoney.of("CHF", 0.0), FastMoney.of("CHF", BigDecimal.ZERO),
				FastMoney.of("CHF", new BigDecimal("0.00000000000000000")),
				FastMoney.of("CHF", -100),
				FastMoney.of("CHF", -34242344),
				FastMoney.of("CHF", -23123213.435) };
		for (FastMoney m : moneys) {
			assertFalse(m.isPositive());
		}
	}

	/**
	 * Test method for {@link org.javamoney.moneta.FastMoney#isPositiveOrZero()} .
	 */
	@Test
	public void testIsPositiveOrZero() {
		FastMoney[] moneys = new FastMoney[] { FastMoney.of("CHF", 0),
				FastMoney.of("CHF", 0.0), FastMoney.of("CHF", BigDecimal.ZERO),
				FastMoney.of("CHF", new BigDecimal("0.00000000000000000")),
				FastMoney.of("CHF", 100),
				FastMoney.of("CHF", 34242344),
				FastMoney.of("CHF", 23123213.435) };
		for (FastMoney m : moneys) {
			assertTrue("Invalid positiveOrZero (expected true): " + m,
					m.isPositiveOrZero());
		}
		moneys = new FastMoney[] {
				FastMoney.of("CHF", -100),
				FastMoney.of("CHF", -34242344),
				FastMoney.of("CHF", -23123213.435) };
		for (FastMoney m : moneys) {
			assertFalse("Invalid positiveOrZero (expected false): " + m,
					m.isPositiveOrZero());
		}
	}

	/**
	 * Test method for {@link org.javamoney.moneta.FastMoney#isNegative()}.
	 */
	@Test
	public void testIsNegative() {
		FastMoney[] moneys = new FastMoney[] { FastMoney.of("CHF", 0),
				FastMoney.of("CHF", 0.0), FastMoney.of("CHF", BigDecimal.ZERO),
				FastMoney.of("CHF", new BigDecimal("0.00000000000000000")),
				FastMoney.of("CHF", 100),
				FastMoney.of("CHF", 34242344),
				FastMoney.of("CHF", 23123213.435) };
		for (FastMoney m : moneys) {
			assertFalse("Invalid isNegative (expected false): " + m,
					m.isNegative());
		}
		moneys = new FastMoney[] {
				FastMoney.of("CHF", -100),
				FastMoney.of("CHF", -34242344),
				FastMoney.of("CHF", -23123213.435) };
		for (FastMoney m : moneys) {
			assertTrue("Invalid isNegative (expected true): " + m,
					m.isNegative());
		}
	}

	/**
	 * Test method for {@link org.javamoney.moneta.FastMoney#isNegativeOrZero()} .
	 */
	@Test
	public void testIsNegativeOrZero() {
		FastMoney[] moneys = new FastMoney[] {
				FastMoney.of("CHF", 100),
				FastMoney.of("CHF", 34242344),
				FastMoney.of("CHF", 23123213.435) };
		for (FastMoney m : moneys) {
			assertFalse("Invalid negativeOrZero (expected false): " + m,
					m.isNegativeOrZero());
		}
		moneys = new FastMoney[] { FastMoney.of("CHF", 0),
				FastMoney.of("CHF", 0.0), FastMoney.of("CHF", BigDecimal.ZERO),
				FastMoney.of("CHF", new BigDecimal("0.00000000000000000")),
				FastMoney.of("CHF", -100),
				FastMoney.of("CHF", -34242344),
				FastMoney.of("CHF", -23123213.435) };
		for (FastMoney m : moneys) {
			assertTrue("Invalid negativeOrZero (expected true): " + m,
					m.isNegativeOrZero());
		}
	}

	/**
	 * Test method for {@link org.javamoney.moneta.FastMoney#with(java.lang.Number)} .
	 */
	@Test
	public void testWithNumber() {
		FastMoney[] moneys = new FastMoney[] {
				FastMoney.of("CHF", 100),
				FastMoney.of("CHF", 34242344),
				FastMoney.of("CHF", new BigDecimal("23123213.435")),
				FastMoney.of("CHF", new BigDecimal("-23123213.435")),
				FastMoney.of("CHF", -23123213),
				FastMoney.of("CHF", 0) };
		FastMoney s = FastMoney.of("CHF", 10);
		MonetaryAmount[] moneys2 = new MonetaryAmount[] {
				s.getFactory().with(100).create(),
				s.getFactory().with(34242344).create(),
				s.getFactory().with(new BigDecimal("23123213.435")).create(),
				s.getFactory().with(new BigDecimal("-23123213.435")).create(),
				s.getFactory().with(-23123213).create(),
				s.getFactory().with(0).create() };
		for (int i = 0; i < moneys.length; i++) {
			assertEquals("with(Number) failed.", moneys[i], moneys2[i]);
		}
	}

	/**
	 * Test method for
	 * {@link org.javamoney.moneta.FastMoney#with(javax.money.CurrencyUnit, java.lang.Number)} .
	 */
	@Test
	public void testWithCurrencyUnitNumber() {
		FastMoney[] moneys = new FastMoney[] {
				FastMoney.of("CHF", 100),
				FastMoney.of("USD", 34242344),
				FastMoney.of("EUR", 23123213.435),
				FastMoney.of("USS", -23123213.435),
				FastMoney.of("USN", -23123213),
				FastMoney.of("GBP", 0) };
		FastMoney s = FastMoney.of("XXX", 10);
		MonetaryAmount[] moneys2 = new MonetaryAmount[] {
				s.getFactory().with(MonetaryCurrencies.getCurrency("CHF")).with(100).create(),
				s.getFactory().with(MonetaryCurrencies.getCurrency("USD")).with(34242344).create(),
				s.getFactory().with(MonetaryCurrencies.getCurrency("EUR")).with(new BigDecimal(
						"23123213.435")).create(),
				s.getFactory().with(MonetaryCurrencies.getCurrency("USS")).with(new BigDecimal(
						"-23123213.435")).create(),
				s.getFactory().with(MonetaryCurrencies.getCurrency("USN")).with(-23123213).create(),
				s.getFactory().with(MonetaryCurrencies.getCurrency("GBP")).with(0).create() };
		for (int i = 0; i < moneys.length; i++) {
			assertEquals("with(Number) failed.", moneys[i], moneys2[i]);
		}
	}

	/**
	 * Test method for {@link org.javamoney.moneta.FastMoney#getScale()}.
	 */
	@Test
	public void testGetScale() {
		FastMoney[] moneys = new FastMoney[] {
				FastMoney.of("CHF", 100),
				FastMoney.of("USD", 34242344),
				FastMoney.of("EUR", 23123213.435),
				FastMoney.of("USS", -23123213.435),
				FastMoney.of("USN", -23123213),
				FastMoney.of("GBP", 0) };
		for (FastMoney m : moneys) {
			assertEquals("Scale for " + m,
					5,
					m.getScale());
		}
	}

	/**
	 * Test method for {@link org.javamoney.moneta.FastMoney#getPrecision()}.
	 */
	@Test
	public void testGetPrecision() {
		FastMoney[] moneys = new FastMoney[] {
				FastMoney.of("CHF", 111),
				FastMoney.of("USD", 34242344),
				FastMoney.of("EUR", 23123213.435),
				FastMoney.of("USS", -23123213.435),
				FastMoney.of("USN", -23123213),
				FastMoney.of("GBP", 0) };
		for (FastMoney m : moneys) {
			assertEquals("Precision for " + m,
					m.getNumber().numberValue(BigDecimal.class)
							.precision(),
					m.getPrecision());
		}
	}

	/**
	 * Test method for {@link org.javamoney.moneta.FastMoney#longValue()}.
	 */
	@Test
	public void testLongValue() {
		FastMoney m = FastMoney.of("CHF", 100);
		assertEquals("longValue of " + m, 100L, m.getNumber().longValue());
		m = FastMoney.of("CHF", -100);
		assertEquals("longValue of " + m, -100L, m.getNumber().longValue());
		m = FastMoney.of("CHF", -100.3434);
		assertEquals("longValue of " + m, -100L, m.getNumber().longValue());
		m = FastMoney.of("CHF", 100.3434);
		assertEquals("longValue of " + m, 100L, m.getNumber().longValue());
		m = FastMoney.of("CHF", 0);
		assertEquals("longValue of " + m, 0L, m.getNumber().longValue());
		m = FastMoney.of("CHF", -0.0);
		assertEquals("longValue of " + m, 0L, m.getNumber().longValue());
		// try {
		m = FastMoney
				.of("CHF",
						new BigDecimal(
								"12121762517652176251725178251872652765321876352187635217835378125"));
		m.getNumber().longValue();
		// fail("longValue(12121762517652176251725178251872652765321876352187635217835378125) should fail!");
		// } catch (ArithmeticException e) {
		// // OK
		// }
	}

	/**
	 * Test method for {@link org.javamoney.moneta.FastMoney#longValueExact()}.
	 */
	@Test
	public void testLongValueExact() {
		FastMoney m = FastMoney.of("CHF", 100);
		assertEquals("longValue of " + m, 100L, m.getNumber().longValueExact());
		m = FastMoney.of("CHF", -100);
		assertEquals("longValue of " + m, -100L, m.getNumber().longValueExact());
		m = FastMoney.of("CHF", 0);
		assertEquals("longValue of " + m, 0L, m.getNumber().longValueExact());
		m = FastMoney.of("CHF", -0.0);
		assertEquals("longValue of " + m, 0L, m.getNumber().longValueExact());
		m = FastMoney.of("CHF", Long.MAX_VALUE);
		try {
			m = FastMoney
					.of("CHF",
							new BigDecimal(
									"12121762517652176251725178251872652765321876352187635217835378125"));
			m.getNumber().longValueExact();
			fail("longValueExact(12121762517652176251725178251872652765321876352187635217835378125) should fail!");
		} catch (ArithmeticException e) {
			// OK
		}
		try {
			m = FastMoney.of("CHF", -100.3434);
			m.getNumber().longValueExact();
			fail("longValueExact(-100.3434) should raise an ArithmeticException.");
		} catch (ArithmeticException e) {
			// OK
		}
		try {
			m = FastMoney.of("CHF", 100.3434);
			m.getNumber().longValueExact();
			fail("longValueExact(100.3434) should raise an ArithmeticException.");
		} catch (ArithmeticException e) {
			// OK
		}
	}

	/**
	 * Test method for {@link org.javamoney.moneta.FastMoney#doubleValue()}.
	 */
	@Test
	public void testDoubleValue() {
		FastMoney m = FastMoney.of("CHF", 100);
		assertEquals("doubleValue of " + m, 100d, m.getNumber().doubleValue(), 0.0d);
		m = FastMoney.of("CHF", -100);
		assertEquals("doubleValue of " + m, -100d, m.getNumber().doubleValue(), 0.0d);
		m = FastMoney.of("CHF", -100.3434);
		assertEquals("doubleValue of " + m, -100.3434, m.getNumber().doubleValue(), 0.0d);
		m = FastMoney.of("CHF", 100.3434);
		assertEquals("doubleValue of " + m, 100.3434, m.getNumber().doubleValue(), 0.0d);
		m = FastMoney.of("CHF", 0);
		assertEquals("doubleValue of " + m, 0d, m.getNumber().doubleValue(), 0.0d);
		m = FastMoney.of("CHF", -0.0);
		assertEquals("doubleValue of " + m, 0d, m.getNumber().doubleValue(), 0.0d);
		// try {
		m = FastMoney
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
	 * Test method for {@link org.javamoney.moneta.FastMoney#signum()}.
	 */
	@Test
	public void testSignum() {
		FastMoney m = FastMoney.of("CHF", 100);
		assertEquals("signum of " + m, 1, m.signum());
		m = FastMoney.of("CHF", -100);
		assertEquals("signum of " + m, -1, m.signum());
		m = FastMoney.of("CHF", 100.3435);
		assertEquals("signum of " + m, 1, m.signum());
		m = FastMoney.of("CHF", -100.3435);
		assertEquals("signum of " + m, -1, m.signum());
		m = FastMoney.of("CHF", 0);
		assertEquals("signum of " + m, 0, m.signum());
		m = FastMoney.of("CHF", -0);
		assertEquals("signum of " + m, 0, m.signum());
	}


	/**
	 * Test method for {@link org.javamoney.moneta.FastMoney#isLessThan(javax.money.MonetaryAmount)}
	 * .
	 */
	@Test
	public void testIsLessThan() {
		assertFalse(FastMoney.of("CHF", BigDecimal.valueOf(0d)).isLessThan(
				FastMoney.of("CHF", BigDecimal.valueOf(0))));
		assertFalse(FastMoney.of("CHF", BigDecimal.valueOf(0.00000000001d))
				.isLessThan(FastMoney.of("CHF", BigDecimal.valueOf(0d))));
		assertFalse(FastMoney.of("CHF", 15).isLessThan(
				FastMoney.of("CHF", 10)));
		assertFalse(FastMoney.of("CHF", 15.546).isLessThan(
				FastMoney.of("CHF", 10.34)));
		assertTrue(FastMoney.of("CHF", 5).isLessThan(
				FastMoney.of("CHF", 10)));
		assertTrue(FastMoney.of("CHF", 5.546).isLessThan(
				FastMoney.of("CHF", 10.34)));
	}

	/**
	 * Test method for
	 * {@link org.javamoney.moneta.FastMoney#isLessThanOrEqualTo(javax.money.MonetaryAmount)} .
	 */
	@Test
	public void testIsLessThanOrEqualTo() {
		assertTrue(FastMoney.of("CHF", BigDecimal.valueOf(0d))
				.isLessThanOrEqualTo(
						FastMoney.of("CHF", BigDecimal.valueOf(0))));
		assertFalse(FastMoney.of("CHF", BigDecimal.valueOf(0.00001d))
				.isLessThanOrEqualTo(
						FastMoney.of("CHF", BigDecimal.valueOf(0d))));
		assertFalse(FastMoney.of("CHF", 15).isLessThanOrEqualTo(
				FastMoney.of("CHF", 10)));
		assertFalse(FastMoney.of("CHF", 15.546).isLessThan(
				FastMoney.of("CHF", 10.34)));
		assertTrue(FastMoney.of("CHF", 5).isLessThanOrEqualTo(
				FastMoney.of("CHF", 10)));
		assertTrue(FastMoney.of("CHF", 5.546).isLessThanOrEqualTo(
				FastMoney.of("CHF", 10.34)));
	}

	/**
	 * Test method for
	 * {@link org.javamoney.moneta.FastMoney#isGreaterThan(javax.money.MonetaryAmount)} .
	 */
	@Test
	public void testIsGreaterThan() {
		assertFalse(FastMoney.of("CHF", BigDecimal.valueOf(0d)).isGreaterThan(
				FastMoney.of("CHF", BigDecimal.valueOf(0))));
		assertTrue(FastMoney.of("CHF", BigDecimal.valueOf(0.00001d))
				.isGreaterThan(FastMoney.of("CHF", BigDecimal.valueOf(0d))));
		assertTrue(FastMoney.of("CHF", 15).isGreaterThan(
				FastMoney.of("CHF", 10)));
		assertTrue(FastMoney.of("CHF", 15.546).isGreaterThan(
				FastMoney.of("CHF", 10.34)));
		assertFalse(FastMoney.of("CHF", 5).isGreaterThan(
				FastMoney.of("CHF", 10)));
		assertFalse(FastMoney.of("CHF", 5.546).isGreaterThan(
				FastMoney.of("CHF", 10.34)));
	}

	/**
	 * Test method for
	 * {@link org.javamoney.moneta.FastMoney#isGreaterThanOrEqualTo(javax.money.MonetaryAmount)} .
	 */
	@Test
	public void testIsGreaterThanOrEqualTo() {
		assertTrue(FastMoney.of("CHF", BigDecimal.valueOf(0d))
				.isGreaterThanOrEqualTo(
						FastMoney.of("CHF", BigDecimal.valueOf(0))));
		assertTrue(FastMoney
				.of("CHF", BigDecimal.valueOf(0.00000000001d))
				.isGreaterThanOrEqualTo(
						FastMoney.of("CHF", BigDecimal.valueOf(0d))));
		assertTrue(FastMoney.of("CHF", 15).isGreaterThanOrEqualTo(
				FastMoney.of("CHF", 10)));
		assertTrue(FastMoney.of("CHF", 15.546).isGreaterThanOrEqualTo(
				FastMoney.of("CHF", 10.34)));
		assertFalse(FastMoney.of("CHF", 5).isGreaterThanOrEqualTo(
				FastMoney.of("CHF", 10)));
		assertFalse(FastMoney.of("CHF", 5.546).isGreaterThanOrEqualTo(
				FastMoney.of("CHF", 10.34)));
	}

	/**
	 * Test method for {@link org.javamoney.moneta.FastMoney#isEqualTo(javax.money.MonetaryAmount)}
	 * .
	 */
	@Test
	public void testIsEqualTo() {
		assertTrue(FastMoney.of("CHF", BigDecimal.valueOf(0d)).isEqualTo(
				FastMoney.of("CHF", BigDecimal.valueOf(0))));
		assertFalse(FastMoney.of("CHF", BigDecimal.valueOf(0.00001d))
				.isEqualTo(FastMoney.of("CHF", BigDecimal.valueOf(0d))));
		assertTrue(FastMoney.of("CHF", BigDecimal.valueOf(5d)).isEqualTo(
				FastMoney.of("CHF", BigDecimal.valueOf(5))));
		assertTrue(FastMoney.of("CHF", BigDecimal.valueOf(1d)).isEqualTo(
				FastMoney.of("CHF", BigDecimal.valueOf(1.00))));
		assertTrue(FastMoney.of("CHF", BigDecimal.valueOf(1d)).isEqualTo(
				FastMoney.of("CHF", BigDecimal.ONE)));
		assertTrue(FastMoney.of("CHF", BigDecimal.valueOf(1)).isEqualTo(
				FastMoney.of("CHF", BigDecimal.ONE)));
		assertTrue(FastMoney.of("CHF", new BigDecimal("1.0000")).isEqualTo(
				FastMoney.of("CHF", new BigDecimal("1.00"))));
	}

	/**
	 * Test method for
	 * {@link org.javamoney.moneta.FastMoney#isNotEqualTo(javax.money.MonetaryAmount)} .
	 */
	@Test
	public void testIsNotEqualTo() {
		assertFalse(FastMoney.of("CHF", BigDecimal.valueOf(0d)).isNotEqualTo(
				FastMoney.of("CHF", BigDecimal.valueOf(0))));
		assertTrue(FastMoney.of("CHF", BigDecimal.valueOf(0.00001d))
				.isNotEqualTo(FastMoney.of("CHF", BigDecimal.valueOf(0d))));
		assertFalse(FastMoney.of("CHF", BigDecimal.valueOf(5d)).isNotEqualTo(
				FastMoney.of("CHF", BigDecimal.valueOf(5))));
		assertFalse(FastMoney.of("CHF", BigDecimal.valueOf(1d)).isNotEqualTo(
				FastMoney.of("CHF", BigDecimal.valueOf(1.00))));
		assertFalse(FastMoney.of("CHF", BigDecimal.valueOf(1d)).isNotEqualTo(
				FastMoney.of("CHF", BigDecimal.ONE)));
		assertFalse(FastMoney.of("CHF", BigDecimal.valueOf(1)).isNotEqualTo(
				FastMoney.of("CHF", BigDecimal.ONE)));
		assertFalse(FastMoney.of("CHF", new BigDecimal("1.0000")).isNotEqualTo(
				FastMoney.of("CHF", new BigDecimal("1.00"))));
	}

	/**
	 * Test method for {@link org.javamoney.moneta.FastMoney#getNumberType()}.
	 */
	@Test
	public void testGetImplementationType() {
		assertEquals(FastMoney.of("CHF", 0).getMonetaryContext()
				.getAmountType(), FastMoney.class);
		assertEquals(FastMoney.of("CHF", 0.34738746d).getMonetaryContext()
				.getAmountType(),
				FastMoney.class);
		assertEquals(FastMoney.of("CHF", 100034L).getMonetaryContext()
				.getAmountType(), FastMoney.class);
	}

	/**
	 * Test method for {@link org.javamoney.moneta.FastMoney#query(javax.money.MonetaryQuery)}.
	 */
	@Test
	public void testQuery() {
		MonetaryQuery<Integer> q = new MonetaryQuery<Integer>() {
			@Override
			public Integer queryFrom(MonetaryAmount amount) {
				return FastMoney.from(amount).getPrecision();
			}
		};
		FastMoney[] moneys = new FastMoney[] {
				FastMoney.of("CHF", 100),
				FastMoney.of("USD", 34242344),
				FastMoney.of("EUR", 23123213.435),
				FastMoney.of("USS", -23123213.435),
				FastMoney.of("USN", -23123213),
				FastMoney.of("GBP", 0) };
		for (int i = 0; i < moneys.length; i++) {
			assertEquals((Integer) moneys[i].query(q),
					(Integer) moneys[i].getPrecision());
		}
	}

	/**
	 * Test method for {@link org.javamoney.moneta.FastMoney#asType(java.lang.Class)}.
	 */
	@Test
	public void testGetNumberClassOfT() {
		FastMoney m = FastMoney.of("CHF", 13.656);
		assertEquals(m.getNumber().numberValue(Byte.class),
				Byte.valueOf((byte) 13));
		assertEquals(m.getNumber().numberValue(Short.class),
				Short.valueOf((short) 13));
		assertEquals(m.getNumber().numberValue(Integer.class),
				Integer.valueOf(13));
		assertEquals(m.getNumber().numberValue(Long.class), Long.valueOf(13L));
		assertEquals(m.getNumber().numberValue(Float.class),
				Float.valueOf(13.656f));
		assertEquals(m.getNumber().numberValue(Double.class),
				Double.valueOf(13.656));
		assertEquals(m.getNumber().numberValue(BigDecimal.class),
				new BigDecimal("13.656"));
	}

	/**
	 * Test method for {@link org.javamoney.moneta.FastMoney#asNumber()}.
	 */
	@Test
	public void testGetNumber() {
		assertEquals(BigDecimal.ZERO, FastMoney.of("CHF", 0)
				.getNumber().numberValue(BigDecimal.class));
		assertEquals(new BigDecimal("100034"),
				FastMoney.of("CHF", 100034L)
						.getNumber().numberValue(BigDecimal.class));
		assertEquals(new BigDecimal("0.34738"), FastMoney
				.of("CHF", new BigDecimal("0.34738")).getNumber()
				.numberValue(BigDecimal.class));
	}

	/**
	 * Test method for {@link org.javamoney.moneta.FastMoney#toString()}.
	 */
	@Test
	public void testToString() {
		assertEquals("XXX 1.23455",
				FastMoney.of("XXX", new BigDecimal("1.23455645"))
						.toString());
		assertEquals("CHF 1234.00000", FastMoney.of("CHF", 1234).toString());
		assertEquals("CHF 1234.00000",
				FastMoney.of("CHF", new BigDecimal("1234.0")).toString());
		assertEquals("CHF 1234.10000",
				FastMoney.of("CHF", new BigDecimal("1234.1"))
						.toString());
		assertEquals("CHF 0.01000",
				FastMoney.of("CHF", new BigDecimal("0.0100"))
						.toString());
	}

	/**
	 * Test method for {@link org.javamoney.moneta.FastMoney#with(javax.money.MonetaryAdjuster)} .
	 */
	@Test
	public void testWithMonetaryOperator() {
		MonetaryOperator adj = new MonetaryOperator() {
			@Override
			public MonetaryAmount apply(MonetaryAmount amount) {
				return FastMoney.of(amount.getCurrency(), -100);
			}
		};
		FastMoney m = FastMoney.of("XXX", new BigDecimal("1.2345"));
		FastMoney a = m.with(adj);
		assertNotNull(a);
		assertNotSame(m, a);
		assertEquals(m.getCurrency(), a.getCurrency());
		assertEquals(FastMoney.of(m.getCurrency(), -100), a);
		adj = new MonetaryOperator() {
			@Override
			public <T extends MonetaryAmount> T apply(T amount) {
				return (T) amount.multiply(2).getFactory()
						.with(MonetaryCurrencies.getCurrency("CHF")).create();
			}
		};
		a = m.with(adj);
		assertNotNull(a);
		assertNotSame(m, a);
		assertEquals(MonetaryCurrencies.getCurrency("CHF"), a.getCurrency());
		assertEquals(FastMoney.of(a.getCurrency(), 1.2345 * 2), a);
	}

	/**
	 * Test method for {@link org.javamoney.moneta.FastMoney#from(javax.money.MonetaryAmount)}.
	 */
	@Test
	public void testFrom() {
		FastMoney m = FastMoney.of("XXX", new BigDecimal("1.2345"));
		FastMoney m2 = FastMoney.from(m);
		assertTrue(m == m2);
		Money fm = Money.of("XXX", new BigDecimal("1.2345"));
		m2 = FastMoney.from(fm);
		assertFalse(m == m2);
		assertEquals(m, m2);
	}

	@Test
	public void testSerialization() throws IOException, ClassNotFoundException {
		FastMoney m = FastMoney.of("XXX", new BigDecimal("1.2345"));
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		ObjectOutputStream oos = new ObjectOutputStream(bos);
		oos.writeObject(m);
		oos.flush();
		ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(
				bos.toByteArray()));
		FastMoney m2 = (FastMoney) ois.readObject();
		assertEquals(m, m2);
		assertTrue(m != m2);
	}

	/**
	 * Test method for {@link org.javamoney.moneta.Money#from(javax.money.MonetaryAmount)}.
	 */
	@Test
	public void testFromInversed() {
		Money m = Money.of("XXX", new BigDecimal("1.2345"));
		Money m2 = Money.from(m);
		assertTrue(m == m2);
		FastMoney fm = FastMoney.of("XXX", new BigDecimal("1.2345"));
		m2 = Money.from(fm);
		assertFalse(m == m2);
		assertEquals(m, m2);
	}

}
