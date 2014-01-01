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
import java.util.Collections;
import java.util.Locale;
import java.util.Objects;
import java.util.Set;

import javax.money.CurrencyUnit;
import javax.money.MonetaryContext;
import javax.money.format.AmountStyle;
import javax.money.format.CurrencyStyle;
import javax.money.format.MonetaryAmountFormat;
import javax.money.format.AmountFormatSymbols;
import javax.money.spi.MonetaryAmountFormatProviderSpi;

import org.javamoney.moneta.format.internal.DefaultMonetaryAmountFormat.Builder;

/**
 * Default format provider, which mainly maps the existing JDK functionality into the JSR 354 logic.
 * 
 * @author Anatole Tresch
 */
public class DefaultAmountFormatProviderSpi implements
		MonetaryAmountFormatProviderSpi {
	/*
	 * (non-Javadoc)
	 * @see javax.money.spi.MonetaryAmountFormatProviderSpi#getFormat(java.util.Locale,
	 * javax.money.MonetaryContext, javax.money.CurrencyUnit)
	 */
	@Override
	public MonetaryAmountFormat getFormat(Locale locale,
			MonetaryContext monetaryContext, CurrencyUnit defaultCurrency) {
		Objects.requireNonNull(locale, "Locale required");
		Builder builder = new DefaultMonetaryAmountFormat.Builder(locale);
		if (defaultCurrency != null) {
			builder.setDefaultCurrency(defaultCurrency);
		}
		if (monetaryContext != null) {
			builder.setMonetaryContext(monetaryContext);
		}
		AmountStyle style = new AmountStyle.Builder(locale).build();
		builder.setStyle(style);
		return builder.build();
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * javax.money.spi.MonetaryAmountFormatProviderSpi#getFormat(javax.money.format.AmountStyle,
	 * javax.money.MonetaryContext, javax.money.CurrencyUnit)
	 */
	@Override
	public MonetaryAmountFormat getFormat(AmountStyle formatStyle,
			MonetaryContext monetaryContext, CurrencyUnit defaultCurrency) {
		Objects.requireNonNull(formatStyle, "FormatStyle required");
		Builder builder = new DefaultMonetaryAmountFormat.Builder(
				formatStyle.getLocale());
		if (defaultCurrency != null) {
			builder.setDefaultCurrency(defaultCurrency);
		}
		if (monetaryContext != null) {
			builder.setMonetaryContext(monetaryContext);
		}
		builder.setStyle(formatStyle);
		return builder.build();
	}

	@Override
	public Collection<Locale> getSupportedLocales() {
		return Arrays.asList(DecimalFormatSymbols.getAvailableLocales());
	}


}
