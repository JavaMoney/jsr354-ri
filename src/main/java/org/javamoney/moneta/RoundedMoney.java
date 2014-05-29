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

import org.javamoney.moneta.internal.RoundedMoneyAmountFactory;
import org.javamoney.moneta.spi.AbstractMoney;
import org.javamoney.moneta.spi.DefaultNumberValue;

import javax.money.*;
import java.io.*;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.Objects;

/**
 * Platform RI: Default immutable implementation of {@link MonetaryAmount} based on
 * {@link BigDecimal} for the numeric representation.
 * <p/>
 * As required by {@link MonetaryAmount} this class is final, thread-safe, immutable and
 * serializable.
 *
 * @author Anatole Tresch
 * @author Werner Keil
 * @version 0.6.1
 */
public final class RoundedMoney extends AbstractMoney implements Comparable<MonetaryAmount>, Serializable{

    /**
     * serialVersionUID.
     */
    private static final long serialVersionUID = 366517590511294389L;
    /**
     * The default {@link MonetaryContext} applied.
     */
    public static final MonetaryContext DEFAULT_MONETARY_CONTEXT =
            MonetaryContext.from(Money.DEFAULT_MONETARY_CONTEXT, RoundedMoney.class);

    /**
     * The numeric part of this amount.
     */
    private BigDecimal number;

    /**
     * The rounding to be done.
     */
    private MonetaryOperator rounding;

    /**
     * Required for deserialization only.
     */
    private RoundedMoney(){
    }

    /**
     * Creates a new instance os {@link RoundedMoney}.
     *
     * @param currency the currency, not null.
     * @param number   the amount, not null.
     */
    private RoundedMoney(Number number, CurrencyUnit currency, MonetaryContext monetaryContext,
                         MonetaryOperator rounding){
        super(currency, monetaryContext);
        Objects.requireNonNull(number, "Number is required.");
        checkNumber(number);
        this.currency = currency;
		if (Objects.nonNull(rounding)) {
			this.rounding = rounding;
		} else {
			this.rounding = MonetaryRoundings.getRounding(currency);
		}
        this.number = getBigDecimal(number, monetaryContext);
    }

    // Static Factory Methods

    /**
     * Translates a {@code BigDecimal} value and a {@code CurrencyUnit} currency into a
     * {@code Money}.
     *
     * @param number   numeric value of the {@code Money}.
     * @param currency currency unit of the {@code Money}.
     * @return a {@code Money} combining the numeric value and currency unit.
     */
    public static RoundedMoney of(CurrencyUnit currency, BigDecimal number){
        return new RoundedMoney(number, currency, DEFAULT_MONETARY_CONTEXT, null);
    }

    /**
     * Translates a {@code BigDecimal} value and a {@code CurrencyUnit} currency into a
     * {@code Money}.
     *
     * @param number   numeric value of the {@code Money}.
     * @param currency currency unit of the {@code Money}.
     * @param rounding The rounding to be applied.
     * @return a {@code Money} combining the numeric value and currency unit.
     */
    public static RoundedMoney of(CurrencyUnit currency, BigDecimal number, MonetaryOperator rounding){
        return new RoundedMoney(number, currency, DEFAULT_MONETARY_CONTEXT, rounding);
    }

    /**
     * Translates a {@code BigDecimal} value and a {@code CurrencyUnit} currency into a
     * {@code Money}.
     *
     * @param number          numeric value of the {@code Money}.
     * @param currency        currency unit of the {@code Money}.
     * @param monetaryContext the {@link MathContext} to be used.
     * @return a {@code Money} combining the numeric value and currency unit.
     */
    public static RoundedMoney of(BigDecimal number, CurrencyUnit currency, MonetaryContext monetaryContext){
        return new RoundedMoney(number, currency, MonetaryContext.from(monetaryContext, RoundedMoney.class), null);
    }

