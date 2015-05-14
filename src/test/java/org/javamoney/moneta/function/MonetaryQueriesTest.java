package org.javamoney.moneta.function;

import static org.testng.Assert.assertEquals;

import javax.money.MonetaryAmount;

import org.javamoney.moneta.Money;
import org.testng.annotations.Test;

public class MonetaryQueriesTest {


	@Test
	public void shouldExtractMajorPart(){
		MonetaryAmount money = Money.parse("EUR 2.35");
		Long result = money.query(MonetaryQueries.extractMajorPart());
		assertEquals(result, Long.valueOf(2L));
	}

	@Test
	public void shouldConvertMinorPart(){
		MonetaryAmount money = Money.parse("EUR 2.35");
		Long result = money.query(MonetaryQueries.convertMinorPart());
		assertEquals(result, Long.valueOf(235L));
	}

	@Test
	public void shouldExtractMinorPart(){
		MonetaryAmount money = Money.parse("EUR 2.35");
		Long result = money.query(MonetaryQueries.extractMinorPart());
		assertEquals(result, Long.valueOf(35L));
	}
}
