package org.javamoney.moneta.format.internal;

import java.text.DecimalFormatSymbols;
import java.util.Arrays;
import java.util.Collection;
import java.util.Locale;

import javax.money.format.AmountFormatSymbols;
import javax.money.spi.AmountFormatSymbolsProviderSpi;

public class DefaultAmountFormatSymbolsProviderSpi implements
		AmountFormatSymbolsProviderSpi {

	@Override
	public AmountFormatSymbols getAmountFormatSymbols(Locale locale) {
		try {
			DecimalFormatSymbols syms = DecimalFormatSymbols
					.getInstance(locale);
			return new AmountFormatSymbols.Builder(locale)
					.setDecimalSeparator(syms.getDecimalSeparator())
					.setDigit(syms.getDigit())
					.setExponentialSeparator(syms.getExponentSeparator())
					.setGroupingSeparator(syms.getGroupingSeparator())
					.setMinusSign(syms.getMinusSign())
					.setPatternSeparator(syms.getPatternSeparator())
					.setZeroDigit(syms.getZeroDigit()).build();
		} catch (Exception e) {
			// not supported, ignore exception
			return null;
		}
	}

	@Override
	public Collection<Locale> getSupportedLocales() {
		return Arrays.asList(DecimalFormatSymbols.getAvailableLocales());
	}

}
