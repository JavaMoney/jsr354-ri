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
package org.javamoney.moneta.convert;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

import javax.money.CurrencyUnit;
import javax.money.convert.*;
import javax.money.spi.MonetaryConversionsSingletonSpi;

import org.javamoney.moneta.spi.CompoundRateProvider;

public class SEMonetaryConversionsSingletonSpi implements MonetaryConversionsSingletonSpi{

	private Map<String, ExchangeRateProvider> conversionProviders = new ConcurrentHashMap<>();

	public SEMonetaryConversionsSingletonSpi() {
		reload();
	}

    public void reload() {
		Map<String, ExchangeRateProvider> newProviders = new ConcurrentHashMap<>();
		for (ExchangeRateProvider prov : ServiceLoader
				.load(ExchangeRateProvider.class)) {
			newProviders.put(prov.getProviderContext().getProvider(), prov);
		}
		this.conversionProviders = newProviders;
	}

    @Override
    public ExchangeRateProvider getExchangeRateProvider(String... providers) {
		List<ExchangeRateProvider> provInstances = new ArrayList<>();
		for (String provName : providers) {
			provInstances.add(Optional.ofNullable(this.conversionProviders.get(provName)).orElseThrow(() -> new IllegalArgumentException(
					"Unsupported conversion/rate provider: " + provName)));
		}
		return new CompoundRateProvider(provInstances);
	}

    @Override
    public ExchangeRateProvider getExchangeRateProvider(ConversionQuery query) {
        List<ExchangeRateProvider> provInstances = new ArrayList<>();
        for (String provName : query.getProviders()) {
            provInstances.add(Optional.ofNullable(this.conversionProviders.get(provName)).orElseThrow(() -> new IllegalArgumentException(
                    "Unsupported conversion/rate provider: " + provName)));
        }
        return new CompoundRateProvider(provInstances);
    }

    @Override
    public boolean isExchangeRateProviderAvailable(ConversionQuery conversionQuery){
        for (String provName : conversionQuery.getProviders()) {
            if(this.conversionProviders.get(provName)!=null){
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean isConversionAvailable(ConversionQuery conversionQuery){
        if(conversionQuery.getTermCurrency()==null){
            return false;
        }
        for (String provName : conversionQuery.getProviders()) {
            if(this.conversionProviders.get(provName)!=null){
                if(this.conversionProviders.get(provName).isAvailable(conversionQuery)){
                    return true;
                }
            }
        }
        return false;
    }

    private Collection<ExchangeRateProvider> getExchangeRateProviders(ConversionQuery query) {
        List<ExchangeRateProvider> provInstances = new ArrayList<>();
        for (String provName : query.getProviders()) {
            provInstances.add(Optional.ofNullable(this.conversionProviders.get(provName)).orElseThrow(() -> new IllegalArgumentException(
                    "Unsupported conversion/rate provider: " + provName)));
        }
        return provInstances;
    }

	@Override
	public Set<String> getProviderNames() {
		return this.conversionProviders.keySet();
	}

	@Override
	public List<String> getDefaultProviderChain() {
		List<String> stringList = new ArrayList<>();
		stringList.add("test");
		return stringList;
	}

}
