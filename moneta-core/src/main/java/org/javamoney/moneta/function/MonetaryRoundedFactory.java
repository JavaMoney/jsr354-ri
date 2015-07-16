package org.javamoney.moneta.function;

import static java.util.Objects.requireNonNull;

import java.math.MathContext;
import java.math.RoundingMode;

import javax.money.CurrencyUnit;
import javax.money.MonetaryAmount;
import javax.money.MonetaryOperator;

import org.javamoney.moneta.RoundedMoney;

/**
 * this interface is used to create {@link RoundedMoney} using the {@link MonetaryOperator} as rounding.
 * @see {@link MonetaryRoundedFactory#of(MathContext)}
 * @see {@link MonetaryRoundedFactory#of(MonetaryOperator)}
 * @see {@link MonetaryRoundedFactory#withRoundingMode(RoundingMode)}
 * @author Otavio Santana
 *
 */
interface MonetaryRoundedFactory {

	/**
	 * return the {@link MonetaryOperator} as rounding operator
	 * @return the rounding operator
	 */
	MonetaryOperator getRoundingOperator();

	/**
	 * Create a {@link MonetaryAmount} with {@link Number}, {@link CurrencyUnit} and
	 * the {@link MonetaryOperator} as rounding operator given in this factory with the
	 * {@link MonetaryRoundedFactory#getRoundingOperator()}. The implementation will {@link RoundedMoney}
	 * @param number
	 * @param currencyUnit
	 * @return the {@link MonetaryAmount} from number and {@link CurrencyUnit}
	 */
	MonetaryAmount create(Number number, CurrencyUnit currencyUnit);

	/**
	 * Create a factory to {@link MonetaryRoundedFactoryBuilder} with this factory is possible make
	 * a custom {@link MonetaryOperator} as rounding operator, setting the precision, scale or both.
	 * @param roundingMode
	 * @see {@link ScaleRoundedOperator}
	 * @see {@link PrecisionContextRoundedOperator}
	 * @see {@link PrecisionScaleRoundedOperator}
	 * @see {@link RoundingMode}
	 * @return the builder to set scale, precision or both
	 * @throws NullPointerException if roundingMode is null
	 */
	static MonetaryRoundedFactoryBuilder withRoundingMode(RoundingMode roundingMode) {
		return new MonetaryRoundedFactoryBuilder(requireNonNull(roundingMode));
	}

	/**
	 * Create the {@link MonetaryRoundedFactory} using the {@link PrecisionContextRoundedOperator} as rounding operator.
	 * @param mathContext the mathContext that will be used to create the {@link PrecisionContextRoundedOperator}
	 * @see {@link PrecisionContextRoundedOperator#of(MathContext)}
	 * @see {@link PrecisionContextRoundedOperator}
	 * @return the factory using the MathContextRoundedOperator
	 * @throws NullPointerException if mathContext is null
	 */
	static MonetaryRoundedFactory of(MathContext mathContext) {
		return new DefaultMonetaryRoundedFactory(PrecisionContextRoundedOperator.of(requireNonNull(mathContext)));
	}

	/**
	 * Create the {@link MonetaryRoundedFactory} using a custom {@link MonetaryOperator} as rounding operator.
	 * @param roundingOperator a custom {@link MonetaryOperator} that will be used in this factory
	 * @return the factory using the MathContextRoundedOperator
	 * @throws NullPointerException if roundingOperator is null
	 */
	static MonetaryRoundedFactory of(MonetaryOperator roundingOperator) {
		return new DefaultMonetaryRoundedFactory(requireNonNull(roundingOperator));
	}
}
