/**
 * Copyright (c) 2012, 2018, Werner Keil and others by the @author tag.
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

import javax.money.MonetaryAmount;
import javax.money.format.AmountFormatQueryBuilder;
import javax.money.format.MonetaryAmountFormat;
import javax.money.format.MonetaryFormats;

import org.testng.annotations.Test;

public class MonetaryFormatsTest {
	
	@Test(enabled = false)
	public void testParseDK() {
		final Locale.Builder localeBuilder = new Locale.Builder();
		localeBuilder.setLanguage("da"); // Danish
	    MonetaryAmountFormat format = MonetaryFormats
	            .getAmountFormat(AmountFormatQueryBuilder.of(localeBuilder.build())
	                    .set(CurrencyStyle.CODE)
	                    .build());
	    MonetaryAmount amountOk = format.parse("123,01 DKK");
	    MonetaryAmount amountKo = format.parse("14 000,12 DKK");
	    assertEquals(amountOk.getNumber().doubleValueExact(), 123.01); // OK
	    assertEquals(amountKo.getNumber().doubleValueExact(), 14000.12); // KO
	}
	
	@Test(enabled = false)
	public void testParseFR() {
	    MonetaryAmountFormat format = MonetaryFormats
	            .getAmountFormat(AmountFormatQueryBuilder.of(Locale.FRANCE)
	                    .set(CurrencyStyle.CODE)
	                    .build());
	    MonetaryAmount amountOk = format.parse("123,01 EUR");
	    MonetaryAmount amountKo = format.parse("14 000,12 EUR");
	    assertEquals(amountOk.getNumber().doubleValueExact(), 123.01); // OK
	    assertEquals(amountKo.getNumber().doubleValueExact(), 14000.12); // KO
	}
}
