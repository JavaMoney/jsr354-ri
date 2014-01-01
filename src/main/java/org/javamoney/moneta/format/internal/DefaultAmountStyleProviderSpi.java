package org.javamoney.moneta.format.internal;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Arrays;
import java.util.Collection;
import java.util.Locale;

import javax.money.format.AmountFormatSymbols;
import javax.money.format.AmountStyle;
import javax.money.format.CurrencyStyle;
import javax.money.spi.AmountStyleProviderSpi;

public class DefaultAmountStyleProviderSpi implements
		AmountStyleProviderSpi {

	@Override
	public AmountStyle getAmountStyle(Locale locale) {
		DecimalFormat df = (DecimalFormat) DecimalFormat
				.getCurrencyInstance(locale);
		return new AmountStyle.Builder(locale)
				.setNumberGroupSizes(df.getGroupingSize())
				.setPattern(df.toPattern())
				.setCurrencyStyle(CurrencyStyle.CODE)
				.setSymbols(AmountFormatSymbols.getInstance(locale))
				.build();
	}

	@Override
	public Collection<Locale> getSupportedLocales() {
		return Arrays.asList(DecimalFormatSymbols.getAvailableLocales());
	}

}
