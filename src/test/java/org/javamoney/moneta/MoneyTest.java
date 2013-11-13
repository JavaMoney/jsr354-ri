/**
 * 
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

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.MathContext;
import java.math.RoundingMode;

import javax.money.CurrencyUnit;
import javax.money.MonetaryAdjuster;
import javax.money.MonetaryAmount;

import org.junit.Test;

/**
 * @author Anatole
 * 
 */
public class MoneyTest {

	private static final BigDecimal TEN = new BigDecimal(10.0d);
	protected static final CurrencyUnit EURO = MoneyCurrency.of("EUR");
	protected static final CurrencyUnit DOLLAR = MoneyCurrency
			.of("USD");

	/**
	 * Test method for
	 * {@link org.javamoney.moneta.Money#of(javax.money.CurrencyUnit, java.math.BigDecimal)}
	 * .
	 */
	@Test
	public void testOfCurrencyUnitBigDecimal() {
		Money m = Money.of(MoneyCurrency.of("EUR"), TEN);
		assertEquals(TEN, m.asType(BigDecimal.class));
	}

	@Test
	public void testOfCurrencyUnitDouble() {
		Money m = Money.of(MoneyCurrency.of("EUR"), 10.0d);
		assertTrue(TEN.doubleValue() == m.doubleValue());
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
		assertEquals(9d, moneyResult.doubleValue(), 0d);
	}

	@Test
	public void testDivideAndRemainder_BigDecimal() {
		Money money1 = Money.of(EURO, BigDecimal.ONE);
		Money[] divideAndRemainder = money1.divideAndRemainder(new BigDecimal(
				"0.50000000000000000001"));
		assertThat(divideAndRemainder[0].asType(BigDecimal.class),
				equalTo(BigDecimal.ONE));
		assertThat(divideAndRemainder[1].asType(BigDecimal.class),
				equalTo(new BigDecimal("0.49999999999999999999")));
	}

	@Test
	public void testDivideToIntegralValue_BigDecimal() {
		Money money1 = Money.of(EURO, BigDecimal.ONE);
		Money result = money1.divideToIntegralValue(new BigDecimal(
				"0.50000000000000000001"));
		assertThat(result.asType(BigDecimal.class), equalTo(BigDecimal.ONE));
	}

	@Test
	public void comparePerformance() {
		Money money1 = Money.of(EURO, BigDecimal.ONE);
		long start = System.currentTimeMillis();
		final int NUM = 1000000;
		for (int i = 0; i < NUM; i++) {
			money1 = money1.add(Money.of(EURO, 1234567.3444));
			money1 = money1.subtract(Money.of(EURO, 232323));
			money1 = money1.multiply(3.4);
			money1 = money1.divide(5.456);
			// money1 = money1.with(MonetaryRoundings.getRounding());
		}
		long end = System.currentTimeMillis();
		long duration = end - start;
		System.out.println("Duration for 1000000 operations (Money/BD): "
				+ duration + " ms (" + ((duration * 1000) / NUM)
				+ " ns per loop) -> "
				+ money1);

		FastMoney money2 = FastMoney.of(EURO, BigDecimal.ONE);
		start = System.currentTimeMillis();
		for (int i = 0; i < NUM; i++) {
			money2 = money2.add(FastMoney.of(EURO, 1234567.3444));
			money2 = money2.subtract(FastMoney.of(EURO, 232323));
			money2 = money2.multiply(3.4);
			money2 = money2.divide(5.456);
			// money2 = money1.with(MonetaryRoundings.getRounding());
		}
		end = System.currentTimeMillis();
		duration = end - start;
		System.out.println("Duration for " + NUM
				+ " operations (IntegralMoney/long): "
				+ duration + " ms (" + ((duration * 1000) / NUM)
				+ " ns per loop) -> "
				+ money2);

		FastMoney money3 = FastMoney.of(EURO, BigDecimal.ONE);
		start = System.currentTimeMillis();
		for (int i = 0; i < NUM; i++) {
			money3 = money3.add(Money.of(EURO, 1234567.3444));
			money3 = money3.subtract(FastMoney.of(EURO, 232323));
			money3 = money3.multiply(3.4);
			money3 = money3.divide(5.456);
			// money3 = money3.with(MonetaryRoundings.getRounding());
		}
		end = System.currentTimeMillis();
		duration = end - start;
		System.out.println("Duration for " + NUM
				+ " operations (IntegralMoney/Money mixed): "
				+ duration + " ms (" + ((duration * 1000) / NUM)
				+ " ns per loop) -> "
				+ money3);
	}

	/**
	 * Test method for {@link org.javamoney.moneta.Money#hashCode()}.
	 */
	@Test
	public void testHashCode() {
		Money money1 = Money.of(EURO, BigDecimal.ONE);
		Money money2 = Money.of(EURO, 1.0);
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
	}

	/**
	 * Test method for
	 * {@link org.javamoney.moneta.Money#getDefaultMathContext()}.
	 */
	@Test
	public void testGetDefaultMathContext() {
		Money money1 = Money.of(EURO, BigDecimal.ONE);
		assertEquals(money1.getMathContext(), MathContext.DECIMAL64);
	}

