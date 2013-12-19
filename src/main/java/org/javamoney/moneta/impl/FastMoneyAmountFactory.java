package org.javamoney.moneta.impl;

import java.math.BigDecimal;
import java.math.RoundingMode;

import javax.money.CurrencyUnit;
import javax.money.MonetaryAmount;
import javax.money.MonetaryContext;
import javax.money.MonetaryContext.AmountFlavor;

import org.javamoney.moneta.FastMoney;

public class FastMoneyAmountFactory extends AbstractAmountFactory{

	@Override
	protected MonetaryAmount create(CurrencyUnit currency, Number number,
			MonetaryContext monetaryContext) {
		return FastMoney.of(currency, number);
	}

	@Override
	public Class<FastMoney> getAmountType() {
		return FastMoney.class;
	}

	@Override
	protected MonetaryContext loadDefaultMonetaryContext() {
		return new MonetaryContext.Builder(FastMoney.class).setPrecision(18)
				.setMaxScale(5).setFixedScale(true).setAttribute(RoundingMode.HALF_EVEN)
				.setFlavor(AmountFlavor.PERFORMANCE).build();
	}

	@Override
	protected MonetaryContext loadMaxMonetaryContext() {
		return new MonetaryContext.Builder(FastMoney.class).setPrecision(18)
				.setMaxScale(5).setFixedScale(true).setAttribute(RoundingMode.HALF_EVEN)
				.setFlavor(AmountFlavor.PERFORMANCE).build();
	}

}
