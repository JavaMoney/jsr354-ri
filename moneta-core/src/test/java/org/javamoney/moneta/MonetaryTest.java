/**
 * Copyright (c) 2012, 2017, Anatole Tresch, Werner Keil and others by the @author tag.
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
package org.javamoney.moneta;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNull;

import java.math.BigDecimal;
import java.math.RoundingMode;

import javax.money.Monetary;
import javax.money.MonetaryAmount;
import javax.money.MonetaryAmountFactoryQueryBuilder;
import javax.money.MonetaryContext;
import javax.money.MonetaryContextBuilder;

import org.junit.Ignore;
import org.testng.annotations.Test;

public class MonetaryTest {

	@Test
	public void shouldCreateMonetaryFactory() {
		MonetaryAmount monetaryAmount = Monetary
				.getAmountFactory(
						MonetaryAmountFactoryQueryBuilder.of().set(RoundingMode.DOWN).setPrecision(256).build())
				.setCurrency("CHF").setNumber(1234.5678).create();
		assertEquals(256, monetaryAmount.getContext().getPrecision());
		assertEquals(RoundingMode.DOWN, monetaryAmount.getContext().get(RoundingMode.class));
	}

	@Test
	public void shouldCreateMonetaryFactoryWithRoundindModeNull() {
		MonetaryAmount monetaryAmount = Monetary
				.getAmountFactory(MonetaryAmountFactoryQueryBuilder.of().setPrecision(256).build()).setCurrency("CHF")
				.setNumber(1234.5678).create();
		assertEquals(256, monetaryAmount.getContext().getPrecision());
		assertNull(monetaryAmount.getContext().get(RoundingMode.class));
	}

	@Test
	public void shouldCreateMonetaryFactoryWithPrecisionNull() {
		MonetaryAmount monetaryAmount = Monetary.getAmountFactory(
				MonetaryAmountFactoryQueryBuilder.of().set(RoundingMode.HALF_DOWN).setTargetType(Money.class).build())
				.setCurrency("CHF").setNumber(1234.5678).create();
		assertEquals(0, monetaryAmount.getContext().getPrecision());
		assertEquals(RoundingMode.HALF_DOWN, monetaryAmount.getContext().get(RoundingMode.class));
	}

	@Test
	public void shouldCreateMonetaryFactoryWithBothNull() {
		MonetaryAmount monetaryAmount = Monetary.getAmountFactory(
				MonetaryAmountFactoryQueryBuilder.of().set(RoundingMode.HALF_DOWN).setTargetType(Money.class).build())
				.setCurrency("CHF").setNumber(1234.5678).create();
		assertEquals(0, monetaryAmount.getContext().getPrecision());
		assertEquals(RoundingMode.HALF_DOWN, monetaryAmount.getContext().get(RoundingMode.class));
	}

	@Test //(enabled=false)
	// TODO Currently maxScale is not used in some places, see https://github.com/JavaMoney/jsr354-ri/issues/162
	public void shouldUseContextInFactory() {
		MonetaryContext mc = MonetaryContextBuilder.of().setMaxScale(2).setPrecision(64).set(RoundingMode.HALF_DOWN).build();
		MonetaryAmount am = Monetary.getDefaultAmountFactory().setContext(mc).setNumber(999.999).setCurrency("EUR").create();
		assertEquals(am.getNumber(), BigDecimal.valueOf(999.99));
	}
}
