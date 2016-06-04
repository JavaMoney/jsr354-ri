/**
 * Copyright (c) 2012, 2015, Credit Suisse (Anatole Tresch), Werner Keil and others by the @author tag.
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

import org.javamoney.moneta.ToStringMonetaryAmountFormat.ToStringMonetaryAmountFormatStyle;
import org.javamoney.moneta.internal.MoneyAmountBuilder;
import org.javamoney.moneta.spi.DefaultNumberValue;
import org.javamoney.moneta.spi.MoneyUtils;

import javax.money.*;
import javax.money.format.MonetaryAmountFormat;
import java.io.Serializable;
import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.Objects;

/**
 * Default immutable implementation of {@link MonetaryAmount} based
 * on {@link BigDecimal} as numeric representation.
 * <p>
 * As required by {@link MonetaryAmount} this class is final, thread-safe,
 * immutable and serializable.
 * </p><p>
 * This class can be configured with an arbitrary {@link MonetaryContext}. The
 * default {@link MonetaryContext} used models by default the same settings as
 * {@link MathContext#DECIMAL64} . This default {@link MonetaryContext} can also
 * be reconfigured by adding a file {@code /javamoney.properties} to the
 * classpath, with the following content:
 * </p>
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
 * @author Anatole Tresch
 * @author Werner Keil
 * @version 0.7
 */
public final class Money implements MonetaryAmount, Comparable<MonetaryAmount>, Serializable {

    /**
     * serialVersionUID.
     */
    private static final long serialVersionUID = -7565813772046251748L;

    /**
     * The default {@link MonetaryContext} applied, if not set explicitly on
     * creation.
     */
    public static final MonetaryContext DEFAULT_MONETARY_CONTEXT = new DefaultMonetaryContextFactory().getContext();

    /**
     * The currency of this amount.
     */
    private final CurrencyUnit currency;

    /**
     * the {@link MonetaryContext} used by this instance, e.g. on division.
     */
    private final MonetaryContext monetaryContext;

    /**
     * The numeric part of this amount.
     */
    private final BigDecimal number;

    /**
     * Creates a new instance os {@link Money}.
     *
     * @param currency the currency, not null.
     * @param number   the amount, not null.
     * @throws ArithmeticException If the number exceeds the capabilities of the default
     *                             {@link MonetaryContext}.
     */
    private Money(BigDecimal number, CurrencyUnit currency) {
        this(number, currency, null);
    }

    /**
     * Creates a new instance of {@link Money}.
     *
     * @param currency        the currency, not {@code null}.
     * @param number          the amount, not {@code null}.
     * @param monetaryContext the {@link MonetaryContext}, if {@code null}, the default is
     *                        used.
     * @throws ArithmeticException If the number exceeds the capabilities of the
     *                             {@link MonetaryContext} used.
     */
    private Money(BigDecimal number, CurrencyUnit currency, MonetaryContext monetaryContext) {
        Objects.requireNonNull(currency, "Currency is required.");
        this.currency = currency;
        if (Objects.nonNull(monetaryContext)) {
            this.monetaryContext = monetaryContext;
        } else {
            this.monetaryContext = DEFAULT_MONETARY_CONTEXT;
        }
        Objects.requireNonNull(number, "Number is required.");
        this.number = MoneyUtils.getBigDecimal(number, monetaryContext);
    }

    /**
     * Returns the amount’s currency, modelled as {@link CurrencyUnit}.
     * Implementations may co-variantly change the return type to a more
     * specific implementation of {@link CurrencyUnit} if desired.
     *
     * @return the currency, never {@code null}
     * @see javax.money.MonetaryAmount#getCurrency()
     */
    @Override
    public CurrencyUnit getCurrency() {
        return currency;
    }

    /**
     * Access the {@link MonetaryContext} used by this instance.
     *
     * @return the {@link MonetaryContext} used, never null.
     * @see javax.money.MonetaryAmount#getContext()
     */
    @Override
    public MonetaryContext getContext() {
        return monetaryContext;
    }

