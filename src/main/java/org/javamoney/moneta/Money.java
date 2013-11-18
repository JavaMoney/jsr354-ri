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
import java.util.concurrent.atomic.AtomicLong;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.money.CurrencyUnit;
import javax.money.MonetaryAdjuster;
import javax.money.MonetaryAmount;
import javax.money.MonetaryQuery;

/**
 * Platform RI: Default immutable implementation of {@link MonetaryAmount} based
 * on {@link BigDecimal} for the numeric representation.
 * <p>
 * As required by {@link MonetaryAmount} this class is final, thread-safe,
 * immutable and serializable.
 * <p>
 * This class uses a default MathContext. The default MathContext can be
 * configured by adding a file {@code /javamoney.properties} to the classpath,
 * with the following content:
 * 
 * <pre>
 * # Default MathContext for Money
 * #-------------------------------
 * # Custom MathContext, overrides entries from org.javamoney.moneta.Money.mathContext
 * # RoundingMode hereby is optional (default = HALF_EVEN)
 * org.javamoney.moneta.Money.defaults.precision=256
 * org.javamoney.moneta.Money.defaults.roundingMode=HALF_EVEN
 * # or, 
 * # use one of DECIMAL32,DECIMAL64(default),DECIMAL128,UNLIMITED
 * # org.javamoney.moneta.Money.mathContext=DECIMAL128
 * </pre>
 * 
 * @version 0.6.1
 * @author Anatole Tresch
 * @author Werner Keil
 */
