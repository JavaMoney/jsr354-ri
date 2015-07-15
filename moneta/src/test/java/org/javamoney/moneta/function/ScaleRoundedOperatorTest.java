package org.javamoney.moneta.function;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;
import static org.testng.Assert.fail;

import java.math.BigDecimal;
import java.math.RoundingMode;

import javax.money.CurrencyUnit;
import javax.money.Monetary;
import javax.money.MonetaryAmount;

import org.javamoney.moneta.Money;
import org.javamoney.moneta.RoundedMoney;
import org.testng.annotations.Test;

public class ScaleRoundedOperatorTest {

	@Test(expectedExceptions = NullPointerException.class)
	public void shouldReturnNullPointerExceptionWhenParameterIsNull() {
		ScaleRoundedOperator.of(0, null);
		fail();
	}

	@Test(expectedExceptions = NullPointerException.class)
	public void shouldReturnErrorWhenParameterIsNUll() {
		ScaleRoundedOperator monetaryOperator = ScaleRoundedOperator.of(0, RoundingMode.HALF_EVEN);
		monetaryOperator.apply(null);
		fail();
	}

	@Test(expectedExceptions = IllegalStateException.class)
	public void shouldReturnErrorWhenScaleIsUNNECESSARY() {
		ScaleRoundedOperator.of(0, RoundingMode.UNNECESSARY);
		fail();
	}

	@Test
	public void shouldRoundedMonetaryOperatorWhenTheImplementationIsMoney() {
		int scale = 4;

		CurrencyUnit real = Monetary.getCurrency("BRL");
		MonetaryAmount money = Money.of(BigDecimal.valueOf(35.34567), real);

		ScaleRoundedOperator monetaryOperator = ScaleRoundedOperator.of(scale, RoundingMode.HALF_EVEN);
		MonetaryAmount result = monetaryOperator.apply(money);
		assertTrue(RoundedMoney.class.isInstance(result));
		assertEquals(result.getCurrency(), real);
		assertEquals(result.getNumber().getScale(), scale);
		assertEquals(BigDecimal.valueOf(35.3457), result.getNumber().numberValue(BigDecimal.class));

	}
}
