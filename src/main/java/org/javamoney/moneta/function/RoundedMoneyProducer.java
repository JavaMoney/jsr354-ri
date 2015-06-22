package org.javamoney.moneta.function;

import java.util.Objects;

import javax.money.CurrencyUnit;
import javax.money.MonetaryAmount;
import javax.money.MonetaryOperator;

import org.javamoney.moneta.RoundedMoney;

/**
 * The implementation of {@link MonetaryAmountProducer} that creates {@link MonetaryAmount}
 * using {@link RoundedMoney} using the {@link MonetaryOperator} as rounding operator
 * @see {@link RoundedMoneyProducer#RoundedMoneyProducer(MonetaryOperator)}
 * @author Otavio Santana
 */
public final class RoundedMoneyProducer implements MonetaryAmountProducer {

	private final MonetaryOperator operator;

	/**
	 * Creates this producer using this operator
	 * as rounding operator in all MonetaryAmount produced.
	 * @param operator
	 * @throws NullPointerException if operator is null
	 */
	public RoundedMoneyProducer(MonetaryOperator operator) {
		this.operator = Objects.requireNonNull(operator);
	}

	/**
	 * Returns the {@link MonetaryAmountProducer} that creates {@link MonetaryAmount}
	 * using the {@link RoundedMoney} implementation using {@link MonetaryOperators#rounding()}
	 * as rounding operator
	 * @see {@link RoundedMoneyProducer}
	 * @return the rounded money producer
	 */
	public RoundedMoneyProducer() {
		this.operator = MonetaryOperators.rounding();
	}

	@Override
	public MonetaryAmount create(CurrencyUnit currency, Number number) {
		return RoundedMoney.of(Objects.requireNonNull(number), Objects.requireNonNull(currency), operator);
	}

	public MonetaryOperator getOperator() {
		return operator;
	}

}
