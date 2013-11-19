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
package javax.money.format;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Arrays;
import java.util.Locale;

import javax.money.MonetaryAmount;
import javax.money.MonetaryOperator;

/**
 * The amount style defines how a {@link MonetaryAmount} should be formatted.
 * 
 * @author Anatole Tresch
 */
public final class AmountStyle {
	/** Default array for uncostimized group characters. */
	private static final char[] EMPTY_CHAR_ARRAY = new char[0];
	/** Default array for uncostimized group sizes. */
	private static final int[] EMPTY_INT_ARRAY = new int[0];
	/** The {@link DecimalFormat} used. */
	private DecimalFormat format;
	/** THe rounding used (optional). */
	private MonetaryOperator rounding;
	/** The customized group sizes. */
	private int[] groupSizes;
	/** The customized group characters. */
	private char[] groupChars;

	/**
	 * Constructor.
	 * 
	 * @param format
	 *            The {@link DecimalFormat} used.
	 * @param groupSizes
	 *            the customized group sizes.
	 * @param groupChars
	 *            the customized group characters.
	 * @param rounding
	 *            the custom rounding.
	 */
	private AmountStyle(DecimalFormat format, int[] groupSizes,
			char[] groupChars, MonetaryOperator rounding) {
		this.groupSizes = groupSizes;
		this.groupChars = groupChars;
		this.rounding = rounding;
		this.format = format;
	}

	/**
	 * Get the rounding used.
	 * 
	 * @return the rounding used, or null.
	 */
	public MonetaryOperator getMoneyRounding() {
		return this.rounding;
	}

	/**
	 * Get the number groups sizes used, or an empty array if no custom sizes
	 * are configured.
	 * 
	 * @return the groupings sizes, never null.
	 */
	public int[] getNumberGroupSizes() {
		if (this.groupSizes == null) {
			return EMPTY_INT_ARRAY;
		}
		return this.groupSizes.clone();
	}

	/**
	 * Get the number groups chars used, or an empty array if no custom chars
	 * are configured.
	 * 
	 * @return the groupings chars, never null.
	 */
	public char[] getNumberGroupChars() {
		if (this.groupChars == null) {
			return EMPTY_CHAR_ARRAY;
		}
		return this.groupChars.clone();
	}

	/**
	 * Get the current number format pattern.
	 * 
	 * @see DecimalFormat#toPattern()
	 * @return the current pattern.
	 */
	public String getPattern() {
		return this.format.toPattern();
	}

	/**
	 * Get the current number format localized pattern.
	 * 
	 * @see DecimalFormat#toLocalizedPattern()
	 * @return the current localized pattern.
	 */
	public String getLocalizedPattern(String pattern) {
		return this.format.toLocalizedPattern();
	}

	/**
	 * Get the current {@link DecimalFormatSymbols}.
	 * 
	 * @see DecimalFormat#getDecimalFormatSymbols()
	 * @return the current {@link DecimalFormatSymbols}.
	 */
	public DecimalFormatSymbols getDecimalSymbols() {
		return this.format.getDecimalFormatSymbols();
	}

	/**
	 * Get the current {@link DecimalFormat#getMaximumFractionDigits()}.
	 * 
	 * @see DecimalFormat#getMaximumFractionDigits()
	 * @return the current {@link DecimalFormat#getMaximumFractionDigits()}.
	 */
	public int getMaximumFractionDigits() {
		return this.format.getMaximumFractionDigits();
	}

	/**
	 * Get the current {@link DecimalFormat#getMaximumIntegerDigits()}.
	 * 
	 * @see DecimalFormat#getMaximumIntegerDigits()
	 * @return the current {@link DecimalFormat#getMaximumIntegerDigits()}.
	 */
	public int withMaximumIntegerDigits() {
		return this.format.getMaximumIntegerDigits();
	}

	/**
	 * Get the current {@link DecimalFormat#getMinimumFractionDigits()}.
	 * 
	 * @see DecimalFormat#getMinimumFractionDigits()
	 * @return the current {@link DecimalFormat#getMinimumFractionDigits()}.
	 */
	public int getMinimumFractionDigits() {
		return this.format.getMinimumFractionDigits();
	}

