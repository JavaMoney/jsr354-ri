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
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertNotSame;
import static org.testng.Assert.assertTrue;
import static org.testng.Assert.fail;

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
import javax.money.MonetaryContextBuilder;
import javax.money.MonetaryCurrencies;
import javax.money.MonetaryException;
import javax.money.MonetaryOperator;
import javax.money.MonetaryQuery;

import org.testng.annotations.Test;

/**
 * @author Anatole
 * @author Werner
 */
public class MoneyTest {
    // TODO break this down into smaller test classes, 1.5k LOC seems a bit large;-)

    private static final BigDecimal TEN = new BigDecimal(10.0d);
    protected static final CurrencyUnit EURO = MonetaryCurrencies.getCurrency("EUR");
    protected static final CurrencyUnit DOLLAR = MonetaryCurrencies.getCurrency("USD");
    protected static final CurrencyUnit BRAZILIAN_REAL = MonetaryCurrencies.getCurrency("BRL");

    /**
     * Test method for
     * {@link org.javamoney.moneta.Money#of(java.math.BigDecimal, javax.money.CurrencyUnit)} .
     */
    @Test
    public void testOfCurrencyUnitBigDecimal() {
        Money m = Money.of(TEN, MonetaryCurrencies.getCurrency("EUR"));
        assertEquals(TEN, m.getNumber().numberValue(BigDecimal.class));
    }

    @Test
    public void testOfCurrencyUnitDouble() {
        Money m = Money.of(10.0d, MonetaryCurrencies.getCurrency("EUR"));
        assertTrue(TEN.doubleValue() == m.getNumber().doubleValue());
    }

    /**
     * Test method for {@link org.javamoney.moneta.Money#getCurrency()}.
     */
    @Test
    public void testGetCurrency() {
        MonetaryAmount money = Money.of(BigDecimal.TEN, EURO);
        assertNotNull(money.getCurrency());
        assertEquals("EUR", money.getCurrency().getCurrencyCode());
    }

    @Test
    public void testSubtractMonetaryAmount() {
        Money money1 = Money.of(BigDecimal.TEN, EURO);
        Money money2 = Money.of(BigDecimal.ONE, EURO);
        Money moneyResult = money1.subtract(money2);
        assertNotNull(moneyResult);
        assertEquals(9d, moneyResult.getNumber().doubleValue(), 0d);
    }

    @Test
    public void testDivideAndRemainder_BigDecimal() {
        Money money1 = Money.of(BigDecimal.ONE, EURO);
        Money[] divideAndRemainder = money1.divideAndRemainder(new BigDecimal("0.50000000000000000001"));
        assertEquals(divideAndRemainder[0].getNumber().numberValue(BigDecimal.class), BigDecimal.ONE);
        assertEquals(divideAndRemainder[1].getNumber().numberValue(BigDecimal.class),
                new BigDecimal("0.49999999999999999999"));
    }

    @Test
    public void testDivideToIntegralValue_BigDecimal() {
        Money money1 = Money.of(BigDecimal.ONE, EURO);
        Money result = money1.divideToIntegralValue(new BigDecimal("0.50000000000000000001"));
        assertEquals(result.getNumber().numberValue(BigDecimal.class), BigDecimal.ONE);
        result = money1.divideToIntegralValue(new BigDecimal("0.2001"));
        assertEquals(result.getNumber().numberValue(BigDecimal.class).stripTrailingZeros(),
                new BigDecimal("4.0").stripTrailingZeros());
        result = money1.divideToIntegralValue(new BigDecimal("5.0"));
        assertTrue(result.getNumber().numberValue(BigDecimal.class).intValueExact() == 0);
    }

    /**
     * Test method for {@link org.javamoney.moneta.Money#hashCode()}.
     */
    @Test
    public void testHashCode() {
        Money money1 = Money.of(BigDecimal.ONE, EURO);
        Money money2 = Money.of(new BigDecimal("1"), EURO);
        assertEquals(money1.hashCode(), money2.hashCode());
        Money money3 = Money.of(1.0, DOLLAR);
        assertTrue(money1.hashCode() != money3.hashCode());
        assertTrue(money2.hashCode() != money3.hashCode());
        Money money4 = Money.of(BigDecimal.ONE, DOLLAR);
        assertTrue(money1.hashCode() != money4.hashCode());
        assertTrue(money2.hashCode() != money4.hashCode());
        Money money5 = Money.of(BigDecimal.ONE, DOLLAR);
        Money money6 = Money.of(1.0, DOLLAR);
        assertTrue(money1.hashCode() != money5.hashCode());
        assertTrue(money2.hashCode() != money5.hashCode());
        assertTrue(money1.hashCode() != money6.hashCode());
        assertTrue(money2.hashCode() != money6.hashCode());
        // Test equality for values with different scales, but same numeric
        // values
        assertTrue(Money.of(BigDecimal.valueOf(0d), "CHF").hashCode() ==
                Money.of(BigDecimal.valueOf(0), "CHF").hashCode());
        assertTrue(Money.of(BigDecimal.ZERO, "CHF").hashCode() == Money.of(BigDecimal.valueOf(0), "CHF").hashCode());
        assertTrue(
                Money.of(BigDecimal.valueOf(5), "CHF").hashCode() == Money.of(new BigDecimal("5.0"), "CHF").hashCode());
        assertTrue(Money.of(BigDecimal.valueOf(5), "CHF").hashCode() ==
                Money.of(new BigDecimal("5.00"), "CHF").hashCode());
        assertTrue(Money.of(BigDecimal.valueOf(5), "CHF").hashCode() ==
                Money.of(new BigDecimal("5.000"), "CHF").hashCode());
        assertTrue(Money.of(BigDecimal.valueOf(5), "CHF").hashCode() ==
                Money.of(new BigDecimal("5.0000"), "CHF").hashCode());
        assertTrue(Money.of(new BigDecimal("-1.23"), "CHF").hashCode() ==
                Money.of(new BigDecimal("-1.230"), "CHF").hashCode());
        assertTrue(Money.of(new BigDecimal("-1.23"), "CHF").hashCode() ==
                Money.of(new BigDecimal("-1.2300"), "CHF").hashCode());
        assertTrue(Money.of(new BigDecimal("-1.23"), "CHF").hashCode() ==
                Money.of(new BigDecimal("-1.23000"), "CHF").hashCode());
        assertTrue(Money.of(new BigDecimal("-1.23"), "CHF").hashCode() ==
                Money.of(new BigDecimal("-1.230000000000000000000"), "CHF").hashCode());
    }

    /**
     * Test method for {@link org.javamoney.moneta.Money#getContext()}.
     */
    @Test
    public void testGetDefaultMathContext() {
        Money money1 = Money.of(BigDecimal.ONE, EURO);
        assertEquals(Money.DEFAULT_MONETARY_CONTEXT, money1.getContext());
    }

    /**
     * Test method for
     * {@link org.javamoney.moneta.Money#of(java.math.BigDecimal, javax.money.CurrencyUnit,
     * javax.money.MonetaryContext)}
     * .
     */
    @Test
    public void testOfCurrencyUnitBigDecimalMathContext() {
        Money m = Money.of(

                BigDecimal.valueOf(2.15), EURO,
                MonetaryContextBuilder.of(Money.class).setPrecision(2).setFixedScale(true).set(RoundingMode.DOWN)
                        .build());
        Money m2 = Money.of(BigDecimal.valueOf(2.1), EURO);
        assertEquals(m, m2);
        Money m3 = m.multiply(100);
        assertEquals(Money.of(210, EURO), m3.abs());
    }

