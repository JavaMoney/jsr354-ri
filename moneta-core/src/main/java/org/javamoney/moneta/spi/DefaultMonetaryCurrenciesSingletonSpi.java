/*
 * Copyright (c) 2012, 2019, Credit Suisse (Anatole Tresch), Werner Keil and others by the @author tag.
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

import javax.money.CurrencyQuery;
import javax.money.CurrencyQueryBuilder;
import javax.money.CurrencyUnit;
import javax.money.spi.Bootstrap;
import javax.money.spi.CurrencyProviderSpi;
import javax.money.spi.MonetaryCurrenciesSingletonSpi;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Factory singleton for {@link javax.money.CurrencyUnit} instances as provided by the
 * different registered {@link javax.money.spi.CurrencyProviderSpi} instances.
 * <p/>
 * This class is thread safe.
 *
 * @author Anatole Tresch
 */
public class DefaultMonetaryCurrenciesSingletonSpi implements MonetaryCurrenciesSingletonSpi {

    @Override
    public Set<CurrencyUnit> getCurrencies(CurrencyQuery query) {
        Set<CurrencyUnit> result = new HashSet<>();
        List<CurrencyProviderSpi> providers = collectProviders(query);
        for (CurrencyProviderSpi spi : providers) {
            try {
                result.addAll(spi.getCurrencies(query));
            } catch (Exception e) {
                Logger.getLogger(DefaultMonetaryCurrenciesSingletonSpi.class.getName())
                        .log(Level.SEVERE, "Error loading currency provider names for " + spi.getClass().getName(),
                                e);
            }
        }
        return result;
    }

    @Override
    public boolean isCurrencyAvailable(String code, String... providers) {
        return isCurrencyAvailable(CurrencyQueryBuilder.of().setCurrencyCodes(code).setProviderNames(providers).build());
    }

    @Override
    public boolean isCurrencyAvailable(Locale locale, String... providers) {
        return isCurrencyAvailable(CurrencyQueryBuilder.of().setCountries(locale).setProviderNames(providers).build());
    }

    private boolean isCurrencyAvailable(CurrencyQuery query) {
        for (CurrencyProviderSpi provider : collectProviders(query)) {
            if (provider.isCurrencyAvailable(query)) {
                return true;
            }
        }
        return false;
    }

    private List<CurrencyProviderSpi> collectProviders(CurrencyQuery query) {
        List<CurrencyProviderSpi> result = new ArrayList<>();
        if (!query.getProviderNames().isEmpty()) {
            for (String providerName : query.getProviderNames()) {
                CurrencyProviderSpi provider = getProvider(providerName);
                if (provider == null) {
                    Logger.getLogger(DefaultMonetaryCurrenciesSingletonSpi.class.getName()).warning("No such currency " +
                            "provider found, ignoring: " + providerName);
                } else {
                    result.add(provider);
                }
            }
        }
        else{
            for(String providerName:getDefaultProviderChain()){
                CurrencyProviderSpi provider = getProvider(providerName);
                if (provider == null) {
                    Logger.getLogger(DefaultMonetaryCurrenciesSingletonSpi.class.getName()).warning("No such currency " +
                            "provider found, ignoring: " + providerName);
                } else {
                    result.add(provider);
                }
            }
        }
        return result;
    }

    private CurrencyProviderSpi getProvider(String providerName) {
        for(CurrencyProviderSpi provider:Bootstrap.getServices(CurrencyProviderSpi.class)){
            if(provider.getProviderName().equals(providerName)){
                return provider;
            }
        }
        return null;
    }

    /**
     * This default implementation simply returns all providers defined in arbitrary order.
     *
     * @return the default provider chain, never null.
     */
    @Override
    public List<String> getDefaultProviderChain() {
        List<String> provList = new ArrayList<>();
        String defaultChain = MonetaryConfig.getConfig().get("currencies.default-chain");
        if(defaultChain!=null) {
            String[] items = defaultChain.split(",");
            for (String item : items) {
                if (getProviderNames().contains(item.trim())) {
                    provList.add(item);
                } else {
                    Logger.getLogger(getClass().getName())
                            .warning("Ignoring non existing default provider: " + item);
                }
            }
        }
        else{
            Bootstrap.getServices(CurrencyProviderSpi.class).forEach(
                    p -> provList.add(p.getProviderName())
            );
        }
        return provList;
    }

    /**
     * Get the names of the currently loaded providers.
     *
     * @return the names of the currently loaded providers, never null.
     */
    @Override
    public Set<String> getProviderNames() {
        Set<String> result = new HashSet<>();
        for (CurrencyProviderSpi spi : Bootstrap.getServices(CurrencyProviderSpi.class)) {
            try {
                result.add(spi.getProviderName());
            } catch (Exception e) {
                Logger.getLogger(DefaultMonetaryCurrenciesSingletonSpi.class.getName())
                        .log(Level.SEVERE, "Error loading currency provider names for " + spi.getClass().getName(),
                                e);
            }
        }
        return result;
    }

}