	/**
	 * Test method for
	 * {@link org.javamoney.moneta.Money#of(javax.money.CurrencyUnit, java.math.BigDecimal, java.math.MathContext)}
	 * .
	 */
	@Test
	public void testOfCurrencyUnitBigDecimalMathContext() {
		Money m = Money.of(EURO, BigDecimal.valueOf(2.15), new MathContext(2,
				RoundingMode.DOWN));
		Money m2 = Money.of(EURO, BigDecimal.valueOf(2.1));
		assertEquals(m, m2);
		Money m3 = m.multiply(100);
		assertEquals(Money.of(EURO,210), m3.abs());
	}

	/**
	 * Test method for
	 * {@link org.javamoney.moneta.Money#of(javax.money.CurrencyUnit, java.lang.Number)}
	 * .
	 */
	@Test
	public void testOfCurrencyUnitNumber() {
		Money m = Money.of(EURO, (byte) 2);
		assertNotNull(m);
		assertEquals(EURO, m.getCurrency());
		assertEquals(Byte.valueOf((byte) 2), m.asType(Byte.class));
		m = Money.of(DOLLAR, (short) -2);
		assertNotNull(m);
		assertEquals(DOLLAR, m.getCurrency());
		assertEquals(Short.valueOf((short) -2), m.asType(Short.class));
		m = Money.of(EURO, (int) -12);
		assertNotNull(m);
		assertEquals(EURO, m.getCurrency());
		assertEquals(Integer.valueOf((int) -12), m.asType(Integer.class));
		m = Money.of(DOLLAR, (long) 12);
		assertNotNull(m);
		assertEquals(DOLLAR, m.getCurrency());
		assertEquals(Long.valueOf((long) 12), m.asType(Long.class));
		m = Money.of(EURO, (float) 12.23);
		assertNotNull(m);
		assertEquals(EURO, m.getCurrency());
		assertEquals(Float.valueOf((float) 12.23), m.asType(Float.class));
		m = Money.of(DOLLAR, (double) -12.23);
		assertNotNull(m);
		assertEquals(DOLLAR, m.getCurrency());
		assertEquals(Double.valueOf((double) -12.23), m.asType(Double.class));
		m = Money.of(EURO, (Number) BigDecimal.valueOf(234.2345));
		assertNotNull(m);
		assertEquals(EURO, m.getCurrency());
		assertEquals(BigDecimal.valueOf(234.2345), m.asType(BigDecimal.class));
		m = Money.of(DOLLAR, (Number) BigInteger.valueOf(23232312321432432L));
		assertNotNull(m);
		assertEquals(DOLLAR, m.getCurrency());
		assertEquals(Long.valueOf(23232312321432432L), m.asType(Long.class));
		assertEquals(BigInteger.valueOf(23232312321432432L),
				m.asType(BigInteger.class));
	}

	/**
	 * Test method for
	 * {@link org.javamoney.moneta.Money#of(javax.money.CurrencyUnit, java.lang.Number, java.math.MathContext)}
	 * .
	 */
	@Test
	public void testOfCurrencyUnitNumberMathContext() {
		MathContext mc = new MathContext(2345, RoundingMode.CEILING);
		Money m = Money.of(EURO, (byte) 2, mc);
		assertNotNull(m);
		assertEquals(mc, m.getMathContext());
		assertEquals(EURO, m.getCurrency());
		assertEquals(Byte.valueOf((byte) 2), m.asType(Byte.class));
		m = Money.of(DOLLAR, (short) -2, mc);
		assertNotNull(m);
		assertEquals(mc, m.getMathContext());
		assertEquals(DOLLAR, m.getCurrency());
		assertEquals(Short.valueOf((short) -2), m.asType(Short.class));
		m = Money.of(EURO, (int) -12, mc);
		assertNotNull(m);
		assertEquals(mc, m.getMathContext());
		assertEquals(EURO, m.getCurrency());
		assertEquals(Integer.valueOf((int) -12), m.asType(Integer.class));
		m = Money.of(DOLLAR, (long) 12, mc);
		assertEquals(mc, m.getMathContext());
		assertNotNull(m);
		assertEquals(DOLLAR, m.getCurrency());
		assertEquals(Long.valueOf((long) 12), m.asType(Long.class));
		m = Money.of(EURO, (float) 12.23, mc);
		assertNotNull(m);
		assertEquals(mc, m.getMathContext());
		assertEquals(EURO, m.getCurrency());
		assertEquals(Float.valueOf((float) 12.23), m.asType(Float.class));
		m = Money.of(DOLLAR, (double) -12.23, mc);
		assertNotNull(m);
		assertEquals(mc, m.getMathContext());
		assertEquals(DOLLAR, m.getCurrency());
		assertEquals(mc, m.getMathContext());
		assertEquals(Double.valueOf((double) -12.23), m.asType(Double.class));
		m = Money.of(EURO, (Number) BigDecimal.valueOf(234.2345), mc);
		assertNotNull(m);
		assertEquals(EURO, m.getCurrency());
		assertEquals(mc, m.getMathContext());
		assertEquals(BigDecimal.valueOf(234.2345), m.asType(BigDecimal.class));
		m = Money.of(DOLLAR, (Number) BigInteger.valueOf(23232312321432432L),
				mc);
		assertNotNull(m);
		assertEquals(DOLLAR, m.getCurrency());
		assertEquals(mc, m.getMathContext());
		assertEquals(Long.valueOf(23232312321432432L), m.asType(Long.class));
		assertEquals(BigInteger.valueOf(23232312321432432L),
				m.asType(BigInteger.class));
	}

