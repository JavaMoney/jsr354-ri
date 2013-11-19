/*
 * CREDIT SUISSE IS WILLING TO LICENSE THIS SPECIFICATION TO YOU ONLY UPON THE
 * CONDITION THAT YOU ACCEPT ALL OF THE TERMS CONTAINED IN THIS AGREEMENT.
 * PLEASE READ THE TERMS AND CONDITIONS OF THIS AGREEMENT CAREFULLY. BY
 * DOWNLOADING THIS SPECIFICATION, YOU ACCEPT THE TERMS AND CONDITIONS OF THE
 * AGREEMENT. IF YOU ARE NOT WILLING TO BE BOUND BY IT, SELECT THE "DECLINE"
 * BUTTON AT THE BOTTOM OF THIS PAGE. Specification: JSR-354 Money and Currency
 * API ("Specification") Copyright (c) 2012-2013, Credit Suisse All rights
 * reserved.
 */
package javax.money.function;

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
	public static MonetaryOperator reciprocal() {
		return RECIPROCAL;
	}

/**
	 * Factory method creating a new instance with the given {@code BigDecimal) permil value;
	 * @param decimal the decimal value of the permil operator being created.
	 * @return a new  {@code Permil} operator
	 */
	public static MonetaryOperator permil(BigDecimal decimal) {
		return new Permil(decimal);
	}

/**
	 * Factory method creating a new instance with the given {@code Number) permil value;
	 * @param decimal the decimal value of the permil operator being created.
	 * @return a new  {@code Permil} operator
	 */
	public static MonetaryOperator permil(Number number) {
		return permil(number, DEFAULT_MATH_CONTEXT);
	}

/**
	 * Factory method creating a new instance with the given {@code Number) permil value;
	 * @param decimal the decimal value of the permil operator being created.
	 * @return a new  {@code Permil} operator
	 */
	public static MonetaryOperator permil(Number number, MathContext mathContext) {
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
	private static final BigDecimal getBigDecimal(Number num,
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
		} catch (NumberFormatException e) {
		}
		return BigDecimal.valueOf(num.doubleValue());
	}

/**
	 * Factory method creating a new instance with the given {@code BigDecimal) percent value;
	 * @param decimal the decimal value of the percent operator being created.
	 * @return a new  {@code Percent} operator
	 */
	public static MonetaryOperator percent(BigDecimal decimal) {
		return new Percent(decimal); // TODO caching, e.g. array for 1-100 might
										// work.
	}

/**
	 * Factory method creating a new instance with the given {@code Number) percent value;
	 * @param decimal the decimal value of the percent operator being created.
	 * 
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

}
