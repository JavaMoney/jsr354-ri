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

import java.io.Serializable;
import java.util.Currency;
import java.util.Objects;

import javax.money.CurrencyContext;
import javax.money.CurrencyContextBuilder;
import javax.money.CurrencyUnit;

/**
 * Default implementation of a {@link CurrencyUnit} based on the using the JDK's
 * {@link Currency}.
 *
 * @author Anatole Tresch
 * @author Werner Keil
 * @version 0.6
 */
public final class JDKCurrencyAdapter implements CurrencyUnit, Serializable, Comparable<CurrencyUnit> {

    /**
     * serialVersionUID.
     */
    private static final long serialVersionUID = -798486953910548549L;

    /**
     * JDK currency instance.
     */
    private final Currency baseCurrency;

    private final CurrencyContext context = CurrencyContextBuilder.of(Currency.class.getName()).build();

    /**
     * Private constructor, uses a {@link java.util.Currency} for creating new instances.
     *
     * @param currency the Currency instance, not {@code null}.
     */
    JDKCurrencyAdapter(Currency currency) {
        this.baseCurrency = currency;
    }

    /**
     * Gets the unique currency code, the effective code depends on the
     * currency.
     * <p>
     * Since each currency is identified by this code, the currency code is
     * required to be defined for every {@link CurrencyUnit} and not
     * {@code null} or empty.
     * <p>
     * For ISO codes the 3-letter ISO code should be returned. For non ISO
     * currencies no constraints are defined.
     *
     * @return the currency code, never {@code null}. For ISO-4217 this this
     * will be the three letter ISO-4217 code. However, alternate
     * currencies can have different codes. Also there is no constraint
     * about the formatting of alternate codes, despite they fact that
     * the currency codes must be unique.
     * @see javax.money.CurrencyUnit#getCurrencyCode()
     */
    public String getCurrencyCode() {
        return baseCurrency.getCurrencyCode();
    }

    /**
     * Gets a numeric currency code. Within the ISO-4217 name space, this equals
     * to the ISO numeric code. In other currency name spaces this number may be
     * different, or even undefined (-1).
     * <p>
     * The numeric code is an optional alternative to the standard currency
     * code. If defined, the numeric code is required to be unique.
     * <p>
     * This method matches the API of <type>java.util.Currency</type>.
     *
     * @return the numeric currency code
     * @see CurrencyUnit#getNumericCode()
     */
    public int getNumericCode() {
        return baseCurrency.getNumericCode();
    }

    /**
     * Gets the number of fractional digits typically used by this currency.
     * <p>
     * Different currencies have different numbers of fractional digits by
     * default. * For example, 'GBP' has 2 fractional digits, but 'JPY' has
     * zero. * virtual currencies or those with no applicable fractional are
     * indicated by -1. *
     * <p>
     * This method matches the API of <type>java.util.Currency</type>.
     *
     * @return the fractional digits, from 0 to 9 (normally 0, 2 or 3), or 0 for
     * pseudo-currencies.
     */
    public int getDefaultFractionDigits() {
        return baseCurrency.getDefaultFractionDigits();
    }

    @Override
    public CurrencyContext getContext() {
        return context;
    }

    /**
     * Compares two instances, based on {@link #getCurrencyCode()}.
     *
     * @param currency The instance to be compared with.
     * @see java.lang.Comparable#compareTo(java.lang.Object)
     */
    @Override
    public int compareTo(CurrencyUnit currency) {
        Objects.requireNonNull(currency);
        return getCurrencyCode().compareTo(currency.getCurrencyCode());
    }

    /*
     * (non-Javadoc)
     *
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        return Objects.hashCode(baseCurrency);
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
        if (obj instanceof CurrencyUnit) {
            CurrencyUnit other = (CurrencyUnit) obj;
            return Objects.equals(getCurrencyCode(), other.getCurrencyCode());
        }
        return false;
    }

    /**
     * Returns {@link #getCurrencyCode()}
     *
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return baseCurrency.toString();
    }

}