    /**
     * Test method for
     * {@link org.javamoney.moneta.Money#of(java.lang.Number, javax.money.CurrencyUnit)} .
     */
    @Test
    public void testOfCurrencyUnitNumber() {
        Money m = Money.of((byte) 2, EURO);
        assertNotNull(m);
        assertEquals(EURO, m.getCurrency());
        assertEquals(Byte.valueOf((byte) 2), m.getNumber().numberValue(Byte.class));
        m = Money.of((short) -2, DOLLAR);
        assertNotNull(m);
        assertEquals(DOLLAR, m.getCurrency());
        assertEquals(Short.valueOf((short) -2), m.getNumber().numberValue(Short.class));
        m = Money.of(-12, EURO);
        assertNotNull(m);
        assertEquals(EURO, m.getCurrency());
        assertEquals(m.getNumber().numberValue(Integer.class), Integer.valueOf(-12));
        m = Money.of((long) 12, DOLLAR);
        assertNotNull(m);
        assertEquals(DOLLAR, m.getCurrency());
        assertEquals(Long.valueOf(12), m.getNumber().numberValue(Long.class));
        m = Money.of((float) 12.23, EURO);
        assertNotNull(m);
        assertEquals(EURO, m.getCurrency());
        assertEquals((float) 12.23, m.getNumber().numberValue(Float.class));
        m = Money.of(-12.23, DOLLAR);
        assertNotNull(m);
        assertEquals(DOLLAR, m.getCurrency());
        assertEquals(-12.23, m.getNumber().numberValue(Double.class));
        m = Money.of((Number) BigDecimal.valueOf(234.2345), EURO);
        assertNotNull(m);
        assertEquals(EURO, m.getCurrency());
        assertEquals(BigDecimal.valueOf(234.2345), m.getNumber().numberValue(BigDecimal.class));
        m = Money.of(BigInteger.valueOf(23232312321432432L), DOLLAR);
        assertNotNull(m);
        assertEquals(DOLLAR, m.getCurrency());
        assertEquals(Long.valueOf(23232312321432432L), m.getNumber().numberValue(Long.class));
        assertEquals(BigInteger.valueOf(23232312321432432L), m.getNumber().numberValue(BigInteger.class));
    }

    /**
     * Test method for
     * {@link org.javamoney.moneta.Money#of(java.math.BigDecimal, javax.money.CurrencyUnit,
     * javax.money.MonetaryContext)} .
     * .
     */
    @Test
    public void testOfCurrencyUnitNumberMathContext() {
        MonetaryContext mc =
                MonetaryContextBuilder.of(Money.class).setMaxScale(2345).setFixedScale(true).set(RoundingMode.CEILING)
                        .build();
        Money m = Money.of((byte) 2, EURO, mc);
        assertNotNull(m);
        assertEquals(mc, m.getContext());
        assertEquals(EURO, m.getCurrency());
        assertEquals(Byte.valueOf((byte) 2), m.getNumber().numberValue(Byte.class));
        m = Money.of((short) -2, DOLLAR, mc);
        assertNotNull(m);
        assertEquals(mc, m.getContext());
        assertEquals(DOLLAR, m.getCurrency());
        assertEquals(Short.valueOf((short) -2), m.getNumber().numberValue(Short.class));
        m = Money.of(-12, EURO, mc);
        assertNotNull(m);
        assertEquals(mc, m.getContext());
        assertEquals(EURO, m.getCurrency());
        assertEquals(Integer.valueOf(-12), m.getNumber().numberValue(Integer.class));
        m = Money.of((long) 12, DOLLAR, mc);
        assertEquals(mc, m.getContext());
        assertNotNull(m);
        assertEquals(DOLLAR, m.getCurrency());
        assertEquals(Long.valueOf(12), m.getNumber().numberValue(Long.class));
        m = Money.of((float) 12.23, EURO, mc);
        assertNotNull(m);
        assertEquals(mc, m.getContext());
        assertEquals(EURO, m.getCurrency());
        assertEquals((float) 12.23, m.getNumber().numberValue(Float.class));
        m = Money.of(-12.23, DOLLAR, mc);
        assertNotNull(m);
        assertEquals(mc, m.getContext());
        assertEquals(DOLLAR, m.getCurrency());
        assertEquals(mc, m.getContext());
        assertEquals(-12.23, m.getNumber().numberValue(Double.class));
        m = Money.of((Number) BigDecimal.valueOf(234.2345), EURO, mc);
        assertNotNull(m);
        assertEquals(EURO, m.getCurrency());
        assertEquals(mc, m.getContext());
        assertEquals(BigDecimal.valueOf(234.2345), m.getNumber().numberValue(BigDecimal.class));
        m = Money.of(BigInteger.valueOf(23232312321432432L), DOLLAR, mc);
        assertNotNull(m);
        assertEquals(DOLLAR, m.getCurrency());
        assertEquals(mc, m.getContext());
        assertEquals(Long.valueOf(23232312321432432L), m.getNumber().numberValue(Long.class));
        assertEquals(BigInteger.valueOf(23232312321432432L), m.getNumber().numberValue(BigInteger.class));
    }

    /**
     * Test method for {@link org.javamoney.moneta.Money#of(java.lang.Number, java.lang.String)} .
     */
    @Test
    public void testOfStringNumber() {
        Money m = Money.of((byte) 2, "EUR");
        assertNotNull(m);
        assertEquals(EURO, m.getCurrency());
        assertEquals(Byte.valueOf((byte) 2), m.getNumber().numberValue(Byte.class));
        m = Money.of((short) -2, "USD");
        assertNotNull(m);
        assertEquals(DOLLAR, m.getCurrency());
        assertEquals(Short.valueOf((short) -2), m.getNumber().numberValue(Short.class));
        m = Money.of(-12, "EUR");
        assertNotNull(m);
        assertEquals(EURO, m.getCurrency());
        assertEquals(Integer.valueOf(-12), m.getNumber().numberValue(Integer.class));
        m = Money.of((long) 12, "USD");
        assertNotNull(m);
        assertEquals(DOLLAR, m.getCurrency());
        assertEquals(Long.valueOf(12), m.getNumber().numberValue(Long.class));
        m = Money.of((float) 12.23, "EUR");
        assertNotNull(m);
        assertEquals(EURO, m.getCurrency());
        assertEquals((float) 12.23, m.getNumber().numberValue(Float.class));
        m = Money.of(-12.23, "USD");
        assertNotNull(m);
        assertEquals(DOLLAR, m.getCurrency());
        assertEquals(-12.23, m.getNumber().numberValue(Double.class));
        m = Money.of((Number) BigDecimal.valueOf(234.2345), "EUR");
        assertNotNull(m);
        assertEquals(EURO, m.getCurrency());
        assertEquals(BigDecimal.valueOf(234.2345), m.getNumber().numberValue(BigDecimal.class));
        m = Money.of(BigInteger.valueOf(23232312321432432L), "USD");
        assertNotNull(m);
        assertEquals(DOLLAR, m.getCurrency());
        assertEquals(Long.valueOf(23232312321432432L), m.getNumber().numberValue(Long.class));
        assertEquals(BigInteger.valueOf(23232312321432432L), m.getNumber().numberValue(BigInteger.class));
    }

