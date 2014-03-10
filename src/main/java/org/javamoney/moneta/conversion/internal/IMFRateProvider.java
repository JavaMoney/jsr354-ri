/*
 * Copyright (c) 2012, 2014, Credit Suisse (Anatole Tresch), Werner Keil.
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
 * 
 * Contributors: Anatole Tresch - initial implementation.
 */
package org.javamoney.moneta.conversion.internal;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Currency;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.logging.Level;

import javax.money.CurrencyUnit;
import javax.money.MonetaryCurrencies;
import javax.money.convert.ConversionContext;
import javax.money.convert.ExchangeRate;
import javax.money.convert.ExchangeRateProvider;
import javax.money.convert.ProviderContext;
import javax.money.convert.RateType;
import javax.money.spi.Bootstrap;

import org.javamoney.moneta.BuildableCurrencyUnit;
import org.javamoney.moneta.spi.AbstractRateProvider;
import org.javamoney.moneta.spi.DefaultNumberValue;
import org.javamoney.moneta.spi.LoaderService;
import org.javamoney.moneta.spi.LoaderService.LoaderListener;

/**
 * Implements a {@link ExchangeRateProvider} that loads the IMF conversion data.
 * In most cases this provider will provide chained rates, since IMF always is
 * converting from/to the IMF <i>SDR</i> currency unit.
 * 
 * @author Anatole Tresch
 * @author Werner Keil
 */
