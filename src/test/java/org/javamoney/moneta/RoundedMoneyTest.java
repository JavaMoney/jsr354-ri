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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.MathContext;
import java.math.RoundingMode;

import javax.money.*;

import org.testng.annotations.Test;

/**
 * @author Anatole
 */
public class RoundedMoneyTest {

    private static final BigDecimal TEN = new BigDecimal(10.0d);
    protected static final CurrencyUnit EURO = MonetaryCurrencies.getCurrency("EUR");
    protected static final CurrencyUnit DOLLAR = MonetaryCurrencies.getCurrency("USD");

    /**
     * Test method for
     * {@link org.javamoney.moneta.RoundedMoney#of(java.math.BigDecimal, javax.money.CurrencyUnit)}
     * .
     */
    @Test
    public void testOfCurrencyUnitBigDecimal() {
        RoundedMoney m = RoundedMoney.of(TEN, MonetaryCurrencies.getCurrency("EUR"));
        assertEquals(TEN, m.getNumber().numberValue(BigDecimal.class));
    }

    @Test
    public void testOfCurrencyUnitDouble() {
        RoundedMoney m = RoundedMoney.of(10.0d, MonetaryCurrencies.getCurrency("EUR"));
        assertTrue(TEN.doubleValue() == m.getNumber().doubleValue());
    }

    /**
     * Test method for {@link org.javamoney.moneta.RoundedMoney#getCurrency()}.
     */
    @Test
    public void testGetCurrency() {
        MonetaryAmount money = RoundedMoney.of(BigDecimal.TEN, EURO);
        assertNotNull(money.getCurrency());
        assertEquals("EUR", money.getCurrency().getCurrencyCode());
    }

    @Test
    public void testSubtractMonetaryAmount() {
        RoundedMoney money1 = RoundedMoney.of(BigDecimal.TEN, EURO);
        RoundedMoney money2 = RoundedMoney.of(BigDecimal.ONE, EURO);
        RoundedMoney moneyResult = money1.subtract(money2);
        assertNotNull(moneyResult);
        assertEquals(9d, moneyResult.getNumber().doubleValue(), 0d);
    }

    @Test
    public void testDivideAndRemainder_BigDecimal() {
        RoundedMoney money1 = RoundedMoney.of(BigDecimal.ONE, EURO);
        RoundedMoney[] divideAndRemainder = money1.divideAndRemainder(new BigDecimal("0.50000001"));
        assertEquals(divideAndRemainder[0].getNumber().numberValue(BigDecimal.class), BigDecimal.ONE);
        assertEquals(divideAndRemainder[1].getNumber().numberValue(BigDecimal.class), new BigDecimal("0.5"));
    }

    @Test
    public void testDivideToIntegralValue_BigDecimal() {
        RoundedMoney money1 = RoundedMoney.of(BigDecimal.ONE, EURO);
        RoundedMoney result = money1.divideToIntegralValue(new BigDecimal("0.50000000000000000001"));
        assertEquals(result.getNumber().numberValue(BigDecimal.class), BigDecimal.ONE);
    }

    /**
     * Test method for {@link org.javamoney.moneta.RoundedMoney#hashCode()}.
     */
    @Test
    public void testHashCode() {
        RoundedMoney money1 = RoundedMoney.of(BigDecimal.ONE, EURO);
        RoundedMoney money2 = RoundedMoney.of(new BigDecimal("1"), EURO);
        assertEquals(money1.hashCode(), money2.hashCode());
        RoundedMoney money3 = RoundedMoney.of(1.0, DOLLAR);
        assertTrue(money1.hashCode() != money3.hashCode());
        assertTrue(money2.hashCode() != money3.hashCode());
        RoundedMoney money4 = RoundedMoney.of(BigDecimal.ONE, DOLLAR);
        assertTrue(money1.hashCode() != money4.hashCode());
        assertTrue(money2.hashCode() != money4.hashCode());
        RoundedMoney money5 = RoundedMoney.of(BigDecimal.ONE, DOLLAR);
        RoundedMoney money6 = RoundedMoney.of(1.0, DOLLAR);
        assertTrue(money1.hashCode() != money5.hashCode());
        assertTrue(money2.hashCode() != money5.hashCode());
        assertTrue(money1.hashCode() != money6.hashCode());
        assertTrue(money2.hashCode() != money6.hashCode());
    }

    /**
     * Test method for
     * {@link RoundedMoney#getFactory()#testGetDefaultMathContext()} .
     */
    @Test
    public void testGetDefaultMathContext() {
        RoundedMoney money1 = RoundedMoney.of(BigDecimal.ONE, EURO);
        assertEquals(RoundedMoney.DEFAULT_MONETARY_CONTEXT, money1.getContext());
    }

    /**
     * Test method for
     * {@link org.javamoney.moneta.RoundedMoney#of(Number, javax.money.CurrencyUnit, javax.money.MonetaryOperator)}
     * .
     */
    @Test
    public void testOfCurrencyUnitBigDecimalMathContext() {
        RoundedMoney m = RoundedMoney.of(BigDecimal.valueOf(2.15), EURO, new MathContext(2, RoundingMode.DOWN));
        RoundedMoney m2 = RoundedMoney.of(BigDecimal.valueOf(2.1), EURO);
        assertEquals(m, m2);
        RoundedMoney m3 = m.multiply(100);
        assertEquals(RoundedMoney.of(210, EURO), m3.abs());
    }

    /**
     * Test method for
     * {@link org.javamoney.moneta.RoundedMoney#of(Number, javax.money.CurrencyUnit)}
     * .
     */
    @Test
    public void testOfCurrencyUnitNumber() {
        RoundedMoney m = RoundedMoney.of((byte) 2, EURO);
        assertNotNull(m);
        assertEquals(EURO, m.getCurrency());
        assertEquals(Byte.valueOf((byte) 2), m.getNumber().numberValue(Byte.class));
        m = RoundedMoney.of((short) -2, DOLLAR);
        assertNotNull(m);
        assertEquals(DOLLAR, m.getCurrency());
        assertEquals(Short.valueOf((short) -2), m.getNumber().numberValue(Short.class));
        m = RoundedMoney.of(-12, EURO);
        assertNotNull(m);
        assertEquals(EURO, m.getCurrency());
        assertEquals(Integer.valueOf(-12), m.getNumber().numberValue(Integer.class));
        m = RoundedMoney.of((long) 12, DOLLAR);
        assertNotNull(m);
        assertEquals(DOLLAR, m.getCurrency());
        assertEquals(Long.valueOf(12), m.getNumber().numberValue(Long.class));
        m = RoundedMoney.of((float) 12.23, EURO);
        assertNotNull(m);
        assertEquals(EURO, m.getCurrency());
        assertEquals((float) 12.23, m.getNumber().numberValue(Float.class));
        m = RoundedMoney.of(-12.23, DOLLAR);
        assertNotNull(m);
        assertEquals(DOLLAR, m.getCurrency());
        assertEquals(-12.23, m.getNumber().numberValue(Double.class));
        m = RoundedMoney.of((Number) BigDecimal.valueOf(234.2345), EURO);
        assertNotNull(m);
        assertEquals(EURO, m.getCurrency());
        assertEquals(BigDecimal.valueOf(234.2345), m.getNumber().numberValue(BigDecimal.class));
        m = RoundedMoney.of(BigInteger.valueOf(23232312321432432L), DOLLAR);
        assertNotNull(m);
        assertEquals(DOLLAR, m.getCurrency());
        assertEquals(Long.valueOf(23232312321432432L), m.getNumber().numberValue(Long.class));
        assertEquals(BigInteger.valueOf(23232312321432432L), m.getNumber().numberValue(BigInteger.class));
    }

