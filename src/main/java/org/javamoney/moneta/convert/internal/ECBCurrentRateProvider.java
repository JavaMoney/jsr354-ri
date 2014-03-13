/*
 * Copyright (c) 2012, 2014, Credit Suisse (Anatole Tresch), Werner Keil. Licensed under the Apache
 * License, Version 2.0 (the "License"); you may not use this file except in compliance with the
 * License. You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License. Contributors: Anatole Tresch - initial implementation.
 */
package org.javamoney.moneta.convert.internal;

import org.javamoney.moneta.spi.AbstractRateProvider;
import org.javamoney.moneta.spi.DefaultNumberValue;
import org.javamoney.moneta.spi.LoaderService;
import org.javamoney.moneta.spi.LoaderService.LoaderListener;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import javax.money.CurrencyUnit;
import javax.money.MonetaryCurrencies;
import javax.money.convert.ConversionContext;
import javax.money.convert.ExchangeRate;
import javax.money.convert.ProviderContext;
import javax.money.convert.RateType;
import javax.money.spi.Bootstrap;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.net.MalformedURLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.TimeZone;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;

/**
 * This class implements an {@link javax.money.convert.ExchangeRateProvider} that loads data from
 * the European Central Bank data feed (XML). It loads the current exchange
 * rates, as well as historic rates for the past 90 days. The provider loads all data up to 1999 into its
 * historic data cache.
 *
 * @author Anatole Tresch
 * @author Werner Keil
 */
public class ECBCurrentRateProvider extends AbstractRateProvider implements LoaderListener{

    private static final String BASE_CURRENCY_CODE = "EUR";
    /**
     * Base currency of the loaded rates is always EUR.
     */
    public static final CurrencyUnit BASE_CURRENCY = MonetaryCurrencies.getCurrency(BASE_CURRENCY_CODE);
    /**
     * The data id used for the LoaderService.
     */
    private static final String DATA_ID = ECBCurrentRateProvider.class.getSimpleName();

    /**
     * Current exchange rates.
     */
    private Map<String,ExchangeRate> currentRates = new ConcurrentHashMap<String,ExchangeRate>();
    /**
     * Parser factory.
     */
    private SAXParserFactory saxParserFactory = SAXParserFactory.newInstance();
    /**
     * The {@link ConversionContext} of this provider.
     */
    private static final ProviderContext CONTEXT = new ProviderContext.Builder("ECB").setRateTypes(RateType.DEFERRED)
            .set("European Central Bank", "providerDescription").set(1, "days").create();

    /**
     * Constructor, also loads initial data.
     *
     * @throws MalformedURLException
     */
    public ECBCurrentRateProvider() throws MalformedURLException{
        super(CONTEXT);
        saxParserFactory.setNamespaceAware(false);
        saxParserFactory.setValidating(false);
        LoaderService loader = Bootstrap.getService(LoaderService.class);
        loader.addLoaderListener(this, DATA_ID);
        try{
            loader.loadData(DATA_ID);
        }
        catch(IOException e){
            LOGGER.log(Level.SEVERE, "Error loading ECB data.", e);
        }
    }

    /**
     * (Re)load the given data feed.
     *
     * @throws IOException
     * @throws SAXException
     * @throws ParserConfigurationException
     */
    @Override
    public void newDataLoaded(String data, InputStream is){
        try{
            SAXParser parser = saxParserFactory.newSAXParser();
            parser.parse(is, new RateReadingHandler());
            LOGGER.info("Loaded current " + DATA_ID + " exchange rates.");
        }
        catch(Exception e){
            LOGGER.log(Level.SEVERE, "Error reading resource for ECB currencies: ", e);
        }
    }

    protected ExchangeRate getExchangeRateInternal(CurrencyUnit base, CurrencyUnit term, ConversionContext context){
        if(context.getTimestamp() != null){
            return null;
        }
        ExchangeRate.Builder builder = new ExchangeRate.Builder(
                ConversionContext.of(CONTEXT.getProviderName(), RateType.DEFERRED, context.getTimestamp()));
        builder.setBase(base);
        builder.setTerm(term);
        ExchangeRate sourceRate = null;
        ExchangeRate target = null;
        if(currentRates.isEmpty()){
            return null;
        }
        sourceRate = currentRates.get(base.getCurrencyCode());
        target = currentRates.get(term.getCurrencyCode());
        if(BASE_CURRENCY_CODE.equals(base.getCurrencyCode()) && BASE_CURRENCY_CODE.equals(term.getCurrencyCode())){
            builder.setFactor(DefaultNumberValue.ONE);
            return builder.create();
        }else if(BASE_CURRENCY_CODE.equals(term.getCurrencyCode())){
            if(sourceRate == null){
                return null;
            }
            return getReversed(sourceRate);
        }else if(BASE_CURRENCY_CODE.equals(base.getCurrencyCode())){
            return target;
        }else{
            // Get Conversion base as derived rate: base -> EUR -> term
            ExchangeRate rate1 =
                    getExchangeRateInternal(base, MonetaryCurrencies.getCurrency(BASE_CURRENCY_CODE), context);
            ExchangeRate rate2 =
                    getExchangeRateInternal(MonetaryCurrencies.getCurrency(BASE_CURRENCY_CODE), term, context);
            if(rate1 != null && rate2 != null){
                builder.setFactor(multiply(rate1.getFactor(), rate2.getFactor()));
                builder.setRateChain(rate1, rate2);
                return builder.create();
            }
        }
        return null;
    }

    /**
     * SAX Event Handler that reads the quotes.
     * <p/>
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
                    if(attributes.getValue("time") != null){
                        Date date = dateFormat.parse(attributes.getValue("time"));
                        timestamp = Long.valueOf(date.getTime());
                    }else if(attributes.getValue("currency") != null){
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
     * @param term        the term (target) currency, mapped from EUR.
     * @param timestamp   The target day.
     * @param factor      The conversion factor.
     * @param loadCurrent Flag, if current or historic data is loaded.
     */
    void addRate(CurrencyUnit term, Long timestamp, Number factor){
        ExchangeRate.Builder builder =
                new ExchangeRate.Builder(ConversionContext.of(CONTEXT.getProviderName(), RateType.DEFERRED, timestamp));
        builder.setBase(BASE_CURRENCY);
        builder.setTerm(term);
        builder.setFactor(new DefaultNumberValue(factor));
        this.currentRates.put(term.getCurrencyCode(), builder.create());
    }

}