	/**
	 * Get the current {@link DecimalFormat#getMinimumIntegerDigits()}.
	 * 
	 * @see DecimalFormat#getMinimumIntegerDigits()
	 * @return the current {@link DecimalFormat#getMinimumIntegerDigits()}.
	 */
	public int getMinimumIntegerDigits() {
		return this.format.getMinimumIntegerDigits();
	}

	/**
	 * Get the current {@link DecimalFormat#getMultiplier()}.
	 * 
	 * @see DecimalFormat#getMultiplier()
	 * @return the current {@link DecimalFormat#getMultiplier()}.
	 */
	public int getMultiplier() {
		return this.format.getMultiplier();
	}

	/**
	 * Get the current {@link DecimalFormat#getNegativePrefix()}.
	 * 
	 * @see DecimalFormat#getNegativePrefix()
	 * @return the current {@link DecimalFormat#getNegativePrefix()}.
	 */
	public String getNegativePrefix() {
		return this.format.getNegativePrefix();
	}

	/**
	 * Get the current {@link DecimalFormat#getNegativeSuffix()}.
	 * 
	 * @see DecimalFormat#getNegativeSuffix()
	 * @return the current {@link DecimalFormat#getNegativeSuffix()}.
	 */
	public String getNegativeSuffix() {
		return this.format.getNegativeSuffix();
	}

	/**
	 * Get the current {@link DecimalFormat#getPositivePrefix()}.
	 * 
	 * @see DecimalFormat#getPositivePrefix()
	 * @return the current {@link DecimalFormat#getPositivePrefix()}.
	 */
	public String getPositivePrefix() {
		return this.format.getPositivePrefix();
	}

	/**
	 * Get the current {@link DecimalFormat#getPositiveSuffix()}.
	 * 
	 * @see DecimalFormat#getPositiveSuffix()
	 * @return the current {@link DecimalFormat#getPositiveSuffix()}.
	 */
	public String getPositiveSuffix() {
		return this.format.getPositiveSuffix();
	}

	/**
	 * Get the current {@link DecimalFormat#isDecimalSeparatorAlwaysShown()}.
	 * 
	 * @see DecimalFormat#isDecimalSeparatorAlwaysShown()
	 * @return the current {@link DecimalFormat#isDecimalSeparatorAlwaysShown()}
	 *         .
	 */
	public boolean isDecimalSeparatorAlwaysShown() {
		return this.format.isDecimalSeparatorAlwaysShown();
	}

	/**
	 * Get the current {@link DecimalFormat#isParseIntegerOnly()}.
	 * 
	 * @see DecimalFormat#isParseIntegerOnly()
	 * @return the current {@link DecimalFormat#isParseIntegerOnly()}.
	 */
	public boolean isParseIntegerOnly() {
		return this.format.isParseIntegerOnly();
	}

	/**
	 * Get the current used {@link DecimalFormat}.
	 * 
	 * @return the current used {@link DecimalFormat}
	 */
	DecimalFormat getDecimalFormat() {
		return this.format;
	}

	/**
	 * Builder for creating a new {@link AmountStyle}
	 * 
	 * @author Anatole Tresch
	 */
	public static final class Builder {
		/** The underlying {@link DecimalFormat}. */
		private DecimalFormat format;
		/** The rounding operator, if any. */
		private MonetaryOperator rounding;
		/** The customized goup sizes, if any. */
		private int[] groupSizes;
		/** The customized group characters, if any. */
		private char[] groupChars;

		/**
		 * Creates a new {@link Builder}.
		 * 
		 * @param locale
		 *            the target {@link Locale}, not {@code null}.
		 */
		public Builder(Locale locale) {
			if (locale == null) {
				throw new IllegalArgumentException("Locale required.");
			}
			this.format = (DecimalFormat) DecimalFormat.getInstance(locale);
		}

		/**
		 * Sets the rounding to be used for formatting.
		 * 
		 * @param rounding
		 *            the rounding, not null.
		 * @return the {@link Builder} for chaining.
		 */
		public Builder setRounding(MonetaryOperator rounding) {
			this.rounding = rounding;
			return this;
		}

