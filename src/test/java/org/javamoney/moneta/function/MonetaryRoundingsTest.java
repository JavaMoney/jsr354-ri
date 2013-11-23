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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Currency;

import javax.money.CurrencyUnit;
import javax.money.MonetaryAdjuster;

import org.javamoney.moneta.Money;
import org.javamoney.moneta.MoneyCurrency;
import org.junit.Test;

/**
 * Test for the {@link MonetaryRoundings} singleton.
 * 
 * @author Anatole Tresch
 */
public class MonetaryRoundingsTest {

	/**
	 * Test method for
	 * {@link org.javamoney.moneta.function.MonetaryRoundings#getRounding()}.
	 */
	@Test
	public void testGetRounding() {
		MonetaryAdjuster rounding = MonetaryRoundings.getRounding();
		assertNotNull(rounding);
		Money m = Money.of("CHF", new BigDecimal("10.123456"));
		Money r = m.with(rounding);
		assertEquals(Money.of("CHF", new BigDecimal("10.12")), r);
	}

	/**
	 * Test method for
	 * {@link org.javamoney.moneta.function.MonetaryRoundings#getRounding(int, java.math.RoundingMode)}
	 * .
	 */
	@Test
	public void testGetRoundingIntRoundingMode() {
		Money[] samples = new Money[] {
				Money.of("CHF", new BigDecimal("0.0000000001")),
				Money.of("CHF", new BigDecimal("1.00000000000023")),
				Money.of("CHF", new BigDecimal("1.1123442323")),
				Money.of("CHF", new BigDecimal("1.50000000000")),
				Money.of("CHF", new BigDecimal("-1.000000003")),
				Money.of("CHF", new BigDecimal("-1.100232876532876389")),
				Money.of("CHF", new BigDecimal("-1.500000000000")) };
		int[] scales = new int[] { 0, 1, 2, 3, 4, 5 };
		for (int i = 0; i < samples.length; i++) {
			for (int scale : scales) {
				for (RoundingMode roundingMode : RoundingMode.values()) {
					if (roundingMode == RoundingMode.UNNECESSARY) {
						continue;
					}
					MonetaryAdjuster rounding = MonetaryRoundings.getRounding(
							scale, roundingMode);
					BigDecimal dec = samples[i].asNumber();
					BigDecimal expected = dec.setScale(scale, roundingMode);
					Money r = samples[i].with(rounding);
					assertEquals(Money.of("CHF", expected), r);
				}
			}
		}
	}

	/**
	 * Test method for
	 * {@link org.javamoney.moneta.function.MonetaryRoundings#getRounding(javax.money.CurrencyUnit)}
	 * .
	 */
	@Test
	public void testGetRoundingCurrencyUnit() {
		Money[] samples = new Money[] {
				Money.of("CHF", new BigDecimal("0.0000000001")),
				Money.of("CHF", new BigDecimal("1.00000000000023")),
				Money.of("CHF", new BigDecimal("1.1123442323")),
				Money.of("CHF", new BigDecimal("1.50000000000")),
				Money.of("CHF", new BigDecimal("-1.000000003")),
				Money.of("CHF", new BigDecimal("-1.100232876532876389")),
				Money.of("CHF", new BigDecimal("-1.500000000000")) };
		int[] scales = new int[] { 0, 1, 2, 3, 4, 5 };
		for (int i = 0; i < samples.length; i++) {
			for (Currency currency : Currency.getAvailableCurrencies()) {
				MoneyCurrency cur = MoneyCurrency
						.of(currency.getCurrencyCode());
				// Omit test roundings, which are for testing only...
				if("XXX".equals(cur.getCurrencyCode())){
					continue;
				}
				else if("CHF".equals(cur.getCurrencyCode())){
					continue;
				}
				MonetaryAdjuster rounding = MonetaryRoundings.getRounding(cur);
				BigDecimal dec = samples[i].asNumber();
				BigDecimal expected = null;
				if (cur.getDefaultFractionDigits() < 0) {
					expected = dec.setScale(0, RoundingMode.HALF_UP);
				}
				else {
					expected = dec.setScale(cur.getDefaultFractionDigits(),
							RoundingMode.HALF_UP);
				}
				Money r = samples[i].with(rounding);
				assertEquals("Rouding for: " + samples[i],
						Money.of("CHF", expected), r);
			}
		}
	}

	/**
	 * Test method for
	 * {@link org.javamoney.moneta.function.MonetaryRoundings#getCashRounding(javax.money.CurrencyUnit)}
	 * .
	 */
	@Test
	public void testGetCashRoundingCurrencyUnit() {
		MonetaryAdjuster r = MonetaryRoundings.getCashRounding(MoneyCurrency
				.of("GBP"));
		assertNotNull(r);
		r = MonetaryRoundings.getCashRounding(MoneyCurrency.of("CHF"));
		assertNotNull(r);
		assertEquals(Money.of("CHF", 2), Money.of("CHF", 2.02).with(r));
		assertEquals(Money.of("CHF", 2.05), Money.of("CHF", 2.025).with(r));
	}

