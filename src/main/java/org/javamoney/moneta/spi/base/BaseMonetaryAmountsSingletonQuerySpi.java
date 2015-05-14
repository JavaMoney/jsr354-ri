/*
 * CREDIT SUISSE IS WILLING TO LICENSE THIS SPECIFICATION TO YOU ONLY UPON THE CONDITION THAT YOU
 * ACCEPT ALL OF THE TERMS CONTAINED IN THIS AGREEMENT. PLEASE READ THE TERMS AND CONDITIONS OF THIS
 * AGREEMENT CAREFULLY. BY DOWNLOADING THIS SPECIFICATION, YOU ACCEPT THE TERMS AND CONDITIONS OF
 * THE AGREEMENT. IF YOU ARE NOT WILLING TO BE BOUND BY IT, SELECT THE "DECLINE" BUTTON AT THE
 * BOTTOM OF THIS PAGE. Specification: JSR-354 Money and Currency API ("Specification") Copyright
 * (c) 2012-2013, Credit Suisse All rights reserved.
 */
package org.javamoney.moneta.spi.base;

import javax.money.MonetaryAmount;
import javax.money.MonetaryAmountFactory;
import javax.money.MonetaryAmountFactoryQuery;
import javax.money.spi.MonetaryAmountsSingletonQuerySpi;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

/**
 * SPI (core) for the backing implementation of the {@link javax.money.Monetary} singleton, implementing
 * the query functionality for amounts.
 *
 * @author Anatole Tresch
 * @deprecated This functionality is moved into Java 8 default methods.
 */
@Deprecated
public abstract class BaseMonetaryAmountsSingletonQuerySpi implements MonetaryAmountsSingletonQuerySpi {

    /**
     * Checks if an {@link javax.money.MonetaryAmountFactory} is matching the given query.
     *
     * @param query the factory query, not null.
     * @return true, if at least one {@link javax.money.MonetaryAmountFactory} matches the query.
     */
    public boolean isAvailable(MonetaryAmountFactoryQuery query) {
        return !getAmountFactories(query).isEmpty();
    }

    /**
     * Executes the query and returns the {@link javax.money.MonetaryAmount} implementation type found,
     * if there is only one type.
     * If multiple types match the query, the first one is selected.
     *
     * @param query the factory query, not null.
     * @return the type found, or null.
     */
    public Class<? extends MonetaryAmount> getAmountType(MonetaryAmountFactoryQuery query) {
        MonetaryAmountFactory<?> f = getAmountFactory(query);
        if (f != null) {
            return f.getAmountType();
        }
        return null;
    }

    /**
     * Executes the query and returns the {@link javax.money.MonetaryAmount} implementation types found.
     *
     * @param query the factory query, not null.
     * @return the type found, or null.
     */
    public Collection<Class<? extends MonetaryAmount>> getAmountTypes(MonetaryAmountFactoryQuery query) {
        Collection<MonetaryAmountFactory<? extends MonetaryAmount>> factories = getAmountFactories(query);
        Set<Class<? extends MonetaryAmount>> result = new HashSet<>();
        for(MonetaryAmountFactory f:factories){
            //noinspection unchecked
            result.add(f.getAmountType());
        }
        return result;
    }

    /**
     * Executes the query and returns the {@link javax.money.MonetaryAmountFactory} implementation type found,
     * if there is only one type. If multiple types match the query, the first one is selected.
     *
     * @param query the factory query, not null.
     * @return the type found, or null.
     */
    public MonetaryAmountFactory getAmountFactory(MonetaryAmountFactoryQuery query) {
        Collection<MonetaryAmountFactory<?>> factories = getAmountFactories(query);
        if (factories.isEmpty()) {
            return null;
        }
        return factories.iterator().next();
    }

}
