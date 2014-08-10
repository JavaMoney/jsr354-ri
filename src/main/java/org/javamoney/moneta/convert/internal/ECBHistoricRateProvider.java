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
package org.javamoney.moneta.convert.internal;

import java.io.InputStream;
import java.math.BigDecimal;
import java.math.MathContext;
import java.net.MalformedURLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;

import javax.money.CurrencyUnit;
import javax.money.MonetaryCurrencies;
import javax.money.convert.*;
import javax.money.spi.Bootstrap;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.javamoney.moneta.DefaultExchangeRate;
import org.javamoney.moneta.spi.AbstractRateProvider;
import org.javamoney.moneta.spi.DefaultNumberValue;
import org.javamoney.moneta.spi.LoaderService;
import org.javamoney.moneta.spi.LoaderService.LoaderListener;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

/**
 * This class implements an {@link javax.money.convert.ExchangeRateProvider} that loads data from
 * the European Central Bank data feed (XML). It loads the current exchange
 * rates, as well as historic rates for the past 90 days. The provider loads all data up to 1999 into its
 * historic data cache.
 *
 * @author Anatole Tresch
 * @author Werner Keil
 */
public class ECBHistoricRateProvider extends AbstractRateProvider implements LoaderListener{

    /**
     * The data id used for the LoaderService.
     */
    private static final String DATA_ID = ECBHistoricRateProvider.class.getSimpleName();
    private static final String BASE_CURRENCY_CODE = "EUR";
    /**
     * Base currency of the loaded rates is always EUR.
     */
    public static final CurrencyUnit BASE_CURRENCY = MonetaryCurrencies.getCurrency(BASE_CURRENCY_CODE);

    /**
     * Historic exchange rates, rate timestamp as UTC long.
     */
    private final Map<Long,Map<String,ExchangeRate>> historicRates =
            new ConcurrentHashMap<>();
    /**
     * Parser factory.
     */
    private SAXParserFactory saxParserFactory = SAXParserFactory.newInstance();
    /**
     * The {@link ConversionContext} of this provider.
     */
    private static final ProviderContext CONTEXT =
            ProviderContextBuilder.create("ECB-HIST", RateType.HISTORIC, RateType.DEFERRED)
                    .set("providerDescription", "European Central Bank").set("days", 1500).build();

    /**
     * Constructor, also loads initial data.
     *
     * @throws MalformedURLException
     */
    public ECBHistoricRateProvider() throws MalformedURLException{
        super(CONTEXT);
        saxParserFactory.setNamespaceAware(false);
        saxParserFactory.setValidating(false);
        LoaderService loader = Bootstrap.getService(LoaderService.class);
        loader.addLoaderListener(this, DATA_ID);
        loader.loadDataAsync(DATA_ID);
    }

    @Override
    public void newDataLoaded(String data, InputStream is){
        final int oldSize = this.historicRates.size();
        try{
            SAXParser parser = saxParserFactory.newSAXParser();
            parser.parse(is, new RateReadingHandler());
        }
        catch(Exception e){
            LOGGER.log(Level.FINEST, "Error during data load.", e);
        }
        int newSize = this.historicRates.size();
        LOGGER.info("Loaded " + DATA_ID + " exchange rates for days:" + (newSize - oldSize));
    }

    /*
     * (non-Javadoc)
     *
     * @see javax.money.convert.spi.ExchangeRateProviderSpi#getExchangeRateType
     * ()
     */
    @Override
    public ProviderContext getProviderContext(){
        return CONTEXT;
    }

