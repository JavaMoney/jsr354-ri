/*
 * Copyright (c) 2012, 2023, Otavio Santana,, Werner Keil and others by the @author tag.
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
package org.javamoney.moneta.convert.ecb;

import org.javamoney.moneta.convert.ExchangeRateBuilder;
import org.javamoney.moneta.spi.AbstractRateProvider;
import org.javamoney.moneta.spi.DefaultNumberValue;
import org.javamoney.moneta.spi.loader.LoadDataInformation;
import org.javamoney.moneta.spi.loader.LoaderService;
import org.javamoney.moneta.spi.loader.LoaderService.LoaderListener;
import org.xml.sax.InputSource;

import javax.money.CurrencyUnit;
import javax.money.Monetary;
import javax.money.MonetaryException;
import javax.money.convert.ConversionQuery;
import javax.money.convert.CurrencyConversionException;
import javax.money.convert.ExchangeRate;
import javax.money.convert.ProviderContext;
import javax.money.spi.Bootstrap;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.io.InputStream;
import java.math.MathContext;
import java.net.URL;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Base to all Europe Central Bank implementation.
 *
 * @author otaviojava
 * @author Werner Keil
 */
abstract class ECBAbstractRateProvider extends AbstractRateProvider implements
        LoaderListener {

	private static final Logger LOG = Logger.getLogger(ECBAbstractRateProvider.class.getName());

    private static final String BASE_CURRENCY_CODE = "EUR";

    /**
     * Base currency of the loaded rates is always EUR.
     */
    public static final CurrencyUnit BASE_CURRENCY = Monetary.getCurrency(BASE_CURRENCY_CODE);

    /**
     * Historic exchange rates, rate timestamp as UTC long.
     */
    protected final Map<LocalDate, Map<String, ExchangeRate>> rates = new ConcurrentHashMap<>();

    protected volatile String loadState;

    protected volatile CountDownLatch loadLock = new CountDownLatch(1);

    /**
     * Parser factory.
     */
    private final SAXParserFactory saxParserFactory = SAXParserFactory.newInstance();

    private final ProviderContext context;

    protected final String remoteResource;

    ECBAbstractRateProvider(ProviderContext context, final String remoteRes) {
        super(context);
        this.remoteResource = remoteRes;
		this.context = context;
        saxParserFactory.setNamespaceAware(false);
        saxParserFactory.setValidating(false);
        LoaderService loader = Bootstrap.getService(LoaderService.class);
        if (!loader.isResourceRegistered(getDataId())) {
            loader.registerData(getDefaultLoadData());
        }
        loader.addLoaderListener(this, getDataId());
        loader.loadDataAsync(getDataId());
    }

    protected abstract String getDataId();

    /**
     * Gets the default data loading configuration.
     *
     * This configuration is used if no other is provided.
     * @return the configuration
     */
    protected abstract LoadDataInformation getDefaultLoadData();

    @Override
    public void newDataLoaded(String resourceId, InputStream is) {
        final int oldSize = this.rates.size();
        try {
            //final ExchangeRateParser erp = new ExchangeRateParser(is, getContext().getText());
            final SAXParser parser = saxParserFactory.newSAXParser();
            //parser.parse(is, new ECBRateReadingHandler(rates, getContext()));

            //XMLReader xmlReader = parser.getXMLReader();
            //xmlReader.setContentHandler(new ECBRateReadingHandler(rates, getContext()));

            //final InputSource source = new InputSource(is);

            // different encoding
            //source.setEncoding(StandardCharsets.UTF_8.displayName());
            //xmlReader.parse(source, new ECBRateReadingHandler(rates, getContext()));

            //Reader isr = new InputStreamReader(is);
            //InputSource src = new InputSource();
            //src.setCharacterStream(isr);
            InputSource src = new InputSource(new URL(remoteResource).openStream());
            parser.parse(src, new ECBRateReadingHandler(rates, getContext()));

            int newSize = this.rates.size();
            loadState = "Loaded " + resourceId + " exchange rates for days:" + (newSize - oldSize);
            LOG.config(loadState);
        } catch (Exception e) {
            loadState = "Last Error during data load: " + e.getMessage();
        	LOG.log(Level.FINEST, "Error during data load.", e); //TODO could be WARNING?
        } finally{
            loadLock.countDown();
        }
    }

    @Override
    public ExchangeRate getExchangeRate(ConversionQuery conversionQuery) {
        Objects.requireNonNull(conversionQuery);
        try {
            if (loadLock.await(30, TimeUnit.SECONDS)) {
                if (rates.isEmpty()) {
                    return null;
                }
                RateResult result = findExchangeRate(conversionQuery);

                ExchangeRateBuilder builder = getBuilder(conversionQuery);
                ExchangeRate sourceRate = result.targets.get(conversionQuery.getBaseCurrency()
                        .getCurrencyCode());
                ExchangeRate target = result.targets
                        .get(conversionQuery.getCurrency().getCurrencyCode());
                return createExchangeRate(conversionQuery, builder, sourceRate, target);
            }else{
                throw new MonetaryException("Failed to load currency conversion data: " + loadState);
            }
        }
        catch(InterruptedException e){
            throw new MonetaryException("Failed to load currency conversion data: Load task has been interrupted.", e);
        }
    }

	private RateResult findExchangeRate(ConversionQuery conversionQuery) {
		LocalDate[] dates = getQueryDates(conversionQuery);

        if (dates == null) {
        	Comparator<LocalDate> comparator = Comparator.naturalOrder();
    		LocalDate date = this.rates.keySet().stream()
                    .max(comparator)
                    .orElseThrow(() -> new MonetaryException("There is not more recent exchange rate to  rate on ECBRateProvider."));
        	return new RateResult(this.rates.get(date));
        } else {
        	for (LocalDate localDate : dates) {
        		Map<String, ExchangeRate> targets = this.rates.get(localDate);

        		if(Objects.nonNull(targets)) {
        			return new RateResult(targets);
        		}
			}
        	String datesOnErros = Stream.of(dates).map(date -> date.format(DateTimeFormatter.ISO_LOCAL_DATE)).collect(Collectors.joining(","));
        	throw new MonetaryException("There is no exchange on day " + datesOnErros + " to rate to  rate on ECBRateProvider.");
        }


	}



    private ExchangeRate createExchangeRate(ConversionQuery query,
                                            ExchangeRateBuilder builder, ExchangeRate sourceRate,
                                            ExchangeRate target) {

        if (areBothBaseCurrencies(query)) {
            builder.setFactor(DefaultNumberValue.ONE);
            return builder.build();
        } else if (BASE_CURRENCY_CODE.equals(query.getCurrency().getCurrencyCode())) {
            if (Objects.isNull(sourceRate)) {
                return null;
            }
            return reverse(sourceRate);
        } else if (BASE_CURRENCY_CODE.equals(query.getBaseCurrency()
                .getCurrencyCode())) {
            return target;
        } else {

            ExchangeRate rate1 = getExchangeRate(
                    query.toBuilder().setTermCurrency(Monetary.getCurrency(BASE_CURRENCY_CODE)).build());
            ExchangeRate rate2 = getExchangeRate(
                    query.toBuilder().setBaseCurrency(Monetary.getCurrency(BASE_CURRENCY_CODE))
                            .setTermCurrency(query.getCurrency()).build());
            if (Objects.nonNull(rate1) && Objects.nonNull(rate2)) {
                builder.setFactor(multiply(rate1.getFactor(), rate2.getFactor()));
                builder.setRateChain(rate1, rate2);
                return builder.build();
            }
            throw new CurrencyConversionException(query.getBaseCurrency(),
                    query.getCurrency(), sourceRate.getContext());
        }
    }

    private boolean areBothBaseCurrencies(ConversionQuery query) {
        return BASE_CURRENCY_CODE.equals(query.getBaseCurrency().getCurrencyCode()) &&
                BASE_CURRENCY_CODE.equals(query.getCurrency().getCurrencyCode());
    }


    private ExchangeRateBuilder getBuilder(ConversionQuery query) {
        ExchangeRateBuilder builder = new ExchangeRateBuilder(getExchangeContext("ecb.digit.fraction"));
        builder.setBase(query.getBaseCurrency());
        builder.setTerm(query.getCurrency());

        return builder;
    }

    private ExchangeRate reverse(ExchangeRate rate) {
        if (Objects.isNull(rate)) {
            throw new IllegalArgumentException("Rate null is not reversible.");
        }
        return new ExchangeRateBuilder(rate).setRate(rate).setBase(rate.getCurrency()).setTerm(rate.getBaseCurrency())
                .setFactor(divide(DefaultNumberValue.ONE, rate.getFactor(), MathContext.DECIMAL64)).build();
    }

    @Override
    public String toString() {
        return getClass().getName() + '{' +
                " context: " + context + '}';
    }

    private class RateResult {

    	private final Map<String, ExchangeRate> targets;

    	RateResult(Map<String, ExchangeRate> targets) {
    		this.targets = targets;
    	}
    }

}
