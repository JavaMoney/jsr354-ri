package org.javamoney.moneta;

import javax.money.CurrencyUnit;
import javax.money.MonetaryOperator;

class DefaultRoundedMoneyFactory implements RoundedMoneyFactory {

	private final MonetaryOperator roundingOperator;

	public DefaultRoundedMoneyFactory(MonetaryOperator roundingOperator) {
		this.roundingOperator = roundingOperator;
	}

	@Override
	public RoundedMoney produces(Number number, CurrencyUnit currencyUnit) {
		return RoundedMoney.of(number, currencyUnit, roundingOperator);
	}

	@Override
	public MonetaryOperator getRoundingOperator() {
		return roundingOperator;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append(DefaultRoundedMoneyFactory.class.getName()).append('{')
		.append("roundingOperator: ").append(roundingOperator).append('}');
		return sb.toString();
	}

}