		/**
		 * Sets the customized number group sizes to be used for formatting.
		 * Hereby each value in the array represents a group size, starting from
		 * the decimal point and going up the significant digits. The last entry
		 * in the array is used as a default group size for all subsequent
		 * groupings.
		 * 
		 * @param groupSizes
		 *            the group sizes, not null.
		 * @return the {@link Builder} for chaining.
		 */
		public Builder setNumberGroupSizes(int... groupSizes) {
			this.groupSizes = groupSizes;
			return this;
		}

		/**
		 * Sets the customized number group characters to be used for
		 * formatting. Hereby each value in the array represents a group
		 * character for a group, starting from the decimal point and going up
		 * the significant digits. The last entry in the array is used as a
		 * default group character for all subsequent groupings.
		 * 
		 * @param groupChars
		 *            the group characters, not null.
		 * @return the {@link Builder} for chaining.
		 */
		public Builder setNumberGroupChars(char... groupChars) {
			this.groupChars = groupChars;
			return this;
		}

		/**
		 * Set the {@link DecimalFormat} pattern used for the number formatting.
		 * 
		 * @see DecimalFormat#applyPattern(String)
		 * @param pattern
		 *            the {@link DecimalFormat} pattern used, not {@code null}.
		 * @return the {@link Builder} for chaining.
		 */
		public Builder setPattern(String pattern) {
			this.format.applyPattern(pattern);
			return this;
		}

		/**
		 * Set the {@link DecimalFormat} localized pattern used for the number
		 * formatting.
		 * 
		 * @see DecimalFormat#applyLocalizedPattern(String)
		 * @param pattern
		 *            the {@link DecimalFormat} pattern used, not {@code null}.
		 * @return the {@link Builder} for chaining.
		 */
		public Builder setLocalizedPattern(String pattern) {
			this.format.applyLocalizedPattern(pattern);
			return this;
		}

		/**
		 * Sets the {@link DecimalFormat} to be used.
		 * 
		 * @see DecimalFormat
		 * @param format
		 *            the {@link DecimalFormat}, not null
		 * @return the {@link Builder} for chaining.
		 */
		public Builder setDecimalFormat(DecimalFormat format) {
			if (format == null) {
				throw new IllegalArgumentException("format required.");
			}
			this.format = format;
			return this;
		}

		/**
		 * Sets the {@link DecimalFormatSymbols} to be used.
		 * 
		 * @see DecimalFormat#setDecimalFormatSymbols(DecimalFormatSymbols)
		 * @param symbols
		 *            the {@link DecimalFormatSymbols} to be used, not null.
		 * @return the {@link Builder} for chaining.
		 */
		public Builder setDecimalSymbols(DecimalFormatSymbols symbols) {
			if (symbols == null) {
				throw new IllegalArgumentException("symbols required.");
			}
			this.format.setDecimalFormatSymbols(symbols);
			return this;
		}

		/**
		 * Set the maximum fraction digits.
		 * 
		 * @see {@link DecimalFormat#setMaximumFractionDigits(int)}
		 * @param maxFractionDigits
		 * @return the {@link Builder} for chaining.
		 */
		public Builder setMaximumFractionDigits(int maxFractionDigits) {
			this.format.setMaximumFractionDigits(maxFractionDigits);
			return this;
		}

		/**
		 * Set the maximum integer digits.
		 * 
		 * @see {@link DecimalFormat#setMaximumIntegerDigits(int)}
		 * @param maxFractionDigits
		 * @return the {@link Builder} for chaining.
		 */
		public Builder setMaximumIntegerDigits(int maxIntegerDigits) {
			this.format.setMaximumIntegerDigits(maxIntegerDigits);
			return this;
		}

		/**
		 * Set the minimum fraction digits.
		 * 
		 * @see {@link DecimalFormat#setMinimumFractionDigits(int)}
		 * @param maxFractionDigits
		 * @return the {@link Builder} for chaining.
		 */
		public Builder setMinimumFractionDigits(int minFractionDigits) {
			this.format.setMinimumFractionDigits(minFractionDigits);
			return this;
		}

