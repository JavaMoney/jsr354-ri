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
import java.math.RoundingMode;
import java.util.Objects;

import javax.money.MonetaryOperator;
import javax.money.MonetaryAmount;

import org.javamoney.moneta.Money;

/**
 * This class allows to extract the minor part of a {@link MonetaryAmount}
 * instance.
 * 
 * @author Anatole Tresch
 * @author Werner Keil
 */
final class MinorPart<T extends MonetaryAmount> implements MonetaryOperator {

	/**
	 * Private constructor, there is only one instance of this class, accessible
	 * calling {@link #of()}.
	 */
	MinorPart() {
	}

	/**
	 * Gets the minor part of a {@code MonetaryAmount} with the same scale.
	 * <p>
	 * This returns the monetary amount in terms of the minor units of the
	 * currency, truncating the whole part if necessary. For example, 'EUR 2.35'
	 * will return 'EUR 0.35', and 'BHD -1.345' will return 'BHD -0.345'.
	 * <p>
	 * This is returned as a {@code MonetaryAmount} rather than a
	 * {@code BigDecimal} . This is to allow further calculations to be
	 * performed on the result. Should you need a {@code BigDecimal}, simply
	 * call {@code asType(BigDecimal.class)}.
	 * 
	 * @return the minor units part of the amount, never {@code null}
	 * 
	 * TODO why do we show decimal number here, a result like "ct 35" seems more appropriate, but we need to find a way of handling subunits
	 */
	@Override
	public MonetaryAmount apply(MonetaryAmount amount) {
		Objects.requireNonNull(amount, "Amount required.");
		BigDecimal number = Money.from(amount).asType(BigDecimal.class);
		BigDecimal wholes = number.setScale(0, RoundingMode.DOWN);
		return Money.of(amount.getCurrency(),
				number.subtract(wholes));
	}

}
