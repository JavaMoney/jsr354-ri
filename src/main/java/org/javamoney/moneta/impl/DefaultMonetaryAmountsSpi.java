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

	private Map<Class<? extends MonetaryAmount<?>>, MonetaryAmountFactory<?>> factories = new ConcurrentHashMap<>();

	public DefaultMonetaryAmountsSpi() {
		for (MonetaryAmountFactory<?> f : Bootstrap.getServices(MonetaryAmountFactory.class)) {
			factories.put(f.getAmountType(), f);
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T extends MonetaryAmount<T>> MonetaryAmountFactory<T> getAmountFactory(
			Class<T> amountType) {
		return (MonetaryAmountFactory<T>) factories.get(amountType);
	}

	@Override
	public Set<Class<? extends MonetaryAmount<?>>> getAmountTypes() {
		return factories.keySet();
	}

	@Override
	public Class<? extends MonetaryAmount<?>> getDefaultAmountType() {
		for (MonetaryAmountFactory<?> f : Bootstrap.getServices(MonetaryAmountFactory.class)) {
			return f.getAmountType();
		}
		return null;
	}

	@Override
	public Class<? extends MonetaryAmount<?>> queryAmountType(
			MonetaryContext<?> requiredContext) {
		for (MonetaryAmountFactory<?> f : Bootstrap.getServices(MonetaryAmountFactory.class)) {
			return f.getAmountType();
		}
		return null;
	}

}
