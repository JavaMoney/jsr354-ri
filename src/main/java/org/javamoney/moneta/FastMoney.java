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

import org.javamoney.moneta.internal.FastMoneyAmountFactory;
import org.javamoney.moneta.spi.AbstractMoney;
import org.javamoney.moneta.spi.DefaultNumberValue;

import javax.money.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Objects;

/**
 * <type>long</type> based implementation of {@link MonetaryAmount}. This class internally uses a
 * single long number as numeric representation, which basically is interpreted as minor units.<br/>
 * It suggested to have a performance advantage of a 10-15 times faster compared to {@link Money},
 * which internally uses {@link BigDecimal}. Nevertheless this comes with a price of less precision.
 * As an example performing the following calculation one million times, results in slightly
 * different results:
 * <p/>
 * <pre>
 * Money money1 = money1.add(Money.of(EURO, 1234567.3444));
 * money1 = money1.subtract(Money.of(EURO, 232323));
 * money1 = money1.multiply(3.4);
 * money1 = money1.divide(5.456);
 * </pre>
 * <p/>
 * Executed one million (1000000) times this results in {@code EUR 1657407.962529182}, calculated in
 * 3680 ms, or roughly 3ns/loop.
 * <p/>
 * whrereas
 * <p/>
 * <pre>
 * FastMoney money1 = money1.add(FastMoney.of(EURO, 1234567.3444));
 * money1 = money1.subtract(FastMoney.of(EURO, 232323));
 * money1 = money1.multiply(3.4);
 * money1 = money1.divide(5.456);
 * </pre>
 * <p/>
 * executed one million (1000000) times results in {@code EUR 1657407.96251}, calculated in 179 ms,
 * which is less than 1ns/loop.
 * <p/>
 * Also note than mixing up types my drastically change the performance behavior. E.g. replacing the
 * code above with the following: *
 * <p/>
 * <pre>
 * FastMoney money1 = money1.add(Money.of(EURO, 1234567.3444));
 * money1 = money1.subtract(FastMoney.of(EURO, 232323));
 * money1 = money1.multiply(3.4);
 * money1 = money1.divide(5.456);
 * </pre>
 * <p/>
 * executed one million (1000000) times may execute significantly longer, since monetary amount type
 * conversion is involved.
 * <p/>
 * Basically, when mixing amount implementations, the performance of the amount, on which most of
 * the operations are operated, has the most significant impact on the overall performance behavior.
 *
 * @author Anatole Tresch
 * @author Werner Keil
 * @version 0.5.2
 */
public final class FastMoney extends AbstractMoney implements Comparable<MonetaryAmount>, Serializable{

    private static final long serialVersionUID = 1L;

    /**
     * The numeric part of this amount.
     */
    private long number;

    /**
     * The number value.
     */
    private transient NumberValue numberValue;

    /**
     * The current scale represented by the number.
     */
    private static final int SCALE = 5;

    /**
     * the {@link MonetaryContext} used by this instance, e.g. on division.
     */
    private static final MonetaryContext MONETARY_CONTEXT =
            new MonetaryContext.Builder(FastMoney.class).setMaxScale(SCALE)
                    .setFixedScale(true).setPrecision(14).build();

    /**
     * Maximum possible value supported, using XX (no currency).
     */
    public static final FastMoney MAX_VALUE = new FastMoney(Long.MAX_VALUE, MonetaryCurrencies.getCurrency("XXX"));
    /**
     * Maximum possible numeric value supported.
     */
    private static final BigDecimal MAX_BD = MAX_VALUE.getBigDecimal();
    /**
     * Minimum possible value supported, using XX (no currency).
     */
    public static final FastMoney MIN_VALUE = new FastMoney(Long.MIN_VALUE, MonetaryCurrencies.getCurrency("XXX"));
    /**
     * Minimum possible numeric value supported.
     */
    private static final BigDecimal MIN_BD = MIN_VALUE.getBigDecimal();

    /**
     * Required for deserialization only.
     */
    private FastMoney(){
    }

    /**
     * Creates a new instance os {@link FastMoney}.
     *
     * @param currency the currency, not null.
     * @param number   the amount, not null.
     */
    private FastMoney(Number number, CurrencyUnit currency, boolean allowInternalRounding){
        super(currency, MONETARY_CONTEXT);
        Objects.requireNonNull(number, "Number is required.");
        this.number = getInternalNumber(number, allowInternalRounding);
        this.numberValue = new DefaultNumberValue(number);
    }

