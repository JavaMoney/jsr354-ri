/**
 * Copyright (c) 2012, 2015, Credit Suisse (Anatole Tresch), Werner Keil and others by the @author tag.
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

import static java.util.Objects.requireNonNull;

import javax.money.CurrencyUnit;
import javax.money.MonetaryAmount;
import javax.money.MonetaryOperator;

import org.javamoney.moneta.RoundedMoney;

/**
 * The default implementation to {@link MonetaryRoundedFactory}, this implementation returns the {@link RoundedMoney}.
 * @see {@link MonetaryRoundedFactory#create(Number, CurrencyUnit)}
 * @author Otavio Santana
 * @deprecated Do not use, access is only provided for backward compatibility and will be removed.
 */
@Deprecated
public class DefaultMonetaryRoundedFactory implements MonetaryRoundedFactory, org.javamoney.moneta.MonetaryRoundedFactory {

	private final MonetaryOperator roundingOperator;

	public DefaultMonetaryRoundedFactory(MonetaryOperator roundingOperator) {
		this.roundingOperator = roundingOperator;
	}

	@Override
	public MonetaryAmount create(Number number, CurrencyUnit currencyUnit) {
		return RoundedMoney.of(requireNonNull(number), requireNonNull(currencyUnit), roundingOperator);
	}

	@Override
	public MonetaryOperator getRoundingOperator() {
		return roundingOperator;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append(DefaultMonetaryRoundedFactory.class.getName()).append('{')
		.append("roundingOperator: ").append(roundingOperator).append('}');
		return sb.toString();
	}

}