    /**
     * Test method for
     * {@link org.javamoney.moneta.Money#of(Number, String, javax.money.MonetaryContext)} .
     * .
     */
    @Test
    public void testOfStringNumberMathContext() {
        MonetaryContext mc =
                MonetaryContextBuilder.of(Money.class).setMaxScale(2345).setFixedScale(true).set(RoundingMode.CEILING)
                        .build();
        Money m = Money.of((byte) 2, "EUR", mc);
        assertNotNull(m);
        assertEquals(mc, m.getContext());
        assertEquals(EURO, m.getCurrency());
        assertEquals(Byte.valueOf((byte) 2), m.getNumber().numberValue(Byte.class));
        m = Money.of((short) -2, "USD", mc);
        assertNotNull(m);
        assertEquals(mc, m.getContext());
        assertEquals(DOLLAR, m.getCurrency());
        assertEquals(Short.valueOf((short) -2), m.getNumber().numberValue(Short.class));
        m = Money.of(-12, "EUR", mc);
        assertNotNull(m);
        assertEquals(mc, m.getContext());
        assertEquals(EURO, m.getCurrency());
        assertEquals(Integer.valueOf(-12), m.getNumber().numberValue(Integer.class));
        m = Money.of((long) 12, "USD", mc);
        assertEquals(mc, m.getContext());
        assertNotNull(m);
        assertEquals(DOLLAR, m.getCurrency());
        assertEquals(Long.valueOf(12), m.getNumber().numberValue(Long.class));
        m = Money.of((float) 12.23, "EUR", mc);
        assertNotNull(m);
        assertEquals(mc, m.getContext());
        assertEquals(EURO, m.getCurrency());
        assertEquals(m.getNumber().numberValue(Float.class), (float) 12.23);
        m = Money.of(-12.23, "USD", mc);
        assertNotNull(m);
        assertEquals(mc, m.getContext());
        assertEquals(DOLLAR, m.getCurrency());
        assertEquals(mc, m.getContext());
        assertEquals(m.getNumber().numberValue(Double.class), -12.23);
        m = Money.of((Number) BigDecimal.valueOf(234.2345), "EUR", mc);
        assertNotNull(m);
        assertEquals(EURO, m.getCurrency());
        assertEquals(mc, m.getContext());
        assertEquals(m.getNumber().numberValue(BigDecimal.class), BigDecimal.valueOf(234.2345));
        m = Money.of(BigInteger.valueOf(23232312321432432L), "USD", mc);
        assertNotNull(m);
        assertEquals(DOLLAR, m.getCurrency());
        assertEquals(mc, m.getContext());
        assertEquals(Long.valueOf(23232312321432432L), m.getNumber().numberValue(Long.class));
        assertEquals(BigInteger.valueOf(23232312321432432L), m.getNumber().numberValue(BigInteger.class));
    }

    /**
     * Test method for {@link org.javamoney.moneta.Money#equals(java.lang.Object)}.
     */
    @Test
    public void testEqualsObject() {
        Money[] moneys =
                new Money[]{Money.of(0, "CHF"), Money.of(BigDecimal.ONE, "CHF"), Money.of(BigDecimal.ONE, "XXX"),
                        Money.of(BigDecimal.ONE.negate(), "XXX")};
        for (int i = 0; i < moneys.length; i++) {
            for (int j = 0; j < moneys.length; j++) {
                if (i == j) {
                    assertEquals(moneys[i], moneys[j]);
                } else {
                    assertNotSame(moneys[i], moneys[j]);
                }
            }
        }
        // Test equality for values with different scales, but same numeric
        // values
        assertTrue(Money.of(BigDecimal.valueOf(0d), "CHF").equals(Money.of(BigDecimal.valueOf(0), "CHF")));
        assertTrue(Money.of(BigDecimal.ZERO, "CHF").equals(Money.of(BigDecimal.valueOf(0), "CHF")));
        assertTrue(Money.of(BigDecimal.valueOf(5), "CHF").equals(Money.of(new BigDecimal("5.0"), "CHF")));
        assertTrue(Money.of(BigDecimal.valueOf(5), "CHF").equals(Money.of(new BigDecimal("5.00"), "CHF")));
        assertTrue(Money.of(BigDecimal.valueOf(5), "CHF").equals(Money.of(new BigDecimal("5.000"), "CHF")));
        assertTrue(Money.of(BigDecimal.valueOf(5), "CHF").equals(Money.of(new BigDecimal("5.0000"), "CHF")));
        assertTrue(Money.of(new BigDecimal("-1.23"), "CHF").equals(Money.of(new BigDecimal("-1.230"), "CHF")));
        assertTrue(Money.of(new BigDecimal("-1.23"), "CHF").equals(Money.of(new BigDecimal("-1.2300"), "CHF")));
        assertTrue(Money.of(new BigDecimal("-1.23"), "CHF").equals(Money.of(new BigDecimal("-1.23000"), "CHF")));
        assertTrue(Money.of(new BigDecimal("-1.23"), "CHF")
                .equals(Money.of(new BigDecimal("-1.230000000000000000000"), "CHF")));
    }

    /**
     * Test differently created MonetaryAmount or Money instances for equality
     * {@link org.javamoney.moneta.Money#equals(Object)}.
     */
    @Test
    public void testEqualsMonetarAmount() {
        MonetaryAmount m = MonetaryAmounts.getDefaultAmountFactory().setCurrency("CHF").setNumber(100).create();
        MonetaryAmount m2 = Money.of(100, "CHF");
        Money m3 = Money.of(100, "CHF");
        assertTrue(m.equals(m2));
        assertTrue(m.equals(m3));
        assertTrue(m2.equals(m3));
    }

