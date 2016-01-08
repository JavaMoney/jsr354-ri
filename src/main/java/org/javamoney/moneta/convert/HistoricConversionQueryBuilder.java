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

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.function.Predicate;
import java.util.stream.Stream;

import javax.money.CurrencyUnit;
import javax.money.convert.ConversionQuery;
import javax.money.convert.ConversionQueryBuilder;
/**
 * Class builder to find exchange rate from historical.
 * @see {@link HistoricConversionQueryBuilder#of(CurrencyUnit)}
 * @author Otavio Santana
 * @deprecated
 */
@Deprecated
public final class HistoricConversionQueryBuilder {

	private final ConversionQueryBuilder conversionQueryBuilder;

	private HistoricConversionQueryBuilder(ConversionQueryBuilder conversionQuery) {
		this.conversionQueryBuilder = conversionQuery;
	}

	/**
	 *Create a {@link HistoricConversionQueryBuilder} from currency
	 * @param currencyUnit to be used in term currency.
	 * @return a HistoricConversionQuery from currency
	 * @throws NullPointerException when currency is null
	 */
	public static HistoricConversionQueryBuilder of(CurrencyUnit currencyUnit) {
		Objects.requireNonNull(currencyUnit, "Currency is required");
		return new HistoricConversionQueryBuilder(ConversionQueryBuilder.of()
                .setTermCurrency(currencyUnit));
	}

	/**
	 * Set a specify day on {@link HistoricConversionQueryBuilder}
	 * @param localDate
	 * @return this
	 * @throws NullPointerException when {@link LocalDate} is null
	 */
	public HistoricConversionQueryWithDayBuilder withDay(LocalDate localDate) {
		Objects.requireNonNull(localDate);
		conversionQueryBuilder.set(LocalDate.class, localDate);

		return new HistoricConversionQueryWithDayBuilder(conversionQueryBuilder);
	}

	/**
	 *Set days on {@link HistoricConversionQueryBuilder} to be used on ExchangeRateProvider,
	 *these parameters will sort to most recent to be more priority than other.
	 * @param localDates
	 * @return this
	 * @throws IllegalArgumentException when is empty or the parameter has an null value
	 */
	@SafeVarargs
	public final HistoricConversionQueryWithDayBuilder withDays(LocalDate... localDates) {
		Objects.requireNonNull(localDates);
		if(localDates.length == 0) {
			throw new IllegalArgumentException("LocalDates are required");
		}

		if(Stream.of(localDates).anyMatch(Predicate.isEqual(null))) {
			throw new IllegalArgumentException("LocalDates cannot be null");
		}
		Comparator<LocalDate> comparator = Comparator.naturalOrder();
		LocalDate[] sortedDates =  Stream.of(localDates).sorted(comparator.reversed()).toArray(LocalDate[]::new);
		conversionQueryBuilder.set(LocalDate[].class, sortedDates);

		return new HistoricConversionQueryWithDayBuilder(conversionQueryBuilder);
	}

	/**
	 *Set days on {@link HistoricConversionQueryBuilder} to be used on ExchangeRateProvider,
	 *these parameters, different of  {@link HistoricConversionQueryBuilder#withDays(LocalDate...)}, consider the order already defined.
	 * @param localDates
	 * @return this
	 * @throws IllegalArgumentException when is empty or the parameter has an null value
	 */
	@SafeVarargs
	public final HistoricConversionQueryWithDayBuilder withDaysPriorityDefined(LocalDate... localDates) {
		Objects.requireNonNull(localDates);
		if(localDates.length == 0) {
			throw new IllegalArgumentException("LocalDates are required");
		}

		if(Stream.of(localDates).anyMatch(Predicate.isEqual(null))) {
			throw new IllegalArgumentException("LocalDates cannot be null");
		}
		conversionQueryBuilder.set(LocalDate[].class, localDates);

		return new HistoricConversionQueryWithDayBuilder(conversionQueryBuilder);
	}

	/**
	 * Set the period of days on {@link HistoricConversionQueryBuilder}
	 *  to be used on ExchangeRateProvider,
	 * @param begin
	 * @param end
	 * @return this;
	 * <p>Example:</p>
	 * <pre>
	 * {@code
	 *LocalDate today = LocalDate.parse("2015-04-03");
	 *LocalDate yesterday = today.minusDays(1);
	 *LocalDate tomorrow = today.plusDays(1);
	 *ConversionQuery query = HistoricConversionQueryBuilder.of(real).onDaysBetween(yesterday, tomorrow).build();//the query with new LocalDate[] {tomorrow, today, yesterday}
	 * }
	 * </pre>
	 * @throws NullPointerException when either begin or end is null
	 * @throws IllegalArgumentException when the begin is bigger than end
	 */
	public final HistoricConversionQueryWithDayBuilder withDaysBetween(LocalDate begin, LocalDate end) {
		Objects.requireNonNull(begin);
		Objects.requireNonNull(end);
		if(end.isBefore(begin)) {
			throw new IllegalArgumentException("The end period should be bigger than the begin period.");
		}

		int days = (int) ChronoUnit.DAYS.between(begin, end);

		List<LocalDate> dates = new ArrayList<>();
		for(int index = days; index >= 0; index--) {
			dates.add(begin.plusDays(index));
		}
		conversionQueryBuilder.set(LocalDate[].class, dates.toArray(new LocalDate[dates.size()]));

		return new HistoricConversionQueryWithDayBuilder(conversionQueryBuilder);
	}

	/**
	 * Create the {@link ConversionQuery} just with {@link CurrencyUnit}, to term currency, already defined.
	 * @return the conversion query
	 */
	public ConversionQuery build() {
		return conversionQueryBuilder.build();
	}

	@Override
	public String toString() {
	    StringBuilder sb = new StringBuilder();
	    sb.append(HistoricConversionQueryBuilder.class.getName())
	    .append('{').append(" conversionQueryBuilder: ")
	    .append(conversionQueryBuilder).append('}');
		return sb.toString();
	}

	public class HistoricConversionQueryWithDayBuilder {

		private final ConversionQueryBuilder conversionQueryBuilder;

		HistoricConversionQueryWithDayBuilder(
				ConversionQueryBuilder conversionQueryBuilder) {
			this.conversionQueryBuilder = conversionQueryBuilder;
		}

		/**
		 * Create the {@link ConversionQuery} with {@link LocalDate} and {@link CurrencyUnit} to term currency already defined.
		 * @return the conversion query
		 */
		public ConversionQuery build() {
			return conversionQueryBuilder.build();
		}

		@Override
		public String toString() {
		    StringBuilder sb = new StringBuilder();
		    sb.append(HistoricConversionQueryWithDayBuilder.class.getName())
		    .append('{').append(" conversionQueryBuilder: ")
		    .append(conversionQueryBuilder).append('}');
			return sb.toString();
		}

	}

}
