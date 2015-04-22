package org.javamoney.moneta;

import java.math.BigDecimal;
import java.math.MathContext;
import java.util.Objects;

import javax.money.MonetaryAmount;
import javax.money.MonetaryOperator;

/**
 * <p>This implementation uses a {@link MathContext} to does the rounding operations. The implementation will use the <b>precision</b>, in other words, the total number of digits in a number</p>
 * <p>The derived class will implements the {@link RoundedMoney} with this rounding monetary operator</p>
 * <p>Case the parameter in {@link MonetaryOperator#apply(MonetaryAmount)} be null, the apply will return a {@link NullPointerException}</p>
 * @author Otavio Santana
 * @see {@link MathContextRoundedOperator#of(MathContext)}
 * @see {@link RoundedMoney}
 * @see {@link MonetaryOperator}
 * @see {@link BigDecimal#precision()}
 */
public final class MathContextRoundedOperator implements MonetaryOperator {

	private final MathContext mathContext;

	private MathContextRoundedOperator(MathContext mathContext) {
		this.mathContext = mathContext;
	}

	/**
	 * Creates the rounded Operator from mathContext
	 * @param mathContext
	 * @return the {@link MonetaryOperator} using the {@link MathContext} used in parameter
	 * @throws NullPointerException when the {@link MathContext} is null
	 * @see {@linkplain MathContext}
	 */
	public static MathContextRoundedOperator of(MathContext mathContext) {
		return new MathContextRoundedOperator(Objects.requireNonNull(mathContext));
	}

	@Override
	public MonetaryAmount apply(MonetaryAmount amount) {
		RoundedMoney roundedMoney = RoundedMoney.from(Objects.requireNonNull(amount));
		BigDecimal numberValue = roundedMoney.getNumber().numberValue(BigDecimal.class);
		BigDecimal numberRounded = numberValue.round(mathContext);
		return RoundedMoney.of(numberRounded, roundedMoney.getCurrency(), this);
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append(MathContextRoundedOperator.class.getName()).append('{')
		.append("mathContext:").append(mathContext).append('}');
		return sb.toString();
	}

}