	/**
	 * Test method for
	 * {@link org.javamoney.moneta.Money#of(java.lang.String, java.lang.Number)}
	 * .
	 */
	@Test
	public void testOfStringNumber() {
		Money m = Money.of("EUR", (byte) 2);
		assertNotNull(m);
		assertEquals(EURO, m.getCurrency());
		assertEquals(Byte.valueOf((byte) 2), m.asType(Byte.class));
		m = Money.of("USD", (short) -2);
		assertNotNull(m);
		assertEquals(DOLLAR, m.getCurrency());
		assertEquals(Short.valueOf((short) -2), m.asType(Short.class));
		m = Money.of("EUR", (int) -12);
		assertNotNull(m);
		assertEquals(EURO, m.getCurrency());
		assertEquals(Integer.valueOf((int) -12), m.asType(Integer.class));
		m = Money.of("USD", (long) 12);
		assertNotNull(m);
		assertEquals(DOLLAR, m.getCurrency());
		assertEquals(Long.valueOf((long) 12), m.asType(Long.class));
		m = Money.of("EUR", (float) 12.23);
		assertNotNull(m);
		assertEquals(EURO, m.getCurrency());
		assertEquals(Float.valueOf((float) 12.23), m.asType(Float.class));
		m = Money.of("USD", (double) -12.23);
		assertNotNull(m);
		assertEquals(DOLLAR, m.getCurrency());
		assertEquals(Double.valueOf((double) -12.23), m.asType(Double.class));
		m = Money.of("EUR", (Number) BigDecimal.valueOf(234.2345));
		assertNotNull(m);
		assertEquals(EURO, m.getCurrency());
		assertEquals(BigDecimal.valueOf(234.2345), m.asType(BigDecimal.class));
		m = Money.of("USD", (Number) BigInteger.valueOf(23232312321432432L));
		assertNotNull(m);
		assertEquals(DOLLAR, m.getCurrency());
		assertEquals(Long.valueOf(23232312321432432L), m.asType(Long.class));
		assertEquals(BigInteger.valueOf(23232312321432432L),
				m.asType(BigInteger.class));
	}

	/**
	 * Test method for
	 * {@link org.javamoney.moneta.Money#of(java.lang.String, java.lang.Number, java.math.MathContext)}
	 * .
	 */
	@Test
	public void testOfStringNumberMathContext() {
		MathContext mc = new MathContext(2345, RoundingMode.CEILING);
		Money m = Money.of("EUR", (byte) 2, mc);
		assertNotNull(m);
		assertEquals(mc, m.getMathContext());
		assertEquals(EURO, m.getCurrency());
		assertEquals(Byte.valueOf((byte) 2), m.asType(Byte.class));
		m = Money.of("USD", (short) -2, mc);
		assertNotNull(m);
		assertEquals(mc, m.getMathContext());
		assertEquals(DOLLAR, m.getCurrency());
		assertEquals(Short.valueOf((short) -2), m.asType(Short.class));
		m = Money.of("EUR", (int) -12, mc);
		assertNotNull(m);
		assertEquals(mc, m.getMathContext());
		assertEquals(EURO, m.getCurrency());
		assertEquals(Integer.valueOf((int) -12), m.asType(Integer.class));
		m = Money.of("USD", (long) 12, mc);
		assertEquals(mc, m.getMathContext());
		assertNotNull(m);
		assertEquals(DOLLAR, m.getCurrency());
		assertEquals(Long.valueOf((long) 12), m.asType(Long.class));
		m = Money.of("EUR", (float) 12.23, mc);
		assertNotNull(m);
		assertEquals(mc, m.getMathContext());
		assertEquals(EURO, m.getCurrency());
		assertEquals(Float.valueOf((float) 12.23), m.asType(Float.class));
		m = Money.of("USD", (double) -12.23, mc);
		assertNotNull(m);
		assertEquals(mc, m.getMathContext());
		assertEquals(DOLLAR, m.getCurrency());
		assertEquals(mc, m.getMathContext());
		assertEquals(Double.valueOf((double) -12.23), m.asType(Double.class));
		m = Money.of("EUR", (Number) BigDecimal.valueOf(234.2345), mc);
		assertNotNull(m);
		assertEquals(EURO, m.getCurrency());
		assertEquals(mc, m.getMathContext());
		assertEquals(BigDecimal.valueOf(234.2345), m.asType(BigDecimal.class));
		m = Money
				.of("USD", (Number) BigInteger.valueOf(23232312321432432L), mc);
		assertNotNull(m);
		assertEquals(DOLLAR, m.getCurrency());
		assertEquals(mc, m.getMathContext());
		assertEquals(Long.valueOf(23232312321432432L), m.asType(Long.class));
		assertEquals(BigInteger.valueOf(23232312321432432L),
				m.asType(BigInteger.class));
	}

