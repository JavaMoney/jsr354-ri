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

import javax.money.convert.ConversionContext;
import javax.money.convert.ProviderContext;
import javax.money.convert.ProviderContextBuilder;
import javax.money.convert.RateType;

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
 * <p><code>ConversionQuery conversionQuery = ConversionQueryBuilder.of().setTermCurrency(euro).setTimestamp(localDate).build();</code>v
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


}