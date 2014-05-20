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

import static org.javamoney.moneta.convert.internal.ProviderConstants.TIMESTAMP;

import org.javamoney.moneta.BuildableCurrencyUnit;
import org.javamoney.moneta.DefaultExchangeRate;
import org.javamoney.moneta.spi.AbstractRateProvider;
import org.javamoney.moneta.spi.DefaultNumberValue;
import org.javamoney.moneta.spi.LoaderService;
import org.javamoney.moneta.spi.LoaderService.LoaderListener;

import javax.money.CurrencyUnit;
import javax.money.MonetaryCurrencies;
import javax.money.convert.*;
import javax.money.spi.Bootstrap;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.logging.Level;

/**
 * Implements a {@link ExchangeRateProvider} that loads the IMF conversion data.
 * In most cases this provider will provide chained rates, since IMF always is
 * converting from/to the IMF <i>SDR</i> currency unit.
 *
 * @author Anatole Tresch
 * @author Werner Keil
 */
public class IMFRateProvider extends AbstractRateProvider implements LoaderListener{

    /**
     * The data id used for the LoaderService.
     */
    private static final String DATA_ID = IMFRateProvider.class.getSimpleName();
    /**
     * The {@link ConversionContext} of this provider.
     */
    private static final ProviderContext CONTEXT = new ProviderContext.Builder("IMF", RateType.DEFERRED)
            .setAttribute("providerDescription", "International Monetary Fond").setAttribute("days", 1).build();

    private static final CurrencyUnit SDR =
            new BuildableCurrencyUnit.Builder("SDR").setDefaultFractionDigits(3).build(true);

    private Map<CurrencyUnit,List<DefaultExchangeRate>> currencyToSdr = new HashMap<>();

    private Map<CurrencyUnit,List<DefaultExchangeRate>> sdrToCurrency = new HashMap<>();

    private static Map<String,CurrencyUnit> currenciesByName = new HashMap<String,CurrencyUnit>();

    static{
        for(Currency currency : Currency.getAvailableCurrencies()){
            currenciesByName.put(currency.getDisplayName(Locale.ENGLISH),
                                 MonetaryCurrencies.getCurrency(currency.getCurrencyCode()));
        }
        // Additional IMF differing codes:
        // This mapping is required to fix data issues in the input stream, it has nothing to do with i18n
        currenciesByName.put("U.K. Pound Sterling", MonetaryCurrencies.getCurrency("GBP"));
        currenciesByName.put("U.S. Dollar", MonetaryCurrencies.getCurrency("USD"));
        currenciesByName.put("Bahrain Dinar", MonetaryCurrencies.getCurrency("BHD"));
        currenciesByName.put("Botswana Pula", MonetaryCurrencies.getCurrency("BWP"));
        currenciesByName.put("Czech Koruna", MonetaryCurrencies.getCurrency("CZK"));
        currenciesByName.put("Icelandic Krona", MonetaryCurrencies.getCurrency("ISK"));
        currenciesByName.put("Korean Won", MonetaryCurrencies.getCurrency("KRW"));
        currenciesByName.put("Rial Omani", MonetaryCurrencies.getCurrency("OMR"));
        currenciesByName.put("Nuevo Sol", MonetaryCurrencies.getCurrency("PEN"));
        currenciesByName.put("Qatar Riyal", MonetaryCurrencies.getCurrency("QAR"));
        currenciesByName.put("Saudi Arabian Riyal", MonetaryCurrencies.getCurrency("SAR"));
        currenciesByName.put("Sri Lanka Rupee", MonetaryCurrencies.getCurrency("LKR"));
        currenciesByName.put("Trinidad And Tobago Dollar", MonetaryCurrencies.getCurrency("TTD"));
        currenciesByName.put("U.A.E. Dirham", MonetaryCurrencies.getCurrency("AED"));
        currenciesByName.put("Peso Uruguayo", MonetaryCurrencies.getCurrency("UYU"));
        currenciesByName.put("Bolivar Fuerte", MonetaryCurrencies.getCurrency("VEF"));
    }

    public IMFRateProvider() throws MalformedURLException{
        super(CONTEXT);
        LoaderService loader = Bootstrap.getService(LoaderService.class);
        loader.addLoaderListener(this, DATA_ID);
        loader.loadDataAsync(DATA_ID);
    }

    @Override
    public void newDataLoaded(String data, InputStream is){
        try{
            loadRatesTSV(is);
        }
        catch(Exception e){
            LOGGER.log(Level.SEVERE, "Error", e);
        }
    }

