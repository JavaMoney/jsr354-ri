/*
 * Copyright (c) 2012, 2013, Credit Suisse (Anatole Tresch), Werner Keil.
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
 * 
 * Contributors: Anatole Tresch - initial implementation Werner Keil -
 * extensions and adaptions.
 */
package org.javamoney.moneta;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.MathContext;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

// github.com/JavaMoney/jsr354-ri.git
import javax.money.CurrencyUnit;
import javax.money.MonetaryAmount;
import javax.money.MonetaryContext;
import javax.money.MonetaryCurrencies;
import javax.money.MonetaryOperator;
import javax.money.MonetaryQuery;

/**
 * <type>long</type> based implementation of {@link MonetaryAmount}. This class
 * internally uses a single long number as numeric reporesentation, which
 * basically is interpreted as minor units.<br/>
 * It suggested to have a performance advantage of a 10-15 times faster compared
 * to {@link Money}, which interally uses {@link BigDecimal}. Nevertheless this
 * comes with a price of less precision. As an example performing the following
 * calulcation one milltion times, results in slightly different results:
 * 
 * <pre>
 * Money money1 = money1.add(Money.of(EURO, 1234567.3444));
 * money1 = money1.subtract(Money.of(EURO, 232323));
 * money1 = money1.multiply(3.4);
 * money1 = money1.divide(5.456);
 * </pre>
 * 
 * Executed one million (1000000) times this results in
 * {@code EUR 1657407.962529182}, calculated in 3680 ms, or roughly 3ns/loop.
 * <p>
 * whrereas
 * 
 * <pre>
 * FastMoney money1 = money1.add(FastMoney.of(EURO, 1234567.3444));
 * money1 = money1.subtract(FastMoney.of(EURO, 232323));
 * money1 = money1.multiply(3.4);
 * money1 = money1.divide(5.456);
 * </pre>
 * 
 * executed one million (1000000) times results in {@code EUR 1657407.96251},
 * calculated in 179 ms, which is less than 1ns/loop.
 * <p>
 * Also note than mixxing up types my drastically change the performance
 * behaviour. E.g. replacing the code above with the following: *
 * 
 * <pre>
 * FastMoney money1 = money1.add(Money.of(EURO, 1234567.3444));
 * money1 = money1.subtract(FastMoney.of(EURO, 232323));
 * money1 = money1.multiply(3.4);
 * money1 = money1.divide(5.456);
 * </pre>
 * 
 * executed one million (1000000) times may execute significantly longer, since
 * monetary amount type conversion is involved.
 * 
 * @version 0.5
 * @author Anatole Tresch
 * @author Werner Keil
 */
