/*
 * CREDIT SUISSE IS WILLING TO LICENSE THIS SPECIFICATION TO YOU ONLY UPON THE CONDITION THAT YOU
 * ACCEPT ALL OF THE TERMS CONTAINED IN THIS AGREEMENT. PLEASE READ THE TERMS AND CONDITIONS OF THIS
 * AGREEMENT CAREFULLY. BY DOWNLOADING THIS SPECIFICATION, YOU ACCEPT THE TERMS AND CONDITIONS OF
 * THE AGREEMENT. IF YOU ARE NOT WILLING TO BE BOUND BY IT, SELECT THE "DECLINE" BUTTON AT THE
 * BOTTOM OF THIS PAGE. Specification: JSR-354 Money and Currency API ("Specification") Copyright
 * (c) 2012-2013, Credit Suisse All rights reserved.
 */
package org.javamoney.moneta.format.internal;

import java.util.Collections;
import java.util.Locale;
import java.util.Objects;
import java.util.Set;

import javax.money.format.AmountFormatContext;
import javax.money.format.MonetaryAmountFormat;
import javax.money.spi.MonetaryAmountFormatProviderSpi;

/**
 * Default format provider, which mainly maps the existing JDK functionality into the JSR 354 logic.
 * 
 * @author Anatole Tresch
 */
public class DefaultAmountFormatProviderSpi implements
		MonetaryAmountFormatProviderSpi {

    @Override
    public String getStyleId(){
        return "default";
    }

    /*
         * (non-Javadoc)
         * @see
         * javax.money.spi.MonetaryAmountFormatProviderSpi#getFormat(javax.money.format.AmountFormatContext)
         */
	@Override
	public MonetaryAmountFormat getAmountFormat(AmountFormatContext style) {
		Objects.requireNonNull(style, "AmountFormatContext required");
		return new DefaultMonetaryAmountFormat(style);
	}

    @Override
    public Set<Locale> getAvailableLocales(){
        return Collections.emptySet();
    }

}
