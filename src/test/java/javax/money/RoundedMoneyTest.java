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
package javax.money;

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
import java.math.MathContext;
import java.math.RoundingMode;

import org.junit.Test;

/**
 * @author Anatole
 * 
 */
public class RoundedMoneyTest {

	private static final BigDecimal TEN = new BigDecimal(10.0d);
	protected static final CurrencyUnit EURO = MoneyCurrency.of("EUR");
	protected static final CurrencyUnit DOLLAR = MoneyCurrency
			.of("USD");

	/**
	 * Test method for
	 * {@link javax.money.RoundedMoney#of(javax.money.CurrencyUnit, java.math.BigDecimal)}
	 * .
	 */
	@Test
	public void testOfCurrencyUnitBigDecimal() {
		RoundedMoney m = RoundedMoney.of(MoneyCurrency.of("EUR"), TEN);
		assertEquals(TEN, m.asType(BigDecimal.class));
	}

	@Test
	public void testOfCurrencyUnitDouble() {
		RoundedMoney m = RoundedMoney.of(MoneyCurrency.of("EUR"), 10.0d);
		assertTrue(TEN.doubleValue() == m.getNumber().doubleValue());
	}

	/**
	 * Test method for {@link javax.money.RoundedMoney#getCurrency()}.
	 */
	@Test
	public void testGetCurrency() {
		MonetaryAmount money = RoundedMoney.of(EURO, BigDecimal.TEN);
		assertNotNull(money.getCurrency());
		assertEquals("EUR", money.getCurrency().getCurrencyCode());
	}

	@Test
	public void testSubtractMonetaryAmount() {
		RoundedMoney money1 = RoundedMoney.of(EURO, BigDecimal.TEN);
		RoundedMoney money2 = RoundedMoney.of(EURO, BigDecimal.ONE);
		RoundedMoney moneyResult = money1.subtract(money2);
		assertNotNull(moneyResult);
		assertEquals(9d, moneyResult.getNumber().doubleValue(), 0d);
	}

	@Test
	public void testDivideAndRemainder_BigDecimal() {
		RoundedMoney money1 = RoundedMoney.of(EURO, BigDecimal.ONE);
		RoundedMoney[] divideAndRemainder = money1
				.divideAndRemainder(new BigDecimal(
						"0.50000001"));
		assertThat(divideAndRemainder[0].asType(BigDecimal.class),
				equalTo(BigDecimal.ONE));
		assertThat(divideAndRemainder[1].asType(BigDecimal.class),
				equalTo(new BigDecimal("0.49")));
	}

	@Test
	public void testDivideToIntegralValue_BigDecimal() {
		RoundedMoney money1 = RoundedMoney.of(EURO, BigDecimal.ONE);
		RoundedMoney result = money1.divideToIntegralValue(new BigDecimal(
				"0.50000000000000000001"));
		assertThat(result.asType(BigDecimal.class), equalTo(BigDecimal.ONE));
	}

	/**
	 * Test method for {@link javax.money.RoundedMoney#hashCode()}.
	 */
	@Test
	public void testHashCode() {
		RoundedMoney money1 = RoundedMoney.of(EURO, BigDecimal.ONE);
		RoundedMoney money2 = RoundedMoney.of(EURO, new BigDecimal("1"));
		assertEquals(money1.hashCode(), money2.hashCode());
		RoundedMoney money3 = RoundedMoney.of(DOLLAR, 1.0);
		assertTrue(money1.hashCode() != money3.hashCode());
		assertTrue(money2.hashCode() != money3.hashCode());
		RoundedMoney money4 = RoundedMoney.of(DOLLAR, BigDecimal.ONE);
		assertTrue(money1.hashCode() != money4.hashCode());
		assertTrue(money2.hashCode() != money4.hashCode());
		RoundedMoney money5 = RoundedMoney.of(DOLLAR, BigDecimal.ONE);
		RoundedMoney money6 = RoundedMoney.of(DOLLAR, 1.0);
		assertTrue(money1.hashCode() != money5.hashCode());
		assertTrue(money2.hashCode() != money5.hashCode());
		assertTrue(money1.hashCode() != money6.hashCode());
		assertTrue(money2.hashCode() != money6.hashCode());
	}

	/**
	 * Test method for
	 * {@link javax.money.RoundedMoney#getDefaultMathContext()}.
	 */
	@Test
	public void testGetDefaultMathContext() {
		RoundedMoney money1 = RoundedMoney.of(EURO, BigDecimal.ONE);
		assertEquals(RoundedMoney.DEFAULT_MONETARY_CONTEXT,
				money1.getMonetaryContext());
	}

	/**
	 * Test method for
	 * {@link javax.money.RoundedMoney#of(javax.money.CurrencyUnit, java.math.BigDecimal, java.math.MathContext)}
	 * .
	 */
	@Test
	public void testOfCurrencyUnitBigDecimalMathContext() {
		RoundedMoney m = RoundedMoney.of(EURO, BigDecimal.valueOf(2.15),
				new MonetaryContext.Builder(BigDecimal.class).setMaxScale(2)
						.setAttribute(
								RoundingMode.DOWN).build());
		RoundedMoney m2 = RoundedMoney.of(EURO, BigDecimal.valueOf(2.1));
		assertEquals(m, m2);
		RoundedMoney m3 = m.multiply(100);
		assertEquals(RoundedMoney.of(EURO, 210), m3.abs());
	}

	/**
	 * Test method for
	 * {@link javax.money.RoundedMoney#of(javax.money.CurrencyUnit, java.lang.Number)}
	 * .
	 */
	@Test
	public void testOfCurrencyUnitNumber() {
		RoundedMoney m = RoundedMoney.of(EURO, (byte) 2);
		assertNotNull(m);
		assertEquals(EURO, m.getCurrency());
		assertEquals(Byte.valueOf((byte) 2), m.asType(Byte.class));
		m = RoundedMoney.of(DOLLAR, (short) -2);
		assertNotNull(m);
		assertEquals(DOLLAR, m.getCurrency());
		assertEquals(Short.valueOf((short) -2), m.asType(Short.class));
		m = RoundedMoney.of(EURO, (int) -12);
		assertNotNull(m);
		assertEquals(EURO, m.getCurrency());
		assertEquals(Integer.valueOf((int) -12), m.asType(Integer.class));
		m = RoundedMoney.of(DOLLAR, (long) 12);
		assertNotNull(m);
		assertEquals(DOLLAR, m.getCurrency());
		assertEquals(Long.valueOf((long) 12), m.asType(Long.class));
		m = RoundedMoney.of(EURO, (float) 12.23);
		assertNotNull(m);
		assertEquals(EURO, m.getCurrency());
		assertEquals(Float.valueOf((float) 12.23), m.asType(Float.class));
		m = RoundedMoney.of(DOLLAR, (double) -12.23);
		assertNotNull(m);
		assertEquals(DOLLAR, m.getCurrency());
		assertEquals(Double.valueOf((double) -12.23), m.asType(Double.class));
		m = RoundedMoney.of(EURO, (Number) BigDecimal.valueOf(234.2345));
		assertNotNull(m);
		assertEquals(EURO, m.getCurrency());
		assertEquals(BigDecimal.valueOf(234.2345), m.asType(BigDecimal.class));
		m = RoundedMoney.of(DOLLAR,
				(Number) BigInteger.valueOf(23232312321432432L));
		assertNotNull(m);
		assertEquals(DOLLAR, m.getCurrency());
		assertEquals(Long.valueOf(23232312321432432L), m.asType(Long.class));
		assertEquals(BigInteger.valueOf(23232312321432432L),
				m.asType(BigInteger.class));
	}