public final class FastMoney extends AbstractMoney<FastMoney> implements
		Comparable<MonetaryAmount<?>>, Serializable {

	private static final long serialVersionUID = 1L;

	/** The numeric part of this amount. */
	private long number;

	/** The current scale represented by the number. */
	private static final int SCALE = 5;

	private static final long SCALING_DENOMINATOR = 100000L;

	/** the {@link MonetaryContext} used by this instance, e.g. on division. */
	private static final MonetaryContext<FastMoney> MONETARY_CONTEXT = new MonetaryContext.Builder()
			.setMaxScale(SCALE).setFixedScale(true)
			.setPrecision(String.valueOf(Integer.MAX_VALUE).length())
			.build(FastMoney.class);

	private static final Map<String, FastMoney> CACHE = Collections
			.synchronizedMap(new LRUMap<String, FastMoney>(1000));
	private static final ThreadLocal<StringBuilder> builders = new ThreadLocal<StringBuilder>() {
		@Override
		protected StringBuilder initialValue() {
			return new StringBuilder();
		}
	};

	/**
	 * Required for deserialization only.
	 */
	private FastMoney() {
	}

	/**
	 * Creates a new instance os {@link FastMoney}.
	 * 
	 * @param currency
	 *            the currency, not null.
	 * @param number
	 *            the amount, not null.
	 */
	private FastMoney(CurrencyUnit currency, Number number) {
		super(currency, MONETARY_CONTEXT);
		Objects.requireNonNull(number, "Number is required.");
		checkNumber(number);
		this.number = getInternalNumber(number);
	}

	private long getInternalNumber(Number number) {
		BigDecimal bd = getBigDecimal(number);
		return bd.movePointRight(SCALE).longValue();
	}

	private FastMoney(CurrencyUnit currency, long number) {
		super(currency, MONETARY_CONTEXT);
		Objects.requireNonNull(currency, "Currency is required.");
		this.currency = currency;
		this.number = number;
	}

	/**
	 * Static factory method for creating a new instance of {@link FastMoney}.
	 * 
	 * @param currency
	 *            The target currency, not null.
	 * @param number
	 *            The numeric part, not null.
	 * @return A new instance of {@link FastMoney}.
	 */
	public static FastMoney of(CurrencyUnit currency, Number number) {
		String numString = number.toString();
		FastMoney cached = getFromCache(currency, numString);
		if (cached != null) {
			return cached;
		}
		FastMoney fm = new FastMoney(currency, number);
		storeInCache(currency, numString, fm);
		return fm;
	}

	/**
	 * Static factory method for creating a new instance of {@link FastMoney}.
	 * 
	 * @param currencyCode
	 *            The target currency as currency code.
	 * @param number
	 *            The numeric part, not null.
	 * @return A new instance of {@link FastMoney}.
	 */
	public static FastMoney of(String currencyCode, Number number) {
		CurrencyUnit currency = MonetaryCurrencies.getCurrency(currencyCode);
		String numString = number.toString();
		FastMoney cached = getFromCache(currency, numString);
		if (cached != null) {
			return cached;
		}
		FastMoney fm = new FastMoney(
				currency,
				number);
		storeInCache(currency, numString, fm);
		return fm;
	}

/**
	 * Factory method creating a zero instance with the given {@code currency);
	 * @param currency the target currency of the amount being created.
	 * @return
	 */
	public static FastMoney ofZero(CurrencyUnit currency) {
		FastMoney cached = getFromCache(currency, "0");
		if (cached != null) {
			return cached;
		}
		FastMoney fm = new FastMoney(currency, 0L);
		storeInCache(currency, "0", fm);
		return fm;
	}

/**
	 * Factory method creating a zero instance with the given {@code currency);
	 * @param currency the target currency of the amount being created.
	 * @return
	 */
	public static FastMoney ofZero(String currencyCode) {
		CurrencyUnit unitUnit = MonetaryCurrencies.getCurrency(currencyCode);
		FastMoney cached = getFromCache(unitUnit, "0");
		if (cached != null) {
			return cached;
		}
		FastMoney fm = new FastMoney(unitUnit, 0L);
		storeInCache(unitUnit, "0", fm);
		return fm;
	}

	private static FastMoney getFromCache(CurrencyUnit unit, String amt) {
		StringBuilder builder = builders.get();
		builder.setLength(0);
		builder.append(unit).append('-').append(amt);
		return CACHE.get(builder.toString());
	}

	private static void storeInCache(CurrencyUnit unit, String amt, FastMoney fm) {
		StringBuilder builder = builders.get();
		builder.setLength(0);
		builder.append(unit).append('-').append(amt);
		CACHE.put(builder.toString(), fm);
	}

	/*
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	public int compareTo(MonetaryAmount<?> o) {
		int compare = -1;
		if (this.currency.equals(o.getCurrency())) {
			return getNumber(BigDecimal.class).compareTo(
					o.getNumber(BigDecimal.class));
		}
		return compare;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((currency == null) ? 0 : currency.hashCode());
		result = prime * result + (int) number;
		return result;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		FastMoney other = (FastMoney) obj;
		if (currency == null) {
			if (other.getCurrency() != null)
				return false;
		} else if (!currency.equals(other.getCurrency()))
			return false;
		if (number != other.number)
			return false;
		return true;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.money.MonetaryAmount#getCurrency()
	 */
	public CurrencyUnit getCurrency() {
		return currency;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.money.MonetaryAmount#abs()
	 */
	public FastMoney abs() {
		if (this.isPositiveOrZero()) {
			return this;
		}
		return this.negate();
	}

	// Arithmetic Operations

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.money.MonetaryAmount#add(javax.money.MonetaryAmount)
	 */
	public FastMoney add(MonetaryAmount<?> amount) {
		checkAmountParameter(amount);
		return new FastMoney(getCurrency(), this.number
				+ FastMoney.from(amount).number);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.money.MonetaryAmount#divide(java.lang.Number)
	 */
	public FastMoney divide(Number divisor) {
		checkNumber(divisor);
		return new FastMoney(getCurrency(), Math.round(this.number
				/ divisor.doubleValue()));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.money.MonetaryAmount#divideAndRemainder(java.lang.Number)
	 */
	public FastMoney[] divideAndRemainder(Number divisor) {
		checkNumber(divisor);
		BigDecimal div = getBigDecimal(divisor);
		BigDecimal[] res = getNumber(BigDecimal.class).divideAndRemainder(div);
		return new FastMoney[] {
				new FastMoney(getCurrency(), res[0]),
				new FastMoney(getCurrency(), res[1]) };
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.money.MonetaryAmount#divideToIntegralValue(java.lang.Number)
	 */
	public FastMoney divideToIntegralValue(Number divisor) {
		checkNumber(divisor);
		BigDecimal div = getBigDecimal(divisor);
		return new FastMoney(getCurrency(), getNumber(BigDecimal.class)
				.divideToIntegralValue(div));
	}

	public FastMoney multiply(Number multiplicand) {
		checkNumber(multiplicand);
		double multiplicandNum = multiplicand.doubleValue();
		return new FastMoney(getCurrency(),
				Math.round(this.number * multiplicandNum));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.money.MonetaryAmount#negate()
	 */
	public FastMoney negate() {
		return new FastMoney(getCurrency(), this.number * -1);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.money.MonetaryAmount#plus()
	 */
	public FastMoney plus() {
		if (this.number >= 0) {
			return this;
		}
		return new FastMoney(getCurrency(), this.number * -1);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.money.MonetaryAmount#subtract(javax.money.MonetaryAmount)
	 */
	public FastMoney subtract(MonetaryAmount<?> subtrahend) {
		checkAmountParameter(subtrahend);
		if (FastMoney.from(subtrahend).isZero()) {
			return this;
		}
		return new FastMoney(getCurrency(), this.number
				- FastMoney.from(subtrahend).number);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.money.MonetaryAmount#pow(int)
	 */
	public FastMoney pow(int n) {
		return with(getNumber(BigDecimal.class).pow(n));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.money.MonetaryAmount#ulp()
	 */
	public FastMoney ulp() {
		return with(getNumber(BigDecimal.class).ulp());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.money.MonetaryAmount#remainder(java.lang.Number)
	 */
	public FastMoney remainder(Number divisor) {
		checkNumber(divisor);
		return new FastMoney(getCurrency(), this.number
				% getInternalNumber(divisor));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.money.MonetaryAmount#scaleByPowerOfTen(int)
	 */
	public FastMoney scaleByPowerOfTen(int n) {
		return new FastMoney(getCurrency(), getNumber(BigDecimal.class)
				.scaleByPowerOfTen(n));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.money.MonetaryAmount#isZero()
	 */
	public boolean isZero() {
		return this.number == 0L;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.money.MonetaryAmount#isPositive()
	 */
	public boolean isPositive() {
		return this.number > 0L;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.money.MonetaryAmount#isPositiveOrZero()
	 */
	public boolean isPositiveOrZero() {
		return this.number >= 0L;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.money.MonetaryAmount#isNegative()
	 */
	public boolean isNegative() {
		return this.number < 0L;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.money.MonetaryAmount#isNegativeOrZero()
	 */
	public boolean isNegativeOrZero() {
		return this.number <= 0L;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.money.MonetaryAmount#with(java.lang.Number)
	 */
	public FastMoney with(Number number) {
		return new FastMoney(getCurrency(), getInternalNumber(number));
	}

	/**
	 * Creates a new FastMoney instance, by just replacing the
	 * {@link CurrencyUnit} and the numeric amount.
	 * 
	 * @param currency
	 *            the currency unit to be replaced, not {@code null}
	 * @return the new amount with the same numeric value and
	 *         {@link MathContext}, but the new {@link CurrencyUnit}.
	 */
	public FastMoney with(CurrencyUnit currency, Number amount) {
		checkNumber(amount);
		return new FastMoney(currency, getBigDecimal(amount));
	}

	/**
	 * Creates a new Money instance, by just replacing the {@link CurrencyUnit}.
	 * 
	 * @param currency
	 *            the currency unit to be replaced, not {@code null}
	 * @return the new amount with the same numeric value and
	 *         {@link MathContext}, but the new {@link CurrencyUnit}.
	 */
	public FastMoney with(CurrencyUnit currency) {
		Objects.requireNonNull(currency, "currency required");
		return new FastMoney(currency, this.number);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.money.MonetaryAmount#getScale()
	 */
	public int getScale() {
		return FastMoney.SCALE;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.money.MonetaryAmount#getPrecision()
	 */
	public int getPrecision() {
		if (this.number < 0) {
			return String.valueOf(this.number).length() - 1;
		}
		return String.valueOf(this.number).length();
	}

	public long longValue() {
		return this.number / SCALING_DENOMINATOR;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.money.MonetaryAmount#longValueExact()
	 */
	public long longValueExact() {
		if ((this.number % SCALING_DENOMINATOR) == 0) {
			return this.number / SCALING_DENOMINATOR;
		}
		throw new ArithmeticException("Amount has fractions: " + this);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.money.MonetaryAmount#doubleValue()
	 */
	public double doubleValue() {
		return ((double) this.number) / SCALING_DENOMINATOR;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.money.MonetaryAmount#signum()
	 */

	public int signum() {
		if (this.number < 0) {
			return -1;
		}
		if (this.number == 0) {
			return 0;
		}
		return 1;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.money.MonetaryAmount#toEngineeringString()
	 */
	public String toEngineeringString() {
		return getCurrency().getCurrencyCode() + ' '
				+ getBigDecimal().toEngineeringString();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.money.MonetaryAmount#toPlainString()
	 */
	public String toPlainString() {
		return getCurrency().getCurrencyCode() + ' '
				+ getBigDecimal().toPlainString();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.money.MonetaryAmount#lessThan(javax.money.MonetaryAmount)
	 */
	public boolean isLessThan(MonetaryAmount<?> amount) {
		checkAmountParameter(amount);
		return this.number < FastMoney.from(amount).number;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.money.MonetaryAmount#lessThan(java.lang.Number)
	 */
	public boolean isLessThan(Number number) {
		checkNumber(number);
		return this.number < getInternalNumber(number);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * javax.money.MonetaryAmount#lessThanOrEqualTo(javax.money.MonetaryAmount)
	 */
	public boolean isLessThanOrEqualTo(MonetaryAmount<?> amount) {
		checkAmountParameter(amount);
		return this.number <= FastMoney.from(amount).number;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.money.MonetaryAmount#lessThanOrEqualTo(java.lang.Number)
	 */
	public boolean isLessThanOrEqualTo(Number number) {
		checkNumber(number);
		return this.number <= getInternalNumber(number);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.money.MonetaryAmount#greaterThan(javax.money.MonetaryAmount)
	 */
	public boolean isGreaterThan(MonetaryAmount<?> amount) {
		checkAmountParameter(amount);
		return this.number > FastMoney.from(amount).number;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.money.MonetaryAmount#greaterThan(java.lang.Number)
	 */
	public boolean isGreaterThan(Number number) {
		checkNumber(number);
		return this.number > getInternalNumber(number);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * javax.money.MonetaryAmount#greaterThanOrEqualTo(javax.money.MonetaryAmount
	 * ) #see
	 */
	public boolean isGreaterThanOrEqualTo(MonetaryAmount<?> amount) {
		checkAmountParameter(amount);
		return this.number >= FastMoney.from(amount).number;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.money.MonetaryAmount#greaterThanOrEqualTo(java.lang.Number)
	 */
	public boolean isGreaterThanOrEqualTo(Number number) {
		checkNumber(number);
		return this.number >= getInternalNumber(number);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.money.MonetaryAmount#isEqualTo(javax.money.MonetaryAmount)
	 */
	public boolean isEqualTo(MonetaryAmount<?> amount) {
		checkAmountParameter(amount);
		return this.number == FastMoney.from(amount).number;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.money.MonetaryAmount#hasSameNumberAs(java.lang.Number)
	 */
	public boolean hasSameNumberAs(Number number) {
		checkNumber(number);
		return this.number == getInternalNumber(number);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.money.MonetaryAmount#isNotEqualTo(javax.money.MonetaryAmount)
	 */
	public boolean isNotEqualTo(MonetaryAmount<?> amount) {
		checkAmountParameter(amount);
		return this.number != FastMoney.from(amount).number;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.money.MonetaryAmount#isNotEqualTo(java.lang.Number)
	 */
	public boolean isNotEqualTo(Number number) {
		checkNumber(number);
		return this.number != getInternalNumber(number);
	}

	/*
	 * @see javax.money.MonetaryAmount#asType(java.lang.Class)
	 */
	@SuppressWarnings("unchecked")
	public <N extends Number> N getNumber(Class<N> type) {
		if (BigDecimal.class.equals(type)) {
			return (N) getBigDecimal();
		}
		if (Number.class.equals(type)) {
			return (N) getBigDecimal();
		}
		if (Double.class.equals(type)) {
			return (N) Double.valueOf(getBigDecimal().doubleValue());
		}
		if (Float.class.equals(type)) {
			return (N) Float.valueOf(getBigDecimal().floatValue());
		}
		if (Long.class.equals(type)) {
			return (N) Long.valueOf(getBigDecimal().longValue());
		}
		if (Integer.class.equals(type)) {
			return (N) Integer.valueOf(getBigDecimal().intValue());
		}
		if (Short.class.equals(type)) {
			return (N) Short.valueOf(getBigDecimal().shortValue());
		}
		if (Byte.class.equals(type)) {
			return (N) Byte.valueOf(getBigDecimal().byteValue());
		}
		if (BigInteger.class.equals(type)) {
			return (N) BigInteger.valueOf(getBigDecimal().longValue());
		}
		throw new IllegalArgumentException("Unsupported representation type: "
				+ type);
	}

	/**
	 * Gets the number representation of the numeric value of this item.
	 * 
	 * @return The {@link Number} represention matching best.
	 */
	public BigDecimal getNumber() {
		return getBigDecimal();
	}

	// Static Factory Methods
	/**
	 * Translates a {@code BigDecimal} value and a {@code CurrencyUnit} currency
	 * into a {@code Money}.
	 * 
	 * @param number
	 *            numeric value of the {@code Money}.
	 * @param currency
	 *            currency unit of the {@code Money}.
	 * @return a {@code Money} combining the numeric value and currency unit.
	 */
	public static FastMoney of(CurrencyUnit currency, BigDecimal number) {
		FastMoney cached = getFromCache(currency, number.toString());
		if (cached != null) {
			return cached;
		}
		return new FastMoney(currency, number);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return currency.toString() + ' ' + getBigDecimal();
	}

	// Internal helper methods

	/**
	 * Internal method to check for correct number parameter.
	 * 
	 * @param number
	 * @throws IllegalArgumentException
	 *             If the number is null
	 */
	public void checkNumber(Number number) {
		Objects.requireNonNull(number, "Number is required.");
	}

	/*
	 * }(non-Javadoc)
	 * 
	 * @see javax.money.MonetaryAmount#adjust(javax.money.AmountAdjuster)
	 */
	@Override
	public FastMoney with(MonetaryOperator adjuster) {
		return (FastMoney) adjuster.apply(this);
	}

	@Override
	public <R> R query(MonetaryQuery<R> query) {
		return query.queryFrom(this);
	}

	public static FastMoney from(MonetaryAmount<?> amount) {
		if (FastMoney.class == amount.getClass()) {
			return (FastMoney) amount;
		}
		else if (Money.class == amount.getClass()) {
			return new FastMoney(amount.getCurrency(),
					amount.getNumber(BigDecimal.class));
		}
		return new FastMoney(amount.getCurrency(),
				amount.getNumber(BigDecimal.class));
	}

	private BigDecimal getBigDecimal() {
		return BigDecimal.valueOf(this.number).movePointLeft(SCALE);
	}

	// @Override
	// public long getAmountWhole() {
	// return longValue();
	// }
	//
	// @Override
	// public long getAmountFractionNumerator() {
	// return this.number % SCALING_DENOMINATOR;
	// }
	//
	// @Override
	// public long getAmountFractionDenominator() {
	// return SCALING_DENOMINATOR;
	// }

	@SuppressWarnings("unchecked")
	@Override
	public <N extends Number> N getNumberExact(Class<N> type) {
		if (BigDecimal.class.equals(type)) {
			return (N) getBigDecimal();
		}
		if (Number.class.equals(type)) {
			return (N) getBigDecimal();
		}
		if (Double.class.equals(type)) {
			Double d = Double.valueOf(getBigDecimal().doubleValue());
			if (d.equals(Double.NEGATIVE_INFINITY)
					|| d.equals(Double.NEGATIVE_INFINITY)) {
				throw new ArithmeticException(this.number
						+ " is out of range for double.");
			}
			return (N) d;
		}
		if (Float.class.equals(type)) {
			Float f = Float.valueOf(getBigDecimal().floatValue());
			if (f.equals(Float.NEGATIVE_INFINITY)
					|| f.equals(Float.NEGATIVE_INFINITY)) {
				throw new ArithmeticException(this.number
						+ " is out of range for float.");
			}
			return (N) f;
		}
		if (Long.class.equals(type)) {
			return (N) Long.valueOf(getBigDecimal().longValueExact());
		}
		if (Integer.class.equals(type)) {
			return (N) Integer.valueOf(getBigDecimal().intValueExact());
		}
		if (Short.class.equals(type)) {
			return (N) Short.valueOf(getBigDecimal().shortValueExact());
		}
		if (Byte.class.equals(type)) {
			return (N) Byte.valueOf(getBigDecimal().byteValueExact());
		}
		if (BigInteger.class.equals(type)) {
			return (N) getBigDecimal().toBigInteger();
		}
		throw new IllegalArgumentException("Unsupported representation type: "
				+ type);
	}

	@Override
	public MonetaryContext<FastMoney> getMonetaryContext() {
		return MONETARY_CONTEXT;
	}

	@Override
	public FastMoney with(CurrencyUnit unit, long amount) {
		return of(unit, amount);
	}

	@Override
	public FastMoney with(CurrencyUnit unit, double amount) {
		return of(unit, new BigDecimal(String.valueOf(amount)));
	}

	@Override
	public FastMoney multiply(double amount) {
		return multiply(new BigDecimal(String.valueOf(amount)));
	}

	@Override
	public FastMoney divide(long amount) {
		return new FastMoney(this.currency, this.number / amount);
	}

	@Override
	public FastMoney divide(double amount) {
		return divide(new BigDecimal(String.valueOf(amount)));
	}

	@Override
	public FastMoney remainder(long amount) {
		return remainder(new BigDecimal(amount));
	}

	@Override
	public FastMoney remainder(double amount) {
		return remainder(new BigDecimal(String.valueOf(amount)));
	}

	@Override
	public FastMoney[] divideAndRemainder(long amount) {
		return divideAndRemainder(BigDecimal.valueOf(amount));
	}

	@Override
	public FastMoney[] divideAndRemainder(double amount) {
		return divideAndRemainder(new BigDecimal(String.valueOf(amount)));
	}

	@Override
	public FastMoney stripTrailingZeros() {
		return this;
	}

	@Override
	public FastMoney multiply(long multiplicand) {
		return multiply(getBigDecimal(multiplicand));
	}

	@Override
	public FastMoney divideToIntegralValue(long divisor) {
		return divideToIntegralValue(getBigDecimal(divisor));
	}

	@Override
	public FastMoney divideToIntegralValue(double divisor) {
		return divideToIntegralValue(getBigDecimal(divisor));
	}

	@Override
	protected MonetaryContext<FastMoney> getDefaultMonetaryContext() {
		return MONETARY_CONTEXT;
	}

	private static final class LRUMap<K, V> extends LinkedHashMap<K, V> {

		/**
		 * serialVersionUID.
		 */
		private static final long serialVersionUID = -3609851324668582780L;
		private int cacheSize = 200;

		public LRUMap(int cacheSize) {
			super();
			if (cacheSize < 0) {
				throw new IllegalArgumentException(
						"cacheSize must >= 0, 0 = unlimited");
			}
			this.cacheSize = cacheSize;
		}

		@Override
		protected boolean removeEldestEntry(java.util.Map.Entry<K, V> eldest) {
			if (cacheSize == 0) {
				return false;
			}
			if (size() > cacheSize) {
				return true;
			}
			return false;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see java.lang.Object#toString()
		 */
		@Override
		public String toString() {
			return "LRUMap [cacheSize=" + cacheSize + "]";
		}

	}

}
