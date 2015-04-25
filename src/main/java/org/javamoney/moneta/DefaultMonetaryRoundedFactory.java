package org.javamoney.moneta;

import javax.money.CurrencyUnit;
import javax.money.MonetaryAmount;
import javax.money.MonetaryOperator;

class DefaultMonetaryRoundedFactory implements MonetaryRoundedFactory {

	private final MonetaryOperator roundingOperator;

	public DefaultMonetaryRoundedFactory(MonetaryOperator roundingOperator) {
		this.roundingOperator = roundingOperator;
	}

	@Override
	public MonetaryAmount create(Number number, CurrencyUnit currencyUnit) {
		return RoundedMoney.of(number, currencyUnit, roundingOperator);
	}

	@Override
	public MonetaryOperator getRoundingOperator() {
		return roundingOperator;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append(DefaultMonetaryRoundedFactory.class.getName()).append('{')
		.append("roundingOperator: ").append(roundingOperator).append('}');
		return sb.toString();
	}

}