	/**
	 * Test method for
	 * {@link javax.money.RoundedMoney#of(javax.money.CurrencyUnit, java.lang.Number, java.math.MathContext)}
	 * .
	 */
	@Test
	public void testOfCurrencyUnitNumberMonetaryContext() {
		MonetaryContext mc = new MonetaryContext.Builder(BigDecimal.class)
				.setPrecision(2345).setAttribute(RoundingMode.CEILING).build();
		RoundedMoney m = RoundedMoney.of(EURO, (byte) 2, mc);
		assertNotNull(m);
		assertEquals(mc, m.getMonetaryContext());
		assertEquals(EURO, m.getCurrency());
		assertEquals(Byte.valueOf((byte) 2), m.asType(Byte.class));
		m = RoundedMoney.of(DOLLAR, (short) -2, mc);
		assertNotNull(m);
		assertEquals(mc, m.getMonetaryContext());
		assertEquals(DOLLAR, m.getCurrency());
		assertEquals(Short.valueOf((short) -2), m.asType(Short.class));
		m = RoundedMoney.of(EURO, (int) -12, mc);
		assertNotNull(m);
		assertEquals(mc, m.getMonetaryContext());
		assertEquals(EURO, m.getCurrency());
		assertEquals(Integer.valueOf((int) -12), m.asType(Integer.class));
		m = RoundedMoney.of(DOLLAR, (long) 12, mc);
		assertEquals(mc, m.getMonetaryContext());
		assertNotNull(m);
		assertEquals(DOLLAR, m.getCurrency());
		assertEquals(Long.valueOf((long) 12), m.asType(Long.class));
		m = RoundedMoney.of(EURO, (float) 12.23, mc);
		assertNotNull(m);
		assertEquals(mc, m.getMonetaryContext());
		assertEquals(EURO, m.getCurrency());
		assertEquals(Float.valueOf((float) 12.23), m.asType(Float.class));
		m = RoundedMoney.of(DOLLAR, (double) -12.23, mc);
		assertNotNull(m);
		assertEquals(mc, m.getMonetaryContext());
		assertEquals(DOLLAR, m.getCurrency());
		assertEquals(mc, m.getMonetaryContext());
		assertEquals(Double.valueOf((double) -12.23), m.asType(Double.class));
		m = RoundedMoney.of(EURO, (Number) BigDecimal.valueOf(234.2345), mc);
		assertNotNull(m);
		assertEquals(EURO, m.getCurrency());
		assertEquals(mc, m.getMonetaryContext());
		assertEquals(BigDecimal.valueOf(234.2345), m.asType(BigDecimal.class));
		m = RoundedMoney.of(DOLLAR,
				(Number) BigInteger.valueOf(23232312321432432L),
				mc);
		assertNotNull(m);
		assertEquals(DOLLAR, m.getCurrency());
		assertEquals(mc, m.getMonetaryContext());
		assertEquals(Long.valueOf(23232312321432432L), m.asType(Long.class));
		assertEquals(BigInteger.valueOf(23232312321432432L),
				m.asType(BigInteger.class));
	}

	/**
	 * Test method for
	 * {@link javax.money.RoundedMoney#of(java.lang.String, java.lang.Number)}
	 * .
	 */
	@Test
	public void testOfStringNumber() {
		RoundedMoney m = RoundedMoney.of("EUR", (byte) 2);
		assertNotNull(m);
		assertEquals(EURO, m.getCurrency());
		assertEquals(Byte.valueOf((byte) 2), m.asType(Byte.class));
		m = RoundedMoney.of("USD", (short) -2);
		assertNotNull(m);
		assertEquals(DOLLAR, m.getCurrency());
		assertEquals(Short.valueOf((short) -2), m.asType(Short.class));
		m = RoundedMoney.of("EUR", (int) -12);
		assertNotNull(m);
		assertEquals(EURO, m.getCurrency());
		assertEquals(Integer.valueOf((int) -12), m.asType(Integer.class));
		m = RoundedMoney.of("USD", (long) 12);
		assertNotNull(m);
		assertEquals(DOLLAR, m.getCurrency());
		assertEquals(Long.valueOf((long) 12), m.asType(Long.class));
		m = RoundedMoney.of("EUR", (float) 12.23);
		assertNotNull(m);
		assertEquals(EURO, m.getCurrency());
		assertEquals(Float.valueOf((float) 12.23), m.asType(Float.class));
		m = RoundedMoney.of("USD", (double) -12.23);
		assertNotNull(m);
		assertEquals(DOLLAR, m.getCurrency());
		assertEquals(Double.valueOf((double) -12.23), m.asType(Double.class));
		m = RoundedMoney.of("EUR", (Number) BigDecimal.valueOf(234.2345));
		assertNotNull(m);
		assertEquals(EURO, m.getCurrency());
		assertEquals(BigDecimal.valueOf(234.2345), m.asType(BigDecimal.class));
		m = RoundedMoney.of("USD",
				(Number) BigInteger.valueOf(23232312321432432L));
		assertNotNull(m);
		assertEquals(DOLLAR, m.getCurrency());
		assertEquals(Long.valueOf(23232312321432432L), m.asType(Long.class));
		assertEquals(BigInteger.valueOf(23232312321432432L),
				m.asType(BigInteger.class));
	}

	/**
	 * Test a round trip using from.
	 */
	@Test
	public void testRoundtrip() {
		RoundedMoney m = RoundedMoney.of("USD", new BigDecimal("0.5"));
		Money mm = Money.from(m);
		RoundedMoney m2 = RoundedMoney.from(mm);
		assertEquals(m, m2);
	}

