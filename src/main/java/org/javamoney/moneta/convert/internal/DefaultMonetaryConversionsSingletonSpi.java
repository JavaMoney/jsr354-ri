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
package org.javamoney.moneta.convert.internal;

import org.javamoney.moneta.spi.CompoundRateProvider;
import org.javamoney.moneta.spi.MonetaryConfig;

import javax.money.CurrencyUnit;
import javax.money.convert.ConversionContext;
import javax.money.convert.CurrencyConversion;
import javax.money.convert.ExchangeRateProvider;
import javax.money.convert.ProviderContext;
import javax.money.spi.Bootstrap;
import javax.money.spi.MonetaryConversionsSingletonSpi;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;

/**
 * This is the default implementation of the {@link javax.money.spi.MonetaryConversionsSingletonSpi} interface, backing
 * up the {@link javax.money.convert.MonetaryConversions} singleton.
 */
public class DefaultMonetaryConversionsSingletonSpi implements MonetaryConversionsSingletonSpi{
    /**
     * Logger used.
     */
    private static final Logger LOG = Logger.getLogger(DefaultMonetaryConversionsSingletonSpi.class.getName());

    /**
     * The providers loaded.
     */
    private Map<String,ExchangeRateProvider> conversionProviders = new ConcurrentHashMap<>();

    /**
     * Constructors, loads the providers from the {@link javax.money.spi.Bootstrap} component.
     */
    public DefaultMonetaryConversionsSingletonSpi(){
        reload();
    }

    /**
     * Reloads/reinitializes the providers found.
     */
    public void reload(){
        Map<String,ExchangeRateProvider> newProviders = new ConcurrentHashMap<>();
        for(ExchangeRateProvider prov : Bootstrap.getServices(ExchangeRateProvider.class)){
            newProviders.put(prov.getProviderContext().getProvider(), prov);
        }
        this.conversionProviders = newProviders;
    }

    @Override
    public ExchangeRateProvider getExchangeRateProvider(String... providers){
        List<ExchangeRateProvider> provInstances = new ArrayList<>();
        for(String provName : providers){
            ExchangeRateProvider prov = this.conversionProviders.get(provName);
            if(prov == null){
                throw new IllegalArgumentException("Unsupported conversion/rate provider: " + provName);
            }
            provInstances.add(prov);
        }
        return new CompoundRateProvider(provInstances);
    }

    @Override
    public Set<String> getProviderNames(){
        return this.conversionProviders.keySet();
    }

    @Override
    public boolean isProviderAvailable(String provider){
        return conversionProviders.containsKey(provider);
    }

    @Override
    public CurrencyConversion getConversion(CurrencyUnit termCurrency, ConversionContext conversionContext,
                                            String... providers){
        return getExchangeRateProvider(providers).getCurrencyConversion(termCurrency);
    }

    @Override
    public List<String> getDefaultProviderChain(){
        List<String> provList = new ArrayList<>();
        String defaultChain = MonetaryConfig.getConfig().get("conversion.default-chain");
        String[] items = defaultChain.split(",");
        for(String item : items){
            if(isProviderAvailable(item.trim())){
                provList.add(item);
            }else{
                LOG.warning("Ignoring non existing default provider: " + item);
            }
        }
        return provList;
    }

    @Override
    public ProviderContext getProviderContext(String provider){
        ExchangeRateProvider prov = getExchangeRateProvider(provider);
        return prov.getProviderContext();
    }

}
