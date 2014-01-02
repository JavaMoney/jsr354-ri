/*
 * CREDIT SUISSE IS WILLING TO LICENSE THIS SPECIFICATION TO YOU ONLY UPON THE CONDITION THAT YOU
 * ACCEPT ALL OF THE TERMS CONTAINED IN THIS AGREEMENT. PLEASE READ THE TERMS AND CONDITIONS OF THIS
 * AGREEMENT CAREFULLY. BY DOWNLOADING THIS SPECIFICATION, YOU ACCEPT THE TERMS AND CONDITIONS OF
 * THE AGREEMENT. IF YOU ARE NOT WILLING TO BE BOUND BY IT, SELECT THE "DECLINE" BUTTON AT THE
 * BOTTOM OF THIS PAGE. Specification: JSR-354 Money and Currency API ("Specification") Copyright
 * (c) 2012-2013, Credit Suisse All rights reserved.
 */
package org.javamoney.moneta.format.internal;

import java.text.DecimalFormatSymbols;
import java.util.Arrays;
import java.util.Collection;
import java.util.Locale;

import javax.money.format.AmountFormatSymbols;
import javax.money.spi.AmountFormatSymbolsProviderSpi;
import javax.money.spi.Bootstrap;
import javax.money.spi.MonetaryLogger;

/**
 * Implementation of {@link AmountFormatSymbolsProviderSpi} providing the symbols as defined by
 * {@link DecimalFormatSymbols}.
 * 
 * @author Anatole Tresch
 */
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
					.setZeroDigit(syms.getZeroDigit()).create();
		} catch (Exception e) {
			// not supported, ignore exception
			Bootstrap.getService(MonetaryLogger.class).logWarning(
					"Unsupported format locale: " + locale);
			return null;
		}
	}

	@Override
	public Collection<Locale> getSupportedLocales() {
		return Arrays.asList(DecimalFormatSymbols.getAvailableLocales());
	}

}
