/**
 * Copyright (c) 2012, 2014, Credit Suisse (Anatole Tresch), Werner Keil and others by the @author tag.
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
import java.util.concurrent.atomic.AtomicLong;

import javax.money.MonetaryAmount;
import javax.money.MonetaryOperator;

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
 * @author Anatole Tresch
 * @author Otavio Santana
 */
public final class MonetaryOperators {

    private static final MathContext DEFAULT_MATH_CONTEXT = initDefaultMathContext();

    private static final ReciprocalOperator RECIPROCAL = new ReciprocalOperator();

    private static final ExtractorMinorPartOperator EXTRACTOR_MINOR_PART = new ExtractorMinorPartOperator();

    private static final ExtractorMajorPartOperator EXTRACTOR_MAJOR_PART = new ExtractorMajorPartOperator();


    private MonetaryOperators() {
    }

    /**
     * Get {@link MathContext} for {@link PermilOperator} instances.
     *
     * @return the {@link MathContext} to be used, by default
     * {@link MathContext#DECIMAL64}.
     */
    private static MathContext initDefaultMathContext() {
        return MathContext.DECIMAL64;
    }

    /**
	 * Gets the reciprocal of {@link MonetaryAmount}
	 * <p>
	 * Gets the amount as reciprocal, multiplicative inverse value (1/n).
	 * <p>
	 *<pre>
	 *{@code
	 *MonetaryAmount money = Money.parse("EUR 2.0");
	 *MonetaryAmount result = MonetaryOperators.reciprocal().apply(money);//EUR 0.5
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
	 *MonetaryAmount result = MonetaryOperators.permil(BigDecimal.TEN).apply(money);//EUR 0.0235
	 *}
	 *</pre>
	 * @return the permil as {@link MonetaryOperator}
	 */
    public static MonetaryOperator permil(BigDecimal decimal) {
        return new PermilOperator(decimal);
    }

    /**
     * Returns the {@link MonetaryOperators#percent(BigDecimal)} converting
     * this number to {@link BigDecimal} and using the {@link MonetaryOperators#DEFAULT_MATH_CONTEXT}
     * @param number to be converted to {@link BigDecimal}
     * @see {@link MonetaryOperators#permil(BigDecimal)}
     * @return the permil {@link MonetaryOperator}
     */
    public static MonetaryOperator permil(Number number) {
        return permil(number, DEFAULT_MATH_CONTEXT);
    }


    /**
     * Returns the {@link MonetaryOperators#percent(BigDecimal)} converting
     * this number to {@link BigDecimal} and using the {@link MathContext} in parameters
     * @param number to be converted to {@link BigDecimal}
     * @param mathContext the mathContext to be used
     * @see {@link MonetaryOperators#permil(BigDecimal)}
     * @return the permil {@link MonetaryOperator}
     */
    public static MonetaryOperator permil(Number number, MathContext mathContext) {
        return new PermilOperator(getBigDecimal(number, mathContext));
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
	 *MonetaryAmount result = MonetaryOperators.percent(BigDecimal.TEN).apply(money);//EUR 20.0
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
     * @see {@link MonetaryOperators#percent(BigDecimal)}
     * @return the percent of {@link MonetaryOperator}
     */
    public static MonetaryOperator percent(Number number) {
        return percent(getBigDecimal(number, DEFAULT_MATH_CONTEXT));
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
	 *MonetaryAmount result = MonetaryOperators.minorPart().apply(money);//EUR 0.35
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
	 *MonetaryAmount result = MonetaryOperators.majorPart().apply(money);//EUR 2.0
	 *}
	 *</pre>
	 * @return the major part as {@link MonetaryOperator}
	 */
	public static MonetaryOperator majorPart() {
		return EXTRACTOR_MAJOR_PART;
	}

    /**
     * Converts to {@link BigDecimal}, if necessary, or casts, if possible.
     *
     * @param num         The {@link Number}
     * @param mathContext the {@link MathContext}
     * @return the {@code number} as {@link BigDecimal}
     */
    private static BigDecimal getBigDecimal(Number num,
                                            MathContext mathContext) {
        if (num instanceof BigDecimal) {
            return (BigDecimal) num;
        }
        if (num instanceof Long || num instanceof Integer
                || num instanceof Byte || num instanceof AtomicLong) {
            return BigDecimal.valueOf(num.longValue());
        }
        if (num instanceof Float || num instanceof Double) {
            return new BigDecimal(num.toString());
        }
        try {
            // Avoid imprecise conversion to double value if at all possible
            return new BigDecimal(num.toString(), mathContext);
        } catch (NumberFormatException ignored) {
        }
        return BigDecimal.valueOf(num.doubleValue());
    }
}