    /**
     * Creates a new instance os {@link FastMoney}.
     *
     * @param currency    the currency, not null.
     * @param numberValue the numeric value, not null.
     */
    private FastMoney(NumberValue numberValue, CurrencyUnit currency, boolean allowInternalRounding){
        super(currency, MONETARY_CONTEXT);
        Objects.requireNonNull(numberValue, "Number is required.");
        this.number = getInternalNumber(numberValue.numberValue(BigDecimal.class), allowInternalRounding);
    }

    private long getInternalNumber(Number number, boolean allowInternalRounding){
        BigDecimal bd = getBigDecimal(number);
        if(!allowInternalRounding && bd.scale() > SCALE){
            throw new ArithmeticException(number + " can not be represented by this class, scale > " + SCALE);
        }
        if(bd.compareTo(MIN_BD) < 0){
            throw new ArithmeticException("Overflow: " + number + " < " + MIN_BD);
        }else if(bd.compareTo(MAX_BD) > 0){
            throw new ArithmeticException("Overflow: " + number + " > " + MAX_BD);
        }
        return bd.movePointRight(SCALE).longValue();
    }

    private FastMoney(long number, CurrencyUnit currency){
        super(currency, MONETARY_CONTEXT);
        Objects.requireNonNull(currency, "Currency is required.");
        this.currency = currency;
        this.number = number;
    }

    /**
     * Static factory method for creating a new instance of {@link FastMoney}.
     *
     * @param currency      The target currency, not null.
     * @param numberBinding The numeric part, not null.
     * @return A new instance of {@link FastMoney}.
     */
    public static FastMoney of(NumberValue numberBinding, CurrencyUnit currency){
        return new FastMoney(numberBinding, currency, false);
    }

    /**
     * Static factory method for creating a new instance of {@link FastMoney}.
     *
     * @param currency The target currency, not null.
     * @param number   The numeric part, not null.
     * @return A new instance of {@link FastMoney}.
     */
    public static FastMoney of(Number number, CurrencyUnit currency){
        return new FastMoney(number, currency, false);
    }

    /**
     * Static factory method for creating a new instance of {@link FastMoney}.
     *
     * @param currencyCode The target currency as currency code.
     * @param number       The numeric part, not null.
     * @return A new instance of {@link FastMoney}.
     */
    public static FastMoney of(Number number, String currencyCode){
        CurrencyUnit currency = MonetaryCurrencies.getCurrency(currencyCode);
        return of(number, currency);
    }

    /*
     * @see java.lang.Comparable#compareTo(java.lang.Object)
     */
    public int compareTo(MonetaryAmount o){
        int compare = getCurrency().getCurrencyCode().compareTo(o.getCurrency().getCurrencyCode());
        if(compare == 0){
            compare = getNumber().numberValue(BigDecimal.class).compareTo(o.getNumber().numberValue(BigDecimal.class));
        }
        return compare;
    }

    /*
     * (non-Javadoc)
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode(){
        final int prime = 31;
        int result = 1;
        result = prime * result + ((currency == null) ? 0 : currency.hashCode());
        result = prime * result + (int) number;
        return result;
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
        if(obj == null){
            return false;
        }
        if(getClass() != obj.getClass()){
            return false;
        }
        FastMoney other = (FastMoney) obj;
        if(currency == null){
            if (Objects.nonNull(other.getCurrency())) {
                return false;
            }
        }else if(!currency.equals(other.getCurrency())){
            return false;
        }
        return number == other.number;
    }

    /*
     * (non-Javadoc)
     * @see javax.money.MonetaryAmount#getCurrency()
     */
    public CurrencyUnit getCurrency(){
        return currency;
    }

    /*
     * (non-Javadoc)
     * @see javax.money.MonetaryAmount#abs()
     */
    public FastMoney abs(){
        if(this.isPositiveOrZero()){
            return this;
        }
        return this.negate();
    }

    // Arithmetic Operations

