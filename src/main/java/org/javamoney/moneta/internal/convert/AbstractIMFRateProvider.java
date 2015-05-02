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
import java.util.Currency;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.logging.Level;

import javax.money.CurrencyContextBuilder;
import javax.money.CurrencyUnit;
import javax.money.Monetary;
import javax.money.convert.ConversionContextBuilder;
import javax.money.convert.ExchangeRate;
import javax.money.convert.ProviderContext;
import javax.money.convert.RateType;

import org.javamoney.moneta.CurrencyUnitBuilder;
import org.javamoney.moneta.ExchangeRateBuilder;
import org.javamoney.moneta.spi.AbstractRateProvider;
import org.javamoney.moneta.spi.DefaultNumberValue;
import org.javamoney.moneta.spi.LoaderService.LoaderListener;

abstract class AbstractIMFRateProvider extends AbstractRateProvider implements LoaderListener {

	protected static final Map<String, CurrencyUnit> CURRENCIES_BY_NAME = new HashMap<>();

	protected static final CurrencyUnit SDR =
            CurrencyUnitBuilder.of("SDR", CurrencyContextBuilder.of(IMFRateProvider.class.getSimpleName()).build())
                    .setDefaultFractionDigits(3).build(true);

	protected Map<CurrencyUnit, List<ExchangeRate>> currencyToSdr = new HashMap<>();

	protected Map<CurrencyUnit, List<ExchangeRate>> sdrToCurrency = new HashMap<>();

	private final ProviderContext context;

	public AbstractIMFRateProvider(ProviderContext providerContext) {
		super(providerContext);
		this.context = providerContext;
	}


    static {
        for (Currency currency : Currency.getAvailableCurrencies()) {
            CURRENCIES_BY_NAME.put(currency.getDisplayName(Locale.ENGLISH),
                    Monetary.getCurrency(currency.getCurrencyCode()));
        }
        CURRENCIES_BY_NAME.put("U.K. Pound Sterling", Monetary.getCurrency("GBP"));
        CURRENCIES_BY_NAME.put("U.S. Dollar", Monetary.getCurrency("USD"));
        CURRENCIES_BY_NAME.put("Bahrain Dinar", Monetary.getCurrency("BHD"));
        CURRENCIES_BY_NAME.put("Botswana Pula", Monetary.getCurrency("BWP"));
        CURRENCIES_BY_NAME.put("Czech Koruna", Monetary.getCurrency("CZK"));
        CURRENCIES_BY_NAME.put("Icelandic Krona", Monetary.getCurrency("ISK"));
        CURRENCIES_BY_NAME.put("Korean Won", Monetary.getCurrency("KRW"));
        CURRENCIES_BY_NAME.put("Rial Omani", Monetary.getCurrency("OMR"));
        CURRENCIES_BY_NAME.put("Nuevo Sol", Monetary.getCurrency("PEN"));
        CURRENCIES_BY_NAME.put("Qatar Riyal", Monetary.getCurrency("QAR"));
        CURRENCIES_BY_NAME.put("Saudi Arabian Riyal", Monetary.getCurrency("SAR"));
        CURRENCIES_BY_NAME.put("Sri Lanka Rupee", Monetary.getCurrency("LKR"));
        CURRENCIES_BY_NAME.put("Trinidad And Tobago Dollar", Monetary.getCurrency("TTD"));
        CURRENCIES_BY_NAME.put("U.A.E. Dirham", Monetary.getCurrency("AED"));
        CURRENCIES_BY_NAME.put("Peso Uruguayo", Monetary.getCurrency("UYU"));
        CURRENCIES_BY_NAME.put("Bolivar Fuerte", Monetary.getCurrency("VEF"));
    }


    @Override
    public void newDataLoaded(String resourceId, InputStream is) {
        try {
            loadRatesTSV(is);
        } catch (Exception e) {
            log.log(Level.SEVERE, "Error", e);
        }
    }

