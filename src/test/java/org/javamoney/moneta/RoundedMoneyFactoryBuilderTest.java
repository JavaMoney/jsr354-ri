package org.javamoney.moneta;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.fail;

import java.math.MathContext;
import java.math.RoundingMode;

import javax.money.MonetaryOperator;

import org.testng.annotations.Test;

public class RoundedMoneyFactoryBuilderTest {

	@Test(expectedExceptions = IllegalArgumentException.class)
	public void shouldReturnErrorWhenTheScaleIsLesserThanZero() {
		RoundedMoneyFactory.build().withScale(-1);
		fail();
	}

	@Test(expectedExceptions = IllegalArgumentException.class)
	public void shouldReturnErrorWhenTheScaleIsEqualsZero() {
		RoundedMoneyFactory.build().withScale(0);
		fail();
	}

	@Test(expectedExceptions = NullPointerException.class)
	public void shouldReturnErrorWhenTheMathContextIsNull() {
		RoundedMoneyFactory.build().withMathContext(null);
		fail();
	}

	@Test(expectedExceptions = NullPointerException.class)
	public void shouldReturnErrorWhenTheRoundingModeIsNull() {
		RoundedMoneyFactory.build().withMathContext(null);
		fail();
	}

	@Test(expectedExceptions = NullPointerException.class)
	public void shouldReturnErrorWhenTheRoundingOperatorIsNull() {
		RoundedMoneyFactory.build().witRoundingOperator(null);
		fail();
	}

	@Test(expectedExceptions = IllegalStateException.class)
	public void shouldReturnErrorWhenNotOneElementWasInformed() {
		RoundedMoneyFactory.build().build();
		fail();
	}

	@Test(expectedExceptions = IllegalStateException.class)
	public void shouldReturnErrorWhenJustScaleWasInformed() {
		RoundedMoneyFactory.build().withScale(2).build();
		fail();
	}

	@Test(expectedExceptions = IllegalStateException.class)
	public void shouldReturnErrorWhenJustRoundingModeWasInformed() {
		RoundedMoneyFactory.build().withRoundingMode(RoundingMode.DOWN).build();
		fail();
	}

	@Test
	public void shouldCreateRoundedFactoryUsingRoundingOperator() {
		MonetaryOperator roundingOperator = MonetaryOperator.identity();

		RoundedMoneyFactory factory = RoundedMoneyFactory.build().witRoundingOperator(roundingOperator).build();
		assertNotNull(factory);
		assertEquals(roundingOperator, factory.getRoundingOperator());
	}

	@Test
	public void shouldCreateRoundedFactoryUsingMathContext() {
		RoundedMoneyFactory factory = RoundedMoneyFactory.build().withMathContext(MathContext.DECIMAL128).build();
		assertNotNull(factory);
	}

	@Test
	public void shouldCreateRoundedFactoryUsingRoundingModeWithScale() {
		RoundedMoneyFactory factory = RoundedMoneyFactory.build().withRoundingMode(RoundingMode.CEILING).withScale(4).build();
		assertNotNull(factory);
	}


}