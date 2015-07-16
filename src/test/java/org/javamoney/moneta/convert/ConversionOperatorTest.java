package org.javamoney.moneta.convert;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;

import javax.money.CurrencyUnit;
import javax.money.Monetary;
import javax.money.MonetaryAmount;

import org.javamoney.moneta.Money;
import org.testng.annotations.Test;

public class ConversionOperatorTest {
	@Test
	public void shouldExchangeCurrencyPositiveValue() {
		CurrencyUnit real = Monetary.getCurrency("BRL");
		MonetaryAmount money = Money.parse("EUR 2.35");
		MonetaryAmount result = ConversionOperators.exchange(real).apply(money);
		assertNotNull(result);
		assertEquals(result.getCurrency(), real);
		assertEquals(Double.valueOf(2.35), result.getNumber().doubleValue());
	}

	@Test
	public void shouldExchangeCurrencyNegativeValue() {
		CurrencyUnit real = Monetary.getCurrency("BRL");
		MonetaryAmount money = Money.parse("BHD -1.345");
		MonetaryAmount result = ConversionOperators.exchange(real).apply(money);
		assertNotNull(result);
		assertEquals(result.getCurrency(), real);
		assertEquals(Double.valueOf(-1.345), result.getNumber().doubleValue());
	}
	//
	@Test(expectedExceptions = NullPointerException.class)
	public void shouldReturnErrorWhenExchangeCurrencyIsNull() {
		ConversionOperators.exchange(null);
	}
}