    /**
     * Test method for
     * {@link org.javamoney.moneta.RoundedMoney#of(Number, javax.money.CurrencyUnit, javax.money.MonetaryContext)}
     * .
     */
    @Test
    public void testOfCurrencyUnitNumberMonetaryContext() {
        MonetaryContext mc =
                MonetaryContextBuilder.of(RoundedMoney.class).setPrecision(2345).set(RoundingMode.CEILING).build();
        RoundedMoney m = RoundedMoney.of((byte) 2, EURO, mc);
        assertNotNull(m);
        assertEquals(m.getContext().get(RoundingMode.class), RoundingMode.CEILING);
        assertEquals(m.getContext().getInt("precision"), Integer.valueOf(2345));
        assertEquals(EURO, m.getCurrency());
        assertEquals(Byte.valueOf((byte) 2), m.getNumber().numberValue(Byte.class));
        m = RoundedMoney.of((short) -2, DOLLAR, mc);
        assertNotNull(m);
        assertEquals(m.getContext().get(RoundingMode.class), RoundingMode.CEILING);
        assertEquals(m.getContext().getInt("precision"), Integer.valueOf(2345));
        assertEquals(DOLLAR, m.getCurrency());
        assertEquals(Short.valueOf((short) -2), m.getNumber().numberValue(Short.class));
        m = RoundedMoney.of(-12, EURO, mc);
        assertNotNull(m);
        assertEquals(m.getContext().get(RoundingMode.class), RoundingMode.CEILING);
        assertEquals(m.getContext().getInt("precision"), Integer.valueOf(2345));
        assertEquals(EURO, m.getCurrency());
        assertEquals(Integer.valueOf(-12), m.getNumber().numberValue(Integer.class));
        m = RoundedMoney.of((long) 12, DOLLAR, mc);
        assertEquals(m.getContext().get(RoundingMode.class), RoundingMode.CEILING);
        assertEquals(m.getContext().getInt("precision"), Integer.valueOf(2345));
        assertNotNull(m);
        assertEquals(DOLLAR, m.getCurrency());
        assertEquals(Long.valueOf(12), m.getNumber().numberValue(Long.class));
        m = RoundedMoney.of((float) 12.23, EURO, mc);
        assertNotNull(m);
        assertEquals(m.getContext().get(RoundingMode.class), RoundingMode.CEILING);
        assertEquals(m.getContext().getInt("precision"), Integer.valueOf(2345));
        assertEquals(EURO, m.getCurrency());
        assertEquals((float) 12.23, m.getNumber().numberValue(Float.class));
        m = RoundedMoney.of(-12.23, DOLLAR, mc);
        assertNotNull(m);
        assertEquals(m.getContext().get(RoundingMode.class), RoundingMode.CEILING);
        assertEquals(m.getContext().getInt("precision"), Integer.valueOf(2345));
        assertEquals(DOLLAR, m.getCurrency());
        assertEquals(m.getContext().get(RoundingMode.class), RoundingMode.CEILING);
        assertEquals(m.getContext().getInt("precision"), Integer.valueOf(2345));
        assertEquals(-12.23, m.getNumber().numberValue(Double.class));
        m = RoundedMoney.of(BigDecimal.valueOf(234.2345), EURO, mc);
        assertNotNull(m);
        assertEquals(EURO, m.getCurrency());
        assertEquals(m.getContext().get(RoundingMode.class), RoundingMode.CEILING);
        assertEquals(m.getContext().getInt("precision"), Integer.valueOf(2345));
        assertEquals(BigDecimal.valueOf(234.2345), m.getNumber().numberValue(BigDecimal.class));
        m = RoundedMoney.of(BigInteger.valueOf(23232312321432432L), DOLLAR, mc);
        assertNotNull(m);
        assertEquals(DOLLAR, m.getCurrency());
        assertEquals(m.getContext().get(RoundingMode.class), RoundingMode.CEILING);
        assertEquals(m.getContext().getInt("precision"), Integer.valueOf(2345));
        assertEquals(Long.valueOf(23232312321432432L), m.getNumber().numberValue(Long.class));
        assertEquals(BigInteger.valueOf(23232312321432432L), m.getNumber().numberValue(BigInteger.class));
    }

    /**
     * Test method for
     * {@link org.javamoney.moneta.RoundedMoney#of(java.lang.Number, java.lang.String)}
     * .
     */
    @Test
    public void testOfStringNumber() {
        RoundedMoney m = RoundedMoney.of((byte) 2, "EUR");
        assertNotNull(m);
        assertEquals(EURO, m.getCurrency());
        assertEquals(Byte.valueOf((byte) 2), m.getNumber().numberValue(Byte.class));
        m = RoundedMoney.of((short) -2, "USD");
        assertNotNull(m);
        assertEquals(DOLLAR, m.getCurrency());
        assertEquals(Short.valueOf((short) -2), m.getNumber().numberValue(Short.class));
        m = RoundedMoney.of(-12, "EUR");
        assertNotNull(m);
        assertEquals(EURO, m.getCurrency());
        assertEquals(Integer.valueOf(-12), m.getNumber().numberValue(Integer.class));
        m = RoundedMoney.of((long) 12, "USD");
        assertNotNull(m);
        assertEquals(DOLLAR, m.getCurrency());
        assertEquals(Long.valueOf(12), m.getNumber().numberValue(Long.class));
        m = RoundedMoney.of((float) 12.23, "EUR");
        assertNotNull(m);
        assertEquals(EURO, m.getCurrency());
        assertEquals(m.getNumber().numberValue(Float.class), (float) 12.23);
        m = RoundedMoney.of(-12.23, "USD");
        assertNotNull(m);
        assertEquals(DOLLAR, m.getCurrency());
        assertEquals(-12.23, m.getNumber().numberValue(Double.class));
        m = RoundedMoney.of(BigDecimal.valueOf(234.2345), "EUR");
        assertNotNull(m);
        assertEquals(EURO, m.getCurrency());
        assertEquals(BigDecimal.valueOf(234.2345), m.getNumber().numberValue(BigDecimal.class));
        m = RoundedMoney.of(BigInteger.valueOf(23232312321432432L), "USD");
        assertNotNull(m);
        assertEquals(DOLLAR, m.getCurrency());
        assertEquals(Long.valueOf(23232312321432432L), m.getNumber().numberValue(Long.class));
        assertEquals(BigInteger.valueOf(23232312321432432L), m.getNumber().numberValue(BigInteger.class));
    }

    /**
     * Test a round trip using from.
     */
    @Test
    public void testRoundtrip() {
        RoundedMoney m = RoundedMoney.of(new BigDecimal("0.5"), "USD");
        Money mm = Money.from(m);
        RoundedMoney m2 = RoundedMoney.from(mm);
        assertEquals(m, m2);
    }