    private void loadRatesTSV(InputStream inputStream) throws IOException, ParseException {
        Map<CurrencyUnit, List<ExchangeRate>> newCurrencyToSdr = new HashMap<>();
        Map<CurrencyUnit, List<ExchangeRate>> newSdrToCurrency = new HashMap<>();

        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        String line = reader.readLine();
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
        List<LocalDate> timestamps = null;
        while (Objects.nonNull(line)) {
            if (line.trim().isEmpty()) {
                line = reader.readLine();
                continue;
            }
            if (line.startsWith("SDRs per Currency unit")) {
                currencyToSdr = false;
                line = reader.readLine();
                continue;
            } else if (line.startsWith("Currency units per SDR")) {
                currencyToSdr = true;
                line = reader.readLine();
                continue;
            } else if (line.startsWith("Currency")) {
                timestamps = readTimestamps(line);
                line = reader.readLine();
                continue;
            }
            String[] parts = line.split("\\t");
            CurrencyUnit currency = CURRENCIES_BY_NAME.get(parts[0]);
            if (Objects.isNull(currency)) {
                log.finest(() -> "Uninterpretable data from IMF data feed: " + parts[0]);
                line = reader.readLine();
                continue;
            }
            saveExchangeRate(newCurrencyToSdr, newSdrToCurrency, currencyToSdr,
					timestamps, currency, parseValues(parts));
            line = reader.readLine();
        }
        // Cast is save, since contained DefaultExchangeRate is Comparable!
        savingResults(newCurrencyToSdr, newSdrToCurrency);
    }

	private void saveExchangeRate(
			Map<CurrencyUnit, List<ExchangeRate>> newCurrencyToSdr,
			Map<CurrencyUnit, List<ExchangeRate>> newSdrToCurrency,
			boolean currencyToSdr, List<LocalDate> timestamps,
			CurrencyUnit currency, Double[] values) {

		for (int index = 0; index < values.length; index++) {

		    if (Objects.isNull(values[index]) || Objects.isNull(getLocalDateFromTS(timestamps, index))) {
		        continue;
		    }

		    LocalDate fromTS = getLocalDateFromTS(timestamps, index);
		    RateType rateType = getRateType(fromTS);

		    if (currencyToSdr) {
		        ExchangeRate rate = new ExchangeRateBuilder(
		                ConversionContextBuilder.create(context, rateType).set(fromTS).build())
		                .setBase(currency).setTerm(SDR).setFactor(new DefaultNumberValue(1D / values[index])).build();
		        List<ExchangeRate> rates = newCurrencyToSdr.computeIfAbsent(currency, c -> new ArrayList<>(5));
		        rates.add(rate);
		    } else {
		        ExchangeRate rate = new ExchangeRateBuilder(
		                ConversionContextBuilder.create(context, rateType).set(fromTS).build())
		                .setBase(SDR).setTerm(currency).setFactor(DefaultNumberValue.of(1D / values[index])).build();
		        List<ExchangeRate> rates = newSdrToCurrency.computeIfAbsent(currency, (c) -> new ArrayList<>(5));
		        rates.add(rate);
		    }
		}
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

	@SuppressWarnings("unchecked")
	private void savingResults(
			Map<CurrencyUnit, List<ExchangeRate>> newCurrencyToSdr,
			Map<CurrencyUnit, List<ExchangeRate>> newSdrToCurrency) {

		newSdrToCurrency.values().forEach((c) -> Collections.sort(List.class.cast(c)));
        newCurrencyToSdr.values().forEach((c) -> Collections.sort(List.class.cast(c)));
        this.sdrToCurrency = newSdrToCurrency;
        this.currencyToSdr = newCurrencyToSdr;
        this.sdrToCurrency.forEach((c, l) -> log.finest(() -> "SDR -> " + c.getCurrencyCode() + ": " + l));
        this.currencyToSdr.forEach((c, l) -> log.finest(() -> c.getCurrencyCode() + " -> SDR: " + l));
	}


    private Double[] parseValues(String[] parts) throws ParseException {

        List<Double> result = new ArrayList<>();
        int index = 0;
        for (String part: parts) {
            if (part.isEmpty() || index == 0) {
            	index++;
                continue;
            }
            index++;
            result.add(Double.valueOf(part.trim().replace(",", "")));
        }
        return result.toArray(new Double[parts.length -1]);
    }

    private List<LocalDate> readTimestamps(String line) {
        // Currency May 01, 2013 April 30, 2013 April 29, 2013 April 26, 2013
        // April 25, 2013
        DateTimeFormatter sdf = DateTimeFormatter.ofPattern("MMMM dd, uuuu").withLocale(Locale.ENGLISH);
        String[] parts = line.split("\\\t");
        List<LocalDate> dates = new ArrayList<>(parts.length);
        for (int i = 1; i < parts.length; i++) {
            dates.add(LocalDate.parse(parts[i], sdf));
        }
        return dates;
    }

}
