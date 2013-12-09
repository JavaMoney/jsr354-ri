/*
 * CREDIT SUISSE IS WILLING TO LICENSE THIS SPECIFICATION TO YOU ONLY UPON THE
 * CONDITION THAT YOU ACCEPT ALL OF THE TERMS CONTAINED IN THIS AGREEMENT.
 * PLEASE READ THE TERMS AND CONDITIONS OF THIS AGREEMENT CAREFULLY. BY
 * DOWNLOADING THIS SPECIFICATION, YOU ACCEPT THE TERMS AND CONDITIONS OF THE
 * AGREEMENT. IF YOU ARE NOT WILLING TO BE BOUND BY IT, SELECT THE "DECLINE"
 * BUTTON AT THE BOTTOM OF THIS PAGE. Specification: JSR-354 Money and Currency
 * API ("Specification") Copyright (c) 2012-2013, Credit Suisse All rights
 * reserved.
 */
package org.javamoney.moneta.format;

import java.util.Locale;
import java.util.Objects;

import javax.money.CurrencyUnit;
import javax.money.MonetaryContext;
import javax.money.format.FormatStyle;
import javax.money.format.MonetaryAmountFormat;
import javax.money.spi.MonetaryAmountFormatProviderSpi;

import org.javamoney.moneta.format.DefaultMonetaryAmountFormat.Builder;

public class DefaultAmountFormatProvider implements
		MonetaryAmountFormatProviderSpi {

	@Override
	public MonetaryAmountFormat getFormat(Locale locale,
			MonetaryContext monetaryContext, CurrencyUnit defaultCurrency) {
		Objects.requireNonNull(locale, "Locale required");
		Builder builder = new DefaultMonetaryAmountFormat.Builder(locale);
		if(defaultCurrency!=null){
			builder.setDefaultCurrency(defaultCurrency);
		}
		if(monetaryContext!=null){
			builder.setMonetaryContext(monetaryContext);
		}
		FormatStyle style = new FormatStyle.Builder(locale).build();
		builder.setFormatStyle(style);
		return builder.build();
	}

	@Override
	public MonetaryAmountFormat getFormat(FormatStyle formatStyle,
			MonetaryContext monetaryContext, CurrencyUnit defaultCurrency) {
		Objects.requireNonNull(formatStyle, "FormatStyle required");
		Builder builder = new DefaultMonetaryAmountFormat.Builder(formatStyle.getLocale());
		if(defaultCurrency!=null){
			builder.setDefaultCurrency(defaultCurrency);
		}
		if(monetaryContext!=null){
			builder.setMonetaryContext(monetaryContext);
		}
		builder.setFormatStyle(formatStyle);
		return builder.build();
	}

}
