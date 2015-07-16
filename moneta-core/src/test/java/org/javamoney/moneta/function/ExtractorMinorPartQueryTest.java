package org.javamoney.moneta.function;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.fail;

import javax.money.MonetaryAmount;
import javax.money.MonetaryQuery;

import org.javamoney.moneta.Money;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

public class ExtractorMinorPartQueryTest {

	private MonetaryQuery<Long> query;

	@BeforeMethod
	public void setup() {
		query = new ExtractorMinorPartQuery();
	}


	@Test(expectedExceptions = NullPointerException.class)
	public void shouldReturnNPEWhenTheMonetaryAmountIsNull() {
		query.queryFrom(null);
		fail();
	}

	@Test
	public void shouldReturnMajorPartPositive() {
		MonetaryAmount monetaryAmount = Money.parse("EUR 2.35");
		Long result = query.queryFrom(monetaryAmount);
		Long expected = 35L;
		assertEquals(result, expected);
	}

	@Test
	public void shouldReturnMajorPartNegative() {
		MonetaryAmount monetaryAmount = Money.parse("BHD -1.345");
		Long result = query.queryFrom(monetaryAmount);
		Long expected = -345L;
		assertEquals(result, expected);
	}
}