	/**
	 * Test method for
	 * {@link javax.money.RoundedMoney#of(java.lang.String, java.lang.Number, java.math.MathContext)}
	 * .
	 */
	@Test
	public void testOfStringNumberMathContext() {
		MonetaryContext mc = new MonetaryContext.Builder(BigDecimal.class)
				.setPrecision(2345).setAttribute(RoundingMode.CEILING).build();
		RoundedMoney m = RoundedMoney.of("EUR", (byte) 2, mc);
		assertNotNull(m);
		assertEquals(mc, m.getMonetaryContext());
		assertEquals(EURO, m.getCurrency());
		assertEquals(Byte.valueOf((byte) 2), m.asType(Byte.class));
		m = RoundedMoney.of("USD", (short) -2, mc);
		assertNotNull(m);
		assertEquals(mc, m.getMonetaryContext());
		assertEquals(DOLLAR, m.getCurrency());
		assertEquals(Short.valueOf((short) -2), m.asType(Short.class));
		m = RoundedMoney.of("EUR", (int) -12, mc);
		assertNotNull(m);
		assertEquals(mc, m.getMonetaryContext());
		assertEquals(EURO, m.getCurrency());
		assertEquals(Integer.valueOf((int) -12), m.asType(Integer.class));
		m = RoundedMoney.of("USD", (long) 12, mc);
		assertEquals(mc, m.getMonetaryContext());
		assertNotNull(m);
		assertEquals(DOLLAR, m.getCurrency());
		assertEquals(Long.valueOf((long) 12), m.asType(Long.class));
		m = RoundedMoney.of("EUR", (float) 12.23, mc);
		assertNotNull(m);
		assertEquals(mc, m.getMonetaryContext());
		assertEquals(EURO, m.getCurrency());
		assertEquals(Float.valueOf((float) 12.23), m.asType(Float.class));
		m = RoundedMoney.of("USD", (double) -12.23, mc);
		assertNotNull(m);
		assertEquals(mc, m.getMonetaryContext());
		assertEquals(DOLLAR, m.getCurrency());
		assertEquals(mc, m.getMonetaryContext());
		assertEquals(Double.valueOf((double) -12.23), m.asType(Double.class));
		m = RoundedMoney.of("EUR", (Number) BigDecimal.valueOf(234.2345), mc);
		assertNotNull(m);
		assertEquals(EURO, m.getCurrency());
		assertEquals(mc, m.getMonetaryContext());
		assertEquals(BigDecimal.valueOf(234.2345), m.asType(BigDecimal.class));
		m = RoundedMoney
				.of("USD", (Number) BigInteger.valueOf(23232312321432432L), mc);
		assertNotNull(m);
		assertEquals(DOLLAR, m.getCurrency());
		assertEquals(mc, m.getMonetaryContext());
		assertEquals(Long.valueOf(23232312321432432L), m.asType(Long.class));
		assertEquals(BigInteger.valueOf(23232312321432432L),
				m.asType(BigInteger.class));
	}

	/**
	 * Test method for
	 * {@link javax.money.RoundedMoney#ofZero(javax.money.CurrencyUnit)}
	 * .
	 */
	@Test
	public void testOfZeroCurrencyUnit() {
		RoundedMoney m = RoundedMoney.ofZero(MoneyCurrency.of("USD"));
		assertNotNull(m);
		assertEquals(MoneyCurrency.of("USD"), m.getCurrency());
		assertEquals(m.getNumber().doubleValue(), 0d, 0d);
	}

	/**
	 * Test method for
	 * {@link javax.money.RoundedMoney#ofZero(java.lang.String)}.
	 */
	@Test
	public void testOfZeroString() {
		RoundedMoney m = RoundedMoney.ofZero("CHF");
		assertNotNull(m);
		assertEquals(MoneyCurrency.of("CHF"), m.getCurrency());
		assertEquals(m.getNumber().doubleValue(), 0d, 0d);
	}

