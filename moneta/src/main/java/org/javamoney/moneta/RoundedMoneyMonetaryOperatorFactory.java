package org.javamoney.moneta;

import static java.util.Optional.ofNullable;

import java.math.MathContext;
import java.math.RoundingMode;
import java.util.Objects;

import javax.money.Monetary;
import javax.money.MonetaryContext;
import javax.money.MonetaryContextBuilder;
import javax.money.MonetaryOperator;
import javax.money.RoundingQueryBuilder;

/**
 * Factory of default {@link RoundedMoney}
 * @see {@link RoundedMoney}
 * @see {@link RoundedMoney#divide(double)}
 * @author Otavio Santana
 */
enum RoundedMoneyMonetaryOperatorFactory {

INSTANCE;

	private static final int SCALE_DEFAULT = 2;

	MonetaryOperator getDefaultMonetaryOperator(MonetaryOperator rounding,
			MonetaryContext context,
			MonetaryContextBuilder monetaryContextBuilder) {

		if (Objects.nonNull(rounding)) {
			return rounding;
		}
		if (Objects.nonNull(context)) {
			return createUsingMonetaryContext(context, monetaryContextBuilder);
		} else {
			return Monetary.getDefaultRounding();
		}
	}

	private MonetaryOperator createUsingMonetaryContext(
			MonetaryContext context,
			MonetaryContextBuilder monetaryContextBuilder) {

		MathContext mathContext = context.get(MathContext.class);
		if (Objects.isNull(mathContext)) {

			RoundingMode roundingMode = context.get(RoundingMode.class);
			if (Objects.nonNull(roundingMode)) {

				int scale = ofNullable(context.getInt("scale")).orElse(SCALE_DEFAULT);

				monetaryContextBuilder.set(roundingMode);
				monetaryContextBuilder.set("scale", scale);
				return Monetary.getRounding(RoundingQueryBuilder.of()
						.setScale(scale).set(roundingMode).build());
			} else {
				return Monetary.getDefaultRounding();
			}
		} else {
			monetaryContextBuilder.set(mathContext.getRoundingMode());
			monetaryContextBuilder.set("scale", SCALE_DEFAULT);
			return Monetary.getRounding(RoundingQueryBuilder.of().set(mathContext)
					.setScale(SCALE_DEFAULT).build());
		}
	}
}
