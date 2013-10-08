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

import javax.money.CurrencyUnit;
import javax.money.MonetaryAdjuster;
import javax.money.MonetaryAmount;
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
public final class FastMoney implements MonetaryAmount,
		Comparable<FastMoney>, Serializable {

	private static final long serialVersionUID = 1L;

	/** The numeric part of this amount. */
	private final long number;

	/** The current scale represented by the number. */
	private static final int SCALE = 5;

	private static final long SCALING_DENOMINATOR = 100000L;

	/** The currency of this amount. */
	private final CurrencyUnit currency;

	/**
	 * Creates a new instance os {@link FastMoney}.
	 * 
	 * @param currency
	 *            the currency, not null.
	 * @param number
	 *            the amount, not null.
	 */
	private FastMoney(CurrencyUnit currency, Number number) {
		if (currency == null) {
			throw new IllegalArgumentException("Currency is required.");
		}
		if (number == null) {
			throw new IllegalArgumentException("Number is required.");
		}
		checkNumber(number);
		this.currency = currency;
		this.number = getInternalNumber(number);
	}

	private long getInternalNumber(Number number) {
		// long whole = number.longValue();
		// double fractions = number.doubleValue() % 1;
		// return (whole * SCALING_DENOMINATOR)
		// + ((long) (fractions * SCALING_DENOMINATOR));
		BigDecimal bd = getBigDecimal(number);
		return bd.movePointRight(SCALE).longValue();
	}

	private static long getInternalNumber(MonetaryAmount amount) {
		BigDecimal bd = Money.asNumber(amount);
		return bd.movePointRight(SCALE).longValue();
		//
		// long fractionNumerator = amount.getAmountFractionNumerator();
		// long divisor = amount.getAmountFractionDenominator()
		// * 10 / SCALING_DENOMINATOR;
		//
		// // Variant BD: slow!
		// // BigDecimal fraction =
		// BigDecimal.valueOf(fractionNumerator).divide(
		// // BigDecimal.valueOf(divisor));
		// // return (amount.getAmountWhole() * SCALING_DENOMINATOR) +
		// fraction.longValue();
		//
		// // Variant double
		// double fraction = ((double) fractionNumerator) / divisor;
		// return (amount.getAmountWhole() * SCALING_DENOMINATOR) +
		// (long)fraction;
		//
		// // Variant number: fastest!
		// // return (long)(amount.asNumber().doubleValue() *
		// SCALING_DENOMINATOR);
	}

	private static double getInternalDouble(MonetaryAmount amount) {
		if (amount.getClass() == Money.class) {
			return ((Money) amount).asNumber().doubleValue();
		}

		long fractionNumerator = amount.getAmountFractionNumerator();
		long divisor = amount.getAmountFractionDenominator() / SCALING_DENOMINATOR;

		double fraction = ((double) fractionNumerator) / divisor / SCALING_DENOMINATOR;
		return amount.getAmountWhole() +
				fraction;
	}

	// private static double getDoubleValue(MonetaryAmount amount) {
	// long fraction = amount.getAmountFractionNumerator();
	// double divisor = ((double) amount.getAmountFractionDenominator())
	// / SCALING_DENOMINATOR;
	// double fractionNum = (long) (fraction / divisor);
	// return ((double) amount.getAmountWhole())
	// + (fractionNum / SCALING_DENOMINATOR);
	// }

	private FastMoney(CurrencyUnit currency, long number) {
		if (currency == null) {
			throw new IllegalArgumentException("Currency is required.");
		}
		this.currency = currency;
		this.number = number;
	}

	private BigDecimal getBigDecimal(Number num) {
		if (num instanceof BigDecimal) {
			return (BigDecimal) num;
		}
		if (num instanceof Long || num instanceof Integer
				|| num instanceof Byte) {
			return BigDecimal.valueOf(num.longValue());
		}
		if (num instanceof Float || num instanceof Double) {
			return new BigDecimal(num.toString());
		}
		try {
			// Avoid imprecise conversion to double value if at all possible
			return new BigDecimal(num.toString());
		} catch (NumberFormatException e) {
		}
		return BigDecimal.valueOf(num.doubleValue());
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
		// TODO caching
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
		return new FastMoney(MoneyCurrency.of(currencyCode), number);
	}

/**
	 * Facory method creating a zero instance with the given {@code currency);
	 * @param currency the target currency of the amount being created.
	 * @return
	 */
	public static MonetaryAmount zero(CurrencyUnit currency) {
		return new FastMoney(currency, 0L);
	}

	/**
	 * Get the number represnetation type, E.g. {@code java.math.BigDecimal}.
	 * 
	 * @return
	 */
	public static Class<?> getNumberClass() {
		return Long.class;
	}

	/*
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	public int compareTo(FastMoney o) {
		int compare = -1;
		if (this.currency.equals(o.getCurrency())) {
			if (this.number < o.number) {
				compare = 0;
			} else if (this.number < o.number) {
				compare = -1;
			} else {
				compare = 1;
			}
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
	public FastMoney add(MonetaryAmount amount) {
		checkAmountParameter(amount);
		return new FastMoney(getCurrency(), this.number
				+ FastMoney.from(amount).number);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.money.MonetaryAmount#divide(javax.money.MonetaryAmount)
	 */
	public FastMoney divide(MonetaryAmount divisor) {
		checkAmountParameter(divisor);
		double factor = getInternalDouble(divisor);
		if (divisor.getClass() == FastMoney.class) {
			return new FastMoney(getCurrency(), Math.round(this.number
					/ factor));
		}
		double divNum = getInternalDouble(divisor);
		BigDecimal div = Money.asNumber(divisor);
		return new FastMoney(getCurrency(), Math.round(this.number
				/ divNum));
		// return new FastMoney(getCurrency(), getBigDecimal().divide(div,
		// MathContext.DECIMAL64));
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
	 * @see
	 * javax.money.MonetaryAmount#divideAndRemainder(javax.money.MonetaryAmount)
	 */
	public FastMoney[] divideAndRemainder(MonetaryAmount divisor) {
		checkAmountParameter(divisor);
		double divNum = getInternalDouble(divisor);
		return new FastMoney[] {
				new FastMoney(getCurrency(), Math.round(this.number
						/ divNum)),
				new FastMoney(getCurrency(), Math.round(this.number % Math.round((divNum * SCALING_DENOMINATOR)))) };
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.money.MonetaryAmount#divideAndRemainder(java.lang.Number)
	 */
	public FastMoney[] divideAndRemainder(Number divisor) {
		checkNumber(divisor);
		double divNum = divisor.doubleValue();
		return new FastMoney[] {
				new FastMoney(getCurrency(), Math.round(this.number
						/ divNum)),
				new FastMoney(getCurrency(), Math.round(this.number % Math.round((divNum * SCALING_DENOMINATOR)))) };
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * javax.money.MonetaryAmount#divideToIntegralValue(javax.money.MonetaryAmount
	 * )
	 */
	public FastMoney divideToIntegralValue(MonetaryAmount divisor) {
		checkAmountParameter(divisor);
		long divisorNum = getInternalNumber(divisor);
		return new FastMoney(getCurrency(),
				(Number) ((this.number / divisorNum) / SCALING_DENOMINATOR));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.money.MonetaryAmount#divideToIntegralValue(java.lang.Number)
	 */
	public FastMoney divideToIntegralValue(Number divisor) {
		checkNumber(divisor);
		long divisorNum = getInternalNumber(divisor);
		long result = Math.round(this.number / divisor.doubleValue());
		long remainder = result % SCALING_DENOMINATOR;
		result -= remainder;
		return new FastMoney(getCurrency(),
				result);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.money.MonetaryAmount#multiply(javax.money.MonetaryAmount)
	 */
	public FastMoney multiply(MonetaryAmount multiplicand) {
		checkAmountParameter(multiplicand);
		double multiplicandNum = getInternalDouble(multiplicand);
		return new FastMoney(getCurrency(),
				Math.round(this.number * multiplicandNum));
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
		if (this.number <= 0) {
			return this;
		}
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
	public FastMoney subtract(MonetaryAmount subtrahend) {
		checkAmountParameter(subtrahend);
		return new FastMoney(getCurrency(), this.number
				- FastMoney.from(subtrahend).number);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.money.MonetaryAmount#pow(int)
	 */
	public FastMoney pow(int n) {
		return with(asType(BigDecimal.class).pow(n));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.money.MonetaryAmount#ulp()
	 */
	public FastMoney ulp() {
		return with(asType(BigDecimal.class).ulp());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.money.MonetaryAmount#remainder(javax.money.MonetaryAmount)
	 */
	public FastMoney remainder(MonetaryAmount divisor) {
		checkAmountParameter(divisor);
		return new FastMoney(getCurrency(), this.number
				% getInternalNumber(divisor));
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
		long result = this.number;
		for (int i = 0; i < n; i++) {
			result *= 10;
		}
		return new FastMoney(getCurrency(), result);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.money.MonetaryAmount#isZero()
	 */
	public boolean isZero() {
		return this.number != 0L;
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

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.money.MonetaryAmount#getScale()
	 */
	public int getScale() {
		return this.SCALE;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.money.MonetaryAmount#getPrecision()
	 */
	public int getPrecision() {
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
		return getBigDecimal().toEngineeringString();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.money.MonetaryAmount#toPlainString()
	 */
	public String toPlainString() {
		return getBigDecimal().toPlainString();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.money.MonetaryAmount#lessThan(javax.money.MonetaryAmount)
	 */
	public boolean isLessThan(MonetaryAmount amount) {
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
	public boolean isLessThanOrEqualTo(MonetaryAmount amount) {
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
	public boolean isGreaterThan(MonetaryAmount amount) {
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
	public boolean isGreaterThanOrEqualTo(MonetaryAmount amount) {
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
	public boolean isEqualTo(MonetaryAmount amount) {
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
	public boolean isNotEqualTo(MonetaryAmount amount) {
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
	 * (non-Javadoc)
	 * 
	 * @see javax.money.MonetaryAmount#getNumberType()
	 */
	public Class<?> getNumberType() {
		return Long.class;
	}

	/*
	 * @see javax.money.MonetaryAmount#asType(java.lang.Class)
	 */
	@SuppressWarnings("unchecked")
	public <T> T asType(Class<T> type) {
		if (BigDecimal.class.equals(type)) {
			return (T) getBigDecimal();
		}
		if (Number.class.equals(type)) {
			return (T) getBigDecimal();
		}
		if (Double.class.equals(type)) {
			return (T) Double.valueOf(getBigDecimal().doubleValue());
		}
		if (Float.class.equals(type)) {
			return (T) Float.valueOf(getBigDecimal().floatValue());
		}
		if (Long.class.equals(type)) {
			return (T) Long.valueOf(getBigDecimal().longValue());
		}
		if (Integer.class.equals(type)) {
			return (T) Integer.valueOf(getBigDecimal().intValue());
		}
		if (Short.class.equals(type)) {
			return (T) Short.valueOf(getBigDecimal().shortValue());
		}
		if (Byte.class.equals(type)) {
			return (T) Byte.valueOf(getBigDecimal().byteValue());
		}
		if (BigInteger.class.equals(type)) {
			return (T) BigInteger.valueOf(getBigDecimal().longValue());
		}
		throw new IllegalArgumentException("Unsupported representation type: "
				+ type);
	}

	/**
	 * Gets the number representation of the numeric value of this item.
	 * 
	 * @return The {@link Number} represention matching best.
	 */
	public Number asNumber() {
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
		if (number == null) {
			throw new IllegalArgumentException("Number is required.");
		}
	}

	/**
	 * Method to check if a currency is compatible with this amount instance.
	 * 
	 * @param amount
	 *            The monetary amount to be compared to, never null.
	 * @throws IllegalArgumentException
	 *             If the amount is null, or the amount's currency is not
	 *             compatible (same {@link CurrencyUnit#getNamespace()} and same
	 *             {@link CurrencyUnit#getCurrencyCode()}).
	 */
	private void checkAmountParameter(MonetaryAmount amount) {
		if (amount == null) {
			throw new IllegalArgumentException("Amount must not be null.");
		}
		final CurrencyUnit amountCurrency = amount.getCurrency();
		if (!(this.currency
				.getCurrencyCode().equals(amountCurrency.getCurrencyCode()))) {
			throw new IllegalArgumentException("Currency mismatch: "
					+ this.currency + '/' + amountCurrency);
		}
	}

	/*
	 * }(non-Javadoc)
	 * 
	 * @see javax.money.MonetaryAmount#adjust(javax.money.AmountAdjuster)
	 */
	@Override
	public MonetaryAmount with(MonetaryAdjuster adjuster) {
		return adjuster.adjustInto(this);
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
					((Money) amount).asNumber());
		}
		return new FastMoney(amount.getCurrency(),
				Money.asNumber(amount));
	}

	private BigDecimal getBigDecimal() {
		return BigDecimal.valueOf(this.number).movePointLeft(SCALE);
	}

	@Override
	public long getAmountWhole() {
		return longValue();
	}

	@Override
	public long getAmountFractionNumerator() {
		return this.number % SCALING_DENOMINATOR;
	}

	@Override
	public long getAmountFractionDenominator() {
		return SCALING_DENOMINATOR;
	}

}
