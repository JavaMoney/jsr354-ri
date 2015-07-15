package org.javamoney.moneta.function;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

import java.util.Locale;

import javax.money.CurrencyUnit;
import javax.money.Monetary;
import javax.money.MonetaryAmount;

import org.javamoney.moneta.Money;
import org.javamoney.moneta.function.MonetaryAmountProducer;
import org.javamoney.moneta.function.MoneyProducer;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

public class MoneyProducerTest {

	private MonetaryAmountProducer producer;

	private CurrencyUnit currency;

	@BeforeMethod
	public void setup() {
		producer = new MoneyProducer();
		currency = Monetary.getCurrency(Locale.getDefault());
	}

	@Test(expectedExceptions = NullPointerException.class)
	public void shouldReturnErrorWhenCurrencyIsNull() {
		producer.create(null, 10);
	}

	@Test(expectedExceptions = NullPointerException.class)
	public void shouldReturnErrorWhenNumberIsNull() {
		producer.create(currency, null);
	}

	@Test
	public void shouldCreateMonetaryAmount() {
		Long value = 10L;
		MonetaryAmount amount = producer.create(currency, value);
		assertEquals(amount.getCurrency(), currency);
		assertEquals(Long.valueOf(amount.getNumber().longValue()), value);
	}

	@Test
	public void shouldCreateUsingMoneyImplementation() {
		Long value = 10L;
		MonetaryAmount amount = producer.create(currency, value);
		assertTrue(Money.class.isInstance(amount));
	}

}
