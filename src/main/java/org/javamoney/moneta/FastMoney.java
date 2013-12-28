/*
 * Copyright (c) 2012, 2013, Credit Suisse (Anatole Tresch), Werner Keil. Licensed under the Apache
 * License, Version 2.0 (the "License"); you may not use this file except in compliance with the
 * License. You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License. Contributors: Anatole Tresch - initial implementation Werner Keil - extensions and
 * adaptions.
 */
package org.javamoney.moneta;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;






// github.com/JavaMoney/jsr354-ri.git
import javax.money.CurrencyUnit;
import javax.money.MonetaryAmount;
import javax.money.MonetaryAmountFactory;
import javax.money.MonetaryContext;
import javax.money.MonetaryCurrencies;
import javax.money.MonetaryOperator;
import javax.money.MonetaryQuery;
import javax.money.NumberValue;

import org.javamoney.moneta.impl.FastMoneyAmountFactory;
import org.javamoney.moneta.spi.AbstractMoney;
import org.javamoney.moneta.spi.DefaultNumberValue;

/**
 * <type>long</type> based implementation of {@link MonetaryAmount}. This class internally uses a
 * single long number as numeric representation, which basically is interpreted as minor units.<br/>
 * It suggested to have a performance advantage of a 10-15 times faster compared to {@link Money},
 * which internally uses {@link BigDecimal}. Nevertheless this comes with a price of less precision.
 * As an example performing the following calculation one million times, results in slightly
 * different results:
 * 
 * <pre>
 * Money money1 = money1.add(Money.of(EURO, 1234567.3444));
 * money1 = money1.subtract(Money.of(EURO, 232323));
 * money1 = money1.multiply(3.4);
 * money1 = money1.divide(5.456);
 * </pre>
 * 
 * Executed one million (1000000) times this results in {@code EUR 1657407.962529182}, calculated in
 * 3680 ms, or roughly 3ns/loop.
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
 * executed one million (1000000) times results in {@code EUR 1657407.96251}, calculated in 179 ms,
 * which is less than 1ns/loop.
 * <p>
 * Also note than mixing up types my drastically change the performance behavior. E.g. replacing the
 * code above with the following: *
 * 
 * <pre>
 * FastMoney money1 = money1.add(Money.of(EURO, 1234567.3444));
 * money1 = money1.subtract(FastMoney.of(EURO, 232323));
 * money1 = money1.multiply(3.4);
 * money1 = money1.divide(5.456);
 * </pre>
 * 
 * executed one million (1000000) times may execute significantly longer, since monetary amount type
 * conversion is involved.
 * <p>
 * Basically, when mixing amount implementations, the performance of the amount, on which most of
 * the operations are operated, has the most significant impact on the overall performance behavior.
 * 
 * @version 0.5.2
 * @author Anatole Tresch
 * @author Werner Keil
 */
