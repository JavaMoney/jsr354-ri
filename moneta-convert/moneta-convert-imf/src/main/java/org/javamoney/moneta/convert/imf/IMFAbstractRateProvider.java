/*
  Copyright (c) 2012, 2018, Anatole Tresch, Werner Keil and others by the @author tag.

  Licensed under the Apache License, Version 2.0 (the "License"); you may not
  use this file except in compliance with the License. You may obtain a copy of
  the License at

  http://www.apache.org/licenses/LICENSE-2.0

  Unless required by applicable law or agreed to in writing, software
  distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
  WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
  License for the specific language governing permissions and limitations under
  the License.
 */
package org.javamoney.moneta.convert.imf;

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
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.function.Predicate;
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
import org.javamoney.moneta.convert.imf.IMFRateReadingHandler.RateIMFResult;
import org.javamoney.moneta.spi.AbstractRateProvider;
import org.javamoney.moneta.spi.LoaderService.LoaderListener;

abstract class IMFAbstractRateProvider extends AbstractRateProvider implements LoaderListener {


    private static final Logger LOG = Logger.getLogger(IMFAbstractRateProvider.class.getName());

    static final Comparator<ExchangeRate> COMPARATOR_EXCHANGE_BY_LOCAL_DATE = Comparator.comparing(c -> c.getContext().get(LocalDate.class));

    static final String DEFAULT_USER_AGENT = "Chrome/51.0.2704.103";

	protected static final Map<String, CurrencyUnit> CURRENCIES_BY_NAME = new HashMap<>();

	protected static final CurrencyUnit SDR =
            CurrencyUnitBuilder.of("SDR", CurrencyContextBuilder.of(IMFRateProvider.class.getSimpleName()).build())
                    .setDefaultFractionDigits(3).build(true);

	protected Map<CurrencyUnit, List<ExchangeRate>> currencyToSdr = Collections.emptyMap();

	protected Map<CurrencyUnit, List<ExchangeRate>> sdrToCurrency = Collections.emptyMap();

    protected volatile String loadState;

    protected volatile CountDownLatch loadLock = new CountDownLatch(1);

	protected final IMFRateReadingHandler handler;

	private final ProviderContext context;

	public IMFAbstractRateProvider(ProviderContext providerContext) {
		super(providerContext);
		this.context = providerContext;
		handler = new IMFRateReadingHandler(CURRENCIES_BY_NAME, context);
	}


    static {
        for (Currency currency : Currency.getAvailableCurrencies()) {
            CURRENCIES_BY_NAME.put(currency.getDisplayName(Locale.ENGLISH).toLowerCase(Locale.ENGLISH),
                    Monetary.getCurrency(currency.getCurrencyCode()));
        }
        CURRENCIES_BY_NAME.put("U.K. pound".toLowerCase(Locale.ENGLISH), Monetary.getCurrency("GBP"));
        CURRENCIES_BY_NAME.put("U.S. dollar".toLowerCase(Locale.ENGLISH), Monetary.getCurrency("USD"));
        CURRENCIES_BY_NAME.put("Bahrain dinar".toLowerCase(Locale.ENGLISH), Monetary.getCurrency("BHD"));
        CURRENCIES_BY_NAME.put("Botswana pula".toLowerCase(Locale.ENGLISH), Monetary.getCurrency("BWP"));
        CURRENCIES_BY_NAME.put("Czech koruna".toLowerCase(Locale.ENGLISH), Monetary.getCurrency("CZK"));
        CURRENCIES_BY_NAME.put("Icelandic krona".toLowerCase(Locale.ENGLISH), Monetary.getCurrency("ISK"));
        CURRENCIES_BY_NAME.put("Korean won".toLowerCase(Locale.ENGLISH), Monetary.getCurrency("KRW"));
        CURRENCIES_BY_NAME.put("Omani rial".toLowerCase(Locale.ENGLISH), Monetary.getCurrency("OMR"));
        CURRENCIES_BY_NAME.put("Peruvian sol".toLowerCase(Locale.ENGLISH), Monetary.getCurrency("PEN"));
        CURRENCIES_BY_NAME.put("Qatari riyal".toLowerCase(Locale.ENGLISH), Monetary.getCurrency("QAR"));
        CURRENCIES_BY_NAME.put("Saudi Arabian riyal".toLowerCase(Locale.ENGLISH), Monetary.getCurrency("SAR"));
        CURRENCIES_BY_NAME.put("Sri Lankan rupee".toLowerCase(Locale.ENGLISH), Monetary.getCurrency("LKR"));
        CURRENCIES_BY_NAME.put("Trinidadian dollar".toLowerCase(Locale.ENGLISH), Monetary.getCurrency("TTD"));
        CURRENCIES_BY_NAME.put("U.A.E. dirham".toLowerCase(Locale.ENGLISH), Monetary.getCurrency("AED"));
        CURRENCIES_BY_NAME.put("Uruguayan peso".toLowerCase(Locale.ENGLISH), Monetary.getCurrency("UYU"));
        CURRENCIES_BY_NAME.put("Bolivar Fuerte".toLowerCase(Locale.ENGLISH), Monetary.getCurrency("VEF"));
    }


    @Override
    public void newDataLoaded(String resourceId, InputStream is) {
        try {
            int oldSize = this.sdrToCurrency.size();
        	RateIMFResult result = handler.read(is);
        	this.sdrToCurrency = result.getSdrToCurrency();
            this.currencyToSdr = result.getCurrencyToSdr();
            int newSize = this.sdrToCurrency.size();
            loadState = "Loaded " + resourceId + " exchange rates for days:" + (newSize - oldSize);
            LOG.info(loadState);
            loadLock.countDown();
        } catch (Exception e) {
            loadState = "Last Error during data load: " + e.getMessage();
            throw new IllegalArgumentException("Failed to load IMF data provided.", e);
        }
    }

    @Override
    public ExchangeRate getExchangeRate(ConversionQuery conversionQuery) {
        try {
            if (loadLock.await(30, TimeUnit.SECONDS)) {
                if (currencyToSdr.isEmpty()) {
                    return null;
                }
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
            }else{
                // Lets wait for a successful load only once, then answer requests as data is present.
                loadLock.countDown();
                throw new MonetaryException("Failed to load currency conversion data: " + loadState);
            }
        }
        catch(InterruptedException e){
            throw new MonetaryException("Failed to load currency conversion data: Load task has been interrupted.", e);
        }
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
        String sb = getClass().getName() + '{' +
                " context: " + context + '}';
        return sb;
    }

}