    /**
     * Translates a {@code BigDecimal} value and a {@code CurrencyUnit} currency into a
     * {@code Money}.
     *
     * @param number   numeric value of the {@code Money}.
     * @param currency currency unit of the {@code Money}.
     * @param rounding The rounding to be applied.
     * @return a {@code Money} combining the numeric value and currency unit.
     */
    public static RoundedMoney of(BigDecimal number, CurrencyUnit currency, MonetaryContext monetaryContext,
                                  MonetaryOperator rounding){
        return new RoundedMoney(number, currency, MonetaryContext.from(monetaryContext, RoundedMoney.class), rounding);
    }

    /**
     * Static factory method for creating a new instance of {@link RoundedMoney} .
     *
     * @param currency The target currency, not null.
     * @param number   The numeric part, not null.
     * @return A new instance of {@link RoundedMoney}.
     */
    public static RoundedMoney of(Number number, CurrencyUnit currency){
        return new RoundedMoney(number, currency, (MonetaryContext) null, null);
    }

    /**
     * Static factory method for creating a new instance of {@link RoundedMoney} .
     *
     * @param currency The target currency, not null.
     * @param number   The numeric part, not null.
     * @param rounding The rounding to be applied.
     * @return A new instance of {@link RoundedMoney}.
     */
    public static RoundedMoney of(Number number, CurrencyUnit currency, MonetaryOperator rounding){
        return new RoundedMoney(number, currency, (MonetaryContext) null, rounding);
    }

    /**
     * Static factory method for creating a new instance of {@link RoundedMoney} .
     *
     * @param currency The target currency, not null.
     * @param number   The numeric part, not null.
     * @return A new instance of {@link RoundedMoney}.
     */
    public static RoundedMoney of(Number number, CurrencyUnit currency, MonetaryContext monetaryContext){
        return new RoundedMoney(number, currency, MonetaryContext.from(monetaryContext, RoundedMoney.class), null);
    }

    /**
     * Static factory method for creating a new instance of {@link RoundedMoney} .
     *
     * @param currency        The target currency, not null.
     * @param number          The numeric part, not null.
     * @param monetaryContext the {@link MonetaryContext} to be used.
     * @param rounding        The rounding to be applied.
     * @return A new instance of {@link RoundedMoney}.
     */
    public static RoundedMoney of(CurrencyUnit currency, Number number, MonetaryContext monetaryContext,
                                  MonetaryOperator rounding){
        return new RoundedMoney(number, currency, MonetaryContext.from(monetaryContext, RoundedMoney.class), rounding);
    }

    /**
     * Static factory method for creating a new instance of {@link RoundedMoney} .
     *
     * @param currencyCode The target currency as ISO currency code.
     * @param number       The numeric part, not null.
     * @return A new instance of {@link RoundedMoney}.
     */
    public static RoundedMoney of(Number number, String currencyCode){
        return new RoundedMoney(number, MonetaryCurrencies.getCurrency(currencyCode), DEFAULT_MONETARY_CONTEXT,
                                MonetaryRoundings.getRounding(MonetaryCurrencies.getCurrency(currencyCode)));
    }

    /**
     * Static factory method for creating a new instance of {@link RoundedMoney} .
     *
     * @param currencyCode The target currency as ISO currency code.
     * @param number       The numeric part, not null.
     * @param rounding     The rounding to be applied.
     * @return A new instance of {@link RoundedMoney}.
     */
    public static RoundedMoney of(Number number, String currencyCode, MonetaryOperator rounding){
        return new RoundedMoney(number, MonetaryCurrencies.getCurrency(currencyCode), DEFAULT_MONETARY_CONTEXT,
                                rounding);
    }

    /**
     * Static factory method for creating a new instance of {@link RoundedMoney} .
     *
     * @param currencyCode The target currency as ISO currency code.
     * @param number       The numeric part, not null.
     * @return A new instance of {@link RoundedMoney}.
     */
    public static RoundedMoney of(Number number, String currencyCode, MonetaryContext monetaryContext){
        return new RoundedMoney(number, MonetaryCurrencies.getCurrency(currencyCode),
                                MonetaryContext.from(monetaryContext, RoundedMoney.class),
                                MonetaryRoundings.getRounding());
    }

