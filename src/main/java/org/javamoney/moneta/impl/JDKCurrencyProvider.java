/*
 * CREDIT SUISSE IS WILLING TO LICENSE THIS SPECIFICATION TO YOU ONLY UPON THE
 * CONDITION THAT YOU ACCEPT ALL OF THE TERMS CONTAINED IN THIS AGREEMENT.
 * PLEASE READ THE TERMS AND CONDITIONS OF THIS AGREEMENT CAREFULLY. BY
 * DOWNLOADING THIS SPECIFICATION, YOU ACCEPT THE TERMS AND CONDITIONS OF THE
 * AGREEMENT. IF YOU ARE NOT WILLING TO BE BOUND BY IT, SELECT THE "DECLINE"
 * BUTTON AT THE BOTTOM OF THIS PAGE.
 * 
 * Specification: JSR-354 Money and Currency API ("Specification")
 * 
 * Copyright (c) 2012-2013, Credit Suisse All rights reserved.
 */
package org.javamoney.moneta.impl;

import java.util.Currency;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.money.CurrencyUnit;
import javax.money.spi.CurrencyProviderSpi;

/**
 * Default implementation of a {@link CurrencyUnit} based on the using the JDK's
 * {@link MoneyCurrency}, but also extendable using a {@link Builder} instance.
 * 
 * @version 0.5.1
 * @author Anatole Tresch
 * @author Werner Keil
 */
public final class JDKCurrencyProvider implements CurrencyProviderSpi {

	/** Internal shared cache of {@link MoneyCurrency} instances. */
	private static final Map<String, CurrencyUnit> CACHED = new HashMap<String, CurrencyUnit>();

	public JDKCurrencyProvider() {
		for (Currency jdkCurrency : Currency.getAvailableCurrencies()) {
			CurrencyUnit cu = new JDKCurrencyAdapter(jdkCurrency);
			CACHED.put(cu.getCurrencyCode(), cu);
		}
	}

	@Override
	public CurrencyUnit getCurrencyUnit(String currencyCode) {
		return CACHED.get(currencyCode);
	}

	@Override
	public CurrencyUnit getCurrencyUnit(Locale locale) {
		Currency cur = null;
		try {
			cur = Currency.getInstance(locale);
			if(cur!=null){
				return getCurrencyUnit(cur.getCurrencyCode());
			}
		} catch (Exception e) {
			if (Logger.getLogger(getClass().getName()).isLoggable(Level.FINEST)) {
				Logger.getLogger(getClass().getName()).finest(
						"No currency for locale found: " + locale);
			}
		}
		return null;
	}

}
