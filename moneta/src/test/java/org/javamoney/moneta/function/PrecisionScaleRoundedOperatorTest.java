package org.javamoney.moneta.function;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;
import static org.testng.Assert.fail;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;

import javax.money.CurrencyUnit;
import javax.money.Monetary;
import javax.money.MonetaryAmount;

import org.javamoney.moneta.Money;
import org.javamoney.moneta.RoundedMoney;
import org.testng.annotations.Test;

public class PrecisionScaleRoundedOperatorTest {

	@Test(expectedExceptions = NullPointerException.class)
	public void shouldReturnNullPointerExceptionWhenParameterIsNull() {
		PrecisionScaleRoundedOperator.of(0, null);
		fail();
	}

	@Test(expectedExceptions = IllegalArgumentException.class)
	public void shouldReturnErrorWhenScaleIsUNNECESSARY() {
		MathContext mathContext = new MathContext(2, RoundingMode.UNNECESSARY);
		PrecisionScaleRoundedOperator.of(0, mathContext);
		fail();
	}

	@Test(expectedExceptions = IllegalArgumentException.class)
	public void shouldReturnErrorWhenPrecisionIsZero() {
		MathContext mathContext = new MathContext(0, RoundingMode.HALF_EVEN);
		PrecisionScaleRoundedOperator.of(0, mathContext);
		fail();
	}

	@Test(expectedExceptions = IllegalArgumentException.class)
	public void shouldReturnErrorWhenPrecisionIsLesserThanZero() {
		MathContext mathContext = new MathContext(-1, RoundingMode.HALF_EVEN);
		PrecisionScaleRoundedOperator.of(0, mathContext);
		fail();
	}

	@Test(expectedExceptions = NullPointerException.class)
	public void shouldReturnErrorWhenParameterIsNUll() {
		PrecisionScaleRoundedOperator monetaryOperator = PrecisionScaleRoundedOperator.of(0, MathContext.DECIMAL32);
		monetaryOperator.apply(null);
		fail();
	}


	@Test
	public void shouldRoundedMonetaryOperatorWhenTheImplementationIsMoney() {
		int scale = 3;
		int precision = 5;

		CurrencyUnit real = Monetary.getCurrency("BRL");
		BigDecimal valueOf = BigDecimal.valueOf(35.34567);
		MonetaryAmount money = Money.of(valueOf, real);

		MathContext mathContext = new MathContext(precision, RoundingMode.HALF_EVEN);
		PrecisionScaleRoundedOperator monetaryOperator = PrecisionScaleRoundedOperator.of(scale, mathContext);

		MonetaryAmount result = monetaryOperator.apply(money);
		assertTrue(RoundedMoney.class.isInstance(result));
		assertEquals(result.getCurrency(), real);
		assertEquals(result.getNumber().getScale(), scale);
		assertEquals(result.getNumber().getPrecision(), precision);
		assertEquals(BigDecimal.valueOf(35.346), result.getNumber().numberValue(BigDecimal.class));



	}
}
