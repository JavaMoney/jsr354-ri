/*
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
package org.javamoney.moneta;

import org.javamoney.moneta.internal.ConfigurableCurrencyUnitProvider;

import javax.money.CurrencyContextBuilder;
import javax.money.CurrencyUnit;
import java.util.Locale;
import java.util.Objects;

/**
 * Builder for constructing new instances of {@link BuildableCurrencyUnit} using a fluent
 * API.
 */
public final class CurrencyUnitBuilder {
    /**
     * The currency code.
     */
    String currencyCode;
    /**
     * The (optional) numeric code.
     */
    int numericCode = -1;
    /**
     * The default fraction digits.
     */
    int defaultFractionDigits = 2;
    /**
     * The currency's context.
     */
    javax.money.CurrencyContext currencyContext;

    /**
     * Private constructor, use #of() methods.
     */
    private CurrencyUnitBuilder() {
    }

    /**
     * Creates a new CurrencyUnitBuilder.
     *
     * @param currencyCode    the (unique) and identifying currency code, not null.
     * @param currencyContext The currency context to be used.
     */
    public static CurrencyUnitBuilder of(String currencyCode, javax.money.CurrencyContext currencyContext) {
        return new CurrencyUnitBuilder(currencyCode, currencyContext);
    }

    /**
     * Creates a new CurrencyUnitBuilder, creates a simple CurrencyContext using the given provider name.
     *
     * @param currencyCode the (unique) and identifying currency code, not null.
     * @param providerName the currency provider, not null.
     */
    public static CurrencyUnitBuilder of(String currencyCode, String providerName) {
        return new CurrencyUnitBuilder(currencyCode, CurrencyContextBuilder.of(providerName).build());
    }

    /**
     * Creates a new Builder.
     *
     * @param currencyCode the (unique) and identifying currency code, not null.
     */
    private CurrencyUnitBuilder(String currencyCode, javax.money.CurrencyContext currencyContext) {
        Objects.requireNonNull(currencyCode, "currencyCode required");
        this.currencyCode = currencyCode;
        Objects.requireNonNull(currencyContext, "currencyContext required");
        this.currencyContext = currencyContext;
    }

    /**
     * Allows to set the currency code, for creating multiple instances, using one Builder.
     *
     * @param currencyCode the (unique) and identifying currency code, not null.
     * @return the Builder, for chaining.
     * @see javax.money.CurrencyUnit#getCurrencyCode()
     */
    public CurrencyUnitBuilder setCurrencyCode(String currencyCode) {
        Objects.requireNonNull(currencyCode, "currencyCode required");
        this.currencyCode = currencyCode;
        this.currencyContext = CurrencyContextBuilder.of(getClass().getSimpleName()).build();
        return this;
    }

    /**
     * Set the numeric code (optional).
     *
     * @param numericCode The numeric currency code, &gt;= -1. .1 hereby means <i>undefined</i>.
     * @return the Builder, for chaining.
     * @see javax.money.CurrencyUnit#getNumericCode()
     */
    public CurrencyUnitBuilder setNumericCode(int numericCode) {
        if (numericCode < -1) {
            throw new IllegalArgumentException("numericCode must be >= -1");
        }
        this.numericCode = numericCode;
        return this;
    }

    /**
     * Set the default fraction digits.
     *
     * @param defaultFractionDigits the default fraction digits, &gt;= 0.
     * @return the Builder, for chaining.
     * @see javax.money.CurrencyUnit#getDefaultFractionDigits()
     */
    public CurrencyUnitBuilder setDefaultFractionDigits(int defaultFractionDigits) {
        if (defaultFractionDigits < 0) {
            throw new IllegalArgumentException("defaultFractionDigits must be >= 0");
        }
        this.defaultFractionDigits = defaultFractionDigits;
        return this;
    }

    /**
     * Returns a new instance of {@link BuildableCurrencyUnit}.
     *
     * @return the new CurrencyUnit instance.
     * @throws javax.money.MonetaryException if creation fails
     */
    public CurrencyUnit build() {
        return build(false);
    }

    /**
     * Returns a new instance of {@link BuildableCurrencyUnit} and publishes it so it is
     * accessible from the {@code MonetaryCurrencies} singleton.
     *
     * @param register if {@code true} the instance created is published so it is accessible from
     *                 the {@code MonetaryCurrencies} singleton.
     * @return the new CurrencyUnit instance.
     * @see javax.money.Monetary#getCurrency(String, String...)
     */
    public CurrencyUnit build(boolean register) {
        BuildableCurrencyUnit cu = new BuildableCurrencyUnit(this);
        if (register) {
            ConfigurableCurrencyUnitProvider.registerCurrencyUnit(cu);
        }
        return cu;
    }

    /**
     * Returns a new instance of {@link BuildableCurrencyUnit} and publishes it so it is
     * accessible from the {@code MonetaryCurrencies} singleton.
     *
     * @param register if {@code true} the instance created is published so it is accessible from
     *                 the {@code MonetaryCurrencies} singleton.
     * @param locale   country Locale for making the currency for the given country.
     * @return the new CurrencyUnit instance.
     * @see javax.money.Monetary#getCurrency(String, String...)
     * @see javax.money.Monetary#getCurrency(java.util.Locale, String...)
     */
    public CurrencyUnit build(boolean register, Locale locale) {
        BuildableCurrencyUnit cu = new BuildableCurrencyUnit(this);
        if (register) {
            ConfigurableCurrencyUnitProvider.registerCurrencyUnit(cu);
            ConfigurableCurrencyUnitProvider.registerCurrencyUnit(cu, locale);
        }
        return cu;
    }
}