    /**
     * Static factory method for creating a new instance of {@link RoundedMoney} .
     *
     * @param currencyCode The target currency as ISO currency code.
     * @param number       The numeric part, not null.
     * @param rounding     The rounding to be applied.
     * @return A new instance of {@link RoundedMoney}.
     */
    public static RoundedMoney of(String currencyCode, Number number, MonetaryContext monetaryContext,
                                  MonetaryOperator rounding){
        return new RoundedMoney(number, MonetaryCurrencies.getCurrency(currencyCode),
                                MonetaryContext.from(monetaryContext, RoundedMoney.class), rounding);
    }

    /*
     * (non-Javadoc)
     * @see javax.money.MonetaryAmount#getCurrency()
     */
    public CurrencyUnit getCurrency(){
        return currency;
    }

    /**
     * Access the {@link MathContext} used by this instance.
     *
     * @return the {@link MathContext} used, never null.
     */
    public MonetaryContext getMonetaryContext(){
        return this.monetaryContext;
    }

    public RoundedMoney abs(){
        if(this.isPositiveOrZero()){
            return this;
        }
        return this.negate();
    }

    // Arithmetic Operations

    public RoundedMoney add(MonetaryAmount amount){
        checkAmountParameter(amount);
        if(amount.isZero()){
            return this;
        }
        return (RoundedMoney) new RoundedMoney(this.number.add(amount.getNumber().numberValue(BigDecimal.class)),
                                               this.currency, this.monetaryContext, this.rounding).with(rounding);
    }

    /*
     * (non-Javadoc)
     * @see javax.money.MonetaryAmount#divide(javax.money.MonetaryAmount)
     */
    public RoundedMoney divide(Number divisor){
        BigDecimal bd = getBigDecimal(divisor);
        if(isOne(bd)){
            return this;
        }
        BigDecimal dec =
                this.number.divide(bd, this.monetaryContext.getAttribute(RoundingMode.class, RoundingMode.HALF_EVEN));
        return (RoundedMoney) new RoundedMoney(dec, this.currency, this.monetaryContext, this.rounding).with(rounding);
    }

    /*
     * (non-Javadoc)
     * @see javax.money.MonetaryAmount#divideAndRemainder(javax.money.MonetaryAmount)
     */
    public RoundedMoney[] divideAndRemainder(Number divisor){
        BigDecimal bd = getBigDecimal(divisor);
        if(isOne(bd)){
            return new RoundedMoney[]{this, new RoundedMoney(0L, getCurrency(), this.monetaryContext, this.rounding)};
        }
        BigDecimal[] dec = this.number.divideAndRemainder(getBigDecimal(divisor), this.monetaryContext
                .getAttribute(MathContext.class, MathContext.DECIMAL64));
        return new RoundedMoney[]{new RoundedMoney(dec[0], this.currency, this.monetaryContext, this.rounding),
                (RoundedMoney) new RoundedMoney(dec[1], this.currency, this.monetaryContext, this.rounding)
                        .with(rounding)};
    }

    /*
     * (non-Javadoc)
     * @see javax.money.MonetaryAmount#divideToIntegralValue(Number) )D
     */
    public RoundedMoney divideToIntegralValue(Number divisor){
        BigDecimal dec = this.number.divideToIntegralValue(getBigDecimal(divisor), this.monetaryContext
                .getAttribute(MathContext.class, MathContext.DECIMAL64));
        return new RoundedMoney(dec, this.currency, this.monetaryContext, this.rounding);
    }

    /*
     * (non-Javadoc)
     * @see javax.money.MonetaryAmount#multiply(Number)
     */
    public RoundedMoney multiply(Number multiplicand){
        BigDecimal bd = getBigDecimal(multiplicand);
        if(isOne(bd)){
            return this;
        }
        BigDecimal dec =
                this.number.multiply(bd, this.monetaryContext.getAttribute(MathContext.class, MathContext.DECIMAL64));
        return (RoundedMoney) new RoundedMoney(dec, this.currency, this.monetaryContext, this.rounding).with(rounding);
    }

