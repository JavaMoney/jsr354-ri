/*
 * Copyright (c) 2012, 2025, Werner Keil and others by the @author tag.
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

import javax.money.convert.ProviderContext;
import javax.money.convert.ProviderContextBuilder;
import javax.money.convert.RateType;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import static org.javamoney.moneta.convert.ecb.defaults.Defaults.ECB_HIST90_FALLBACK_PATH;
import static org.javamoney.moneta.convert.ecb.defaults.Defaults.ECB_HIST90_URL;

/**
 * <p>
 * This class implements an {@link javax.money.convert.ExchangeRateProvider}
 * that loads data from the European Central Bank data feed (XML). It loads the
 * current exchange rates, as well as historic rates for the past 90 days. The
 * provider loads all data up to 1999 into its historic data cache.
 * </p>
 * <p>The default date is yesterday or the most recent day of week. To uses exchange rate from a specific date, you can use this way:</p>
 * <p><code>CurrencyUnit termCurrency = ...;</code></p>
 * <p><code>LocalDate localDate = ...;</code></p>
 * <p><code>ConversionQuery conversionQuery = ConversionQueryBuilder.of().setTermCurrency(euro).setTimestamp(localDate).build();</code>v
 * <p><code>CurrencyConversion currencyConversion = provider.getCurrencyConversion(conversionQuery);</code></p>
 * <p><code>MonetaryAmount money = ...;</code></p>
 * <p><code>MonetaryAmount result = currencyConversion.apply(money);</code></p>
 *
 * @author Anatole Tresch
 * @author Werner Keil
 * @author otaviojava
 */
public class ECBHistoric90RateProvider extends ECBAbstractRateProvider {


    private static final String DATA_ID = ECBHistoric90RateProvider.class.getSimpleName();

    private static final ProviderContext CONTEXT =
            ProviderContextBuilder.of("ECB-HIST90", RateType.HISTORIC, RateType.DEFERRED)
                    .set("providerDescription", "European Central Bank (last 90 days)").set("days", 90).build();

    public ECBHistoric90RateProvider() {
        super(CONTEXT, ECB_HIST90_URL);
    }

    @Override
    public String getDataId() {
        return DATA_ID;
    }

    @Override
    protected LoadDataInformation getDefaultLoadData() {
        final Map<String, String> props = new HashMap<>();
        props.put("period", "03:00");

        return new LoadDataInformationBuilder()
                .withResourceId(getDataId())
                .withUpdatePolicy(LoaderService.UpdatePolicy.SCHEDULED)
                .withProperties(props)
                .withBackupResource(getResourceFromPath(ECB_HIST90_FALLBACK_PATH, getClass()))
                .withResourceLocations(URI.create(ECB_HIST90_URL))
                .withStartRemote(true)
                .build();
    }

//    @Override TODO this is for a MRJ version <=Java 9
//    protected LoadDataInformation getDefaultLoadData() {
//        return new LoadDataInformationBuilder()
//            .withResourceId(getDataId())
//            .withUpdatePolicy(LoaderService.UpdatePolicy.SCHEDULED)
//            .withProperties(Map.of("period", "03:00"))
//            .withBackupResource(URI.create("org/javamoney/moneta/convert/ecb/defaults/eurofxref-hist-90d.xml"))
//            .withResourceLocations(URI.create("https://www.ecb.europa.eu/stats/eurofxref/eurofxref-hist-90d.xml"))
//            .withStartRemote(true)
//            .build();
//    }

}
