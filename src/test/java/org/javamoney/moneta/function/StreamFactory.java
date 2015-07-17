/*
 * CREDIT SUISSE IS WILLING TO LICENSE THIS SPECIFICATION TO YOU ONLY UPON THE CONDITION THAT YOU
 * ACCEPT ALL OF THE TERMS CONTAINED IN THIS AGREEMENT. PLEASE READ THE TERMS AND CONDITIONS OF THIS
 * AGREEMENT CAREFULLY. BY DOWNLOADING THIS SPECIFICATION, YOU ACCEPT THE TERMS AND CONDITIONS OF
 * THE AGREEMENT. IF YOU ARE NOT WILLING TO BE BOUND BY IT, SELECT THE "DECLINE" BUTTON AT THE
 * BOTTOM OF THIS PAGE. Specification: JSR-354 Money and Currency API ("Specification") Copyright
 * (c) 2012-2015, Credit Suisse All rights reserved.
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
