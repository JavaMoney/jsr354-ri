/*
 * CREDIT SUISSE IS WILLING TO LICENSE THIS SPECIFICATION TO YOU ONLY UPON THE CONDITION THAT YOU
 * ACCEPT ALL OF THE TERMS CONTAINED IN THIS AGREEMENT. PLEASE READ THE TERMS AND CONDITIONS OF THIS
 * AGREEMENT CAREFULLY. BY DOWNLOADING THIS SPECIFICATION, YOU ACCEPT THE TERMS AND CONDITIONS OF
 * THE AGREEMENT. IF YOU ARE NOT WILLING TO BE BOUND BY IT, SELECT THE "DECLINE" BUTTON AT THE
 * BOTTOM OF THIS PAGE. Specification: JSR-354 Money and Currency API ("Specification") Copyright
 * (c) 2012-2013, Credit Suisse All rights reserved.
 */
package org.javamoney.moneta.format;
//
//import java.io.Serializable;
//import java.util.Arrays;
//import java.util.HashSet;
//import java.util.Locale;
//import java.util.Objects;
//import java.util.Set;
//
//import javax.money.MonetaryException;
//import javax.money.spi.AmountFormatSymbolsProviderSpi;
//import javax.money.spi.Bootstrap;
//
///**
// * This class represents the set of symbols (such as the decimal separator, the grouping separators,
// * and so on) needed by {@link javax.money.format.AmountFormatContext} to format {@link javax.money.MonetaryAmount} instances.
// * Instances of this class can be obtained by calling {@link #of(Locale)}. If you need to
// * change any of these symbols, you can access also access a <code>Builder</code> by calling
// * {@link #toBuilder()} .
// * <p>
// * This class itself has no dependencies to <code>java.text</code> to be platform independent.
// * Nevertheless the similarities are obvious. In most cases users will never work with this class
// * here explicitly, since it is loaded with correct localized platform defaults implicitly, when
// * accessing a default {@link javax.money.format.MonetaryAmountFormat} instance.
// *
// * @see java.text.DecimalFormat
// * @see java.util.Locale
// * @author Anatole Tresch
// */
//public final class AmountFormatSymbols implements Cloneable, Serializable {
//
//	/**
//	 * serialVersionUID.
//	 */
//	private static final long serialVersionUID = 5247515200471590539L;
//
//	/**
//	 * Character used for zero.
//	 *
//	 * @see #getZeroDigit
//	 */
//	private char zeroDigit;
//
//	/**
//	 * Characters used for thousands separator.
//	 *
//	 * @see #getGroupingSeparators
//	 */
//	private char[] groupingSeparators = new char[0];
//
//	/**
//	 * Character used for decimal sign.
//	 *
//	 * @see #getDecimalSeparator
//	 */
//	private char decimalSeparator;
//
//	/**
//	 * Character used for a digit in a pattern.
//	 *
//	 * @see #getDigit
//	 */
//	private char digit;
//
//	/**
//	 * Character used to separate positive and negative subpatterns in a pattern.
//	 *
//	 * @see #getPatternSeparator
//	 */
//	private char patternSeparator;
//
//	/**
//	 * String used to represent infinity.
//	 *
//	 * @see #getInfinity
//	 */
//	private String infinity;
//
//	/**
//	 * The string used to separate the mantissa from the exponent. Examples: "x10^" for 1.23x10^4,
//	 * "E" for 1.23E4.
//	 * <p>
//	 * If both <code>exponential</code> and <code>exponentialSeparator</code> exist, this
//	 * <code>exponentialSeparator</code> has the precedence.
//	 */
//	private String exponentialSeparator;
//
//	/**
//	 * Character used to represent minus sign.
//	 *
//	 * @see #getMinusSign
//	 */
//	private char minusSign;
//
//	/**
//	 * The locale of these currency format symbols.
//	 */
//	private Locale locale;
//
//	/**
//	 * Constructor, use {@link Builder} to create instances of this class.
//	 *
//	 * @param builder
//	 */
//	private AmountFormatSymbols(Builder builder) {
//		this.locale = builder.locale;
//		this.groupingSeparators = builder.groupingSeparators;
//		this.decimalSeparator = builder.decimalSeparator;
//		this.digit = builder.digit;
//		this.minusSign = builder.minusSign;
//		this.patternSeparator = builder.patternSeparator;
//		this.zeroDigit = builder.zeroDigit;
//		this.exponentialSeparator = builder.exponentialSeparator;
//		this.infinity = builder.infinity;
//	}
//
//	/**
//	 * Get an {@link AmountFormatSymbols} given a {@link Locale}.
//	 *
//	 * @param locale
//	 *            the target {@link Locale}
//	 * @return a corresponding {@link AmountFormatSymbols} instance, never {@code null}.
//	 * @throws MonetaryException
//	 *             if no registered {@link AmountFormatSymbolsProviderSpi} can provide a matching
//	 *             instance.
//	 */
//	public static final AmountFormatSymbols of(Locale locale) {
//		Objects.requireNonNull(locale, "Locale required.");
//		for (AmountFormatSymbolsProviderSpi spi : Bootstrap
//				.getServices(
//				AmountFormatSymbolsProviderSpi.class)) {
//			AmountFormatSymbols symbols = spi.getAmountFormatSymbols(locale);
//			if (symbols != null) {
//				return symbols;
//			}
//		}
//		throw new MonetaryException(
//				"No AmountFormatSymbols for locale "
//						+ locale);
//	}
//
//	/**
//	 * Get all available locales. For each {@link Locale} returned {@link #of(Locale)} will
//	 * return an instance of {@link AmountFormatSymbols}.
//	 *
//	 * @return all available locales, never {@code null}.
//	 */
//	public static final Set<Locale> getAvailableLocales() {
//		Set<Locale> locales = new HashSet<>();
//		for (AmountFormatSymbolsProviderSpi spi : Bootstrap
//				.getServices(
//				AmountFormatSymbolsProviderSpi.class)) {
//			locales.addAll(spi.getSupportedLocales());
//		}
//		return locales;
//	}
//
//	/**
//	 * Gets the {@link Locale} of this format symbols.
//	 *
//	 * @return the {@link Locale} of this format symbols, never {@code null}.
//	 */
//	public final Locale getLocale() {
//		return locale;
//	}
//
//	/**
//	 * Gets the character used for zero. Different for Arabic, etc.
//	 *
//	 * @return the character used for zero
//	 */
//	public final Character getZeroDigit() {
//		return zeroDigit;
//	}
//
//	/**
//	 * Gets the characters used for thousands separator. Hereby each value in the array represents a
//	 * group character for a group, starting from the decimal point and going up the significant
//	 * digits. The last entry in the array is used as a default group character for all subsequent
//	 * groupings.
//	 *
//	 * @return the grouping separator characters to be used.
//	 */
//	public final char[] getGroupingSeparators() {
//		return groupingSeparators.clone();
//	}
//
//	/**
//	 * Gets the character used for decimal sign. Different for French, etc.
//	 *
//	 * @return the character used for decimal sign
//	 */
//	public final Character getDecimalSeparator() {
//		return decimalSeparator;
//	}
//
//	/**
//	 * Gets the character used for a digit in a pattern.
//	 *
//	 * @return the character used for a digit in a pattern
//	 */
//	public final Character getDigit() {
//		return digit;
//	}
//
//	/**
//	 * Gets the character used to separate positive and negative subpatterns in a pattern.
//	 *
//	 * @return the character used to separate positive and negative subpatterns
//	 */
//	public final Character getPatternSeparator() {
//		return patternSeparator;
//	}
//
//	/**
//	 * Gets the string used to represent infinity. Almost always left unchanged.
//	 *
//	 * @return the string used to represent infinity
//	 */
//	public final String getInfinity() {
//		return infinity;
//	}
//
//	/**
//	 * Gets the character used to represent minus sign. If no explicit negative format is specified,
//	 * one is formed by prefixing minusSign to the positive format.
//	 *
//	 * @return the minus sign
//	 */
//	public final Character getMinusSign() {
//		return minusSign;
//	}
//
//	/**
//	 * Returns the string used to separate the mantissa from the exponent. Examples: "x10^" for
//	 * 1.23x10^4, "E" for 1.23E4.
//	 *
//	 * @return the exponent separator string
//	 */
//	public final String getExponentSeparator() {
//		return exponentialSeparator;
//	}
//
//	/**
//	 * Get a {@link Builder} initialized with this instance.
//	 *
//	 * @return a new {@link Builder}, never {@code null}.
//	 */
//	public Builder toBuilder() {
//		return new Builder(this);
//	}
//
//	/*
//	 * (non-Javadoc)
//	 * @see java.lang.Object#toString()
//	 */
//	@Override
//	public String toString() {
//		return "MonetaryFormatSymbols [locale=" + locale
//				+ ", patternSeparator=" + patternSeparator + ", digit=" + digit
//				+ ", zeroDigit=" + zeroDigit + ", decimalSeparator="
//				+ decimalSeparator + ", groupingSeparators="
//				+ Arrays.toString(groupingSeparators) + ", minusSign="
//				+ minusSign
//				+ ", exponentialSeparator=" + exponentialSeparator + "]";
//	}
//
//	/**
//	 * Builder for creating new instances of {@link AmountFormatSymbols}.
//	 *
//	 * @author Anatole Tresch
//	 */
//	public static final class Builder {
//
//		// Constants for characters used in programmatic (unlocalized) patterns.
//		/** Default zero digit, equals to value from DecimalFormat. */
//		private static final char PATTERN_ZERO_DIGIT = '0';
//		/** Default decimal separator, equals to value from DecimalFormat. */
//		private static final char PATTERN_DECIMAL_SEPARATOR = '.';
//		/** Default digit character used in patterns, equals to value from DecimalFormat. */
//		private static final char PATTERN_DIGIT = '#';
//		/** Default pattern separator, equals to value from DecimalFormat. */
//		private static final char PATTERN_SEPARATOR = ';';
//		/** Default exponent character, equals to value from DecimalFormat. */
//		private static final String PATTERN_EXPONENT = "E";
//		/** Default minus sign, equals to value from DecimalFormat. */
//		private static final char PATTERN_MINUS = '-';
//
//		/**
//		 * Character used for zero.
//		 *
//		 * @see #getZeroDigit
//		 */
//		private char zeroDigit = PATTERN_ZERO_DIGIT;
//
//		/**
//		 * Characters used for thousands separator.
//		 *
//		 * @see #getGroupingSeparators
//		 */
//		private char[] groupingSeparators = new char[0];
//
//		/**
//		 * Character used for decimal sign.
//		 *
//		 * @see #getDecimalSeparator
//		 */
//		private char decimalSeparator = PATTERN_DECIMAL_SEPARATOR;
//
//		/**
//		 * Character used for a digit in a pattern.
//		 *
//		 * @see #getDigit
//		 */
//		private char digit = PATTERN_DIGIT;
//
//		/**
//		 * Character used to separate positive and negative subpatterns in a pattern.
//		 *
//		 * @see #getPatternSeparator
//		 */
//		private char patternSeparator = PATTERN_SEPARATOR;
//
//		/**
//		 * String used to represent infinity.
//		 *
//		 * @see #getInfinity
//		 */
//		private String infinity;
//
//		/**
//		 * The string used to separate the mantissa from the exponent. Examples: "x10^" for
//		 * 1.23x10^4, "E" for 1.23E4.
//		 * <p>
//		 * If both <code>exponential</code> and <code>exponentialSeparator</code> exist, this
//		 * <code>exponentialSeparator</code> has the precedence.
//		 */
//		private String exponentialSeparator = PATTERN_EXPONENT;
//
//		/**
//		 * Character used to represent minus sign.
//		 *
//		 * @see #getMinusSign
//		 */
//		private char minusSign = PATTERN_MINUS;
//
//		/**
//		 * The locale of these currency format symbols.
//		 */
//		private Locale locale;
//
//		/**
//		 * Creates a new Builder. The builder will be initialized with non localized defaults:
//		 * <blockquote>
//		 *
//		 * <pre>
//		 * private static final char PATTERN_ZERO_DIGIT = '0';
//		 * private static final char PATTERN_DECIMAL_SEPARATOR = '.';
//		 * private static final char PATTERN_DIGIT = '#';
//		 * private static final char PATTERN_SEPARATOR = ';';
//		 * private static final String PATTERN_EXPONENT = &quot;E&quot;;
//		 * private static final char PATTERN_MINUS = '-';
//		 * </pre>
//		 *
//		 * </blockquote>
//		 *
//		 * @param locale
//		 *            the {@link Locale}, not {@code null}.
//		 */
//		public Builder(Locale locale) {
//			Objects.requireNonNull(locale);
//			this.locale = locale;
//		}
//
//		/**
//		 * Creates a new Builder based on existing {@link AmountFormatSymbols}, e.g. for changing
//		 * the given <code>AmountFormatSymbols</code>.
//		 *
//		 * @param symbols
//		 *            the {@link AmountFormatSymbols}, not {@code null}.
//		 */
//		public Builder(AmountFormatSymbols symbols) {
//			Objects.requireNonNull(symbols);
//			this.locale = symbols.locale;
//			this.decimalSeparator = symbols.decimalSeparator;
//			this.digit = symbols.digit;
//			this.groupingSeparators = symbols.groupingSeparators;
//			this.minusSign = symbols.minusSign;
//			this.patternSeparator = symbols.patternSeparator;
//			this.zeroDigit = symbols.zeroDigit;
//		}
//
//		/**
//		 * Sets the character used for zero. Different for Arabic, etc.
//		 *
//		 * @param zeroDigit
//		 *            the zeroDigit char to be used.
//		 * @return the {@link Builder} for chaining.
//		 */
//		public Builder setZeroDigit(char zeroDigit) {
//			this.zeroDigit = zeroDigit;
//			return this;
//		}
//
//		/**
//		 * Sets the character used for thousands separator. Different for French, etc.
//		 *
//		 * @param groupingSeparators
//		 *            the grouping separators to be used, not {@code null} and not empty.
//		 * @return the {@link Builder} for chaining.
//		 */
//		public Builder setGroupingSeparator(char... groupingSeparators) {
//			Objects.requireNonNull(groupingSeparators);
//			this.groupingSeparators = groupingSeparators;
//			return this;
//		}
//
//		/**
//		 * Sets the character used for decimal sign. Different for French, etc.
//		 *
//		 * @param decimalSeparator
//		 *            the separator char to be used.
//		 * @return the {@link Builder} for chaining.
//		 */
//		public Builder setDecimalSeparator(char decimalSeparator) {
//			this.decimalSeparator = decimalSeparator;
//			return this;
//		}
//
//		/**
//		 * Sets the character used for decimal sign. Different for French, etc.
//		 *
//		 * @param exponentialSeparator
//		 *            the separator String to be used.
//		 * @return the {@link Builder} for chaining.
//		 */
//		public Builder setExponentialSeparator(String exponentialSeparator) {
//			this.exponentialSeparator = exponentialSeparator;
//			return this;
//		}
//
//		/**
//		 * Sets the infinity String used. Different for French, etc.
//		 *
//		 * @param infinity
//		 *            the infinity String to be used.
//		 * @return the {@link Builder} for chaining.
//		 */
//		public Builder setInfinity(String infinity) {
//			this.infinity = infinity;
//			return this;
//		}
//
//		/**
//		 * Sets the character used for a digit in a pattern.
//		 *
//		 * @param digit
//		 *            the digit char to be used.
//		 * @return the {@link Builder} for chaining.
//		 */
//		public Builder setDigit(char digit) {
//			this.digit = digit;
//			return this;
//		}
//
//		/**
//		 * Sets the character used to separate positive and negative subpatterns in a pattern.
//		 *
//		 * @param patternSeparator
//		 *            the separator char to be used.
//		 * @return the {@link Builder} for chaining.
//		 */
//		public Builder setPatternSeparator(char patternSeparator) {
//			this.patternSeparator = patternSeparator;
//			return this;
//		}
//
//		/**
//		 * Sets the character used to represent minus sign. If no explicit negative format is
//		 * specified, one is formed by prefixing minusSign to the positive format.
//		 *
//		 * @param minusSign
//		 *            the sign to be used.
//		 * @return the {@link Builder} for chaining.
//		 */
//		public Builder setMinusSign(char minusSign) {
//			this.minusSign = minusSign;
//			return this;
//		}
//
//		/**
//		 * Creates an instance of {@link AmountFormatSymbols} given the current data,
//		 *
//		 * @return a new instance of {@link AmountFormatSymbols}, never {@code null}.
//		 */
//		public AmountFormatSymbols create() {
//			return new AmountFormatSymbols(this);
//		}
//
//		/*
//		 * (non-Javadoc)
//		 * @see java.lang.Object#toString()
//		 */
//		@Override
//		public String toString() {
//			return "MonetaryFormatSymbols.Builder [locale=" + locale
//					+ ", patternSeparator=" + patternSeparator + ", digit="
//					+ digit
//					+ ", zeroDigit=" + zeroDigit + ", decimalSeparator="
//					+ decimalSeparator + ", groupingSeparators="
//					+ Arrays.toString(groupingSeparators) + ", minusSign="
//					+ minusSign + "]";
//		}
//	}
//
//}
