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
package org.javamoney.moneta.function;

import java.math.BigDecimal;
import java.math.MathContext;

import javax.money.MonetaryAmount;
import javax.money.MonetaryAdjuster;
import javax.money.MonetaryAdjuster;
import javax.money.MonetaryQuery;

/**
 * This singleton class provides access to the predefined monetary functions.
 * <p>
 * The class is thread-safe, which is also true for all functions returned by
 * this class.
 * 
 * @author Anatole Tresch
 */
public final class MonetaryFunctions {
	/** defaulkt Math context used. */
	private static final MathContext DEFAULT_MATH_CONTEXT = initDefaultMathContext();
	/** Shared reciprocal instance. */
	private static final Reciprocal RECIPROCAL = new Reciprocal();

	/**
	 * The shared instance of this class.
	 */
	private static final MinorPart MINORPART = new MinorPart();
	/** SHared minor units class. */
	private static final MinorUnits MINORUNITS = new MinorUnits();
	/** Shared major part instance. */
	private static final MajorPart MAJORPART = new MajorPart();
	/** Shared major units instance. */
	private static final MajorUnits MAJORUNITS = new MajorUnits();


	/**
	 * Private singleton constructor.
	 */
	private MonetaryFunctions() {
		// Singleton constructor
	}

	/**
	 * Get {@link MathContext} for {@link Permil} instances.
	 * 
	 * @return the {@link MathContext} to be used, by default
	 *         {@link MathContext#DECIMAL64}.
	 */
	private static MathContext initDefaultMathContext() {
		// TODO Initialize default, e.g. by system properties, or better:
		// classpath properties!
		return MathContext.DECIMAL64;
	}

	/**
	 * Return a {@link MonetaryAdjuster} realizing the recorpocal value of
	 * {@code f(R) = 1/R}.
	 * 
	 * @return the reciprocal operator, never {@code null}
	 */
	public static MonetaryAdjuster reciprocal() {
		return RECIPROCAL;
	}

	/**
	 * Factory method creating a new instance with the given {@code BigDecimal) permil value;
	 * @param decimal the decimal value of the permil operator being created.
	 * @return a new  {@code Permil} operator
	 */
	public static MonetaryAdjuster permil(BigDecimal decimal) {
		return new Permil(decimal);
	}

	/**
	 * Factory method creating a new instance with the given {@code Number) permil value;
	 * @param decimal the decimal value of the permil operator being created.
	 * @return a new  {@code Permil} operator
	 */
	public static MonetaryAdjuster permil(Number number) {
		return permil(number, DEFAULT_MATH_CONTEXT);
	}

	/**
	 * Factory method creating a new instance with the given {@code Number) permil value;
	 * @param decimal the decimal value of the permil operator being created.
	 * @return a new  {@code Permil} operator
	 */
	public static MonetaryAdjuster permil(Number number, MathContext mathContext) {
		return new Permil(getBigDecimal(number, mathContext));
	}

	/**
	 * Converts to {@link BigDecimal}, if necessary, or casts, if possible.
	 * 
	 * @param number
	 *            The {@link Number}
	 * @param mathContext
	 *            the {@link MathContext}
	 * @return the {@code number} as {@link BigDecimal}
	 */
	private static final BigDecimal getBigDecimal(Number number,
			MathContext mathContext) {
		if (number instanceof BigDecimal) {
			return (BigDecimal) number;
		} else {
			return new BigDecimal(number.doubleValue(), mathContext);
		}
	}

	/**
	 * Factory method creating a new instance with the given {@code BigDecimal) percent value;
	 * @param decimal the decimal value of the percent operator being created.
	 * @return a new  {@code Percent} operator
	 */
	public static MonetaryAdjuster percent(BigDecimal decimal) {
		return new Percent(decimal); // TODO caching, e.g. array for 1-100 might
										// work.
	}

	/**
	 * Factory method creating a new instance with the given {@code Number) percent value;
	 * @param decimal the decimal value of the percent operator being created.
	 * 
	 * @return a new  {@code Percent} operator
	 */
	public static MonetaryAdjuster percent(Number number) {
		return percent(getBigDecimal(number, DEFAULT_MATH_CONTEXT));
	}

	/**
	 * Access the shared instance of {@link MinorPart} for use.
	 * 
	 * @return the shared instance, never {@code null}.
	 */
	public static MonetaryAdjuster minorPart() {
		return MINORPART;
	}

	/**
	 * Access the shared instance of {@link MajorPart} for use.
	 * 
	 * @return the shared instance, never {@code null}.
	 */
	public static MonetaryAdjuster majorPart() {
		return MAJORPART;
	}

	/**
	 * Access the shared instance of {@link MinorPart} for use.
	 * 
	 * @return the shared instance, never {@code null}.
	 */
	public static MonetaryQuery<Long> minorUnits() {
		return MINORUNITS;
	}

	/**
	 * Access the shared instance of {@link MinorPart} for use.
	 * 
	 * @return the shared instance, never {@code null}.
	 */
	public static MonetaryQuery<Long> majorUnits() {
		return MAJORUNITS;
	}


}
