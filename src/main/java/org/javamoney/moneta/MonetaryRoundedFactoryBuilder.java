package org.javamoney.moneta;

import java.math.MathContext;
import java.math.RoundingMode;

public class MonetaryRoundedFactoryBuilder {


	private final RoundingMode roundingMode;

	MonetaryRoundedFactoryBuilder(RoundingMode roundingMode) {
		this.roundingMode = roundingMode;
	}

	public MonetaryRoundedFactoryWithScaleBuilder withScale(int scale) {
		return new MonetaryRoundedFactoryWithScaleBuilder(roundingMode, scale);
	}

	public MonetaryRoundedFactoryWithPrecisionBuilder withPrecision(int precision) {
		return new MonetaryRoundedFactoryWithPrecisionBuilder(roundingMode, precision);
	}

	public static class MonetaryRoundedFactoryWithScaleBuilder {

		private final RoundingMode roundingMode;

		private final int scale;

		private MonetaryRoundedFactoryWithScaleBuilder(RoundingMode roundingMode, int scale) {
			this.roundingMode = roundingMode;
			this.scale = scale;
		}

		public MonetaryRoundedFactory build() {
			return new DefaultMonetaryRoundedFactory(ScaleRoundedOperator.of(scale, roundingMode));
		}

		public MonetaryRoundedFactoryWithPrecisionScaleBuilder withPrecision(int precision) {
			MonetaryRoundedFactoryWithPrecisionScaleBuilder builder = new MonetaryRoundedFactoryWithPrecisionScaleBuilder(roundingMode);
			builder.scale = this.scale;
			builder.precision = precision;
			return builder;
		}

	}

	public static class MonetaryRoundedFactoryWithPrecisionBuilder {

		private final int precision;

		private final RoundingMode roundingMode;

		private MonetaryRoundedFactoryWithPrecisionBuilder(RoundingMode roundingMode, int precision) {
			this.roundingMode = roundingMode;
			this.precision = precision;
		}

		public MonetaryRoundedFactoryWithPrecisionScaleBuilder withScale(int scale) {
			MonetaryRoundedFactoryWithPrecisionScaleBuilder builder = new MonetaryRoundedFactoryWithPrecisionScaleBuilder(roundingMode);
			builder.precision = this.precision;
			builder.scale = scale;
			return builder;
		}

		public MonetaryRoundedFactory build() {
			MathContext mathContext = new MathContext(precision, roundingMode);
			return new DefaultMonetaryRoundedFactory(MathContextRoundedOperator.of(mathContext));
		}

	}

	public static class MonetaryRoundedFactoryWithPrecisionScaleBuilder {

		private int scale;

		private int precision;

		private final RoundingMode roundingMode;

		public MonetaryRoundedFactoryWithPrecisionScaleBuilder(
				RoundingMode roundingMode) {
			this.roundingMode = roundingMode;
		}

		public MonetaryRoundedFactory build() {
			MathContext mathContext = new MathContext(precision, roundingMode);
			return new DefaultMonetaryRoundedFactory(PrecisionScaleRoundedOperator.of(scale, mathContext));
		}



	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append(MonetaryRoundedFactoryBuilder.class.getName()).append('{')
		.append("roundingMode: ").append(roundingMode).append('}');
		return sb.toString();
	}

}