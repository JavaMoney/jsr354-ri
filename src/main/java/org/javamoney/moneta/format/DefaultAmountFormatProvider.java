package org.javamoney.moneta.format;

import java.util.Locale;

import javax.money.CurrencyUnit;
import javax.money.MonetaryContext;
import javax.money.format.FormatStyle;
import javax.money.format.MonetaryAmountFormat;
import javax.money.spi.MonetaryAmountFormatProviderSpi;

public class DefaultAmountFormatProvider implements
		MonetaryAmountFormatProviderSpi {

	@Override
	public MonetaryAmountFormat getFormat(Locale locale,
			FormatStyle formatStyle, MonetaryContext monetaryContext,
			CurrencyUnit defaultCurrency) {
//		return new DefaultMonetaryAmountFormat.DefaultBuilder(locale)
//				.setDefaultCurrency(defaultCurrency).build();
//		;
		return null;
	}

}
