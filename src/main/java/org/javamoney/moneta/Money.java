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
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.ObjectStreamException;
import java.io.Serializable;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.MathContext;
import java.math.RoundingMode;
import java.net.URL;
import java.util.Locale;
import java.util.Objects;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.money.CurrencyUnit;
import javax.money.MonetaryAmount;
import javax.money.MonetaryContext;
import javax.money.MonetaryCurrencies;
import javax.money.MonetaryOperator;
import javax.money.MonetaryQuery;

/**
 * Platform RI: Default immutable implementation of {@link MonetaryAmount} based
 * on {@link BigDecimal} for the numeric representation.
 * <p>
 * As required by {@link MonetaryAmount} this class is final, thread-safe,
 * immutable and serializable.
 * <p>
 * This class can be configured with an arbitrary {@link MonetaryContext}. The
 * default {@link MonetaryContext} used models by default the same settings as
 * {@link MathContext#DECIMAL64}. This default {@link MonetaryContext} can also
 * be reconfigured by adding a file {@code /javamoney.properties} to the
 * classpath, with the following content:
 * 
 * <pre>
 * # Default MathContext for Money
 * #-------------------------------
 * # Custom MonetaryContext, overrides default entries from 
 * # org.javamoney.moneta.Money.monetaryContext
 * # RoundingMode hereby is optional (default = HALF_EVEN)
 * org.javamoney.moneta.Money.defaults.precision=256
 * org.javamoney.moneta.Money.defaults.roundingMode=HALF_EVEN
 * </pre>
 * 
 * @version 0.6.1
 * @author Anatole Tresch
 * @author Werner Keil
 */
