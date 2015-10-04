package org.javamoney.moneta.convert;

import javax.money.CurrencyUnit;
import javax.money.Monetary;

class ConversionConstants {
	static final CurrencyUnit EURO = Monetary.getCurrency("EUR");
	static final CurrencyUnit DOLLAR = Monetary.getCurrency("USD");
	static final CurrencyUnit BRAZILIAN_REAL = Monetary
			.getCurrency("BRL");

}
