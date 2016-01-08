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

import java.math.BigDecimal;
import java.util.stream.Stream;

import javax.money.CurrencyUnit;
import javax.money.MonetaryAmount;
import javax.money.Monetary;

import org.javamoney.moneta.Money;

/**
 * Test utility class for testing streams.
 */
public final class StreamFactory {

	static final CurrencyUnit EURO = Monetary.getCurrency("EUR");
	static final CurrencyUnit DOLLAR = Monetary.getCurrency("USD");
	static final CurrencyUnit BRAZILIAN_REAL = Monetary
			.getCurrency("BRL");

    /**
     * Private constructor.
     */
    private StreamFactory(){}

	public static Stream<MonetaryAmount> streamCurrencyDifferent() {
		Money m1 = Money.of(BigDecimal.TEN, BRAZILIAN_REAL);
		Money m2 = Money.of(BigDecimal.ZERO, BRAZILIAN_REAL);
		Money m3 = Money.of(BigDecimal.ONE, BRAZILIAN_REAL);
		Money m4 = Money.of(BigDecimal.valueOf(4L), EURO);
		Money m5 = Money.of(BigDecimal.valueOf(5L), BRAZILIAN_REAL);
		return Stream.of(m1, m2, m3, m4, m5);
	}

	public static Stream<MonetaryAmount> currencies() {
		Money r1 = Money.of(BigDecimal.TEN, BRAZILIAN_REAL);
		Money r2 = Money.of(BigDecimal.ZERO, BRAZILIAN_REAL);
		Money r3 = Money.of(BigDecimal.ONE, BRAZILIAN_REAL);

		Money e1 = Money.of(BigDecimal.TEN, EURO);
		Money e2 = Money.of(BigDecimal.ZERO, EURO);
		Money e3 = Money.of(BigDecimal.ONE, EURO);

		Money d1 = Money.of(BigDecimal.TEN, DOLLAR);
		Money d2 = Money.of(BigDecimal.ZERO, DOLLAR);
		Money d3 = Money.of(BigDecimal.ONE, DOLLAR);
		return Stream.of(r1, r2, r3, e1, e2, e3, d1, d2, d3);
	}

	public static Stream<MonetaryAmount> currenciesToSummary() {
		Money r1 = Money.of(BigDecimal.TEN, BRAZILIAN_REAL);
		Money r2 = Money.of(BigDecimal.ZERO, BRAZILIAN_REAL);
		Money r3 = Money.of(BigDecimal.ONE, BRAZILIAN_REAL);
		Money r4 = Money.of(BigDecimal.ONE, BRAZILIAN_REAL);
		Money r5 = Money.of(BigDecimal.ONE, BRAZILIAN_REAL);
		Money r6 = Money.of(BigDecimal.ONE, BRAZILIAN_REAL);
		Money r7 = Money.of(BigDecimal.ONE, BRAZILIAN_REAL);
		Money r8 = Money.of(BigDecimal.ONE, BRAZILIAN_REAL);

		Money e1 = Money.of(BigDecimal.TEN, EURO);
		Money d1 = Money.of(BigDecimal.ONE, DOLLAR);
		return Stream.of(r1, r2, r3, r4, r5, r6, r7, r8, e1, d1);
	}
	public static  Stream<MonetaryAmount> streamNormal() {
		Money m1 = Money.of(BigDecimal.TEN, BRAZILIAN_REAL);
		Money m2 = Money.of(BigDecimal.ZERO, BRAZILIAN_REAL);
		Money m3 = Money.of(BigDecimal.ONE, BRAZILIAN_REAL);
		Money m4 = Money.of(BigDecimal.valueOf(4L), BRAZILIAN_REAL);
		Money m5 = Money.of(BigDecimal.valueOf(5L), BRAZILIAN_REAL);
		return Stream.of(m1, m2, m3, m4, m5);
	}

	public static  Stream<MonetaryAmount> streamNull() {
		Money m1 = Money.of(BigDecimal.TEN, BRAZILIAN_REAL);
		Money m2 = Money.of(BigDecimal.ZERO, BRAZILIAN_REAL);
		Money m3 = Money.of(BigDecimal.ONE, BRAZILIAN_REAL);
		Money m4 = Money.of(BigDecimal.valueOf(4L), BRAZILIAN_REAL);
		Money m5 = Money.of(BigDecimal.valueOf(5L), BRAZILIAN_REAL);
		return Stream.of(m1, m2, m3, m4, m5, null);
	}
}
