/*
 * Copyright (c) 2012, 2013, Credit Suisse (Anatole Tresch), Werner Keil.
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
 * 
 * Contributors: Anatole Tresch - initial implementation.
 */
package org.javamoney.moneta.conversion;

import java.util.ArrayList;
import java.util.List;

import javax.money.CurrencyUnit;
import javax.money.convert.ConversionContext;
import javax.money.convert.CurrencyConversion;
import javax.money.convert.ExchangeRate;
import javax.money.convert.ExchangeRateProvider;
import javax.money.convert.ProviderContext;

/**
 * This class implements a {@link ExchangeRateProvider} that delegates calls to
 * a collection of child {@link ExchangeRateProvider} instance.
 * 
 * @author Anatole Tresch
 */
public class CompoundRateProvider implements ExchangeRateProvider {
	/** The {@link ExchangeRateProvider} instances. */
	private final List<ExchangeRateProvider> providers = new ArrayList<ExchangeRateProvider>();

	private ProviderContext providerContext;

	/**
	 * Constructor.
	 * 
	 * @param providerContext
	 *            The {@link ProviderContext} this instance is providing.
	 *            Providers added must return the same on
	 *            {@link ProviderContext#getProviderName()}.
	 */
	public CompoundRateProvider(Iterable<ExchangeRateProvider> providers) {
		StringBuilder providerName = new StringBuilder("Compound: ");
		for (ExchangeRateProvider exchangeRateProvider : providers) {
			providerName.append(exchangeRateProvider.getProviderContext()
					.getProviderName());
			providerName.append(',');
		}
		providerName.setLength(providerName.length() - 1);
		ProviderContext.Builder b = new ProviderContext.Builder(
				providerName.toString());
		for (ExchangeRateProvider exchangeRateProvider : providers) {
			addProvider(exchangeRateProvider);
		}
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
	 * @see javax.money.convert.ExchangeRateProvider#getProviderContext()
	 */
	@Override
	public ProviderContext getProviderContext() {
		return providerContext;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * javax.money.convert.ExchangeRateProvider#isAvailable(javax.money.CurrencyUnit
	 * , javax.money.CurrencyUnit)
	 */
	@Override
	public boolean isAvailable(CurrencyUnit base, CurrencyUnit term) {
		for (ExchangeRateProvider prov : this.providers) {
			if (prov.isAvailable(base, term)) {
				return true;
			}
		}
		return false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * javax.money.convert.ExchangeRateProvider#isAvailable(javax.money.CurrencyUnit
	 * , javax.money.CurrencyUnit, javax.money.convert.ConversionContext)
	 */
	@Override
	public boolean isAvailable(CurrencyUnit base, CurrencyUnit term,
			ConversionContext context) {
		for (ExchangeRateProvider prov : this.providers) {
			if (prov.isAvailable(base, term, context)) {
				return true;
			}
		}
		return false;
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
	public ExchangeRate getExchangeRate(CurrencyUnit base, CurrencyUnit term,
			ConversionContext context) {
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

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * javax.money.convert.ExchangeRateProvider#getExchangeRate(javax.money.
	 * CurrencyUnit, javax.money.CurrencyUnit)
	 */
	@Override
	public ExchangeRate getExchangeRate(CurrencyUnit base, CurrencyUnit term) {
		for (ExchangeRateProvider prov : this.providers) {
			ExchangeRate rate = prov.getExchangeRate(base, term);
			if (rate != null) {
				return rate;
			}
		}
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * javax.money.convert.ExchangeRateProvider#getReversed(javax.money.convert
	 * .ExchangeRate)
	 */
	@Override
	public ExchangeRate getReversed(ExchangeRate rate) {
		for (ExchangeRateProvider prov : this.providers) {
			ExchangeRate revRate = prov.getReversed(rate);
			if (revRate != null) {
				return revRate;
			}
		}
		return null;
	}

	@Override
	public CurrencyConversion getCurrencyConversion(CurrencyUnit termCurrency) {
		return new LazyBoundCurrencyConversion(termCurrency, this, ConversionContext.of());
	}

	@Override
	public CurrencyConversion getCurrencyConversion(CurrencyUnit termCurrency,
			ConversionContext conversionContext) {
		return new LazyBoundCurrencyConversion(termCurrency, this,
				conversionContext);
	}

}