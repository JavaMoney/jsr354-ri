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
package org.javamoney.moneta.spi;

import java.util.Objects;

import javax.money.CurrencyUnit;
import javax.money.MonetaryAmount;
import javax.money.MonetaryOperator;

import org.javamoney.moneta.RoundedMoney;
import org.javamoney.moneta.function.MonetaryOperators;

/**
 * The implementation of {@link MonetaryAmountProducer} that creates {@link MonetaryAmount}
 * using {@link RoundedMoney} using the {@link MonetaryOperator} as rounding operator
 * @see {@link RoundedMoneyProducer#RoundedMoneyProducer(MonetaryOperator)}
 * @author Otavio Santana
 */
public final class RoundedMoneyProducer implements MonetaryAmountProducer {

	private final MonetaryOperator operator;

	/**
	 * Creates this producer using this operator
	 * as rounding operator in all MonetaryAmount produced.
	 * @param operator
	 * @throws NullPointerException if operator is null
	 */
	public RoundedMoneyProducer(MonetaryOperator operator) {
		this.operator = Objects.requireNonNull(operator);
	}

	/**
	 * Returns the {@link MonetaryAmountProducer} that creates {@link MonetaryAmount}
	 * using the {@link RoundedMoney} implementation using {@link MonetaryOperators#rounding()}
	 * as rounding operator
	 * @see {@link RoundedMoneyProducer}
	 * @return the rounded money producer
	 */
	public RoundedMoneyProducer() {
		this.operator = MonetaryOperators.rounding();
	}

	@Override
	public MonetaryAmount create(CurrencyUnit currency, Number number) {
		return RoundedMoney.of(Objects.requireNonNull(number), Objects.requireNonNull(currency), operator);
	}

	public MonetaryOperator getOperator() {
		return operator;
	}

}
