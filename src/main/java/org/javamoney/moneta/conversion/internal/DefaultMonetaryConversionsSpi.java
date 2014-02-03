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
package org.javamoney.moneta.conversion.internal;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.ServiceLoader;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import javax.money.CurrencyUnit;
import javax.money.convert.ConversionContext;
import javax.money.convert.CurrencyConversion;
import javax.money.convert.ExchangeRateProvider;
import javax.money.convert.ProviderContext;
import javax.money.spi.MonetaryConversionsSpi;

import org.javamoney.moneta.conversion.CompoundRateProvider;

public class DefaultMonetaryConversionsSpi implements MonetaryConversionsSpi {

	private Map<String, ExchangeRateProvider> conversionProviders = new ConcurrentHashMap<>();

	public DefaultMonetaryConversionsSpi() {
		reload();
	}

	public void reload() {
		Map<String, ExchangeRateProvider> newProviders = new ConcurrentHashMap<>();
		for (ExchangeRateProvider prov : ServiceLoader
				.load(ExchangeRateProvider.class)) {
			newProviders.put(prov.getProviderContext().getProviderName(), prov);
		}
		this.conversionProviders = newProviders;
	}

	@Override
	public ExchangeRateProvider getExchangeRateProvider(String... providers) {
		List<ExchangeRateProvider> provInstances = new ArrayList<>();
		for (String provName : providers) {
			ExchangeRateProvider prov = this.conversionProviders.get(provName);
			if (prov == null) {
				throw new IllegalArgumentException(
						"Unsupported conversion/rate provider: " + provName);
			}
			provInstances.add(prov);
		}
		return new CompoundRateProvider(provInstances);
	}

	@Override
	public Set<String> getProviderNames() {
		return this.conversionProviders.keySet();
	}

	@Override
	public boolean isProviderAvailable(String provider) {
		return conversionProviders.containsKey(provider);
	}

	@Override
	public CurrencyConversion getConversion(CurrencyUnit termCurrency,
			ConversionContext conversionContext, String... providers) {
		return getExchangeRateProvider(providers).getCurrencyConversion(
				termCurrency);
	}

	@Override
	public List<String> getDefaultProviderChain() {
		List<String> stringList = new ArrayList<>();
		stringList.add("test");
		return stringList;
	}

	@Override
	public ProviderContext getProviderContext(String provider) {
		ExchangeRateProvider prov = getExchangeRateProvider(provider);
		return prov.getProviderContext();
	}

}
