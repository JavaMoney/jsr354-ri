/**
 * Copyright (c) 2012, 2015, Anatole Tresch, Werner Keil and others by the @author tag.
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
package org.javamoney.moneta.convert;

import java.util.Objects;

import javax.money.CurrencyUnit;
import javax.money.MonetaryAmount;
import javax.money.MonetaryOperator;

/**
 * MonetaryOperator class that applies an exchange rate to an amount.
 * @deprecated
 */
@Deprecated
public class ExchangeCurrencyOperator implements MonetaryOperator {
	/** The target currency. */
	private final CurrencyUnit currency;

	/**
	 * Constrcutor.
	 * @param currency the target currency, not null.
     */
	ExchangeCurrencyOperator(CurrencyUnit currency) {
		this.currency = Objects.requireNonNull(currency);
	}

	@Override
	public MonetaryAmount apply(MonetaryAmount amount) {
		Objects.requireNonNull(amount, "Amount required.");
		return amount.getFactory().setCurrency(currency).create();
	}

}