    /**
     * Gets the number representation of the numeric value of this item.
     *
     * @return The {@link Number} representation matching best.
     */
    @Override
    public NumberValue getNumber() {
        return new DefaultNumberValue(number);
    }

    /**
     * Method that returns BigDecimal.ZERO, if {@link #isZero()}, and
     * {@link #number #stripTrailingZeros()} in all other cases.
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
    public int compareTo(MonetaryAmount o) {
        Objects.requireNonNull(o);
        int compare = getCurrency().getCurrencyCode().compareTo(o.getCurrency().getCurrencyCode());
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
        if (NumberVerifier.isInfinityAndNotNaN(divisor)) {
            return Money.of(0, getCurrency());
        }
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
        if (NumberVerifier.isInfinityAndNotNaN(divisor)) {
            Money zero = Money.of(0, getCurrency());
            return new Money[]{zero, zero};
        }
        return divideAndRemainder(new BigDecimal(String.valueOf(divisor)));
    }

    /*
     * (non-Javadoc)
     *
     * @see javax.money.MonetaryAmount#multiply(Number)
     */
    @Override
    public Money multiply(long multiplicand) {
        if (multiplicand == 1L) {
            return this;
        }
        return multiply(BigDecimal.valueOf(multiplicand));
    }

    /*
     * (non-Javadoc)
     *
     * @see javax.money.MonetaryAmount#multiply(Number)
     */
    @Override
    public Money multiply(double multiplicand) {
    	NumberVerifier.checkNoInfinityOrNaN(multiplicand);
        if (multiplicand == 1.0d) {
            return this;
        }
        return multiply(new BigDecimal(String.valueOf(multiplicand)));
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
        if (NumberVerifier.isInfinityAndNotNaN(divisor)) {
            return Money.of(0, getCurrency());
        }
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
     * }(non-Javadoc)
     *
     * @see javax.money.MonetaryAmount#query(javax.money.MonetaryQuery)
     */
    @Override
    public <R> R query(MonetaryQuery<R> query) {
        Objects.requireNonNull(query);
        try {
            return query.queryFrom(this);
        } catch (MonetaryException e) {
            throw e;
        } catch (Exception e) {
            throw new MonetaryException("Query failed: " + query, e);
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see javax.money.MonetaryAmount#with(javax.money.MonetaryOperator)
     */
    @Override
    public Money with(MonetaryOperator operator) {
        Objects.requireNonNull(operator);
        try {
            return Money.class.cast(operator.apply(this));
        } catch (MonetaryException e) {
            throw e;
        } catch (Exception e) {
            throw new MonetaryException("Operator failed: " + operator, e);
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see javax.money.MonetaryAmount#add(javax.money.MonetaryAmount)
     */
    @Override
    public Money add(MonetaryAmount amount) {
        MoneyUtils.checkAmountParameter(amount, this.currency);
        if (amount.isZero()) {
            return this;
        }
        return new Money(this.number.add(amount.getNumber().numberValue(BigDecimal.class)), getCurrency());
    }

    /*
     * (non-Javadoc)
     *
     * @see javax.money.MonetaryAmount#divide(java.lang.Number)
     */
    @Override
    public Money divide(Number divisor) {
        if (NumberVerifier.isInfinityAndNotNaN(divisor)) {
            return Money.of(0, getCurrency());
        }
        BigDecimal divisorBD = MoneyUtils.getBigDecimal(divisor);
        if (divisorBD.equals(BigDecimal.ONE)) {
            return this;
        }
        BigDecimal dec =
                this.number.divide(divisorBD, MoneyUtils.getMathContext(getContext(), RoundingMode.HALF_EVEN));
        return new Money(dec, getCurrency());
    }

    @Override
    public Money[] divideAndRemainder(Number divisor) {
        if (NumberVerifier.isInfinityAndNotNaN(divisor)) {
            Money zero = Money.of(0, getCurrency());
            return new Money[]{zero, zero};
        }
        BigDecimal divisorBD = MoneyUtils.getBigDecimal(divisor);
        if (divisorBD.equals(BigDecimal.ONE)) {
            return new Money[]{this, new Money(BigDecimal.ZERO, getCurrency())};
        }
        BigDecimal[] dec = this.number.divideAndRemainder(divisorBD);
        return new Money[]{new Money(dec[0], getCurrency()), new Money(dec[1], getCurrency())};
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
        return divideToIntegralValue(MoneyUtils.getBigDecimal(divisor));
    }

    @Override
    public Money divideToIntegralValue(double divisor) {
        if (NumberVerifier.isInfinityAndNotNaN(divisor)) {
            return Money.of(0, getCurrency());
        }
        return divideToIntegralValue(MoneyUtils.getBigDecimal(divisor));
    }

    @Override
    public Money divideToIntegralValue(Number divisor) {
        if (NumberVerifier.isInfinityAndNotNaN(divisor)) {
            return Money.of(0, getCurrency());
        }
        BigDecimal divisorBD = MoneyUtils.getBigDecimal(divisor);
        BigDecimal dec = this.number.divideToIntegralValue(divisorBD);
        return new Money(dec, getCurrency());
    }

    /*
     * (non-Javadoc)
     *
     * @see org.javamoney.moneta.AbstractMoney#multiply(java.lang.Number)
     */
    @Override
    public Money multiply(Number multiplicand) {
    	NumberVerifier.checkNoInfinityOrNaN(multiplicand);
        BigDecimal multiplicandBD = MoneyUtils.getBigDecimal(multiplicand);
        if (multiplicandBD.equals(BigDecimal.ONE)) {
            return this;
        }
        BigDecimal dec = this.number.multiply(multiplicandBD);
        return new Money(dec, getCurrency());
    }

    /*
     * (non-Javadoc)
     *
     * @see javax.money.MonetaryAmount#negate()
     */
    @Override
    public Money negate() {
        return new Money(this.number.negate(), getCurrency());
    }

    /*
     * (non-Javadoc)
     *
     * @see javax.money.MonetaryAmount#plus()
     */
    @Override
    public Money plus() {
        return this;
    }

    /*
     * (non-Javadoc)
     *
     * @see javax.money.MonetaryAmount#subtract(javax.money.MonetaryAmount)
     */
    @Override
    public Money subtract(MonetaryAmount amount) {
        MoneyUtils.checkAmountParameter(amount, this.currency);
        if (amount.isZero()) {
            return this;
        }
        return new Money(this.number.subtract(amount.getNumber().numberValue(BigDecimal.class)), getCurrency());
    }

    /*
     * (non-Javadoc)
     *
     * @see javax.money.MonetaryAmount#stripTrailingZeros()
     */
    @Override
    public Money stripTrailingZeros() {
        if (isZero()) {
            return new Money(BigDecimal.ZERO, getCurrency());
        }
        return new Money(this.number.stripTrailingZeros(), getCurrency());
    }

    /*
     * (non-Javadoc)
     *
     * @see org.javamoney.moneta.AbstractMoney#remainder(java.math.BigDecimal)
     */
    @Override
    public Money remainder(Number divisor) {
        if (NumberVerifier.isInfinityAndNotNaN(divisor)) {
            return new Money(BigDecimal.ZERO, getCurrency());
        }
        BigDecimal bd = MoneyUtils.getBigDecimal(divisor);
        return new Money(this.number.remainder(bd), getCurrency());
    }

    /*
     * (non-Javadoc)
     *
     * @see javax.money.MonetaryAmount#scaleByPowerOfTen(int)
     */
    @Override
    public Money scaleByPowerOfTen(int power) {
        return new Money(this.number.scaleByPowerOfTen(power), getCurrency());
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
    public boolean isLessThan(MonetaryAmount amount) {
        MoneyUtils.checkAmountParameter(amount, this.currency);
        return number.stripTrailingZeros()
                .compareTo(amount.getNumber().numberValue(BigDecimal.class).stripTrailingZeros()) < 0;
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * javax.money.MonetaryAmount#isLessThanOrEqualTo(javax.money.MonetaryAmount
     * )
     */
    @Override
    public boolean isLessThanOrEqualTo(MonetaryAmount amount) {
        MoneyUtils.checkAmountParameter(amount, this.currency);
        return number.stripTrailingZeros()
                .compareTo(amount.getNumber().numberValue(BigDecimal.class).stripTrailingZeros()) <= 0;
    }

    /*
     * (non-Javadoc)
     *
     * @see javax.money.MonetaryAmount#isGreaterThan(javax.money.MonetaryAmount)
     */
    @Override
    public boolean isGreaterThan(MonetaryAmount amount) {
        MoneyUtils.checkAmountParameter(amount, this.currency);
        return number.stripTrailingZeros()
                .compareTo(amount.getNumber().numberValue(BigDecimal.class).stripTrailingZeros()) > 0;
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * javax.money.MonetaryAmount#isGreaterThanOrEqualTo(javax.money.MonetaryAmount
     * ) #see
     */
    @Override
    public boolean isGreaterThanOrEqualTo(MonetaryAmount amount) {
        MoneyUtils.checkAmountParameter(amount, this.currency);
        return number.stripTrailingZeros()
                .compareTo(amount.getNumber().numberValue(BigDecimal.class).stripTrailingZeros()) >= 0;
    }

    /*
     * (non-Javadoc)
     *
     * @see javax.money.MonetaryAmount#isEqualTo(javax.money.MonetaryAmount)
     */
    @Override
    public boolean isEqualTo(MonetaryAmount amount) {
        MoneyUtils.checkAmountParameter(amount, this.currency);
        return number.stripTrailingZeros()
                .compareTo(amount.getNumber().numberValue(BigDecimal.class).stripTrailingZeros()) == 0;
    }

    /*
     * (non-Javadoc)
     *
     * @see javax.money.MonetaryAmount#getFactory()
     */
    @Override
    public MonetaryAmountFactory<Money> getFactory() {
        return new MoneyAmountBuilder().setAmount(this);
    }

    /*
     * (non-Javadoc)
     *
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (obj instanceof Money) {
            Money other = (Money) obj;
            return Objects.equals(getCurrency(), other.getCurrency()) &&
                    Objects.equals(getNumberStripped(), other.getNumberStripped());
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
        return getCurrency().getCurrencyCode() + ' ' + number.toString();
    }

    /*
     * (non-Javadoc)
     *
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        return Objects.hash(getCurrency(), getNumberStripped());
    }

    /**
     * Creates a new instance of {@link Money}, using the default
     * {@link MonetaryContext}.
     *
     * @param number   numeric value, not {@code null}.
     * @param currency currency unit, not {@code null}.
     * @return a {@code Money} combining the numeric value and currency unit.
     * @throws ArithmeticException If the number exceeds the capabilities of the default
     *                             {@link MonetaryContext} used.
     */
    public static Money of(BigDecimal number, CurrencyUnit currency) {
        return new Money(number, currency);
    }

    /**
     * Creates a new instance of {@link Money}, using an explicit
     * {@link MonetaryContext}.
     *
     * @param number          numeric value, not {@code null}.
     * @param currency        currency unit, not {@code null}.
     * @param monetaryContext the {@link MonetaryContext} to be used, if {@code null} the
     *                        default {@link MonetaryContext} is used.
     * @return a {@code Money} instance based on the monetary context with the
     * given numeric value, currency unit.
     * @throws ArithmeticException If the number exceeds the capabilities of the
     *                             {@link MonetaryContext} used.
     */
    public static Money of(BigDecimal number, CurrencyUnit currency, MonetaryContext monetaryContext) {
        return new Money(number, currency, monetaryContext);
    }

    /**
     * Creates a new instance of {@link Money}, using the default
     * {@link MonetaryContext}.
     *
     * @param currency The target currency, not null.
     * @param number   The numeric part, not null.
     * @return A new instance of {@link Money}.
     * @throws ArithmeticException If the number exceeds the capabilities of the default
     *                             {@link MonetaryContext} used.
     */
    public static Money of(Number number, CurrencyUnit currency) {
        return new Money(MoneyUtils.getBigDecimal(number), currency);
    }

    /**
     * Creates a new instance of {@link Money}, using an explicit
     * {@link MonetaryContext}.
     *
     * @param currency        The target currency, not null.
     * @param number          The numeric part, not null.
     * @param monetaryContext the {@link MonetaryContext} to be used, if {@code null} the
     *                        default {@link MonetaryContext} is used.
     * @return A new instance of {@link Money}.
     * @throws ArithmeticException If the number exceeds the capabilities of the
     *                             {@link MonetaryContext} used.
     */
    public static Money of(Number number, CurrencyUnit currency, MonetaryContext monetaryContext) {
        return new Money(MoneyUtils.getBigDecimal(number), currency, monetaryContext);
    }

    /**
     * Static factory method for creating a new instance of {@link Money}.
     *
     * @param currencyCode The target currency as ISO currency code.
     * @param number       The numeric part, not null.
     * @return A new instance of {@link Money}.
     */
    public static Money of(Number number, String currencyCode) {
        return new Money(MoneyUtils.getBigDecimal(number), Monetary.getCurrency(currencyCode));
    }

    /**
     * Static factory method for creating a new instance of {@link Money}.
     *
     * @param currencyCode The target currency as ISO currency code.
     * @param number       The numeric part, not null.
     * @return A new instance of {@link Money}.
     */
    public static Money of(BigDecimal number, String currencyCode) {
        return new Money(number, Monetary.getCurrency(currencyCode));
    }

    /**
     * Static factory method for creating a new instance of {@link Money}.
     *
     * @param currencyCode    The target currency as ISO currency code.
     * @param number          The numeric part, not null.
     * @param monetaryContext the {@link MonetaryContext} to be used, if {@code null} the
     *                        default {@link MonetaryContext} is used.
     * @return A new instance of {@link Money}.
     */
    public static Money of(Number number, String currencyCode, MonetaryContext monetaryContext) {
        return new Money(MoneyUtils.getBigDecimal(number), Monetary.getCurrency(currencyCode),
                monetaryContext);
    }

    /**
     * Static factory method for creating a new instance of {@link Money}.
     *
     * @param currencyCode    The target currency as ISO currency code.
     * @param number          The numeric part, not null.
     * @param monetaryContext the {@link MonetaryContext} to be used, if {@code null} the
     *                        default {@link MonetaryContext} is used.
     * @return A new instance of {@link Money}.
     */
    public static Money of(BigDecimal number, String currencyCode, MonetaryContext monetaryContext) {
        return new Money(number, Monetary.getCurrency(currencyCode), monetaryContext);
    }

    /**
     * Obtains an instance of {@link Money} representing zero.
     * @param currency
     * @return an instance of {@link Money} representing zero.
     * @since 1.0.1
     */
    public static Money zero(CurrencyUnit currency) {
        return new Money(BigDecimal.ZERO, currency);
    }

   	 /**
      * Obtains an instance of {@code Money} from an amount in minor units.
      * For example, {@code ofMinor(USD, 1234)} creates the instance {@code USD 12.34}.
      * @param currency  the currency
      * @param amountMinor  the amount of money in the minor division of the currency
      * @return the Money from minor units
      * @throws NullPointerException when the currency is null
      * @throws IllegalArgumentException when {@link CurrencyUnit#getDefaultFractionDigits()} is lesser than zero.
      * @see {@link CurrencyUnit#getDefaultFractionDigits()}
      * @since 1.0.1
      */
     public static Money ofMinor(CurrencyUnit currency, long amountMinor) {
    	 return ofMinor(currency, amountMinor, currency.getDefaultFractionDigits());
     }

     /**
      * Obtains an instance of {@code Money} from an amount in minor units.
      * For example, {@code ofMinor(USD, 1234, 2)} creates the instance {@code USD 12.34}.
      * @param currency  the currency, not null
      * @param amountMinor  the amount of money in the minor division of the currency
      * @param fractionDigits number of digits
      * @return the monetary amount from minor units
      * @see {@link CurrencyUnit#getDefaultFractionDigits()}
      * @see {@link Money#ofMinor(CurrencyUnit, long, int)}
      * @throws NullPointerException when the currency is null
      * @throws IllegalArgumentException when the fractionDigits is negative
      * @since 1.0.1
      */
     public static Money ofMinor(CurrencyUnit currency, long amountMinor, int fractionDigits) {
     	if(fractionDigits < 0) {
     		throw new IllegalArgumentException("The fractionDigits cannot be negative");
     	}
     	return of(BigDecimal.valueOf(amountMinor, fractionDigits), currency);
     }

    /**
     * Converts (if necessary) the given {@link MonetaryAmount} to a
     * {@link Money} instance. The {@link MonetaryContext} will be adapted as
     * necessary, if the precision of the given amount exceeds the capabilities
     * of the default {@link MonetaryContext}.
     *
     * @param amt the amount to be converted
     * @return an according Money instance.
     */
    public static Money from(MonetaryAmount amt) {
        if (amt.getClass() == Money.class) {
            return (Money) amt;
        }
        return Money.of(amt.getNumber().numberValue(BigDecimal.class), amt.getCurrency(), amt.getContext());
    }

    /**
     * Obtains an instance of Money from a text string such as 'EUR 25.25'.
     *
     * @param text the text to parse not null
     * @return Money instance
     * @throws NullPointerException
     * @throws NumberFormatException
     * @throws UnknownCurrencyException
     */
    public static Money parse(CharSequence text) {
        return parse(text, DEFAULT_FORMATTER);
    }

    /**
     * Obtains an instance of Money from a text using specific formatter.
     *
     * @param text      the text to parse not null
     * @param formatter the formatter to use not null
     * @return Money instance
     */
    public static Money parse(CharSequence text, MonetaryAmountFormat formatter) {
        return from(formatter.parse(text));
    }

    private static final ToStringMonetaryAmountFormat DEFAULT_FORMATTER = ToStringMonetaryAmountFormat
            .of(ToStringMonetaryAmountFormatStyle.MONEY);

    /**
     * Just to don't break the compatibility.
     * Don't use it
     * @param number
     */
    @Deprecated
    public static boolean isInfinityAndNotNaN(Number number) {
        if (Double.class == number.getClass() || Float.class == number.getClass()) {
            double dValue = number.doubleValue();
            if (Double.isNaN(dValue)) {
                throw new ArithmeticException("Not a valid input: NaN.");
            } else if (Double.isInfinite(dValue)) {
                return true;
            }
        }
        return false;
    }
    /**
     * Just to don't break the compatibility.
     * Don't use it
     * @param number
     */
    @Deprecated
    public static void checkNoInfinityOrNaN(Number number) {
        if (Double.class == number.getClass() || Float.class == number.getClass()) {
            double dValue = number.doubleValue();
            if (Double.isNaN(dValue)) {
                throw new ArithmeticException("Not a valid input: NaN.");
            } else if (Double.isInfinite(dValue)) {
                throw new ArithmeticException("Not a valid input: INFINITY: " + dValue);
            }
        }
    }
}
