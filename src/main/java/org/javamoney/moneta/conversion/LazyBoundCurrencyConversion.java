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

import javax.money.CurrencyUnit;
import javax.money.MonetaryAmount;
import javax.money.convert.ConversionContext;
import javax.money.convert.CurrencyConversion;
import javax.money.convert.ExchangeRate;
import javax.money.convert.ExchangeRateProvider;

/**
 * This class defines a {@link CurrencyConversion} that is converting to a
 * specific target {@link CurrencyUnit}. Each instance of this class is bound to
 * a specific {@link ExchangeRateProvider}, a term {@link CurrencyUnit} and a
 * target timestamp.
 * 
 * @author Anatole Tresch
 */
public class LazyBoundCurrencyConversion extends AbstractCurrencyConversion
		implements CurrencyConversion {

	private ExchangeRateProvider rateProvider;

	public LazyBoundCurrencyConversion(CurrencyUnit termCurrency,
			ExchangeRateProvider rateProvider,
			ConversionContext conversionContext) {
		super(termCurrency, conversionContext);
		this.rateProvider = rateProvider;
	}

	/**
	 * Get the exchange rate type that this provider instance is providing data
	 * for.
	 * 
	 * @return the exchange rate type if this instance.
	 */
	@Override
	public ExchangeRate getExchangeRate(MonetaryAmount amount) {
		return this.rateProvider.getExchangeRate(amount.getCurrency(),
				getTermCurrency(), getConversionContext());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.javamoney.moneta.conversion.AbstractCurrencyConversion#with(javax
	 * .money.convert.ConversionContext)
	 */
	public CurrencyConversion with(ConversionContext conversionContext) {
		return new LazyBoundCurrencyConversion(getTermCurrency(), rateProvider,
				conversionContext);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "CurrencyConversion [MonetaryAmount -> MonetaryAmount; provider="
				+ rateProvider
				+ ", context="
				+ getConversionContext()
				+ ", termCurrency=" + getTermCurrency() + "]";
	}

}
