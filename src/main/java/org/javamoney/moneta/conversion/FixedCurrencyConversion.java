/*
 * Copyright (c) 2012, 2013, Credit Suisse (Anatole Tresch), Werner Keil. Licensed under the Apache
 * License, Version 2.0 (the "License"); you may not use this file except in compliance with the
 * License. You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */
package org.javamoney.moneta.conversion;

import java.math.BigDecimal;

import javax.money.CurrencyUnit;
import javax.money.MonetaryAmount;
import javax.money.convert.CurrencyConversionException;
import javax.money.convert.ExchangeRate;

/**
 * This interface defines access to the exchange conversion logic of JavaMoney. It is provided by
 * the Money singleton. It is provided by the Money singleton.
 * 
 * @author Anatole Tresch
 */
public class FixedCurrencyConversion extends AbstractCurrencyConversion {

	private ExchangeRate rate;

	public FixedCurrencyConversion(ExchangeRate rate) {
		super(rate.getTerm(), rate.getConversionContext());
		this.rate = rate;
	}

	@Override
	public CurrencyUnit getTermCurrency() {
		return this.rate.getTerm();
	}

	/**
	 * Get the exchange rate type that this provider instance is providing data for.
	 * 
	 * @return the {@link ExchangeRateType} if this instance.
	 */
	@Override
	public ExchangeRate getExchangeRate(MonetaryAmount amount) {
		if (!amount.getCurrency().equals(this.rate.getBase())) {
			throw new CurrencyConversionException(amount.getCurrency(),
					rate.getTerm(), null);
		}
		return this.rate;
	}

	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "FixedCurrencyConversion [MonetaryAmount -> MonetaryAmount; rate="
				+ rate + "]";
	}

	// Save due to MonetaryAmount contract
	@SuppressWarnings("unchecked")
	@Override
	public <T extends MonetaryAmount> T apply(T amount) {
		ExchangeRate rate = getExchangeRate(amount);
		return (T) amount.getFactory().setCurrency(rate.getTerm())
				.setNumber(amount.getNumber().numberValue(BigDecimal.class)
						.multiply(rate.getFactor())).create();
	}
}
