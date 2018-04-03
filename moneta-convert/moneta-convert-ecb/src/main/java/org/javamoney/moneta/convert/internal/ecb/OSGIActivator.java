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
package org.javamoney.moneta.convert.internal.ecb;

import org.javamoney.moneta.OSGIServiceHelper;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

import javax.money.convert.ExchangeRateProvider;
import java.util.logging.Logger;

/**
 * A bundle activator that registers the OSGI services.
 */
public class OSGIActivator implements BundleActivator {

    private static final Logger LOG = Logger.getLogger(OSGIActivator.class.getName());

    @Override
    public void start(BundleContext context) {
        LOG.info("Registering JavaMoney services...");
        OSGIServiceHelper.registerService(context.getBundle(), ExchangeRateProvider.class, ECBCurrentRateProvider.class);
        OSGIServiceHelper.registerService(context.getBundle(), ExchangeRateProvider.class, ECBHistoricRateProvider.class);
        OSGIServiceHelper.registerService(context.getBundle(), ExchangeRateProvider.class, ECBHistoric90RateProvider.class);
        LOG.info("Registered JavaMoney services...");
    }

    @Override
    public void stop(BundleContext context) {
        LOG.info("Unregistering JavaMoney services...");
        OSGIServiceHelper.unregisterService(context.getBundle(), ExchangeRateProvider.class, ECBCurrentRateProvider.class);
        OSGIServiceHelper.unregisterService(context.getBundle(), ExchangeRateProvider.class, ECBHistoricRateProvider.class);
        OSGIServiceHelper.unregisterService(context.getBundle(), ExchangeRateProvider.class, ECBHistoric90RateProvider.class);
    }
}