	/**
	 * Test method for
	 * {@link javax.money.RoundedMoney#equals(java.lang.Object)}.
	 */
	@Test
	public void testEqualsObject() {
		RoundedMoney[] moneys = new RoundedMoney[] {
				RoundedMoney.ofZero("CHF"),
				RoundedMoney.of("CHF", BigDecimal.ONE),
				RoundedMoney.of("XXX", BigDecimal.ONE),
				RoundedMoney.of("XXX", BigDecimal.ONE.negate())
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
	 * Test method for
	 * {@link javax.money.RoundedMoney#compareTo(javax.money.MonetaryAmount)}
	 * .
	 */
	@Test
	public void testCompareTo() {
		RoundedMoney m1 = RoundedMoney.of("CHF", -2);
		RoundedMoney m2 = RoundedMoney.of("CHF", 0);
		RoundedMoney m3 = RoundedMoney.of("CHF", -0);
		RoundedMoney m4 = RoundedMoney.of("CHF", 2);
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
	 * Test method for
	 * {@link javax.money.RoundedMoney#getMathContext()}.
	 */
	@Test
	public void testGetMathContext() {
		RoundedMoney m = RoundedMoney.of("CHF", 10);
		assertEquals(RoundedMoney.DEFAULT_MONETARY_CONTEXT,
				m.getMonetaryContext());
		MonetaryContext mc = new MonetaryContext.Builder(BigDecimal.class)
				.setPrecision(2345).setAttribute(RoundingMode.CEILING).build();
		m = RoundedMoney.of("CHF", 10, mc);
		assertEquals(mc, m.getMonetaryContext());
	}

	/**
	 * Test method for {@link javax.money.RoundedMoney#abs()}.
	 */
	@Test
	public void testAbs() {
		RoundedMoney m = RoundedMoney.of("CHF", 10);
		assertEquals(m, m.abs());
		assertTrue(m == m.abs());
		m = RoundedMoney.of("CHF", 0);
		assertEquals(m, m.abs());
		assertTrue(m == m.abs());
		m = RoundedMoney.of("CHF", -10);
		assertEquals(m.negate(), m.abs());
		assertTrue(m != m.abs());
	}

	/**
	 * Test method for
	 * {@link javax.money.RoundedMoney#add(javax.money.MonetaryAmount)}
	 * .
	 */
	@Test
	public void testAdd() {
		RoundedMoney money1 = RoundedMoney.of(EURO, BigDecimal.TEN);
		RoundedMoney money2 = RoundedMoney.of(EURO, BigDecimal.ONE);
		RoundedMoney moneyResult = money1.add(money2);
		assertNotNull(moneyResult);
		assertEquals(11d, moneyResult.getNumber().doubleValue(), 0d);
	}

	/**
	 * Test method for
	 * {@link javax.money.RoundedMoney#divide(java.lang.Number)}.
	 */
	@Test
	public void testDivideNumber() {
		RoundedMoney m = RoundedMoney.of("CHF", 100);
		assertEquals(
				RoundedMoney.of("CHF",
						new BigDecimal("100.00").divide(BigDecimal.valueOf(5))),
				m.divide(BigDecimal.valueOf(5)));
	}

	/**
	 * Test method for
	 * {@link javax.money.RoundedMoney#divideAndRemainder(java.lang.Number)}
	 * .
	 */
	@Test
	public void testDivideAndRemainderNumber() {
		RoundedMoney m = RoundedMoney.of("CHF", 100);
		assertEquals(
				RoundedMoney.of(
						"CHF",
						BigDecimal.valueOf(33)),
				m.divideAndRemainder(
						BigDecimal.valueOf(3))[0]);
		assertEquals(
				RoundedMoney.of(
						"CHF",
						BigDecimal.valueOf(1)),
				m.divideAndRemainder(
						BigDecimal.valueOf(3))[1]);
	}

	/**
	 * Test method for
	 * {@link javax.money.RoundedMoney#divideToIntegralValue(java.lang.Number)}
	 * .
	 */
	@Test
	public void testDivideToIntegralValueNumber() {
		RoundedMoney m = RoundedMoney.of("CHF", 100);
		assertEquals(
				RoundedMoney.of(
						"CHF",
						BigDecimal.valueOf(5)),
				m.divideToIntegralValue(
						BigDecimal.valueOf(20)));
		assertEquals(
				RoundedMoney.of(
						"CHF",
						BigDecimal.valueOf(33)),
				m.divideToIntegralValue(
						BigDecimal.valueOf(3)));
	}

	/**
	 * Test method for
	 * {@link javax.money.RoundedMoney#multiply(java.lang.Number)}.
	 */
	@Test
	public void testMultiplyNumber() {
		RoundedMoney m = RoundedMoney.of("CHF", 100);
		assertEquals(RoundedMoney.of("CHF", new BigDecimal("400.00")),
				m.multiply(4));
		assertEquals(RoundedMoney.of("CHF", new BigDecimal("200.00")),
				m.multiply(2));
		assertEquals(RoundedMoney.of("CHF", new BigDecimal("50.0")),
				m.multiply(new BigDecimal("0.5")));
	}

	/**
	 * Test method for {@link javax.money.RoundedMoney#negate()}.
	 */
	@Test
	public void testNegate() {
		RoundedMoney m = RoundedMoney.of("CHF", 100);
		assertEquals(RoundedMoney.of("CHF", -100), m.negate());
		m = RoundedMoney.of("CHF", -123.234);
		assertEquals(RoundedMoney.of("CHF", 123.234), m.negate());
	}

	/**
	 * Test method for {@link javax.money.RoundedMoney#plus()}.
	 */
	@Test
	public void testPlus() {
		RoundedMoney m = RoundedMoney.of("CHF", 100);
		assertEquals(RoundedMoney.of("CHF", 100), m.plus());
		m = RoundedMoney.of("CHF", 123.234);
		assertEquals(RoundedMoney.of("CHF", 123.234), m.plus());
	}

	/**
	 * Test method for
	 * {@link javax.money.RoundedMoney#subtract(javax.money.MonetaryAmount)}
	 * .
	 */
	@Test
	public void testSubtract() {
		RoundedMoney m = RoundedMoney.of("CHF", 100);
		RoundedMoney s1 = RoundedMoney.of("CHF", 100);
		RoundedMoney s2 = RoundedMoney.of("CHF", 200);
		RoundedMoney s3 = RoundedMoney.of("CHF", 0);
		assertEquals(RoundedMoney.of("CHF", 0), m.subtract(s1));
		assertEquals(RoundedMoney.of("CHF", -100), m.subtract(s2));
		assertEquals(RoundedMoney.of("CHF", 100), m.subtract(s3));
		assertTrue(m == m.subtract(s3));
		m = RoundedMoney.of("CHF", new BigDecimal("-123.234"));
		assertEquals(RoundedMoney.of("CHF", new BigDecimal("-223.234")),
				m.subtract(s1));
		assertEquals(RoundedMoney.of("CHF", new BigDecimal("-323.234")),
				m.subtract(s2));
		assertEquals(RoundedMoney.of("CHF", new BigDecimal("-123.234")),
				m.subtract(s3));
		assertTrue(m == m.subtract(s3));
		m = RoundedMoney.of("CHF", new BigDecimal("12.402345534"));
		s1 = RoundedMoney.of("CHF", new BigDecimal("2343.45"));
		s2 = RoundedMoney.of("CHF", new BigDecimal("12.402345534"));
		s3 = RoundedMoney.of("CHF", new BigDecimal("-2343.45"));
		assertEquals(RoundedMoney.of("CHF", new BigDecimal("12.402345534")
				.subtract(new BigDecimal("2343.45"))), m.subtract(s1));
		assertEquals(RoundedMoney.of("CHF", new BigDecimal("12.402345534")
				.subtract(new BigDecimal("12.402345534"))),
				m.subtract(s2));
		assertTrue(m.subtract(s2).isZero());
		assertEquals(RoundedMoney.of("CHF", new BigDecimal("2355.852345534")),
				m.subtract(s3));
		assertTrue(m == m.subtract(RoundedMoney.of("CHF", 0)));
	}

	/**
	 * Test method for {@link javax.money.RoundedMoney#pow(int)}.
	 */
	@Test
	public void testPow() {
		RoundedMoney m = RoundedMoney.of("CHF", new BigDecimal("23.234"));
		for (int p = 0; p < 100; p++) {
			assertEquals(
					RoundedMoney.of("CHF", m.asType(BigDecimal.class).pow(p)),
					m.pow(p));
		}
	}

	/**
	 * Test method for {@link javax.money.RoundedMoney#ulp()}.
	 */
	@Test
	public void testUlp() {
		RoundedMoney[] moneys = new RoundedMoney[] {
				RoundedMoney.of("CHF", 100),
				RoundedMoney.of("CHF", 34242344),
				RoundedMoney.of("CHF", 23123213.435),
				RoundedMoney.of("CHF", 0), RoundedMoney.of("CHF", -100),
				RoundedMoney.of("CHF", -723527.36532) };
		for (RoundedMoney m : moneys) {
			assertEquals("Invalid ulp.",
					m.with(m.asType(BigDecimal.class).ulp()), m.ulp());
		}
	}

	/**
	 * Test method for
	 * {@link javax.money.RoundedMoney#remainder(java.lang.Number)}.
	 */
	@Test
	public void testRemainderNumber() {
		RoundedMoney[] moneys = new RoundedMoney[] {
				RoundedMoney.of("CHF", 100),
				RoundedMoney.of("CHF", 34242344),
				RoundedMoney.of("CHF", 23123213.435),
				RoundedMoney.of("CHF", 0), RoundedMoney.of("CHF", -100),
				RoundedMoney.of("CHF", -723527.36532) };
		for (RoundedMoney m : moneys) {
			assertEquals(
					"Invalid remainder of " + 10.50,
					m.with(m.asType(BigDecimal.class).remainder(
							BigDecimal.valueOf(10.50))),
					m.remainder(10.50));
			assertEquals(
					"Invalid remainder of " + -30.20,
					m.with(m.asType(BigDecimal.class).remainder(
							BigDecimal.valueOf(-30.20))),
					m.remainder(-30.20));
			assertEquals(
					"Invalid remainder of " + -3,
					m.with(m.asType(BigDecimal.class).remainder(
							BigDecimal.valueOf(-3))),
					m.remainder(-3));
			assertEquals(
					"Invalid remainder of " + 3,
					m.with(m.asType(BigDecimal.class).remainder(
							BigDecimal.valueOf(3))),
					m.remainder(3));
		}
	}

	/**
	 * Test method for
	 * {@link javax.money.RoundedMoney#scaleByPowerOfTen(int)} .
	 */
	@Test
	public void testScaleByPowerOfTen() {
		RoundedMoney[] moneys = new RoundedMoney[] {
				RoundedMoney.of("CHF", 100),
				RoundedMoney.of("CHF", 34242344),
				RoundedMoney.of("CHF", 23123213.435),
				RoundedMoney.of("CHF", 0), RoundedMoney.of("CHF", -100),
				RoundedMoney.of("CHF", -723527.36532) };
		for (RoundedMoney m : moneys) {
			for (int p = -10; p < 10; p++) {
				assertEquals(
						"Invalid scaleByPowerOfTen.",
						m.with(m.asType(BigDecimal.class).scaleByPowerOfTen(p)),
						m.scaleByPowerOfTen(p));
			}
		}
	}

	/**
	 * Test method for {@link javax.money.RoundedMoney#isZero()}.
	 */
	@Test
	public void testIsZero() {
		RoundedMoney[] moneys = new RoundedMoney[] {
				RoundedMoney.of("CHF", 100),
				RoundedMoney.of("CHF", 34242344),
				RoundedMoney.of("CHF", 23123213.435),
				RoundedMoney.of("CHF", -100),
				RoundedMoney.of("CHF", -723527.36532) };
		for (RoundedMoney m : moneys) {
			assertFalse(m.isZero());
		}
		moneys = new RoundedMoney[] { RoundedMoney.of("CHF", 0),
				RoundedMoney.of("CHF", 0.0),
				RoundedMoney.of("CHF", BigDecimal.ZERO),
				RoundedMoney.of("CHF", new BigDecimal("0.00000000000000000")) };
		for (RoundedMoney m : moneys) {
			assertTrue(m.isZero());
		}
	}

	/**
	 * Test method for {@link javax.money.RoundedMoney#isPositive()}.
	 */
	@Test
	public void testIsPositive() {
		RoundedMoney[] moneys = new RoundedMoney[] {
				RoundedMoney.of("CHF", 100),
				RoundedMoney.of("CHF", 34242344),
				RoundedMoney.of("CHF", 23123213.435) };
		for (RoundedMoney m : moneys) {
			assertTrue(m.isPositive());
		}
		moneys = new RoundedMoney[] { RoundedMoney.of("CHF", 0),
				RoundedMoney.of("CHF", 0.0),
				RoundedMoney.of("CHF", BigDecimal.ZERO),
				RoundedMoney.of("CHF", new BigDecimal("0.00000000000000000")),
				RoundedMoney.of("CHF", -100),
				RoundedMoney.of("CHF", -34242344),
				RoundedMoney.of("CHF", -23123213.435) };
		for (RoundedMoney m : moneys) {
			assertFalse(m.isPositive());
		}
	}

	/**
	 * Test method for
	 * {@link javax.money.RoundedMoney#isPositiveOrZero()}.
	 */
	@Test
	public void testIsPositiveOrZero() {
		RoundedMoney[] moneys = new RoundedMoney[] { RoundedMoney.of("CHF", 0),
				RoundedMoney.of("CHF", 0.0),
				RoundedMoney.of("CHF", BigDecimal.ZERO),
				RoundedMoney.of("CHF", new BigDecimal("0.00000000000000000")),
				RoundedMoney.of("CHF", 100),
				RoundedMoney.of("CHF", 34242344),
				RoundedMoney.of("CHF", 23123213.435) };
		for (RoundedMoney m : moneys) {
			assertTrue("Invalid positiveOrZero (expected true): " + m,
					m.isPositiveOrZero());
		}
		moneys = new RoundedMoney[] {
				RoundedMoney.of("CHF", -100),
				RoundedMoney.of("CHF", -34242344),
				RoundedMoney.of("CHF", -23123213.435) };
		for (RoundedMoney m : moneys) {
			assertFalse("Invalid positiveOrZero (expected false): " + m,
					m.isPositiveOrZero());
		}
	}

	/**
	 * Test method for {@link javax.money.RoundedMoney#isNegative()}.
	 */
	@Test
	public void testIsNegative() {
		RoundedMoney[] moneys = new RoundedMoney[] { RoundedMoney.of("CHF", 0),
				RoundedMoney.of("CHF", 0.0),
				RoundedMoney.of("CHF", BigDecimal.ZERO),
				RoundedMoney.of("CHF", new BigDecimal("0.00000000000000000")),
				RoundedMoney.of("CHF", 100),
				RoundedMoney.of("CHF", 34242344),
				RoundedMoney.of("CHF", 23123213.435) };
		for (RoundedMoney m : moneys) {
			assertFalse("Invalid isNegative (expected false): " + m,
					m.isNegative());
		}
		moneys = new RoundedMoney[] {
				RoundedMoney.of("CHF", -100),
				RoundedMoney.of("CHF", -34242344),
				RoundedMoney.of("CHF", -23123213.435) };
		for (RoundedMoney m : moneys) {
			assertTrue("Invalid isNegative (expected true): " + m,
					m.isNegative());
		}
	}

	/**
	 * Test method for
	 * {@link javax.money.RoundedMoney#isNegativeOrZero()}.
	 */
	@Test
	public void testIsNegativeOrZero() {
		RoundedMoney[] moneys = new RoundedMoney[] {
				RoundedMoney.of("CHF", 100),
				RoundedMoney.of("CHF", 34242344),
				RoundedMoney.of("CHF", 23123213.435) };
		for (RoundedMoney m : moneys) {
			assertFalse("Invalid negativeOrZero (expected false): " + m,
					m.isNegativeOrZero());
		}
		moneys = new RoundedMoney[] { RoundedMoney.of("CHF", 0),
				RoundedMoney.of("CHF", 0.0),
				RoundedMoney.of("CHF", BigDecimal.ZERO),
				RoundedMoney.of("CHF", new BigDecimal("0.00000000000000000")),
				RoundedMoney.of("CHF", -100),
				RoundedMoney.of("CHF", -34242344),
				RoundedMoney.of("CHF", -23123213.435) };
		for (RoundedMoney m : moneys) {
			assertTrue("Invalid negativeOrZero (expected true): " + m,
					m.isNegativeOrZero());
		}
	}

	/**
	 * Test method for
	 * {@link javax.money.RoundedMoney#with(java.lang.Number)} .
	 */
	@Test
	public void testWithNumber() {
		RoundedMoney[] moneys = new RoundedMoney[] {
				RoundedMoney.of("CHF", 100),
				RoundedMoney.of("CHF", 34242344),
				RoundedMoney.of("CHF", new BigDecimal("23123213.435")),
				RoundedMoney.of("CHF", new BigDecimal("-23123213.435")),
				RoundedMoney.of("CHF", -23123213),
				RoundedMoney.of("CHF", 0) };
		RoundedMoney s = RoundedMoney.of("CHF", 10);
		RoundedMoney[] moneys2 = new RoundedMoney[] {
				s.with(100),
				s.with(34242344), s.with(new BigDecimal("23123213.435")),
				s.with(new BigDecimal("-23123213.435")), s.with(-23123213),
				s.with(0) };
		for (int i = 0; i < moneys.length; i++) {
			assertEquals("with(Number) failed.", moneys[i], moneys2[i]);
		}
	}

	/**
	 * Test method for
	 * {@link javax.money.RoundedMoney#with(javax.money.CurrencyUnit, java.lang.Number)}
	 * .
	 */
	@Test
	public void testWithCurrencyUnitNumber() {
		RoundedMoney[] moneys = new RoundedMoney[] {
				RoundedMoney.of("CHF", 100),
				RoundedMoney.of("USD", 34242344),
				RoundedMoney.of("EUR", new BigDecimal("23123213.435")),
				RoundedMoney.of("USS", new BigDecimal("-23123213.435")),
				RoundedMoney.of("USN", -23123213),
				RoundedMoney.of("GBP", 0) };
		RoundedMoney s = RoundedMoney.of("XXX", 10);
		RoundedMoney[] moneys2 = new RoundedMoney[] {
				s.with(MoneyCurrency.of("CHF"), 100),
				s.with(MoneyCurrency.of("USD"), 34242344),
				s.with(MoneyCurrency.of("EUR"), new BigDecimal("23123213.435")),
				s.with(MoneyCurrency.of("USS"), new BigDecimal("-23123213.435")),
				s.with(MoneyCurrency.of("USN"), -23123213),
				s.with(MoneyCurrency.of("GBP"), 0) };
		for (int i = 0; i < moneys.length; i++) {
			assertEquals("with(Number) failed.", moneys[i], moneys2[i]);
		}
	}

	/**
	 * Test method for {@link javax.money.RoundedMoney#getScale()}.
	 */
	@Test
	public void testGetScale() {
		RoundedMoney[] moneys = new RoundedMoney[] {
				RoundedMoney.of("CHF", 100),
				RoundedMoney.of("USD", 34242344),
				RoundedMoney.of("EUR", 23123213.435),
				RoundedMoney.of("USS", -23123213.435),
				RoundedMoney.of("USN", -23123213),
				RoundedMoney.of("GBP", 0) };
		for (RoundedMoney m : moneys) {
			assertEquals("Scale for " + m, m.asType(BigDecimal.class).scale(),
					m.getScale());
		}
	}

	/**
	 * Test method for {@link javax.money.RoundedMoney#getPrecision()}.
	 */
	@Test
	public void testGetPrecision() {
		RoundedMoney[] moneys = new RoundedMoney[] {
				RoundedMoney.of("CHF", 100),
				RoundedMoney.of("USD", 34242344),
				RoundedMoney.of("EUR", 23123213.435),
				RoundedMoney.of("USS", -23123213.435),
				RoundedMoney.of("USN", -23123213),
				RoundedMoney.of("GBP", 0) };
		for (RoundedMoney m : moneys) {
			assertEquals("Precision for " + m, m.asType(BigDecimal.class)
					.precision(),
					m.getPrecision());
		}
	}

	/**
	 * Test method for {@link javax.money.RoundedMoney#signum()}.
	 */
	@Test
	public void testSignum() {
		RoundedMoney m = RoundedMoney.of("CHF", 100);
		assertEquals("signum of " + m, 1, m.signum());
		m = RoundedMoney.of("CHF", -100);
		assertEquals("signum of " + m, -1, m.signum());
		m = RoundedMoney.of("CHF", 100.3435);
		assertEquals("signum of " + m, 1, m.signum());
		m = RoundedMoney.of("CHF", -100.3435);
		assertEquals("signum of " + m, -1, m.signum());
		m = RoundedMoney.of("CHF", 0);
		assertEquals("signum of " + m, 0, m.signum());
		m = RoundedMoney.of("CHF", -0);
		assertEquals("signum of " + m, 0, m.signum());
	}

	/**
	 * Test method for
	 * {@link javax.money.RoundedMoney#isLessThan(javax.money.MonetaryAmount)}
	 * .
	 */
	@Test
	public void testIsLessThan() {
		assertFalse(RoundedMoney.of("CHF", BigDecimal.valueOf(0d)).isLessThan(
				RoundedMoney.of("CHF", BigDecimal.valueOf(0))));
		assertFalse(RoundedMoney.of("CHF", BigDecimal.valueOf(0.00000000001d))
				.isLessThan(RoundedMoney.of("CHF", BigDecimal.valueOf(0d))));
		assertFalse(RoundedMoney.of("CHF", 15).isLessThan(
				RoundedMoney.of("CHF", 10)));
		assertFalse(RoundedMoney.of("CHF", 15.546).isLessThan(
				RoundedMoney.of("CHF", 10.34)));
		assertTrue(RoundedMoney.of("CHF", 5).isLessThan(
				RoundedMoney.of("CHF", 10)));
		assertTrue(RoundedMoney.of("CHF", 5.546).isLessThan(
				RoundedMoney.of("CHF", 10.34)));
	}

	/**
	 * Test method for
	 * {@link javax.money.RoundedMoney#isLessThanOrEqualTo(javax.money.MonetaryAmount)}
	 * .
	 */
	@Test
	public void testIsLessThanOrEqualTo() {
		assertTrue(RoundedMoney.of("CHF", BigDecimal.valueOf(0d))
				.isLessThanOrEqualTo(
						RoundedMoney.of("CHF", BigDecimal.valueOf(0))));
		assertFalse(RoundedMoney.of("CHF", BigDecimal.valueOf(0.00000000001d))
				.isLessThanOrEqualTo(
						RoundedMoney.of("CHF", BigDecimal.valueOf(0d))));
		assertFalse(RoundedMoney.of("CHF", 15).isLessThanOrEqualTo(
				RoundedMoney.of("CHF", 10)));
		assertFalse(RoundedMoney.of("CHF", 15.546).isLessThan(
				RoundedMoney.of("CHF", 10.34)));
		assertTrue(RoundedMoney.of("CHF", 5).isLessThanOrEqualTo(
				RoundedMoney.of("CHF", 10)));
		assertTrue(RoundedMoney.of("CHF", 5.546).isLessThanOrEqualTo(
				RoundedMoney.of("CHF", 10.34)));
	}

	/**
	 * Test method for
	 * {@link javax.money.RoundedMoney#isGreaterThan(javax.money.MonetaryAmount)}
	 * .
	 */
	@Test
	public void testIsGreaterThan() {
		assertFalse(RoundedMoney.of("CHF", BigDecimal.valueOf(0d))
				.isGreaterThan(
						RoundedMoney.of("CHF", BigDecimal.valueOf(0))));
		assertTrue(RoundedMoney.of("CHF", BigDecimal.valueOf(0.00000000001d))
				.isGreaterThan(RoundedMoney.of("CHF", BigDecimal.valueOf(0d))));
		assertTrue(RoundedMoney.of("CHF", 15).isGreaterThan(
				RoundedMoney.of("CHF", 10)));
		assertTrue(RoundedMoney.of("CHF", 15.546).isGreaterThan(
				RoundedMoney.of("CHF", 10.34)));
		assertFalse(RoundedMoney.of("CHF", 5).isGreaterThan(
				RoundedMoney.of("CHF", 10)));
		assertFalse(RoundedMoney.of("CHF", 5.546).isGreaterThan(
				RoundedMoney.of("CHF", 10.34)));
	}

	/**
	 * Test method for
	 * {@link javax.money.RoundedMoney#isGreaterThanOrEqualTo(javax.money.MonetaryAmount)}
	 * .
	 */
	@Test
	public void testIsGreaterThanOrEqualTo() {
		assertTrue(RoundedMoney.of("CHF", BigDecimal.valueOf(0d))
				.isGreaterThanOrEqualTo(
						RoundedMoney.of("CHF", BigDecimal.valueOf(0))));
		assertTrue(RoundedMoney
				.of("CHF", BigDecimal.valueOf(0.00000000001d))
				.isGreaterThanOrEqualTo(
						RoundedMoney.of("CHF", BigDecimal.valueOf(0d))));
		assertTrue(RoundedMoney.of("CHF", 15).isGreaterThanOrEqualTo(
				RoundedMoney.of("CHF", 10)));
		assertTrue(RoundedMoney.of("CHF", 15.546).isGreaterThanOrEqualTo(
				RoundedMoney.of("CHF", 10.34)));
		assertFalse(RoundedMoney.of("CHF", 5).isGreaterThanOrEqualTo(
				RoundedMoney.of("CHF", 10)));
		assertFalse(RoundedMoney.of("CHF", 5.546).isGreaterThanOrEqualTo(
				RoundedMoney.of("CHF", 10.34)));
	}

	/**
	 * Test method for
	 * {@link javax.money.RoundedMoney#isEqualTo(javax.money.MonetaryAmount)}
	 * .
	 */
	@Test
	public void testIsEqualTo() {
		assertTrue(RoundedMoney.of("CHF", BigDecimal.valueOf(0d)).isEqualTo(
				RoundedMoney.of("CHF", BigDecimal.valueOf(0))));
		assertFalse(RoundedMoney.of("CHF", BigDecimal.valueOf(0.00000000001d))
				.isEqualTo(RoundedMoney.of("CHF", BigDecimal.valueOf(0d))));
		assertTrue(RoundedMoney.of("CHF", BigDecimal.valueOf(5d)).isEqualTo(
				RoundedMoney.of("CHF", BigDecimal.valueOf(5))));
		assertTrue(RoundedMoney.of("CHF", BigDecimal.valueOf(1d)).isEqualTo(
				RoundedMoney.of("CHF", BigDecimal.valueOf(1.00))));
		assertTrue(RoundedMoney.of("CHF", BigDecimal.valueOf(1d)).isEqualTo(
				RoundedMoney.of("CHF", BigDecimal.ONE)));
		assertTrue(RoundedMoney.of("CHF", BigDecimal.valueOf(1)).isEqualTo(
				RoundedMoney.of("CHF", BigDecimal.ONE)));
		assertTrue(RoundedMoney.of("CHF", new BigDecimal("1.0000")).isEqualTo(
				RoundedMoney.of("CHF", new BigDecimal("1.00"))));
	}

	/**
	 * Test method for
	 * {@link javax.money.RoundedMoney#isNotEqualTo(javax.money.MonetaryAmount)}
	 * .
	 */
	@Test
	public void testIsNotEqualTo() {
		assertFalse(RoundedMoney.of("CHF", BigDecimal.valueOf(0d))
				.isNotEqualTo(
						RoundedMoney.of("CHF", BigDecimal.valueOf(0))));
		assertTrue(RoundedMoney.of("CHF", BigDecimal.valueOf(0.00000000001d))
				.isNotEqualTo(RoundedMoney.of("CHF", BigDecimal.valueOf(0d))));
		assertFalse(RoundedMoney.of("CHF", BigDecimal.valueOf(5d))
				.isNotEqualTo(
						RoundedMoney.of("CHF", BigDecimal.valueOf(5))));
		assertFalse(RoundedMoney.of("CHF", BigDecimal.valueOf(1d))
				.isNotEqualTo(
						RoundedMoney.of("CHF", BigDecimal.valueOf(1.00))));
		assertFalse(RoundedMoney.of("CHF", BigDecimal.valueOf(1d))
				.isNotEqualTo(
						RoundedMoney.of("CHF", BigDecimal.ONE)));
		assertFalse(RoundedMoney.of("CHF", BigDecimal.valueOf(1)).isNotEqualTo(
				RoundedMoney.of("CHF", BigDecimal.ONE)));
		assertFalse(RoundedMoney.of("CHF", new BigDecimal("1.0000"))
				.isNotEqualTo(
						RoundedMoney.of("CHF", new BigDecimal("1.00"))));
	}

	/**
	 * Test method for
	 * {@link javax.money.RoundedMoney#query(javax.money.MonetaryQuery)}
	 * .
	 */
	@Test
	public void testQuery() {
		MonetaryQuery<Integer> q = new MonetaryQuery<Integer>() {
			@Override
			public Integer queryFrom(MonetaryAmount amount) {
				return RoundedMoney.from(amount).getPrecision();
			}
		};
		RoundedMoney[] moneys = new RoundedMoney[] {
				RoundedMoney.of("CHF", 100),
				RoundedMoney.of("USD", 34242344),
				RoundedMoney.of("EUR", 23123213.435),
				RoundedMoney.of("USS", -23123213.435),
				RoundedMoney.of("USN", -23123213),
				RoundedMoney.of("GBP", 0) };
		for (int i = 0; i < moneys.length; i++) {
			assertEquals((Integer) moneys[i].query(q),
					(Integer) moneys[i].getPrecision());
		}
	}

	/**
	 * Test method for
	 * {@link javax.money.RoundedMoney#asType(java.lang.Class)}.
	 */
	@Test
	public void testgetNumberClassOfT() {
		RoundedMoney m = RoundedMoney.of("CHF", 13.656);
		assertEquals(m.getNumber(Byte.class), Byte.valueOf((byte) 13));
		assertEquals(m.getNumber(Short.class), Short.valueOf((short) 13));
		assertEquals(m.getNumber(Integer.class), Integer.valueOf(13));
		assertEquals(m.getNumber(Long.class), Long.valueOf(13L));
		assertEquals(m.getNumber(Float.class), Float.valueOf(13.656f));
		assertEquals(m.getNumber(Double.class), Double.valueOf(13.656));
		assertEquals(
				m.getNumber(BigDecimal.class).setScale(3,
						RoundingMode.HALF_EVEN),
				BigDecimal.valueOf(13.656));
		assertEquals(m.asType(BigDecimal.class), m.getNumber());
	}

	/**
	 * Test method for {@link javax.money.RoundedMoney#asNumber()}.
	 */
	@Test
	public void testGetNumber() {
		assertEquals(BigDecimal.ZERO, RoundedMoney.of("CHF", 0).getNumber());
		assertEquals(BigDecimal.valueOf(100034L),
				RoundedMoney.of("CHF", 100034L)
						.getNumber());
		assertEquals(new BigDecimal("0.34738746"), RoundedMoney
				.of("CHF", new BigDecimal("0.34738746")).getNumber());
	}

	/**
	 * Test method for {@link javax.money.RoundedMoney#toString()}.
	 */
	@Test
	public void testToString() {
		assertEquals("XXX 1.23455645",
				RoundedMoney.of("XXX", new BigDecimal("1.23455645"))
						.toString());
		assertEquals("CHF 1234", RoundedMoney.of("CHF", 1234).toString());
		assertEquals("CHF 1234.0",
				RoundedMoney.of("CHF", new BigDecimal("1234.0"))
						.toString());
		assertEquals("CHF 1234.1",
				RoundedMoney.of("CHF", new BigDecimal("1234.1"))
						.toString());
		assertEquals("CHF 0.0100",
				RoundedMoney.of("CHF", new BigDecimal("0.0100"))
						.toString());
	}

	// /**
	// * Test method for
	// * {@link org.javamoney.moneta.RoundedMoney#getAmountWhole()}.
	// */
	// @Test
	// public void testGetAmountWhole() {
	// assertEquals(1, RoundedMoney.of("XXX", 1.23455645d).getAmountWhole());
	// assertEquals(1, RoundedMoney.of("CHF", 1).getAmountWhole());
	// assertEquals(11, RoundedMoney.of("CHF", 11.0d).getAmountWhole());
	// assertEquals(1234, RoundedMoney.of("CHF", 1234.1d).getAmountWhole());
	// assertEquals(0, RoundedMoney.of("CHF", 0.0100d).getAmountWhole());
	// }
	//
	// /**
	// * Test method for
	// * {@link org.javamoney.moneta.RoundedMoney#getAmountFractionNumerator()}.
	// */
	// @Test
	// public void testGetAmountFractionNumerator() {
	// assertEquals(0, RoundedMoney.of("XXX", new BigDecimal("1.23455645"))
	// .getAmountFractionNumerator());
	// assertEquals(0, RoundedMoney.of("CHF", 1).getAmountFractionNumerator());
	// assertEquals(0, RoundedMoney.of("CHF", new BigDecimal("11.0"))
	// .getAmountFractionNumerator());
	// assertEquals(10L, RoundedMoney.of("CHF", new BigDecimal("1234.1"))
	// .getAmountFractionNumerator());
	// assertEquals(1L, RoundedMoney.of("CHF", new BigDecimal("0.0100"))
	// .getAmountFractionNumerator());
	// }
	//
	// /**
	// * Test method for
	// * {@link
	// org.javamoney.moneta.RoundedMoney#getAmountFractionDenominator()}.
	// */
	// @Test
	// public void testGetAmountFractionDenominator() {
	// assertEquals(1, RoundedMoney.of("XXX", new BigDecimal("1.23455645"))
	// .getAmountFractionDenominator());
	// assertEquals(100, RoundedMoney.of("CHF", 1)
	// .getAmountFractionDenominator());
	// assertEquals(100, RoundedMoney.of("CHF", new BigDecimal("11.0"))
	// .getAmountFractionDenominator());
	// assertEquals(100L, RoundedMoney.of("CHF", new BigDecimal("1234.1"))
	// .getAmountFractionDenominator());
	// assertEquals(100L, RoundedMoney.of("CHF", new BigDecimal("0.0100"))
	// .getAmountFractionDenominator());
	// }

	/**
	 * Test method for
	 * {@link javax.money.RoundedMoney#with(javax.money.MonetaryAdjuster)}
	 * .
	 */
	@Test
	public void testWithMonetaryOperator() {
		MonetaryOperator adj = new MonetaryOperator() {
			@Override
			public <T extends MonetaryAmount<?>> T apply(T amount) {
				return (T) amount.with(amount.getCurrency(), -100);
			}
		};
		RoundedMoney m = RoundedMoney.of("USD", new BigDecimal("1.23645"));
		RoundedMoney a = m.with(adj);
		assertNotNull(a);
		assertNotSame(m, a);
		assertEquals(m.getCurrency(), a.getCurrency());
		assertEquals(RoundedMoney.of(m.getCurrency(), -100), a);
		adj = new MonetaryOperator() {
			@Override
			public <T extends MonetaryAmount<?>> T apply(T amount) {
				return (T) amount.multiply(2)
						.with(MoneyCurrency.of("CHF"));
			}
		};
		a = m.with(adj);
		assertNotNull(a);
		assertNotSame(m, a);
		assertEquals(MoneyCurrency.of("CHF"), a.getCurrency());
		assertEquals(RoundedMoney.of(a.getCurrency(), new BigDecimal(
				"2.47")), a);
	}

	/**
	 * Test method for
	 * {@link javax.money.RoundedMoney#from(javax.money.MonetaryAmount)}
	 * .
	 */
	@Test
	public void testFrom() {
		RoundedMoney m = RoundedMoney.of("XXX", new BigDecimal("1.2345"));
		RoundedMoney m2 = RoundedMoney.from(m);
		assertTrue(m == m2);
		FastMoney fm = FastMoney.of("XXX", new BigDecimal("1.2345"));
		m2 = RoundedMoney.from(fm);
		assertFalse(m == m2);
		assertEquals(m, m2);
	}

	@Test
	public void testSerialization() throws IOException, ClassNotFoundException {
		RoundedMoney m = RoundedMoney.of("XXX", new BigDecimal("1.2345"));
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		ObjectOutputStream oos = new ObjectOutputStream(bos);
		oos.writeObject(m);
		oos.flush();
		ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(
				bos.toByteArray()));
		RoundedMoney m2 = (RoundedMoney) ois.readObject();
		assertEquals(m, m2);
		assertTrue(m != m2);
	}

	// Bad Cases

	/**
	 * Test method for
	 * {@link javax.money.RoundedMoney#add(javax.money.MonetaryAmount)}
	 * .
	 */
	@Test(expected = IllegalArgumentException.class)
	public void testAdd_WrongCurrency() {
		RoundedMoney m1 = RoundedMoney.of(EURO, BigDecimal.TEN);
		RoundedMoney m2 = RoundedMoney.of("CHF", BigDecimal.TEN);
		m1.add(m2);
	}

	/**
	 * Test method for
	 * {@link javax.money.RoundedMoney#add(javax.money.MonetaryAmount)}
	 * .
	 */
	@Test(expected = IllegalArgumentException.class)
	public void testSubtract_WrongCurrency() {
		RoundedMoney m1 = RoundedMoney.of(EURO, BigDecimal.TEN);
		RoundedMoney m2 = RoundedMoney.of("CHF", BigDecimal.TEN);
		m1.subtract(m2);
	}

	/**
	 * Test method for
	 * {@link javax.money.RoundedMoney#add(javax.money.MonetaryAmount)}
	 * .
	 */
	@Test(expected = IllegalArgumentException.class)
	public void testDivide_WrongCurrency() {
		RoundedMoney m1 = RoundedMoney.of(EURO, BigDecimal.TEN);
		RoundedMoney m2 = RoundedMoney.of("CHF", BigDecimal.TEN);
		m1.subtract(m2);
	}

}
