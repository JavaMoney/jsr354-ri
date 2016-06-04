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

import java.math.MathContext;
import java.util.Objects;
import java.util.function.Supplier;
import java.util.stream.Collector;

import javax.money.CurrencyUnit;
import javax.money.MonetaryAmount;
import javax.money.MonetaryOperator;
import javax.money.convert.ExchangeRateProvider;

import org.javamoney.moneta.convert.ExchangeCurrencyOperator;
import org.javamoney.moneta.function.MonetarySummaryStatistics;

/**
 * This singleton class provides access to the predefined monetary functions.
 * <p>
 * The class is thread-safe, which is also true for all functions returned by
 * this class.
 * <pre>
 * {@code
 * 	MonetaryAmount money = Money.parse("EUR 2.35");
 *  MonetaryAmount result = operator.apply(money);
 * }
 * </pre>
 * <p>Or using: </p>
 * <pre>
 * {@code
 * 	MonetaryAmount money = Money.parse("EUR 2.35");
 *  MonetaryAmount result = money.with(operator);
 * }
 * </pre>
 * @see {@link MonetaryAmount#with(MonetaryOperator)}
 * @see {@link MonetaryOperator}
 * @see {@link MonetaryOperator#apply(MonetaryAmount)}
 * @author Werner Keil
 * @since 1.0.1
 */
public final class ConversionOperators {

    private static final MathContext DEFAULT_MATH_CONTEXT = MathContext.DECIMAL64;

    private ConversionOperators() {
    }

	/**
	 * Do exchange of currency, in other words, create the monetary amount with the
	 * same value but with currency different.
	 * <p>
	 * For example, 'EUR 2.35', using the currency 'USD' as exchange parameter, will return 'USD 2.35',
	 * and 'BHD -1.345', using the currency 'USD' as exchange parameter, will return 'BHD -1.345'.
	 * <p>
	 *<pre>
	 *{@code
	 *Currency real = Monetary.getCurrency("BRL");
	 *MonetaryAmount money = Money.parse("EUR 2.355");
	 *MonetaryAmount result = ConversionOperators.exchangeCurrency(real).apply(money);//BRL 2.355
	 *}
	 *</pre>
	 * @param roundingMode rounding to be used
	 * @return the major part as {@link MonetaryOperator}
	 * @since 1.0.1
	 */
	public static MonetaryOperator exchange(CurrencyUnit currencyUnit){
		return new ExchangeCurrencyOperator(Objects.requireNonNull(currencyUnit));
	}
	
	/**
	 * of the summary of the MonetaryAmount
	 * @param currencyUnit
	 *            the target {@link javax.money.CurrencyUnit}
	 * @return the MonetarySummaryStatistics
	 */
	public static Collector<MonetaryAmount, MonetarySummaryStatistics, MonetarySummaryStatistics> summarizingMonetary(
			CurrencyUnit currencyUnit, ExchangeRateProvider provider) {

		Supplier<MonetarySummaryStatistics> supplier = () -> new ExchangeRateMonetarySummaryStatistics(
				currencyUnit, provider);
		return Collector.of(supplier, MonetarySummaryStatistics::accept,
				MonetarySummaryStatistics::combine);
	}
}
