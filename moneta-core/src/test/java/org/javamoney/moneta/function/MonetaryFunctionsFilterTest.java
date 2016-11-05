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

import static org.javamoney.moneta.function.MonetaryFunctions.*;
import static org.javamoney.moneta.function.StreamFactory.BRAZILIAN_REAL;
import static org.javamoney.moneta.function.StreamFactory.DOLLAR;
import static org.javamoney.moneta.function.StreamFactory.currencies;

import java.util.List;
import java.util.stream.Collectors;

import javax.money.MonetaryAmount;

import org.javamoney.moneta.FastMoney;
import org.junit.Assert;
import org.testng.annotations.Test;

public class MonetaryFunctionsFilterTest {


	@Test
	public void isCurrencyTest() {
		List<MonetaryAmount> justRealList = currencies().filter(isCurrency(BRAZILIAN_REAL)).collect(
				Collectors.toList());
		Assert.assertEquals(3, justRealList.size());
	}

	@Test
	public void isNotCurrencyTest() {
		List<MonetaryAmount> justRealList = currencies().filter(filterByExcludingCurrency(BRAZILIAN_REAL)).collect(
				Collectors.toList());
		Assert.assertEquals(6, justRealList.size());
	}

	@Test
	public void containsCurrenciesTest() {
		List<MonetaryAmount> justRealList = currencies().filter(
				isCurrency(BRAZILIAN_REAL, DOLLAR))
				.collect(
				Collectors.toList());
		Assert.assertEquals(6, justRealList.size());
	}

	@Test
	public void shouldReturnAllisCurrencyEmptyTest() {
		List<MonetaryAmount> justRealList = currencies().filter(isCurrency())
				.collect(Collectors.toList());
		Assert.assertEquals(9, justRealList.size());
	}

	@Test
	public void shouldReturnAllfiterByExcludingCurrencyEmptyTest() {
		List<MonetaryAmount> justRealList = currencies().filter(
				filterByExcludingCurrency()).collect(Collectors.toList());
		Assert.assertEquals(9, justRealList.size());
	}
	@Test
	public void isGreaterThanTest() {
		MonetaryAmount money = FastMoney.of(1, BRAZILIAN_REAL);
		List<MonetaryAmount> justRealList = currencies().filter(isCurrency(BRAZILIAN_REAL).and(isGreaterThan(money))).collect(
				Collectors.toList());
		Assert.assertEquals(1, justRealList.size());
	}

	@Test
	public void isGreaterThanOrEqualToTest() {
		MonetaryAmount money = FastMoney.of(1, BRAZILIAN_REAL);
		List<MonetaryAmount> justRealList = currencies().filter(isCurrency(BRAZILIAN_REAL).and(isGreaterThanOrEqualTo(money))).collect(
				Collectors.toList());
		Assert.assertEquals(2, justRealList.size());
	}

	@Test
	public void isLessThanTest() {
		MonetaryAmount money = FastMoney.of(1, BRAZILIAN_REAL);
		List<MonetaryAmount> justRealList = currencies().filter(isCurrency(BRAZILIAN_REAL).and(isLessThan(money))).collect(
				Collectors.toList());
		Assert.assertEquals(1, justRealList.size());
	}

	@Test
	public void isLessThanOrEqualToTest() {
		MonetaryAmount money = FastMoney.of(1, BRAZILIAN_REAL);
		List<MonetaryAmount> justRealList = currencies().filter(isCurrency(BRAZILIAN_REAL).and(isLessThanOrEqualTo(money))).collect(
				Collectors.toList());
		Assert.assertEquals(2, justRealList.size());
	}

	@Test
	public void isBetweenTest() {
		MonetaryAmount min = FastMoney.of(0, BRAZILIAN_REAL);
		MonetaryAmount max = FastMoney.of(10, BRAZILIAN_REAL);
		List<MonetaryAmount> justRealList = currencies().filter(isCurrency(BRAZILIAN_REAL).and(isBetween(min, max))).collect(
				Collectors.toList());
		Assert.assertEquals(3, justRealList.size());
	}
}
