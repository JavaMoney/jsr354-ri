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
import java.util.Objects;

import javax.money.CurrencyUnit;
import javax.money.MonetaryAmount;
import javax.money.MonetaryOperator;
import javax.money.convert.ConversionContext;
import javax.money.convert.CurrencyConversion;
import javax.money.convert.CurrencyConversionException;
import javax.money.convert.ExchangeRate;
import javax.money.convert.ProviderContext;

/**
 * Abstract base class used for implementing currency conversion.
 * 
 * @author Anatole Tresch
 * @author Werner Keil
 */
public abstract class AbstractCurrencyConversion implements CurrencyConversion {

	private CurrencyUnit termCurrency;
	private ConversionContext conversionContext;

	public AbstractCurrencyConversion(CurrencyUnit termCurrency,
			ConversionContext conversionContext) {
		Objects.requireNonNull(termCurrency);
		Objects.requireNonNull(conversionContext);
		this.termCurrency = termCurrency;
		this.conversionContext = conversionContext;
	}

	/**
	 * Access the terminating {@link CurrencyUnit} of this conversion instance.
	 * 
	 * @return the terminating {@link CurrencyUnit} , never {@code null}.
	 */
	public CurrencyUnit getTermCurrency() {
		return termCurrency;
	}

	/**
	 * Access the target {@link ConversionContext} of this conversion instance.
	 * 
	 * @return the target {@link ConversionContext}.
	 */
	public ConversionContext getConversionContext() {
		return conversionContext;
	}

	/**
	 * Get the exchange rate type that this {@link MonetaryOperator} instance is
	 * using for conversion.
	 * 
	 * @see #apply(MonetaryAmount)
	 * @return the {@link ExchangeRate} to be used, or null, if this conversion
	 *         is not supported (will lead to a
	 *         {@link CurrencyConversionException}.
	 */
	@Override
	public abstract ExchangeRate getExchangeRate(MonetaryAmount amount);

	/**
	 * Method that converts the source {@link MonetaryAmount} to an
	 * {@link MonetaryAmount} based on the {@link ExchangeRate} of this
	 * conversion.<br/>
	 * 
	 * @see #getExchangeRate(MonetaryAmount)
	 * @param amount
	 *            The source amount
	 * @return The converted amount, never null.
	 * @throws CurrencyConversionException
	 *             if conversion failed, or the required data is not available.
	 */
	// safe conversion due to MonetaryAmount contract
	@Override
	@SuppressWarnings("unchecked")
	public <T extends MonetaryAmount> T apply(T amount) {
		ExchangeRate rate = getExchangeRate(amount);
		if (rate == null || !amount.getCurrency().equals(rate.getBase())) {
			throw new CurrencyConversionException(amount.getCurrency(),
					rate == null ? null : rate.getTerm(), null);
		}
		return (T) amount
				.getFactory()
				.setCurrency(rate.getTerm())
				.setNumber(
						amount.multiply(rate.getFactor()).getNumber()
								.numberValue(BigDecimal.class)).create();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return getClass().getName() + " [MonetaryAmount -> MonetaryAmount"
				+ "]";
	}

}
