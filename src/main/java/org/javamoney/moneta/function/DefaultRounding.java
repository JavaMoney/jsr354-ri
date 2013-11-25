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
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.Objects;

import javax.money.CurrencyUnit;
import javax.money.MonetaryOperator;
import javax.money.MonetaryAmount;

import org.javamoney.moneta.Money;
import org.javamoney.moneta.MoneyCurrency;

/**
 * Implementation class providing rounding {@link MonetaryOperator} instances
 * for {@link CurrencyUnit} instances. modeling rounding based on standard JDK
 * math, a scale and {@link RoundingMode}.
 * <p>
 * This class is thread safe.
 * 
 * @author Anatole Tresch
 * @author Werner Keil
 * @see RoundingMode
 */
final class DefaultRounding implements MonetaryOperator {

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
	DefaultRounding(int scale, RoundingMode roundingMode) {
		Objects.requireNonNull(roundingMode, "RoundingMode required.");
		if (scale < 0) {
			scale = 0;
		}
		this.scale = scale;
		this.roundingMode = roundingMode;
	}

	/**
	 * Creates an {@link DefaultRounding} for rounding {@link MonetaryAmount}
	 * instances given a currency.
	 * 
	 * @param currency
	 *            The currency, which determines the required precision. As
	 *            {@link RoundingMode}, by default, {@link RoundingMode#HALF_UP}
	 *            is sued.
	 * @return a new instance {@link MonetaryOperator} implementing the
	 *         rounding.
	 */
	DefaultRounding(CurrencyUnit currency,
			RoundingMode roundingMode) {
		this(MoneyCurrency.of(currency.getCurrencyCode())
				.getDefaultFractionDigits(), roundingMode);
	}

	/**
	 * Creates an {@link MonetaryOperator} for rounding {@link MonetaryAmount}
	 * instances given a currency.
	 * 
	 * @param currency
	 *            The currency, which determines the required precision. As
	 *            {@link RoundingMode}, by default, {@link RoundingMode#HALF_UP}
	 *            is sued.
	 * @return a new instance {@link MonetaryOperator} implementing the
	 *         rounding.
	 */
	DefaultRounding(CurrencyUnit currency) {
		this(MoneyCurrency.of(currency.getCurrencyCode())
				.getDefaultFractionDigits(), RoundingMode.HALF_UP);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.money.MonetaryFunction#apply(java.lang.Object)
	 */
	@Override
	public MonetaryAmount apply(MonetaryAmount amount) {
		return Money.of(amount.getCurrency(), Money.from(amount).asType(BigDecimal.class).setScale(
				this.scale,
				this.roundingMode));
	}

}