public final class Money implements MonetaryAmount, Comparable<MonetaryAmount>,
		Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7565813772046251748L;

	public static final MathContext DEFAULT_MATH_CONTEXT = initDefaultMathContext();

	private static final int[] DENOM_ARRAY = new int[] { 1, 10, 100, 1000,
			10000, 100000, 1000000 };

	/** The numeric part of this amount. */
	private BigDecimal number;

	/** The currency of this amount. */
	private CurrencyUnit currency;

	/** tHE DEFAULT {@link MathContext} used by this instance, e.g. on division. */
	private MathContext mathContext;

	/**
	 * Creates a new instance os {@link Money}.
	 * 
	 * @param currency
	 *            the currency, not null.
	 * @param number
	 *            the amount, not null.
	 */
	private Money(CurrencyUnit currency, Number number) {
		this(currency, number, null);
	}

	/**
	 * Evaluates the default MathContext to be used for {@link Money}. This will
	 * try to read /javamoney.properties from the classpath:
	 * 
	 * <pre>
	 * # Default MathContext for Money
	 * #-------------------------------
	 * # Custom MathContext, overrides entries from org.javamoney.moneta.Money.mathContext
	 * # RoundingMode hereby is optional (default = HALF_EVEN)
	 * org.javamoney.moneta.Money.defaults.precision=256
	 * org.javamoney.moneta.Money.defaults.roundingMode=HALF_EVEN
	 * # or, 
	 * # use one of DECIMAL32,DECIMAL64(default),DECIMAL128,UNLIMITED
	 * # org.javamoney.moneta.Money.mathContext=DECIMAL128
	 * </pre>
	 * 
	 * @return
	 */
	private static MathContext initDefaultMathContext() {
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
							: RoundingMode.HALF_EVEN;
					MathContext mc = new MathContext(prec, rm);
					Logger.getLogger(Money.class.getName()).info(
							"Using custom MathContext: precision=" + prec
									+ ", roundingMode=" + rm);
					return mc;
				}
				else {
					value = props
							.getProperty("org.javamoney.moneta.Money.defaults.mathContext");
					if (value != null) {
						switch (value.toUpperCase(Locale.ENGLISH)) {
						case "DECIMAL32":
							Logger.getLogger(Money.class.getName()).info(
									"Using MathContext.DECIMAL32");
							return MathContext.DECIMAL32;
						case "DECIMAL64":
							Logger.getLogger(Money.class.getName()).info(
									"Using MathContext.DECIMAL64");
							return MathContext.DECIMAL64;
						case "DECIMAL128":
							Logger.getLogger(Money.class.getName())
									.info(
											"Using MathContext.DECIMAL128");
							return MathContext.DECIMAL128;
						case "UNLIMITED":
							Logger.getLogger(Money.class.getName()).info(
									"Using MathContext.UNLIMITED");
							return MathContext.UNLIMITED;
						}
					}
				}
			}
			Logger.getLogger(Money.class.getName()).info(
					"Using default MathContext.DECIMAL64");
			return MathContext.DECIMAL64;
		} catch (Exception e) {
			Logger.getLogger(Money.class.getName())
					.log(Level.SEVERE,
							"Error evaluating default MathContext, using default (MathContext.DECIMAL64).",
							e);
			return MathContext.DECIMAL64;
		} finally {
			if (is != null) {
				try {
					is.close();
				} catch (IOException e) {
					Logger.getLogger(Money.class.getName())
							.log(Level.WARNING,
									"Error closing InputStream after evaluating default MathContext.",
									e);
				}
			}
		}
	}

	/**
	 * Creates a new instance os {@link Money}.
	 * 
	 * @param currency
	 *            the currency, not null.
	 * @param number
	 *            the amount, not null.
	 */
	private Money(CurrencyUnit currency, Number number, MathContext mathContext) {
		Objects.requireNonNull(currency, "Currency is required.");
		Objects.requireNonNull(number, "Number is required.");
		checkNumber(number);
		this.currency = currency;
		if (mathContext != null) {
			this.mathContext = mathContext;
			this.number = getBigDecimal(number, mathContext);
		}
		else {
			this.mathContext = DEFAULT_MATH_CONTEXT;
			this.number = getBigDecimal(number, null);
		}

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
	public static Money of(CurrencyUnit currency, BigDecimal number) {
		return new Money(currency, number);
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
	public static Money of(CurrencyUnit currency, BigDecimal number,
			MathContext mathContext) {
		return new Money(currency, number, mathContext);
	}

	/**
	 * Static factory method for creating a new instance of {@link Money}.
	 * 
	 * @param currency
	 *            The target currency, not null.
	 * @param number
	 *            The numeric part, not null.
	 * @return A new instance of {@link Money}.
	 */
	public static Money of(CurrencyUnit currency, Number number) {
		return new Money(currency, number);
	}

	/**
	 * Static factory method for creating a new instance of {@link Money}.
	 * 
	 * @param currency
	 *            The target currency, not null.
	 * @param number
	 *            The numeric part, not null.
	 * @return A new instance of {@link Money}.
	 */
	public static Money of(CurrencyUnit currency, Number number,
			MathContext mathContext) {
		return new Money(currency, number, mathContext);
	}

	/**
	 * Static factory method for creating a new instance of {@link Money}.
	 * 
	 * @param isoCurrencyCode
	 *            The target currency as ISO currency code.
	 * @param number
	 *            The numeric part, not null.
	 * @return A new instance of {@link Money}.
	 */
	public static Money of(String currencyCode, Number number) {
		return new Money(MoneyCurrency.of(currencyCode), number);
	}

	/**
	 * Static factory method for creating a new instance of {@link Money}.
	 * 
	 * @param isoCurrencyCode
	 *            The target currency as ISO currency code.
	 * @param number
	 *            The numeric part, not null.
	 * @return A new instance of {@link Money}.
	 */
	public static Money of(String currencyCode, Number number,
			MathContext mathContext) {
		return new Money(MoneyCurrency.of(currencyCode), number, mathContext);
	}

/**
	 * Factory method creating a zero instance with the given {@code currency);
	 * @param currency the target currency of the amount being created.
	 * @return
	 */
	public static Money ofZero(CurrencyUnit currency) {
		return new Money(currency, BigDecimal.ZERO, DEFAULT_MATH_CONTEXT);
	}

/**
	 * Factory method creating a zero instance with the given {@code currency);
	 * @param currency the target currency of the amount being created.
	 * @return
	 */
	public static Money ofZero(String currency) {
		return ofZero(MoneyCurrency.of(currency));
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
		Money other = (Money) obj;
		if (currency == null) {
			if (other.currency != null)
				return false;
		} else if (!currency.equals(other.currency))
			return false;
		return asNumberStripped().equals(other.asNumberStripped());
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

	/*
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	public int compareTo(MonetaryAmount o) {
		Objects.requireNonNull(o);
		int compare = -1;
		if (this.currency.equals(o.getCurrency())) {
			compare = this.number.compareTo(Money.from(o).number);
		} else {
			compare = this.currency.getCurrencyCode().compareTo(
					o.getCurrency().getCurrencyCode());
		}
		return compare;
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


	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.money.MonetaryAmount#abs()
	 */
	public Money abs() {
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
	public Money add(MonetaryAmount amount) {
		checkAmountParameter(amount);
		if (Money.from(amount).isZero()) {
			return this;
		}
		return new Money(this.currency, this.number.add(
				Money.from(amount).number));
	}

	private BigDecimal getBigDecimal(Number num) {
		if (num instanceof BigDecimal) {
			return (BigDecimal) num;
		}
		if (num instanceof Long || num instanceof Integer || num instanceof Byte || num instanceof AtomicLong) {
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

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.money.MonetaryAmount#divide(javax.money.MonetaryAmount)
	 */
	public Money divide(MonetaryAmount divisor) {
		checkAmountParameter(divisor);
		if (divisor.getAmountWhole() == 1
				&& divisor.getAmountFractionNumerator() == 0) {
			return this;
		}
		BigDecimal dec = this.number.divide(Money.from(divisor).number,
				this.mathContext);
		return new Money(this.currency, dec, null);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.money.MonetaryAmount#divide(javax.money.MonetaryAmount)
	 */
	public Money divide(Number divisor) {
		if (getBigDecimal(divisor).equals(BigDecimal.ONE)) {
			return this;
		}
		BigDecimal dec = this.number.divide(getBigDecimal(divisor),
				this.mathContext);
		return new Money(this.currency, dec);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * javax.money.MonetaryAmount#divideAndRemainder(javax.money.MonetaryAmount)
	 */
	public Money[] divideAndRemainder(MonetaryAmount divisor) {
		checkAmountParameter(divisor);
		BigDecimal[] dec = this.number.divideAndRemainder(
				Money.from(divisor).number);
		return new Money[] {
				new Money(this.currency, dec[0]),
				new Money(this.currency, dec[1]) };
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * javax.money.MonetaryAmount#divideAndRemainder(javax.money.MonetaryAmount)
	 */
	public Money[] divideAndRemainder(Number divisor) {
		BigDecimal[] dec = this.number.divideAndRemainder(
				getBigDecimal(divisor));
		return new Money[] {
				new Money(this.currency, dec[0]),
				new Money(this.currency, dec[1]) };
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * javax.money.MonetaryAmount#divideToIntegralValue(javax.money.MonetaryAmount
	 * )
	 */
	public Money divideToIntegralValue(MonetaryAmount divisor) {
		checkAmountParameter(divisor);
		BigDecimal dec = this.number.divideToIntegralValue(
				Money.from(divisor).number);
		return new Money(this.currency, dec);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.money.MonetaryAmount#divideToIntegralValue(Number) )D
	 */
	public Money divideToIntegralValue(Number divisor) {
		BigDecimal dec = this.number.divideToIntegralValue(
				getBigDecimal(divisor));
		return new Money(this.currency, dec);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.money.MonetaryAmount#multiply(javax.money.MonetaryAmount)
	 */
	public Money multiply(MonetaryAmount multiplicand) {
		checkAmountParameter(multiplicand);
		if (multiplicand.getAmountWhole() == 1
				&& multiplicand.getAmountFractionNumerator() == 0) {
			return this;
		}
		BigDecimal dec = this.number.multiply(
				Money.from(multiplicand).number);
		return new Money(this.currency, dec);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.money.MonetaryAmount#multiply(Number)
	 */
	public Money multiply(Number multiplicand) {
		if (getBigDecimal(multiplicand).equals(BigDecimal.ONE)) {
			return this;
		}
		BigDecimal dec = this.number.multiply(getBigDecimal(multiplicand));
		return new Money(this.currency, dec);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.money.MonetaryAmount#negate()
	 */
	public Money negate() {
		return new Money(this.currency, this.number.negate());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.money.MonetaryAmount#plus()
	 */
	public Money plus() {
		return new Money(this.currency, this.number.plus());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.money.MonetaryAmount#subtract(javax.money.MonetaryAmount)
	 */
	public Money subtract(MonetaryAmount subtrahend) {
		checkAmountParameter(subtrahend);
		if (Money.from(subtrahend).isZero()) {
			return this;
		}
		return new Money(this.currency, this.number.subtract(
				Money.from(subtrahend).number));
	}

	/**
	 * Returns an amount with all trailing zeroes stripped.
	 * 
	 * @see BigDecimal#stripTrailingZeros()
	 * @return the amount with the zeroes stripped.
	 */
	public Money stripTrailingZeros() {
		if (isZero()) {
			return new Money(this.currency, BigDecimal.ZERO);
		}
		return new Money(this.currency, this.number.stripTrailingZeros());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.money.MonetaryAmount#pow(int)
	 */
	public Money pow(int n) {
		return new Money(this.currency, this.number.pow(n));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.money.MonetaryAmount#ulp()
	 */
	public Money ulp() {
		return new Money(this.currency, this.number.ulp());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.money.MonetaryAmount#remainder(javax.money.MonetaryAmount)
	 */
	public Money remainder(MonetaryAmount divisor) {
		checkAmountParameter(divisor);
		return new Money(this.currency, this.number.remainder(
				Money.from(divisor).number));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.money.MonetaryAmount#remainder(Number)
	 */
	public Money remainder(Number divisor) {
		return new Money(this.currency, this.number.remainder(
				getBigDecimal(divisor)));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.money.MonetaryAmount#scaleByPowerOfTen(int)
	 */
	public Money scaleByPowerOfTen(int n) {
		return new Money(this.currency, this.number.scaleByPowerOfTen(n));
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
	public Money with(Number amount) {
		checkNumber(amount);
		return new Money(this.currency, getBigDecimal(amount, this.mathContext));
	}

	/**
	 * Creates a new Money instance, hereby changing the {@link MathContext} to
	 * be used. This allows to adapt the {@link MathContext}, when required.
	 * 
	 * @param context
	 *            the {@link MathContext} to be replaced, not {@code null}
	 * @return the new amount with the same numeric value and
	 *         {@link CurrencyUnit}, but the new {@link MathContext}.
	 */
	public Money with(MathContext context) {
		Objects.requireNonNull(context, "MathContext required");
		return new Money(currency, this.number, context);
	}

	/**
	 * Creates a new Money instance, by just replacing the {@link CurrencyUnit}
	 * and the numeric amount.
	 * 
	 * @param currency
	 *            the currency unit to be replaced, not {@code null}
	 * @return the new amount with the same numeric value and
	 *         {@link MathContext}, but the new {@link CurrencyUnit}.
	 */
	public Money with(CurrencyUnit currency, Number amount) {
		checkNumber(amount);
		return new Money(currency, getBigDecimal(amount, this.mathContext));
	}

	/**
	 * Creates a new Money instance, by just replacing the {@link CurrencyUnit}.
	 * 
	 * @param currency
	 *            the currency unit to be replaced, not {@code null}
	 * @return the new amount with the same numeric value and
	 *         {@link MathContext}, but the new {@link CurrencyUnit}.
	 */
	public Money with(CurrencyUnit currency) {
		Objects.requireNonNull(currency, "currency required");
		return new Money(currency, this.number);
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
	public boolean isLessThan(MonetaryAmount amount) {
		checkAmountParameter(amount);
		return number.compareTo(Money.from(amount).number) < 0;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * javax.money.MonetaryAmount#lessThanOrEqualTo(javax.money.MonetaryAmount)
	 */
	public boolean isLessThanOrEqualTo(MonetaryAmount amount) {
		checkAmountParameter(amount);
		return number.compareTo(Money.from(amount).number) <= 0;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.money.MonetaryAmount#greaterThan(javax.money.MonetaryAmount)
	 */
	public boolean isGreaterThan(MonetaryAmount amount) {
		checkAmountParameter(amount);
		return number.compareTo(Money.from(amount).number) > 0;
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
		return number.compareTo(Money.from(amount).number) >= 0;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.money.MonetaryAmount#isEqualTo(javax.money.MonetaryAmount)
	 */
	public boolean isEqualTo(MonetaryAmount amount) {
		checkAmountParameter(amount);
		return equals(Money.from(amount));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.money.MonetaryAmount#isNotEqualTo(javax.money.MonetaryAmount)
	 */
	public boolean isNotEqualTo(MonetaryAmount amount) {
		checkAmountParameter(amount);
		return !equals(Money.from(amount));
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
	 * @see javax.money.MonetaryAmount#query(javax.money.MonetaryQuery)
	 */
	@Override
	public <T> T query(MonetaryQuery<T> query) {
		return query.queryFrom(this);
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

	/**
	 * Gets the {@link BigDecimal} representation of the numeric value of this
	 * item.
	 * 
	 * @return The {@link BigDecimal} represention matching best.
	 */
	public BigDecimal asNumber() {
		return this.number;
	}

	private void writeObject(ObjectOutputStream oos) throws IOException {
		oos.writeObject(this.number);
		oos.writeObject(this.currency);
		oos.writeObject(this.mathContext);
	}

	private void readObject(ObjectInputStream ois) throws IOException,
			ClassNotFoundException {
		this.number = (BigDecimal) ois.readObject();
		this.currency = (CurrencyUnit) ois.readObject();
		this.mathContext = (MathContext) ois.readObject();
	}

	private void readObjectNoData()
			throws ObjectStreamException {
		if (this.number == null) {
			this.number = BigDecimal.ZERO;
		}
		if (this.currency == null) {
			this.currency = MoneyCurrency.of(
					"XXX"); // no currency
		}
		if (this.mathContext == null) {
			this.mathContext = DEFAULT_MATH_CONTEXT;
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return currency.getCurrencyCode() + ' ' + number.toString();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.money.MonetaryAmount#getAmountWhole()
	 */
	@Override
	public long getAmountWhole() {
		return this.number.setScale(0,
				RoundingMode.DOWN).longValueExact();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.money.MonetaryAmount#getAmountFractionNumerator()
	 */
	@Override
	public long getAmountFractionNumerator() {
		BigDecimal bd = this.number.remainder(BigDecimal.ONE);
		return bd.movePointRight(getScale()).longValueExact();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.money.MonetaryAmount#getAmountFractionDenominator()
	 */
	@Override
	public long getAmountFractionDenominator() {
		return BigDecimal.valueOf(10).pow(getScale()).longValueExact();
	}

	@Override
	public Money with(MonetaryAdjuster adjuster) {
		MonetaryAmount amt = adjuster.adjustInto(this);
		return Money.from(amt);
	}

	public static Money from(MonetaryAmount amt) {
		if (amt.getClass() == Money.class) {
			return (Money) amt;
		}
		if (amt.getClass() == FastMoney.class) {
			return Money.of(amt.getCurrency(), ((FastMoney) amt).asNumber(),
					DEFAULT_MATH_CONTEXT);
		}
		return Money.of(amt.getCurrency(), asNumber(amt),
				DEFAULT_MATH_CONTEXT);
	}

	public static BigDecimal asNumber(MonetaryAmount amt) {
		if (amt instanceof Money) {
			return ((Money) amt).number;
		}
		long denom = amt.getAmountFractionDenominator();
		for (int i = 0; i < DENOM_ARRAY.length; i++) {
			if (denom == DENOM_ARRAY[i]) {
				try {
					long total = amt.getAmountWhole() * denom;
					total = total + amt.getAmountFractionNumerator();
					return BigDecimal.valueOf(total, i);
				} catch (ArithmeticException ex) {
					// go ahead, using slow conversion
				}
			}
		}
		// slow creation follows here
		BigDecimal whole = BigDecimal.valueOf(amt.getAmountWhole());
		BigDecimal fraction = BigDecimal.valueOf(amt
				.getAmountFractionNumerator());
		return whole.add(fraction);
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
