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
 */
package org.javamoney.moneta;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.ObjectStreamException;
import java.io.Serializable;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.MathContext;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicLong;

import javax.money.CurrencyUnit;
import javax.money.MonetaryAdjuster;
import javax.money.MonetaryAmount;
import javax.money.MonetaryQuery;

import org.javamoney.moneta.function.MonetaryRoundings;

/**
 * Platform RI: Default immutable implementation of {@link MonetaryAmount} based
 * on {@link BigDecimal} for the numeric representation.
 * <p>
 * As required by {@link MonetaryAmount} this class is final, thread-safe,
 * immutable and serializable.
 * 
 * @version 0.6
 * @author Anatole Tresch
 * @author Werner Keil
 */
public final class RoundedMoney implements MonetaryAmount,
		Comparable<RoundedMoney>,
		Serializable {

	public static final MathContext DEFAULT_MATH_CONTEXT = Money.DEFAULT_MATH_CONTEXT;

	/** The numeric part of this amount. */
	private BigDecimal number;

	/** The currency of this amount. */
	private CurrencyUnit currency;

	/** tHE DEFAULT {@link MathContext} used by this instance, e.g. on division. */
	private MathContext mathContext = DEFAULT_MATH_CONTEXT;

	/**
	 * The rounding to be done.
	 */
	private MonetaryAdjuster rounding;

	/**
	 * Creates a new instance os {@link RoundedMoney}.
	 * 
	 * @param currency
	 *            the currency, not null.
	 * @param number
	 *            the amount, not null.
	 */
	private RoundedMoney(CurrencyUnit currency, Number number,
			MathContext mathContext, MonetaryAdjuster rounding) {
		Objects.requireNonNull(currency, "Currency is required.");
		Objects.requireNonNull(number, "Number is required.");
		checkNumber(number);
		this.currency = currency;
		if (mathContext != null) {
			this.mathContext = mathContext;
		}
		if (rounding != null) {
			this.rounding = rounding;
		}
		else {
			this.rounding = MonetaryRoundings.getRounding(currency);
		}
		this.number = getBigDecimal(number, mathContext);
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
	public static RoundedMoney of(CurrencyUnit currency, BigDecimal number) {
		return new RoundedMoney(currency, number, DEFAULT_MATH_CONTEXT, null);
	}

	/**
	 * Translates a {@code BigDecimal} value and a {@code CurrencyUnit} currency
	 * into a {@code Money}.
	 * 
	 * @param number
	 *            numeric value of the {@code Money}.
	 * @param currency
	 *            currency unit of the {@code Money}.
	 * @param rounding
	 *            The rounding to be applied.
	 * @return a {@code Money} combining the numeric value and currency unit.
	 */
	public static RoundedMoney of(CurrencyUnit currency, BigDecimal number,
			MonetaryAdjuster rounding) {
		return new RoundedMoney(currency, number, DEFAULT_MATH_CONTEXT,
				rounding);
	}

	/**
	 * Translates a {@code BigDecimal} value and a {@code CurrencyUnit} currency
	 * into a {@code Money}.
	 * 
	 * @param number
	 *            numeric value of the {@code Money}.
	 * @param currency
	 *            currency unit of the {@code Money}.
	 * @param mathContext
	 *            the {@link MathContext} to be used.
	 * @return a {@code Money} combining the numeric value and currency unit.
	 */
	public static RoundedMoney of(CurrencyUnit currency, BigDecimal number,
			MathContext mathContext) {
		return new RoundedMoney(currency, number, mathContext, null);
	}

	/**
	 * Translates a {@code BigDecimal} value and a {@code CurrencyUnit} currency
	 * into a {@code Money}.
	 * 
	 * @param number
	 *            numeric value of the {@code Money}.
	 * @param currency
	 *            currency unit of the {@code Money}.
	 * @param mathContext
	 *            the {@link MathContext} to be used.
	 * @param rounding
	 *            The rounding to be applied.
	 * @return a {@code Money} combining the numeric value and currency unit.
	 */
	public static RoundedMoney of(CurrencyUnit currency, BigDecimal number,
			MathContext mathContext, MonetaryAdjuster rounding) {
		return new RoundedMoney(currency, number, mathContext, rounding);
	}

	/**
	 * Static factory method for creating a new instance of {@link RoundedMoney}
	 * .
	 * 
	 * @param currency
	 *            The target currency, not null.
	 * @param number
	 *            The numeric part, not null.
	 * @return A new instance of {@link RoundedMoney}.
	 */
	public static RoundedMoney of(CurrencyUnit currency, Number number) {
		return new RoundedMoney(currency, number, (MathContext) null, null);
	}

	/**
	 * Static factory method for creating a new instance of {@link RoundedMoney}
	 * .
	 * 
	 * @param currency
	 *            The target currency, not null.
	 * @param number
	 *            The numeric part, not null.
	 * @param rounding
	 *            The rounding to be applied.
	 * @return A new instance of {@link RoundedMoney}.
	 */
	public static RoundedMoney of(CurrencyUnit currency, Number number,
			MonetaryAdjuster rounding) {
		return new RoundedMoney(currency, number, (MathContext) null, rounding);
	}

	/**
	 * Static factory method for creating a new instance of {@link RoundedMoney}
	 * .
	 * 
	 * @param currency
	 *            The target currency, not null.
	 * @param number
	 *            The numeric part, not null.
	 * @return A new instance of {@link RoundedMoney}.
	 */
	public static RoundedMoney of(CurrencyUnit currency, Number number,
			MathContext mathContext) {
		return new RoundedMoney(currency, number, mathContext, null);
	}

	/**
	 * Static factory method for creating a new instance of {@link RoundedMoney}
	 * .
	 * 
	 * @param currency
	 *            The target currency, not null.
	 * @param number
	 *            The numeric part, not null.
	 * @param rounding
	 *            The rounding to be applied.
	 * @return A new instance of {@link RoundedMoney}.
	 */
	public static RoundedMoney of(CurrencyUnit currency, Number number,
			MathContext mathContext, MonetaryAdjuster rounding) {
		return new RoundedMoney(currency, number, mathContext, rounding);
	}

	/**
	 * Static factory method for creating a new instance of {@link RoundedMoney}
	 * .
	 * 
	 * @param isoCurrencyCode
	 *            The target currency as ISO currency code.
	 * @param number
	 *            The numeric part, not null.
	 * @return A new instance of {@link RoundedMoney}.
	 */
	public static RoundedMoney of(String currencyCode, Number number) {
		return new RoundedMoney(MoneyCurrency.of(currencyCode), number,
				DEFAULT_MATH_CONTEXT, null);
	}

	/**
	 * Static factory method for creating a new instance of {@link RoundedMoney}
	 * .
	 * 
	 * @param isoCurrencyCode
	 *            The target currency as ISO currency code.
	 * @param number
	 *            The numeric part, not null.
	 * @param rounding
	 *            The rounding to be applied.
	 * @return A new instance of {@link RoundedMoney}.
	 */
	public static RoundedMoney of(String currencyCode, Number number,
			MonetaryAdjuster rounding) {
		return new RoundedMoney(MoneyCurrency.of(currencyCode), number,
				DEFAULT_MATH_CONTEXT, rounding);
	}

	/**
	 * Static factory method for creating a new instance of {@link RoundedMoney}
	 * .
	 * 
	 * @param isoCurrencyCode
	 *            The target currency as ISO currency code.
	 * @param number
	 *            The numeric part, not null.
	 * @return A new instance of {@link RoundedMoney}.
	 */
	public static RoundedMoney of(String currencyCode, Number number,
			MathContext mathContext) {
		return new RoundedMoney(MoneyCurrency.of(currencyCode), number,
				mathContext, null);
	}

	/**
	 * Static factory method for creating a new instance of {@link RoundedMoney}
	 * .
	 * 
	 * @param isoCurrencyCode
	 *            The target currency as ISO currency code.
	 * @param number
	 *            The numeric part, not null.
	 * @param rounding
	 *            The rounding to be applied.
	 * @return A new instance of {@link RoundedMoney}.
	 */
	public static RoundedMoney of(String currencyCode, Number number,
			MathContext mathContext, MonetaryAdjuster rounding) {
		return new RoundedMoney(MoneyCurrency.of(currencyCode), number,
				mathContext, rounding);
	}

/**
	 * Factory method creating a zero instance with the given {@code currency);
	 * @param currency the target currency of the amount being created.
	 * @return
	 */
	public static RoundedMoney ofZero(CurrencyUnit currency) {
		return new RoundedMoney(currency, BigDecimal.ZERO,
				DEFAULT_MATH_CONTEXT, null);
	}

/**
	 * Factory method creating a zero instance with the given {@code currency);
	 * @param currency the target currency of the amount being created.
	 * @return
	 */
	public static RoundedMoney ofZero(CurrencyUnit currency,
			MonetaryAdjuster rounding) {
		return new RoundedMoney(currency, BigDecimal.ZERO,
				DEFAULT_MATH_CONTEXT, rounding);
	}

/**
	 * Factory method creating a zero instance with the given {@code currency);
	 * @param currency the target currency of the amount being created.
	 * @return
	 */
	public static RoundedMoney ofZero(String currency) {
		return ofZero(MoneyCurrency.of(currency));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.money.MonetaryAmount#getCurrency()
	 */
	public CurrencyUnit getCurrency() {
		return currency;
	}

	/**
	 * Access the {@link MathContext} used by this instance.
	 * 
	 * @return the {@link MathContext} used, never null.
	 */
	public MathContext getMathContext() {
		return this.mathContext;
	}

	/**
	 * Allows to change the {@link MathContext}. The context will used, on
	 * subsequent operation, where feasible and also propagated to child results
	 * of arithmetic calculations.
	 * 
	 * @param mathContext
	 *            The new {@link MathContext}, not null.
	 * @return a new {@link RoundedMoney} instance, with the new
	 *         {@link MathContext}.
	 */
	public RoundedMoney setMathContext(MathContext mathContext) {
		Objects.requireNonNull(mathContext, "MathContext required.");
		return new RoundedMoney(this.currency, this.number, mathContext,
				this.rounding);
	}

	public RoundedMoney abs() {
		if (this.isPositiveOrZero()) {
			return this;
		}
		return this.negate();
	}

	// Arithmetic Operations

	public RoundedMoney add(RoundedMoney amount) {
		checkAmountParameter(amount);
		return new RoundedMoney(this.currency, this.number.add(
				amount.asType(BigDecimal.class)), this.mathContext,
				this.rounding).with(rounding);
	}

	private BigDecimal getBigDecimal(Number num) {
		if (num instanceof BigDecimal) {
			return (BigDecimal) num;
		}
		if (num instanceof Long || num instanceof Integer) {
			return BigDecimal.valueOf(num.longValue());
		}
		if (num instanceof Float || num instanceof Double) {
			return new BigDecimal(num.toString());
		}
		if (num instanceof Byte || num instanceof AtomicLong) {
			return BigDecimal.valueOf(num.longValue());
		}
		try {
			// Avoid imprecise conversion to double value if at all possible
			return new BigDecimal(num.toString());
		} catch (NumberFormatException e) {
		}
		return BigDecimal.valueOf(num.doubleValue());
	}

	private BigDecimal getBigDecimal(Number num, MathContext mathContext) {
		if (num instanceof BigDecimal) {
			BigDecimal bd = (BigDecimal) num;
			if (mathContext != null) {
				return new BigDecimal(bd.toString(), mathContext);
			}
			return bd;
		}
		if (num instanceof Long || num instanceof Integer) {
			if (mathContext != null) {
				return new BigDecimal(num.longValue(), mathContext);
			}
			return BigDecimal.valueOf(num.longValue());
		}
		if (num instanceof Float || num instanceof Double) {
			if (mathContext != null) {
				return new BigDecimal(num.doubleValue(), mathContext);
			}
			return new BigDecimal(num.toString());
		}
		if (num instanceof Byte || num instanceof AtomicLong) {
			if (mathContext != null) {
				return new BigDecimal(num.longValue(), mathContext);
			}
			return BigDecimal.valueOf(num.longValue());
		}
		try {
			// Avoid imprecise conversion to double value if at all possible
			if (mathContext != null) {
				return new BigDecimal(num.toString(), mathContext);
			}
			return new BigDecimal(num.toString());
		} catch (NumberFormatException e) {
		}
		if (mathContext != null) {
			return new BigDecimal(num.doubleValue(), mathContext);
		}
		return BigDecimal.valueOf(num.doubleValue());
	}

	public RoundedMoney divide(RoundedMoney divisor) {
		checkAmountParameter(divisor);
		BigDecimal dec = this.number.divide(divisor.asType(BigDecimal.class),
				this.mathContext);
		return new RoundedMoney(this.currency, dec, this.mathContext,
				this.rounding)
				.with(rounding);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.money.MonetaryAmount#divide(javax.money.MonetaryAmount)
	 */
	public RoundedMoney divide(Number divisor) {
		BigDecimal dec = this.number.divide(getBigDecimal(divisor),
				this.mathContext);
		return new RoundedMoney(this.currency, dec, this.mathContext,
				this.rounding)
				.with(rounding);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * javax.money.MonetaryAmount#divideAndRemainder(javax.money.MonetaryAmount)
	 */
	public RoundedMoney[] divideAndRemainder(MonetaryAmount divisor) {
		checkAmountParameter(divisor);
		BigDecimal[] dec = this.number.divideAndRemainder(
				Money.from(divisor).asType(BigDecimal.class), this.mathContext);
		return new RoundedMoney[] {
				new RoundedMoney(this.currency, dec[0], this.mathContext,
						this.rounding),
				new RoundedMoney(this.currency, dec[1], this.mathContext,
						this.rounding)
						.with(rounding) };
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * javax.money.MonetaryAmount#divideAndRemainder(javax.money.MonetaryAmount)
	 */
	public RoundedMoney[] divideAndRemainder(Number divisor) {
		BigDecimal[] dec = this.number.divideAndRemainder(
				getBigDecimal(divisor), this.mathContext);
		return new RoundedMoney[] {
				new RoundedMoney(this.currency, dec[0], this.mathContext,
						this.rounding),
				new RoundedMoney(this.currency, dec[1], this.mathContext,
						this.rounding)
						.with(rounding) };
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * javax.money.MonetaryAmount#divideToIntegralValue(javax.money.MonetaryAmount
	 * )
	 */
	public RoundedMoney divideToIntegralValue(MonetaryAmount divisor) {
		checkAmountParameter(divisor);
		BigDecimal dec = this.number.divideToIntegralValue(
				Money.from(divisor).asType(BigDecimal.class), this.mathContext);
		return new RoundedMoney(this.currency, dec, this.mathContext,
				this.rounding);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.money.MonetaryAmount#divideToIntegralValue(Number) )D
	 */
	public RoundedMoney divideToIntegralValue(Number divisor) {
		BigDecimal dec = this.number.divideToIntegralValue(
				getBigDecimal(divisor), this.mathContext);
		return new RoundedMoney(this.currency, dec, this.mathContext,
				this.rounding);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.money.MonetaryAmount#multiply(javax.money.MonetaryAmount)
	 */
	public RoundedMoney multiply(MonetaryAmount multiplicand) {
		checkAmountParameter(multiplicand);
		BigDecimal dec = this.number.multiply(
				Money.from(multiplicand).asNumber(),
				this.mathContext);
		return new RoundedMoney(this.currency, dec, this.mathContext,
				this.rounding);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.money.MonetaryAmount#multiply(Number)
	 */
	public RoundedMoney multiply(Number multiplicand) {
		BigDecimal dec = this.number.multiply(getBigDecimal(multiplicand),
				this.mathContext);
		return new RoundedMoney(this.currency, dec, this.mathContext,
				this.rounding)
				.with(rounding);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.money.MonetaryAmount#negate()
	 */
	public RoundedMoney negate() {
		return new RoundedMoney(this.currency,
				this.number.negate(this.mathContext),
				this.mathContext, this.rounding);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.money.MonetaryAmount#plus()
	 */
	public RoundedMoney plus() {
		return new RoundedMoney(this.currency,
				this.number.plus(this.mathContext),
				this.mathContext, this.rounding);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.money.MonetaryAmount#subtract(javax.money.MonetaryAmount)
	 */
	public RoundedMoney subtract(RoundedMoney subtrahend) {
		checkAmountParameter(subtrahend);
		if (subtrahend.isZero()) {
			return this;
		}
		return new RoundedMoney(this.currency, this.number.subtract(
				subtrahend.asType(BigDecimal.class), this.mathContext),
				this.mathContext, this.rounding);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.money.MonetaryAmount#pow(int)
	 */
	public RoundedMoney pow(int n) {
		return new RoundedMoney(this.currency, this.number.pow(n,
				this.mathContext),
				this.mathContext, this.rounding);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.money.MonetaryAmount#ulp()
	 */
	public RoundedMoney ulp() {
		return new RoundedMoney(this.currency, this.number.ulp(),
				DEFAULT_MATH_CONTEXT, this.rounding);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.money.MonetaryAmount#remainder(javax.money.MonetaryAmount)
	 */
	public RoundedMoney remainder(MonetaryAmount divisor) {
		checkAmountParameter(divisor);
		return new RoundedMoney(this.currency, this.number.remainder(
				Money.from(divisor).asType(BigDecimal.class), this.mathContext),
				this.mathContext, this.rounding);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.money.MonetaryAmount#remainder(Number)
	 */
	public RoundedMoney remainder(Number divisor) {
		return new RoundedMoney(this.currency, this.number.remainder(
				getBigDecimal(divisor), this.mathContext),
				this.mathContext, this.rounding);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.money.MonetaryAmount#scaleByPowerOfTen(int)
	 */
	public RoundedMoney scaleByPowerOfTen(int n) {
		return new RoundedMoney(this.currency,
				this.number.scaleByPowerOfTen(n),
				this.mathContext, this.rounding);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.money.MonetaryAmount#isZero()
	 */
	public boolean isZero() {
		return this.number.signum() == 0;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.money.MonetaryAmount#isPositive()
	 */
	public boolean isPositive() {
		return signum() == 1;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.money.MonetaryAmount#isPositiveOrZero()
	 */
	public boolean isPositiveOrZero() {
		return signum() >= 0;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.money.MonetaryAmount#isNegative()
	 */
	public boolean isNegative() {
		return signum() == -1;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.money.MonetaryAmount#isNegativeOrZero()
	 */
	public boolean isNegativeOrZero() {
		return signum() <= 0;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.money.MonetaryAmount#with(java.lang.Number)
	 */
	public RoundedMoney with(Number amount) {
		checkNumber(amount);
		return new RoundedMoney(this.currency, getBigDecimal(amount),
				this.mathContext, this.rounding);
	}

	/**
	 * Creates a new Money instance, by just replacing the {@link CurrencyUnit}.
	 * 
	 * @param currency
	 *            the currency unit to be replaced, not {@code null}
	 * @return the new amount with the same numeric value and
	 *         {@link MathContext}, but the new {@link CurrencyUnit}.
	 */
	public RoundedMoney with(CurrencyUnit currency) {
		Objects.requireNonNull(currency, "currency required");
		return new RoundedMoney(currency, asType(BigDecimal.class),
				this.mathContext, this.rounding);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.money.MonetaryAmount#with(CurrencyUnit, java.lang.Number)
	 */
	public RoundedMoney with(CurrencyUnit currency, Number amount) {
		checkNumber(amount);
		return new RoundedMoney(currency, getBigDecimal(amount),
				this.mathContext, this.rounding);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.money.MonetaryAmount#getScale()
	 */
	public int getScale() {
		return this.number.scale();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.money.MonetaryAmount#getPrecision()
	 */
	public int getPrecision() {
		return this.number.precision();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.money.MonetaryAmount#longValue()
	 */
	public long longValue() {
		return this.number.longValue();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.money.MonetaryAmount#longValueExact()
	 */
	public long longValueExact() {
		return this.number.longValueExact();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.money.MonetaryAmount#doubleValue()
	 */
	public double doubleValue() {
		// TODO round!
		return this.number.doubleValue();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.money.MonetaryAmount#signum()
	 */

	public int signum() {
		return this.number.signum();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.money.MonetaryAmount#toEngineeringString()
	 */
	public String toEngineeringString() {
		return this.currency.getCurrencyCode() + ' '
				+ this.number.toEngineeringString();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.money.MonetaryAmount#toPlainString()
	 */
	public String toPlainString() {
		return this.currency.getCurrencyCode() + ' '
				+ this.number.toPlainString();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.money.MonetaryAmount#lessThan(javax.money.MonetaryAmount)
	 */
	public boolean isLessThan(RoundedMoney amount) {
		checkAmountParameter(amount);
		return number.compareTo(amount.number) < 0;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * javax.money.MonetaryAmount#lessThanOrEqualTo(javax.money.MonetaryAmount)
	 */
	public boolean isLessThanOrEqualTo(RoundedMoney amount) {
		checkAmountParameter(amount);
		return number.compareTo(amount.number) <= 0;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.money.MonetaryAmount#greaterThan(javax.money.MonetaryAmount)
	 */
	public boolean isGreaterThan(RoundedMoney amount) {
		checkAmountParameter(amount);
		return number.compareTo(amount.number) > 0;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * javax.money.MonetaryAmount#greaterThanOrEqualTo(javax.money.MonetaryAmount
	 * ) #see
	 */
	public boolean isGreaterThanOrEqualTo(RoundedMoney amount) {
		checkAmountParameter(amount);
		return number.compareTo(amount.number) >= 0;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.money.MonetaryAmount#isEqualTo(javax.money.MonetaryAmount)
	 */
	public boolean isEqualTo(RoundedMoney amount) {
		checkAmountParameter(amount);
		return number.compareTo(amount.asType(BigDecimal.class)) == 0;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.money.MonetaryAmount#isNotEqualTo(javax.money.MonetaryAmount)
	 */
	public boolean isNotEqualTo(RoundedMoney amount) {
		checkAmountParameter(amount);
		return number.compareTo(amount.number) != 0;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.money.MonetaryAmount#getNumberType()
	 */
	public Class<?> getNumberType() {
		return BigDecimal.class;
	}

	/*
	 * }(non-Javadoc)
	 * 
	 * @see javax.money.MonetaryAmount#adjust(javax.money.AmountAdjuster)
	 */
	@Override
	public RoundedMoney with(MonetaryAdjuster operation) {
		return RoundedMoney.from(operation.adjustInto(this));
	}

	public static RoundedMoney from(MonetaryAmount amt) {
		if (amt.getClass() == RoundedMoney.class) {
			return (RoundedMoney) amt;
		}
		if (amt.getClass() == FastMoney.class) {
			return RoundedMoney.of(amt.getCurrency(),
					((FastMoney) amt).asNumber(),
					DEFAULT_MATH_CONTEXT);
		}
		else if (amt.getClass() == Money.class) {
			return RoundedMoney.of(amt.getCurrency(), ((Money) amt).asNumber(),
					DEFAULT_MATH_CONTEXT);
		}
		return RoundedMoney.of(amt.getCurrency(), Money.asNumber(amt),
				DEFAULT_MATH_CONTEXT);
	}

	/*
	 * }(non-Javadoc)
	 * 
	 * @see javax.money.MonetaryAmount#adjust(javax.money.AmountAdjuster)
	 */
	@Override
	public <T> T query(MonetaryQuery<T> function) {
		return function.queryFrom(this);
	}

	/*
	 * @see javax.money.MonetaryAmount#asType(java.lang.Class)
	 */
	@SuppressWarnings("unchecked")
	public <T> T asType(Class<T> type) {
		if (BigDecimal.class.equals(type)) {
			return (T) this.number;
		}
		if (Number.class.equals(type)) {
			final T asType = (T) this.number;
			return asType;
		}
		if (Double.class.equals(type)) {
			return (T) Double.valueOf(this.number.doubleValue());
		}
		if (Float.class.equals(type)) {
			return (T) Float.valueOf(this.number.floatValue());
		}
		if (Long.class.equals(type)) {
			return (T) Long.valueOf(this.number.longValue());
		}
		if (Integer.class.equals(type)) {
			return (T) Integer.valueOf(this.number.intValue());
		}
		if (Short.class.equals(type)) {
			return (T) Short.valueOf(this.number.shortValue());
		}
		if (Byte.class.equals(type)) {
			return (T) Byte.valueOf(this.number.byteValue());
		}
		if (BigInteger.class.equals(type)) {
			return (T) this.number.toBigInteger();
		}
		throw new IllegalArgumentException("Unsupported representation type: "
				+ type);
	}

	/*
	 * }(non-Javadoc)
	 * 
	 * @see javax.money.MonetaryAmount#asType(java.lang.Class,
	 * javax.money.Rounding)
	 */
	public <T> T asType(Class<T> type, MonetaryAdjuster adjuster) {
		RoundedMoney amount = (RoundedMoney) adjuster.adjustInto(this);
		return amount.asType(type);
	}

	private void writeObject(ObjectOutputStream oos) throws IOException {
		oos.writeObject(this.number);
		oos.writeObject(this.mathContext);
		oos.writeObject(this.currency);
	}

	private void readObject(ObjectInputStream ois) throws IOException,
			ClassNotFoundException {
		this.number = (BigDecimal) ois.readObject();
		this.mathContext = (MathContext) ois.readObject();
		this.currency = (CurrencyUnit) ois.readObject();
	}

	private void readObjectNoData()
			throws ObjectStreamException {
		if (this.number == null) {
			this.number = BigDecimal.ZERO;
		}
		if (this.mathContext == null) {
			this.mathContext = DEFAULT_MATH_CONTEXT;
		}
		if (this.currency == null) {
			this.currency = MoneyCurrency.of(
					"XXX"); // no currency
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return currency.getCurrencyCode() + ' ' + number;
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
		return prime * result + asNumberStripped().hashCode();
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
		RoundedMoney other = (RoundedMoney) obj;
		if (currency == null) {
			if (other.currency != null)
				return false;
		} else if (!currency.equals(other.currency))
			return false;
		return asNumberStripped().equals(other.asNumberStripped());
	}

	/*
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	public int compareTo(RoundedMoney o) {
		Objects.requireNonNull(o);
		int compare = -1;
		if (this.currency.equals(o.getCurrency())) {
			compare = asNumberStripped().compareTo(RoundedMoney.from(o).asNumberStripped());
		} else {
			compare = this.currency.getCurrencyCode().compareTo(
					o.getCurrency().getCurrencyCode());
		}
		return compare;
	}

	/**
	 * Platform RI: This is an inner checker class for aspects of
	 * {@link MonetaryAmount}. It may be used by multiple implementations
	 * (inside the same package) to avoid code duplication.
	 * 
	 * This class is for internal use only.
	 * 
	 * @author Werner Keil
	 */
	static final class Checker {
		private Checker() {
		}

		/**
		 * Internal method to check for correct number parameter.
		 * 
		 * @param number
		 * @throws IllegalArgumentException
		 *             If the number is null
		 */
		static final void checkNumber(Number number) {
			Objects.requireNonNull(number, "Number is required.");
		}

		/**
		 * Method to check if a currency is compatible with this amount
		 * instance.
		 * 
		 * @param amount
		 *            The monetary amount to be compared to, never null.
		 * @throws IllegalArgumentException
		 *             If the amount is null, or the amount's currency is not
		 *             compatible (same {@link CurrencyUnit#getNamespace()} and
		 *             same {@link CurrencyUnit#getCurrencyCode()}).
		 */
		static final void checkAmountParameter(CurrencyUnit currency,
				MonetaryAmount amount) {
			Objects.requireNonNull(amount, "Amount must not be null.");
			final CurrencyUnit amountCurrency = amount.getCurrency();
			if (!(currency.getCurrencyCode().equals(amountCurrency
					.getCurrencyCode()))) {
				throw new CurrencyMismatchException(currency, amountCurrency);
			}
		}
	}

	@Override
	public long getAmountWhole() {
		return this.number.longValue();
	}

	@Override
	public long getAmountFractionNumerator() {
		MoneyCurrency mc = MoneyCurrency.from(currency);
		if (mc.getDefaultFractionDigits() >= 0) {
			BigDecimal bd = this.number.remainder(BigDecimal.ONE);
			return bd.movePointRight(mc.getDefaultFractionDigits()).longValue();
		}
		return 0L;
	}

	@Override
	public long getAmountFractionDenominator() {
		MoneyCurrency mc = MoneyCurrency.from(currency);
		if (mc.getDefaultFractionDigits() >= 0) {
			return BigDecimal.valueOf(10)
					.pow(mc.getDefaultFractionDigits())
					.longValue();
		}
		return 1L;
	}

	public Number asNumber() {
		return this.number;
	}
	
	/**
	 * Method that returns BigDecimal.ZERO, if {@link #isZero()}, and #number
	 * {@link #stripTrailingZeros()} in all other cases.
	 * 
	 * @return the stripped number value.
	 */
	public BigDecimal asNumberStripped() {
		if (isZero()) {
			return BigDecimal.ZERO;
		}
		return this.number.stripTrailingZeros();
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
		Objects.requireNonNull(amount, "Amount must not be null.");
		final CurrencyUnit amountCurrency = amount.getCurrency();
		if (!(this.currency
				.getCurrencyCode().equals(amountCurrency.getCurrencyCode()))) {
			throw new IllegalArgumentException("Currency mismatch: "
					+ this.currency + '/' + amountCurrency);
		}
	}

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
}
