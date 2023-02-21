/*
  Copyright (c) 2012, 2023, Werner Keil and others by the @author tag.

  Licensed under the Apache License, Version 2.0 (the "License"); you may not
  use this file except in compliance with the License. You may obtain a copy of
  the License at

  http://www.apache.org/licenses/LICENSE-2.0

  Unless required by applicable law or agreed to in writing, software
  distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
  WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
  License for the specific language governing permissions and limitations under
  the License.
 */
package org.javamoney.moneta.convert.imf;

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
        LOG.config("Registering JavaMoney services...");
        OSGIServiceHelper.registerService(context.getBundle(), ExchangeRateProvider.class, IMFRateProvider.class);
        LOG.config("Registered JavaMoney services...");
    }

    @Override
    public void stop(BundleContext context) {
        LOG.config("Unregistering JavaMoney services...");
        OSGIServiceHelper.unregisterService(context.getBundle(), ExchangeRateProvider.class, IMFRateProvider.class);
    }
}
