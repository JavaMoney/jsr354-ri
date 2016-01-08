/**
 * Copyright (c) 2012, 2015, Anatole Tresch, Werner Keil and others by the @author tag.
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
package org.javamoney.moneta.function;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.Objects;

import javax.money.CurrencyUnit;
import javax.money.MonetaryAmount;
import javax.money.MonetaryOperator;

import org.javamoney.moneta.spi.DefaultNumberValue;

/**
 * This singleton class provides access to the predefined monetary functions.
 * <p>
 * The class is thread-safe, which is also true for all functions returned by
 * this class.
 * <pre>
 * {@code
 * 	MonetaryAmount money = Money.parse("EUR 2.35");
 *  MonetaryAmount result = operator.apply(money);
 * }
 * </pre>
 * <p>Or using: </p>
 * <pre>
 * {@code
 * 	MonetaryAmount money = Money.parse("EUR 2.35");
 *  MonetaryAmount result = money.with(operator);
 * }
 * </pre>
 * @see {@link MonetaryAmount#with(MonetaryOperator)}
 * @see {@link MonetaryOperator}
 * @see {@link MonetaryOperator#apply(MonetaryAmount)}
 * @author Anatole Tresch
 * @author Otavio Santana
 * @since 1.0.1
 */
public final class MonetaryOperators {

    private static final MathContext DEFAULT_MATH_CONTEXT = MathContext.DECIMAL64;

    private static final ReciprocalOperator RECIPROCAL = new ReciprocalOperator();

    private static final ExtractorMinorPartOperator EXTRACTOR_MINOR_PART = new ExtractorMinorPartOperator();

    private static final ExtractorMajorPartOperator EXTRACTOR_MAJOR_PART = new ExtractorMajorPartOperator();

    private static final RoudingMonetaryAmountOperator ROUNDING_MONETARY_AMOUNT = new RoudingMonetaryAmountOperator();

    private MonetaryOperators() {
    }

    /**
	 * Gets the reciprocal of {@link MonetaryAmount}
	 * <p>
	 * Gets the amount as reciprocal, multiplicative inverse value (1/n).
	 * <p>
	 *<pre>
	 *{@code
	 *MonetaryAmount money = Money.parse("EUR 2.0");
	 *MonetaryAmount result = ConversionOperators.reciprocal().apply(money);//EUR 0.5
	 *}
	 *</pre>
	 * @return the reciprocal part as {@link MonetaryOperator}
	 */
    public static MonetaryOperator reciprocal() {
        return RECIPROCAL;
    }

    /**
	 * Gets the permil of the amount.
	 * <p>
	 * This returns the monetary amount in permil. For example, for 10% 'EUR
	 * 2.35' will return 0.235.
	 * <p>
	 *<pre>
	 *{@code
	 *MonetaryAmount money = Money.parse("EUR EUR 2.35");
	 *MonetaryAmount result = ConversionOperators.permil(BigDecimal.TEN).apply(money);//EUR 0.0235
	 *}
	 *</pre>
	 * @return the permil as {@link MonetaryOperator}
	 */
    public static MonetaryOperator permil(BigDecimal decimal) {
        return new PermilOperator(decimal);
    }

    /**
     * Returns the {@link #percent(BigDecimal)} converting
     * this number to {@link BigDecimal} and using the {@link org.javamoney.moneta.convert.ConversionOperators#DEFAULT_MATH_CONTEXT}
     * @param number to be converted to {@link BigDecimal}
     * @see {@link #permil(BigDecimal)}
     * @return the permil {@link MonetaryOperator}
     */
    public static MonetaryOperator permil(Number number) {
        return permil(number, DEFAULT_MATH_CONTEXT);
    }


    /**
     * Returns the {@link #percent(BigDecimal)} converting
     * this number to {@link BigDecimal} and using the {@link MathContext} in parameters
     * @param number to be converted to {@link BigDecimal}
     * @param mathContext the mathContext to be used
     * @see {@link #permil(BigDecimal)}
     * @return the permil {@link MonetaryOperator}
     */
    public static MonetaryOperator permil(Number number, MathContext mathContext) {
        return new PermilOperator(new DefaultNumberValue(number).numberValue(BigDecimal.class));
    }

	/**
	 * Gets the percentage of the amount.
	 * <p>
	 * This returns the monetary amount in percent. For example, for 10% 'EUR
	 * 2.35' will return 0.235.
	 * <p>
	 *<pre>
	 *{@code
	 *MonetaryAmount money = Money.parse("EUR 200.0");
	 *MonetaryAmount result = ConversionOperators.percent(BigDecimal.TEN).apply(money);//EUR 20.0
	 *}
	 *</pre>
	 * @param decimal the value to percent
	 * @return the percent of {@link MonetaryOperator}
	 */
    public static MonetaryOperator percent(BigDecimal decimal) {
        return new PercentOperator(decimal);
    }

    /**
     * Gets the percentage of the amount.
     * @param number to be used in percent
     * @see {@link #percent(BigDecimal)}
     * @return the percent of {@link MonetaryOperator}
     */
    public static MonetaryOperator percent(Number number) {
        return percent(new DefaultNumberValue(number).numberValue(BigDecimal.class));
    }

	/**
	 * Extract minor part of {@link MonetaryAmount}
	 * <p>
	 * This returns the monetary amount in terms of the minor units of the
	 * currency, truncating the whole part if necessary. For example, 'EUR 2.35'
	 * will return 'EUR 0.35', and 'BHD -1.345' will return 'BHD -0.345'.
	 * <p>
	 *<pre>
	 *{@code
	 *MonetaryAmount money = Money.parse("EUR 2.35");
	 *MonetaryAmount result = ConversionOperators.minorPart().apply(money);//EUR 0.35
	 *}
	 *</pre>
	 * @return the minor part as {@link MonetaryOperator}
	 */
    public static MonetaryOperator minorPart() {
        return EXTRACTOR_MINOR_PART;
    }

