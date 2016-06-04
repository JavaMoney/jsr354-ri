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
package org.javamoney.moneta.internal.convert;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.ParseException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.logging.Logger;

import javax.money.CurrencyUnit;
import javax.money.convert.ConversionContextBuilder;
import javax.money.convert.ExchangeRate;
import javax.money.convert.ProviderContext;
import javax.money.convert.RateType;

import org.javamoney.moneta.convert.ExchangeRateBuilder;
import org.javamoney.moneta.spi.DefaultNumberValue;

class IMFRateReadingHandler {

	private static final Logger LOG = Logger
			.getLogger(IMFRateReadingHandler.class.getName());

	private final Map<String, CurrencyUnit> currenciresByName;

	private final ProviderContext context;

	IMFRateReadingHandler(Map<String, CurrencyUnit> currenciresByName, ProviderContext context) {
		this.currenciresByName = currenciresByName;
		this.context = context;
	}

	RateIMFResult read(InputStream inputStream) throws IOException,
			ParseException {
		Map<CurrencyUnit, List<ExchangeRate>> currencyToSdr = new HashMap<>();
		Map<CurrencyUnit, List<ExchangeRate>> sdrToCurrency = new HashMap<>();

		BufferedReader reader = new BufferedReader(new InputStreamReader(
				inputStream));
		String line = reader.readLine();
		boolean isCurrencyToSdr = true;
		// SDRs per Currency unit (2)
		//
		// Currency January 31, 2013 January 30, 2013 January 29, 2013
		// January 28, 2013 January 25, 2013
		// Euro 0.8791080000 0.8789170000 0.8742470000 0.8752180000
		// 0.8768020000

		// Currency units per SDR(3)
		//
		// Currency January 31, 2013 January 30, 2013 January 29, 2013
		// January 28, 2013 January 25, 2013
		// Euro 1.137520 1.137760 1.143840 1.142570 1.140510
		List<LocalDate> timestamps = null;
		while (Objects.nonNull(line)) {
			if (line.trim().isEmpty()) {
				line = reader.readLine();
				continue;
			}
			if (line.startsWith("SDRs per Currency unit")) {
				isCurrencyToSdr = false;
				line = reader.readLine();
				continue;
			} else if (line.startsWith("Currency units per SDR")) {
				isCurrencyToSdr = true;
				line = reader.readLine();
				continue;
			} else if (line.startsWith("Currency")) {
				timestamps = readTimestamps(line);
				line = reader.readLine();
				continue;
			}
			String[] parts = line.split("\\t");
			CurrencyUnit currency = currenciresByName.get(parts[0]);
			if (Objects.isNull(currency)) {
				LOG.finest(() -> "Uninterpretable data from IMF data feed: "
						+ parts[0]);
				line = reader.readLine();
				continue;
			}
			saveExchangeRate(currencyToSdr, sdrToCurrency, isCurrencyToSdr,
					timestamps, currency, parseValues(parts));
			line = reader.readLine();
		}
		// Cast is save, since contained DefaultExchangeRate is Comparable!
		sortResult(currencyToSdr, sdrToCurrency);
		return new RateIMFResult(currencyToSdr, sdrToCurrency);
	}

	@SuppressWarnings("unchecked")
	private void sortResult(
			Map<CurrencyUnit, List<ExchangeRate>> newCurrencyToSdr,
			Map<CurrencyUnit, List<ExchangeRate>> newSdrToCurrency) {

		newSdrToCurrency.values().forEach(
				(c) -> Collections.sort(List.class.cast(c)));
		newCurrencyToSdr.values().forEach(
				(c) -> Collections.sort(List.class.cast(c)));
		newSdrToCurrency.forEach((c, l) -> LOG.finest(() -> "SDR -> "
				+ c.getCurrencyCode() + ": " + l));
		newCurrencyToSdr.forEach((c, l) -> LOG.finest(() -> c
				.getCurrencyCode() + " -> SDR: " + l));
	}



