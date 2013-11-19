/*
 * CREDIT SUISSE IS WILLING TO LICENSE THIS SPECIFICATION TO YOU ONLY UPON THE
 * CONDITION THAT YOU ACCEPT ALL OF THE TERMS CONTAINED IN THIS AGREEMENT.
 * PLEASE READ THE TERMS AND CONDITIONS OF THIS AGREEMENT CAREFULLY. BY
 * DOWNLOADING THIS SPECIFICATION, YOU ACCEPT THE TERMS AND CONDITIONS OF THE
 * AGREEMENT. IF YOU ARE NOT WILLING TO BE BOUND BY IT, SELECT THE "DECLINE"
 * BUTTON AT THE BOTTOM OF THIS PAGE.
 * 
 * Specification: JSR-354 Money and Currency API ("Specification")
 * 
 * Copyright (c) 2012-2013, Credit Suisse All rights reserved.
 */
package javax.money;

import java.io.Serializable;
import java.util.Currency;
import java.util.Map;
import java.util.Objects;
import java.util.ServiceLoader;
import java.util.concurrent.ConcurrentHashMap;

import javax.money.spi.CurrencyProviderSpi;

/**
 * Default implementation of a {@link CurrencyUnit} based on the using the JDK's
 * {@link Currency}, but also extendable using a {@link Builder} instance.
 * 
 * @version 0.5.1
 * @author Anatole Tresch
 * @author Werner Keil
 */