	/**
	 * Test method for
	 * {@link org.javamoney.moneta.Money#ofZero(javax.money.CurrencyUnit)}.
	 */
	@Test
	public void testOfZeroCurrencyUnit() {
		Money m = Money.ofZero(MoneyCurrency.of("USD"));
		assertNotNull(m);
		assertEquals(MoneyCurrency.of("USD"), m.getCurrency());
		assertEquals(m.doubleValue(), 0d, 0d);
	}

	/**
	 * Test method for
	 * {@link org.javamoney.moneta.Money#ofZero(java.lang.String)}.
	 */
	@Test
	public void testOfZeroString() {
		Money m = Money.ofZero("CHF");
		assertNotNull(m);
		assertEquals(MoneyCurrency.of("CHF"), m.getCurrency());
		assertEquals(m.doubleValue(), 0d, 0d);
	}

	/**
	 * Test method for
	 * {@link org.javamoney.moneta.Money#equals(java.lang.Object)}.
	 */
	@Test
	public void testEqualsObject() {
		fail("Not yet implemented");
	}

	/**
	 * Test method for
	 * {@link org.javamoney.moneta.Money#compareTo(javax.money.MonetaryAmount)}.
	 */
	@Test
	public void testCompareTo() {
		fail("Not yet implemented");
	}

	/**
	 * Test method for {@link org.javamoney.moneta.Money#getMathContext()}.
	 */
	@Test
	public void testGetMathContext() {
		Money m = Money.of("CHF", 10);
		assertEquals(MathContext.DECIMAL64, m.getMathContext());
		m = Money.of("CHF", 10, MathContext.DECIMAL128);
		assertEquals(MathContext.DECIMAL128, m.getMathContext());
	}

	/**
	 * Test method for
	 * {@link org.javamoney.moneta.Money#with(java.math.MathContext)}.
	 */
	@Test
	public void testWithMathContext() {
		Money m = Money.of("CHF", 10);
		assertEquals(MathContext.DECIMAL64, m.getMathContext());
		Money m2 = m.with(MathContext.DECIMAL128);
		assertNotNull(m2);
		assertTrue(m != m2);
		assertEquals(MathContext.DECIMAL64, m.getMathContext());
		assertEquals(MathContext.DECIMAL128, m2.getMathContext());
	}

	/**
	 * Test method for {@link org.javamoney.moneta.Money#abs()}.
	 */
	@Test
	public void testAbs() {
		fail("Not yet implemented");
	}

	/**
	 * Test method for
	 * {@link org.javamoney.moneta.Money#add(javax.money.MonetaryAmount)} .
	 */
	@Test
	public void testAdd() {
		Money money1 = Money.of(EURO, BigDecimal.TEN);
		Money money2 = Money.of(EURO, BigDecimal.ONE);
		Money moneyResult = money1.add(money2);
		assertNotNull(moneyResult);
		assertEquals(11d, moneyResult.doubleValue(), 0d);
	}

	/**
	 * Test method for
	 * {@link org.javamoney.moneta.Money#divide(javax.money.MonetaryAmount)}.
	 */
	@Test
	public void testDivideMonetaryAmount() {
		fail("Not yet implemented");
	}

	/**
	 * Test method for
	 * {@link org.javamoney.moneta.Money#divide(java.lang.Number)}.
	 */
	@Test
	public void testDivideNumber() {
		fail("Not yet implemented");
	}

	/**
	 * Test method for
	 * {@link org.javamoney.moneta.Money#divideAndRemainder(javax.money.MonetaryAmount)}
	 * .
	 */
	@Test
	public void testDivideAndRemainderMonetaryAmount() {
		fail("Not yet implemented");
	}

	/**
	 * Test method for
	 * {@link org.javamoney.moneta.Money#divideAndRemainder(java.lang.Number)}.
	 */
	@Test
	public void testDivideAndRemainderNumber() {
		fail("Not yet implemented");
	}

	/**
	 * Test method for
	 * {@link org.javamoney.moneta.Money#divideToIntegralValue(javax.money.MonetaryAmount)}
	 * .
	 */
	@Test
	public void testDivideToIntegralValueMonetaryAmount() {
		fail("Not yet implemented");
	}

	/**
	 * Test method for
	 * {@link org.javamoney.moneta.Money#divideToIntegralValue(java.lang.Number)}
	 * .
	 */
	@Test
	public void testDivideToIntegralValueNumber() {
		fail("Not yet implemented");
	}

