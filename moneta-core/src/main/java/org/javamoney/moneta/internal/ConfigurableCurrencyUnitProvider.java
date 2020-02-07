/*
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

import javax.money.CurrencyQuery;
import javax.money.CurrencyUnit;
import javax.money.spi.CurrencyProviderSpi;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Pattern;

/**
 * This class provides a programmatic singleton for globally registering new {@link java.util.Currency}  into the
 * {@link javax.money.Monetary} singleton either by currency code, locale, or both.
 */
public class ConfigurableCurrencyUnitProvider implements CurrencyProviderSpi {
    /**
     * The currency units, identified by currency code.
     */
    private static final Map<String, CurrencyUnit> CURRENCY_UNITS = new ConcurrentHashMap<>();
    /**
     * The currency units, identified by numeric code.
     */
    private static final Map<Integer, CurrencyUnit> CURRENCY_UNITS_BY_NUMERIC_CODE = new ConcurrentHashMap<>();
    /**
     * The currency units identified by Locale.
     */
    private static final Map<Locale, CurrencyUnit> CURRENCY_UNITS_BY_LOCALE = new ConcurrentHashMap<>();


    /**
     * Return a {@link CurrencyUnit} instances matching the given
     * {@link javax.money.CurrencyContext}.
     *
     * @param currencyQuery the {@link javax.money.CurrencyQuery} containing the parameters determining the query. not null.
     * @return the corresponding {@link CurrencyUnit}, or null, if no such unit
     * is provided by this provider.
     */
    public Set<CurrencyUnit> getCurrencies(CurrencyQuery currencyQuery) {
        Set<CurrencyUnit> result = new HashSet<>(CURRENCY_UNITS.size());
        if (currencyQuery.get(LocalDateTime.class) != null || currencyQuery.get(LocalDate.class) != null) {
            return Collections.emptySet();
        }
        if (!currencyQuery.getCurrencyCodes().isEmpty()) {
            for (String code : currencyQuery.getCurrencyCodes()) {
                CurrencyUnit cu = CURRENCY_UNITS.get(code);
                if (cu != null) {
                    result.add(cu);
                }
                else{
                    Pattern pattern = Pattern.compile(code);
                    CURRENCY_UNITS.keySet().stream()
                            .filter(k -> pattern.matcher(k).matches())
                            .forEach(r -> result.add(CURRENCY_UNITS.get(r)));
                }
            }
            return result;
        }
        if (!currencyQuery.getCountries().isEmpty()) {
            for (Locale locale : currencyQuery.getCountries()) {
                CurrencyUnit cu = CURRENCY_UNITS_BY_LOCALE.get(locale);
                if (cu != null) {
                    result.add(cu);
                }
            }
            return result;
        }
        if (!currencyQuery.getNumericCodes().isEmpty()) {
            for (Integer numericCode : currencyQuery.getNumericCodes()) {
                CurrencyUnit cu = CURRENCY_UNITS_BY_NUMERIC_CODE.get(numericCode);
                if (cu != null) {
                    result.add(cu);
                }
            }
            return result;
        }
        result.addAll(CURRENCY_UNITS.values());
        return result;
    }

    /**
     * Registers a new currency unit under its currency code and potentially numeric code.
     *
     * @param currencyUnit the new currency to be registered, not null.
     * @return any unit instance registered previously by this instance, or null.
     */
    public static CurrencyUnit registerCurrencyUnit(CurrencyUnit currencyUnit) {
        Objects.requireNonNull(currencyUnit);
        CurrencyUnit registered = ConfigurableCurrencyUnitProvider.CURRENCY_UNITS.put(currencyUnit.getCurrencyCode(), currencyUnit);
        int numericCode = currencyUnit.getNumericCode();
        if (numericCode != -1) {
            ConfigurableCurrencyUnitProvider.CURRENCY_UNITS_BY_NUMERIC_CODE.put(numericCode, currencyUnit);
        }
        return registered;
    }

    /**
     * Registers a new currency unit under the given Locale.
     *
     * @param currencyUnit the new currency to be registered, not null.
     * @param locale       the Locale, not null.
     * @return any unit instance registered previously by this instance, or null.
     */
    public static CurrencyUnit registerCurrencyUnit(CurrencyUnit currencyUnit, Locale locale) {
        Objects.requireNonNull(locale);
        Objects.requireNonNull(currencyUnit);
        return ConfigurableCurrencyUnitProvider.CURRENCY_UNITS_BY_LOCALE.put(locale, currencyUnit);
    }

    /**
     * Removes a CurrencyUnit.
     *
     * @param currencyCode the currency code, not null.
     * @return any unit instance removed, or null.
     */
    public static CurrencyUnit removeCurrencyUnit(String currencyCode) {
        Objects.requireNonNull(currencyCode);
        CurrencyUnit removed = ConfigurableCurrencyUnitProvider.CURRENCY_UNITS.remove(currencyCode);
        if (removed != null) {
            int numericCode = removed.getNumericCode();
            if (numericCode != -1) {
                ConfigurableCurrencyUnitProvider.CURRENCY_UNITS_BY_NUMERIC_CODE.remove(numericCode);
            }
        }
        return removed;
    }

    /**
     * Removes a CurrencyUnit.
     *
     * @param locale the Locale, not null.
     * @return any unit instance removed, or null.
     */
    public static CurrencyUnit removeCurrencyUnit(Locale locale) {
        Objects.requireNonNull(locale);
        return ConfigurableCurrencyUnitProvider.CURRENCY_UNITS_BY_LOCALE.remove(locale);
    }

    /*
     * (non-Javadoc)
     *
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return "ConfigurableCurrencyUnitProvider [CURRENCY_UNITS=" + CURRENCY_UNITS
                        + ", CURRENCY_UNITS_BY_NUMERIC_CODE=" + CURRENCY_UNITS_BY_NUMERIC_CODE
                        + ", CURRENCY_UNITS_BY_LOCALE=" + CURRENCY_UNITS_BY_LOCALE + ']';
    }

}
