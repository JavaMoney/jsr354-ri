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

import org.javamoney.moneta.FastMoney;

/**
 * Implementation of {@link MonetaryAmountFactoryProviderSpi} creating instances of
 * {@link FastMoneyAmountFactory}.
 * 
 * @author Anatole Tresch
 */
public final class FastMoneyAmountFactoryProvider implements
		MonetaryAmountFactoryProviderSpi<FastMoney> {

	@Override
	public Class<FastMoney> getAmountType() {
		return FastMoney.class;
	}

	@Override
	public MonetaryAmountFactory<FastMoney> createMonetaryAmountFactory() {
		// TODO ensure context!
		return new FastMoneyAmountFactory();
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
		return FastMoneyAmountFactory.DEFAULT_CONTEXT;
	}

	@Override
	public MonetaryContext getMaximalMonetaryContext() {
		return FastMoneyAmountFactory.MAX_CONTEXT;
	}

}
