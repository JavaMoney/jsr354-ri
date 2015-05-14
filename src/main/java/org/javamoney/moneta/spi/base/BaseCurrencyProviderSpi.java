/*
 * CREDIT SUISSE IS WILLING TO LICENSE THIS SPECIFICATION TO YOU ONLY UPON THE
 * CONDITION THAT YOU ACCEPT ALL OF THE TERMS CONTAINED IN THIS AGREEMENT.
 * PLEASE READ THE TERMS AND CONDITIONS OF THIS AGREEMENT CAREFULLY. BY
 * DOWNLOADING THIS SPECIFICATION, YOU ACCEPT THE TERMS AND CONDITIONS OF THE
 * AGREEMENT. IF YOU ARE NOT WILLING TO BE BOUND BY IT, SELECT THE "DECLINE"
 * BUTTON AT THE BOTTOM OF THIS PAGE.
 * 
 * Specification: JSR-354 Money and Currency API ("Specification")
 * 
 * Copyright (c) 2012-2014, Credit Suisse All rights reserved.
 */
package org.javamoney.moneta.spi.base;

import javax.money.CurrencyQuery;
import javax.money.spi.CurrencyProviderSpi;

/**
 * SPI (core) to be registered using the {@link javax.money.spi.Bootstrap}, which allows to
 * register/provide additional currencies into the system automatically on
 * startup. The implementation is allowed to be implemented in y contextual way,
 * so depending on the runtime context, different currencies may be available.
 *
 * @author Anatole Tresch
 * @deprecated This functionality is moved into Java 8 default methods.
 */
@Deprecated
public abstract class BaseCurrencyProviderSpi implements CurrencyProviderSpi {


    /**
     * The unique name of this currency provider instance.
     * @return hte unique provider id, never null or empty.
     */
    public String getProviderName(){
        return getClass().getSimpleName();
    }

    /**
     * CHecks if a {@link javax.money.CurrencyUnit} instances matching the given
     * {@link javax.money.CurrencyContext} is available from this provider.
     *
     * @param query the {@link javax.money.CurrencyQuery} containing the parameters determining the query. not null.
     * @return false, if no such unit is provided by this provider.
     */
    public boolean isCurrencyAvailable(CurrencyQuery query){
        return !getCurrencies(query).isEmpty();
    }

}