    /**
     * Test method for
     * {@link org.javamoney.moneta.RoundedMoney#of(java.lang.Number, java.lang.String, javax.money.MonetaryContext)}
     * .
     */
    @Test
    public void testOfStringNumberMathContext() {
        MonetaryContext mc =
                MonetaryContextBuilder.of(RoundedMoney.class).setPrecision(2345).set(RoundingMode.CEILING).build();
        RoundedMoney m = RoundedMoney.of((byte) 2, "EUR", mc);
        assertNotNull(m);
        assertEquals(m.getContext().get(RoundingMode.class), RoundingMode.CEILING);
        assertEquals(m.getContext().getInt("precision"), Integer.valueOf(2345));
        assertEquals(EURO, m.getCurrency());
        assertEquals(Byte.valueOf((byte) 2), m.getNumber().numberValue(Byte.class));
        m = RoundedMoney.of((short) -2, "USD", mc);
        assertNotNull(m);
        assertEquals(m.getContext().get(RoundingMode.class), RoundingMode.CEILING);
        assertEquals(m.getContext().getInt("precision"), Integer.valueOf(2345));
        assertEquals(DOLLAR, m.getCurrency());
        assertEquals(Short.valueOf((short) -2), m.getNumber().numberValue(Short.class));
        m = RoundedMoney.of(-12, "EUR", mc);
        assertNotNull(m);
        assertEquals(m.getContext().get(RoundingMode.class), RoundingMode.CEILING);
        assertEquals(m.getContext().getInt("precision"), Integer.valueOf(2345));
        assertEquals(EURO, m.getCurrency());
        assertEquals(Integer.valueOf(-12), m.getNumber().numberValue(Integer.class));
        m = RoundedMoney.of((long) 12, "USD", mc);
        assertEquals(m.getContext().get(RoundingMode.class), RoundingMode.CEILING);
        assertEquals(m.getContext().getInt("precision"), Integer.valueOf(2345));
        assertNotNull(m);
        assertEquals(DOLLAR, m.getCurrency());
        assertEquals(Long.valueOf(12), m.getNumber().numberValue(Long.class));
        m = RoundedMoney.of((float) 12.23, "EUR", mc);
        assertNotNull(m);
        assertEquals(m.getContext().get(RoundingMode.class), RoundingMode.CEILING);
        assertEquals(m.getContext().getInt("precision"), Integer.valueOf(2345));
        assertEquals(EURO, m.getCurrency());
        assertEquals((float) 12.23, m.getNumber().numberValue(Float.class));
        m = RoundedMoney.of(-12.23, "USD", mc);
        assertNotNull(m);
        assertEquals(m.getContext().get(RoundingMode.class), RoundingMode.CEILING);
        assertEquals(m.getContext().getInt("precision"), Integer.valueOf(2345));
        assertEquals(DOLLAR, m.getCurrency());
        assertEquals(m.getContext().get(RoundingMode.class), RoundingMode.CEILING);
        assertEquals(m.getContext().getInt("precision"), Integer.valueOf(2345));
        assertEquals(-12.23, m.getNumber().numberValue(Double.class));
        m = RoundedMoney.of(BigDecimal.valueOf(234.2345), "EUR", mc);
        assertNotNull(m);
        assertEquals(EURO, m.getCurrency());
        assertEquals(m.getContext().get(RoundingMode.class), RoundingMode.CEILING);
        assertEquals(m.getContext().getInt("precision"), Integer.valueOf(2345));
        assertEquals(BigDecimal.valueOf(234.2345), m.getNumber().numberValue(BigDecimal.class));
        m = RoundedMoney.of(BigInteger.valueOf(23232312321432432L), "USD", mc);
        assertNotNull(m);
        assertEquals(DOLLAR, m.getCurrency());
        assertEquals(m.getContext().get(RoundingMode.class), RoundingMode.CEILING);
        assertEquals(m.getContext().getInt("precision"), Integer.valueOf(2345));
        assertEquals(Long.valueOf(23232312321432432L), m.getNumber().numberValue(Long.class));
        assertEquals(BigInteger.valueOf(23232312321432432L), m.getNumber().numberValue(BigInteger.class));
    }

    /**
     * Test method for
     * {@link org.javamoney.moneta.RoundedMoney#equals(java.lang.Object)} .
     */
    @Test
    public void testEqualsObject() {
        RoundedMoney[] moneys =
                new RoundedMoney[]{RoundedMoney.of(BigDecimal.ZERO, "CHF"), RoundedMoney.of(BigDecimal.ONE, "CHF"),
                        RoundedMoney.of(BigDecimal.ONE, "XXX"), RoundedMoney.of(BigDecimal.ONE.negate(), "XXX")};
        for (int i = 0; i < moneys.length; i++) {
            for (int j = 0; j < moneys.length; j++) {
                if (i == j) {
                    assertEquals(moneys[i], moneys[j]);
                } else {
                    assertNotSame(moneys[i], moneys[j]);
                }
            }
        }
    }