		/**
		 * Set the minimum integer digits.
		 * 
		 * @@see {@link DecimalFormat#setMinimumIntegerDigits(int)}
		 * @param maxFractionDigits
		 * @return the {@link Builder} for chaining.
		 */
		public Builder setMinimumIntegerDigits(int minIntegerDigits) {
			this.format.setMinimumIntegerDigits(minIntegerDigits);
			return this;
		}

		/**
		 * Set the multiplier.
		 * 
		 * @see {@link DecimalFormat#setMultiplier(int)}
		 * @param multiplier
		 * @return the {@link Builder} for chaining.
		 */
		public Builder setMultiplier(int multiplier) {
			this.format.setMultiplier(multiplier);
			return this;
		}

		/**
		 * Set the negative prefix.
		 * 
		 * @see {@link DecimalFormat#setNegativePrefix(String)}
		 * @param prefix
		 * @return the {@link Builder} for chaining.
		 */
		public Builder setNegativePrefix(String prefix) {
			this.format.setNegativePrefix(prefix);
			return this;
		}

		/**
		 * Set the negative suffix.
		 * 
		 * @see {@link DecimalFormat#setNegativeSuffix(String)}
		 * @param suffix
		 * @return the {@link Builder} for chaining.
		 */
		public Builder setNegativeSuffix(String suffix) {
			this.format.setNegativeSuffix(suffix);
			return this;
		}

		/**
		 * Set the positive prefix.
		 * 
		 * @see {@link DecimalFormat#setPositivePrefix(String)}
		 * @param prefix
		 * @return the {@link Builder} for chaining.
		 */
		public Builder setPositivePrefix(String prefix) {
			this.format.setPositivePrefix(prefix);
			return this;
		}

		/**
		 * Set the positive suffix.
		 * 
		 * @see {@link DecimalFormat#setPositiveSuffix(String)}
		 * @param suffix
		 * @return the {@link Builder} for chaining.
		 */
		public Builder setPositiveSuffix(String suffix) {
			this.format.setPositiveSuffix(suffix);
			return this;
		}

		/**
		 * Set flag if decimal separator is always shown.
		 * 
		 * @see {@link DecimalFormat#setDecimalSeparatorAlwaysShown(String)}
		 * @param value
		 * @return the {@link Builder} for chaining.
		 */
		public Builder setDecimalSeparatorAlwaysShown(boolean value) {
			this.format.setDecimalSeparatorAlwaysShown(value);
			return this;
		}

		/**
		 * Set parse integer only.
		 * 
		 * @see {@link DecimalFormat#setParseIntegerOnly(String)}
		 * @param value
		 * @return the {@link Builder} for chaining.
		 */
		public Builder setParseIntegerOnly(boolean value) {
			this.format.setParseIntegerOnly(value);
			return this;
		}

		/**
		 * Set the {@link DecimalFormat} as defined by
		 * {@link DecimalFormat#getInstance(Locale)} by the given {@link Locale}
		 * .
		 * 
		 * @see {@link DecimalFormat#getInstance(Locale)}
		 * @param locale
		 *            The target {@link Locale}, not null.
		 * @return the {@link Builder} for chaining.
		 */
		public Builder setCurrencyFormat(Locale locale) {
			if (locale == null) {
				throw new IllegalArgumentException("locale required.");
			}
			this.format = (DecimalFormat) DecimalFormat.getInstance(locale);
			return this;
		}

		/**
		 * Creates a new {@link AmountStyle}.
		 * 
		 * @return a new {@link AmountStyle} instance, never {@code null}.
		 * @throws IllegalStateException
		 *             if no {@link DecimalFormat} could be applied.
		 */
		public AmountStyle build() {
			if (format == null) {
				throw new IllegalStateException("DecimalFormat required.");
			}
			return new AmountStyle(format, groupSizes, groupChars, rounding);
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see java.lang.Object#toString()
		 */
		@Override
		public String toString() {
			return "AmountStyle.Builder [format=" + format + ", rounding="
					+ rounding
					+ ", groupSizes=" + Arrays.toString(groupSizes)
					+ ", groupChars=" + Arrays.toString(groupChars) + "]";
		}

	}
}
