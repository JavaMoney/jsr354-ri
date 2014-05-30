/**
 * Copyright (c) 2012, 2014, Credit Suisse (Anatole Tresch), Werner Keil and others by the @author tag.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package org.javamoney.moneta.internal;

import java.util.*;
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
public class JDKCurrencyProvider implements CurrencyProviderSpi {

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
			if (Objects.nonNull(cur)) {
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

    @Override
    public Collection<CurrencyUnit> getCurrencies(){
        return CACHED.values();
    }

}
