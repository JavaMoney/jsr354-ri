package org.javamoney.moneta.spi;

import static org.junit.Assert.assertEquals;

import javax.money.MonetaryOperator;

import org.javamoney.moneta.function.MonetaryOperators;
import org.testng.annotations.Test;

public class MonetaryAmountProducerTest {

	@Test
	public void shouldReturnFastMoneyProducer() {
		MonetaryAmountProducer producer = MonetaryAmountProducer.fastMoneyProducer();
		assertEquals(producer.getClass(), FastMoneyProducer.class);
	}

	@Test
	public void shouldReturnMoneyProducer() {
		MonetaryAmountProducer producer = MonetaryAmountProducer.moneyProducer();
		assertEquals(producer.getClass(), MoneyProducer.class);
	}

	@Test
	public void shouldReturnRoundedMoneyProducer() {
		MonetaryAmountProducer producer = MonetaryAmountProducer.roundedMoneyProducer();
		assertEquals(producer.getClass(), RoundedMoneyProducer.class);
		RoundedMoneyProducer roundedMoney = (RoundedMoneyProducer) producer;
		assertEquals(roundedMoney.getOperator(), MonetaryOperators.rounding());
	}

	@Test
	public void shouldReturnRoundedMoneyProducerUsingOperator() {
		MonetaryOperator operator = m -> m;
		MonetaryAmountProducer producer = MonetaryAmountProducer.roundedMoneyProducer(operator);
		assertEquals(producer.getClass(), RoundedMoneyProducer.class);
		RoundedMoneyProducer roundedMoney = (RoundedMoneyProducer) producer;
		assertEquals(roundedMoney.getOperator(), operator);
	}
}
