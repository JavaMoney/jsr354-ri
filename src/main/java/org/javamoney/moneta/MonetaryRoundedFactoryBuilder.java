/**
 * Copyright (c) 2012, 2015, Credit Suisse (Anatole Tresch), Werner Keil and others by the @author tag.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package org.javamoney.moneta;

import org.javamoney.moneta.function.DefaultMonetaryRoundedFactory;
import org.javamoney.moneta.function.PrecisionContextRoundedOperator;
import org.javamoney.moneta.function.PrecisionScaleRoundedOperator;
import org.javamoney.moneta.function.ScaleRoundedOperator;

import java.math.MathContext;
import java.math.RoundingMode;

/**
 * Builder to {@link MonetaryRoundedFactory} once the {@link RoundingMode}, is possible
 * choose the <b>scale</b>, the number of digits to the right of the decimal point, and the <b>precision</b>, the total number of digits in a number or both.
 * @author Otavio Santana
 * @see {@link org.javamoney.moneta.function.MonetaryRoundedFactoryBuilder#withScale(int)}
 * @see {@link org.javamoney.moneta.function.MonetaryRoundedFactoryBuilder#withPrecision(int)}
 * @since 1.0.1
 * @deprecated Moved to function package.
 */
public final class MonetaryRoundedFactoryBuilder {

	private final RoundingMode roundingMode;

	MonetaryRoundedFactoryBuilder(RoundingMode roundingMode) {
		this.roundingMode = roundingMode;
	}

	/**
	 * Set the number of digits to the right of the decimal point
	 * @param scale
	 * @return {@link MonetaryRoundedFactoryWithScaleBuilder}
	 */
	public MonetaryRoundedFactoryWithScaleBuilder withScale(int scale) {
		return new MonetaryRoundedFactoryWithScaleBuilder(roundingMode, scale);
	}

	/**
	 * Set the total number of digits in a number
	 * @param precision
	 * @return @{@link MonetaryRoundedFactoryWithPrecisionBuilder}
	 */
	public MonetaryRoundedFactoryWithPrecisionBuilder withPrecision(int precision) {
		return new MonetaryRoundedFactoryWithPrecisionBuilder(roundingMode, precision);
	}

	/**
	 * Once the {@link RoundingMode} and scale informed, is possible create a {@link MonetaryRoundedFactory}
	 * or set the number of precision.
	 * @author Otavio Santana
	 *@see {@link MonetaryRoundedFactoryWithScaleBuilder#withPrecision(int)}
	 *@see {@link MonetaryRoundedFactoryWithScaleBuilder#build()}
	 */
	public static class MonetaryRoundedFactoryWithScaleBuilder {

		private final RoundingMode roundingMode;

		private final int scale;

		private MonetaryRoundedFactoryWithScaleBuilder(RoundingMode roundingMode, int scale) {
			this.roundingMode = roundingMode;
			this.scale = scale;
		}

		/**
		 * Make the {@link MonetaryRoundedFactory} using the {@link ScaleRoundedOperator} as rounding operator.
		 * @return {@link MonetaryRoundedFactory} with {@link ScaleRoundedOperator}
		 * @see {@link ScaleRoundedOperator}
		 * @see {@link MonetaryRoundedFactory}
		 */
		public MonetaryRoundedFactory build() {
			return new DefaultMonetaryRoundedFactory(ScaleRoundedOperator.of(scale, roundingMode));
		}

		/**
		 * Set the total number of digits in a number
		 * @param precision
		 * @return {@link MonetaryRoundedFactoryWithPrecisionBuilder}
		 */
		public MonetaryRoundedFactoryWithPrecisionScaleBuilder withPrecision(int precision) {
			MonetaryRoundedFactoryWithPrecisionScaleBuilder builder = new MonetaryRoundedFactoryWithPrecisionScaleBuilder(roundingMode);
			builder.scale = this.scale;
			builder.precision = precision;
			return builder;
		}

	}

	/**
	 * Once the {@link RoundingMode} and precision informed, is possible create a {@link MonetaryRoundedFactory}
	 * or set the number of scale.
	 * @author Otavio Santana
	 *@see {@link MonetaryRoundedFactoryWithPrecisionBuilder#withScale(int)}
	 *@see {@link MonetaryRoundedFactoryWithPrecisionBuilder#build()}
	 */
	public static class MonetaryRoundedFactoryWithPrecisionBuilder {

		private final int precision;

		private final RoundingMode roundingMode;

		private MonetaryRoundedFactoryWithPrecisionBuilder(RoundingMode roundingMode, int precision) {
			this.roundingMode = roundingMode;
			this.precision = precision;
		}
		/**
		 * Set the number of digits to the right of the decimal point
		 * @param scale
		 * @return {@link MonetaryRoundedFactoryWithPrecisionScaleBuilder}
		 */
		public MonetaryRoundedFactoryWithPrecisionScaleBuilder withScale(int scale) {
			MonetaryRoundedFactoryWithPrecisionScaleBuilder builder = new MonetaryRoundedFactoryWithPrecisionScaleBuilder(roundingMode);
			builder.precision = this.precision;
			builder.scale = scale;
			return builder;
		}

		/**
		 * Make the {@link MonetaryRoundedFactory} using the {@link PrecisionContextRoundedOperator} as rounding operator.
		 * @return {@link MonetaryRoundedFactory} with {@link PrecisionContextRoundedOperator}
		 * @see {@link PrecisionContextRoundedOperator}
		 * @see {@link MonetaryRoundedFactory}
		 */
		public MonetaryRoundedFactory build() {
			MathContext mathContext = new MathContext(precision, roundingMode);
			return new DefaultMonetaryRoundedFactory(PrecisionContextRoundedOperator.of(mathContext));
		}

	}

	/**
	 * Once the {@link RoundingMode}, precision and scale informed, the next step will build a {@link MonetaryRoundedFactory}
	 * with all these information.
	 * @author Otavio Santana
	 */
	public static class MonetaryRoundedFactoryWithPrecisionScaleBuilder {

		private int scale;

		private int precision;

		private final RoundingMode roundingMode;

		public MonetaryRoundedFactoryWithPrecisionScaleBuilder(
				RoundingMode roundingMode) {
			this.roundingMode = roundingMode;
		}

		/**
		 * Make the {@link MonetaryRoundedFactory} using the {@link PrecisionScaleRoundedOperator} as rounding operator.
		 * @return {@link MonetaryRoundedFactory} with {@link PrecisionScaleRoundedOperator}
		 * @see {@link PrecisionContextRoundedOperator}
		 * @see {@link PrecisionScaleRoundedOperator}
		 */
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