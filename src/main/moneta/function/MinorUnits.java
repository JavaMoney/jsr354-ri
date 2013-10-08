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

import javax.money.MonetaryAmount;
import javax.money.MonetaryAdjuster;
import javax.money.MonetaryQuery;

import org.javamoney.moneta.Money;

/**
 * This class allows to extract the minor part of a {@link MonetaryAmount}
 * instance.
 * 
 * @author Anatole Tresch
 */
final class MinorUnits implements MonetaryQuery<Long> {

	/**
	 * Private constructor, there is only one instance of this class, accessible
	 * calling {@link #of()}.
	 */
	MinorUnits() {
	}

	/**
	 * Gets the amount in minor units as a {@code long}.
	 * <p>
	 * This returns the monetary amount in terms of the minor units of the
	 * currency, truncating the amount if necessary. For example, 'EUR 2.35'
	 * will return 235, and 'BHD -1.345' will return -1345.
	 * <p>
	 * This method matches the API of {@link java.math.BigDecimal}.
	 * 
	 * @return the minor units part of the amount
	 * @throws ArithmeticException
	 *             if the amount is too large for a {@code long}
	 */
	@Override
	public Long queryFrom(MonetaryAmount amount) {
		if (amount == null) {
			throw new IllegalArgumentException("Amount required.");
		}
		BigDecimal number = Money.from(amount).asType(BigDecimal.class);
		return number.movePointRight(number.precision()).longValueExact();
	}

}
