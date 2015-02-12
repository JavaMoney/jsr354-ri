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

import org.testng.annotations.Test;

import javax.money.*;

import java.io.*;
import java.lang.invoke.MethodHandles;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.logging.Level;
import java.util.logging.Logger;

import static org.testng.Assert.*;

/**
 * @author Anatole
 */
public class FastMoneyTest{

    private static final Logger LOG = Logger.getLogger(MethodHandles.lookup().lookupClass().getName());

    private static final BigDecimal TEN = new BigDecimal(10.0d);
    protected static final CurrencyUnit EURO = MonetaryCurrencies.getCurrency("EUR");
    protected static final CurrencyUnit DOLLAR = MonetaryCurrencies.getCurrency("USD");

    /**
     * Test method for
     * {@link org.javamoney.moneta.FastMoney#of(java.lang.Number, javax.money.CurrencyUnit)} .
     */
    @Test
    public void testOfCurrencyUnitBigDecimal(){
        FastMoney m = FastMoney.of(TEN, MonetaryCurrencies.getCurrency("EUR"));
        assertEquals(new BigDecimal("10").intValue(), m.getNumber().numberValue(BigDecimal.class).intValue());
    }

    @Test
    public void testOfCurrencyUnitDouble(){
        FastMoney m = FastMoney.of(10.0d, MonetaryCurrencies.getCurrency("EUR"));
        assertTrue(TEN.doubleValue() == m.getNumber().doubleValue());
    }

    /**
     * Test method for {@link org.javamoney.moneta.FastMoney#getCurrency()}.
     */
    @Test
    public void testGetCurrency(){
        MonetaryAmount money = FastMoney.of(BigDecimal.TEN, EURO);
        assertNotNull(money.getCurrency());
        assertEquals("EUR", money.getCurrency().getCurrencyCode());
    }

    @Test
    public void testSubtractMonetaryAmount(){
        FastMoney money1 = FastMoney.of(BigDecimal.TEN, EURO);
        FastMoney money2 = FastMoney.of(BigDecimal.ONE, EURO);
        FastMoney moneyResult = money1.subtract(money2);
        assertNotNull(moneyResult);
        assertEquals(9d, moneyResult.getNumber().doubleValue(), 0d);
    }

    @Test
    public void testDivideAndRemainder_BigDecimal(){
        FastMoney money1 = FastMoney.of(BigDecimal.ONE, EURO);
        FastMoney[] divideAndRemainder = money1.divideAndRemainder(new BigDecimal("0.50001"));
        assertEquals(divideAndRemainder[0].getNumber().numberValue(BigDecimal.class), new BigDecimal("1"));
        assertEquals(divideAndRemainder[1].getNumber().numberValue(BigDecimal.class), new BigDecimal("0.49999"));
    }

    @Test
    public void testDivideToIntegralValue_BigDecimal(){
        FastMoney money1 = FastMoney.of(BigDecimal.ONE, EURO);
        FastMoney result = money1.divideToIntegralValue(new BigDecimal("0.5001"));
        assertEquals(result.getNumber().numberValue(BigDecimal.class),BigDecimal.ONE);
        result = money1.divideToIntegralValue(new BigDecimal("0.2001"));
        assertEquals(result.getNumber().numberValue(BigDecimal.class),BigDecimal.valueOf(4l));
        result = money1.divideToIntegralValue(BigDecimal.valueOf(5));
        assertTrue(result.getNumber().numberValue(BigDecimal.class).intValueExact() == 0);
    }


    /**
     * Test method for {@link org.javamoney.moneta.FastMoney#hashCode()}.
     */
    @Test
    public void testHashCode(){
        FastMoney money1 = FastMoney.of(BigDecimal.ONE, EURO);
        FastMoney money2 = FastMoney.of(new BigDecimal("1"), EURO);
        assertEquals(money1.hashCode(), money2.hashCode());
        FastMoney money3 = FastMoney.of(1.0, DOLLAR);
        assertTrue(money1.hashCode() != money3.hashCode());
        assertTrue(money2.hashCode() != money3.hashCode());
        FastMoney money4 = FastMoney.of(BigDecimal.ONE, DOLLAR);
        assertTrue(money1.hashCode() != money4.hashCode());
        assertTrue(money2.hashCode() != money4.hashCode());
        FastMoney money5 = FastMoney.of(BigDecimal.ONE, DOLLAR);
        FastMoney money6 = FastMoney.of(1.0, DOLLAR);
        assertTrue(money1.hashCode() != money5.hashCode());
        assertTrue(money2.hashCode() != money5.hashCode());
        assertTrue(money1.hashCode() != money6.hashCode());
        assertTrue(money2.hashCode() != money6.hashCode());
    }

    /**
     * Test method for
     * {@link org.javamoney.moneta.FastMoney#of(java.lang.Number, javax.money.CurrencyUnit)} .
     */
    @Test
    public void testOfCurrencyUnitNumber(){
        FastMoney m = FastMoney.of((byte) 2, EURO);
        assertNotNull(m);
        assertEquals(EURO, m.getCurrency());
        assertEquals(Byte.valueOf((byte) 2), m.getNumber().numberValue(Byte.class));
        m = FastMoney.of((short) -2, DOLLAR);
        assertNotNull(m);
        assertEquals(DOLLAR, m.getCurrency());
        assertEquals(Short.valueOf((short) -2), m.getNumber().numberValue(Short.class));
        m = FastMoney.of(-12, EURO);
        assertNotNull(m);
        assertEquals(EURO, m.getCurrency());
        assertEquals(Integer.valueOf(-12), m.getNumber().numberValue(Integer.class));
        m = FastMoney.of((long) 12, DOLLAR);
        assertNotNull(m);
        assertEquals(DOLLAR, m.getCurrency());
        assertEquals(Long.valueOf(12), m.getNumber().numberValue(Long.class));
        m = FastMoney.of((float) 12.23, EURO);
        assertNotNull(m);
        assertEquals(EURO, m.getCurrency());
        assertEquals((float) 12.23, m.getNumber().numberValue(Float.class));
        m = FastMoney.of(-12.23, DOLLAR);
        assertNotNull(m);
        assertEquals(DOLLAR, m.getCurrency());
        assertEquals(-12.23, m.getNumber().numberValue(Double.class));
        m = FastMoney.of(BigDecimal.valueOf(234.2345), EURO);
        assertNotNull(m);
        assertEquals(EURO, m.getCurrency());
        assertEquals(new BigDecimal("234.2345"), m.getNumber().numberValue(BigDecimal.class));
        m = FastMoney.of(BigInteger.valueOf(232323123L), DOLLAR);
        assertNotNull(m);
        assertEquals(DOLLAR, m.getCurrency());
        assertEquals(Long.valueOf(232323123L), m.getNumber().numberValue(Long.class));
        assertEquals(BigInteger.valueOf(232323123L), m.getNumber().numberValue(BigInteger.class));
    }