    /*
     * (non-Javadoc)
     * @see javax.money.MonetaryAmount#add(javax.money.MonetaryAmount)
     */
    public FastMoney add(MonetaryAmount amount){
        checkAmountParameter(amount);
        if(amount.isZero()){
            return this;
        }
        // TODO add numeric check for overflow...
        return new FastMoney(this.number + getInternalNumber(amount.getNumber(), false), getCurrency());
    }

    /*
     * (non-Javadoc)
     * @see javax.money.MonetaryAmount#divide(java.lang.Number)
     */
    public FastMoney divide(Number divisor){
        checkNumber(divisor);
        if(isOne(divisor)){
            return this;
        }
        return new FastMoney(Math.round(this.number / divisor.doubleValue()), getCurrency());
    }

    /*
     * (non-Javadoc)
     * @see javax.money.MonetaryAmount#divideAndRemainder(java.lang.Number)
     */
    public FastMoney[] divideAndRemainder(Number divisor){
        checkNumber(divisor);
        if(isOne(divisor)){
            return new FastMoney[]{this, FastMoney.of(0, getCurrency())};
        }
        BigDecimal div = getBigDecimal(divisor);
        BigDecimal[] res = getBigDecimal().divideAndRemainder(div);
        return new FastMoney[]{new FastMoney(res[0], getCurrency(), true), new FastMoney(res[1], getCurrency(), true)};
    }

    /*
     * (non-Javadoc)
     * @see javax.money.MonetaryAmount#divideToIntegralValue(java.lang.Number)
     */
    public FastMoney divideToIntegralValue(Number divisor){
        checkNumber(divisor);
        if(isOne(divisor)){
            return this;
        }
        BigDecimal div = getBigDecimal(divisor);
        return new FastMoney(getBigDecimal().divideToIntegralValue(div), getCurrency(), false);
    }

    public FastMoney multiply(Number multiplicand){
        checkNumber(multiplicand);
        if(isOne(multiplicand)){
            return this;
        }
        BigDecimal mult = getBigDecimal(multiplicand);
        try{
            return new FastMoney(mult.multiply(BigDecimal.valueOf(this.number)).longValueExact(), getCurrency());
        }
        catch(ArithmeticException e){
            throw new MonetaryException("Multiplication exceeds capabilities of " + getClass().getName(), e);
        }
    }

    /*
     * (non-Javadoc)
     * @see javax.money.MonetaryAmount#negate()
     */
    public FastMoney negate(){
        return new FastMoney(this.number * -1, getCurrency());
    }

    /*
     * (non-Javadoc)
     * @see javax.money.MonetaryAmount#plus()
     */
    public FastMoney plus(){
        if(this.number >= 0){
            return this;
        }
        return new FastMoney(this.number * -1, getCurrency());
    }

    /*
     * (non-Javadoc)
     * @see javax.money.MonetaryAmount#subtract(javax.money.MonetaryAmount)
     */
    public FastMoney subtract(MonetaryAmount subtrahend){
        checkAmountParameter(subtrahend);
        if(subtrahend.isZero()){
            return this;
        }
        long subtrahendAsLong = getInternalNumber(subtrahend.getNumber(), false);
        // TODO check for numeric overflow
        return new FastMoney(this.number - subtrahendAsLong, getCurrency());
    }

