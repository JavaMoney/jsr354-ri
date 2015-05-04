package org.javamoney.moneta.function;

import java.util.Objects;

import javax.money.CurrencyUnit;
import javax.money.MonetaryAmount;
import javax.money.MonetaryOperator;

public class ExchangeCurrencyOperator implements MonetaryOperator {

	private final CurrencyUnit currency;

	ExchangeCurrencyOperator(CurrencyUnit currency) {
		this.currency = currency;
	}

	@Override
	public MonetaryAmount apply(MonetaryAmount amount) {
		Objects.requireNonNull(amount, "Amount required.");
		return amount.getFactory().setCurrency(currency).create();
	}

}
