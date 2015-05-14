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
 * This class implements an {@link javax.money.convert.ExchangeRateProvider} that loads data from
 * the European Central Bank data feed (XML). It loads the current exchange
 * rates. The provider loads all data up to 1999 into its
 * historic data cache.
 *
 * @author Anatole Tresch
 * @author Werner Keil
 * @author otaviojava
 */
public class ECBCurrentRateProvider extends ECBAbstractRateProvider {

    /**
     * The data id used for the LoaderService.
     */
    private static final String DATA_ID = ECBCurrentRateProvider.class.getSimpleName();
    /**
     * The {@link ConversionContext} of this provider.
     */
    private static final ProviderContext CONTEXT =
            ProviderContextBuilder.of("ECB", RateType.DEFERRED).set("providerDescription", "European Central Bank")
                    .set("days", 1).build();

    public ECBCurrentRateProvider() {
        super(CONTEXT);
    }

    @Override
    public String getDataId() {
        return DATA_ID;
    }
}