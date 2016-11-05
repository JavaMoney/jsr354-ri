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

import static org.javamoney.moneta.function.MonetaryOperators.rounding;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

import java.util.Locale;

import javax.money.CurrencyUnit;
import javax.money.Monetary;
import javax.money.MonetaryAmount;

import org.javamoney.moneta.RoundedMoney;
import org.javamoney.moneta.function.MonetaryAmountProducer;
import org.javamoney.moneta.function.RoundedMoneyProducer;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

public class RoundedMoneyProducerTest {

	private MonetaryAmountProducer producer;

	private CurrencyUnit currency;

	@BeforeMethod
	public void setup() {
		producer = new RoundedMoneyProducer(rounding());
		currency = Monetary.getCurrency(Locale.getDefault());
	}

	@Test(expectedExceptions = NullPointerException.class)
	public void shouldReturnErrorWhenCurrencyIsNull() {
		producer.create(null, 10);
	}

	@Test(expectedExceptions = NullPointerException.class)
	public void shouldReturnErrorWhenNumberIsNull() {
		producer.create(currency, null);
	}

	@Test
	public void shouldCreateMonetaryAmount() {
		Long value = 10L;
		MonetaryAmount amount = producer.create(currency, value);
		assertEquals(amount.getCurrency(), currency);
		assertEquals(Long.valueOf(amount.getNumber().longValue()), value);
	}

	@Test
	public void shouldCreateUsingRoundedMoneyImplementation() {
		Long value = 10L;
		MonetaryAmount amount = producer.create(currency, value);
		assertTrue(RoundedMoney.class.isInstance(amount));
	}

}
