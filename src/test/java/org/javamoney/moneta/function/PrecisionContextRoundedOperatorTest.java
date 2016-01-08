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
import static org.testng.Assert.assertTrue;
import static org.testng.Assert.fail;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;

import javax.money.CurrencyUnit;
import javax.money.Monetary;
import javax.money.MonetaryAmount;

import org.javamoney.moneta.Money;
import org.javamoney.moneta.RoundedMoney;
import org.testng.annotations.Test;

public class PrecisionContextRoundedOperatorTest {

	@Test(expectedExceptions = NullPointerException.class)
	public void shouldReturnNullPointerExceptionWhenParameterIsNull() {
		PrecisionContextRoundedOperator.of(null);
		fail();
	}

	@Test(expectedExceptions = IllegalArgumentException.class)
	public void shouldReturnErrorWhenScaleIsUNNECESSARY() {
		MathContext mathContext = new MathContext(2, RoundingMode.UNNECESSARY);
		PrecisionContextRoundedOperator.of(mathContext);
		fail();
	}

	@Test(expectedExceptions = IllegalArgumentException.class)
	public void shouldReturnErrorWhenPrecisionIsZero() {
		MathContext mathContext = new MathContext(0, RoundingMode.HALF_EVEN);
		PrecisionContextRoundedOperator.of(mathContext);
		fail();
	}

	@Test(expectedExceptions = NullPointerException.class)
	public void shouldReturnErrorWhenParameterIsNull() {
		MathContext mathContext = new MathContext(2, RoundingMode.CEILING);
		PrecisionContextRoundedOperator monetaryOperator = PrecisionContextRoundedOperator.of(mathContext);
		monetaryOperator.apply(null);
		fail();
	}

	@Test
	public void shouldRoundedMonetaryOperatorWhenTheImplementationIsMoney() {
		int scale = 4;
		MathContext mathContext = new MathContext(scale, RoundingMode.HALF_EVEN);

		CurrencyUnit real = Monetary.getCurrency("BRL");
		MonetaryAmount money = Money.of(BigDecimal.valueOf(35.34567), real);

		PrecisionContextRoundedOperator monetaryOperator = PrecisionContextRoundedOperator.of(mathContext);
		MonetaryAmount result = monetaryOperator.apply(money);
		assertTrue(RoundedMoney.class.isInstance(result));
		assertEquals(result.getCurrency(), real);
		assertEquals(result.getNumber().getPrecision(), scale);
		assertEquals(BigDecimal.valueOf(35.35), result.getNumber().numberValue(BigDecimal.class));



	}
}
