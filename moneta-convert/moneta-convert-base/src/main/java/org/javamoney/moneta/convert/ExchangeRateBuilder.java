/**
 * Copyright (c) 2012, 2015, Credit Suisse (Anatole Tresch), Werner Keil and others by the @author tag.
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

import javax.money.CurrencyUnit;
import javax.money.NumberValue;
import javax.money.convert.ConversionContext;
import javax.money.convert.ExchangeRate;
import javax.money.convert.RateType;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

/**
 * Builder for creating new instances of {@link javax.money.convert.ExchangeRate}. Note that
 * instances of this class are not thread-safe.
 *
 * @author Anatole Tresch
 * @author Werner Keil
 */
public class ExchangeRateBuilder {
// TODO this should probably go to "convert" in future releases. Analyze feasability of refactoring.
    /**
     * The {@link javax.money.convert.ConversionContext}.
     */
    ConversionContext conversionContext;
    /**
     * The base (source) currency.
     */
    CurrencyUnit base;
    /**
     * The term (target) currency.
     */
    CurrencyUnit term;
    /**
     * The conversion factor.
     */
    NumberValue factor;
    /**
     * The chain of involved rates.
     */
    List<ExchangeRate> rateChain = new ArrayList<>();

    /**
     * Sets the exchange rate type
     *
     * @param rateType the {@link javax.money.convert.RateType} contained
     */
    public ExchangeRateBuilder(String provider, RateType rateType) {
        this(ConversionContext.of(provider, rateType));
    }

    /**
     * Sets the exchange rate type
     *
     * @param context the {@link javax.money.convert.ConversionContext} to be applied
     */
    public ExchangeRateBuilder(ConversionContext context) {
        setContext(context);
    }

    /**
     * Sets the exchange rate type
     *
     * @param rate the {@link javax.money.convert.ExchangeRate} to be applied
     */
    public ExchangeRateBuilder(ExchangeRate rate) {
        setContext(rate.getContext());
        setFactor(rate.getFactor());
        setTerm(rate.getCurrency());
        setBase(rate.getBaseCurrency());
        setRateChain(rate.getExchangeRateChain());
    }

    /**
     * Sets the base {@link javax.money.CurrencyUnit}
     *
     * @param base to base (source) {@link javax.money.CurrencyUnit} to be applied
     * @return the builder instance
     */
    public ExchangeRateBuilder setBase(CurrencyUnit base) {
        this.base = base;
        return this;
    }

    /**
     * Sets the terminating (target) {@link javax.money.CurrencyUnit}
     *
     * @param term to terminating {@link javax.money.CurrencyUnit} to be applied
     * @return the builder instance
     */
    public ExchangeRateBuilder setTerm(CurrencyUnit term) {
        this.term = term;
        return this;
    }

    /**
     * Sets the {@link javax.money.convert.ExchangeRate} chain.
     *
     * @param exchangeRates the {@link javax.money.convert.ExchangeRate} chain to be applied
     * @return the builder instance
     */
    public ExchangeRateBuilder setRateChain(ExchangeRate... exchangeRates) {
        this.rateChain.clear();
        if (Objects.nonNull(exchangeRates)) {
            this.rateChain.addAll(Arrays.asList(exchangeRates.clone()));
        }
        return this;
    }

    /**
     * Sets the {@link javax.money.convert.ExchangeRate} chain.
     *
     * @param exchangeRates the {@link javax.money.convert.ExchangeRate} chain to be applied
     * @return the builder instance
     */
    public ExchangeRateBuilder setRateChain(List<ExchangeRate> exchangeRates) {
        this.rateChain.clear();
        if (Objects.nonNull(exchangeRates)) {
            this.rateChain.addAll(exchangeRates);
        }
        return this;
    }


    /**
     * Sets the conversion factor, as the factor
     * {@code base * factor = target}.
     *
     * @param factor the factor.
     * @return The builder instance.
     */
    public ExchangeRateBuilder setFactor(NumberValue factor) {
        this.factor = factor;
        return this;
    }

    /**
     * Sets the provider to be applied.
     *
     * @param conversionContext the {@link javax.money.convert.ConversionContext}, not null.
     * @return The builder.
     */
    public ExchangeRateBuilder setContext(ConversionContext conversionContext) {
        Objects.requireNonNull(conversionContext);
        this.conversionContext = conversionContext;
        return this;
    }

    /**
     * Builds a new instance of {@link javax.money.convert.ExchangeRate}.
     *
     * @return a new instance of {@link javax.money.convert.ExchangeRate}.
     * @throws IllegalArgumentException if the rate could not be built.
     */
    public ExchangeRate build() {
        return new DefaultExchangeRate(this);
    }

    /**
     * Initialize the {@link ExchangeRateBuilder} with an {@link javax.money.convert.ExchangeRate}. This is
     * useful for creating a new rate, reusing some properties from an
     * existing one.
     *
     * @param rate the base rate
     * @return the Builder, for chaining.
     */
    public ExchangeRateBuilder setRate(ExchangeRate rate) {
        this.base = rate.getBaseCurrency();
        this.term = rate.getCurrency();
        this.conversionContext = rate.getContext();
        this.factor = rate.getFactor();
        this.rateChain = rate.getExchangeRateChain();
        this.term = rate.getCurrency();
        return this;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("org.javamoney.moneta.ExchangeRateBuilder: ");
        sb.append("[conversionContext").append(conversionContext).append(',');
        sb.append("base").append(base).append(',');
        sb.append("term").append(term).append(',');
        sb.append("factor").append(factor).append(',');
        sb.append("rateChain").append(rateChain).append(']');
        return sb.toString();
    }
}
