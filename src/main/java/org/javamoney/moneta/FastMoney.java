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
import org.javamoney.moneta.internal.FastMoneyAmountBuilder;
import org.javamoney.moneta.spi.DefaultNumberValue;
import org.javamoney.moneta.spi.MonetaryConfig;
import org.javamoney.moneta.spi.MoneyUtils;

import javax.money.*;
import javax.money.format.MonetaryAmountFormat;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * <code>long</code> based implementation of {@link MonetaryAmount}.This class internally uses a
 * single long number as numeric representation, which basically is interpreted as minor units.<p>
 * It suggested to have a performance advantage of a 10-15 times faster compared to {@link Money},
 * which internally uses {@link BigDecimal}. Nevertheless this comes with a price of less precision.
 * As an example performing the following calculation one million times, results in slightly
 * different results:
 * </p>
 * <pre><code>
 * Money money1 = money1.add(Money.of(1234567.3444, "EUR"));
 * money1 = money1.subtract(Money.of(232323, "EUR"));
 * money1 = money1.multiply(3.4);
 * money1 = money1.divide(5.456);
 * </code></pre>
 * <p>
 * Executed one million (1000000) times this results in {@code EUR 1657407.962529182}, calculated in
 * 3680 ms, or roughly 3ns/loop.
 * <p>
 * whereas
 * </p>
 * <pre><code>
 * FastMoney money1 = money1.add(FastMoney.of(1234567.3444, "EUR"));
 * money1 = money1.subtract(FastMoney.of(232323, "EUR"));
 * money1 = money1.multiply(3.4);
 * money1 = money1.divide(5.456);
 * </code></pre>
 * <p>
 * executed one million (1000000) times results in {@code EUR 1657407.96251}, calculated in 179 ms,
 * which is less than 1ns/loop.
 * </p><p>
 * Also note than mixing up types my drastically change the performance behavior. E.g. replacing the
 * code above with the following: *
 * </p>
 * <pre><code>
 * FastMoney money1 = money1.add(Money.of(1234567.3444, "EUR"));
 * money1 = money1.subtract(FastMoney.of(232323, "EUR"));
 * money1 = money1.multiply(3.4);
 * money1 = money1.divide(5.456);
 * </code></pre>
 * <p>
 * executed one million (1000000) times may execute significantly longer, since monetary amount type
 * conversion is involved.
 * </p><p>
 * Basically, when mixing amount implementations, the performance of the amount, on which most of
 * the operations are operated, has the most significant impact on the overall performance behavior.
 *
 * @author Anatole Tresch
 * @author Werner Keil
 * @version 1.0
 */
