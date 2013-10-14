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
import java.util.concurrent.ConcurrentHashMap;

import javax.money.CurrencyUnit;
import javax.money.SubUnit;

/**
 * Platform RI: Adapter that implements the new {@link SubUnit} interface
 * using the JDK's {@link Currency}.
 * 
 * @version 0.2
 * @author Werner Keil
 */
public final class FractionCurrency implements SubUnit, Serializable,
		Comparable<SubUnit> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8482610025974818160L;
	
	/** main currency code for this fraction currency. */
	private String currencyCode;
	
    /**
     * Id for this fraction currency.
     *
     * @serial
     */
    private final String id;
	
    /**
     * Fraction digits for this fraction currency.
     * Set from currency data tables.
     */
    transient private final int fractionDigits;
    
	/** The cache rounding value, -1 if not defined. */
	private int cacheRounding = -1;

	private static final Map<String, FractionCurrency> CACHED = new ConcurrentHashMap<String, FractionCurrency>();

	/**
	 * Private constructor.
	 * 
	 * @param currency
	 * @param id
	 */
	private FractionCurrency(String code, String id, int fractionDigits) {
		this.currencyCode = code;
		this.id = id;
		this.fractionDigits = fractionDigits;
	}

	/**
	 * Private constructor.
	 * 
	 * @param currency
	 * @param id
	 */
	private FractionCurrency(Currency currency, String id) {
		if (currency == null) {
			throw new IllegalArgumentException("Currency required.");
		}
		this.currencyCode = currency.getCurrencyCode();
		this.fractionDigits = currency.getDefaultFractionDigits();
		this.id= id;
	}
	
	/**
	 * Private constructor.
	 * 
	 * @param currency
	 * @param id
	 */
	private FractionCurrency(Currency currency) {
		if (currency == null) {
			throw new IllegalArgumentException("Currency required.");
		}
		this.currencyCode = currency.getCurrencyCode();
		this.fractionDigits = currency.getDefaultFractionDigits();
		this.id= "";
	}

	/**
	 * Access a new instance based on {@link Currency}.
	 * 
	 * @param currency
	 *            the currency unit not null.
	 * @return the new instance, never null.
	 */
	public static FractionCurrency of(Currency currency, String id) {
		String key = currency.getCurrencyCode();
		FractionCurrency cachedItem = CACHED.get(key);
		if (cachedItem == null) {
			cachedItem = new FractionCurrency(currency, id);
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
	public static FractionCurrency of(String currencyCode, String id) {
		FractionCurrency cu = CACHED.get(currencyCode);
		if (cu == null) {
			if (MoneyCurrency.isJavaCurrency(currencyCode)) {
				Currency cur = Currency.getInstance(currencyCode);
				if (cur != null) {
					return of(cur, id);
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

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	public int compareTo(SubUnit currency) {
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
		FractionCurrency other = (FractionCurrency) obj;
		if (currencyCode == null) {
			if (other.currencyCode != null)
				return false;
		} else if (!currencyCode.equals(other.currencyCode))
			return false;
		return true;
	}

	/**
	 * Returns {@link #getCurrencyCode()} and {@link #getId()}
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return currencyCode + " " + id;
	}

	/**
	 * Platform RI: Builder class that supports building complex instances of
	 * {@link FractionCurrency}.
	 * 
	 * @author Werner Keil
	 */
	public static final class Builder {
		/** currency code for this currency. */
		private String currencyCode;
		
		/** Id for this currency. */
		private String id;
		
		/** numeric code, or -1. */
		private int numericCode = -1;
		/** fraction digits, or -1. */
		private int fractionDigits = -1;
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
		public Builder withCurrencyCode(String currencyCode) {
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
		public Builder withDefaultFractionDigits(int defaultFractionDigits) {
			if (defaultFractionDigits < -1) {
				throw new IllegalArgumentException(
						"Invalid value for defaultFractionDigits: "
								+ defaultFractionDigits);
			}
			this.fractionDigits = defaultFractionDigits;
			return this;
		}

		/**
		 * Set the default fraction digits.
		 * 
		 * @param defaultFractionDigits
		 *            the default fraction digits
		 * @return the builder, for chaining
		 */
		public Builder withCashRounding(int cacheRounding) {
			if (cacheRounding < -1) {
				throw new IllegalArgumentException(
						"Invalid value for cacheRounding: " + cacheRounding);
			}
			return this;
		}

		/**
		 * Set the numeric currency code.
		 * 
		 * @param numericCode
		 *            the numeric currency code
		 * @return the builder, for chaining
		 */
		public Builder withNumericCode(int numericCode) {
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
		 * @return a new instance of {@link FractionCurrency}.
		 */
		public FractionCurrency build() {
			return build(true);
		}

		/**
		 * Builds a new currency instance, which ia additinoally stored to the
		 * internal cache for reuse.
		 * 
		 * @param cache
		 *            flag to optionally store the instance created into the
		 *            locale cache.
		 * @return a new instance of {@link FractionCurrency}.
		 */
		public FractionCurrency build(boolean cache) {
			if (cache) {
				FractionCurrency current = CACHED.get(currencyCode);
				if (current == null) {
					current = new FractionCurrency(currencyCode, id,
							fractionDigits);
					CACHED.put(currencyCode, current);
				}
				return current;
			}
			return new FractionCurrency(currencyCode, id, fractionDigits);
		}
	}

	public static FractionCurrency from(CurrencyUnit currency, String id) {
		if (FractionCurrency.class == currency.getClass()) {
			return (FractionCurrency) currency;
		}
		return FractionCurrency.of(currency.getCurrencyCode(), id);
	}

	public static FractionCurrency from(CurrencyUnit currency) {
		if (FractionCurrency.class == currency.getClass()) {
			return (FractionCurrency) currency;
		}
		String defId = "";
		return from(currency, defId);
	}


//	public static boolean isAvailable(String code) {
//		try {
//			of(code);
//			return true;
//		} catch (Exception e) {
//			return false;
//		}
//	}

	@Override
	public String getId() {
		return id;
	}

	/**
     * Gets the number of fraction digits used with this fraction currency.
     * For example, the default number of fraction digits for the Euro is 2,
     * while for the Japanese Yen it's 0.
     * In the case of pseudo-currencies, such as IMF Special Drawing Rights,
     * -1 is returned.
     *
     * @return the number of fraction digits used with this fraction currency
     */
	@Override
	public int getFractionDigits() {
		return fractionDigits;
	}

}