public final class MoneyCurrency implements CurrencyUnit, Serializable,
		Comparable<CurrencyUnit> {

	/**
	 * serialVersionUID.
	 */
	private static final long serialVersionUID = -2523936311372374236L;

	/** currency code for this currency. */
	private String currencyCode;
	/** numeric code, or -1. */
	private int numericCode;
	/** fraction digits, or -1. */
	private int defaultFractionDigits;
	/** Internal shared cache of {@link MoneyCurrency} instances. */
	private static final Map<String, MoneyCurrency> CACHED = new ConcurrentHashMap<String, MoneyCurrency>();

	/**
	 * Private constructor, use a {@link Builder} for creating new instances.
	 * 
	 * @param code
	 *            the currency code, not {@code null} or empty.
	 * @param numCode
	 *            the numeric code, >= -1.
	 * @param fractionDigits
	 *            the fraction digits, >= -1.
	 */
	private MoneyCurrency(String code,
			int numCode,
			int fractionDigits) {
		this.currencyCode = code;
		this.numericCode = numCode;
		this.defaultFractionDigits = fractionDigits;
	}

	/**
	 * Private constructor.
	 * 
	 * @param currency
	 *            the JDK {@link Currency}, not {@code null}.
	 */
	private MoneyCurrency(Currency currency) {
		Objects.requireNonNull(currency, "Currency required.");
		this.currencyCode = currency.getCurrencyCode();
		this.numericCode = currency.getNumericCode();
		this.defaultFractionDigits = currency.getDefaultFractionDigits();
	}

	/**
	 * Access a new instance based on {@link Currency}.
	 * 
	 * @param currency
	 *            the currency unit not {@code null}.
	 * @return the new instance, never {@code null}.
	 */
	public static MoneyCurrency of(Currency currency) {
		String key = currency.getCurrencyCode();
		MoneyCurrency cachedItem = CACHED.get(key);
		if (cachedItem == null) {
			cachedItem = new MoneyCurrency(currency);
			CACHED.put(key, cachedItem);
		}
		return cachedItem;
	}

	/**
	 * Access a new instance based on the currency code. Currencies that are
	 * available also on {@link Currency#getInstance(String)} are accessible by
	 * default. For other currencies, instances of {@link CurrencyProviderSpi}
	 * may registered with the {@link ServiceLoader}, or they can be created and
	 * registered programmatically using a {@link Builder}.
	 * 
	 * @param currencyCode
	 *            the ISO currency code, not {@code null}.
	 * @return the corresponding {@link MoneyCurrency} instance.
	 * @throws IllegalArgumentException
	 *             if no such currency exists.
	 */
	public static MoneyCurrency of(String currencyCode) {
		MoneyCurrency cu = CACHED.get(currencyCode);
		if (cu == null) {
			if (MoneyCurrency.isJavaCurrency(currencyCode)) {
				Currency cur = Currency.getInstance(currencyCode);
				if (cur != null) {
					return of(cur);
				}
			}
		}
		if (cu == null) {
			throw new IllegalArgumentException("No such currency: "
					+ currencyCode);
		}
		return cu;
	}

	/**
	 * Gets the unique currency code, the effective code depends on the
	 * currency.
	 * <p>
	 * Since each currency is identified by this code, the currency code is
	 * required to be defined for every {@link CurrencyUnit} and not
	 * {@code null} or empty.
	 * <p>
	 * For ISO codes the 3-letter ISO code should be returned. For non ISO
	 * currencies no constraints are defined.
	 * 
	 * @return the currency code, never {@code null}. For ISO-4217 this this
	 *         will be the three letter ISO-4217 code. However, alternate
	 *         currencies can have different codes. Also there is no constraint
	 *         about the formatting of alternate codes, despite they fact that
	 *         the currency codes must be unique.
	 * @see javax.money.CurrencyUnit#getCurrencyCode()
	 */
	public String getCurrencyCode() {
		return currencyCode;
	}

	/**
	 * Gets a numeric currency code. Within the ISO-4217 name space, this equals
	 * to the ISO numeric code. In other currency name spaces this number may be
	 * different, or even undefined (-1).
	 * <p>
	 * The numeric code is an optional alternative to the standard currency
	 * code. If defined, the numeric code is required to be unique.
	 * <p>
	 * This method matches the API of <type>java.util.Currency</type>.
	 * 
	 * @see CurrencyUnit#getNumericCode()
	 * @return the numeric currency code
	 */
	public int getNumericCode() {
		return numericCode;
	}

	/**
	 * Gets the number of fractional digits typically used by this currency.
	 * <p>
	 * Different currencies have different numbers of fractional digits by
	 * default. * For example, 'GBP' has 2 fractional digits, but 'JPY' has
	 * zero. * virtual currencies or those with no applicable fractional are
	 * indicated by -1. *
	 * <p>
	 * This method matches the API of <type>java.util.Currency</type>.
	 * 
	 * @return the fractional digits, from 0 to 9 (normally 0, 2 or 3), or 0 for
	 *         pseudo-currencies.
	 * 
	 */
	public int getDefaultFractionDigits() {
		return defaultFractionDigits;
	}

	/**
	 * Compares two instances, based on {@link #getCurrencyCode()}.
	 * 
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	public int compareTo(CurrencyUnit currency) {
		return getCurrencyCode().compareTo(currency.getCurrencyCode());
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
				+ ((currencyCode == null) ? 0 : currencyCode.hashCode());
		return result;
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
		MoneyCurrency other = (MoneyCurrency) obj;
		if (currencyCode == null) {
			if (other.currencyCode != null)
				return false;
		} else if (!currencyCode.equals(other.currencyCode))
			return false;
		return true;
	}

	/**
	 * Returns {@link #getCurrencyCode()}
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return currencyCode;
	}

	/**
	 * Builder class that supports building and registering instances of
	 * {@link MoneyCurrency} programmatically.
	 * 
	 * @author Anatole Tresch
	 */
	public static final class Builder {
		/** Currency code for the currency built. */
		private String currencyCode;
		/** Numeric code, or -1. */
		private int numericCode = -1;
		/** Default fraction digits, or -1. */
		private int defaultFractionDigits = -1;

		/**
		 * Creates a new {@link Builder}.
		 */
		public Builder() {
		}

		/**
		 * Set the currency code.
		 * 
		 * @see CurrencyUnit#getCurrencyCode()
		 * @param currencyCode
		 *            the currency code, not {@code null}
		 * @return the builder, for chaining
		 */
		public Builder setCurrencyCode(String currencyCode) {
			if (currencyCode == null) {
				throw new IllegalArgumentException(
						"currencyCode may not be null.");
			}
			this.currencyCode = currencyCode;
			return this;
		}

		/**
		 * Set the default fraction digits.
		 * 
		 * @see CurrencyUnit#getDefaultFractionDigits()
		 * @param defaultFractionDigits
		 *            the default fraction digits, >= -1;
		 * @return the builder, for chaining
		 */
		public Builder setDefaultFractionDigits(int defaultFractionDigits) {
			if (defaultFractionDigits < -1) {
				throw new IllegalArgumentException(
						"Invalid value for defaultFractionDigits: "
								+ defaultFractionDigits);
			}
			this.defaultFractionDigits = defaultFractionDigits;
			return this;
		}

		/**
		 * Set the numeric currency code.
		 * 
		 * @see CurrencyUnit#getNumericCode()
		 * @param numericCode
		 *            the numeric currency code, >= -1
		 * @return the builder, for chaining
		 */
		public Builder setNumericCode(int numericCode) {
			if (numericCode < -1) {
				throw new IllegalArgumentException(
						"Invalid value for numericCode: " + numericCode);
			}
			this.numericCode = numericCode;
			return this;
		}

		/**
		 * Builds a new {@link MoneyCurrency} instance, the instance build is
		 * not cached internally.
		 * 
		 * @see {@link MoneyCurrency#of(String)}
		 * @see #build(boolean)
		 * @return a new instance of {@link MoneyCurrency}.
		 */
		public MoneyCurrency build() {
			return build(true);
		}

		/**
		 * Builds a new {@link MoneyCurrency} instance, which is additionally
		 * stored to the internal cache for reuse, so it is accessible calling
		 * {@link MoneyCurrency#of(String)}.
		 * 
		 * @see {@link MoneyCurrency#of(String)}
		 * @param cache
		 *            flag to optionally store the instance created into the
		 *            locale cache.
		 * @return the new instance of {@link MoneyCurrency}.
		 */
		public MoneyCurrency build(boolean cache) {
			if (cache) {
				MoneyCurrency current = CACHED.get(currencyCode);
				if (current == null) {
					current = new MoneyCurrency(currencyCode,
							numericCode, defaultFractionDigits);
					CACHED.put(currencyCode, current);
				}
				return current;
			}
			return new MoneyCurrency(currencyCode, numericCode,
					defaultFractionDigits);
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see java.lang.Object#toString()
		 */
		@Override
		public String toString() {
			return "MoneyCurrency.Builder [currencyCode=" + currencyCode
					+ ", numericCode="
					+ numericCode + ", defaultFractionDigits="
					+ defaultFractionDigits + "]";
		}

	}

	/**
	 * Access an instance of {@link MoneyCurrency} that is backed up by the
	 * given {@link Currency}.
	 * 
	 * @param currency
	 *            the {@link Currency}, not {@code null}
	 * @return the according {@link MoneyCurrency}, never {@code null}.
	 */
	public static MoneyCurrency from(CurrencyUnit currency) {
		if (MoneyCurrency.class == currency.getClass()) {
			return (MoneyCurrency) currency;
		}
		return MoneyCurrency.of(currency.getCurrencyCode());
	}

	/**
	 * Method to determine if the given {@link MoneyCurrency} instance is backed
	 * up by {@link Currency}.
	 * 
	 * @param code
	 *            the currency code, not {@code null}
	 * @return {@code true} if {@link Currency#getInstance(String)} returns a
	 *         valid {@link Currency} for the given code.
	 */
	public static boolean isJavaCurrency(String code) {
		try {
			return Currency.getInstance(code) != null;
		} catch (Exception e) {
			return false;
		}
	}

	/**
	 * Allows to check if a {@link MoneyCurrency} instance is defined, i.e.
	 * accessible from {@link MoneyCurrency#of(String)}.
	 * 
	 * @param code
	 *            the currency code, not {@code null}.
	 * @return {@code true} if {@link MoneyCurrency#of(String)} would return a
	 *         result for the given code.
	 */
	public static boolean isAvailable(String code) {
		try {
			of(code);
			return true;
		} catch (Exception e) {
			return false;
		}
	}

}
