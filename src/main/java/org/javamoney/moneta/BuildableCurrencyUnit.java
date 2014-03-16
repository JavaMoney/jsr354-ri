/*
 * CREDIT SUISSE IS WILLING TO LICENSE THIS SPECIFICATION TO YOU ONLY UPON THE
 * CONDITION THAT YOU ACCEPT ALL OF THE TERMS CONTAINED IN THIS AGREEMENT.
 * PLEASE READ THE TERMS AND CONDITIONS OF THIS AGREEMENT CAREFULLY. BY
 * DOWNLOADING THIS SPECIFICATION, YOU ACCEPT THE TERMS AND CONDITIONS OF THE
 * AGREEMENT. IF YOU ARE NOT WILLING TO BE BOUND BY IT, SELECT THE "DECLINE"
 * BUTTON AT THE BOTTOM OF THIS PAGE. Specification: JSR-354 Money and Currency
 * API ("Specification") Copyright (c) 2012-2013, Credit Suisse All rights
 * reserved.
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
     * The uniqie currency code.
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
        final int prime = 31;
        int result = 1;
        result = prime * result + ((currencyCode == null) ? 0 : currencyCode.hashCode());
        return result;
    }

    /* (non-Javadoc)
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object obj){
        if(this == obj){
            return true;
        }
        if(obj == null){
            return false;
        }
        if(getClass() != obj.getClass()){
            return false;
        }
        BuildableCurrencyUnit other = (BuildableCurrencyUnit) obj;
        if(currencyCode == null){
            if(other.currencyCode != null){
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
         * Creates a new instance of {@link org.javamoney.moneta.BuildableCurrencyUnit}.
         *
         * @return the new CurrencyUnit instance.
         * @throws MonetaryException, if creation fails
         */
        public BuildableCurrencyUnit create(){
            return create(false);
        }

        /**
         * Creates a new instance of {@link org.javamoney.moneta.BuildableCurrencyUnit} and publishes it so it is
         * accessible from the {@code MonetaryCurrencies} singleton.
         *
         * @param register if {@code true} the instance created is published so it is accessible from
         *                 the {@code MonetaryCurrencies} singleton.
         * @return the new CurrencyUnit instance.
         * @see javax.money.MonetaryCurrencies#getCurrency(String)
         */
        public BuildableCurrencyUnit create(boolean register){
            BuildableCurrencyUnit cu = new BuildableCurrencyUnit(this);
            if(register){
                ConfigurableCurrencyUnitProvider.registerCurrencyUnit(cu);
            }
            return cu;
        }

        /**
         * Creates a new instance of {@link org.javamoney.moneta.BuildableCurrencyUnit} and publishes it so it is
         * accessible from the {@code MonetaryCurrencies} singleton.
         *
         * @param register if {@code true} the instance created is published so it is accessible from
         *                 the {@code MonetaryCurrencies} singleton.
         * @param locale   country Locale for making the currency for the given country.
         * @return the new CurrencyUnit instance.
         * @see javax.money.MonetaryCurrencies#getCurrency(String)
         * @see javax.money.MonetaryCurrencies#getCurrency(java.util.Locale)
         */
        public BuildableCurrencyUnit create(boolean register, Locale locale){
            BuildableCurrencyUnit cu = new BuildableCurrencyUnit(this);
            if(register){
                ConfigurableCurrencyUnitProvider.registerCurrencyUnit(cu);
                ConfigurableCurrencyUnitProvider.registerCurrencyUnit(cu, locale);
            }
            return cu;
        }
    }

}
