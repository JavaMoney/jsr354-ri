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

import java.math.MathContext;
import java.math.RoundingMode;
import java.util.Objects;

import javax.money.CurrencyUnit;
import javax.money.MonetaryAdjuster;
import javax.money.MonetaryAmount;

import org.javamoney.moneta.MoneyCurrency;

/**
 * Implementation class providing cash rounding {@link MonetaryAdjuster}
 * instances for {@link CurrencyUnit} instances. modeling rounding based on
 * {@link CurrencyUnit#getCashRounding()}.
 * <p>
 * This class is thread safe.
 * 
 * @author Anatole Tresch
 */
final class DefaultCashRounding implements
		MonetaryAdjuster {

	/** The {@link RoundingMode} used. */
	private final RoundingMode roundingMode;
	/** The scale to be applied. */
	private final int scale;

	/**
	 * Creates an rounding instance.
	 * 
	 * @param mathContext
	 *            The {@link MathContext} to be used, not {@code null}.
	 */
	DefaultCashRounding(int scale, RoundingMode roundingMode) {
		if (scale < 0) {
			throw new IllegalArgumentException("scale < 0");
		}
		if (roundingMode == null) {
			throw new IllegalArgumentException("roundingMode missing");
		}
		this.scale = scale;
		this.roundingMode = roundingMode;
	}

	/**
	 * Creates an {@link DefaultCashRounding} for rounding
	 * {@link MonetaryAmount} instances given a currency.
	 * 
	 * @param currency
	 *            The currency, which determines the required precision. As
	 *            {@link RoundingMode}, by default, {@link RoundingMode#HALF_UP}
	 *            is sued.
	 * @return a new instance {@link MonetaryAdjuster} implementing the
	 *         rounding.
	 */
	DefaultCashRounding(CurrencyUnit currency,
			RoundingMode roundingMode) {
		this(MoneyCurrency.of(currency.getCurrencyCode())
				.getDefaultFractionDigits(), roundingMode);
	}

	/**
	 * Creates an {@link MonetaryAdjuster} for rounding {@link MonetaryAmount}
	 * instances given a currency.
	 * 
	 * @param currency
	 *            The currency, which determines the required precision. As
	 *            {@link RoundingMode}, by default, {@link RoundingMode#HALF_UP}
	 *            is sued.
	 * @return a new instance {@link MonetaryAdjuster} implementing the
	 *         rounding.
	 */
	DefaultCashRounding(CurrencyUnit currency) {
		this(MoneyCurrency.of(currency.getCurrencyCode())
				.getDefaultFractionDigits(), RoundingMode.HALF_UP);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.money.MonetaryFunction#apply(java.lang.Object)
	 */
	@Override
	public MonetaryAmount adjustInto(MonetaryAmount value) {
		Objects.requireNonNull(value, "Amunt required.");
		throw new UnsupportedOperationException(
				"Cash Rounding not yet implemented.");
	}

}
