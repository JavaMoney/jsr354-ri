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

import javax.money.MonetaryAmount;
import javax.money.MonetaryQuery;

/**
 * This class allows to extract the minor part of a {@link MonetaryAmount}
 * instance.
 *
 * @author Anatole Tresch
 * @author Otavio Santana
 */
final class ExtractorMinorPartQuery implements MonetaryQuery<Long> {

	/**
	 * Package private constructor used from MonetaryFunctions.
	 */
	ExtractorMinorPartQuery() {
	}

	/**
	 * Extract the minor part of a {@code MonetaryAmount} with the same scale.
	 * <p>
	 * This returns the monetary amount in terms of the minor units of the
	 * currency, truncating the whole part if necessary. For example, 'EUR 2.35'
	 * will return 35, and 'BHD -1.345' will return -345.
	 * <p>
	 * This is returned as a {@code MonetaryAmount} rather than a
	 * {@code BigDecimal} . This is to allow further calculations to be
	 * performed on the result. Should you need a {@code BigDecimal}, simply
	 * call {@code asType(BigDecimal.class)}.
	 * @return the minor units part of the amount, never {@code null}
	 */
	@Override
	public Long queryFrom(MonetaryAmount amount) {
		Objects.requireNonNull(amount, "Amount required.");
		int fractionDigits = amount.getCurrency().getDefaultFractionDigits();
		BigDecimal number = amount.getNumber().numberValue(BigDecimal.class);
		return number.setScale(fractionDigits, RoundingMode.DOWN)
		        .remainder(BigDecimal.ONE)
		        .movePointRight(fractionDigits).longValue();
	}

}