	/**
	 * Test method for
	 * {@link org.javamoney.moneta.function.MonetaryRoundings#getRounding(javax.money.CurrencyUnit, long)}
	 * .
	 */
	@Test
	public void testGetRoundingCurrencyUnitLong() {
		MonetaryAdjuster r = MonetaryRoundings.getRounding(MoneyCurrency
				.of("XXX"), System.currentTimeMillis()+20000L);
		assertNotNull(r);
		assertEquals(Money.of("XXX", -1), Money.of("XXX", 2.0234343).with(r));
	}

	/**
	 * Test method for
	 * {@link org.javamoney.moneta.function.MonetaryRoundings#getCashRounding(javax.money.CurrencyUnit, long)}
	 * .
	 */
	@Test
	public void testGetCashRoundingCurrencyUnitLong() {
		MonetaryAdjuster r = MonetaryRoundings.getCashRounding(MoneyCurrency
				.of("CHF"), System.currentTimeMillis()+20000L);
		assertNotNull(r);
		assertEquals(Money.of("CHF", -1), Money.of("CHF", 2.0234343).with(r));
	}

	/**
	 * Test method for
	 * {@link org.javamoney.moneta.function.MonetaryRoundings#getRounding(java.lang.String)}
	 * .
	 */
	@Test
	public void testGetRoundingString() {
		assertNotNull(MonetaryRoundings.getRounding("zero"));
		assertNotNull(MonetaryRoundings.getRounding("minusOne"));
		assertNotNull(MonetaryRoundings.getRounding("CHF-cash"));
		MonetaryAdjuster minusOne = MonetaryRoundings.getRounding("minusOne");
		assertEquals(Money.of("CHF", -1), Money.of("CHF", 213873434.3463843847)
				.with(minusOne));
	}

	/**
	 * Test method for
	 * {@link org.javamoney.moneta.function.MonetaryRoundings#getCustomRoundingIds()}
	 * .
	 */
	@Test
	public void testGetCustomRoundingIds() {
		assertNotNull(MonetaryRoundings.getCustomRoundingIds());
		assertTrue(MonetaryRoundings.getCustomRoundingIds().size() == 3);
		assertTrue(MonetaryRoundings.getCustomRoundingIds().contains("zero"));
		assertTrue(MonetaryRoundings.getCustomRoundingIds()
				.contains("minusOne"));
		assertTrue(MonetaryRoundings.getCustomRoundingIds()
				.contains("CHF-cash"));
	}
	
	// Bad cases
	
	/**
	 * Test method for
	 * {@link org.javamoney.moneta.function.MonetaryRoundings#getRounding(java.lang.String)}
	 * .
	 */
	@Test(expected = IllegalArgumentException.class)
	public void testGetRoundingString_Invalid() {
		assertNull(MonetaryRoundings.getRounding("foo"));
	}

	
	/**
	 * Test method for
	 * {@link org.javamoney.moneta.function.MonetaryFunctions#reciprocal()}.
	 */
	@Test(expected=NullPointerException.class)
	public void testGetCashRounding_Null1() {
		MonetaryRoundings.getCashRounding(null);
	}
	
	/**
	 * Test method for
	 * {@link org.javamoney.moneta.function.MonetaryFunctions#reciprocal()}.
	 */
	@Test(expected=NullPointerException.class)
	public void testGetCashRounding_Null2() {
		MonetaryRoundings.getCashRounding(MoneyCurrency.of("USD")).adjustInto(null);
	}
	
	/**
	 * Test method for
	 * {@link org.javamoney.moneta.function.MonetaryFunctions#reciprocal()}.
	 */
	@Test(expected=NullPointerException.class)
	public void testGetRounding_Null1() {
		MonetaryRoundings.getRounding((CurrencyUnit)null);
	}
	
	/**
	 * Test method for
	 * {@link org.javamoney.moneta.function.MonetaryFunctions#reciprocal()}.
	 */
	@Test(expected=NullPointerException.class)
	public void testGetRounding_Null3() {
		MonetaryRoundings.getRounding((String)null);
	}
	
	/**
	 * Test method for
	 * {@link org.javamoney.moneta.function.MonetaryFunctions#reciprocal()}.
	 */
	@Test(expected=NullPointerException.class)
	public void testGetRounding_Null2() {
		MonetaryRoundings.getRounding(MoneyCurrency.of("USD")).adjustInto(null);
	}

}
