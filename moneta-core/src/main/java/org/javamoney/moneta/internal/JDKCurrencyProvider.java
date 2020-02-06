/*
  Copyright (c) 2012, 2014, Credit Suisse (Anatole Tresch), Werner Keil and others by the @author tag.

  Licensed under the Apache License, Version 2.0 (the "License"); you may not
  use this file except in compliance with the License. You may obtain a copy of
  the License at

  http://www.apache.org/licenses/LICENSE-2.0

  Unless required by applicable law or agreed to in writing, software
  distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
  WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
  License for the specific language governing permissions and limitations under
  the License.
 */
package org.javamoney.moneta.internal;

import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.money.CurrencyQuery;
import javax.money.CurrencyUnit;
import javax.money.spi.CurrencyProviderSpi;

/**
 * Default implementation of a {@link CurrencyUnit} based on the using the JDK's
 * {@link Currency}.
 *
 * @version 0.5.1
 * @author Anatole Tresch
 * @author Werner Keil
 */
public class JDKCurrencyProvider implements CurrencyProviderSpi {

	/** Internal shared cache of {@link javax.money.CurrencyUnit} instances. */
    private static final Map<String, CurrencyUnit> CACHED = loadCurrencies();

    private static Map<String, CurrencyUnit> loadCurrencies() {
        Set<Currency> availableCurrencies = Currency.getAvailableCurrencies();
        Map<String, CurrencyUnit> result = new HashMap<>(availableCurrencies.size());
        for (Currency jdkCurrency : availableCurrencies) {
            CurrencyUnit cu = new JDKCurrencyAdapter(jdkCurrency);
            result.put(cu.getCurrencyCode(), cu);
        }
        return Collections.unmodifiableMap(result);
    }

    @Override
    public String getProviderName(){
        return "default";
    }

    /**
     * Return a {@link CurrencyUnit} instances matching the given
     * {@link javax.money.CurrencyContext}.
     *
     * @param currencyQuery the {@link javax.money.CurrencyContext} containing the parameters determining the query. not null.
     * @return the corresponding {@link CurrencyUnit}, or null, if no such unit
     * is provided by this provider.
     */
    public Set<CurrencyUnit> getCurrencies(CurrencyQuery currencyQuery){
        Set<CurrencyUnit> result = new HashSet<>();
        if(!currencyQuery.getCurrencyCodes().isEmpty()) {
            for (String code : currencyQuery.getCurrencyCodes()) {
                CurrencyUnit cu = CACHED.get(code);
                if (cu != null) {
                    result.add(cu);
                }
                else{
                    // try regex
                    CACHED.keySet().stream()
                            .filter(k -> k.matches(code))
                            .forEach(r -> result.add(CACHED.get(code)));
                }
            }
            return result;
        }
        if(!currencyQuery.getCountries().isEmpty()) {
            for (Locale country : currencyQuery.getCountries()) {
                CurrencyUnit cu = getCurrencyUnit(country);
                if (cu != null) {
                    result.add(cu);
                }
            }
            return result;
        }
        if(!currencyQuery.getNumericCodes().isEmpty()) {
            for (Integer numCode : currencyQuery.getNumericCodes()) {
                List<CurrencyUnit> cus = getCurrencyUnits(numCode);
                result.addAll(cus);
            }
            return result;
        }
        // No constraints defined, return all.
        result.addAll(CACHED.values());
        return result;
    }

    private List<CurrencyUnit> getCurrencyUnits(int numCode) {
        List<CurrencyUnit> result = new ArrayList<>();
        for(Currency currency: Currency.getAvailableCurrencies()){
            if(currency.getNumericCode()==numCode){
                result.add(CACHED.get(currency.getCurrencyCode()));
            }
        }
        return result;
    }


    private CurrencyUnit getCurrencyUnit(Locale locale) {
		Currency cur;
		try {
			cur = Currency.getInstance(locale);
			if (Objects.nonNull(cur)) {
				return CACHED.get(cur.getCurrencyCode());
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
