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
package org.javamoney.moneta.spi;

import javax.money.CurrencyUnit;
import javax.money.MonetaryAmount;
import javax.money.MonetaryOperator;
import javax.money.NumberValue;
import javax.money.convert.ConversionContext;
import javax.money.convert.CurrencyConversion;
import javax.money.convert.CurrencyConversionException;
import javax.money.convert.ExchangeRate;

import org.javamoney.moneta.function.MonetaryOperators;

import java.math.MathContext;
import java.math.RoundingMode;
import java.util.Objects;

/**
 * Abstract base class used for implementing currency conversion.
 *
 * @author Anatole Tresch
 * @author Werner Keil
 */
public abstract class AbstractCurrencyConversion implements CurrencyConversion {

    private final CurrencyUnit termCurrency;
    private final ConversionContext conversionContext;

    public static final String KEY_SCALE = "exchangeRateScale";

    public AbstractCurrencyConversion(CurrencyUnit termCurrency, ConversionContext conversionContext) {
        Objects.requireNonNull(termCurrency);
        Objects.requireNonNull(conversionContext);
        this.termCurrency = termCurrency;
        this.conversionContext = conversionContext;
    }

    /**
     * Access the terminating {@link CurrencyUnit} of this conversion instance.
     *
     * @return the terminating {@link CurrencyUnit} , never {@code null}.
     */
    @Override
    public CurrencyUnit getCurrency() {
        return termCurrency;
    }

    /**
     * Access the target {@link ConversionContext} of this conversion instance.
     *
     * @return the target {@link ConversionContext}.
     */
    @Override
    public ConversionContext getContext() {
        return conversionContext;
    }

    /**
     * Get the exchange rate type that this {@link MonetaryOperator} instance is
     * using for conversion.
     *
     * @return the {@link ExchangeRate} to be used, or null, if this conversion
     * is not supported (will lead to a
     * {@link CurrencyConversionException}.
     * @see #apply(MonetaryAmount)
     */
    @Override
    public abstract ExchangeRate getExchangeRate(MonetaryAmount sourceAmount);

    /*
     * (non-Javadoc)
     * @see javax.money.convert.CurrencyConversion#with(javax.money.convert.ConversionContext)
     */
    public abstract CurrencyConversion with(ConversionContext conversionContext);

    /**
     * Method that converts the source {@link MonetaryAmount} to an
     * {@link MonetaryAmount} based on the {@link ExchangeRate} of this
     * conversion.
     *
     * @param amount The source amount
     * @return The converted amount, never null.
     * @throws CurrencyConversionException if conversion failed, or the required data is not available.
     * @see #getExchangeRate(MonetaryAmount)
     */
    @Override
    public MonetaryAmount apply(MonetaryAmount amount) {
        if (termCurrency.equals(Objects.requireNonNull(amount).getCurrency())) {
            return amount;
        }
        ExchangeRate rate = getExchangeRate(amount);
        if (Objects.isNull(rate) || !amount.getCurrency().equals(rate.getBaseCurrency())) {
            throw new CurrencyConversionException(amount.getCurrency(),
                    Objects.isNull(rate) ? null : rate.getCurrency(), null);
        }

        NumberValue factor = rate.getFactor();
        factor = roundFactor(amount, factor);

        Integer scale = rate.getContext().get(KEY_SCALE, Integer.class);
        if(Objects.isNull(scale) || scale < 0) {
        	return amount.multiply(factor).getFactory().setCurrency(rate.getCurrency()).create();
        } else {
        	return amount.multiply(factor).getFactory().setCurrency(rate.getCurrency()).create().with(MonetaryOperators.rounding(scale));
        }
    }

    /**
     * Optionally rounds the factor to be used. By default this method will only round
     * as much as its is needed, so the factor can be handled by the target amount instance based on its
     * numeric capabilities.
     *
     * @param amount the amount, not null.
     * @param factor the factor
     * @return the new NumberValue, never null.
     */
    protected NumberValue roundFactor(MonetaryAmount amount, NumberValue factor) {
        if (amount.getContext().getMaxScale() > 0) {
            if (factor.getScale() > amount.getContext().getMaxScale()) {
                return factor.round(new MathContext(amount.getContext().getMaxScale(), RoundingMode.HALF_EVEN));
            }
        }
        return factor;
    }


    /*
     * (non-Javadoc)
     *
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return getClass().getName() + " [MonetaryAmount -> MonetaryAmount" + ']';
    }

}
