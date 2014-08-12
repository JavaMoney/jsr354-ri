package org.javamoney.moneta.function;

import java.math.BigDecimal;
import java.util.stream.Stream;

import javax.money.CurrencyUnit;
import javax.money.MonetaryAmount;
import javax.money.MonetaryCurrencies;
import javax.money.MonetaryException;

import org.javamoney.moneta.Money;
import org.testng.Assert;
import org.testng.annotations.Test;

public class MonetaryFunctionsStream {

	protected static final CurrencyUnit EURO = MonetaryCurrencies.getCurrency("EUR");
    protected static final CurrencyUnit DOLLAR = MonetaryCurrencies.getCurrency("USD");
    protected static final CurrencyUnit BRAZILIAN_REAL = MonetaryCurrencies.getCurrency("BRL");

	@Test
	public void shouldSumCorretly() {
		Stream<MonetaryAmount> stream = streamNormal();
		MonetaryAmount sum = stream.reduce(MonetaryFunctions::sum).get();
		Assert.assertTrue(sum.getNumber().intValue() == 20);
	}

	@Test(expectedExceptions = NullPointerException.class)
	public void shouldsumWithNPEWhenAnElementIsNull() {
		Stream<MonetaryAmount> stream = streamNull();
		stream.reduce(MonetaryFunctions::sum).get();
	}

	@Test(expectedExceptions = MonetaryException.class)
	public void shouldSumMoneratyExceptionWhenHasDifferenctsCurrencies() {
		Stream<MonetaryAmount> stream = streamCurrencyDifferent();
		stream.reduce(MonetaryFunctions::sum).get();
	}

	@Test
	public void shouldMinCorretly() {
		Stream<MonetaryAmount> stream = streamNormal();
		MonetaryAmount min = stream.reduce(MonetaryFunctions::min).get();
		Assert.assertTrue(min.getNumber().intValue() == 0);
	}

	@Test(expectedExceptions = NullPointerException.class)
	public void shouldMinWithNPEWhenAnElementIsNull() {
		Stream<MonetaryAmount> stream = streamNull();
		stream.reduce(MonetaryFunctions::min).get();
	}

	@Test(expectedExceptions = MonetaryException.class)
	public void shouldMinMoneratyExceptionWhenHasDifferenctsCurrencies() {
		Stream<MonetaryAmount> stream = streamCurrencyDifferent();
		stream.reduce(MonetaryFunctions::min).get();
	}

	@Test
	public void shouldMaxCorretly() {
		Stream<MonetaryAmount> stream = streamNormal();
		MonetaryAmount max = stream.reduce(MonetaryFunctions::max).get();
		Assert.assertTrue(max.getNumber().intValue() == 10);
	}

	@Test(expectedExceptions = NullPointerException.class)
	public void shouldMaxWithNPEWhenAnElementIsNull() {
		Stream<MonetaryAmount> stream = streamNull();
		stream.reduce(MonetaryFunctions::max).get();
	}

	@Test(expectedExceptions = MonetaryException.class)
	public void shouldMaxMoneratyExceptionWhenHasDifferenctsCurrencies() {
		Stream<MonetaryAmount> stream = streamCurrencyDifferent();
		stream.reduce(MonetaryFunctions::max).get();
	}

	private Stream<MonetaryAmount> streamCurrencyDifferent() {
		Money m1 = Money.of(BigDecimal.TEN, BRAZILIAN_REAL);
		Money m2 = Money.of(BigDecimal.ZERO, BRAZILIAN_REAL);
		Money m3 = Money.of(BigDecimal.ONE, BRAZILIAN_REAL);
		Money m4 = Money.of(BigDecimal.valueOf(4L), EURO);
		Money m5 = Money.of(BigDecimal.valueOf(5L), BRAZILIAN_REAL);
		return Stream.of(m1, m2, m3, m4, m5);
	}

	private Stream<MonetaryAmount> streamNormal() {
		Money m1 = Money.of(BigDecimal.TEN, BRAZILIAN_REAL);
		Money m2 = Money.of(BigDecimal.ZERO, BRAZILIAN_REAL);
		Money m3 = Money.of(BigDecimal.ONE, BRAZILIAN_REAL);
		Money m4 = Money.of(BigDecimal.valueOf(4L), BRAZILIAN_REAL);
		Money m5 = Money.of(BigDecimal.valueOf(5L), BRAZILIAN_REAL);
		return Stream.of(m1, m2, m3, m4, m5);
	}

	private Stream<MonetaryAmount> streamNull() {
		Money m1 = Money.of(BigDecimal.TEN, BRAZILIAN_REAL);
		Money m2 = Money.of(BigDecimal.ZERO, BRAZILIAN_REAL);
		Money m3 = Money.of(BigDecimal.ONE, BRAZILIAN_REAL);
		Money m4 = Money.of(BigDecimal.valueOf(4L), BRAZILIAN_REAL);
		Money m5 = Money.of(BigDecimal.valueOf(5L), BRAZILIAN_REAL);
		return Stream.of(m1, m2, m3, m4, m5, null);
	}
}