    /**
     * Test method for {@link org.javamoney.moneta.FastMoney#of(java.lang.Number, java.lang.String)}
     * .
     */
    @Test
    public void testOfStringNumber(){
        FastMoney m = FastMoney.of((byte) 2, "EUR");
        assertNotNull(m);
        assertEquals(EURO, m.getCurrency());
        assertEquals(Byte.valueOf((byte) 2), m.getNumber().numberValue(Byte.class));
        m = FastMoney.of((short) -2, "USD");
        assertNotNull(m);
        assertEquals(DOLLAR, m.getCurrency());
        assertEquals(Short.valueOf((short) -2), m.getNumber().numberValue(Short.class));
        m = FastMoney.of(-12, "EUR");
        assertNotNull(m);
        assertEquals(EURO, m.getCurrency());
        assertEquals(Integer.valueOf(-12), m.getNumber().numberValue(Integer.class));
        m = FastMoney.of((long) 12, "USD");
        assertNotNull(m);
        assertEquals(DOLLAR, m.getCurrency());
        assertEquals(Long.valueOf(12), m.getNumber().numberValue(Long.class));
        m = FastMoney.of((float) 12.23, "EUR");
        assertNotNull(m);
        assertEquals(EURO, m.getCurrency());
        assertEquals((float) 12.23, m.getNumber().numberValue(Float.class));
        m = FastMoney.of(-12.23, "USD");
        assertNotNull(m);
        assertEquals(DOLLAR, m.getCurrency());
        assertEquals(-12.23, m.getNumber().numberValue(Double.class));
        m = FastMoney.of(BigDecimal.valueOf(234.2345), "EUR");
        assertNotNull(m);
        assertEquals(EURO, m.getCurrency());
        assertEquals(new BigDecimal("234.2345"), m.getNumber().numberValue(BigDecimal.class));
        m = FastMoney.of(BigInteger.valueOf(21432432L), "USD");
        assertNotNull(m);
        assertEquals(DOLLAR, m.getCurrency());
        assertEquals(Long.valueOf(21432432L), m.getNumber().numberValue(Long.class));
        assertEquals(BigInteger.valueOf(21432432L), m.getNumber().numberValue(BigInteger.class));
    }

