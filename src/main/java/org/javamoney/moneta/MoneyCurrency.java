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
package org.javamoney.moneta;

import java.io.Serializable;
import java.util.Currency;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

import javax.money.CurrencyUnit;

/**
 * Platform RI: Adapter that implements the new {@link CurrencyUnit} interface
 * using the JDK's {@link Currency}.
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
	/** The cache rounding value, -1 if not defined. */
	private int cacheRounding = -1;

	private static final Map<String, MoneyCurrency> CACHED = new ConcurrentHashMap<String, MoneyCurrency>();

	public static final String ISO_NAMESPACE = "IDO 4217";

	/**
	 * Private constructor.
	 * 
	 * @param currency
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
	 */
	private MoneyCurrency(Currency currency) {
		Objects.requireNonNull(currency,"Currency required.");
		this.currencyCode = currency.getCurrencyCode();
		this.numericCode = currency.getNumericCode();
		this.defaultFractionDigits = currency.getDefaultFractionDigits();
	}

	/**
	 * Access a new instance based on {@link Currency}.
	 * 
	 * @param currency
	 *            the currency unit not null.
	 * @return the new instance, never null.
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
	 * Access a new instance based on the ISO currency code. The code must
	 * return a {@link Currency} when passed to
	 * {@link Currency#getInstance(String)}.
	 * 
	 * @param namespace
	 *            the target namespace.
	 * @param currencyCode
	 *            the ISO currency code, not null.
	 * @return the corresponding {@link MonetaryCurrency} instance.
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

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.money.CurrencyUnit#getCurrencyCode()
	 */
	public String getCurrencyCode() {
		return currencyCode;
	}

	/**
	 * Gets a numeric currency code. within the ISO-4217 name space, this equals
	 * to the ISO numeric code. In other currency name spaces this number may be
	 * different, or even undefined (-1).
	 * <p>
	 * The numeric code is an optional alternative to the standard currency
	 * code. If defined, the numeric code is required to be unique within its
	 * namespace.
	 * <p>
	 * This method matches the API of <type>java.util.Currency</type>.
	 * 
	 * @see #getNamespace()
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
	 * @return the fractional digits, from 0 to 9 (normally 0, 2 or 3), or 0
	 *         for pseudo-currencies.
	 * 
	 */
	public int getDefaultFractionDigits() {
		return defaultFractionDigits;
	}

	/**
	 * Get the rounding steps in minor units for when using a cash amount of
	 * this currency. E.g. Swiss Francs in cash are always rounded in 5 minor
	 * unit steps. This results in {@code 1.00, 1.05, 1.10} etc. The cash
	 * rounding consequently extends the default fraction units for certain
	 * currencies.
	 * 
	 * @return the cash rounding, or -1, if not defined.
	 */
	public int getCashRounding() {
		return cacheRounding;
	}

	/*
	 * (non-Javadoc)
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
	 * Platform RI: Builder class that supports building complex instances of
	 * {@link MoneyCurrency}.
	 * 
	 * @author Anatole Tresch
	 */
	public static final class Builder {
		/** currency code for this currency. */
		private String currencyCode;
		/** numeric code, or -1. */
		private int numericCode = -1;
		/** fraction digits, or -1. */
		private int defaultFractionDigits = -1;
		/** Cache rounding. */
		private int cacheRounding = -1;

		/**
		 * Creates a new {@link Builder}.
		 */
		public Builder() {
		}

		/**
		 * Set the currency code.
		 * 
		 * @param namespace
		 *            the currency code, not null
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
		 * @param defaultFractionDigits
		 *            the default fraction digits
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
		 * Set the default fraction digits.
		 * 
		 * @param defaultFractionDigits
		 *            the default fraction digits
		 * @return the builder, for chaining
		 */
		public Builder setCashRounding(int cacheRounding) {
			if (cacheRounding < -1) {
				throw new IllegalArgumentException(
						"Invalid value for cacheRounding: " + cacheRounding);
			}
			this.cacheRounding = cacheRounding;
			return this;
		}

		/**
		 * Set the numeric currency code.
		 * 
		 * @param numericCode
		 *            the numeric currency code
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
		 * Builds a new currency instance, the instance build is not cached
		 * internally.
		 * 
		 * @see #build(boolean)
		 * @return a new instance of {@link MoneyCurrency}.
		 */
		public MoneyCurrency build() {
			return build(true);
		}

		/**
		 * Builds a new currency instance, which ia additinoally stored to the
		 * internal cache for reuse.
		 * 
		 * @param cache
		 *            flag to optionally store the instance created into the
		 *            locale cache.
		 * @return a new instance of {@link MoneyCurrency}.
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
	}

	public static MoneyCurrency from(CurrencyUnit currency) {
		if (MoneyCurrency.class == currency.getClass()) {
			return (MoneyCurrency) currency;
		}
		return MoneyCurrency.of(currency.getCurrencyCode());
	}

	public static boolean isJavaCurrency(String code) {
		try {
			return Currency.getInstance(code) != null;
		} catch (Exception e) {
			return false;
		}
	}

	public static boolean isAvailable(String code) {
		try {
			of(code);
			return true;
		} catch (Exception e) {
			return false;
		}
	}

}
