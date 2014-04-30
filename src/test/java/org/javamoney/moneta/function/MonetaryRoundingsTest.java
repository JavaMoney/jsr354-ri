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
package org.javamoney.moneta.function;

import org.junit.Test;

import javax.money.*;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Currency;

import static org.junit.Assert.*;

/**
 * Test for the {@link MonetaryRoundings} singleton.
 *
 * @author Anatole Tresch
 */
public class MonetaryRoundingsTest{

    /**
     * Test method for {@link javax.money.MonetaryRoundings#getRounding()}.
     */
    @Test
    public void testGetRounding(){
        MonetaryOperator rounding = MonetaryRoundings.getRounding();
        assertNotNull(rounding);
        MonetaryAmount m =
                MonetaryAmounts.getAmountFactory().setCurrency("CHF").setNumber(new BigDecimal("10.123456")).create();
        MonetaryAmount r = m.with(rounding);
        assertEquals(MonetaryAmounts.getAmountFactory().setCurrency("CHF").setNumber(new BigDecimal("10.12")).create(),
                     r);
    }

    /**
     * Test method for
     * {@link javax.money.MonetaryRoundings#getRounding(javax.money.RoundingContext)} for arithmetic rounding.
     * .
     */
    @Test
    public void testGetRoundingIntRoundingMode(){
        MonetaryAmount[] samples = new MonetaryAmount[]{
                MonetaryAmounts.getAmountFactory().setCurrency("CHF").setNumber(new BigDecimal("0.0000000001"))
                        .create(),
                MonetaryAmounts.getAmountFactory().setCurrency("CHF").setNumber(new BigDecimal("1.00000000000023"))
                        .create(),
                MonetaryAmounts.getAmountFactory().setCurrency("CHF").setNumber(new BigDecimal("1.1123442323"))
                        .create(),
                MonetaryAmounts.getAmountFactory().setCurrency("CHF").setNumber(new BigDecimal("1.50000000000"))
                        .create(),
                MonetaryAmounts.getAmountFactory().setCurrency("CHF").setNumber(new BigDecimal("-1.000000003"))
                        .create(),
                MonetaryAmounts.getAmountFactory().setCurrency("CHF").setNumber(new BigDecimal("-1.100232876532876389"))
                        .create(),
                MonetaryAmounts.getAmountFactory().setCurrency("CHF").setNumber(new BigDecimal("-1.500000000000"))
                        .create()};
        int[] scales = new int[]{0, 1, 2, 3, 4, 5};
        for(int i = 0; i < samples.length; i++){
            for(int scale : scales){
                for(RoundingMode roundingMode : RoundingMode.values()){
                    if(roundingMode == RoundingMode.UNNECESSARY){
                        continue;
                    }
                    MonetaryOperator rounding = MonetaryRoundings.getRounding(
                            new RoundingContext.Builder().setInt("scale", scale).setObject(roundingMode)
                                    .create()
                    );
                    BigDecimal dec = samples[i].getNumber().numberValue(BigDecimal.class);
                    BigDecimal expected = dec.setScale(scale, roundingMode);
                    MonetaryAmount r = samples[i].with(rounding);
                    assertEquals(MonetaryAmounts.getAmountFactory().setCurrency("CHF").setNumber(expected).create(), r);
                }
            }
        }
    }