public class IMFRateProvider extends AbstractRateProvider implements
		LoaderListener {

	/** The data id used for the LoaderService. */
	private static final String DATA_ID = IMFRateProvider.class.getSimpleName();
	/** The {@link ConversionContext} of this provider. */
	private static final ProviderContext CONTEXT = new ProviderContext.Builder(
			"IMF").setRateTypes(RateType.DEFERRED)
			.set("Internation Monetary Fond", "providerDescription")
			.set(1, "days").create();

	private static final CurrencyUnit SDR = new BuildableCurrencyUnit.Builder(
			"SDR").setDefaultFractionDigits(3).build(true);

	private Map<CurrencyUnit, List<ExchangeRate>> currencyToSdr = new HashMap<CurrencyUnit, List<ExchangeRate>>();

	private Map<CurrencyUnit, List<ExchangeRate>> sdrToCurrency = new HashMap<CurrencyUnit, List<ExchangeRate>>();

	private static Map<String, CurrencyUnit> currenciesByName = new HashMap<String, CurrencyUnit>();

	static {
		for (Currency currency : Currency.getAvailableCurrencies()) {
			currenciesByName.put(currency.getDisplayName(Locale.ENGLISH),
					MonetaryCurrencies.getCurrency(currency.getCurrencyCode()));
		}
		// Additional IMF differing codes:
		// TODO i18n?
		currenciesByName.put("U.K. Pound Sterling",
				MonetaryCurrencies.getCurrency("GBP"));
		currenciesByName.put("U.S. Dollar",
				MonetaryCurrencies.getCurrency("USD"));
		currenciesByName.put("Bahrain Dinar",
				MonetaryCurrencies.getCurrency("BHD"));
		currenciesByName.put("Botswana Pula",
				MonetaryCurrencies.getCurrency("BWP"));
		currenciesByName.put("Czech Koruna",
				MonetaryCurrencies.getCurrency("CZK"));
		currenciesByName.put("Icelandic Krona",
				MonetaryCurrencies.getCurrency("ISK"));
		currenciesByName.put("Korean Won",
				MonetaryCurrencies.getCurrency("KRW"));
		currenciesByName.put("Rial Omani",
				MonetaryCurrencies.getCurrency("OMR"));
		currenciesByName
				.put("Nuevo Sol", MonetaryCurrencies.getCurrency("PEN"));
		currenciesByName.put("Qatar Riyal",
				MonetaryCurrencies.getCurrency("QAR"));
		currenciesByName.put("Saudi Arabian Riyal",
				MonetaryCurrencies.getCurrency("SAR"));
		currenciesByName.put("Sri Lanka Rupee",
				MonetaryCurrencies.getCurrency("LKR"));
		currenciesByName.put("Trinidad And Tobago Dollar",
				MonetaryCurrencies.getCurrency("TTD"));
		currenciesByName.put("U.A.E. Dirham",
				MonetaryCurrencies.getCurrency("AED"));
		currenciesByName.put("Peso Uruguayo",
				MonetaryCurrencies.getCurrency("UYU"));
		currenciesByName.put("Bolivar Fuerte",
				MonetaryCurrencies.getCurrency("VEF"));
	}

	public IMFRateProvider() throws MalformedURLException {
		super(CONTEXT);
		LoaderService loader = Bootstrap.getService(LoaderService.class);
		loader.addLoaderListener(this, DATA_ID);
		loader.loadDataAsync(DATA_ID);
	}

	@Override
	public void newDataLoaded(String data, InputStream is) {
		try {
			loadRatesTSV(is);
		} catch (Exception e) {
			LOGGER.log(Level.SEVERE, "Error", e);
		}
	}

	private void loadRatesTSV(InputStream inputStream) throws IOException,
			ParseException {
		Map<CurrencyUnit, List<ExchangeRate>> newCurrencyToSdr = new HashMap<CurrencyUnit, List<ExchangeRate>>();
		Map<CurrencyUnit, List<ExchangeRate>> newSdrToCurrency = new HashMap<CurrencyUnit, List<ExchangeRate>>();
		NumberFormat f = new DecimalFormat("#0.0000000000");
		f.setGroupingUsed(false);
		BufferedReader pr = new BufferedReader(new InputStreamReader(
				inputStream));
		String line = pr.readLine();
		// int lineType = 0;
		boolean currencyToSdr = true;
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
		List<Long> timestamps = null;
		while (line != null) {
			if (line.trim().isEmpty()) {
				line = pr.readLine();
				continue;
			}
			if (line.startsWith("SDRs per Currency unit")) {
				currencyToSdr = false;
				line = pr.readLine();
				continue;
			} else if (line.startsWith("Currency units per SDR")) {
				currencyToSdr = true;
				line = pr.readLine();
				continue;
			} else if (line.startsWith("Currency")) {
				timestamps = readTimestamps(line);
				line = pr.readLine();
				continue;
			}
			String[] parts = line.split("\\t");
			CurrencyUnit currency = currenciesByName.get(parts[0]);
			if (currency == null) {
				LOGGER.warning("Unknown currency from, IMF data feed: "
						+ parts[0]);
				line = pr.readLine();
				continue;
			}
			Double[] values = parseValues(f, parts);
			for (int i = 0; i < values.length; i++) {
				if (values[i] == null) {
					continue;
				}
				Long fromTS = timestamps.get(i);
				Long toTS = fromTS + 3600L * 1000L * 24L; // One day
				RateType rateType = RateType.HISTORIC;
				if (toTS > System.currentTimeMillis()) {
					rateType = RateType.DEFERRED;
				}
				if (currencyToSdr) { // Currency -> SDR
					List<ExchangeRate> rates = this.currencyToSdr.get(currency);
					if (rates == null) {
						rates = new ArrayList<ExchangeRate>(5);
						newCurrencyToSdr.put(currency, rates);
					}
					ExchangeRate rate = new ExchangeRate.Builder(
							ConversionContext.of(CONTEXT.getProviderName(),
									rateType, toTS)).setBase(currency)
							.setTerm(SDR).setFactor(new DefaultNumberValue(values[i])).create();
					rates.add(rate);
				} else { // SDR -> Currency
					List<ExchangeRate> rates = this.sdrToCurrency.get(currency);
					if (rates == null) {
						rates = new ArrayList<ExchangeRate>(5);
						newSdrToCurrency.put(currency, rates);
					}
					ExchangeRate rate = new ExchangeRate.Builder(
							ConversionContext.of(CONTEXT.getProviderName(),
									rateType, fromTS)).setBase(SDR)
							.setTerm(currency).setFactor(DefaultNumberValue.of(values[i])).create();
					rates.add(rate);
				}
			}
			line = pr.readLine();
		}
		for (List<ExchangeRate> rateList : newSdrToCurrency.values()) {
			Collections.sort(rateList);
		}
		for (List<ExchangeRate> rateList : newCurrencyToSdr.values()) {
			Collections.sort(rateList);
		}
		this.sdrToCurrency = newSdrToCurrency;
		this.currencyToSdr = newCurrencyToSdr;
	}

	private Double[] parseValues(NumberFormat f, String[] parts)
			throws ParseException {
		Double[] result = new Double[parts.length - 1];
		for (int i = 1; i < parts.length; i++) {
			if (parts[i].isEmpty()) {
				continue;
			}
			result[i - 1] = f.parse(parts[i]).doubleValue();
		}
		return result;
	}

	private List<Long> readTimestamps(String line) throws ParseException {
		// Currency May 01, 2013 April 30, 2013 April 29, 2013 April 26, 2013
		// April 25, 2013
		SimpleDateFormat sdf = new SimpleDateFormat("MMM DD, yyyy",
				Locale.ENGLISH);
		String[] parts = line.split("\\\t");
		List<Long> dates = new ArrayList<Long>(parts.length);
		for (int i = 1; i < parts.length; i++) {
			dates.add(sdf.parse(parts[i]).getTime());
		}
		return dates;
	}

	protected ExchangeRate getExchangeRateInternal(CurrencyUnit base,
			CurrencyUnit term, ConversionContext context) {
		ExchangeRate rate1 = lookupRate(currencyToSdr.get(base),
				context.getTimestamp());
		ExchangeRate rate2 = lookupRate(sdrToCurrency.get(term),
				context.getTimestamp());
		if (base.equals(SDR)) {
			return rate2;
		} else if (term.equals(SDR)) {
			return rate1;
		}
		if (rate1 == null || rate2 == null) {
			return null;
		}
		ExchangeRate.Builder builder = new ExchangeRate.Builder(
				ConversionContext.of(CONTEXT.getProviderName(),
						RateType.HISTORIC));
		builder.setBase(base);
		builder.setTerm(term);
		builder.setFactor(multiply(rate1.getFactor(), rate2.getFactor()));
		builder.setRateChain(rate1, rate2);
		return builder.create();
	}

	private ExchangeRate lookupRate(List<ExchangeRate> list, Long timestamp) {
		if (list == null) {
			return null;
		}
		ExchangeRate found = null;
		for (ExchangeRate rate : list) {
			if (timestamp == null) {
				timestamp = System.currentTimeMillis();
			}
			if (rate.getConversionContext().isValid(timestamp)) {
				return rate;
			}
			if (found == null) {
				found = rate;
			}
		}
		return found;
	}

}
