package org.javamoney.moneta.function;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertTrue;
import static org.testng.Assert.fail;

import java.math.BigDecimal;

import javax.money.CurrencyUnit;
import javax.money.Monetary;
import javax.money.MonetaryAmount;
import javax.money.MonetaryOperator;

import org.javamoney.moneta.RoundedMoney;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

public class DefaultMonetaryRoundedFactoryTest {

	private MonetaryRoundedFactory factory;

	private CurrencyUnit real = Monetary.getCurrency("BRL");

	private MonetaryOperator identical = m -> m;


	@BeforeMethod
	public void setup() {
		factory = new DefaultMonetaryRoundedFactory(identical);
	}

	@Test
	public void shouldReturnGetRoudingOperator() {
		MonetaryOperator roundingOperator = factory.getRoundingOperator();
		assertEquals(identical, roundingOperator);
	}

	@Test(expectedExceptions = NullPointerException.class)
	public void shouldReturnErrorWhenCreateWithNumberNull() {
		factory.create(null, real);
		fail();
	}

	@Test(expectedExceptions = NullPointerException.class)
	public void shouldReturnErrorWhenCreateWithCurrencyUnitNull() {
		factory.create(20, null);
		fail();
	}

	@Test
	public void shouldCreateMonetaryAmount() {
		MonetaryAmount monetaryAmount = factory.create(BigDecimal.TEN, real);

		assertNotNull(monetaryAmount);
		assertTrue(RoundedMoney.class.isInstance(monetaryAmount));

		RoundedMoney roundedMoney = RoundedMoney.class.cast(monetaryAmount);
		assertEquals(real, roundedMoney.getCurrency());
		assertEquals(BigDecimal.TEN, roundedMoney.getNumber().numberValue(BigDecimal.class));


	}
}
