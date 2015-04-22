package org.javamoney.moneta;

import java.math.MathContext;

import javax.money.MonetaryAmount;
import javax.money.MonetaryOperator;

class DefaultRoundedOperator implements MonetaryOperator {

	private final MathContext mathContext;

	private DefaultRoundedOperator(MathContext mathContext) {
		this.mathContext = mathContext;
	}

	public static DefaultRoundedOperator of(MathContext mathContext) {
		return new DefaultRoundedOperator(mathContext);
	}

	@Override
	public MonetaryAmount apply(MonetaryAmount amount) {
		return amount;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append(DefaultRoundedOperator.class.getName()).append('{')
		.append("mathContext:").append(mathContext).append('}');
		return sb.toString();
	}

}
