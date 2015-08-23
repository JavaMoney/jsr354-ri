package org.javamoney.moneta.function;

import java.util.Objects;

import javax.money.CurrencyUnit;
import javax.money.MonetaryAmount;

import org.javamoney.moneta.Money;

/**
 * The implementation of {@link MonetaryAmountProducer} that creates {@link MonetaryAmount}
 * using {@link Money} implementation.
 * @author Otavio Santana
 * @since 1.0.1
 */
public final class MoneyProducer implements MonetaryAmountProducer {

	@Override
	public MonetaryAmount create(CurrencyUnit currency, Number number) {
		return Money.of(Objects.requireNonNull(number), Objects.requireNonNull(currency));
	}

}
