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
import java.util.Objects;

import javax.money.MonetaryAmount;
import javax.money.MonetaryOperator;

/**
 * This class allows to extract the reciprocal value (multiplcative inversion)
 * of a {@link MonetaryAmount} instance.
 * 
 * @author Anatole Tresch
 */
final class Reciprocal implements MonetaryOperator {

	/**
	 * Access the shared instance of {@link Reciprocal} for use.
	 * 
	 * @return the shared instance, never {@code null}.
	 */
	Reciprocal() {
	}

	/**
	 * Gets the amount as reciprocal / multiplcative inversed value (1/n).
	 * <p>
	 * E.g. 'EUR 2.0' will be converted to 'EUR 0.5'.
	 * 
	 * @return
	 * 
	 * @return the reciprocal / multiplcative inversed of the amount
	 * @throws ArithmeticException
	 *             if the arithmetic operation failed
	 */
	// unchecked cast {@code (T)amount.with(MonetaryOperator)} is
	// safe, if the operator is implemented as specified by this JSR.
	@SuppressWarnings("unchecked")
	@Override
	public <T extends MonetaryAmount> T apply(T amount) {
		Objects.requireNonNull(amount, "Amount required.");
		return (T)amount.getFactory().setNumber(BigDecimal.ONE.divide(
				amount.getNumber().numberValue(BigDecimal.class))).create();
	}

}
