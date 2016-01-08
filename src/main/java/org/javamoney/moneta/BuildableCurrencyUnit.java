/*
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
package org.javamoney.moneta;

import javax.money.*;

import java.io.Serializable;
import java.util.Objects;

/**
 * Implementation of {@link javax.money.CurrencyUnit} that allows to of new instances using a fluent API.
 * Instances created also can be added to the {@link org.javamoney.moneta.internal.ConfigurableCurrencyUnitProvider}
 * singleton, which publishes the instances, so they are visible from the {@link javax.money.Monetary}
 * singleton.
 */
final class BuildableCurrencyUnit implements CurrencyUnit, Comparable<CurrencyUnit>, Serializable {

    /**
     * serialVersionUID.
     */
    private static final long serialVersionUID = -2389580389919492220L;
    /**
     * The unique currency code.
     */
    private String currencyCode;
    /**
     * The (optional) numeric code.
     */
    private int numericCode;
    /**
     * The default fraction digits.
     */
    private int defaultFractionDigits;
    /**
     * THe currency's context.
     */
    private javax.money.CurrencyContext currencyContext;

    /**
     * Constructor, called from the Builder.
     *
     * @param builder the builder, never null.
     */
    BuildableCurrencyUnit(CurrencyUnitBuilder builder) {
        Objects.requireNonNull(builder.currencyCode, "currencyCode required");
        if (builder.numericCode < -1) {
            throw new MonetaryException("numericCode must be >= -1");
        }
        if (builder.defaultFractionDigits < 0) {
            throw new MonetaryException("defaultFractionDigits must be >= 0");
        }
        if (builder.currencyContext == null) {
            throw new MonetaryException("currencyContext must be != null");
        }
        this.defaultFractionDigits = builder.defaultFractionDigits;
        this.numericCode = builder.numericCode;
        this.currencyCode = builder.currencyCode;
        this.currencyContext = builder.currencyContext;
    }

    @Override
    public String getCurrencyCode() {
        return currencyCode;
    }

    @Override
    public int getNumericCode() {
        return numericCode;
    }

    @Override
    public int getDefaultFractionDigits() {
        return defaultFractionDigits;
    }

    @Override
    public CurrencyContext getContext() {
        return currencyContext;
    }

    @Override
    public int compareTo(CurrencyUnit o) {
        Objects.requireNonNull(o);
        return this.currencyCode.compareTo(o.getCurrencyCode());
    }

    /* (non-Javadoc)
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        return Objects.hashCode(currencyCode);
    }

    /* (non-Javadoc)
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (obj instanceof BuildableCurrencyUnit) {
            BuildableCurrencyUnit other = (BuildableCurrencyUnit) obj;
            return Objects.equals(currencyCode, other.currencyCode);
        }
        return false;
    }

    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return "BuildableCurrencyUnit(currencyCode=" + currencyCode + ", numericCode=" + numericCode +
                ", defaultFractionDigits=" + defaultFractionDigits + ", context=" + this.currencyContext + ')';
    }


}
