/*
 * CREDIT SUISSE IS WILLING TO LICENSE THIS SPECIFICATION TO YOU ONLY UPON THE CONDITION THAT YOU
 * ACCEPT ALL OF THE TERMS CONTAINED IN THIS AGREEMENT. PLEASE READ THE TERMS AND CONDITIONS OF THIS
 * AGREEMENT CAREFULLY. BY DOWNLOADING THIS SPECIFICATION, YOU ACCEPT THE TERMS AND CONDITIONS OF
 * THE AGREEMENT. IF YOU ARE NOT WILLING TO BE BOUND BY IT, SELECT THE "DECLINE" BUTTON AT THE
 * BOTTOM OF THIS PAGE. Specification: JSR-354 Money and Currency API ("Specification") Copyright
 * (c) 2012-2013, Credit Suisse All rights reserved.
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
import javax.money.AmountFlavor;
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

	private static final Comparator<MonetaryAmountFactoryProviderSpi<? extends MonetaryAmount>> CONTEXT_COMPARATOR = new Comparator<MonetaryAmountFactoryProviderSpi<? extends MonetaryAmount>>() {

		@Override
		public int compare(
				MonetaryAmountFactoryProviderSpi<? extends MonetaryAmount> f1,
				MonetaryAmountFactoryProviderSpi<? extends MonetaryAmount> f2) {
			int compare = 0;
			MonetaryContext c1 = f1.getMaximalMonetaryContext();
			MonetaryContext c2 = f2.getMaximalMonetaryContext();
			if (c1.getAmountFlavor() == AmountFlavor.PRECISION
					&& c2.getAmountFlavor() != AmountFlavor.PRECISION) {
				compare = -1;
			}
			if (compare == 0 && c2.getAmountFlavor() == AmountFlavor.PRECISION
					&& c1.getAmountFlavor() != AmountFlavor.PRECISION) {
				compare = 1;
			}
			if (compare == 0 && c1.getPrecision() == 0
					&& c2.getPrecision() != 0) {
				compare = -1;
			}
			if (compare == 0 && c2.getPrecision() == 0
					&& c1.getPrecision() != 0) {
				compare = 1;
			}
			if (compare == 0 && (c1.getMaxScale() > c2.getMaxScale())) {
				compare = -1;
			}
			if (compare == 0 && (c1.getMaxScale() < c2.getMaxScale())) {
				compare = 1;
			}
			return compare;
		}
	};

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
