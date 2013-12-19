package org.javamoney.moneta.impl;

import java.math.RoundingMode;

import javax.money.CurrencyUnit;
import javax.money.MonetaryAmount;
import javax.money.MonetaryContext;
import javax.money.MonetaryContext.AmountFlavor;

import org.javamoney.moneta.RoundedMoney;

public class RoundedMoneyAmountFactory extends AbstractAmountFactory {

	@Override
	protected MonetaryAmount create(CurrencyUnit currency, Number number,
			MonetaryContext monetaryContext) {
		return RoundedMoney.of(currency, number);
	}

	@Override
	public Class<RoundedMoney> getAmountType() {
		return RoundedMoney.class;
	}

	@Override
	protected MonetaryContext loadDefaultMonetaryContext() {
		return new MonetaryContext.Builder(RoundedMoney.class).setPrecision(0)
				.setAttribute(RoundingMode.HALF_EVEN)
				.setFlavor(AmountFlavor.UNDEFINED).build();
	}

	@Override
	protected MonetaryContext loadMaxMonetaryContext() {
		return new MonetaryContext.Builder(RoundedMoney.class).setPrecision(0)
				.setAttribute(RoundingMode.HALF_EVEN)
				.setFlavor(AmountFlavor.UNDEFINED).build();
	}

}
