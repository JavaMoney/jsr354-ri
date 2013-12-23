/*
 * CREDIT SUISSE IS WILLING TO LICENSE THIS SPECIFICATION TO YOU ONLY UPON THE
 * CONDITION THAT YOU ACCEPT ALL OF THE TERMS CONTAINED IN THIS AGREEMENT.
 * PLEASE READ THE TERMS AND CONDITIONS OF THIS AGREEMENT CAREFULLY. BY
 * DOWNLOADING THIS SPECIFICATION, YOU ACCEPT THE TERMS AND CONDITIONS OF THE
 * AGREEMENT. IF YOU ARE NOT WILLING TO BE BOUND BY IT, SELECT THE "DECLINE"
 * BUTTON AT THE BOTTOM OF THIS PAGE.
 * 
 * Specification: JSR-354 Money and Currency API ("Specification")
 * 
 * Copyright (c) 2012-2013, Credit Suisse All rights reserved.
 */
package org.javamoney.moneta;

import static org.junit.Assert.*;

import javax.money.MonetaryAmounts;

import org.junit.Test;

/**
 * @author Anatole
 * 
 */
public class MonetaryAmountsRegistrationTest {

	/**
	 * Test method for
	 * {@link javax.money.MonetaryAmounts#getAmountFactory(java.lang.Class)}.
	 */
	@Test
	public void testGetFactory() {
		assertNotNull(MonetaryAmounts.getDefaultAmountFactory());
		assertNotNull(MonetaryAmounts.getAmountFactory(FastMoney.class));
		assertNotNull(MonetaryAmounts.getAmountFactory(Money.class));
		assertTrue(MonetaryAmounts.getDefaultAmountFactory() == MonetaryAmounts
				.getAmountFactory(Money.class));
	}

	/**
	 * Test method for {@link javax.money.MonetaryAmounts#getAmountTypes()}.
	 */
	@Test
	public void testGetTypes() {
		assertNotNull(MonetaryAmounts.getAmountTypes());
		assertTrue(MonetaryAmounts.getAmountTypes().size() == 2);
		assertTrue(MonetaryAmounts.getAmountTypes().contains(FastMoney.class));
		assertTrue(MonetaryAmounts.getAmountTypes().contains(Money.class));
	}

	/**
	 * Test method for
	 * {@link javax.money.MonetaryAmounts#getDefaultAmountType()}.
	 */
	@Test
	public void testGetDefaultAmountType() {
		assertNotNull(MonetaryAmounts.getDefaultAmountType());
		assertEquals(Money.class, MonetaryAmounts.getDefaultAmountType());
	}

	/**
	 * Test method for
	 * {@link javax.money.MonetaryAmounts#queryAmountType(javax.money.MonetaryContext)}
	 * .
	 */
	@Test
	public void testGetAmountType() {
		assertNotNull(MonetaryAmounts.queryAmountType(null));
		assertEquals(Money.class, MonetaryAmounts.queryAmountType(null));
	}

}
