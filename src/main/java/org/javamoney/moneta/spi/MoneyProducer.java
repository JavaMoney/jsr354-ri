package org.javamoney.moneta.spi;

import java.util.Objects;

import javax.money.CurrencyUnit;
import javax.money.MonetaryAmount;

import org.javamoney.moneta.Money;

/**
 * The implementation of {@link MonetaryAmountProducer} that creates {@link MonetaryAmount}
 * using {@link Money}
 * @author Otavio Santana
 */
public final class MoneyProducer implements MonetaryAmountProducer {

	@Override
	public MonetaryAmount create(CurrencyUnit currency, Number number) {
		return Money.of(Objects.requireNonNull(number), Objects.requireNonNull(currency));
	}

}
