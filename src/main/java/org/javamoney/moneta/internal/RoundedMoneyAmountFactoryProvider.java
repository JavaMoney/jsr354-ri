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
import javax.money.spi.MonetaryAmountFactoryProviderSpi.QueryInclusionPolicy;

import org.javamoney.moneta.RoundedMoney;

/**
 * Implementation of {@link MonetaryAmountFactoryProviderSpi} creating instances of
 * {@link RoundedMoneyAmountFactory}.
 * 
 * @author Anatole Tresch
 */
public final class RoundedMoneyAmountFactoryProvider implements
		MonetaryAmountFactoryProviderSpi<RoundedMoney> {

	@Override
	public Class<RoundedMoney> getAmountType() {
		return RoundedMoney.class;
	}

	@Override
	public MonetaryAmountFactory<RoundedMoney> createMonetaryAmountFactory() {
		return new RoundedMoneyAmountFactory();
	}

	/*
	 * (non-Javadoc)
	 * @see javax.money.spi.MonetaryAmountFactoryProviderSpi#getQueryInclusionPolicy()
	 */
	@Override
	public QueryInclusionPolicy getQueryInclusionPolicy() {
		return QueryInclusionPolicy.DIRECT_REFERENCE_ONLY;
	}

	@Override
	public MonetaryContext getDefaultMonetaryContext() {
		return RoundedMoneyAmountFactory.DEFAULT_CONTEXT;
	}

	@Override
	public MonetaryContext getMaximalMonetaryContext() {
		return RoundedMoneyAmountFactory.MAX_CONTEXT;
	}

}
