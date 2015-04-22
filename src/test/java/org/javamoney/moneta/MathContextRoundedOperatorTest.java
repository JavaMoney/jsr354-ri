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

	@Test(expectedExceptions = NullPointerException.class)
	public void shouldReturnErrorWhenParameterIsNUll() {
		MathContext mathContext = new MathContext(2, RoundingMode.CEILING);
		MathContextRoundedOperator monetaryOperator = MathContextRoundedOperator.of(mathContext);
		monetaryOperator.apply(null);
		fail();
	}

	@Test
	public void shouldRoundedMonetaryOperatorWhenTheImplementationIsMoney() {
		int scale = 2;
		MathContext mathContext = new MathContext(scale, RoundingMode.HALF_EVEN);

		CurrencyUnit real = Monetary.getCurrency("BRL");
		Money money = Money.of(BigDecimal.valueOf(130002.56895), real);

		MathContextRoundedOperator monetaryOperator = MathContextRoundedOperator.of(mathContext);
		MonetaryAmount result = monetaryOperator.apply(money);
		assertTrue(RoundedMoney.class.isInstance(result));
		assertEquals(result.getCurrency(), real);
		assertEquals(result.getNumber().getPrecision(), scale);



	}
}
