/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.javamoney.moneta.internal;

import org.javamoney.moneta.OSGIServiceHelper;
import org.javamoney.moneta.internal.loader.DefaultLoaderService;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

import javax.money.spi.Bootstrap;
import javax.money.spi.CurrencyProviderSpi;
import javax.money.spi.MonetaryAmountFactoryProviderSpi;
import javax.money.spi.MonetaryAmountFormatProviderSpi;
import java.util.logging.Logger;

/**
 * A bundle activator that registers the OSGI services.
 */
public class OSGIActivator implements BundleActivator {

    private static final Logger LOG = Logger.getLogger(OSGIActivator.class.getName());

    private volatile OSGIServiceProvider serviceProvider;

    @Override
    public void start(BundleContext context) {
        // Register marker service
        this.serviceProvider = new OSGIServiceProvider(context);
        LOG.info("Registered OSGI ServiceProvider...");
        Bootstrap.init(this.serviceProvider);
        LOG.info("Registering JavaMoney services...");
        OSGIServiceHelper.registerService(context.getBundle(), CurrencyProviderSpi.class, JDKCurrencyProvider.class);
        OSGIServiceHelper.registerService(context.getBundle(), CurrencyProviderSpi.class, ConfigurableCurrencyUnitProvider.class);

        OSGIServiceHelper.registerService(context.getBundle(), MonetaryAmountFactoryProviderSpi.class, FastMoneyAmountFactoryProvider.class);
        OSGIServiceHelper.registerService(context.getBundle(), MonetaryAmountFactoryProviderSpi.class, MoneyAmountFactoryProvider.class);
        OSGIServiceHelper.registerService(context.getBundle(), MonetaryAmountFactoryProviderSpi.class, RoundedMoneyAmountFactoryProvider.class);

        OSGIServiceHelper.registerService(context.getBundle(), MonetaryAmountFormatProviderSpi.class, org.javamoney.moneta.internal.format.DefaultAmountFormatProviderSpi.class);

        OSGIServiceHelper.registerService(context.getBundle(), javax.money.spi.MonetaryAmountsSingletonQuerySpi.class, DefaultMonetaryAmountsSingletonQuerySpi.class);

        OSGIServiceHelper.registerService(context.getBundle(), javax.money.spi.MonetaryAmountsSingletonQuerySpi.class, DefaultMonetaryAmountsSingletonQuerySpi.class);
        OSGIServiceHelper.registerService(context.getBundle(), javax.money.spi.MonetaryAmountsSingletonSpi.class, DefaultMonetaryAmountsSingletonSpi.class);
        OSGIServiceHelper.registerService(context.getBundle(), javax.money.spi.MonetaryCurrenciesSingletonSpi.class, DefaultMonetaryCurrenciesSingletonSpi.class);
        OSGIServiceHelper.registerService(context.getBundle(), javax.money.spi.RoundingProviderSpi.class, DefaultRoundingProvider.class);
        OSGIServiceHelper.registerService(context.getBundle(), org.javamoney.moneta.spi.LoaderService.class, DefaultLoaderService.class);
        LOG.info("Registered JavaMoney services...");
    }

    @Override
    public void stop(BundleContext context) {
        if(serviceProvider!=null) {
            LOG.info("Unregistering JavaMoney services...");
            OSGIServiceHelper.unregisterService(context.getBundle(), CurrencyProviderSpi.class, JDKCurrencyProvider.class);
            OSGIServiceHelper.unregisterService(context.getBundle(), CurrencyProviderSpi.class, ConfigurableCurrencyUnitProvider.class);

            OSGIServiceHelper.unregisterService(context.getBundle(), MonetaryAmountFactoryProviderSpi.class, FastMoneyAmountFactoryProvider.class);
            OSGIServiceHelper.unregisterService(context.getBundle(), MonetaryAmountFactoryProviderSpi.class, MoneyAmountFactoryProvider.class);
            OSGIServiceHelper.unregisterService(context.getBundle(), MonetaryAmountFactoryProviderSpi.class, RoundedMoneyAmountFactoryProvider.class);

            OSGIServiceHelper.unregisterService(context.getBundle(), MonetaryAmountFormatProviderSpi.class, org.javamoney.moneta.internal.format.DefaultAmountFormatProviderSpi.class);

            OSGIServiceHelper.unregisterService(context.getBundle(), javax.money.spi.MonetaryAmountsSingletonQuerySpi.class, DefaultMonetaryAmountsSingletonQuerySpi.class);

            OSGIServiceHelper.unregisterService(context.getBundle(), javax.money.spi.MonetaryAmountsSingletonQuerySpi.class, DefaultMonetaryAmountsSingletonQuerySpi.class);
            OSGIServiceHelper.unregisterService(context.getBundle(), javax.money.spi.MonetaryAmountsSingletonSpi.class, DefaultMonetaryAmountsSingletonSpi.class);
            OSGIServiceHelper.unregisterService(context.getBundle(), javax.money.spi.MonetaryCurrenciesSingletonSpi.class, DefaultMonetaryCurrenciesSingletonSpi.class);
            OSGIServiceHelper.unregisterService(context.getBundle(), javax.money.spi.RoundingProviderSpi.class, DefaultRoundingProvider.class);
            OSGIServiceHelper.unregisterService(context.getBundle(), org.javamoney.moneta.spi.LoaderService.class, DefaultLoaderService.class);
        }
    }
}