    /**
     * Test method for {@link org.javamoney.moneta.Money#compareTo(javax.money.MonetaryAmount)}.
     */
    @Test
    public void testCompareTo() {
        Money m1 = Money.of(-2, "CHF");
        Money m2 = Money.of(0, "CHF");
        Money m3 = Money.of(-0, "CHF");
        Money m4 = Money.of(2, "CHF");
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
     * Test method for {@link org.javamoney.moneta.Money#getFactory()#setContext(java.math.MathContext)}.
     */
    @Test
    public void testWithMonetaryContext() {
        Money m = Money.of(10, "CHF");
        assertEquals(Money.DEFAULT_MONETARY_CONTEXT, m.getContext());
        MonetaryContext mc =
                MonetaryContextBuilder.of(Money.class).setPrecision(128).set(RoundingMode.HALF_EVEN).build();
        MonetaryAmount m2 = m.getFactory().setContext(mc).create();
        assertNotNull(m2);
        assertTrue(m != m2);
        assertEquals(Money.DEFAULT_MONETARY_CONTEXT, m.getContext());
        assertEquals(mc, m2.getContext());
    }

    /**
     * Test method for {@link org.javamoney.moneta.Money#abs()}.
     */
    @Test
    public void testAbs() {
        Money m = Money.of(10, "CHF");
        assertEquals(m, m.abs());
        assertTrue(m == m.abs());
        m = Money.of(0, "CHF");
        assertEquals(m, m.abs());
        assertTrue(m == m.abs());
        m = Money.of(-10, "CHF");
        assertEquals(m.negate(), m.abs());
        assertTrue(m != m.abs());
    }

    /**
     * Test method for {@link org.javamoney.moneta.Money#add(javax.money.MonetaryAmount)} .
     */
    @Test
    public void testAdd() {
        Money money1 = Money.of(BigDecimal.TEN, EURO);
        Money money2 = Money.of(BigDecimal.ONE, EURO);
        Money moneyResult = money1.add(money2);
        assertNotNull(moneyResult);
        assertEquals(11d, moneyResult.getNumber().doubleValue(), 0d);
    }

    /**
     * Test method for {@link org.javamoney.moneta.Money#divide(java.lang.Number)}.
     */
    @Test
    public void testDivideNumber() {
        Money m = Money.of(100, "CHF");
        assertEquals(Money.of(BigDecimal.valueOf(100).divide(BigDecimal.valueOf(5)), "CHF"),
                m.divide(BigDecimal.valueOf(5)));
    }

    /**
     * Test method for {@link org.javamoney.moneta.Money#divideAndRemainder(java.lang.Number)}.
     */
    @Test
    public void testDivideAndRemainderNumber() {
        Money m = Money.of(100, "CHF");
        assertEquals(Money.of(BigDecimal.valueOf(33), "CHF"), m.divideAndRemainder(BigDecimal.valueOf(3))[0]);
        assertEquals(Money.of(BigDecimal.valueOf(1), "CHF"), m.divideAndRemainder(BigDecimal.valueOf(3))[1]);
    }

    /**
     * Test method for {@link org.javamoney.moneta.Money#divideToIntegralValue(java.lang.Number)} .
     */
    @Test
    public void testDivideToIntegralValueNumber() {
        Money m = Money.of(100, "CHF");
        assertEquals(Money.of(BigDecimal.valueOf(5), "CHF"), m.divideToIntegralValue(BigDecimal.valueOf(20)));
        assertEquals(Money.of(BigDecimal.valueOf(33), "CHF"), m.divideToIntegralValue(BigDecimal.valueOf(3)));
    }

    /**
     * Test method for {@link org.javamoney.moneta.Money#multiply(java.lang.Number)}.
     */
    @Test
    public void testMultiplyNumber() {
        Money m = Money.of(100, "CHF");
        assertEquals(Money.of(400, "CHF"), m.multiply(4));
        assertEquals(Money.of(200, "CHF"), m.multiply(2));
        assertEquals(Money.of(new BigDecimal("50.0"), "CHF"), m.multiply(0.5));
    }


    /**
     * Test method for {@link org.javamoney.moneta.Money#multiply(double)}.
     */
    @Test
    public void testMultiplyBadDoubles() {
        double[] values = new double[]{
                Double.POSITIVE_INFINITY,
                Double.NEGATIVE_INFINITY,
                Double.NaN};
        Money m = Money.of(new BigDecimal("50.0"), "USD");
        for (double d : values) {
            try {
                m.multiply(d);
                fail("multiplying with:" + d + "should not be allowed");
            } catch (ArithmeticException e) {
                // should reach here
            }
            try {
                m.multiply(Double.valueOf(d));
                fail("multiplying with:" + d + "should not be allowed");
            } catch (ArithmeticException e) {
                // should reach here
            }
        }
    }

    /**
     * Test method for {@link org.javamoney.moneta.Money#divide(double)}.
     */
    @Test
    public void testDivideBadDoubles() {
        Money m = Money.of(new BigDecimal("50.0"), "USD");
        try {
            m.divide(Double.NaN);
            fail("dividing with:NaN should not be allowed");
        } catch (ArithmeticException e) {
            // should reach here
        }
        try {
            m.divide(Double.valueOf(Double.NaN));
            fail("dividing with NaN should not be allowed");
        } catch (ArithmeticException e) {
            // should reach here
        }
    }

    /**
     * Test method for {@link org.javamoney.moneta.Money#divide(double)}.
     */
    @Test
    public void testDivideInfinityDoubles() {
        double[] values = new double[]{Double.POSITIVE_INFINITY, Double.NEGATIVE_INFINITY};
        Money m = Money.of(new BigDecimal("50.0"), "USD");
        for (double d : values) {
            assertTrue(m.divide(d).isZero());
            assertTrue(m.divide(Double.valueOf(d)).isZero());
        }
    }

    /**
     * Test method for {@link org.javamoney.moneta.Money#negate()}.
     */
    @Test
    public void testNegate() {
        Money m = Money.of(100, "CHF");
        assertEquals(Money.of(-100, "CHF"), m.negate());
        m = Money.of(-123.234, "CHF");
        assertEquals(Money.of(123.234, "CHF"), m.negate());
    }

    /**
     * Test method for {@link org.javamoney.moneta.Money#plus()}.
     */
    @Test
    public void testPlus() {
        Money m = Money.of(100, "CHF");
        assertEquals(Money.of(100, "CHF"), m.plus());
        m = Money.of(123.234, "CHF");
        assertEquals(Money.of(123.234, "CHF"), m.plus());
    }

    /**
     * Test method for {@link org.javamoney.moneta.Money#subtract(javax.money.MonetaryAmount)}.
     */
    @Test
    public void testSubtract() {
        Money m = Money.of(100, "CHF");
        Money s1 = Money.of(100, "CHF");
        Money s2 = Money.of(200, "CHF");
        Money s3 = Money.of(0, "CHF");
        assertEquals(Money.of(0, "CHF"), m.subtract(s1));
        assertEquals(Money.of(-100, "CHF"), m.subtract(s2));
        assertEquals(Money.of(100, "CHF"), m.subtract(s3));
        assertTrue(m == m.subtract(s3));
        m = Money.of(-123.234, "CHF");
        assertEquals(Money.of(new BigDecimal("-223.234"), "CHF"), m.subtract(s1));
        assertEquals(Money.of(new BigDecimal("-323.234"), "CHF"), m.subtract(s2));
        assertEquals(Money.of(new BigDecimal("-123.234"), "CHF"), m.subtract(s3));
        assertTrue(m == m.subtract(s3));
        m = Money.of(12.402345534, "CHF");
        s1 = Money.of(2343.45, "CHF");
        s2 = Money.of(12.402345534, "CHF");
        s3 = Money.of(-2343.45, "CHF");
        assertEquals(Money.of(new BigDecimal("12.402345534").subtract(new BigDecimal("2343.45")), "CHF"),
                m.subtract(s1));
        assertEquals(Money.of(new BigDecimal("12.402345534").subtract(new BigDecimal("12.402345534")), "CHF"),
                m.subtract(s2));
        assertEquals(Money.of(0, "CHF"), m.subtract(s2));
        assertEquals(Money.of(new BigDecimal("2355.852345534"), "CHF"), m.subtract(s3));
        assertTrue(m == m.subtract(Money.of(0, "CHF")));
    }

    /**
     * Test method for {@link org.javamoney.moneta.Money#remainder(java.lang.Number)}.
     */
    @Test
    public void testRemainderNumber() {
        Money[] moneys = new Money[]{Money.of(100, "CHF"), Money.of(34242344, "CHF"), Money.of(23123213.435, "CHF"),
                Money.of(0, "CHF"), Money.of(-100, "CHF"), Money.of(-723527.36532, "CHF")};
        for (Money m : moneys) {
            assertEquals(m.getFactory().setCurrency(m.getCurrency()).setNumber(
                    m.getNumber().numberValue(BigDecimal.class).remainder(BigDecimal.valueOf(10.50)))
                    .create(), m.remainder(10.50), "Invalid remainder of " + 10.50);
            assertEquals(m.getFactory().setCurrency(m.getCurrency()).setNumber(
                    m.getNumber().numberValue(BigDecimal.class).remainder(BigDecimal.valueOf(-30.20)))
                    .create(), m.remainder(-30.20), "Invalid remainder of " + -30.20);
            assertEquals(m.getFactory().setCurrency(m.getCurrency()).setNumber(
                    m.getNumber().numberValue(BigDecimal.class).remainder(BigDecimal.valueOf(-3)))
                    .create(), m.remainder(-3), "Invalid remainder of " + -3);
            assertEquals(m.getFactory().setCurrency(m.getCurrency()).setNumber(
                            m.getNumber().numberValue(BigDecimal.class).remainder(BigDecimal.valueOf(3))).create(),
                    m.remainder(3));
        }
    }

    /**
     * Test method for {@link org.javamoney.moneta.Money#scaleByPowerOfTen(int)} .
     */
    @Test
    public void testScaleByPowerOfTen() {
        Money[] moneys = new Money[]{Money.of(100, "CHF"), Money.of(34242344, "CHF"), Money.of(23123213.435, "CHF"),
                Money.of(0, "CHF"), Money.of(-100, "CHF"), Money.of(-723527.36532, "CHF")};
        for (Money m : moneys) {
            for (int p = -10; p < 10; p++) {
                assertEquals(m.getFactory().setCurrency(m.getCurrency())
                        .setNumber(m.getNumber().numberValue(BigDecimal.class).scaleByPowerOfTen(p))
                        .create(), m.scaleByPowerOfTen(p), "Invalid scaleByPowerOfTen.");
            }
        }
    }

    /**
     * Test method for {@link org.javamoney.moneta.Money#isZero()}.
     */
    @Test
    public void testIsZero() {
        Money[] moneys = new Money[]{Money.of(100, "CHF"), Money.of(34242344, "CHF"), Money.of(23123213.435, "CHF"),
                Money.of(-100, "CHF"), Money.of(-723527.36532, "CHF")};
        for (Money m : moneys) {
            assertFalse(m.isZero());
        }
        moneys = new Money[]{Money.of(0, "CHF"), Money.of(0.0, "CHF"), Money.of(BigDecimal.ZERO, "CHF"),
                Money.of(new BigDecimal("0.00000000000000000"), "CHF")};
        for (Money m : moneys) {
            assertTrue(m.isZero());
        }
    }

    /**
     * Test method for {@link org.javamoney.moneta.Money#isPositive()}.
     */
    @Test
    public void testIsPositive() {
        Money[] moneys = new Money[]{Money.of(100, "CHF"), Money.of(34242344, "CHF"), Money.of(23123213.435, "CHF")};
        for (Money m : moneys) {
            assertTrue(m.isPositive());
        }
        moneys = new Money[]{Money.of(0, "CHF"), Money.of(0.0, "CHF"), Money.of(BigDecimal.ZERO, "CHF"),
                Money.of(new BigDecimal("0.00000000000000000"), "CHF"), Money.of(-100, "CHF"),
                Money.of(-34242344, "CHF"), Money.of(-23123213.435, "CHF")};
        for (Money m : moneys) {
            assertFalse(m.isPositive());
        }
    }

    /**
     * Test method for {@link org.javamoney.moneta.Money#isPositiveOrZero()}.
     */
    @Test
    public void testIsPositiveOrZero() {
        Money[] moneys = new Money[]{Money.of(0, "CHF"), Money.of(0.0, "CHF"), Money.of(BigDecimal.ZERO, "CHF"),
                Money.of(new BigDecimal("0.00000000000000000"), "CHF"), Money.of(100, "CHF"), Money.of(34242344, "CHF"),
                Money.of(23123213.435, "CHF")};
        for (Money m : moneys) {
            assertTrue(m.isPositiveOrZero(), "Invalid positiveOrZero (expected true): " + m);
        }
        moneys = new Money[]{Money.of(-100, "CHF"), Money.of(-34242344, "CHF"), Money.of(-23123213.435, "CHF")};
        for (Money m : moneys) {
            assertFalse(m.isPositiveOrZero(), "Invalid positiveOrZero (expected false): " + m);
        }
    }

    /**
     * Test method for {@link org.javamoney.moneta.Money#isNegative()}.
     */
    @Test
    public void testIsNegative() {
        Money[] moneys = new Money[]{Money.of(0, "CHF"), Money.of(0.0, "CHF"), Money.of(BigDecimal.ZERO, "CHF"),
                Money.of(new BigDecimal("0.00000000000000000"), "CHF"), Money.of(100, "CHF"), Money.of(34242344, "CHF"),
                Money.of(23123213.435, "CHF")};
        for (Money m : moneys) {
            assertFalse(m.isNegative(), "Invalid isNegative (expected false): " + m);
        }
        moneys = new Money[]{Money.of(-100, "CHF"), Money.of(-34242344, "CHF"), Money.of(-23123213.435, "CHF")};
        for (Money m : moneys) {
            assertTrue(m.isNegative(), "Invalid isNegative (expected true): " + m);
        }
    }

    /**
     * Test method for {@link org.javamoney.moneta.Money#isNegativeOrZero()}.
     */
    @Test
    public void testIsNegativeOrZero() {
        Money[] moneys = new Money[]{Money.of(100, "CHF"), Money.of(34242344, "CHF"), Money.of(23123213.435, "CHF")};
        for (Money m : moneys) {
            assertFalse(m.isNegativeOrZero(), "Invalid negativeOrZero (expected false): " + m);
        }
        moneys = new Money[]{Money.of(0, "CHF"), Money.of(0.0, "CHF"), Money.of(BigDecimal.ZERO, "CHF"),
                Money.of(new BigDecimal("0.00000000000000000"), "CHF"), Money.of(-100, "CHF"),
                Money.of(-34242344, "CHF"), Money.of(-23123213.435, "CHF")};
        for (Money m : moneys) {
            assertTrue(m.isNegativeOrZero(), "Invalid negativeOrZero (expected true): " + m);
        }
    }

    /**
     * Test method for {@link Money#getFactory()#setNumber(java.lang.Number)}.
     */
    @Test
    public void testWithNumber() {
        Money[] moneys = new Money[]{Money.of(100, "CHF"), Money.of(34242344, "CHF"),
                Money.of(new BigDecimal("23123213.435"), "CHF"), Money.of(new BigDecimal("-23123213.435"), "CHF"),
                Money.of(-23123213, "CHF"), Money.of(0, "CHF")};
        Money s = Money.of(10, "CHF");
        MonetaryAmount[] moneys2 =
                new MonetaryAmount[]{s.getFactory().setCurrency(s.getCurrency()).setNumber(100).create(),
                        s.getFactory().setCurrency(s.getCurrency()).setNumber(34242344).create(),
                        s.getFactory().setCurrency(s.getCurrency()).setNumber(new BigDecimal("23123213.435")).create(),
                        s.getFactory().setCurrency(s.getCurrency()).setNumber(new BigDecimal("-23123213.435")).create(),
                        s.getFactory().setCurrency(s.getCurrency()).setNumber(-23123213).create(),
                        s.getFactory().setCurrency(s.getCurrency()).setNumber(0).create()};
        for (int i = 0; i < moneys.length; i++) {
            assertEquals(moneys[i], moneys2[i], "with(Number) failed.");
        }
    }

    /**
     * Test method for
     * {@link Money#getFactory()#setCurrency(javax.money.CurrencyUnit)} and {@link Money#getFactory()#setNumber
     * (Number)}.
     */
    @Test
    public void testWithCurrencyUnitNumber() {
        Money[] moneys = new Money[]{Money.of(100, "CHF"), Money.of(34242344, "USD"), Money.of(23123213.435, "EUR"),
                Money.of(-23123213.435, "USS"), Money.of(-23123213, "USN"), Money.of(0, "GBP")};
        Money s = Money.of(10, "XXX");
        MonetaryAmount[] moneys2 = new MonetaryAmount[]{
                s.getFactory().setCurrency(MonetaryCurrencies.getCurrency("CHF")).setNumber(100).create(),
                s.getFactory().setCurrency(MonetaryCurrencies.getCurrency("USD")).setNumber(34242344).create(),
                s.getFactory().setCurrency(MonetaryCurrencies.getCurrency("EUR"))
                        .setNumber(new BigDecimal("23123213.435")).create(),
                s.getFactory().setCurrency(MonetaryCurrencies.getCurrency("USS"))
                        .setNumber(new BigDecimal("-23123213.435")).create(),
                s.getFactory().setCurrency(MonetaryCurrencies.getCurrency("USN")).setNumber(-23123213).create(),
                s.getFactory().setCurrency(MonetaryCurrencies.getCurrency("GBP")).setNumber(0).create()};
        for (int i = 0; i < moneys.length; i++) {
            assertEquals(moneys[i], moneys2[i], "with(Number) failed.");
        }
    }

    /**
     * Test method for {@link org.javamoney.moneta.Money#getNumber()#longValue()}.
     */
    @Test
    public void testLongValue() {
        Money m = Money.of(100, "CHF");
        assertEquals(100L, m.getNumber().longValue(), "longValue of " + m);
        m = Money.of(-100, "CHF");
        assertEquals(-100L, m.getNumber().longValue(), "longValue of " + m);
        m = Money.of(-100.3434, "CHF");
        assertEquals(-100L, m.getNumber().longValue(), "longValue of " + m);
        m = Money.of(100.3434, "CHF");
        assertEquals(100L, m.getNumber().longValue(), "longValue of " + m);
        m = Money.of(0, "CHF");
        assertEquals(0L, m.getNumber().longValue(), "longValue of " + m);
        m = Money.of(-0.0, "CHF");
        assertEquals(0L, m.getNumber().longValue(), "longValue of " + m);
        m = Money.of(Long.MAX_VALUE, "CHF");
        assertEquals(Long.MAX_VALUE, m.getNumber().longValue(), "longValue of " + m);
        m = Money.of(Long.MIN_VALUE, "CHF");
        assertEquals(Long.MIN_VALUE, m.getNumber().longValue(), "longValue of " + m);
        // try {
        m = Money.of(new BigDecimal("12121762517652176251725178251872652765321876352187635217835378125"), "CHF");
        m.getNumber().longValue();
    }

    /**
     * Test method for {@link org.javamoney.moneta.Money#getNumber()#longValueExact()}.
     */
    @Test
    public void testLongValueExact() {
        Money m = Money.of(100, "CHF");
        assertEquals(100L, m.getNumber().longValueExact(), "longValue of " + m);
        m = Money.of(-100, "CHF");
        assertEquals(-100L, m.getNumber().longValueExact(), "longValue of " + m);
        m = Money.of(0, "CHF");
        assertEquals(0L, m.getNumber().longValueExact(), "longValue of " + m);
        m = Money.of(-0.0, "CHF");
        assertEquals(0L, m.getNumber().longValue(), "longValue of " + m);
        m = Money.of(Long.MAX_VALUE, "CHF");
        assertEquals(Long.MAX_VALUE, m.getNumber().longValue(), "longValue of " + m);
        m = Money.of(Long.MIN_VALUE, "CHF");
        assertEquals(Long.MIN_VALUE, m.getNumber().longValue(), "longValue of " + m);
        try {
            m = Money.of(new BigDecimal("12121762517652176251725178251872652765321876352187635217835378125"), "CHF");
            m.getNumber().longValueExact();
            fail("longValueExact(12121762517652176251725178251872652765321876352187635217835378125) should fail!");
        } catch (ArithmeticException e) {
            // OK
        }
        try {
            m = Money.of(-100.3434, "CHF");
            m.getNumber().longValueExact();
            fail("longValueExact(-100.3434) should raise an ArithmeticException.");
        } catch (ArithmeticException e) {
            // OK
        }
        try {
            m = Money.of(100.3434, "CHF");
            m.getNumber().longValueExact();
            fail("longValueExact(100.3434) should raise an ArithmeticException.");
        } catch (ArithmeticException e) {
            // OK
        }
    }

    /**
     * Test method for {@link org.javamoney.moneta.Money#getNumber()#doubleValue()}.
     */
    @Test
    public void testDoubleValue() {
        Money m = Money.of(100, "CHF");
        assertEquals(100d, m.getNumber().doubleValue(), 0.0d, "doubleValue of " + m);
        m = Money.of(-100, "CHF");
        assertEquals(-100d, m.getNumber().doubleValue(), 0.0d, "doubleValue of " + m);
        m = Money.of(-100.3434, "CHF");
        assertEquals(-100.3434, m.getNumber().doubleValue(), 0.0d, "doubleValue of " + m);
        m = Money.of(100.3434, "CHF");
        assertEquals(100.3434, m.getNumber().doubleValue(), 0.0d, "doubleValue of " + m);
        m = Money.of(0, "CHF");
        assertEquals(0d, m.getNumber().doubleValue(), 0.0d, "doubleValue of " + m);
        m = Money.of(-0.0, "CHF");
        assertEquals(0d, m.getNumber().doubleValue(), 0.0d, "doubleValue of " + m);
        m = Money.of(Double.MAX_VALUE, "CHF");
        assertEquals(Double.MAX_VALUE, m.getNumber().doubleValue(), 0.0d, "doubleValue of " + m);
        m = Money.of(Double.MIN_VALUE, "CHF");
        assertEquals(Double.MIN_VALUE, m.getNumber().doubleValue(), 0.0d, "doubleValue of " + m);
        // try {
        m = Money.of(new BigDecimal("12121762517652176251725178251872652765321876352187635217835378125"), "CHF");
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
        Money m = Money.of(100, "CHF");
        assertEquals(1, m.signum(), "signum of " + m);
        m = Money.of(-100, "CHF");
        assertEquals(-1, m.signum(), "signum of " + m);
        m = Money.of(100.3435, "CHF");
        assertEquals(1, m.signum(), "signum of " + m);
        m = Money.of(-100.3435, "CHF");
        assertEquals(-1, m.signum(), "signum of " + m);
        m = Money.of(0, "CHF");
        assertEquals(0, m.signum(), "signum of " + m);
        m = Money.of(-0, "CHF");
        assertEquals(0, m.signum(), "signum of " + m);
    }

    /**
     * Test method for {@link org.javamoney.moneta.Money#isLessThan(javax.money.MonetaryAmount)} .
     */
    @Test
    public void testIsLessThan() {
        assertFalse(Money.of(BigDecimal.valueOf(0d), "CHF").isLessThan(Money.of(BigDecimal.valueOf(0), "CHF")));
        assertFalse(Money.of(BigDecimal.valueOf(0.00000000001d), "CHF")
                .isLessThan(Money.of(BigDecimal.valueOf(0d), "CHF")));
        assertFalse(Money.of(15, "CHF").isLessThan(Money.of(10, "CHF")));
        assertFalse(Money.of(15.546, "CHF").isLessThan(Money.of(10.34, "CHF")));
        assertTrue(Money.of(5, "CHF").isLessThan(Money.of(10, "CHF")));
        assertTrue(Money.of(5.546, "CHF").isLessThan(Money.of(10.34, "CHF")));
    }

    /**
     * Test method for
     * {@link org.javamoney.moneta.Money#isLessThanOrEqualTo(javax.money.MonetaryAmount)} .
     */
    @Test
    public void testIsLessThanOrEqualTo() {
        assertTrue(Money.of(BigDecimal.valueOf(0d), "CHF").isLessThanOrEqualTo(Money.of(BigDecimal.valueOf(0), "CHF")));
        assertFalse(Money.of(BigDecimal.valueOf(0.00000000001d), "CHF")
                .isLessThanOrEqualTo(Money.of(BigDecimal.valueOf(0d), "CHF")));
        assertFalse(Money.of(15, "CHF").isLessThanOrEqualTo(Money.of(10, "CHF")));
        assertFalse(Money.of(15.546, "CHF").isLessThan(Money.of(10.34, "CHF")));
        assertTrue(Money.of(5, "CHF").isLessThanOrEqualTo(Money.of(10, "CHF")));
        assertTrue(Money.of(5.546, "CHF").isLessThanOrEqualTo(Money.of(10.34, "CHF")));
    }

    /**
     * Test method for {@link org.javamoney.moneta.Money#isGreaterThan(javax.money.MonetaryAmount)}
     * .
     */
    @Test
    public void testIsGreaterThan() {
        assertFalse(Money.of(BigDecimal.valueOf(0d), "CHF").isGreaterThan(Money.of(BigDecimal.valueOf(0), "CHF")));
        assertTrue(Money.of(BigDecimal.valueOf(0.00000000001d), "CHF")
                .isGreaterThan(Money.of(BigDecimal.valueOf(0d), "CHF")));
        assertTrue(Money.of(15, "CHF").isGreaterThan(Money.of(10, "CHF")));
        assertTrue(Money.of(15.546, "CHF").isGreaterThan(Money.of(10.34, "CHF")));
        assertFalse(Money.of(5, "CHF").isGreaterThan(Money.of(10, "CHF")));
        assertFalse(Money.of(5.546, "CHF").isGreaterThan(Money.of(10.34, "CHF")));
    }

    /**
     * Test method for
     * {@link org.javamoney.moneta.Money#isGreaterThanOrEqualTo(javax.money.MonetaryAmount)} .
     */
    @Test
    public void testIsGreaterThanOrEqualTo() {
        assertTrue(
                Money.of(BigDecimal.valueOf(0d), "CHF").isGreaterThanOrEqualTo(Money.of(BigDecimal.valueOf(0), "CHF")));
        assertTrue(Money.of(BigDecimal.valueOf(0.00000000001d), "CHF")
                .isGreaterThanOrEqualTo(Money.of(BigDecimal.valueOf(0d), "CHF")));
        assertTrue(Money.of(15, "CHF").isGreaterThanOrEqualTo(Money.of(10, "CHF")));
        assertTrue(Money.of(15.546, "CHF").isGreaterThanOrEqualTo(Money.of(10.34, "CHF")));
        assertFalse(Money.of(5, "CHF").isGreaterThanOrEqualTo(Money.of(10, "CHF")));
        assertFalse(Money.of(5.546, "CHF").isGreaterThanOrEqualTo(Money.of(10.34, "CHF")));
    }

    /**
     * Test method for {@link org.javamoney.moneta.Money#isEqualTo(javax.money.MonetaryAmount)}.
     */
    @Test
    public void testIsEqualTo() {
        assertTrue(Money.of(BigDecimal.valueOf(0d), "CHF").isEqualTo(Money.of(BigDecimal.valueOf(0), "CHF")));
        assertFalse(
                Money.of(BigDecimal.valueOf(0.00000000001d), "CHF").isEqualTo(Money.of(BigDecimal.valueOf(0d), "CHF")));
        assertTrue(Money.of(BigDecimal.valueOf(5d), "CHF").isEqualTo(Money.of(BigDecimal.valueOf(5), "CHF")));
        assertTrue(Money.of(BigDecimal.valueOf(1d), "CHF").isEqualTo(Money.of(BigDecimal.valueOf(1.00), "CHF")));
        assertTrue(Money.of(BigDecimal.valueOf(1d), "CHF").isEqualTo(Money.of(BigDecimal.ONE, "CHF")));
        assertTrue(Money.of(BigDecimal.valueOf(1), "CHF").isEqualTo(Money.of(BigDecimal.ONE, "CHF")));
        assertTrue(Money.of(new BigDecimal("1.0000"), "CHF").isEqualTo(Money.of(new BigDecimal("1.00"), "CHF")));
        // Test with different scales, but numeric equal values
        assertTrue(Money.of(BigDecimal.valueOf(0d), "CHF").isEqualTo(Money.of(BigDecimal.valueOf(0), "CHF")));
        assertTrue(Money.of(BigDecimal.ZERO, "CHF").isEqualTo(Money.of(BigDecimal.valueOf(0), "CHF")));
        assertTrue(Money.of(BigDecimal.valueOf(5), "CHF").isEqualTo(Money.of(new BigDecimal("5.0"), "CHF")));
        assertTrue(Money.of(BigDecimal.valueOf(5), "CHF").isEqualTo(Money.of(new BigDecimal("5.00"), "CHF")));
        assertTrue(Money.of(BigDecimal.valueOf(5), "CHF").isEqualTo(Money.of(new BigDecimal("5.000"), "CHF")));
        assertTrue(Money.of(BigDecimal.valueOf(5), "CHF").isEqualTo(Money.of(new BigDecimal("5.0000"), "CHF")));
        assertTrue(Money.of(new BigDecimal("-1.23"), "CHF").isEqualTo(Money.of(new BigDecimal("-1.230"), "CHF")));
        assertTrue(Money.of(new BigDecimal("-1.23"), "CHF").isEqualTo(Money.of(new BigDecimal("-1.2300"), "CHF")));
        assertTrue(Money.of(new BigDecimal("-1.23"), "CHF").isEqualTo(Money.of(new BigDecimal("-1.23000"), "CHF")));
        assertTrue(Money.of(new BigDecimal("-1.23"), "CHF")
                .isEqualTo(Money.of(new BigDecimal("-1.230000000000000000000"), "CHF")));
    }

    /**
     * Test method for {@link org.javamoney.moneta.Money#getNumber()#getNumberType()}.
     */
    @Test
    public void testGetImplementationType() {
        assertEquals(Money.of(0, "CHF").getContext().getAmountType(), Money.class);
        assertEquals(Money.of(0.34738746d, "CHF").getContext().getAmountType(), Money.class);
        assertEquals(Money.of(100034L, "CHF").getContext().getAmountType(), Money.class);
    }

    /**
     * Test method for {@link org.javamoney.moneta.Money#query(javax.money.MonetaryQuery)}.
     */
    @Test
    public void testQuery() {
        MonetaryQuery<Integer> q = amount -> Money.from(amount).getNumber().getPrecision();
        Money[] moneys = new Money[]{Money.of(100, "CHF"), Money.of(34242344, "USD"), Money.of(23123213.435, "EUR"),
                Money.of(-23123213.435, "USS"), Money.of(-23123213, "USN"), Money.of(0, "GBP")};
        for (Money money : moneys) {
            assertEquals(money.query(q), Integer.valueOf(money.getNumber().getPrecision()));
        }
    }

    /**
     * Test method for {@link org.javamoney.moneta.Money#getNumber()#asType(java.lang.Class)}.
     */
    @Test
    public void testAsTypeClassOfT() {
        Money m = Money.of(13.656, "CHF");
        assertEquals(m.getNumber().byteValue(), 13);
        assertEquals(m.getNumber().shortValue(), 13);
        assertEquals(m.getNumber().intValue(), 13);
        assertEquals(m.getNumber().longValue(), 13L);
        assertEquals(m.getNumber().floatValue(), 13.656f, 0.0);
        assertEquals(m.getNumber().doubleValue(), 13.656, 0.0);
        assertEquals(m.getNumber().numberValue(BigDecimal.class), BigDecimal.valueOf(13.656));
    }


    /**
     * Test method for {@link org.javamoney.moneta.Money#stripTrailingZeros()}.
     */
    @Test
    public void testStripTrailingZeroes() {
        assertEquals(BigDecimal.ZERO, Money.of(new BigDecimal("0.0"), "CHF").stripTrailingZeros().getNumber()
                .numberValue(BigDecimal.class));
        assertEquals(BigDecimal.ZERO, Money.of(new BigDecimal("0.00"), "CHF").stripTrailingZeros().getNumber()
                .numberValue(BigDecimal.class));
        assertEquals(BigDecimal.ZERO, Money.of(new BigDecimal("0.000"), "CHF").stripTrailingZeros().getNumber()
                .numberValue(BigDecimal.class));
        assertEquals(new BigDecimal("12.123"),
                Money.of(new BigDecimal("12.123000"), "CHF").stripTrailingZeros().getNumber()
                        .numberValue(BigDecimal.class));
        assertEquals(new BigDecimal("12.123"),
                Money.of(new BigDecimal("12.12300"), "CHF").stripTrailingZeros().getNumber()
                        .numberValue(BigDecimal.class));
        assertEquals(new BigDecimal("12.123"),
                Money.of(new BigDecimal("12.1230"), "CHF").stripTrailingZeros().getNumber()
                        .numberValue(BigDecimal.class));
        assertEquals(new BigDecimal("12.123"),
                Money.of(new BigDecimal("12.123"), "CHF").stripTrailingZeros().getNumber()
                        .numberValue(BigDecimal.class));
    }

    /**
     * Test method for {@link org.javamoney.moneta.Money#toString()}.
     */
    @Test
    public void testToString() {
        assertEquals("XXX 1.23455645", Money.of(new BigDecimal("1.23455645"), "XXX").toString());
        assertEquals("CHF 1234", Money.of(1234, "CHF").toString());
        assertEquals("CHF 1234", Money.of(new BigDecimal("1234.0"), "CHF").toString());
        assertEquals("CHF 1234.1", Money.of(new BigDecimal("1234.1"), "CHF").toString());
        assertEquals("CHF 0.01", Money.of(new BigDecimal("0.0100"), "CHF").toString());
    }

    /**
     * Test method for {@link org.javamoney.moneta.Money#with(javax.money.MonetaryOperator)}.
     */
    @Test
    public void testWithMonetaryOperator() {
        MonetaryOperator adj = amount -> Money.of(-100, amount.getCurrency());
        Money m = Money.of(1.23455645d, "XXX");
        Money a = m.with(adj);
        assertNotNull(a);
        assertNotSame(m, a);
        assertEquals(m.getCurrency(), a.getCurrency());
        assertEquals(Money.of(-100, m.getCurrency()), a);
        adj = amount -> amount.multiply(2).getFactory().setCurrency(MonetaryCurrencies.getCurrency("CHF")).create();
        a = m.with(adj);
        assertNotNull(a);
        assertNotSame(m, a);
        assertEquals(MonetaryCurrencies.getCurrency("CHF"), a.getCurrency());
        assertEquals(Money.of(1.23455645d * 2, a.getCurrency()), a);
    }

    /**
     * Test method for {@link org.javamoney.moneta.Money#from(javax.money.MonetaryAmount)}.
     */
    @Test
    public void testFrom() {
        Money m = Money.of(new BigDecimal("1.2345"), "XXX");
        Money m2 = Money.from(m);
        assertTrue(m == m2);
    }

    @Test
    public void parseTest() {
        Money money = Money.parse("EUR 25.25");
        assertEquals(money.getCurrency(), EURO);
        assertEquals(money.getNumber().doubleValue(), 25.25);
    }

    @Test
    public void testSerialization() throws IOException, ClassNotFoundException {
        Money m = Money.of(new BigDecimal("1.2345"), "XXX");
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(bos);
        oos.writeObject(m);
        oos.flush();
        ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(bos.toByteArray()));
        Money m2 = (Money) ois.readObject();
        assertEquals(m, m2);
        assertTrue(m != m2);
    }

    // Bad cases

    /**
     * Test method for {@link org.javamoney.moneta.RoundedMoney#add(javax.money.MonetaryAmount)} .
     */
    @Test(expectedExceptions = MonetaryException.class)
    public void testAdd_WrongCurrency() {
        Money m1 = Money.of(BigDecimal.TEN, EURO);
        Money m2 = Money.of(BigDecimal.TEN, "CHF");
        m1.add(m2);
    }

    /**
     * Test method for {@link org.javamoney.moneta.RoundedMoney#add(javax.money.MonetaryAmount)} .
     */
    @Test(expectedExceptions = MonetaryException.class)
    public void testSubtract_WrongCurrency() {
        Money m1 = Money.of(BigDecimal.TEN, EURO);
        Money m2 = Money.of(BigDecimal.TEN, "CHF");
        m1.subtract(m2);
    }

    /**
     * Test method for {@link org.javamoney.moneta.RoundedMoney#add(javax.money.MonetaryAmount)} .
     */
    @Test(expectedExceptions = MonetaryException.class)
    public void testDivide_WrongCurrency() {
        Money m1 = Money.of(BigDecimal.TEN, EURO);
        Money m2 = Money.of(BigDecimal.TEN, "CHF");
        m1.subtract(m2);
    }

    @Test(expectedExceptions = ArithmeticException.class)
    public void testCreatingFromDoubleNan(){
    	Money.of(Double.NaN, "XXX");
    }

    @Test(expectedExceptions = ArithmeticException.class)
    public void testCreatingFromDoublePositiveInfinity(){
    	Money.of(Double.POSITIVE_INFINITY, "XXX");
    }

    @Test(expectedExceptions = ArithmeticException.class)
    public void testCreatingFromDoubleNegativeInfinity(){
    	Money.of(Double.NEGATIVE_INFINITY, "XXX");
    }
}
