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
package org.javamoney.moneta.internal;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;

import javax.money.MonetaryAmount;
import javax.money.MonetaryAmountFactory;
import javax.money.MonetaryContext;
import javax.money.MonetaryException;
import javax.money.spi.Bootstrap;
import javax.money.spi.MonetaryAmountFactoryProviderSpi;
import javax.money.spi.MonetaryAmountFactoryProviderSpi.QueryInclusionPolicy;
import javax.money.spi.MonetaryAmountsSingletonSpi;

import org.javamoney.moneta.ServicePriority;

/**
 * Default implementation ot {@link javax.money.spi.MonetaryAmountsSingletonSpi} loading the SPIs on startup initially once, using the
 * JSR's {@link javax.money.spi.Bootstrap} mechanism.
 */
public class DefaultMonetaryAmountsSingletonSpi implements MonetaryAmountsSingletonSpi{

	private Map<Class<? extends MonetaryAmount>, MonetaryAmountFactoryProviderSpi<?>> factories = new ConcurrentHashMap<>();

	private Class<? extends MonetaryAmount> configuredDefaultAmountType = loadDefaultAmountType();

	public DefaultMonetaryAmountsSingletonSpi() {
		for (MonetaryAmountFactoryProviderSpi<?> f : Bootstrap
				.getServices(MonetaryAmountFactoryProviderSpi.class)) {
			MonetaryAmountFactoryProviderSpi<?> existing = factories.put(
					f.getAmountType(), f);
			if (existing != null) {
				int compare = comparePriority(existing, f);
				if (compare < 0) {
					Logger.getLogger(getClass().getName()).warning(
							"MonetaryAmountFactoryProviderSpi with lower prio ignored: "
									+ f);
					factories.put(f.getAmountType(), existing);
				} else if (compare == 0) {
					throw new IllegalStateException(
							"Ambigous MonetaryAmountFactoryProviderSpi found for "
									+ f.getAmountType() + ": "
									+ f.getClass().getName() + '/'
									+ existing.getClass().getName());
				}
			}
		}
	}

	/**
	 * Comparator used for ordering the services provided.
	 * 
	 * @author Anatole Tresch
	 */
	public static final class ProviderComparator implements Comparator<Object> {
		@Override
		public int compare(Object p1, Object p2) {
			return comparePriority(p1, p2);
		}
	}

	/**
	 * Evaluates the service priority. Uses a {@link ServicePriority}, if
	 * present.
	 * 
	 * @param service
	 *            the service, not null.
	 * @return the priority from {@link ServicePriority}, or 0.
	 */
	private static int getServicePriority(Object service) {
		if (service == null) {
			return Integer.MIN_VALUE;
		}
		ServicePriority prio = service.getClass().getAnnotation(
				ServicePriority.class);
		if (prio != null) {
			return prio.value();
		}
		return 0;
	}

	/**
	 * Compare two service priorities given the same service interface.
	 * 
	 * @param service1
	 *            first service, not null.
	 * @param service2
	 *            second service, not null.
	 * @param <T>
	 *            the interface type
	 * @return the comparison result.
	 */
	public static <T> int comparePriority(T service1, T service2) {
		return getServicePriority(service2) - getServicePriority(service1);
	}

	/**
	 * Tries to load the default {@link MonetaryAmount} class from
	 * {@code javamoney.properties} with contents as follows:<br/>
	 * <code>
	 * javax.money.defaults.amount.class=my.fully.qualified.ClassName
	 * </code>
	 * 
	 * @return the loaded default class, or {@code null}
	 */
	// type check should be safe, exception will be logged if not.
	@SuppressWarnings("unchecked")
	private Class<? extends MonetaryAmount> loadDefaultAmountType() {
		return null;
	}


	// save cast, since members are managed by this instance
	@SuppressWarnings("unchecked")
	@Override
	public <T extends MonetaryAmount> MonetaryAmountFactory<T> getAmountFactory(
			Class<T> amountType) {
		MonetaryAmountFactoryProviderSpi<T> f = MonetaryAmountFactoryProviderSpi.class
				.cast(factories.get(amountType));
		if (f != null) {
			return f.createMonetaryAmountFactory();
		}
		throw new MonetaryException(
				"No matching MonetaryAmountFactory found, type="
						+ amountType.getName());
	}

	@Override
	public Set<Class<? extends MonetaryAmount>> getAmountTypes() {
		return factories.keySet();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.money.spi.MonetaryAmountsSpi#getDefaultAmountType()
	 */
	@Override
	public Class<? extends MonetaryAmount> getDefaultAmountType() {
		if (configuredDefaultAmountType == null) {
			for (MonetaryAmountFactoryProviderSpi<?> f : Bootstrap
					.getServices(MonetaryAmountFactoryProviderSpi.class)) {
				configuredDefaultAmountType = f.getAmountType();
				break;
			}
		}
		if (configuredDefaultAmountType == null) {
			throw new MonetaryException(
					"No MonetaryAmountFactoryProviderSpi registered.");
		}
		return configuredDefaultAmountType;
	}

}
