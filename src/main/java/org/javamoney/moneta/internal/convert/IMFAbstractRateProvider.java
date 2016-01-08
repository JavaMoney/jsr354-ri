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

import java.io.InputStream;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.Comparator;
import java.util.Currency;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.money.CurrencyContextBuilder;
import javax.money.CurrencyUnit;
import javax.money.Monetary;
import javax.money.MonetaryException;
import javax.money.convert.ConversionContext;
import javax.money.convert.ConversionQuery;
import javax.money.convert.ExchangeRate;
import javax.money.convert.ProviderContext;

import org.javamoney.moneta.CurrencyUnitBuilder;
import org.javamoney.moneta.convert.ExchangeRateBuilder;
import org.javamoney.moneta.internal.convert.IMFRateReadingHandler.RateIMFResult;
import org.javamoney.moneta.spi.AbstractRateProvider;
import org.javamoney.moneta.spi.LoaderService.LoaderListener;

abstract class IMFAbstractRateProvider extends AbstractRateProvider implements LoaderListener {


    private static final Logger LOG = Logger.getLogger(IMFAbstractRateProvider.class.getName());

    static final Comparator<ExchangeRate> COMPARATOR_EXCHANGE_BY_LOCAL_DATE = Comparator.comparing(c -> c.getContext().get(LocalDate.class));

	protected static final Map<String, CurrencyUnit> CURRENCIES_BY_NAME = new HashMap<>();

	protected static final CurrencyUnit SDR =
            CurrencyUnitBuilder.of("SDR", CurrencyContextBuilder.of(IMFRateProvider.class.getSimpleName()).build())
                    .setDefaultFractionDigits(3).build(true);

	protected Map<CurrencyUnit, List<ExchangeRate>> currencyToSdr = Collections.emptyMap();

	protected Map<CurrencyUnit, List<ExchangeRate>> sdrToCurrency = Collections.emptyMap();

	protected final IMFRateReadingHandler handler;

	private final ProviderContext context;

	public IMFAbstractRateProvider(ProviderContext providerContext) {
		super(providerContext);
		this.context = providerContext;
		handler = new IMFRateReadingHandler(CURRENCIES_BY_NAME, context);
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
        	 RateIMFResult result = handler.read(is);
        	 this.sdrToCurrency = result.getSdrToCurrency();
             this.currencyToSdr = result.getCurrencyToSdr();
        } catch (Exception e) {
        	LOG.log(Level.SEVERE, "Error", e);
        }
    }

    @Override
    public ExchangeRate getExchangeRate(ConversionQuery conversionQuery) {
        if (!isAvailable(conversionQuery)) {
            return null;
        }
        CurrencyUnit base = conversionQuery.getBaseCurrency();
        CurrencyUnit term = conversionQuery.getCurrency();
        LocalDate[] times = getQueryDates(conversionQuery);
        ExchangeRate rate1 = getExchangeRate(currencyToSdr.get(base), times);
        ExchangeRate rate2 = getExchangeRate(sdrToCurrency.get(term), times);
        if (base.equals(SDR)) {
            return rate2;
        } else if (term.equals(SDR)) {
            return rate1;
        }
        if (Objects.isNull(rate1) || Objects.isNull(rate2)) {
            return null;
        }

        ConversionContext context = getExchangeContext("imf.digit.fraction");

		ExchangeRateBuilder builder =
                new ExchangeRateBuilder(context);
        builder.setBase(base);
        builder.setTerm(term);
        builder.setFactor(multiply(rate1.getFactor(), rate2.getFactor()));
        builder.setRateChain(rate1, rate2);

        return builder.build();
    }

    private ExchangeRate getExchangeRate(List<ExchangeRate> rates,final LocalDate[] dates) {
        if (Objects.isNull(rates) ) {
            return null;
        }
        if (Objects.isNull(dates)) {
        	return rates.stream().sorted(COMPARATOR_EXCHANGE_BY_LOCAL_DATE.reversed()).findFirst().orElseThrow(() -> new MonetaryException("There is not more recent exchange rate to  rate on IMFRateProvider."));
        } else {
        	for (LocalDate localDate : dates) {
        		Predicate<ExchangeRate> filter = rate -> rate.getContext().get(LocalDate.class).equals(localDate);
        		Optional<ExchangeRate> exchangeRateOptional = rates.stream().filter(filter).findFirst();
        		if(exchangeRateOptional.isPresent()) {
        			return exchangeRateOptional.get();
        		}
			}
          	String datesOnErros = Stream.of(dates).map(date -> date.format(DateTimeFormatter.ISO_LOCAL_DATE)).collect(Collectors.joining(","));
        	throw new MonetaryException("There is not exchange on day " + datesOnErros + " to rate to  rate on IFMRateProvider.");
        }
    }

    @Override
    public String toString() {
    	StringBuilder sb = new StringBuilder();
    	sb.append(getClass().getName()).append('{')
    	.append(" context: ").append(context).append('}');
    	return sb.toString();
    }

}