public final class Money extends AbstractMoney<Money> implements
		Comparable<MonetaryAmount<?>>,
		Serializable {

	/** serialVersionUID. */
	private static final long serialVersionUID = -7565813772046251748L;

	/**
	 * The default {@link MonetaryContext} applied, if not set explicitly on
	 * creation.
	 */
	public static final MonetaryContext<Money> DEFAULT_MONETARY_CONTEXT = initDefaultMathContext();

	/** The numeric part of this amount. */
	private BigDecimal number;

	/**
	 * Required for deserialization only.
	 */
	private Money() {
	}

	/**
	 * Creates a new instance os {@link Money}.
	 * 
	 * @param currency
	 *            the currency, not null.
	 * @param number
	 *            the amount, not null.
	 * @throws ArithmeticException
	 *             If the number exceeds the capabilities of the default
	 *             {@link MonetaryContext}.
	 */
	private Money(CurrencyUnit currency, BigDecimal number) {
		this(currency, number, null);
	}

	/**
	 * Evaluates the default {@link MonetaryContext} to be used for
	 * {@link Money}. The default {@link MonetaryContext} can be configured by
	 * adding a file {@code /javamoney.properties} from the classpath with the
	 * following content:
	 * 
	 * <pre>
	 * # Default MathContext for Money
	 * #-------------------------------
	 * # Custom MathContext, overrides entries from org.javamoney.moneta.Money.mathContext
	 * # RoundingMode hereby is optional (default = HALF_EVEN)
	 * org.javamoney.moneta.Money.defaults.precision=256
	 * org.javamoney.moneta.Money.defaults.roundingMode=HALF_EVEN
	 * </pre>
	 * 
	 * Hereby the roundingMode constants are the same as defined on
	 * {@link RoundingMode}.
	 * 
	 * @return default MonetaryContext, never {@code null}.
	 */
	private static MonetaryContext<Money> initDefaultMathContext() {
		InputStream is = null;
		try {
			Properties props = new Properties();
			URL url = Money.class.getResource("/javamoney.properties");
			if (url != null) {
				is = url
						.openStream();
				props.load(is);
				String value = props
						.getProperty("org.javamoney.moneta.Money.defaults.precision");
				if (value != null) {
					int prec = Integer.parseInt(value);
					value = props
							.getProperty("org.javamoney.moneta.Money.defaults.roundingMode");
					RoundingMode rm = value != null ? RoundingMode
							.valueOf(value
									.toUpperCase(Locale.ENGLISH))
							: RoundingMode.HALF_UP;
					MonetaryContext<Money> mc = new MonetaryContext.Builder()
							.setPrecision(prec)
							.setAttribute(rm).build(Money.class);
					Logger.getLogger(Money.class.getName()).info(
							"Using custom MathContext: precision=" + prec
									+ ", roundingMode=" + rm);
					return mc;
				}
				else {
					MonetaryContext.Builder builder = new MonetaryContext.Builder();
					value = props
							.getProperty("org.javamoney.moneta.Money.defaults.mathContext");
					if (value != null) {
						switch (value.toUpperCase(Locale.ENGLISH)) {
						case "DECIMAL32":
							Logger.getLogger(Money.class.getName()).info(
									"Using MathContext.DECIMAL32");
							builder.setAttribute(MathContext.DECIMAL32);
							break;
						case "DECIMAL64":
							Logger.getLogger(Money.class.getName()).info(
									"Using MathContext.DECIMAL64");
							builder.setAttribute(MathContext.DECIMAL64);
							break;
						case "DECIMAL128":
							Logger.getLogger(Money.class.getName())
									.info(
											"Using MathContext.DECIMAL128");
							builder.setAttribute(MathContext.DECIMAL128);
							break;
						case "UNLIMITED":
							Logger.getLogger(Money.class.getName()).info(
									"Using MathContext.UNLIMITED");
							builder.setAttribute(MathContext.UNLIMITED);
							break;
						}
					}
					return builder.build(Money.class);
				}
			}
			MonetaryContext.Builder builder = new MonetaryContext.Builder();
			Logger.getLogger(Money.class.getName()).info(
					"Using default MathContext.DECIMAL64");
			builder.setAttribute(MathContext.DECIMAL64);
			return builder.build(Money.class);
		} catch (Exception e) {
			Logger.getLogger(Money.class.getName())
					.log(Level.SEVERE,
							"Error evaluating default NumericContext, using default (NumericContext.NUM64).",
							e);
			return new MonetaryContext.Builder().setAttribute(MathContext.DECIMAL64)
					.build(Money.class);
		} finally {
			if (is != null) {
				try {
					is.close();
				} catch (IOException e) {
					Logger.getLogger(Money.class.getName())
							.log(Level.WARNING,
									"Error closing InputStream after evaluating default NumericContext.",
									e);
				}
			}
		}
	}

	/**
	 * Creates a new instance of {@link Money}.
	 * 
	 * @param currency
	 *            the currency, not {@code null}.
	 * @param number
	 *            the amount, not {@code null}.
	 * @param monetaryContext
	 *            the {@link MonetaryContext}, if {@code null}, the default is
	 *            used.
	 * @throws ArithmeticException
	 *             If the number exceeds the capabilities of the
	 *             {@link MonetaryContext} used.
	 */
	private Money(CurrencyUnit currency, BigDecimal number,
			MonetaryContext<Money> monetaryContext) {
		super(currency, monetaryContext);
		Objects.requireNonNull(number, "Number is required.");
		if (monetaryContext != null) {
			this.number = getBigDecimal(number, monetaryContext);
		}
		else {
			this.number = getBigDecimal(number, DEFAULT_MONETARY_CONTEXT);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.money.MonetaryAmount#getNumber(java.lang.Class)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public <N extends Number> N getNumber(Class<N> type) {
		if (BigDecimal.class.equals(type)) {
			return (N) this.number;
		}
		if (Number.class.equals(type)) {
			final N asType = (N) this.number;
			return asType;
		}
		if (Double.class.equals(type)) {
			return (N) Double.valueOf(this.number.doubleValue());
		}
		if (Float.class.equals(type)) {
			return (N) Float.valueOf(this.number.floatValue());
		}
		if (Long.class.equals(type)) {
			return (N) Long.valueOf(this.number.longValue());
		}
		if (Integer.class.equals(type)) {
			return (N) Integer.valueOf(this.number.intValue());
		}
		if (Short.class.equals(type)) {
			return (N) Short.valueOf(this.number.shortValue());
		}
		if (Byte.class.equals(type)) {
			return (N) Byte.valueOf(this.number.byteValue());
		}
		if (BigInteger.class.equals(type)) {
			return (N) this.number.toBigInteger();
		}
		throw new IllegalArgumentException("Unsupported representation type: "
				+ type);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.money.MonetaryAmount#getNumberExact(java.lang.Class)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public <N extends Number> N getNumberExact(Class<N> type) {
		if (BigDecimal.class.equals(type)) {
			return (N) this.number;
		}
		if (Number.class.equals(type)) {
			return (N) this.number;
		}
		if (Double.class.equals(type)) {
			Double d = Double.valueOf(this.number.doubleValue());
			if (d.equals(Double.NEGATIVE_INFINITY)
					|| d.equals(Double.NEGATIVE_INFINITY)) {
				throw new ArithmeticException(this.number
						+ " is out of range for double.");
			}
			return (N) d;
		}
		if (Float.class.equals(type)) {
			Float f = Float.valueOf(this.number.floatValue());
			if (f.equals(Float.NEGATIVE_INFINITY)
					|| f.equals(Float.NEGATIVE_INFINITY)) {
				throw new ArithmeticException(this.number
						+ " is out of range for float.");
			}
			return (N) f;
		}
		if (Long.class.equals(type)) {
			return (N) Long.valueOf(this.number.longValueExact());
		}
		if (Integer.class.equals(type)) {
			return (N) Integer.valueOf(this.number.intValueExact());
		}
		if (Short.class.equals(type)) {
			return (N) Short.valueOf(this.number.shortValueExact());
		}
		if (Byte.class.equals(type)) {
			return (N) Byte.valueOf(this.number.byteValueExact());
		}
		if (BigInteger.class.equals(type)) {
			return (N) this.number.toBigInteger();
		}
		throw new IllegalArgumentException("Unsupported representation type: "
				+ type);
	}

	/**
	 * Gets the {@link BigDecimal} representation of the numeric value of this
	 * item.
	 * 
	 * @return The {@link BigDecimal} representation.
	 */
	public BigDecimal getNumber() {
		return this.number;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.money.MonetaryAmount#getScale()
	 */
	@Override
	public int getScale() {
		return this.number.scale();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.money.MonetaryAmount#getPrecision()
	 */
	@Override
	public int getPrecision() {
		return this.number.precision();
	}

	/**
	 * Method that returns BigDecimal.ZERO, if {@link #isZero()}, and
	 * {@link #number#stripTrailingZeros()} in all other cases.
	 * 
	 * @return the stripped number value.
	 */
	public BigDecimal getNumberStripped() {
		if (isZero()) {
			return BigDecimal.ZERO;
		}
		return this.number.stripTrailingZeros();
	}

	/*
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	@Override
	public int compareTo(MonetaryAmount<?> o) {
		Objects.requireNonNull(o);
		int compare = getCurrency().getCurrencyCode().compareTo(
				o.getCurrency().getCurrencyCode());
		if (compare == 0) {
			compare = this.number.compareTo(Money.from(o).number);
		}
		return compare;
	}

	// Arithmetic Operations

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.money.MonetaryAmount#abs()
	 */
	@Override
	public Money abs() {
		if (this.isPositiveOrZero()) {
			return this;
		}
		return negate();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.money.MonetaryAmount#divide(javax.money.MonetaryAmount)
	 */
	@Override
	public Money divide(long divisor) {
		if (divisor == 1L) {
			return this;
		}
		return divide(BigDecimal.valueOf(divisor));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.money.MonetaryAmount#divide(javax.money.MonetaryAmount)
	 */
	@Override
	public Money divide(double divisor) {
		if (divisor == 1.0) {
			return this;
		}
		return divide(new BigDecimal(String.valueOf(divisor)));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * javax.money.MonetaryAmount#divideAndRemainder(javax.money.MonetaryAmount)
	 */
	@Override
	public Money[] divideAndRemainder(long divisor) {
		return divideAndRemainder(BigDecimal.valueOf(divisor));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * javax.money.MonetaryAmount#divideAndRemainder(javax.money.MonetaryAmount)
	 */
	@Override
	public Money[] divideAndRemainder(double divisor) {
		return divideAndRemainder(new BigDecimal(String.valueOf(divisor)));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.money.MonetaryAmount#multiply(Number)
	 */
	@Override
	public Money multiply(long multiplicand) {
		return multiply(BigDecimal.valueOf(multiplicand));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.money.MonetaryAmount#multiply(Number)
	 */
	@Override
	public Money multiply(double multiplicand) {
		return multiply(new BigDecimal(String
				.valueOf(multiplicand)));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.money.MonetaryAmount#remainder(Number)
	 */
	@Override
	public Money remainder(long divisor) {
		return remainder(BigDecimal.valueOf(divisor));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.money.MonetaryAmount#remainder(Number)
	 */
	@Override
	public Money remainder(double divisor) {
		return remainder(new BigDecimal(String.valueOf(divisor)));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.money.MonetaryAmount#isZero()
	 */
	@Override
	public boolean isZero() {
		return signum() == 0;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.money.MonetaryAmount#isPositive()
	 */
	@Override
	public boolean isPositive() {
		return signum() == 1;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.money.MonetaryAmount#isPositiveOrZero()
	 */
	@Override
	public boolean isPositiveOrZero() {
		return signum() >= 0;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.money.MonetaryAmount#isNegative()
	 */
	@Override
	public boolean isNegative() {
		return signum() == -1;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.money.MonetaryAmount#isNegativeOrZero()
	 */
	@Override
	public boolean isNegativeOrZero() {
		return signum() <= 0;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.money.MonetaryAmount#with(javax.money.CurrencyUnit, long)
	 */
	@Override
	public Money with(CurrencyUnit unit, long amount) {
		return with(unit, BigDecimal.valueOf(amount));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.money.MonetaryAmount#with(javax.money.CurrencyUnit, double)
	 */
	@Override
	public Money with(CurrencyUnit unit, double amount) {
		return with(unit, new BigDecimal(String.valueOf(amount)));
	}

	/*
	 * }(non-Javadoc)
	 * 
	 * @see javax.money.MonetaryAmount#query(javax.money.MonetaryQuery)
	 */
	@Override
	public <R> R query(MonetaryQuery<R> query) {
		return query.queryFrom(this);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.money.MonetaryAmount#with(javax.money.MonetaryOperator)
	 */
	@Override
	public Money with(MonetaryOperator operator) {
		return operator.apply(this);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.money.MonetaryAmount#add(javax.money.MonetaryAmount)
	 */
	@Override
	public Money add(MonetaryAmount<?> amount) {
		checkAmountParameter(amount);
		if (amount.isZero()) {
			return this;
		}
		return new Money(getCurrency(), this.number.add(
				amount.getNumber(BigDecimal.class)));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.money.MonetaryAmount#divide(java.lang.Number)
	 */
	@Override
	public Money divide(Number divisor) {
		BigDecimal divisorBD = getBigDecimal(divisor);
		if (divisorBD.equals(BigDecimal.ONE)) {
			return this;
		}
		BigDecimal dec = this.number.divide(divisorBD,
				getMathContext(getMonetaryContext(), RoundingMode.HALF_EVEN));
		return new Money(getCurrency(), dec);
	}

	@Override
	public Money[] divideAndRemainder(Number divisor) {
		BigDecimal divisorBD = getBigDecimal(divisor);
		if (divisorBD.equals(BigDecimal.ONE)) {
			return new Money[] {
					this,
					new Money(getCurrency(), BigDecimal.ZERO) };
		}
		BigDecimal[] dec = this.number.divideAndRemainder(
				divisorBD);
		return new Money[] {
				new Money(getCurrency(), dec[0]),
				new Money(getCurrency(), dec[1]) };
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.javamoney.moneta.AbstractMoney#divideToIntegralValue(java.lang.Number
	 * )
	 */
	@Override
	public Money divideToIntegralValue(long divisor) {
		return divideToIntegralValue(getBigDecimal(divisor));
	}

	@Override
	public Money divideToIntegralValue(double divisor) {
		return divideToIntegralValue(getBigDecimal(divisor));
	}

	@Override
	public Money divideToIntegralValue(Number divisor) {
		BigDecimal divisorBD = getBigDecimal(divisor);
		BigDecimal dec = this.number.divideToIntegralValue(
				divisorBD);
		return new Money(getCurrency(), dec);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.javamoney.moneta.AbstractMoney#multiply(java.lang.Number)
	 */
	@Override
	public Money multiply(Number multiplicand) {
		BigDecimal multiplicandBD = getBigDecimal(multiplicand);
		if (multiplicandBD.equals(BigDecimal.ONE)) {
			return this;
		}
		BigDecimal dec = this.number.multiply(multiplicandBD);
		return new Money(getCurrency(), dec);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.money.MonetaryAmount#negate()
	 */
	@Override
	public Money negate() {
		return new Money(getCurrency(), this.number.negate());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.money.MonetaryAmount#plus()
	 */
	@Override
	public Money plus() {
		return new Money(getCurrency(), this.number.plus());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.money.MonetaryAmount#subtract(javax.money.MonetaryAmount)
	 */
	@Override
	public Money subtract(MonetaryAmount<?> subtrahend) {
		checkAmountParameter(subtrahend);
		if (subtrahend.isZero()) {
			return this;
		}
		return new Money(getCurrency(), this.number.subtract(
				subtrahend.getNumber(BigDecimal.class)));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.money.MonetaryAmount#stripTrailingZeros()
	 */
	@Override
	public Money stripTrailingZeros() {
		if (isZero()) {
			return new Money(getCurrency(), BigDecimal.ZERO);
		}
		return new Money(getCurrency(), this.number.stripTrailingZeros());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.money.MonetaryAmount#ulp()
	 */
	public Money ulp() {
		return new Money(getCurrency(), this.number.ulp());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.javamoney.moneta.AbstractMoney#remainder(java.math.BigDecimal)
	 */
	@Override
	public Money remainder(Number divisor) {
		BigDecimal bd = getBigDecimal(divisor);
		return new Money(getCurrency(), this.number.remainder(
				bd));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.money.MonetaryAmount#scaleByPowerOfTen(int)
	 */
	@Override
	public Money scaleByPowerOfTen(int n) {
		return new Money(getCurrency(), this.number.scaleByPowerOfTen(n));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.money.MonetaryAmount#signum()
	 */
	@Override
	public int signum() {
		return this.number.signum();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.money.MonetaryAmount#isLessThan(javax.money.MonetaryAmount)
	 */
	@Override
	public boolean isLessThan(MonetaryAmount<?> amount) {
		checkAmountParameter(amount);
		return number.compareTo(Money.from(amount).number) < 0;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * javax.money.MonetaryAmount#isLessThanOrEqualTo(javax.money.MonetaryAmount
	 * )
	 */
	@Override
	public boolean isLessThanOrEqualTo(MonetaryAmount<?> amount) {
		checkAmountParameter(amount);
		return number.compareTo(Money.from(amount).number) <= 0;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.money.MonetaryAmount#isGreaterThan(javax.money.MonetaryAmount)
	 */
	@Override
	public boolean isGreaterThan(MonetaryAmount<?> amount) {
		checkAmountParameter(amount);
		return number.compareTo(Money.from(amount).number) > 0;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * javax.money.MonetaryAmount#isGreaterThanOrEqualTo(javax.money.MonetaryAmount
	 * ) #see
	 */
	@Override
	public boolean isGreaterThanOrEqualTo(MonetaryAmount<?> amount) {
		checkAmountParameter(amount);
		return number.compareTo(Money.from(amount).number) >= 0;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.money.MonetaryAmount#isEqualTo(javax.money.MonetaryAmount)
	 */
	@Override
	public boolean isEqualTo(MonetaryAmount<?> amount) {
		checkAmountParameter(amount);
		return equals(Money.from(amount));
	}

	/**
	 * Creates a new {@link MonetaryAmount}, using the current amount as a
	 * template, reusing the algorithmic implementation, the current
	 * {@link MonetaryContext} and the numeric value.
	 * <p>
	 * This method is used for creating a new amount result after having done
	 * calculations that are not directly mappable to the default monetary
	 * arithmetics, e.g. currency conversion.
	 * 
	 * @see javax.money.MonetaryAmount#with(javax.money.CurrencyUnit)
	 * @param unit
	 *            the new {@link CurrencyUnit} of the amount to be created.
	 * @return the new {@link MonetaryAmount} with the given
	 *         {@link CurrencyUnit}, but the same numeric value and
	 *         {@link MonetaryContext}.
	 */
	@Override
	public Money with(CurrencyUnit unit) {
		return Money.of(unit, this.number, this.monetaryContext);
	}

	/**
	 * Creates a new Money instance, hereby changing the {@link MonetaryContext}
	 * to be used. This allows to adapt the {@link MonetaryContext}, when
	 * required.
	 * 
	 * @param context
	 *            the {@link MonetaryContext} to be replaced, not {@code null}
	 * @return the new amount with the same numeric value and
	 *         {@link CurrencyUnit}, but the new {@link MonetaryContext}.
	 * @throws ArithemticException
	 *             if the number of this instance exceeds the capabilities of
	 *             the given {@link MonetaryContext}.
	 */
	public Money with(MonetaryContext<Money> context) {
		Objects.requireNonNull(context, "MathContext required");
		return new Money(getCurrency(), this.number, context);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.javamoney.moneta.AbstractMoney#with(javax.money.CurrencyUnit,
	 * java.math.BigDecimal)
	 */
	@Override
	public Money with(CurrencyUnit unit, Number amount) {
		return Money.of(unit, getBigDecimal(amount), getMonetaryContext());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.javamoney.moneta.AbstractMoney#getDefaultMonetaryContext()
	 */
	@Override
	protected MonetaryContext<Money> getDefaultMonetaryContext() {
		return DEFAULT_MONETARY_CONTEXT;
	}

	/**
	 * Implement serialization explicitly.
	 */
	private void writeObject(ObjectOutputStream oos) throws IOException {
		oos.writeObject(this.number);
		oos.writeObject(this.currency);
		oos.writeObject(this.monetaryContext);
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
		Money other = (Money) obj;
		if (!getCurrency().equals(other.getCurrency()))
			return false;
		return getNumberStripped().equals(other.getNumberStripped());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return getCurrency().getCurrencyCode() + ' ' + number.toString();
	}

	/**
	 * Implement deserialization explicitly.
	 */
	@SuppressWarnings("unchecked")
	private void readObject(ObjectInputStream ois) throws IOException,
			ClassNotFoundException {
		this.number = (BigDecimal) ois.readObject();
		this.currency = (CurrencyUnit) ois.readObject();
		this.monetaryContext = (MonetaryContext<Money>) ois.readObject();
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
				+ getCurrency().hashCode();
		return prime * result + getNumberStripped().hashCode();
	}

	/**
	 * Creates a new instance of {@link Money}, using the default
	 * {@link MonetaryContext}.
	 * 
	 * @param number
	 *            numeric value, not {@code null}.
	 * @param currency
	 *            currency unit, not {@code null}.
	 * @return a {@code Money} combining the numeric value and currency unit.
	 * @throws ArithmeticException
	 *             If the number exceeds the capabilities of the default
	 *             {@link MonetaryContext} used.
	 */
	public static Money of(CurrencyUnit currency, BigDecimal number) {
		return new Money(currency, number);
	}

	/**
	 * Creates a new instance of {@link Money}, using an explicit
	 * {@link MonetaryContext}.
	 * 
	 * @param number
	 *            numeric value, not {@code null}.
	 * @param currency
	 *            currency unit, not {@code null}.
	 * @param monetaryContext
	 *            the {@link MonetaryContext} to be used, if {@code null} the
	 *            default {@link MonetaryContext} is used.
	 * @return a {@code Money} instance based on the monetary context with the
	 *         given numeric value, currency unit.
	 * @throws ArithmeticException
	 *             If the number exceeds the capabilities of the
	 *             {@link MonetaryContext} used.
	 */
	public static Money of(CurrencyUnit currency, BigDecimal number,
			MonetaryContext<Money> monetaryContext) {
		return new Money(currency, number, monetaryContext);
	}

	/**
	 * Creates a new instance of {@link Money}, using the default
	 * {@link MonetaryContext}.
	 * 
	 * @param currency
	 *            The target currency, not null.
	 * @param number
	 *            The numeric part, not null.
	 * @return A new instance of {@link Money}.
	 * @throws ArithmeticException
	 *             If the number exceeds the capabilities of the default
	 *             {@link MonetaryContext} used.
	 */
	public static Money of(CurrencyUnit currency, Number number) {
		return new Money(currency, getBigDecimal(number));
	}

	/**
	 * Creates a new instance of {@link Money}, using an explicit
	 * {@link MonetaryContext}.
	 * 
	 * @param currency
	 *            The target currency, not null.
	 * @param number
	 *            The numeric part, not null.
	 * @param monetaryContext
	 *            the {@link MonetaryContext} to be used, if {@code null} the
	 *            default {@link MonetaryContext} is used.
	 * @return A new instance of {@link Money}.
	 * @throws ArithmeticException
	 *             If the number exceeds the capabilities of the
	 *             {@link MonetaryContext} used.
	 */
	public static Money of(CurrencyUnit currency, Number number,
			MonetaryContext<Money> monetaryContext) {
		return new Money(currency, getBigDecimal(number), monetaryContext);
	}

	/**
	 * Static factory method for creating a new instance of {@link Money}.
	 * 
	 * @param currencyCode
	 *            The target currency as ISO currency code.
	 * @param number
	 *            The numeric part, not null.
	 * @return A new instance of {@link Money}.
	 */
	public static Money of(String currencyCode, Number number) {
		return new Money(MonetaryCurrencies.getCurrency(currencyCode),
				getBigDecimal(number));
	}

	/**
	 * Static factory method for creating a new instance of {@link Money}.
	 * 
	 * @param currencyCode
	 *            The target currency as ISO currency code.
	 * @param number
	 *            The numeric part, not null.
	 * @return A new instance of {@link Money}.
	 */
	public static Money of(String currencyCode, BigDecimal number) {
		return new Money(MonetaryCurrencies.getCurrency(currencyCode), number);
	}

	/**
	 * Static factory method for creating a new instance of {@link Money}.
	 * 
	 * @param currencyCode
	 *            The target currency as ISO currency code.
	 * @param number
	 *            The numeric part, not null.
	 * @param monetaryContext
	 *            the {@link MonetaryContext} to be used, if {@code null} the
	 *            default {@link MonetaryContext} is used.
	 * @return A new instance of {@link Money}.
	 */
	public static Money of(String currencyCode, Number number,
			MonetaryContext<Money> monetaryContext) {
		return new Money(MonetaryCurrencies.getCurrency(currencyCode),
				getBigDecimal(number),
				monetaryContext);
	}

	/**
	 * Static factory method for creating a new instance of {@link Money}.
	 * 
	 * @param currencyCode
	 *            The target currency as ISO currency code.
	 * @param number
	 *            The numeric part, not null.
	 * @param monetaryContext
	 *            the {@link MonetaryContext} to be used, if {@code null} the
	 *            default {@link MonetaryContext} is used.
	 * @return A new instance of {@link Money}.
	 */
	public static Money of(String currencyCode, BigDecimal number,
			MonetaryContext<Money> monetaryContext) {
		return new Money(MonetaryCurrencies.getCurrency(currencyCode), number,
				monetaryContext);
	}

/**
	 * Factory method creating a zero instance with the given {@code currency);
	 * @param currency 
	 * 			the target currency of the amount being created, not {@code null}.
	 * @return a new Money instance of zero, with a default {@link MonetaryContext}.
	 */
	public static Money ofZero(CurrencyUnit currency) {
		return new Money(currency, BigDecimal.ZERO, DEFAULT_MONETARY_CONTEXT);
	}

/**
	 * Factory method creating a zero instance with the given {@code currency);
	 * @param currency 
	 * 			the target currency of the amount being created.
	 * @return a new Money instance of zero, with a default {@link MonetaryContext}.
	 */
	public static Money ofZero(String currency) {
		return ofZero(MonetaryCurrencies.getCurrency(currency));
	}

	/**
	 * Converts (if necessary) the given {@link MonetaryAmount} to a
	 * {@link Money} instance. The {@link MonetaryContext} will be adapted as
	 * necessary, if the precision of the given amount exceeds the capabilities
	 * of the default {@link MonetaryContext}.
	 * 
	 * @param amt
	 *            the amount to be converted
	 * @return an according Money instance.
	 */
	public static Money from(MonetaryAmount<?> amt) {
		if (amt.getClass() == Money.class) {
			return (Money) amt;
		}
		return Money.of(amt.getCurrency(), amt.getNumber(BigDecimal.class),
				DEFAULT_MONETARY_CONTEXT);
	}

}
