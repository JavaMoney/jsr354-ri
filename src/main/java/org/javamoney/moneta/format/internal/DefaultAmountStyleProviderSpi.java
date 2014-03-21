/*
 * CREDIT SUISSE IS WILLING TO LICENSE THIS SPECIFICATION TO YOU ONLY UPON THE CONDITION THAT YOU
 * ACCEPT ALL OF THE TERMS CONTAINED IN THIS AGREEMENT. PLEASE READ THE TERMS AND CONDITIONS OF THIS
 * AGREEMENT CAREFULLY. BY DOWNLOADING THIS SPECIFICATION, YOU ACCEPT THE TERMS AND CONDITIONS OF
 * THE AGREEMENT. IF YOU ARE NOT WILLING TO BE BOUND BY IT, SELECT THE "DECLINE" BUTTON AT THE
 * BOTTOM OF THIS PAGE. Specification: JSR-354 Money and Currency API ("Specification") Copyright
 * (c) 2012-2013, Credit Suisse All rights reserved.
 */
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

/**
 * Implementation of {@link AmountStyleProviderSpi} based on the corresponding {@link DecimalFormat}
 * .
 * 
 * @author Anatole Tresch
 */
public class DefaultAmountStyleProviderSpi implements
		AmountStyleProviderSpi {
	/*
	 * (non-Javadoc)
	 * @see javax.money.spi.AmountStyleProviderSpi#getAmountStyle(java.util.Locale)
	 */
	@Override
	public AmountStyle getAmountStyle(Locale locale) {
		DecimalFormat df = (DecimalFormat) DecimalFormat
				.getCurrencyInstance(locale);
		return new AmountStyle.Builder(locale)
				.setGroupingSizes(df.getGroupingSize())
				.setPattern(df.toPattern())
				.setCurrencyStyle(CurrencyStyle.CODE)
				.setSymbols(AmountFormatSymbols.of(locale))
				.create();
	}

	/*
	 * (non-Javadoc)
	 * @see javax.money.spi.AmountStyleProviderSpi#getSupportedLocales()
	 */
	@Override
	public Collection<Locale> getSupportedLocales() {
		return Arrays.asList(DecimalFormatSymbols.getAvailableLocales());
	}

}