	/**
	 * Extract major part of {@link MonetaryAmount}
	 * <p>
	 * This returns the monetary amount in terms of the minor units of the
	 * currency, truncating the whole part if necessary. For example, 'EUR 2.35'
	 * will return 'EUR 0.35', and 'BHD -1.345' will return 'BHD -0.345'.
	 * <p>
	 *<pre>
	 *{@code
	 *MonetaryAmount money = Money.parse("EUR 2.35");
	 *MonetaryAmount result = ConversionOperators.majorPart().apply(money);//EUR 2.0
	 *}
	 *</pre>
	 * @return the major part as {@link MonetaryOperator}
	 */
	public static MonetaryOperator majorPart() {
		return EXTRACTOR_MAJOR_PART;
	}

	/**
	 * Rounding the {@link MonetaryAmount} using {@link CurrencyUnit#getDefaultFractionDigits()}
	 * and {@link RoundingMode#HALF_EVEN}.
	 * <p>
	 * For example, 'EUR 2.3523' will return 'EUR 2.35',
	 * and 'BHD -1.34534432' will return 'BHD -1.345'.
	 * <p>
	 *<pre>
	 *{@code
	 *MonetaryAmount money = Money.parse("EUR 2.355432");
	 *MonetaryAmount result = ConversionOperators.rounding().apply(money);//EUR 2.36
	 *}
	 *</pre>
	 * @return the major part as {@link MonetaryOperator}
	 */
	public static MonetaryOperator rounding() {
		return ROUNDING_MONETARY_AMOUNT;
	}

	/**
	 * Rounding the {@link MonetaryAmount} using {@link CurrencyUnit#getDefaultFractionDigits()}
	 * and {@link RoundingMode}.
	 * <p>
	 * For example, 'EUR 2.3523' will return 'EUR 2.35',
	 * and 'BHD -1.34534432' will return 'BHD -1.345'.
	 * <p>
	 *<pre>
	 *{@code
	 *MonetaryAmount money = Money.parse("EUR 2.355432");
	 *MonetaryAmount result = ConversionOperators.rounding(RoundingMode.HALF_EVEN).apply(money);//EUR 2.35
	 *}
	 *</pre>
	 * @param roundingMode rounding to be used
	 * @return the major part as {@link MonetaryOperator}
	 */
	public static MonetaryOperator rounding(RoundingMode roundingMode) {
		return new RoudingMonetaryAmountOperator(Objects.requireNonNull(roundingMode));
	}

	/**
	 * Rounding the {@link MonetaryAmount} using {@link CurrencyUnit#getDefaultFractionDigits()}
	 * and {@link RoundingMode}.
	 * <p>
	 * For example, 'EUR 2.3523' will return 'EUR 2.35',
	 * and 'BHD -1.34534432' will return 'BHD -1.345'.
	 * <p>
	 *<pre>
	 *{@code
	 *MonetaryAmount money = Money.parse("EUR 2.355432");
	 *MonetaryAmount result = ConversionOperators.rounding(RoundingMode.HALF_EVEN, 3).apply(money);//EUR 2.352
	 *}
	 *</pre>
	 * @param roundingMode rounding to be used
	 * @param scale to be used
	 * @return the major part as {@link MonetaryOperator}
	 */
	public static MonetaryOperator rounding(RoundingMode roundingMode, int scale) {
		return new RoudingMonetaryAmountOperator(Objects.requireNonNull(roundingMode), scale);
	}

	/**
	 * Rounding the {@link MonetaryAmount} using the scale informed
	 * and {@link RoundingMode#HALF_EVEN}.
	 * <p>
	 * For example, 'EUR 2.3523' will return 'EUR 2.35',
	 * and 'BHD -1.34534432' will return 'BHD -1.345'.
	 * <p>
	 *<pre>
	 *{@code
	 *MonetaryAmount money = Money.parse("EUR 2.355432");
	 *MonetaryAmount result = ConversionOperators.rounding(2).apply(money);//EUR 2.35
	 *}
	 *</pre>
	 * @param scale scale to be used
	 * @return the major part as {@link MonetaryOperator}
	 */
	public static MonetaryOperator rounding(int scale) {
		return new RoudingMonetaryAmountOperator(RoudingMonetaryAmountOperator.DEFAULT_ROUDING_MONETARY_AMOUNT, scale);
	}

	/**
	 * Do exchange of currency, in other words, create the monetary amount with the
	 * same value but with currency different.
	 * <p>
	 * For example, 'EUR 2.35', using the currency 'USD' as exchange parameter, will return 'USD 2.35',
	 * and 'BHD -1.345', using the currency 'USD' as exchange parameter, will return 'BHD -1.345'.
	 * <p>
	 *<pre>
	 *{@code
	 *Currency real = Monetary.getCurrency("BRL");
	 *MonetaryAmount money = Money.parse("EUR 2.355");
	 *MonetaryAmount result = MonetaryOperators.exchangeCurrency(real).apply(money);//BRL 2.355
	 *}
	 *</pre>
	 * @param currencyUnit currency to be used
	 * @return the major part as {@link MonetaryOperator}
	 * @deprecated
	 */
	@Deprecated
	public static MonetaryOperator exchangeCurrency(CurrencyUnit currencyUnit){
		return new ExchangeCurrencyOperator(Objects.requireNonNull(currencyUnit));
	}
}