    /**
     * Test method for {@link org.javamoney.moneta.FastMoney#equals(java.lang.Object)}.
     */
    @Test
    public void testEqualsObject(){
        FastMoney[] moneys = new FastMoney[]{FastMoney.of(BigDecimal.ZERO, "CHF"), FastMoney.of(BigDecimal.ONE, "CHF"),
                FastMoney.of(BigDecimal.ONE, "XXX"), FastMoney.of(BigDecimal.ONE.negate(), "XXX")};
        for(int i = 0; i < moneys.length; i++){
            for(int j = 0; j < moneys.length; j++){
                if(i == j){
                    assertEquals(moneys[i], moneys[j]);
                }else{
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
    public void testCompareTo(){
        FastMoney m1 = FastMoney.of(-2, "CHF");
        FastMoney m2 = FastMoney.of(0, "CHF");
        FastMoney m3 = FastMoney.of(-0, "CHF");
        FastMoney m4 = FastMoney.of(2, "CHF");
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
    public void testAbs(){
        FastMoney m = FastMoney.of(10, "CHF");
        assertEquals(m, m.abs());
        assertTrue(m == m.abs());
        m = FastMoney.of(0, "CHF");
        assertEquals(m, m.abs());
        assertTrue(m == m.abs());
        m = FastMoney.of(-10, "CHF");
        assertEquals(m.negate(), m.abs());
        assertTrue(m != m.abs());
        
        // Long.MIN_VALUE * -1 == Long.MIN_VALUE
        m = FastMoney.of(new BigDecimal(Long.MIN_VALUE).movePointLeft(5), "CHF");
        assertFalse(m.isPositiveOrZero());
        try {
            assertTrue(m.abs().isPositiveOrZero());
        } catch (ArithmeticException e) {
            // should happen
        }
    }

    /**
     * Test method for {@link org.javamoney.moneta.FastMoney#add(javax.money.MonetaryAmount)} .
     */
    @Test
    public void testAdd(){
        FastMoney money1 = FastMoney.of(BigDecimal.TEN, EURO);
        FastMoney money2 = FastMoney.of(BigDecimal.ONE, EURO);
        FastMoney moneyResult = money1.add(money2);
        assertNotNull(moneyResult);
        assertEquals(11d, moneyResult.getNumber().doubleValue(), 0d);
        
        FastMoney money3 = FastMoney.of(90000000000000L, "CHF");
        try {
            // the maximum value for FastMoney is 92233720368547.75807 so this should overflow
            money3.add(money3);
            fail("overflow should raise ArithmeticException");
        } catch (ArithmeticException e) {
            // should happen
        }
    }

    /**
     * Test method for {@link org.javamoney.moneta.FastMoney#divide(java.lang.Number)}.
     */
    @Test(expectedExceptions = java.lang.ArithmeticException.class)
    public void testDivideNumber_Overflow() {
        FastMoney m = FastMoney.of(100, "CHF");
        // the argument exceeds the numeric capabilities but the result will not
        BigDecimal divisor = new BigDecimal("100000000000000000");
        m.divide(divisor);
    }

    /**
     * Test method for {@link org.javamoney.moneta.FastMoney#divide(java.lang.Number)}.
     */
    @Test
    public void testDivideNumber(){
        FastMoney m = FastMoney.of(100, "CHF");
        assertEquals(FastMoney.of(BigDecimal.valueOf(20), "CHF"), m.divide(BigDecimal.valueOf(5)));

        // the maximum value for FastMoney is 92233720368547.75807
        // so this should fit right below this limit
        BigDecimal baseValue = new BigDecimal("90000000000");
        // the argument exceeds the numeric capabilities but the result will not
        BigDecimal divisor = new BigDecimal("1000000");
        BigDecimal expectedValue = baseValue.divide(divisor);

        m = FastMoney.of(baseValue, "CHF");
        assertEquals(FastMoney.of(expectedValue, "CHF"), m.divide(divisor));
    }
    
    /**
     * Test method for {@link org.javamoney.moneta.FastMoney#divide(long)}.
     */
    @Test
    public void testDivideLong(){
        FastMoney m = FastMoney.of(100, "CHF");
        assertEquals(FastMoney.of(BigDecimal.valueOf(20), "CHF"), m.divide(5L));
    }
    
    /**
     * Test method for {@link org.javamoney.moneta.FastMoney#divide(double)}.
     */
    @Test
    public void testDividedouble(){
        FastMoney m = FastMoney.of(100, "CHF");
        assertEquals(FastMoney.of(BigDecimal.valueOf(20), "CHF"), m.divide(5.0d));
    }

    /**
     * Test method for {@link org.javamoney.moneta.FastMoney#divideAndRemainder(java.lang.Number)} .
     */
    @Test
    public void testDivideAndRemainderNumber(){
        FastMoney m = FastMoney.of(100, "CHF");
        assertEquals(FastMoney.of(

                             BigDecimal.valueOf(33), "CHF"), m.divideAndRemainder(BigDecimal.valueOf(3))[0]
        );
        assertEquals(FastMoney.of(

                             BigDecimal.valueOf(1), "CHF"), m.divideAndRemainder(BigDecimal.valueOf(3))[1]
        );
    }

    /**
     * Test method for
     * {@link org.javamoney.moneta.FastMoney#divideToIntegralValue(java.lang.Number)} .
     */
    @Test
    public void testDivideToIntegralValueNumber(){
        FastMoney m = FastMoney.of(100, "CHF");
        assertEquals(FastMoney.of(

                             BigDecimal.valueOf(5), "CHF"), m.divideToIntegralValue(BigDecimal.valueOf(20))
        );
        assertEquals(FastMoney.of(

                             BigDecimal.valueOf(33), "CHF"), m.divideToIntegralValue(BigDecimal.valueOf(3))
        );
    }

    /**
     * Test method for {@link org.javamoney.moneta.FastMoney#multiply(java.lang.Number)}.
     */
    @Test
    public void testMultiplyNumber(){
        FastMoney m = FastMoney.of(100, "CHF");
        assertEquals(FastMoney.of(10, "CHF"), m.multiply(new BigDecimal("0.1")));
        
        // the maximum value for FastMoney is 92233720368547.75807
        // so this should fit right below this limit
        BigDecimal baseValue = new BigDecimal("90000000000000");
        BigDecimal expectedValue = new BigDecimal("90000000000000.00009");
        BigDecimal multiplicant = new BigDecimal("1.000000000000000001");
        
        // verify the expected results
        assertEquals(0, expectedValue.compareTo(baseValue.multiply(multiplicant)));
        
        m = FastMoney.of(baseValue, "CHF");
        
        try {
            m.multiply(baseValue);
            fail("overflow should raise ArithmeticException");
        } catch (ArithmeticException e) {
            // should happen
        }
    }
    
    /**
     * Test method for {@link org.javamoney.moneta.FastMoney#multiply(long)}.
     */
    @Test
    public void testMultiplyLong(){
        FastMoney m = FastMoney.of(100, "CHF");
        assertEquals(FastMoney.of(400, "CHF"), m.multiply(4));
        assertEquals(FastMoney.of(200, "CHF"), m.multiply(2));
        assertEquals(FastMoney.of(new BigDecimal("50.0"), "CHF"), m.multiply(0.5));
        
        try {
            // the maximum value for FastMoney is 92233720368547.75807 so this should overflow
            FastMoney.of(90000000000000L, "CHF").multiply(90000000000000L);
            fail("overflow should raise ArithmeticException");
        } catch (ArithmeticException e) {
            // should happen
        }
    }
    
    /**
     * Test method for {@link org.javamoney.moneta.FastMoney#multiply(double)}.
     */
    @Test
    public void testMultiplyDouble(){
        FastMoney m = FastMoney.of(100, "CHF");
        assertEquals(FastMoney.of(new BigDecimal("50.0"), "CHF"), m.multiply(0.5));
    }

    /**
     * Test method for {@link org.javamoney.moneta.FastMoney#multiply(double)}.
     */
    @Test
    public void testMultiplyDoublePositiveInfinity() {
        FastMoney m = FastMoney.of(new BigDecimal("50.0"), "USD");
        try {
            m.multiply(Double.POSITIVE_INFINITY);
            fail("multiplying with POSITIVE_INFINITY should fail");
        } catch (ArithmeticException e) {
            LOG.log(Level.FINE, "multiplying with POSITIVE_INFINITY fails as expected", e);
        }
    }

    /**
     * Test method for {@link org.javamoney.moneta.FastMoney#multiply(double)}.
     */
    @Test
    public void testMultiplyDoubleNegativeInfinity() {
        FastMoney m = FastMoney.of(new BigDecimal("50.0"), "USD");
        try {
            m.multiply(Double.NEGATIVE_INFINITY);
            fail("multiplying with NEGATIVE_INFINITY should fail");
        } catch (ArithmeticException e) {
            LOG.log(Level.FINE, "multiplying with NEGATIVE_INFINITY fails as expected", e);
        }
    }

    /**
     * Test method for {@link org.javamoney.moneta.FastMoney#multiply(double)}.
     */
    @Test
    public void testMultiplyDoubleNaN() {
        FastMoney m = FastMoney.of(new BigDecimal("50.0"), "USD");
        try {
            m.multiply(Double.NaN);
            fail("multiplying with NaN should fail");
        } catch (ArithmeticException e) {
            LOG.log(Level.FINE, "multiplying with NaN fails as expected", e);
        }
    }

    /**
     * Test method for {@link org.javamoney.moneta.FastMoney#multiply(Number)}.
     */
    @Test
    public void testMultiplyNumberPositiveInfinity() {
        FastMoney m = FastMoney.of(new BigDecimal("50.0"), "USD");
        try {
            m.multiply(Double.valueOf(Double.POSITIVE_INFINITY));
            fail("multiplying with POSITIVE_INFINITY should fail");
        } catch (ArithmeticException e) {
            LOG.log(Level.FINE, "multiplying with POSITIVE_INFINITY fails as expected", e);
        }
    }

    /**
     * Test method for {@link org.javamoney.moneta.FastMoney#multiply(Number)}.
     */
    @Test
    public void testMultiplyNumberNegativeInfinity() {
        FastMoney m = FastMoney.of(new BigDecimal("50.0"), "USD");
        try {
            m.multiply(Double.valueOf(Double.NEGATIVE_INFINITY));
            fail("multiplying with NEGATIVE_INFINITY should fail");
        } catch (ArithmeticException e) {
            LOG.log(Level.FINE, "multiplying with NEGATIVE_INFINITY fails as expected", e);
        }
    }

    /**
     * Test method for {@link org.javamoney.moneta.FastMoney#multiply(Number)}.
     */
    @Test
    public void testMultiplyNumberNaN() {
        FastMoney m = FastMoney.of(new BigDecimal("50.0"), "USD");
        try {
            m.multiply(Double.valueOf(Double.NaN));
            fail("multiplying with NaN should fail");
        } catch (ArithmeticException e) {
            LOG.log(Level.FINE, "multiplying with NaN fails as expected", e);
        }
    }

    /**
     * Test method for {@link org.javamoney.moneta.FastMoney#divide(double)}.
     */
    @Test
    public void testDivideBadNaN() {
        FastMoney m = FastMoney.of(new BigDecimal("50.0"), "USD");
        try {
            m.divide(Double.NaN);
            fail("dividing by NaN should not be allowed");
        } catch (ArithmeticException e) {
            LOG.log(Level.FINE, "dividing by NaN fails as expected", e);
        }
        try {
            m.divide(Double.valueOf(Double.NaN));
            fail("dividing by h NaN should not be allowed");
        } catch (ArithmeticException e) {
            LOG.log(Level.FINE, "dividing by NaN fails as expected", e);
        }
    }

    /**
     * Test method for {@link org.javamoney.moneta.FastMoney#divide(double)}.
     */
    @Test
    public void testDivideInfinityDoubles() {
        double[] values = new double[]{Double.POSITIVE_INFINITY, Double.NEGATIVE_INFINITY};
        FastMoney m = FastMoney.of(new BigDecimal("50.0"), "USD");
        for (double d : values) {
            assertTrue(m.divide(d).isZero());
            assertTrue(m.divide(Double.valueOf(d)).isZero());
        }
    }

    /**
     * Test method for {@link org.javamoney.moneta.FastMoney#negate()}.
     */
    @Test
    public void testNegate(){
        FastMoney m = FastMoney.of(100, "CHF");
        assertEquals(FastMoney.of(-100, "CHF"), m.negate());
        m = FastMoney.of(-123.234, "CHF");
        assertEquals(FastMoney.of(123.234, "CHF"), m.negate());
        
        // Long.MIN_VALUE * -1 == Long.MIN_VALUE
        m = FastMoney.of(new BigDecimal(Long.MIN_VALUE).movePointLeft(5), "CHF");
        assertTrue(m.isNegative());
        try {
            assertFalse(m.negate().isNegative());
        } catch (ArithmeticException e) {
            // should happen
        }
    }

    /**
     * Test method for {@link org.javamoney.moneta.FastMoney#plus()}.
     */
    @Test
    public void testPlus(){
        FastMoney m = FastMoney.of(100, "CHF");
        assertEquals(FastMoney.of(100, "CHF"), m.plus());
        m = FastMoney.of(123.234, "CHF");
        assertEquals(FastMoney.of(123.234, "CHF"), m.plus());
    }

    /**
     * Test method for {@link org.javamoney.moneta.FastMoney#subtract(javax.money.MonetaryAmount)} .
     */
    @Test
    public void testSubtract(){
        FastMoney m = FastMoney.of(100, "CHF");
        FastMoney s1 = FastMoney.of(100, "CHF");
        FastMoney s2 = FastMoney.of(200, "CHF");
        FastMoney s3 = FastMoney.of(0, "CHF");
        assertEquals(FastMoney.of(0, "CHF"), m.subtract(s1));
        assertEquals(FastMoney.of(-100, "CHF"), m.subtract(s2));
        assertEquals(FastMoney.of(100, "CHF"), m.subtract(s3));
        assertTrue(m == m.subtract(s3));
        m = FastMoney.of(-123.234, "CHF");
        assertEquals(FastMoney.of(new BigDecimal("-223.234"), "CHF"), m.subtract(s1));
        assertEquals(FastMoney.of(new BigDecimal("-323.234"), "CHF"), m.subtract(s2));
        assertEquals(FastMoney.of(new BigDecimal("-123.234"), "CHF"), m.subtract(s3));
        assertTrue(m == m.subtract(s3));
        m = FastMoney.of(12.40234, "CHF");
        s1 = FastMoney.of(2343.45, "CHF");
        s2 = FastMoney.of(12.40234, "CHF");
        s3 = FastMoney.of(-2343.45, "CHF");
        assertEquals(FastMoney.of(new BigDecimal("12.40234").subtract(new BigDecimal("2343.45")), "CHF"),
                     m.subtract(s1));
        assertEquals(FastMoney.of(new BigDecimal("12.402345534").subtract(new BigDecimal("12.402345534")), "CHF"),
                     m.subtract(s2));
        assertEquals(FastMoney.of(0, "CHF"), m.subtract(s2));
        assertEquals(FastMoney.of(new BigDecimal("2355.85234"), "CHF"), m.subtract(s3));
        assertTrue(m == m.subtract(FastMoney.of(0, "CHF")));
    }

    /**
     * Test method for {@link org.javamoney.moneta.FastMoney#remainder(java.lang.Number)} .
     */
    @Test
    public void testRemainderNumber(){
        FastMoney[] moneys = new FastMoney[]{FastMoney.of(100, "CHF"), FastMoney.of(34242344, "CHF"),
                FastMoney.of(23123213.435, "CHF"), FastMoney.of(0, "CHF"), FastMoney.of(-100, "CHF"),
                FastMoney.of(-723527.36532, "CHF")};
        for(FastMoney m : moneys){
            assertEquals(m.getFactory().setNumber(
                                 m.getNumber().numberValue(BigDecimal.class).remainder(BigDecimal.valueOf(10.50)))
                                 .create(), m.remainder(10.50), "Invalid remainder of " + 10.50
            );
            assertEquals(m.getFactory().setNumber(
                                 m.getNumber().numberValue(BigDecimal.class).remainder(BigDecimal.valueOf(-30.20)))
                                 .create(), m.remainder(-30.20), "Invalid remainder of " + -30.20
            );
            assertEquals(m.getFactory().setNumber(
                                 m.getNumber().numberValue(BigDecimal.class).remainder(BigDecimal.valueOf(-3)))
                                 .create(), m.remainder(-3),"Invalid remainder of " + -3
            );
            assertEquals(m.getFactory().setNumber(
                                 m.getNumber().numberValue(BigDecimal.class).remainder(BigDecimal.valueOf(3))).create(),
                         m.remainder(3), "Invalid remainder of " + 3
            );
        }
    }

    /**
     * Test method for {@link org.javamoney.moneta.FastMoney#scaleByPowerOfTen(int)} .
     */
    @Test
    public void testScaleByPowerOfTen(){
        FastMoney[] moneys = new FastMoney[]{FastMoney.of(100, "CHF"), FastMoney.of(34242344, "CHF"),
                FastMoney.of(23123213.435, "CHF"), FastMoney.of(0, "CHF"), FastMoney.of(-100, "CHF"),
                FastMoney.of(-723527.36532, "CHF")};
        for(FastMoney m : moneys){
            for(int p = 0; p < 3; p++){
                assertEquals(m.getFactory().setNumber(m.getNumber().numberValue(BigDecimal.class).scaleByPowerOfTen(p))
                                     .create(), m.scaleByPowerOfTen(p), "Invalid scaleByPowerOfTen."
                );
            }
        }
        moneys = new FastMoney[]{FastMoney.of(100, "CHF"), FastMoney.of(34242344, "CHF"),
                FastMoney.of(23123213.435, "CHF"), FastMoney.of(0, "CHF"), FastMoney.of(-100, "CHF"),
                FastMoney.of(-723527.32, "CHF")};
        for(FastMoney m : moneys){
            for(int p = -2; p < 0; p++){
                assertEquals(m.getFactory().setNumber(m.getNumber().numberValue(BigDecimal.class).scaleByPowerOfTen(p))
                                     .create(), m.scaleByPowerOfTen(p), "Invalid scaleByPowerOfTen."
                );
            }
        }
    }

    /**
     * Test method for {@link org.javamoney.moneta.FastMoney#isZero()}.
     */
    @Test
    public void testIsZero(){
        FastMoney[] moneys = new FastMoney[]{FastMoney.of(100, "CHF"), FastMoney.of(34242344, "CHF"),
                FastMoney.of(23123213.435, "CHF"), FastMoney.of(-100, "CHF"), FastMoney.of(-723527.36532, "CHF")};
        for(FastMoney m : moneys){
            assertFalse(m.isZero());
        }
        moneys = new FastMoney[]{FastMoney.of(0, "CHF"), FastMoney.of(0.0, "CHF"), FastMoney.of(BigDecimal.ZERO, "CHF"),
                FastMoney.of(new BigDecimal("0.00000000000000000"), "CHF")};
        for(FastMoney m : moneys){
            assertTrue(m.isZero());
        }
    }

    /**
     * Test method for {@link org.javamoney.moneta.FastMoney#isPositive()}.
     */
    @Test
    public void testIsPositive(){
        FastMoney[] moneys = new FastMoney[]{FastMoney.of(100, "CHF"), FastMoney.of(34242344, "CHF"),
                FastMoney.of(23123213.435, "CHF")};
        for(FastMoney m : moneys){
            assertTrue(m.isPositive());
        }
        moneys = new FastMoney[]{FastMoney.of(0, "CHF"), FastMoney.of(0.0, "CHF"), FastMoney.of(BigDecimal.ZERO, "CHF"),
                FastMoney.of(new BigDecimal("0.00000000000000000"), "CHF"), FastMoney.of(-100, "CHF"),
                FastMoney.of(-34242344, "CHF"), FastMoney.of(-23123213.435, "CHF")};
        for(FastMoney m : moneys){
            assertFalse(m.isPositive());
        }
    }

    /**
     * Test method for {@link org.javamoney.moneta.FastMoney#isPositiveOrZero()} .
     */
    @Test
    public void testIsPositiveOrZero(){
        FastMoney[] moneys =
                new FastMoney[]{FastMoney.of(0, "CHF"), FastMoney.of(0.0, "CHF"), FastMoney.of(BigDecimal.ZERO, "CHF"),
                        FastMoney.of(new BigDecimal("0.00000000000000000"), "CHF"), FastMoney.of(100, "CHF"),
                        FastMoney.of(34242344, "CHF"), FastMoney.of(23123213.435, "CHF")};
        for(FastMoney m : moneys){
            assertTrue(m.isPositiveOrZero(), "Invalid positiveOrZero (expected true): " + m);
        }
        moneys = new FastMoney[]{FastMoney.of(-100, "CHF"), FastMoney.of(-34242344, "CHF"),
                FastMoney.of(-23123213.435, "CHF")};
        for(FastMoney m : moneys){
            assertFalse(m.isPositiveOrZero(), "Invalid positiveOrZero (expected false): " + m);
        }
    }

    /**
     * Test method for {@link org.javamoney.moneta.FastMoney#isNegative()}.
     */
    @Test
    public void testIsNegative(){
        FastMoney[] moneys =
                new FastMoney[]{FastMoney.of(0, "CHF"), FastMoney.of(0.0, "CHF"), FastMoney.of(BigDecimal.ZERO, "CHF"),
                        FastMoney.of(new BigDecimal("0.00000000000000000"), "CHF"), FastMoney.of(100, "CHF"),
                        FastMoney.of(34242344, "CHF"), FastMoney.of(23123213.435, "CHF")};
        for(FastMoney m : moneys){
            assertFalse(m.isNegative(), "Invalid isNegative (expected false): " + m);
        }
        moneys = new FastMoney[]{FastMoney.of(-100, "CHF"), FastMoney.of(-34242344, "CHF"),
                FastMoney.of(-23123213.435, "CHF")};
        for(FastMoney m : moneys){
            assertTrue(m.isNegative(), "Invalid isNegative (expected true): " + m);
        }
    }

    /**
     * Test method for {@link org.javamoney.moneta.FastMoney#isNegativeOrZero()} .
     */
    @Test
    public void testIsNegativeOrZero(){
        FastMoney[] moneys = new FastMoney[]{FastMoney.of(100, "CHF"), FastMoney.of(34242344, "CHF"),
                FastMoney.of(23123213.435, "CHF")};
        for(FastMoney m : moneys){
            assertFalse(m.isNegativeOrZero(), "Invalid negativeOrZero (expected false): " + m);
        }
        moneys = new FastMoney[]{FastMoney.of(0, "CHF"), FastMoney.of(0.0, "CHF"), FastMoney.of(BigDecimal.ZERO, "CHF"),
                FastMoney.of(new BigDecimal("0.00000000000000000"), "CHF"), FastMoney.of(-100, "CHF"),
                FastMoney.of(-34242344, "CHF"), FastMoney.of(-23123213.435, "CHF")};
        for(FastMoney m : moneys){
            assertTrue(m.isNegativeOrZero(), "Invalid negativeOrZero (expected true): " + m);
        }
    }

    /**
     * Test method for {@link FastMoney#getFactory()#setNumber(java.lang.Number)} .
     */
    @Test
    public void testWithNumber(){
        FastMoney[] moneys = new FastMoney[]{FastMoney.of(100, "CHF"), FastMoney.of(34242344, "CHF"),
                FastMoney.of(new BigDecimal("23123213.435"), "CHF"),
                FastMoney.of(new BigDecimal("-23123213.435"), "CHF"), FastMoney.of(-23123213, "CHF"),
                FastMoney.of(0, "CHF")};
        FastMoney s = FastMoney.of(10, "CHF");
        MonetaryAmount[] moneys2 = new MonetaryAmount[]{s.getFactory().setNumber(100).create(),
                s.getFactory().setNumber(34242344).create(),
                s.getFactory().setNumber(new BigDecimal("23123213.435")).create(),
                s.getFactory().setNumber(new BigDecimal("-23123213.435")).create(),
                s.getFactory().setNumber(-23123213).create(), s.getFactory().setNumber(0).create()};
        for(int i = 0; i < moneys.length; i++){
            assertEquals(moneys[i], moneys2[i], "with(Number) failed.");
        }
    }

    /**
     * Test method for
     * {@link org.javamoney.moneta.FastMoney#getFactory()#setCurrency(javax.money.CurrencyUnit)} and {@link org
     * .javamoney.moneta.FastMoney#getFactory()#setNumber(java.lang.Number)}  .
     */
    @Test
    public void testWithCurrencyUnitNumber(){
        FastMoney[] moneys = new FastMoney[]{FastMoney.of(100, "CHF"), FastMoney.of(34242344, "USD"),
                FastMoney.of(23123213.435, "EUR"), FastMoney.of(-23123213.435, "USS"), FastMoney.of(-23123213, "USN"),
                FastMoney.of(0, "GBP")};
        FastMoney s = FastMoney.of(10, "XXX");
        MonetaryAmount[] moneys2 = new MonetaryAmount[]{
                s.getFactory().setCurrency(MonetaryCurrencies.getCurrency("CHF")).setNumber(100).create(),
                s.getFactory().setCurrency(MonetaryCurrencies.getCurrency("USD")).setNumber(34242344).create(),
                s.getFactory().setCurrency(MonetaryCurrencies.getCurrency("EUR"))
                        .setNumber(new BigDecimal("23123213.435")).create(),
                s.getFactory().setCurrency(MonetaryCurrencies.getCurrency("USS"))
                        .setNumber(new BigDecimal("-23123213.435")).create(),
                s.getFactory().setCurrency(MonetaryCurrencies.getCurrency("USN")).setNumber(-23123213).create(),
                s.getFactory().setCurrency(MonetaryCurrencies.getCurrency("GBP")).setNumber(0).create()};
        for(int i = 0; i < moneys.length; i++){
            assertEquals(moneys[i], moneys2[i], "with(Number) failed.");
        }
    }

    /**
     * Test method for {@link org.javamoney.moneta.FastMoney#getScale()}.
     */
    @Test
    public void testGetScale(){
        FastMoney[] moneys = new FastMoney[]{FastMoney.of(100, "CHF"), FastMoney.of(34242344, "USD"),
                FastMoney.of(23123213.435, "EUR"), FastMoney.of(-23123213.435, "USS"), FastMoney.of(-23123213, "USN"),
                FastMoney.of(0, "GBP")};
        for(FastMoney m : moneys){
            assertEquals(5, m.getScale(), "Scale for " + m);
        }
    }

    /**
     * Test method for {@link org.javamoney.moneta.FastMoney#getPrecision()}.
     */
    @Test
    public void testGetPrecision(){
        FastMoney[] moneys = new FastMoney[]{FastMoney.of(111, "CHF"), FastMoney.of(34242344, "USD"),
                FastMoney.of(23123213.435, "EUR"), FastMoney.of(-23123213.435, "USS"), FastMoney.of(-23123213, "USN"),
                FastMoney.of(0, "GBP")};
        for(FastMoney m : moneys){
            assertEquals(m.getNumber().numberValue(BigDecimal.class).precision(), m
                        .getPrecision(), "Precision for " + m);
        }
    }

    /**
     * Test method for {@link org.javamoney.moneta.FastMoney#getNumber()#longValue()}.
     */
    @Test(expectedExceptions = ArithmeticException.class)
    public void testLongValue(){
        FastMoney m = FastMoney.of(100, "CHF");
        assertEquals(100L, m.getNumber().longValue(), "longValue of " + m);
        m = FastMoney.of(-100, "CHF");
        assertEquals(-100L, m.getNumber().longValue(), "longValue of " + m);
        m = FastMoney.of(-100.3434, "CHF");
        assertEquals(-100L, m.getNumber().longValue(), "longValue of " + m);
        m = FastMoney.of(100.3434, "CHF");
        assertEquals(100L, m.getNumber().longValue(), "longValue of " + m);
        m = FastMoney.of(0, "CHF");
        assertEquals(0L, m.getNumber().longValue(), "longValue of " + m);
        m = FastMoney.of(-0.0, "CHF");
        assertEquals(0L, m.getNumber().longValue(), "longValue of " + m);
		m = FastMoney
				.of(new BigDecimal(
						"12121762517652176251725178251872652765321876352187635217835378125"),
						"CHF");
		fail("longValue(12121762517652176251725178251872652765321876352187635217835378125) should fail!");
    }

    /**
     * Test method for {@link org.javamoney.moneta.FastMoney#getNumber()#longValueExact()}.
     */
    @Test
    public void testLongValueExact(){
        FastMoney m = FastMoney.of(100, "CHF");
        assertEquals(100L, m.getNumber().longValueExact(), "longValue of " + m);
        m = FastMoney.of(-100, "CHF");
        assertEquals(-100L, m.getNumber().longValueExact(), "longValue of " + m);
        m = FastMoney.of(0, "CHF");
        assertEquals(0L, m.getNumber().longValueExact(), "longValue of " + m);
        m = FastMoney.of(-0.0, "CHF");
        assertEquals(0L, m.getNumber().longValueExact(), "longValue of " + m);
        try{
            m = FastMoney.of(Long.MAX_VALUE, "CHF");
            fail("longValueExact(12121762517652176251725178251872652765321876352187635217835378125) should fail!");
        }
        catch(ArithmeticException e){
            // OK
        }
        try{
            m = FastMoney.of(Long.MIN_VALUE, "CHF");
            fail("longValueExact(-100.3434) should raise an ArithmeticException.");
        }
        catch(ArithmeticException e){
            // OK
        }
        try{
            m = FastMoney.of(100.3434, "CHF");
            m.getNumber().longValueExact();
            fail("longValueExact(100.3434) should raise an ArithmeticException.");
        }
        catch(ArithmeticException e){
            // OK
        }
    }

    /**
     * Test method for {@link org.javamoney.moneta.FastMoney#getNumber()#doubleValue()}.
     */
    @Test(expectedExceptions = ArithmeticException.class)
    public void testDoubleValue(){
        FastMoney m = FastMoney.of(100, "CHF");
        assertEquals(100d, m.getNumber().doubleValue(), 0.0d, "doubleValue of " + m);
        m = FastMoney.of(-100, "CHF");
        assertEquals(-100d, m.getNumber().doubleValue(), 0.0d, "doubleValue of " + m);
        m = FastMoney.of(-100.3434, "CHF");
        assertEquals(-100.3434, m.getNumber().doubleValue(), 0.0d, "doubleValue of " + m);
        m = FastMoney.of(100.3434, "CHF");
        assertEquals(100.3434, m.getNumber().doubleValue(), 0.0d, "doubleValue of " + m);
        m = FastMoney.of(0, "CHF");
        assertEquals(0d, m.getNumber().doubleValue(), 0.0d, "doubleValue of " + m);
        m = FastMoney.of(-0.0, "CHF");
        assertEquals(0d, m.getNumber().doubleValue(), 0.0d, "doubleValue of " + m);
		m = FastMoney
				.of(new BigDecimal(
						"12121762517652176251725178251872652765321876352187635217835378125"),
						"CHF");
		m.getNumber().doubleValue();
		fail("doubleValue(12121762517652176251725178251872652765321876352187635217835378125) should fail!");
    }

    /**
     * Test method for {@link org.javamoney.moneta.FastMoney#signum()}.
     */
    @Test
    public void testSignum(){
        FastMoney m = FastMoney.of(100, "CHF");
        assertEquals(1, m.signum(), "signum of " + m);
        m = FastMoney.of(-100, "CHF");
        assertEquals(-1, m.signum(), "signum of " + m);
        m = FastMoney.of(100.3435, "CHF");
        assertEquals(1, m.signum(), "signum of " + m);
        m = FastMoney.of(-100.3435, "CHF");
        assertEquals(-1, m.signum(), "signum of " + m);
        m = FastMoney.of(0, "CHF");
        assertEquals(0, m.signum(), "signum of " + m);
        m = FastMoney.of(-0, "CHF");
        assertEquals(0, m.signum(), "signum of " + m);
    }


    /**
     * Test method for {@link org.javamoney.moneta.FastMoney#isLessThan(javax.money.MonetaryAmount)}
     * .
     */
    @Test
    public void testIsLessThan(){
        assertFalse(FastMoney.of(BigDecimal.valueOf(0d), "CHF").isLessThan(FastMoney.of(BigDecimal.valueOf(0), "CHF")));
        assertFalse(FastMoney.of(BigDecimal.valueOf(0.00001d), "CHF")
                            .isLessThan(FastMoney.of(BigDecimal.valueOf(0d), "CHF")));
        assertFalse(FastMoney.of(15, "CHF").isLessThan(FastMoney.of(10, "CHF")));
        assertFalse(FastMoney.of(15.546, "CHF").isLessThan(FastMoney.of(10.34, "CHF")));
        assertTrue(FastMoney.of(5, "CHF").isLessThan(FastMoney.of(10, "CHF")));
        assertTrue(FastMoney.of(5.546, "CHF").isLessThan(FastMoney.of(10.34, "CHF")));
    }

    /**
     * Test method for
     * {@link org.javamoney.moneta.FastMoney#isLessThanOrEqualTo(javax.money.MonetaryAmount)} .
     */
    @Test
    public void testIsLessThanOrEqualTo(){
        assertTrue(FastMoney.of(BigDecimal.valueOf(0d), "CHF")
                           .isLessThanOrEqualTo(FastMoney.of(BigDecimal.valueOf(0), "CHF")));
        assertFalse(FastMoney.of(BigDecimal.valueOf(0.00001d), "CHF")
                            .isLessThanOrEqualTo(FastMoney.of(BigDecimal.valueOf(0d), "CHF")));
        assertFalse(FastMoney.of(15, "CHF").isLessThanOrEqualTo(FastMoney.of(10, "CHF")));
        assertFalse(FastMoney.of(15.546, "CHF").isLessThan(FastMoney.of(10.34, "CHF")));
        assertTrue(FastMoney.of(5, "CHF").isLessThanOrEqualTo(FastMoney.of(10, "CHF")));
        assertTrue(FastMoney.of(5.546, "CHF").isLessThanOrEqualTo(FastMoney.of(10.34, "CHF")));
    }

    /**
     * Test method for
     * {@link org.javamoney.moneta.FastMoney#isGreaterThan(javax.money.MonetaryAmount)} .
     */
    @Test
    public void testIsGreaterThan(){
        assertFalse(
                FastMoney.of(BigDecimal.valueOf(0d), "CHF").isGreaterThan(FastMoney.of(BigDecimal.valueOf(0), "CHF")));
        assertTrue(FastMoney.of(BigDecimal.valueOf(0.00001d), "CHF")
                           .isGreaterThan(FastMoney.of(BigDecimal.valueOf(0d), "CHF")));
        assertTrue(FastMoney.of(15, "CHF").isGreaterThan(FastMoney.of(10, "CHF")));
        assertTrue(FastMoney.of(15.546, "CHF").isGreaterThan(FastMoney.of(10.34, "CHF")));
        assertFalse(FastMoney.of(5, "CHF").isGreaterThan(FastMoney.of(10, "CHF")));
        assertFalse(FastMoney.of(5.546, "CHF").isGreaterThan(FastMoney.of(10.34, "CHF")));
    }

    /**
     * Test method for
     * {@link org.javamoney.moneta.FastMoney#isGreaterThanOrEqualTo(javax.money.MonetaryAmount)} .
     */
    @Test
    public void testIsGreaterThanOrEqualTo(){
        assertTrue(FastMoney.of(BigDecimal.valueOf(0d), "CHF")
                           .isGreaterThanOrEqualTo(FastMoney.of(BigDecimal.valueOf(0), "CHF")));
        assertTrue(FastMoney.of(BigDecimal.valueOf(0.00001d), "CHF")
                           .isGreaterThanOrEqualTo(FastMoney.of(BigDecimal.valueOf(0d), "CHF")));
        assertTrue(FastMoney.of(15, "CHF").isGreaterThanOrEqualTo(FastMoney.of(10, "CHF")));
        assertTrue(FastMoney.of(15.546, "CHF").isGreaterThanOrEqualTo(FastMoney.of(10.34, "CHF")));
        assertFalse(FastMoney.of(5, "CHF").isGreaterThanOrEqualTo(FastMoney.of(10, "CHF")));
        assertFalse(FastMoney.of(5.546, "CHF").isGreaterThanOrEqualTo(FastMoney.of(10.34, "CHF")));
    }

    /**
     * Test method for {@link org.javamoney.moneta.FastMoney#isEqualTo(javax.money.MonetaryAmount)}
     * .
     */
    @Test
    public void testIsEqualTo(){
        assertTrue(FastMoney.of(BigDecimal.valueOf(0d), "CHF").isEqualTo(FastMoney.of(BigDecimal.valueOf(0), "CHF")));
        assertFalse(FastMoney.of(BigDecimal.valueOf(0.00001d), "CHF")
                            .isEqualTo(FastMoney.of(BigDecimal.valueOf(0d), "CHF")));
        assertTrue(FastMoney.of(BigDecimal.valueOf(5d), "CHF").isEqualTo(FastMoney.of(BigDecimal.valueOf(5), "CHF")));
        assertTrue(
                FastMoney.of(BigDecimal.valueOf(1d), "CHF").isEqualTo(FastMoney.of(BigDecimal.valueOf(1.00), "CHF")));
        assertTrue(FastMoney.of(BigDecimal.valueOf(1d), "CHF").isEqualTo(FastMoney.of(BigDecimal.ONE, "CHF")));
        assertTrue(FastMoney.of(BigDecimal.valueOf(1), "CHF").isEqualTo(FastMoney.of(BigDecimal.ONE, "CHF")));
        assertTrue(
                FastMoney.of(new BigDecimal("1.0000"), "CHF").isEqualTo(FastMoney.of(new BigDecimal("1.00"), "CHF")));
    }


    /**
     * Test method for {@link org.javamoney.moneta.FastMoney#getNumber()}.
     */
    @Test
    public void testGetImplementationType(){
        assertEquals(FastMoney.of(0, "CHF").getContext().getAmountType(), FastMoney.class);
        assertEquals(FastMoney.of(0.34746d, "CHF").getContext().getAmountType(), FastMoney.class);
        assertEquals(FastMoney.of(100034L, "CHF").getContext().getAmountType(), FastMoney.class);
    }

    /**
     * Test method for {@link org.javamoney.moneta.FastMoney#query(javax.money.MonetaryQuery)}.
     */
    @Test
    public void testQuery(){
        MonetaryQuery<Integer> q = amount -> FastMoney.from(amount).getPrecision();
        FastMoney[] moneys = new FastMoney[]{FastMoney.of(100, "CHF"), FastMoney.of(34242344, "USD"),
                FastMoney.of(23123213.435, "EUR"), FastMoney.of(-23123213.435, "USS"), FastMoney.of(-23123213, "USN"),
                FastMoney.of(0, "GBP")};
        for (FastMoney money : moneys) {
            assertEquals(money.query(q), (Integer) money.getPrecision());
        }
    }

    /**
     * Test method for {@link org.javamoney.moneta.FastMoney#getNumber()#asType(java.lang.Class)}.
     */
    @Test
    public void testGetNumberClassOfT(){
        FastMoney m = FastMoney.of(13.656, "CHF");
        assertEquals(m.getNumber().numberValue(Byte.class), Byte.valueOf((byte) 13));
        assertEquals(m.getNumber().numberValue(Short.class), Short.valueOf((short) 13));
        assertEquals(m.getNumber().numberValue(Integer.class), Integer.valueOf(13));
        assertEquals(m.getNumber().numberValue(Long.class), Long.valueOf(13L));
        assertEquals(m.getNumber().numberValue(Float.class), 13.656f);
        assertEquals(m.getNumber().numberValue(Double.class), 13.656);
        assertEquals(m.getNumber().numberValue(BigDecimal.class), new BigDecimal("13.656"));
    }

    /**
     * Test method for {@link org.javamoney.moneta.FastMoney#getNumber()#asNumber()}.
     */
    @Test
    public void testGetNumber(){
        assertEquals(BigDecimal.ZERO, FastMoney.of(0, "CHF").getNumber().numberValue(BigDecimal.class));
        assertEquals(new BigDecimal("100034"), FastMoney.of(100034L, "CHF").getNumber().numberValue(BigDecimal.class));
        assertEquals(new BigDecimal("0.34738"),
                     FastMoney.of(new BigDecimal("0.34738"), "CHF").getNumber().numberValue(BigDecimal.class));
    }

    /**
     * Test method for {@link org.javamoney.moneta.FastMoney#toString()}.
     */
    @Test
    public void testToString(){
        assertEquals("XXX 1.23455", FastMoney.of(new BigDecimal("1.23455"), "XXX").toString());
        assertEquals("CHF 1234.00000", FastMoney.of(1234, "CHF").toString());
        assertEquals("CHF 1234.00000", FastMoney.of(new BigDecimal("1234.0"), "CHF").toString());
        assertEquals("CHF 1234.10000", FastMoney.of(new BigDecimal("1234.1"), "CHF").toString());
        assertEquals("CHF 0.01000", FastMoney.of(new BigDecimal("0.0100"), "CHF").toString());
    }

    /**
     * Test method for {@link org.javamoney.moneta.FastMoney#with(javax.money.MonetaryOperator)} .
     */
    @Test
    public void testWithMonetaryOperator(){
        MonetaryOperator adj = amount -> FastMoney.of(-100, amount.getCurrency());
        FastMoney m = FastMoney.of(new BigDecimal("1.2345"), "XXX");
        FastMoney a = m.with(adj);
        assertNotNull(a);
        assertNotSame(m, a);
        assertEquals(m.getCurrency(), a.getCurrency());
        assertEquals(FastMoney.of(-100, m.getCurrency()), a);
        adj = amount -> amount.multiply(2).getFactory().setCurrency(MonetaryCurrencies.getCurrency("CHF")).create();
        a = m.with(adj);
        assertNotNull(a);
        assertNotSame(m, a);
        assertEquals(MonetaryCurrencies.getCurrency("CHF"), a.getCurrency());
        assertEquals(FastMoney.of(1.2345 * 2, a.getCurrency()), a);
    }

    /**
     * Test method for {@link org.javamoney.moneta.FastMoney#from(javax.money.MonetaryAmount)}.
     */
    @Test
    public void testFrom(){
        FastMoney m = FastMoney.of(new BigDecimal("1.2345"), "XXX");
        FastMoney m2 = FastMoney.from(m);
        assertTrue(m == m2);
        Money fm = Money.of(new BigDecimal("1.2345"), "XXX");
        m2 = FastMoney.from(fm);
        assertFalse(m == m2);
        assertEquals(m, m2);
    }

    @Test
    public void testSerialization() throws IOException, ClassNotFoundException{
        FastMoney m = FastMoney.of(new BigDecimal("1.2345"), "XXX");
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(bos);
        oos.writeObject(m);
        oos.flush();
        ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(bos.toByteArray()));
        FastMoney m2 = (FastMoney) ois.readObject();
        assertEquals(m, m2);
        assertTrue(m != m2);
    }

	@Test
	public void parseTest() {
		FastMoney money = FastMoney.parse("EUR 25.25");
		assertEquals(money.getCurrency(), EURO);
		assertEquals(money.getNumber().doubleValue(), 25.25);
	}

    /**
     * Test method for {@link org.javamoney.moneta.Money#from(javax.money.MonetaryAmount)}.
     */
    @Test
    public void testFromInversed(){
        Money m = Money.of(new BigDecimal("1.2345"), "XXX");
        Money m2 = Money.from(m);
        assertTrue(m == m2);
        FastMoney fm = FastMoney.of(new BigDecimal("1.2345"), "XXX");
        m2 = Money.from(fm);
        assertFalse(m == m2);
        assertEquals(m, m2);
    }

    @Test(expectedExceptions = ArithmeticException.class)
    public void testCreatingFromDoubleNan(){
        FastMoney.of(Double.NaN, "XXX");
    }

    @Test(expectedExceptions = ArithmeticException.class)
    public void testCreatingFromDoublePositiveInfinity(){
        FastMoney.of(Double.POSITIVE_INFINITY, "XXX");
    }

    @Test(expectedExceptions = ArithmeticException.class)
    public void testCreatingFromDoubleNegativeInfinity(){
        FastMoney.of(Double.NEGATIVE_INFINITY, "XXX");
    }
}
