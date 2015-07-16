package org.javamoney.moneta.function;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.fail;

import javax.money.CurrencyUnit;
import javax.money.Monetary;
import javax.money.MonetaryAmount;
import javax.money.MonetaryOperator;

import org.javamoney.moneta.Money;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

public class ExchangeCurrencyOperatorTest {

	private MonetaryOperator operator;

	private CurrencyUnit real;

	@BeforeMethod
	public void setup() {
		real = Monetary.getCurrency("BRL");
		operator = new ExchangeCurrencyOperator(real);
	}

	@Test
	public void shouldReturnPositiveValue() {
		MonetaryAmount money = Money.parse("EUR 2.35");
		MonetaryAmount result = operator.apply(money);
		assertEquals(result.getCurrency(), this.real);
		assertEquals(result.getNumber().doubleValue(), 2.35);
	}


	@Test
	public void shouldReturnNegativeValue() {
		MonetaryAmount money = Money.parse("BHD -1.345");
		MonetaryAmount result = operator.apply(money);
		assertEquals(result.getCurrency(), real);
		assertEquals(result.getNumber().doubleValue(), -1.345);

	}

	@Test(expectedExceptions = NullPointerException.class)
	public void shouldReturnErroWhenIsNull() {
		operator.apply(null);
		fail();
	}
}
