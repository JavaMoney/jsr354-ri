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

import javax.money.*;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Currency;

import static org.testng.Assert.*;

/**
 * Test for the {@link MonetaryRoundings} singleton.
 *
 * @author Anatole Tresch
 */
public class MonetaryRoundingsTest {

    /**
     * Test method for {@link javax.money.MonetaryRoundings#getDefaultRounding()}.
     */
    @Test
    public void testGetRounding() {
        MonetaryRounding rounding = Monetary.getDefaultRounding();
        assertNotNull(rounding);
        MonetaryAmount m =
                Monetary.getDefaultAmountFactory().setCurrency("CHF").setNumber(new BigDecimal("10.123456"))
                        .create();
        MonetaryAmount r = m.with(rounding);
        assertEquals(Monetary.getDefaultAmountFactory().setCurrency("CHF").setNumber(new BigDecimal("10.12"))
                .create(), r);
    }

    /**
     * Test method for
     * {@link javax.money.MonetaryRoundings#getRounding(javax.money.RoundingQuery)} for arithmetic rounding.
     * .
     */
    @Test
    public void testGetRoundingIntRoundingMode() {
        MonetaryAmount[] samples = new MonetaryAmount[]{
                Monetary.getDefaultAmountFactory().setCurrency("CHF").setNumber(new BigDecimal("0.0000000001"))
                        .create(), Monetary.getDefaultAmountFactory().setCurrency("CHF")
                .setNumber(new BigDecimal("1.00000000000023")).create(),
                Monetary.getDefaultAmountFactory().setCurrency("CHF").setNumber(new BigDecimal("1.1123442323"))
                        .create(),
                Monetary.getDefaultAmountFactory().setCurrency("CHF").setNumber(new BigDecimal("1.50000000000"))
                        .create(),
                Monetary.getDefaultAmountFactory().setCurrency("CHF").setNumber(new BigDecimal("-1.000000003"))
                        .create(), Monetary.getDefaultAmountFactory().setCurrency("CHF")
                .setNumber(new BigDecimal("-1.100232876532876389")).create(),
                Monetary.getDefaultAmountFactory().setCurrency("CHF")
                        .setNumber(new BigDecimal("-1.500000000000")).create()};
        int[] scales = new int[]{0, 1, 2, 3, 4, 5};
        for (MonetaryAmount sample : samples) {
            for (int scale : scales) {
                for (RoundingMode roundingMode : RoundingMode.values()) {
                    if (roundingMode == RoundingMode.UNNECESSARY) {
                        continue;
                    }
                    MonetaryOperator rounding = Monetary
                            .getRounding(RoundingQueryBuilder.of().setScale(scale).set(roundingMode).build());
                    BigDecimal dec = sample.getNumber().numberValue(BigDecimal.class);
                    BigDecimal expected = dec.setScale(scale, roundingMode);
                    MonetaryAmount r = sample.with(rounding);
                    assertEquals(
                            Monetary.getDefaultAmountFactory().setCurrency("CHF").setNumber(expected).create(),
                            r);
                }
            }
        }
    }

    /**
     * Test method for
     * {@link javax.money.Monetary#getRounding(javax.money.CurrencyUnit, String...)}
     * .
     */
    @Test
    public void testGetRoundingCurrencyUnit() {
        MonetaryAmount[] samples = new MonetaryAmount[]{
                Monetary.getDefaultAmountFactory().setCurrency("CHF").setNumber(new BigDecimal("0.0000000001"))
                        .create(), Monetary.getDefaultAmountFactory().setCurrency("CHF")
                .setNumber(new BigDecimal("1.00000000000023")).create(),
                Monetary.getDefaultAmountFactory().setCurrency("CHF").setNumber(new BigDecimal("1.1123442323"))
                        .create(),
                Monetary.getDefaultAmountFactory().setCurrency("CHF").setNumber(new BigDecimal("1.50000000000"))
                        .create(),
                Monetary.getDefaultAmountFactory().setCurrency("CHF").setNumber(new BigDecimal("-1.000000003"))
                        .create(), Monetary.getDefaultAmountFactory().setCurrency("CHF")
                .setNumber(new BigDecimal("-1.100232876532876389")).create(),
                Monetary.getDefaultAmountFactory().setCurrency("CHF")
                        .setNumber(new BigDecimal("-1.500000000000")).create()};
        for (MonetaryAmount sample : samples) {
            for (Currency currency : Currency.getAvailableCurrencies()) {
                CurrencyUnit cur = Monetary.getCurrency(currency.getCurrencyCode());
                // Omit test roundings, which are for testing only...
                if ("XXX".equals(cur.getCurrencyCode())) {
                    continue;
                } else if ("CHF".equals(cur.getCurrencyCode())) {
                    continue;
                }
                MonetaryOperator rounding = Monetary.getRounding(cur);
                BigDecimal dec = sample.getNumber().numberValue(BigDecimal.class);
                BigDecimal expected;
                if (cur.getDefaultFractionDigits() < 0) {
                    expected = dec.setScale(0, RoundingMode.HALF_UP);
                } else {
                    expected = dec.setScale(cur.getDefaultFractionDigits(), RoundingMode.HALF_UP);
                }
                MonetaryAmount r = sample.with(rounding);
                assertEquals(Monetary.getDefaultAmountFactory().setCurrency("CHF").setNumber(expected).create(),
                        r, "Rounding for: " + sample);
            }
        }
    }