    /**
     * Test method for
     * {@link javax.money.MonetaryRoundings#getRounding(javax.money.CurrencyUnit)}
     * .
     */
    @Test
    public void testGetRoundingCurrencyUnit(){
        MonetaryAmount[] samples = new MonetaryAmount[]{
                MonetaryAmounts.getAmountFactory().setCurrency("CHF").setNumber(new BigDecimal("0.0000000001"))
                        .create(),
                MonetaryAmounts.getAmountFactory().setCurrency("CHF").setNumber(new BigDecimal("1.00000000000023"))
                        .create(),
                MonetaryAmounts.getAmountFactory().setCurrency("CHF").setNumber(new BigDecimal("1.1123442323"))
                        .create(),
                MonetaryAmounts.getAmountFactory().setCurrency("CHF").setNumber(new BigDecimal("1.50000000000"))
                        .create(),
                MonetaryAmounts.getAmountFactory().setCurrency("CHF").setNumber(new BigDecimal("-1.000000003"))
                        .create(),
                MonetaryAmounts.getAmountFactory().setCurrency("CHF").setNumber(new BigDecimal("-1.100232876532876389"))
                        .create(),
                MonetaryAmounts.getAmountFactory().setCurrency("CHF").setNumber(new BigDecimal("-1.500000000000"))
                        .create()};
        for(int i = 0; i < samples.length; i++){
            for(Currency currency : Currency.getAvailableCurrencies()){
                CurrencyUnit cur = MonetaryCurrencies.getCurrency(currency.getCurrencyCode());
                // Omit test roundings, which are for testing only...
                if("XXX".equals(cur.getCurrencyCode())){
                    continue;
                }else if("CHF".equals(cur.getCurrencyCode())){
                    continue;
                }
                MonetaryOperator rounding = MonetaryRoundings.getRounding(cur);
                BigDecimal dec = samples[i].getNumber().numberValue(BigDecimal.class);
                BigDecimal expected = null;
                if(cur.getDefaultFractionDigits() < 0){
                    expected = dec.setScale(0, RoundingMode.HALF_UP);
                }else{
                    expected = dec.setScale(cur.getDefaultFractionDigits(), RoundingMode.HALF_UP);
                }
                MonetaryAmount r = samples[i].with(rounding);
                assertEquals("Rouding for: " + samples[i],
                             MonetaryAmounts.getAmountFactory().setCurrency("CHF").setNumber(expected).create(), r);
            }
        }
    }

    /**
     * Test method for
     * {@link javax.money.MonetaryRoundings#getRounding(javax.money.CurrencyUnit)} for cash ropundings.
     * .
     */
    @Test
    public void testGetCashRoundingCurrencyUnit(){
        MonetaryOperator r = MonetaryRoundings.getRounding(
                new RoundingContext.Builder().setCurrencyUnit(MonetaryCurrencies.getCurrency("GBP"))
                        .setAttribute("cashRounding", true).create()
        );
        assertNotNull(r);
        r = MonetaryRoundings.getRounding(
                new RoundingContext.Builder().setCurrencyUnit(MonetaryCurrencies.getCurrency("CHF"))
                        .setAttribute("cashRounding", true).create()
        );
        assertNotNull(r);
        assertEquals(MonetaryAmounts.getAmountFactory().setCurrency("CHF").setNumber(2).create(),
                     MonetaryAmounts.getAmountFactory().setCurrency("CHF").setNumber(2.02).create().with(r));
        assertEquals(MonetaryAmounts.getAmountFactory().setCurrency("CHF").setNumber(2.05).create(),
                     MonetaryAmounts.getAmountFactory().setCurrency("CHF").setNumber(2.025).create().with(r));
    }

    /**
     * Test method for
     * {@link javax.money.MonetaryRoundings#getRounding(javax.money.RoundingContext)} with timestamps.
     * .
     */
    @Test
    public void testGetRoundingCurrencyUnitLong(){
        MonetaryOperator r = MonetaryRoundings.getRounding(
                new RoundingContext.Builder().setCurrencyUnit(MonetaryCurrencies.getCurrency("XXX"))
                        .setAttribute("timestamp", System.currentTimeMillis() + 20000L).create()
        );
        assertNotNull(r);
        assertEquals(MonetaryAmounts.getAmountFactory().setCurrency("XXX").setNumber(-1).create(),
                     MonetaryAmounts.getAmountFactory().setCurrency("XXX").setNumber(2.0234343).create().with(r));
    }

