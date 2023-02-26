/*
 * Copyright (c) 2012, 2023, Werner Keil and others by the @author tag.
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

import org.javamoney.moneta.spi.loader.LoadDataInformation;
import org.javamoney.moneta.spi.loader.LoadDataInformationBuilder;
import org.javamoney.moneta.spi.loader.LoaderService;

import javax.money.convert.ConversionContext;
import javax.money.convert.ProviderContext;
import javax.money.convert.ProviderContextBuilder;
import javax.money.convert.RateType;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;

/**
 * <p>
 * This class implements an {@link javax.money.convert.ExchangeRateProvider}
 * that loads data from the European Central Bank data feed (XML). It loads the
 * current exchange rates, as well as historic rates for the past 1500 days. The
 * provider loads all data up to 1999 into its historic data cache.
 * </p>
 * <p>The default date is yesterday or the most recent day of week. To uses exchange rate from a specific date, you can use this way:</p>
 * <p><code>CurrencyUnit termCurrency = ...;</code></p>
 * <p><code>LocalDate localDate = ...;</code></p>
 * <p><code>ConversionQuery conversionQuery = ConversionQueryBuilder.of().setTermCurrency(euro).set(localDate).build();</code>
 * <p><code>CurrencyConversion currencyConversion = provider.getCurrencyConversion(conversionQuery);</code></p>
 * <p><code>MonetaryAmount money = ...;</code></p>
 * <p><code>MonetaryAmount result = currencyConversion.apply(money);</code></p>
 *
 * @author Anatole Tresch
 * @author Werner Keil
 * @author otaviojava
 */
public class ECBHistoricRateProvider extends ECBAbstractRateProvider {

    /**
     * The data id used for the LoaderService.
     */
    private static final String DATA_ID = ECBHistoricRateProvider.class.getSimpleName();

    /**
     * The {@link ConversionContext} of this provider.
     */
    private static final ProviderContext CONTEXT =
            ProviderContextBuilder.of("ECB-HIST", RateType.HISTORIC, RateType.DEFERRED)
                    .set("providerDescription", "European Central Bank").set("days", 1500).build();

    public ECBHistoricRateProvider() {
        super(CONTEXT);
    }

    @Override
    public String getDataId() {
        return DATA_ID;
    }
    @Override
    protected LoadDataInformation getDefaultLoadData() {
        final Map<String, String> props = new HashMap<>();
        props.put("period", "24:00");
        props.put("delay", "01:00");
        props.put("at", "07:00");

        return new LoadDataInformationBuilder()
            .withResourceId(getDataId())
            .withUpdatePolicy(LoaderService.UpdatePolicy.SCHEDULED)
            .withProperties(props)
            .withBackupResource(URI.create("org/javamoney/moneta/convert/ecb/defaults/eurofxref-hist.xml"))
            .withResourceLocations(URI.create("https://www.ecb.europa.eu/stats/eurofxref/eurofxref-hist.xml"))
            .withStartRemote(true)
            .build();
    }

//    @Override TODO a Java 9+ version for a MRJ
//    protected LoadDataInformation getDefaultLoadData() {
//        return new LoadDataInformationBuilder()
//            .withResourceId(getDataId())
//            .withUpdatePolicy(LoaderService.UpdatePolicy.SCHEDULED)
//            .withProperties(Map.of("period", "24:00",
//                "delay", "01:00",
//                "at", "07:00"))
//            .withBackupResource(URI.create("org/javamoney/moneta/convert/ecb/defaults/eurofxref-hist.xml"))
//            .withResourceLocations(URI.create("https://www.ecb.europa.eu/stats/eurofxref/eurofxref-hist.xml"))
//            .withStartRemote(true)
//            .build();
//    }

}
