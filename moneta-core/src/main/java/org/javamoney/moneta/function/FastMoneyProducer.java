package org.javamoney.moneta.function;

import java.util.Objects;

import javax.money.CurrencyUnit;
import javax.money.MonetaryAmount;

import org.javamoney.moneta.FastMoney;

/**
 * The implementation of {@link MonetaryAmountProducer} that creates {@link MonetaryAmount}
 * using {@link FastMoney} implementation.
 * @author Otavio Santana
 * @since 1.0.1
 */
public final class FastMoneyProducer implements MonetaryAmountProducer {

	@Override
	public MonetaryAmount create(CurrencyUnit currency, Number number) {
		return FastMoney.of(Objects.requireNonNull(number), Objects.requireNonNull(currency));
	}

}
