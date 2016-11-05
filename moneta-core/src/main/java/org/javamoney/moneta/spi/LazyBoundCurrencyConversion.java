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
import javax.money.convert.ConversionContext;
import javax.money.convert.ConversionQuery;
import javax.money.convert.ConversionQueryBuilder;
import javax.money.convert.CurrencyConversion;
import javax.money.convert.ExchangeRate;
import javax.money.convert.ExchangeRateProvider;

/**
 * This class defines a {@link CurrencyConversion} that is converting to a
 * specific target {@link CurrencyUnit}. Each instance of this class is bound to
 * a specific {@link ExchangeRateProvider}, a term {@link CurrencyUnit} and a
 * target timestamp.
 *
 * @author Anatole Tresch
 */
public class LazyBoundCurrencyConversion extends AbstractCurrencyConversion implements CurrencyConversion {

    private final ExchangeRateProvider rateProvider;

    private final ConversionQuery conversionQuery;

    public LazyBoundCurrencyConversion(ConversionQuery conversionQuery, ExchangeRateProvider rateProvider,
                                       ConversionContext conversionContext) {

        super(conversionQuery.getCurrency(), conversionContext);
        this.conversionQuery = conversionQuery;
        this.rateProvider = rateProvider;
    }

    /**
     * Get the exchange rate type that this provider instance is providing data
     * for.
     *
     * @return the exchange rate type if this instance.
     */
    @Override
    public ExchangeRate getExchangeRate(MonetaryAmount amount) {
        return this.rateProvider.getExchangeRate(ConversionQueryBuilder
                .of(conversionQuery).setBaseCurrency(amount.getCurrency())
                .build());
        // return this.rateProvider.getExchangeRate(amount.getCurrency(),
        // getCurrency());
    }

    @Override
    public ExchangeRateProvider getExchangeRateProvider() {
        return this.rateProvider;
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * org.javamoney.moneta.conversion.AbstractCurrencyConversion#with(javax
     * .money.convert.ConversionContext)
     */
    @Override
    public CurrencyConversion with(ConversionContext conversionContext) {
        return new LazyBoundCurrencyConversion(conversionQuery, rateProvider,
                conversionContext);
    }

    /*
     * (non-Javadoc)
     *
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return "CurrencyConversion [MonetaryAmount -> MonetaryAmount; provider=" + rateProvider + ", context=" +
                getContext() + ", termCurrency=" + getCurrency() + ']';
    }

}
