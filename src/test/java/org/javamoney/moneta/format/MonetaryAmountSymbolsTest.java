package org.javamoney.moneta.format;

import static org.testng.Assert.assertEquals;

import java.util.Locale;

import javax.money.CurrencyUnit;
import javax.money.Monetary;

import org.testng.annotations.Test;

public class MonetaryAmountSymbolsTest {

	@Test
	public void shouldReturnDefaultCurrency() {
		MonetaryAmountSymbols symbols = new MonetaryAmountSymbols();
		CurrencyUnit currency = Monetary.getCurrency(Locale.getDefault(Locale.Category.FORMAT));
		assertEquals(currency, symbols.getCurrency());
	}

	@Test
	public void shouldReturnCurrencyFromLocale() {
		MonetaryAmountSymbols symbols = new MonetaryAmountSymbols(Locale.US);
		CurrencyUnit currency = Monetary.getCurrency(Locale.US);
		assertEquals(currency, symbols.getCurrency());
	}

	@Test(expectedExceptions = NullPointerException.class)
	public void shouldReturnNullPointerWhenCurrencyIsNull() {
		MonetaryAmountSymbols symbols = new MonetaryAmountSymbols(Locale.US);
		symbols.setCurrency(null);
	}

	@Test
	public void shouldSetCurrency() {
		CurrencyUnit real = Monetary.getCurrency("BRL");
		MonetaryAmountSymbols symbols = new MonetaryAmountSymbols(Locale.US);
		symbols.setCurrency(real);
		assertEquals(real, symbols.getCurrency());
	}

	@Test(expectedExceptions = IllegalArgumentException.class)
	public void shouldReturnErrorWhenInternationalCurrencySymbolIsInvalid() {
		MonetaryAmountSymbols symbols = new MonetaryAmountSymbols(Locale.US);
		symbols.setInternationalCurrencySymbol("ERROR");
	}

	@Test(expectedExceptions = NullPointerException.class)
	public void shouldReturnErrorWhenInternationalCurrencySymbolIsNull() {
		MonetaryAmountSymbols symbols = new MonetaryAmountSymbols(Locale.US);
		symbols.setInternationalCurrencySymbol(null);
	}

}