    private void loadRatesTSV(InputStream inputStream) throws IOException, ParseException{
        Map<CurrencyUnit,List<DefaultExchangeRate>> newCurrencyToSdr = new HashMap<>();
        Map<CurrencyUnit,List<DefaultExchangeRate>> newSdrToCurrency = new HashMap<>();
        NumberFormat f = new DecimalFormat("#0.0000000000");
        f.setGroupingUsed(false);
        BufferedReader pr = new BufferedReader(new InputStreamReader(inputStream));
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
        while(line != null){
            if(line.trim().isEmpty()){
                line = pr.readLine();
                continue;
            }
            if(line.startsWith("SDRs per Currency unit")){
                currencyToSdr = false;
                line = pr.readLine();
                continue;
            }else if(line.startsWith("Currency units per SDR")){
                currencyToSdr = true;
                line = pr.readLine();
                continue;
            }else if(line.startsWith("Currency")){
                timestamps = readTimestamps(line);
                line = pr.readLine();
                continue;
            }
            String[] parts = line.split("\\t");
            CurrencyUnit currency = currenciesByName.get(parts[0]);
            if(currency == null){
                LOGGER.warning("Unknown currency from, IMF data feed: " + parts[0]);
                line = pr.readLine();
                continue;
            }
            Double[] values = parseValues(f, parts);
            for(int i = 0; i < values.length; i++){
                if(values[i] == null){
                    continue;
                }
                Long fromTS = timestamps.get(i);
                Long toTS = fromTS + 3600L * 1000L * 24L; // One day
                RateType rateType = RateType.HISTORIC;
                if(toTS > System.currentTimeMillis()){
                    rateType = RateType.DEFERRED;
                }
                if(currencyToSdr){ // Currency -> SDR
                    List<DefaultExchangeRate> rates = this.currencyToSdr.get(currency);
                    if(rates == null){
                        rates = new ArrayList<DefaultExchangeRate>(5);
                        newCurrencyToSdr.put(currency, rates);
                    }
                    DefaultExchangeRate rate = new DefaultExchangeRate.Builder(
                            new ConversionContext.Builder(CONTEXT, rateType).setAttribute(TIMESTAMP, toTS).build())
                            .setBase(currency).setTerm(SDR).setFactor(new DefaultNumberValue(values[i])).build();
                    rates.add(rate);
                }else{ // SDR -> Currency
                    List<DefaultExchangeRate> rates = this.sdrToCurrency.get(currency);
                    if(rates == null){
                        rates = new ArrayList<DefaultExchangeRate>(5);
                        newSdrToCurrency.put(currency, rates);
                    }
                    DefaultExchangeRate rate = new DefaultExchangeRate.Builder(
                            new ConversionContext.Builder(CONTEXT, rateType).setAttribute(TIMESTAMP, fromTS).build())
                            .setBase(SDR).setTerm(currency).setFactor(DefaultNumberValue.of(values[i])).build();
                    rates.add(rate);
                }
            }
            line = pr.readLine();
        }
        for(List<DefaultExchangeRate> rateList : newSdrToCurrency.values()){
            Collections.sort(rateList);
        }
        for(List<DefaultExchangeRate> rateList : newCurrencyToSdr.values()){
            Collections.sort(rateList);
        }
        this.sdrToCurrency = newSdrToCurrency;
        this.currencyToSdr = newCurrencyToSdr;
    }

    private Double[] parseValues(NumberFormat f, String[] parts) throws ParseException{
        Double[] result = new Double[parts.length - 1];
        for(int i = 1; i < parts.length; i++){
            if(parts[i].isEmpty()){
                continue;
            }
            result[i - 1] = f.parse(parts[i]).doubleValue();
        }
        return result;
    }

    private List<Long> readTimestamps(String line) throws ParseException{
        // Currency May 01, 2013 April 30, 2013 April 29, 2013 April 26, 2013
        // April 25, 2013
        SimpleDateFormat sdf = new SimpleDateFormat("MMM DD, yyyy", Locale.ENGLISH);
        String[] parts = line.split("\\\t");
        List<Long> dates = new ArrayList<Long>(parts.length);
        for(int i = 1; i < parts.length; i++){
            dates.add(sdf.parse(parts[i]).getTime());
        }
        return dates;
    }

    protected ExchangeRate getExchangeRateInternal(CurrencyUnit base, CurrencyUnit term, ConversionContext context){
        ExchangeRate rate1 = lookupRate(currencyToSdr.get(base), context.getNamedAttribute(TIMESTAMP, Long.class));
        ExchangeRate rate2 = lookupRate(sdrToCurrency.get(term), context.getNamedAttribute(TIMESTAMP, Long.class));
        if(base.equals(SDR)){
            return rate2;
        }else if(term.equals(SDR)){
            return rate1;
        }
        if(rate1 == null || rate2 == null){
            return null;
        }
        DefaultExchangeRate.Builder builder =
                new DefaultExchangeRate.Builder(ConversionContext.of(CONTEXT.getProvider(), RateType.HISTORIC));
        builder.setBase(base);
        builder.setTerm(term);
        builder.setFactor(multiply(rate1.getFactor(), rate2.getFactor()));
        builder.setRateChain(rate1, rate2);
        return builder.build();
    }

    private ExchangeRate lookupRate(List<DefaultExchangeRate> list, Long timestamp){
        if(list == null){
            return null;
        }
        ExchangeRate found = null;
        for(ExchangeRate rate : list){
            if(timestamp == null){
                timestamp = System.currentTimeMillis();
            }
            if(isValid(rate.getConversionContext(), timestamp)){
                return rate;
            }
            if(found == null){
                found = rate;
            }
        }
        return found;
    }

    private boolean isValid(ConversionContext conversionContext, Long timestamp){
        Long validFrom = conversionContext.getNamedAttribute("validFrom", Long.class);
        Long validTo = conversionContext.getNamedAttribute("validTo", Long.class);
        if(validFrom!=null && validFrom > timestamp){
            return false;
        }
        return !(validTo != null && validTo < timestamp);
    }

}
