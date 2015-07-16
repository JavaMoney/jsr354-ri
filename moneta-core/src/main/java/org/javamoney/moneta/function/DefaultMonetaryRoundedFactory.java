package org.javamoney.moneta.function;

import static java.util.Objects.requireNonNull;

import javax.money.CurrencyUnit;
import javax.money.MonetaryAmount;
import javax.money.MonetaryOperator;

import org.javamoney.moneta.RoundedMoney;

/**
 * The default implementation to {@link MonetaryRoundedFactory}, this implementation returns the {@link RoundedMoney}.
 * @see {@link MonetaryRoundedFactory#create(Number, CurrencyUnit)}
 * @author Otavio Santana
 * 
 * @deprecated see https://java.net/jira/browse/JAVAMONEY-126. Could go to a module like javamoney-calc.
 */
class DefaultMonetaryRoundedFactory implements MonetaryRoundedFactory {

	private final MonetaryOperator roundingOperator;

	public DefaultMonetaryRoundedFactory(MonetaryOperator roundingOperator) {
		this.roundingOperator = roundingOperator;
	}

	@Override
	public MonetaryAmount create(Number number, CurrencyUnit currencyUnit) {
		return RoundedMoney.of(requireNonNull(number), requireNonNull(currencyUnit), roundingOperator);
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