	private List<LocalDate> readTimestamps(String line) {
		// Currency May 01, 2013 April 30, 2013 April 29, 2013 April 26, 2013
		// April 25, 2013
		DateTimeFormatter sdf = DateTimeFormatter.ofPattern("MMMM dd, uuuu")
				.withLocale(Locale.ENGLISH);
		String[] parts = line.split("\\\t");
		List<LocalDate> dates = new ArrayList<>(parts.length);
		for (int i = 1; i < parts.length; i++) {
			dates.add(LocalDate.parse(parts[i], sdf));
		}
		return dates;
	}

	private void saveExchangeRate(
			Map<CurrencyUnit, List<ExchangeRate>> currencyToSdr,
			Map<CurrencyUnit, List<ExchangeRate>> sdrToCurrency,
			boolean isCurrencyToSdr, List<LocalDate> timestamps,
			CurrencyUnit currency, Double[] values) {

		for (int index = 0; index < values.length; index++) {

			if (Objects.isNull(values[index])
					|| Objects.isNull(getLocalDateFromTS(timestamps, index))) {
				continue;
			}

			LocalDate fromTS = getLocalDateFromTS(timestamps, index);
			RateType rateType = getRateType(fromTS);

			if (isCurrencyToSdr) {
				ExchangeRate rate = new ExchangeRateBuilder(
						ConversionContextBuilder.create(context, rateType)
								.set(fromTS).build()).setBase(currency)
						.setTerm(IMFAbstractRateProvider.SDR)
						.setFactor(new DefaultNumberValue(1D / values[index]))
						.build();
				List<ExchangeRate> rates = currencyToSdr.computeIfAbsent(
						currency, c -> new ArrayList<>(5));
				rates.add(rate);
			} else {
				ExchangeRate rate = new ExchangeRateBuilder(
						ConversionContextBuilder.create(context, rateType)
								.set(fromTS).build()).setBase(IMFAbstractRateProvider.SDR)
						.setTerm(currency)
						.setFactor(DefaultNumberValue.of(1D / values[index]))
						.build();
				List<ExchangeRate> rates = sdrToCurrency.computeIfAbsent(
						currency, (c) -> new ArrayList<>(5));
				rates.add(rate);
			}
		}
	}

	private Double[] parseValues(String[] parts) throws ParseException {

		ArrayList<Double> result = new ArrayList<>();
		int index = 0;
		for (String part : parts) {
			if(index == 0) {
				index++;
				continue;
			}
			if (part.isEmpty() || "NA".equals(part)) {
				index++;
				result.add(null);
				continue;
			}
			index++;
			result.add(Double.valueOf(part.trim().replace(",", "")));
		}
		return result.toArray(new Double[parts.length - 1]);
	}

	private LocalDate getLocalDateFromTS(List<LocalDate> timestamps, int index) {
		LocalDate fromTS = timestamps != null ? timestamps.get(index) : null;
		return fromTS;
	}

	private RateType getRateType(LocalDate fromTS) {
		RateType rateType = RateType.HISTORIC;
		if (fromTS.equals(LocalDate.now())) {
			rateType = RateType.DEFERRED;
		}
		return rateType;
	}

	class RateIMFResult {

		private final Map<CurrencyUnit, List<ExchangeRate>> currencyToSdr;

		private final Map<CurrencyUnit, List<ExchangeRate>> sdrToCurrency;

		RateIMFResult(Map<CurrencyUnit, List<ExchangeRate>> currencyToSdr,
				Map<CurrencyUnit, List<ExchangeRate>> sdrToCurrency) {
			this.currencyToSdr = currencyToSdr;
			this.sdrToCurrency = sdrToCurrency;
		}

		public Map<CurrencyUnit, List<ExchangeRate>> getCurrencyToSdr() {
			return currencyToSdr;
		}

		public Map<CurrencyUnit, List<ExchangeRate>> getSdrToCurrency() {
			return sdrToCurrency;
		}

	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append(IMFRateReadingHandler.class.getName()).append('{').append(" currenciresByName: ").append(currenciresByName).append(',')
		.append(" context: ").append(context).append('}');
		return sb.toString();
	}
}
