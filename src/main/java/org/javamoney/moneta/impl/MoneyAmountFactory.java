package org.javamoney.moneta.impl;

import java.math.BigDecimal;
import java.math.RoundingMode;

import javax.money.CurrencyUnit;
import javax.money.MonetaryAmount;
import javax.money.MonetaryContext;
import javax.money.MonetaryContext.AmountFlavor;

import org.javamoney.moneta.Money;

public class MoneyAmountFactory extends AbstractAmountFactory<Money> {

	@Override
	public Money getAmount(CurrencyUnit currency, long number,
			MonetaryContext<?> monetaryContext) {
		return Money.of(currency, number, MonetaryContext.from(monetaryContext, Money.class));
	}

	@Override
	public Money getAmount(CurrencyUnit currency, double number,
			MonetaryContext<?> monetaryContext) {
		return Money.of(currency, number, MonetaryContext.from(monetaryContext, Money.class));
	}

	@Override
	public Money getAmount(CurrencyUnit currency, Number number,
			MonetaryContext<?> monetaryContext) {
		return Money.of(currency, number, MonetaryContext.from(monetaryContext, Money.class));
	}

	@Override
	public Money getAmountFrom(MonetaryAmount<?> amt,
			MonetaryContext<?> monetaryContext) {
		return Money.of(amt.getCurrency(), amt.getNumber(BigDecimal.class),
				MonetaryContext.from(monetaryContext, Money.class));
	}

	@Override
	public Class<Money> getAmountType() {
		return Money.class;
	}

	@Override
	protected MonetaryContext<Money> loadDefaultMonetaryContext() {
		return new MonetaryContext.Builder().setPrecision(64)
				.setMaxScale(63).setAttribute(RoundingMode.HALF_EVEN)
				.setFlavor(AmountFlavor.PRECISION).build(Money.class);
	}

	@Override
	protected MonetaryContext<Money> loadMaxMonetaryContext() {
		return new MonetaryContext.Builder().setPrecision(0)
				.setMaxScale(-1).setAttribute(RoundingMode.HALF_EVEN)
				.setFlavor(AmountFlavor.PRECISION).build(Money.class);
	}

}
