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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import javax.money.MonetaryAmount;
import javax.money.MonetaryAmounts;
import javax.money.MonetaryContext;
import javax.money.MonetaryException;

import org.junit.Test;

/**
 * @author Anatole
 * 
 */
public class MonetaryAmountsRegistrationTest {

	/**
	 * Test method for {@link javax.money.MonetaryAmounts#getAmountFactory(java.lang.Class)}.
	 */
	@Test
	public void testGetFactory() {
		assertNotNull(MonetaryAmounts.getAmountFactory());
		assertNotNull(MonetaryAmounts.getAmountFactory(FastMoney.class));
		assertNotNull(MonetaryAmounts.getAmountFactory(Money.class));
		assertTrue(MonetaryAmounts.getAmountFactory().getClass() == MonetaryAmounts
				.getAmountFactory(Money.class).getClass());
	}

	/**
	 * Test method for {@link javax.money.MonetaryAmounts#getAmountTypes()}.
	 */
	@Test
	public void testGetTypes() {
		assertNotNull(MonetaryAmounts.getAmountTypes());
		assertTrue(MonetaryAmounts.getAmountTypes().size() == 3);
		assertTrue(MonetaryAmounts.getAmountTypes().contains(FastMoney.class));
		assertTrue(MonetaryAmounts.getAmountTypes().contains(Money.class));
		assertTrue(MonetaryAmounts.getAmountTypes()
				.contains(RoundedMoney.class));
	}

	/**
	 * Test method for {@link javax.money.MonetaryAmounts#getDefaultAmountType()}.
	 */
	@Test
	public void testGetDefaultAmountType() {
		assertNotNull(MonetaryAmounts.getDefaultAmountType());
		assertEquals(Money.class, MonetaryAmounts.getDefaultAmountType());
	}

	/**
	 * Test method for
	 * {@link javax.money.MonetaryAmounts#queryAmountType(javax.money.MonetaryContext)} .
	 */
	@Test
	public void testGetAmountType() {
		assertNotNull(MonetaryAmounts.queryAmountType(null));
		assertEquals(Money.class, MonetaryAmounts.queryAmountType(null));
	}

	/**
	 * Test method for
	 * {@link javax.money.MonetaryAmounts#queryAmountType(javax.money.MonetaryContext)} .
	 */
	@Test
	public void testQueryAmountType() {
		Class<? extends MonetaryAmount> type = MonetaryAmounts
				.queryAmountType(new MonetaryContext.Builder(RoundedMoney.class)
						.build());
		assertNotNull(type);
		assertTrue(type == RoundedMoney.class);
		type = MonetaryAmounts.queryAmountType(new MonetaryContext.Builder(
				FastMoney.class).setPrecision(5).build());
		assertNotNull(type);
		assertTrue(type == FastMoney.class);
		type = MonetaryAmounts.queryAmountType(new MonetaryContext.Builder(
				Money.class).build());
		assertNotNull(type);
		assertTrue(type == Money.class);
		type = MonetaryAmounts.queryAmountType(new MonetaryContext.Builder()
				.build());
		assertNotNull(type);
		assertTrue(type == MonetaryAmounts.getDefaultAmountType());
		type = MonetaryAmounts.queryAmountType(new MonetaryContext.Builder()
				.build());
		assertNotNull(type);
		assertTrue(type == Money.class);
		type = MonetaryAmounts.queryAmountType(new MonetaryContext.Builder()
				.setPrecision(5).build());
		assertNotNull(type);
		type = MonetaryAmounts.queryAmountType(new MonetaryContext.Builder()
				.setPrecision(20).build());
		assertNotNull(type);
		assertTrue(type == Money.class);
		type = MonetaryAmounts.queryAmountType(new MonetaryContext.Builder()
				.setPrecision(5).build());
		assertNotNull(type);
		assertTrue(type == Money.class);
		type = MonetaryAmounts.queryAmountType(new MonetaryContext.Builder()
				.setPrecision(5).build());
		assertNotNull(type);
		assertTrue(type == Money.class);
		type = MonetaryAmounts.queryAmountType(new MonetaryContext.Builder()
				.setPrecision(5).build());
		assertNotNull(type);
		assertTrue(type == Money.class);
		type = MonetaryAmounts.queryAmountType(new MonetaryContext.Builder()
				.setPrecision(200).build());
		assertNotNull(type);
		assertTrue(type == Money.class);
	}

	/**
	 * Test method for
	 * {@link javax.money.MonetaryAmounts#queryAmountType(javax.money.MonetaryContext)} .
	 */
	@Test(expected = MonetaryException.class)
	public void testQueryAmountType_InvalidContext() {
		MonetaryAmounts
				.queryAmountType(new MonetaryContext.Builder(FastMoney.class)
						.setPrecision(20).build());
	}

}
