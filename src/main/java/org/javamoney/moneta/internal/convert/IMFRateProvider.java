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
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.money.convert.ConversionContext;
import javax.money.convert.ExchangeRateProvider;
import javax.money.convert.ProviderContext;
import javax.money.convert.ProviderContextBuilder;
import javax.money.convert.RateType;
import javax.money.spi.Bootstrap;

import org.javamoney.moneta.spi.LoaderService;

/**
 * Implements a {@link ExchangeRateProvider} that loads the IMF conversion data.
 * In most cases this provider will provide chained rates, since IMF always is
 * converting from/to the IMF <i>SDR</i> currency unit.
 *
 * @author Anatole Tresch
 * @author Werner Keil
 */
public class IMFRateProvider extends IMFAbstractRateProvider {

	private static final Logger LOG = Logger
			.getLogger(IMFRateProvider.class.getName());

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
        	LOG.log(Level.WARNING, "Error loading initial data from IMF provider...", e);
        }
    }

}
