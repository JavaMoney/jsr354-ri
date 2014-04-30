/*
 * CREDIT SUISSE IS WILLING TO LICENSE THIS SPECIFICATION TO YOU ONLY UPON THE CONDITION THAT YOU
 * ACCEPT ALL OF THE TERMS CONTAINED IN THIS AGREEMENT. PLEASE READ THE TERMS AND CONDITIONS OF THIS
 * AGREEMENT CAREFULLY. BY DOWNLOADING THIS SPECIFICATION, YOU ACCEPT THE TERMS AND CONDITIONS OF
 * THE AGREEMENT. IF YOU ARE NOT WILLING TO BE BOUND BY IT, SELECT THE "DECLINE" BUTTON AT THE
 * BOTTOM OF THIS PAGE. Specification: JSR-354 Money and Currency API ("Specification") Copyright
 * (c) 2012-2013, Credit Suisse All rights reserved.
 */
package org.javamoney.moneta.internal;

import java.math.RoundingMode;

import javax.money.CurrencyUnit;
import javax.money.MonetaryAmountFactory;
import javax.money.MonetaryContext;
import javax.money.AmountFlavor;

import org.javamoney.moneta.FastMoney;
import org.javamoney.moneta.spi.AbstractAmountFactory;

/**
 * Implementation of {@link MonetaryAmountFactory} creating instances of {@link FastMoney}.
 * 
 * @author Anatole Tresch
 */
public class FastMoneyAmountFactory extends AbstractAmountFactory<FastMoney> {

	static final MonetaryContext DEFAULT_CONTEXT = new MonetaryContext.Builder(
			FastMoney.class).setPrecision(18)
			.setMaxScale(5).setFixedScale(true)
			.setObject(RoundingMode.HALF_EVEN)
			.setFlavor(AmountFlavor.PERFORMANCE).create();
	static final MonetaryContext MAX_CONTEXT = new MonetaryContext.Builder(
			FastMoney.class).setPrecision(18)
			.setMaxScale(5).setFixedScale(true)
			.setObject(RoundingMode.HALF_EVEN)
			.setFlavor(AmountFlavor.PERFORMANCE).create();

	@Override
	protected FastMoney create(Number number, CurrencyUnit currency,
                               MonetaryContext monetaryContext) {
		return FastMoney.of(number, currency);
	}

	@Override
	public Class<FastMoney> getAmountType() {
		return FastMoney.class;
	}

	@Override
	protected MonetaryContext loadDefaultMonetaryContext() {
		return DEFAULT_CONTEXT;
	}

	@Override
	protected MonetaryContext loadMaxMonetaryContext() {
		return MAX_CONTEXT;
	}

}
