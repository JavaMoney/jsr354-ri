package org.javamoney.moneta.format;

import static org.testng.Assert.assertEquals;

import java.util.Locale;

import javax.money.CurrencyUnit;
import javax.money.Monetary;

import org.testng.annotations.Test;

public class MonetaryAmountFormatSymbolsTest {

	@Test
	public void shouldReturnDefaultCurrency() {
		MonetaryAmountFormatSymbols symbols = new MonetaryAmountFormatSymbols();
		CurrencyUnit currency = Monetary.getCurrency(Locale.getDefault(Locale.Category.FORMAT));
		assertEquals(currency, symbols.getCurrency());
	}

	@Test
	public void shouldReturnCurrencyFromLocale() {
		MonetaryAmountFormatSymbols symbols = new MonetaryAmountFormatSymbols(Locale.US);
		CurrencyUnit currency = Monetary.getCurrency(Locale.US);
		assertEquals(currency, symbols.getCurrency());
	}

	@Test(expectedExceptions = NullPointerException.class)
	public void shouldReturnNullPointerWhenCurrencyIsNull() {
		MonetaryAmountFormatSymbols symbols = new MonetaryAmountFormatSymbols(Locale.US);
		symbols.setCurrency(null);
	}

	@Test
	public void shouldSetCurrency() {
		CurrencyUnit real = Monetary.getCurrency("BRL");
		MonetaryAmountFormatSymbols symbols = new MonetaryAmountFormatSymbols(Locale.US);
		symbols.setCurrency(real);
		assertEquals(real, symbols.getCurrency());
	}

	@Test(expectedExceptions = IllegalArgumentException.class)
	public void shouldReturnErrorWhenInternationalCurrencySymbolIsInvalid() {
		MonetaryAmountFormatSymbols symbols = new MonetaryAmountFormatSymbols(Locale.US);
		symbols.setInternationalCurrencySymbol("ERROR");
	}

	@Test(expectedExceptions = NullPointerException.class)
	public void shouldReturnErrorWhenInternationalCurrencySymbolIsNull() {
		MonetaryAmountFormatSymbols symbols = new MonetaryAmountFormatSymbols(Locale.US);
		symbols.setInternationalCurrencySymbol(null);
	}

}
