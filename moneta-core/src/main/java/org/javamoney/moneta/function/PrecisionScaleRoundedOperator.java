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
package org.javamoney.moneta.function;

import static java.util.Objects.requireNonNull;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.Objects;

import javax.money.MonetaryAmount;
import javax.money.MonetaryOperator;

/**
 * <p>This implementation uses a scale and {@link RoundingMode} and precision to does the rounding operations. The implementation will use both the <b>scale</b> and <b>precision</b>, in other words, the number of digits to the right of the decimal point and the number of digits.</p>
 * <p>The derived class will implements the {@link org.javamoney.moneta.RoundedMoney} with this rounding monetary operator</p>
 *  <pre>
 *   {@code
 *     int scale = 3;
 *     int precision = 5;
 *     MathContext mathContext = new MathContext(precision, RoundingMode.HALF_EVEN);
 *     MonetaryOperator monetaryOperator = PrecisionScaleRoundedOperator.of(scale, mathContext);
 *     CurrencyUnit real = Monetary.getCurrency("BRL");
 *     MonetaryAmount money = Money.of(BigDecimal.valueOf(35.34567), real);
 *     MonetaryAmount result = monetaryOperator.apply(money); // BRL 35.346
 *
 *    }
* </pre>
 * <p>Case the parameter in {@link MonetaryOperator#apply(MonetaryAmount)} be null, the apply will return a {@link NullPointerException}</p>
 * @author Otavio Santana
 * @see {@link PrecisionScaleRoundedOperator#of(int, MathContext)}
 * @see {@link org.javamoney.moneta.RoundedMoney}
 * @see {@link MonetaryOperator}
 * @see {@link BigDecimal#scale()}
 * @see {@link MathContext}
 * @see {@link BigDecimal#precision()}
 * @since 1.0.1
 * @deprecated Do not use, access is only provided for backward compatibility and will be removed.
 */
@Deprecated
public final class PrecisionScaleRoundedOperator implements MonetaryOperator {

	private final PrecisionContextRoundedOperator mathContextOperator;

	private final ScaleRoundedOperator scaleRoundedOperator;

	private final int scale;

	private final MathContext mathContext;

	private PrecisionScaleRoundedOperator(int scale, MathContext mathContext) {
		this.scale = scale;
		this.mathContext = mathContext;
		this.mathContextOperator = PrecisionContextRoundedOperator.of(mathContext);
		this.scaleRoundedOperator = ScaleRoundedOperator.of(scale, mathContext.getRoundingMode());
	}

	/**
	 * Creates the rounded Operator from scale and roundingMode
	 * @param mathContext
	 * @return the {@link MonetaryOperator} using the scale and {@link RoundingMode} used in parameter
	 * @throws NullPointerException when the {@link MathContext} is null
	 * @throws IllegalArgumentException if {@link MathContext#getPrecision()} is lesser than zero
	 * @throws IllegalArgumentException if {@link MathContext#getRoundingMode()} is {@link RoundingMode#UNNECESSARY}
	 * @see {@linkplain RoundingMode}
	 */
	public static PrecisionScaleRoundedOperator of(int scale, MathContext mathContext) {

		Objects.requireNonNull(mathContext);

		if(RoundingMode.UNNECESSARY.equals(mathContext.getRoundingMode())) {
		   throw new IllegalArgumentException("To create the ScaleRoundedOperator you cannot use the RoundingMode.UNNECESSARY on MathContext");
		}

		if(mathContext.getPrecision() <= 0) {
			throw new IllegalArgumentException("To create the ScaleRoundedOperator you cannot use the zero precision on MathContext");
		}
		return new PrecisionScaleRoundedOperator(scale, mathContext);
	}

	@Override
	public MonetaryAmount apply(MonetaryAmount amount) {
		return scaleRoundedOperator.apply(requireNonNull(amount)).with(mathContextOperator);
	}

	public int getScale() {
		return scale;
	}

	public MathContext getMathContext() {
		return mathContext;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append(PrecisionScaleRoundedOperator.class.getName()).append('{')
		.append("scale:").append(Integer.toString(scale)).append(',')
		.append("mathContext:").append(mathContext).append('}');
		return sb.toString();
	}

}