package org.javamoney.moneta.internal;

import java.math.RoundingMode;

import javax.money.CurrencyUnit;
import javax.money.MonetaryContext;
import javax.money.MonetaryContext.AmountFlavor;

import org.javamoney.moneta.RoundedMoney;
import org.javamoney.moneta.spi.AbstractAmountFactory;

public class RoundedMoneyAmountFactory extends
		AbstractAmountFactory<RoundedMoney> {

	@Override
	protected RoundedMoney create(CurrencyUnit currency, Number number,
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
				.setFlavor(AmountFlavor.DECORATION)
				.setFlavor(AmountFlavor.UNDEFINED).build();
	}

	@Override
	protected MonetaryContext loadMaxMonetaryContext() {
		return new MonetaryContext.Builder(RoundedMoney.class).setPrecision(0)
				.setAttribute(RoundingMode.HALF_EVEN)
				.setFlavor(AmountFlavor.DECORATION)
				.setFlavor(AmountFlavor.UNDEFINED).build();
	}

}
