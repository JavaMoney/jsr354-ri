/**
 * Copyright (c) 2012, 2015, Credit Suisse (Anatole Tresch), Werner Keil and others by the @author tag.
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

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.fail;

import javax.money.CurrencyUnit;
import javax.money.Monetary;
import javax.money.MonetaryAmount;
import javax.money.MonetaryQuery;

import org.javamoney.moneta.Money;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

public class ExtractorMajorPartQueryTest {

	private MonetaryQuery<Long> query;

	@BeforeMethod
	public void setup() {
		query = new ExtractorMajorPartQuery();
	}


	@Test(expectedExceptions = NullPointerException.class)
	public void shouldReturnNPEWhenTheMonetaryAmountIsNull() {
		query.queryFrom(null);
		fail();
	}

	@Test(expectedExceptions = ArithmeticException.class)
	public void shouldReturnWhenTheValueIsBiggerThanLong() {
		CurrencyUnit real = Monetary.getCurrency("BRL");
		MonetaryAmount monetaryAmount = Money.of(Long.MAX_VALUE, real);
		query.queryFrom(monetaryAmount.add(Money.of(10, real)));
		fail();
	}

	@Test
	public void shouldReturnMajorPartPositive() {
		MonetaryAmount monetaryAmount = Money.parse("EUR 2.35");
		Long result = query.queryFrom(monetaryAmount);
		Long expected = 2L;
		assertEquals(result, expected );
	}

	@Test
	public void shouldReturnMajorPartNegative() {
		MonetaryAmount monetaryAmount = Money.parse("BHD -1.345");
		Long result = query.queryFrom(monetaryAmount);
		Long expected = -1L;
		assertEquals(result, expected );
	}


}
