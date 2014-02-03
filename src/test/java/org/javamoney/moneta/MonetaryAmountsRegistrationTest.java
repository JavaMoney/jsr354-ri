/*
 * CREDIT SUISSE IS WILLING TO LICENSE THIS SPECIFICATION TO YOU ONLY UPON THE CONDITION THAT YOU
 * ACCEPT ALL OF THE TERMS CONTAINED IN THIS AGREEMENT. PLEASE READ THE TERMS AND CONDITIONS OF THIS
 * AGREEMENT CAREFULLY. BY DOWNLOADING THIS SPECIFICATION, YOU ACCEPT THE TERMS AND CONDITIONS OF
 * THE AGREEMENT. IF YOU ARE NOT WILLING TO BE BOUND BY IT, SELECT THE "DECLINE" BUTTON AT THE
 * BOTTOM OF THIS PAGE. Specification: JSR-354 Money and Currency API ("Specification") Copyright
 * (c) 2012-2013, Credit Suisse All rights reserved.
 */
package org.javamoney.moneta;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import javax.money.MonetaryAmount;
import javax.money.MonetaryAmounts;
import javax.money.MonetaryContext;
import javax.money.MonetaryContext.AmountFlavor;
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
						.create());
		assertNotNull(type);
		assertTrue(type == RoundedMoney.class);
		type = MonetaryAmounts.queryAmountType(new MonetaryContext.Builder(
				FastMoney.class).setPrecision(5).create());
		assertNotNull(type);
		assertTrue(type == FastMoney.class);
		type = MonetaryAmounts.queryAmountType(new MonetaryContext.Builder(
				Money.class).create());
		assertNotNull(type);
		assertTrue(type == Money.class);
		type = MonetaryAmounts.queryAmountType(new MonetaryContext.Builder()
				.create());
		assertNotNull(type);
		assertTrue(type == MonetaryAmounts.getDefaultAmountType());
		type = MonetaryAmounts.queryAmountType(new MonetaryContext.Builder()
				.setFlavor(AmountFlavor.PRECISION).create());
		assertNotNull(type);
		assertTrue(type == Money.class);
		type = MonetaryAmounts.queryAmountType(new MonetaryContext.Builder()
				.setFlavor(AmountFlavor.PERFORMANCE).setPrecision(5).create());
		assertNotNull(type);
		assertTrue(type == FastMoney.class);
		type = MonetaryAmounts.queryAmountType(new MonetaryContext.Builder()
				.setFlavor(AmountFlavor.PERFORMANCE).setPrecision(20).create());
		assertNotNull(type);
		assertTrue(type == Money.class);
		type = MonetaryAmounts.queryAmountType(new MonetaryContext.Builder()
				.setFlavor(AmountFlavor.PRECISION).setPrecision(5).create());
		assertNotNull(type);
		assertTrue(type == Money.class);
		type = MonetaryAmounts.queryAmountType(new MonetaryContext.Builder()
				.setFlavor(AmountFlavor.UNDEFINED).setPrecision(5).create());
		assertNotNull(type);
		assertTrue(type == Money.class);
		type = MonetaryAmounts.queryAmountType(new MonetaryContext.Builder()
				.setFlavor(AmountFlavor.UNDEFINED).setPrecision(5).create());
		assertNotNull(type);
		assertTrue(type == Money.class);
		type = MonetaryAmounts.queryAmountType(new MonetaryContext.Builder()
				.setFlavor(AmountFlavor.UNDEFINED).setPrecision(200).create());
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
						.setPrecision(20).create());
	}

}