public final class FastMoney implements MonetaryAmount, Comparable<MonetaryAmount>, Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * The logger used.
     */
    private static final Logger LOG = Logger.getLogger(FastMoney.class.getName());

    /**
     * The currency of this amount.
     */
    private final CurrencyUnit currency;

    /**
     * The numeric part of this amount.
     */
    private final long number;

    /**
     * The current scale represented by the number.
     */
    private static final int SCALE = 5;

    /**
     * the {@link MonetaryContext} used by this instance, e.g. on division.
     */
    private static final MonetaryContext MONETARY_CONTEXT =
            MonetaryContextBuilder.of(FastMoney.class).setMaxScale(SCALE).setFixedScale(true).setPrecision(19).build();

    /**
     * Maximum possible value supported, using XX (no currency).
     */
    public static final FastMoney MAX_VALUE = new FastMoney(Long.MAX_VALUE, Monetary.getCurrency("XXX"));
    /**
     * Maximum possible numeric value supported.
     */
    private static final BigDecimal MAX_BD = MAX_VALUE.getBigDecimal();
    /**
     * Minimum possible value supported, using XX (no currency).
     */
    public static final FastMoney MIN_VALUE = new FastMoney(Long.MIN_VALUE, Monetary.getCurrency("XXX"));
    /**
     * Minimum possible numeric value supported.
     */
    private static final BigDecimal MIN_BD = MIN_VALUE.getBigDecimal();


    /**
     * Creates a new instance os {@link FastMoney}.
     *
     * @param currency the currency, not null.
     * @param number   the amount, not null.
     */
    private FastMoney(Number number, CurrencyUnit currency, boolean allowInternalRounding) {
        Objects.requireNonNull(currency, "Currency is required.");
        this.currency = currency;
        Objects.requireNonNull(number, "Number is required.");
        this.number = getInternalNumber(number, allowInternalRounding);
    }

    /**
     * Creates a new instance os {@link FastMoney}.
     *
     * @param currency    the currency, not null.
     * @param numberValue the numeric value, not null.
     */
    private FastMoney(NumberValue numberValue, CurrencyUnit currency, boolean allowInternalRounding) {
        Objects.requireNonNull(currency, "Currency is required.");
        this.currency = currency;
        Objects.requireNonNull(numberValue, "Number is required.");
        this.number = getInternalNumber(numberValue.numberValue(BigDecimal.class), allowInternalRounding);
    }

    /**
     * Creates a new instance os {@link FastMoney}.
     *
     * @param number   The format number value
     * @param currency the currency, not null.
     */
    private FastMoney(long number, CurrencyUnit currency) {
        Objects.requireNonNull(currency, "Currency is required.");
        this.currency = currency;
        this.number = number;
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
        return MONETARY_CONTEXT;
    }

    private long getInternalNumber(Number number, boolean allowInternalRounding) {
        BigDecimal bd = MoneyUtils.getBigDecimal(number);
        if (!allowInternalRounding && bd.scale() > SCALE) {
            throw new ArithmeticException(number + " can not be represented by this class, scale > " + SCALE);
        }
        if (bd.compareTo(MIN_BD) < 0) {
            throw new ArithmeticException("Overflow: " + number + " < " + MIN_BD);
        } else if (bd.compareTo(MAX_BD) > 0) {
            throw new ArithmeticException("Overflow: " + number + " > " + MAX_BD);
        }
        return bd.movePointRight(SCALE).longValue();
    }


    /**
     * Static factory method for creating a new instance of {@link FastMoney}.
     *
     * @param currency      The target currency, not null.
     * @param numberBinding The numeric part, not null.
     * @return A new instance of {@link FastMoney}.
     */
    public static FastMoney of(NumberValue numberBinding, CurrencyUnit currency) {
        return new FastMoney(numberBinding, currency, false);
    }

    /**
     * Static factory method for creating a new instance of {@link FastMoney}.
     *
     * @param currency The target currency, not null.
     * @param number   The numeric part, not null.
     * @return A new instance of {@link FastMoney}.
     */
    public static FastMoney of(Number number, CurrencyUnit currency) {
        return new FastMoney(number, currency, false);
    }

    /**
     * Static factory method for creating a new instance of {@link FastMoney}.
     *
     * @param currencyCode The target currency as currency code.
     * @param number       The numeric part, not null.
     * @return A new instance of {@link FastMoney}.
     */
    public static FastMoney of(Number number, String currencyCode) {
        CurrencyUnit currency = Monetary.getCurrency(currencyCode);
        return of(number, currency);
    }

    /**
     * Obtains an instance of {@link FastMoney} representing zero.
     * @param currency
     * @return an instance of {@link FastMoney} representing zero.
     * @since 1.0.1
     */
    public static FastMoney zero(CurrencyUnit currency) {
        return of(BigDecimal.ZERO, currency);
    }

    /**
     * Obtains an instance of {@code FastMoney} from an amount in minor units.
     * For example, {@code ofMinor(USD, 1234)} creates the instance {@code USD 12.34}.
     * @param currency  the currency, not null
     * @param amountMinor  the amount of money in the minor division of the currency
     * @return the monetary amount from minor units
     * @see {@link CurrencyUnit#getDefaultFractionDigits()}
     * @see {@link FastMoney#ofMinor(CurrencyUnit, long, int)}
     * @throws NullPointerException when the currency is null
     * @throws IllegalArgumentException when {@link CurrencyUnit#getDefaultFractionDigits()} is lesser than zero.
     * @since 1.0.1
     */
    public static FastMoney ofMinor(CurrencyUnit currency, long amountMinor) {
    	return ofMinor(currency, amountMinor, currency.getDefaultFractionDigits());
    }

    /**
     * Obtains an instance of {@code FastMoney} from an amount in minor units.
     * For example, {@code ofMinor(USD, 1234, 2)} creates the instance {@code USD 12.34}.
     * @param currency  the currency, not null
     * @param amountMinor  the amount of money in the minor division of the currency
     * @param factionDigits number of digits
     * @return the monetary amount from minor units
     * @see {@link CurrencyUnit#getDefaultFractionDigits()}
     * @see {@link FastMoney#ofMinor(CurrencyUnit, long, int)}
     * @throws NullPointerException when the currency is null
     * @throws IllegalArgumentException when the factionDigits is negative
     * @since 1.0.1
     */
    public static FastMoney ofMinor(CurrencyUnit currency, long amountMinor, int factionDigits) {
    	if(factionDigits < 0) {
    		throw new IllegalArgumentException("The factionDigits cannot be negative");
    	}
    	return of(BigDecimal.valueOf(amountMinor, factionDigits), currency);
    }

    @Override
    public int compareTo(MonetaryAmount o) {
        Objects.requireNonNull(o);
        int compare = getCurrency().getCurrencyCode().compareTo(o.getCurrency().getCurrencyCode());
        if (compare == 0) {
            compare = getNumber().numberValue(BigDecimal.class).compareTo(o.getNumber().numberValue(BigDecimal.class));
        }
        return compare;
    }

    @Override
    public int hashCode() {
        return Objects.hash(currency, number);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (obj instanceof FastMoney) {
            FastMoney other = (FastMoney) obj;
            return Objects.equals(currency, other.currency) && Objects.equals(number, other.number);
        }
        return false;
    }


    @Override
    public FastMoney abs() {
        if (this.isPositiveOrZero()) {
            return this;
        }
        return this.negate();
    }

    @Override
    public FastMoney add(MonetaryAmount amount) {
        checkAmountParameter(amount);
        if (amount.isZero()) {
            return this;
        }
        return new FastMoney(Math.addExact(this.number, getInternalNumber(amount.getNumber(), false)), getCurrency());
    }

    private void checkAmountParameter(MonetaryAmount amount) {
        MoneyUtils.checkAmountParameter(amount, this.currency);
        // numeric check for overflow...
        if (amount.getNumber().getScale() > SCALE) {
            throw new ArithmeticException("Parameter exceeds maximal scale: " + SCALE);
        }
        if (amount.getNumber().getPrecision() > MAX_BD.precision()) {
            throw new ArithmeticException("Parameter exceeds maximal precision: " + SCALE);
        }
    }


    @Override
    public FastMoney divide(Number divisor) {
        if (NumberVerifier.isInfinityAndNotNaN(divisor)) {
            return new FastMoney(0L, getCurrency());
        }
        checkNumber(divisor);
        if (isOne(divisor)) {
            return this;
        }
        return new FastMoney(Math.round(this.number / divisor.doubleValue()), getCurrency());
    }

    @Override
    public FastMoney[] divideAndRemainder(Number divisor) {
        if (NumberVerifier.isInfinityAndNotNaN(divisor)) {
            FastMoney zero = new FastMoney(0L, getCurrency());
            return new FastMoney[]{zero, zero};
        }
        checkNumber(divisor);
        BigDecimal div = MoneyUtils.getBigDecimal(divisor);
        BigDecimal[] res = getBigDecimal().divideAndRemainder(div);
        return new FastMoney[]{new FastMoney(res[0], getCurrency(), true), new FastMoney(res[1], getCurrency(), true)};
    }

    @Override
    public FastMoney divideToIntegralValue(Number divisor) {
        if (NumberVerifier.isInfinityAndNotNaN(divisor)) {
            return new FastMoney(0L, getCurrency());
        }
        checkNumber(divisor);
        if (isOne(divisor)) {
            return this;
        }
        BigDecimal div = MoneyUtils.getBigDecimal(divisor);
        return new FastMoney(getBigDecimal().divideToIntegralValue(div), getCurrency(), false);
    }

    @Override
    public FastMoney multiply(Number multiplicand) {
    	NumberVerifier.checkNoInfinityOrNaN(multiplicand);
        checkNumber(multiplicand);
        if (isOne(multiplicand)) {
            return this;
        }
        return new FastMoney(Math.multiplyExact(this.number, getInternalNumber(multiplicand, false)) / 100000L,
                getCurrency());
    }

    @Override
    public FastMoney negate() {
        return new FastMoney(Math.multiplyExact(this.number, -1), getCurrency());
    }

    @Override
    public FastMoney plus() {
       return this;
    }

    @Override
    public FastMoney subtract(MonetaryAmount subtrahend) {
        checkAmountParameter(subtrahend);
        if (subtrahend.isZero()) {
            return this;
        }
        return new FastMoney(Math.subtractExact(this.number, getInternalNumber(subtrahend.getNumber(), false)),
                getCurrency());
    }

    @Override
    public FastMoney remainder(Number divisor) {
        checkNumber(divisor);
        return new FastMoney(this.number % getInternalNumber(divisor, false), getCurrency());
    }

    private boolean isOne(Number number) {
        BigDecimal bd = MoneyUtils.getBigDecimal(number);
        try {
            return bd.scale() == 0 && bd.longValueExact() == 1L;
        } catch (Exception e) {
            // The only way to end up here is that longValueExact throws an ArithmeticException,
            // so the amount is definitively not equal to 1.
            return false;
        }
    }

    @Override
    public FastMoney scaleByPowerOfTen(int power) {
        return new FastMoney(getNumber().numberValue(BigDecimal.class).scaleByPowerOfTen(power), getCurrency(), true);
    }

    @Override
    public boolean isZero() {
        return this.number == 0L;
    }

    @Override
    public boolean isPositive() {
        return this.number > 0L;
    }

    @Override
    public boolean isPositiveOrZero() {
        return this.number >= 0L;
    }

    @Override
    public boolean isNegative() {
        return this.number < 0L;
    }

    @Override
    public boolean isNegativeOrZero() {
        return this.number <= 0L;
    }

    public int getScale() {
        return FastMoney.SCALE;
    }

    public int getPrecision() {
        return getNumber().numberValue(BigDecimal.class).precision();
    }

    @Override
    public int signum() {
        if (this.number < 0) {
            return -1;
        }
        if (this.number == 0) {
            return 0;
        }
        return 1;
    }

    @Override
    public boolean isLessThan(MonetaryAmount amount) {
        checkAmountParameter(amount);
        return getBigDecimal().compareTo(amount.getNumber().numberValue(BigDecimal.class)) < 0;
    }

    public boolean isLessThan(Number number) {
        checkNumber(number);
        return getBigDecimal().compareTo(MoneyUtils.getBigDecimal(number)) < 0;
    }

    @Override
    public boolean isLessThanOrEqualTo(MonetaryAmount amount) {
        checkAmountParameter(amount);
        return getBigDecimal().compareTo(amount.getNumber().numberValue(BigDecimal.class)) <= 0;
    }

    public boolean isLessThanOrEqualTo(Number number) {
        checkNumber(number);
        return getBigDecimal().compareTo(MoneyUtils.getBigDecimal(number)) <= 0;
    }


    @Override
    public boolean isGreaterThan(MonetaryAmount amount) {
        checkAmountParameter(amount);
        return getBigDecimal().compareTo(amount.getNumber().numberValue(BigDecimal.class)) > 0;
    }

    public boolean isGreaterThan(Number number) {
        checkNumber(number);
        return getBigDecimal().compareTo(MoneyUtils.getBigDecimal(number)) > 0;
    }

    @Override
    public boolean isGreaterThanOrEqualTo(MonetaryAmount amount) {
        checkAmountParameter(amount);
        return getBigDecimal().compareTo(amount.getNumber().numberValue(BigDecimal.class)) >= 0;
    }

    public boolean isGreaterThanOrEqualTo(Number number) {
        checkNumber(number);
        return getBigDecimal().compareTo(MoneyUtils.getBigDecimal(number)) >= 0;
    }

    @Override
    public boolean isEqualTo(MonetaryAmount amount) {
        checkAmountParameter(amount);
        return getBigDecimal().compareTo(amount.getNumber().numberValue(BigDecimal.class)) == 0;
    }

    public boolean hasSameNumberAs(Number number) {
        checkNumber(number);
        try {
            return this.number == getInternalNumber(number, false);
        } catch (ArithmeticException e) {
            return false;
        }
    }


    /**
     * Gets the number representation of the numeric value of this item.
     *
     * @return The {@link Number} representation matching best.
     */
    @Override
    public NumberValue getNumber() {
        return new DefaultNumberValue(getBigDecimal());
    }

    @Override
    public String toString() {
        return currency.toString() + ' ' + getBigDecimal();
    }

    // Internal helper methods

    /**
     * Internal method to check for correct number parameter.
     *
     * @param number the number to be checked, including null..
     * @throws NullPointerException          If the number is null
     * @throws java.lang.ArithmeticException If the number exceeds the capabilities of this class.
     */
    protected void checkNumber(Number number) {
        Objects.requireNonNull(number, "Number is required.");
        // numeric check for overflow...
        if (number.longValue() > MAX_BD.longValue()) {
            throw new ArithmeticException("Value exceeds maximal value: " + MAX_BD);
        }
        BigDecimal bd = MoneyUtils.getBigDecimal(number);
        if (bd.precision() > MAX_BD.precision()) {
            throw new ArithmeticException("Precision exceeds maximal precision: " + MAX_BD.precision());
        }
        if (bd.scale() > SCALE) {
            if (Boolean.parseBoolean(MonetaryConfig.getConfig()
                    .getOrDefault("org.javamoney.moneta.FastMoney.enforceScaleCompatibility",
                            "false"))) {
                throw new ArithmeticException("Scale of " + bd + " exceeds maximal scale: " + SCALE);
            } else {
                if (LOG.isLoggable(Level.FINEST)) {
                    LOG.finest("Scale exceeds maximal scale of FastMoney (" + SCALE +
                            "), implicit rounding will be applied to " + number);
                }
            }
        }
    }

    @Override
    public FastMoney with(MonetaryOperator operator) {
        Objects.requireNonNull(operator);
        try {
            return FastMoney.class.cast(operator.apply(this));
        } catch (ArithmeticException e) {
            throw e;
        } catch (Exception e) {
            throw new MonetaryException("Operator failed: " + operator, e);
        }
    }

    @Override
    public <R> R query(MonetaryQuery<R> query) {
        Objects.requireNonNull(query);
        try {
            return query.queryFrom(this);
        } catch (MonetaryException | ArithmeticException e) {
            throw e;
        } catch (Exception e) {
            throw new MonetaryException("Query failed: " + query, e);
        }
    }

    public static FastMoney from(MonetaryAmount amount) {
        if (FastMoney.class.isInstance(amount)) {
            return FastMoney.class.cast(amount);
        }
        return new FastMoney(amount.getNumber(), amount.getCurrency(), false);
    }

    /**
     * Obtains an instance of FastMoney from a text string such as 'EUR 25.25'.
     *
     * @param text the text to parse not null
     * @return FastMoney instance
     * @throws NullPointerException
     * @throws NumberFormatException
     * @throws UnknownCurrencyException
     */
    public static FastMoney parse(CharSequence text) {
        return parse(text, DEFAULT_FORMATTER);
    }

    /**
     * Obtains an instance of FastMoney from a text using specific formatter.
     *
     * @param text      the text to parse not null
     * @param formatter the formatter to use not null
     * @return FastMoney instance
     */
    public static FastMoney parse(CharSequence text, MonetaryAmountFormat formatter) {
        return from(formatter.parse(text));
    }

    private static final ToStringMonetaryAmountFormat DEFAULT_FORMATTER = ToStringMonetaryAmountFormat
            .of(ToStringMonetaryAmountFormatStyle.FAST_MONEY);

    private BigDecimal getBigDecimal() {
        return BigDecimal.valueOf(this.number).movePointLeft(SCALE);
    }

    @Override
    public FastMoney multiply(double multiplicand) {
    	NumberVerifier.checkNoInfinityOrNaN(multiplicand);
        if (multiplicand == 1.0) {
            return this;
        }
        if (multiplicand == 0.0) {
            return new FastMoney(0, this.currency);
        }
        return new FastMoney(Math.round(this.number * multiplicand), this.currency);
    }

    @Override
    public FastMoney divide(long divisor) {
        if (divisor == 1L) {
            return this;
        }
        return new FastMoney(this.number / divisor, this.currency);
    }

    @Override
    public FastMoney divide(double divisor) {
        if (NumberVerifier.isInfinityAndNotNaN(divisor)) {
            return new FastMoney(0L, getCurrency());
        }
        if (divisor == 1.0d) {
            return this;
        }
        return new FastMoney(Math.round(this.number / divisor), getCurrency());
    }

    @Override
    public FastMoney remainder(long divisor) {
        return remainder(BigDecimal.valueOf(divisor));
    }

    @Override
    public FastMoney remainder(double divisor) {
        if (NumberVerifier.isInfinityAndNotNaN(divisor)) {
            return new FastMoney(0L, getCurrency());
        }
        return remainder(new BigDecimal(String.valueOf(divisor)));
    }

    @Override
    public FastMoney[] divideAndRemainder(long divisor) {
        return divideAndRemainder(BigDecimal.valueOf(divisor));
    }

    @Override
    public FastMoney[] divideAndRemainder(double divisor) {
        if (NumberVerifier.isInfinityAndNotNaN(divisor)) {
            FastMoney zero = new FastMoney(0L, getCurrency());
            return new FastMoney[]{zero, zero};
        } else if (divisor == Double.NaN) {
            throw new ArithmeticException("Not a number: NaN.");
        }
        return divideAndRemainder(new BigDecimal(String.valueOf(divisor)));
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
            return new FastMoney(0L, this.currency);
        }
        return new FastMoney(Math.multiplyExact(multiplicand, this.number), this.currency);
    }

    @Override
    public FastMoney divideToIntegralValue(long divisor) {
        if (divisor == 1) {
            return this;
        }
        return divideToIntegralValue(MoneyUtils.getBigDecimal(divisor));
    }

    @Override
    public FastMoney divideToIntegralValue(double divisor) {
        if (NumberVerifier.isInfinityAndNotNaN(divisor)) {
            return new FastMoney(0L, getCurrency());
        }
        if (divisor == 1.0) {
            return this;
        }
        return divideToIntegralValue(MoneyUtils.getBigDecimal(divisor));
    }

    @Override
    public MonetaryAmountFactory<FastMoney> getFactory() {
        return new FastMoneyAmountBuilder().setAmount(this);
    }

}
