package org.javamoney.moneta.spi;

import javax.money.CurrencyUnit;
import javax.money.MonetaryAmount;

/**
 *The producer of {@link MonetaryAmount} from {@link CurrencyUnit} and {@link Number}
 * @author Otavio Santana
 * @see {@link FastMoneyProducer}
 * @see {@link MoneyProducer}
 * @see {@link RoundedMoneyProducer}
 */
@FunctionalInterface
public interface MonetaryAmountProducer {
	/**
	 * Creates a {@link MonetaryAmount} from {@link CurrencyUnit} and {@link Number}
	 * @param currency
	 * @param number
	 * @return a {@link MonetaryAmount} never null
	 * @throws NullPointerException if currency and Number is null
	 */
	MonetaryAmount create(CurrencyUnit currency, Number number);

}