    /**
     * Test method for
     * {@link javax.money.Monetary#getRounding(javax.money.CurrencyUnit, String...)} for cash roundings.
     * .
     */
    @Test
    public void testGetCashRoundingCurrencyUnit() {
        MonetaryOperator r = Monetary.getRounding(
                RoundingQueryBuilder.of().setCurrency(Monetary.getCurrency("GBP")).set("cashRounding", true)
                        .build());
        assertNotNull(r);
        r = Monetary.getRounding(
                RoundingQueryBuilder.of().setCurrency(Monetary.getCurrency("CHF")).set("cashRounding", true)
                        .build());
        assertNotNull(r);
        assertEquals(Monetary.getDefaultAmountFactory().setCurrency("CHF").setNumber(2).create(),
                Monetary.getDefaultAmountFactory().setCurrency("CHF").setNumber(2.02).create().with(r));
        assertEquals(Monetary.getDefaultAmountFactory().setCurrency("CHF").setNumber(2.05).create(),
                Monetary.getDefaultAmountFactory().setCurrency("CHF").setNumber(2.025).create().with(r));
    }

    /**
     * Test method for
     * {@link javax.money.Monetary#getRounding(javax.money.RoundingQuery)} with timestamps.
     * .
     */
    @Test
    public void testGetRoundingCurrencyUnitLong() {
        MonetaryOperator r = Monetary.getRounding(
                RoundingQueryBuilder.of().setCurrency(Monetary.getCurrency("XXX"))
                        .set(LocalDate.now().plus(1, ChronoUnit.DAYS)).build());
        assertNotNull(r);
        assertEquals(Monetary.getDefaultAmountFactory().setCurrency("XXX").setNumber(-1).create(),
                Monetary.getDefaultAmountFactory().setCurrency("XXX").setNumber(2.0234343).create()
                        .with(r));
    }

    /**
     * Test method for
     * {@link javax.money.Monetarys#getRounding(javax.money.RoundingQuery)} with cashRounding, timestamps.
     * .
     */
    @Test
    public void testGetCashRoundingCurrencyUnitLong() {
        MonetaryOperator r = Monetary.getRounding(
                RoundingQueryBuilder.of().setCurrency(Monetary.getCurrency("XXX"))
                        .set(LocalDate.now().plus(1, ChronoUnit.DAYS)).set("cashRounding", true).build());
        assertNotNull(r);
        assertEquals(Monetary.getDefaultAmountFactory().setCurrency("CHF").setNumber(-1).create(),
                Monetary.getDefaultAmountFactory().setCurrency("CHF").setNumber(2.0234343).create()
                        .with(r));
    }

    /**
     * Test method for
     * {@link javax.money.Monetary#getRounding(String, String...)}.
     */
    @Test
    public void testGetRoundingString() {
        assertNotNull(Monetary.getRounding("zero"));
        assertNotNull(Monetary.getRounding("minusOne"));
        assertNotNull(Monetary.getRounding("CHF-cash"));
        MonetaryOperator minusOne = Monetary.getRounding("minusOne");
        assertEquals(Monetary.getDefaultAmountFactory().setCurrency("CHF").setNumber(-1).create(),
                Monetary.getDefaultAmountFactory().setCurrency("CHF").setNumber(213873434.3463843847)
                        .create().with(minusOne));
    }

    /**
     * Test method for
     * {@link javax.money.Monetary#getRoundingNames(String...)}  .
     */
    @Test
    public void testGetCustomRoundingNames() {
        assertNotNull(Monetary.getRoundingNames());
        assertTrue(Monetary.getRoundingNames().size() >= 3);
        assertTrue(Monetary.getRoundingNames().contains("zero"));
        assertTrue(Monetary.getRoundingNames().contains("minusOne"));
        assertTrue(Monetary.getRoundingNames().contains("CHF-cash"));
    }

    // Bad cases

    /**
     * Test method for
     * {@link javax.money.Monetary#getRounding(java.lang.String, java.lang.String...)}.
     */
    @Test(expectedExceptions = MonetaryException.class)
    public void testGetRoundingString_Invalid() {
        assertNull(Monetary.getRounding("foo"));
    }

    //    /**
    //     * Test method for
    //     * {@link org.javamoney.moneta.function.MonetaryFunctions#reciprocal()}.
    //     */
    //    @Test(expected = NullPointerException.class)
    //    public void testGetCashRounding_Null1(){
    //        Monetary.getCashRounding(null);
    //    }
    //
    //    /**
    //     * Test method for
    //     * {@link org.javamoney.moneta.function.MonetaryFunctions#reciprocal()}.
    //     */
    //    @Test(expected = NullPointerException.class)
    //    public void testGetCashRounding_Null2(){
    //
    //        Monetary.getCashRounding(Monetary.getCurrency("USD")).apply(null);
    //    }

    /**
     * Test method for
     * {@link org.javamoney.moneta.function.MonetaryOperators#reciprocal()}.
     */
    @Test(expectedExceptions = NullPointerException.class)
    public void testGetRounding_Null1() {
        Monetary.getRounding((CurrencyUnit) null);
    }

    /**
     * Test method for
     * {@link org.javamoney.moneta.function.MonetaryOperators#reciprocal()}.
     */
    @Test(expectedExceptions = NullPointerException.class)
    public void testGetRounding_Null3() {
        Monetary.getRounding((String) null);
    }

    /**
     * Test method for
     * {@link org.javamoney.moneta.function.MonetaryOperators#reciprocal()}.
     */
    @Test(expectedExceptions = NullPointerException.class)
    public void testGetRounding_Null2() {
        Monetary.getRounding(Monetary.getCurrency("USD")).apply(null);
    }

}