public final class FastMoney extends AbstractMoney implements
		Comparable<MonetaryAmount>, Serializable {

	private static final long serialVersionUID = 1L;

	/** The numeric part of this amount. */
	private long number;

	/** The number value. */
	private transient NumberValue numberValue;

	/** The current scale represented by the number. */
	private static final int SCALE = 5;

	/** the {@link MonetaryContext} used by this instance, e.g. on division. */
	private static final MonetaryContext MONETARY_CONTEXT = new MonetaryContext.Builder(
			FastMoney.class)
			.setMaxScale(SCALE).setFixedScale(true)
			.setPrecision(String.valueOf(Integer.MAX_VALUE).length())
			.build();

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
		this.number = getInternalNumber(number);
		this.numberValue = new DefaultNumberValue(number);
	}

	/**
	 * Creates a new instance os {@link FastMoney}.
	 * 
	 * @param currency
	 *            the currency, not null.
	 * @param number
	 *            the amount, not null.
	 */
	private FastMoney(CurrencyUnit currency, NumberValue numberBinding) {
		super(currency, MONETARY_CONTEXT);
		Objects.requireNonNull(numberBinding, "Number is required.");
		this.number = getInternalNumber(numberBinding
				.numberValue(BigDecimal.class));
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
	 * @param numberBinding
	 *            The numeric part, not null.
	 * @return A new instance of {@link FastMoney}.
	 */
	public static FastMoney of(CurrencyUnit currency, NumberValue numberBinding) {
		return new FastMoney(currency, numberBinding);
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
		return new FastMoney(currency, number);
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
		return of(currency, number);
	}

	/*
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	public int compareTo(MonetaryAmount o) {
		int compare = -1;
		if (this.currency.equals(o.getCurrency())) {
			return getNumber().numberValue(BigDecimal.class).compareTo(
					o.getNumber().numberValue(BigDecimal.class));
		}
		return compare;
	}

	/*
	 * (non-Javadoc)
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
	 * @see javax.money.MonetaryAmount#getCurrency()
	 */
	public CurrencyUnit getCurrency() {
		return currency;
	}

	/*
	 * (non-Javadoc)
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
	 * @see javax.money.MonetaryAmount#add(javax.money.MonetaryAmount)
	 */
	public FastMoney add(MonetaryAmount amount) {
		checkAmountParameter(amount);
		return new FastMoney(getCurrency(), this.number
				+ getInternalNumber(amount.getNumber()));
	}

	/*
	 * (non-Javadoc)
	 * @see javax.money.MonetaryAmount#divide(java.lang.Number)
	 */
	public FastMoney divide(Number divisor) {
		checkNumber(divisor);
		return new FastMoney(getCurrency(), Math.round(this.number
				/ divisor.doubleValue()));
	}

	/*
	 * (non-Javadoc)
	 * @see javax.money.MonetaryAmount#divideAndRemainder(java.lang.Number)
	 */
	public FastMoney[] divideAndRemainder(Number divisor) {
		checkNumber(divisor);
		BigDecimal div = getBigDecimal(divisor);
		BigDecimal[] res = getBigDecimal().divideAndRemainder(div);
		return new FastMoney[] {
				new FastMoney(getCurrency(), res[0]),
				new FastMoney(getCurrency(), res[1]) };
	}

	/*
	 * (non-Javadoc)
	 * @see javax.money.MonetaryAmount#divideToIntegralValue(java.lang.Number)
	 */
	public FastMoney divideToIntegralValue(Number divisor) {
		checkNumber(divisor);
		BigDecimal div = getBigDecimal(divisor);
		return new FastMoney(getCurrency(), getBigDecimal()
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
	 * @see javax.money.MonetaryAmount#negate()
	 */
	public FastMoney negate() {
		return new FastMoney(getCurrency(), this.number * -1);
	}

	/*
	 * (non-Javadoc)
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
	 * @see javax.money.MonetaryAmount#subtract(javax.money.MonetaryAmount)
	 */
	public FastMoney subtract(MonetaryAmount subtrahend) {
		checkAmountParameter(subtrahend);
		if (subtrahend.isZero()) {
			return this;
		}
		return new FastMoney(getCurrency(), this.number
				- getInternalNumber(subtrahend.getNumber()));
	}

	/*
	 * (non-Javadoc)
	 * @see javax.money.MonetaryAmount#remainder(java.lang.Number)
	 */
	public FastMoney remainder(Number divisor) {
		checkNumber(divisor);
		return new FastMoney(getCurrency(), this.number
				% getInternalNumber(divisor));
	}

	/*
	 * (non-Javadoc)
	 * @see javax.money.MonetaryAmount#scaleByPowerOfTen(int)
	 */
	public FastMoney scaleByPowerOfTen(int n) {
		return new FastMoney(getCurrency(), getBigDecimal()
				.scaleByPowerOfTen(n));
	}

	/*
	 * (non-Javadoc)
	 * @see javax.money.MonetaryAmount#isZero()
	 */
	public boolean isZero() {
		return this.number == 0L;
	}

	/*
	 * (non-Javadoc)
	 * @see javax.money.MonetaryAmount#isPositive()
	 */
	public boolean isPositive() {
		return this.number > 0L;
	}

	/*
	 * (non-Javadoc)
	 * @see javax.money.MonetaryAmount#isPositiveOrZero()
	 */
	public boolean isPositiveOrZero() {
		return this.number >= 0L;
	}

	/*
	 * (non-Javadoc)
	 * @see javax.money.MonetaryAmount#isNegative()
	 */
	public boolean isNegative() {
		return this.number < 0L;
	}

	/*
	 * (non-Javadoc)
	 * @see javax.money.MonetaryAmount#isNegativeOrZero()
	 */
	public boolean isNegativeOrZero() {
		return this.number <= 0L;
	}

	/*
	 * (non-Javadoc)
	 * @see javax.money.MonetaryAmount#getScale()
	 */
	public int getScale() {
		return FastMoney.SCALE;
	}

	/*
	 * (non-Javadoc)
	 * @see javax.money.MonetaryAmount#getPrecision()
	 */
	public int getPrecision() {
		return getNumber().numberValue(BigDecimal.class).precision();
	}

	/*
	 * (non-Javadoc)
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
	 * @see javax.money.MonetaryAmount#lessThan(javax.money.MonetaryAmount)
	 */
	public boolean isLessThan(MonetaryAmount amount) {
		checkAmountParameter(amount);
		return this.number < getInternalNumber(amount.getNumber());
	}

	/*
	 * (non-Javadoc)
	 * @see javax.money.MonetaryAmount#lessThan(java.lang.Number)
	 */
	public boolean isLessThan(Number number) {
		checkNumber(number);
		return this.number < getInternalNumber(number);
	}

	/*
	 * (non-Javadoc)
	 * @see javax.money.MonetaryAmount#lessThanOrEqualTo(javax.money.MonetaryAmount)
	 */
	public boolean isLessThanOrEqualTo(MonetaryAmount amount) {
		checkAmountParameter(amount);
		return this.number <= getInternalNumber(amount.getNumber());
	}

	/*
	 * (non-Javadoc)
	 * @see javax.money.MonetaryAmount#lessThanOrEqualTo(java.lang.Number)
	 */
	public boolean isLessThanOrEqualTo(Number number) {
		checkNumber(number);
		return this.number <= getInternalNumber(number);
	}

	/*
	 * (non-Javadoc)
	 * @see javax.money.MonetaryAmount#greaterThan(javax.money.MonetaryAmount)
	 */
	public boolean isGreaterThan(MonetaryAmount amount) {
		checkAmountParameter(amount);
		return this.number > getInternalNumber(amount.getNumber());
	}

	/*
	 * (non-Javadoc)
	 * @see javax.money.MonetaryAmount#greaterThan(java.lang.Number)
	 */
	public boolean isGreaterThan(Number number) {
		checkNumber(number);
		return this.number > getInternalNumber(number);
	}

	/*
	 * (non-Javadoc)
	 * @see javax.money.MonetaryAmount#greaterThanOrEqualTo(javax.money.MonetaryAmount ) #see
	 */
	public boolean isGreaterThanOrEqualTo(MonetaryAmount amount) {
		checkAmountParameter(amount);
		return this.number >= getInternalNumber(amount.getNumber());
	}

	/*
	 * (non-Javadoc)
	 * @see javax.money.MonetaryAmount#greaterThanOrEqualTo(java.lang.Number)
	 */
	public boolean isGreaterThanOrEqualTo(Number number) {
		checkNumber(number);
		return this.number >= getInternalNumber(number);
	}

	/*
	 * (non-Javadoc)
	 * @see javax.money.MonetaryAmount#isEqualTo(javax.money.MonetaryAmount)
	 */
	public boolean isEqualTo(MonetaryAmount amount) {
		checkAmountParameter(amount);
		return this.number == getInternalNumber(amount.getNumber());
	}

	/*
	 * (non-Javadoc)
	 * @see javax.money.MonetaryAmount#hasSameNumberAs(java.lang.Number)
	 */
	public boolean hasSameNumberAs(Number number) {
		checkNumber(number);
		return this.number == getInternalNumber(number);
	}

	/*
	 * (non-Javadoc)
	 * @see javax.money.MonetaryAmount#isNotEqualTo(javax.money.MonetaryAmount)
	 */
	public boolean isNotEqualTo(MonetaryAmount amount) {
		checkAmountParameter(amount);
		return this.number != getInternalNumber(amount.getNumber());
	}

	/*
	 * (non-Javadoc)
	 * @see javax.money.MonetaryAmount#isNotEqualTo(java.lang.Number)
	 */
	public boolean isNotEqualTo(Number number) {
		checkNumber(number);
		return this.number != getInternalNumber(number);
	}

	/**
	 * Gets the number representation of the numeric value of this item.
	 * 
	 * @return The {@link Number} represention matching best.
	 */
	@Override
	public NumberValue getNumber() {
		if (numberValue == null) {
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
	private void checkNumber(Number number) {
		Objects.requireNonNull(number, "Number is required.");
	}

	/*
	 * }(non-Javadoc)
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

	public static FastMoney from(MonetaryAmount amount) {
		if (FastMoney.class == amount.getClass()) {
			return (FastMoney) amount;
		}
		else if (Money.class == amount.getClass()) {
			return new FastMoney(amount.getCurrency(),
					amount.getNumber());
		}
		return new FastMoney(amount.getCurrency(),
				amount.getNumber());
	}

	private BigDecimal getBigDecimal() {
		return BigDecimal.valueOf(this.number).movePointLeft(SCALE);
	}

	@Override
	public MonetaryContext getMonetaryContext() {
		return MONETARY_CONTEXT;
	}

	@Override
	public FastMoney multiply(double amount) {
		if (amount == 1.0) {
			return this;
		}
		if (amount == 0.0) {
			return new FastMoney(this.currency, 0);
		}
		return new FastMoney(this.currency, Math.round(this.number * amount));
	}

	@Override
	public FastMoney divide(long amount) {
		if (amount == 1) {
			return this;
		}
		return new FastMoney(this.currency, this.number / amount);
	}

	@Override
	public FastMoney divide(double number) {
		return new FastMoney(getCurrency(), Math.round(this.number
				/ number));
	}

	@Override
	public FastMoney remainder(long number) {
		return remainder(BigDecimal.valueOf(number));
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
		if (multiplicand == 1) {
			return this;
		}
		if (multiplicand == 0) {
			return new FastMoney(this.currency, 0L);
		}
		return new FastMoney(this.currency, multiplicand * this.number);
	}

	@Override
	public FastMoney divideToIntegralValue(long divisor) {
		if (divisor == 1) {
			return this;
		}
		return divideToIntegralValue(getBigDecimal(divisor));
	}

	@Override
	public FastMoney divideToIntegralValue(double divisor) {
		if (divisor == 1.0) {
			return this;
		}
		return divideToIntegralValue(getBigDecimal(divisor));
	}

	@Override
	protected MonetaryContext getDefaultMonetaryContext() {
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
		 * @see java.lang.Object#toString()
		 */
		@Override
		public String toString() {
			return "LRUMap [cacheSize=" + cacheSize + "]";
		}

	}

	@Override
	public MonetaryAmountFactory getFactory() {
		return new FastMoneyAmountFactory().with(this);
	}

}