    /**
     * Test method for
     * {@link org.javamoney.moneta.RoundedMoney#compareTo(javax.money.MonetaryAmount)}
     * .
     */
    @Test
    public void testCompareTo() {
        RoundedMoney m1 = RoundedMoney.of(-2, "CHF");
        RoundedMoney m2 = RoundedMoney.of(0, "CHF");
        RoundedMoney m3 = RoundedMoney.of(-0, "CHF");
        RoundedMoney m4 = RoundedMoney.of(2, "CHF");
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
     * {@link RoundedMoney#getContext()} .
     */
    @Test
    public void testGetMonetaryContext() {
        RoundedMoney m = RoundedMoney.of(10, "CHF");
        assertEquals(RoundedMoney.DEFAULT_MONETARY_CONTEXT, m.getContext());
        MonetaryContext mc =
                MonetaryContextBuilder.of(RoundedMoney.class).setPrecision(2345).set(RoundingMode.CEILING).build();
        m = RoundedMoney.of(10, "CHF", mc);
        assertEquals(m.getContext().get(RoundingMode.class), RoundingMode.CEILING);
        assertEquals((int) m.getContext().getInt("precision"), 2345);
    }

    /**
     * Test method for {@link org.javamoney.moneta.RoundedMoney#abs()}.
     */
    @Test
    public void testAbs() {
        RoundedMoney m = RoundedMoney.of(10, "CHF");
        assertEquals(m, m.abs());
        assertTrue(m == m.abs());
        m = RoundedMoney.of(0, "CHF");
        assertEquals(m, m.abs());
        assertTrue(m == m.abs());
        m = RoundedMoney.of(-10, "CHF");
        assertEquals(m.negate(), m.abs());
        assertTrue(m != m.abs());
    }

    /**
     * Test method for
     * {@link org.javamoney.moneta.RoundedMoney#add(javax.money.MonetaryAmount)}
     * .
     */
    @Test
    public void testAdd() {
        RoundedMoney money1 = RoundedMoney.of(BigDecimal.TEN, EURO);
        RoundedMoney money2 = RoundedMoney.of(BigDecimal.ONE, EURO);
        RoundedMoney moneyResult = money1.add(money2);
        assertNotNull(moneyResult);
        assertEquals(11d, moneyResult.getNumber().doubleValue(), 0d);
    }

    /**
     * Test method for
     * {@link org.javamoney.moneta.RoundedMoney#divide(java.lang.Number)} .
     */
    @Test
    public void testDivideNumber() {
        RoundedMoney m = RoundedMoney.of(100, "CHF");
        assertEquals(RoundedMoney.of(new BigDecimal("100.00").divide(BigDecimal.valueOf(5)), "CHF"),
                m.divide(BigDecimal.valueOf(5)));
    }

    /**
     * Test method for
     * {@link org.javamoney.moneta.RoundedMoney#divideAndRemainder(java.lang.Number)}
     * .
     */
    @Test
    public void testDivideAndRemainderNumber() {
        RoundedMoney m = RoundedMoney.of(100, "CHF");
        assertEquals(RoundedMoney.of(BigDecimal.valueOf(33), "CHF"), m.divideAndRemainder(BigDecimal.valueOf(3))[0]);
        assertEquals(RoundedMoney.of(BigDecimal.valueOf(1), "CHF"), m.divideAndRemainder(BigDecimal.valueOf(3))[1]);
    }

    /**
     * Test method for
     * {@link org.javamoney.moneta.RoundedMoney#divideToIntegralValue(java.lang.Number)}
     * .
     */
    @Test
    public void testDivideToIntegralValueNumber() {
        RoundedMoney m = RoundedMoney.of(100, "CHF");
        assertEquals(RoundedMoney.of(BigDecimal.valueOf(5), "CHF"), m.divideToIntegralValue(BigDecimal.valueOf(20)));
        assertEquals(RoundedMoney.of(BigDecimal.valueOf(33), "CHF"), m.divideToIntegralValue(BigDecimal.valueOf(3)));
    }

    /**
     * Test method for
     * {@link org.javamoney.moneta.RoundedMoney#multiply(java.lang.Number)}.
     */
    @Test
    public void testMultiplyNumber() {
        RoundedMoney m = RoundedMoney.of(100, "CHF");
        assertEquals(RoundedMoney.of(new BigDecimal("400.00"), "CHF"), m.multiply(4));
        assertEquals(RoundedMoney.of(new BigDecimal("200.00"), "CHF"), m.multiply(2));
        assertEquals(RoundedMoney.of(new BigDecimal("50.0"), "CHF"), m.multiply(new BigDecimal("0.5")));
    }

    /**
     * Test method for {@link org.javamoney.moneta.RoundedMoney#negate()}.
     */
    @Test
    public void testNegate() {
        RoundedMoney m = RoundedMoney.of(100, "CHF");
        assertEquals(RoundedMoney.of(-100, "CHF"), m.negate());
        m = RoundedMoney.of(-123.234, "CHF");
        assertEquals(RoundedMoney.of(123.234, "CHF"), m.negate());
    }

    /**
     * Test method for {@link org.javamoney.moneta.RoundedMoney#plus()}.
     */
    @Test
    public void testPlus() {
        RoundedMoney m = RoundedMoney.of(100, "CHF");
        assertEquals(RoundedMoney.of(100, "CHF"), m.plus());
        m = RoundedMoney.of(123.234, "CHF");
        assertEquals(RoundedMoney.of(123.234, "CHF"), m.plus());
    }

    /**
     * Test method for
     * {@link org.javamoney.moneta.RoundedMoney#subtract(javax.money.MonetaryAmount)}
     * .
     */
    @Test
    public void testSubtract() {
        RoundedMoney m = RoundedMoney.of(100, "CHF");
        RoundedMoney s1 = RoundedMoney.of(100, "CHF");
        RoundedMoney s2 = RoundedMoney.of(200, "CHF");
        RoundedMoney s3 = RoundedMoney.of(0, "CHF");
        assertEquals(RoundedMoney.of(0, "CHF"), m.subtract(s1));
        assertEquals(RoundedMoney.of(-100, "CHF"), m.subtract(s2));
        assertEquals(RoundedMoney.of(100, "CHF"), m.subtract(s3));
        assertTrue(m == m.subtract(s3));
        m = RoundedMoney.of(new BigDecimal("-123.234"), "CHF");
        assertEquals(RoundedMoney.of(new BigDecimal("-223.234"), "CHF"), m.subtract(s1));
        assertEquals(RoundedMoney.of(new BigDecimal("-323.234"), "CHF"), m.subtract(s2));
        assertEquals(RoundedMoney.of(new BigDecimal("-123.234"), "CHF"), m.subtract(s3));
        assertTrue(m == m.subtract(s3));
        m = RoundedMoney.of(new BigDecimal("12.402345534"), "CHF");
        s1 = RoundedMoney.of(new BigDecimal("2343.45"), "CHF");
        s2 = RoundedMoney.of(new BigDecimal("12.402345534"), "CHF");
        s3 = RoundedMoney.of(new BigDecimal("-2343.45"), "CHF");
        assertEquals(RoundedMoney.of(new BigDecimal("12.402345534").subtract(new BigDecimal("2343.45")), "CHF"),
                m.subtract(s1));
        assertEquals(RoundedMoney.of(new BigDecimal("12.402345534").subtract(new BigDecimal("12.402345534")), "CHF"),
                m.subtract(s2));
        assertTrue(m.subtract(s2).isZero());
        assertEquals(RoundedMoney.of(new BigDecimal("2355.852345534"), "CHF"), m.subtract(s3));
        assertTrue(m == m.subtract(RoundedMoney.of(0, "CHF")));
    }

    /**
     * Test method for {@link org.javamoney.moneta.RoundedMoney#pow(int)}.
     */
    @Test
    public void testPow() {
        RoundedMoney m = RoundedMoney.of(new BigDecimal("23.23"), "CHF");
        for (int p = 0; p < 10; p++) {
            assertEquals(RoundedMoney.of(m.getNumber().numberValue(BigDecimal.class).pow(p)
                    .setScale(2, RoundingMode.HALF_EVEN), "CHF"), m.pow(p));
        }
    }

    /**
     * Test method for {@link org.javamoney.moneta.RoundedMoney#ulp()}.
     */
    @Test
    public void testUlp() {
        RoundedMoney[] moneys = new RoundedMoney[]{RoundedMoney.of(100, "CHF"), RoundedMoney.of(34242344, "CHF"),
                RoundedMoney.of(23123213.435, "CHF"), RoundedMoney.of(0, "CHF"), RoundedMoney.of(-100, "CHF"),
                RoundedMoney.of(-723527.36532, "CHF")};
        for (RoundedMoney m : moneys) {
            assertEquals(m.with(m.getNumber().numberValue(BigDecimal.class).ulp()), m.ulp(), "Invalid ulp.");
        }
    }

    /**
     * Test method for
     * {@link org.javamoney.moneta.RoundedMoney#remainder(java.lang.Number)}.
     */
    @Test
    public void testRemainderNumber() {
        RoundedMoney[] moneys = new RoundedMoney[]{RoundedMoney.of(100, "CHF"), RoundedMoney.of(34242344, "CHF"),
                RoundedMoney.of(23123213.435, "CHF"), RoundedMoney.of(0, "CHF"), RoundedMoney.of(-100, "CHF"),
                RoundedMoney.of(-723527.36532, "CHF")};
        for (RoundedMoney m : moneys) {
            assertEquals(m.with(m.getNumber().numberValue(BigDecimal.class).remainder(BigDecimal.valueOf(10.50))),
                    m.remainder(10.50), "Invalid remainder of " + 10.50);
            assertEquals(m.with(m.getNumber().numberValue(BigDecimal.class).remainder(BigDecimal.valueOf(-30.20))),
                    m.remainder(-30.20), "Invalid remainder of " + -30.20);
            assertEquals(m.with(m.getNumber().numberValue(BigDecimal.class).remainder(BigDecimal.valueOf(-3))),
                    m.remainder(-3), "Invalid remainder of " + -3);
            assertEquals(m.with(m.getNumber().numberValue(BigDecimal.class).remainder(BigDecimal.valueOf(3))),
                    m.remainder(3), "Invalid remainder of " + 3);
        }
    }

    /**
     * Test method for
     * {@link org.javamoney.moneta.RoundedMoney#scaleByPowerOfTen(int)} .
     */
    @Test
    public void testScaleByPowerOfTen() {
        RoundedMoney[] moneys = new RoundedMoney[]{RoundedMoney.of(100, "CHF"), RoundedMoney.of(34242344, "CHF"),
                RoundedMoney.of(23123213.435, "CHF"), RoundedMoney.of(0, "CHF"), RoundedMoney.of(-100, "CHF"),
                RoundedMoney.of(-723527.36532, "CHF")};
        for (RoundedMoney m : moneys) {
            for (int p = -10; p < 10; p++) {
                assertEquals(m.with(m.getNumber().numberValue(BigDecimal.class).scaleByPowerOfTen(p)),
                        m.scaleByPowerOfTen(p), "Invalid scaleByPowerOfTen.");
            }
        }
    }

    /**
     * Test method for {@link org.javamoney.moneta.RoundedMoney#isZero()}.
     */
    @Test
    public void testIsZero() {
        RoundedMoney[] moneys = new RoundedMoney[]{RoundedMoney.of(100, "CHF"), RoundedMoney.of(34242344, "CHF"),
                RoundedMoney.of(23123213.435, "CHF"), RoundedMoney.of(-100, "CHF"),
                RoundedMoney.of(-723527.36532, "CHF")};
        for (RoundedMoney m : moneys) {
            assertFalse(m.isZero());
        }
        moneys = new RoundedMoney[]{RoundedMoney.of(0, "CHF"), RoundedMoney.of(0.0, "CHF"),
                RoundedMoney.of(BigDecimal.ZERO, "CHF"), RoundedMoney.of(new BigDecimal("0.00000000000000000"), "CHF")};
        for (RoundedMoney m : moneys) {
            assertTrue(m.isZero());
        }
    }

    /**
     * Test method for {@link org.javamoney.moneta.RoundedMoney#isPositive()}.
     */
    @Test
    public void testIsPositive() {
        RoundedMoney[] moneys = new RoundedMoney[]{RoundedMoney.of(100, "CHF"), RoundedMoney.of(34242344, "CHF"),
                RoundedMoney.of(23123213.435, "CHF")};
        for (RoundedMoney m : moneys) {
            assertTrue(m.isPositive());
        }
        moneys = new RoundedMoney[]{RoundedMoney.of(0, "CHF"), RoundedMoney.of(0.0, "CHF"),
                RoundedMoney.of(BigDecimal.ZERO, "CHF"), RoundedMoney.of(new BigDecimal("0.00000000000000000"), "CHF"),
                RoundedMoney.of(-100, "CHF"), RoundedMoney.of(-34242344, "CHF"), RoundedMoney.of(-23123213.435, "CHF")};
        for (RoundedMoney m : moneys) {
            assertFalse(m.isPositive());
        }
    }

    /**
     * Test method for
     * {@link org.javamoney.moneta.RoundedMoney#isPositiveOrZero()}.
     */
    @Test
    public void testIsPositiveOrZero() {
        RoundedMoney[] moneys = new RoundedMoney[]{RoundedMoney.of(0, "CHF"), RoundedMoney.of(0.0, "CHF"),
                RoundedMoney.of(BigDecimal.ZERO, "CHF"), RoundedMoney.of(new BigDecimal("0.00000000000000000"), "CHF"),
                RoundedMoney.of(100, "CHF"), RoundedMoney.of(34242344, "CHF"), RoundedMoney.of(23123213.435, "CHF")};
        for (RoundedMoney m : moneys) {
            assertTrue(m.isPositiveOrZero(), "Invalid positiveOrZero (expected true): " + m);
        }
        moneys = new RoundedMoney[]{RoundedMoney.of(-100, "CHF"), RoundedMoney.of(-34242344, "CHF"),
                RoundedMoney.of(-23123213.435, "CHF")};
        for (RoundedMoney m : moneys) {
            assertFalse(m.isPositiveOrZero(), "Invalid positiveOrZero (expected false): " + m);
        }
    }

    /**
     * Test method for {@link org.javamoney.moneta.RoundedMoney#isNegative()}.
     */
    @Test
    public void testIsNegative() {
        RoundedMoney[] moneys = new RoundedMoney[]{RoundedMoney.of(0, "CHF"), RoundedMoney.of(0.0, "CHF"),
                RoundedMoney.of(BigDecimal.ZERO, "CHF"), RoundedMoney.of(new BigDecimal("0.00000000000000000"), "CHF"),
                RoundedMoney.of(100, "CHF"), RoundedMoney.of(34242344, "CHF"), RoundedMoney.of(23123213.435, "CHF")};
        for (RoundedMoney m : moneys) {
            assertFalse(m.isNegative(), "Invalid isNegative (expected false): " + m);
        }
        moneys = new RoundedMoney[]{RoundedMoney.of(-100, "CHF"), RoundedMoney.of(-34242344, "CHF"),
                RoundedMoney.of(-23123213.435, "CHF")};
        for (RoundedMoney m : moneys) {
            assertTrue(m.isNegative(), "Invalid isNegative (expected true): " + m);
        }
    }

    /**
     * Test method for
     * {@link org.javamoney.moneta.RoundedMoney#isNegativeOrZero()}.
     */
    @Test
    public void testIsNegativeOrZero() {
        RoundedMoney[] moneys = new RoundedMoney[]{RoundedMoney.of(100, "CHF"), RoundedMoney.of(34242344, "CHF"),
                RoundedMoney.of(23123213.435, "CHF")};
        for (RoundedMoney m : moneys) {
            assertFalse(m.isNegativeOrZero(), "Invalid negativeOrZero (expected false): " + m);
        }
        moneys = new RoundedMoney[]{RoundedMoney.of(0, "CHF"), RoundedMoney.of(0.0, "CHF"),
                RoundedMoney.of(BigDecimal.ZERO, "CHF"), RoundedMoney.of(new BigDecimal("0.00000000000000000"), "CHF"),
                RoundedMoney.of(-100, "CHF"), RoundedMoney.of(-34242344, "CHF"), RoundedMoney.of(-23123213.435, "CHF")};
        for (RoundedMoney m : moneys) {
            assertTrue(m.isNegativeOrZero(), "Invalid negativeOrZero (expected true): " + m);
        }
    }

    /**
     * Test method for
     * {@link org.javamoney.moneta.RoundedMoney#with(java.lang.Number)} .
     */
    @Test
    public void testWithNumber() {
        RoundedMoney[] moneys = new RoundedMoney[]{RoundedMoney.of(100, "CHF"), RoundedMoney.of(34242344, "CHF"),
                RoundedMoney.of(new BigDecimal("23123213.435"), "CHF"),
                RoundedMoney.of(new BigDecimal("-23123213.435"), "CHF"), RoundedMoney.of(-23123213, "CHF"),
                RoundedMoney.of(0, "CHF")};
        RoundedMoney s = RoundedMoney.of(10, "CHF");
        RoundedMoney[] moneys2 =
                new RoundedMoney[]{s.with(100), s.with(34242344), s.with(new BigDecimal("23123213.435")),
                        s.with(new BigDecimal("-23123213.435")), s.with(-23123213), s.with(0)};
        for (int i = 0; i < moneys.length; i++) {
            assertEquals(moneys[i], moneys2[i], "with(Number) failed.");
        }
    }

    /**
     * Test method for
     * {@link org.javamoney.moneta.RoundedMoney#with(javax.money.CurrencyUnit, java.lang.Number)}
     * .
     */
    @Test
    public void testWithCurrencyUnitNumber() {
        RoundedMoney[] moneys = new RoundedMoney[]{RoundedMoney.of(100, "CHF"), RoundedMoney.of(34242344, "USD"),
                RoundedMoney.of(new BigDecimal("23123213.435"), "EUR"),
                RoundedMoney.of(new BigDecimal("-23123213.435"), "USS"), RoundedMoney.of(-23123213, "USN"),
                RoundedMoney.of(0, "GBP")};
        RoundedMoney s = RoundedMoney.of(10, "XXX");
        RoundedMoney[] moneys2 = new RoundedMoney[]{s.with(MonetaryCurrencies.getCurrency("CHF"), 100),
                s.with(MonetaryCurrencies.getCurrency("USD"), 34242344),
                s.with(MonetaryCurrencies.getCurrency("EUR"), new BigDecimal("23123213.435")),
                s.with(MonetaryCurrencies.getCurrency("USS"), new BigDecimal("-23123213.435")),
                s.with(MonetaryCurrencies.getCurrency("USN"), -23123213),
                s.with(MonetaryCurrencies.getCurrency("GBP"), 0)};
        for (int i = 0; i < moneys.length; i++) {
            assertEquals(moneys[i], moneys2[i], "with(Number) failed.");
        }
    }

    /**
     * Test method for {@link org.javamoney.moneta.RoundedMoney#getScale()}.
     */
    @Test
    public void testGetScale() {
        RoundedMoney[] moneys = new RoundedMoney[]{RoundedMoney.of(100, "CHF"), RoundedMoney.of(34242344, "USD"),
                RoundedMoney.of(23123213.435, "EUR"), RoundedMoney.of(-23123213.435, "USS"),
                RoundedMoney.of(-23123213, "USN"), RoundedMoney.of(0, "GBP")};
        for (RoundedMoney m : moneys) {
            assertEquals(m.getNumber().numberValue(BigDecimal.class).scale(), m.getScale(), "Scale for " + m);
        }
    }

    /**
     * Test method for {@link org.javamoney.moneta.RoundedMoney#getPrecision()}.
     */
    @Test
    public void testGetPrecision() {
        RoundedMoney[] moneys = new RoundedMoney[]{RoundedMoney.of(100, "CHF"), RoundedMoney.of(34242344, "USD"),
                RoundedMoney.of(23123213.435, "EUR"), RoundedMoney.of(-23123213.435, "USS"),
                RoundedMoney.of(-23123213, "USN"), RoundedMoney.of(0, "GBP")};
        for (RoundedMoney m : moneys) {
            assertEquals(m.getNumber().numberValue(BigDecimal.class).precision(), m.getPrecision(),
                    "Precision for " + m);
        }
    }

    /**
     * Test method for {@link org.javamoney.moneta.RoundedMoney#signum()}.
     */
    @Test
    public void testSignum() {
        RoundedMoney m = RoundedMoney.of(100, "CHF");
        assertEquals(1, m.signum(), "signum of " + m);
        m = RoundedMoney.of(-100, "CHF");
        assertEquals(-1, m.signum(), "signum of " + m);
        m = RoundedMoney.of(100.3435, "CHF");
        assertEquals(1, m.signum(), "signum of " + m);
        m = RoundedMoney.of(-100.3435, "CHF");
        assertEquals(-1, m.signum(), "signum of " + m);
        m = RoundedMoney.of(0, "CHF");
        assertEquals(0, m.signum(), "signum of " + m);
        m = RoundedMoney.of(-0, "CHF");
        assertEquals(0, m.signum(), "signum of " + m);
    }

    /**
     * Test method for
     * {@link org.javamoney.moneta.RoundedMoney#isLessThan(javax.money.MonetaryAmount)}
     * .
     */
    @Test
    public void testIsLessThan() {
        assertFalse(RoundedMoney.of(BigDecimal.valueOf(0d), "CHF")
                .isLessThan(RoundedMoney.of(BigDecimal.valueOf(0), "CHF")));
        assertFalse(RoundedMoney.of(BigDecimal.valueOf(0.00000000001d), "CHF")
                .isLessThan(RoundedMoney.of(BigDecimal.valueOf(0d), "CHF")));
        assertFalse(RoundedMoney.of(15, "CHF").isLessThan(RoundedMoney.of(10, "CHF")));
        assertFalse(RoundedMoney.of(15.546, "CHF").isLessThan(RoundedMoney.of(10.34, "CHF")));
        assertTrue(RoundedMoney.of(5, "CHF").isLessThan(RoundedMoney.of(10, "CHF")));
        assertTrue(RoundedMoney.of(5.546, "CHF").isLessThan(RoundedMoney.of(10.34, "CHF")));
    }

    /**
     * Test method for
     * {@link org.javamoney.moneta.RoundedMoney#isLessThanOrEqualTo(javax.money.MonetaryAmount)}
     * .
     */
    @Test
    public void testIsLessThanOrEqualTo() {
        assertTrue(RoundedMoney.of(BigDecimal.valueOf(0d), "CHF")
                .isLessThanOrEqualTo(RoundedMoney.of(BigDecimal.valueOf(0), "CHF")));
        assertFalse(RoundedMoney.of(BigDecimal.valueOf(0.00000000001d), "CHF")
                .isLessThanOrEqualTo(RoundedMoney.of(BigDecimal.valueOf(0d), "CHF")));
        assertFalse(RoundedMoney.of(15, "CHF").isLessThanOrEqualTo(RoundedMoney.of(10, "CHF")));
        assertFalse(RoundedMoney.of(15.546, "CHF").isLessThan(RoundedMoney.of(10.34, "CHF")));
        assertTrue(RoundedMoney.of(5, "CHF").isLessThanOrEqualTo(RoundedMoney.of(10, "CHF")));
        assertTrue(RoundedMoney.of(5.546, "CHF").isLessThanOrEqualTo(RoundedMoney.of(10.34, "CHF")));
    }

    /**
     * Test method for
     * {@link org.javamoney.moneta.RoundedMoney#isGreaterThan(javax.money.MonetaryAmount)}
     * .
     */
    @Test
    public void testIsGreaterThan() {
        assertFalse(RoundedMoney.of(BigDecimal.valueOf(0d), "CHF")
                .isGreaterThan(RoundedMoney.of(BigDecimal.valueOf(0), "CHF")));
        assertTrue(RoundedMoney.of(BigDecimal.valueOf(0.00000000001d), "CHF")
                .isGreaterThan(RoundedMoney.of(BigDecimal.valueOf(0d), "CHF")));
        assertTrue(RoundedMoney.of(15, "CHF").isGreaterThan(RoundedMoney.of(10, "CHF")));
        assertTrue(RoundedMoney.of(15.546, "CHF").isGreaterThan(RoundedMoney.of(10.34, "CHF")));
        assertFalse(RoundedMoney.of(5, "CHF").isGreaterThan(RoundedMoney.of(10, "CHF")));
        assertFalse(RoundedMoney.of(5.546, "CHF").isGreaterThan(RoundedMoney.of(10.34, "CHF")));
    }

    /**
     * Test method for
     * {@link org.javamoney.moneta.RoundedMoney#isGreaterThanOrEqualTo(javax.money.MonetaryAmount)}
     * .
     */
    @Test
    public void testIsGreaterThanOrEqualTo() {
        assertTrue(RoundedMoney.of(BigDecimal.valueOf(0d), "CHF")
                .isGreaterThanOrEqualTo(RoundedMoney.of(BigDecimal.valueOf(0), "CHF")));
        assertTrue(RoundedMoney.of(BigDecimal.valueOf(0.00000000001d), "CHF")
                .isGreaterThanOrEqualTo(RoundedMoney.of(BigDecimal.valueOf(0d), "CHF")));
        assertTrue(RoundedMoney.of(15, "CHF").isGreaterThanOrEqualTo(RoundedMoney.of(10, "CHF")));
        assertTrue(RoundedMoney.of(15.546, "CHF").isGreaterThanOrEqualTo(RoundedMoney.of(10.34, "CHF")));
        assertFalse(RoundedMoney.of(5, "CHF").isGreaterThanOrEqualTo(RoundedMoney.of(10, "CHF")));
        assertFalse(RoundedMoney.of(5.546, "CHF").isGreaterThanOrEqualTo(RoundedMoney.of(10.34, "CHF")));
    }

    /**
     * Test method for
     * {@link org.javamoney.moneta.RoundedMoney#isEqualTo(javax.money.MonetaryAmount)}
     * .
     */
    @Test
    public void testIsEqualTo() {
        assertTrue(RoundedMoney.of(BigDecimal.valueOf(0d), "CHF")
                .isEqualTo(RoundedMoney.of(BigDecimal.valueOf(0), "CHF")));
        assertFalse(RoundedMoney.of(BigDecimal.valueOf(0.00000000001d), "CHF")
                .isEqualTo(RoundedMoney.of(BigDecimal.valueOf(0d), "CHF")));
        assertTrue(RoundedMoney.of(BigDecimal.valueOf(5d), "CHF")
                .isEqualTo(RoundedMoney.of(BigDecimal.valueOf(5), "CHF")));
        assertTrue(RoundedMoney.of(BigDecimal.valueOf(1d), "CHF")
                .isEqualTo(RoundedMoney.of(BigDecimal.valueOf(1.00), "CHF")));
        assertTrue(RoundedMoney.of(BigDecimal.valueOf(1d), "CHF").isEqualTo(RoundedMoney.of(BigDecimal.ONE, "CHF")));
        assertTrue(RoundedMoney.of(BigDecimal.valueOf(1), "CHF").isEqualTo(RoundedMoney.of(BigDecimal.ONE, "CHF")));
        assertTrue(RoundedMoney.of(new BigDecimal("1.0000"), "CHF")
                .isEqualTo(RoundedMoney.of(new BigDecimal("1.00"), "CHF")));
    }

    /**
     * Test method for
     * {@link org.javamoney.moneta.RoundedMoney#isNotEqualTo(javax.money.MonetaryAmount)}
     * .
     */
    @Test
    public void testIsNotEqualTo() {
        assertFalse(RoundedMoney.of(BigDecimal.valueOf(0d), "CHF")
                .isNotEqualTo(RoundedMoney.of(BigDecimal.valueOf(0), "CHF")));
        assertTrue(RoundedMoney.of(BigDecimal.valueOf(0.00000000001d), "CHF")
                .isNotEqualTo(RoundedMoney.of(BigDecimal.valueOf(0d), "CHF")));
        assertFalse(RoundedMoney.of(BigDecimal.valueOf(5d), "CHF")
                .isNotEqualTo(RoundedMoney.of(BigDecimal.valueOf(5), "CHF")));
        assertFalse(RoundedMoney.of(BigDecimal.valueOf(1d), "CHF")
                .isNotEqualTo(RoundedMoney.of(BigDecimal.valueOf(1.00), "CHF")));
        assertFalse(
                RoundedMoney.of(BigDecimal.valueOf(1d), "CHF").isNotEqualTo(RoundedMoney.of(BigDecimal.ONE, "CHF")));
        assertFalse(RoundedMoney.of(BigDecimal.valueOf(1), "CHF").isNotEqualTo(RoundedMoney.of(BigDecimal.ONE, "CHF")));
        assertFalse(RoundedMoney.of(new BigDecimal("1.0000"), "CHF")
                .isNotEqualTo(RoundedMoney.of(new BigDecimal("1.00"), "CHF")));
    }

    /**
     * Test method for
     * {@link org.javamoney.moneta.RoundedMoney#query(javax.money.MonetaryQuery)}
     * .
     */
    @Test
    public void testQuery() {
        MonetaryQuery<Integer> q = amount -> RoundedMoney.from(amount).getPrecision();
        RoundedMoney[] moneys = new RoundedMoney[]{RoundedMoney.of(100, "CHF"), RoundedMoney.of(34242344, "USD"),
                RoundedMoney.of(23123213.435, "EUR"), RoundedMoney.of(-23123213.435, "USS"),
                RoundedMoney.of(-23123213, "USN"), RoundedMoney.of(0, "GBP")};
        for (RoundedMoney money : moneys) {
            assertEquals(money.query(q), (Integer) money.getPrecision());
        }
    }

    /**
     * Test method for
     * {@link org.javamoney.moneta.RoundedMoney#getNumber()}.
     */
    @Test
    public void testgetNumberClassOfT() {
        RoundedMoney m = RoundedMoney.of(13.656, "CHF");
        assertEquals(m.getNumber().numberValue(Byte.class), Byte.valueOf((byte) 13));
        assertEquals(m.getNumber().numberValue(Short.class), Short.valueOf((short) 13));
        assertEquals(m.getNumber().numberValue(Integer.class), Integer.valueOf(13));
        assertEquals(m.getNumber().numberValue(Long.class), Long.valueOf(13L));
        assertEquals(m.getNumber().numberValue(Float.class), 13.656f);
        assertEquals(m.getNumber().numberValue(Double.class), 13.656);
        assertEquals(m.getNumber().numberValue(BigDecimal.class).setScale(3, RoundingMode.HALF_EVEN),
                BigDecimal.valueOf(13.656));
        assertEquals(m.getNumber().numberValue(BigDecimal.class), m.getNumber().numberValue(BigDecimal.class));
    }

    /**
     * Test method for {@link org.javamoney.moneta.RoundedMoney#getNumber()#asNumber()}.
     */
    @Test
    public void testGetNumber() {
        assertEquals(BigDecimal.ZERO, RoundedMoney.of(0, "CHF").getNumber().numberValue(BigDecimal.class));
        assertEquals(BigDecimal.valueOf(100034L),
                RoundedMoney.of(100034L, "CHF").getNumber().numberValue(BigDecimal.class));
        assertEquals(new BigDecimal("0.34738746"),
                RoundedMoney.of(new BigDecimal("0.34738746"), "CHF").getNumber().numberValue(BigDecimal.class));
    }

    /**
     * Test method for {@link org.javamoney.moneta.RoundedMoney#toString()}.
     */
    @Test
    public void testToString() {
        assertEquals("XXX 1.23455645", RoundedMoney.of(new BigDecimal("1.23455645"), "XXX").toString());
        assertEquals("CHF 1234", RoundedMoney.of(1234, "CHF").toString());
        assertEquals("CHF 1234", RoundedMoney.of(new BigDecimal("1234.0"), "CHF").toString());
        assertEquals("CHF 1234.1", RoundedMoney.of(new BigDecimal("1234.1"), "CHF").toString());
        assertEquals("CHF 0.01", RoundedMoney.of(new BigDecimal("0.0100"), "CHF").toString());
    }

    // /**
    // * Test method for
    // * {@link org.javamoney.moneta.RoundedMoney#getAmountWhole()}.
    // */
    // @Test
    // public void testGetAmountWhole() {
    // assertEquals(1, RoundedMoney.of("XXX", 1.23455645d).getAmountWhole());
    // assertEquals(1, RoundedMoney.of( 1).getAmountWhole());
    // assertEquals(11, RoundedMoney.of( 11.0d).getAmountWhole());
    // assertEquals(1234, RoundedMoney.of( 1234.1d).getAmountWhole());
    // assertEquals(0, RoundedMoney.of( 0.0100d).getAmountWhole());
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
    // assertEquals(0, RoundedMoney.of( 1).getAmountFractionNumerator());
    // assertEquals(0, RoundedMoney.of( new BigDecimal("11.0"))
    // .getAmountFractionNumerator());
    // assertEquals(10L, RoundedMoney.of( new BigDecimal("1234.1"))
    // .getAmountFractionNumerator());
    // assertEquals(1L, RoundedMoney.of( new BigDecimal("0.0100"))
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
    // assertEquals(100, RoundedMoney.of( 1)
    // .getAmountFractionDenominator());
    // assertEquals(100, RoundedMoney.of( new BigDecimal("11.0"))
    // .getAmountFractionDenominator());
    // assertEquals(100L, RoundedMoney.of( new BigDecimal("1234.1"))
    // .getAmountFractionDenominator());
    // assertEquals(100L, RoundedMoney.of( new BigDecimal("0.0100"))
    // .getAmountFractionDenominator());
    // }

    /**
     * Test method for
     * {@link org.javamoney.moneta.RoundedMoney#with(javax.money.MonetaryOperator)}
     * .
     */
    @Test
    public void testWithMonetaryOperator() {
        MonetaryOperator adj = amount -> amount.getFactory().setCurrency(amount.getCurrency()).setNumber(-100).create();
        RoundedMoney m = RoundedMoney.of(new BigDecimal("1.23645"), "USD");
        RoundedMoney a = m.with(adj);
        assertNotNull(a);
        assertNotSame(m, a);
        assertEquals(m.getCurrency(), a.getCurrency());
        assertEquals(RoundedMoney.of(-100, m.getCurrency()), a);
        adj = amount -> amount.multiply(2).getFactory().setCurrency(MonetaryCurrencies.getCurrency("CHF")).create();
        a = m.with(adj);
        assertNotNull(a);
        assertNotSame(m, a);
        assertEquals(MonetaryCurrencies.getCurrency("CHF"), a.getCurrency());
        assertEquals(RoundedMoney.of(new BigDecimal("2.47"), a.getCurrency()), a);
    }

    /**
     * Test method for
     * {@link org.javamoney.moneta.RoundedMoney#from(javax.money.MonetaryAmount)}
     * .
     */
    @Test
    public void testFrom() {
        RoundedMoney m = RoundedMoney.of(new BigDecimal("1.2345"), "XXX");
        RoundedMoney m2 = RoundedMoney.from(m);
        assertTrue(m == m2);
        FastMoney fm = FastMoney.of(new BigDecimal("1.2345"), "XXX");
        m2 = RoundedMoney.from(fm);
        assertFalse(m == m2);
        assertEquals(m, m2);
    }

    @Test
    public void parseTest() {
        RoundedMoney money = RoundedMoney.parse("EUR 25.25");
        assertEquals(money.getCurrency(), EURO);
        assertEquals(money.getNumber().doubleValue(), 25.25);
    }

    @Test
    public void testSerialization() throws IOException, ClassNotFoundException {
        RoundedMoney m = RoundedMoney.of(new BigDecimal("1.2345"), "XXX");
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(bos);
        oos.writeObject(m);
        oos.flush();
        ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(bos.toByteArray()));
        RoundedMoney m2 = (RoundedMoney) ois.readObject();
        assertEquals(m, m2);
        assertTrue(m != m2);
    }

    // Bad Cases

    /**
     * Test method for
     * {@link org.javamoney.moneta.RoundedMoney#add(javax.money.MonetaryAmount)}
     * .
     */
    @Test(expectedExceptions = MonetaryException.class)
    public void testAdd_WrongCurrency() {
        RoundedMoney m1 = RoundedMoney.of(BigDecimal.TEN, EURO);
        RoundedMoney m2 = RoundedMoney.of(BigDecimal.TEN, "CHF");
        m1.add(m2);
    }

    /**
     * Test method for
     * {@link org.javamoney.moneta.RoundedMoney#add(javax.money.MonetaryAmount)}
     * .
     */
    @Test(expectedExceptions = MonetaryException.class)
    public void testSubtract_WrongCurrency() {
        RoundedMoney m1 = RoundedMoney.of(BigDecimal.TEN, EURO);
        RoundedMoney m2 = RoundedMoney.of(BigDecimal.TEN, "CHF");
        m1.subtract(m2);
    }

    /**
     * Test method for
     * {@link org.javamoney.moneta.RoundedMoney#add(javax.money.MonetaryAmount)}
     * .
     */
    @Test(expectedExceptions = MonetaryException.class)
    public void testDivide_WrongCurrency() {
        RoundedMoney m1 = RoundedMoney.of(BigDecimal.TEN, EURO);
        RoundedMoney m2 = RoundedMoney.of(BigDecimal.TEN, "CHF");
        m1.subtract(m2);
    }

    @Test(expectedExceptions = ArithmeticException.class)
    public void testCreatingFromDoubleNan(){
    	RoundedMoney.of(Double.NaN, "XXX");
    }

    @Test(expectedExceptions = ArithmeticException.class)
    public void testCreatingFromDoublePositiveInfinity(){
    	RoundedMoney.of(Double.POSITIVE_INFINITY, "XXX");
    }

    @Test(expectedExceptions = ArithmeticException.class)
    public void testCreatingFromDoubleNegativeInfinity(){
    	RoundedMoney.of(Double.NEGATIVE_INFINITY, "XXX");
    }

}
