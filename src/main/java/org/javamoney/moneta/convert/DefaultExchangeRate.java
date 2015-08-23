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

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import javax.money.CurrencyUnit;
import javax.money.NumberValue;
import javax.money.convert.ConversionContext;
import javax.money.convert.ExchangeRate;

/**
 * This class models an exchange rate, which defines the factor the numeric value of a base amount in some currency
 * 'A' must be multiplied
 * to get the corresponding amount in the terminating currency 'B'. Hereby
 * <ul>
 * <li>an exchange rate always models one rate from a base (source) to a term
 * (target) {@link javax.money.CurrencyUnit}.</li>
 * <li>an exchange rate is always bound to a rate type, which typically matches
 * the data source of the conversion data, e.g. different credit card providers
 * may use different rates for the same conversion.</li>
 * <li>an exchange rate may restrict its validity. In most of the use cases a
 * rates' validity will be well defined, but it is also possible that the data
 * provider is not able to support the rate's validity, leaving it undefined-</li>
 * <li>an exchange rate has a provider, which is responsible for defining the
 * rate. A provider hereby may be, but must not be the same as the rate's data
 * source.</li>
 * <li>an exchange rate can be a <i>direct</i> rate, where its factor is
 * represented by a single conversion step. Or it can model a <i>derived</i>
 * rate, where multiple conversion steps are required to define the overall
 * base/term conversion. In case of derived rates the chained rates define the
 * overall factor, by multiplying the individual chain rate factors. Of course,
 * this also requires that each subsequent rate's base currency in the chain
 * does match the previous term currency (and vice versa):</li>
 * <li>Whereas the factor should be directly implied by the format rate chain
 * for derived rates, this is obviously not the case for the validity range,
 * since rates can have a undefined validity range. Nevertheless in many cases
 * also the validity range can (but must not) be derived from the rate chain.</li>
 * <li>Finally a conversion rate is always unidirectional. There might be cases
 * where the reciprocal value of {@link #factor} matches the correct reverse
 * rate. But in most use cases the reverse rate either has a different rate (not
 * equal to the reciprocal value), or might not be defined at all. Therefore for
 * reversing a ExchangeRate one must access an {@link javax.money.convert.ExchangeRateProvider} and
 * query for the reverse rate.</li>
 * </ul>
 * <p>
 * The class also implements {@link Comparable} to allow sorting of multiple
 * exchange rates using the following sorting order;
 * <ul>
 * <li>Exchange rate type</li>
 * <li>Exchange rate provider</li>
 * <li>base currency</li>
 * <li>term currency</li>
 * </ul>
 * <p>
 * Finally ExchangeRate is modeled as an immutable and thread safe type. Also
 * exchange rates are {@link java.io.Serializable}, hereby serializing in the following
 * form and order:
 * <ul>
 * <li>The base {@link javax.money.CurrencyUnit}
 * <li>The target {@link javax.money.CurrencyUnit}
 * <li>The factor (NumberValue)
 * <li>The {@link javax.money.convert.ConversionContext}
 * <li>The rate chain
 * </ul>
 *
 * @author Werner Keil
 * @author Anatole Tresch
 * @see <a
 * href="https://en.wikipedia.org/wiki/Exchange_rate#Quotations">Wikipedia:
 * Exchange Rate (Quotations)</a>
 */
class DefaultExchangeRate implements ExchangeRate, Serializable, Comparable<ExchangeRate> {
// TODO this should probably go to "convert" in future releases. Analyze feasability of refactoring.

    /**
     * serialVersionUID.
     */
    private static final long serialVersionUID = 5077295306570465837L;
    /**
     * The base currency.
     */
    private final CurrencyUnit base;
    /**
     * The terminating currency.
     */
    private final CurrencyUnit term;
    /**
     * The conversion factor.
     */
    private final NumberValue factor;
    /**
     * The {@link javax.money.convert.ConversionContext}
     */
    private final ConversionContext conversionContext;
    /**
     * The full chain, at least one instance long.
     */
    private final List<ExchangeRate> chain = new ArrayList<>();


    /**
     * Creates a new instance with a custom chain of exchange rate type, e.g. or
     * creating <i>derived</i> rates.
     *
     * @param builder The Builder, never {@code null}.
     */
    DefaultExchangeRate(ExchangeRateBuilder builder) {
        Objects.requireNonNull(builder.base, "base may not be null.");
        Objects.requireNonNull(builder.term, "term may not be null.");
        Objects.requireNonNull(builder.factor, "factor may not be null.");
        Objects.requireNonNull(builder.conversionContext, "exchangeRateType may not be null.");
        this.base = builder.base;
        this.term = builder.term;
        this.factor = builder.factor;
        this.conversionContext = builder.conversionContext;

        setExchangeRateChain(builder.rateChain);
    }

