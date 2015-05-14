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
import javax.money.MonetaryContext;
import javax.money.spi.MonetaryAmountFactoryProviderSpi;

/**
 * SPI (core): Implementations of this interface are used by the {@link javax.money.spi.MonetaryAmountsSingletonSpi} to evaluate the
 * correct {@link javax.money.MonetaryAmountFactory} instances.
 *
 * @param <T> the concrete amount type.
 * @author Anatole Tresch
 * @deprecated This functionality is moved into Java 8 default methods.
 */
@Deprecated
public abstract class BaseMonetaryAmountFactoryProviderSpi<T extends MonetaryAmount>
implements MonetaryAmountFactoryProviderSpi<T> {

    /**
     * Method that determines if this factory should be considered for general evaluation of
     * matching {@link javax.money.MonetaryAmount} implementation types when calling
     * {@link javax.money.Monetary#getAmountFactory(javax.money.MonetaryAmountFactoryQuery)}.
     *
     * @return {@code true} to include this factory into the evaluation.
     * @see javax.money.Monetary#getAmountFactory(javax.money.MonetaryAmountFactoryQuery)
     */
    public QueryInclusionPolicy getQueryInclusionPolicy(){
        return QueryInclusionPolicy.ALWAYS;
    }

    /**
     * Returns the maximal {@link javax.money.MonetaryContext} supported, for requests that exceed these maximal
     * capabilities, an {@link ArithmeticException} must be thrown.
     *
     * @return the maximal {@link javax.money.MonetaryContext} supported, never {@code null}
     */
    public MonetaryContext getMaximalMonetaryContext(){
        return getDefaultMonetaryContext();
    }

}
