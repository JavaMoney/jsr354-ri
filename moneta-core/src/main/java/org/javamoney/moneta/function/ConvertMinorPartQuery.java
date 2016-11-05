/**
 * Copyright (c) 2012, 2014, Credit Suisse (Anatole Tresch), Werner Keil and others by the @author tag.
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
import java.math.RoundingMode;
import java.util.Objects;

import javax.money.CurrencyUnit;
import javax.money.MonetaryAmount;
import javax.money.MonetaryQuery;

/**
 * This class allows to convert to minor part a {@link MonetaryAmount}
 * instance.
 *  Recovery all value as minor units as a {@code long}.
 * <p>
 * This returns the monetary amount in terms of the minor units of the
 * currency, truncating the amount if necessary. For example, 'EUR 2.35'
 * will return 235, and 'BHD -1.345' will return -1345.
 * <p>
 * @author Anatole Tresch
 */
final class ConvertMinorPartQuery implements MonetaryQuery<Long> {

	/**
	 * Private constructor, there is only one instance of this class, accessible
	 * calling {@link ConversionOperators#minorUnits()} ()}.
	 */
	ConvertMinorPartQuery() {
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
		Objects.requireNonNull(amount, "Amount required.");
		BigDecimal number = amount.getNumber().numberValue(BigDecimal.class);
		CurrencyUnit cur = amount.getCurrency();
		int scale = cur.getDefaultFractionDigits();
		if(scale<0){
			scale = 0;
		}
		number = number.setScale(scale, RoundingMode.DOWN);
		return number.movePointRight(number.scale()).longValueExact();
	}

}