    /*
     * (non-Javadoc)
     * @see javax.money.MonetaryAmount#negate()
     */
    public RoundedMoney negate(){
        return new RoundedMoney(
                this.number.negate(this.monetaryContext.getAttribute(MathContext.class, MathContext.DECIMAL64)),
                this.currency, this.monetaryContext, this.rounding);
    }

    /*
     * (non-Javadoc)
     * @see javax.money.MonetaryAmount#plus()
     */
    public RoundedMoney plus(){
        return new RoundedMoney(
                this.number.plus(this.monetaryContext.getAttribute(MathContext.class, MathContext.DECIMAL64)),
                this.currency, this.monetaryContext, this.rounding);
    }

    /*
     * (non-Javadoc)
     * @see javax.money.MonetaryAmount#subtract(javax.money.MonetaryAmount)
     */
    public RoundedMoney subtract(MonetaryAmount subtrahend){
        checkAmountParameter(subtrahend);
        if(subtrahend.isZero()){
            return this;
        }
        return new RoundedMoney(this.number.subtract(subtrahend.getNumber().numberValue(BigDecimal.class),
                                                     this.monetaryContext
                                                             .getAttribute(MathContext.class, MathContext.DECIMAL64)
        ), this.currency, this.monetaryContext, this.rounding
        );
    }

    /*
     * (non-Javadoc)
     * @see javax.money.MonetaryAmount#pow(int)
     */
    public RoundedMoney pow(int n){
        return new RoundedMoney(
                this.number.pow(n, this.monetaryContext.getAttribute(MathContext.class, MathContext.DECIMAL64)),
                this.currency, this.monetaryContext, this.rounding).with(rounding);
    }

    /*
     * (non-Javadoc)
     * @see javax.money.MonetaryAmount#ulp()
     */
    public RoundedMoney ulp(){
        return new RoundedMoney(this.number.ulp(), this.currency, DEFAULT_MONETARY_CONTEXT, this.rounding);
    }

    /*
     * (non-Javadoc)
     * @see javax.money.MonetaryAmount#remainder(Number)
     */
    public RoundedMoney remainder(Number divisor){
        return new RoundedMoney(this.number.remainder(getBigDecimal(divisor), this.monetaryContext
                .getAttribute(MathContext.class, MathContext.DECIMAL64)), this.currency, this.monetaryContext,
                                this.rounding
        );
    }

    /*
     * (non-Javadoc)
     * @see javax.money.MonetaryAmount#scaleByPowerOfTen(int)
     */
    public RoundedMoney scaleByPowerOfTen(int n){
        return new RoundedMoney(this.number.scaleByPowerOfTen(n), this.currency, this.monetaryContext, this.rounding);
    }

    /*
     * (non-Javadoc)
     * @see javax.money.MonetaryAmount#isZero()
     */
    public boolean isZero(){
        return this.number.signum() == 0;
    }

    /*
     * (non-Javadoc)
     * @see javax.money.MonetaryAmount#isPositive()
     */
    public boolean isPositive(){
        return signum() == 1;
    }

    /*
     * (non-Javadoc)
     * @see javax.money.MonetaryAmount#isPositiveOrZero()
     */
    public boolean isPositiveOrZero(){
        return signum() >= 0;
    }

    /*
     * (non-Javadoc)
     * @see javax.money.MonetaryAmount#isNegative()
     */
    public boolean isNegative(){
        return signum() == -1;
    }

    /*
     * (non-Javadoc)
     * @see javax.money.MonetaryAmount#isNegativeOrZero()
     */
    public boolean isNegativeOrZero(){
        return signum() <= 0;
    }

    /*
     * (non-Javadoc)
     * @see javax.money.MonetaryAmount#with(java.lang.Number)
     */
    public RoundedMoney with(Number amount){
        checkNumber(amount);
        return new RoundedMoney(getBigDecimal(amount), this.currency, this.monetaryContext, this.rounding);
    }

