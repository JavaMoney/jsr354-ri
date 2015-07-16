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

import static org.testng.Assert.*;

/**
 * @author Anatole
 */
public class MonetaryAmountsRegistrationTest{

    /**
     * Test method for {@link javax.money.Monetary#getAmountFactory(java.lang.Class)}.
     */
    @Test
    public void testGetFactory(){
        assertNotNull(Monetary.getDefaultAmountFactory());
        assertNotNull(Monetary.getAmountFactory(FastMoney.class));
        assertNotNull(Monetary.getAmountFactory(Money.class));
        assertTrue(Monetary.getDefaultAmountFactory().getClass() ==
                           Monetary.getAmountFactory(Money.class).getClass());
    }

    /**
     * Test method for {@link javax.money.Monetary#getAmountTypes()}.
     */
    @Test
    public void testGetTypes(){
        assertNotNull(Monetary.getAmountTypes());
        assertTrue(Monetary.getAmountTypes().size() == 3);
        assertTrue(Monetary.getAmountTypes().contains(FastMoney.class));
        assertTrue(Monetary.getAmountTypes().contains(Money.class));
        assertTrue(Monetary.getAmountTypes().contains(RoundedMoney.class));
    }

    /**
     * Test method for {@link javax.money.Monetary#getDefaultAmountType()}.
     */
    @Test
    public void testGetDefaultAmountType(){
        assertNotNull(Monetary.getDefaultAmountType());
        assertEquals(Money.class, Monetary.getDefaultAmountType());
    }

    /**
     * Test method for
     * {@link javax.money.Monetary#getAmountFactory(javax.money.MonetaryAmountFactoryQuery)} .
     */
    @Test(expectedExceptions = NullPointerException.class)
    public void testGetAmountFactory_WithNull(){
        Monetary.getAmountFactory((MonetaryAmountFactoryQuery) null);
    }

    /**
     * Test method for
     * {@link javax.money.Monetary#getAmountFactory(javax.money.MonetaryAmountFactoryQuery)}.
     */
    @Test
    public void testQueryAmountType(){
        MonetaryAmountFactory<?> f = Monetary
                .getAmountFactory(MonetaryAmountFactoryQueryBuilder.of().setTargetType(RoundedMoney.class).build());
        assertNotNull(f);
        assertTrue(f.getAmountType() == RoundedMoney.class);
        f = Monetary.getAmountFactory(
                MonetaryAmountFactoryQueryBuilder.of().setTargetType(FastMoney.class).setPrecision(5).build());
        assertNotNull(f);
        assertTrue(f.getAmountType() == FastMoney.class);
        f = Monetary.getAmountFactory(MonetaryAmountFactoryQueryBuilder.of().setTargetType(Money.class).build());
        assertNotNull(f);
        assertTrue(f.getAmountType() == Money.class);
        f = Monetary.getAmountFactory(MonetaryAmountFactoryQueryBuilder.of().build());
        assertNotNull(f);
        assertTrue(f.getAmountType() == Monetary.getDefaultAmountType());
        f = Monetary.getAmountFactory(MonetaryAmountFactoryQueryBuilder.of().build());
        assertNotNull(f);
        assertTrue(f.getAmountType() == Money.class);
        f = Monetary.getAmountFactory(MonetaryAmountFactoryQueryBuilder.of().setPrecision(5).build());
        assertNotNull(f);
        f = Monetary.getAmountFactory(MonetaryAmountFactoryQueryBuilder.of().setPrecision(20).build());
        assertNotNull(f);
        assertTrue(f.getAmountType() == Money.class);
        f = Monetary.getAmountFactory(MonetaryAmountFactoryQueryBuilder.of().setPrecision(5).build());
        assertNotNull(f);
        assertTrue(f.getAmountType() == Money.class);
        f = Monetary.getAmountFactory(MonetaryAmountFactoryQueryBuilder.of().setPrecision(5).build());
        assertNotNull(f);
        assertTrue(f.getAmountType() == Money.class);
        f = Monetary.getAmountFactory(MonetaryAmountFactoryQueryBuilder.of().setPrecision(5).build());
        assertNotNull(f);
        assertTrue(f.getAmountType() == Money.class);
        f = Monetary.getAmountFactory(MonetaryAmountFactoryQueryBuilder.of().setPrecision(200).build());
        assertNotNull(f);
        assertTrue(f.getAmountType() == Money.class);
    }

    /**
     * Test method for
     * {@link javax.money.Monetary#getAmountFactory(javax.money.MonetaryAmountFactoryQuery)} .
     */
    @Test(expectedExceptions = MonetaryException.class)
    public void testQueryAmountType_InvalidContext(){
        Monetary.getAmountFactory(
                MonetaryAmountFactoryQueryBuilder.of().setTargetType(FastMoney.class).setPrecision(20).build());
    }

}
