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
package org.javamoney.moneta;

import org.javamoney.moneta.internal.ConfigurableCurrencyUnitProvider;

import javax.money.CurrencyUnit;
import javax.money.MonetaryException;
import java.util.Locale;
import java.util.Objects;

/**
 * Implementation of {@link javax.money.CurrencyUnit} that allows to create new instances using a fluent API.
 * Instances created also can be added to the {@link org.javamoney.moneta.internal.ConfigurableCurrencyUnitProvider}
 * singleton, which publishes the instances, so they are visible from the {@link javax.money.MonetaryCurrencies}
 * singleton.
 */
public final class BuildableCurrencyUnit implements CurrencyUnit, Comparable<CurrencyUnit>{

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
     * Constructor, called from the Builder.
     *
     * @param builder the builder, never null.
     */
    private BuildableCurrencyUnit(Builder builder){
        Objects.requireNonNull(builder.currencyCode, "currencyCode required");
        if(builder.numericCode < -1){
            throw new MonetaryException("numericCode must be >= -1");
        }
        if(builder.defaultFractionDigits < 0){
            throw new MonetaryException("defaultFractionDigits must be >= 0");
        }
        this.defaultFractionDigits = builder.defaultFractionDigits;
        this.numericCode = builder.numericCode;
        this.currencyCode = builder.currencyCode;
    }

    @Override
    public String getCurrencyCode(){
        return currencyCode;
    }

    @Override
    public int getNumericCode(){
        return numericCode;
    }

    @Override
    public int getDefaultFractionDigits(){
        return defaultFractionDigits;
    }

    @Override
    public int compareTo(CurrencyUnit o){
        return this.currencyCode.compareTo(o.getCurrencyCode());
    }

    /* (non-Javadoc)
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode(){
        return Objects.hashCode(currencyCode);
    }

    /* (non-Javadoc)
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object obj){
        if(this == obj){
            return true;
        }
        if (Objects.isNull(obj)) {
            return false;
        }
        if(getClass() != obj.getClass()){
            return false;
        }
        BuildableCurrencyUnit other = (BuildableCurrencyUnit) obj;
        if (Objects.isNull(currencyCode)) {
            if (Objects.nonNull(other.currencyCode)) {
                return false;
            }
        }else if(!currencyCode.equals(other.currencyCode)){
            return false;
        }
        return true;
    }

    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString(){
        return "BuildableCurrencyUnit [currencyCode=" + currencyCode + ", numericCode=" + numericCode +
                ", defaultFractionDigits=" + defaultFractionDigits + "]";
    }


    /**
     * Builder for constructing new instances o{@link org.javamoney.moneta.BuildableCurrencyUnit} using a fluent
     * API.
     */
    public static final class Builder{
        /**
         * The currency code.
         */
        private String currencyCode;
        /**
         * The (optional) numeric code.
         */
        private int numericCode = -1;
        /**
         * The default fraction digits.
         */
        private int defaultFractionDigits = 2;

        /**
         * Creats a new Builder.
         *
         * @param currencyCode the (unique) and identifying currency code, not null.
         */
        public Builder(String currencyCode){
            Objects.requireNonNull(currencyCode, "currencyCode required");
            this.currencyCode = currencyCode;
        }

        /**
         * Allows to set the currenc< code, for creating multiple instances, using one Builder.
         *
         * @param currencyCode the (unique) and identifying currency code, not null.
         * @return the Builder, for chaining.
         * @see javax.money.CurrencyUnit#getCurrencyCode()
         */
        public Builder setCurrencyCode(String currencyCode){
            Objects.requireNonNull(currencyCode, "currencyCode required");
            this.currencyCode = currencyCode;
            return this;
        }

        /**
         * Set the numeric code (optional).
         *
         * @param numericCode The numeric currency code, >= -1. .1 hereby means <i>undefined</i>.
         * @return the Builder, for chaining.
         * @see javax.money.CurrencyUnit#getNumericCode()
         */
        public Builder setNumericCode(int numericCode){
            if(numericCode < -1){
                throw new IllegalArgumentException("numericCode must be >= -1");
            }
            this.numericCode = numericCode;
            return this;
        }

        /**
         * Set the default fraction digits.
         *
         * @param defaultFractionDigits the default fraction digits, >= 0.
         * @return
         * @see javax.money.CurrencyUnit#getDefaultFractionDigits()
         */
        public Builder setDefaultFractionDigits(int defaultFractionDigits){
            if(defaultFractionDigits < 0){
                throw new IllegalArgumentException("defaultFractionDigits must be >= 0");
            }
            this.defaultFractionDigits = defaultFractionDigits;
            return this;
        }

        /**
         * Returns a new instance of {@link org.javamoney.moneta.BuildableCurrencyUnit}.
         *
         * @return the new CurrencyUnit instance.
         * @throws MonetaryException, if creation fails
         */
        public BuildableCurrencyUnit build(){
            return build(false);
        }

        /**
         * Returns a new instance of {@link org.javamoney.moneta.BuildableCurrencyUnit} and publishes it so it is
         * accessible from the {@code MonetaryCurrencies} singleton.
         *
         * @param register if {@code true} the instance created is published so it is accessible from
         *                 the {@code MonetaryCurrencies} singleton.
         * @return the new CurrencyUnit instance.
         * @see javax.money.MonetaryCurrencies#getCurrency(String)
         */
        public BuildableCurrencyUnit build(boolean register){
            BuildableCurrencyUnit cu = new BuildableCurrencyUnit(this);
            if(register){
                ConfigurableCurrencyUnitProvider.registerCurrencyUnit(cu);
            }
            return cu;
        }

        /**
         * Returns a new instance of {@link org.javamoney.moneta.BuildableCurrencyUnit} and publishes it so it is
         * accessible from the {@code MonetaryCurrencies} singleton.
         *
         * @param register if {@code true} the instance created is published so it is accessible from
         *                 the {@code MonetaryCurrencies} singleton.
         * @param locale   country Locale for making the currency for the given country.
         * @return the new CurrencyUnit instance.
         * @see javax.money.MonetaryCurrencies#getCurrency(String)
         * @see javax.money.MonetaryCurrencies#getCurrency(java.util.Locale)
         */
        public BuildableCurrencyUnit build(boolean register, Locale locale){
            BuildableCurrencyUnit cu = new BuildableCurrencyUnit(this);
            if(register){
                ConfigurableCurrencyUnitProvider.registerCurrencyUnit(cu);
                ConfigurableCurrencyUnitProvider.registerCurrencyUnit(cu, locale);
            }
            return cu;
        }
    }

}
