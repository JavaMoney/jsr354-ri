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
package org.javamoney.moneta.spi;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.money.CurrencyUnit;
import javax.money.convert.*;

/**
 * This class implements a {@link ExchangeRateProvider} that delegates calls to
 * a collection of child {@link ExchangeRateProvider} instance.
 * 
 * @author Anatole Tresch
 */
public class CompoundRateProvider extends AbstractRateProvider {
	/** The {@link ExchangeRateProvider} instances. */
	private final List<ExchangeRateProvider> providers = new ArrayList<ExchangeRateProvider>();

	/**
	 * Constructor.
	 * 
	 * @param providerContext
	 *            The {@link ProviderContext} this instance is providing.
	 *            Providers added must return the same on
	 *            {@link ProviderContext#getProvider()}.
	 */
	public CompoundRateProvider(Iterable<ExchangeRateProvider> providers) {
		super(createContext(providers));
		for (ExchangeRateProvider exchangeRateProvider : providers) {
			addProvider(exchangeRateProvider);
		}
	}

	private static ProviderContext createContext(
			Iterable<ExchangeRateProvider> providers) {
        Set<RateType> rateTypeSet = new HashSet<>();
		StringBuilder providerName = new StringBuilder("Compound: ");
		for (ExchangeRateProvider exchangeRateProvider : providers) {
			providerName.append(exchangeRateProvider.getProviderContext()
					.getProvider());
			providerName.append(',');
            rateTypeSet.addAll(exchangeRateProvider.getProviderContext().getRateTypes());
		}
		providerName.setLength(providerName.length() - 1);

		return new ProviderContext.Builder(providerName.toString(),rateTypeSet).build();
	}

	/**
	 * Add an additional {@link ExchangeRateProvider} to the instance's delegate
	 * list. Hereby {@link ExchangeRateProvider#getExchangeRateType()} of the
	 * provider added must be equal to {@link #getExchangeRateType()}.
	 * 
	 * @param prov
	 *            The {@link ExchangeRateProvider} to be added, not {@code null}
	 *            .
	 * @throws IllegalArgumentException
	 *             if {@link ExchangeRateProvider#getExchangeRateType()} of the
	 *             provider added is not equal to {@link #getExchangeRateType()}
	 *             .
	 */
	private void addProvider(ExchangeRateProvider prov) {
		if (prov == null) {
			throw new IllegalArgumentException("ConversionProvider required.");
		}
		providers.add(prov);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * javax.money.convert.ExchangeRateProvider#getExchangeRate(javax.money.
	 * CurrencyUnit, javax.money.CurrencyUnit,
	 * javax.money.convert.ConversionContext)
	 */
	@Override
	protected ExchangeRate getExchangeRateInternal(CurrencyUnit base,
			CurrencyUnit term, ConversionContext context) {
		for (ExchangeRateProvider prov : this.providers) {
			if (prov.isAvailable(base, term, context)) {
				ExchangeRate rate = prov.getExchangeRate(base, term, context);
				if (rate != null) {
					return rate;
				}
			}
		}
		return null;
	}

}