    /**
     * Internal method to set the rate chain, which also ensure that the chain
     * passed, when not null, contains valid elements.
     *
     * @param chain the chain to set.
     */
    private void setExchangeRateChain(List<ExchangeRate> chain) {
        this.chain.clear();
        if (Objects.isNull(chain) || chain.isEmpty()) {
            this.chain.add(this);
        } else {
            for (ExchangeRate rate : chain) {
                if (Objects.isNull(rate)) {
                    throw new IllegalArgumentException("Rate Chain element can not be null.");
                }
            }
            this.chain.addAll(chain);
        }
    }

    /**
     * Access the {@link javax.money.convert.ConversionContext} of {@link javax.money.convert.ExchangeRate}.
     *
     * @return the conversion context, never null.
     */
    @Override
	public final ConversionContext getContext() {
        return this.conversionContext;
    }

    /**
     * Get the base (source) {@link javax.money.CurrencyUnit}.
     *
     * @return the base {@link javax.money.CurrencyUnit}.
     */
    @Override
	public final CurrencyUnit getBaseCurrency() {
        return this.base;
    }

    /**
     * Get the term (target) {@link javax.money.CurrencyUnit}.
     *
     * @return the term {@link javax.money.CurrencyUnit}.
     */
    @Override
	public final CurrencyUnit getCurrency() {
        return this.term;
    }

    /**
     * Access the rate's bid factor.
     *
     * @return the bid factor for this exchange rate, or {@code null}.
     */
    @Override
	public final NumberValue getFactor() {
        return this.factor;
    }

    /**
     * Access the chain of exchange rates.
     *
     * @return the chain of rates, in case of a derived rate, this may be
     * several instances. For a direct exchange rate, this equals to
     * <code>new ExchangeRate[]{this}</code>.
     */
    @Override
	public final List<ExchangeRate> getExchangeRateChain() {
        return this.chain;
    }

    /**
     * Allows to evaluate if this exchange rate is a derived exchange rate.
     * <p>
     * Derived exchange rates are defined by an ordered list of subconversions
     * with intermediate steps, whereas a direct conversion is possible in one
     * steps.
     * <p>
     * This method always returns {@code true}, if the chain contains more than
     * one rate. Direct rates, have also a chain, but with exact one rate.
     *
     * @return true, if the exchange rate is derived.
     */
    @Override
	public final boolean isDerived() {
        return this.chain.size() > 1;
    }

    /*
     * (non-Javadoc)
     *
     * @see java.lang.Comparable#compareTo(java.lang.Object)
     */
    @Override
    public int compareTo(ExchangeRate o) {
        Objects.requireNonNull(o);
        int compare = this.getBaseCurrency().getCurrencyCode().compareTo(o.getBaseCurrency().getCurrencyCode());
        if (compare == 0) {
            compare = this.getCurrency().getCurrencyCode().compareTo(o.getCurrency().getCurrencyCode());
        }
        if (compare == 0) {
            compare = this.getContext().getProviderName().compareTo(o.getContext().getProviderName());
        }
        return compare;
    }

    /*
     * (non-Javadoc)
     *
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return "ExchangeRate [base=" + base + ", term=" + term + ", factor=" + factor + ", conversionContext=" + conversionContext + ']';
    }

    /*
     * (non-Javadoc)
     *
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        return Objects.hash(base, conversionContext, factor, term, chain);
    }

    /*
     * (non-Javadoc)
     *
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (obj instanceof DefaultExchangeRate) {
            DefaultExchangeRate other = (DefaultExchangeRate) obj;
            return Objects.equals(base, other.base) &&
                    Objects.equals(conversionContext, other.conversionContext) &&
                    Objects.equals(factor, other.factor) && Objects.equals(term, other.term);
        }
        return false;
    }

    /**
     * Create a {@link ExchangeRateBuilder} based on the current rate instance.
     *
     * @return a new {@link ExchangeRateBuilder}, never {@code null}.
     */
    public ExchangeRateBuilder toBuilder() {
        return new ExchangeRateBuilder(getContext()).setBase(getBaseCurrency()).setTerm(getCurrency())
                .setFactor(getFactor()).setRateChain(getExchangeRateChain());
    }
}
