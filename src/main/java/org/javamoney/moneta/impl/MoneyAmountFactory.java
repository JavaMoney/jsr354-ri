package org.javamoney.moneta.impl;

import java.math.BigDecimal;
import java.math.RoundingMode;

import javax.money.CurrencyUnit;
import javax.money.MonetaryAmount;
import javax.money.MonetaryContext;
import javax.money.MonetaryContext.AmountFlavor;

import org.javamoney.moneta.Money;

public class MoneyAmountFactory extends AbstractAmountFactory {

	@Override
	protected MonetaryAmount create(CurrencyUnit currency, Number number,
			MonetaryContext monetaryContext) {
		return Money.of(currency, number, MonetaryContext.from(monetaryContext, Money.class));
	}

	@Override
	public Class<Money> getAmountType() {
		return Money.class;
	}

	@Override
	protected MonetaryContext loadDefaultMonetaryContext() {
		return new MonetaryContext.Builder(Money.class).setPrecision(64)
				.setMaxScale(63).setAttribute(RoundingMode.HALF_EVEN)
				.setFlavor(AmountFlavor.PRECISION).build();
	}

	@Override
	protected MonetaryContext loadMaxMonetaryContext() {
		return new MonetaryContext.Builder(Money.class).setPrecision(0)
				.setMaxScale(-1).setAttribute(RoundingMode.HALF_EVEN)
				.setFlavor(AmountFlavor.PRECISION).build();
	}

}