    /**
     * Creates a new Money instance, by just replacing the {@link CurrencyUnit}.
     *
     * @param currency the currency unit to be replaced, not {@code null}
     * @return the new amount with the same numeric value and {@link MathContext}, but the new
     * {@link CurrencyUnit}.
     */
    public RoundedMoney with(CurrencyUnit currency){
        Objects.requireNonNull(currency, "currency required");
        return new RoundedMoney(asType(BigDecimal.class), currency, this.monetaryContext, this.rounding);
    }

    /*
     * (non-Javadoc)
     * @see javax.money.MonetaryAmount#with(CurrencyUnit, java.lang.Number)
     */
    public RoundedMoney with(CurrencyUnit currency, Number amount){
        checkNumber(amount);
        return new RoundedMoney(getBigDecimal(amount), currency, this.monetaryContext, this.rounding);
    }

    /*
     * (non-Javadoc)
     * @see javax.money.MonetaryAmount#getScale()
     */
    public int getScale(){
        return this.number.scale();
    }

    /*
     * (non-Javadoc)
     * @see javax.money.MonetaryAmount#getPrecision()
     */
    public int getPrecision(){
        return this.number.precision();
    }

	/*
     * (non-Javadoc)
	 * @see javax.money.MonetaryAmount#signum()
	 */

    public int signum(){
        return this.number.signum();
    }

    /*
     * (non-Javadoc)
     * @see javax.money.MonetaryAmount#lessThan(javax.money.MonetaryAmount)
     */
    public boolean isLessThan(MonetaryAmount amount){
        checkAmountParameter(amount);
        return number.stripTrailingZeros()
                .compareTo(amount.getNumber().numberValue(BigDecimal.class).stripTrailingZeros()) < 0;
    }

    /*
     * (non-Javadoc)
     * @see javax.money.MonetaryAmount#lessThanOrEqualTo(javax.money.MonetaryAmount)
     */
    public boolean isLessThanOrEqualTo(MonetaryAmount amount){
        checkAmountParameter(amount);
        return number.stripTrailingZeros()
                .compareTo(amount.getNumber().numberValue(BigDecimal.class).stripTrailingZeros()) <= 0;
    }

    /*
     * (non-Javadoc)
     * @see javax.money.MonetaryAmount#greaterThan(javax.money.MonetaryAmount)
     */
    public boolean isGreaterThan(MonetaryAmount amount){
        checkAmountParameter(amount);
        return number.stripTrailingZeros()
                .compareTo(amount.getNumber().numberValue(BigDecimal.class).stripTrailingZeros()) > 0;
    }

    /*
     * (non-Javadoc)
     * @see javax.money.MonetaryAmount#greaterThanOrEqualTo(javax.money.MonetaryAmount ) #see
     */
    public boolean isGreaterThanOrEqualTo(MonetaryAmount amount){
        checkAmountParameter(amount);
        return number.stripTrailingZeros()
                .compareTo(amount.getNumber().numberValue(BigDecimal.class).stripTrailingZeros()) >= 0;
    }

    /*
     * (non-Javadoc)
     * @see javax.money.MonetaryAmount#isEqualTo(javax.money.MonetaryAmount)
     */
    public boolean isEqualTo(MonetaryAmount amount){
        checkAmountParameter(amount);
        return number.stripTrailingZeros()
                .compareTo(amount.getNumber().numberValue(BigDecimal.class).stripTrailingZeros()) == 0;
    }

    /*
     * (non-Javadoc)
     * @see javax.money.MonetaryAmount#isNotEqualTo(javax.money.MonetaryAmount)
     */
    public boolean isNotEqualTo(MonetaryAmount amount){
        checkAmountParameter(amount);
        return number.stripTrailingZeros()
                .compareTo(amount.getNumber().numberValue(BigDecimal.class).stripTrailingZeros()) != 0;
    }

    /*
     * }(non-Javadoc)
     * @see javax.money.MonetaryAmount#adjust(javax.money.AmountAdjuster)
     */
    @Override
    public RoundedMoney with(MonetaryOperator operator){
        Objects.requireNonNull(operator);
        try{
            return RoundedMoney.from(operator.apply(this));
        }
        catch(MonetaryException e){
            throw e;
        }
        catch(Exception e){
            throw new MonetaryException("Query failed: " + operator, e);
        }
    }

