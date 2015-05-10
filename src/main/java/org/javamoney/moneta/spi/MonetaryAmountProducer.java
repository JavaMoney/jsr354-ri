package org.javamoney.moneta.spi;

import static org.javamoney.moneta.function.MonetaryOperators.rounding;

import javax.money.CurrencyUnit;
import javax.money.MonetaryAmount;
import javax.money.MonetaryOperator;

import org.javamoney.moneta.FastMoney;
import org.javamoney.moneta.RoundedMoney;
import org.javamoney.moneta.function.MonetaryOperators;

/**
 *The producer of {@link MonetaryAmount} from {@link CurrencyUnit} and {@link Number}
 * @author Otavio Santana
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

	/**
	 * Returns the {@link MonetaryAmountProducer} that creates {@link MonetaryAmount}
	 * using the {@link FastMoney} implementation
	 * @see {@link FastMoneyProducer}
	 * @return the fast money producer
	 */
	static MonetaryAmountProducer fastMoneyProducer() {
		return new FastMoneyProducer();
	}

	/**
	 * Returns the {@link MonetaryAmountProducer} that creates {@link MonetaryAmount}
	 * using the {@link Money} implementation
	 * @see {@link MoneyProducer}
	 * @return the money producer
	 */
	static MonetaryAmountProducer moneyProducer() {
		return new MoneyProducer();
	}

	/**
	 * Returns the {@link MonetaryAmountProducer} that creates {@link MonetaryAmount}
	 * using the {@link RoundedMoney} implementation using {@link MonetaryOperators#rounding()}
	 * as rounding operator
	 * @see {@link RoundedMoneyProducer}
	 * @return the rounded money producer
	 */
	static MonetaryAmountProducer roundedMoneyProducer() {
		return new RoundedMoneyProducer(rounding());
	}

	/**
	 * Returns the {@link MonetaryAmountProducer} that creates {@link MonetaryAmount}
	 * using the {@link RoundedMoney} implementation using the operator
	 * as rounding operator
	 * @param operator
	 * @see {@link RoundedMoneyProducer}
	 * @return the rounded money producer
	 */
	static MonetaryAmountProducer roundedMoneyProducer(MonetaryOperator operator) {
		return new RoundedMoneyProducer(operator);
	}

}
