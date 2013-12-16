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
import java.math.RoundingMode;
import java.util.Objects;

import javax.money.CurrencyUnit;
import javax.money.MonetaryAmount;
import javax.money.MonetaryContext;
import javax.money.MonetaryCurrencies;
import javax.money.MonetaryOperator;
import javax.money.MonetaryQuery;
import javax.money.MonetaryRoundings;

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
public final class RoundedMoney extends AbstractMoney<RoundedMoney> implements
		Comparable<MonetaryAmount<?>>,
		Serializable {

	/**
	 * serialVersionUID.
	 */
	private static final long serialVersionUID = 366517590511294389L;
	/** The default {@link MonetaryContext} applied. */
	public static final MonetaryContext<RoundedMoney> DEFAULT_MONETARY_CONTEXT = MonetaryContext
			.from(Money.DEFAULT_MONETARY_CONTEXT, RoundedMoney.class);

	/** The numeric part of this amount. */
	private BigDecimal number;

	/**
	 * The rounding to be done.
	 */
	private MonetaryOperator rounding;

	/**
	 * Required for deserialization only.
	 */
	private RoundedMoney() {
	}

	/**
	 * Creates a new instance os {@link RoundedMoney}.
	 * 
	 * @param currency
	 *            the currency, not null.
	 * @param number
	 *            the amount, not null.
	 */
	private RoundedMoney(CurrencyUnit currency, Number number,
			MonetaryContext<RoundedMoney> monetaryContext,
			MonetaryOperator rounding) {
		super(currency, monetaryContext);
		Objects.requireNonNull(number, "Number is required.");
		checkNumber(number);
		this.currency = currency;
		if (rounding != null) {
			this.rounding = rounding;
		}
		else {
			this.rounding = MonetaryRoundings.getRounding(currency);
		}
		this.number = getBigDecimal(number, monetaryContext);
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
		return new RoundedMoney(currency, number, DEFAULT_MONETARY_CONTEXT,
				null);
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
			MonetaryOperator rounding) {
		return new RoundedMoney(currency, number, DEFAULT_MONETARY_CONTEXT,
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
	 * @param monetaryContext
	 *            the {@link MathContext} to be used.
	 * @return a {@code Money} combining the numeric value and currency unit.
	 */
	public static RoundedMoney of(CurrencyUnit currency, BigDecimal number,
			MonetaryContext<?> monetaryContext) {
		return new RoundedMoney(currency, number, MonetaryContext.from(
				monetaryContext, RoundedMoney.class), null);
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
			MonetaryContext<?> monetaryContext, MonetaryOperator rounding) {
		return new RoundedMoney(currency, number, MonetaryContext.from(
				monetaryContext, RoundedMoney.class), rounding);
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
		return new RoundedMoney(currency, number,
				(MonetaryContext<RoundedMoney>) null, null);
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
			MonetaryOperator rounding) {
		return new RoundedMoney(currency, number,
				(MonetaryContext<RoundedMoney>) null,
				rounding);
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
			MonetaryContext<?> monetaryContext) {
		return new RoundedMoney(currency, number, MonetaryContext.from(
				monetaryContext, RoundedMoney.class), null);
	}

	/**
	 * Static factory method for creating a new instance of {@link RoundedMoney}
	 * .
	 * 
	 * @param currency
	 *            The target currency, not null.
	 * @param number
	 *            The numeric part, not null.
	 * @param monetaryContext
	 *            the {@link MonetaryContext} to be used.
	 * @param rounding
	 *            The rounding to be applied.
	 * @return A new instance of {@link RoundedMoney}.
	 */
	public static RoundedMoney of(CurrencyUnit currency, Number number,
			MonetaryContext<?> monetaryContext, MonetaryOperator rounding) {
		return new RoundedMoney(currency, number, MonetaryContext.from(
				monetaryContext, RoundedMoney.class), rounding);
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
		return new RoundedMoney(MonetaryCurrencies.getCurrency(currencyCode),
				number,
				DEFAULT_MONETARY_CONTEXT,
				MonetaryRoundings.getRounding(MonetaryCurrencies
						.getCurrency(currencyCode)));
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
			MonetaryOperator rounding) {
		return new RoundedMoney(MonetaryCurrencies.getCurrency(currencyCode),
				number,
				DEFAULT_MONETARY_CONTEXT, rounding);
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
			MonetaryContext<?> monetaryContext) {
		return new RoundedMoney(MonetaryCurrencies.getCurrency(currencyCode),
				number,
				MonetaryContext.from(
						monetaryContext, RoundedMoney.class),
				MonetaryRoundings.getRounding(monetaryContext));
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
			MonetaryContext<?> monetaryContext, MonetaryOperator rounding) {
		return new RoundedMoney(MonetaryCurrencies.getCurrency(currencyCode),
				number,
				MonetaryContext.from(
						monetaryContext, RoundedMoney.class), rounding);
	}

/**
	 * Factory method creating a zero instance with the given {@code currency);
	 * @param currency the target currency of the amount being created.
	 * @return
	 */
	public static RoundedMoney ofZero(CurrencyUnit currency) {
		return new RoundedMoney(currency, BigDecimal.ZERO,
				DEFAULT_MONETARY_CONTEXT, null);
	}

/**
	 * Factory method creating a zero instance with the given {@code currency);
	 * @param currency the target currency of the amount being created.
	 * @return
	 */
	public static RoundedMoney ofZero(CurrencyUnit currency,
			MonetaryOperator rounding) {
		return new RoundedMoney(currency, BigDecimal.ZERO,
				DEFAULT_MONETARY_CONTEXT, rounding);
	}

/**
	 * Factory method creating a zero instance with the given {@code currency);
	 * @param currency the target currency of the amount being created.
	 * @return
	 */
	public static RoundedMoney ofZero(String currency) {
		return ofZero(MonetaryCurrencies.getCurrency(currency));
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
	public MonetaryContext<RoundedMoney> getMonetaryContext() {
		return this.monetaryContext;
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
	public RoundedMoney setMathContext(MonetaryContext<?> monetaryContext) {
		Objects.requireNonNull(monetaryContext, "MonetaryContext required.");
		return new RoundedMoney(this.currency, this.number,
				MonetaryContext.from(
						monetaryContext, RoundedMoney.class),
				this.rounding);
	}

	public RoundedMoney abs() {
		if (this.isPositiveOrZero()) {
			return this;
		}
		return this.negate();
	}

	// Arithmetic Operations

	public RoundedMoney add(MonetaryAmount<?> amount) {
		checkAmountParameter(amount);
		return (RoundedMoney) new RoundedMoney(this.currency, this.number.add(
				amount.getNumber(BigDecimal.class)), this.monetaryContext,
				this.rounding).with(rounding);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.money.MonetaryAmount#divide(javax.money.MonetaryAmount)
	 */
	public RoundedMoney divide(Number divisor) {

		BigDecimal dec = this.number.divide(getBigDecimal(divisor),
				this.monetaryContext.getAttribute(RoundingMode.class,
						RoundingMode.HALF_EVEN));
		return (RoundedMoney) new RoundedMoney(this.currency, dec,
				this.monetaryContext,
				this.rounding)
				.with(rounding);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * javax.money.MonetaryAmount#divideAndRemainder(javax.money.MonetaryAmount)
	 */
	public RoundedMoney[] divideAndRemainder(Number divisor) {
		BigDecimal[] dec = this.number.divideAndRemainder(
				getBigDecimal(divisor),
				this.monetaryContext.getAttribute(MathContext.class,
						MathContext.DECIMAL64));
		return new RoundedMoney[] {
				new RoundedMoney(this.currency, dec[0], this.monetaryContext,
						this.rounding),
				(RoundedMoney) new RoundedMoney(this.currency, dec[1],
						this.monetaryContext,
						this.rounding)
						.with(rounding) };
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.money.MonetaryAmount#divideToIntegralValue(Number) )D
	 */
	public RoundedMoney divideToIntegralValue(Number divisor) {
		BigDecimal dec = this.number.divideToIntegralValue(
				getBigDecimal(divisor),
				this.monetaryContext.getAttribute(MathContext.class,
						MathContext.DECIMAL64));
		return new RoundedMoney(this.currency, dec, this.monetaryContext,
				this.rounding);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.money.MonetaryAmount#multiply(Number)
	 */
	public RoundedMoney multiply(Number multiplicand) {
		BigDecimal dec = this.number.multiply(getBigDecimal(multiplicand),
				this.monetaryContext.getAttribute(MathContext.class,
						MathContext.DECIMAL64));
		return (RoundedMoney) new RoundedMoney(this.currency, dec,
				this.monetaryContext,
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
				this.number.negate(this.monetaryContext.getAttribute(
						MathContext.class,
						MathContext.DECIMAL64)),
				this.monetaryContext, this.rounding);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.money.MonetaryAmount#plus()
	 */
	public RoundedMoney plus() {
		return new RoundedMoney(this.currency,
				this.number.plus(this.monetaryContext.getAttribute(
						MathContext.class,
						MathContext.DECIMAL64)),
				this.monetaryContext, this.rounding);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.money.MonetaryAmount#subtract(javax.money.MonetaryAmount)
	 */
	public RoundedMoney subtract(MonetaryAmount<?> subtrahend) {
		checkAmountParameter(subtrahend);
		if (subtrahend.isZero()) {
			return this;
		}
		return new RoundedMoney(this.currency, this.number.subtract(
				subtrahend.getNumber(BigDecimal.class),
				this.monetaryContext.getAttribute(MathContext.class,
						MathContext.DECIMAL64)),
				this.monetaryContext, this.rounding);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.money.MonetaryAmount#pow(int)
	 */
	public RoundedMoney pow(int n) {
		return new RoundedMoney(this.currency, this.number.pow(n,
				this.monetaryContext.getAttribute(MathContext.class,
						MathContext.DECIMAL64)),
				this.monetaryContext,
				this.rounding).with(rounding);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.money.MonetaryAmount#ulp()
	 */
	public RoundedMoney ulp() {
		return new RoundedMoney(this.currency, this.number.ulp(),
				DEFAULT_MONETARY_CONTEXT, this.rounding);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.money.MonetaryAmount#remainder(Number)
	 */
	public RoundedMoney remainder(Number divisor) {
		return new RoundedMoney(this.currency, this.number.remainder(
				getBigDecimal(divisor),
				this.monetaryContext.getAttribute(MathContext.class,
						MathContext.DECIMAL64)),
				this.monetaryContext, this.rounding);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.money.MonetaryAmount#scaleByPowerOfTen(int)
	 */
	public RoundedMoney scaleByPowerOfTen(int n) {
		return new RoundedMoney(this.currency,
				this.number.scaleByPowerOfTen(n),
				this.monetaryContext, this.rounding);
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
				this.monetaryContext, this.rounding);
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
				this.monetaryContext, this.rounding);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.money.MonetaryAmount#with(CurrencyUnit, java.lang.Number)
	 */
	public RoundedMoney with(CurrencyUnit currency, Number amount) {
		checkNumber(amount);
		return new RoundedMoney(currency, getBigDecimal(amount),
				this.monetaryContext, this.rounding);
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
	 * @see javax.money.MonetaryAmount#signum()
	 */

	public int signum() {
		return this.number.signum();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.money.MonetaryAmount#lessThan(javax.money.MonetaryAmount)
	 */
	public boolean isLessThan(MonetaryAmount<?> amount) {
		checkAmountParameter(amount);
		return number.compareTo(amount.getNumber(BigDecimal.class)) < 0;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * javax.money.MonetaryAmount#lessThanOrEqualTo(javax.money.MonetaryAmount)
	 */
	public boolean isLessThanOrEqualTo(MonetaryAmount<?> amount) {
		checkAmountParameter(amount);
		return number.compareTo(amount.getNumber(BigDecimal.class)) <= 0;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.money.MonetaryAmount#greaterThan(javax.money.MonetaryAmount)
	 */
	public boolean isGreaterThan(MonetaryAmount<?> amount) {
		checkAmountParameter(amount);
		return number.compareTo(amount.getNumber(BigDecimal.class)) > 0;
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
		return number.compareTo(amount.getNumber(BigDecimal.class)) >= 0;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.money.MonetaryAmount#isEqualTo(javax.money.MonetaryAmount)
	 */
	public boolean isEqualTo(MonetaryAmount<?> amount) {
		checkAmountParameter(amount);
		return number.compareTo(amount.getNumber(BigDecimal.class)) == 0;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.money.MonetaryAmount#isNotEqualTo(javax.money.MonetaryAmount)
	 */
	public boolean isNotEqualTo(MonetaryAmount<?> amount) {
		checkAmountParameter(amount);
		return number.compareTo(amount.getNumber(BigDecimal.class)) != 0;
	}

	/*
	 * }(non-Javadoc)
	 * 
	 * @see javax.money.MonetaryAmount#adjust(javax.money.AmountAdjuster)
	 */
	@Override
	public RoundedMoney with(MonetaryOperator operation) {
		return operation.apply(this);
	}

	public static RoundedMoney from(MonetaryAmount<?> amt) {
		if (amt.getClass() == RoundedMoney.class) {
			return (RoundedMoney) amt;
		}
		if (amt.getClass() == FastMoney.class) {
			return RoundedMoney.of(amt.getCurrency(),
					((FastMoney) amt).getNumber(),
					DEFAULT_MONETARY_CONTEXT);
		}
		else if (amt.getClass() == Money.class) {
			return RoundedMoney.of(amt.getCurrency(),
					amt.getNumber(BigDecimal.class),
					DEFAULT_MONETARY_CONTEXT);
		}
		return RoundedMoney.of(amt.getCurrency(),
				amt.getNumber(BigDecimal.class),
				DEFAULT_MONETARY_CONTEXT);
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
	public <T> T asType(Class<T> type, MonetaryOperator adjuster) {
		RoundedMoney amount = (RoundedMoney) adjuster.apply(this);
		return amount.asType(type);
	}

	private void writeObject(ObjectOutputStream oos) throws IOException {
		oos.writeObject(this.number);
		oos.writeObject(this.monetaryContext);
		oos.writeObject(this.currency);
	}

	@SuppressWarnings("unchecked")
	private void readObject(ObjectInputStream ois) throws IOException,
			ClassNotFoundException {
		this.number = (BigDecimal) ois.readObject();
		this.monetaryContext = (MonetaryContext<RoundedMoney>) ois.readObject();
		this.currency = (CurrencyUnit) ois.readObject();
	}

	@SuppressWarnings("unused")
	private void readObjectNoData()
			throws ObjectStreamException {
		if (this.number == null) {
			this.number = BigDecimal.ZERO;
		}
		if (this.monetaryContext == null) {
			this.monetaryContext = DEFAULT_MONETARY_CONTEXT;
		}
		if (this.currency == null) {
			this.currency = MonetaryCurrencies.getCurrency(
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
	public int compareTo(MonetaryAmount<?> o) {
		Objects.requireNonNull(o);
		int compare = -1;
		if (this.currency.equals(o.getCurrency())) {
			compare = asNumberStripped().compareTo(
					RoundedMoney.from(o).asNumberStripped());
		} else {
			compare = this.currency.getCurrencyCode().compareTo(
					o.getCurrency().getCurrencyCode());
		}
		return compare;
	}

	// /**
	// * Platform RI: This is an inner checker class for aspects of
	// * {@link MonetaryAmount}. It may be used by multiple implementations
	// * (inside the same package) to avoid code duplication.
	// *
	// * This class is for internal use only.
	// *
	// * @author Werner Keil
	// */
	// static final class Checker {
	// private Checker() {
	// }
	//
	// /**
	// * Internal method to check for correct number parameter.
	// *
	// * @param number
	// * @throws IllegalArgumentException
	// * If the number is null
	// */
	// static final void checkNumber(Number number) {
	// Objects.requireNonNull(number, "Number is required.");
	// }
	//
	// /**
	// * Method to check if a currency is compatible with this amount
	// * instance.
	// *
	// * @param amount
	// * The monetary amount to be compared to, never null.
	// * @throws IllegalArgumentException
	// * If the amount is null, or the amount's currency is not
	// * compatible (same {@link CurrencyUnit#getNamespace()} and
	// * same {@link CurrencyUnit#getCurrencyCode()}).
	// */
	// static final void checkAmountParameter(CurrencyUnit currency,
	// MonetaryAmount amount) {
	// Objects.requireNonNull(amount, "Amount must not be null.");
	// final CurrencyUnit amountCurrency = amount.getCurrency();
	// if (!(currency.getCurrencyCode().equals(amountCurrency
	// .getCurrencyCode()))) {
	// throw new CurrencyMismatchException(currency, amountCurrency);
	// }
	// }
	// }
	//
	// @Override
	// public long getAmountWhole() {
	// return this.number.longValue();
	// }
	//
	// @Override
	// public long getAmountFractionNumerator() {
	// MoneyCurrency mc = MoneyCurrency.from(currency);
	// if (mc.getDefaultFractionDigits() >= 0) {
	// BigDecimal bd = this.number.remainder(BigDecimal.ONE);
	// return bd.movePointRight(mc.getDefaultFractionDigits()).longValue();
	// }
	// return 0L;
	// }
	//
	// @Override
	// public long getAmountFractionDenominator() {
	// MoneyCurrency mc = MoneyCurrency.from(currency);
	// if (mc.getDefaultFractionDigits() >= 0) {
	// return BigDecimal.valueOf(10)
	// .pow(mc.getDefaultFractionDigits())
	// .longValue();
	// }
	// return 1L;
	// }

	public BigDecimal getNumber() {
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
	 * Internal method to check for correct number parameter.
	 * 
	 * @param number
	 * @throws IllegalArgumentException
	 *             If the number is null
	 */
	private void checkNumber(Number number) {
		Objects.requireNonNull(number, "Number is required.");
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T extends Number> T getNumber(Class<T> type) {
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

	@SuppressWarnings("unchecked")
	@Override
	public <T extends Number> T getNumberExact(Class<T> type) {
		if (BigDecimal.class.equals(type)) {
			return (T) this.number;
		}
		if (Number.class.equals(type)) {
			return (T) this.number;
		}
		if (Double.class.equals(type)) {
			Double d = Double.valueOf(this.number.doubleValue());
			if (d.equals(Double.NEGATIVE_INFINITY)
					|| d.equals(Double.NEGATIVE_INFINITY)) {
				throw new ArithmeticException(this.number
						+ " is out of range for double.");
			}
			return (T) d;
		}
		if (Float.class.equals(type)) {
			Float f = Float.valueOf(this.number.floatValue());
			if (f.equals(Float.NEGATIVE_INFINITY)
					|| f.equals(Float.NEGATIVE_INFINITY)) {
				throw new ArithmeticException(this.number
						+ " is out of range for float.");
			}
			return (T) f;
		}
		if (Long.class.equals(type)) {
			return (T) Long.valueOf(this.number.longValueExact());
		}
		if (Integer.class.equals(type)) {
			return (T) Integer.valueOf(this.number.intValueExact());
		}
		if (Short.class.equals(type)) {
			return (T) Short.valueOf(this.number.shortValueExact());
		}
		if (Byte.class.equals(type)) {
			return (T) Byte.valueOf(this.number.byteValueExact());
		}
		if (BigInteger.class.equals(type)) {
			return (T) this.number.toBigInteger();
		}
		throw new IllegalArgumentException("Unsupported representation type: "
				+ type);
	}

	@Override
	public RoundedMoney with(CurrencyUnit unit, long amount) {
		return RoundedMoney.of(unit, getBigDecimal(amount),
				this.monetaryContext);
	}

	@Override
	public RoundedMoney with(CurrencyUnit unit, double amount) {
		return RoundedMoney.of(unit, getBigDecimal(amount),
				this.monetaryContext);
	}

	@Override
	public RoundedMoney multiply(long amount) {
		return multiply(getBigDecimal(amount));
	}

	@Override
	public RoundedMoney multiply(double amount) {
		return multiply(getBigDecimal(amount));
	}

	@Override
	public RoundedMoney divide(long amount) {
		return divide(getBigDecimal(amount));
	}

	@Override
	public RoundedMoney divide(double amount) {
		return divide(getBigDecimal(amount));
	}

	@Override
	public RoundedMoney remainder(long amount) {
		return remainder(getBigDecimal(amount));
	}

	@Override
	public RoundedMoney remainder(double amount) {
		return remainder(getBigDecimal(amount));
	}

	@Override
	public RoundedMoney[] divideAndRemainder(long amount) {
		return divideAndRemainder(getBigDecimal(amount));
	}

	@Override
	public RoundedMoney[] divideAndRemainder(double amount) {
		return divideAndRemainder(getBigDecimal(amount));
	}

	@Override
	public RoundedMoney stripTrailingZeros() {
		if (isZero()) {
			return ofZero(getCurrency());
		}
		return of(getCurrency(), this.number.stripTrailingZeros());
	}

	@Override
	public RoundedMoney divideToIntegralValue(long divisor) {
		return divideToIntegralValue(getBigDecimal(divisor));
	}

	@Override
	public RoundedMoney divideToIntegralValue(double divisor) {
		return divideToIntegralValue(getBigDecimal(divisor));
	}

	@Override
	protected MonetaryContext<RoundedMoney> getDefaultMonetaryContext() {
		return DEFAULT_MONETARY_CONTEXT;
	}
}
