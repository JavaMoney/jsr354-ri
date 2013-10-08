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

import javax.money.CurrencyUnit;
import javax.money.MonetaryAmount;
import javax.money.MonetaryException;

/**
 * Exception thrown when the currency of a {@link MonetaryAmount} passed to
 * arithmetic operations on another {@link MonetaryAmount} is not compatible.
 * <p>
 * For example, this exception would be thrown when trying to multiply a
 * {@link MonetaryAmount} in (ISO-4217) CHF with a a {@link MonetaryAmount} in
 * (ISO-4217) USD.
 * 
 * @author Werner Keil
 * @author Anatole Tresch
 */
public class CurrencyMismatchException extends MonetaryException {
	/**
	 * serialVersionUID.
	 */
	private static final long serialVersionUID = 3277879391197687869L;

	/** The source currrency */
	private CurrencyUnit source;

	/** The target currrency */
	private CurrencyUnit target;

	/**
	 * Constructor.
	 * 
	 * @param source
	 *            the source currency, not {@code null}.
	 * @param target
	 *            the mismatching target currency, not {@code null}.
	 */
	public CurrencyMismatchException(CurrencyUnit source, CurrencyUnit target) {
		super("Currency mismatch: " + source + " != " + target);
		if (source == null || target == null) {
			throw new IllegalArgumentException(
					"Source or target currency may not be null.");
		}
		this.source = source;
		this.target = target;
	}

	/**
	 * Access the source {@link CurrencyUnit} instance.
	 * 
	 * @return the source currency, not {@code null}
	 */
	public CurrencyUnit getSource() {
		return source;
	}

	/**
	 * Access the target {@link CurrencyUnit} instance.
	 * 
	 * @return the target currency, not {@code null}
	 */
	public CurrencyUnit getTarget() {
		return target;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "CurrencyMismatchException [source=" + source + ", target="
				+ target + "]";
	}

}
