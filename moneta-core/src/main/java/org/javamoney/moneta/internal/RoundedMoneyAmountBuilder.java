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
package org.javamoney.moneta.internal;

import java.math.RoundingMode;

import javax.money.CurrencyUnit;
import javax.money.MonetaryContext;
import javax.money.MonetaryContextBuilder;
import javax.money.NumberValue;

import org.javamoney.moneta.RoundedMoney;
import org.javamoney.moneta.spi.AbstractAmountBuilder;

/**
 * Implementation of {@link javax.money.MonetaryAmountFactory} creating instances of {@link org.javamoney.moneta
 * .RoundedMoney}.
 *
 * @author Anatole Tresch
 */
public class RoundedMoneyAmountBuilder extends AbstractAmountBuilder<RoundedMoney> {

    static final MonetaryContext DEFAULT_CONTEXT =
            MonetaryContextBuilder.of(RoundedMoney.class).setPrecision(0).set(RoundingMode.HALF_EVEN).build();
    static final MonetaryContext MAX_CONTEXT =
            MonetaryContextBuilder.of(RoundedMoney.class).setPrecision(0).set(RoundingMode.HALF_EVEN).build();

    /*
     * (non-Javadoc)
     * @see org.javamoney.moneta.spi.AbstractAmountFactory#of(javax.money.CurrencyUnit,
     * java.lang.Number, javax.money.MonetaryContext)
     */
    @Override
    protected RoundedMoney create(Number number, CurrencyUnit currency, MonetaryContext monetaryContext) {
        return RoundedMoney.of(number, currency, MonetaryContext.from(monetaryContext, RoundedMoney.class));
    }

    @Override
    public NumberValue getMaxNumber() {
        return null;
    }

    @Override
    public NumberValue getMinNumber() {
        return null;
    }

    /*
     * (non-Javadoc)
     * @see javax.money.MonetaryAmountFactory#getAmountType()
     */
    @Override
    public Class<RoundedMoney> getAmountType() {
        return RoundedMoney.class;
    }

    /*
     * (non-Javadoc)
     * @see org.javamoney.moneta.spi.AbstractAmountFactory#loadDefaultMonetaryContext()
     */
    @Override
    protected MonetaryContext loadDefaultMonetaryContext() {
        return DEFAULT_CONTEXT;
    }

    /*
     * (non-Javadoc)
     * @see org.javamoney.moneta.spi.AbstractAmountFactory#loadMaxMonetaryContext()
     */
    @Override
    protected MonetaryContext loadMaxMonetaryContext() {
        return MAX_CONTEXT;
    }

}
