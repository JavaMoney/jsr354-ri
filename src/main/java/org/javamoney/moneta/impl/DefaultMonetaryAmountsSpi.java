/*
 * CREDIT SUISSE IS WILLING TO LICENSE THIS SPECIFICATION TO YOU ONLY UPON THE
 * CONDITION THAT YOU ACCEPT ALL OF THE TERMS CONTAINED IN THIS AGREEMENT.
 * PLEASE READ THE TERMS AND CONDITIONS OF THIS AGREEMENT CAREFULLY. BY
 * DOWNLOADING THIS SPECIFICATION, YOU ACCEPT THE TERMS AND CONDITIONS OF THE
 * AGREEMENT. IF YOU ARE NOT WILLING TO BE BOUND BY IT, SELECT THE "DECLINE"
 * BUTTON AT THE BOTTOM OF THIS PAGE. Specification: JSR-354 Money and Currency
 * API ("Specification") Copyright (c) 2012-2013, Credit Suisse All rights
 * reserved.
 */
package org.javamoney.moneta.impl;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import javax.money.MonetaryAmountFactory;
import javax.money.MonetaryAmount;
import javax.money.MonetaryContext;
import javax.money.spi.Bootstrap;
import javax.money.spi.MonetaryAmountsSpi;

public class DefaultMonetaryAmountsSpi implements MonetaryAmountsSpi {

	private Map<Class<? extends MonetaryAmount>, MonetaryAmountFactory> factories = new ConcurrentHashMap<>();

	public DefaultMonetaryAmountsSpi() {
		for (MonetaryAmountFactory f : Bootstrap.getServices(MonetaryAmountFactory.class)) {
			factories.put(f.getAmountType(), f);
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public MonetaryAmountFactory getAmountFactory(
			Class<? extends MonetaryAmount> amountType) {
		return factories.get(amountType);
	}

	@Override
	public Set<Class<? extends MonetaryAmount>> getAmountTypes() {
		return factories.keySet();
	}

	@Override
	public Class<? extends MonetaryAmount> getDefaultAmountType() {
		for (MonetaryAmountFactory f : Bootstrap.getServices(MonetaryAmountFactory.class)) {
			return f.getAmountType();
		}
		return null;
	}

	@Override
	public Class<? extends MonetaryAmount> queryAmountType(
			MonetaryContext requiredContext) {
		for (MonetaryAmountFactory f : Bootstrap.getServices(MonetaryAmountFactory.class)) {
			return f.getAmountType();
		}
		return null;
	}

}
