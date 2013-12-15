package org.javamoney.moneta.impl;

import java.math.BigDecimal;
import java.math.RoundingMode;

import javax.money.CurrencyUnit;
import javax.money.MonetaryAmount;
import javax.money.MonetaryContext;
import javax.money.MonetaryContext.AmountFlavor;

import org.javamoney.moneta.FastMoney;

public class FastMoneyAmountFactory extends AbstractAmountFactory<FastMoney> {

	@Override
	public FastMoney getAmount(CurrencyUnit currency, long number,
			MonetaryContext<?> monetaryContext) {
		// Check for exceedeing monetaryContext here...
		return FastMoney.of(currency, number);
	}

	@Override
	public FastMoney getAmount(CurrencyUnit currency, double number,
			MonetaryContext<?> monetaryContext) {
		// Check for exceedeing monetaryContext here...
		return FastMoney.of(currency, number);
	}

	@Override
	public FastMoney getAmount(CurrencyUnit currency, Number number,
			MonetaryContext<?> monetaryContext) {
		// Check for exceedeing monetaryContext here...
		return FastMoney.of(currency, number);
	}

	@Override
	public FastMoney getAmountFrom(MonetaryAmount<?> amt,
			MonetaryContext<?> monetaryContext) {
		// Check for exceedeing monetaryContext here...
		return FastMoney.of(amt.getCurrency(), amt.getNumber(BigDecimal.class));
	}

	@Override
	public Class<FastMoney> getAmountType() {
		return FastMoney.class;
	}

	@Override
	protected MonetaryContext<FastMoney> loadDefaultMonetaryContext() {
		return new MonetaryContext.Builder().setPrecision(18)
				.setMaxScale(5).setFixedScale(true).setAttribute(RoundingMode.HALF_EVEN)
				.setFlavor(AmountFlavor.PERFORMANCE).build(FastMoney.class);
	}

	@Override
	protected MonetaryContext<FastMoney> loadMaxMonetaryContext() {
		return new MonetaryContext.Builder().setPrecision(18)
				.setMaxScale(5).setFixedScale(true).setAttribute(RoundingMode.HALF_EVEN)
				.setFlavor(AmountFlavor.PERFORMANCE).build(FastMoney.class);
	}

}
