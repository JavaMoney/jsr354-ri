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
 * <p>The derived class will implements the {@link RoundedMoney} with this rounding monetary operator</p>
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
 * @see {@link PrecisionScaleRoundedOperator#of(MathContext)}
 * @see {@link RoundedMoney}
 * @see {@link MonetaryOperator}
 * @see {@link BigDecimal#scale()}
 * @see {@link MathContext}
 * @see {@link BigDecimal#precision()}
 */
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
	 * @return the {@link MonetaryOperator} using the scale and {@link roundingMode} used in parameter
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