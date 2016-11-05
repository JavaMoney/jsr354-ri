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
package org.javamoney.moneta.format;

import static org.testng.Assert.assertEquals;

import java.util.Locale;

import javax.money.CurrencyUnit;
import javax.money.Monetary;
import javax.money.MonetaryAmount;

import org.javamoney.moneta.Money;
import org.javamoney.moneta.spi.MoneyProducer;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;


public class MonetaryAmountFormatSymbolsTest {

	private MonetaryAmount money;

	private CurrencyUnit currency;

	private Number number = Long.valueOf(10_000_000L);

	@BeforeMethod
	public void setup() {
		currency = Monetary.getCurrency(Locale.getDefault(Locale.Category.FORMAT));
		money = Money.of(number, currency);
	}

	@Test
	public void shouldReturnDefaultMonetaryAmountFormatSymbol() {
		MonetaryAmountFormatSymbols defafult = MonetaryAmountFormatSymbols.getDefafult();
		String format = defafult.format(money);
		MonetaryAmount amount = defafult.parse(format);
		assertEquals(amount.getCurrency(), currency);
		assertEquals(amount.getNumber().numberValue(Long.class), number);
	}

	@Test
	public void shouldReturnMonetaryAmountFromPattern() {
		MonetaryAmountFormatSymbols monetaryFormat = MonetaryAmountFormatSymbols.of("##,####,####" , new MonetaryAmountSymbols(), new MoneyProducer());
		money = Money.of(number, currency);
		String format = monetaryFormat.format(money);
		MonetaryAmount amount = monetaryFormat.parse(format);
		assertEquals(amount.getCurrency(), currency);
		assertEquals(amount.getNumber().numberValue(Long.class), number);

	}

	@Test
	public void shouldReturnMonetaryAmountDecimalSymbols() {
		CurrencyUnit dollar = Monetary.getCurrency(Locale.US);
		MonetaryAmountSymbols symbols = new MonetaryAmountSymbols();
		symbols.setCurrency(dollar);
		MonetaryAmountFormatSymbols monetaryFormat = MonetaryAmountFormatSymbols.of(symbols, new MoneyProducer());
		money = Money.of(number, dollar);
		String format = monetaryFormat.format(money);
		MonetaryAmount amount = monetaryFormat.parse(format);
		assertEquals(amount.getCurrency(), dollar);
		assertEquals(amount.getNumber().numberValue(Long.class), number);

	}


}
