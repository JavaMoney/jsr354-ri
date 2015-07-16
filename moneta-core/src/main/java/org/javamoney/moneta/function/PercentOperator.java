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

import static java.text.NumberFormat.getPercentInstance;

import java.math.BigDecimal;
import java.math.MathContext;
import java.util.Objects;

import javax.money.MonetaryAmount;
import javax.money.MonetaryOperator;

/**
 * This class allows to extract the percentage of a {@link MonetaryAmount}
 * instance.
 *
 * @version 0.5
 * @author Werner Keil
 *
 * @see <a href="http://en.wikipedia.org/wiki/Percent">Wikipedia: Percentage</a>
 */
final class PercentOperator implements MonetaryOperator {

	private static final BigDecimal ONE_HUNDRED = new BigDecimal(100,
			MathContext.DECIMAL64);

	private final BigDecimal percentValue;

	/**
	 * Access the shared instance of {@link PercentOperator} for use.
	 */
	PercentOperator(final BigDecimal decimal) {
		percentValue = calcPercent(decimal);
	}

	/**
	 * Gets the percentage of the amount.
	 * <p>
	 * This returns the monetary amount in percent. For example, for 10% 'EUR
	 * 2.35' will return 0.235.
	 * <p>
	 * This is returned as a {@code MonetaryAmount}.
	 *
	 * @return the percent result of the amount, never {@code null}
	 */
	@Override
	public MonetaryAmount apply(MonetaryAmount amount) {
		Objects.requireNonNull(amount, "Amount required.");
		return amount.multiply(percentValue);
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return getPercentInstance().format(percentValue);
	}

	/**
	 * Calculate a BigDecimal value for a Percent e.g. "3" (3 percent) will
	 * generate .03
	 *
	 * @return java.math.BigDecimal
	 * @param decimal
	 *            java.math.BigDecimal
	 */
	private static BigDecimal calcPercent(BigDecimal decimal) {
		return decimal.divide(ONE_HUNDRED, MathContext.DECIMAL64); // we now
																	// have
	}

}