	/**
	 * Test method for
	 * {@link org.javamoney.moneta.Money#multiply(javax.money.MonetaryAmount)}.
	 */
	@Test
	public void testMultiplyMonetaryAmount() {
		Money m = Money.of("CHF", 100);
		assertEquals(Money.of("CHF", 400), m.multiply(Money.of("CHF", 4)));
		assertEquals(Money.of("CHF", 200), m.multiply(Money.of("CHF", 2)));
		assertEquals(Money.of("CHF", new BigDecimal("50.0")),
				m.multiply(Money.of("CHF", 0.5)));
	}

	/**
	 * Test method for
	 * {@link org.javamoney.moneta.Money#multiply(java.lang.Number)}.
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
	 * Test method for
	 * {@link org.javamoney.moneta.Money#subtract(javax.money.MonetaryAmount)}.
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
		assertEquals(Money.of("CHF", -123.234 - 100), m.subtract(s1));
		assertEquals(Money.of("CHF", -123.234 - 200), m.subtract(s2));
		assertEquals(Money.of("CHF", -123.234), m.subtract(s3));
		assertTrue(m == m.subtract(s3));
		m = Money.of("CHF", 12.402345534);
		s1 = Money.of("CHF", 2343.45);
		s2 = Money.of("CHF", 12.402345534);
		s3 = Money.of("CHF", -2343.45);
		assertEquals(Money.of("CHF", 12.402345534 - 2343.45), m.subtract(s1));
		assertEquals(Money.of("CHF", 12.402345534 - 12.402345534),
				m.subtract(s2));
		assertEquals(Money.of("CHF", 0), m.subtract(s2));
		assertEquals(Money.of("CHF", 12.402345534 + 2343.45), m.subtract(s3));
		assertTrue(m == m.subtract(s3));
	}

	/**
	 * Test method for {@link org.javamoney.moneta.Money#pow(int)}.
	 */
	@Test
	public void testPow() {
		Money m = Money.of("CHF", 23.234);
		for (int p = 0; p < 100; p++) {
			assertEquals(Money.of("CHF", BigDecimal.valueOf(23.234).pow(p)),
					m.pow(p));
		}
	}

	/**
	 * Test method for {@link org.javamoney.moneta.Money#ulp()}.
	 */
	@Test
	public void testUlp() {
		Money[] moneys = new Money[] { Money.of("CHF", 100),
				Money.of("CHF", 34242344), Money.of("CHF", 23123213.435),
				Money.of("CHF", 0), Money.of("CHF", -100),
				Money.of("CHF", -723527.36532) };
		for (Money m : moneys) {
			assertEquals("Invalid ulp.",
					m.with(m.asType(BigDecimal.class).ulp()), m.ulp());
		}
	}

	/**
	 * Test method for
	 * {@link org.javamoney.moneta.Money#remainder(javax.money.MonetaryAmount)}.
	 */
	@Test
	public void testRemainderMonetaryAmount() {
		fail("Not yet implemented");
	}

	/**
	 * Test method for
	 * {@link org.javamoney.moneta.Money#remainder(java.lang.Number)}.
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
	 * Test method for {@link org.javamoney.moneta.Money#scaleByPowerOfTen(int)}
	 * .
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
						m.with(m.asType(BigDecimal.class).scaleByPowerOfTen(p)),
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
	 * Test method for {@link org.javamoney.moneta.Money#with(java.lang.Number)}
	 * .
	 */
	@Test
	public void testWithNumber() {
		Money[] moneys = new Money[] {
				Money.of("CHF", 100),
				Money.of("CHF", 34242344), Money.of("CHF", 23123213.435),
				Money.of("CHF", -23123213.435), Money.of("CHF", -23123213),
				Money.of("CHF", 0) };
		Money s = Money.of("CHF", 10);
		Money[] moneys2 = new Money[] {
				s.with(100),
				s.with(34242344), s.with(23123213.435),
				s.with(-23123213.435), s.with(-23123213),
				s.with(0) };
		for (int i = 0; i < moneys.length; i++) {
			assertEquals("with(Number) failed.", moneys[i], moneys2[i]);
		}
	}

