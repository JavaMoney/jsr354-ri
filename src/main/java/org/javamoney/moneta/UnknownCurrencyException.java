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
 * 
 * Contributors: Anatole Tresch - initial implementation Werner Keil -
 * extensions and adaptions.
 */
package org.javamoney.moneta;

import java.util.Objects;

import javax.money.MonetaryException;

/**
 * Exception thrown when the requested currency is unknown to the currency
 * system in use.
 * <p>
 * For example, this exception would be thrown when trying to obtain a currency
 * using an unrecognized currency code or locale.
 * 
 * @author Werner Keil
 */
public class UnknownCurrencyException extends MonetaryException {

	private static final long serialVersionUID = 3277879391197687869L;
	/** The requested currency code. */
	private final String currencyCode;

	/**
	 * Constructor.
	 * 
	 * @param currencyCode
	 *            the currencyCode, not {@code null}.
	 * @param message
	 *            the exception message, if null a default message will be
	 *            created.
	 */
	public UnknownCurrencyException(String currencyCode) {
		this(currencyCode, null);
	}

	/**
	 * Constructor.
	 * 
	 * @param currencyCode
	 *            the currencyCode, not {@code null}.
	 * @param message
	 *            the exception message, if null a default message will be
	 *            created.
	 */
	public UnknownCurrencyException(String currencyCode,
			String message) {
		super(message == null ? "Unknown currency - "
				+ currencyCode : message);
		Objects.requireNonNull(currencyCode,"currencyCode may not be null.");
		this.currencyCode = currencyCode;
	}

	/**
	 * Access the currency code of the unknown currency.
	 * 
	 * @return the currency code of the unknown currency.
	 */
	public String getCurrencyCode() {
		return this.currencyCode;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "UnknownCurrencyException [currencyCode=" + currencyCode + "]";
	}

}
