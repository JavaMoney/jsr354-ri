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

import javax.money.MonetaryAmount;
import javax.money.MonetaryAmountFactory;
import javax.money.MonetaryAmountFactory.QueryInclusionPolicy;
import javax.money.MonetaryContext;
import javax.money.MonetaryContext.AmountFlavor;
import javax.money.MonetaryException;
import javax.money.spi.Bootstrap;
import javax.money.spi.MonetaryAmountsSpi;

public class DefaultMonetaryAmountsSpi implements MonetaryAmountsSpi {

	private Map<Class<? extends MonetaryAmount>, MonetaryAmountFactory<?>> factories = new ConcurrentHashMap<>();

	private static final Comparator<MonetaryAmountFactory<? extends MonetaryAmount>> CONTEXT_COMPARATOR = new Comparator<MonetaryAmountFactory<? extends MonetaryAmount>>() {

		@Override
		public int compare(MonetaryAmountFactory<? extends MonetaryAmount> f1,
				MonetaryAmountFactory<? extends MonetaryAmount> f2) {
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

	public DefaultMonetaryAmountsSpi() {
		for (MonetaryAmountFactory<?> f : Bootstrap
				.getServices(MonetaryAmountFactory.class)) {
			factories.put(f.getAmountType(), f);
		}
	}

	// save cast, since members are managed by this instance
	@SuppressWarnings("unchecked")
	@Override
	public <T extends MonetaryAmount> MonetaryAmountFactory<T> getAmountFactory(
			Class<T> amountType) {
		return  (MonetaryAmountFactory<T>) factories.get(amountType);
	}

	@Override
	public Set<Class<? extends MonetaryAmount>> getAmountTypes() {
		return factories.keySet();
	}

	@Override
	public Class<? extends MonetaryAmount> getDefaultAmountType() {
		for (MonetaryAmountFactory<?> f : Bootstrap
				.getServices(MonetaryAmountFactory.class)) {
			return f.getAmountType();
		}
		return null;
	}

	/**
	 * (non-Javadoc)
	 * 
	 * @see javax.money.spi.MonetaryAmountsSpi#queryAmountType(javax.money.MonetaryContext)
	 */
	@Override
	public Class<? extends MonetaryAmount> queryAmountType(
			MonetaryContext requiredContext) {
		if (requiredContext == null) {
			return getDefaultAmountType();
		}
		// first check for explicit type
		for (@SuppressWarnings("unchecked")
		MonetaryAmountFactory<? extends MonetaryAmount> f : Bootstrap
				.getServices(MonetaryAmountFactory.class)) {
			if (f.getQueryInclusionPolicy() == QueryInclusionPolicy.NEVER) {
				continue;
			}
			if (requiredContext.getAmountType() == f.getAmountType()) {
				if (isPrecisionOK(requiredContext,
						f.getMaximalMonetaryContext())) {
					return f.getAmountType();
				}
				else {
					throw new MonetaryException(
							"Incompatible context required=" + requiredContext
									+ ", maximal="
									+ f.getMaximalMonetaryContext());
				}
			}
		}
		// Select on required flavor
		List<MonetaryAmountFactory<? extends MonetaryAmount>> selection = new ArrayList<>();
		for (@SuppressWarnings("unchecked")
		MonetaryAmountFactory<? extends MonetaryAmount> f : Bootstrap
				.getServices(MonetaryAmountFactory.class)) {
			if (f.getDefaultMonetaryContext().getAmountFlavor() == AmountFlavor.UNDEFINED) {
				if (f.getQueryInclusionPolicy() == QueryInclusionPolicy.DIRECT_REFERENCE_ONLY
						||
						f.getQueryInclusionPolicy() == QueryInclusionPolicy.NEVER) {
					continue;
				}
				if (isPrecisionOK(requiredContext,
						f.getMaximalMonetaryContext())) {
					selection.add(f);
				}
			}
			else if (requiredContext.getAmountFlavor() == f
					.getDefaultMonetaryContext().getAmountFlavor()) {
				if (isPrecisionOK(requiredContext,
						f.getMaximalMonetaryContext())) {
					selection.add(f);
				}
			}
		}
		if (selection.isEmpty()) {
			// fall back, add all selections, ignore flavor
			for (@SuppressWarnings("unchecked")
			MonetaryAmountFactory<? extends MonetaryAmount> f : Bootstrap
					.getServices(MonetaryAmountFactory.class)) {
				if (f.getQueryInclusionPolicy() == QueryInclusionPolicy.DIRECT_REFERENCE_ONLY
						||
						f.getQueryInclusionPolicy() == QueryInclusionPolicy.NEVER) {
					continue;
				}
				selection.add(f);
			}
		}
		if (selection.size() == 1) {
			return selection.get(0).getAmountType();
		}
		Collections.sort(selection, CONTEXT_COMPARATOR);
		return selection.get(0).getAmountType();
	}

	private boolean isPrecisionOK(MonetaryContext requiredContext,
			MonetaryContext maximalMonetaryContext) {
		if (requiredContext.getPrecision() == 0
				&& maximalMonetaryContext.getPrecision() != 0) {
			return false;
		}
		if (requiredContext.getPrecision() > maximalMonetaryContext
				.getPrecision()) {
			return false;
		}
		if (requiredContext.getMaxScale() > maximalMonetaryContext
				.getMaxScale()) {
			return false;
		}
		return true;
	}

}