    public ExchangeRate getExchangeRate(ConversionQuery query){
        if(Objects.isNull(query.getTimestampMillis())){
            return null;
        }
        DefaultExchangeRate.Builder builder = new DefaultExchangeRate.Builder(
                ConversionContextBuilder.create(CONTEXT, RateType.HISTORIC).setTimestampMillis(query.getTimestampMillis())
                        .build());
        builder.setBase(query.getBaseCurrency());
        builder.setTerm(query.getTermCurrency());
        ExchangeRate sourceRate;
        ExchangeRate target;
        if(historicRates.isEmpty()){
            return null;
        }
        final Calendar cal = new GregorianCalendar(TimeZone.getTimeZone("UTC"));
        cal.setTimeInMillis(query.getTimestampMillis());
        cal.set(Calendar.HOUR, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        Long targetTS = cal.getTimeInMillis();
        Map<String,ExchangeRate> targets = this.historicRates.get(targetTS);
        if(Objects.isNull(targets)){
            return null;
        }
        sourceRate = targets.get(query.getBaseCurrency().getCurrencyCode());
        target = targets.get(query.getTermCurrency().getCurrencyCode());
        if(BASE_CURRENCY_CODE.equals(query.getBaseCurrency().getCurrencyCode()) &&
                BASE_CURRENCY_CODE.equals(query.getTermCurrency().getCurrencyCode())){
            builder.setFactor(DefaultNumberValue.ONE);
            return builder.build();
        }else if(BASE_CURRENCY_CODE.equals(query.getTermCurrency().getCurrencyCode())){
            if(Objects.isNull(sourceRate)){
                return null;
            }
            return reverse(sourceRate);
        }else if(BASE_CURRENCY_CODE.equals(query.getBaseCurrency().getCurrencyCode())){
            return target;
        }else{
            // Get Conversion base as derived rate: base -> EUR -> term
            ExchangeRate rate1 = getExchangeRate(
                    query.toBuilder().setTermCurrency(MonetaryCurrencies.getCurrency(BASE_CURRENCY_CODE)).build());
            ExchangeRate rate2 = getExchangeRate(
                    query.toBuilder().setBaseCurrency(MonetaryCurrencies.getCurrency(BASE_CURRENCY_CODE))
                            .setTermCurrency(query.getTermCurrency()).build());
            if(Objects.nonNull(rate1) || Objects.nonNull(rate2)){
                builder.setFactor(multiply(rate1.getFactor(), rate2.getFactor()));
                builder.setRateChain(rate1, rate2);
                return builder.build();
            }
            return null;
        }
    }

    private static ExchangeRate reverse(ExchangeRate rate){
        if(Objects.isNull(rate)){
            throw new IllegalArgumentException("Rate null is not reversable.");
        }
        return new DefaultExchangeRate.Builder(rate).setRate(rate).setBase(rate.getTerm()).setTerm(rate.getBase())
                .setFactor(divide(DefaultNumberValue.ONE, rate.getFactor(), MathContext.DECIMAL64)).build();
    }

    /**
     * SAX Event Handler that reads the quotes.
     * <p>
     * Format: <gesmes:Envelope
     * xmlns:gesmes="http://www.gesmes.org/xml/2002-08-01"
     * xmlns="http://www.ecb.int/vocabulary/2002-08-01/eurofxref">
     * <gesmes:subject>Reference rates</gesmes:subject> <gesmes:Sender>
     * <gesmes:name>European Central Bank</gesmes:name> </gesmes:Sender> <Cube>
     * <Cube time="2013-02-21">...</Cube> <Cube time="2013-02-20">...</Cube>
     * <Cube time="2013-02-19"> <Cube currency="USD" rate="1.3349"/> <Cube
     * currency="JPY" rate="124.81"/> <Cube currency="BGN" rate="1.9558"/> <Cube
     * currency="CZK" rate="25.434"/> <Cube currency="DKK" rate="7.4599"/> <Cube
     * currency="GBP" rate="0.8631"/> <Cube currency="HUF" rate="290.79"/> <Cube
     * currency="LTL" rate="3.4528"/> ...
     *
     * @author Anatole Tresch
     */
    private class RateReadingHandler extends DefaultHandler{

        /**
         * Date parser.
         */
        private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        /**
         * Current timestamp for the given section.
         */
        private Long timestamp;

        /** Flag, if current or historic data is loaded. */
        // private boolean loadCurrent;

        /**
         * Creates a new parser.
         */
        public RateReadingHandler(){
            dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
        }

        /*
         * (non-Javadoc)
         *
         * @see
         * org.xml.sax.helpers.DefaultHandler#startElement(java.lang.String,
         * java.lang.String, java.lang.String, org.xml.sax.Attributes)
         */
        @Override
        public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException{
            try{
                if("Cube".equals(qName)){
                    if(Objects.nonNull(attributes.getValue("time"))){
                        Date date = dateFormat.parse(attributes.getValue("time"));
                        timestamp = date.getTime();
                    }else if(Objects.nonNull(attributes.getValue("currency"))){
                        // read data <Cube currency="USD" rate="1.3349"/>
                        CurrencyUnit tgtCurrency = MonetaryCurrencies.getCurrency(attributes.getValue("currency"));
                        addRate(tgtCurrency, timestamp,
                                BigDecimal.valueOf(Double.parseDouble(attributes.getValue("rate"))));
                    }
                }
                super.startElement(uri, localName, qName, attributes);
            }
            catch(ParseException e){
                throw new SAXException("Failed to read.", e);
            }
        }

    }

    /**
     * Method to add a currency exchange rate.
     *
     * @param term      the term (target) currency, mapped from EUR.
     * @param timestamp The target day.
     * @param rate      The rate.
     */
    void addRate(CurrencyUnit term, Long timestamp, Number rate){
        RateType rateType = RateType.HISTORIC;
        DefaultExchangeRate.Builder builder;
        if(Objects.nonNull(timestamp)){
            if(timestamp > System.currentTimeMillis()){
                rateType = RateType.DEFERRED;
            }
            builder = new DefaultExchangeRate.Builder(
                    ConversionContextBuilder.create(CONTEXT, rateType).setTimestampMillis(timestamp).build());
        }else{
            builder = new DefaultExchangeRate.Builder(ConversionContextBuilder.create(CONTEXT, rateType).build());
        }
        builder.setBase(BASE_CURRENCY);
        builder.setTerm(term);
        builder.setFactor(DefaultNumberValue.of(rate));
        ExchangeRate exchangeRate = builder.build();
        Map<String,ExchangeRate> rateMap = this.historicRates.get(timestamp);
        if(Objects.isNull(rateMap)){
            synchronized(this.historicRates){
                rateMap = Optional.ofNullable(this.historicRates.get(timestamp)).orElse(new ConcurrentHashMap<>());
                this.historicRates.putIfAbsent(timestamp, rateMap);

            }
        }
        rateMap.put(term.getCurrencyCode(), exchangeRate);
    }
}