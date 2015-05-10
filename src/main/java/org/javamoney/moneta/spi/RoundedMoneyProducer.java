package org.javamoney.moneta.spi;

import java.util.Objects;

import javax.money.CurrencyUnit;
import javax.money.MonetaryAmount;
import javax.money.MonetaryOperator;

import org.javamoney.moneta.RoundedMoney;

public class RoundedMoneyProducer implements MonetaryAmountProducer {

	private final MonetaryOperator operator;

	public RoundedMoneyProducer(MonetaryOperator operator) {
		this.operator = Objects.requireNonNull(operator);
	}

	@Override
	public MonetaryAmount create(CurrencyUnit currency, Number number) {
		return RoundedMoney.of(Objects.requireNonNull(number), Objects.requireNonNull(currency), operator);
	}

}
