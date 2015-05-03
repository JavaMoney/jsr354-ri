/**
 * Copyright (c) 2012, 2014, Credit Suisse (Anatole Tresch), Werner Keil and others by the @author tag.
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

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.function.Predicate;
import java.util.logging.Level;

import javax.money.CurrencyUnit;
import javax.money.convert.ConversionContext;
import javax.money.convert.ConversionQuery;
import javax.money.convert.ExchangeRate;
import javax.money.convert.ExchangeRateProvider;
import javax.money.convert.ProviderContext;
import javax.money.convert.ProviderContextBuilder;
import javax.money.convert.RateType;
import javax.money.spi.Bootstrap;

import org.javamoney.moneta.ExchangeRateBuilder;
import org.javamoney.moneta.spi.LoaderService;

/**
 * Implements a {@link ExchangeRateProvider} that loads the IMF conversion data.
 * In most cases this provider will provide chained rates, since IMF always is
 * converting from/to the IMF <i>SDR</i> currency unit.
 *
 * @author Anatole Tresch
 * @author Werner Keil
 */
public class IMFRateProvider extends AbstractIMFRateProvider {

    /**
     * The data id used for the LoaderService.
     */
    private static final String DATA_ID = IMFRateProvider.class.getSimpleName();
    /**
     * The {@link ConversionContext} of this provider.
     */
    private static final ProviderContext CONTEXT = ProviderContextBuilder.of("IMF", RateType.DEFERRED)
            .set("providerDescription", "International Monetary Fond").set("days", 1).build();

    public IMFRateProvider() {
        super(CONTEXT);
        LoaderService loader = Bootstrap.getService(LoaderService.class);
        loader.addLoaderListener(this, DATA_ID);
        try {
            loader.loadData(DATA_ID);
        } catch (IOException e) {
            log.log(Level.WARNING, "Error loading initial data from IMF provider...", e);
        }
    }


    @Override
    public ExchangeRate getExchangeRate(ConversionQuery conversionQuery) {
        if (!isAvailable(conversionQuery)) {
            return null;
        }
        CurrencyUnit base = conversionQuery.getBaseCurrency();
        CurrencyUnit term = conversionQuery.getCurrency();
        LocalDate timestamp = getTimeStamp(conversionQuery);
        ExchangeRate rate1 = lookupRate(currencyToSdr.get(base), timestamp);
        ExchangeRate rate2 = lookupRate(sdrToCurrency.get(term), timestamp);
        if (base.equals(SDR)) {
            return rate2;
        } else if (term.equals(SDR)) {
            return rate1;
        }
        if (Objects.isNull(rate1) || Objects.isNull(rate2)) {
            return null;
        }
        ExchangeRateBuilder builder =
                new ExchangeRateBuilder(ConversionContext.of(CONTEXT.getProviderName(), RateType.HISTORIC));
        builder.setBase(base);
        builder.setTerm(term);
        builder.setFactor(multiply(rate1.getFactor(), rate2.getFactor()));
        builder.setRateChain(rate1, rate2);
        return builder.build();
    }


	private LocalDate getTimeStamp(ConversionQuery conversionQuery) {
		LocalDate timestamp = conversionQuery.get(LocalDate.class);
        if (timestamp == null) {
            LocalDateTime dateTime = conversionQuery.get(LocalDateTime.class);
            if (dateTime != null) {
                timestamp = dateTime.toLocalDate();
            }
        }
		return timestamp;
	}

    private ExchangeRate lookupRate(List<ExchangeRate> rates,final LocalDate localDate) {
        if (Objects.isNull(rates) ) {
            return null;
        }
        if (Objects.isNull(localDate)) {
        	Comparator<ExchangeRate> comparator = Comparator.comparing(c -> c.getContext().get(LocalDate.class));
        	return rates.stream().sorted(comparator.reversed()).findFirst().orElseThrow(() -> new ExchangeRateException("There is not more recent exchange rate to  rate on IMFRateProvider."));
        } else {
        	Predicate<ExchangeRate> filter = rate -> rate.getContext().get(LocalDate.class).equals(localDate);
        	return rates.stream().filter(filter).findFirst().orElseThrow(() -> new ExchangeRateException("There is not recent exchange on day " + localDate.format(DateTimeFormatter.ISO_LOCAL_DATE) + " to rate to  rate on IMFRateProvider."));
        }
    }
}
