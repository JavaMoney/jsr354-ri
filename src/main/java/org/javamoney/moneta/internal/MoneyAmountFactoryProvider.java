/*
 * CREDIT SUISSE IS WILLING TO LICENSE THIS SPECIFICATION TO YOU ONLY UPON THE CONDITION THAT YOU
 * ACCEPT ALL OF THE TERMS CONTAINED IN THIS AGREEMENT. PLEASE READ THE TERMS AND CONDITIONS OF THIS
 * AGREEMENT CAREFULLY. BY DOWNLOADING THIS SPECIFICATION, YOU ACCEPT THE TERMS AND CONDITIONS OF
 * THE AGREEMENT. IF YOU ARE NOT WILLING TO BE BOUND BY IT, SELECT THE "DECLINE" BUTTON AT THE
 * BOTTOM OF THIS PAGE. Specification: JSR-354 Money and Currency API ("Specification") Copyright
 * (c) 2012-2013, Credit Suisse All rights reserved.
 */
package org.javamoney.moneta.internal;

import javax.money.MonetaryAmountFactory;
import javax.money.MonetaryContext;
import javax.money.spi.MonetaryAmountFactoryProviderSpi;

import org.javamoney.moneta.Money;
import org.javamoney.moneta.ServicePriority;

/**
 * Implementation of {@link MonetaryAmountFactoryProviderSpi} creating instances of
 * {@link MoneyAmountFactory}.
 * 
 * @author Anatole Tresch
 */
@ServicePriority(10)
public final class MoneyAmountFactoryProvider implements
		MonetaryAmountFactoryProviderSpi<Money> {

	@Override
	public Class<Money> getAmountType() {
		return Money.class;
	}

	@Override
	public MonetaryAmountFactory<Money> createMonetaryAmountFactory() {
		return new MoneyAmountFactory();
	}

	/*
	 * (non-Javadoc)
	 * @see javax.money.spi.MonetaryAmountFactoryProviderSpi#getQueryInclusionPolicy()
	 */
	@Override
	public QueryInclusionPolicy getQueryInclusionPolicy() {
		return QueryInclusionPolicy.ALWAYS;
	}

	@Override
	public MonetaryContext getDefaultMonetaryContext() {
		return MoneyAmountFactory.DEFAULT_CONTEXT;
	}

	@Override
	public MonetaryContext getMaximalMonetaryContext() {
		return MoneyAmountFactory.MAX_CONTEXT;
	}
}