    /*
     * (non-Javadoc)
     * @see javax.money.MonetaryAmount#remainder(java.lang.Number)
     */
    public FastMoney remainder(Number divisor){
        checkNumber(divisor);
        if(isOne(divisor)){
            return new FastMoney(0, getCurrency());
        }
        return new FastMoney(this.number % getInternalNumber(divisor, true), getCurrency());
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

    /*
     * (non-Javadoc)
     * @see javax.money.MonetaryAmount#scaleByPowerOfTen(int)
     */
    public FastMoney scaleByPowerOfTen(int n){
        return new FastMoney(getBigDecimal().scaleByPowerOfTen(n), getCurrency(), true);
    }

    /*
     * (non-Javadoc)
     * @see javax.money.MonetaryAmount#isZero()
     */
    public boolean isZero(){
        return this.number == 0L;
    }

    /*
     * (non-Javadoc)
     * @see javax.money.MonetaryAmount#isPositive()
     */
    public boolean isPositive(){
        return this.number > 0L;
    }

    /*
     * (non-Javadoc)
     * @see javax.money.MonetaryAmount#isPositiveOrZero()
     */
    public boolean isPositiveOrZero(){
        return this.number >= 0L;
    }

    /*
     * (non-Javadoc)
     * @see javax.money.MonetaryAmount#isNegative()
     */
    public boolean isNegative(){
        return this.number < 0L;
    }

    /*
     * (non-Javadoc)
     * @see javax.money.MonetaryAmount#isNegativeOrZero()
     */
    public boolean isNegativeOrZero(){
        return this.number <= 0L;
    }

    /*
     * (non-Javadoc)
     * @see javax.money.MonetaryAmount#getScale()
     */
    public int getScale(){
        return FastMoney.SCALE;
    }

    /*
     * (non-Javadoc)
     * @see javax.money.MonetaryAmount#getPrecision()
     */
    public int getPrecision(){
        return getNumber().numberValue(BigDecimal.class).precision();
    }

	/*
     * (non-Javadoc)
	 * @see javax.money.MonetaryAmount#signum()
	 */

    public int signum(){
        if(this.number < 0){
            return -1;
        }
        if(this.number == 0){
            return 0;
        }
        return 1;
    }

    /*
     * (non-Javadoc)
     * @see javax.money.MonetaryAmount#lessThan(javax.money.MonetaryAmount)
     */
    public boolean isLessThan(MonetaryAmount amount){
        checkAmountParameter(amount);
        return getBigDecimal().compareTo(amount.getNumber().numberValue(BigDecimal.class))<0;
    }

    /*
     * (non-Javadoc)
     * @see javax.money.MonetaryAmount#lessThan(java.lang.Number)
     */
    public boolean isLessThan(Number number){
        checkNumber(number);
        return getBigDecimal().compareTo(getBigDecimal(number))<0;
    }

    /*
     * (non-Javadoc)
     * @see javax.money.MonetaryAmount#lessThanOrEqualTo(javax.money.MonetaryAmount)
     */
    public boolean isLessThanOrEqualTo(MonetaryAmount amount){
        checkAmountParameter(amount);
        return getBigDecimal().compareTo(amount.getNumber().numberValue(BigDecimal.class))<=0;
    }

    /*
     * (non-Javadoc)
     * @see javax.money.MonetaryAmount#lessThanOrEqualTo(java.lang.Number)
     */
    public boolean isLessThanOrEqualTo(Number number){
        checkNumber(number);
        return getBigDecimal().compareTo(getBigDecimal(number))<=0;
    }

    /*
     * (non-Javadoc)
     * @see javax.money.MonetaryAmount#greaterThan(javax.money.MonetaryAmount)
     */
    public boolean isGreaterThan(MonetaryAmount amount){
        checkAmountParameter(amount);
        return getBigDecimal().compareTo(amount.getNumber().numberValue(BigDecimal.class))>0;
    }

    /*
     * (non-Javadoc)
     * @see javax.money.MonetaryAmount#greaterThan(java.lang.Number)
     */
    public boolean isGreaterThan(Number number){
        checkNumber(number);
        return getBigDecimal().compareTo(getBigDecimal(number))>0;
    }

    /*
     * (non-Javadoc)
     * @see javax.money.MonetaryAmount#greaterThanOrEqualTo(javax.money.MonetaryAmount ) #see
     */
    public boolean isGreaterThanOrEqualTo(MonetaryAmount amount){
        checkAmountParameter(amount);
        return getBigDecimal().compareTo(amount.getNumber().numberValue(BigDecimal.class))>=0;
    }

    /*
     * (non-Javadoc)
     * @see javax.money.MonetaryAmount#greaterThanOrEqualTo(java.lang.Number)
     */
    public boolean isGreaterThanOrEqualTo(Number number){
        checkNumber(number);
        return getBigDecimal().compareTo(getBigDecimal(number))>=0;
    }

    /*
     * (non-Javadoc)
     * @see javax.money.MonetaryAmount#isEqualTo(javax.money.MonetaryAmount)
     */
    public boolean isEqualTo(MonetaryAmount amount){
        checkAmountParameter(amount);
        return getBigDecimal().compareTo(amount.getNumber().numberValue(BigDecimal.class))==0;
    }

    /*
     * (non-Javadoc)
     * @see javax.money.MonetaryAmount#hasSameNumberAs(java.lang.Number)
     */
    public boolean hasSameNumberAs(Number number){
        checkNumber(number);
        try{
            return this.number == getInternalNumber(number, false);
        }
        catch(ArithmeticException e){
            return false;
        }
    }


    /**
     * Gets the number representation of the numeric value of this item.
     *
     * @return The {@link Number} represention matching best.
     */
    @Override
    public NumberValue getNumber(){
        if(numberValue == null){
            numberValue = new DefaultNumberValue(getBigDecimal());
        }
        return numberValue;
    }

    // Static Factory Methods

    /*
     * (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString(){
        return currency.toString() + ' ' + getBigDecimal();
    }

    // Internal helper methods

    /**
     * Internal method to check for correct number parameter.
     *
     * @param number
     * @throws IllegalArgumentException If the number is null
     */
    private void checkNumber(Number number){
        Objects.requireNonNull(number, "Number is required.");
    }

    /*
     * }(non-Javadoc)
     * @see javax.money.MonetaryAmount#adjust(javax.money.AmountAdjuster)
     */
    @Override
    public FastMoney with(MonetaryOperator operator){
        Objects.requireNonNull(operator);
        try{
            return FastMoney.class.cast(operator.apply(this));
        }
        catch(MonetaryException e){
            throw e;
        }
        catch(Exception e){
            throw new MonetaryException("Operator failed: " + operator, e);
        }
    }

    @Override
    public <R> R query(MonetaryQuery<R> query){
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

    public static FastMoney from(MonetaryAmount amount){
        if(FastMoney.class == amount.getClass()){
            return (FastMoney) amount;
        }else if(Money.class == amount.getClass()){
            return new FastMoney(amount.getNumber(), amount.getCurrency(), false);
        }
        return new FastMoney(amount.getNumber(), amount.getCurrency(), false);
    }

    private BigDecimal getBigDecimal(){
        return BigDecimal.valueOf(this.number).movePointLeft(SCALE);
    }

    @Override
    public MonetaryContext getMonetaryContext(){
        return MONETARY_CONTEXT;
    }

    @Override
    public FastMoney multiply(double amount){
        if(amount == 1.0){
            return this;
        }
        if(amount == 0.0){
            return new FastMoney(0, this.currency);
        }
        return new FastMoney(Math.round(this.number * amount), this.currency);
    }

    @Override
    public FastMoney divide(long amount){
        if(amount == 1L){
            return this;
        }
        return new FastMoney(this.number / amount, this.currency);
    }

    @Override
    public FastMoney divide(double number){
        if(number == 1.0d){
            return this;
        }
        return new FastMoney(Math.round(this.number / number), getCurrency());
    }

    @Override
    public FastMoney remainder(long number){
        return remainder(BigDecimal.valueOf(number));
    }

    @Override
    public FastMoney remainder(double amount){
        return remainder(new BigDecimal(String.valueOf(amount)));
    }

    @Override
    public FastMoney[] divideAndRemainder(long amount){
        return divideAndRemainder(BigDecimal.valueOf(amount));
    }

    @Override
    public FastMoney[] divideAndRemainder(double amount){
        return divideAndRemainder(new BigDecimal(String.valueOf(amount)));
    }

    @Override
    public FastMoney stripTrailingZeros(){
        return this;
    }

    @Override
    public FastMoney multiply(long multiplicand){
        if(multiplicand == 1){
            return this;
        }
        if(multiplicand == 0){
            return new FastMoney(0L, this.currency);
        }
        return new FastMoney(multiplicand * this.number, this.currency);
    }

    @Override
    public FastMoney divideToIntegralValue(long divisor){
        if(divisor == 1){
            return this;
        }
        return divideToIntegralValue(getBigDecimal(divisor));
    }

    @Override
    public FastMoney divideToIntegralValue(double divisor){
        if(divisor == 1.0){
            return this;
        }
        return divideToIntegralValue(getBigDecimal(divisor));
    }

    @Override
    protected MonetaryContext getDefaultMonetaryContext(){
        return MONETARY_CONTEXT;
    }

    @Override
    public MonetaryAmountFactory<FastMoney> getFactory(){
        return new FastMoneyAmountFactory().setAmount(this);
    }

}