	/**
	 * Test method for
	 * {@link org.javamoney.moneta.Money#with(javax.money.CurrencyUnit, java.lang.Number)}
	 * .
	 */
	@Test
	public void testWithCurrencyUnitNumber() {
		Money[] moneys = new Money[] {
				Money.of("CHF", 100),
				Money.of("USD", 34242344), Money.of("EUR", 23123213.435),
				Money.of("USS", -23123213.435), Money.of("USN", -23123213),
				Money.of("GBP", 0) };
		Money s = Money.of("CHF", 10);
		Money[] moneys2 = new Money[] {
				s.with(MoneyCurrency.of("CHF"), 100),
				s.with(MoneyCurrency.of("USD"), 34242344),
				s.with(MoneyCurrency.of("EUR"), 23123213.435),
				s.with(MoneyCurrency.of("USS"), -23123213.435),
				s.with(MoneyCurrency.of("USN"), -23123213),
				s.with(MoneyCurrency.of("GBP"), 0) };
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
			assertEquals("Scale for " + m, m.asType(BigDecimal.class).scale(),
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
			assertEquals("Precision for " + m, m.asType(BigDecimal.class)
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
		assertEquals("longValue of " + m, 100L, m.longValue());
		m = Money.of("CHF", -100);
		assertEquals("longValue of " + m, -100L, m.longValue());
		m = Money.of("CHF", -100.3434);
		assertEquals("longValue of " + m, -100L, m.longValue());
		m = Money.of("CHF", 100.3434);
		assertEquals("longValue of " + m, 100L, m.longValue());
		m = Money.of("CHF", 0);
		assertEquals("longValue of " + m, 0L, m.longValue());
		m = Money.of("CHF", -0.0);
		assertEquals("longValue of " + m, 0L, m.longValue());
		m = Money.of("CHF", Long.MAX_VALUE);
		assertEquals("longValue of " + m, Long.MAX_VALUE, m.longValue());
		m = Money.of("CHF", Long.MIN_VALUE);
		assertEquals("longValue of " + m, Long.MIN_VALUE, m.longValue());
//		try {
			m = Money
					.of("CHF",
							new BigDecimal(
									"12121762517652176251725178251872652765321876352187635217835378125"));
			m.longValue();
//			fail("longValue(12121762517652176251725178251872652765321876352187635217835378125) should fail!");
//		} catch (ArithmeticException e) {
//			// OK
//		}
	}

	/**
	 * Test method for {@link org.javamoney.moneta.Money#longValueExact()}.
	 */
	@Test
	public void testLongValueExact() {
		Money m = Money.of("CHF", 100);
		assertEquals("longValue of " + m, 100L, m.longValueExact());
		m = Money.of("CHF", -100);
		assertEquals("longValue of " + m, -100L, m.longValueExact());
		m = Money.of("CHF", 0);
		assertEquals("longValue of " + m, 0L, m.longValueExact());
		m = Money.of("CHF", -0.0);
		assertEquals("longValue of " + m, 0L, m.longValueExact());
		m = Money.of("CHF", Long.MAX_VALUE);
		assertEquals("longValue of " + m, Long.MAX_VALUE, m.longValueExact());
		m = Money.of("CHF", Long.MIN_VALUE);
		assertEquals("longValue of " + m, Long.MIN_VALUE, m.longValueExact());
		try {
			m = Money
					.of("CHF",
							new BigDecimal(
									"12121762517652176251725178251872652765321876352187635217835378125"));
			m.longValueExact();
			fail("longValueExact(12121762517652176251725178251872652765321876352187635217835378125) should fail!");
		} catch (ArithmeticException e) {
			// OK
		}
		try {
			m = Money.of("CHF", -100.3434);
			m.longValueExact();
			fail("longValueExact(-100.3434) should raise an ArithmeticException.");
		} catch (ArithmeticException e) {
			// OK
		}
		try {
			m = Money.of("CHF", 100.3434);
			m.longValueExact();
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
		assertEquals("doubleValue of " + m, 100d, m.doubleValue(), 0.0d);
		m = Money.of("CHF", -100);
		assertEquals("doubleValue of " + m, -100d, m.doubleValue(), 0.0d);
		m = Money.of("CHF", -100.3434);
		assertEquals("doubleValue of " + m, -100.3434, m.doubleValue(), 0.0d);
		m = Money.of("CHF", 100.3434);
		assertEquals("doubleValue of " + m, 100.3434, m.doubleValue(), 0.0d);
		m = Money.of("CHF", 0);
		assertEquals("doubleValue of " + m, 0d, m.doubleValue(), 0.0d);
		m = Money.of("CHF", -0.0);
		assertEquals("doubleValue of " + m, 0d, m.doubleValue(), 0.0d);
		m = Money.of("CHF", Double.MAX_VALUE);
		assertEquals("doubleValue of " + m, Double.MAX_VALUE, m.doubleValue(),
				0.0d);
		m = Money.of("CHF", Double.MIN_VALUE);
		assertEquals("doubleValue of " + m, Double.MIN_VALUE, m.doubleValue(),
				0.0d);
		try {
			m = Money
					.of("CHF",
							new BigDecimal(
									"12121762517652176251725178251872652765321876352187635217835378125"));
			m.doubleValue();
			fail("doubleValue(12121762517652176251725178251872652765321876352187635217835378125) should fail!");
		} catch (ArithmeticException e) {
			// OK
		}
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
	 * Test method for {@link org.javamoney.moneta.Money#toEngineeringString()}.
	 */
	@Test
	public void testToEngineeringString() {
		Money[] moneys = new Money[] {
				Money.of("CHF", 100),
				Money.of("USD", 34242344), Money.of("EUR", 23123213.435),
				Money.of("USS", -23123213.435), Money.of("USN", -23123213),
				Money.of("GBP", 0) };
		for (Money m : moneys) {
			assertEquals("toEngineeringString for " + m, m.getCurrency()
					.getCurrencyCode() + " " + m.asType(BigDecimal.class)
					.toEngineeringString(),
					m.toEngineeringString());
		}
	}

	/**
	 * Test method for {@link org.javamoney.moneta.Money#toPlainString()}.
	 */
	@Test
	public void testToPlainString() {
		Money[] moneys = new Money[] {
				Money.of("CHF", 100),
				Money.of("USD", 34242344), Money.of("EUR", 23123213.435),
				Money.of("USS", -23123213.435), Money.of("USN", -23123213),
				Money.of("GBP", 0) };
		for (Money m : moneys) {
			assertEquals("toEngineeringString for " + m, m.getCurrency()
					.getCurrencyCode() + " " + m.asType(BigDecimal.class)
					.toPlainString(),
					m.toPlainString());
		}
	}

	/**
	 * Test method for
	 * {@link org.javamoney.moneta.Money#isLessThan(javax.money.MonetaryAmount)}
	 * .
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
	 * {@link org.javamoney.moneta.Money#isLessThanOrEqualTo(javax.money.MonetaryAmount)}
	 * .
	 */
	@Test
	public void testIsLessThanOrEqualTo() {
		assertTrue(Money.of("CHF", BigDecimal.valueOf(0d)).isLessThanOrEqualTo(
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
	 * Test method for
	 * {@link org.javamoney.moneta.Money#isGreaterThan(javax.money.MonetaryAmount)}
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
	 * {@link org.javamoney.moneta.Money#isGreaterThanOrEqualTo(javax.money.MonetaryAmount)}
	 * .
	 */
	@Test
	public void testIsGreaterThanOrEqualTo() {
		assertTrue(Money.of("CHF", BigDecimal.valueOf(0d))
				.isGreaterThanOrEqualTo(
						Money.of("CHF", BigDecimal.valueOf(0))));
		assertTrue(Money
				.of("CHF", BigDecimal.valueOf(0.00000000001d))
				.isGreaterThanOrEqualTo(Money.of("CHF", BigDecimal.valueOf(0d))));
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
	 * Test method for
	 * {@link org.javamoney.moneta.Money#isEqualTo(javax.money.MonetaryAmount)}.
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
	}

	/**
	 * Test method for
	 * {@link org.javamoney.moneta.Money#isNotEqualTo(javax.money.MonetaryAmount)}
	 * .
	 */
	@Test
	public void testIsNotEqualTo() {
		assertFalse(Money.of("CHF", BigDecimal.valueOf(0d)).isNotEqualTo(
				Money.of("CHF", BigDecimal.valueOf(0))));
		assertTrue(Money.of("CHF", BigDecimal.valueOf(0.00000000001d))
				.isNotEqualTo(Money.of("CHF", BigDecimal.valueOf(0d))));
		assertFalse(Money.of("CHF", BigDecimal.valueOf(5d)).isNotEqualTo(
				Money.of("CHF", BigDecimal.valueOf(5))));
		assertFalse(Money.of("CHF", BigDecimal.valueOf(1d)).isNotEqualTo(
				Money.of("CHF", BigDecimal.valueOf(1.00))));
		assertFalse(Money.of("CHF", BigDecimal.valueOf(1d)).isNotEqualTo(
				Money.of("CHF", BigDecimal.ONE)));
		assertFalse(Money.of("CHF", BigDecimal.valueOf(1)).isNotEqualTo(
				Money.of("CHF", BigDecimal.ONE)));
		assertFalse(Money.of("CHF", new BigDecimal("1.0000")).isNotEqualTo(
				Money.of("CHF", new BigDecimal("1.00"))));
	}

	/**
	 * Test method for {@link org.javamoney.moneta.Money#getNumberType()}.
	 */
	@Test
	public void testGetNumberType() {
		assertEquals(Money.of("CHF", 0).getNumberType(), BigDecimal.class);
		assertEquals(Money.of("CHF", 0.34738746d).getNumberType(),
				BigDecimal.class);
		assertEquals(Money.of("CHF", 100034L).getNumberType(), BigDecimal.class);
	}

	/**
	 * Test method for
	 * {@link org.javamoney.moneta.Money#query(javax.money.MonetaryQuery)}.
	 */
	@Test
	public void testQuery() {
		fail("Not yet implemented");
	}

	/**
	 * Test method for
	 * {@link org.javamoney.moneta.Money#asType(java.lang.Class)}.
	 */
	@Test
	public void testAsTypeClassOfT() {
		Money m = Money.of("CHF", 13.656);
		assertEquals(m.asType(Byte.class), Byte.valueOf((byte) 13));
		assertEquals(m.asType(Short.class), Short.valueOf((short) 13));
		assertEquals(m.asType(Integer.class), Integer.valueOf(13));
		assertEquals(m.asType(Long.class), Long.valueOf(13L));
		assertEquals(m.asType(Float.class), Float.valueOf(13.656f));
		assertEquals(m.asType(Double.class), Double.valueOf(13.656));
		assertEquals(m.asType(BigDecimal.class), BigDecimal.valueOf(13.656));
		assertEquals(m.asType(BigDecimal.class), m.asNumber());
	}

	/**
	 * Test method for {@link org.javamoney.moneta.Money#asNumber()}.
	 */
	@Test
	public void testAsNumber() {
		assertEquals(BigDecimal.ZERO, Money.of("CHF", 0).asNumber());
		assertEquals(BigDecimal.valueOf(100034L), Money.of("CHF", 100034L)
				.asNumber());
		assertEquals(new BigDecimal(0.34738746d), Money
				.of("CHF", 0.34738746d).asNumber());
	}

	/**
	 * Test method for {@link org.javamoney.moneta.Money#toString()}.
	 */
	@Test
	public void testToString() {
		assertEquals("XXX 1.234556450000000", Money.of("XXX", 1.23455645d)
				.toString());
		assertEquals("CHF 1234", Money.of("CHF", 1234).toString());
		assertEquals("CHF 1234", Money.of("CHF", 1234.0d).toString());
		assertEquals("CHF 1234.100000000000", Money.of("CHF", 1234.1d)
				.toString());
		assertEquals("CHF 0.01000000000000000", Money.of("CHF", 0.0100d)
				.toString());
	}

	/**
	 * Test method for {@link org.javamoney.moneta.Money#getAmountWhole()}.
	 */
	@Test
	public void testGetAmountWhole() {
		assertEquals(1, Money.of("XXX", 1.23455645d).getAmountWhole());
		assertEquals(1, Money.of("CHF", 1).getAmountWhole());
		assertEquals(11, Money.of("CHF", 11.0d).getAmountWhole());
		assertEquals(1234, Money.of("CHF", 1234.1d).getAmountWhole());
		assertEquals(0, Money.of("CHF", 0.0100d).getAmountWhole());
	}

	/**
	 * Test method for
	 * {@link org.javamoney.moneta.Money#getAmountFractionNumerator()}.
	 */
	@Test
	public void testGetAmountFractionNumerator() {
		assertEquals(234556450000000L, Money.of("XXX", 1.23455645d)
				.getAmountFractionNumerator());
		assertEquals(0, Money.of("CHF", 1).getAmountFractionNumerator());
		assertEquals(0, Money.of("CHF", 11.0d).getAmountFractionNumerator());
		assertEquals(100000000000L, Money.of("CHF", 1234.1d)
				.getAmountFractionNumerator());
		assertEquals(1000000000000000L, Money.of("CHF", 0.0100d)
				.getAmountFractionNumerator());
	}

	/**
	 * Test method for
	 * {@link org.javamoney.moneta.Money#getAmountFractionDenominator()}.
	 */
	@Test
	public void testGetAmountFractionDenominator() {
		assertEquals(1000000000000000L, Money.of("XXX", 1.23455645d)
				.getAmountFractionDenominator());
		assertEquals(1, Money.of("CHF", 1).getAmountFractionDenominator());
		assertEquals(1, Money.of("CHF", 11.0d).getAmountFractionDenominator());
		assertEquals(1000000000000L, Money.of("CHF", 1234.1d)
				.getAmountFractionDenominator());
		assertEquals(100000000000000000L, Money.of("CHF", 0.0100d)
				.getAmountFractionDenominator());
	}

	/**
	 * Test method for
	 * {@link org.javamoney.moneta.Money#with(javax.money.MonetaryAdjuster)}.
	 */
	@Test
	public void testWithMonetaryAdjuster() {
		MonetaryAdjuster adj = new MonetaryAdjuster() {
			@Override
			public MonetaryAmount adjustInto(MonetaryAmount amount) {
				return Money.of(amount.getCurrency(), -100);
			}
		};
		Money m = Money.of("XXX", 1.23455645d);
		Money a = m.with(adj);
		assertNotNull(a);
		assertNotSame(m, a);
		assertEquals(m.getCurrency(), a.getCurrency());
		assertEquals(Money.of(m.getCurrency(), -100), a);
		adj = new MonetaryAdjuster() {
			@Override
			public MonetaryAmount adjustInto(MonetaryAmount amount) {
				return Money.from(amount).multiply(2)
						.with(MoneyCurrency.of("CHF"));
			}
		};
		a = m.with(adj);
		assertNotNull(a);
		assertNotSame(m, a);
		assertEquals(MoneyCurrency.of("CHF"), a.getCurrency());
		assertEquals(Money.of(a.getCurrency(), 1.23455645d * 2), a);
	}

	/**
	 * Test method for
	 * {@link org.javamoney.moneta.Money#from(javax.money.MonetaryAmount)}.
	 */
	@Test
	public void testFrom() {
		Money m = Money.of("XXX", 1.23455645d);
		Money m2 = Money.from(m);
		assertTrue(m == m2);
		FastMoney fm = FastMoney.of("XXX", 1.2345d);
		m2 = Money.from(m);
		assertFalse(m == m2);
		assertEquals(m, m2);
	}

}
