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
package org.javamoney.moneta.convert;

import java.util.Comparator;
import java.util.Objects;
import java.util.function.BinaryOperator;
import java.util.function.Supplier;
import java.util.stream.Collector;

import javax.money.CurrencyUnit;
import javax.money.MonetaryAmount;
import javax.money.MonetaryOperator;
import javax.money.convert.CurrencyConversion;
import javax.money.convert.ExchangeRate;
import javax.money.convert.ExchangeRateProvider;

import org.javamoney.moneta.function.MonetarySummaryStatistics;
import org.javamoney.moneta.spi.MoneyUtils;

/**
 * This singleton class provides access to the predefined conversion operators.
 *
 * @author Werner Keil
 */
public final class ConversionOperators {

    /**
     * Private constructor for static accessor class.
     */
    private ConversionOperators(){}
    
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
	 *MonetaryAmount result = MonetaryOperators.exchangeCurrency(real).apply(money);//BRL 2.355
	 *}
	 *</pre>
	 * @param roundingMode rounding to be used
	 * @return the major part as {@link MonetaryOperator}
	 */
	public static MonetaryOperator exchange(CurrencyUnit currencyUnit){
		return new ExchangeCurrencyOperator(Objects.requireNonNull(currencyUnit));
	}
	
    /**
	 * comparator to sort the {@link MonetaryAmount} considering the
	 * {@link ExchangeRate}
	 * @param provider the rate provider to be used, not null.
	 * @return the sort of {@link MonetaryAmount} using {@link ExchangeRate}
	 */
	public static Comparator<? super MonetaryAmount> sortValuable(
			ExchangeRateProvider provider) {

		return (m1, m2) -> {
			CurrencyConversion conversion = provider.getCurrencyConversion(m1
					.getCurrency());
			return m1.compareTo(conversion.apply(m2));
		};
	}

	/**
	 * Descending order of
	 * {@link ConversionOperators#sortValuable(ExchangeRateProvider)}
	 * @param provider the rate provider to be used, not null.
	 * @return the Descending order of
	 *         {@link ConversionOperators#sortValuable(ExchangeRateProvider)}
	 */
	public static Comparator<? super MonetaryAmount> sortValuableDesc(
			ExchangeRateProvider provider) {
		return sortValuable(provider).reversed();
	}

    /**
     * Returns the smaller of two {@code MonetaryAmount} values. If the arguments
     * have the same value, the result is that same value.
     * @param a an argument.
     * @param b another argument.
     * @return the smaller of {@code a} and {@code b}.
     */
	static MonetaryAmount min(MonetaryAmount a, MonetaryAmount b) {
        MoneyUtils.checkAmountParameter(Objects.requireNonNull(a), Objects.requireNonNull(b.getCurrency()));
        return a.isLessThan(b) ? a : b;
    }

    /**
     * Returns the greater of two {@code MonetaryAmount} values. If the
     * arguments have the same value, the result is that same value.
     * @param a an argument.
     * @param b another argument.
     * @return the larger of {@code a} and {@code b}.
     */
	static MonetaryAmount max(MonetaryAmount a, MonetaryAmount b) {
        MoneyUtils.checkAmountParameter(Objects.requireNonNull(a), Objects.requireNonNull(b.getCurrency()));
        return a.isGreaterThan(b) ? a : b;
    }

	/**
	 * return the sum and convert all values to specific currency using the
	 * provider, if necessary
	 * @param provider the rate provider to be used, not null.
	 * @param currency
	 *            currency
	 * @return the list convert to specific currency unit
	 */
	public static BinaryOperator<MonetaryAmount> sum(
			ExchangeRateProvider provider, CurrencyUnit currency) {
		CurrencyConversion currencyConversion = provider
				.getCurrencyConversion(currency);

		return (m1, m2) -> currencyConversion.apply(m1).add(
				currencyConversion.apply(m2));
	}

    /**
	 * return the minimum value, if the monetary amounts have different
	 * currencies, will converter first using the given ExchangeRateProvider
	 * @param provider
	 *            the ExchangeRateProvider to convert the currencies
	 * @return the minimum value
	 */
	public static BinaryOperator<MonetaryAmount> min(
			ExchangeRateProvider provider) {

		return (m1, m2) -> {
			CurrencyConversion conversion = provider.getCurrencyConversion(m1
					.getCurrency());

			if (m1.isGreaterThan(conversion.apply(m2))) {
				return m2;
			}
			return m1;
		};
	}

    /**
	 * return the maximum value, if the monetary amounts have different
	 * currencies, will converter first using the given ExchangeRateProvider
	 * @param provider
	 *            the ExchangeRateProvider to convert the currencies
	 * @return the maximum value
	 */
	public static BinaryOperator<MonetaryAmount> max(
			ExchangeRateProvider provider) {

		return (m1, m2) -> {
			CurrencyConversion conversion = provider
					.getCurrencyConversion(m1.getCurrency());

			if (m1.isGreaterThan(conversion.apply(m2))) {
				return m1;
			}
			return m2;
		};
	}

	/**
	 * of the summary of the MonetaryAmount
	 * @param currencyUnit
	 *            the target {@link javax.money.CurrencyUnit}
	 * @return the MonetarySummaryStatistics
	 */
	public static Collector<MonetaryAmount, MonetarySummaryStatistics, MonetarySummaryStatistics> exchangeSummary(
			CurrencyUnit currencyUnit, ExchangeRateProvider provider) {

		Supplier<MonetarySummaryStatistics> supplier = () -> new ExchangeRateMonetarySummaryStatistics(
				currencyUnit, provider);
		return Collector.of(supplier, MonetarySummaryStatistics::accept,
				MonetarySummaryStatistics::combine);
	}
}