    /**
     * Test method for
     * {@link javax.money.MonetaryRoundings#getRounding(javax.money.RoundingContext)} with cashRounding, timestamps.
     * .
     */
    @Test
    public void testGetCashRoundingCurrencyUnitLong(){
        MonetaryOperator r = MonetaryRoundings.getRounding(
                new RoundingContext.Builder().setCurrencyUnit(MonetaryCurrencies.getCurrency("XXX"))
                        .setAttribute("timestamp", System.currentTimeMillis() + 20000L)
                        .setAttribute("cashRounding", true).create()
        );
        assertNotNull(r);
        assertEquals(MonetaryAmounts.getAmountFactory().setCurrency("CHF").setNumber(-1).create(),
                     MonetaryAmounts.getAmountFactory().setCurrency("CHF").setNumber(2.0234343).create().with(r));
    }

    /**
     * Test method for
     * {@link javax.money.MonetaryRoundings#getRounding(java.lang.String)} .
     */
    @Test
    public void testGetRoundingString(){
        assertNotNull(MonetaryRoundings.getRounding("zero"));
        assertNotNull(MonetaryRoundings.getRounding("minusOne"));
        assertNotNull(MonetaryRoundings.getRounding("CHF-cash"));
        MonetaryOperator minusOne = MonetaryRoundings.getRounding("minusOne");
        assertEquals(MonetaryAmounts.getAmountFactory().setCurrency("CHF").setNumber(-1).create(),
                     MonetaryAmounts.getAmountFactory().setCurrency("CHF").setNumber(213873434.3463843847).create()
                             .with(minusOne)
        );
    }

    /**
     * Test method for
     * {@link javax.money.MonetaryRoundings#getCustomRoundingIds()} .
     */
    @Test
    public void testGetCustomRoundingIds(){
        assertNotNull(MonetaryRoundings.getCustomRoundingIds());
        assertTrue(MonetaryRoundings.getCustomRoundingIds().size() == 3);
        assertTrue(MonetaryRoundings.getCustomRoundingIds().contains("zero"));
        assertTrue(MonetaryRoundings.getCustomRoundingIds().contains("minusOne"));
        assertTrue(MonetaryRoundings.getCustomRoundingIds().contains("CHF-cash"));
    }

    // Bad cases

    /**
     * Test method for
     * {@link javax.money.MonetaryRoundings#getRounding(java.lang.String)} .
     */
    @Test(expected = MonetaryException.class)
    public void testGetRoundingString_Invalid(){
        assertNull(MonetaryRoundings.getRounding("foo"));
    }

    //    /**
    //     * Test method for
    //     * {@link org.javamoney.moneta.function.MonetaryFunctions#reciprocal()}.
    //     */
    //    @Test(expected = NullPointerException.class)
    //    public void testGetCashRounding_Null1(){
    //        MonetaryRoundings.getCashRounding(null);
    //    }
    //
    //    /**
    //     * Test method for
    //     * {@link org.javamoney.moneta.function.MonetaryFunctions#reciprocal()}.
    //     */
    //    @Test(expected = NullPointerException.class)
    //    public void testGetCashRounding_Null2(){
    //
    //        MonetaryRoundings.getCashRounding(MonetaryCurrencies.getCurrency("USD")).apply(null);
    //    }

    /**
     * Test method for
     * {@link org.javamoney.moneta.function.MonetaryFunctions#reciprocal()}.
     */
    @Test(expected = NullPointerException.class)
    public void testGetRounding_Null1(){
        MonetaryRoundings.getRounding((CurrencyUnit) null);
    }

    /**
     * Test method for
     * {@link org.javamoney.moneta.function.MonetaryFunctions#reciprocal()}.
     */
    @Test(expected = NullPointerException.class)
    public void testGetRounding_Null3(){
        MonetaryRoundings.getRounding((String) null);
    }

    /**
     * Test method for
     * {@link org.javamoney.moneta.function.MonetaryFunctions#reciprocal()}.
     */
    @Test(expected = NullPointerException.class)
    public void testGetRounding_Null2(){
        MonetaryRoundings.getRounding(MonetaryCurrencies.getCurrency("USD")).apply(null);
    }

}
