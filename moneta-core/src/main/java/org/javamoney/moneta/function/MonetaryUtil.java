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
package org.javamoney.moneta.function;


import java.math.BigDecimal;
import java.math.MathContext;
import java.util.concurrent.atomic.AtomicLong;

import javax.money.MonetaryOperator;
import javax.money.MonetaryQuery;

/**
 * This singleton class provides access to the predefined monetary functions.
 * <p>
 * The class is thread-safe, which is also true for all functions returned by
 * this class.
 *
 * @author Anatole Tresch
 * @deprecated use {@link ConversionOperators} or {@link MonetaryQuery} instead.
 */
@Deprecated
public final class MonetaryUtil {
    /**
     * defaulkt Math context used.
     */
    private static final MathContext DEFAULT_MATH_CONTEXT = initDefaultMathContext();
    /**
     * Shared reciprocal instance.
     */
    private static final ReciprocalOperator RECIPROCAL = new ReciprocalOperator();

    /**
     * The shared instance of this class.
     */
    private static final ExtractorMinorPartOperator MINORPART = new ExtractorMinorPartOperator();
    /**
     * SHared minor units class.
     */
    private static final ExtractorMinorPartQuery MINORUNITS = new ExtractorMinorPartQuery();
    /**
     * Shared major part instance.
     */
    private static final ExtractorMajorPartOperator MAJORPART = new ExtractorMajorPartOperator();
    /**
     * Shared major units instance.
     */
    private static final ExtractorMajorPartQuery MAJORUNITS = new ExtractorMajorPartQuery();

    /**
     * Private singleton constructor.
     */
    private MonetaryUtil() {
        // Singleton constructor
    }

    /**
     * Get {@link MathContext} for {@link Permil} instances.
     *
     * @return the {@link MathContext} to be used, by default
     * {@link MathContext#DECIMAL64}.
     */
    private static MathContext initDefaultMathContext() {
        // TODO Initialize default, e.g. by system properties, or better:
        // classpath properties!
        return MathContext.DECIMAL64;
    }

    /**
     * Return a {@link MonetaryOperator} realizing the recorpocal value of
     * {@code f(R) = 1/R}.
     *
     * @return the reciprocal operator, never {@code null}
     */
    public static MonetaryOperator reciprocal() {
        return RECIPROCAL;
    }

    /**
     * Factory method creating a new instance with the given {@code BigDecimal} permil value.
     *
     * @param decimal the decimal value of the permil operator being created.
     * @return a new  {@code Permil} operator
     */
    public static MonetaryOperator permil(BigDecimal decimal) {
        return new PermilOperator(decimal);
    }

    /**
     * Factory method creating a new instance with the given {@code Number} permil value.
     *
     * @param number the number value of the permil operator being created.
     * @return a new  {@code Permil} operator
     */
    public static MonetaryOperator permil(Number number) {
        return permil(number, DEFAULT_MATH_CONTEXT);
    }

    /**
     * Factory method creating a new instance with the given {@code Number} permil value.
     *
     * @param number the number value of the permil operator being created.
     * @return a new  {@code Permil} operator
     */
    public static MonetaryOperator permil(Number number, MathContext mathContext) {
        return new PermilOperator(getBigDecimal(number, mathContext));
    }

    /**
     * Factory method creating a new instance with the given {@code BigDecimal} percent value.
     *
     * @param decimal the decimal value of the percent operator being created.
     * @return a new  {@code Percent} operator
     */
    public static MonetaryOperator percent(BigDecimal decimal) {
        return new PercentOperator(decimal); // TODO caching, e.g. array for 1-100 might
        // work.
    }

    /**
     * Factory method creating a new instance with the given {@code Number} percent value.
     *
     * @param number the number value of the percent operator being created.
     * @return a new  {@code Percent} operator
     */
    public static MonetaryOperator percent(Number number) {
        return percent(getBigDecimal(number, DEFAULT_MATH_CONTEXT));
    }

    /**
     * Access the shared instance of {@link MinorPart} for use.
     *
     * @return the shared instance, never {@code null}.
     */
    public static MonetaryOperator minorPart() {
        return MINORPART;
    }

    /**
     * Access the shared instance of {@link MajorPart} for use.
     *
     * @return the shared instance, never {@code null}.
     */
    public static MonetaryOperator majorPart() {
        return MAJORPART;
    }

    /**
     * Access the shared instance of {@link MinorUnits} for use.
     *
     * @return the shared instance, never {@code null}.
     */
    public static MonetaryQuery<Long> minorUnits() {
        return MINORUNITS;
    }

    /**
     * Access the shared instance of {@link MajorUnits} for use.
     *
     * @return the shared instance, never {@code null}.
     */
    public static MonetaryQuery<Long> majorUnits() {
        return MAJORUNITS;
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