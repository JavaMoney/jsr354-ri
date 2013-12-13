package org.javamoney.moneta.impl;

import java.math.BigDecimal;

import javax.money.CurrencyUnit;
import javax.money.MonetaryAmount;
import javax.money.MonetaryContext;
import javax.money.spi.MonetaryAmountProviderSpi;

import org.javamoney.moneta.Money;

public class DefaultAmountProvider implements MonetaryAmountProviderSpi {

	// TODO add support for FastMoney here...
	
	@Override
	public MonetaryAmount<?> getAmount(CurrencyUnit currency, long number,
			MonetaryContext monetaryContext) {
		return Money.of(currency, number, monetaryContext);
	}

	@Override
	public MonetaryAmount<?> getAmount(CurrencyUnit currency, double number,
			MonetaryContext monetaryContext) {
		return Money.of(currency, number, monetaryContext);
	}

	@Override
	public MonetaryAmount<?> getAmount(CurrencyUnit currency, Number number,
			MonetaryContext monetaryContext) {
		return Money.of(currency, number, monetaryContext);
	}

	@Override
	public MonetaryAmount<?> getAmountFrom(MonetaryAmount<?> amt,
			MonetaryContext monetaryContext) {
		return Money.of(amt.getCurrency(), amt.getNumber(BigDecimal.class),
				monetaryContext);
	}

}
