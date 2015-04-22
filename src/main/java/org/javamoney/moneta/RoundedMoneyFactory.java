package org.javamoney.moneta;

import javax.money.CurrencyUnit;
import javax.money.MonetaryOperator;

public interface RoundedMoneyFactory {

	MonetaryOperator getRoundingOperator();

	RoundedMoney produces(Number value, CurrencyUnit currencyUnit);

	static RoundedMoneyFactoryBuilder build() {
		return new RoundedMoneyFactoryBuilder();
	}
}