    public static RoundedMoney from(MonetaryAmount amt){
        if(amt.getClass() == RoundedMoney.class){
            return (RoundedMoney) amt;
        }
        if(amt.getClass() == FastMoney.class){
            return RoundedMoney.of(((FastMoney) amt).getNumber().numberValue(BigDecimal.class), amt.getCurrency(),
                                   DEFAULT_MONETARY_CONTEXT);
        }else if(amt.getClass() == Money.class){
            return RoundedMoney
                    .of(amt.getNumber().numberValue(BigDecimal.class), amt.getCurrency(), DEFAULT_MONETARY_CONTEXT);
        }
        return RoundedMoney
                .of(amt.getNumber().numberValue(BigDecimal.class), amt.getCurrency(), DEFAULT_MONETARY_CONTEXT);
    }

    /*
     * }(non-Javadoc)
     * @see javax.money.MonetaryAmount#adjust(javax.money.AmountAdjuster)
     */
    @Override
    public <T> T query(MonetaryQuery<T> query){
        Objects.requireNonNull(query);
        try{
            return query.queryFrom(this);
        }
        catch(MonetaryException e){
            throw e;
        }
        catch(Exception e){
            throw new MonetaryException("Query failed: " + query, e);
        }
    }

    /*
     * @see javax.money.MonetaryAmount#asType(java.lang.Class)
     */
    @SuppressWarnings("unchecked")
    public <T> T asType(Class<T> type){
        if(BigDecimal.class.equals(type)){
            return (T) this.number;
        }
        if(Number.class.equals(type)){
            final T asType = (T) this.number;
            return asType;
        }
        if(Double.class.equals(type)){
            return (T) Double.valueOf(this.number.doubleValue());
        }
        if(Float.class.equals(type)){
            return (T) Float.valueOf(this.number.floatValue());
        }
        if(Long.class.equals(type)){
            return (T) Long.valueOf(this.number.longValue());
        }
        if(Integer.class.equals(type)){
            return (T) Integer.valueOf(this.number.intValue());
        }
        if(Short.class.equals(type)){
            return (T) Short.valueOf(this.number.shortValue());
        }
        if(Byte.class.equals(type)){
            return (T) Byte.valueOf(this.number.byteValue());
        }
        if(BigInteger.class.equals(type)){
            return (T) this.number.toBigInteger();
        }
        throw new IllegalArgumentException("Unsupported representation type: " + type);
    }

    /*
     * }(non-Javadoc)
     * @see javax.money.MonetaryAmount#asType(java.lang.Class, javax.money.Rounding)
     */
    public <T> T asType(Class<T> type, MonetaryOperator adjuster){
        RoundedMoney amount = (RoundedMoney) adjuster.apply(this);
        return amount.asType(type);
    }

    private void writeObject(ObjectOutputStream oos) throws IOException{
        oos.writeObject(this.number);
        oos.writeObject(this.monetaryContext);
        oos.writeObject(this.currency);
    }

    private void readObject(ObjectInputStream ois) throws IOException, ClassNotFoundException{
        this.number = (BigDecimal) ois.readObject();
        this.monetaryContext = (MonetaryContext) ois.readObject();
        this.currency = (CurrencyUnit) ois.readObject();
    }

    @SuppressWarnings("unused")
    private void readObjectNoData() throws ObjectStreamException{
        if (Objects.isNull(this.number)) {
            this.number = BigDecimal.ZERO;
        }
        if (Objects.isNull(this.monetaryContext)) {
            this.monetaryContext = DEFAULT_MONETARY_CONTEXT;
        }
        if(Objects.isNull(this.currency)) {
            this.currency = MonetaryCurrencies.getCurrency("XXX"); // no
        }
    }

