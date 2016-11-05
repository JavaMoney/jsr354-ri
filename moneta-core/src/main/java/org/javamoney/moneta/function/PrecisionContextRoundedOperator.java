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

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.Objects;

import javax.money.MonetaryAmount;
import javax.money.MonetaryOperator;

import org.javamoney.moneta.RoundedMoney;

/**
 * <p>This implementation uses a {@link MathContext} to does the rounding operations. The implementation will use the <b>precision</b>, in other words, the total number of digits in a number</p>
 * <p>The derived class will implements the {@link RoundedMoney} with this rounding monetary operator</p>
 *  <pre>
 *   {@code
 *
 *     MathContext mathContext = new MathContext(4, RoundingMode.HALF_EVEN);
 *     MonetaryOperator monetaryOperator = PrecisionContextRoundedOperator.of(mathContext);
 *     CurrencyUnit real = Monetary.getCurrency("BRL");
 *     MonetaryAmount money = Money.of(BigDecimal.valueOf(35.34567), real);
 *     MonetaryAmount result = monetaryOperator.apply(money); // BRL 35.35
 *
 *    }
* </pre>
* <p>Case the parameter in {@link MonetaryOperator#apply(MonetaryAmount)} be null, the apply will return a {@link NullPointerException}</p>
 * @author Otavio Santana
 * @see {@link PrecisionContextRoundedOperator#of(MathContext)}
 * @see {@link RoundedMoney}
 * @see {@link MonetaryOperator}
 * @see {@link BigDecimal#precision()}
 * @since 1.0.1
 */
public final class PrecisionContextRoundedOperator implements MonetaryOperator {

	private final MathContext mathContext;

	private PrecisionContextRoundedOperator(MathContext mathContext) {
		this.mathContext = mathContext;
	}

	/**
	 * Creates the rounded Operator from mathContext
	 * @param mathContext
	 * @return the {@link MonetaryOperator} using the {@link MathContext} used in parameter
	 * @throws NullPointerException when the {@link MathContext} is null
	 * @throws IllegalArgumentException when the {@link MathContext#getPrecision()} is lesser than zero
	 * @throws IllegalArgumentException when the mathContext is {@link MathContext#getRoundingMode()} is {@link RoundingMode#UNNECESSARY}
	 * @see {@linkplain MathContext}
	 */
	public static PrecisionContextRoundedOperator of(MathContext mathContext) {

		Objects.requireNonNull(mathContext);

		if(RoundingMode.UNNECESSARY.equals(mathContext.getRoundingMode())) {
			   throw new IllegalArgumentException("To create the MathContextRoundedOperator you cannot use the RoundingMode.UNNECESSARY on MathContext");
		}

		if(mathContext.getPrecision() <= 0) {
				throw new IllegalArgumentException("To create the MathContextRoundedOperator you cannot use the zero precision on MathContext");
		}

		return new PrecisionContextRoundedOperator(mathContext);
	}

	@Override
	public MonetaryAmount apply(MonetaryAmount amount) {
		RoundedMoney roundedMoney = RoundedMoney.from(Objects.requireNonNull(amount));
		BigDecimal numberValue = roundedMoney.getNumber().numberValue(BigDecimal.class);
		BigDecimal numberRounded = numberValue.round(mathContext);
		return RoundedMoney.of(numberRounded, roundedMoney.getCurrency(), this);
	}

	public MathContext getMathContext() {
		return mathContext;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append(PrecisionContextRoundedOperator.class.getName()).append('{')
		.append("mathContext:").append(mathContext).append('}');
		return sb.toString();
	}

}
