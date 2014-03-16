/*
 * CREDIT SUISSE IS WILLING TO LICENSE THIS SPECIFICATION TO YOU ONLY UPON THE
 * CONDITION THAT YOU ACCEPT ALL OF THE TERMS CONTAINED IN THIS AGREEMENT.
 * PLEASE READ THE TERMS AND CONDITIONS OF THIS AGREEMENT CAREFULLY. BY
 * DOWNLOADING THIS SPECIFICATION, YOU ACCEPT THE TERMS AND CONDITIONS OF THE
 * AGREEMENT. IF YOU ARE NOT WILLING TO BE BOUND BY IT, SELECT THE "DECLINE"
 * BUTTON AT THE BOTTOM OF THIS PAGE. Specification: JSR-354 Money and Currency
 * API ("Specification") Copyright (c) 2012-2013, Credit Suisse All rights
 * reserved.
 */
package org.javamoney.moneta.internal;

import javax.money.CurrencyUnit;
import javax.money.spi.CurrencyProviderSpi;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

/**
 * This class provides a programmatic singleton for globally registering new {@link java.util.Currency}  into the
 * {@link javax.money.MonetaryCurrencies} singleton either by currency code, locale, or both.
 */
public class ConfigurableCurrencyUnitProvider implements CurrencyProviderSpi{
    /** The currency units, identified by currency code. */
    private static Map<String,CurrencyUnit> currencyUnits = new ConcurrentHashMap<>();
    /** The currency units identified by Locale. */
    private static Map<Locale,CurrencyUnit> currencyUnitsByLocale = new ConcurrentHashMap<>();

    @Override
    public CurrencyUnit getCurrencyUnit(String currencyCode){
        return currencyUnits.get(currencyCode);
    }

    @Override
    public CurrencyUnit getCurrencyUnit(Locale locale){
        return currencyUnitsByLocale.get(locale);
    }

    /**
     * Registers a bew currency unit under its currency code.
     * @param currencyUnit the new currency to be registered, not null.
     * @return any unit instance registered previously by this instance, or null.
     */
    public static CurrencyUnit registerCurrencyUnit(CurrencyUnit currencyUnit){
        Objects.requireNonNull(currencyUnit);
        return ConfigurableCurrencyUnitProvider.currencyUnits.put(currencyUnit.getCurrencyCode(), currencyUnit);
    }

    /**
     * Registers a bew currency unit under the given Locale.
     * @param currencyUnit the new currency to be registered, not null.
     * @param locale
     * @return any unit instance registered previously by this instance, or null.
     */
    public static CurrencyUnit registerCurrencyUnit(CurrencyUnit currencyUnit, Locale locale){
        Objects.requireNonNull(locale);
        Objects.requireNonNull(currencyUnit);
        return ConfigurableCurrencyUnitProvider.currencyUnitsByLocale.put(locale, currencyUnit);
    }

    /**
     * Removes a CurrencyUnit.
     * @param currencyCode the currency code, not null.
     * @return any unit instance removed, or null.
     */
    public static CurrencyUnit removeCurrencyUnit(String currencyCode){
        Objects.requireNonNull(currencyCode);
        return ConfigurableCurrencyUnitProvider.currencyUnits.remove(currencyCode);
    }

    /**
     * Removes a CurrencyUnit.
     * @param locale the Locale, not null.
     * @return  any unit instance removed, or null.
     */
    public static CurrencyUnit removeCurrencyUnit(Locale locale){
        Objects.requireNonNull(locale);
        return ConfigurableCurrencyUnitProvider.currencyUnitsByLocale.remove(locale);
    }

    /*
     * (non-Javadoc)
     *
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString(){
        return "ConfigurableCurrencyUnitProvider [currencyUnits=" + currencyUnits + ", currencyUnitsByLocale=" +
                currencyUnitsByLocale + "]";
    }

}