    /*
     * (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString(){
        return currency.getCurrencyCode() + ' ' + number;
    }

    /*
     * (non-Javadoc)
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode(){
		return Objects.hash(currency, asNumberStripped());
    }

    /*
     * (non-Javadoc)
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
        RoundedMoney other = (RoundedMoney) obj;
        if (Objects.isNull(currency)){
            if (Objects.nonNull(other.currency)) {
                return false;
            }
        }else if(!currency.equals(other.currency)){
            return false;
        }
        return asNumberStripped().equals(other.asNumberStripped());
    }

    /*
     * @see java.lang.Comparable#compareTo(java.lang.Object)
     */
    public int compareTo(MonetaryAmount o){
        Objects.requireNonNull(o);
        int compare = -1;
        if(this.currency.equals(o.getCurrency())){
            compare = asNumberStripped().compareTo(RoundedMoney.from(o).asNumberStripped());
        }else{
            compare = this.currency.getCurrencyCode().compareTo(o.getCurrency().getCurrencyCode());
        }
        return compare;
    }

    /*
     * (non-Javadoc)
     * @see javax.money.MonetaryAmount#getNumber()
     */
    public NumberValue getNumber(){
        return new DefaultNumberValue(number);
    }

    /**
     * Method that returns BigDecimal.ZERO, if {@link #isZero()}, and #number
     * {@link #stripTrailingZeros()} in all other cases.
     *
     * @return the stripped number value.
     */
    public BigDecimal asNumberStripped(){
        if(isZero()){
            return BigDecimal.ZERO;
        }
        return this.number.stripTrailingZeros();
    }

    /**
     * Internal method to check for correct number parameter.
     *
     * @param number
     * @throws IllegalArgumentException If the number is null
     */
    private void checkNumber(Number number){
        Objects.requireNonNull(number, "Number is required.");
    }

    @Override
    public RoundedMoney multiply(long amount){
        if(amount == 1L){
            return this;
        }
        return multiply(getBigDecimal(amount));
    }

    @Override
    public RoundedMoney multiply(double amount){
        if(amount == 1.0d){
            return this;
        }
        return multiply(getBigDecimal(amount));
    }

    @Override
    public RoundedMoney divide(long amount){
        if(amount == 1L){
            return this;
        }
        return divide(getBigDecimal(amount));
    }

    @Override
    public RoundedMoney divide(double amount){

        if(amount == 1.0d){
            return this;
        }
        return divide(getBigDecimal(amount));
    }

    @Override
    public RoundedMoney remainder(long amount){
        return remainder(getBigDecimal(amount));
    }

    @Override
    public RoundedMoney remainder(double amount){
        return remainder(getBigDecimal(amount));
    }

    @Override
    public RoundedMoney[] divideAndRemainder(long amount){
        return divideAndRemainder(getBigDecimal(amount));
    }

    @Override
    public RoundedMoney[] divideAndRemainder(double amount){
        return divideAndRemainder(getBigDecimal(amount));
    }

    @Override
    public RoundedMoney stripTrailingZeros(){
        if(isZero()){
            return of(getCurrency(), BigDecimal.ZERO);
        }
        return of(getCurrency(), this.number.stripTrailingZeros());
    }

    @Override
    public RoundedMoney divideToIntegralValue(long divisor){
        return divideToIntegralValue(getBigDecimal(divisor));
    }

    @Override
    public RoundedMoney divideToIntegralValue(double divisor){
        return divideToIntegralValue(getBigDecimal(divisor));
    }

    @Override
    protected MonetaryContext getDefaultMonetaryContext(){
        return DEFAULT_MONETARY_CONTEXT;
    }

    @Override
    public MonetaryAmountFactory<RoundedMoney> getFactory(){
        return new RoundedMoneyAmountFactory().setAmount(this);
    }

    private boolean isOne(Number number){
        BigDecimal bd = getBigDecimal(number);
        try{
            return bd.scale() == 0 && bd.longValueExact() == 1L;
        }
        catch(Exception e){
            // The only way to end up here is that longValueExact throws an ArithmeticException,
            // so the amount is definitively not equal to 1.
            return false;
        }
    }
}
