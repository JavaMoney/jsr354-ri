/*
 *  Copyright (c) 2012, 2013, Credit Suisse (Anatole Tresch), Werner Keil.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *
 * Contributors:
 *    Anatole Tresch - initial implementation
 *    Wernner Keil - extensions and adaptions.
 */
package org.javamoney.moneta;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.math.BigDecimal;
import java.math.RoundingMode;

import javax.money.CurrencyUnit;
import javax.money.MonetaryAmount;

import org.junit.Test;

public class FastMoneyTest {

	private static final BigDecimal TEN = new BigDecimal(10.0d);
	protected static final CurrencyUnit EURO = MoneyCurrency.of("EUR");
	protected static final CurrencyUnit DOLLAR = MoneyCurrency
			.of("USD");

	@Test
	public void testGetInstanceCurrencyBigDecimal() {
		FastMoney m = FastMoney.of(MoneyCurrency.of("EUR"), TEN);
		assertEquals(TEN.setScale(4, RoundingMode.HALF_EVEN), m.asType(BigDecimal.class).setScale(4, RoundingMode.HALF_EVEN));
		assertEquals(Long.valueOf(10L), m.asType(Long.class));
	}

	@Test
	public void testGetInstanceCurrencyDouble() {
		FastMoney m = FastMoney.of(MoneyCurrency.of("EUR"), 10.0d);
		assertTrue(TEN.doubleValue() == m.doubleValue());
	}

	@Test
	public void testGetCurrency() {
		MonetaryAmount money = FastMoney.of(EURO, BigDecimal.TEN);
		assertNotNull(money.getCurrency());
		assertEquals("EUR", money.getCurrency().getCurrencyCode());
	}

	@Test
	public void testAddNumber() {
		FastMoney money1 = FastMoney.of(EURO, BigDecimal.TEN);
		FastMoney money2 = FastMoney.of(EURO, BigDecimal.ONE);
		FastMoney moneyResult = money1.add(money2);
		assertNotNull(moneyResult);
		assertEquals(11d, moneyResult.doubleValue(), 0d);
		FastMoney money3 = FastMoney.of(EURO, BigDecimal.valueOf(12345.23345));
		moneyResult = moneyResult.add(money3);
		assertNotNull(moneyResult);
		assertEquals(11d + 12345.23345d, moneyResult.doubleValue(), 0.00001d);
		Money money4 = Money.of(EURO, 12345678.123d);
		moneyResult = moneyResult.add(money4);
		assertNotNull(moneyResult);
		assertEquals(11d + 12345.23345d + 12345678.123d, moneyResult.doubleValue(), 0.00001d);
	}
	
	@Test
	public void testDivide() {
		FastMoney money1 = FastMoney.of(EURO, BigDecimal.TEN);
		FastMoney moneyResult = money1.divide(2);
		assertNotNull(moneyResult);
		assertEquals(10 / 2d, moneyResult.doubleValue(), 0.000001d);
		
		FastMoney money2 = FastMoney.of(EURO, 1.56);
		moneyResult = money1.divide(money2);
		assertNotNull(moneyResult);
		assertEquals(10d / 1.56d, moneyResult.doubleValue(), 0.00001d);
		
	}

	@Test
	public void testSubtractMonetaryAmount() {
		FastMoney money1 = FastMoney.of(EURO, BigDecimal.TEN);
		FastMoney money2 = FastMoney.of(EURO, BigDecimal.ONE);
		FastMoney moneyResult = money1.subtract(money2);
		assertNotNull(moneyResult);
		assertEquals(9d, moneyResult.doubleValue(), 0d);
		Money money3 = Money.of(EURO, 12234.234);
		moneyResult = moneyResult.subtract(money3);
		assertNotNull(moneyResult);
		assertEquals(9d - 12234.234, moneyResult.doubleValue(), 0d);
	}

	@Test
	public void testDivideAndRemainder() {
		FastMoney money1 = FastMoney.of(EURO, 1000);
		FastMoney[] divideAndRemainder = money1.divideAndRemainder(11);
		assertEquals(90L, divideAndRemainder[0].longValue());
		assertEquals(10L, divideAndRemainder[1].longValue());
	}

	@Test
	public void testDivideToIntegralValue() {
		FastMoney money1 = FastMoney.of(EURO, 1000);
		FastMoney result = money1.divideToIntegralValue(5);
		assertEquals(200L, result.longValue());
	}
	
	@Test
	public void testFrom() {
		FastMoney m = FastMoney.of("CHF",  10.56);
		FastMoney m2 = FastMoney.from(m);
		assertTrue(m==m2);
		FastMoney m3 = FastMoney.from(Money.of("CHF", 10.56));
		assertEquals(m.asNumber(), m3.asNumber());
	}
}
