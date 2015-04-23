package org.javamoney.moneta;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;
import static org.testng.Assert.fail;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;

import javax.money.CurrencyUnit;
import javax.money.Monetary;
import javax.money.MonetaryAmount;

import org.testng.annotations.Test;

public class MathContextRoundedOperatorTest {

	@Test(expectedExceptions = NullPointerException.class)
	public void shouldReturnNullPointerExceptionWhenParameterIsNull() {
		MathContextRoundedOperator.of(null);
		fail();
	}

	@Test(expectedExceptions = IllegalArgumentException.class)
	public void shouldReturnErrorWhenScaleIsUNNECESSARY() {
		MathContext mathContext = new MathContext(2, RoundingMode.UNNECESSARY);
		MathContextRoundedOperator.of(mathContext);
		fail();
	}

	@Test(expectedExceptions = IllegalArgumentException.class)
	public void shouldReturnErrorWhenPrecisionIsZero() {
		MathContext mathContext = new MathContext(0, RoundingMode.HALF_EVEN);
		MathContextRoundedOperator.of(mathContext);
		fail();
	}

	@Test(expectedExceptions = NullPointerException.class)
	public void shouldReturnErrorWhenParameterIsNull() {
		MathContext mathContext = new MathContext(2, RoundingMode.CEILING);
		MathContextRoundedOperator monetaryOperator = MathContextRoundedOperator.of(mathContext);
		monetaryOperator.apply(null);
		fail();
	}

	@Test
	public void shouldRoundedMonetaryOperatorWhenTheImplementationIsMoney() {
		int scale = 4;
		MathContext mathContext = new MathContext(scale, RoundingMode.HALF_EVEN);

		CurrencyUnit real = Monetary.getCurrency("BRL");
		MonetaryAmount money = Money.of(BigDecimal.valueOf(35.34567), real);

		MathContextRoundedOperator monetaryOperator = MathContextRoundedOperator.of(mathContext);
		MonetaryAmount result = monetaryOperator.apply(money);
		assertTrue(RoundedMoney.class.isInstance(result));
		assertEquals(result.getCurrency(), real);
		assertEquals(result.getNumber().getPrecision(), scale);
		assertEquals(BigDecimal.valueOf(35.35), result.getNumber().numberValue(BigDecimal.class));



	}
}
