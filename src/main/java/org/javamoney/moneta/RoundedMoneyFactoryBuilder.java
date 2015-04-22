package org.javamoney.moneta;

import java.math.MathContext;
import java.math.RoundingMode;
import java.util.Objects;

import javax.money.MonetaryOperator;

class RoundedMoneyFactoryBuilder {

	private int scale;

	private MathContext mathContext;

	private RoundingMode roundingMode;

	private MonetaryOperator roundingOperator;

	RoundedMoneyFactoryBuilder() {}

	public RoundedMoneyFactoryBuilder withScale(int scale) {
		if (scale <= 0){
			throw new IllegalArgumentException("The scale should be greater than zero");
		}
		this.scale = scale;
		return this;
	}

	public RoundedMoneyFactoryBuilder withMathContext(MathContext mathContext) {
		this.mathContext = Objects.requireNonNull(mathContext);
		return this;
	}

	public RoundedMoneyFactoryBuilder withRoundingMode(RoundingMode roundingMode) {
		this.roundingMode = Objects.requireNonNull(roundingMode);
		return this;
	}

	public RoundedMoneyFactoryBuilder witRoundingOperator(MonetaryOperator roundingOperator) {
		this.roundingOperator = Objects.requireNonNull(roundingOperator);
		return this;
	}

	public RoundedMoneyFactory build() {
		if (Objects.isNull(mathContext) && Objects.isNull(roundingMode) && Objects.isNull(roundingOperator)) {
			throw new IllegalStateException("You should inform either mathContext or roundingMode with scale");
		}

		if(Objects.nonNull(roundingOperator)) {
			return new DefaultRoundedMoneyFactory(roundingOperator);
		}

		if(Objects.nonNull(mathContext)) {
			return new DefaultRoundedMoneyFactory(DefaultRoundedOperator.of(mathContext));
		}

		if (Objects.nonNull(roundingMode) && scale == 0) {
			throw new IllegalStateException("You should inform scale");
		}

		MathContext mathContext = new MathContext(scale, roundingMode);
		return new DefaultRoundedMoneyFactory(DefaultRoundedOperator.of(mathContext));

	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append(RoundedMoneyFactoryBuilder.class.getName()).append('{')
		.append("scale: ").append(scale).append(',')
		.append("mathContext: ").append(mathContext).append(',')
		.append("roundingMode: ").append(roundingMode).append('}');
		return sb.toString();
	}

}